/*
 * @(#)ToneAdjustmentOp.java
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
import java.awt.color.*;
import java.awt.event.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * This filter provides a way to modify the tones of a gray scale image
 * to a color gradient. The idea is to replace, for example the midtone, 
 * highlight and shadow grays with specific colors. This filter is 
 * commonly used to create dramatic effects on pictures, such as 
 * antiquing.<br>
 * This filter performs color interpolation is the sRGB color space.<br>
 * 
 * @author          Vincent Hardy
 * @version         1.0 06/02/1999
 */
public class ToneAdjustmentOp implements BufferedImageOp {
  /**
   * This table maps gray scale intensity to rgb intensity
   */
  private static int gsToRGB[];

  static{
    gsToRGB = new int[256];
    ColorSpace gscs = ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_GRAY);
    for(int gs=0; gs<256; gs++){
      float rgb[] = gscs.toRGB(new float[]{gs/255f});
      gsToRGB[gs] = (int)(Math.rint(255*rgb[0]));
    }
  }

  /**
   * Color model used to adjust the filtered image tones.
   */
  private IndexColorModel colorModel;

  /**
   * Controls what format should be used for null filtering destination:
   * if true, the same format as the source is used. Otherwise, the internal
   * color model is used.
   */
  private boolean nullDestAsSource = false;

  /**
   * @param midTone the midtone color that should be used.
   */
  public ToneAdjustmentOp(Color midTone){
    this(new Color[]{Color.black, midTone, Color.white}, new float[]{1, 1}, true);
  }

  /**
   * @param shadowTone the tone that should be used for shadows
   * @param midTone the tone that should be used for mid-tone
   * @param highlightTone the tone that shoudl be used for hightlights
   */
  public ToneAdjustmentOp(Color shadowTone, Color midTone, Color highlightTone){
    this(new Color[]{Color.black, shadowTone, midTone, highlightTone, Color.white}, new float[]{1, 1, 1, 1}, true);
  }

  /**
   * General purpose constructor
   * 
   * @param tones array of color values that should replace the input image tones.
   * @param toneRanges array of range for the input values
   * @param black color that should replace black
   * @param white color that should replace white
   * 
   * @exception IllegalArgumentException if tones.length != toneRanges.length-1
   */
  public ToneAdjustmentOp(Color tones[], float toneRanges[]){
    this(tones, toneRanges, false);
  }

  /**
   * General purpose constructor
   * 
   * @param tones array of color values that should replace the input image tones.
   * @param toneRanges array of range for the input values
   * @param black color that should replace black
   * @param white color that should replace white
   * @param nullDestAsSource if true and if a null destination is given to the filter method,
   *        the destination is created in the same format as the source. Otherwise, the filters
   *        internal color model is used (which is more efficient).
   * 
   * @exception IllegalArgumentException if tones.length != toneRanges.length-1
   */
  public ToneAdjustmentOp(Color tones[], float toneRanges[], boolean nullDestAsSource){
    if(toneRanges==null || tones==null)
      throw new IllegalArgumentException("tones and toneRanges should not be null");

    if(toneRanges.length != tones.length-1)
      throw new IllegalArgumentException("tones length and toneRanges length to not match. tones should have one more elements than toneRanges");
    this.nullDestAsSource = nullDestAsSource;

    // Check tone values
    Color tmpTones[] = new Color[tones.length];
    System.arraycopy(tones, 0, tmpTones, 0, tones.length);
    tones = tmpTones;
    for(int i=0; i<tones.length; i++)
      if(tones[i]==null)
	throw new IllegalArgumentException("Cannot use null tone value. Element " + i + " is null");    

    // Normalize interval array and convert to number of elements 
    // to be used by interval.
    float tmp[] = new float[toneRanges.length];
    System.arraycopy(toneRanges, 0, tmp, 0, toneRanges.length);
    float sum = 0;
    for(int i=0; i<tmp.length; i++){
      if(tmp[i]<0)
	throw new IllegalArgumentException("Cannot use negative intervals : " + i + " is " + tmp[i]);

      sum += tmp[i];
    }

    if(sum==0)
      throw new IllegalArgumentException("Interval ranges should not all be zero");

    float newSum = 0;
    for(int i=0; i<tmp.length; i++){
      tmp[i] /= sum;
      tmp[i] *= 256;
      tmp[i] = (int)tmp[i];
      newSum += tmp[i];
    }

    if(newSum>256)
      throw new Error("Internal error");

    if(newSum<256)
      tmp[tmp.length-1] += (256-newSum);

    toneRanges = tmp;

    //
    // Create a color map based on the input tones
    //
    int colorMap[] = new int[256];
    int startIndex = 0;
    int endIndex = 0;
    for(int i=0; i<toneRanges.length; i++){
      int rangeLength = (int)toneRanges[i];

      endIndex = startIndex + rangeLength;
      int startRGB = tones[i].getRGB();
      int endRGB = tones[i+1].getRGB();

      int sr = (startRGB & 0xff0000)>>16;
      int sg = (startRGB & 0x00ff00)>>8;
      int sb = (startRGB & 0x0000ff);

      int er = (endRGB & 0xff0000)>>16;
      int eg = (endRGB & 0x00ff00)>>8;
      int eb = (endRGB & 0x0000ff);

      float dr = (er-sr);
      float dg = (eg-sg);
      float db = (eb-sb);

      if(rangeLength>1){
	dr /= (rangeLength-1);
	dg /= (rangeLength-1);
	db /= (rangeLength-1);
      }

      int r = 0, g = 0, b = 0;
      for(int j=0; j<rangeLength; j++){
	r = sr + (int)(dr*j);
	g = sg + (int)(dg*j);
	b = sb + (int)(db*j);

	colorMap[startIndex+j] = 0xff000000 | (r<<16) | (g<<8) | b;
      }
      
      startIndex = endIndex;
    }

    //
    // Now, create an IndexColorModel that uses the color map
    //
    colorModel = new IndexColorModel(8,                    // 8 bits per pixel
				     256,                  // 256 possible values (0-255)
				     colorMap,             // Color map
				     0,                    // Start index in the color map
				     false,                // No alpha: all values are opaque
				     (-1),                 // No fully transparent pixel
				     DataBuffer.TYPE_BYTE);// Values stored as bytes

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

    // First, convert src image to gray scale if necessary
    BufferedImage origSrc = src;
    if(src.getType() != BufferedImage.TYPE_BYTE_GRAY){
      BufferedImage tmp = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D g = tmp.createGraphics();
      g.drawImage(src, 0, 0, null);
      g.dispose();
      src = tmp;
    }

    // Reuse the source gray scale raster to create the destination image.
    // Note that at this point, we have made sure we are dealing with a 
    // gray scale byte image, therefore we can safely use the source Raster.
    BufferedImage adjustedSource = new BufferedImage(colorModel, src.getRaster(), true, null);

    // Now, write result to destination image. 
    if(nullDestAsSource)
      dest = createCompatibleDestImage(origSrc, null);

    if(dest==null)
      dest = adjustedSource;
    else{
      Graphics2D g = dest.createGraphics();
      g.drawImage(adjustedSource, 0, 0, null);
      g.dispose();
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
   * Creates a destination image compatible with the source.
   */
  public BufferedImage createCompatibleDestImage (BufferedImage src,
						  ColorModel destCM){
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

  public static final String USAGE = "java com.sun.glf.goodies.ToneAdjustmentOp <imageFileName>";

  /**
   * Unit testing
   */
  public static void main(String args[]){
    BufferedImage graySource = null, rgbSource = null, rgbGraySource;

    // Check input argument
    if(args.length < 1){
      // Build images
      graySource = new BufferedImage(256, 20, BufferedImage.TYPE_BYTE_GRAY);
      rgbSource = new BufferedImage(256, 20, BufferedImage.TYPE_INT_RGB);

      // Draw gradients
      Graphics2D g = graySource.createGraphics();
      GradientPaint paint = new GradientPaint(0, 0, Color.black, graySource.getWidth(), 0, Color.white);
      g.setPaint(paint);
      g.fillRect(0, 0, 256, 20);

      g = rgbSource.createGraphics();
      g.setPaint(paint);
      g.fillRect(0, 0, 256, 20);

    }
    else{
      String imageFileName = args[0];
      
      // Load input image as gray scale
      graySource = Toolbox.loadImage(imageFileName, BufferedImage.TYPE_BYTE_GRAY);
      
      // Load input image as rgb
      rgbSource = Toolbox.loadImage(imageFileName, BufferedImage.TYPE_INT_RGB);
      
      if(graySource==null || rgbSource==null)
	throw new Error("Could not load : " + imageFileName);
    }

    rgbGraySource = new BufferedImage(256, 20, BufferedImage.TYPE_BYTE_GRAY);
    ColorConvertOp colConvert = new ColorConvertOp(null);
    colConvert.filter(rgbSource, rgbGraySource);

    /*Color tones[][] = { { Color.black, Color.white},
			{ new Color(20, 5, 40), Color.red, new Color(220, 200, 255), new Color(255, 255, 200) },
			{ Color.white, Color.gray, Color.black} };
    float ranges[][] = { {1},
			 {0.1f, 0.001f, 0.1f},
			 {2, 1} };*/
    Color tones[][] = { { Color.black, Color.white},                                           // Unmodified
			{ Color.black, Color.red, Color.white},                                // Red midtone
			{ Color.black, Color.blue, Color.white},                               // Blue midtone
			{ Color.black, new Color(128, 30, 30), Color.yellow, Color.white },    // shadow and hightone
			{ Color.white, Color.black }                                           // Invert
    };
    float ranges[][] = { {1},
			 {1, 1},
			 {1, 1},
			 {1, 1, 1},
			 {1} };

    JPanel content = new JPanel();
    content.setLayout(new GridLayout(0, 1));

    int iw = graySource.getWidth(), ih = graySource.getHeight();
    Dimension size = new Dimension(iw*3, ih);


    LayerComposition cmp = new LayerComposition(size);
    ImageLayer layers[] = new ImageLayer[3];

    layers[0] = new ImageLayer(cmp, graySource, Position.TOP_LEFT);
    layers[1] = new ImageLayer(cmp, rgbSource, Position.TOP);
    layers[2] = new ImageLayer(cmp, rgbGraySource, Position.TOP_RIGHT);
    cmp.setLayers(layers);
    content.add(new CompositionComponent(cmp));

    for(int i=0; i<tones.length; i++){
      cmp = new LayerComposition(size);
      layers = new ImageLayer[3];      
      layers[0] = new ImageLayer(cmp, graySource, Position.TOP_LEFT);
      layers[1] = new ImageLayer(cmp, rgbSource, Position.TOP);
      layers[2] = new ImageLayer(cmp, rgbGraySource, Position.TOP_RIGHT);
      ToneAdjustmentOp filter = new ToneAdjustmentOp(tones[i], ranges[i]);
      layers[0].setImageFilter(filter);
      layers[1].setImageFilter(filter);
      layers[2].setImageFilter(filter);
      cmp.setLayers(layers);
      content.add(new CompositionComponent(cmp));
    }

    JFrame frame = new JFrame("ToneAdjustmentOp regression testing");
    frame.getContentPane().add(new JScrollPane(content));
    frame.pack();

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    frame.setVisible(true);
  }
}

