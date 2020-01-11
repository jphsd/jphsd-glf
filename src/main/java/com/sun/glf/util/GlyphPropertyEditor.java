/*
 * @(#)GlyphPropertyEditor.java
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
 * Provides a UI Component to configure a Glyph
 * property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class GlyphPropertyEditor extends PropertyEditorSupport{
  static final String CHOOSE_GLYPH = "Choose Glyph";

  /** Glyph Chooser */
  GridBagPanel editor;

  /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
  public String getJavaInitializationString() {
    Glyph glyph = (Glyph)getValue();
    Font font = glyph.getFont();
    String str = "Font font = new Font(" + font.getFamily() + "," + font.getStyle() + ", " + font.getSize() + ");";
    str += "Glyph glyph = new Glyph(font, " + glyph.getChar() + ");";
    return str;
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
    if(editor == null){
      final GlyphPreview preview = new GlyphPreview(true);
      final JPanel boxOutline = new JPanel(new GridLayout(1,1,0,0));
      boxOutline.add(preview);
      boxOutline.setPreferredSize(new Dimension(33, 33));
      boxOutline.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
							     BorderFactory.createEmptyBorder(2,2,2,2)));

      class ValueChangeListener extends MouseAdapter implements PropertyChangeListener{
	// Synchronize value with UI
	public void mousePressed(MouseEvent evt){
	  if((evt.getModifiers()&evt.BUTTON1_MASK) != 0){
	    Glyph curVal = (Glyph)getValue();
	    Glyph newVal = GlyphChooser.showDialog(CHOOSE_GLYPH, (Glyph)getValue());
	    if(newVal!=null)
	      setValue(newVal);
	  }
	}

	public void propertyChange(PropertyChangeEvent evt){
	  Glyph newVal = (Glyph)getValue();
	  preview.setGlyph(newVal);
	  preview.repaint();
	}
      };

      ValueChangeListener listener = new ValueChangeListener();

      // Synchronize Value with UI
      preview.addMouseListener(listener);

      // Synchronize UI with value
      addPropertyChangeListener(listener);

      editor = new GridBagPanel();

      editor.add(boxOutline, 0, 0, 1, 1, editor.CENTER, editor.NONE, 0, 0);
      editor.add(Box.createHorizontalGlue(), 1, 1, 1, 1, editor.CENTER, editor.HORIZONTAL, 1, 0);
    }

    return editor;
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
  // Unit testing for GlyphChooser
  //
  public static void main(String args[]){
    Toolbox.swingDefaultsInit();
    GlyphChooser.showDialog("Glyph Chooser Unit Testing", null);
  }
  
}

/**
 * The GlyphPanel contains thumbnail of Glyphs
 */
class GlyphPanel extends GridBagPanel{
  int tnWidth = 18;
  int tnHeight = 18;
  int tnFontSize = 15;

  /** Set of thumbnails showing Glyphs */
  GlyphPreview thumbNails[];

  /** Currently selected thumbnail */
  GlyphPreview selected;

  /** Value of the first Glyph's Unicode value */
  char cStart = 0;

  /** Font used to produce Glyphs */
  Font font;

  /** iSelected change support */
  PropertyChangeSupport selectedChange;

  public GlyphPanel(){
    thumbNails = new GlyphPreview[0xff];
    GridBagPanel tumbNailPanel = new GridBagPanel();
    Insets is = new Insets(0, 0, 0, 0);

    MouseListener listener = new MouseAdapter(){
      public void mouseClicked(MouseEvent evt){
	GlyphPreview gp = (GlyphPreview)evt.getSource();
	setSelected(gp);
      }
    };

    Dimension ths = new Dimension(tnWidth, tnHeight);

    for(int i=0; i<10; i++){
      for(int j=0; j<25; j++){
	thumbNails[25*i + j] = new GlyphPreview(true);
	thumbNails[25*i + j].setPreferredSize(ths);
	add(thumbNails[25*i + j], j, i, 1, 1, CENTER, NONE, 0, 0, is);
	thumbNails[25*i + j].addMouseListener(listener);
      }
    }
    for(int i=0; i<5; i++){
      thumbNails[250+i] = new GlyphPreview(true);
      thumbNails[250+i].setPreferredSize(ths);
      add(thumbNails[250+i], i, 10, 1, 1, CENTER, NONE, 0, 0, is);
      thumbNails[250+i].addMouseListener(listener);
    }

    selectedChange = new PropertyChangeSupport(this);
    selected = thumbNails[0];
    selected.setBackground(Color.red);

    // 
    // Force white background
    //
    setBackground(Color.white);
  }

