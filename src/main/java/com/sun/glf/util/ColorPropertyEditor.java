/*
 * @(#)ColorPropertyEditor.java
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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.sun.glf.util.*;

/**
 * Provides a UI Component to configure a Color
 * property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class ColorPropertyEditor extends PropertyEditorSupport{
  static final String CHANGE = "Change ...";
  static final String CHOOSE_COLOR = "Choose Color";
  static final String TOOLTIP_SET_TRANSPARENCY = "Sets color opacity";

  /** Color editor */
  GridBagPanel colorChooser;

  /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
  public String getJavaInitializationString() {
    Color color = (Color)getValue();
    return "new Color(" + color.getRed() + "," + color.getGreen() + ", " + color.getBlue() + ", " + color.getAlpha() + ")";
  }

  /**
   * Work around Color serialization problem
   */
  public Object getValue(){
    Color c = (Color)super.getValue();
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
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
   * The Color Property editor displays a color box and a "Change" button
   * which lets the user modify the Color value using a ColorChooser
   *
   * @return A java.awt.Component that will allow a human to directly
   *      edit the current property value.  May be null if this is
   *	    not supported.
   */
  public java.awt.Component getCustomEditor() {
    if(colorChooser == null){
      colorChooser = new GridBagPanel();

      final ColorBox colorBox = new ColorBox();

      final JPanel boxOutline = new JPanel(new GridLayout(1,1,0,0));
      boxOutline.add(colorBox);
      boxOutline.setPreferredSize(new Dimension(25, 25));
      boxOutline.setBorder(BorderFactory.createEtchedBorder());

      final JSlider transparency = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
      final JLabel transparencyValue = new JLabel();
      transparencyValue.setHorizontalTextPosition(JLabel.CENTER);
      transparencyValue.setHorizontalAlignment(SwingConstants.CENTER);

      Dimension ps = transparency.getPreferredSize();
      transparency.setPreferredSize(new Dimension(100, ps.height));

      class ValueChangeListener extends MouseAdapter implements PropertyChangeListener, ChangeListener{
	// Synchronize value with UI
	public void mouseClicked(MouseEvent evt){
	  Color newVal = JColorChooser.showDialog(colorBox, CHOOSE_COLOR, (Color)getValue());
	  if(newVal!=null){
	    newVal = new Color(newVal.getRed(), newVal.getGreen(), newVal.getBlue(), transparency.getValue());
	    setValue(newVal);
	  }
	}

	public void propertyChange(PropertyChangeEvent evt){
	  Color newVal = (Color)getValue();
	  colorBox.setBackground(newVal);
	  transparency.setValue(newVal.getAlpha());
	  transparencyValue.setText("" + (int)Math.rint(100.*newVal.getAlpha()/255.) + "%");
	  colorBox.repaint();
	  transparency.repaint();
	  transparencyValue.repaint();
	}

	public void stateChanged(ChangeEvent evt){
	  Color oldVal = (Color)getValue();
	  Color newVal = new Color(oldVal.getRed(), oldVal.getGreen(), oldVal.getBlue(), transparency.getValue());
	  transparencyValue.setText("" + (int)Math.rint(100.*newVal.getAlpha()/255.) + "%");
	  transparencyValue.repaint();
	  setValue(newVal);
	}
      };

      ValueChangeListener listener = new ValueChangeListener();

      // Synchronize Value with UI
      colorBox.addMouseListener(listener);

      // Synchronize UI with value
      addPropertyChangeListener(listener);

      // Take transparency changes into account
      transparency.addChangeListener(listener);

      // Layout
      JLabel tmp = new JLabel("100%");
      transparencyValue.setPreferredSize(tmp.getPreferredSize());

      colorChooser.add(boxOutline, 0, 0, 1, 1, colorChooser.CENTER, colorChooser.NONE, 0, 0);
      colorChooser.add(Box.createHorizontalStrut(5), 1, 0, 1, 1, colorChooser.CENTER, colorChooser.NONE, 0, 0);
      colorChooser.add(transparency, 2, 0, 1, 1, colorChooser.CENTER, colorChooser.HORIZONTAL, 0, 0);
      colorChooser.add(transparencyValue, 3, 0, 1, 1, colorChooser.CENTER, colorChooser.HORIZONTAL, 0, 0);
      colorChooser.add(Box.createHorizontalGlue(), 4, 1, 1, 1, colorChooser.CENTER, colorChooser.HORIZONTAL, 1, 0);
      
      // ToolTip on transparency slider
      transparency.setToolTipText(TOOLTIP_SET_TRANSPARENCY);
    }

    return colorChooser;
  }

  /**
   * Determines whether the propertyEditor can provide a custom editor.
   *
   * @return  True if the propertyEditor can provide a custom editor.
   */
  public boolean supportsCustomEditor() {
    return true;
  }
  
}

class ColorBox extends JComponent {
  static Paint bkgPaint;

  static {
    BufferedImage background = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = background.createGraphics();
    g.setPaint(Color.white);
    g.fillRect(0, 0, 8, 8);
    g.setPaint(Color.gray);
    g.fillRect(0, 0, 4, 4);
    g.fillRect(4, 4, 8, 8);
    g.dispose();

    bkgPaint = new TexturePaint(background, new Rectangle(0, 0, 8, 8));
  }
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    Dimension dim = getSize();
    g.setPaint(bkgPaint);
    g.fillRect(0, 0, dim.width, dim.height);
    g.setComposite(AlphaComposite.SrcOver);
    g.setPaint(getBackground());
    g.fillRect(0, 0, dim.width, dim.height);
  }
}
