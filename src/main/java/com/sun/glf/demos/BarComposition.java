/*
 * @(#)BarComposition.java
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

import java.awt.*;
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
public class BarComposition implements CompositionFactory{
  Color highlightColor = Color.yellow;

  Color midtoneColor = Color.orange;

  Color shadowColor = Color.black;

  Color barShadowColor = new Color(0, 0, 0, 128);

  int barWidth = 120;

  int barHeight = 200;

  float shearAngle = 30;

  Dimension margins = new Dimension(40, 40);

  float midtoneInterval = 2;

  float shadowInterval = 1;

  float depth = 10;

  float shadowScaleX = 0.5f;

  float shadowScaleY = 0.5f;

  Dimension shadowOffset = new Dimension(40, 40);

  float shadowShearX = 0.1f;

  int shadowBlurRadius = 0;
 
  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }
  public Color getShadowColor(){
    return shadowColor;
  }

  public void setBarShadowColor(Color barShadowColor){
    this.barShadowColor = barShadowColor;
  }

  public Color getBarShadowColor(){
    return barShadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
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

  public float getShadowShearX(){
    return shadowShearX;
  }

  public void setShadowShearX(float shadowShearX){
    this.shadowShearX = shadowShearX;
  }

  public float getDepth(){
    return depth;
  }

  public void setDepth(float depth){
    this.depth = depth;
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

  public int getBarWidth(){
    return barWidth;
  }

  public void setBarWidth(int barWidth){
    this.barWidth = barWidth;
  }

  public int getBarHeight(){
    return barHeight;
  }

  public void setBarHeight(int barHeight){
    this.barHeight = barHeight;
  }

  public float getShearAngle(){
    return shearAngle;
  }

  public void setShearAngle(float shearAngle){
    this.shearAngle = shearAngle;
  }

  public Dimension getMargins(){
    return margins;
  }

  public void setMargins(Dimension margins){
    this.margins = margins;
  }

  public Composition build(){
    //
    // Build base building block : rectangle and bar top
    //
    Rectangle baseShape = new Rectangle(0, 0, barWidth, barHeight);
    
    //
    // Build bar top
    //
    Rectangle topShape = new Rectangle(0, 0, barWidth, barWidth);

    //
    // Now, build the GradientPaint and GradientPaintExt that will be
    // used to fill the object sides. Note that those paints are defined
    // in user space.
    //
    GradientPaint topPaint = new GradientPaint(0, 0, midtoneColor, barWidth, 0, highlightColor);

    Color gradientColors[] = {highlightColor, midtoneColor, shadowColor};
    float gradientIntervals[] = {midtoneInterval, shadowInterval};

    GradientPaintExt sidePaint = new GradientPaintExt(0, 0, barWidth, barHeight, gradientIntervals, gradientColors);

    FillRenderer sidePainter = new FillRenderer(sidePaint);
    FillRenderer topPainter = new FillRenderer(topPaint);

    //
    // Process size and create composition
    //
    Dimension size = new Dimension(barWidth + margins.width*2,
				   barHeight + margins.height*2);
    LayerComposition cmp = new LayerComposition(size);

    //
    // Process positions for each elements
    //
    
    //
    // Process side and top shear transforms
    //
    double angle = (float)(shearAngle*Math.PI/180f);
    angle %= 2*Math.PI;
    if(angle>0 && angle>Math.PI)
      angle -= Math.PI;
    if(angle<0){
      if(angle>-Math.PI)
	angle = Math.PI + angle;
      else
	angle = 2*Math.PI + angle;
    }

    float cosAngle = (float)Math.cos(angle);
    float sinAngle = (float)Math.sin(angle);
    float sideShear = (float)Math.tan(angle);
    float topShear = 1/sideShear;

    float sideScale = (float)Math.abs(cosAngle*depth/barWidth);
    AffineTransform sideShearTxf = AffineTransform.getShearInstance(0, sideShear);
    sideShearTxf.scale(sideScale, 1);
    sideShearTxf.rotate(Math.PI);

    float topScale = (depth*sinAngle)/barWidth;
    AffineTransform topShearTxf = AffineTransform.getShearInstance(topShear, 0);
    topShearTxf.scale(1, topScale);

    // Face is positioned in the center
    Position facePosition = Position.CENTER;

    // Side is positioned to the left of the face if shear is positive,
    // otherwise, it is positioned to the right
    Anchor sideAnchor = Anchor.BOTTOM_LEFT;
    if(angle>Math.PI/2)
      sideAnchor = Anchor.BOTTOM_RIGHT;
    Position sidePosition = new Position(sideAnchor, -barWidth*sideScale, 0, sideShearTxf, false);
    AffineTransform sideTxf = sidePosition.getTransform(baseShape, baseShape);

    // Top is ... at the top of the face
    Anchor topAnchor = angle<Math.PI/2?Anchor.TOP_RIGHT:Anchor.TOP_LEFT;
    Position topPosition = new Position(topAnchor, 0, -barWidth*topScale,
					topShearTxf, false);
    AffineTransform topTxf = topPosition.getTransform(topShape, baseShape);

    //
    // Build bar layers 
    //
    ShapeLayer faceLayer = new ShapeLayer(cmp, baseShape, sidePainter, facePosition);
    ShapeLayer sideLayer = new ShapeLayer(cmp, baseShape, sidePainter);
    sideTxf.preConcatenate(faceLayer.getTransform());
    sideLayer.setTransform(sideTxf);
    ShapeLayer topLayer = new ShapeLayer(cmp, topShape, topPainter);
    topTxf.preConcatenate(faceLayer.getTransform());
    topLayer.setTransform(topTxf);

    //
    // Add a Shadow to the bar. First, build the shadow base shape
    //
    GeneralPath shadowShape = new GeneralPath(faceLayer.createTransformedShape());
    shadowShape.append(sideLayer.createTransformedShape(), false);
    shadowShape.append(topLayer.createTransformedShape(), false);
    AffineTransform shadowCastTxf = getBottomLeftShearTransform(shadowShape);

    Position shadowPosition = new Position(Anchor.CENTER, 0, 0, shadowCastTxf);
    FillRenderer shadowPainter = new FillRenderer(barShadowColor);
    
    ShapeLayer shadowLayer = new ShapeLayer(cmp, shadowShape, shadowPainter, shadowPosition);

    if(shadowBlurRadius>0){
      ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
      shadowLayer.setImageFilter(blur, new Dimension(shadowBlurRadius*2, shadowBlurRadius*2));
    }

    cmp.setLayers(new Layer[]{ shadowLayer, faceLayer, sideLayer, topLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