  public void addSelectedChangeListener(PropertyChangeListener l){
    selectedChange.addPropertyChangeListener(l);
  }

  public void removeselectedChangeListener(PropertyChangeListener l){
    selectedChange.removePropertyChangeListener(l);
  }

  public void setFont(Font font){
    this.font = font;

    resetGlyphDisplay();
  }

  private void resetGlyphDisplay(){
    Font tnFont = new Font(font.getName(), font.getStyle(), tnFontSize);
    if(thumbNails!=null){
      char n = (char)thumbNails.length;
      for(char i=0; i<n; i++){
	thumbNails[i].setGlyph(new Glyph(tnFont, (char)(cStart + i)));
      }
      repaint();
    }
  }

  public void setRange(char cStart){
    this.cStart = cStart;
    resetGlyphDisplay();
  }

  private void setSelected(GlyphPreview newSelected){
    GlyphPreview oldSelected = selected;
    selected = newSelected;
    oldSelected.setBackground(Color.white);
    newSelected.setBackground(Color.red);
    oldSelected.repaint();
    newSelected.repaint();
    selectedChange.firePropertyChange("selected", oldSelected, selected);
  }

  public void setSelected(int index){
    if(index<0 || index>=255)
      throw new IllegalArgumentException();

    setSelected(thumbNails[index]);
  }

  public Glyph getSelected(){
    Glyph glyph = selected.getGlyph();
    return new Glyph(font, glyph.getChar());
  }
}

/**
 * The GlyphChooser dialog allows selection of a Glyph from a list 
 * of available Glyphs
 */
class GlyphChooser implements GridBagConstants{
  static final String GLYPH_CHOOSER = "Glyph Chooser";
  static final String FONT_NAME = "Font Name: ";
  static final String FONT_SIZE = "Size: ";
  static final String RANGE = "Range: ";
  static final String ERROR_INVALID_FONT_SIZE = "Error: invalid font size : should be a number";
  static final String OK = "OK";
  static final String CANCEL = "Cancel";
  static final String TO = "to:";
  static final String PREDEFINED_RANGES = "Predefined Ranges:";

  // Predefined Unicode Ranges
  static final String BASIC_LATIN = "Basic Latin";
  static final String LATIN_1 = "Latin 1";
  static final String LATIN_EXTENDED_A = "Latin Extended-A";
  static final String LATIN_EXTENDED_B = "Latin Extended-B";
  static final String IPA_EXTENSIONS = "IPA Extensions";
  static final String SPACING_MODIFIERS = "Spacing Modifiers";
  static final String COMBINING_DIACRITICAL_MARKS = "Combining Diacritical Marks";
  static final String GREEK = "Greek";
  static final String CYRILLIC = "Cyrillic";
  static final String HEBREW = "Hebrew";
  static final String ARABIC = "Arabic";
  static final String DEVANAGARI = "Devanagari";
  static final String THAI = "Thai";
  static final String LATIN_EXTENDED_ADDITIONAL = "Latin Extended Additional";
  static final String GREEK_EXTENDED = "Greek Extended";
  static final String GENERAL_PUNCTUATION = "General Punctuation";
  static final String SUPERSCRIPTS_AND_SUBSCRIPTS = "Superscripts and Subscripts";
  static final String CURRENCY = "Currency";
  static final String LETTERLIKE_SYMBOLS = "Letterlike Symbols";
  static final String NUMBER_FORMS = "Number Forms";
  static final String ARROWS = "Arrows";
  static final String MATHEMATICAL_OPERATORS = "Mathematical Operators";
  static final String MISCELLANEOUS_TECHNICAL = "Miscellaneous Technical";
  static final String CONTROL_PICTURES = "Control Pictures";
  static final String ENCLOSED_ALPHANUMERICS = "Enclosed Alphanumerics";
  static final String BOX_DRAWING = "Box Drawing";
  static final String BLOCK_ELEMENTS = "Block Elements";
  static final String GEOMETRIC_SHAPES = "Geometric Shapes";
  static final String MISCELLANEOUS_SYMBOLS = "Miscellaneous Symbols";
  static final String DINGBATS = "Dingbats";
  static final String ALPHABETIC_PRES_FORMS = "Alphabetic Pres. Forms";
  static final String ARABIC_PRES_FORMS_A = "Arabic Pres Forms-A";
  static final String ARABIC_PRES_FORMS_B = "Arabic Pres Forms-B";

