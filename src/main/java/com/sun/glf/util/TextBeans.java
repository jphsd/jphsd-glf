/*
 * @(#)TextBeans.java
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
import java.beans.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.*;

import com.sun.glf.*;

/**
 * This utility class allows read and write of bean properties in
 * ascii files. <p>
 * This can be usefull to save and restore the properties of a beans
 * which needs to be modified.
 * 
 * @author        Vincent Hardy
 * @version       1.0, 10/27/1998
 */
public final class TextBeans {
  /*
   * Constants
   */
  static final String CLASS_DEF = "class";
  static final String INVOKE_EXCEPTION = "Invocatin exception : ";
  static final String INVALID_FORMAT = "Invalid stream format : expected to start with " + "CLASS_DEF";

  /*
   * Interface for objects able to read/write a given type in ascii format
   */
  static interface TypeTextGrinder{
    public String writeProperty(Object value);
    public Object readProperty(String value);
  }

  /**
   * @return the property name for a given set method
   * @param set/get method. 
   */
  public static String getPropertyName(Method method){
    String methodName = method.getName();
    return methodName.substring(3);
  }

  /**
   * Maps class names to TypeTextGrinder implementations
   * @see #registerTypeGrinder
   */
  private static Map grinderMap = new Hashtable();

  /**
   * Adds a new TypeTextGrinder for the given type. If one
   * was already in the map, it is replaced by the new value
   */
  public static void registerTypeGrinder(Class type, TypeTextGrinder grinder){
    grinderMap.put(type, grinder);
  }

  /*
   * Static initializers : registers default set of TypeTextGrinders
   */
  static{
    registerTypeGrinder(String.class, new StringGrinder());
    registerTypeGrinder(Integer.class, new IntegerGrinder());
    registerTypeGrinder(int.class, new IntegerGrinder());
    registerTypeGrinder(Double.class, new DoubleGrinder());
    registerTypeGrinder(double.class, new DoubleGrinder());
    registerTypeGrinder(Float.class, new FloatGrinder());
    registerTypeGrinder(float.class, new FloatGrinder());
    registerTypeGrinder(Color.class, new ColorGrinder());
    registerTypeGrinder(Font.class, new FontGrinder());
    registerTypeGrinder(Dimension.class, new DimensionGrinder());
    registerTypeGrinder(Boolean.class, new BooleanGrinder());
    registerTypeGrinder(boolean.class, new BooleanGrinder());
    registerTypeGrinder(Anchor.class, new AnchorGrinder());
    registerTypeGrinder(TextAlignment.class, new TextAlignmentGrinder());
    registerTypeGrinder(Glyph.class, new GlyphGrinder());
    registerTypeGrinder(File.class, new FileGrinder());
  }

  /**
   * Saves the input bean properties in text format into OutputStream
   * @param bean object whose properties should be saved.
   * @param out stream where the object's configuration is saved
   */
  public static void save(Object bean, OutputStream out) throws IOException{
    PrintWriter o = new PrintWriter(out);
    Class beanClass = bean.getClass();

    // First, write out class name.
    o.println(CLASS_DEF + "=" + beanClass.getName());

    // Now, get all bean properties.
    Method[][] accessors = getPropertyMethods(beanClass);
    
    int nMethods = accessors!=null?accessors.length:0;
    for(int i=0; i<nMethods; i++){
      Method getter = accessors[i][0];
      Class type = getter.getReturnType();
      TypeTextGrinder writer = (TypeTextGrinder)grinderMap.get(type);
      try{
	if(writer!=null)
	  o.println(getPropertyName(getter) + "=" + writer.writeProperty(getter.invoke(bean, null)));
      }catch(IllegalAccessException e){
	System.out.println(INVOKE_EXCEPTION + " " + getPropertyName(getter));
      }catch(InvocationTargetException e){
	System.out.println(INVOKE_EXCEPTION + " " + getPropertyName(getter));
      }
    }

    o.flush();
    out.flush();
  }

