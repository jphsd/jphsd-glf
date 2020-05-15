/*
 * @(#)ShapeSplatter.java
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

interface PaintGenerator {
  /** @return Paint for a given index */
  public Paint getPaint(int i);

  /** @return the next Paint */
  public Paint next();

  /** @return whether or not there are more Paints */
  public boolean hasNext();

  /** Resets the generator so that it starts from the first Paint again */
  public void reset();

  /** @return total number of Paints generated */
  public int getPaintCount();
}

abstract class AbstractPaintGenerator implements PaintGenerator {
  /** Total number of Paints generated */
  int nPaints;

  /** Index of the current Paint */
  int curPaint = -1;

  /** @return whether or not there are more Paints */
  public boolean hasNext(){
    return curPaint < nPaints;
  }

  /** @return the next Paint */
  public Paint next(){
    curPaint++;
    curPaint %= nPaints;

    return getPaint(curPaint);
  }

  /** Resets the generator so that it starts from the first Paint again */
  public void reset(){
    curPaint = -1;
  }
  
  /** @return total number of Paints generated */
  public int getPaintCount(){
    return nPaints;
  }
}

class VariableBrightnessColorGenerator extends AbstractPaintGenerator{
  /** Base color hue, saturation, and starting brightness*/
  float h, s, b;

  /** Delta brightness between two consecutive colors*/
  float deltaB;

  /**
   * @param color base color whose hue and saturation are used for all the generated colors.
   * @param steps number of steps from bs to be
   * @param be end brightness value for the generated colors.
   */
  public VariableBrightnessColorGenerator(Color color, int steps, float be){
    if(steps <= 0 || be<0 || be>1)
      throw new IllegalArgumentException();

    nPaints = steps;
    float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    h = hsb[0];
    s = hsb[1];
    b = hsb[2];
    deltaB = (be-b)/steps;
  }

  public Paint getPaint(int i){
    i = i%nPaints;
    Color col = Color.getHSBColor(h, s, b + i*deltaB);
    if(i==0)
      System.out.println("hsv = " + h + "." + s + "." + b + " rgb = " + Integer.toHexString(col.getRGB()));

    return col;
  }
}

class VariableHueColorGenerator  extends AbstractPaintGenerator{
  /** Base color saturation, brightness and starting hue*/
  float s, b, h;

  /** Delta hue between two consecutive colors*/
  float deltaH;

  /**
   * @param color base color whose brightness and saturation are used for all the generated colors.
   * @param steps number of steps from bs to be
   * @param he end he value for the generated colors.
   */
  public VariableHueColorGenerator(Color color, int steps, float he){
    if(steps <= 0 || he<0 || he>360)
      throw new IllegalArgumentException();

    nPaints = steps;
    float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    s = hsb[1];
    b = hsb[2];
    h = hsb[0];
    deltaH= (he/360-h)/steps;
  }

  public Paint getPaint(int i){
    i = i%nPaints;
    return Color.getHSBColor(h + i*deltaH, s, b);
  }
}

class VariableSaturationColorGenerator extends AbstractPaintGenerator {
  /** Base color hue, brightness and starting saturation*/
  float h, b, s;

  /** Delta hue between two consecutive colors*/
  float deltaS;

  /**
   * @param color base color whose brightness and hue are used for all the generated colors.
   * @param steps number of steps from bs to be
   * @param se end saturation value for the generated colors.
   */
  public VariableSaturationColorGenerator(Color color, int steps, float se){
    if(steps <= 0 || se<0 || se>1)
      throw new IllegalArgumentException();

    nPaints = steps;
    float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    h = hsb[0];
    s = hsb[1];
    b = hsb[2];
    deltaS= (se-s)/steps;
  }

  public Paint getPaint(int i){
    i = i%nPaints;
    return Color.getHSBColor(h, s + deltaS*i, b);
  }
}

class VariableAlphaColorGenerator extends AbstractPaintGenerator {
  static final String ALPHA_OUT_OF_RANGE = "Alpha should be 0-255. Input value is out of range : ";

  /** Base color Red, Green and blue value */
  float r, g, b;

