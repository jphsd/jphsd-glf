/*
 * @(#)SunSet.java
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
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Illustrates the use of the RadialGradientPaintExt class to create a sun set 
 * effect. This example also use another kind of non-affine geometrical transformation
 * as also illustrated in the TextWave demo program.
 *
 * @author           Vincent Hardy
 * @version          1.0, 11/11/1998
 */
public class SunSet implements CompositionFactory{
  /** Composition's size */
  Dimension size = new Dimension(400, 400);
  
  /*
   * Sky colors
   */
  Color skyColor1 = new Color(240, 100, 100);
  Color skyColor2 = new Color(250, 200, 100);
  Color skyColor3 = new Color(250, 250, 200);
  Color skyColor4 = new Color(180, 160, 255);
  Color skyColor5 = new Color(20, 20, 120);

  /*
   * Sun Set control
   */
  int sunDiameter = 10;
  Color sunSetColor1 = new Color(250, 250, 200);
  Color sunSetColor2 = new Color(250, 200, 100);
  Color sunSetColor3 = new Color(240, 100, 100);
  Color sunSetColor4 = new Color(255, 255, 255, 0); // Transparent white

  /**
   * Land Scape Color
   */
  Color seaHorizonColor = new Color(5, 40, 40);
  Color seaColor = new Color(50, 100, 100);
  String seaText = "... Creating a sunset with Gradient Paints is really fun!! ...";
  Font seaTextFont = new Font("serif", Font.PLAIN, 30);
  Color seaTextColor = new Color(128, 0, 0);
  float seaTextBumpArc = (float)Math.PI;
  
  int seaTextAdjustment = 20;

  Color seaTextSeparationColor = Color.white;

  public Color getSeaTextSeparationColor(){
    return seaTextSeparationColor;
  }

  public void setSeaTextSeparationColor(Color seaTextSeparationColor){
    this.seaTextSeparationColor = seaTextSeparationColor;
  }

  public int getSeaTextAdjustment(){
    return seaTextAdjustment;
  }

  public void setSeaTextAdjustment(int seaTextAdjustment){
    this.seaTextAdjustment = seaTextAdjustment;
  }

  public Color getSeaHorizonColor(){
    return seaHorizonColor;
  }

  public void setSeaHorizonColor(Color seaHorizonColor){
    this.seaHorizonColor = seaHorizonColor;
  }

  public Color getSeaColor(){
    return seaColor;
  }

  public void setSeaColor(Color seaColor){
    this.seaColor = seaColor;
  }

  public String getSeaText(){
    return seaText;
  }

  public void setSeaText(String seaText){
    this.seaText = seaText;
  }

  public Font getSeaTextFont(){
    return seaTextFont;
  }

  public void setSeaTextFont(Font seaTextFont){
    this.seaTextFont = seaTextFont;
  }

  public float getSeaTextBumpArc(){
    return seaTextBumpArc;
  }

  public void setSeaTextBumpArc(float seaTextBumpArc){
    this.seaTextBumpArc = seaTextBumpArc;
  }

  public Color getSeaTextColor(){
    return seaTextColor;
  }

