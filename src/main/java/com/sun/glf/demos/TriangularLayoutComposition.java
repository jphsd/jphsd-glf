/*
 * @(#)TriangularLayoutComposition.java
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
 * This example illustrates how to layout text into triangular boundaries.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/08/1999
 */
public class TriangularLayoutComposition implements CompositionFactory{
  String text = "I am having fun with TextLayout";

  Font textFont = new Font("Impact MT", Font.PLAIN, 20);

  Color textColor = Color.orange;

  Color backgroundColor = Color.black;

  int shapeMargin = 5;

  int textMargin = 5;

  int blockWidth = 250;

  boolean tipOnTop = true;

  float shapeStrokeWidth = 3;

  public float getShapeStrokeWidth(){
    return shapeStrokeWidth;
  }

  public void setShapeStrokeWidth(float shapeStrokeWidth){
    this.shapeStrokeWidth = shapeStrokeWidth;
  }

  public boolean getTipOnTop(){
    return tipOnTop;
  }

  public void setTipOnTop(boolean tipOnTop){
    this.tipOnTop = tipOnTop;
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
    FontRenderContext frc = new FontRenderContext(null, true, true);
    AttributedString styledString = new AttributedString(text);
    styledString.addAttribute(TextAttribute.FONT, textFont);
    AttributedCharacterIterator iter = styledString.getIterator();
    LineBreakMeasurer measurer = new LineBreakMeasurer(iter, frc);

    LineMetrics lm = textFont.getLineMetrics("The quick brown fox", frc);
    float ascent = lm.getAscent();

    TextLayout layout = null;
    int limit = iter.getEndIndex();
    int nChar=0, start=0;
    float curH = 0, wrapWidth = 0;
    AffineTransform t = new AffineTransform();
    Shape lineShape = null;
    float slope = w/h;
    GeneralPath textBlock = new GeneralPath();
    AffineTransform adjuster = new AffineTransform();
    float advance = 0;

    if(tipOnTop){
      while (measurer.getPosition() < limit) { // While the string is not exhausted
	wrapWidth = curH*slope; 
	
	if(wrapWidth>0)
	  layout = measurer.nextLayout(wrapWidth);
	else
	  layout = null;
	
	if(layout != null){
	  layout = layout.getJustifiedLayout(wrapWidth);
	  
	  advance = layout.getVisibleAdvance();
	  t.setToTranslation(-advance/2f, curH + layout.getAscent());
	  lineShape = layout.getOutline(t); // adjuster);
	  textBlock.append(lineShape, false);
	  
	  curH += layout.getAscent() + layout.getDescent() + layout.getLeading();
	}
	else{
	  System.out.println("null layout");
	  curH += ascent/2f;
	  t.translate(-ascent*slope/4f, ascent/2f); 
	}
	
	if(curH+ascent>h)
	  break;
      }
    }
    else{
      t.translate(-w/2, 0);

      while (measurer.getPosition() < limit) { // While the string is not exhausted
	wrapWidth = (h-curH-ascent)*slope;
	
	if(wrapWidth>0)
	  layout = measurer.nextLayout(wrapWidth);
	else
	  layout = null;
	
	if(layout != null){
	  layout = layout.getJustifiedLayout(wrapWidth);
	  t.translate(layout.getAscent()*slope/2, layout.getAscent());
	  
	  advance = layout.getVisibleAdvance();
	  adjuster.setToTranslation((wrapWidth-advance)/2, 0);
	  adjuster.concatenate(t);
	  lineShape = layout.getOutline(adjuster);
	  textBlock.append(lineShape, false);
	  
	  t.translate((layout.getDescent() + layout.getLeading())*slope/2, layout.getDescent() + layout.getLeading());
	  curH += layout.getAscent() + layout.getDescent() + layout.getLeading();
	}
	else{
	  System.out.println("null layout");
	  curH += ascent/2f;
	  t.translate(ascent*slope/4f, ascent/2f); 
	}
	
	if(curH+ascent>h)
	  break;
      }

    }


    //
    // Create shape based layers
    //
    Renderer backgroundRenderer = new FillRenderer(backgroundColor);
    Renderer textRenderer = new FillRenderer(textColor);
    Renderer shapeRenderer = new StrokeRenderer(textColor, shapeStrokeWidth);
    Rectangle baseSquare = new Rectangle(0, 0, blockWidth, blockWidth);

    GeneralPath triangle = new GeneralPath();
    GeneralPath textTriangle = new GeneralPath();

    if(tipOnTop){
      triangle.moveTo(blockWidth/2f, shapeMargin);
      triangle.lineTo(blockWidth-shapeMargin, blockWidth - shapeMargin);
      triangle.lineTo(shapeMargin, blockWidth - shapeMargin);
      triangle.closePath();

      textTriangle.moveTo(0, 0);
      textTriangle.lineTo(w/2, h);
      textTriangle.lineTo(-w/2, h);
      textTriangle.closePath();
    }
    else{
      triangle.moveTo(shapeMargin, shapeMargin);
      triangle.lineTo(blockWidth-shapeMargin, shapeMargin);
      triangle.lineTo(blockWidth/2, blockWidth - shapeMargin);
      triangle.closePath();

      textTriangle.moveTo(-w/2, 0);
      textTriangle.lineTo(w/2, 0);
      textTriangle.lineTo(0, h);
      textTriangle.closePath();
    }

    ShapeLayer squareBlock = new ShapeLayer(cmp, baseSquare, backgroundRenderer, Position.TOP_LEFT);
    ShapeLayer triangleLayer = new ShapeLayer(cmp, triangle, shapeRenderer, Position.CENTER);
    ShapeLayer textLayer = new ShapeLayer(cmp, textBlock, textRenderer);
    textLayer.setTransform(Position.CENTER.getTransform(textTriangle, cmp.getBounds()));

    ShapeLayer triangleControlLayer = new ShapeLayer(cmp, textTriangle, new StrokeRenderer(Color.red, 3), Position.CENTER);

    // Stack up layers
    cmp.setLayers(new Layer[]{ squareBlock, triangleLayer, textLayer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;				    
  }
}

