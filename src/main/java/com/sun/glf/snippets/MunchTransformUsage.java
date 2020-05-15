/*
 * @(#)MunchTransformUsage.java
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
import java.awt.image.*;
import java.io.*;

import javax.swing.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Illustrates usage of the MunchTransform class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class MunchTransformUsage implements CompositionFactory{
  int width = 500;

  int height = 300;

  float munchSize = 1;

  float rectStrokeWidth = 1.5f;

  Color rectColor = Color.black;

  float bumpAngle = (float)(3*Math.PI/4);

  int margins = 20;

  public int getWidth(){
    return width;
  }

  public void setWidth(int width){
    this.width = width;
  }

  public int getHeight(){
    return height;
  }

  public void setHeight(int height){
    this.height = height;
  }

  public float getMunchSize(){
    return munchSize;
  }

  public void setMunchSize(float munchSize){
    this.munchSize = munchSize;
  }

  public float getRectStrokeWidth(){
    return rectStrokeWidth;
  }

  public void setRectStrokeWidth(float rectStrokeWidth){
    this.rectStrokeWidth = rectStrokeWidth;
  }

  public Color getRectColor(){
    return rectColor;
  }

  public void setRectColor(Color rectColor){
    this.rectColor = rectColor;
  }

  public float getBumpAngle(){
    return bumpAngle;
  }

  public void setBumpAngle(float bumpAngle){
    this.bumpAngle = bumpAngle;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
  }

  public Composition build(){
    Rectangle rect = new Rectangle(0, 0, width, height);
    BumpTransform bumpTransform = new BumpTransform(bumpAngle);
    
    if(munchSize>0){
      MunchTransform munchTransform = new MunchTransform(munchSize);
      bumpTransform.concatenate(munchTransform);
    }

    Shape bentRect = bumpTransform.transform(rect);
    Rectangle bounds = bentRect.getBounds();
    Dimension size = new Dimension(bounds.width + 2*margins, bounds.height + 2*margins);
    LayerComposition cmp = new LayerComposition(size);
    ShapeLayer layer = new ShapeLayer(cmp, bentRect, new StrokeRenderer(rectColor, rectStrokeWidth), Position.CENTER);

    cmp.setLayers(new Layer[]{layer});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setBackgroundPaint(Color.white);
    return cmp;
  }
}
