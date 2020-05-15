/*
 * @(#)PostCardComposition.java
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
 * This examples illustrates several techniques to create a sample
 * postcard creation application:
 * + Antiquing a picture. This is used both for the postcard background
 *   and for the postcard image itself.
 * + Masking of the postcard image for a creative border effect.
 * + Shadow effect around the postcard image
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/03/1999
 */
public class PostCardComposition implements CompositionFactory{
  File imageFile = new File("res/images/misc/dianeBigSmile.jpg");

  Color midtoneColor = Color.orange;

  Color shadowColor = new Color(128, 0, 0);

  Color highlightColor = new Color(255, 255, 120);

  String message = "Hello Grand-Ma";

  Font messageFont = new Font("French Script MT", Font.PLAIN, 30);

  Color messageFillColor = new Color(128, 0, 0);

  Color messageStrokeColor = new Color(128, 0, 0);

  Color messageSeparationColor = Color.yellow;

  Anchor messageAnchor = Anchor.BOTTOM_RIGHT;

  Dimension messageAdjustment = new Dimension(10, 10);

  Dimension messageSeparationOffset = new Dimension(1, 1);

  int marginSize = 30;

  File textureImageFile= new File("res/images/syberia/syberia43.jpg");

  int imageShadowBlurRadius = 1;

  int imageShadowWidth = 10;

  Color imageShadowColor = new Color(0, 0, 0, 128); 

  float shadowScaleFactor = 3;

  float messageStrokeWidth = 4;

  float miterLimitAngle = 30; 

  public float getMiterLimitAngle(){
    return miterLimitAngle;
  }

  public void setMiterLimitAngle(float miterLimitAngle){
    this.miterLimitAngle = miterLimitAngle;
  }

  public float getMessageStrokeWidth(){
    return messageStrokeWidth;
  }

  public void setMessageStrokeWidth(float messageStrokeWidth){
    this.messageStrokeWidth = messageStrokeWidth;
  }

  public Dimension getMessageSeparationOffset(){
    return messageSeparationOffset;
  }

  public void setMessageSeparationOffset(Dimension messageSeparationOffset){
    this.messageSeparationOffset = messageSeparationOffset;
  }

  public float getShadowScaleFactor(){
    return shadowScaleFactor;
  }

  public void setShadowScaleFactor(float shadowScaleFactor){
    this.shadowScaleFactor = shadowScaleFactor;
  }

  public Color getImageShadowColor(){
    return imageShadowColor;
  }

  public void setImageShadowColor(Color imageShadowColor){
    this.imageShadowColor = imageShadowColor;
  }

  public int getImageShadowBlurRadius(){
    return imageShadowBlurRadius;
  }

  public void setImageShadowBlurRadius(int imageShadowBlurRadius){
    this.imageShadowBlurRadius = imageShadowBlurRadius;
  }

  public int getImageShadowWidth(){
    return imageShadowWidth;
  }

  public void setImageShadowWidth(int imageShadowWidth){
    this.imageShadowWidth = imageShadowWidth;
  }

  public Anchor getMessageAnchor(){
    return messageAnchor;
  }

  public void setMessageAnchor(Anchor messageAnchor){
    this.messageAnchor = messageAnchor;
  }

  public Dimension getMessageAdjustment(){
    return messageAdjustment;
  }

  public void setMessageAdjustment(Dimension messageAdjustment){
    this.messageAdjustment = messageAdjustment;
  }

  public int getMarginSize(){
    return marginSize;
  }

  public void setMarginSize(int marginSize){
    this.marginSize = marginSize;
  }

  public File getTextureImageFile(){
    return textureImageFile;
  }

  public void setTextureImageFile(File textureImageFile){
    this.textureImageFile = textureImageFile;
  }  

  public String getMessage(){
    return message;
  }

  public void setMessage(String message){
    this.message = message;
  }

  public Font getMessageFont(){
    return messageFont;
  }

  public void setMessageFont(Font messageFont){
    this.messageFont = messageFont;
  }

  public Color getMessageFillColor(){
    return messageFillColor;
  }

  public void setMessageFillColor(Color messageFillColor){
    this.messageFillColor = messageFillColor;
  }

  public Color getMessageStrokeColor(){
    return messageStrokeColor;
  }

  public void setMessageStrokeColor(Color messageStrokeColor){
    this.messageStrokeColor = messageStrokeColor;
  }

  public Color getMessageSeparationColor(){
    return messageSeparationColor;
  }

