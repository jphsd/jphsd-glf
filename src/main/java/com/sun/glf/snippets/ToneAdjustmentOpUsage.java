/*
 * @(#)HelloLayerReuse.java
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
 * Illustrates how to use ToneAdjustmentOp
 *
 * @author           Vincent Hardy
 * @version          1.0, 07.15.1999
 */
public class ToneAdjustmentOpUsage implements CompositionFactory{
  Color tone1 = Color.black;

  Color tone2 = new Color(128, 0, 0);

  Color tone3 = Color.orange;

  Color tone4 = Color.yellow;

  Color tone5 = Color.white;

  float interval1 = 1f;

  float interval2 = 5f;

  float interval3 = 2f;

  float interval4 = 3f;

  File imageFile = new File("");

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Color getTone1(){
    return tone1;
  }

  public void setTone1(Color tone1){
    this.tone1 = tone1;
  }

  public Color getTone2(){
    return tone2;
  }

  public void setTone2(Color tone2){
    this.tone2 = tone2;
  }

  public Color getTone3(){
    return tone3;
  }

  public void setTone3(Color tone3){
    this.tone3 = tone3;
  }

  public Color getTone4(){
    return tone4;
  }

  public void setTone4(Color tone4){
    this.tone4 = tone4;
  }

  public Color getTone5(){
    return tone5;
  }

  public void setTone5(Color tone5){
    this.tone5 = tone5;
  }

  public float getInterval1(){
    return interval1;
  }

  public void setInterval1(float interval1){
    this.interval1 = interval1;
  }

  public float getInterval2(){
    return interval2;
  }

  public void setInterval2(float interval2){
    this.interval2 = interval2;
  }

  public float getInterval3(){
    return interval3;
  }

  public void setInterval3(float interval3){
    this.interval3 = interval3;
  }

  public float getInterval4(){
    return interval4;
  }

  public void setInterval4(float interval4){
    this.interval4 = interval4;
  }

  public Composition build(){
    //
    // First, load image to filter 
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB);
    if(image == null)
      throw new Error("Could not load : " + imageFile);

    //
    // Now, build a ToneAdjustmentOp filter
    //
    Color tones[] = { tone1, tone2, tone3, tone4, tone5 };
    float toneIntervals[] = {interval1,  // tone1 to tone2
			     interval2,  // tone2 to tone3
			     interval3,  // tone3 to tone4
			     interval4}; // tone4 to tone5

    ToneAdjustmentOp toneAdjuster = new ToneAdjustmentOp(tones, toneIntervals);
    
    //
    // Build a composition the size of the filtered image
    //
    Dimension size = new Dimension(image.getWidth(), image.getHeight());
    LayerComposition cmp = new LayerComposition(size);
    
    ImageLayer imageLayer = new ImageLayer(cmp, image);
    imageLayer.setImageFilter(toneAdjuster);

    cmp.setLayers(new Layer[]{ imageLayer });
    return cmp;
  }
}
