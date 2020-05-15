/*
 * @(#)VolumeComposition.java
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
import java.awt.Font;  
import java.awt.GradientPaint;  
import java.awt.Graphics;  
import java.awt.Graphics2D;  
import java.awt.Point;  
import java.awt.Rectangle;  
import java.awt.RenderingHints;  
import java.awt.Shape;  
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * Combines several of the gradient based 3D shapes: sphere, bars and
 * cylinders in a single composition.
 *
 * @author        Vincent Hardy 
 * @version       1.0, 06/12/1999
 */
public class VolumeComposition implements CompositionFactory{
  Dimension size = new Dimension(650, 450);

  float skyRatio = 0.3f;

  float rayAngle = 30f;

  Color skyColor = new Color(10, 10, 20) ;

  Color seaColor = new Color(120, 120, 180);

  Color seaDarkColor = new Color(40, 40, 60);

  File sphereBean = new File("");

  File cylinderBean = new File("");

  File barBean = new File("");

  Anchor sphereAnchor = Anchor.BOTTOM_LEFT;

  Anchor cylinderAnchor = Anchor.BOTTOM_RIGHT;

  Anchor barAnchor = Anchor.RIGHT;

  Dimension sphereAdjust = new Dimension(0, 0);

  Dimension cylinderAdjust = new Dimension(0, 0);

  Dimension barAdjust = new Dimension(0, 0);

  int graphValueOne = 150;

  int graphValueTwo = 100;

  int graphValueThree = 50;

  float sphereScale = 1;

  float barScale = 1;

  float cylinderScale = 1;

  int sphereOverlap = 0;

  int cylinderOverlap = 0;

  int barOverlap = 0;

  Color backgroundColor = new Color(20, 20, 30);

  int sunRadius = 75;

  Color gridColor = new Color(0, 0, 0, 128);

  float gridStrokeWidth = 1;

  float gridTopCellWidth = 10;

  float gridBottomCellWidth = 30;

  String message = "Stroking Path with Text is Fun!";

  Font messageFont = new Font("Impact MT", Font.PLAIN, 80);

  Color messageColor = Color.white;

  Color shadowColor = new Color(0, 0, 0, 128);

  int shadowBlurRadius = 12;

