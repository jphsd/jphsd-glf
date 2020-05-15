/*
 * @(#)SpotLightUsage.java
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
 * Illustrates usage of the SpotLight class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class SpotLightUsage implements CompositionFactory{
  int width = 100;

  int height = 150;

  Color spotColor = Color.orange;

  float spotIntensity = 4f;

  double spotAngle = 0;

  int spotFocus = 4;

  int margin = 20;

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

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

  public Color getSpotColor(){
    return spotColor;
  }

  public void setSpotColor(Color spotColor){
    this.spotColor = spotColor;
  }

  public float getSpotIntensity(){
    return spotIntensity;
  }

  public void setSpotIntensity(float spotIntensity){
    this.spotIntensity = spotIntensity;
  }

  public double getSpotAngle(){
    return spotAngle;
  }

  public void setSpotAngle(double spotAngle){
    this.spotAngle = spotAngle;
  }

  public int getSpotFocus(){
    return spotFocus;
  }

  public void setSpotFocus(int spotFocus){
    this.spotFocus = spotFocus;
  }

  public Composition build(){
    Rectangle spotLightRect = new Rectangle(margin, margin, width, height);
    Rectangle rect = new Rectangle(0, 0, width + 2*margin, height + 2*margin);
    LayerComposition cmp = new LayerComposition(new Dimension(rect.width, rect.height));
    Layer shapeLayer = new ShapeLayer(cmp, rect, new FillRenderer(Color.white));
    LitSurface litSurface = new LitSurface(0, LitSurfaceType.NORMAL, null);
    SpotLight spot = new SpotLight(spotLightRect, spotColor, spotIntensity, spotAngle, spotFocus);
    litSurface.addLight(spot);
    LightOp lighting = new LightOp(litSurface);
    shapeLayer.setRasterFilter(lighting);
    cmp.setLayers(new Layer[]{shapeLayer});

    return cmp;
  }
}
