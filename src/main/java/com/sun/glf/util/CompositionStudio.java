/*
 * @(#)CompositionStudio.java
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package com.sun.glf.util;

import java.awt.*;
import java.awt.event.*;

import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;

import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Helper class, used to ease the creation of composition beans.
 *
 * @author        Vincent Hardy
 * @version       1.0, 10/12/1998
 */
public class CompositionStudio extends GridBagPanel{

  /*
   * Display strings
   */
  static final String BEAN_NAME = "Bean Name";
  static final String BEANS_SAVED = "Beans saved";
  static final String COMPOSITION_PREVIEW = "Composition Preview";
  static final String ENTER_BEAN_NAME = "Enter Bean name";
  static final String INSPECTING_BEANS = "Inspecting bean ... ";
  static final String CONFIRM = "Confirm";
  static final String ERROR_CANNOT_LOAD_BEAN = "Error. Cannot load bean : ";
  static final String ERROR_CANNOT_SERIALIZE_BEANS = "Could not serialize beans: ";
  static final String ERROR_DIRECTORY_SELECTED = "Error: you selected a directory";
  static final String ERROR_TARGET_EXCEPTION = "Error : " ;
  static final String FILE_EXIST_OVERRIDE = "File already exists. Overwrite?";
  static final String FRAME_TITLE = "Composition Studio";
  static final String LOAD = "Load";
  static final String LOAD_A_BEAN = "Load a Bean with the Load Button";
  static final String LOAD_BEAN = "Load Beans";
  static final String LOADING_CLASS = "Loading Class : ";
  static final String SAVE = "Save";
  static final String PREVIEW = "Preview";
  static final String PREVIEW_SEPARATE = "Preview in separate windows";

  /** File Chooser */
  private static JFileChooser fileChooser;

  /** Controls whether or not loading should be allowed */
  private static boolean isLoadEnabled = true;

  /** Controls whether or not loading should be allowed */
  private static boolean isSaveEnabled = true;

  /** Text Serialized format file filter : only shows in File open dialog */
  private static javax.swing.filechooser.FileFilter serTxtFilter;

  /** Serialized format file filter */
  private static javax.swing.filechooser.FileFilter serFilter;

  static {
    Toolbox.swingDefaultsInit();

    fileChooser = new JFileChooser();
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    serFilter = new SerializedBeanFileFilter();
    serTxtFilter = new TextBeanFileFilter();

    fileChooser.addChoosableFileFilter(serFilter);
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    //
    // Register property editors
    //
    
    // Glyph
    PropertyEditorManager.registerEditor(com.sun.glf.util.Glyph.class, GlyphPropertyEditor.class);

    // Color
    PropertyEditorManager.registerEditor(java.awt.Color.class, ColorPropertyEditor.class);

    // Boolean
    PropertyEditorManager.registerEditor(boolean.class, BooleanPropertyEditor.class);
    PropertyEditorManager.registerEditor(Boolean.class, BooleanPropertyEditor.class);

    // Font
    PropertyEditorManager.registerEditor(Font.class, FontPropertyEditor.class);

    // Dimension
    PropertyEditorManager.registerEditor(Dimension.class, DimensionPropertyEditor.class);

    // File
    PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);

    // Integer
    PropertyEditorManager.registerEditor(Integer.class, IntegerPropertyEditor.class);
    PropertyEditorManager.registerEditor(int.class, IntegerPropertyEditor.class);

    // Float
    PropertyEditorManager.registerEditor(Float.class, FloatPropertyEditor.class);
    PropertyEditorManager.registerEditor(float.class, FloatPropertyEditor.class);

    // Double
    PropertyEditorManager.registerEditor(Double.class, DoublePropertyEditor.class);
    PropertyEditorManager.registerEditor(double.class, DoublePropertyEditor.class);

    // String 
    PropertyEditorManager.registerEditor(String.class, StringPropertyEditor.class);

