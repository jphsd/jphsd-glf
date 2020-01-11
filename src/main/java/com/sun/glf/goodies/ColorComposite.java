/*
 * @(#)ColorComposite.java
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

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * This implementation of the Composite interface combines the source and
 * destination according to the following rules:<p>
 * a. Hue Composite.<p>
 *    The destination takes the hue of the source and the brightness and 
 *    saturation of the destination.
 * b. Brightness Composite.<p>
 *    The destination takes the brightness of the source and the hue and
 *    saturatio of the destination.
 * c. Saturation Composite.<p>
 *    The destination takes the saturation of the source and the hue and
 *    brightness of the destination.<p>
 * d. Color Composite. <p>
 *    The destination takes the hue and saturation of the source, and the
 *    brightness of the destination.<p>
 * For each type, the rule is modulated by the alpha value. Assuming that
 * we work in the HSB color space, and if we call sa, sh, ss, sb the components
 * of the source and dia, dih, dis, dib the components of the destination before
 * composition, the destination components are, after composition (destination
 * out): <p>
 *
 * a. Hue Composite.<br>
 *    doh = sh<br>
 *    dos = dis<br>
 *    dob = dib<br>
 *
 * b. Brightness Composite.<br>
 *    doh = dih<br>
 *    dos = dis<br>
 *    dob = sb<br>
 *
 * c. Saturation Composite.<br>
 *    doh = dih<br>
 *    dos = ss<br>
 *    dob = dib<br>
 * 
 * d. Color Composite.<br>
 *    doh = sh<br>
 *    dos = ss<br>
 *    dob = dib<br>
 * 
 * Alpha composition is made in the sRGB space (pseudo code):<br>
 *    dor = HSVtoRGB(doh, dos, dob).red; // Destination out red<br>
 *    dog = HSVtoRGB(doh, dos, dob).green; // Destination out green<br>
 *    dob = HSVtoRGB(doh, dos, dob).blue; // Destination out blue<br>
 *    
 *    a = as*alpha;<br> // 
 *    ac = 1-a;<br>
 *    dor = a*dor + ac.dir // dir = Destination in red <br>
 *    dog = a*dog + ac*dig // dig = Destination in green <br>
 *    dob = a*dob + ac*dib // dib = Destination in blue <br>
 *    doa = Ad;
 *    
 *
 * @author           Vincent Hardy
 * @version          1.0, 01/28/1999
 * @see              com.sun.glf.goodies.ColorCompositeContext
 */
public class ColorComposite implements Composite{
  /**
   * The ColorComposite composition rule
   */
  private ColorCompositeRule rule;

  /**
   * Alpha value, premultiplied with source before compositing with destination
   */
  private float alpha;

  /**
   * Hue ColorComposite
   */
  public static final ColorComposite Hue = new ColorComposite(ColorCompositeRule.HUE, 1f);

  /**
   * Brightness ColorComposite
   */
  public static final ColorComposite Brightness = new ColorComposite(ColorCompositeRule.BRIGHTNESS, 1f);

  /**
   * Saturation ColorComposite
   */
  public static final ColorComposite Saturation = new ColorComposite(ColorCompositeRule.SATURATION, 1f);

  /**
   * Color ColorComposite
   */
  public static final ColorComposite Hs = new ColorComposite(ColorCompositeRule.COLOR, 1f);

  /**
   * @param rule one of ColorCompositeRule: HUE, BRIGHTNESS, SATURATION, COLOR.
   * @param alpha additional opacity premultiplied with the source before compositing
   *        with destination.
   * @return ColorComposite matching rule and alpha
   */
  public static ColorComposite getInstance(ColorCompositeRule rule, float alpha){
    if(alpha<0 || alpha>1)
      throw new IllegalArgumentException("Out of range alpha: " + alpha + ". Should be between 0 and 1");

    if(rule==null)
      throw new IllegalArgumentException("rule should not be null");
    
    if(alpha==1.f){
      switch(rule.toInt()){
      case ColorCompositeRule.COLOR_COMPOSITE_HUE:
	return Hue;
      case ColorCompositeRule.COLOR_COMPOSITE_SATURATION:
	return Saturation;
      case ColorCompositeRule.COLOR_COMPOSITE_BRIGHTNESS:
	return Brightness;
      case ColorCompositeRule.COLOR_COMPOSITE_COLOR:
	return Hs;
      default:
	throw new Error("Unknown ColorCompositeRule");
      }
    }

    return new ColorComposite(rule, alpha);
  }
  
  /**
   * @param rule one of ColorCompositeRule: HUE, BRIGHTNESS, SATURATION, COLOR.
   * @param alpha additional opacity premultiplied with the source before compositing
   *        with destination.
   */
  protected ColorComposite(ColorCompositeRule rule, float alpha){
    if(alpha<0 || alpha>1)
      throw new IllegalArgumentException("Out of range alpha: " + alpha + ". Should be between 0 and 1");

    if(rule==null)
      throw new IllegalArgumentException("rule should not be null");

    this.alpha = alpha;
    this.rule = rule;
  }