  public void setMessageSeparationColor(Color messageSeparationColor){
    this.messageSeparationColor = messageSeparationColor;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Color getMidtoneColor(){
    return midtoneColor;
  }

  public void setMidtoneColor(Color midtoneColor){
    this.midtoneColor = midtoneColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setHighlightColor(Color highlightColor){
    this.highlightColor = highlightColor;
  }

  public Color getHighlightColor(){
    return highlightColor;
  }

  public Composition build(){
    //
    // First, load raw images
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB);
    if(image==null)
      throw new Error("Could not load : " + imageFile);

    BufferedImage textureImage = Toolbox.loadImage(textureImageFile, BufferedImage.TYPE_INT_RGB);
    if(textureImage==null)
      throw new Error("Could not load : " + textureImageFile);

    //
    // Create a ToneAdjustmentOp filter to unify the image and the background:
    // it is used to filter both the background texture and the image
    //
    ToneAdjustmentOp toneAdjustment 
      = new ToneAdjustmentOp(
			     new Color[]{Color.black, shadowColor, midtoneColor, highlightColor, Color.white },
			     new float[]{1, 1, 1, 1}, true);

    //
    // Process composition size based on image and outer margins
    //
    int iw = image.getWidth();
    int ih = image.getHeight();
    Dimension size = new Dimension(iw + 2*marginSize, ih + 2*marginSize);
    LayerComposition cmp = new LayerComposition(size);

    //
    // Fill background image with texture
    //
    textureImage = toneAdjustment.filter(textureImage, textureImage);
    TexturePaint texturePaint = new TexturePaint(textureImage, cmp.getBounds());
    cmp.setBackgroundPaint(texturePaint);

    //
    // Create a layer with the image. Use the ToneAdjustmentOp filter 
    // to modify the image's tonality and set its mask
    //
    ImageLayer imageLayer = new ImageLayer(cmp, image, Position.CENTER);
    imageLayer.setImageFilter(toneAdjustment);
    BufferedImage mask = makeImageMask(iw, ih);
    Rectangle imageRect = new Rectangle(marginSize, marginSize, iw, ih);
    // imageLayer.setLayerMask(mask, imageRect);

    //
    // Create a shadow for the image. Here, we use a trick to speed up processing.
    //
    AffineTransform shrinkShadowTxf 
      = AffineTransform.getScaleInstance(1/shadowScaleFactor, 
					 1/shadowScaleFactor);
    AffineTransform blowUpShadowTxf 
      = AffineTransform.getScaleInstance(shadowScaleFactor, 
					 shadowScaleFactor);

    AffineTransformOp shrinkShadow 
      = new AffineTransformOp(shrinkShadowTxf, null);
    AffineTransformOp blowUpShadow 
      = new AffineTransformOp(blowUpShadowTxf, AffineTransformOp.TYPE_BILINEAR);

    GaussianKernel blurKernel 
      = new GaussianKernel((int)(imageShadowBlurRadius/shadowScaleFactor));

    ConvolveOp shadowBlur = new ConvolveOp(blurKernel);
    CompositeOp compositeOp = new CompositeOp(new BufferedImageOp[]{ shrinkShadow, shadowBlur, blowUpShadow });

    Rectangle shadowRectangle = new Rectangle(marginSize - imageShadowWidth,
					      marginSize - imageShadowWidth, 
					      image.getWidth() + 2*imageShadowWidth, 
					      image.getHeight() + 2*imageShadowWidth);
    ShapeLayer imageShadowLayer = new ShapeLayer(cmp, shadowRectangle, new FillRenderer(imageShadowColor));
    imageShadowLayer.setImageFilter(compositeOp, new Dimension(imageShadowBlurRadius*2,
							       imageShadowBlurRadius*2));
    Area shadowMask = new Area(cmp.getBounds());
    shadowMask.subtract(new Area(imageRect));
    imageShadowLayer.setClip(shadowMask);

    //
    // Add text layers
    //
    float miterLimit = Float.MAX_VALUE;
    miterLimitAngle %= 180;
    if(miterLimitAngle<0)
      miterLimitAngle *= -1;
    if(miterLimitAngle!=0)
      miterLimit = (float)(1/Math.sin((Math.PI*miterLimitAngle)/(360.0)));

    BasicStroke messageStroke = new BasicStroke(messageStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, miterLimit);
    FillRenderer textFill = new FillRenderer(messageFillColor);
    StrokeRenderer textStroke = new StrokeRenderer( messageStrokeColor, messageStroke);
    CompositeRenderer textPainter = new CompositeRenderer(textStroke, textFill);

    TextLayer messageLayer = new TextLayer(cmp, message, messageFont, textPainter, 
					   new Position(messageAnchor, messageAdjustment.width, messageAdjustment.height),
					   (-1), TextAlignment.CENTER);
    Shape textBlock = messageLayer.createTransformedShape();
    StrokeRenderer messageSeparationPainter = new StrokeRenderer(messageSeparationColor, messageStroke);
    ShapeLayer messageSeparationLayer = new ShapeLayer(cmp, textBlock, messageSeparationPainter); 
    AffineTransform messageSeparationAdjustment 
      = AffineTransform.getTranslateInstance(messageSeparationOffset.width,
					     messageSeparationOffset.height);
    messageSeparationLayer.setTransform(messageSeparationAdjustment);

    

    cmp.setLayers(new Layer[]{ imageShadowLayer, imageLayer, messageSeparationLayer, messageLayer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
    
  }

  private BufferedImage makeImageMask(int iw, int ih){
    BufferedImage mask = new BufferedImage(iw, ih, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g = mask.createGraphics();
    
    g.setPaint(Color.black);
    g.fillRect(0, 0, iw, ih);

    GeneralPath triangle = new GeneralPath();
    triangle.moveTo(0, 0);
    triangle.lineTo(iw/2, ih/2);
    triangle.lineTo(0, ih);
    triangle.closePath();
    GradientPaint fuzzyEdgePaint = new GradientPaint(0, 0, Color.black,
				       iw*.05f, 0, Color.white);
    g.setPaint(fuzzyEdgePaint);
    g.fill(triangle);

    g.rotate(Math.PI, iw/2, ih/2);
    g.fill(triangle);

    g.setTransform(new AffineTransform());
    triangle = new GeneralPath();
    triangle.moveTo(0, 0);
    triangle.lineTo(iw/2, ih/2);
    triangle.lineTo(iw, 0);
    triangle.closePath();    
    fuzzyEdgePaint = new GradientPaint(0, 0, Color.black,
				       0, ih*.05f, Color.white);
    g.setPaint(fuzzyEdgePaint);
    g.fill(triangle);
    g.rotate(Math.PI, iw/2, ih/2);
    g.fill(triangle);

    return mask;
  }

}
