/*
 * @(#)RadialGradientPaintContext.java
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
import java.awt.image.*;
import java.awt.geom.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/** The gradient value in a point is: 
 *  (1/ma)*( Math.sqrt( (f1.x - x)*(f1.x - x) + (f1.y - y)*(f1.y - y)) +
 *           Math.sqrt( (f1.x - x)*(f1.x - x) + (f1.y - y)*(f1.y - y)) )
 *
 * Where ma is the ellipse's major axis.<br>
 * Note that the gradient value is processed in user space.<br>
 *
 * Because the processing bottle neck is the square root calculation, the
 * following code interpolates square root values as follows:<br>
 * + A square root lookup table of MAX_PRECISION size contains the square
 *   root values for square values between 0 and 1<br>
 * + To process the distance to a focal point, the square distance is 
 *   processed and divided by the maximum square distance value. The maximum
 *   square distance is for the gradient's ellipse bounding box corner the further
 *   for the focal point. The resulting square value is between 0 and 1.<br>
 * + If the square value is in the very first interpolation interval, then the 
 *   Math.sqrt method is used. This prevents the resulting disc approximation  
 *   to become visible about the focal points.<br>
 *   Otherwise, the square root lookup table is used and values are linearly 
 *   interpolated from those values.<br>
 * <br>
 * Note that with this square root approximation, the actual square root value
 * is processed for the first 0.5% of the range for which values are processed
 * (assuming a max precision of 65538). Other values are interpolated. This is
 * 0.5% of the maximum value in user space.
 * 
 * @author         Vincent Hardy
 * @version        1.1 04.06.99
 */

class RadialGradientPaintContext implements PaintContext {
  /** Used to limit the size of the square root lookup table */
  static final int MAX_PRECISION = 256;

  /** Length of a square distance intervale in the lookup table */
  static final float sqStep = 1.f/(MAX_PRECISION - 2);

  /** Square root lookup table */
  static final float sqrtLut[] = new float[MAX_PRECISION];

  static{
    /**
     * Build square root lookup table
     */
    // The last two values are the same so that linear square root interpolation
    // can happen on the maximum reachable element in the lookup table (precision-2)
    int i=0;
    for(; i<MAX_PRECISION-1; i++)
      sqrtLut[i] = (float)Math.sqrt(i*sqStep);
    sqrtLut[i] = sqrtLut[i-1];
  }

