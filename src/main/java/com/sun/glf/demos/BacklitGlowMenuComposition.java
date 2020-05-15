/*
 * @(#)BacklitGlowMenuComposition.java
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

package com.sun.glf.demos;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import java.awt.color.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This example illustrates how to create a backlit glow effect. This is illustrated
 * for text and images.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/06/1999
 */
public class BacklitGlowMenuComposition implements CompositionFactory{
  String menuLabels = "";

  File imageFile = new File("");

  File imageMaskFile = new File("");

  File backgroundImageFile = new File("");

  Color backlitColor = Color.orange;

  Dimension margins = new Dimension(40, 40);

  int menuImageGap = 20;

  Font menuLabelsFont = new Font("Impact MT", Font.PLAIN, 80);

  TextAlignment menuLabelsAlignment = TextAlignment.LEFT;

  int backlitRadius = 6;

  float menuLabelsScale = 1.1f;

  float glowScale = 1.5f;

  Color textureMaskColor = Color.black;

  public Color getTextureMaskColor(){
    return textureMaskColor;
  }

  public void setTextureMaskColor(Color textureMaskColor){
    this.textureMaskColor = textureMaskColor;
  }

  public float getGlowScale(){
    return glowScale;
  }

  public void setGlowScale(float glowScale){
    this.glowScale = glowScale;
  }

  public float getMenuLabelsScale(){
    return menuLabelsScale;
  }

  public void setMenuLabelsScale(float menuLabelsScale){
    this.menuLabelsScale = menuLabelsScale;
  }

  public Dimension getMargins(){
    return margins;
  }

  public void setMargins(Dimension margins){
    this.margins = margins;
  }

  public int getMenuImageGap(){
    return menuImageGap;
  }

  public void setMenuImageGap(int menuImageGap){
    this.menuImageGap = menuImageGap;
  }

  public Font getMenuLabelsFont(){
    return menuLabelsFont;
  }

  public void setMenuLabelsFont(Font menuLabelsFont){
    this.menuLabelsFont = menuLabelsFont;
  }

  public TextAlignment getMenuLabelsAlignment(){
    return menuLabelsAlignment;
  }

  public void setMenuLabelsAlignment(TextAlignment menuLabelsAlignment){
    this.menuLabelsAlignment = menuLabelsAlignment;
  }

  public int getBacklitRadius(){
    return backlitRadius;
  }

  public void setBacklitRadius(int backlitRadius){
    this.backlitRadius = backlitRadius;
  }

  public String getMenuLabels(){
    return menuLabels;
  }

