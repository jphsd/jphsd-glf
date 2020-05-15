/*
 * @(#)ShapeCastShadowComposition.java
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
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import java.awt.color.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This examples illustrates how to create a cast shadow effect on an 
 * image. 
 *
 * @author         Vincent Hardy
 * @version        1.0, 06/03/1999
 */
public class ShapeCastShadowComposition implements CompositionFactory{
  Glyph glyph = new Glyph(new Font("Impact MT", Font.PLAIN, 120), '@');

  Color glyphColor = new Color(30, 30, 40);

  int ovalMargin = 10;

  int labelGap = 10;

  Font labelFont = new Font("Impact MT", Font.PLAIN, 30);;

  String label = "email us";

  Dimension shadowOffset = new Dimension(10, 10);

  Color shadowColor = new Color(0, 0, 0, 128);

  int labelWaves = 2;

  float labelWaveHeight = 12;

  int embossingBlurRadius = 4;

  int embossingScale = 10;

  Dimension margins = new Dimension(20, 20);

  int shadowBlurRadius = 4;

  Color labelColor = Color.black;

  float shadowShearX = 0.2f;

  Color backgroundColor = new Color(255, 255, 255, 0);

  float shadowScaleX = 1f;

  float shadowScaleY = .5f;

  public float getShadowScaleX(){
    return shadowScaleX;
  }

  public void setShadowScaleX(float shadowScaleX){
    this.shadowScaleX = shadowScaleX;
  }

  public float getShadowScaleY(){
    return shadowScaleY;
  }

