/*
 * @(#)LightOpUsage3.java
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
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Illustrates usage of the LightOp class with DirectionalLights
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class LightOpUsage3 implements CompositionFactory{
  File imageFile;

  File textureFile;

  Color lightColor;

  float lightIntensity;

  public float getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(float lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public File getTextureFile(){
    return textureFile;
  }

  public void setTextureFile(File textureFile){
    this.textureFile = textureFile;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }  

  public LightOpUsage3(String imageFileName, float lightIntensity, String textureMapFile){
    setImageFile(new File(imageFileName));
    setTextureFile(new File(textureMapFile));
    setLightColor(new Color(40, 110, 120));
    setLightIntensity(lightIntensity);
  }

  public Composition build(){
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_RGB);
    if(image==null)
      throw new IllegalArgumentException("Could not load imageFileName");
    BufferedImage textureMap = Toolbox.loadImage(textureFile, BufferedImage.TYPE_BYTE_GRAY);
    if(textureMap==null)
      throw new IllegalArgumentException("Could not load textureMapFile");

    double L[] = {-1, -1, 1};                      // Light comes from the top left corner
    float I = lightIntensity;                      // Intensity.
    DirectionalLight eveningSun = new DirectionalLight(L, I, lightColor);
    LitSurface litSurface = new LitSurface(0);     // No ambient light
    litSurface.addLight(eveningSun);
    LightOp lightOp = new LightOp(litSurface);
    BufferedImage filteredImage = lightOp.filter(image, null);    

    // Create another image of the same size.
    int w = image.getWidth();
    int h = image.getHeight();
    BufferedImage buf = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    g.setPaint(Color.gray);
    g.fillRect(0, 0, w, h);

    // Filter with no texture
    BufferedImage noTexture = lightOp.filter(buf, null);

    // Now, set a texture
    ElevationMap texture = new ElevationMap(textureMap, true, 5);
    litSurface.setElevationMap(texture);

    BufferedImage withTexture = lightOp.filter(buf, null);

    BufferedImage withTexture2 = lightOp.filter(image, null);

    // Create Composition with images
    Dimension size = new Dimension(image.getWidth()*2, image.getHeight()*2);
    LayerComposition cmp = new LayerComposition(size);
    
    ImageLayer filteredLayer = new ImageLayer(cmp, filteredImage, Position.TOP_LEFT);
    ImageLayer noTextureLayer = new ImageLayer(cmp, noTexture, Position.TOP_RIGHT);
    ImageLayer withTexture2Layer = new ImageLayer(cmp, withTexture2, Position.BOTTOM_LEFT);
    ImageLayer withTextureLayer = new ImageLayer(cmp, withTexture, Position.BOTTOM_RIGHT);

    cmp.setLayers(new Layer[]{filteredLayer, noTextureLayer, withTexture2Layer, withTextureLayer});
    return cmp;
  }
  static final String USAGE = "java com.sun.glf.snippets.LightOpUsage3 <imageFileName> <lightIntensity> <textureMap>";

  public static void main(String args[]){
    if(args.length<3){
      System.out.println(USAGE);
      System.exit(0);
    }

    float lightIntensity = Float.parseFloat(args[1]);
    LightOpUsage3 factory = new LightOpUsage3(args[0], lightIntensity, args[2]);
    CompositionStudio studio = new CompositionStudio();
    studio.loadBeans(factory);

    final JFrame frame = new JFrame();
    frame.getContentPane().add(studio);
    frame.pack();
    frame.setVisible(true);  

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });
  }
}