  static class UnicodeRange {
    public UnicodeRange(String desc, char cStart){
      this.desc = desc;
      this.cStart = cStart;
    }

    public String toString(){
      return desc;
    }

    String desc;
    char cStart;
  }

  /** Predefined Unicode Ranges **/
  static UnicodeRange ranges[] = { new UnicodeRange(BASIC_LATIN, (char)(0xffff & 0x000)),
				   new UnicodeRange(LATIN_1, (char)(0xffff & 0x0080)),
				   new UnicodeRange(LATIN_EXTENDED_A, (char)(0xffff & 0x0100)),
				   new UnicodeRange(LATIN_EXTENDED_B, (char)(0xffff & 0x0180)),
				   new UnicodeRange(IPA_EXTENSIONS, (char)(0xffff & 0x0250)),
				   new UnicodeRange(SPACING_MODIFIERS, (char)(0xffff & 0x02B0)),
				   new UnicodeRange(COMBINING_DIACRITICAL_MARKS, (char)(0xffff & 0x0300)),
				   new UnicodeRange(GREEK, (char)(0xffff & 0x0370)),
				   new UnicodeRange(CYRILLIC, (char)(0xffff & 0x0400)),
				   new UnicodeRange(HEBREW, (char)(0xffff & 0x0590)),
				   new UnicodeRange(ARABIC, (char)(0xffff & 0x0600)),
				   new UnicodeRange(DEVANAGARI, (char)(0xffff & 0x0900)),
				   new UnicodeRange(THAI, (char)(0xffff & 0x0E00)),
				   new UnicodeRange(LATIN_EXTENDED_ADDITIONAL, (char)(0xffff & 0x1E00)),
				   new UnicodeRange(GREEK_EXTENDED, (char)(0xffff & 0x1F00)),
				   new UnicodeRange(GENERAL_PUNCTUATION, (char)(0xffff & 0x2000)),
				   new UnicodeRange(SUPERSCRIPTS_AND_SUBSCRIPTS, (char)(0xffff & 0x2070)),
				   new UnicodeRange(CURRENCY, (char)(0xffff & 0x20A0)),
				   new UnicodeRange(LETTERLIKE_SYMBOLS, (char)(0xffff & 0x2100)),
				   new UnicodeRange(NUMBER_FORMS, (char)(0xffff & 0x2150)),
				   new UnicodeRange(ARROWS, (char)(0xffff & 0x2190)),
				   new UnicodeRange(MATHEMATICAL_OPERATORS, (char)(0xffff & 0x2200)),
				   new UnicodeRange(MISCELLANEOUS_TECHNICAL, (char)(0xffff & 0x2300)),
				   new UnicodeRange(CONTROL_PICTURES, (char)(0xffff & 0x2400)),
				   new UnicodeRange(ENCLOSED_ALPHANUMERICS, (char)(0xffff & 0x2460)),
				   new UnicodeRange(BOX_DRAWING, (char)(0xffff & 0x2500)),
				   new UnicodeRange(BLOCK_ELEMENTS, (char)(0xffff & 0x2580)),
				   new UnicodeRange(GEOMETRIC_SHAPES, (char)(0xffff & 0x25A0)),
				   new UnicodeRange(MISCELLANEOUS_SYMBOLS, (char)(0xffff & 0x2600)),
				   new UnicodeRange(DINGBATS, (char)(0xffff & 0x2700)),
				   new UnicodeRange(ALPHABETIC_PRES_FORMS, (char)(0xffff & 0xFB00)),
				   new UnicodeRange(ARABIC_PRES_FORMS_A, (char)(0xffff & 0xFB50)),
				   new UnicodeRange(ARABIC_PRES_FORMS_B, (char)(0xffff & 0xFB70)) };

