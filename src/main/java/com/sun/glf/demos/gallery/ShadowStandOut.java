/*
 * @(#)ShadowStandOut.java
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
 * Illustrates the use of shadows to make Shapes stand out
 * in front of a background. The shadows are created with
 * ConvolveOp.
 *
 * @author           Vincent Hardy
 * @version          1.0, 10/01/1998
 */
public class ShadowStandOut implements CompositionFactory {
  
  /** Text to be displayed */
  String text = "Shadow Stand Out";

  /** Text shadow color */
  Color shadowColor = new Color(0, 0, 0, 205);

  /** Text Font */
  Font font = new Font("Lucida Bright Demibold Italic", Font.BOLD | Font.ITALIC, 70);

  /** Blur extent */
  int shadowBlurRadius = 5;

  /** Offset applied to thin shadow, in both directions */
  int thinShadowOffset = -1;

  File imageFile = new File("");

  Anchor textAnchor = Anchor.RIGHT;

  Dimension textAdjustment = new Dimension(30, 30);

  float textRotate = 180f;

  Glyph glyph = new Glyph(new Font("serif", Font.PLAIN, 200), '&');;

  Anchor glyphAnchor = Anchor.TOP_LEFT;

  Dimension glyphAdjustment = new Dimension(40, 40);

  float brightnessScale = 2f;

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Anchor getTextAnchor(){
    return textAnchor;
  }

  public void setTextAnchor(Anchor textAnchor){
    this.textAnchor = textAnchor;
  }

  public Dimension getTextAdjustment(){
    return textAdjustment;
  }

  public void setTextAdjustment(Dimension textAdjustment){
    this.textAdjustment = textAdjustment;
  }

  public float getTextRotate(){
    return textRotate;
  }

  public void setTextRotate(float textRotate){
    this.textRotate = textRotate;
  }

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

  public Anchor getGlyphAnchor(){
    return glyphAnchor;
  }

  public void setGlyphAnchor(Anchor glyphAnchor){
    this.glyphAnchor = glyphAnchor;
  }

  public Dimension getGlyphAdjustment(){
    return glyphAdjustment;
  }

  public void setGlyphAdjustment(Dimension glyphAdjustment){
    this.glyphAdjustment = glyphAdjustment;
  }

  public float getBrightnessScale(){
    return brightnessScale;
  }

  public void setBrightnessScale(float brightnessScale){
    this.brightnessScale = brightnessScale;
  }

  public int getThinShadowOffset(){
    return thinShadowOffset;
  }

  public void setThinShadowOffset(int thinShadowOffset){
    this.thinShadowOffset = thinShadowOffset;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public Font getFont(){
    return font;
  }

  public void setFont(Font font){
    this.font = font;
  }

  public Composition build(){
    //
    // First, load background image
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load " + imageFile);

    //
    // Build a composition the size of the image
    //
    int iw = image.getWidth(), ih = image.getHeight();
    Dimension size = new Dimension(iw, ih);
    LayerComposition cmp = new LayerComposition(size);

    TexturePaint texturePaint = new TexturePaint(image, new Rectangle(0, 0, iw, ih));
    FillRenderer texturePainter = new FillRenderer(texturePaint);
    cmp.setBackgroundPaint(texturePaint);

    //
    // shadowPainter is used both for the text and the glyph
    // blur is used for both as well
    //
    FillRenderer shadowPainter = new FillRenderer(shadowColor);

    float blurScale = 3f/shadowBlurRadius;
    GaussianKernel kernel = new GaussianKernel(3);
    
    AffineTransform shrinkTxf = AffineTransform.getScaleInstance(blurScale, blurScale);
    AffineTransform blowUpTxf = AffineTransform.getScaleInstance(1/blurScale, 1/blurScale);
    
    AffineTransformOp shrinkOp = new AffineTransformOp(shrinkTxf, null);
    AffineTransformOp blowUpOp = new AffineTransformOp(blowUpTxf, AffineTransformOp.TYPE_BILINEAR);
    ConvolveOp smallBlurOp = new ConvolveOp(kernel);

    CompositeOp blur = new CompositeOp(new BufferedImageOp[]{shrinkOp, smallBlurOp, blowUpOp});

    //
    // Create a shape from the text parameters
    //
    Shape textShape = TextLayer.makeTextBlock(text, font);

    AffineTransform thinShadowTxf = AffineTransform.getTranslateInstance(thinShadowOffset, thinShadowOffset);
    AffineTransform textRotator = AffineTransform.getRotateInstance(textRotate*Math.PI/180f);
    Position textPosition = new Position(textAnchor, textAdjustment.width, textAdjustment.height, textRotator, false);

    textShape = textPosition.createTransformedShape(textShape, cmp.getBounds());
    ShapeLayer textLayer = new ShapeLayer(cmp, textShape, shadowPainter);
    textLayer.setTransform(thinShadowTxf);
    Dimension blurMargins = new Dimension(shadowBlurRadius*2, shadowBlurRadius*2);
    textLayer.setImageFilter(blur, blurMargins);

    //
    // Create a shape from the glyph parameter.
    //
    Shape symbol = glyph.getShape();

    Position glyphPosition = new Position(glyphAnchor, glyphAdjustment.width, glyphAdjustment.height);
    symbol = glyphPosition.createTransformedShape(symbol, cmp.getBounds());

    ShapeLayer glyphLayer = new ShapeLayer(cmp, symbol, shadowPainter);
    glyphLayer.setTransform(thinShadowTxf);
    glyphLayer.setImageFilter(blur, blurMargins);

    //
    // Now, create adjustment for the text and glyph
    //
    RescaleOp brightener 
      = new RescaleOp(new float[]{brightnessScale, brightnessScale, brightnessScale, 1},
      new float[]{0, 0, 0, 0}, null);

    ShapeLayer textBrightenerLayer = new ShapeLayer(cmp, textShape.getBounds(), texturePainter);
    textBrightenerLayer.setImageFilter(brightener);
    textBrightenerLayer.setLayerMask(textShape);

    byte lut[] = new byte[256];
    for(int i=0; i<255; i++)
      lut[i] = (byte)(255-i);

    ByteLookupTable blut = new ByteLookupTable(0, lut);
    LookupOp inverter = new LookupOp(blut, null);

    ShapeLayer glyphBrightenerLayer = new ShapeLayer(cmp, symbol.getBounds(), texturePainter);
    glyphBrightenerLayer.setImageFilter(inverter);
    glyphBrightenerLayer.setLayerMask(symbol);

    

    cmp.setLayers(new Layer[]{ textLayer, glyphLayer, textBrightenerLayer, glyphBrightenerLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }
}
