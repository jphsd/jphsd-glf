/*
 * @(#)GLFWebDemoTwo.java
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

package com.sun.glf.demos;

import java.awt.*;
import java.text.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.Vector;
import java.io.File;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * Illustrates the use of some of the extensions in the com.sun.glf.goodies
 * package.
 *
 * @author                  Vincent Hardy
 * @version                 1.0, 04/21/1999
 */
public class GLFWebDemoTwo implements CompositionFactory{
  Dimension size = new Dimension(600, 400);

  File backgroundImageFile = new File("");

  String text = "Java 2D is Gold";

  Font textFont = new Font("SunSansCondensed-Heavy", Font.PLAIN, 140);

  int blurRadius = 12;

  int textShadowOffsetX = 10;

  int textShadowOffsetY = 10;

  int embossBlurRadius = 12;

  boolean whiteIsHigh = true;

  int heightScale = 10;

  Anchor lightAnchor = Anchor.TOP;

  float lightIntensity = 2;

  Color lightColor = Color.white;

  float backgroundRescale = 1.5f;

  Glyph borderGlyph = new Glyph(new Font("serif", Font.PLAIN, 50), '@');

  Color borderColor = Color.orange;

  String centerText = "GLF";

  Font centerTextFont = new Font("SunSansCondensed-Heavy", Font.PLAIN, 140);

  public String getCenterText(){
    return centerText;
  }

  public void setCenterText(String centerText){
    this.centerText = centerText;
  }

  public Font getCenterTextFont(){
    return centerTextFont;
  }

  public void setCenterTextFont(Font centerTextFont){
    this.centerTextFont = centerTextFont;
  }

  public Glyph getBorderGlyph(){
    return borderGlyph;
  }

  public void setBorderGlyph(Glyph borderGlyph){
    this.borderGlyph = borderGlyph;
  }

  public Color getBorderColor(){
    return borderColor;
  }

  public void setBorderColor(Color borderColor){
    this.borderColor = borderColor;
  }

  public float getBackgroundRescale(){
    return backgroundRescale;
  }

  public void setBackgroundRescale(float backgroundRescale){
    this.backgroundRescale = backgroundRescale;
  }