  /** Possible sizes */
  static String fontSizes[] = {"8", "9", "10", "11", "12", "14", "16", "18",
			       "20", "22", "24", "26", "28", "32", "36", "48", "72"};


  /** The dialog itself */
  static JDialog dialog;
  
  /** Font list */
  static JComboBox fontList;

  /** Font size */
  static JComboBox fontSize;

  /** Predefined Ranges */
  static JComboBox predefinedRanges;

  /** Range start */
  static JTextField range;

  /** Range end */
  static JLabel rangeEnd;

  /** Glyph preview : full size display of current selection*/
  static GlyphPreview preview;

  /** Glyph preview of current character range */
  static GlyphPanel glyphPanel;
    
  /** Value returned from showDialog */
  static Glyph retVal;

  /** Default font */
  static final Font defaultFont = new Font("Times New Roman", Font.PLAIN, 12);

  /** Default Glyph */
  static final Glyph defaultGlyph = new Glyph(defaultFont, '\u2200');

  /** Font Name List */
  static String fontNames[];

  /** Name to Font map */
  static Hashtable fontMap;

  /**
   * This may take some time, as loading the fonts is time consuming.
   * Therefore, a 'wait' dialog is shown while the dialog is building.
   */
  static void buildDialog()
  {
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] allFonts = env.getAllFonts();
    int nFonts = allFonts!=null?allFonts.length:0;
    fontNames = new String[nFonts];
    fontMap = new Hashtable();
    for(int i=0; i<nFonts; i++){
      Font font = allFonts[i];
      fontNames[i] = font.getName();
      fontMap.put(fontNames[i], font);
    }

    dialog = new JDialog((Frame)null, GLYPH_CHOOSER, true);

    fontList = new JComboBox(fontNames);
    fontSize = new JComboBox(fontSizes);
    fontSize.setEditable(true);
    preview = new GlyphPreview(false);
    CharacterDocument doc = new CharacterDocument();
    range = new JTextField(doc, "", 4);
    rangeEnd = new JLabel("0000");
    predefinedRanges = new JComboBox(ranges);

    GridBagPanel content = new GridBagPanel();

    // Font selection
    content.add(new JLabel(FONT_NAME), 0, 0, 1, 1, WEST, NONE, 0, 0);
    content.add(fontList, 0, 1, 1, 1, CENTER, NONE, 0, 0);

    // Font size selection
    content.add(new JLabel(FONT_SIZE), 1, 0, 1, 1, WEST, NONE, 0, 0);
    content.add(fontSize, 1, 1, 1, 1, WEST, HORIZONTAL, 0, 0);

    // Predefined Ranges
    content.add(new JLabel(PREDEFINED_RANGES), 2, 0, 1, 1, WEST, NONE, 0, 0);
    content.add(predefinedRanges, 2, 1, 1, 1, WEST, NONE, 0, 0);

    // Unicode range
    content.add(new JLabel(RANGE), 3, 0, 1, 1, WEST, NONE, 0, 0);
    content.add(range, 3, 1, 1, 1, WEST, HORIZONTAL, 0, 0);