  public void setMenuLabels(String menuLabels){
    this.menuLabels = menuLabels;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public File getImageMaskFile(){
    return imageMaskFile;
  }

  public void setImageMaskFile(File imageMaskFile){
    this.imageMaskFile = imageMaskFile;
  }

  public File getBackgroundImageFile(){
    return backgroundImageFile;
  }

  public void setBackgroundImageFile(File backgroundImageFile){
    this.backgroundImageFile = backgroundImageFile;
  }

  public Color getBacklitColor(){
    return backlitColor;
  }

  public void setBacklitColor(Color backlitColor){
    this.backlitColor = backlitColor;
  }


  public Composition build(){
    //
    // First, load images
    //
    BufferedImage backgroundImage = Toolbox.loadImage(backgroundImageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(backgroundImage==null)
      throw new Error("Could not load : " + backgroundImageFile);

    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load : " + imageFile);

    BufferedImage imageMask = Toolbox.loadImage(imageMaskFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(imageMask==null)
      throw new Error("Could not load : " + imageMaskFile);

    //
    // Build a text block with all the menu items
    //
    int doNotWrap = -1;
    Shape textBlock = TextLayer.makeTextBlock(menuLabels, menuLabelsFont, doNotWrap, menuLabelsAlignment);
    
    //
    // Compute Composition size based on the base elements
    //
    Rectangle textBlockBounds = textBlock.getBounds();
    Dimension size = new Dimension();
    size.width = (int)Math.max(textBlockBounds.width, image.getWidth()) + 2*margins.width;
    size.height = textBlockBounds.height + menuImageGap + image.getHeight() + 2*margins.height;
    LayerComposition cmp = new LayerComposition(size);

    // 
    // Fit background image to composition size
    //
    if(backgroundImage.getWidth()>size.width)
      backgroundImage = backgroundImage.getSubimage(0, 0, size.width, backgroundImage.getHeight());
    if(backgroundImage.getHeight()>size.height)
      backgroundImage = backgroundImage.getSubimage(0, 0, backgroundImage.getWidth(), size.height);
    
    AffineTransform backgroundScale = AffineTransform.getScaleInstance(size.width/(float)backgroundImage.getWidth(),
								       size.height/(float)backgroundImage.getHeight());
    AffineTransformOp backgroundScaler = new AffineTransformOp(backgroundScale, AffineTransformOp.TYPE_BILINEAR);
    backgroundImage = backgroundScaler.filter(backgroundImage, null);
    
    // Create texture paint with background image
    TexturePaint texturePaint = new TexturePaint(backgroundImage,
						 cmp.getBounds());

    //
    // Position text in composition
    //
    Position menuPosition = new Position(Anchor.TOP, 0, margins.height);
    textBlock = menuPosition.createTransformedShape(textBlock, cmp.getBounds());

    //
    // Create background layer
    //
    // ImageLayer backgroundLayer = new ImageLayer(cmp, backgroundImage);
    cmp.setBackgroundPaint(texturePaint);

    //
    // Create text backlit glow
    //
    FillRenderer glowRenderer = new FillRenderer(backlitColor);
    ShapeLayer backlitTextLayer = new ShapeLayer(cmp, textBlock, glowRenderer);

    // Create blur
    boolean useScaleTxf = (backlitRadius>12);
    float blurScale = 12f/backlitRadius;

    Dimension blurMargins = new Dimension(backlitRadius*2,
					  backlitRadius*2);

    if(useScaleTxf)
      backlitRadius = 12;

    GaussianKernel blurKernel = new GaussianKernel(backlitRadius);
    ConvolveOp blur = new ConvolveOp(blurKernel);

    RescaleOp alphaScale 
      = new RescaleOp(
		      new float[]{glowScale, glowScale, glowScale, glowScale},
		      new float[]{0, 0, 0, 0},
		      null
		      );
    
    CompositeOp glowFilter = null;

    if(useScaleTxf){
      AffineTransform shrinkGlowTxf 
	= AffineTransform.getScaleInstance(blurScale, 
					   blurScale);
      AffineTransform blowUpGlowTxf 
	= AffineTransform.getScaleInstance(1/blurScale, 
					   1/blurScale);

      AffineTransformOp shrinkGlow 
	= new AffineTransformOp(shrinkGlowTxf, null);
      AffineTransformOp blowUpGlow 
	= new AffineTransformOp(blowUpGlowTxf, AffineTransformOp.TYPE_BILINEAR);

      glowFilter = new CompositeOp(new BufferedImageOp[]{shrinkGlow, blur, blowUpGlow, alphaScale});
    }
    else{
      glowFilter = new CompositeOp(blur, alphaScale);
    }

    backlitTextLayer.setImageFilter(glowFilter, blurMargins);

    //
    // Create adjusted text menu labels
    //
    ShapeLayer menuLabelsLayer = new ShapeLayer(cmp, textBlock, new FillRenderer(texturePaint));
    RescaleOp brightener 
      = new RescaleOp(
		      new float[]{menuLabelsScale, menuLabelsScale, menuLabelsScale, 1}, 
		      new float[]{0, 0, 0, 0}, null);

    menuLabelsLayer.setImageFilter(brightener);

    //
    // Create image layer
    //
    Position imagePosition = new Position(Anchor.BOTTOM, 0, margins.height);
    ImageLayer imageLayer = new ImageLayer(cmp, image, imagePosition);

    // Set mask
    Point imageLeftCorner = new Point(0, 0);
    imageLayer.getTransform().transform(imageLeftCorner, imageLeftCorner);
    Rectangle imageRect = new Rectangle(imageLeftCorner.x, imageLeftCorner.y,
					imageMask.getWidth(), imageMask.getHeight());
    imageLayer.setLayerMask(imageMask, imageRect);
    
    //
    // Create image glow
    //
    BufferedImage imageGlow = new BufferedImage(imageMask.getWidth(), imageMask.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g = imageGlow.createGraphics();
    g.drawImage(imageLayer.getLayerMask(), 0, 0, null);
    g.setComposite(AlphaComposite.SrcIn);
    g.setPaint(backlitColor);
    g.fillRect(0, 0, imageMask.getWidth(), imageMask.getHeight());

    ImageLayer backlitImageLayer = new ImageLayer(cmp, imageGlow, imagePosition);    
    backlitImageLayer.setImageFilter(glowFilter, blurMargins);

    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{ backlitTextLayer,
				 menuLabelsLayer,
				 backlitImageLayer, 
				 imageLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }
}

