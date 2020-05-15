/*
 * @(#)NeonGlowMenuComposition.java
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
public class NeonGlowMenuComposition implements CompositionFactory{
  String menuLabels = "";

  File imageFile = new File("");

  File imageMaskFile = new File("");

  Color backgroundColor = Color.black;

  Color neonOuterColor = Color.red;

  Color neonInnerColor = Color.yellow;

  Dimension margins = new Dimension(40, 40);

  int menuImageGap = 20;

  Font menuLabelsFont = new Font("Impact MT", Font.PLAIN, 80);

  TextAlignment menuLabelsAlignment = TextAlignment.LEFT;

  int neonRadius = 6;

  float glowScale = 1.5f;

  Color menuLabelsColor = Color.black;

  Color backgroundTint = Color.gray;

  Color backgroundTextureColor = Color.white;

  String backgroundTextureText = "Texture";

  Font backgroundTextureFont = new Font("Impact MT", Font.PLAIN, 80);

  public Color getBackgroundTextureColor(){
    return backgroundTextureColor;
  }

  public void setBackgroundTextureColor(Color backgroundTextureColor){
    this.backgroundTextureColor = backgroundTextureColor;
  }

  public String getBackgroundTextureText(){
    return backgroundTextureText;
  }

  public void setBackgroundTextureText(String backgroundTextureText){
    this.backgroundTextureText = backgroundTextureText;
  }

  public Font getBackgroundTextureFont(){
    return backgroundTextureFont;
  }

  public void setBackgroundTextureFont(Font backgroundTextureFont){
    this.backgroundTextureFont = backgroundTextureFont;
  }

  public Color getBackgroundTint(){
    return backgroundTint;
  }

  public void setBackgroundTint(Color backgroundTint){
    this.backgroundTint = backgroundTint;
  }

  public Color getMenuLabelsColor(){
    return menuLabelsColor;
  }

  public void setMenuLabelsColor(Color menuLabelsColor){
    this.menuLabelsColor = menuLabelsColor;
  }

  public float getGlowScale(){
    return glowScale;
  }

  public void setGlowScale(float glowScale){
    this.glowScale = glowScale;
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

  public int getNeonRadius(){
    return neonRadius;
  }

  public void setNeonRadius(int neonRadius){
    this.neonRadius = neonRadius;
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

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Color getNeonInnerColor(){
    return neonInnerColor;
  }

  public void setNeonInnerColor(Color neonInnerColor){
    this.neonInnerColor = neonInnerColor;
  }

  public Color getNeonOuterColor(){
    return neonOuterColor;
  }

  public void setNeonOuterColor(Color neonOuterColor){
    this.neonOuterColor = neonOuterColor;
  }


  public Composition build(){
    //
    // First, load images
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load : " + imageFile);

    BufferedImage imageMask = Toolbox.loadImage(imageMaskFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(imageMask==null)
      throw new Error("Could not load : " + imageMaskFile);


    //
    // Build a text block with all the menu items
    //
    Shape textBlock = TextLayer.makeTextBlock(menuLabels, menuLabelsFont, -1, menuLabelsAlignment);
    
    //
    // Compute Composition size based on the base elements
    //
    Rectangle textBlockBounds = textBlock.getBounds();
    Dimension size = new Dimension();
    size.width = (int)Math.max(textBlockBounds.width, image.getWidth()) + 2*margins.width;
    size.height = textBlockBounds.height + menuImageGap + image.getHeight() + 2*margins.height;
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    //
    // Create the background texture layer
    //
    Shape textureShape = TextLayer.makeTextBlock(backgroundTextureText,
						 backgroundTextureFont);
    Rectangle textureShapeBounds = textureShape.getBounds();

    // Rescale texture Shape so that it is as wide as the composition
    float textureScale= size.width/(float)textureShapeBounds.width;
    AffineTransform scale = AffineTransform.getScaleInstance(textureScale, textureScale);
    textureShape = scale.createTransformedShape(textureShape);

    // PositionShape at the bottom
    textureShape = Position.BOTTOM.createTransformedShape(textureShape, cmp.getBounds());
    textureShapeBounds = textureShape.getBounds();

    // Now, concatenate the Shape several times so as to fill the background
    GeneralPath backgroundShape = new GeneralPath(textureShape);
    int curY = size.height - textureShapeBounds.height;
    AffineTransform textureMover = new AffineTransform();
    while(curY>0){
      textureMover.translate(0, -textureShapeBounds.height);
      backgroundShape.append(textureMover.createTransformedShape(textureShape), false);
      curY -= textureShapeBounds.height;
    }

    // Build ShapeLayer with our backgroundShape
    ShapeLayer backgroundLayer = new ShapeLayer(cmp, backgroundShape, new FillRenderer(backgroundTextureColor));

    // Set the backgroundLayer's mask
    BufferedImage backgroundMask = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g = backgroundMask.createGraphics();
    GradientPaintExt maskPaint 
      = new GradientPaintExt(0, 0, 0, size.height, 
			     new float[]{3, 1}, 
			     new Color[]{Color.black, Color.darkGray, Color.white});
    g.setPaint(maskPaint);
    g.fillRect(0, 0, size.width, size.height);

    backgroundLayer.setLayerMask(backgroundMask);

    //
    // Position text in composition
    //
    Position menuPosition = new Position(Anchor.TOP, 0, margins.height);
    textBlock = menuPosition.createTransformedShape(textBlock, cmp.getBounds());

    //
    // Create text neon glow : the neon effect is created by 
    // stacking up two different glows that have different colors.
    //

    // Create inner blur
    boolean useScaleTxf = (neonRadius > 12);
    float blurScale = 12f/neonRadius;
    
    Dimension innerBlurMargins = new Dimension(neonRadius,
					       neonRadius);
    Dimension outerBlurMargins = new Dimension(neonRadius*2,
					       neonRadius*2);

    if(useScaleTxf)
      neonRadius = 12;

    GaussianKernel innerBlurKernel = new GaussianKernel(neonRadius/2);
    ConvolveOp innerBlur = new ConvolveOp(innerBlurKernel);
    RescaleOp innerAlphaScale 
      = new RescaleOp(
		      new float[]{glowScale, glowScale, glowScale, glowScale},
		      new float[]{0, 0, 0, 0},
		      null
		      );
      
      // Create outer blur
    GaussianKernel outerBlurKernel = new GaussianKernel(neonRadius);
    ConvolveOp outerBlur = new ConvolveOp(outerBlurKernel);
    RescaleOp outerAlphaScale 
      = new RescaleOp(
		      new float[]{glowScale, glowScale, glowScale, glowScale},
		      new float[]{0, 0, 0, 0},
		      null
		      );
      
    CompositeOp outerGlowFilter = null;
    CompositeOp innerGlowFilter = null;

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

      innerGlowFilter = new CompositeOp(new BufferedImageOp[]{shrinkGlow, innerBlur, blowUpGlow, innerAlphaScale});
      outerGlowFilter = new CompositeOp(new BufferedImageOp[]{shrinkGlow, outerBlur, blowUpGlow, outerAlphaScale});
    }
    else{
      innerGlowFilter = new CompositeOp(innerBlur, innerAlphaScale);
      outerGlowFilter = new CompositeOp(outerBlur, outerAlphaScale);
    }
      


    FillRenderer innerGlowRenderer = new FillRenderer(neonInnerColor);
    ShapeLayer neonInnerTextLayer = new ShapeLayer(cmp, textBlock, innerGlowRenderer);
    neonInnerTextLayer.setImageFilter(innerGlowFilter, innerBlurMargins);

    FillRenderer outerGlowRenderer = new FillRenderer(neonOuterColor);
    ShapeLayer neonOuterTextLayer = new ShapeLayer(cmp, textBlock, outerGlowRenderer);
    neonOuterTextLayer.setImageFilter(outerGlowFilter, outerBlurMargins);

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
    BufferedImage imageInnerGlow = new BufferedImage(imageMask.getWidth(), imageMask.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
    g = imageInnerGlow.createGraphics();
    g.drawImage(imageLayer.getLayerMask(), 0, 0, null);
    g.setComposite(AlphaComposite.SrcIn);
    g.setPaint(neonInnerColor);
    g.fillRect(0, 0, imageMask.getWidth(), imageMask.getHeight());
    g.dispose();

    BufferedImage imageOuterGlow = new BufferedImage(imageMask.getWidth(), imageMask.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
    g = imageOuterGlow.createGraphics();
    g.drawImage(imageLayer.getLayerMask(), 0, 0, null);
    g.setComposite(AlphaComposite.SrcIn);
    g.setPaint(neonOuterColor);
    g.fillRect(0, 0, imageMask.getWidth(), imageMask.getHeight());

    ImageLayer neonInnerImageLayer = new ImageLayer(cmp, imageInnerGlow, imagePosition);    
    neonInnerImageLayer.setImageFilter(innerGlowFilter, innerBlurMargins);

    ImageLayer neonOuterImageLayer = new ImageLayer(cmp, imageOuterGlow, imagePosition);    
    neonOuterImageLayer.setImageFilter(outerGlowFilter, outerBlurMargins);

    //
    // Add a final layer to paint the labels
    //
    ShapeLayer menuLabelsLayer = new ShapeLayer(cmp, textBlock, new FillRenderer(menuLabelsColor));

    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{backgroundLayer, 
				 neonOuterTextLayer,
				 neonInnerTextLayer,
				 menuLabelsLayer,
				 neonOuterImageLayer, 
				 neonInnerImageLayer,
				 imageLayer});

    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }
}

