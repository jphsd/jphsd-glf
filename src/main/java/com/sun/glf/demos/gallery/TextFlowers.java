/*
 * @(#)TextFlower.java
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

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * Using AffineTransforms and color brightness variations, this example shows
 * how to create a flower-like effect with a text.
 *
 * @author         Vincent Hardy
 * @version        1.0, 11/19/1998
 */
public class TextFlowers implements CompositionFactory{
  Dimension margins = new Dimension(30, 30);

  String text = "GLF";

  Font textFont = new Font("serif", Font.PLAIN, 80);

  Color backgroundColor = Color.black;

  int numberOfShapes = 30;

  int numberOfRotations = 1;

  Color baseColor = new Color(100, 100, 0);

  float brightnessStart = .2f;

  float brightnessEnd = .8f;

  int numberOfCycles = 1;

  float strokeWidth = 4;

  Color strokeColor = Color.black;

  Color fillColor = new Color(200, 200, 100);

  public Color getFillColor(){
    return fillColor;
  }

  public void setFillColor(Color fillColor){
    this.fillColor = fillColor;
  }

  public float getStrokeWidth(){
    return strokeWidth;
  }

  public void setStrokeWidth(float strokeWidth){
    this.strokeWidth = strokeWidth;
  }

  public Color getStrokeColor(){
    return strokeColor;
  }

  public void setStrokeColor(Color strokeColor){
    this.strokeColor = strokeColor;
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

  public int getNumberOfCycles(){
    return numberOfCycles;
  }

  public void setNumberOfCycles(int numberOfCycles){
    this.numberOfCycles = numberOfCycles;
  }

  public float getBrightnessStart(){
    return brightnessStart;
  }

  public void setBrightnessStart(float brightnessStart){
    this.brightnessStart = brightnessStart;
  }

  public float getBrightnessEnd(){
    return brightnessEnd;
  }

  public void setBrightnessEnd(float brightnessEnd){
    this.brightnessEnd = brightnessEnd;
  }

  public Dimension getMargins(){
    return margins;
  }

  public void setMargins(Dimension margins){
    this.margins = margins;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getNumberOfShapes(){
    return numberOfShapes;
  }

  public void setNumberOfShapes(int numberOfShapes){
    this.numberOfShapes = numberOfShapes;
  }

  public int getNumberOfRotations(){
    return numberOfRotations;
  }

  public void setNumberOfRotations(int numberOfRotations){
    this.numberOfRotations = numberOfRotations;
  }

  public Color getBaseColor(){
    return baseColor;
  }

  public void setBaseColor(Color baseColor){
    this.baseColor = baseColor;
  }

  public Composition build(){

    Shape shape = TextLayer.makeTextBlock(text, textFont);

    //
    // Position Shape so that its bottom left corner is at the 
    // center of the composition. Compute size based on the text shape's bounds
    //
    Rectangle shapeBounds = shape.getBounds();
    int cmpWidth = shapeBounds.width>shapeBounds.height?shapeBounds.width:shapeBounds.height;
    Dimension size = new Dimension(2*cmpWidth + 2*margins.width, 
				   2*cmpWidth + 2*margins.height);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    AffineTransform t = new AffineTransform();
    t.translate(shapeBounds.width/2f, -shapeBounds.height/2f);
    Position shapePosition = new Position(Anchor.CENTER, 0, 0, t);

    shape = shapePosition.createTransformedShape(shape, cmp.getBounds());
    
    float angleTotal = 2*numberOfRotations*(float)Math.PI;
    float angleStep = angleTotal/numberOfShapes;

    // Get color HSB values
    float hsb[] = Color.RGBtoHSB(baseColor.getRed(),
				 baseColor.getGreen(),
				 baseColor.getBlue(),
				 null);
    float deltaBrightness = (brightnessEnd-brightnessStart)/(numberOfShapes/(float)numberOfCycles);

    int rcx = size.width/2;
    int rcy = size.height/2;
    float b = 0;

    Layer layers[] = new Layer[numberOfShapes + 2];
    AffineTransform rotate = new AffineTransform();
    int i = 0;
    for(i=0; i<numberOfShapes; i++){
      b = brightnessStart + (i*deltaBrightness)%(brightnessEnd-brightnessStart);
      Color color = Color.getHSBColor(hsb[0], hsb[1], b);
      rotate.rotate(angleStep, rcx, rcy);
      layers[i] = new ShapeLayer(cmp, shape, new FillRenderer(color));
      layers[i].setTransform((AffineTransform)rotate.clone());
    }


    //
    // Add two layers to make text stand out
    //
    FillRenderer textFill = new FillRenderer(fillColor);
    FillRenderer textStroke = new FillRenderer(strokeColor);

    ShapeLayer textStrokeLayer = new ShapeLayer(cmp, shape, textStroke);
    textStrokeLayer.setTransform(AffineTransform.getTranslateInstance(strokeWidth, strokeWidth));
    ShapeLayer textFillLayer = new ShapeLayer(cmp, shape, textFill);

    layers[i++] = textStrokeLayer;
    layers[i++] = textFillLayer;
      
    cmp.setLayers(layers);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
    
  }
}
