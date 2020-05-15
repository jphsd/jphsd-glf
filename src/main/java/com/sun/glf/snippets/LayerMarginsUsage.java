/*
 * @(#)LayerMarginsUsage.java
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
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;


/**
 * Illustrates the usage of margins in layer filter
 *
 * @author           Vincent Hardy
 * @version          1.0, 01/10/1999
 */
public class LayerMarginsUsage implements CompositionFactory{
  int rectWidth = 50;

  int rectHeight = 50;

  Color color = Color.black;

  int blurRadius = 12;

  int margins = 24;

  boolean showBounds = true;

  public boolean getShowBounds(){
    return showBounds;
  }

  public void setShowBounds(boolean showBounds){
    this.showBounds = showBounds;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
  }

  public int getRectWidth(){
    return rectWidth;
  }

  public void setRectWidth(int rectWidth){
    this.rectWidth = rectWidth;
  }

  public int getRectHeight(){
    return rectHeight;
  }

  public void setRectHeight(int rectHeight){
    this.rectHeight = rectHeight;
  }

  public Color getColor(){
    return color;
  }

  public void setColor(Color color){
    this.color = color;
  }

  public int getBlurRadius(){
    return blurRadius;
  }

  public void setBlurRadius(int blurRadius){
    this.blurRadius = blurRadius;
  }

  public Composition build(){
    Rectangle rect = new Rectangle(0, 0, rectWidth, rectHeight);
    Renderer painter = new FillRenderer(color);
    
    Dimension size = new Dimension(rectWidth + blurRadius*3, rectWidth + blurRadius*3);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(Color.white);

    ShapeLayer blurredRect = new ShapeLayer(cmp, rect, painter, Position.CENTER);
    ConvolveOp blur = new ConvolveOp(new GaussianKernel(blurRadius));
    blurredRect.setImageFilter(blur, new Dimension(margins, margins));

    Color boundsColor = Color.red;
    if(!showBounds)
      boundsColor = new Color(0, 0, 0, 0);
      
    ShapeLayer redRect = new ShapeLayer(cmp, rect, new StrokeRenderer(boundsColor, 0.33f), Position.CENTER);
    
    cmp.setLayers(new Layer[]{blurredRect, redRect});
    return cmp;
  }
}
