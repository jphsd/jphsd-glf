/*
 * @(#)GlyphDecorationComposition.java
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
 * This example illustrates how to make Shapes stand out well against 
 * any given background.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/09/1999
 */
public class GlyphDecorationComposition implements CompositionFactory{
  Glyph glyph = new Glyph(new Font("Impact MT", Font.PLAIN, 200), '1');

  Glyph glyphBackground = new Glyph(new Font("Impact MT", Font.PLAIN, 800), '.');

  int margin = 5;

  Color backgroundColor = Color.black;

  Color symbolStrokeOneColor = Color.white;

  float symbolStrokeOneWidth = 2;

  Color symbolStrokeTwoColor = Color.orange;

  float symbolStrokeTwoWidth = 4;

  Color symbolStrokeThreeColor = Color.yellow;

  float symbolStrokeThreeWidth = 8;

  Color symbolStrokeFourColor = Color.white;

  float symbolStrokeFourWidth = 12;

  Color symbolFillColor = Color.black;

  /*
  int capStyle = 1;

  int joinStyle = 1;

  public int getCapStyle(){
    return capStyle;
  }

  public void setCapStyle(int capStyle){
    this.capStyle = capStyle;
  }

  public int getJoinStyle(){
    return joinStyle;
  }

  public void setJoinStyle(int joinStyle){
    this.joinStyle = joinStyle;
  }*/

  public Color getSymbolFillColor(){
    return symbolFillColor;
  }

  public void setSymbolFillColor(Color symbolFillColor){
    this.symbolFillColor = symbolFillColor;
  }

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

  public Glyph getGlyphBackground(){
    return glyphBackground;
  }

  public void setGlyphBackground(Glyph glyphBackground){
    this.glyphBackground = glyphBackground;
  }

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Color getSymbolStrokeOneColor(){
    return symbolStrokeOneColor;
  }

  public void setSymbolStrokeOneColor(Color symbolStrokeOneColor){
    this.symbolStrokeOneColor = symbolStrokeOneColor;
  }

  public float getSymbolStrokeOneWidth(){
    return symbolStrokeOneWidth;
  }

  public void setSymbolStrokeOneWidth(float symbolStrokeOneWidth){
    this.symbolStrokeOneWidth = symbolStrokeOneWidth;
  }

  public Color getSymbolStrokeTwoColor(){
    return symbolStrokeTwoColor;
  }

  public void setSymbolStrokeTwoColor(Color symbolStrokeTwoColor){
    this.symbolStrokeTwoColor = symbolStrokeTwoColor;
  }

  public float getSymbolStrokeTwoWidth(){
    return symbolStrokeTwoWidth;
  }

  public void setSymbolStrokeTwoWidth(float symbolStrokeTwoWidth){
    this.symbolStrokeTwoWidth = symbolStrokeTwoWidth;
  }

  public Color getSymbolStrokeThreeColor(){
    return symbolStrokeThreeColor;
  }

  public void setSymbolStrokeThreeColor(Color symbolStrokeThreeColor){
    this.symbolStrokeThreeColor = symbolStrokeThreeColor;
  }

  public float getSymbolStrokeThreeWidth(){
    return symbolStrokeThreeWidth;
  }

  public void setSymbolStrokeThreeWidth(float symbolStrokeThreeWidth){
    this.symbolStrokeThreeWidth = symbolStrokeThreeWidth;
  }

  public Color getSymbolStrokeFourColor(){
    return symbolStrokeFourColor;
  }

  public void setSymbolStrokeFourColor(Color symbolStrokeFourColor){
    this.symbolStrokeFourColor = symbolStrokeFourColor;
  }

  public float getSymbolStrokeFourWidth(){
    return symbolStrokeFourWidth;
  }

  public void setSymbolStrokeFourWidth(float symbolStrokeFourWidth){
    this.symbolStrokeFourWidth = symbolStrokeFourWidth;
  }

  public Composition build(){
    //
    // Get Glyph Shape and background
    //
    Shape symbol = glyph.getShape();
    Shape symbolBackground = glyphBackground.getShape();
    
    //
    // Process size based on biggest Shape and margin
    //
    Rectangle symbolBounds = symbol.getBounds();
    Rectangle symbolBkgBounds = symbolBackground.getBounds();
    Dimension size 
      = new Dimension((int)Math.max(symbolBounds.width, symbolBkgBounds.width) + 2*margin,
		      (int)Math.max(symbolBounds.height, symbolBkgBounds.height) + 2*margin);
    
    LayerComposition cmp = new LayerComposition(size);

    //
    // Create the different renderers for the symbol and its background
    //
    FillRenderer bkgPainter = new FillRenderer(backgroundColor);
    FillRenderer symbolFill = new FillRenderer(symbolFillColor);
    int cap = BasicStroke.CAP_ROUND;
    int join = BasicStroke.JOIN_ROUND;
    BasicStroke strokeOne = new BasicStroke(symbolStrokeOneWidth, cap, join);
    BasicStroke strokeTwo = new BasicStroke(symbolStrokeTwoWidth, cap, join);
    BasicStroke strokeThree = new BasicStroke(symbolStrokeThreeWidth, cap, join);
    BasicStroke strokeFour = new BasicStroke(symbolStrokeFourWidth, cap, join);

    StrokeRenderer symbolStrokeOne = new StrokeRenderer(symbolStrokeOneColor, strokeOne);
    StrokeRenderer symbolStrokeTwo = new StrokeRenderer(symbolStrokeTwoColor, strokeTwo);
    StrokeRenderer symbolStrokeThree = new StrokeRenderer(symbolStrokeThreeColor, strokeThree);
    StrokeRenderer symbolStrokeFour = new StrokeRenderer(symbolStrokeFourColor, strokeFour);
    CompositeRenderer symbolPainter = new CompositeRenderer(new Renderer[]{symbolStrokeFour,
									     symbolStrokeThree,
									     symbolStrokeTwo,
									     symbolStrokeOne,
									     symbolFill });
    
    
    //
    // Create layers for the background and symbol
    //
    ShapeLayer symbolBkgLayer = new ShapeLayer(cmp, symbolBackground, bkgPainter, Position.CENTER);
    ShapeLayer symbolLayer = new ShapeLayer(cmp, symbol, symbolPainter, Position.CENTER);

    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{ symbolBkgLayer, symbolLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);

    return cmp;
  }
}

