/*
 * @(#)CylinderComposition.java
 * 
 * Copyright 1999 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * 
 */

package com.sun.glf.demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.image.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 *
 * @author        Vincent Hardy 
 * @version       1.0, 10/27/1998
 */
public class CylinderComposition implements CompositionFactory{
  Color backgroundColor = Color.gray;

  Color highlightColor = Color.white;

  Color midtoneColor = Color.yellow;

  Color shadowColor = new Color(128, 0, 0);

  float highlightInterval = 1;

  float midtoneInterval = 2;

  float shadowInterval = 2;

  float blackInterval = 1;

  Color cylinderShadowColor = new Color(0, 0, 0, 128);
 
  int shadowBlurRadius = 0;

  Dimension cylinderSize = new Dimension(40, 200);

  int margin = 10;

  Dimension shadowOffset = new Dimension(20, 20);

  float shadowScaleX = 0.8f;

  float shadowScaleY = 0.5f;

  float shadowShearX = 0.2f;

  float cylinderTopYScale = 0.3f;

  public float getCylinderTopYScale(){
    return cylinderTopYScale;
  }

  public void setCylinderTopYScale(float cylinderTopYScale){
    this.cylinderTopYScale = cylinderTopYScale;
  }

  public float getShadowShearX(){
    return shadowShearX;
  }

  public void setShadowShearX(float shadowShearX){
    this.shadowShearX = shadowShearX;
  }

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

  public Dimension getShadowOffset(){
    return shadowOffset;
  }

  public void setShadowOffset(Dimension shadowOffset){
    this.shadowOffset = shadowOffset;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public Dimension getCylinderSize(){
    return cylinderSize;
  }

  public void setCylinderSize(Dimension cylinderSize){
    this.cylinderSize = cylinderSize;
  }

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public float getHighlightInterval(){
    return highlightInterval;
  }

  public void setHighlightInterval(float highlightInterval){
    this.highlightInterval = highlightInterval;
  }

  public float getMidtoneInterval(){
    return midtoneInterval;
  }

  public void setMidtoneInterval(float midtoneInterval){
    this.midtoneInterval = midtoneInterval;
  }

  public float getShadowInterval(){
    return shadowInterval;
  }

  public void setShadowInterval(float shadowInterval){
    this.shadowInterval = shadowInterval;
  }

  public float getBlackInterval(){
    return blackInterval;
  }

  public void setBlackInterval(float blackInterval){
    this.blackInterval = blackInterval;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Color getHighlightColor(){
    return highlightColor;
  }

  public void setHighlightColor(Color highlightColor){
    this.highlightColor = highlightColor;
  }

  public Color getMidtoneColor(){
    return midtoneColor;
  }

  public void setMidtoneColor(Color midtoneColor){
    this.midtoneColor = midtoneColor;
  }

  public Color getCylinderShadowColor(){
    return cylinderShadowColor;
  }

  public void setCylinderShadowColor(Color cylinderShadowColor){
    this.cylinderShadowColor = cylinderShadowColor;
  }

  public Composition build(){
    //
    // First, create the Shapes for the cylinder and its shadow
    //

    // Cylinder Body
    Area cylinderBody = new Area(new Rectangle(0, 0, cylinderSize.width, cylinderSize.height));
    cylinderBody.add(new Area(new Arc2D.Float(0, cylinderSize.height - cylinderSize.width*cylinderTopYScale/2, 
					cylinderSize.width, cylinderSize.width*cylinderTopYScale, 
					0, -180, Arc2D.CHORD)));

    Shape cylinderShape = cylinderBody;

    // Cylinder Top
    Shape cylinderTop = new Ellipse2D.Float(0, -cylinderSize.width*cylinderTopYScale/2, 
					    cylinderSize.width, cylinderSize.width*cylinderTopYScale);

    // 
    // Base size on cylinder size, shadow offset and margins.
    // Create a layer composition where layers 
    // will be stacked.
    //
    Dimension size = new Dimension(cylinderSize.width + margin*2,
				   cylinderSize.height + margin*2);

    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Create a GradientPaintExt to fill the cylinder body
    //
    Color gradientColors[] = {Color.white, highlightColor, midtoneColor, shadowColor, Color.black};
    float gradientIntervals[] = {highlightInterval, midtoneInterval, shadowInterval, blackInterval}; 
    GradientPaintExt cylinderFilling 
      = new GradientPaintExt(0, 0, cylinderSize.width, 0,
			     gradientIntervals, gradientColors);
    RadialGradientPaint cylinderTopFilling
      = new RadialGradientPaint(cylinderTop.getBounds2D(), highlightColor, midtoneColor);

    Renderer cylinderPainter = new FillRenderer(cylinderFilling);
    Renderer cylinderTopPainter = new FillRenderer(cylinderTopFilling);

    ShapeLayer cylinderLayer = new ShapeLayer(cmp, cylinderShape, cylinderPainter, Position.CENTER);

    Position cylinderTopPosition = new Position(Anchor.TOP, 0, -cylinderSize.width*cylinderTopYScale/2);
    AffineTransform cylinderTopTxf = cylinderTopPosition.getTransform(cylinderTop, cylinderBody.getBounds());
    cylinderTopTxf.preConcatenate(cylinderLayer.getTransform());
    ShapeLayer cylinderTopLayer = new ShapeLayer(cmp, cylinderTop, cylinderTopPainter);
    cylinderTopLayer.setTransform(cylinderTopTxf);
    

    //
    // Create the Cylinder cast shadow
    //

    // Cylinder Shadow
    Area cylinderShadow = new Area(cylinderBody);
    cylinderShadow.add(new Area(cylinderTop));

    AffineTransform shadowCastTxf = getBottomLeftShearTransform(cylinderShadow);

    Position shadowPosition = new Position(Anchor.CENTER, 0, 0, shadowCastTxf);
    FillRenderer shadowPainter = new FillRenderer(cylinderShadowColor);
    
    ShapeLayer cylinderShadowLayer = new ShapeLayer(cmp, cylinderShadow, shadowPainter, shadowPosition);

    if(shadowBlurRadius>0){
      ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
      cylinderShadowLayer.setImageFilter(blur, new Dimension(shadowBlurRadius*2, shadowBlurRadius*2));
    }

    // 
    // Stack layers in composition
    //
    cmp.setLayers(new Layer[] { cylinderShadowLayer, cylinderLayer, cylinderTopLayer });

    // Set rendering hints for the whole composition
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);

    // Return the composition we have built
    return cmp;
  }

  private AffineTransform getBottomLeftShearTransform(Shape shape){
    AffineTransform shear = new AffineTransform();
    Rectangle bounds = shape.getBounds();

    // Add offset
    shear.translate(shadowOffset.width, shadowOffset.height);

    // Align bottom left point with original Shape
    shear.translate(-bounds.width/2, bounds.height/2);

    // Shear
    shear.shear(shadowShearX, 0);

    // Scale
    shear.scale(shadowScaleX, shadowScaleY);

    // Move bottom left to origin
    shear.translate(bounds.width/2, -bounds.height/2);
      
    return shear;
  }
}