  /**
   * Initializes a bean from text configuration in input stream
   *
   * @param in stream where bean configuration should be read
   * @return Bean initialized from information read in input stream
   */
  public static Object read(InputStream in) throws IOException, ClassNotFoundException{
    Properties properties = new Properties();
    properties.load(in);
    
    // First, get class name
    String className = properties.getProperty(CLASS_DEF);
    if(className==null)
      throw new IllegalArgumentException(INVALID_FORMAT);
    
    // Instantiate a new bean
    Object bean = bean = Beans.instantiate(null, className);

    // Iterate through the list of properties
    Method[][] accessors = getPropertyMethods(bean.getClass());
    
    int nMethods = accessors!=null?accessors.length:0;
    for(int i=0; i<nMethods; i++){
      Method setter = accessors[i][1];
      String propertyName = getPropertyName(setter);
      String textValue = properties.getProperty(propertyName);
      if(textValue!=null){
	Class parameterTypes[] = setter.getParameterTypes();
	if(parameterTypes!=null && parameterTypes.length>0){
	  Class type = parameterTypes[0];
	  TypeTextGrinder reader = (TypeTextGrinder)grinderMap.get(type);
	  if(reader!=null){
	    try{
	      setter.invoke(bean, new Object[]{reader.readProperty(textValue)});
	    }catch(IllegalAccessException e){
	      System.out.println(INVOKE_EXCEPTION + " " + getPropertyName(setter));
	    }catch(InvocationTargetException e){
	      System.out.println(INVOKE_EXCEPTION + " " + getPropertyName(setter));
	    }
	  }
	}
      }
    }
    return bean;
  }


  /**
   * @return the set of get and set methods of the input bean class
   *         for all the properties which follow the Java Beans naming
   *         pattern.
   */
  public static Method[][] getPropertyMethods(Class beanClass){
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
	  }catch(NoSuchMethodException e){
	    // System.out.println("Not set method for " + getPropertyName(m[i]));
	  }
	  catch(SecurityException e){}
	}
      }
    }
    Method pm[][] = new Method[v.size()][2];
    v.copyInto(pm);
    return pm;
  }

  /**
   * Unit testing
   */
  public static void main(String args[]) throws Exception{
    // First, test saving to a stream
    TypeTest bean = new TypeTest();
    bean.initDefault();

    TextBeans.save(bean, System.out);

    // Second, test writing to a stream, read back from it, and
    // compare the two objects: they should be identical

    // Save to Byte stream
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    TextBeans.save(bean, bout);

    // Read from Byte stream
    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
    Object bean2 = TextBeans.read(bin);

    if(!bean.equals(bean2))
      throw new Error();
    else
      System.out.println("Read/Write test completed");
  }

  public static class TypeTest{
    String stringValue;
    Integer integerValue;
    int intValue;
    Double doubleValue;
    double doubleValue2;
    Float floatValue;
    float floatValue2;
    Color colorValue;
    Font fontValue;
    Dimension dimValue;
    Boolean booleanValue;
    boolean booleanValue2;
    Anchor anchorValue;
    TextAlignment justificationValue;

    public void initDefault(){
      stringValue = "Hello";
      integerValue = new Integer(10);
      intValue = 20;
      doubleValue = new Double(1.);
      doubleValue2 = 2.;
      floatValue = new Float(4f);
      floatValue2 = 8f;
      colorValue = new Color(1, 2, 3, 4);
      fontValue = new Font("dialog", Font.BOLD, 34);
      dimValue = new Dimension(50, 100);
      booleanValue = new Boolean(true);
      booleanValue2 = true;
      anchorValue = Anchor.TOP_RIGHT;
      justificationValue = TextAlignment.CENTER;
    }

    public boolean equals(Object obj){
      if(obj==null || !(obj instanceof TypeTest))
	return false;

      TypeTest o = (TypeTest)obj;
      if(
	 stringValue.equals(o.stringValue)
	 &&
	 integerValue.equals(o.integerValue)
	 &&
	 intValue == o.intValue
	 &&
	 doubleValue.equals(o.doubleValue)
	 &&
	 doubleValue2 == o.doubleValue2
	 &&
	 floatValue.equals(o.floatValue)
	 && 
	 floatValue2 == o.floatValue2
	 &&
	 colorValue.equals(o.colorValue)
	 &&
	 fontValue.equals(o.fontValue)
	 &&
	 dimValue.equals(o.dimValue)
	 &&
	 booleanValue.equals(o.booleanValue)
	 &&
	 booleanValue2 == o.booleanValue2
	 &&
	 anchorValue.equals(o.anchorValue)
	 &&
	 justificationValue.equals(o.justificationValue)
	 )
	return true;
      return false;  
    }

    public String getStringValue(){
      return stringValue;
    }

    public void setStringValue(String stringValue){
      this.stringValue = stringValue;
    }

    public Integer getIntegerValue(){
      return integerValue;
    }

    public void setIntegerValue(Integer integerValue){
      this.integerValue = integerValue;
    }

    public int getIntValue(){
      return intValue;
    }

    public void setIntValue(int intValue){
      this.intValue = intValue;
    }

    public Double getDoubleValue(){
      return doubleValue;
    }

    public void setDoubleValue(Double doubleValue){
      this.doubleValue = doubleValue;
    }

    public double getDoubleValue2(){
      return doubleValue2;
    }

    public void setDoubleValue2(double doubleValue2){
      this.doubleValue2 = doubleValue2;
    }

    public Float getFloatValue(){
      return floatValue;
    }

    public void setFloatValue(Float floatValue){
      this.floatValue = floatValue;
    }

    public float getFloatValue2(){
      return floatValue2;
    }

    public void setFloatValue2(float floatValue2){
      this.floatValue2 = floatValue2;
    }

    public Color getColorValue(){
      return colorValue;
    }

    public void setColorValue(Color colorValue){
      this.colorValue = colorValue;
    }

    public Font getFontValue(){
      return fontValue;
    }

    public void setFontValue(Font fontValue){
      this.fontValue = fontValue;
    }

    public Dimension getDimValue(){
      return dimValue;
    }

    public void setDimValue(Dimension dimValue){
      this.dimValue = dimValue;
    }

    public Boolean getBooleanValue(){
      return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue){
      this.booleanValue = booleanValue;
    }

    public boolean getBooleanValue2(){
      return booleanValue2;
    }

    public void setBooleanValue2(boolean booleanValue2){
      this.booleanValue2 = booleanValue2;
    }

    public Anchor getAnchorValue(){
      return anchorValue;
    }

    public void setAnchorValue(Anchor anchorValue){
      this.anchorValue = anchorValue;
    }

    public TextAlignment getTextAlignmentValue(){
      return justificationValue;
    }

    public void setTextAlignmentValue(TextAlignment justificationValue){
      this.justificationValue = justificationValue;
    }
  }
}

