/*
 * @(#)LightPaintingComposition.java
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
 * This example illustrates how to create different 3D effects with
 * LightOp.
 *
 * @author       Vincent Hardy
 * @version      1.0, 06/04/1999
 */
public class LightPaintingComposition implements CompositionFactory{
  File imageFile = new File("");

  String text = "Walk the Talk";

  Font textFont = new Font("serif", Font.BOLD, 80);

  Glyph glyph = new Glyph(new Font("serif", Font.PLAIN, 200), '@');

  int symbolTextGap = 20;

  int bannerGap = 20;

  Dimension margins = new Dimension(30, 30);

  Dimension lightOffset = new Dimension(-20, -20);

  Anchor lightAnchor = Anchor.TOP_LEFT;

  Dimension lightSize = new Dimension(300, 800);

  Color lightColor = Color.white;

  int textureBlurRadius = 5;

  float ambiantLight = 0.2f;

  float lightIntensity = 1.5f;

  int heightScale = 10;

  Color extrudedColor = Color.white;

  Color embossedColor = Color.black;

  LitSurfaceType surfaceType = LitSurfaceType.NORMAL;

  float carvedRedScale = 1f;

  float carvedGreenScale = 1f;

  float carvedBlueScale = 1f;

  Color backgroundColor = Color.white;

  boolean extrudeOrPunch = false;

  public boolean getExtrudeOrPunch(){
    return extrudeOrPunch;
  }

