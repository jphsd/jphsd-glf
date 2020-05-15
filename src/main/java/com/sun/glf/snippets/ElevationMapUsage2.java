/*
 * @(#)ElevationMapUsage2.java
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
import java.beans.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

public class ElevationMapUsage2 implements CompositionFactory{
  Color textureColor = Color.orange;

  Glyph glyph = new Glyph(new Font("Impact", Font.BOLD, 250), '@');

  Dimension size = new Dimension(180, 180);

  float lightIntensity =1.5f;

  int margins = 30;

  boolean whiteIsHigh = true;

  int heightScale = 20;

  public boolean isWhiteIsHigh(){
    return whiteIsHigh;
  }

  public void setWhiteIsHigh(boolean whiteIsHigh){
    this.whiteIsHigh = whiteIsHigh;
  }

  public int getHeightScale(){
    return heightScale;
  }

  public void setHeightScale(int heightScale){
    this.heightScale = heightScale;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
  }

  public float getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(float lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public Color getTextureColor(){
    return textureColor;
  }

  public void setTextureColor(Color textureColor){
    this.textureColor = textureColor;
  }

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

 public Composition build(){    
    Shape shape = glyph.getShape();
    Rectangle bounds = shape.getBounds();
    bounds.x = 0;
    bounds.y = 0;
    bounds.width += 2*margins;
    bounds.height += 2*margins;
    
    Position positions[] = {Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT};
    int blurRadius[] = { 5, 10, 20 };
    Dimension cmpSize = new Dimension(bounds.width*positions.length, bounds.height);
    LayerComposition cmp = new LayerComposition(cmpSize);
    cmp.setBackgroundPaint(Color.black);

    Layer layers[] = new Layer[positions.length];
    FillRenderer painter = new FillRenderer(textureColor);
    int p = 0;
    for(int i=0; i<blurRadius.length; i++){
      layers[i] = new ShapeLayer(cmp, bounds, painter, positions[i]);
      ElevationMap map = new ElevationMap(shape, bounds, Anchor.CENTER, 0, 0, blurRadius[i], whiteIsHigh, heightScale);
      LitSurface litSurface = new LitSurface(0.1, LitSurfaceType.NORMAL, map);
      litSurface.addLight(LightsStudio.getSunLight(Anchor.TOP_LEFT, lightIntensity, Color.white));
      LightOp lighting = new LightOp(litSurface);
      layers[i].setRasterFilter(lighting);
      AffineTransform t = layers[i].getTransform();
      layers[i].setLayerMask(Position.CENTER.createTransformedShape(shape,
      							    t.createTransformedShape(bounds).getBounds()));
    }

    cmp.setLayers(layers);
    return cmp;
  }
}
