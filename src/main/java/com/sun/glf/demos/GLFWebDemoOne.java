/*
 * @(#)GLFWebDemoOne.java
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
import java.awt.font.*;
import java.awt.image.*;
import java.util.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Illustrates the use of the GradientPaint Paint implementation to create
 * movement. This example uses GradientPaints for multiple purposes:<p>
 * a. Wave movements. Wave Shapes are filled with a GradientPaint.<p>
 * b. Subtle background. The background is made with a GradientPaint.<p>
 * c. Fading Shadow. The Text Shadow is created by using a GradientPaint to 
 *    transparent color.<p>
 * <p>
 * The Composition is organized in two halves. The top one is filled with a 
 * vertical GradientPaint and contains a text (topText) which has a drop shadow.
 * The bottom half is filled with Waves each filled with a GradientPaint. A text
 * is also displayed in the bottom half (bottomText) and appears 'behind' one
 * of the waves, determined by the textWaveIndex value.
 * 
 * @author          Vincent Hardy
 * @version         1.0, 9/15/1998
 */
public class GLFWebDemoOne implements CompositionFactory{
  /** Composition size */
  Dimension size = new Dimension(1024, 768);

  /** The number of wavelets */
  int numberOfWavelets = 5;

  /** The number of waves, vertically */
  int numberOfWaves = 4;

  /** Offset for the top text depth layer */
  int topTextDepth = 5;

  /** Amount by which waves overlap */
  float waveOverlapRatio = 1.f/3f;

  /*
   * Wave Gradient Colors
   */
  Color waveTopColor = new Color(76, 163, 232); 
  Color waveBottomColor = new Color(0, 135, 64); 

  /*
   * Background Gradient Colors
   */
  Color backgroundTopColor = new Color(236, 182, 0);
  Color backgroundBottomColor = new Color(224, 204, 149);

  /*
   * Top Text Shadow Gradient Colors
   */
  Color topTextShadowTopColor =  new Color(236, 133, 0, 0);
  Color topTextShadowBottomColor = new Color(236, 133, 0);

  /** Index of the wave under which the text is inserted */
  int textWaveIndex = 2;

  /** Wave Stroke Width */
  float waveStrokeWidth = 5.f;

  /** Texts to be displayed in the sun rise */
  String topText = "Hot &";
  String bottomText = "Cool";

  /** Fonts used to render text */
  Font textFont = new Font("serif", Font.PLAIN, 300);

  /** Text Color */
  Color textColor = Color.black;

  /** Blur width for the top text shadow */
  int shadowBlurRadius = 12;

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public int getNumberOfWavelets(){
    return numberOfWavelets;
  }

  public void setNumberOfWavelets(int numberOfWavelets){
    this.numberOfWavelets = numberOfWavelets;
  }

  public int getNumberOfWaves(){
    return numberOfWaves;
  }

  public void setNumberOfWaves(int numberOfWaves){
    this.numberOfWaves = numberOfWaves;
  }

  public int getTopTextDepth(){
    return topTextDepth;
  }

  public void setTopTextDepth(int topTextDepth){
    this.topTextDepth = topTextDepth;
  }

  public float getWaveOverlapRatio(){
    return waveOverlapRatio;
  }

  public void setWaveOverlapRatio(float waveOverlapRatio){
    this.waveOverlapRatio = waveOverlapRatio;
  }

  public Color getWaveTopColor(){
    return waveTopColor;
  }

  public void setWaveTopColor(Color waveTopColor){
    this.waveTopColor = waveTopColor;
  }

  public Color getWaveBottomColor(){
    return waveBottomColor;
  }

  public void setWaveBottomColor(Color waveBottomColor){
    this.waveBottomColor = waveBottomColor;
  }

  public Color getBackgroundTopColor(){
    return backgroundTopColor;
  }

  public void setBackgroundTopColor(Color backgroundTopColor){
    this.backgroundTopColor = backgroundTopColor;
  }

  public Color getBackgroundBottomColor(){
    return backgroundBottomColor;
  }

  public void setBackgroundBottomColor(Color backgroundBottomColor){
    this.backgroundBottomColor = backgroundBottomColor;
  }

  public Color getTopTextShadowTopColor(){
    return topTextShadowTopColor;
  }

  public void setTopTextShadowTopColor(Color topTextShadowTopColor){
    this.topTextShadowTopColor = topTextShadowTopColor;
  }