  /** Starting alpha */
  float a;

  /** Delta alpha */
  float deltaAlpha;

  /**
   * @param color base color whose rgb components are used for all
   *        generated colors. The color's alpha value is the starting
   *        value
   * @param endAlpha alpha value the last color should have
   * @param steps number of steps from initial color to last color
   */
  public VariableAlphaColorGenerator(Color color, float endAlpha, int steps){
    if(endAlpha<0 || endAlpha>255)
      throw new IllegalArgumentException(ALPHA_OUT_OF_RANGE + endAlpha);

    this.nPaints = steps;
    this.a = color.getAlpha()/255f;
    this.deltaAlpha = (endAlpha/255f - this.a)/steps;
    this.r = color.getRed()/255f;
    this.g = color.getGreen()/255f;
    this.b = color.getBlue()/255f;
  }

  public Paint getPaint(int i){
    i = i%nPaints;
    return new Color(r, g, b, a + i*deltaAlpha);
  }
}

interface TransformGenerator {
  /** @return transform at index */
  public AffineTransform getTransform(int i);

  /** @return the next transform */
  public AffineTransform next();

  /** @return whether or not there are more transforms */
  public boolean hasNext();

  /** Resets the generator so that it starts from the first transform again */
  public void reset();

  /** @return total number of transforms generated */
  public int getTransformCount();
}

/**
 * Abstract implementation of the transform generator interface.
 */
abstract class AbstractTransformGenerator implements TransformGenerator {
  /** Total number of transforms generated */
  int nTransforms;

  /** Index of the current transform */
  int curTransform = -1;

  /** @return whether or not there are more transforms */
  public boolean hasNext(){
    return curTransform < nTransforms;
  }

  /** @return the next transform */
  public AffineTransform next(){
    curTransform++;
    curTransform %= nTransforms;

    return getTransform(curTransform);
  }

  /** Resets the generator so that it starts from the first transform again */
  public void reset(){
    curTransform = -1;
  }
  
  /** @return total number of transforms generated */
  public int getTransformCount(){
    return nTransforms;
  }
}

class TranslationGenerator extends AbstractTransformGenerator {
  int x, y, w, h;
  public TranslationGenerator(int x, int y, int w, int h, int steps){
    nTransforms = steps;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public AffineTransform getTransform(int i){
    return AffineTransform.getTranslateInstance(x + i*w/(nTransforms-1), y + i*h/(nTransforms-1));
  }
}

class ScaleGenerator extends AbstractTransformGenerator {
  static final String ILLEGAL_STEPS_VALUE = "Steps should be at least 2. Illegal value : ";
  double startScale;
  double deltaScale;
  double tx, ty;
  /**
   * @param startScale scale value for the first generated scale
   * @param endScale scale value the the last generated scale
   * @param x scale about (x, y)
   * @param x scale about (x, y)
   */
  public ScaleGenerator(double startScale, double endScale, double x, double y, int steps){
    if(steps<2)
      throw new IllegalArgumentException(ILLEGAL_STEPS_VALUE + steps);

    nTransforms = steps;
    this.tx = x;
    this.ty = y;
    this.startScale = startScale;
    this.deltaScale = (endScale - startScale)/(steps-1);
  }

  public AffineTransform getTransform(int i){
    i = i%nTransforms;
    double scale = startScale + i*deltaScale;
    AffineTransform at = new AffineTransform();
    at.translate(tx, ty);
    at.scale(scale, scale);
    at.translate(-tx, -ty);

    return at;
  }
}

class RandomPlacementGenerator extends AbstractTransformGenerator {
  Rectangle bounds;
  double minScale, maxScale, txMax, tyMax;
  AffineTransform defaultTransform;
  int x, y;

