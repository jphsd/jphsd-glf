/*
 * @(#)JAlphaSlider.java
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
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * A JAlphaSlider can be used to set a double value between 0 and 1.
 * The display is shown between 0 and 100%, which means that the setting
 * is actually allowing precision to 0.01
 *
 * @author        Vincent Hardy
 * @version       1.0, 10/26/1998
 */
public class JAlphaSlider extends GridBagPanel{
  /**
   * Used for change propagation
   */
  private PropertyChangeSupport alphaValueChangeSupport;

  /**
   * The value represented by this Component
   */
  private double alphaValue;

  /** 
   * Constrols whether or not this component is setting its 
   * value after a UI event
   */
  private boolean settingValue;
  
  /**
   * Constructor. Lays out components.
   */
  public JAlphaSlider(){
    alphaValueChangeSupport = new PropertyChangeSupport(this);

    final JSlider alpha = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
    final JLabel alphaValueStr = new JLabel();
    alphaValueStr.setHorizontalTextPosition(JLabel.CENTER);
    alphaValueStr.setHorizontalAlignment(SwingConstants.CENTER);
    Dimension ps = alpha.getPreferredSize();
    alpha.setPreferredSize(new Dimension(100, ps.height));
    JLabel tmp = new JLabel("100%");
    alphaValueStr.setPreferredSize(tmp.getPreferredSize());

    add(alpha, 0, 0, 1, 1, CENTER, HORIZONTAL, 0, 0);
    add(alphaValueStr, 1, 0, 1, 1, CENTER, HORIZONTAL, 0, 0);
    add(Box.createHorizontalGlue(), 4, 1, 1, 1, CENTER, HORIZONTAL, 1, 0);

    // Event handling
    alpha.addChangeListener(new ChangeListener(){
      public synchronized void stateChanged(ChangeEvent evt){
	int newVal = alpha.getValue();
	alphaValueStr.setText("" + newVal + "%");
	settingValue = true;
	setAlphaValue(newVal/100.);
	settingValue = false;
      }
    });

    addAlphaValueChangeListener(new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt){
	if(!settingValue){ // We did not originate this change
	  int newVal = (int)Math.rint(alphaValue*100);
	  alpha.setValue(newVal);
	}
      }
    });

  }

  /**
   * Sets the value this slider should display.
   * If the value is off limit (i.e. not between 0 and 1),
   * then an IllegalArgumentException is thrown
   */
  public void setAlphaValue(double alphaValue){
    if(alphaValue>1. || alphaValue<0.)
      throw new IllegalArgumentException("alphaValue is off limit : " + alphaValue);

    double oldValue = this.alphaValue;
    this.alphaValue = alphaValue;
    alphaValueChangeSupport.firePropertyChange("alphaValue", new Double(oldValue), new Double(alphaValue));
  }

  /**
   * @return current alpha value
   */
  public double getAlphaValue(){
    return alphaValue;
  }

  /**
   * Adds a listener for the alpha value
   */
  public void addAlphaValueChangeListener(PropertyChangeListener l){
    alphaValueChangeSupport.addPropertyChangeListener(l);
  }

  /**
   * Removes a listener for the alpha value
   */
  public void removeAlphaValueChangeListener(PropertyChangeListener l){
    alphaValueChangeSupport.removePropertyChangeListener(l);
  }

}
