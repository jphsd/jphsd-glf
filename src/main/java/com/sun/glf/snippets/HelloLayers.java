/*
 * @(#)HelloLayers.java
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
import java.awt.geom.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;


/**
 * The "Hello World" for the Graphic Layer Framework.
 * Illustrates the stacking of layers.
 *
 * @author        Vincent Hardy 
 * @version       10/27/1998
 */
public class HelloLayers implements CompositionFactory{
  String text = "Hello Layers";
  Font textFont = new Font("serif", Font.ITALIC, 90);
  Color textColor = Color.white;
  Color shadowColor = new Color(0, 0, 0, 128); // Half transparent black
  Dimension size = new Dimension(400, 400);
  Color backgroundColorCenter = new Color(255, 234, 50); // Yellow
  Color backgroundColorOutside = new Color(153, 50, 0); // Dark red
  int shadowOffsetX = 7, shadowOffsetY = 5;

  public int getShadowOffsetX(){
    return shadowOffsetX;
  }

  public void setShadowOffsetX(int shadowOffsetX){
    this.shadowOffsetX = shadowOffsetX;
  }

  public int getShadowOffsetY(){
    return shadowOffsetY;
  }

  public void setShadowOffsetY(int shadowOffsetY){
    this.shadowOffsetY = shadowOffsetY;
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

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Color getShadowColor(){
    return shadowColor;
  }

  public void setShadowColor(Color shadowColor){
    this.shadowColor = shadowColor;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Color getBackgroundColorCenter(){
    return backgroundColorCenter;
  }

  public void setBackgroundColorCenter(Color backgroundColorCenter){
    this.backgroundColorCenter = backgroundColorCenter;
  }
  
  public Color getBackgroundColorOutside(){
    return backgroundColorOutside;
  }

  public void setBackgroundColorOutside(Color backgroundColorOutside){
    this.backgroundColorOutside = backgroundColorOutside;
  }
  
  //
  // In this example, we stack three layers:
  // + a background ShapeLayer that covers the entire composition
  //   and is filled with a RadialGradientPaint, one custom Paint extention
  // + a TextLayer, centered into the composition, that displays a text string
  // + a ShapeLayer that displays the text's shadow
  //
  // The layers are stacked in the following order, from the bottom:
  // a. Background layer
  // b. Shadow layer
  // c. TextLayer
  //
  public Composition build(){
    // Create a layer composition where layers 
    // will be stacked.
    LayerComposition cmp = new LayerComposition(size);

    //
    // Create the background layer consisting of 
    // a Rectangle filled with a RadialGradientPaint,
    // one of our extentions to the 2D API
    //

    // Shape that the ShapeLayer will render.
    Rectangle rect = new Rectangle(0, 0, size.width, size.height);

    // Paint to use for filling : radial gradient from 
    // backgroundColorCenter at the to to backgroundColorOutside at
    // the rim.
    // The center of the radial gradient is at the upper left corner
    // of the composition
    Rectangle gradientRect = new Rectangle(-rect.width, -rect.height,
					   2*rect.width, 2*rect.height);
    com.sun.glf.goodies.RadialGradientPaint filling 
      = new com.sun.glf.goodies.RadialGradientPaint(gradientRect, 
				backgroundColorCenter,
				backgroundColorOutside);

    Renderer rectRenderer = new FillRenderer(filling);
    ShapeLayer backgroundLayer = new ShapeLayer(cmp, rect, rectRenderer);

    //
    // Create a layer for the text
    //
    TextLayer textLayer = new TextLayer(cmp,
					text,
					textFont,
					new FillRenderer(textColor));

    //
    // Create a layer for the text shadow. The shadow is renderered by filling
    // the text block shape with shadowColor and drawing it offset.
    //
    Shape textBlock = textLayer.createTransformedShape();
    Renderer shadowRenderer = new FillRenderer(shadowColor);
    ShapeLayer shadowLayer = new ShapeLayer(cmp, textBlock, shadowRenderer);
    shadowLayer.setTransform(AffineTransform.getTranslateInstance(shadowOffsetX, shadowOffsetY));

    /* 
       ShapeLayer textOutline = new ShapeLayer(cmp, textBlock,
					    new StrokeRenderer(Color.black,
							       new BasicStroke(3, BasicStroke.CAP_BUTT,
									       BasicStroke.JOIN_ROUND,
									       1,
									       new float[] { 3, 3 },
									       0)));*/

    // 
    // Stack layers in composition
    //
    cmp.setLayers(new Layer[] { backgroundLayer, shadowLayer, textLayer});

    // Set rendering hints for the whole composition
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);

    // Return the composition we have built
    return cmp;
  }
}