  public void setShadowScaleY(float shadowScaleY){
    this.shadowScaleY = shadowScaleY;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void  setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public float getShadowShearX(){
    return shadowShearX;
  }

  public void setShadowShearX(float shadowShearX){
    this.shadowShearX = shadowShearX;
  }

  public Color getLabelColor(){
    return labelColor;
  }

  public void setLabelColor(Color labelColor){
    this.labelColor = labelColor;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public Dimension getMargins(){
    return margins;
  }

  public void setMargins(Dimension margins){
    this.margins = margins;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public int getLabelWaves(){
    return labelWaves;
  }

  public void setLabelWaves(int labelWaves){
    this.labelWaves = labelWaves;
  }

  public float getLabelWaveHeight(){
    return labelWaveHeight;
  }

  public void setLabelWaveHeight(float labelWaveHeight){
    this.labelWaveHeight = labelWaveHeight;
  }

  public int getEmbossingBlurRadius(){
    return embossingBlurRadius;
  }

  public void setEmbossingBlurRadius(int embossingBlurRadius){
    this.embossingBlurRadius = embossingBlurRadius;
  }

  public int getEmbossingScale(){
    return embossingScale;
  }

  public void setEmbossingScale(int embossingScale){
    this.embossingScale = embossingScale;
  }

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

  public Color getGlyphColor(){
    return glyphColor;
  }

  public void setGlyphColor(Color glyphColor){
    this.glyphColor = glyphColor;
  }

  public int getOvalMargin(){
    return ovalMargin;
  }

  public void setOvalMargin(int ovalMargin){
    this.ovalMargin = ovalMargin;
  }

  public int getLabelGap(){
    return labelGap;
  }

  public void setLabelGap(int labelGap){
    this.labelGap = labelGap;
  }

  public Font getLabelFont(){
    return labelFont;
  }

  public void setLabelFont(Font labelFont){
    this.labelFont = labelFont;
  }

  public String getLabel(){
    return label;
  }

  public void setLabel(String label){
    this.label = label;
  }

  public Dimension getShadowOffset(){
    return shadowOffset;
  }

  public void setShadowOffset(Dimension shadowOffset){
    this.shadowOffset = shadowOffset;
  }
  
  public Composition build(){
    //
    // First, build the Shape that will represent the button
    //
    Shape symbol = glyph.getShape();
    Rectangle symbolBounds = symbol.getBounds();
    Shape ellipse = new Ellipse2D.Float(symbolBounds.x - ovalMargin*2,
					symbolBounds.y - ovalMargin,
					symbolBounds.width + ovalMargin*4,
					symbolBounds.height + ovalMargin*2);
    Area cutOutSymbolArea = new Area(ellipse);
    cutOutSymbolArea.subtract(new Area(symbol));
    Rectangle cutOutSymbolBounds = cutOutSymbolArea.getBounds();
    
    //
    // Now, create the label Shape and transform it with the WaveTransform
    //
    Shape labelShape = TextLayer.makeTextBlock(label, labelFont);
    WaveTransform labelWaver = new WaveTransform(labelWaves, labelWaveHeight);
    MunchTransform munchTransform = new MunchTransform(1);
    labelWaver.concatenate(munchTransform);
    labelShape = labelWaver.transform(labelShape);

    //
    // Process composition size based on cutOutSymbol and text label.
    //
    Rectangle labelBounds = labelShape.getBounds();
    int width = (int)(Math.max(cutOutSymbolBounds.width, labelBounds.width) + 2*margins.width);
    int height = cutOutSymbolBounds.height + labelGap + labelBounds.height + 2*margins.height;
    Dimension size = new Dimension(width, height);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    // Position Shape in device space, because we will use it to build an elevation map
    Position symbolPosition = new Position(Anchor.TOP, 0, margins.height);
    Shape cutOutSymbol = symbolPosition.createTransformedShape(cutOutSymbolArea, cmp.getBounds());

    //
    // Now, create a LightOp to extrude our cutOutSymbol
    //
    LitSurface litSurface = new LitSurface(0);
    DirectionalLight sunLight = LightsStudio.getSunLight(Anchor.TOP_LEFT,
							 1, Color.white);
    litSurface.addLight(sunLight);
    ElevationMap embossingMap = new ElevationMap(cutOutSymbol, embossingBlurRadius,
						 true, embossingScale);
    litSurface.setElevationMap(embossingMap);
    LightOp embosser = new LightOp(litSurface);

    //
    // Create a first layer with the LightOp
    //
    ShapeLayer embossedSymbolLayer 
      = new ShapeLayer(cmp, cutOutSymbol.getBounds(), new FillRenderer(glyphColor));
    embossedSymbolLayer.setRasterFilter(embosser);
    embossedSymbolLayer.setLayerMask(cutOutSymbol);
    
    //
    // Alternate method, creates a faster but not quite as good result
    // as previous method.
    //
    // ShapeLayer embossedSymbolLayer
    //   = new ShapeLayer(cmp, cutOutSymbol, new FillRenderer(glyphColor));
    // embossedSymbolLayer.setRasterFilter(embosser);

    //
    // Now, create a shadow for the cutOutSymbol shape
    //

    // First, create the gradient paint we will use for the shadow
    Shape symbolShadow = cutOutSymbolArea;
    Rectangle symbolShadowBounds = symbolShadow.getBounds();
    Color transparentShadowColor = new Color(shadowColor.getRed(), shadowColor.getGreen(), 
					     shadowColor.getBlue(), 0);
    GradientPaint symbolShadowPaint = new GradientPaint(0, symbolShadowBounds.y, transparentShadowColor,
							0, symbolShadowBounds.y + symbolShadowBounds.height, 
							shadowColor);

    // Now, create a layer for the symbol shadow
    Position symbolShadowPosition = new Position(Anchor.TOP, 0, margins.height, 
						 getBottomLeftShearTransform(symbolShadow));

    ShapeLayer symbolShadowLayer = new ShapeLayer(cmp, symbolShadow, 
						  new FillRenderer(symbolShadowPaint), 
						  symbolShadowPosition);

    // Blur image
    Kernel blurKernel = new GaussianKernel(shadowBlurRadius);
    ConvolveOp blur = new ConvolveOp(blurKernel);
    Dimension blurMargins = new Dimension(shadowBlurRadius*2, shadowBlurRadius*2);
    symbolShadowLayer.setImageFilter(blur, blurMargins);
   
    //
    // Create a layer for the text label
    //
    Position labelPosition = new Position(Anchor.BOTTOM, 0, margins.height);
    ShapeLayer labelLayer = new ShapeLayer(cmp, labelShape, 
					   new FillRenderer(labelColor),
					   labelPosition);

    //
    // Create a shadow for the text label
    //
    Position labelShadowPosition = new Position(Anchor.BOTTOM, 0, margins.height,
						getBottomLeftShearTransform(labelShape));
    ShapeLayer labelShadowLayer = new ShapeLayer(cmp, labelShape, new FillRenderer(shadowColor), labelShadowPosition);
    labelShadowLayer.setImageFilter(blur, blurMargins);

    
    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{ symbolShadowLayer, embossedSymbolLayer,
				 labelShadowLayer, labelLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    
    return cmp;    
    
  }

  //
  // This is assuming that the transformation will apply about the object's center,
  // as it does with the Position class.
  //
  private AffineTransform getBottomLeftShearTransform(Shape shape){
    AffineTransform shear = new AffineTransform();
    Rectangle bounds = shape.getBounds();

    // Add offset
    shear.translate(shadowOffset.width, shadowOffset.height);

    // Align bottom left point with original Shape
    shear.translate(-bounds.width/2f, bounds.height/2f);

    // Shear
    shear.shear(shadowShearX, 0);

    // Scale
    shear.scale(shadowScaleX, shadowScaleY);

    // Move to top left in center
    shear.translate(bounds.width/2f, -bounds.height/2f);
      
    return shear;
  }
}