  public void setExtrudeOrPunch(boolean extrudeOrPunch){
    this.extrudeOrPunch = extrudeOrPunch;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public float getCarvedRedScale(){
    return carvedRedScale;
  }

  public void setCarvedRedScale(float carvedRedScale){
    this.carvedRedScale = carvedRedScale;
  }

  public float getCarvedGreenScale(){
    return carvedGreenScale;
  }

  public void setCarvedGreenScale(float carvedGreenScale){
    this.carvedGreenScale = carvedGreenScale;
  }

  public float getCarvedBlueScale(){
    return carvedBlueScale;
  }

  public void setCarvedBlueScale(float carvedBlueScale){
    this.carvedBlueScale = carvedBlueScale;
  }

  public LitSurfaceType getSurfaceType(){
    return surfaceType;
  }

  public void setSurfaceType(LitSurfaceType surfaceType){
    this.surfaceType = surfaceType;
  }
  
  public Color getExtrudedColor(){
    return extrudedColor;
  }

  public void setExtrudedColor(Color extrudedColor){
    this.extrudedColor = extrudedColor;
  }

  public Color getEmbossedColor(){
    return embossedColor;
  }

  public void setEmbossedColor(Color embossedColor){
    this.embossedColor = embossedColor;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public Glyph getGlyph(){
    return glyph;
  }

  public void setGlyph(Glyph glyph){
    this.glyph = glyph;
  }

  public int getSymbolTextGap(){
    return symbolTextGap;
  }

  public void setSymbolTextGap(int symbolTextGap){
    this.symbolTextGap = symbolTextGap;
  }

  public int getBannerGap(){
    return bannerGap;
  }

  public void setBannerGap(int bannerGap){
    this.bannerGap = bannerGap;
  }

  public Dimension getMargins(){
    return margins;
  }

  public void setMargins(Dimension margins){
    this.margins = margins;
  }

  public Dimension getLightOffset(){
    return lightOffset;
  }

  public void setLightOffset(Dimension lightOffset){
    this.lightOffset = lightOffset;
  }

  public Anchor getLightAnchor(){
    return lightAnchor;
  }

  public void setLightAnchor(Anchor lightAnchor){
    this.lightAnchor = lightAnchor;
  }

  public Dimension getLightSize(){
    return lightSize;
  }

  public void setLightSize(Dimension lightSize){
    this.lightSize = lightSize;
  }

  public Color getLightColor(){
    return lightColor;
  }

  public void setLightColor(Color lightColor){
    this.lightColor = lightColor;
  }

  public int getTextureBlurRadius(){
    return textureBlurRadius;
  }

  public void setTextureBlurRadius(int textureBlurRadius){
    this.textureBlurRadius = textureBlurRadius;
  }

  public float getAmbiantLight(){
    return ambiantLight;
  }

  public void setAmbiantLight(float ambiantLight){
    this.ambiantLight = ambiantLight;
  }

  public float getLightIntensity(){
    return lightIntensity;
  }

  public void setLightIntensity(float lightIntensity){
    this.lightIntensity = lightIntensity;
  }

  public int getHeightScale(){
    return heightScale;
  }

  public void setHeightScale(int heightScale){
    this.heightScale = heightScale;
  }

  public Composition build(){
    //
    // Load background image texture
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB_PRE);
    if(image==null)
      throw new Error("Could not load : " + imageFile);

    //
    // Build the base Shape that is used to create embossing, carving
    // and extrusion effects.
    //
    Shape symbol = glyph.getShape();
    Shape textShape = TextLayer.makeTextBlock(text, textFont);
    Position symbolPosition = new Position(Anchor.LEFT, symbolTextGap, 0);
    symbol = symbolPosition.createTransformedShape(symbol, textShape.getBounds());
    GeneralPath bannerShape = new GeneralPath(textShape);
    bannerShape.append(symbol, false);

    //
    // Build LayerComposition. Compute composition size based on bannerShape
    //
    Rectangle bounds = bannerShape.getBounds();
    Dimension size = new Dimension(bounds.width + 2*margins.width,
				   bounds.height*3 + 2*bannerGap + 2*margins.height);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    // 
    // Fit image to composition size
    //
    if(image.getWidth()>size.width)
      image = image.getSubimage(0, 0, size.width, image.getHeight());
    if(image.getHeight()>size.height)
      image = image.getSubimage(0, 0, image.getWidth(), size.height);
    
    AffineTransform textureScale = AffineTransform.getScaleInstance(size.width/(float)image.getWidth(),
								    size.height/(float)image.getHeight());
    AffineTransformOp textureScaler = new AffineTransformOp(textureScale, AffineTransformOp.TYPE_BILINEAR);
    image = textureScaler.filter(image, null);

    //
    // Create three Shapes, one for each banner
    //
    Position embossedPosition = new Position(Anchor.TOP_LEFT, margins.width, margins.height);
    Shape embossedBannerShape = embossedPosition.createTransformedShape(bannerShape, cmp.getBounds());
    
    Position carvedPosition = new Position(Anchor.LEFT, margins.width, 0);
    Shape carvedBannerShape = carvedPosition.createTransformedShape(bannerShape, cmp.getBounds());
    
    Position extrudedPosition = new Position(Anchor.BOTTOM_LEFT, margins.width, margins.height);
    Shape extrudedBannerShape = extrudedPosition.createTransformedShape(bannerShape, cmp.getBounds());
    
    //
    // Build Lights and ElevationMaps that we will use to create different LightOps
    //
    Rectangle lightRect = cmp.getBounds();
    lightRect.x = lightOffset.width;
    lightRect.y = lightOffset.height;
    SpotLight spot = LightsStudio.getSpotLight(lightRect, 
					       lightAnchor, 
					       lightSize,
					       lightIntensity, lightColor);

    // Background surface
    ElevationMap extrudedMap = buildExtrudedMap(size, extrudedBannerShape, textureBlurRadius, heightScale, extrudeOrPunch);
    LitSurface backgroundSurface = new LitSurface(ambiantLight, surfaceType, extrudedMap);
    backgroundSurface.addLight(spot);

    // Embossed text surface
    ElevationMap embossedMap = new ElevationMap(embossedBannerShape, textureBlurRadius, true, heightScale);
    LitSurface embossedBannerSurface = new LitSurface(ambiantLight, LitSurfaceType.NORMAL, embossedMap);
    embossedBannerSurface.addLight(spot);

    // Carved text surface
    ElevationMap carvedMap = new ElevationMap(carvedBannerShape, textureBlurRadius, false, heightScale);
    LitSurface carvedBannerSurface = new LitSurface(ambiantLight, LitSurfaceType.NORMAL, carvedMap);
    carvedBannerSurface.addLight(spot);

    // 
    // Build extruded background
    //
    ImageLayer backgroundLayer = new ImageLayer(cmp, image);
    backgroundLayer.setRasterFilter(new LightOp(backgroundSurface));

    //
    // Build carved banner
    //
    TexturePaint texturePaint = new TexturePaint(image, cmp.getBounds());
    FillRenderer texturePainter = new FillRenderer(texturePaint);
    ShapeLayer carvedBannerLayer = new ShapeLayer(cmp, carvedBannerShape.getBounds(), texturePainter);
    LightOp carver = new LightOp(carvedBannerSurface);
    RescaleOp scaleOp = new RescaleOp(new float[]{carvedRedScale, carvedGreenScale, carvedBlueScale, 1}, new float[]{0, 0, 0, 0}, null);
    CompositeRasterOp carvedFilter = new CompositeRasterOp(scaleOp, carver);
    carvedBannerLayer.setRasterFilter(carvedFilter);
    carvedBannerLayer.setLayerMask(carvedBannerShape);

    //
    // Build embossed banner
    //
    ShapeLayer embossedBannerLayer = new ShapeLayer(cmp, embossedBannerShape.getBounds(), texturePainter);
    embossedBannerLayer.setLayerMask(embossedBannerShape);
    LightOp embosser = new LightOp(embossedBannerSurface);
    embossedBannerLayer.setRasterFilter(embosser);

    //
    // Extruded adjustment layer
    //
    ShapeLayer extrudedAdjustmentLayer = new ShapeLayer(cmp, extrudedBannerShape, new FillRenderer(extrudedColor));

    //
    // Embossed stand out
    //
    ShapeLayer embossedStandOutLayer = new ShapeLayer(cmp, embossedBannerShape, new StrokeRenderer(embossedColor, textureBlurRadius));
    ConvolveOp blur = new ConvolveOp(new GaussianKernel(textureBlurRadius));
    embossedStandOutLayer.setImageFilter(blur, new Dimension(textureBlurRadius*2, textureBlurRadius*2));

    cmp.setLayers(new Layer[]{backgroundLayer, extrudedAdjustmentLayer, 
				carvedBannerLayer, embossedStandOutLayer, 
				embossedBannerLayer});

    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }

  private ElevationMap buildExtrudedMap(Dimension size, 
					Shape shape, 
					int blurRadius, 
					int heightScale,
					boolean extrudeOrPunch){
    // Build a BufferedImage large enough for the extrusion blur
    BufferedImage buffer = new BufferedImage(size.width, 
					     size.height,
					     BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g = buffer.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		       RenderingHints.VALUE_ANTIALIAS_ON);

    g.setPaint(Color.white);
    g.fill(shape);
    g.dispose();

    // Blur
    ConvolveOp blur = new ConvolveOp(new GaussianKernel(blurRadius));
    buffer = blur.filter(buffer, null);

    // Now, make a 'clean' shape
    g = buffer.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		       RenderingHints.VALUE_ANTIALIAS_ON);
    g.setPaint(Color.white);
    g.fill(shape);
    g.dispose();

    return new ElevationMap(buffer, extrudeOrPunch, heightScale);
  }
}