  public Color getShadowColor(){
    return shadowColor;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
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

  public Color getMessageColor(){
    return messageColor;
  }

  public void setMessageColor(Color messageColor){
    this.messageColor = messageColor;
  }

  public float getGridTopCellWidth(){
    return gridTopCellWidth;
  }

  public void setGridTopCellWidth(float gridTopCellWidth){
    this.gridTopCellWidth = gridTopCellWidth;
  }

  public float getGridBottomCellWidth(){
    return gridBottomCellWidth;
  }

  public void setGridBottomCellWidth(float gridBottomCellWidth){
    this.gridBottomCellWidth = gridBottomCellWidth;
  }

  public float getGridStrokeWidth(){
   return gridStrokeWidth;
  }

  public void setGridStrokeWidth(float gridStrokeWidth){
    this.gridStrokeWidth = gridStrokeWidth;
  }

  public Color getGridColor(){
    return gridColor;
  }

  public void setGridColor(Color gridColor){
    this.gridColor = gridColor;
  }

  public int getSunRadius(){
    return sunRadius;
  }

  public void setSunRadius(int sunRadius){
    this.sunRadius = sunRadius;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getSphereOverlap(){
    return sphereOverlap;
  }

  public void setSphereOverlap(int sphereOverlap){
    this.sphereOverlap = sphereOverlap;
  }

  public int getCylinderOverlap(){
    return cylinderOverlap;
  }

  public void setCylinderOverlap(int cylinderOverlap){
    this.cylinderOverlap = cylinderOverlap;
  }

  public int getBarOverlap(){
    return barOverlap;
  }

  public void setBarOverlap(int barOverlap){
    this.barOverlap = barOverlap;
  }

  public int getGraphValueOne(){
    return graphValueOne;
  }

  public void setGraphValueOne(int graphValueOne){
    this.graphValueOne = graphValueOne;
  }

  public int getGraphValueTwo(){
    return graphValueTwo;
  }

  public void setGraphValueTwo(int graphValueTwo){
    this.graphValueTwo = graphValueTwo;
  }

  public int getGraphValueThree(){
    return graphValueThree;
  }

  public void setGraphValueThree(int graphValueThree){
    this.graphValueThree = graphValueThree;
  }

  public float getSphereScale(){
    return sphereScale;
  }

  public void setSphereScale(float sphereScale){
    this.sphereScale = sphereScale;
  }

  public float getBarScale(){
    return barScale;
  }

  public void setBarScale(float barScale){
    this.barScale = barScale;
  }

  public float getCylinderScale(){
    return cylinderScale;
  }

  public void setCylinderScale(float cylinderScale){
    this.cylinderScale = cylinderScale;
  }

  public Anchor getSphereAnchor(){
    return sphereAnchor;
  }

  public void setSphereAnchor(Anchor sphereAnchor){
    this.sphereAnchor = sphereAnchor;
  }

  public Anchor getCylinderAnchor(){
    return cylinderAnchor;
  }

  public void setCylinderAnchor(Anchor cylinderAnchor){
    this.cylinderAnchor = cylinderAnchor;
  }

  public Anchor getBarAnchor(){
    return barAnchor;
  }

  public void setBarAnchor(Anchor barAnchor){
    this.barAnchor = barAnchor;
  }

  public Dimension getSphereAdjust(){
    return sphereAdjust;
  }

  public void setSphereAdjust(Dimension sphereAdjust){
    this.sphereAdjust = sphereAdjust;
  }

  public Dimension getCylinderAdjust(){
    return cylinderAdjust;
  }

  public void setCylinderAdjust(Dimension cylinderAdjust){
    this.cylinderAdjust = cylinderAdjust;
  }

  public Dimension getBarAdjust(){
    return barAdjust;
  }

  public void setBarAdjust(Dimension barAdjust){
    this.barAdjust = barAdjust;
  }

  public Color getSeaColor(){
    return seaColor;
  }

  public void setSeaColor(Color seaColor){
    this.seaColor = seaColor;
  }

  public Color getSeaDarkColor(){
    return seaDarkColor;
  }

  public void setSeaDarkColor(Color seaDarkColor){
    this.seaDarkColor = seaDarkColor;
  }

  public File getSphereBean(){
    return sphereBean;
  }

  public void setSphereBean(File sphereBean){
    this.sphereBean = sphereBean;
  }

  public File getCylinderBean(){
    return cylinderBean;
  }

  public void setCylinderBean(File cylinderBean){
    this.cylinderBean = cylinderBean;
  }

  public File getBarBean(){
    return barBean;
  }

  public void setBarBean(File barBean){
    this.barBean = barBean;
  }

  public Color getSkyColor(){
    return skyColor;
  }

  public void setSkyColor(Color skyColor){
    this.skyColor = skyColor;
  }

  public float getRayAngle(){
    return rayAngle;
  }

  public void setRayAngle(float rayAngle){
    this.rayAngle = rayAngle;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public float getSkyRatio(){
    return skyRatio;
  }

  public void setSkyRatio(float skyRatio){
    this.skyRatio = skyRatio;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);

    //
    // Build base shapes used for the top part of this composition
    //

    // Sky & Sea
    skyRatio = skyRatio<0?-skyRatio:skyRatio;
    skyRatio %= 1;
    Rectangle skyShape = new Rectangle(0, 0, size.width, (int)(size.height*skyRatio));
    Rectangle seaShape = new Rectangle(0, skyShape.height, size.width, size.height - skyShape.height);
    
    // Sun
    Point sunCenter = new Point(size.width/2, skyShape.height);
    Rectangle sunBounds = new Rectangle(sunCenter.x-sunRadius,
				    sunCenter.y-sunRadius,
				    sunRadius*2, sunRadius*2);
    Shape sunShape = new Arc2D.Float(sunBounds.x, sunBounds.y, sunBounds.width, sunBounds.height,
				     0, 180, Arc2D.CHORD);
    
    // Sun Ray
    int rayWidth = (int)Point2D.distance(sunCenter.x, sunCenter.y, 0, 0);
    Point rayTopA = new Point(sunCenter.x - rayWidth, sunCenter.y);
    Point rayTopB = new Point(0, 0);
    float angle = (float)(rayAngle*Math.PI/180.0);
    AffineTransform rayRotate = AffineTransform.getRotateInstance(angle, sunCenter.x, sunCenter.y);
    rayRotate.transform(rayTopA, rayTopB);
    GeneralPath rayShape = new GeneralPath();
    rayShape.moveTo(sunCenter.x, sunCenter.y);
    rayShape.lineTo(rayTopA.x, rayTopA.y);
    rayShape.lineTo(rayTopB.x, rayTopB.y);
    rayShape.closePath();

    // Combine rays
    rayRotate.setToIdentity();
    GeneralPath sunRays = new GeneralPath();
    float rayRotateAngle = angle*2;
    int nRays = (int)Math.ceil(Math.PI/rayRotateAngle);
    for(int i=0; i<nRays; i++){
      sunRays.append(rayRotate.createTransformedShape(rayShape), false);
      rayRotate.rotate(rayRotateAngle, sunCenter.x, sunCenter.y);
    }
    
    //
    // Now, create a layer to fade out the sky (background) color.
    //
    GradientPaint skyPaint = new GradientPaint(0, 0, skyColor,
					       0, skyShape.height, backgroundColor);
    ShapeLayer skyLayer = new ShapeLayer(cmp, skyShape, new FillRenderer(skyPaint));

    //
    // Now, create layer with the sun rays
    //
    RadialGradientPaint rayPaint = new RadialGradientPaint( new Rectangle(0, 0, skyShape.width, skyShape.height*2),
							    backgroundColor, skyColor);
    ShapeLayer outsideRaysLayer = new ShapeLayer(cmp, sunRays, new FillRenderer(rayPaint));

    //
    // Now, build a Layer for the sea
    //
    GradientPaint seaPaint = new GradientPaint(seaShape.x, seaShape.y, seaColor,
					       seaShape.x, seaShape.y + seaShape.height, seaDarkColor);
    FillRenderer seaPainter = new FillRenderer(seaPaint);
    ShapeLayer seaLayer = new ShapeLayer(cmp, seaShape, seaPainter);

    //
    // Position the bean compositions on the sea
    //
    SphereComposition sphereFactory 
      = (SphereComposition)CompositionFactoryLoader.loadBeanFile(sphereBean);
    CylinderComposition cylinderFactory 
      = (CylinderComposition)CompositionFactoryLoader.loadBeanFile(cylinderBean);
    BarComposition barFactory 
      = (BarComposition)CompositionFactoryLoader.loadBeanFile(barBean);

    if(sphereFactory==null ||
       cylinderFactory==null ||
       barFactory==null)
      throw new IllegalArgumentException();
    
    //
    // Set the common shadow attributes
    //
    sphereFactory.setSphereShadowColor(shadowColor);
    sphereFactory.setShadowBlurRadius(shadowBlurRadius);
    cylinderFactory.setCylinderShadowColor(shadowColor);
    cylinderFactory.setShadowBlurRadius(shadowBlurRadius);
    barFactory.setBarShadowColor(shadowColor);
    barFactory.setShadowBlurRadius(shadowBlurRadius);

    //
    // Play a round-robin game with the colors of the 3 factories
    //
    Color shadowColors[] = {sphereFactory.getShadowColor(),
			   cylinderFactory.getShadowColor(),
			   barFactory.getShadowColor()};
    Color midtoneColors[] = {sphereFactory.getMidtoneColor(),
			   cylinderFactory.getMidtoneColor(),
			   barFactory.getMidtoneColor()};
    Color highlightColors[] = {sphereFactory.getHighlightColor(),
			   cylinderFactory.getHighlightColor(),
			   barFactory.getHighlightColor()};
    int graphValues[] = {graphValueOne, graphValueTwo, graphValueThree};

    Composition sphereCompositions[] = new Composition[3];
    Dimension sphereCompositionSize = new Dimension(0, 0);
    for(int i=0; i<3; i++){
      sphereFactory.setSphereSize(graphValues[i]);
      sphereFactory.setShadowColor(shadowColors[i]);
      sphereFactory.setMidtoneColor(midtoneColors[i]);
      sphereFactory.setHighlightColor(highlightColors[i]);
      sphereCompositions[i] = sphereFactory.build();
      
      sphereCompositionSize.width += sphereCompositions[i].getSize().width;
      sphereCompositionSize.width -= sphereOverlap;
      sphereCompositionSize.height = (int)Math.max(sphereCompositions[i].getSize().height, sphereCompositionSize.height);
    }
    sphereCompositionSize.width += sphereOverlap;

    LayerComposition sphereComposition = new LayerComposition(sphereCompositionSize);
    int hAdjust = 0;
    Layer sphereCompositionLayers[] = new Layer[3];
    for(int i=0; i<3; i++){
      sphereCompositionLayers[i] 
	= new CompositionProxyLayer(sphereComposition, sphereCompositions[i], 
				    new Position(Anchor.BOTTOM_LEFT, hAdjust, 0));
      hAdjust += sphereCompositions[i].getSize().width;
      hAdjust -= sphereOverlap;
    }
    sphereComposition.setLayers(sphereCompositionLayers);

    Composition cylinderCompositions[] = new Composition[3];
    Dimension cylinderCompositionSize = new Dimension(0, 0);
    Dimension cylinderSize = cylinderFactory.getCylinderSize();
    for(int i=0; i<3; i++){
      cylinderFactory.setCylinderSize(new Dimension(cylinderSize.width,
						    graphValues[i]));
      cylinderFactory.setShadowColor(shadowColors[(1+i)%3]);
      cylinderFactory.setMidtoneColor(midtoneColors[(1+i)%3]);
      cylinderFactory.setHighlightColor(highlightColors[(1+i)%3]);
      cylinderCompositions[i] = cylinderFactory.build();

      cylinderCompositionSize.width += cylinderCompositions[i].getSize().width;
      cylinderCompositionSize.width -= cylinderOverlap;
      cylinderCompositionSize.height = (int)Math.max(cylinderCompositions[i].getSize().height, cylinderCompositionSize.height);
    }
    cylinderCompositionSize.width += cylinderOverlap;
    LayerComposition cylinderComposition = new LayerComposition(cylinderCompositionSize);
    Layer cylinderCompositionLayers[] = new Layer[3];
    hAdjust = 0;
    for(int i=0; i<3; i++){
      cylinderCompositionLayers[i] 
	= new CompositionProxyLayer(cylinderComposition, cylinderCompositions[i], 
				    new Position(Anchor.BOTTOM_LEFT, hAdjust, 0));
      hAdjust += cylinderCompositions[i].getSize().width;
      hAdjust -= cylinderOverlap;
    }
    cylinderComposition.setLayers(cylinderCompositionLayers);


    Composition barCompositions[] = new Composition[3];
    Dimension barCompositionSize = new Dimension(0, 0);
    for(int i=0; i<3; i++){
      barFactory.setBarHeight(graphValues[i]);
      barFactory.setShadowColor(shadowColors[(2+i)%3]);
      barFactory.setMidtoneColor(midtoneColors[(2+i)%3]);
      barFactory.setHighlightColor(highlightColors[(2+i)%3]);
      barCompositions[i] = barFactory.build();

      barCompositionSize.width += barCompositions[i].getSize().width;
      barCompositionSize.width -= barOverlap;
      barCompositionSize.height = (int)Math.max(barCompositions[i].getSize().height, barCompositionSize.height);
    }
    barCompositionSize.width += barOverlap;
    LayerComposition barComposition = new LayerComposition(barCompositionSize);
    Layer barCompositionLayers[] = new Layer[3];
    hAdjust = 0;
    for(int i=0; i<3; i++){
      barCompositionLayers[i] 
	= new CompositionProxyLayer(barComposition, barCompositions[i],
				    new Position(Anchor.BOTTOM_LEFT, hAdjust, 0));
      hAdjust += barCompositions[i].getSize().width;
      hAdjust -= barOverlap;
    }
    barComposition.setLayers(barCompositionLayers);

    AffineTransform sphereScaler = AffineTransform.getScaleInstance(sphereScale, sphereScale);
    AffineTransform cylinderScaler = AffineTransform.getScaleInstance(cylinderScale, cylinderScale);
    AffineTransform barScaler = AffineTransform.getScaleInstance(barScale, barScale);

    Position spherePosition = new Position(sphereAnchor, sphereAdjust.width, sphereAdjust.height, sphereScaler, false);
    Position cylinderPosition = new Position(cylinderAnchor, cylinderAdjust.width, cylinderAdjust.height, cylinderScaler, false);
    Position barPosition = new Position(barAnchor, barAdjust.width, barAdjust.height, barScaler, false);
    
    Layer sphereLayer = new CompositionProxyLayer(cmp, sphereComposition, spherePosition);
    Layer cylinderLayer = new CompositionProxyLayer(cmp, cylinderComposition, cylinderPosition);
    Layer barLayer = new CompositionProxyLayer(cmp, barComposition, barPosition);	

    //
    // Build a layer for the sun
    // 
    Color sunColorCenter = highlightColors[0];
    Color sunColorMiddle = midtoneColors[0];
    Color sunColorOutside = shadowColors[0];
    RadialGradientPaintExt sunPaint = new RadialGradientPaintExt(sunBounds, 
							   new Color[]{sunColorCenter, sunColorMiddle, sunColorOutside},
							   new float[]{sphereFactory.getMidtoneInterval(),
									 sphereFactory.getShadowInterval() });
    RadialGradientPaintExt invertedSunPaint 
      = new RadialGradientPaintExt(sunBounds, 
				   new Color[]{sunColorOutside, sunColorMiddle, sunColorCenter},
				   new float[]{sphereFactory.getMidtoneInterval(),
						 sphereFactory.getShadowInterval() });

    FillRenderer sunPainter = new FillRenderer(sunPaint);
    FillRenderer invertedSunPainter = new FillRenderer(invertedSunPaint);

    ShapeLayer sunLayer = new ShapeLayer(cmp, sunShape, sunPainter);
    ShapeLayer insideRaysLayer = new ShapeLayer(cmp, sunRays, invertedSunPainter);
    insideRaysLayer.setLayerMask(sunShape);
 
    //
    // Finally, add text along a Shape
    //
    GeneralPath textPath = new GeneralPath();
    textPath.moveTo(sunCenter.x - 3*sunRadius/2f, sunCenter.y);
    textPath.curveTo(sunCenter.x - 3*sunRadius/2f, 
		     sunCenter.y - sunRadius,
		     sunCenter.x - sunRadius,
		     sunCenter.y - 3*sunRadius/2f,
		     sunCenter.x,
		     sunCenter.y - 3*sunRadius/2f);
    textPath.lineTo(size.width, sunCenter.y - 3*sunRadius/2f);
    
    TextStroke messageStroke = new TextStroke(message, messageFont, false, 0);
    StrokeRenderer messagePainter = new StrokeRenderer(messageColor, messageStroke);

    ShapeLayer messageLayer = new ShapeLayer(cmp, textPath, messagePainter);

    //
    // Add text shadow
    //
    StrokeRenderer shadowPainter = new StrokeRenderer(shadowColor, messageStroke);
    ShapeLayer messageShadowLayer = new ShapeLayer(cmp, textPath, shadowPainter);
    AffineTransform textPathFlipper = new AffineTransform();
    
    textPathFlipper.translate(0, skyShape.height);
    textPathFlipper.scale(1, -0.5);
    textPathFlipper.translate(0, -skyShape.height);

    messageShadowLayer.setTransform(textPathFlipper);

    if(shadowBlurRadius > 0){
      ConvolveOp blur = new ConvolveOp(new GaussianKernel(shadowBlurRadius));
      messageShadowLayer.setImageFilter(blur, 
					new Dimension(shadowBlurRadius*2 + messageFont.getSize(), 
						      shadowBlurRadius*2 + messageFont.getSize()));
    }

    //
    // Create a grid for the sea
    //
    Shape grid = makeSeaGrid(seaShape);

    ShapeLayer seaGrid = new ShapeLayer(cmp, grid, new StrokeRenderer(gridColor, gridStrokeWidth));
    
    cmp.setLayers(new Layer[]{ seaLayer, seaGrid, skyLayer,
				 outsideRaysLayer,
				 sunLayer, insideRaysLayer, 
				 messageLayer, messageShadowLayer, 
				 cylinderLayer, barLayer, sphereLayer
				 });
    
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }

  private Shape makeSeaGrid(Rectangle seaShape){
    int w = seaShape.width, h =seaShape.height;
    int x = seaShape.x, y = seaShape.y;

    GeneralPath grid = new GeneralPath();

    // Add line to the center
    grid.moveTo(w/2, y);
    grid.lineTo(w/2, y + w);

    // Now, add vertical lines to the right of the center vertical line
    int n = (int)Math.rint(w/(2*gridTopCellWidth));
    for(int i=1; i<=n; i++){
      grid.moveTo(w/2 + i*gridTopCellWidth, y);
      grid.lineTo(w/2 + i*gridBottomCellWidth, y + h);
    }

    // Add vertical lines to the left of the center vertical line
    for(int i=1; i<=n; i++){
      grid.moveTo(w/2 - i*gridTopCellWidth, y);
      grid.lineTo(w/2 - i*gridBottomCellWidth, y + h);
    }

    // Add horizontal lines
    float curY = y;
    float gridWidth = 0;
    while(curY < y + h){
      grid.moveTo(0, curY);
      grid.lineTo(w, curY);

      // Move one cell down
      gridWidth = gridTopCellWidth + ((gridBottomCellWidth - gridTopCellWidth)/(float)h)*(curY-y);
      curY += gridWidth;
    }

    return grid;
  }
}