/**
 * Note that the encoding scheme for Strings is *not* fool proof and
 * does not handle proper encoding/decoding of strings containing "|"
 */
class StringGrinder implements TextBeans.TypeTextGrinder{
  static final String lineSep = System.getProperty("line.separator", "\\n");
  static final String lineSepReplacement = "|";

  public String writeProperty(Object value){
    String str = (String)value;
    
    // Replace lineSeparator with lineSepReplacement
    StringTokenizer st = new StringTokenizer(str, lineSep);
    StringBuffer buf = new StringBuffer();

    if(st.hasMoreElements()){
      String elt = st.nextToken();
      buf.append(elt);
    }

    while(st.hasMoreElements()){
      buf.append(lineSepReplacement);
      String elt = st.nextToken();
      buf.append(elt);
    }

    return buf.toString();
  }

  public Object readProperty(String value){
    // First, replace lineSepReplacement with lineSep
    StringTokenizer st = new StringTokenizer(value, lineSepReplacement);
    StringBuffer buf = new StringBuffer();

    if(st.hasMoreElements()){
      String elt = st.nextToken();
      buf.append(elt);
    }

    while(st.hasMoreElements()){
      buf.append(lineSep);
      String elt = st.nextToken();
      buf.append(elt);
    }
    return buf.toString();
  }
}

class GlyphGrinder implements TextBeans.TypeTextGrinder{
  FontGrinder fontGrinder = new FontGrinder();