  /**
   * @param shape shape to be randomly transformed within rect
   * @param rect rectangle within which the shape should be contained
   * @param minScale minimum value for the random scale factor
   * @param maxScale maximum value for the random scale factor
   */
  public RandomPlacementGenerator(Shape shape, Rectangle rect, double minScale, double maxScale){
    nTransforms = Integer.MAX_VALUE;

    // Create a default transform that centers the shape about (0, 0);
    this.bounds = shape.getBounds();
    this.defaultTransform = new AffineTransform();
    this.defaultTransform.translate(-bounds.x - bounds.width/2,
                                    -bounds.y - bounds.height/2);
    
    
    this.x = rect.x;
    this.y = rect.y;

    this.txMax = rect.width;
    this.tyMax = rect.height;
    
    this.minScale = minScale;
    this.maxScale = maxScale;
    
  }
				  

  public AffineTransform getTransform(int i){
    // First, process random rotate factor
    double theta = 2*Math.random()*Math.PI;

    // Now, process random scale factor (between 1 and 1.5)
    double scale = minScale + (maxScale-minScale)*Math.random();

    // Now, process randow translation 
    double tx = txMax*Math.random();
    double ty = tyMax*Math.random();

    // Create transformation. Scale happens first, then
    // rotation and finally translation
    AffineTransform t = new AffineTransform();
    t.translate(x + tx, y + ty);   // d. Translate to final position
    t.rotate(theta);               // c. Rotate about origin
    t.scale(scale, scale);         // b. Scale about origin
    t.concatenate(defaultTransform); // a. Center about origin

    return t;
  }
}

class WaveGenerator extends AbstractTransformGenerator {
  int x, y, w, h;
  public WaveGenerator(int x, int y, int w, int h, int steps){
    nTransforms = steps;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public AffineTransform getTransform(int i){
    float tx = x + i*w/(nTransforms-1);
    float ty = y + (float)(h*(1 + Math.sin(Math.PI*tx/180.))/2);
    System.out.println(i + " tx / ty = " + tx + "/" + ty);
    return AffineTransform.getTranslateInstance(tx, ty);
  }
}

class RollingGenerator extends AbstractTransformGenerator{
  int x, y, w, nRotations;

  public RollingGenerator(int x, int y, int w, int nRotations, int steps){
    nTransforms = steps;
    this.x = x;
    this.y = y;
    this.w = w;
    this.nRotations = nRotations;
  }

  public AffineTransform getTransform(int i){
    AffineTransform t = AffineTransform.getTranslateInstance(x + i*w/(nTransforms-1), y);
    t.rotate(i*(Math.PI*2*nRotations/(nTransforms-1)));
    return t;
  }
}

class SpinningGenerator extends AbstractTransformGenerator{
  int x, y, nRotations;
  float startScale;

  public SpinningGenerator(int x, int y, int nRotations, float startScale, int steps){
    nTransforms = steps;
    this.nRotations = nRotations;
    this.startScale = startScale;
    this.x = x;
    this.y = y;
  }

  public AffineTransform getTransform(int i){
    AffineTransform t = AffineTransform.getTranslateInstance(x, y);
    float scale = startScale + i*(1-startScale)/(nTransforms-1);
    t.scale(scale, scale);
    t.rotate(i*(Math.PI*2*nRotations/(nTransforms-1)));
    return t;
  }
}

/**
 * This demonstrates how to create a texture pattern with Shapes by
 * repeatedly painting a Shape with different transforms.
 *
 * @author           Vincent Hardy
 * @version          1.0, 12/09/1998
 */
public class ShapeSplatter implements CompositionFactory {
  Dimension size = new Dimension(400, 400);

  Color backgroundColor = Color.black;

  int numberOfGlyphs = 200;

  int numberOfTexts = 200;

  Color glyphColor = new Color(1f, 1f, 1f, .5f);

  float glyphAlphaEndValue = .2f;

  String text = "Shape Splatter";

  Font textFont = new Font("serif", Font.PLAIN, 40);

  int margin = 10;

  Color textBaseColor = Color.yellow;

  float textHueEndValue = 180;

  Color textColor = Color.yellow;

  Glyph glyph = new Glyph(new Font("serif", Font.PLAIN, 100), '@');

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getNumberOfGlyphs(){
    return numberOfGlyphs;
  }

  public void setNumberOfGlyphs(int numberOfGlyphs){
    this.numberOfGlyphs = numberOfGlyphs;
  }