  public Color getTopTextShadowBottomColor(){
    return topTextShadowBottomColor;
  }

  public void setTopTextShadowBottomColor(Color topTextShadowBottomColor){
    this.topTextShadowBottomColor = topTextShadowBottomColor;
  }

  public int getTextWaveIndex(){
    return textWaveIndex;
  }

  public void setTextWaveIndex(int textWaveIndex){
    this.textWaveIndex = textWaveIndex;
  }

  public float getWaveStrokeWidth(){
    return waveStrokeWidth;
  }

  public void setWaveStrokeWidth(float waveStrokeWidth){
    this.waveStrokeWidth = waveStrokeWidth;
  }

  public String getTopText(){
    return topText;
  }

  public void setTopText(String topText){
    this.topText = topText;
  }

  public String getBottomText(){
    return bottomText;
  }

  public void setBottomText(String bottomText){
    this.bottomText = bottomText;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  /**
   * Utility member : creates the wave Shape.
   * The wave is created by concatenating several wavelet (numberOfWavelets) shapes.
   * The base wavelets are scaled to provide the appropriate wave height needed
   * so that numberOfWaves cover the bottom half of the Composition.
   * 
   * 
   */
  private Shape createWave(){
    // The base wavelet shape is based on Quadratic Bezier curves.
    // It height is 4.
    GeneralPath waveletBase = new GeneralPath();
    waveletBase.moveTo(0, 0);
    waveletBase.quadTo(2, 0, 3, 2);
    waveletBase.quadTo(4, 4, 6, 4);
    waveletBase.quadTo(8, 4, 9, 2);
    waveletBase.quadTo(10, 0, 12, 0);
    
    if(numberOfWaves<1)
      throw new IllegalArgumentException();

    // The height of a wavelet is the third of that of a wave. Before scaling,
    // A wave's height would be 12, because a wavelet is 4 points hight.
    // The wave is scaled to fit the bottom half.
    float waveHeight = size.height/(float)((1-waveOverlapRatio)*2*numberOfWaves);
    float waveletHeight = waveHeight/3.f;
    AffineTransform scale = AffineTransform.getScaleInstance(1.f, waveletHeight/4f);

    // Scale wavelet to the appropriate height
    Shape wavelet = scale.createTransformedShape(waveletBase);

    Rectangle waveletBounds = wavelet.getBounds();
    GeneralPath wave = new GeneralPath();
    AffineTransform t = new AffineTransform();
    for(int i=1; i<=numberOfWavelets; i++){
      wave.append(t.createTransformedShape(wavelet), false);
      t.translate(waveletBounds.width, 0);
    }

    wave.lineTo(waveletBounds.width*numberOfWavelets, waveHeight);
    wave.lineTo(0, waveHeight);
    wave.lineTo(0, 0);

    // Now, scale wave so that it fits into the width
    // We also translate it so that the left and right sides
    // do not show on the window.
    Rectangle waveBounds = wave.getBounds();
    waveBounds.width -= 2*waveStrokeWidth; 
    float xScale = ((float)size.width)/(float)waveBounds.width;
    t.setToTranslation(-waveStrokeWidth, 0);
    t.scale(xScale, 1.f);
    return t.createTransformedShape(wave);
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
    Vector layers = new Vector();

    //
    // Background Layer : rectangle covering the top half of the Composition
    // and filled with a vertical GradientPaint.
    //
    Rectangle topHalf = new Rectangle(0, 0, size.width, size.height/2);
    GradientPaint topGradient = new GradientPaint(0, 0, backgroundTopColor, 0, topHalf.height, backgroundBottomColor);
    Layer topBackground = new ShapeLayer(cmp, topHalf, new FillRenderer(topGradient));
    layers.addElement(topBackground);

    //
    // Top Text Layer : text, depth addition and shadow
    //

    // Create TextBlock
    Shape topTextBlock = TextLayer.makeTextBlock(topText, textFont, size.width, TextAlignment.CENTER); 
    
    // Center in top half
    topTextBlock = Position.CENTER.createTransformedShape(topTextBlock, topHalf);

    // Create ShapeLayer for top text
    ShapeLayer topTextLayer = new ShapeLayer(cmp, topTextBlock, new FillRenderer(textColor));

    // Create a second ShapeLayer for top text, slightly offset to add depth
    ShapeLayer topTextLayerDepth = new ShapeLayer(cmp, topTextBlock, new FillRenderer(topTextShadowBottomColor));
    topTextLayerDepth.setTransform( AffineTransform.getTranslateInstance(topTextDepth, 0));

    // Now, create the drop shadow shape : shear and scale the text block
    Rectangle textBounds = topTextBlock.getBounds();
    AffineTransform topTextShadowTransform = new AffineTransform();
    topTextShadowTransform.translate(textBounds.x, textBounds.y+textBounds.height);
    topTextShadowTransform.scale(1f, 0.5f);
    topTextShadowTransform.shear(-0.5, 0);
    topTextShadowTransform.translate(-textBounds.x, -textBounds.y-textBounds.height);

    Shape topTextShadowShape = topTextShadowTransform.createTransformedShape(topTextBlock);

    // Create drop shadow GradientPaint
    Rectangle shadowBounds = topTextShadowShape.getBounds();
    GradientPaint topTextShadowPaint = new GradientPaint(0, shadowBounds.y + shadowBounds.height, topTextShadowBottomColor,
							 0, shadowBounds.y, topTextShadowTopColor);

    // Shadow layer
    ShapeLayer topTextShadowLayer = new ShapeLayer(cmp, topTextShadowShape, new FillRenderer(topTextShadowPaint));
    ConvolveOp shadowBlur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
    topTextShadowLayer.setImageFilter(shadowBlur, new Dimension(2*shadowBlurRadius, 2*shadowBlurRadius));

    layers.addElement(topTextShadowLayer);
    layers.addElement(topTextLayerDepth);
    layers.addElement(topTextLayer);

    //
    // Waves before text
    //
    Shape wave = createWave();
    Rectangle waveBounds = wave.getBounds();
    AffineTransform t = new AffineTransform();
    t.translate(0, -waveBounds.y - waveBounds.height/3 + size.height/2);
    GradientPaint wavePaint = new GradientPaint(0, waveBounds.y, waveTopColor,
						0, waveBounds.y+waveBounds.height, waveBottomColor);
    FillRenderer waveFill = new FillRenderer(wavePaint);
    StrokeRenderer waveStroke = new StrokeRenderer(waveTopColor, new BasicStroke(waveStrokeWidth));
    Renderer waveRenderer = new CompositeRenderer(new Renderer[]{waveFill, waveStroke});
    for(int i=0; i<textWaveIndex; i++){
      ShapeLayer waveLayer = new ShapeLayer(cmp, wave, waveRenderer);
      waveLayer.setTransform((AffineTransform)t.clone());
      layers.addElement(waveLayer);
      t.translate(0, 2*waveBounds.height/3);
    }

    //
    // Bottom text
    //
    Rectangle bottomHalf = new Rectangle(0, size.height/2, size.width, size.height/2);

    // Create TextBlock
    Shape bottomTextBlock = TextLayer.makeTextBlock(bottomText, textFont, size.width, TextAlignment.CENTER); 
    
    // Center in bottom half
    bottomTextBlock = Position.CENTER.createTransformedShape(bottomTextBlock, bottomHalf);

    // Create ShapeLayer for bottom text
    ShapeLayer bottomTextLayer = new ShapeLayer(cmp, bottomTextBlock, new FillRenderer(textColor));

    // Add depth
    ShapeLayer bottomTextLayerDepth = new ShapeLayer(cmp, bottomTextBlock, new FillRenderer(waveTopColor));
    bottomTextLayerDepth.setTransform(AffineTransform.getTranslateInstance(topTextDepth, 0));

    layers.addElement(bottomTextLayerDepth);
    layers.addElement(bottomTextLayer);

    //
    // Waves after text
    //
    for(int i=textWaveIndex; i<numberOfWaves; i++){
      ShapeLayer waveLayer = new ShapeLayer(cmp, wave, waveRenderer);
      waveLayer.setTransform((AffineTransform)t.clone());
      layers.addElement(waveLayer);
      t.translate(0, 2*waveBounds.height/3);
    }

    //
    // Final Layer : Redraw bottom text with transparency to give the impression
    // it is 'under' the bottom waves
    //
    ShapeLayer bottomTextLayerTransparent = new ShapeLayer(cmp, bottomTextBlock, new FillRenderer(textColor));
    bottomTextLayerTransparent.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
    layers.addElement(bottomTextLayerTransparent);

    Layer layerStack[] = new Layer[layers.size()];
    layers.copyInto(layerStack);
    cmp.setLayers(layerStack);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    return cmp;
  }
}
