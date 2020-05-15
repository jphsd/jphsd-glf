/*
 * @(#)Lights.java
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

package com.sun.glf.demos.gallery;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Illustrates the use of the lighting 'goodies'.
 * It paints a background by rendering it using a LightOp
 * on a LitSurface with a directional light, texture, and
 * several color SpotLights.
 * <p>
 * Then, it uses a ConvolveOp to create a text shadow that
 * is rendered on the textured background.
 * <p>
 * Finally, it uses a LightOp again to render a 3D looking 
 * text.
 *
 * @author           Vincent Hardy
 * @version          1.0, 09/29/1998
 * @see              com.sun.glf.goodies.LitSurface
 * @see              com.sun.glf.goodies.LightOp
 */
public class Lights implements CompositionFactory {
  
  /** Text to be displayed */
  String text = "Java 2D is Hot!";

  /** Background base color */
  Color backgroundColor = new Color(152, 152, 204);

  /** Text color */
  Color textColor = new Color(128, 0, 0);

  /** Size */
  Dimension size = new Dimension(200, 100);

  /** Text Font */
  Font font = new Font("SunSans-Heavy", Font.PLAIN, 48);

  /** Background texture */
  File textureFile = new File("caviar.jpg");

  /** Color for the text shadow */
  Color shadowColor = new Color(0, 0, 0, 128);

  double lightIntensity = 1;
  
  /*
   * Shadow placement
   */
  int shadowOffsetX = 10, shadowOffsetY = 10;
  float shadowShearX = 0, shadowShearY = 0;

  /** Controls the top light ramp color */
  Color lightTopRampColor = Color.white;

  /** Controls whether or not a top light ramp should be used */
  boolean lightTopRamp = true;

  /** Controls the bottom light ramp Color */
  Color lightBottomRampColor = Color.white;

  /** Controls whether or not bottom light ramp should be used */
  boolean lightBottomRamp = true;

  /** Directional light color */
  Color lightSunColor = Color.white;

  /** Controls whether or not Directional light should be used */
  boolean lightSun = true;

  /** Directional light intensity */
  double lightSunIntensity = 1.;

  /** Actually mapped to the specular reflection coeficient */
  int materialType = 1;

  /** Controls where the text appears in the composition */
  Anchor textAnchor = Anchor.CENTER;

  /** Controls how the text block is justified */
  TextAlignment textTextAlignment = TextAlignment.CENTER;

  /*
   * Accessors
   */

  public Anchor getTextAnchor(){
    return textAnchor;
  }

  public void setTextAnchor(Anchor textAnchor){
    this.textAnchor = textAnchor;
  }

  public TextAlignment getTextTextAlignment(){
    return textTextAlignment;
  }

  public void setTextTextAlignment(TextAlignment textTextAlignment){
    this.textTextAlignment = textTextAlignment;
  }

  public int getMaterialType(){
    return materialType;
  }

  public void setMaterialType(int materialType){
    this.materialType = materialType;
  }  

  public double getLightSunIntensity(){
    return lightSunIntensity;
  }

  public void setLightSunIntensity(double lightSunIntensity){
    this.lightSunIntensity = lightSunIntensity;
  }

  public Color getLightTopRampColor(){
    return lightTopRampColor;
  }

  public void setLightTopRampColor(Color lightTopRampColor){
    this.lightTopRampColor = lightTopRampColor;
  }

  public boolean getLightTopRamp(){
    return lightTopRamp;
  }

  public void setLightTopRamp(boolean lightTopRamp){
    this.lightTopRamp = lightTopRamp;
  }

  public Color getLightBottomRampColor(){
    return lightBottomRampColor;
  }

  public void setLightBottomRampColor(Color lightBottomRampColor){
    this.lightBottomRampColor = lightBottomRampColor;
  }

  public boolean getLightBottomRamp(){
    return lightBottomRamp;
  }

  public void setLightBottomRamp(boolean lightBottomRamp){
    this.lightBottomRamp = lightBottomRamp;
  }

  public Color getLightSunColor(){
    return lightSunColor;
  }

  public void setLightSunColor(Color lightSunColor){
    this.lightSunColor = lightSunColor;
  }

  public boolean getLightSun(){
    return lightSun;
  }

  public void setLightSun(boolean lightSun){
    this.lightSun = lightSun;
  }

