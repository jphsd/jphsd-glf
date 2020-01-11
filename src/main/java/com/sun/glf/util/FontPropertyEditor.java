/*
 * @(#)FontPropertyEditor.java
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
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Provides a UI Component to configure a Font
 * property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class FontPropertyEditor extends PropertyEditorSupport{
  static{
    FontChooser.loadFontsBackground();
  }

  static final String CHANGE = "Change...";
  static final String CHOOSE_FONT = "Font";

  /** Font editor panel */
  GridBagPanel fontChooser;

  /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
  public String getJavaInitializationString() {
    Font Font = (Font)getValue();
    return "new Font(" + Font.getFamily() + "," + Font.getStyle() + ", " + Font.getSize() + ")";
  }

  /**
   * Gets the property value as a string suitable for presentation
   * to a human to edit.
   *
   * @return The property value as a string suitable for presentation
   *       to a human to edit.
   * <p>   Returns "null" is the value can't be expressed as a string.
   * <p>   If a non-null value is returned, then the PropertyEditor should
   *	     be prepared to parse that string back in setAsText().
   */
  public String getAsText() {
    return null;
  }

  /**
   * Sets the property value by parsing a given String.  May raise
   * java.lang.IllegalArgumentException if either the String is
   * badly formatted or if this kind of property can't be expressed
   * as text.
   *
   * @param text  The string to be parsed.
   */
  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    throw new IllegalArgumentException();
  }

  /**
   * A PropertyEditor may chose to make available a full custom Component
   * that edits its property value.  It is the responsibility of the
   * PropertyEditor to hook itself up to its editor Component itself and
   * to report property value changes by firing a PropertyChange event.
   * <P>
   * The higher-level code that calls getCustomEditor may either embed
   * the Component in some larger property sheet, or it may put it in
   * its own individual dialog, or ...
   *
   * @return A java.awt.Component that will allow a human to directly
   *      edit the current property value.  May be null if this is
   *	    not supported.
   */
  public java.awt.Component getCustomEditor() {
    if(fontChooser == null){
      final FontPreview preview = new FontPreview(null, true);
      final JPanel boxOutline = new JPanel(new GridLayout(1,1,0,0));
      boxOutline.add(preview);
      boxOutline.setPreferredSize(new Dimension(200, 30));
      boxOutline.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
							     BorderFactory.createEmptyBorder(2,2,2,2)));

      class ValueChangeListener extends MouseAdapter implements PropertyChangeListener{
	// Synchronize value with UI
	public void mousePressed(MouseEvent evt){
	  if((evt.getModifiers()&evt.BUTTON1_MASK) != 0){
	    Font curVal = (Font)getValue();
	    Font newVal = FontChooser.showDialog(CHOOSE_FONT, (Font)getValue());
	    if(newVal!=null)
	      setValue(newVal);
	  }
	  else{
	    Font curVal = (Font)getValue();
	    // System.out.println("Font value is : " + curVal.getName() + " " + curVal.getSize());
	  }
	}

	public void propertyChange(PropertyChangeEvent evt){
	  Font newVal = (Font)getValue();
	  preview.setFont(newVal);
	  preview.repaint();
	}
      };

      ValueChangeListener listener = new ValueChangeListener();

      // Synchronize Value with UI
      preview.addMouseListener(listener);

      // Synchronize UI with value
      addPropertyChangeListener(listener);

      fontChooser = new GridBagPanel();

      fontChooser.add(boxOutline, 0, 0, 1, 1, fontChooser.CENTER, fontChooser.NONE, 0, 0);
      fontChooser.add(Box.createHorizontalGlue(), 1, 1, 1, 1, fontChooser.CENTER, fontChooser.HORIZONTAL, 1, 0);
    }

    return fontChooser;
  }

  /**
   * Determines whether the propertyEditor can provide a custom editor.
   *
   * @return  True if the propertyEditor can provide a custom editor.
   */
  public boolean supportsCustomEditor() {
    return true;
  }

  public Object getValue(){
    return super.getValue();
  }
  
  //
  // Unit testing for FontChooser
  //
  public static void main(String args[]){
    FontChooser.showDialog("Font Chooser Unit Testing", null);
  }
  
}

/**
 * The FontChooser dialog allows selection of a Font from a list 
 * of available fonts
 */
