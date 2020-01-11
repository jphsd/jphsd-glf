/*
 * @(#)CompositionFrame.java
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
import java.awt.image.*;
import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * Utility class. A CompositionFrame displays a Composition and allows 
 * saving of the Composition output as an Image.
 * <p>
 * The encoders which a CompositionFrame can use are defined into the
 * com.sun.glf.res.encoder.properties file, which contains a simple list
 * of Image encoder classes. 
 * <p>
 * A CompositionFrame can load a Composition from a CompositionFactory
 * through its load members. This is done in a separate thread.
 */
public class CompositionFrame extends JFrame {
  static final String BEANS_SAVED = "Beans saved";
  static final String ERROR_CANNOT_SERIALIZE_BEANS = "Error. Cannot serialize bean: ";
  static final String ERROR_UNKNOWN_FORMAT = "Unable to save. Unknown format : ";
  static final String FILE_EXIST_OVERRIDE = "File already exists. Overwrite?";
  static final String CONFIRM = "Confirm";
  static final String COULD_NOT_SAVE_IMAGE = "Could not save image";
  static final String COULD_NOT_LOAD_COMPOSITION = "Could not load Composition";
  static final String PLEASE_WAIT = "Please, Wait while image is loading...";
  static final String SAVE_AS_IMAGE = "Save image...";
  static final int    MONITOR_MARGIN = 5;

  /** Used for automatic thread identification. Debug purpose only */
  private static int curThread = 0;

  /** Default size, before any Composition has been set or loaded */
  private static Dimension defaultSize = new Dimension(300, 50);

  /** Popup menu, for Save */
  private JPopupMenu menu;
  
  /** Multi-threaded Composition loading */
  CompositionLoadThread loadingThread;

  /** Currently displayed composition */
  CompositionComponent comp;

  /** Displayed paint progress information while a composition is painting */
  CompositionPaintMonitor paintMonitor = new CompositionPaintMonitor();

  /** Controls whether or not save is enabled in the environment */
  boolean isSaveEnabled = true;

  /**
   * Serialized buffer containing the factory that was last 
   * used to create a Composition
   * @see #load
   */
  byte[] serializedFactory;

  /** File Chooser for image files */
  static JFileChooser fileChooser;

  /** Serialized Bean file filter */
  static SerializedBeanFileFilter serializedBeanFilter = new SerializedBeanFileFilter();

  /** Contains all avaialble image encoders, sorted by file extention */
  static Map encoderMap;

  private void init(){
    try{
      // Initialize file chooser dialog with 
      // filters from the encoder list
      fileChooser = new JFileChooser();
      fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      InputStream ps = CompositionFrame.class.getResourceAsStream("/com/sun/glf/res/encoders.properties");
      Properties encoders = new Properties();
      encoders.load(ps);
      encoderMap = new Hashtable();
      String encoderList = encoders.getProperty("list");

      if(encoderList!=null){
	StringTokenizer st = new StringTokenizer(encoderList);
	while(st.hasMoreTokens()){
	  String className = st.nextToken();
	  try{
	    ImageEncoder encoder = (ImageEncoder)Beans.instantiate(null, className);
	    encoderMap.put(encoder.getFileExtention(), encoder);
	  }catch(ClassCastException e){
	    System.out.println(className + " is not an ImageEncoder");
	  }catch(ClassNotFoundException e){
	    System.out.println(className + " not found");
	  }catch(IOException e){
	    System.out.println(className + " could not be read");
	  }
	}
      }

      Iterator keys = encoderMap.keySet().iterator();
      while(keys.hasNext()){
	ImageEncoder encoder = (ImageEncoder)encoderMap.get(keys.next());
	javax.swing.filechooser.FileFilter filter = new DefaultFileFilter(encoder.getFileExtention(), encoder.getDescription());
	fileChooser.addChoosableFileFilter(filter);
      }

      // Check whether or not saving is allowed in the environment
      ps = CompositionFrame.class.getResourceAsStream("/com/sun/glf/res/config.properties");
      if(ps!=null){
	Properties config = new Properties();
	config.load(ps);
	String canSave = config.getProperty("canSave", "no");
	if(canSave.equalsIgnoreCase("sure"))
	  isSaveEnabled = true;
	else
	  isSaveEnabled = false;
      }
    }catch(Exception e){
    }
  }