  public float getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(float lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public File getBackgroundImageFile(){
    return backgroundImageFile;
  }

  public void setBackgroundImageFile(File backgroundImageFile){
    this.backgroundImageFile = backgroundImageFile;
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

  public int getBlurRadius(){
    return blurRadius;
  }

  public void setBlurRadius(int blurRadius){
    this.blurRadius = blurRadius;
  }

  public int getTextShadowOffsetX(){
    return textShadowOffsetX;
  }

  public void setTextShadowOffsetX(int textShadowOffsetX){
    this.textShadowOffsetX = textShadowOffsetX;
  }

  public int getTextShadowOffsetY(){
    return textShadowOffsetY;
  }

  public void setTextShadowOffsetY(int textShadowOffsetY){
    this.textShadowOffsetY = textShadowOffsetY;
  }

  public int getEmbossBlurRadius(){
    return embossBlurRadius;
  }

  public void setEmbossBlurRadius(int embossBlurRadius){
    this.embossBlurRadius = embossBlurRadius;
  }

  public boolean isWhiteIsHigh(){
    return whiteIsHigh;
  }

  public void setWhiteIsHigh(boolean whiteIsHigh){
    this.whiteIsHigh = whiteIsHigh;
  }

  public int getHeightScale(){
    return heightScale;
  }

  public void setHeightScale(int heightScale){
    this.heightScale = heightScale;
  }

  public Anchor getLightAnchor(){
    return lightAnchor;
  }

  public void setLightAnchor(Anchor lightAnchor){
    this.lightAnchor = lightAnchor;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
   
    //
    // First, create the background layer: load image in gray scale.
    //
    BufferedImage backgroundImage = Toolbox.loadImage(backgroundImageFile, BufferedImage.TYPE_BYTE_GRAY);
    if(backgroundImage == null)
      throw new Error("Could not load : " + backgroundImageFile);
    ImageLayer backgroundLayer = new ImageLayer(cmp, backgroundImage, Position.CENTER);
    RescaleOp adjustBrightness = new RescaleOp(backgroundRescale, 0, null);
    backgroundLayer.setImageFilter(adjustBrightness);

    //
    // Create a border with the ShapeStroke
    //
    Shape borderPattern = borderGlyph.getShape();
    Rectangle borderPatternBounds = borderPattern.getBounds();
    ShapeStroke borderStroke = new ShapeStroke(new Shape[]{borderPattern}, borderPatternBounds.width*1.2f);
    Rectangle borderRect = new Rectangle(borderPatternBounds.width, 
					 borderPatternBounds.height, 
					 size.width - 2*borderPatternBounds.width,
					 size.height - 2*borderPatternBounds.height);
    ShapeLayer borderLayer = new ShapeLayer(cmp, borderRect, new StrokeRenderer(borderColor, borderStroke));
    borderLayer.setComposite(ColorComposite.Hs);

    //
    // Create a second layer for the border outline
    //
    CompositeStroke borderOutliner = new CompositeStroke(borderStroke, new BasicStroke());
    ShapeLayer borderOutlineLayer = new ShapeLayer(cmp, borderRect, 
						   new StrokeRenderer(borderColor, borderOutliner));

    //
    // Now, create a text block
    //
    Shape textShape = TextLayer.makeTextBlock(text, textFont, -1, TextAlignment.CENTER);
    int textHeight = textShape.getBounds().height;
    int textWidth = textShape.getBounds().width;
    
    //
    // Transform text with a BumpTransform
    //
    Transform sealMaker = new BumpTransform((float)Math.PI*2);
    textShape = sealMaker.transform(textShape);

    //
    // Add horizontal text in the center 
    //
    Shape centerTextShape = TextLayer.makeTextBlock(centerText, centerTextFont, -1, TextAlignment.CENTER);
    centerTextShape = Position.CENTER.createTransformedShape(centerTextShape, textShape.getBounds());
    
    //
    // Merge the two text Shapes
    //
    GeneralPath textShapeTmp = new GeneralPath(textShape);
    textShapeTmp.append(centerTextShape, false);
    textShape = textShapeTmp;

    //
    // Position textBlock in Composition
    //
    textShape = Position.CENTER.createTransformedShape(textShape, cmp.getBounds());

    //
    // Create the text shadow
    //    
    ShapeLayer textShadowLayer = new ShapeLayer(cmp, textShape, new FillRenderer(Color.black));
    ConvolveOp largeBlur = new ConvolveOp( new GaussianKernel(blurRadius) );
    Dimension largeBlurMargins = new Dimension(blurRadius*2, blurRadius*2);
    textShadowLayer.setImageFilter(largeBlur, largeBlurMargins);
    textShadowLayer.setTransform(AffineTransform.getTranslateInstance(textShadowOffsetX, textShadowOffsetY));
    
    //
    // Create 'gold' text. First, create the 'gold' paint. Base the height of the Paint on the
    // first line's height
    //
    Color goldColors[] = { Color.white,
			   Color.white,
			   new Color(235, 212, 19),
			   new Color(180, 143, 29)
    };
    Rectangle textBounds = textShape.getBounds();
    float goldIntervals[] = {textWidth/(2*(float)Math.PI) - textHeight, 
			     textHeight/3f, textHeight/2f};
    RadialGradientPaintExt goldPaint = new RadialGradientPaintExt(textBounds, goldColors,
								  goldIntervals);
    ShapeLayer textLayer = new ShapeLayer(cmp, cmp.getBounds(), new FillRenderer(goldPaint));
    textLayer.setLayerMask(textShape);

    ElevationMap textMap = new ElevationMap(textShape, 
						 embossBlurRadius, whiteIsHigh, heightScale);
    LitSurface textSurface = new LitSurface(0, LitSurfaceType.METALLIC, textMap);
    DirectionalLight sunLight = LightsStudio.getSunLight(lightAnchor, lightIntensity, lightColor);
    textSurface.addLight(sunLight);
    LightOp textFilter = new LightOp(textSurface);
    textLayer.setRasterFilter(textFilter);

    // ShapeLayer junkLayer = new ShapeLayer(cmp, textBounds, new FillRenderer(goldPaint));

    Layer layerStack[] = { backgroundLayer, borderLayer, borderOutlineLayer, textShadowLayer, textLayer};
    cmp.setLayers(layerStack);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;    
  }
}
