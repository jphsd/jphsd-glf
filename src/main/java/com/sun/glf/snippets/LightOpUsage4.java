/*
 * @(#)LightOpUsage4.java
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

import javax.swing.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * Illustrates usage of the LightOp class with DirectionalLights
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class LightOpUsage4 extends JComponent{
  BufferedImage imageOneSpot, imageTwoSpots;

  public LightOpUsage4(String imageFileName, float spotIntensity, String textureMapFile){
    BufferedImage image = Toolbox.loadImage(imageFileName, BufferedImage.TYPE_INT_RGB);
    if(image==null){
      image = new BufferedImage(150, 200, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = image.createGraphics();
      g.setPaint(Color.gray);
      g.fillRect(0, 0, 150, 200);
      g.dispose();
    }
    BufferedImage textureMap = null;
    if(textureMapFile!=null)
      textureMap = Toolbox.loadImage(textureMapFile, BufferedImage.TYPE_BYTE_GRAY);

    Color spotColor = Color.white;
    int w = image.getWidth();
    int h = image.getHeight();
    Rectangle topSpotArea = new Rectangle(0, 0, w, 2*h);
    SpotLight topSpotLight = new SpotLight(topSpotArea, spotColor, spotIntensity, 0); // no rotation
    LitSurface litSurface = new LitSurface(0.2);
    if(textureMap!=null){
      ElevationMap texture = new ElevationMap(textureMap, true, 5);
      litSurface.setElevationMap(texture);
    }
    litSurface.addLight(topSpotLight);
    LightOp lightOp = new LightOp(litSurface);
    BufferedImage imageOneSpot = lightOp.createCompatibleDestImage(image, null);
    lightOp.filter(image, imageOneSpot);

    // Now, modify LitSurface: remove all lights and add a SpotLight in the left and in the right angles
    Color leftSpotColor = new Color(200, 200, 120); 
    Color rightSpotColor = new Color(150, 255, 150);
    litSurface.removeAllLights();
    Rectangle leftSpotArea = new Rectangle(-w/2, 0, w, 2*h);
    SpotLight leftSpot = new SpotLight(leftSpotArea, leftSpotColor, spotIntensity, -Math.PI/5);
    Rectangle rightSpotArea = new Rectangle(w/2, 0, w, 2*h);
    SpotLight rightSpot = new SpotLight(rightSpotArea, rightSpotColor, spotIntensity, Math.PI/5);
    litSurface.addLight(leftSpot);
    litSurface.addLight(rightSpot);
    BufferedImage imageTwoSpots = lightOp.createCompatibleDestImage(image, null);
    lightOp.filter(image, imageTwoSpots);

    this.imageOneSpot = imageOneSpot;
    this.imageTwoSpots = imageTwoSpots;
    setPreferredSize(new Dimension(image.getWidth()*2, image.getHeight()));
  }

  public void paint(Graphics _g){
    _g.drawImage(imageOneSpot, 0, 0, null);
    _g.drawImage(imageTwoSpots, imageOneSpot.getWidth(), 0, null);
  }

  static final String USAGE = "java com.sun.glf.snippets.LightOpUsage4 <imageFileName> <spotIntensity> <textureMapName>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    float spotIntensity = Float.parseFloat(args[1]);
    String textureMapName = args.length>2?args[2]:null;
    LightOpUsage4 cmp = new LightOpUsage4(args[0], spotIntensity, textureMapName);
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