  public CompositionFrame(String title){
    super(title);
    
    if(encoderMap==null)
      init();

    setSize(defaultSize);

    // Add popup menu on right click
    if(isSaveEnabled){
      addMouseListener(new MouseAdapter(){
	public void mouseClicked(MouseEvent evt){
	  if((evt.getModifiers()&evt.BUTTON3_MASK)!=0){ // Rigth click
	    Point p = getLocation();
	    menu.setLocation(p.x + evt.getX(), p.y + evt.getY());
	    menu.setVisible(true);
	  }
	  if((evt.getModifiers()&evt.BUTTON1_MASK)!=0){ // Left click
	    menu.setVisible(false);
	  }
	}
      });
    }

    menu = new JPopupMenu();
    JMenuItem save = new JMenuItem(SAVE_AS_IMAGE);
    menu.add(save);

    save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
        onSave();
      }
    });

    // Set monitor's border
    paintMonitor.setBorder(BorderFactory.createEmptyBorder(MONITOR_MARGIN, MONITOR_MARGIN, MONITOR_MARGIN, MONITOR_MARGIN));
  }

  /**
   * Handles save request
   */
  public void onSave(){
    menu.setVisible(false);

    //
    // 1. Get file from user
    //
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    int choice = fileChooser.showDialog(this, null);
    if(choice == JFileChooser.APPROVE_OPTION) {
      //
      // 2. Find out the format the user chose
      //
      File file = fileChooser.getSelectedFile();
      Object fileFilter = fileChooser.getFileFilter();
      ImageEncoder encoder = null;
      boolean saveAsBean=false;

      if(fileFilter==null){
	// Try to guess, based on the file extension
	String fileName = file.getName();
	int s = fileName.lastIndexOf('.');
	if(s!=-1){
	  String extension = fileName.substring(s);
	  if(extension.equalsIgnoreCase(".ser") && serializedFactory != null)
	    saveAsBean = true;
	  else
	    encoder = (ImageEncoder)encoderMap.get(extension);
	}
      }
      else{
	if(fileFilter instanceof DefaultFileFilter){
	  DefaultFileFilter filter = (DefaultFileFilter)fileChooser.getFileFilter();
	  encoder = (ImageEncoder)encoderMap.get(filter.getExtention());
	}
	else{
	  // The only other kind of file filter is the SerializedBeanFileFilter
	  saveAsBean = true;
	}
      }

      //
      // 3. Save according to format: image or bean
      //
      if(encoder!=null){
	// 
	// Save as Image
	//
	try{
	  FileOutputStream out = new FileOutputStream(file);
	  encoder.encode(comp.getOffscreen(), out);
	  out.flush();
	  out.close();
	}catch(IOException e){
	  JOptionPane.showMessageDialog(this, e.getMessage(), COULD_NOT_SAVE_IMAGE, JOptionPane.ERROR_MESSAGE);
	}
      }
      else if(saveAsBean){
	// 
	// Save as serialized stream (and text version for backup
	//
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
	    // Restore factory bean
	    //
	    ByteArrayInputStream bis = new ByteArrayInputStream(serializedFactory);
	    ObjectInputStream ois = new ObjectInputStream(bis);
	    Object bean = ois.readObject();
	    bis.close();
	    ois.close();

	    //
	    // Save in serialized format
	    //
	    FileOutputStream out = new FileOutputStream(file.getPath());
	    ObjectOutputStream s = new ObjectOutputStream(out);
	    s.writeObject(bean);
	    s.flush();
	    s.close();
	    out.close();
	    
	    //
	    // Save in text format
	    //
	    out = new FileOutputStream(file.getPath() + ".txt");
	    TextBeans.save(bean, out);
	    out.close();
	    JOptionPane.showMessageDialog(null, BEANS_SAVED);
	  }
	}catch(IOException e){
	  JOptionPane.showMessageDialog(null, ERROR_CANNOT_SERIALIZE_BEANS + e.getMessage());
	  e.printStackTrace();
	}catch(ClassNotFoundException e){
	  throw new Error(); // Should not happen...
	}
      }
      else{
	JOptionPane.showMessageDialog(null, ERROR_UNKNOWN_FORMAT + file.getName());
      }
    }
  }

  /**
   * Sets the Composition this frame should display
   */
  public void setComposition(Composition cmp){
    getContentPane().setBackground(getBackground());
    getContentPane().removeAll();
    comp = new CompositionComponent(cmp);

    // Request Component to prepare its offscreen buffer
    getContentPane().add(BorderLayout.NORTH, paintMonitor);
    pack();
    validate();

    paintMonitor.monitor(cmp);

    // The costly operation is preparing the offscreen buffer...
    try{
      comp.prepareOffscreen();
      getContentPane().add(comp);
    }catch(Exception e){
      JOptionPane.showMessageDialog(CompositionFrame.this, e.getMessage(), COULD_NOT_LOAD_COMPOSITION, JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
    finally{
      // Preparing the offscreen is finished, nothing left to monitor
      getContentPane().remove(paintMonitor);
      paintMonitor.monitor(null);
      pack();
      // Remove Serialized bean save option
      fileChooser.removeChoosableFileFilter(serializedBeanFilter);
      serializedFactory = null;
    }
  }

  /**
   * Requests this frame to load a new Composition
   * from the input builder
   */
  public synchronized void load(CompositionFactory factory){
    if(loadingThread!=null)
      loadingThread.pleaseDie();
    loadingThread = new CompositionLoadThread(factory);
    loadingThread.setPriority(Thread.currentThread().getPriority()-1);
    loadingThread.start();
  }

  class CompositionLoadThread extends Thread {
    private boolean dead = false;
  
    /** The factory which builds the Composition */
    private CompositionFactory factory;

    public CompositionLoadThread(CompositionFactory factory){
      super("Preview Thread " + curThread++);
      this.factory = factory;
    }

    public void pleaseDie(){
      dead = true;
    }

    public void run(){
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try{
	Composition cmp = factory.build();	
	byte serializedFactory[] = null;
	try{
	  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	  ObjectOutputStream os = new ObjectOutputStream(bos);
	  os.writeObject(factory);
	  os.close();
	  bos.close();
	  serializedFactory = bos.toByteArray();
	}catch(IOException e){
	  e.printStackTrace();
	  serializedFactory=null;
	}

	if(!dead){
	  setComposition(cmp);
	  if(serializedFactory!=null){
	    CompositionFrame.this.serializedFactory = serializedFactory;

	    // Add Serialized bean save option. Remove first, to make sure we do not
	    // add a ton of filters as many compositions are displayed in the frame.
	    fileChooser.removeChoosableFileFilter(serializedBeanFilter);
	    fileChooser.addChoosableFileFilter(serializedBeanFilter);
	  }
	}
      }catch(Exception e){
	JOptionPane.showMessageDialog(CompositionFrame.this, e.getMessage(), COULD_NOT_LOAD_COMPOSITION, JOptionPane.ERROR_MESSAGE);
	e.printStackTrace();
      }finally{
	if(!dead){
	  setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	  setVisible(true);
	}
      }
    }
  }

}