  public double getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(double lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public int getShadowOffsetX(){
    return shadowOffsetX;
  }

  public void setShadowOffsetX(int shadowOffsetX){
    this.shadowOffsetX = shadowOffsetX;
  }

  public int getShadowOffsetY(){
    return shadowOffsetY;
  }

  public void setShadowOffsetY(int shadowOffsetY){
    this.shadowOffsetY = shadowOffsetY;
  }

  public float getShadowShearX(){
    return shadowShearX;
  }

  public void setShadowShearX(float shadowShearX){
    this.shadowShearX = shadowShearX;
  }

  public float getShadowShearY(){
    return shadowShearY;
  }

  public void setShadowShearY(float shadowShearY){
    this.shadowShearY = shadowShearY;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public File getTextureFile(){
    return textureFile;
  }

  public void setTextureFile(File textureFile){
    this.textureFile = textureFile;
  }

  public Font getFont(){
    return font;
  }

  public void setFont(Font font){
    this.font = font;
  }  

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);

    int w = size.width;
    int h = size.height;

    // 
    // Use TextLayer to perform the text layout
    // We then use the resulting Shape
    //
    Shape shape = TextLayer.makeTextBlock(text, font, w, textTextAlignment);
    Position textPosition = new Position(textAnchor, 0, 0);
    shape = textPosition.createTransformedShape(shape, cmp.getBounds());

    //
    // Create Background Layer. 
    //

    // Load texture map
    String textureFileName = textureFile.getPath();
    BufferedImage texture = Toolbox.loadImage(textureFileName, BufferedImage.TYPE_BYTE_GRAY);

    // Create elevation map from texture file
    long curTime = System.currentTimeMillis();
    ElevationMap map = null;
    if(texture != null){
      System.out.print("\nProcessing background elevations .... ");
      map = new ElevationMap(texture, true, 10);
      System.out.println("... Done : " + (System.currentTimeMillis()-curTime)/1000 + "(s)");
    }

    //
    // Create a lit surface which will be used both for
    // the background texture and the embossed text
    //
    LitSurface bkgSurface = new LitSurface(.3, 1, 1, materialType, map);
    if(lightSun)
      bkgSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, lightSunIntensity, lightSunColor));

    // Top spots
    int hm = 1; // Ramp margins
    Rectangle lightBounds = new Rectangle(0, hm, w, h-2*hm);
    SpotLight[] topSpots = LightsStudio.getLightRamp(lightBounds, 4, Anchor.TOP, 
						      new double[]{lightIntensity, lightIntensity, lightIntensity, lightIntensity}, 
						      new Color[] {lightTopRampColor, lightTopRampColor, lightTopRampColor, lightTopRampColor}, .5);
    if(lightTopRamp)
      bkgSurface.addLights(topSpots);

    // Bottom spots
    SpotLight[] bottomSpots = LightsStudio.getLightRamp(lightBounds, 3, Anchor.BOTTOM, new double[]{lightIntensity, lightIntensity, lightIntensity},
								new Color[]{lightBottomRampColor, lightBottomRampColor, lightBottomRampColor},
								0);
    if(lightBottomRamp)
      bkgSurface.addLights(bottomSpots);

    
    LightOp op = new LightOp(bkgSurface);

    //
    // Background is a simple ShapeLayer to which we attach a LightOp
    //
    ShapeLayer backgroundLayer = new ShapeLayer(cmp, cmp.getBounds(), new FillRenderer(backgroundColor));
    backgroundLayer.setRasterFilter(op);
    /*BufferedImage bkg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bkg.createGraphics();
    g.setPaint(backgroundColor);
    g.fillRect(0, 0, bkg.getWidth(), bkg.getHeight());
    g.dispose();
    curTime = System.currentTimeMillis();
    System.out.print("\nTexturizing background .... ");
    bkg = op.filter(bkg, bkg);
    System.out.println("... Done : " + (System.currentTimeMillis()-curTime)/1000 + "(s)");

    ImageLayer backgroundLayer = new ImageLayer(cmp, bkg);*/

    //
    // 

    //
    // Shadow layer
    //
    ConvolveOp blurOp = new ConvolveOp(new GaussianKernel(5));
    AffineTransform shadowTransform = AffineTransform.getShearInstance(shadowShearX, shadowShearY);
    ShapeLayer shadowLayer = new ShapeLayer(cmp, shape, new FillRenderer(shadowColor));
    shadowLayer.setTransform(shadowTransform);
    shadowLayer.setRasterFilter(blurOp, new Dimension(5, 5));

    shadowTransform.setToTranslation(shadowOffsetX, shadowOffsetY);
    shadowLayer.setTransform(shadowTransform);

    //
    // Now, create texturized text. Reuse the same lights.
    //
    curTime = System.currentTimeMillis();
    System.out.print("\nCreating text elevation map .... ");
    ElevationMap textMap = new ElevationMap(shape, 7, true, 10);
    LitSurface textSurface = new LitSurface(.3, 1, 1, materialType, textMap);
    if(lightSun)
      textSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, lightSunIntensity, lightSunColor));
    if(lightTopRamp)
      textSurface.addLights(topSpots);
    if(lightBottomRamp)
      textSurface.addLights(bottomSpots);

    System.out.println("... Done : " + (System.currentTimeMillis()-curTime)/1000 + "(s)");

    ShapeLayer embossedText = new ShapeLayer(cmp, cmp.getBounds(), new FillRenderer(textColor));
    embossedText.setRasterFilter(new LightOp(textSurface));
    embossedText.setLayerMask(shape);

    cmp.setLayers(new Layer[]{ backgroundLayer, shadowLayer, embossedText });
    return cmp;
  }
}
