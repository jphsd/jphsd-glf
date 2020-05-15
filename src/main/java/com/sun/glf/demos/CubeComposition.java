/*
 * @(#)CubeComposition.java
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
public class CubeComposition implements CompositionFactory{
  Color highlightColor = Color.yellow;

  Color midtoneColor = Color.orange;

  Color shadowColor = Color.black;

  Color cubeShadowColor = new Color(0, 0, 0, 128);

  int cubeWidth = 120;

  int cubeHeight = 200;

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

  public void setCubeShadowColor(Color cubeShadowColor){
    this.cubeShadowColor = cubeShadowColor;
  }

  public Color getCubeShadowColor(){
    return cubeShadowColor;
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

  public int getCubeWidth(){
    return cubeWidth;
  }

  public void setCubeWidth(int cubeWidth){
    this.cubeWidth = cubeWidth;
  }

  public int getCubeHeight(){
    return cubeHeight;
  }

  public void setCubeHeight(int cubeHeight){
    this.cubeHeight = cubeHeight;
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
    // Build base building block : rectangle and cube top
    //
    Rectangle baseShape = new Rectangle(0, 0, cubeWidth, cubeHeight);
    
    //
    // Build cube top
    //
    Shape topShape = new Rectangle(0, 0, cubeWidth, cubeWidth);

    //
    // Now, build the GradientPaint and GradientPaintExt that will be
    // used to fill the object sides. Note that those paints are defined
    // in user space.
    //
    GradientPaint topPaint = new GradientPaint(0, 0, midtoneColor, cubeWidth, 0, highlightColor);

    Color gradientColors[] = {highlightColor, midtoneColor, shadowColor};
    float gradientIntervals[] = {midtoneInterval, shadowInterval};

    GradientPaintExt sidePaint = new GradientPaintExt(0, 0, cubeWidth, cubeHeight, gradientIntervals, gradientColors);

    FillRenderer sidePainter = new FillRenderer(sidePaint);
    FillRenderer topPainter = new FillRenderer(topPaint);

    //
    // Process size and create composition
    //
    Dimension size = new Dimension(cubeWidth + margins.width*2,
				   cubeHeight + margins.height*2);
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

    float sideScale = (float)Math.abs(cosAngle*depth/cubeWidth);
    AffineTransform sideShearTxf = AffineTransform.getShearInstance(0, sideShear);
    sideShearTxf.scale(sideScale, 1);
    sideShearTxf.rotate(Math.PI);

    float topScale = (depth*sinAngle)/cubeWidth;
    AffineTransform topShearTxf = AffineTransform.getShearInstance(topShear, 0);
    topShearTxf.scale(1, topScale);

    // Face is positioned in the center
    Position facePosition = Position.CENTER;
    Rectangle positionedFaceBounds = facePosition.createTransformedShape(baseShape, cmp.getBounds()).getBounds();

    // Side is position to the left of the face if shear is positive,
    // otherwise, it is to the right
    Anchor sideAnchor = Anchor.BOTTOM_LEFT;
    if(angle>Math.PI/2)
      sideAnchor = Anchor.BOTTOM_RIGHT;
    Position sidePosition = new Position(sideAnchor, -cubeWidth*sideScale, 0, sideShearTxf, false);
    AffineTransform sideTxf = sidePosition.getTransform(baseShape, positionedFaceBounds);

    // Top is ... at the top of the face
    Anchor topAnchor = angle<Math.PI/2?Anchor.TOP_RIGHT:Anchor.TOP_LEFT;
    Position topPosition = new Position(topAnchor, 0, -cubeWidth*topScale,
					topShearTxf, false);
    AffineTransform topTxf = topPosition.getTransform(topShape, positionedFaceBounds);

    //
    // Build cube layers 
    //
    ShapeLayer faceLayer = new ShapeLayer(cmp, baseShape, sidePainter, facePosition);
    ShapeLayer sideLayer = new ShapeLayer(cmp, baseShape, sidePainter);
    sideLayer.setTransform(sideTxf);
    ShapeLayer topLayer = new ShapeLayer(cmp, topShape, topPainter);
    topLayer.setTransform(topTxf);

    //
    // Add a Shadow to the cube. First, build the shadow base shape
    //
    GeneralPath shadowShape = new GeneralPath(faceLayer.createTransformedShape());
    shadowShape.append(sideLayer.createTransformedShape(), false);
    shadowShape.append(topLayer.createTransformedShape(), false);
    Rectangle shadowBounds = shadowShape.getBounds();

    AffineTransform shadowCastTxf = new AffineTransform();
    shadowCastTxf.translate(0, shadowBounds.height*(1-shadowScaleY)/2);
    shadowCastTxf.translate(shadowOffset.width, shadowOffset.height);
    shadowCastTxf.shear(shadowShearX, 0);
    shadowCastTxf.scale(shadowScaleX, shadowScaleY);

    Position shadowPosition = new Position(Anchor.CENTER, 0, 0, shadowCastTxf);
    FillRenderer shadowPainter = new FillRenderer(cubeShadowColor);
    
    ShapeLayer shadowLayer = new ShapeLayer(cmp, shadowShape, shadowPainter, shadowPosition);

    if(shadowBlurRadius>0){
      ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
      shadowLayer.setImageFilter(blur, new Dimension(shadowBlurRadius*2, shadowBlurRadius*2));
    }

    cmp.setLayers(new Layer[]{ shadowLayer, faceLayer, sideLayer, topLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }
}