  public String writeProperty(Object value){
    Glyph glyph = (Glyph)value;
    String strVal = "null";
    if(glyph!=null){
      strVal = Integer.toHexString(glyph.getChar()) + " ";
      strVal += fontGrinder.writeProperty(glyph.getFont());
    }
    return strVal;
  }

  public Object readProperty(String value){
    Glyph glyph = null;
    if(value!=null && !value.equalsIgnoreCase("null")){
      int sep = value.indexOf(" ");
      if(sep==-1 || value.length()<=sep+1)
	throw new IllegalArgumentException();
      try{
	char c = (char)Integer.parseInt(value.substring(0, sep), 16);
	Font font = (Font)fontGrinder.readProperty(value.substring(sep+1));
	glyph = new Glyph(font, c);
      }catch(NumberFormatException e){
	throw new IllegalArgumentException();
      }
    }
    return glyph;
  }
}

class IntegerGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    return value.toString();
  }

  public Object readProperty(String value){
    try{
      return Integer.valueOf(value);
    }catch(NumberFormatException e){
      throw new IllegalArgumentException();
    }
  }
}

class DoubleGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    return value.toString();
  }

  public Object readProperty(String value){
    try{
      return Double.valueOf(value);
    }catch(NumberFormatException e){
      throw new IllegalArgumentException();
    }
  }
}

class FloatGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    return value.toString();
  }

  public Object readProperty(String value){
    try{
      return Float.valueOf(value);
    }catch(NumberFormatException e){
      throw new IllegalArgumentException();
    }
  }
}

class ColorGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    Color c = (Color)value;
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    int a = c.getAlpha();
    String rStr = Integer.toHexString(r);
    String gStr = Integer.toHexString(g);
    String bStr = Integer.toHexString(b);
    String aStr = Integer.toHexString(a);
    if(rStr.length()<2)
      rStr = "0" + rStr;
    if(gStr.length()<2)
      gStr = "0" + gStr;
    if(bStr.length()<2)
      bStr = "0" + bStr;
    if(aStr.length()<2)
      aStr = "0" + aStr;

    return rStr + gStr + bStr + aStr;
  }

  public Object readProperty(String value){
    if(value==null || value.length()!=8)
      throw new IllegalArgumentException();

    String rStr = value.substring(0, 2);
    String gStr = value.substring(2, 4);
    String bStr = value.substring(4, 6);
    String aStr = value.substring(6, 8);

    try{
      int r = Integer.parseInt(rStr, 16);
      int g = Integer.parseInt(gStr, 16);
      int b = Integer.parseInt(bStr, 16);
      int a = Integer.parseInt(aStr, 16);
      return new Color(r, g, b, a);
    }catch(NumberFormatException e){
      throw new IllegalArgumentException(e.getMessage());
    }
    
  }
}

class FontGrinder implements TextBeans.TypeTextGrinder{
  String getStyleString(int style){
    String styleString = "PLAIN";
    if((style&Font.BOLD)!=0){
      if((style&Font.ITALIC)!=0)
	styleString = "BOLDITALIC";
      else
	styleString = "BOLD";
    }
    else if((style&Font.ITALIC)!=0){
      styleString = "ITALIC";
    }

    return styleString;
  }

  int getFontStyle(String fontStyle){
    int fs = Font.PLAIN;
    if(fontStyle!=null){
      if(fontStyle.equalsIgnoreCase("BOLD"))
	fs = Font.BOLD;
      else if(fontStyle.equalsIgnoreCase("ITALIC"))
	fs = Font.ITALIC;
      else if(fontStyle.equalsIgnoreCase("BOLDITALIC"))
	fs = Font.BOLD | Font.ITALIC;
    }      
    return fs;
  }

  public String writeProperty(Object value){
    Font font = (Font)value;
    String fontText = "";
    if(font==null)
      fontText = "null";
    else
      fontText = font.getName() + "/" + getStyleString(font.getStyle()) + "/" + font.getSize();
    return fontText;    
  }