class FontChooser {
  static final String REGULAR = "Regular";
  static final String ITALIC = "Italic";
  static final String BOLD = "Bold";
  static final String FONT_CHOOSER = "Font Chooser";
  static final String FONT_NAME = "Name: ";
  static final String FONT_STYLE = "Style";
  static final String FONT_SIZE = "Size: ";
  static final String ERROR_INVALID_FONT_SIZE = "Error: invalid font size : should be a number";
  static final String OK = "OK";
  static final String CANCEL = "Cancel";
  static final String LOADING_FONTS_PLEASE_WAIT = "Loading fonts, please wait";

  /** Font Name List */
  static String fontNames[];

  /** Name to Font map */
  static Hashtable fontMap;

  /** Possible styles */
  // static String fontStyles[] = { REGULAR, ITALIC, BOLD, BOLD_ITALIC };
  static JCheckBox bold;
  static JCheckBox italic;

  /** Possible sizes */
  static String fontSizes[] = {"8", "9", "10", "11", "12", "14", "16", "18",
			       "20", "22", "24", "26", "28", "32", "36", "48", "72"};


  /** The dialog itself */
  static JDialog dialog;
  
  /** Font list */
  static JList fontList;

  /** Font size */
  static JComboBox fontSize;

  /** Font preview */
  static FontPreview preview;

  /** Value returned from showDialog */
  static Font retVal;

  /** Default font */
  static final Font defaultFont = new Font("Dialog", Font.PLAIN, 12);

  /*
   * Font loading control
   */
  static class LoadMonitor {
    boolean loaded;
    boolean loading;
  }

  static LoadMonitor loadMonitor = new LoadMonitor();
  static private Font[] allFonts;

  /**
   * Loads fonts
   */
  public static void loadFontsBackground(){
    if(!loadMonitor.loaded && !loadMonitor.loading){
      boolean doLoad = false;
      synchronized(loadMonitor){
	if(!loadMonitor.loading){
	  loadMonitor.loading = true;
	  doLoad = true;
	}
      }

      if(doLoad){
	Thread th = new Thread(){
	  public void run(){
	    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    allFonts = env.getAllFonts();
	    int nFonts = allFonts!=null?allFonts.length:0;
	    fontNames = new String[nFonts];
	    fontMap = new Hashtable();
	    String currentFamily = "";
	    int j=0;
	    for(int i=0; i<nFonts; i++){
	      Font font = allFonts[i];
	      // Note that only one font is kept in the hashtable
	      // per family.
	      if(!currentFamily.equals(font.getFamily())){
		currentFamily = font.getFamily();
		fontNames[j] = currentFamily;
		fontMap.put(currentFamily, font);
		j++;
	      }
	    }

	    String tmp[] = fontNames;
	    fontNames = new String[j];
	    System.arraycopy(tmp, 0, fontNames, 0, j);

	    synchronized(loadMonitor){
	      loadMonitor.loaded = true;
	      loadMonitor.loading = false;
	      loadMonitor.notifyAll();
	    }
	  };
	};
	th.setPriority(Thread.currentThread().getPriority()-1);
	th.start();
      }
    }
  }

