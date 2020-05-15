/*
 * @(#)CombiningLayers.java
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
package com.sun.glf.snippets;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import com.sun.glf.util.*;
import com.sun.glf.goodies.*;
import com.sun.glf.*;

/**
 * Illustrates how to combine the different types of Layers in a 
 * LayerComposition.
 *
 * @author           Vincent Hardy
 * @version          1.0, 03/30/1999
 */
public class CombiningLayers implements CompositionFactory{
  File backgroundImageFile = new File("");

  File imageFile = new File("");

  File imageMaskFile = new File("");

  float imageShadowMargin = 200;

  float textMargins = 20;

  Color textOutlineColor = Color.white;

  Color textFillColor = Color.black;

  float textOutlineWidth = 3;

  String text = "Layers";

  Font textFont = new Font("Impact", Font.BOLD, 50);

  int shadowRadius = 5;

  Color shadowColor = Color.black;

  float shadowOffsetX = 10;

  float shadowOffsetY = 10;

  float imageMargin = 50;

  public float getImageShadowMargin(){
    return imageShadowMargin;
  }

  public void setImageShadowMargin(float imageShadowMargin){
    this.imageShadowMargin = imageShadowMargin;
  }

  public float getTextOutlineWidth(){
    return textOutlineWidth;
  }

  public void setTextOutlineWidth(float textOutlineWidth){
    this.textOutlineWidth = textOutlineWidth;
  }

  public File getBackgroundImageFile(){
    return backgroundImageFile;
  }

  public void setBackgroundImageFile(File backgroundImageFile){
    this.backgroundImageFile = backgroundImageFile;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public File getImageMaskFile(){
    return imageMaskFile;
  }

  public void setImageMaskFile(File imageMaskFile){
    this.imageMaskFile = imageMaskFile;
  }

  public float getTextMargins(){
    return textMargins;
  }

  public void setTextMargins(float textMargins){
    this.textMargins = textMargins;
  }

  public Color getTextOutlineColor(){
    return textOutlineColor;
  }

  public void setTextOutlineColor(Color textOutlineColor){
    this.textOutlineColor = textOutlineColor;
  }

  public Color getTextFillColor(){
    return textFillColor;
  }

  public void setTextFillColor(Color textFillColor){
    this.textFillColor = textFillColor;
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

  public int getShadowRadius(){
    return shadowRadius;
  }

  public void setShadowRadius(int shadowRadius){
    this.shadowRadius = shadowRadius;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public float getShadowOffsetX(){
    return shadowOffsetX;
  }

  public void setShadowOffsetX(float shadowOffsetX){
    this.shadowOffsetX = shadowOffsetX;
  }

  public float getShadowOffsetY(){
    return shadowOffsetY;
  }

  public void setShadowOffsetY(float shadowOffsetY){
    this.shadowOffsetY = shadowOffsetY;
  }

  public float getImageMargin(){
    return imageMargin;
  }

  public void setImageMargin(float imageMargin){
    this.imageMargin = imageMargin;
  }

  public Composition build(){
    // First, load the images used in the Composition
    BufferedImage background = Toolbox.loadImage(backgroundImageFile, BufferedImage.TYPE_INT_ARGB);
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB);
    BufferedImage imageMask = Toolbox.loadImage(imageMaskFile, BufferedImage.TYPE_INT_ARGB);

    // If one the images could no be loaded, we cannot proceed
    if(background==null ||
       image==null ||
       imageMask==null)
      throw new IllegalArgumentException("Could not load one of the images");

    // 
    // Composition size is based on background image size
    //
    int width = background.getWidth();
    int height = background.getHeight();
    Dimension size = new Dimension(width, height);
    LayerComposition cmp = new LayerComposition(size);

    // Background layer
    ImageLayer bkgLayer = new ImageLayer(cmp, background);

    // Vertical TextLayer
    AffineTransform textRotation = new AffineTransform();
    textRotation.rotate(-Math.PI/2);
    Position textPosition = new Position(Anchor.LEFT,
					 textMargins, textMargins, 
					 textRotation, 
					 false);                    // Position to the left after applying transform

    Renderer textOutline = new StrokeRenderer(textOutlineColor, textOutlineWidth);
    Renderer textFill = new FillRenderer(textFillColor);
    Renderer textPainter = new CompositeRenderer(textOutline, textFill);
    TextLayer textLayer = new TextLayer(cmp, text, textFont, textPainter, textPosition, -1, TextAlignment.CENTER);
    
    // TextLayer shadow
    ConvolveOp shadowBlur = new ConvolveOp(new GaussianKernel(shadowRadius));
    Dimension shadowBlurMargins = new Dimension(shadowRadius*2, shadowRadius*2);
    Shape textBlock = textLayer.createTransformedShape();
    Renderer shadowFill = new FillRenderer(shadowColor);
    AffineTransform shadowOffset = AffineTransform.getTranslateInstance(shadowOffsetX, shadowOffsetY);
    ShapeLayer textShadow = new ShapeLayer(cmp, textBlock, shadowFill);
    textShadow.setTransform(shadowOffset);
    textShadow.setImageFilter(shadowBlur, shadowBlurMargins);

    // Masked image
    Position imagePosition = new Position(Anchor.BOTTOM_RIGHT, imageMargin, 0);
    ImageLayer imageLayer = new ImageLayer(cmp, image, imagePosition);
    Rectangle imageRect = new Rectangle(image.getWidth(), image.getHeight());
    AffineTransform imageTransform = imageLayer.getTransform();
    Rectangle maskRect = imageTransform.createTransformedShape(imageRect).getBounds();
    imageLayer.setLayerMask(imageMask, maskRect);

    // Image Shadow Reflection
    Position imageShadowPosition = new Position(Anchor.BOTTOM_RIGHT, imageShadowMargin, 0);
    ImageLayer imageLayerShadow = new ImageLayer(cmp, image, imageShadowPosition);
    AffineTransform imageShadowTransform = imageLayerShadow.getTransform();
    maskRect = imageShadowTransform.createTransformedShape(imageRect).getBounds();
    imageLayerShadow.setLayerMask(imageMask, maskRect);
    imageLayerShadow.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));

    cmp.setLayers(new Layer[]{ bkgLayer, imageLayerShadow, textShadow, textLayer, imageLayer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
    
  }
}