  public Object readProperty(String value){
    Font font = null;
    if(value!=null && !value.equalsIgnoreCase("null")){
      StringTokenizer st = new StringTokenizer(value, "/");
      if(st.countTokens()!=3)
	throw new IllegalArgumentException();

      try{
	String fontName = st.nextToken();
	int fontStyle = getFontStyle(st.nextToken());
	int fontSize = Integer.parseInt(st.nextToken());
	font = new Font(fontName, fontStyle, fontSize);
      }catch(NumberFormatException e){
	throw new IllegalArgumentException();
      }
    }
    return font;
  }
}

class DimensionGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    Dimension dim = (Dimension)value;
    String dimStr = "null";
    if(dim!=null)
      dimStr = "" + dim.width + " " + dim.height;
    
    return dimStr;
  }

  public Object readProperty(String value){
    Dimension dim = null;
    if(value!=null && !value.equalsIgnoreCase("null")){
      try{
	StringTokenizer st = new StringTokenizer(value);
	if(st.countTokens()!=2)
	  throw new IllegalArgumentException();
	dim = new Dimension(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
      }catch(NumberFormatException e){
	throw new IllegalArgumentException();
      }
    }
    return dim;
  }
}

class BooleanGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    if(value==null)
      throw new IllegalArgumentException();
    return value.toString();
  }

  public Object readProperty(String value){
    return Boolean.valueOf(value);
  }
}

class FileGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    File file = (File)value;
    String fileStr = "null";
    if(file!=null){
      try{
	fileStr = file.toURL().toString();
      }catch(MalformedURLException e){}
    }
    return fileStr;
  }

  public Object readProperty(String value){
    File file = null;
    if(value!=null && !value.equalsIgnoreCase("null")){
      try{
	URL url = new URL(value);
	file = new File(url.getFile());
      }catch(MalformedURLException e){
	throw new IllegalArgumentException();
      }
    }
    return file;
  }
}

class AnchorGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    String anchorStr = "null";
    if(value!=null)
      anchorStr = value.toString();
    return anchorStr;
  }

  public Object readProperty(String value){
    Anchor anchor = null;
    if(value != null && !value.equalsIgnoreCase("null")){
      if(value.equalsIgnoreCase(Anchor.TOP_LEFT_STR))
	anchor = Anchor.TOP_LEFT;
      else if(value.equalsIgnoreCase(Anchor.TOP_STR))
	anchor = Anchor.TOP;
      else if(value.equalsIgnoreCase(Anchor.TOP_RIGHT_STR))
	anchor = Anchor.TOP_RIGHT;
      else if(value.equalsIgnoreCase(Anchor.RIGHT_STR))
	anchor = Anchor.RIGHT;
      else if(value.equalsIgnoreCase(Anchor.BOTTOM_RIGHT_STR))
	anchor = Anchor.BOTTOM_RIGHT;
      else if(value.equalsIgnoreCase(Anchor.BOTTOM_STR))
	anchor = Anchor.BOTTOM;
      else if(value.equalsIgnoreCase(Anchor.BOTTOM_LEFT_STR))
	anchor = Anchor.BOTTOM_LEFT;
      else if(value.equalsIgnoreCase(Anchor.LEFT_STR))
	anchor = Anchor.LEFT;
      else if(value.equalsIgnoreCase(Anchor.CENTER_STR))
	anchor = Anchor.CENTER;
      else 
	throw new IllegalArgumentException();
    }
    return anchor;

  }
}

class TextAlignmentGrinder implements TextBeans.TypeTextGrinder{
  public String writeProperty(Object value){
    String justificationStr = "null";
    if(value!=null)
      justificationStr = value.toString();
    return justificationStr;
  }

  public Object readProperty(String value){
    TextAlignment justification = null;
    if(value != null && !value.equalsIgnoreCase("null")){
      if(value.equalsIgnoreCase(TextAlignment.RIGHT_STR))
	justification = TextAlignment.RIGHT;
      else if(value.equalsIgnoreCase(TextAlignment.LEFT_STR))
	justification = TextAlignment.LEFT;
      else if(value.equalsIgnoreCase(TextAlignment.CENTER_STR))
	justification = TextAlignment.CENTER;
      else if(value.equalsIgnoreCase(TextAlignment.JUSTIFY_STR))
	justification = TextAlignment.JUSTIFY;
      else 
	throw new IllegalArgumentException();
    }
    return justification;
  }
}


  