  /**
   * Creates a context containing state that is used to perform
   * the compositing operation.  In a multi-threaded environment,
   * several contexts can exist simultaneously for a single
   * <code>Composite</code> object.
   * @param srcColorModel  the {@link ColorModel} of the source
   * @param dstColorModel  the <code>ColorModel</code> of the destination
   * @param hints the hint that the context object uses to choose between
   * rendering alternatives
   * @return the <code>CompositeContext</code> object used to perform the
   * compositing operation.
   */
  public CompositeContext createContext(ColorModel srcColorModel,
					ColorModel dstColorModel,
					RenderingHints hints){
    return new ColorCompositeContext(rule, alpha, srcColorModel, dstColorModel, hints);
  }

  /**
   * @return this Composite's alpha
   */
  public float getAlpha(){
    return alpha;
  }

  /**
   * @return this Composite's rule
   */
  public ColorCompositeRule getRule(){
    return rule;
  }

  static final String USAGE = "java com.sun.glf.goodies.ColorComposite <imageFile> <red> <green> <blue>";

  /**
   * ColorCompsite unit testing
   */
  public static void main(String args[]){
    // Check input arguments
    if(args.length<3){ 
      System.out.println(USAGE);
      System.exit(0);
    }

    // Build test image if none was passed. If there are only three arguments,
    // they are interpreted as r/g/b. Otherwise, the first argument is interpreted
    // as the test image.
    Image image = null;
    Color color = null;
    if(args.length<4){
      image = buildTestImage();
      color = new Color(Integer.parseInt(args[0]),
			      Integer.parseInt(args[1]),
			      Integer.parseInt(args[2]));
    }
    else{
      image = Toolbox.loadImage(args[0], BufferedImage.TYPE_BYTE_GRAY);
      color = new Color(Integer.parseInt(args[1]),
			      Integer.parseInt(args[2]),
			      Integer.parseInt(args[3]));
    }

    //
    // Top Level Frame
    //
    JFrame frame = new JFrame("ColorComposite unit testing");
    frame.getContentPane().setLayout(new GridLayout(0, 4));
    frame.getContentPane().setBackground(Color.white);
    
    //
    // Array of ColorComposites to test
    //
    ColorComposite composites[] = { ColorComposite.getInstance(ColorCompositeRule.HUE, 1.f),
				    ColorComposite.getInstance(ColorCompositeRule.SATURATION, 1.f),
				    ColorComposite.getInstance(ColorCompositeRule.BRIGHTNESS, 1.f),
				    ColorComposite.getInstance(ColorCompositeRule.COLOR, 1.f),
				    ColorComposite.getInstance(ColorCompositeRule.HUE, .75f),
				    ColorComposite.getInstance(ColorCompositeRule.SATURATION, .75f),
				    ColorComposite.getInstance(ColorCompositeRule.BRIGHTNESS, .75f),
				    ColorComposite.getInstance(ColorCompositeRule.COLOR, .75f),
				    ColorComposite.getInstance(ColorCompositeRule.HUE, .2f),
				    ColorComposite.getInstance(ColorCompositeRule.SATURATION, .2f),
				    ColorComposite.getInstance(ColorCompositeRule.BRIGHTNESS, .2f),
				    ColorComposite.getInstance(ColorCompositeRule.COLOR, .2f) };
    
    String tooltips[] = { "Hue",
			  "Saturation",
			  "Brightness",
			  "Color",
			  "Hue, 75% transparent",
			  "Saturation, 75% transparent",
			  "Brightness, 75% transparent",
			  "Color, 75% transparent",
			  "Hue, 50% transparent",
			  "Saturation, 50% transparent",
			  "Brightness, 50% transparent",
			  "Color, 50% transparent"  };
    //
    // Build one simple composition for each type of composite
    //
    Dimension dim = new Dimension(image.getWidth(null), image.getHeight(null));
    for(int i=0; i<composites.length; i++){
      JComponent comp = makeNewComponent(dim, image, color, composites[i]);
      comp.setToolTipText(tooltips[i]);
      frame.getContentPane().add(comp);
    }

    frame.pack();
    frame.setVisible(true);
  }

  public static JComponent makeNewComponent(Dimension dim, Image image, Color color, Composite composite){
    LayerComposition cmp = new LayerComposition(dim);
    ShapeLayer shapeLayer = new ShapeLayer(cmp, cmp.getBounds(), new FillRenderer(color));
    ImageLayer imageLayer = new ImageLayer(cmp, image, null);
    imageLayer.setComposite(composite);
    cmp.setLayers(new Layer[]{shapeLayer, imageLayer});
    CompositionComponent comp = new CompositionComponent(cmp);
    return comp;
  }

  public static Image buildTestImage(){
    Color colors[] = { Color.red,   Color.green,   Color.blue,
		       Color.black, Color.gray,    Color.white,
		       Color.cyan,  Color.magenta, Color.yellow };
    int w = 15;
    BufferedImage buf = new BufferedImage(w*3, w*3, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    
    for(int i=0; i<colors.length/3; i++){
      g.setColor(colors[3*i]);
      g.fillRect(0, 0, w, w);
      g.setColor(colors[3*i+1]);
      g.fillRect(w, 0, w, w);
      g.setColor(colors[3*i+2]);
      g.fillRect(2*w, 0, w, w);
      g.translate(0, w);
    }

    g.dispose();
    return buf;
  }
}
   
