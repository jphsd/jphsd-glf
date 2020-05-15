/*
 * @(#)CircularLayoutComposition.java
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
import java.text.*;
import java.awt.font.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This example illustrates how to layout text into Circular boundaries.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/08/1999
 */
public class CircularLayoutComposition implements CompositionFactory{
  String text = "I am having fun with TextLayout";

  Font textFont = new Font("Impact MT", Font.PLAIN, 20);

  Color textColor = Color.orange;

  Color backgroundColor = Color.black;

  int shapeMargin = 5;

  int textMargin = 5;

  int blockWidth = 250;

  float shapeStrokeWidth = 3;

  public float getShapeStrokeWidth(){
    return shapeStrokeWidth;
  }

  public void setShapeStrokeWidth(float shapeStrokeWidth){
    this.shapeStrokeWidth = shapeStrokeWidth;
  }

  public int getBlockWidth(){
    return blockWidth;
  }

  public void setBlockWidth(int blockWidth){
    this.blockWidth = blockWidth;
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

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public int getShapeMargin(){
    return shapeMargin;
  }

  public void setShapeMargin(int shapeMargin){
    this.shapeMargin = shapeMargin;
  }

  public int getTextMargin(){
    return textMargin;
  }

  public void setTextMargin(int textMargin){
    this.textMargin = textMargin;
  }

  public Composition build(){
    //
    // Build LayerComposition
    //
    Dimension size = new Dimension(blockWidth, blockWidth);
    LayerComposition cmp = new LayerComposition(size);

    //
    // Create text block 'manually'
    //
    float w = blockWidth - 2*shapeMargin -2*textMargin;
    float h = w;
    float r = h/2; // radius
    FontRenderContext frc = new FontRenderContext(null, true, true);
    AttributedString styledString = new AttributedString(text);
    styledString.addAttribute(TextAttribute.FONT, textFont);
    AttributedCharacterIterator iter = styledString.getIterator();
    LineBreakMeasurer measurer = new LineBreakMeasurer(iter, frc);

    TextLayout layout = null;
    int limit = iter.getEndIndex();
    int nChar=0, start=0;
    float curH = 0, wrapWidthTop=0, wrapWidthBottom=0, wrapWidth = 0;
    AffineTransform t = new AffineTransform();
    Shape lineShape = null;
    GeneralPath textBlock = new GeneralPath();
    float theta;
    LineMetrics lm = textFont.getLineMetrics("The quick brown fox", frc);
    float ascent = lm.getAscent(), advance=0;
    AffineTransform adjuster = new AffineTransform();

    while (measurer.getPosition() < limit) { // While the string is not exhausted
      // Theta is the angle between the vertical axis and the line from the disc center
      // to the intersection of the line at elevation curH with the disc.
      theta = (float)Math.acos((r-curH)/r);
      wrapWidthBottom = 2*(r-curH)*(float)Math.tan(theta);

      if(curH>ascent){
	theta = (float)Math.acos((r-curH + ascent)/r);
	wrapWidthTop = 2*(r-curH + ascent)*(float)Math.tan(theta);
      }else
	wrapWidthTop = 0;

      wrapWidth = wrapWidthTop<wrapWidthBottom?wrapWidthTop:wrapWidthBottom;

      layout = measurer.nextLayout(wrapWidth, iter.getEndIndex(), true);

      if(layout != null){
	layout = layout.getJustifiedLayout(wrapWidth);

	// Place line in bounding circle
	advance = layout.getVisibleAdvance();
	t.setToTranslation(r - advance/2, curH);

	// Convert line to Shape
	lineShape = layout.getOutline(t);// adjuster);
	
	// Concatenate with textBlock
	textBlock.append(lineShape, false);

	curH += layout.getAscent() + layout.getDescent() + layout.getLeading();
      }
      else
	// Could not fit a single word on the line. Move down a bit
	curH += ascent/2;
	
      if(curH>h)
	break;
    }

    
    //
    // Create shape based layers
    //
    Renderer backgroundRenderer = new FillRenderer(backgroundColor);
    Renderer shapeRenderer = new StrokeRenderer(textColor, shapeStrokeWidth);
    Renderer textRenderer = new FillRenderer(textColor);
    Rectangle baseSquare = new Rectangle(0, 0, blockWidth, blockWidth);
    Shape circle = new Ellipse2D.Float(0, 0, blockWidth-shapeMargin*2, blockWidth-shapeMargin*2);
    Shape textCircle = new Ellipse2D.Float(0, 0, w, h);

    cmp.setBackgroundPaint(backgroundColor);
    ShapeLayer circleLayer = new ShapeLayer(cmp, circle, shapeRenderer, Position.CENTER);
    ShapeLayer textLayer = new ShapeLayer(cmp, textBlock, textRenderer, Position.CENTER);
    // textLayer.setTransform(Position.CENTER.getTransform(textCircle, cmp.getBounds()));

    ShapeLayer circleControlLayer = new ShapeLayer(cmp, textCircle, new StrokeRenderer(Color.red, 3), Position.CENTER);

    // Stack up layers
    cmp.setLayers(new Layer[]{ circleLayer, textLayer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;				    
  }
}

