/*
 * @(#)ColorCompositeUsage.java
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
 * Illustrates usage of the ColorComposite settings.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/21/1999
 */
public class ColorCompositeUsage implements CompositionFactory{
  File destinationImageFile = new File("vango09.jpg");

  Color sourceColor = Color.red;

  ColorCompositeRule compositeRule = ColorCompositeRule.HUE;

  float compositeAlpha = .5f;

  public File getDestinationImageFile(){
    return destinationImageFile;
  }

  public void setDestinationImageFile(File destinationImageFile){
    this.destinationImageFile = destinationImageFile;
  }

  public Color getSourceColor(){
    return sourceColor;
  }

  public void setSourceColor(Color sourceColor){
    this.sourceColor = sourceColor;
  }

  public ColorCompositeRule getCompositeRule(){
    return compositeRule;
  }

  public void setCompositeRule(ColorCompositeRule compositeRule){
    this.compositeRule = compositeRule;
  }

  public float getCompositeAlpha(){
    return compositeAlpha;
  }

  public void setCompositeAlpha(float compositeAlpha){
    this.compositeAlpha = compositeAlpha;
  }

  public Composition build(){
    BufferedImage image = Toolbox.loadImage(destinationImageFile, BufferedImage.TYPE_INT_RGB);
    Dimension size = new Dimension(image.getWidth(), image.getHeight());
    LayerComposition cmp = new LayerComposition(size);

    ImageLayer destination = new ImageLayer(cmp, image);

    ShapeLayer source = new ShapeLayer(cmp, cmp.getBounds(), new FillRenderer(sourceColor));
    ColorComposite composite = ColorComposite.getInstance(compositeRule, compositeAlpha);
    source.setComposite(composite);

    cmp.setLayers(new Layer[]{destination, source});
    return cmp;
  }

  public static final String USAGE = "java com.sun.glf.snippets.ColorCompositeUsage <imageFile> <red> <green> <blue>";

  public static void main(String args[]){
    // Check input arguments
    if(args.length<4){ 
      System.out.println(USAGE);
      System.exit(0);
    }

    ColorCompositeUsage factory = new ColorCompositeUsage();
    factory.setDestinationImageFile(new File(args[0]));
    factory.setSourceColor(new Color(Integer.parseInt(args[1]),
				     Integer.parseInt(args[2]),
				     Integer.parseInt(args[3])));


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
