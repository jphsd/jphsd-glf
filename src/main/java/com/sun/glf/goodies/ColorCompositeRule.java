/*
 * @(#)ColorCompositeRule.java
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

package com.sun.glf.goodies;

import java.awt.geom.AffineTransform;
import java.beans.*;
import java.io.*;

import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Defines the Composition rules for a ColorComposition object.  
 * The class defines several final variables, and the API uses the ones
 * of type <code>ColorCompositionRule</code> as API parameters. The <code>int</code> and <code>String
 * </code> types are used as convenience. Strings are used for the <code>toString</code>
 * implementation and the <code>toInt</code> member allows switching on an <code>Anchor
 * </code> value: <p> <blockcode><pre>
 * ColorCompositeRule anchor = ....;
 * switch(ColorCompositeRule.toInt()){
 *   case ColorCompositeRule.HUE:
 *   ....
 *   break;
 *   case ColorCompositeRule.COLOR:
 *   ....
 * </pre></blockcode>
 *
 * @author        Vincent Hardy
 * @version       1.0, 01/28/1999
 * @see           com.sun.glf.goodies.ColorComposite
 */
public final class ColorCompositeRule implements Serializable{
  /*
   * ColorCompositeRule types.
   */
  public static final int COLOR_COMPOSITE_HUE = 0;
  public static final int COLOR_COMPOSITE_SATURATION = 1;
  public static final int COLOR_COMPOSITE_BRIGHTNESS = 2;
  public static final int COLOR_COMPOSITE_COLOR = 3;

  /*
   * Strings used to get a more readable String when
   * a ColorCompositeRule is displayed in a property editor.
   */
  public static final String HUE_STR = "Hue";
  public static final String SATURATION_STR = "Saturation";
  public static final String BRIGHTNESS_STR = "Brightness";
  public static final String COLOR_STR = "Color";

  /*
   * Anchor values
   */
  public static final ColorCompositeRule HUE = new ColorCompositeRule(COLOR_COMPOSITE_HUE, HUE_STR);
  public static final ColorCompositeRule BRIGHTNESS = new ColorCompositeRule(COLOR_COMPOSITE_BRIGHTNESS, BRIGHTNESS_STR);
  public static final ColorCompositeRule SATURATION = new ColorCompositeRule(COLOR_COMPOSITE_SATURATION, SATURATION_STR);
  public static final ColorCompositeRule COLOR = new ColorCompositeRule(COLOR_COMPOSITE_COLOR, COLOR_STR);
  
  /**
   * All values
   */
  public static final ColorCompositeRule[] enumValues = {HUE, BRIGHTNESS, SATURATION, COLOR};

  private String desc;
  private int val;

  /** 
   * Constructor is private so that no other instances than
   * the one in the enumeration can be created.
   * @see #readResolve
   */
  private ColorCompositeRule(int val, String desc){
    this.desc = desc;
    this.val = val;
  }

  /**
   * @return description
   */
  public String toString(){
    return desc;
  }

  /**
   * Convenience for enumeration switching
   * @return value.
   */
  public int toInt(){
    return val;
  }

  /**
   * This is called by the serialization code before it returns an unserialized
   * object. To provide for unicity of instances, the instance that was read
   * is replaced by its static equivalent. See the serialiazation specification
   * for further details on this method's logic.
   */
  public Object readResolve() {
    switch(val){
    case COLOR_COMPOSITE_HUE:
      return HUE;
    case COLOR_COMPOSITE_BRIGHTNESS:
      return BRIGHTNESS;
    case COLOR_COMPOSITE_SATURATION:
      return SATURATION;
    case COLOR_COMPOSITE_COLOR:
      return COLOR;
    default:
      throw new Error("Unknown ColorCompositeRule value");
    }
  }

  /**
   * Property editor for the <code>ColorCompositeRule</code> type. 
   * @see com.sun.glf.util.EnumPropertyEditorSupport
   */
  static public class ColorCompositeRulePropertyEditor extends EnumPropertyEditorSupport{
    /**
     * This method is intended for use when generating Java code to set
     * the value of the property.  It returns a fragment of Java code
     * that can be used to initialize a variable with the current property
     * value.
     */
    public String getJavaInitializationString() {
      ColorCompositeRule val = (ColorCompositeRule)getValue();
      if(val==null)
	return "null";

      switch(val.toInt()){
      case COLOR_COMPOSITE_HUE:
	return "ColorCompositeRule.HUE";
      case COLOR_COMPOSITE_BRIGHTNESS:
	return "ColorCompositeRule.BRIGHTNESS";
      case COLOR_COMPOSITE_SATURATION:
	return "ColorCompositeRule.SATURATION";
      case COLOR_COMPOSITE_COLOR:
	return "ColorCompositeRule.COLOR";
      default:
	throw new Error("Unknown ColorCompositeRule value");
      }
    }

    public ColorCompositeRulePropertyEditor(){
      super(enumValues);
    }
  }

  /**
   * Static initializer registers the editor with the property editor 
   * manager
   */
  static {
    PropertyEditorManager.registerEditor(ColorCompositeRule.class, ColorCompositeRulePropertyEditor.class);
  }
}