  /**
   * This may take some time, as loading the fonts is time consuming.
   * Therefore, a 'wait' dialog is shown while the dialog is building.
   */
  static void buildDialog()
  {
    JDialog waitDialog = new JDialog((Frame)null, FONT_CHOOSER, false);

    if(!loadMonitor.loaded){
      JLabel pleaseWait = new JLabel(LOADING_FONTS_PLEASE_WAIT);
      waitDialog.getContentPane().add(pleaseWait);
      pleaseWait.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),
							      BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
												 BorderFactory.createEmptyBorder(5,5,5,5))));
      pleaseWait.setFont(new Font("Dialog", Font.PLAIN, 32));
      waitDialog.pack();
      waitDialog.setVisible(true);
      waitDialog.paint(waitDialog.getGraphics());
      waitDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      loadFontsBackground();
      synchronized(loadMonitor){
	if(!loadMonitor.loaded){
	  loadFontsBackground();
	  try{
	    loadMonitor.wait();
	  }catch(InterruptedException e){
	  }
	}
      }
    }

    waitDialog.setVisible(false);
    if(!loadMonitor.loaded)
      throw new Error(); // This should never happen.

    dialog = new JDialog((Frame)null, FONT_CHOOSER, true);

    fontList = new JList(fontNames);
    bold = new JCheckBox(BOLD);
    italic = new JCheckBox(ITALIC);
    fontSize = new JComboBox(fontSizes);
    fontSize.setEditable(true);
    preview = new FontPreview(defaultFont, false);

    //
    // Style pad
    //
    GridBagPanel stylePad = new GridBagPanel();
    stylePad.add(bold, 0, 0, 1, 1, stylePad.WEST, stylePad.NONE, 0, 0);
    stylePad.add(italic, 0, 1, 1, 1, stylePad.WEST, stylePad.NONE, 0, 0);

    // Limit sizes
    fontList.setVisibleRowCount(5);
    preview.setPreferredSize(new Dimension(200, 50));

    GridBagPanel content = new GridBagPanel();
    GridBagPanel previewPanel = new GridBagPanel();
    previewPanel.add(preview, 0, 0, 1, 1, content.CENTER, content.BOTH, 1, 1);

    GridBagPanel namePanel = new GridBagPanel();

    namePanel.add(new JLabel(FONT_NAME), 0, 0, 1, 1, content.WEST, content.NONE, 0, 0);
    namePanel.add(new JScrollPane(fontList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
		0, 1, 1, 1, content.CENTER, content.NONE, 0, 0);

    content.add(namePanel, 0, 0, 1, 2, content.WEST, content.NONE, 0, 0);

    content.add(new JLabel(FONT_SIZE), 1, 0, 1, 1, content.WEST, content.NONE, 0, 0);
    content.add(fontSize, 2, 0, 1, 1, content.WEST, content.HORIZONTAL, 0, 0);
    content.add(Box.createHorizontalGlue(), 3, 0, 1, 1, content.WEST, content.HORIZONTAL, 1, 0);

    content.add(stylePad, 1, 1, 2, 1, content.NORTHWEST, content.HORIZONTAL, 0, 0);

    content.add(previewPanel, 0, 3, content.REMAINDER, 1, content.CENTER, content.BOTH, 1, 1);
    
    JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
    JButton okButton = new JButton(OK);
    JButton cancelButton = new JButton(CANCEL);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    GridBagPanel bp = new GridBagPanel();
    bp.add(Box.createHorizontalGlue(), 0, 0, 1, 1, bp.EAST, bp.HORIZONTAL, 1, 0);
    bp.add(buttonPanel, 0, 0, 1, 1, bp.EAST, bp.NONE, 0, 0);

    //
    // Set borders
    //
    content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),
							     BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
												BorderFactory.createEmptyBorder(5,5,5,5))));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
    previewPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
							     BorderFactory.createEmptyBorder(5,5,5,5)));

    stylePad.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
							FONT_STYLE,
							TitledBorder.LEFT,
							TitledBorder.TOP));

    // dialog.setVisible(false);
    // dialog.setModal(true);

    Container pane = dialog.getContentPane();
    pane.removeAll();

    pane.setLayout(new BorderLayout());
    pane.add(BorderLayout.CENTER, content);
    pane.add(BorderLayout.SOUTH, bp);
    dialog.pack();
    
    // 
    // Set event handlers
    //

    // Font name change
    fontList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent evt){
	onFontChange();
      }
    });

    ActionListener checkBoxListener = new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onFontChange();
      }
    };

    bold.addActionListener(checkBoxListener);
    italic.addActionListener(checkBoxListener);

    // Font size change
    fontSize.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onFontChange();
      }
    });

    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	retVal = null;
	dialog.dispose();
      }
    });

    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	retVal = preview.getFont();
	dialog.dispose();
      }
    });

    dialog.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	dialog.setVisible(false);
	dialog.dispose();
      }
    });

    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }


  public static Font showDialog(String dialogTitle, Font defaultFont){
    if(dialog==null)
      buildDialog();

    //
    // Set title
    //
    dialog.setTitle(dialogTitle);

    //
    // Set font we start with
    //
    if(defaultFont==null)
      defaultFont = FontChooser.defaultFont;

    String fontName = bindName(defaultFont.getFamily());
    fontList.setSelectedValue(fontName, true);
    String fontSizeStr = "" + defaultFont.getSize();
    fontSize.setSelectedItem(fontSizeStr);

    bold.setSelected(defaultFont.isBold());
    italic.setSelected(defaultFont.isItalic());
      
    preview.setFont(defaultFont);

    //
    // Show dialog
    //
    // dialog.toFront();
    dialog.show(); // setVisible(true);

    return retVal;
  }

  private static String bindName(String fontName){
    if(fontMap.get(fontName)==null)
      fontName = fontNames[0];
    return fontName;
  }

  /**
   * Assuming that one of the font settings has change, this member
   * creates a new Font object from the UI settings and repaints
   * the preview component.
   */
  private static void onFontChange(){
    if(dialog.isVisible()){
      try{
	int iFont = fontList.getMinSelectionIndex();
	int size = Integer.parseInt((String)fontSize.getSelectedItem());
	Font font = (Font)fontMap.get(fontNames[iFont]);
	
	// WORK AROUND : TO BE REMOVED WHEN FONT BUG FIXED
	// font = newFont(fontNames[iFont], size);
	font = newFont(fontNames[iFont], size+1);
	preview.setFont(font);
	// END WORK AROUND

	font = newFont(fontNames[iFont], size);
	preview.setFont(font);
	preview.repaint();
      }catch(NumberFormatException e){
	JOptionPane.showMessageDialog(null, ERROR_INVALID_FONT_SIZE);
	fontSize.requestFocus();
      }
    }
  }

  private static Font newFont(String fontName, int size){
    Font font = (Font)fontMap.get(fontName);
    Map attrs = new Hashtable();
    attrs.put(TextAttribute.FAMILY, font.getFamily());
    attrs.put(TextAttribute.SIZE, new Float(size));

    if(bold.isSelected())
      attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    else
      attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);

    if(italic.isSelected())
      attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    else
      attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);

    Font newFont  = new Font(attrs);

    return newFont;
  }

}

