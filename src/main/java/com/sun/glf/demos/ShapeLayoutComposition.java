/*
 * @(#)ShapeLayoutComposition.java
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

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This example illustrates how to layout text into non square boundaries.
 * Here, text is laid out into triangles and circular shapes.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/08/1999
 */
public class ShapeLayoutComposition implements CompositionFactory{
  String textOne = "One";

  String textTwo = "Two";

  String textThree = "Three";

  String textFour = "Four";

  Font textFont = new Font("Impact MT", Font.PLAIN, 20);

  Color colorOne = Color.orange;

  Color colorTwo = Color.black; 

  int shapeMargin = 5;

  int textMargin = 5;

  int blockWidth = 250;

  File decoBeanFile = new File("");

  Font decoFont = new Font("Curlz MT", Font.PLAIN, 20);

  Color backgroundColor = Color.white;

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Font getDecoFont(){
    return decoFont;
  }

  public void setDecoFont(Font decoFont){
    this.decoFont = decoFont;
  }
  
  public File getDecoBeanFile(){
    return decoBeanFile;
  }

  public void setDecoBeanFile(File decoBeanFile){
    this.decoBeanFile = decoBeanFile;
  }

  public int getBlockWidth(){
    return blockWidth;
  }

  public void setBlockWidth(int blockWidth){
    this.blockWidth = blockWidth;
  }

  public String getTextOne(){
    return textOne;
  }

  public void setTextOne(String textOne){
    this.textOne = textOne;
  }

  public String getTextTwo(){
    return textTwo;
  }

  public void setTextTwo(String textTwo){
    this.textTwo = textTwo;
  }

  public String getTextThree(){
    return textThree;
  }

  public void setTextThree(String textThree){
    this.textThree = textThree;
  }

  public String getTextFour(){
    return textFour;
  }

