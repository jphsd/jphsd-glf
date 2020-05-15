/*
 * @(#)LightOpUsage5.java
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
 * Illustrates usage of the LightOp class.
 * Shows impact of different settings for parameters describing 
 * material type.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class LightOpUsage5 implements CompositionFactory{
  Dimension size = new Dimension(300, 300);

  File textureFile = new File("elevationMap.jpg");

  Color color = new Color(50, 200, 160);

  LitSurfaceType surfaceType = LitSurfaceType.NORMAL;

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public File getTextureFile(){
    return textureFile;
  }

  public void setTextureFile(File textureFile){
    this.textureFile = textureFile;
  }

  public Color getColor(){
    return color;
  }

  public void setColor(Color color){
    this.color = color;
  }

  public LitSurfaceType getSurfaceType(){
    return surfaceType;
  }

  public void setSurfaceType(LitSurfaceType surfaceType){
    this.surfaceType = surfaceType;
  }
  
  public Composition build(){
    BufferedImage textureMap = Toolbox.loadImage(textureFile, BufferedImage.TYPE_BYTE_GRAY);
    if(textureMap==null)
      throw new Error("Could not load image: " + (textureFile!=null?textureFile.getAbsolutePath():"null"));

    int w = size.width;
    int h = size.height;
    BufferedImage buf = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    g.setPaint(color);
    g.fillRect(0, 0, w, h);
    ElevationMap texture = new ElevationMap(textureMap, true, 10);
    LitSurface litSurface = new LitSurface(.2, surfaceType, texture);
    double L[] = {-1, -1, 1}; // Light comes from the top left corner
    DirectionalLight sunLight = new DirectionalLight(L, 1, Color.white);
    litSurface.addLight(sunLight);

    LightOp lightOp = new LightOp(litSurface);

    
    BufferedImage withTexture = lightOp.filter(buf, null);

    LayerComposition cmp = new LayerComposition(size);
    ImageLayer layer = new ImageLayer(cmp, withTexture);
    cmp.setLayers(new Layer[]{layer});
    return cmp;
  }

  static final String USAGE = "java com.sun.glf.snippets.LightOpUsage5 <textureMapName>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    CompositionStudio studio = new CompositionStudio();
    LightOpUsage5 factory = new LightOpUsage5();
    factory.setTextureFile(new File(args[0]));
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