  public int getNumberOfTexts(){
    return numberOfTexts;
  }

  public void setNumberOfTexts(int numberOfTexts){
    this.numberOfTexts = numberOfTexts;
  }

  public Color getGlyphColor(){
    return glyphColor;
  }

  public void setGlyphColor(Color glyphColor){
    this.glyphColor = glyphColor;
  }

  public float getGlyphAlphaEndValue(){
    return glyphAlphaEndValue;
  }

  public void setGlyphAlphaEndValue(float glyphAlphaEndValue){
    this.glyphAlphaEndValue = glyphAlphaEndValue;
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

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public Color getTextBaseColor(){
    return textBaseColor;
  }

  public void setTextBaseColor(Color textBaseColor){
    this.textBaseColor = textBaseColor;
  }

  public float getTextHueEndValue(){
    return textHueEndValue;
  }

  public void setTextHueEndValue(float textHueEndValue){
    this.textHueEndValue = textHueEndValue;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
    Rectangle cmpRect = new Rectangle(0, 0, size.width, size.height);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // The composition is made of two sets of layers : a set of layers
    // which create a background texture and another set which creates a 
    // gradient text effect.
    ShapeLayer layers[] = new ShapeLayer[numberOfGlyphs + numberOfTexts];

    //
    // First, create a texturized background by stacking 
    // layers showing the same glyph shape filled with the 
    // same color, but with a variable opacity and a variable
    // transform.
    //

    // Get glyph shape a position it to the upper left corner of the 
    // composition area
    Shape glyphShape = glyph.getShape();
    glyphShape = Position.TOP_LEFT.createTransformedShape(glyphShape, cmpRect);
    
    // We use a random AffineTransform generator which will rotate, translate and scale the shape
    // *inside* the composition.
    // A VariableAlphaColorGenerator is used to fill in the shape.
    
    PaintGenerator glyphPaintGenerator = new VariableAlphaColorGenerator(glyphColor, glyphAlphaEndValue, numberOfGlyphs);
    TransformGenerator glyphTxformGenerator = new RandomPlacementGenerator(glyphShape, cmpRect, .5, 1.5);
    for(int i=0; i<numberOfGlyphs; i++){
      layers[i] = new ShapeLayer(cmp, glyphShape, new FillRenderer(glyphPaintGenerator.next()));
      layers[i].setTransform(glyphTxformGenerator.next());
    }

    //
    // Now, create the text effect by using a scale generator
    // The text itself is filled by Paints generated by a VariableHueColorGenerator
    //

    // Get text shape
    Shape textShape = TextLayer.makeTextBlock(text, 
					      textFont, 
					      -1, // No wrapping
					      TextAlignment.CENTER);
    
    // Position Shape at the top
    Position textPosition = new Position(Anchor.BOTTOM, 0, margin);
    textShape = textPosition.createTransformedShape(textShape, cmpRect);

    // Process initial scale so that the full composition is covered
    int textShapeWidth = textShape.getBounds().width;
    double initialScale = Math.max((size.width - 2*margin)/(float)textShapeWidth, 0);

    PaintGenerator textPaintGenerator = new VariableHueColorGenerator(textBaseColor, numberOfTexts, textHueEndValue);
    // PaintGenerator textPaintGenerator = new VariableBrightnessColorGenerator(textBaseColor, numberOfTexts, textHueEndValue);
    TransformGenerator textTxformGenerator = new ScaleGenerator(initialScale, 1, size.width/2, size.height, numberOfTexts);
    
    for(int i=numberOfGlyphs; i<numberOfGlyphs + numberOfTexts -1; i++){
      layers[i] = new ShapeLayer(cmp, textShape, new FillRenderer(textPaintGenerator.next()));
      layers[i].setTransform(textTxformGenerator.next());
    }
    
    layers[numberOfGlyphs + numberOfTexts -1] = new ShapeLayer(cmp, textShape, new FillRenderer(textColor));
    layers[numberOfGlyphs + numberOfTexts -1].setTransform(textTxformGenerator.next());

    // Set rendering quality
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    cmp.setLayers(layers);
    return cmp;
  }
}

