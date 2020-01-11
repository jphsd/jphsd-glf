/*
 * @(#)LightOp.java
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

package com.sun.glf.goodies;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * This class provides a way to modify the brighness of a BufferedImage
 * based on a light description. The BufferedImage is interpreted as a
 * lit surface described by a LitSurface object. This object describes
 * things such as the surface terrain (i.e. elevations), the surface 
 * nature (matte or shiny) and the set of lights which are shining on
 * it. 
 * <p>
 * This filter uses the light intensity on the surface to modify the 
 * brightness of the filtered image pixels.
 *
 * @author         Vincent Hardy
 * @version        1.0, 09/29/1998
 */
public class LightOp implements BufferedImageOp, RasterOp{
  /** 
   * Compatible color models
   */
  private static ColorModel xrgbCM = new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);
  private static ColorModel srgbCM = ColorModel.getRGBdefault();

  /** 
   * Minimum intensity level, i.e. intensity under which intensity 
   * is considered zero.
   */
  private static final double I0 = 0.1;
    
  /** Lighting conditions */
  private LitSurface litSurface;

  /**
   * @param litSurface describes the lighting conditions the filter should use
   */
  public LightOp(LitSurface litSurface){
    if(litSurface==null)
      throw new IllegalArgumentException();

    this.litSurface = litSurface;
  }

  /**
   * Filters src and writes result into dest. If dest is null, then an image
   * is created. If dest and src refer to the same object, then the source is
   * modified.
   * <p>
   * The filter operates on the pixels brightness and operates in the RGB color
   * space.
   *
   * @param src the Raster to be filtered
   * @param dest the filtered image. If null, a destination will be created.
   *        src and dest can refer to the same Raster.
   */
  public WritableRaster filter(Raster src, WritableRaster dest){
    //
    // First, check input arguments
    // 
    checkCompatible(src.getSampleModel());
    if(dest!=null) checkCompatible(dest.getSampleModel());
    else dest = createCompatibleDestRaster(src);

    //
    // Now, filter src.
    //
    int w = src.getWidth();
    int h = src.getHeight();

    // Access the integer buffer for each image.
    DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
    DataBufferInt dstDB = (DataBufferInt)dest.getDataBuffer();
    int srcOff = srcDB.getOffset();
    int dstOff = dstDB.getOffset();
    int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
    int dstScanStride = ((SinglePixelPackedSampleModel)dest.getSampleModel()).getScanlineStride();
    int srcPixels[] = srcDB.getBankData()[0];
    int destPixels[] = dstDB.getBankData()[0];
    int srcAdjust = srcScanStride - w;
    int dstAdjust = dstScanStride - w;
    int minX = src.getMinX();
    int minY = src.getMinY();

    // System.out.println("Source Image     : " + srcPixels.length  + " / " + srcOff + " / " + srcScanStride);
    // System.out.println("Destination Image: " + destPixels.length + " / " + dstOff + " / " + dstScanStride + " : " + dest.getWidth() + " by " + dest.getHeight());
    // System.out.println("w/h = " + w + " / " + h);

    // Filter now
    int r=0, g=0, b=0;
    int p=0, sp=srcOff, dp=dstOff;
    double I[] = {0,0,0};
    double Iwork[] = {0,0,0};
    double nBylnI0 = 256./Math.log(I0);
    double drm=0, dgm=0, dbm=0;
    int rm=0, gm=0, bm=0;
    int pel = 0;

    if(litSurface.isTextured()){
      for(int i=0; i<h; i++){
	for(int j=0; j<w; j++){
	  pel = srcPixels[sp];
	  r = (pel>>16)&0xff;
	  g = (pel>>8)&0xff;
	  b = pel&0xff;

	  litSurface.getTexturedIntensity(j + minX, i + minY, I, Iwork);

	  // Get intensity modification for each color component
	  drm = nBylnI0*Math.log(I[0]);
	  dgm = nBylnI0*Math.log(I[1]);
	  dbm = nBylnI0*Math.log(I[2]);

	  // Round off to int values, taking sign into account
	  rm = drm%1.>.5?(drm>0?((int)drm) + 1:((int)drm)-1):(int)drm;
	  gm = dgm%1.>.5?(dgm>0?((int)dgm) + 1:((int)dgm)-1):(int)dgm;
	  bm = dbm%1.>.5?(dbm>0?((int)dbm) + 1:((int)dbm)-1):(int)dbm;

	  r = r-rm;
	  r = r<0?0:r;
	  r = r>255?255:r;

	  g = g-gm;
	  g = g<0?0:g;
	  g = g>255?255:g;

	  b = b-bm;
	  b = b<0?0:b;
	  b = b>255?255:b;

	  // Modify RGB value. Alpha is left unchanged, which
	  // covers the two supported color models.
	  destPixels[dp] = pel&0xff000000 | (r<<16 & 0xff0000) | (g<<8 & 0xff00) | (b & 0xff);
	  dp++; sp++;
	}
	dp += dstAdjust;
	sp += srcAdjust;
      }
    }
    else{
      // Surface is flat
      for(int i=0; i<h; i++){
	for(int j=0; j<w; j++){
	  pel = srcPixels[sp];
	  r = (pel>>16)&0xff;
	  g = (pel>>8)&0xff;
	  b = pel&0xff;

	  litSurface.getFlatIntensity(j + minX, i + minY, I, Iwork);

	  drm = nBylnI0*Math.log(I[0]);
	  dgm = nBylnI0*Math.log(I[1]);
	  dbm = nBylnI0*Math.log(I[2]);

	  rm = drm%1.>.5?(drm>0?((int)drm) + 1:((int)drm)-1):(int)drm;
	  gm = dgm%1.>.5?(dgm>0?((int)dgm) + 1:((int)dgm)-1):(int)dgm;
	  bm = dbm%1.>.5?(dbm>0?((int)dbm) + 1:((int)dbm)-1):(int)dbm;

	  r = r-rm;
	  r = r<0?0:r;
	  r = r>255?255:r;

	  g = g-gm;
	  g = g<0?0:g;
	  g = g>255?255:g;

	  b = b-bm;
	  b = b<0?0:b;
	  b = b>255?255:b;

	  // Modify RGB value. Alpha is left unchanged, which
	  // covers the two supported color models.
	  destPixels[dp] = pel&0xff000000 | (r<<16 & 0xff0000) | (g<<8 & 0xff00) | (b & 0xff);
	  dp++; sp++;
	}
	dp += dstAdjust;
	sp += srcAdjust;
      }
    }

    return dest;
  }

  /**
   * Filters src and writes result into dest. If dest is null, then an image
   * is created. If dest and src refer to the same object, then the source is
   * modified.
   * <p>
   * The filter operates on the pixels brightness and operates in the RGB color
   * space.
   *
   * @param src the image to be filtered
   * @param dest the filtered image. If null, a destination will be created.
   *        src and dest can refer to the same image.
   */
  public BufferedImage filter(BufferedImage src, BufferedImage dest){
    if (src == null)
      throw new NullPointerException("src image is null");

    // First, convert src image if necessary
    SampleModel model = src.getSampleModel();
    if(!isCompatible(model)){
      BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
      ColorConvertOp toRGB = new ColorConvertOp(null);
      toRGB.filter(src, tmp);
      src = tmp;
    }

    // Now, check destination. If compatible, it is use as is. Otherwise
    // a temporary image is used.
    BufferedImage finalDest = dest;
    if(dest==null){
      dest = createCompatibleDestImage(src, null);
      finalDest = dest;
    }
    else{
      // Check that the destination ColorModel is compatible
      SampleModel destModel = dest.getSampleModel();
      if(!isCompatible(destModel))
	dest = createCompatibleDestImage(src, null);
    }

    // We now have two compatible images. We can safely filter the source
    filter(src.getRaster(), dest.getRaster());

    // If we had to use a temporary destination, copy the result into the
    // real output image
    if(dest != finalDest){
      ColorConvertOp toDestCM = new ColorConvertOp(null);
      toDestCM.filter(dest, finalDest);
    }
    
    return dest;
  }

  /**
   * Returns the bounding box of the filtered destination image.
   * The IllegalArgumentException may be thrown if the source
   * image is incompatible with the types of images allowed
   * by the class implementing this filter.
   */
  public Rectangle2D getBounds2D(BufferedImage src){
    return new Rectangle(0, 0, src.getWidth(), src.getHeight());
  }

  /**
   * @return the bounding box of the filtered destination raster
   * The IllegalArgumentException is thrown if source image is not compatible.
   *
   * @return #checkCompatible
   */
  public Rectangle2D getBounds2D(Raster src){
    checkCompatible(src.getSampleModel());
    return new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
  }


  /**
   * Creates a zeroed destination Raster with the correct size and number 
   * of bands, given this source.
   * @see #checkCompatible
   */
  public WritableRaster createCompatibleDestRaster(Raster src) {
    checkCompatible(src.getSampleModel());
    // Src Raster is OK: create a similar Raster for destination.
    return src.createCompatibleWritableRaster();

  }

  /**
   * Checks that input Raster is compatible with LightOp. Throws an IllegalArgumentException
   * if not.
   * The LightOp filter only operates on SinglePixelPackedSampleModels with
   * 3 or 4 bands, each band using 8 bits and pixels packed into integers.
   * 
   * @param model the input SampleModel
   * @throws IllegalArgumentException if Raster does not have the expected structure (See above
   *         description).
   */
  public void checkCompatible(SampleModel model){
    // Check model is ok: should be SinglePixelPackedSampleModel
    if(!(model instanceof SinglePixelPackedSampleModel))
      throw new IllegalArgumentException("LightOp only works with Rasters using SinglePixelPackedSampleModels");

    // Check number of bands
    int nBands = model.getNumBands();
    if(nBands<3 || nBands>4)
      throw new IllegalArgumentException("LightOp only words with Rasters having 3 or 4 bands");

    // Check that integer packed.
    if(model.getDataType()!=DataBuffer.TYPE_INT)
      throw new IllegalArgumentException("LightOp only works with Rasters using DataBufferInts");

    // Check bit masks
    int bitOffsets[] = ((SinglePixelPackedSampleModel)model).getBitOffsets();
    for(int i=0; i<bitOffsets.length; i++){
      if(bitOffsets[i]%8 != 0)
	throw new IllegalArgumentException("LightOp only works with Rasters using 8 bits per band : " + i + " : " + bitOffsets[i]);
    }
  }

  /**
   * Checks that input Raster is compatible with LightOp. Throws an IllegalArgumentException
   * if not.
   * The LightOp filter only operates on SinglePixelPackedSampleModels with
   * 3 or 4 bands, each band using 8 bits and pixels packed into integers.
   * 
   * @param model the input SampleModel
   * @return true if compatible, false otherwise.
   */
  public boolean isCompatible(SampleModel model){
    // Check model is ok: should be SinglePixelPackedSampleModel
    if(!(model instanceof SinglePixelPackedSampleModel))
      return false;

    // Check number of bands
    int nBands = model.getNumBands();
    if(nBands<3 || nBands>4)
      return false;

    // Check that integer packed.
    if(model.getDataType()!=DataBuffer.TYPE_INT)
      return false;

    // Check bit masks
    int bitOffsets[] = ((SinglePixelPackedSampleModel)model).getBitOffsets();
    for(int i=0; i<bitOffsets.length; i++){
      if(bitOffsets[i]%8 != 0)
	return false;
    }
    
    return true;
  }

  /**
   * If destCM is xrgb or srgb or destCM is null and src's ColorModel
   * is srgb or xrgb, then this will successfully create a compatible 
   * image. Otherwise, an IllegalArgumentException will be thrown.
   */
  public BufferedImage createCompatibleDestImage (BufferedImage src,
						  ColorModel destCM){
    checkCompatible(src.getSampleModel());
    
    BufferedImage dest = null;
    if(destCM==null)
      destCM = src.getColorModel();

    dest = new BufferedImage(destCM, destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
			     destCM.isAlphaPremultiplied(), null);

    return dest;
  }

  /**
   * Returns the location of the destination point given a
   * point in the source image.  If DestPt is non-null, it
   * will be used to hold the return value.
   */
  public Point2D getPoint2D (Point2D srcPt, Point2D destPt){
    // This operation does not affect pixel location
    if(destPt==null)
      destPt = new Point2D.Double();
    destPt.setLocation(srcPt.getX(), srcPt.getY());
    return destPt;
  }

  /**
   * Returns the rendering hints for this BufferedImageOp.  Returns
   * null if no hints have been set.
   */
  public RenderingHints getRenderingHints(){
    return null;
  }

  //
  // Unit testing
  //
  static final String USAGE = "java com.sun.glf.goodies.LightOp [<textureFile>]";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    String textureFileName = args[0];

    // 
    // Create a frame where the different results obtained
    // with LightOps will be displayed
    //
    JFrame frame = new JFrame("LightOp unit testing");
    frame.getContentPane().setLayout(new GridLayout(0, 1));

    BufferedImage textureImage = Toolbox.loadImage(textureFileName, BufferedImage.TYPE_INT_RGB);

    // Create a source image
    int w = 200;
    int h = 100;
    Dimension dim = new Dimension(w, h);
    BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
    Graphics2D g = src.createGraphics();
    g.setPaint(new Color(153, 153, 204));
    g.fillRect(0, 0, w, h);

    // Filter flat image.
    LitSurface litSurface = new LitSurface(.2, 1, 1, 16, null);
    litSurface.addLight( new SpotLight(new Rectangle(0, 0, w, h), new Color(255, 120, 120), 8., 0) );
    LightOp op = new LightOp(litSurface);
    /* BufferedImage flat= op.createCompatibleDestImage(src, null);
    WritableRaster sr = src.getRaster();
    WritableRaster dr = flat.getRaster();
    sr = sr.createWritableChild(w/2, h/2, w/2, h/2, w/2, h/2, new int[] { 0, 1, 2 });
    dr = dr.createWritableChild(w/2, h/2, w/2, h/2, w/2, h/2, new int[] { 0, 1, 2 });
    op.filter(sr, dr);*/
    BufferedImage flat = op.filter(src, null);

    // Filter using an elevation map
    BufferedImage texture=null, textured=null;

    if(textureImage != null){
      texture = new BufferedImage(textureImage.getWidth(null),
				  textureImage.getHeight(null),
				  BufferedImage.TYPE_INT_RGB);
      g = texture.createGraphics();
      g.drawImage(textureImage, 0, 0, null);
      ElevationMap map = new ElevationMap(texture, true, 10);
      litSurface = new LitSurface(.2, 1, 1, 16, map);
      litSurface.addLight( new SpotLight(new Rectangle(0, 0, w, h), Color.white, 2., 0.) );
      op = new LightOp(litSurface);
      textured = op.filter(src, null);
    }
    else{
      Font font = new Font("Times New Roman", Font.BOLD, 40);
      FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
      Shape shape = font.createGlyphVector(frc, "&").getOutline();
      Rectangle bounds = shape.getBounds();
      AffineTransform t = AffineTransform.getTranslateInstance(-bounds.x, -bounds.y);
      shape = t.createTransformedShape(shape);
      ElevationMap map = new ElevationMap(shape, 10, true, 10);
      litSurface = new LitSurface(.2, 1, 1, 16, map);
      litSurface.addLight( new SpotLight(new Rectangle(0, 0, w, h), Color.white, 2., 0.) );
      op = new LightOp(litSurface);
      textured = op.filter(src, null);
      texture = map.makeTexture(shape, 10); // , true, 10); //  map.texture;
    }

    int tw = texture.getWidth();
    int th = texture.getHeight();
    if(tw>w)
      texture = texture.getSubimage(0, 0, w, th);
    if(th>h)
      texture = texture.getSubimage(0, 0, w, h);

    // Filter using an elevationMap and directional light
    litSurface.removeAllLights();
    litSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, .5, Color.white));
    op = new LightOp(litSurface);
    BufferedImage directional = op.filter(src, null);

    // Filter using multiple light sources
    /*litSurface.addLight( new SpotLight(new Rectangle(0, -h/2, w, h), Color.yellow, 2., Math.PI/4) );
    litSurface.addLight( new SpotLight(new Rectangle(-w, -h/2, w, h), Color.green, 2., -5*Math.PI/4) );
    litSurface.addLight( new SpotLight(new Rectangle(-w, h/2, w, h), Color.red, 2., -3*Math.PI/4) );
    litSurface.addLight( new SpotLight(new Rectangle(0, h/2, w, h), Color.white, 2., -Math.PI/4) );*/
    Rectangle litRect = new Rectangle(0, 0, w, h);
    Dimension lightSize = new Dimension(w, h);
    litSurface.addLight(LightsStudio.getSpotLight(litRect, Anchor.TOP_LEFT, lightSize, 2., Color.yellow));
    litSurface.addLight(LightsStudio.getSpotLight(litRect, Anchor.TOP_RIGHT, lightSize, 2., Color.green));
    litSurface.addLight(LightsStudio.getSpotLight(litRect, Anchor.BOTTOM_LEFT, lightSize, 2., Color.red));
    litSurface.addLight(LightsStudio.getSpotLight(litRect, Anchor.TOP_RIGHT, lightSize, 2., Color.white));

    BufferedImage multiple = op.filter(src, null);

    frame.getContentPane().add(makeNewComponent(src, "Original Image"));
    frame.getContentPane().add(makeNewComponent(flat, "SpotLight on flat surface"));
    frame.getContentPane().add(makeNewComponent(texture, "Elevation map used to create texture"));
    frame.getContentPane().add(makeNewComponent(textured, "WarnLlight on textured surface"));
    frame.getContentPane().add(makeNewComponent(directional, "DirectionalLight on textured surface"));
    frame.getContentPane().add(makeNewComponent(multiple, "WarnLigths & DirectionalLight on textured surface"));

    frame.pack();
    frame.setVisible(true);
  }

  private static JComponent makeNewComponent(Image image, String toolTip){
    Dimension dim = new Dimension(image.getWidth(null),
				  image.getHeight(null));
    LayerComposition cmp = new LayerComposition(dim);
    ImageLayer layer = new ImageLayer(cmp, image, null);
    cmp.setLayers(new Layer[]{layer});
    CompositionComponent comp = new CompositionComponent(cmp);
    comp.setToolTipText(toolTip);
    return comp;
  }
}
