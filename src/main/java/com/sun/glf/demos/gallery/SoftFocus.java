/*
 * @(#)SoftFocus.java
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

package com.sun.glf.demos.gallery;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Illustrates how a soft focus effect can be created by compositing
 * an image with its blurred version.
 *
 * @author             Vincent Hardy
 * @version            1.0, 12/01/1998
 */
public class SoftFocus implements CompositionFactory{
  File imageFile = new File("");

  int blurRadius = 3;

  float alpha = .5f;

  int margin = 50;

  Color backgroundColor = Color.white;

  float bottomBorderWidth = 10;

  float topBorderWidth = 3;

  Color bottomBorderColor = new Color(20, 40, 20);

  Color topBorderDarkColor = Color.black;

  Color topBorderLightColor = Color.black;

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public float getBottomBorderWidth(){
    return bottomBorderWidth;
  }

  public void setBottomBorderWidth(float bottomBorderWidth){
    this.bottomBorderWidth = bottomBorderWidth;
  }

  public float getTopBorderWidth(){
    return topBorderWidth;
  }

  public void setTopBorderWidth(float topBorderWidth){
    this.topBorderWidth = topBorderWidth;
  }

  public Color getBottomBorderColor(){
    return bottomBorderColor;
  }

  public void setBottomBorderColor(Color bottomBorderColor){
    this.bottomBorderColor = bottomBorderColor;
  }

  public Color getTopBorderLightColor(){
    return topBorderLightColor;
  }

  public void setTopBorderLightColor(Color topBorderLightColor){
    this.topBorderLightColor = topBorderLightColor;
  }

  public Color getTopBorderDarkColor(){
    return topBorderDarkColor;
  }

  public void setTopBorderDarkColor(Color topBorderDarkColor){
    this.topBorderDarkColor = topBorderDarkColor;
  }

  public int getMargin(){
    return margin;
  }

  public void setMargin(int margin){
    this.margin = margin;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public int getBlurRadius(){
    return blurRadius;
  }

  public void setBlurRadius(int blurRadius){
    this.blurRadius = blurRadius;
  }

  public float getAlpha(){
    return alpha;
  }

  public void setAlpha(float alpha){
    this.alpha = alpha;
  }

  /**
   * Illustrates sophisticated and efficient implementation to obtain the soft focus effect. 
   * This implementation relies
   * solely on the ConvolveOp by using a Kernel that takes the opacity into account. An alternate
   * approach is to composite the blurred image with its original
   */
  public Composition build(){
    //
    // Load image first : the composition size will be based 
    // on the image size.
    //
    BufferedImage imageBase = Toolbox.loadImage(imageFile.getAbsolutePath(), BufferedImage.TYPE_INT_ARGB_PRE);
    if(imageBase==null)
      throw new Error("Could not load : " + imageBase);

    //
    // Create a Composition whose size depends on that of the image. Make the composition
    // large enough to display the original, unmodified image to the right.
    //
    int iw = imageBase.getWidth();
    int ih = imageBase.getHeight();
    Dimension size = new Dimension(iw + 2*margin, ih*2 + 4*margin);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Create a blurred version of the image 
    //
    Position blurredImagePosition = new Position(Anchor.TOP, margin, margin);
    GaussianKernel baseKernel = new GaussianKernel(blurRadius);

    //  Kernel fastKernels[] = baseKernel.separateKernel();
    // ConvolveOp hBlur = new ConvolveOp(fastKernels[0]);
    // ConvolveOp vBlur = new ConvolveOp(fastKernels[1]);
    // CompositeRasterOp fastBlur = new CompositeRasterOp(hBlur, vBlur);
    
    ImageLayer blurredImageLayer = new ImageLayer(cmp, imageBase, blurredImagePosition);
    blurredImageLayer.setImageFilter(new ConvolveOp(baseKernel), new Dimension(blurRadius*2, blurRadius*2));
    // blurredImageLayer.setRasterFilter(fastBlur, new Dimension(blurRadius*2, blurRadius*2));
    blurredImageLayer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    ImageLayer blurredImageOriginalLayer = new ImageLayer(cmp, imageBase, blurredImagePosition);

    //
    // Finally, create an ImageLayer with the original image
    //
    Position originalPosition = new Position(Anchor.BOTTOM, margin, margin);
    ImageLayer imageOriginalLayer = new ImageLayer(cmp, imageBase, originalPosition);

    //
    // Set masks for the layers
    //
    Shape border = new Ellipse2D.Float(0, 0, iw, ih);
    Shape originalBorder = originalPosition.createTransformedShape(border, cmp.getBounds());
    Shape blurredBorder = blurredImagePosition.createTransformedShape(border, cmp.getBounds());

    blurredImageLayer.setClip(blurredBorder);
    blurredImageOriginalLayer.setClip(blurredBorder);
    imageOriginalLayer.setClip(originalBorder);

    GeneralPath borders = new GeneralPath(originalBorder);
    borders.append(blurredBorder, false);

    ShapeLayer bottomBorderLayer = new ShapeLayer(cmp, borders, new StrokeRenderer(bottomBorderColor, bottomBorderWidth));
    ShapeLayer topBorderLightLayer = new ShapeLayer(cmp, borders, new StrokeRenderer(topBorderLightColor, topBorderWidth));
    ShapeLayer topBorderDarkLayer = new ShapeLayer(cmp, borders, new StrokeRenderer(topBorderDarkColor, topBorderWidth));
    
    AffineTransform t = AffineTransform.getTranslateInstance(-topBorderWidth/2f, -topBorderWidth/2f);
    topBorderDarkLayer.setLayerMask(t.createTransformedShape(borders));

    t.setToTranslation(topBorderWidth, topBorderWidth);
    topBorderLightLayer.setLayerMask(t.createTransformedShape(borders));

    //
    // Set composition layers
    //
    cmp.setLayers(new Layer[] { bottomBorderLayer, blurredImageOriginalLayer, blurredImageLayer, 
				  imageOriginalLayer, topBorderLightLayer, topBorderDarkLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			  RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }
}
