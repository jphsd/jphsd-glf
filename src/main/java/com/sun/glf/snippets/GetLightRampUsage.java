/*
 * @(#)GetLightRampUsage.java
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
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;


public class GetLightRampUsage implements CompositionFactory{
  int width = 250;

  int height = 300;

  float lightIntensity = 3;

  Color lightColor = Color.white;

  Color textureColor = Color.orange;

  public Color getTextureColor(){
    return textureColor;
  }

  public void setTextureColor(Color textureColor){
    this.textureColor = textureColor;
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

  public float getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(float lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }

  public Composition build(){
    Anchor anchors[] = {Anchor.TOP, Anchor.RIGHT, Anchor.BOTTOM, Anchor.LEFT};
    Anchor layerAnchors[] = {Anchor.TOP_LEFT, Anchor.TOP_RIGHT, Anchor.BOTTOM_RIGHT, Anchor.BOTTOM_LEFT};

    Dimension size = new Dimension(width*2, height*2);
    LayerComposition cmp = new LayerComposition(size);
    Rectangle litRect = new Rectangle(0, 0, width, height);
    Layer layers[] = new Layer[anchors.length];
    Renderer painter = new FillRenderer(textureColor);
    
    for(int i=0; i<anchors.length; i++){
    LitSurface texturedSurface 
      = new LitSurface(0, 
		       LitSurfaceType.NORMAL, null); 
      SpotLight lights[] = LightsStudio.getLightRamp(litRect,
						     i+2,
						     anchors[i], 
						     lightIntensity,
						     lightColor,
						     0f);
      texturedSurface.addLights(lights);
      LightOp lighting = new LightOp(texturedSurface);
      layers[i] = new ShapeLayer(cmp, litRect, painter, new Position(layerAnchors[i]));
      layers[i].setImageFilter(lighting);
    }


    cmp.setLayers(layers);
    return cmp;
  }
}