  public void setTextFour(String textFour){
    this.textFour = textFour;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public Color getColorOne(){
    return colorOne;
  }

  public void setColorOne(Color colorOne){
    this.colorOne = colorOne;
  }

  public Color getColorTwo(){
    return colorTwo;
  }

  public void setColorTwo(Color colorTwo){
    this.colorTwo = colorTwo;
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
    // First, use two other CompositionFactories to create the basic building
    // blocks
    //
    TriangularLayoutComposition triangleTextFactory = new TriangularLayoutComposition();
    CircularLayoutComposition circleTextFactory = new CircularLayoutComposition();

    // Set common settings
    triangleTextFactory.setBlockWidth(blockWidth);
    triangleTextFactory.setTextFont(textFont);
    triangleTextFactory.setTextMargin(textMargin);
    triangleTextFactory.setShapeMargin(shapeMargin);

    circleTextFactory.setBlockWidth(blockWidth);
    circleTextFactory.setTextFont(textFont);
    circleTextFactory.setTextMargin(textMargin);
    circleTextFactory.setShapeMargin(shapeMargin);

    // Top Left and bottom right have the same color settings
    triangleTextFactory.setBackgroundColor(colorOne);
    triangleTextFactory.setTextColor(colorTwo);

    triangleTextFactory.setText(textOne);
    Composition topLeftBlock = triangleTextFactory.build();

    triangleTextFactory.setText(textThree);
    triangleTextFactory.setTipOnTop(false); // Triangle tip down
    Composition bottomRightBlock = triangleTextFactory.build();

    // Top Right and Bottom Left have the same color settings
    circleTextFactory.setBackgroundColor(colorTwo);
    circleTextFactory.setTextColor(colorOne);

    circleTextFactory.setText(textTwo);
    Composition topRightBlock = circleTextFactory.build();
    
    circleTextFactory.setText(textFour);
    Composition bottomLeftBlock = circleTextFactory.build();

    //
    // Now, load the decoration bean and create a decoration
    // for each of the text blocks
    //
    GlyphDecorationComposition decoFactory 
      = (GlyphDecorationComposition)CompositionFactoryLoader.loadBeanFile(decoBeanFile);
    if(decoFactory==null)
      throw new Error();

    Color transparentColor = new Color(0, 0, 0, 0);
    
    decoFactory.setGlyph(new Glyph(decoFont, '4'));
    decoFactory.setBackgroundColor(colorOne);
    Composition bottomLeftDeco = decoFactory.build();

    decoFactory.setGlyph(new Glyph(decoFont, '3'));
    decoFactory.setBackgroundColor(colorTwo);
    decoFactory.setSymbolStrokeFourColor(transparentColor);
    Composition bottomRightDeco = decoFactory.build();

    decoFactory.setGlyph(new Glyph(decoFont, '2'));
    decoFactory.setBackgroundColor(colorOne);
    decoFactory.setSymbolStrokeThreeColor(transparentColor);
    Composition topRightDeco = decoFactory.build();
    
    decoFactory.setGlyph(new Glyph(decoFont, '1'));
    decoFactory.setBackgroundColor(colorTwo);
    decoFactory.setSymbolStrokeTwoColor(transparentColor);
    Composition topLeftDeco = decoFactory.build();

    //
    // Build LayerComposition. Use the block size and decoration to 
    // determine size.
    //
    Dimension decoSize = topLeftDeco.getSize();
    float blockAdjust = decoSize.height/2f;    
    float decoAdjust = (blockWidth - decoSize.width)/2f;
    Dimension size = new Dimension(blockWidth*2 + (int)(blockAdjust*2), blockWidth*2 + (int)(blockAdjust*2));
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Process positions for the different elements
    //
    Position topLeftBlockPos = new Position(Anchor.TOP_LEFT, blockAdjust, blockAdjust);
    Position topLeftDecoPos = Position.TOP_LEFT; // new Position(Anchor.TOP_LEFT, decoAdjust, 0);

    Position topRightBlockPos = new Position(Anchor.TOP_RIGHT, blockAdjust, blockAdjust);
    Position topRightDecoPos = Position.TOP_RIGHT; // new Position(Anchor.TOP_RIGHT, decoAdjust, 0);

    Position bottomLeftBlockPos = new Position(Anchor.BOTTOM_LEFT, blockAdjust, blockAdjust);
    Position bottomLeftDecoPos = Position.BOTTOM_LEFT; // new Position(Anchor.BOTTOM_LEFT, decoAdjust, 0);

    Position bottomRightBlockPos = new Position(Anchor.BOTTOM_RIGHT, blockAdjust, blockAdjust);
    Position bottomRightDecoPos = Position.BOTTOM_RIGHT; // new Position(Anchor.BOTTOM_RIGHT, decoAdjust, 0);

    // 
    // Create proxy layers for each of the Compositions
    //

    // Text blocks
    Layer topLeftLayer = new CompositionProxyLayer(cmp, topLeftBlock, topLeftBlockPos);
    Layer topRightLayer = new CompositionProxyLayer(cmp, topRightBlock, topRightBlockPos);
    Layer bottomRightLayer = new CompositionProxyLayer(cmp, bottomRightBlock, bottomRightBlockPos);
    Layer bottomLeftLayer = new CompositionProxyLayer(cmp, bottomLeftBlock, bottomLeftBlockPos);

    // Decorations
    Layer topLeftDecoLayer = new CompositionProxyLayer(cmp, topLeftDeco, topLeftDecoPos);
    Layer topRightDecoLayer = new CompositionProxyLayer(cmp, topRightDeco, topRightDecoPos);
    Layer bottomRightDecoLayer = new CompositionProxyLayer(cmp, bottomRightDeco, bottomRightDecoPos);
    Layer bottomLeftDecoLayer = new CompositionProxyLayer(cmp, bottomLeftDeco, bottomLeftDecoPos);

    // Stack up layers
    cmp.setLayers(new Layer[]{ topLeftLayer, topRightLayer, bottomRightLayer, bottomLeftLayer,
				 topLeftDecoLayer, topRightDecoLayer, bottomRightDecoLayer, bottomLeftDecoLayer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;				    
  }

}

