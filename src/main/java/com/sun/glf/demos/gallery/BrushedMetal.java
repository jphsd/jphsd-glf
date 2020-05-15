/*
 * @(#)BrushedMetal.java
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
 * Illustrates how to creatively use a BufferedImage, random noise
 * and a scale transformation to create a BrushedMetal texture.
 *
 * @author             Vincent Hardy
 * @version            1.0, 10/07/1998
 */
public class BrushedMetal implements CompositionFactory{
  /** Composition Size */
  Dimension size = new Dimension(400, 400);

  /** Noise density, in dot per 10 pixel wide square*/
  int textureNoiseDensity = 30;

  /** Noise color */
  Color textureNoiseColor = Color.white;

  /** Texture base color */
  Color textureColor = Color.gray;

  /** Texture horizontal blur width */
  int textureBlurWidth = 10;

  /** 
   * Constrols the scale applied to the texture after noise
   *  has been added.
   */
  int textureScaleFactor = 40;

  /**
   * Controls how high the brush strokes appear
   */
  int textureHeightScale = 10;

  /**
   * Controls the SpotLight's focus
   */
  int lightFocus = 4;

  /*
   * Spot light controls 
   */
  int lightOffset = -100;
  Dimension lightSize = new Dimension(1600, 1000);
  Anchor lightAnchor = Anchor.TOP_LEFT;
  double lightIntensity = 2.;
  Color lightColor = new Color(220, 220, 255); // light blue
  double lightAmbient = .2;

  Color backgroundColor = Color.white;

  String text = "Brushed Metal";

  Font textFont = new Font("sansserif", Font.BOLD, 120);

  Color textShadowColor = new Color(0, 0, 0, 128);

  Dimension textShadowOffset = new Dimension(20, 20);

  int textSeparationOffset = 2;

  Color textSeparationColor = Color.white;

  LitSurfaceType surfaceType = LitSurfaceType.METALLIC;

  boolean useTextureMap;

  int shadowBlurRadius = 12;

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public int getLightFocus(){
    return lightFocus;
  }

  public void setLightFocus(int lightFocus){
    this.lightFocus = lightFocus;
  }

  public boolean getUseTextureMap(){
    return useTextureMap;
  }

  public void setUseTextureMap(boolean useTextureMap){
    this.useTextureMap = useTextureMap;
  }

  public LitSurfaceType getSurfaceType(){
    return surfaceType;
  }

  public void setSurfaceType(LitSurfaceType surfaceType){
    this.surfaceType = surfaceType;
  }

  public int getTextSeparationOffset(){
    return textSeparationOffset;
  }

  public void setTextSeparationOffset(int textSeparationOffset){
    this.textSeparationOffset = textSeparationOffset;
  }

  public Color getTextSeparationColor(){
    return textSeparationColor;
  }

