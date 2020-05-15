/*
 * @(#)RecessedShadow.java
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
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * Demonstrates how to create a recessed shadow effect for a Shape.
 * The configuration parameters define where the spot light (i.e. a SpotLight)
 * is anchored in the composition. This defines the shadow setting.
 * However, the shadow depth can be set (i.e. the amount of offset for the
 * shadow, the direction being implied by the light source).
 * Otherwise, Colors and Fonts can be set, as for other CompositionFactories.
 *
 * @author         Vincent Hardy
 * @version        1.0, 10/24/1998
 */
public class RecessedShadow implements CompositionFactory{
  static final int shadowXDir[] = {1, 0, -1, -1, -1, 0, 1, 1, 0 };
  static final int shadowYDir[] = {1, 1, 1, 0, -1, -1, -1, 0, 0};

  /** Composition Dimension */
  Dimension size = new Dimension(200, 100);

  /** Background Color */
  Color backgroundColor = new Color(120, 120, 150);

  /** Image used in the composition */
  File imageFile = new File("");

  /** Background Light Color */
  Color lightColor = Color.white;

  /** Defines where the spot light is anchored in the composition */
  Anchor lightAnchor = Anchor.TOP_LEFT;

  /** Light intensity */
  double lightIntensity = 1;

  /** Ambiant light intensity */
  double lightAmbiant = .3;

  /** Light Dimension */
  Dimension lightSize = new Dimension(100, 200);

  /** Offset for the light spot position */
  Dimension lightOffset = new Dimension(10, 10);

  /** Text String */
  String text = "Recessed Shadow";

  /** Font used for the text */
  Font textFont = new Font("Times New Roman", Font.PLAIN, 40);

  /** Text TextAlignment in block */
  TextAlignment textTextAlignment = TextAlignment.CENTER;

  /** Text Anchor in composition */
  Anchor textAnchor = Anchor.CENTER;

  /** Text Offset : used in addition to the anchor */
  Dimension textOffset = new Dimension(0, 0);

  /** Text Color */
  Color textColor = new Color(80, 80, 110);

  /** Shadow Color */
  Color shadowColor = new Color(0, 0, 0, 128);

  /** Shadow blur radius */
  int shadowRadius = 3;

  /** Shadow depth */
  int shadowDepth = 5;

  /** Shadow horizontal offset */
  int shadowOffsetX = 5;

  /** Shadow vertical offset */
  int shadowOffsetY = 5;

  /** Surface material type */
  LitSurfaceType surfaceType = LitSurfaceType.NORMAL;

  /** Size of the rim */
  float rimWidth = 1f;

  /** Color of the light rim */
  Color rimLightColor = new Color(1f, 1f, 1f, .3f);

  /** Color of the dark rim */
  Color rimDarkColor = new Color(0f, 0f, 0f, .6f);

  public Color getRimLightColor(){
    return rimLightColor;
  }

  public void setRimLightColor(Color rimLightColor){
    this.rimLightColor = rimLightColor;
  }

  public Color getRimDarkColor(){
    return rimDarkColor;
  }

  public void setRimDarkColor(Color rimDarkColor){
    this.rimDarkColor = rimDarkColor;
  }  

  public float getRimWidth(){
    return rimWidth;
  }

  public void setRimWidth(float rimWidth){
    this.rimWidth = rimWidth;
  }

  public LitSurfaceType getSurfaceType(){
    return surfaceType;
  }

  public void setSurfaceType(LitSurfaceType surfaceType){
    this.surfaceType = surfaceType;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }

  public Anchor getLightAnchor(){
    return this.lightAnchor;
  }

  public void setLightAnchor(Anchor lightAnchor){
    this.lightAnchor = lightAnchor;
    setShadowDepth(shadowDepth); // forces processing of shadow offset
  }

