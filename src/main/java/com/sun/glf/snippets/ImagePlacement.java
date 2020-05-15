/*
 * @(#)ImagePlacement.java
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

/**
 * Illustrates the use of Anchors for placing Images relative the the 
 * LayerComposition's bounding Rectangle.
 *
 * @author             Vincent Hardy
 * @version            1.0, 03.10.1999
 */
public class ImagePlacement implements CompositionFactory{
  File imageFile = new File("");

  Color backgroundColor = Color.black;

  double rotateAngle = Math.PI/4;

  int margins = 20;

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public double getRotateAngle(){
    return rotateAngle;
  }

  public void setRotateAngle(double rotateAngle){
    this.rotateAngle = rotateAngle;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
        }
  public Composition build(){
    // 
    // Load image
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_RGB);
    if(image==null)
      throw new IllegalArgumentException("Could not load : " + imageFile);

    // 
    // Create a LayerComposition 
    //
    int w = image.getWidth();
    int h = image.getHeight();
    Dimension size = new Dimension(w*3 + 4*margins, 
				   h*3 + 4*margins);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);
    
    //
    // Create ImageLayers that render the same Image, but with 
    // different Positions.
    //
    int n = Anchor.enumValues.length;
    Layer layers[] = new Layer[n];
    AffineTransform rotator = AffineTransform.getRotateInstance(rotateAngle);

    for(int i=0; i<n; i++){
      Anchor imageAnchor = Anchor.enumValues[i];
      Position imagePosition = new Position(imageAnchor, 
					    margins, margins, 
					    rotator,
					    false);
      layers[i] = new ImageLayer(cmp, image, imagePosition);
    }

    cmp.setLayers(layers);
    cmp.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    return cmp;
  }
}