class FontPreview extends JComponent {
  Font font, displayFont;
  RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
					 RenderingHints.VALUE_ANTIALIAS_ON);

  static final Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
  static final String string = "The quick brown fox";
  static final int displayFontSize = 20;

  BufferedImage buffer;
  Graphics2D g;
  boolean fixedSize;

  public FontPreview(Font font, boolean fixedSize){
    this.fixedSize = fixedSize;
    setPreferredSize(new Dimension(200, 30)); // Can be overridden by a client.
    setFont(font);
  }

  public void setFont(Font font){
    Font oldFont = this.font;

    if(font == null)
      this.font = defaultFont;
    else
      this.font = font;

    if(fixedSize)
      displayFont = new Font(this.font.getName(), this.font.getStyle(), displayFontSize);
    else
      displayFont =  this.font;

    if(!this.font.equals(oldFont))
      prepareBuffer();

    String toolTip = this.font.getFontName() + " " + this.font.getSize();
    if(this.font.isBold()) 
      toolTip += " Bold";
    if(this.font.isItalic())
      toolTip += " Italic";

    setToolTipText(toolTip);
  }

  private void prepareBuffer(){
    Dimension size = getSize();
    Dimension pSize = getPreferredSize();
    if(size.width<=0) size.width = pSize.width;
    if(size.height<=0) size.height = pSize.height;

    if(buffer == null ||
       size.width != buffer.getWidth() || 
       size.height != buffer.getHeight() ){
      buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      g = buffer.createGraphics();
    }
    
    g.setTransform(new AffineTransform());
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(0, 0, size.width, size.height);
    g.setComposite(AlphaComposite.SrcOver);
    LayerComposition cmp = new LayerComposition(getSize());
    FillRenderer renderer = new FillRenderer(getForeground()!=null?getForeground():Color.black);
    TextLayer layer = new TextLayer(cmp, string, displayFont, renderer, Position.LEFT, Integer.MAX_VALUE, TextAlignment.CENTER);
    cmp.setLayers(new Layer[]{layer});
    cmp.setRenderingHints(rh);
    cmp.paint(g); 
    repaint();
  }

  public Font getFont(){
    return font;
  }

  public void setSize(Dimension dim){
    super.setSize(dim);
    prepareBuffer();
  }

  public void paint(Graphics g){
    Dimension size = getSize();

    if(buffer == null ||
       size.width != buffer.getWidth() || 
       size.height != buffer.getHeight() )
      prepareBuffer();
    
    g.drawImage(buffer, 0, 0, null);
  }    
}