  public void setTextSeparationColor(Color textSeparationColor){
    this.textSeparationColor = textSeparationColor;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
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

  public Color getTextShadowColor(){
    return textShadowColor;
  }

  public void setTextShadowColor(Color textShadowColor){
    this.textShadowColor = textShadowColor;
  }

  public Dimension getTextShadowOffset(){
    return textShadowOffset;
  }

  public void setTextShadowOffset(Dimension textShadowOffset){
    this.textShadowOffset = textShadowOffset;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public int getTextureNoiseDensity(){
    return textureNoiseDensity;
  }

  public void setTextureNoiseDensity(int textureNoiseDensity){
    this.textureNoiseDensity = textureNoiseDensity;
  }

  public Color getTextureNoiseColor(){
    return textureNoiseColor;
  }

  public void setTextureNoiseColor(Color textureNoiseColor){
    this.textureNoiseColor = textureNoiseColor;
  }

  public Color getTextureColor(){
    return textureColor;
  }

  public void setTextureColor(Color textureColor){
    this.textureColor = textureColor;
  }

  public int getTextureBlurWidth(){
    return textureBlurWidth;
  }

  public void setTextureBlurWidth(int textureBlurWidth){
    this.textureBlurWidth = textureBlurWidth;
  }

  public int getTextureScaleFactor(){
    return textureScaleFactor;
  }

  public void setTextureScaleFactor(int textureScaleFactor){
    this.textureScaleFactor = textureScaleFactor;
  }

  public int getTextureHeightScale(){
    return textureHeightScale;
  }

  public void setTextureHeightScale(int textureHeightScale){
    this.textureHeightScale = textureHeightScale;
  }

  public int getLightOffset(){
    return lightOffset;
  }

  public void setLightOffset(int lightOffset){
    this.lightOffset = lightOffset;
  }

  public Dimension getLightSize(){
    return lightSize;
  }

  public void setLightSize(Dimension lightSize){
    this.lightSize = lightSize;
  }

  public Anchor getLightAnchor(){
    return lightAnchor;
  }

  public void setLightAnchor(Anchor lightAnchor){
    this.lightAnchor = lightAnchor;
  }

  public double getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(double lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }

  public double getLightAmbient(){
    return lightAmbient;
  }

  public void setLightAmbient(double lightAmbient){
    this.lightAmbient = lightAmbient;
  }

  private BufferedImage createTexture(){
    // 1. Create a base BufferedImage
    int bufWidth = 2*textureBlurWidth + (int)Math.rint(size.width/textureScaleFactor);
    int bufHeight = 2*textureBlurWidth + size.height;
    BufferedImage buf = new BufferedImage(bufWidth, bufHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 2. Fill with base background color
    g.setPaint(textureColor);
    g.fillRect(0, 0, bufWidth, size.height);
    
    // 3. Add random noise
    int cols = bufWidth/10 + 1;
    int rows = bufHeight/10 + 1;
    g.setPaint(textureNoiseColor);
    Rectangle2D.Float noiseRect = new Rectangle2D.Float(0, 0, 1, 1);
    for (int i=0; i<cols; i++){
      for(int j=0; j<rows; j++){
	for(int k=0; k<textureNoiseDensity; k++){
	  noiseRect.x = i*10 + (float)(10*Math.random());
	  noiseRect.y = j*10 + (float)(10*Math.random());
	  noiseRect.width = 10*(float)Math.random();
	  noiseRect.height = (float)Math.random();
	  g.fill(noiseRect);
	}
      }
    }

    // 4. Blur noise with an horizontal blur
    float horBlur[] = new float[textureBlurWidth];
    for(int i=0; i<textureBlurWidth; i++)
      horBlur[i] = 1f/(float)textureBlurWidth;
    Kernel horBlurKernel = new Kernel(textureBlurWidth, 1, horBlur);
    ConvolveOp blur = new ConvolveOp(horBlurKernel);
    buf = blur.filter(buf, null);
    buf = buf.getSubimage(textureBlurWidth, textureBlurWidth, size.width/textureScaleFactor, size.height);

    // 5. Scale the image to itensify the 'brush' effect
    RenderingHints rh = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    AffineTransform scale = AffineTransform.getScaleInstance(textureScaleFactor, 1);
    AffineTransformOp scaleOp = new AffineTransformOp(scale, rh);
    buf = scaleOp.filter(buf, null);
    
    return buf;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
    Rectangle cmpRect = new Rectangle(0, 0, size.width, size.height);

    //
    // Create a SpotLight : lighting is used both for the background
    // and the brushed metal text.
    //
    Rectangle lightRect = (Rectangle)cmpRect.clone();
    AffineTransform lightPositionAdjustment = lightAnchor.getAdjustmentTranslation(lightOffset, lightOffset);
    lightRect.x = (int)lightPositionAdjustment.getTranslateX();
    lightRect.y = (int)lightPositionAdjustment.getTranslateY();
    SpotLight light = LightsStudio.getSpotLight(lightRect, 
						lightAnchor, 
						lightSize,
						lightIntensity, lightColor);
    light.setNp(lightFocus);
    
    //
    // Create a LitSurface and add the SpotLight to it.
    //
    LitSurface litSurface = new LitSurface(lightAmbient, surfaceType, null);
    litSurface.addLight(light);

    //
    // Create LightOp using the LitSurface just created
    //
    LightOp op = new LightOp(litSurface);

    //
    // Background is a plain rectangle
    //
    ShapeLayer litBackground = new ShapeLayer(cmp, cmpRect, new FillRenderer(backgroundColor));
    litBackground.setRasterFilter(op);

    //
    // Text Shadow Layer
    //
    float blurScale = 3f/shadowBlurRadius;
    GaussianKernel kernel = new GaussianKernel(3);
    
    AffineTransform shrinkTxf = AffineTransform.getScaleInstance(blurScale, blurScale);
    AffineTransform blowUpTxf = AffineTransform.getScaleInstance(1/blurScale, 1/blurScale);
    
    AffineTransformOp shrinkOp = new AffineTransformOp(shrinkTxf, null);
    AffineTransformOp blowUpOp = new AffineTransformOp(blowUpTxf, AffineTransformOp.TYPE_BILINEAR);
    ConvolveOp smallBlurOp = new ConvolveOp(kernel);

    CompositeOp blur = new CompositeOp(new BufferedImageOp[]{shrinkOp, smallBlurOp, blowUpOp});
    Dimension blurMargins = new Dimension(shadowBlurRadius*2, shadowBlurRadius*2);

    Shape textShape = TextLayer.makeTextBlock(text, textFont, size.width, TextAlignment.CENTER);
    textShape = Position.CENTER.createTransformedShape(textShape, cmpRect);
    ShapeLayer textShadowLayer = new ShapeLayer(cmp, textShape, new FillRenderer(textShadowColor));
    AffineTransform shadowAdjustment = lightAnchor.getAdjustmentTranslation(textShadowOffset.width, textShadowOffset.height);
    textShadowLayer.setTransform(shadowAdjustment);
    textShadowLayer.setImageFilter(blur, blurMargins);

    //
    // Add another layer to 'separate' the text from its shadow.
    //
    ShapeLayer textSeparationLayerTop= new ShapeLayer(cmp, 
						      textShape,
						      new FillRenderer(textSeparationColor));
    textSeparationLayerTop.setTransform(lightAnchor.getAdjustmentTranslation(-textSeparationOffset, -textSeparationOffset));

    //
    // Create texture
    //
    BufferedImage brushedMetal = createTexture();

    //
    // Create brushed metal text by setting the lit metal's clip to the 
    // text shape.
    //
    litSurface = new LitSurface(lightAmbient, surfaceType, null);
    if(useTextureMap){
      ElevationMap map = new ElevationMap(brushedMetal, true, textureHeightScale);
      litSurface.setElevationMap(map);
    }
    litSurface.addLight(light);
    op = new LightOp(litSurface);

    ImageLayer metalTextLayer = new ImageLayer(cmp, brushedMetal, Position.CENTER);
    metalTextLayer.setRasterFilter(op);
    metalTextLayer.setLayerMask(textShape);

    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setLayers(new Layer[]{litBackground, textShadowLayer, textSeparationLayerTop, metalTextLayer});

    return cmp;
  }
}
