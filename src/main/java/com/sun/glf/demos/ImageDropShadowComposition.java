/*
 * @(#)ImageDropShadowComposition.java
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
 * This examples illustrates how to create a drop shadow effect on an 
 * image. 
 *
 * @author         Vincent Hardy
 * @version        1.0, 06/03/1999
 */
public class ImageDropShadowComposition implements CompositionFactory{
  File imageFile = new File("");

  File imageMaskFile = new File("");

  String label = "Call us now!";

  Font labelFont = new Font("Impact MT", Font.PLAIN, 30);

  Color labelColor = Color.black;

  Color shadowColor = new Color(0, 0, 0, 128);

  Color imageTint = new Color(30, 60, 30);

  Dimension shadowOffset = new Dimension(10, 10);

  float labelBumpAngle = (float)-Math.PI/4;

  int margin = 10;

  int labelImageGap = 10;

  int shadowBlurRadius = 7;

  int labelHorizontalAdjust = 10;

  Color backgroundColor = new Color(0, 0, 0, 0);
  
  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getLabelHorizontalAdjust(){
    return labelHorizontalAdjust;
  }

  public void setLabelHorizontalAdjust(int labelHorizontalAdjust){
    this.labelHorizontalAdjust = labelHorizontalAdjust;
  }

  public float getLabelBumpAngle(){
    return labelBumpAngle;
  }

  public void setLabelBumpAngle(float labelBumpAngle){
    this.labelBumpAngle = labelBumpAngle;
  }

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public int getLabelImageGap(){
    return labelImageGap;
  }

  public void setLabelImageGap(int labelImageGap){
    this.labelImageGap = labelImageGap;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
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

  public String getLabel(){
    return label;
  }

  public void setLabel(String label){
    this.label = label;
  }

  public Font getLabelFont(){
    return labelFont;
  }

  public void setLabelFont(Font labelFont){
    this.labelFont = labelFont;
  }

  public Color getLabelColor(){
    return labelColor;
  }

  public void setLabelColor(Color labelColor){
    this.labelColor = labelColor;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public Color getImageTint(){
    return imageTint;
  }

  public void setImageTint(Color imageTint){
    this.imageTint = imageTint;
  }

  public Dimension getShadowOffset(){
    return shadowOffset;
  }

  public void setShadowOffset(Dimension shadowOffset){
    this.shadowOffset = shadowOffset;
  }
  
  public Composition build(){
    //
    // First, load raw images
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load : " + imageFile);

    BufferedImage imageMask = Toolbox.loadImage(imageMaskFile, BufferedImage.TYPE_BYTE_GRAY); 
    if(imageMask==null)
      throw new Error("Could not load : " + imageMaskFile);

    //
    // Now, create the label Shape and transform it with the BumpTransform
    //
    Shape labelShape = TextLayer.makeTextBlock(label, labelFont);
    BumpTransform labelBender = new BumpTransform(labelBumpAngle);
    MunchTransform munchTransform = new MunchTransform(1);
    labelBender.concatenate(munchTransform);
    labelShape = labelBender.transform(labelShape);

    //
    // Process composition size based on image and text label.
    //
    Rectangle labelBounds = labelShape.getBounds();
    int width = (int)(Math.max(image.getWidth(), labelBounds.width) + 2*margin);
    int height = image.getHeight() + labelImageGap + labelBounds.height + 2*margin;
    Dimension size = new Dimension(width, height);
    LayerComposition cmp = new LayerComposition(size);

    cmp.setBackgroundPaint(backgroundColor);

    //
    // Create tinted image in an ImageLayer. First create the base ImageLayer, 
    // then set its mask and filter.
    //
    Position imagePosition = new Position(Anchor.TOP, 0, margin);
    ImageLayer tintedImageLayer = new ImageLayer(cmp, image, imagePosition);

    // Set mask
    Point imageLeftCorner = new Point(0, 0);
    tintedImageLayer.getTransform().transform(imageLeftCorner, imageLeftCorner);
    Rectangle imageRect = new Rectangle(imageLeftCorner.x, imageLeftCorner.y,
					imageMask.getWidth(), imageMask.getHeight());
    tintedImageLayer.setLayerMask(imageMask, imageRect);
    
    // Set filter
    ToneAdjustmentOp tinter = new ToneAdjustmentOp(imageTint);
    tintedImageLayer.setImageFilter(tinter);

    //
    // Create a shadow for the image: use the mask as a base image,
    // drow the shadow color using AlphaComposite.SrcIn and blur with 
    // and ConvolveOp
    //
    BufferedImage imageShadow = new BufferedImage(imageMask.getWidth(), imageMask.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g = imageShadow.createGraphics();
    g.drawImage(tintedImageLayer.getLayerMask(), 0, 0, null);
    g.setComposite(AlphaComposite.SrcIn);
    g.setPaint(shadowColor);
    g.fillRect(0, 0, imageMask.getWidth(), imageMask.getHeight());
    g.dispose();
    
    AffineTransform shadowShiftTxf 
      = AffineTransform.getTranslateInstance(shadowOffset.width,
					     shadowOffset.height);
    Position imageShadowPosition = new Position(Anchor.TOP, 0, margin, shadowShiftTxf, true);
    ImageLayer imageShadowLayer = new ImageLayer(cmp, imageShadow, imageShadowPosition);

    Kernel blurKernel = new GaussianKernel(shadowBlurRadius);
    ConvolveOp blur = new ConvolveOp(blurKernel);
    Dimension blurMargins = new Dimension(shadowBlurRadius*2, shadowBlurRadius*2);
    imageShadowLayer.setImageFilter(blur, blurMargins);

    //
    // Create a layer for the text label
    //
    Position labelPosition = new Position(Anchor.BOTTOM, 0, margin);
    FillRenderer labelPainter = new FillRenderer(labelColor);
    ShapeLayer labelLayer = new ShapeLayer(cmp, labelShape, 
					   labelPainter,
					   labelPosition);

    //
    // Create a shadow for the text label
    //
    Position labelShadowPosition = new Position(Anchor.BOTTOM, 0, margin, shadowShiftTxf);
    ShapeLayer labelShadowLayer = new ShapeLayer(cmp, labelShape, 
						 new FillRenderer(shadowColor),
						 labelShadowPosition);
    labelShadowLayer.setImageFilter(blur, blurMargins);

    
    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{ imageShadowLayer,
				 tintedImageLayer,
				 labelShadowLayer,
				 labelLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    
    return cmp;
  }
}

