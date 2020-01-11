/*
 * @(#)TextAlignment.java
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

package com.sun.glf;

import java.beans.*;
import java.io.*;

import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Defines different justification styles for text blocks: left, right or center.
 * The class defines several final variables, and the API uses the ones of
 * type <code>TextAlignment</code> as API parameters. The int and String constants 
 * are used as convenience. The <code>toInt</code> member allows to switch on
 * TextAlignment values: <p><code><pre>
 * TextAlignment justif = ...;
 * switch(justif.toInt()){
 *    case TextAlignment.JUSTIFICATION_LEFT:
 *    ...
 *    break;
 * ....
 * </code></pre>
 *
 * @author        Vincent Hardy
 * @version       1.0, 10/17/1998
 */
public final class TextAlignment implements Serializable{
  /*
   * Text block TextAlignment types.
   */
  public static final int JUSTIFICATION_LEFT = 0;
  public static final int JUSTIFICATION_RIGHT = 1;
  public static final int JUSTIFICATION_CENTER = 2;
  public static final int JUSTIFICATION_JUSTIFY = 3;

  /*
   * Strings used to get a more readable String when
   * an TextAlignment is displayed in a property editor.
   */
  public static final String RIGHT_STR = "Right";
  public static final String LEFT_STR = "Left";
  public static final String CENTER_STR = "Center";
  public static final String JUSTIFY_STR = "Justify";

  /*
   * TextAlignment values
   */
  public static final TextAlignment RIGHT = new TextAlignment(JUSTIFICATION_RIGHT, RIGHT_STR);
  public static final TextAlignment LEFT = new TextAlignment(JUSTIFICATION_LEFT, LEFT_STR);
  public static final TextAlignment CENTER = new TextAlignment(JUSTIFICATION_CENTER, CENTER_STR);
  public static final TextAlignment JUSTIFY = new TextAlignment(JUSTIFICATION_JUSTIFY, JUSTIFY_STR);

  /**
   * All values
   */
  private static final TextAlignment[] enumValues = { RIGHT,
						      LEFT,
						      CENTER,
                                                      JUSTIFY};

  private String desc;
  private int val;

  /** 
   * Constructor is private so that no instances other than
   * the ones in the enumeration can be created.
   * @see #readResolve
   */
  private TextAlignment(int val, String desc){
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
   * is replaced by its static equivalent
   */
  public Object readResolve() {
    switch(val){
    case JUSTIFICATION_RIGHT:
      return TextAlignment.RIGHT;
    case JUSTIFICATION_LEFT:
      return TextAlignment.LEFT;
    case JUSTIFICATION_CENTER:
      return TextAlignment.CENTER;
    case JUSTIFICATION_JUSTIFY:
      return TextAlignment.JUSTIFY;
    default:
      throw new Error("Unknown TextAlignment value");
    }
  }

  /**
   * Property editor for the <code>TextAlignment</code> type.
   * @see com.sun.glf.util.EnumPropertyEditorSupport
   */
  public static class TextAlignmentPropertyEditor extends EnumPropertyEditorSupport{
    /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It returns a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
    public String getJavaInitializationString() {
      TextAlignment val = (TextAlignment)getValue();
      if(val==null)
	return "null";

      switch(val.toInt()){
      case JUSTIFICATION_RIGHT:
	return "TextAlignment.RIGHT";
      case JUSTIFICATION_LEFT:
	return "TextAlignment.LEFT";
      case JUSTIFICATION_CENTER:
	return "TextAlignment.CENTER";
      case JUSTIFICATION_JUSTIFY:
	return "TextAlignment.JUSTIFY";
      default:
	throw new Error("Unknown TextAlignment value");
      }
    }

    public TextAlignmentPropertyEditor(){
      super(enumValues);
    }
  }

  /**
   * Static initializer registers the editor with the property editor 
   * manager
   */
  static {
    PropertyEditorManager.registerEditor(TextAlignment.class, TextAlignmentPropertyEditor.class);
  }
}