  public void setSeaTextColor(Color seaTextColor){
    this.seaTextColor = seaTextColor;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Color getSkyColor1(){
    return skyColor1;
  }

  public void setSkyColor1(Color skyColor1){
    this.skyColor1 = skyColor1;
  }

  public Color getSkyColor2(){
    return skyColor2;
  }

  public void setSkyColor2(Color skyColor2){
    this.skyColor2 = skyColor2;
  }

  public Color getSkyColor3(){
    return skyColor3;
  }

  public void setSkyColor3(Color skyColor3){
    this.skyColor3 = skyColor3;
  }

  public Color getSkyColor4(){
    return skyColor4;
  }

  public void setSkyColor4(Color skyColor4){
    this.skyColor4 = skyColor4;
  }

  public Color getSkyColor5(){
    return skyColor5;
  }

  public void setSkyColor5(Color skyColor5){
    this.skyColor5 = skyColor5;
  }

  public int getSunDiameter(){
    return sunDiameter;
  }

  public void setSunDiameter(int sunDiameter){
    this.sunDiameter = sunDiameter;
  }

  public Color getSunSetColor1(){
    return sunSetColor1;
  }

  public void setSunSetColor1(Color sunSetColor1){
    this.sunSetColor1 = sunSetColor1;
  }

  public Color getSunSetColor2(){
    return sunSetColor2;
  }

  public void setSunSetColor2(Color sunSetColor2){
    this.sunSetColor2 = sunSetColor2;
  }

  public Color getSunSetColor3(){
    return sunSetColor3;
  }

  public void setSunSetColor3(Color sunSetColor3){
    this.sunSetColor3 = sunSetColor3;
  }

  public Color getSunSetColor4(){
    return sunSetColor4;
  }

  public void setSunSetColor4(Color sunSetColor4){
    this.sunSetColor4 = sunSetColor4;
  }

  public Composition build(){
    LayerComposition cmp = new LayerComposition(size);
    
    //
    // Sky Layer : upper rectangle rendered with a GradientPaintExt. The paint uses
    // 5 colors which are using increasingly large parts of the sky. Proportionally, the
    // gradient on the horizon uses 1 part, the second gradient 2 parts, the 3rd gradient 3 parts
    // and the 4th gradient 4 parts. This makes a total of 10 parts. 
    //
    float skyRatio = 1.5f;
    Rectangle skyRect = new Rectangle(0, 0, size.width, (int)(size.height/skyRatio));
    float skySlice = skyRect.height/10f;
    GradientPaintExt skyPaint = new GradientPaintExt(0, skyRect.height, // start on the horizon line
							 0, 0, // Direction : upward, i.e. negative side of the y axis
							 new float[]{skySlice, 2*skySlice, 3*skySlice, 4*skySlice},
							 new Color[]{skyColor1, skyColor2, skyColor3, skyColor4, skyColor5});
    ShapeLayer skyLayer = new ShapeLayer(cmp, skyRect, new FillRenderer(skyPaint));

    //
    // Sun Set layer : the 'sun' is created with a radial gradient paint using multiple colors
    //
    
    // The sun is painted with 4 colors of increasing large size. The core is considered
    // to be the reference width. The second gradient is twice as big and the third 3 times
    // as big. This makes a total of 3 times the sun core width.
    int sunGlowWidth = 3*sunDiameter;

    // Sun core bounds: the Sun is resting on the horizon, only its upper half showing, and
    // on the right hand side.
    int sunTotalWidth = sunDiameter + 2*sunGlowWidth;
    Rectangle sunInnerBounds = new Rectangle(skyRect.x + skyRect.width/2 - sunDiameter/2, 
					     skyRect.y + skyRect.height - sunDiameter/2,
					     sunDiameter,
					     sunDiameter);

    float sunSlice = sunGlowWidth/3f;
    RadialGradientPaintExt sunSetPaint = new RadialGradientPaintExt(sunInnerBounds, 
								    new Color[]{sunSetColor1,sunSetColor2, sunSetColor3, sunSetColor4},
								    new float[]{4*sunSlice, 3*sunSlice, sunSlice});
    Rectangle sunOuterBounds = new Rectangle(sunInnerBounds.x - sunGlowWidth,
					     sunInnerBounds.y - sunGlowWidth,
					     sunInnerBounds.width + 2*sunGlowWidth,
					     sunInnerBounds.height + 2*sunGlowWidth);
    ShapeLayer sunSet = new ShapeLayer(cmp, skyRect, new FillRenderer(sunSetPaint));
							

    //
    // sea layer
    //
    Rectangle seaRect = new Rectangle(0, skyRect.height, 
					    size.width,
					    size.height-skyRect.height);
    GradientPaint seaPaint = new GradientPaint(0, seaRect.y, seaHorizonColor,
						     0, size.height, seaColor);
    ShapeLayer seaLayer = new ShapeLayer(cmp, seaRect, new FillRenderer(seaPaint));
    
    //
    // sea text layer
    //
    Shape bumpedShape = TextLayer.makeTextBlock(seaText, seaTextFont, Integer.MAX_VALUE, TextAlignment.CENTER);
    Transform bumpTransform = new BumpTransform(seaTextBumpArc);
    bumpedShape = bumpTransform.transform(bumpedShape);
    ShapeLayer seaTextLayer = new ShapeLayer(cmp, bumpedShape, 
					     new FillRenderer(seaTextColor), 
					     new Position(Anchor.BOTTOM, 0, seaTextAdjustment));

    
    //
    // Add a 'separation' layer, i.e. a layer which will contrast with
    // the text color so that it remains readable on all the parts of the gradient
    //
    ShapeLayer seaTextSeparationLayer = new ShapeLayer(cmp, 
						       seaTextLayer.createTransformedShape(),
						       new FillRenderer(seaTextSeparationColor));
    seaTextSeparationLayer.setTransform(AffineTransform.getTranslateInstance(1,1));
		
    //
    // sea shadow layer
    //
    ShapeLayer seaTextShadowLayer = new ShapeLayer(cmp, 
						   seaTextLayer.createTransformedShape(),
						   new FillRenderer(new Color(0, 0, 0, 128)));
    Rectangle textBounds = seaTextLayer.createTransformedShape().getBounds();
    AffineTransform shadowTransform = new AffineTransform();
    shadowTransform.translate(textBounds.x, textBounds.y + textBounds.height);
    shadowTransform.scale(1, -.3);
    shadowTransform.translate(-textBounds.x, -textBounds.y -textBounds.height);
    seaTextShadowLayer.setTransform(shadowTransform);

					      
    cmp.setLayers(new Layer[]{skyLayer, 
				sunSet, 
				seaLayer,
				seaTextShadowLayer, 
				seaTextSeparationLayer,
				seaTextLayer});

    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    return cmp;

  }
}

