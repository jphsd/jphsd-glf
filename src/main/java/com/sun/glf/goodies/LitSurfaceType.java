/*
 * @(#)LitSurfaceType.java
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

import java.beans.*;

import com.sun.glf.util.*;
import com.sun.glf.util.*;

public class LitSurfaceType implements java.io.Serializable{
  public static final int LIT_SURFACE_NORMAL = 0;
  public static final int LIT_SURFACE_MATTE = 1;
  public static final int LIT_SURFACE_SHINY = 2;
  public static final int LIT_SURFACE_METALLIC = 3;

  public static final String LIT_SURFACE_NORMAL_STR = "Normal";
  public static final String LIT_SURFACE_MATTE_STR = "Matte";
  public static final String LIT_SURFACE_SHINY_STR = "Shiny";
  public static final String LIT_SURFACE_METALLIC_STR = "Metallic";

  /*
   * Type values
   */
  public static final LitSurfaceType NORMAL = new LitSurfaceType(LIT_SURFACE_NORMAL, LIT_SURFACE_NORMAL_STR, .5, .5, 2);
  public static final LitSurfaceType MATTE = new LitSurfaceType(LIT_SURFACE_MATTE, LIT_SURFACE_MATTE_STR, 1, 0, 2);
  public static final LitSurfaceType SHINY = new LitSurfaceType(LIT_SURFACE_SHINY, LIT_SURFACE_SHINY_STR, .5, 1, 16);
  public static final LitSurfaceType METALLIC = new LitSurfaceType(LIT_SURFACE_METALLIC, LIT_SURFACE_METALLIC_STR, .5, 2, 32);

  /**
   * All values
   */
  public static final LitSurfaceType[] enumValues = { NORMAL,
						      MATTE,
						      SHINY,
						      METALLIC };

  private String desc;
  private int val;

  /**
   * Specular reflection exponent
   * @see LitSurface
   */
  private int ns;

  /**
   * Specular reflection factor
   */
  private double ks;

  /**
   * Diffusion factor
   */
  private double kd;


  /**
   * @return Specular reflection exponent
   */
  public int getNs(){
    return ns;
  }

  /**
   * @return Specular reflection factor
   */
  public double getKs(){
    return ks;
  }

  /**
   * @return Diffusion factor
   */
  public double getKd(){
    return kd;
  }

  /** 
   * Constructor is private so that no other instances than
   * the one in the enumeration can be created.
   */
  private LitSurfaceType(int val, String desc, double kd, double ks, int ns){
    this.desc = desc;
    this.val = val;
    this.kd = kd;
    this.ks = ks;
    this.ns = ns;
  }

  /**
   * @return description
   */
  public String toString(){
    return desc;
  }

  /**
   * This is called by the serialization code before it returns an unserialized
   * object. To provide for unicity of instances, the instance that was read
   * is replaced by its static equivalent
   */
  public Object readResolve() {
    switch(val){
    case LIT_SURFACE_NORMAL:
      return NORMAL;
    case LIT_SURFACE_MATTE:
      return MATTE;
    case LIT_SURFACE_SHINY:
      return SHINY;
    case LIT_SURFACE_METALLIC:
      return METALLIC;
    default:
      throw new Error("Unknown LitSurfaceType value");
    }
  }

  /**
   * Convenience for enumeration switching
   * @return value.
   */
  public int toInt(){
    return val;
  }


  /**
   * Property editor
   */
  static public class LitSurfaceTypePropertyEditor extends EnumPropertyEditorSupport{
    /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
    public String getJavaInitializationString() {
      LitSurfaceType val = (LitSurfaceType)getValue();
      switch(val.toInt()){
      case LIT_SURFACE_NORMAL:
	return "LitSurface.NORMAL";
      case LIT_SURFACE_MATTE:
	return "LitSurface.MATTE";
      case LIT_SURFACE_SHINY:
	return "LitSurface.SHINY";
      case LIT_SURFACE_METALLIC:
	return "LitSurface.METALLIC";
      default:
	throw new Error("Unknown LitSurfaceType value");
      }
    }

    public LitSurfaceTypePropertyEditor(){
      super(enumValues);
    }
  }

  /**
   * Static initializer registers the editor with the property editor 
   * manager
   */
  static {
    PropertyEditorManager.registerEditor(LitSurfaceType.class, LitSurfaceTypePropertyEditor.class);
  }

}