  public double getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(double lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public Dimension getLightSize(){
    return lightSize;
  }

  public void setLightSize(Dimension lightSize){
    this.lightSize = lightSize;
  }

  public double getLightAmbiant(){
    return lightAmbiant;
  }

  public void setLightAmbiant(double lightAmbiant){
    this.lightAmbiant = lightAmbiant;
  }

  public Dimension getLightOffset(){
    return lightOffset;
  }

  public void setLightOffset(Dimension lightOffset){
    this.lightOffset = lightOffset;
  }

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public TextAlignment getTextTextAlignment(){
    return textTextAlignment;
  }

  public void setTextTextAlignment(TextAlignment textTextAlignment){
    this.textTextAlignment = textTextAlignment;
  }

  public Anchor getTextAnchor(){
    return textAnchor;
  }

  public void setTextAnchor(Anchor textAnchor){
    this.textAnchor = textAnchor;
  }

  public Dimension getTextOffset(){
    return textOffset;
  }

  public void setTextOffset(Dimension textOffset){
    this.textOffset = textOffset;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public int getShadowRadius(){
    return shadowRadius;
  }

  public void setShadowRadius(int shadowRadius){
    this.shadowRadius = shadowRadius;
  }

  public int getShadowDepth(){
    return shadowDepth;
  }

  public void setShadowDepth(int shadowDepth){
    this.shadowDepth = shadowDepth;
    shadowOffsetX = shadowXDir[lightAnchor.toInt()]*shadowDepth;
    shadowOffsetY = shadowYDir[lightAnchor.toInt()]*shadowDepth;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
    
    //
    // Background layer : fill with image 
    // and apply flood light.
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load image : " + imageFile);

    //
    // Fit image to background size
    //
    float scaleX = size.width/(float)image.getWidth();
    float scaleY = size.height/(float)image.getHeight();
    AffineTransform imageScale = AffineTransform.getScaleInstance(scaleX, scaleY);
    AffineTransformOp imageScaleOp = new AffineTransformOp(imageScale, AffineTransformOp.TYPE_BILINEAR);
    image = imageScaleOp.filter(image, null);

    // Tint image
    ToneAdjustmentOp backgroundTinter = new ToneAdjustmentOp(backgroundColor);
    image = backgroundTinter.filter(image, null);

    // Use the LightsStudio to create a spot light cast on the backround
    Rectangle rect = new Rectangle(0, 0, size.width, size.height);
    Rectangle lightRect = new Rectangle(rect.x + lightOffset.width, rect.y + lightOffset.height, rect.width, rect.height);
    SpotLight spot = LightsStudio.getSpotLight(lightRect, // Defines the rectangle where the light is positioned
						lightAnchor, // Defines the light position in the rectangle
						lightSize, 
						lightIntensity,  
						lightColor);

    // Create a regulare surface description for the background
    LitSurface backgroundSurface = new LitSurface(lightAmbiant, surfaceType, null);

    // Add the spot light to the surface description
    backgroundSurface.addLight(spot);

    // Create a LightOp based on the surface description
    LightOp lighting = new LightOp(backgroundSurface);

    image = lighting.filter(image, null);

    // Create a plain background rectangle filled with the image
    // The image will be streched to fit the composition rectangle
    TexturePaint texturePaint = new TexturePaint(image, rect);
    FillRenderer texturePainter = new FillRenderer(texturePaint);
    Layer background = new ShapeLayer(cmp, rect, texturePainter);

    //
    // Text layer
    //
    Shape textBlock = TextLayer.makeTextBlock(text, textFont, size.width, textTextAlignment);
    Position textBlockPosition = new Position(textAnchor, textOffset.width, textOffset.height);
    textBlock = textBlockPosition.createTransformedShape(textBlock, cmp.getBounds());
    ShapeLayer textLayer = new ShapeLayer(cmp, textBlock.getBounds(), texturePainter);
    
    ToneAdjustmentOp textTinter = new ToneAdjustmentOp(textColor);
    textLayer.setImageFilter(textTinter);

    textLayer.setLayerMask(textBlock);

    //
    // Shadow. Here again, we use an OpLayer to provide the blurring
    // of the shadow. The blurring is acutally a ConvolveOp using
    // a gaussian Kernel, that is a Kernel where values decrease
    // exponetially around the Kernel center.
    //

    // Once again, the base layer is just a rectangle filled with the shadow color
    Shape shape = textBlock;
    Area shadow = new Area(rect);
    shadow.subtract(new Area(shape));
    Layer blurredShadow = new ShapeLayer(cmp, shadow, new FillRenderer(shadowColor));
    ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowRadius));
    blurredShadow.setImageFilter(blur, new Dimension(shadowRadius, shadowRadius));
    blurredShadow.setTransform(AffineTransform.getTranslateInstance(shadowOffsetX, shadowOffsetY));
    blurredShadow.setLayerMask(shape);

    // 
    // Create 'cheap' embossing with strokes
    //
    StrokeRenderer darkSideStroke = new StrokeRenderer(rimDarkColor, rimWidth);
    StrokeRenderer lightSideStroke = new StrokeRenderer(rimLightColor, rimWidth);
    Layer darkSide = new ShapeLayer(cmp, shape, darkSideStroke);
    Layer lightSide = new ShapeLayer(cmp, shape, lightSideStroke);
    AffineTransform t = AffineTransform.getTranslateInstance(-shadowXDir[lightAnchor.toInt()], -shadowYDir[lightAnchor.toInt()]);
    darkSide.setLayerMask(t.createTransformedShape(shape));

    //
    // Set Layers which make up the composition, in the drawing order
    //
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			       RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setLayers(new Layer[] { background, textLayer, blurredShadow, lightSide, darkSide});

    return cmp;
    
  }
}