  static ColorModel xrgbmodel =
  new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff);

  /** Focal points */
  Point2D.Float f1, f2;

  /** Major axis inverse*/
  float maInv;

  /** Ellipse eccentricity */
  float e;

  /** Inverse matrix */
  double m[];
  
  /** gradient colors */
  int gradient[];

  /** Working raster */
  WritableRaster working;

  /** Color model this context uses */
  ColorModel model;

  /** Paint bounds, in device space */
  Rectangle bounds;

  /** Controls whether or not this is a circular gradient */
  boolean isCircular = false;

  /** Maximal square distance */
  float maxDistanceSq;

  public RadialGradientPaintContext(Rectangle2D.Float bounds, Color color1, Color color2, AffineTransform t)
       throws NoninvertibleTransformException{
    //
    // Process ellipse focal points, in user space
    //

    float w = bounds.width;
    float h = bounds.height;
    float x = bounds.x;
    float y = bounds.y;
    float a = 0, b = 0;
    
    // Process excentricity
    if(w>h){
      a = w/2;
      b = h/2;
    } 
    else{
      a = h/2;
      b = w/2;
    }
    this.e = (float)Math.sqrt(1 - (b*b)/(a*a));

    // Process focal points
    f1 = new Point2D.Float();
    f2 = new Point2D.Float();
    if(w>h){
      f1.x = x + a*(1+e);
      f1.y = y + b;
      f2.x = x + a*(1-e);
      f2.y = y + b;
    }
    else{
      f1.x = x + b;
      f1.y = y + a*(1+e);
      f2.x = x + b;
      f2.y = y + a*(1-e);
    }

    this.f1 = f1;
    this.f2 = f2;
    this.maInv = 1/(2*a);

    this.isCircular = bounds.width == bounds.height;
    if(this.isCircular) this.maInv = 1/a;

    // Process Paint bounds, in device space
    this.bounds = t.createTransformedShape(bounds).getBounds();

    // Store inverse matrix values
    m = new double[6];
    AffineTransform tInv = t.createInverse();
    tInv.getMatrix(m);

    int rgb1 = color1.getRGB();
    int rgb2 = color2.getRGB();    

    // Calculate delta values for each color component,
    // alpha, red, green and blue.
    int a1 = (rgb1 >> 24) & 0xff;
    int r1 = (rgb1 >> 16) & 0xff;
    int g1 = (rgb1 >>  8) & 0xff;
    int b1 = (rgb1      ) & 0xff;
    int da = ((rgb2 >> 24) & 0xff) - a1;
    int dr = ((rgb2 >> 16) & 0xff) - r1;
    int dg = ((rgb2 >>  8) & 0xff) - g1;
    int db = ((rgb2      ) & 0xff) - b1;

    // Use least expensive color model: if both colors
    // are opaque, only use 3 bands, RGB. Otherwise,
    // use the default RGB model.
    if (((rgb1 & rgb2) >>> 24) == 0xff) {
      model = xrgbmodel;
    } else {
      model = ColorModel.getRGBdefault();
    }

    // Preprocess an array of all the variations
    // between color1 and color2.
    gradient = new int[256];
    int rgb = 0;
    for (int i = 0; i <= 255; i++) {
      float rel = i / 255.0f;
      rgb =
	(((int) (a1 + da * rel)) << 24) |
	(((int) (r1 + dr * rel)) << 16) |
	(((int) (g1 + dg * rel)) <<  8) |
	(((int) (b1 + db * rel))      );
      gradient[i] = rgb;
    }

    
    // Scale maInv so that gradient varies between e*gradientIndex and gradientIndex
    int gradientMaxIndex = gradient.length - 1;
    maInv *= gradientMaxIndex;

    // We use the bounds of the ellipse in device space. The following is needed to 'know'
    // the maximum distance in user space.
    Rectangle2D maxBounds = tInv.createTransformedShape(t.createTransformedShape(bounds).getBounds()).getBounds2D();
    maxDistanceSq = (float)f1.distanceSq(maxBounds.getX(), maxBounds.getY());
    float maxDistance = (float)Math.sqrt(maxDistanceSq);

    // Square values are computed between 0 and 1 (i.e. (d*d)/maxDistanceSq)
    // Multiply by maxDistance to scale back to max value
    maInv *= maxDistance;
    
  }

  public void dispose() {
    working = null;
  }

  public ColorModel getColorModel() {
    return model;
  }

  /**
   * Return a Raster containing the colors generated for the graphics
   * operation.
   * @param x,y,w,h The area in device space for which colors are
   * generated.
   */
  public Raster getRaster(int x, int y, int w, int h) {
    // Use working raster if it is big enough to accomodate for
    // the requested area. Otherwise, create a new one.
    if (working == null || working.getWidth() < w || working.getHeight() < h)
      working = getColorModel().createCompatibleWritableRaster(w, h);

    WritableRaster raster = working;
   
    //
    // Access raster internal int array. Because we use a DirectColorModel,
    // we know the DataBuffer is of type DataBufferInt and the SampleModel
    // is SinglePixelPackedSampleModel.
    // Adjust for initial offset in DataBuffer and also for the scanline stride.
    //
    DataBufferInt rasterDB = (DataBufferInt)raster.getDataBuffer();
    int pixels[] = rasterDB.getBankData()[0];
    int off = rasterDB.getOffset();
    int scanlineStride = ((SinglePixelPackedSampleModel)raster.getSampleModel()).getScanlineStride();
    int adjust = scanlineStride - w;

    if(!isCircular)
      fillRasterEllipse(pixels, off, adjust, x, y, w, h);
    else
      fillRasterDisc(pixels, off, adjust, x, y, w, h);      

    return raster;
  }

  void fillRasterEllipse(int pixels[], int off, int adjust, int x, int y, int w, int h){
    int rgb=0;                            // Working rgb value
    int x2 = bounds.x + bounds.width;     // Rightmost x value
    int y2 = bounds.y + bounds.height;    // Rightmost y value
    int rowLimit = x + w;                 // Maximum column index value on current row   
    int lLimit = 0, rLimit = 0;           // Working left and right row limit
    int startOff = 0;                     // Starting offset value on row.
    float g = 0;                          // Current gradient value
    int j=0;                              // Current raster column
    float X=0, Y=0;                       // User space point coordinate
    float a00 = (float)m[0];              // Inverse transform matrix elements.
    float a10 = (float)m[1];
    float a01 = (float)m[2];
    float a11 = (float)m[3];
    float a02 = (float)m[4];
    float a12 = (float)m[5];
    float dX1=0, dY1=0, dX2=0, dY2=0;     // Current deltas on x and y axis, user space
    float eComp = 1-e;
    float maxGradientIndex = gradient.length-1;
    float eMaxGradientIndex = e*maxGradientIndex;
    float sqrt1 = 0, sqrt2 = 0;           // Square root approximations
    float d1Sq = 0, d2Sq = 0;             // Square distance to focal points
    float iSq1=0, iSq2 = 0;               // Square distance index
    int iSq1Int=0, iSq2Int = 0;           // Square distance index
    float p = 0;                          // Squrare root penetration in square root interval

    // Process each raster line in turn
    for(int i=0; i<h; i++){
      rowLimit = off + w;
      if(i+y<bounds.y 
	 || i+y>y2
	 || x+w<bounds.x 
	 || x>x2){
	// Line lies completely out of the bounds
	// All pixels are color2
	rgb = gradient[255];
	while(off<rowLimit)
	  pixels[off++] = rgb;
      }
      else{
	startOff = off;

	// Line intersects with the bounds

	// Color2 while out of bounds
	rgb = gradient[255];
	lLimit = off + bounds.x - x;
	rLimit = lLimit + bounds.width;
	rLimit = rLimit > rowLimit ? rowLimit : rLimit;
	while(off < lLimit)
	  pixels[off++] = rgb;

	j = (x + off - startOff);
	X = a00*j + a01*(y+i) + a02;
	Y = a10*j + a11*(y+i) + a12;
	dX1 = (f1.x - X);
	dY1 = (f1.y - Y);
	dX2 = (f2.x - X);
	dY2 = (f2.y - Y);

	// Inside bounds: color is based on distance to focal points
	while(off < rLimit){
	  d1Sq = (dX1*dX1 + dY1*dY1)/maxDistanceSq;
	  d2Sq = (dX2*dX2 + dY2*dY2)/maxDistanceSq;
	  
	  if(d1Sq < sqStep )
	    sqrt1 = (float)Math.sqrt(d1Sq);
	  else{
	    iSq1 = d1Sq/sqStep;
	    iSq1Int = (int)iSq1;
	    p = iSq1 - iSq1Int;
	    sqrt1 = p*sqrtLut[iSq1Int+1] + (1-p)*sqrtLut[iSq1Int];
	  }

	  if(d2Sq < sqStep)
	    sqrt2 = (float)Math.sqrt(d2Sq);
	  else{
	    iSq2 = d2Sq/sqStep;
	    iSq2Int = (int)iSq2;
	    p = iSq2 - iSq2Int;
	    sqrt2 = p*sqrtLut[iSq2Int+1] + (1-p)*sqrtLut[iSq2Int];
	  }
	  
	  /*
	  if(d1Sq < sqStep )
	    sqrt1 = (float)Math.sqrt(d1Sq);
	  else{
	    sqrt1 = sqrtLut[(int)(d1Sq/sqStep)];
	    sqrt1 = (sqrt1 + d1Sq/sqrt1)*.5f;
	  }

	  if(d2Sq < sqStep)
	    sqrt2 = (float)Math.sqrt(d2Sq);
	  else{
	    sqrt2 = sqrtLut[(int)(d2Sq/sqStep)];
	    sqrt2 = (sqrt2 + d2Sq/sqrt2)*.5f;
	  }
	  */
	  g = (maInv*(sqrt1 + sqrt2) - eMaxGradientIndex)/eComp;
	  
	  // If g is maxGradientIndex or more, color should be color2
	  g = g>maxGradientIndex?maxGradientIndex:g;
	  g = g<0?0:g; // Because of round off and square root approximation, this may happen
	  pixels[off++] = gradient[(int)g];

	  dX1 -= a00;
	  dX2 -= a00;
	  dY1 -= a10;
	  dY2 -= a10;
	}
	
	// Color2 for the remainder (out of bounds)
	rgb = gradient[255];
	while(off < rowLimit)
	  pixels[off++] = rgb;
      }

      off += adjust;
    }
  }

  void fillRasterDisc(int pixels[], int off, int adjust, int x, int y, int w, int h){
    int rgb=0;                            // Working rgb value
    int x2 = bounds.x + bounds.width;     // Rightmost x value
    int y2 = bounds.y + bounds.height;    // Rightmost y value
    int rowLimit = x + w;                 // Maximum column index value on current row   
    int lLimit = 0, rLimit = 0;           // Working left and right row limit
    int startOff = 0;                     // Starting offset value on row.
    float g = 0;                          // Current gradient value
    int j=0;                              // Current raster column
    float X=0, Y=0;                       // User space point coordinate
    float a00 = (float)m[0];              // Inverse transform matrix elements.
    float a10 = (float)m[1];
    float a01 = (float)m[2];
    float a11 = (float)m[3];
    float a02 = (float)m[4];
    float a12 = (float)m[5];
    float dX=0, dY=0;                     // Current deltas on x and y axis, user space
    float maxGradientIndex = gradient.length-1;
    float sqrt = 0;                       // Square root approximations
    float dSq = 0;                        // Square distance to focal points
    float iSq=0;                          // Square distance index
    int iSqInt=0;                         // Square distance index
    float p = 0;                          // Squrare root penetration in square root interval

    // Process each raster line in turn
    for(int i=0; i<h; i++){
      rowLimit = off + w;
      if(i+y<bounds.y 
	 || i+y>y2
	 || x+w<bounds.x 
	 || x>x2){
	// Line lies completely out of the bounds
	// All pixels are color2
	rgb = gradient[(int)maxGradientIndex];
	while(off<rowLimit)
	  pixels[off++] = rgb;
      }
      else{
	startOff = off;

	// Line intersects with the bounds

	// Color2 while out of bounds
	rgb = gradient[(int)maxGradientIndex];
	lLimit = off + bounds.x - x;
	rLimit = lLimit + bounds.width;
	rLimit = rLimit > rowLimit ? rowLimit : rLimit;
	while(off < lLimit)
	  pixels[off++] = rgb;

	j = (x + off - startOff);
	X = a00*j + a01*(y+i) + a02;
	Y = a10*j + a11*(y+i) + a12;
	dX = (f1.x - X);
	dY = (f1.y - Y);

	// Inside bounds: color is based on distance to focal points
	while(off < rLimit){
	  dSq = (dX*dX + dY*dY)/maxDistanceSq;
	  if(dSq < sqStep )
	    sqrt = (float)Math.sqrt(dSq);
	  else{
	    iSq = dSq/sqStep;
	    iSqInt = (int)iSq;
	    p = iSq - iSqInt;
	    sqrt = p*sqrtLut[iSqInt+1] + (1-p)*sqrtLut[iSqInt];
	  }

	  g = maInv*sqrt;

	  // If g is maxGradientIndex or more, color should be color2
	  g = g>maxGradientIndex?maxGradientIndex:g;
	  g = g<0?0:g;
	  pixels[off++] = gradient[(int)g];
	  dX -= a00;
	  dY -= a10;
	}
	
	// Color2 for the remainder (out of bounds)
	rgb = gradient[(int)maxGradientIndex];
	while(off < rowLimit)
	  pixels[off++] = rgb;
      }

      off += adjust;
    }
  }

  public static void main(String args[]){
    Rectangle gradientRect = new Rectangle(0, 0, 1000, 500);
    Rectangle fillRect = new Rectangle(0, 0, 1000, 500);
    RadialGradientPaint paint = new RadialGradientPaint(gradientRect, Color.white, Color.black);
    LayerComposition cmp = new LayerComposition(new Dimension(1000, 500));

    Layer layers[] = new Layer[3];
    for(int i=0; i<layers.length/3; i++){
      layers[3*i + 2] = new ShapeLayer(cmp, fillRect, new FillRenderer(paint));
      layers[3*i + 1] = new ShapeLayer(cmp, fillRect, new FillRenderer(new GradientPaint(0, 0, Color.white, gradientRect.width, gradientRect.height, Color.black)));
      layers[3*i] = new ShapeLayer(cmp, fillRect, new FillRenderer(new GradientPaintExt(0, 0, gradientRect.width, gradientRect.height, 
								     new float[]{1, 1},
								     new Color[]{Color.white, Color.blue, Color.black})));
    }
      

    cmp.setLayers(layers);
    CompositionFrame frame = new CompositionFrame("GradientPaintExtContext unit testing");
    frame.setComposition(cmp);
    frame.pack();
    frame.setVisible(true);    
  }
}
