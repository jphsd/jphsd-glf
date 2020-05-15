/*
 * @(#)ElevationMapUsage.java
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

public class ElevationMapUsage implements CompositionFactory{
  Color textureColor = Color.orange;

  File textureFile = new File("c:\\work\\doc\\java2d\\book\\code\\res\\snippets\\elevationMap.jpg");

  Dimension size = new Dimension(180, 180);

  float lightIntensity = 2;

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

  public File getTextureFile(){
    return textureFile;
  }

  public void setTextureFile(File textureFile){
    this.textureFile = textureFile;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Composition build(){
    BufferedImage texture = Toolbox.loadImage(textureFile, BufferedImage.TYPE_BYTE_GRAY);
    if(texture==null)
      throw new Error("Could not load: " + textureFile.getAbsolutePath());

    Dimension cmpSize = new Dimension(size.width*3, size.height*2);
    LayerComposition cmp = new LayerComposition(cmpSize);
    Rectangle litRect = new Rectangle(0, 0, size.width, size.height);

    boolean whiteIsHigh[] = {true, false};
    int heightScale[] = {1, 10, 50};
    Position positions[] = {Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT,
			    Position.BOTTOM_LEFT, Position.BOTTOM, Position.BOTTOM_RIGHT};
    Layer layers[] = new Layer[positions.length];
    FillRenderer painter = new FillRenderer(textureColor);
    Dimension lightDim = new Dimension(litRect.width, litRect.height);
    int p = 0;

    for(int i=0; i<whiteIsHigh.length; i++){
      for(int j=0; j<heightScale.length; j++){
	layers[p] = new ShapeLayer(cmp, litRect, painter, positions[p]);
	ElevationMap map = new ElevationMap(texture, whiteIsHigh[i], heightScale[j]);
	LitSurface litSurface = new LitSurface(0.1, LitSurfaceType.NORMAL, map);
	Rectangle spotRect = positions[p].createTransformedShape(litRect, cmp.getBounds()).getBounds();
	litSurface.addLight(LightsStudio.getSpotLight(spotRect, Anchor.TOP, lightDim, lightIntensity, Color.white));
	LightOp lighting = new LightOp(litSurface);
	layers[p].setRasterFilter(lighting);
	p++;
      }
    }

    cmp.setLayers(layers);
    return cmp;
  }
}