    // Add padding for header part.
    content.add(Box.createHorizontalGlue(), 5, 0, 1, 1, WEST, HORIZONTAL, 1, 0);

    // Thumb nails
    glyphPanel = new GlyphPanel();
    // content.add(glyphPanel, 0, 2, 6, 1, CENTER, NONE, 0, 0);

    // Limit sizes
    preview.setPreferredSize(new Dimension(glyphPanel.getPreferredSize().height, 
					   glyphPanel.getPreferredSize().height));
    range.setPreferredSize(new Dimension(range.getPreferredSize().width,
					 predefinedRanges.getPreferredSize().height));

    // Preview panel: Thumbnails and Full Size preview
    GridBagPanel previewPanel = new GridBagPanel();
    previewPanel.add(glyphPanel, 0, 0, 1, 1, CENTER, BOTH, 1, 1);
    previewPanel.add(preview, 1, 0, REMAINDER, 1, CENTER, BOTH, 1, 1);
    glyphPanel.setBackground(Color.white);
    preview.setBackground(Color.white);

    content.add(previewPanel, 0, 2, REMAINDER, 1, CENTER, BOTH, 1, 1);
    
    // Button panel.
    JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
    JButton okButton = new JButton(OK);
    JButton cancelButton = new JButton(CANCEL);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    GridBagPanel bp = new GridBagPanel();
    bp.add(Box.createHorizontalGlue(), 0, 0, 1, 1, EAST, HORIZONTAL, 1, 0);
    bp.add(buttonPanel, 0, 0, 1, 1, EAST, NONE, 0, 0);

    //
    // Set borders
    //
    content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),
							     BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
												BorderFactory.createEmptyBorder(5,5,5,5))));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
    glyphPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
							     BorderFactory.createEmptyBorder(5,5,5,5)));
    preview.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
							     BorderFactory.createEmptyBorder(5,5,5,5)));

    dialog.setVisible(false);
    dialog.setModal(true);

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
    fontList.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onFontChange();
      }
    });

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
	retVal = preview.getGlyph();
	dialog.dispose();
      }
    });

    dialog.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	dialog.setVisible(false);
	dialog.dispose();
      }
    });

    glyphPanel.addSelectedChangeListener(new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt){
	Glyph glyph = glyphPanel.getSelected();
	preview.setGlyph(glyph);
      }
    });

    range.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	char cStart = (char)Integer.parseInt(range.getText(), 16);
	glyphPanel.setRange(cStart);
	preview.setGlyph(glyphPanel.getSelected());
	char cEnd = Character.MAX_VALUE;
	if(Character.MAX_VALUE-cStart>=255){
	  cEnd = (char)(cStart + 255);
	  rangeEnd.setText(Integer.toHexString(cEnd));
	}
      }
    });

    predefinedRanges.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	UnicodeRange sel = (UnicodeRange)predefinedRanges.getSelectedItem();
	if(sel!=null){
	  char cStart = sel.cStart;
	  range.setText("" + Integer.toHexString(0xffff & cStart));
	  glyphPanel.setRange(cStart);
	  preview.setGlyph(glyphPanel.getSelected());
	  char cEnd = Character.MAX_VALUE;
	  if(Character.MAX_VALUE-cStart>=255){
	    cEnd = (char)(cStart + 255);
	    rangeEnd.setText(Integer.toHexString(cEnd));
	  }
	}
      }
    });

    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }


  public static Glyph showDialog(String dialogTitle, Glyph defaultGlyph){
    if(dialog==null)
      buildDialog();

    //
    // Set title
    //
    dialog.setTitle(dialogTitle);

    //
    // Set font we start with
    //
    if(defaultGlyph==null)
      defaultGlyph = GlyphChooser.defaultGlyph;

    //
    // Initialize with default Glyph values
    //

    // Font name
    Font font = defaultGlyph.getFont();
    String fontName = bindName(font.getFontName());
    fontList.setSelectedItem(fontName);

    // Font size
    String fontSizeStr = "" + font.getSize();
    fontSize.setSelectedItem(fontSizeStr);

    // Character range
    char c = defaultGlyph.getChar();
    char cStart = (char)((c/0xff)*0xff);
    glyphPanel.setRange(cStart);

    // Character selection
    glyphPanel.setSelected(c%0xff);
    
    // Preview
    preview.setGlyph(defaultGlyph);

    // Range
    range.setText("" + Integer.toHexString(0xffff & cStart));

    //
    //
    // Show dialog
    //
    // dialog.toFront();
    dialog.setVisible(true);

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
    try{
      String fontName = (String)fontList.getSelectedItem();
      int size = Integer.parseInt((String)fontSize.getSelectedItem());
      Font font = (Font)fontMap.get(fontName);
      
      // WORK AROUND : TO BE REMOVED WHEN FONT BUG FIXED
      font = newFont(fontName, size+1);
      // END WORK AROUND
      
      font = newFont(fontName, size);
      glyphPanel.setFont(font);
      Glyph glyph = preview.getGlyph();
      if(glyph!=null)
	preview.setGlyph(new Glyph(font, glyph.getChar()));
      else
	preview.setGlyph(new Glyph(font, '0'));
      preview.repaint();
    }catch(NumberFormatException e){
      JOptionPane.showMessageDialog(null, ERROR_INVALID_FONT_SIZE);
      fontSize.requestFocus();
    }
  }

  private static Font newFont(String fontName, int size){
    Font font = (Font)fontMap.get(fontName);
    Map attrs = new Hashtable();
    attrs.put(TextAttribute.FAMILY, font.getFamily());
    attrs.put(TextAttribute.SIZE, new Float(size));
    Font newFont  = new Font(attrs);
    return newFont;
  }

}

