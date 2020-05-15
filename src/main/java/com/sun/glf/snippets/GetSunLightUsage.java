/*
 * @(#)GetSunLightUsage.java
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


public class GetSunLightUsage implements CompositionFactory{
  File textureImage = new File("c:\\work\\doc\\java2d\\book\\code\\res\\images\\snippets\\coneElevation.jpg");

  Color textureColor = new Color(150, 120, 60);

  int width = 90;

  int height = 120;

  float lightIntensity = 1;

  Color lightColor = Color.white;

  public File getTextureImage(){
    return textureImage;
  }

  public void setTextureImage(File textureImage){
    this.textureImage = textureImage;
  }

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
    Anchor anchors[] = Anchor.enumValues;
    BufferedImage texture = Toolbox.loadImage(textureImage, BufferedImage.TYPE_BYTE_GRAY);
    Renderer painter = new FillRenderer(textureColor);
    Layer layers[] = new Layer[anchors.length];

    Dimension size = new Dimension(width*3, height*3);
    Rectangle litRect = new Rectangle(0, 0, width, height);

    LayerComposition cmp = new LayerComposition(size);
    for(int i=0; i<anchors.length; i++){
      LitSurface texturedSurface 
	= new LitSurface(0, 
			 LitSurfaceType.NORMAL, 
			 new ElevationMap(texture, true, 10)); // Height scale factor
      texturedSurface.addLight( LightsStudio.getSunLight(anchors[i], lightIntensity, lightColor) );
      LightOp lighting = new LightOp(texturedSurface);
      layers[i] = new ShapeLayer(cmp, litRect, painter, new Position(anchors[i]));
      layers[i].setImageFilter(lighting);
    }

    cmp.setLayers(layers);
    return cmp;
  }
}