    // Check whether or not saving is allowed in the environment
    try{
      InputStream ps = CompositionFrame.class.getResourceAsStream("/com/sun/glf/res/config.properties");
      if(ps!=null){
	Properties config = new Properties();
	config.load(ps);
	String canSave = config.getProperty("canSave", "no");
	isSaveEnabled = canSave.equalsIgnoreCase("sure");
	String canLoad = config.getProperty("canLoad", "no");
	isLoadEnabled = canLoad.equalsIgnoreCase("sure");
      }
    }catch(Exception e){
    }

  }

  /** Set of editor UIs used to configure the different bean properties */
  private PropertyEditorUI[] editorUIs;

  /** CompositionFactory configured by this configurator */
  private CompositionFactory beans;

  /** Name of the current bean */
  private String beanName = "";

  /** beanName can be monitored for change */
  private PropertyChangeSupport beanNameChangeSupport;

  /** 
   * Controls whether or not the same frame should be reused to 
   * display the previews.
   */
  private JCheckBox previewSeparate;

  /**
   * Controls whether to load a bean with its fully qualified
   * name (e.g. mypackage.xx.myBean) or as a file (e.g. c:\tmp\myBean.ser)
   */
  private JCheckBox loadQualifiedBean;

  /**
   * Last preview frame
   */
  private CompositionFrame previewFrame = new CompositionFrame("");

  /**
   * Default Constructor.
   * 
   * @param beans the Java Beans which should be configured by this panel.
   */
  public CompositionStudio(){
    // Used to notify observers of beanName changes
    beanNameChangeSupport = new PropertyChangeSupport(this);

    // Start with no bean displayed
    loadBeans(null);

    // Set maximum size
    Dimension size = getToolkit().getScreenSize();
    size.height /= 2;
    setMaximumSize(size);
  }

  /**
   * If the new beans is of the same class as the currently 
   * displayed one, just display the new beans properties.
   * Otherwise, remove all components from display and show
   * the new beans properties instead.
   */
  public void loadBeans(CompositionFactory beans){
    if(this.beans!=null && this.beans.getClass()==beans.getClass()){
      this.beans = beans;
      loadUIValues();
    }
    else{
      // This is either the first bean or it is one from 
      // a different class
      this.beans = beans;
      
      // First, create a panel to display the beans properties.
      // This also initializes the editorUIs.
      JComponent configPanel = buildConfigurationPanel(beans);
      
      // Now, add configuration buttons
      JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
      JButton saveButton = new JButton(SAVE);
      JButton previewButton = new JButton(PREVIEW);
      JButton loadButton = new JButton(LOAD);
      
      buttonPanel.add(loadButton);
      if(beans!=null){
	buttonPanel.add(saveButton);
	buttonPanel.add(previewButton);
      }

      GridBagPanel bp = new GridBagPanel();
      bp.add(Box.createHorizontalGlue(), 0, 0, 1, 1, bp.EAST, bp.HORIZONTAL, 1, 0);
      bp.add(buttonPanel, 1, 0, 1, 1, bp.CENTER, bp.NONE, 0, 0);

      // Top level layout
      removeAll();
      add(new JScrollPane(configPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), 0, 0, 1, 1, CENTER, BOTH, 1, 1);
      add(bp, 0, 2, 1, 1, CENTER, HORIZONTAL, 0, 0);
      
      // Add event handlers
      loadButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent evt){
	  if(loadQualifiedBean!=null && loadQualifiedBean.isSelected())
	    onLoadBean();
	  else
	    onLoadFile();
	}
      });
      saveButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent evt){
	  onSave();
	}
      });
      previewButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent evt){
	  onPreview();
	}
      });

      // Take load/save capabilities into account
      saveButton.setVisible(isSaveEnabled);
      loadButton.setVisible(isLoadEnabled);

      // Set Borders
      configPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),
							       BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
												  BorderFactory.createEmptyBorder(5,5,5,5))));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
      
      // Synchronize UI with beans values
      loadUIValues();

      // If there is already a parent : pack or relayout.
      if(getParent()!=null){
	if(getParent() instanceof Window){
	  ((Window)getParent()).pack();
	}
	else {
	  invalidate();
	  getParent().validate();
	}
      }
    }
  }
  
  /**
   * Sets the UI components with values reflecting the beans state
   */
  private void loadUIValues(){
    int nProperties = editorUIs!=null?editorUIs.length:0;
    for(int i=0; i<nProperties; i++)
      editorUIs[i].load(beans);
  }

  /**
   * Save the value displayed by the UI components into the beans
   */
  private void saveUIValues() throws InvocationTargetException{
    int nProperties = editorUIs!=null?editorUIs.length:0;
    for(int i=0; i<nProperties; i++)
      editorUIs[i].save(beans);
  }

  /**
   * Handles the load event request. Note that this assumes that
   * the working directory is in the classpath.
   */
  private void onLoadFile(){
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    fileChooser.addChoosableFileFilter(serTxtFilter);     // Allows loading of recovery format
    int choice = fileChooser.showDialog(null, null);
    fileChooser.removeChoosableFileFilter(serTxtFilter);
    if(choice == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      loadBeanFile(file);
    }            
  }

  /**
   * Loads a bean file
   */
  private void loadBeanFile(File file){
    CompositionFactory bean = CompositionFactoryLoader.loadBeanFile(file);
    if(bean != null){
      loadBeans(bean);
      setBeanName(file.getName());
    }
  }

  /**
   * Handles the load event request
   */
  private void onLoadBean(){
    String beanName = JOptionPane.showInputDialog(null, ENTER_BEAN_NAME, BEAN_NAME, JOptionPane.PLAIN_MESSAGE);
    if(beanName != null){
      loadBeanObject(beanName);
    }       
  }

  /**
   * Loads the requested bean object
   */
  private void loadBeanObject(String beanName){
    try{
      System.out.println(LOADING_CLASS + beanName);

      // Strip class extension if present.
      if(beanName.endsWith(".class"))
	beanName = beanName.substring(0, beanName.length()-6);

      Object bean = Beans.instantiate(null, beanName);
      loadBeans((CompositionFactory)bean);
      setBeanName(beanName);
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, ERROR_CANNOT_LOAD_BEAN + e.getMessage());
      e.printStackTrace();
    }
  }


  /**
   * Sets the current bean name.
   */
  private void setBeanName(String beanName){
    String oldVal = this.beanName;
    this.beanName = beanName;
    beanNameChangeSupport.firePropertyChange("beanName", oldVal, beanName);
  }

  /** 
   * Returns the current bean name
   */
  public String getBeanName(){
    return beanName;
  }

  /**
   * Add a listener for the beanName property
   */
  public void addBeanNameChangeListener(PropertyChangeListener l){
    beanNameChangeSupport.addPropertyChangeListener(l);
  }

  /**
   * Removes a listener for the beanName property
   */
  public void removeBeanNameListener(PropertyChangeListener l){
    beanNameChangeSupport.removePropertyChangeListener(l);
  }

  /**
   * Handles the save event request:<p>
   * 1. Request a name for the saved bean
   * 2. Save UI value into beans.
   * 3. Open an object stream and save beans.
   *
   * Note that, as a security mechanism, beans are always saved
   * in serialized form and in text format so that it is possible
   * to recover at least some of the property of a serialized bean
   * whose class has changed (new serialVersionUID).
   *
   */
  private void onSave(){
    // 1. Get file name
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);  // This is a save dialog
    fileChooser.setFileFilter(serFilter);
    int choice = fileChooser.showDialog(null, null);
    if(choice == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      
      // 2. Save UI value into beans
      try{
	saveUIValues();
	  
	// 3. Open ObjectStream and save beans
	try{
	  File txtFile = new File(file.getAbsolutePath() + ".txt");
	  boolean keepSaving = false;
	  if(file.exists() || txtFile.exists()){
	    int saveChoice = JOptionPane.showConfirmDialog(null, FILE_EXIST_OVERRIDE, CONFIRM, JOptionPane.YES_NO_OPTION);
	    keepSaving = (saveChoice==JOptionPane.YES_OPTION);
	  }
	  else
	    keepSaving = true;

	  if(keepSaving){
	    //
	    // Save in serialized format
	    //
	    FileOutputStream out = new FileOutputStream(file.getPath());
	    ObjectOutputStream s = new ObjectOutputStream(out);
	    s.writeObject(beans);
	    s.flush();
	    s.close();
	    out.close();
	    
	    //
	    // Save in text format
	    //
	    out = new FileOutputStream(file.getPath() + ".txt");
	    TextBeans.save(beans, out);
	    out.close();
	    JOptionPane.showMessageDialog(null, BEANS_SAVED);
	  }
	}catch(IOException e){
	  JOptionPane.showMessageDialog(null, ERROR_CANNOT_SERIALIZE_BEANS + e.getMessage());
	  e.printStackTrace();
	}
      }catch(InvocationTargetException e){
	JOptionPane.showMessageDialog(null, ERROR_TARGET_EXCEPTION + e.getMessage());
      }	  
    }
  }

  /**
   * Handlers the preview event request. This will create a new frame
   * if the previewSeparate checkbox is selected. Otherwise, the preview
   * frame which was last created is used.
   */
  private synchronized void onPreview(){
    try{
      saveUIValues();
      if(previewSeparate.isSelected() || previewFrame == null){
	previewFrame = new CompositionFrame("");
      }
      previewFrame.load(beans);
      previewFrame.show();
    }catch(InvocationTargetException e){
      JOptionPane.showMessageDialog(null, ERROR_TARGET_EXCEPTION + e.getMessage());
      return;
    }	  
  }

  /** 
   * @param the set of PropertyEditorUIs to use to configure the current bean
   */
  private void setEditorUIs(Vector editorUIs){
    if(editorUIs==null)
      throw new IllegalArgumentException();

    this.editorUIs = new PropertyEditorUI[editorUIs.size()];
    editorUIs.copyInto(this.editorUIs);
  }

  /**
   * @return the property name for a given set method
   * @param set/get method name. If the name is not the one of a set or get method
   *        name, the result is not guaranteed.
   */
  public String getPropertyName(String methodName){
    String propertyName = methodName.substring(3);
    
    // Now, parse name and look for upper case letter
    // A space is inserted in front of each upper case letter
    // to get a more user friendly name
    char buff[] = propertyName.toCharArray();
    int n = buff!=null?buff.length:0;
    char data[] = new char[n*2];
    int j=1;
    data[0] = buff[0];
    for(int i=1; i<n; i++){
      char c = buff[i];
      if(Character.isUpperCase(c))
	data[j++] = ' ';
      data[j++] = c;
    }

    return new String(data, 0, j);
  }

  /**
   * @return the set of get and set methods of the input bean class
   *         for all the properties which follow the Java Beans naming
   *         pattern.
   */
  public Method[][] getPropertyMethods(Class beanClass){
    Vector v = new Vector();
    Method m[] = beanClass.getMethods();
    int n = m!=null?m.length:0;
    for(int i=0; i<n; i++){
      if(m[i].getName().startsWith("get")){
	Method gs[] = new Method[2];
	gs[0] = m[i];
	Class params[] = m[i].getParameterTypes();
	if(params==null || params.length==0){
	  // Check for corresponding set method
	  try{
	    Method setMethod = beanClass.getMethod("set" + m[i].getName().substring(3),
						   new Class[] { m[i].getReturnType() });
	    gs[1] = setMethod;
	    v.addElement(gs);
	  }catch(NoSuchMethodException e){}
	  catch(SecurityException e){}
	}
      }
    }
    Method pm[][] = new Method[v.size()][2];
    v.copyInto(pm);
    return pm;
  }

  /**
   * Inspects a bean object and builds a user interface 
   * to set its properties
   *
   * @param bean the bean which should be configured
   * @return configuration panel containing all the controls to set the bean properties.
   *         The panel is initialized with the current bean properties values.
   */
  private JComponent buildConfigurationPanel(Object bean){
    if(bean==null)
      return new JLabel(LOAD_A_BEAN);

    //
    // First, get all the methods which follow the Beans naming pattern
    //
    Method methods[][] = getPropertyMethods(bean.getClass());

    //
    // Now, build the configuration panel
    //
    GridBagPanel panel = new GridBagPanel();

    //
    // For each property, add a line in the configPanel
    // The property name is written on the left and 
    // the property setting appears on the right
    int nProperties = methods!=null?methods.length:0;
    Vector editorUIs = new Vector();

    for(int i=0; i<nProperties; i++){
      Method getSet[] = methods[i];
      String propertyName = getPropertyName(getSet[0].getName());
      Class targetType = getSet[0].getReturnType();
      
      // Get property editor for this type
      PropertyEditor editor = PropertyEditorManager.findEditor(targetType);

      if(editor != null ){
	JLabel title = new JLabel(propertyName);
	panel.add(title, 0, i, 1, 1, 
			WEST, NONE, 0, 0);

	// Add padding
	panel.add(Box.createHorizontalStrut(10), 1, i, 1, 1, CENTER, NONE, 0, 0);
	
	PropertyEditorUI editorUI = new PropertyEditorUI(editor, getSet[0], getSet[1]);
	panel.add(editorUI, 2, i, 1, 1, WEST, HORIZONTAL, 1, 0);
	editorUIs.addElement(editorUI);	
      }
    }

    setEditorUIs(editorUIs);

    // Create a check box to control whether each preview shoudl be in a separate window
    previewSeparate = new JCheckBox(PREVIEW_SEPARATE);
    previewSeparate.setHorizontalTextPosition(previewSeparate.LEFT);

    // Create a check box to control whether to load a serialized file or a bean
    loadQualifiedBean = new JCheckBox(LOAD_BEAN);
    loadQualifiedBean.setHorizontalTextPosition(loadQualifiedBean.RIGHT);
    loadQualifiedBean.setVisible(isLoadEnabled);
    
    panel.add(loadQualifiedBean, 0, nProperties, 1, 1, WEST, NONE, 0, 0);
    panel.add(previewSeparate, 1, nProperties, REMAINDER, 1, EAST, NONE, 0, 0);

    return panel;
  }

  static final String USAGE = "Usage : java com.sun.glf.util.CompositionStudio <beanFile>\ne.g. java com.sun.glf.util.CompositionStudio myBean.ser\ne.g. java com.sun.glf.util.CompositionStudio myCompositionFactoryClass.class";

  /**
   * Creates a CompositionStudio panel and puts it into a top
   * level frame.
   */
  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    String getAllFonts = System.getProperty("com.sun.glf.getAllFonts", "false");
    if(getAllFonts.equalsIgnoreCase("true")){
      Toolbox.initFonts();
    }
    
    String beanName = args[0];

    final CompositionStudio configurator = new CompositionStudio();
    final JFrame frame = new JFrame(FRAME_TITLE);
    frame.getContentPane().add(configurator);
    frame.pack();
    
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	frame.setVisible(false);
	frame.dispose();
	System.exit(0);
      }
    });

    configurator.addBeanNameChangeListener(new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt){
	frame.setTitle(FRAME_TITLE + " " + configurator.getBeanName());
      }
    });

    if(beanName.equalsIgnoreCase("-test-")){
      configurator.loadBeanObject(SimpleFactory.class.getName());
    }
    else{
      try{
	System.out.println(INSPECTING_BEANS + beanName);
	File beanFile = new File(beanName);
	if(beanName.endsWith(".class"))
	  configurator.loadBeanObject(beanName);
	else if(beanFile.exists()) { // if(beanName.indexOf(System.getProperty("file.separator", "/")) != -1){
	  configurator.loadBeanFile(new File(beanName));
	}
	else
	  configurator.loadBeanObject(beanName);
      }catch(Exception e){
	e.printStackTrace();
      }
    }

    frame.pack();

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size = frame.getSize();
    if( size.height > (80*screenSize.height)/100 ){
      size.height = (80*screenSize.height/100);
      frame.setSize(size);
    }
    frame.show();

    Object dw = new Object(){};
    synchronized(dw){
      try{
	dw.wait();
      }catch(Exception e){}
    }


  }

  public static class SimpleFactory implements CompositionFactory {
    Color textColor = Color.black;
    Color backgroundColor = Color.white;
    Font textFont = new Font("Dialog", Font.PLAIN, 40);
    String text = "Hello There";
    boolean antialiasing;
    int width = 100, height = 100;

    public Color getTextColor(){ return textColor; }
    public void setTextColor(Color textColor) { this.textColor = textColor; }
    public Color getBackgroundColor(){ return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor){ this.backgroundColor = backgroundColor; }
    public Font getTextFont(){ return textFont; }
    public void setTextFont(Font textFont){ this.textFont = textFont; }
    public String getText(){ return text; }
    public void setText(String text){ this.text = text; }
    public boolean getAntialiasing(){ return antialiasing; }
    public void setAntialiasing(boolean antialiasing) { this.antialiasing = antialiasing; }
    public int getCompositionWidth(){ return width; }
    public void setCompositionWidth(int width){ this.width = width; }
    public int getCompositionHeight(){ return height; }
    public void setCompositionHeight(int height){ this.height = height; }

    public Composition build(){
      Dimension dim = new Dimension(width, height);

      LayerComposition cmp = new LayerComposition(dim);
    
      // TextLayer
      FillRenderer textRenderer = new FillRenderer(textColor);
      TextLayer textLayer = new TextLayer(cmp, text, textFont, textRenderer);

      // Background layer : ShapeLayer
      Shape background = new Rectangle(0, 0, dim.width, dim.height);
      FillRenderer bkgRenderer = new FillRenderer(backgroundColor);
      ShapeLayer backgroundLayer = new ShapeLayer(cmp, background, bkgRenderer);

      cmp.setLayers( new Layer[]{ backgroundLayer, textLayer});

      // Rendering Hints
      if(antialiasing){
	RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
					       RenderingHints.VALUE_ANTIALIAS_ON);
	cmp.setRenderingHints(rh);
      }

      return cmp;
    }
  }


}

