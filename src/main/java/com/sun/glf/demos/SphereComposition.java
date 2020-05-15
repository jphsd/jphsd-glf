/*
 * @(#)SphereComposition.java
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
public class SphereComposition implements CompositionFactory{
  Color backgroundColor = Color.white;

  Color highlightColor = Color.white;

  Color midtoneColor = Color.yellow;

  Color shadowColor = new Color(128, 0, 0);

  float highlightInterval = 1;

  float midtoneInterval = 2;

  float shadowInterval = 2;

  float blackInterval = 1;

  Color sphereShadowColor = new Color(0, 0, 0, 128);
 
  int shadowBlurRadius = 0;

  int sphereSize = 200;

  int margin = 10;

  Dimension shadowOffset = new Dimension(20, 20);

  float shadowScaleX = 0.8f;

  float shadowScaleY = 0.5f;

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

  public int getSphereSize(){
    return sphereSize;
  }

  public void setSphereSize(int sphereSize){
    this.sphereSize = sphereSize;
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

  public Color getSphereShadowColor(){
    return sphereShadowColor;
  }

  public void setSphereShadowColor(Color sphereShadowColor){
    this.sphereShadowColor = sphereShadowColor;
  }

  public Composition build(){
    // 
    // Base size on sphere size, shadow offset and margins.
    // Create a layer composition where layers 
    // will be stacked.
    //
    Dimension size = new Dimension(sphereSize + margin*2,
				   sphereSize + margin*2);

    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Create a Sphere object using an ellipse filled
    // with a RadialGradientPaintExt
    //
    Ellipse2D sphere = new Ellipse2D.Float(0, 0, sphereSize, sphereSize);
    Rectangle gradientRect = new Rectangle(-sphereSize/2, -sphereSize/2, 
					   3*sphereSize/2, 3*sphereSize/2);

    Color gradientColors[] = {Color.white, highlightColor, midtoneColor, shadowColor, Color.black};
    float gradientIntervals[] = {highlightInterval, midtoneInterval, shadowInterval, blackInterval}; 
    RadialGradientPaintExt sphereFilling 
      = new RadialGradientPaintExt(gradientRect, 
				   gradientColors,
				   gradientIntervals);

    Renderer spherePainter = new FillRenderer(sphereFilling);
    ShapeLayer sphereLayer = new ShapeLayer(cmp, sphere, spherePainter, Position.CENTER);

    //
    // Create the Sphere cast shadow
    //

    AffineTransform shadowTxf = new AffineTransform();
    shadowTxf.translate(shadowOffset.width, shadowOffset.height);
    shadowTxf.translate(0, sphereSize/2f);
    shadowTxf.scale(shadowScaleX, shadowScaleY);
    shadowTxf.translate(0, -sphereSize/2f);
    
    Position shadowPosition = new Position(Anchor.CENTER, 0, 0, shadowTxf);

    FillRenderer shadowPainter = new FillRenderer(sphereShadowColor);    
    ShapeLayer sphereShadowLayer = new ShapeLayer(cmp, sphere, shadowPainter, shadowPosition);


    if(shadowBlurRadius>0){
      ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
      sphereShadowLayer.setImageFilter(blur, new Dimension(shadowBlurRadius*2, shadowBlurRadius*2));
    }

    // 
    // Stack layers in composition
    //
    cmp.setLayers(new Layer[] { sphereShadowLayer, sphereLayer });

    // Set rendering hints for the whole composition
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);

    // Return the composition we have built
    return cmp;
  }

}
