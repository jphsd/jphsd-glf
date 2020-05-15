/*
 * @(#)TextRecessedShadowComposition.java
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
 * This examples illustrates how to create a recessed shadow effect on an 
 * string of text. 
 *
 * @author         Vincent Hardy
 * @version        1.0, 06/03/1999
 */
public class TextRecessedShadowComposition implements CompositionFactory{
  String label = "GO!";

  Font labelFont = new Font("Impact", Font.BOLD, 120);

  Dimension shadowOffset = new Dimension(10, 10);

  Color labelColor = new Color(200, 200, 120);

  Color shadowColor = new Color(0, 0, 0, 128);

  Color rimLightColor = Color.white;

  Color rimDarkColor = Color.black;

  float rimWidth = 1;

  int margin = 20;

  int shadowBlurRadius = 6;

  Color backgroundColor = new Color(255, 255, 255, 0); 

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public int getShadowBlurRadius(){
    return shadowBlurRadius;
  }

  public void setShadowBlurRadius(int shadowBlurRadius){
    this.shadowBlurRadius = shadowBlurRadius;
  }

  public float getRimWidth(){
    return rimWidth;
  }

  public void setRimWidth(float rimWidth){
    this.rimWidth = rimWidth;
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

  public Dimension getShadowOffset(){
    return shadowOffset;
  }

  public void setShadowOffset(Dimension shadowOffset){
    this.shadowOffset = shadowOffset;
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

  public Color getRimLightColor(){
    return rimLightColor;
  }

  public void setRimLightColor(Color rimLightColor){
    this.rimLightColor = rimLightColor;
  }

  public Color getRimDarkColor(){
    return rimDarkColor;
  }

  public void setRimDarkColor(Color rimDarkColor){
    this.rimDarkColor = rimDarkColor;
  }
  
  public Composition build(){
    //
    // First, get the label Shape
    //
    Shape labelShape = TextLayer.makeTextBlock(label, labelFont);
    Rectangle labelShapeBounds = labelShape.getBounds();

    //
    // Process the composition size based on the labelShape bounds
    //
    Dimension size = new Dimension(labelShapeBounds.width + 2*margin,
				   labelShapeBounds.height + 2*margin);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Center labelShape in composition
    //
    labelShape = Position.CENTER.createTransformedShape(labelShape, cmp.getBounds());

    //
    // Create the 'inverted' labelShape: we will use it for the shadow
    //
    Area cutOutLabelShape = new Area(cmp.getBounds());
    cutOutLabelShape.subtract(new Area(labelShape));

    //
    // Lowest layer is the colored text
    //
    ShapeLayer labelLayer = new ShapeLayer(cmp, labelShape, new FillRenderer(labelColor));

    //
    // Shadow layer
    //
    ShapeLayer labelShadowLayer = new ShapeLayer(cmp, cutOutLabelShape, new FillRenderer(shadowColor));
    Kernel blurKernel = new GaussianKernel(shadowBlurRadius);
    ConvolveOp blur = new ConvolveOp(blurKernel);
    Dimension blurMargins = new Dimension(shadowBlurRadius*2, shadowBlurRadius*2);
    labelShadowLayer.setImageFilter(blur, blurMargins);
    labelShadowLayer.setLayerMask(labelShape);
    AffineTransform shadowShiftTxf 
      = AffineTransform.getTranslateInstance(shadowOffset.width,
					     shadowOffset.height);
    labelShadowLayer.setTransform(shadowShiftTxf);

    //
    // Hightlight rim
    //
    StrokeRenderer darkSideStroke = new StrokeRenderer(rimDarkColor, rimWidth);
    StrokeRenderer lightSideStroke = new StrokeRenderer(rimLightColor, rimWidth);
    Layer rimDarkSide = new ShapeLayer(cmp, labelShape, darkSideStroke);
    Layer rimLightSide = new ShapeLayer(cmp, labelShape, lightSideStroke);

    AffineTransform t = AffineTransform.getTranslateInstance(-rimWidth/2f, -rimWidth/2f);
    rimDarkSide.setLayerMask(t.createTransformedShape(labelShape));
    
    t.setToTranslation(rimWidth, rimWidth);
    rimLightSide.setLayerMask(t.createTransformedShape(labelShape));

    //
    // Pile up layers
    //
    cmp.setLayers(new Layer[]{ labelLayer, labelShadowLayer,
				 rimDarkSide, rimLightSide });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);

    return cmp;
    
  }
}