class GlyphPreview extends JComponent {
  RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
					 RenderingHints.VALUE_ANTIALIAS_ON);

  BufferedImage buffer;
  Graphics2D g;
  boolean fixedSize;
  Glyph glyph;

  public GlyphPreview(boolean fixedSize){
    this.fixedSize = fixedSize;
    setPreferredSize(new Dimension(15, 15)); // Can be overridden by a client.
  }

  public void setGlyph(Glyph glyph){
    Glyph oldGlyph = this.glyph;

    this.glyph = glyph;

    if(!this.glyph.equals(oldGlyph))
      prepareBuffer();

    setToolTipText("\\u" + Integer.toHexString(glyph.getChar()));
  }

  public void setForeground(Color color){
    super.setForeground(color);
    buffer = null;
  }

  public void setBackground(Color color){
    super.setBackground(color);
    buffer = null;
  }

  static int c = 0;

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
    Shape shape = glyph!=null?glyph.getShape():null;
    if(shape!=null){
      if(fixedSize){
	// Size to fit
	Rectangle bounds = shape.getBounds();
	float scale = 0;
	if(bounds.width!=0) 
	  scale = size.width/(float)bounds.width;
	if(bounds.height!=0) 
	  scale = (float)Math.min(scale, size.height/(float)bounds.height);

	if(scale<1){
	  AffineTransform t = new AffineTransform();
	  t.scale(scale, scale);
	  t.translate(-bounds.width/2, -bounds.height/2);
	  shape = t.createTransformedShape(shape);
	}
      }

      ShapeLayer layer = new ShapeLayer(cmp, shape, renderer, Position.CENTER);
      ShapeLayer rect = new ShapeLayer(cmp, new Rectangle(0, 0, size.width, size.height), new FillRenderer(getBackground()));
      cmp.setLayers(new Layer[]{rect, layer});
      cmp.setRenderingHints(rh);
      cmp.paint(g); 
      g.setTransform(new AffineTransform());
      paintBorder(g);
    }
    repaint();
  }

  public Glyph getGlyph(){
    return glyph;
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
