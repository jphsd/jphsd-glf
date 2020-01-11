/*
 * @(#)CompositeRasterOp.java
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

import java.awt.geom.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * A CompositeOp allows chaining of filters. By combining several 
 * RasterOp in a CompositeOp, it is possible to create a 
 * filter pipeline.
 *
 * @author             Vincent Hardy
 * @version            1.0, 06/5/1999
 */
public class CompositeRasterOp implements RasterOp {
  /** Filter chain */
  RasterOp rasterFilters[];

  /**
   * @param firstFilter first filter in the chain
   * @param secondFilter second filter in the filter chain
   */
  public CompositeRasterOp(RasterOp firstFilter, RasterOp secondFilter){
    this(new RasterOp[]{firstFilter, secondFilter});
  }

  /**
   * @param rasterFilters set of filters which should be combined into a single
   *        chain.
   */
  public CompositeRasterOp(RasterOp rasterFilters[]){
    if(rasterFilters==null || rasterFilters.length<1)
      throw new IllegalArgumentException();

    synchronized(rasterFilters){
      this.rasterFilters = new RasterOp[rasterFilters.length];
      System.arraycopy(rasterFilters, 0, this.rasterFilters, 0, rasterFilters.length);
    }

    for(int i=0; i<this.rasterFilters.length; i++)
      if(this.rasterFilters[i]==null)
	throw new IllegalArgumentException();
  }

  /**
   * Performs a single-input/single-output operation on a Raster.
   * Note that we make sure to set the proper origin in the destination rasters
   */
  public WritableRaster filter(Raster src, WritableRaster dest){
    // Working destination raster
    WritableRaster tmpDest = null;

    for(int i=0; i<rasterFilters.length-1; i++){
      // Get destination bounding rectangle
      Rectangle srcRect = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
      Rectangle2D dstRect = rasterFilters[i].getBounds2D(src);

      // Filter now
      tmpDest = rasterFilters[i].filter(src, null);

      // Force origin in destination raster
      tmpDest = tmpDest.createWritableTranslatedChild((int)dstRect.getX(), (int)dstRect.getY());

      src = tmpDest;
    }

    // dest = tmpDest;
    return rasterFilters[rasterFilters.length-1].filter(src, dest);
  }
  
  /**
   * Returns the bounding box of the filtered destination Raster.
   * This uses the getPoint2D method of each filter to process
   * the bounds of the resulting image. Note that this may fail
   * for filters which would transform pixels in a non-linear way.
   * <p> 
   * The IllegalArgumentException may be thrown if the source
   * image is incompatible with the types of images allowed
   * by the class implementing the first filter in the chain.
   */
  public Rectangle2D getBounds2D (Raster src){
    Rectangle2D bounds2D = rasterFilters[0].getBounds2D(src);
    double xMin=0, yMin=0, xMax=0, yMax=0;
    for(int i=1; i<rasterFilters.length; i++){
      // Use getPoint2D to get the transformed bounds
      Point2D tl = new Point2D.Double(bounds2D.getX(), bounds2D.getY());
      Point2D tr = new Point2D.Double(bounds2D.getX()+bounds2D.getWidth(), bounds2D.getY());
      Point2D br = new Point2D.Double(bounds2D.getX()+bounds2D.getWidth(), bounds2D.getY() + bounds2D.getHeight());
      Point2D bl = new Point2D.Double(bounds2D.getX(), bounds2D.getY() + bounds2D.getHeight());
      tl = rasterFilters[i].getPoint2D(tl, null);
      tr = rasterFilters[i].getPoint2D(tr, null);
      br = rasterFilters[i].getPoint2D(br, null);
      bl = rasterFilters[i].getPoint2D(bl, null);

      Point2D cur = tl;
      Point2D pts[] = {tr, br, bl};
      xMin = cur.getX();
      yMin = cur.getY();
      xMax = xMin;
      yMax = yMin;
      for(int j=0; j<3; j++){
	xMin = Math.min(xMin, pts[j].getX());
	yMin = Math.min(yMin, pts[j].getY());
	xMax = Math.max(xMax, pts[j].getX());
	yMax = Math.max(yMax, pts[j].getY());
      }

      bounds2D = new Rectangle2D.Double(xMin, yMin, xMax-xMin, yMax-yMin);
    }
    return bounds2D;
  }
  
  /**
   * 
   * @param src       Source Raster for the filter operation.
   */
  public WritableRaster createCompatibleDestRaster(Raster src){
    return rasterFilters[rasterFilters.length-1].createCompatibleDestRaster(src);
  }
  
  /**
   * Returns the location of the destination point given a
   * point in the source image.  If dstPt is non-null, it
   * will be used to hold the return value.
   */
  public Point2D getPoint2D (Point2D srcPt, Point2D dstPt){
    for(int i=0; i<rasterFilters.length; i++)
      dstPt = rasterFilters[i].getPoint2D(srcPt, dstPt);
    return dstPt;
  }
  
  /**
   * Returns the rendering hints for this RasterOp.  Returns
   * null if no hints have been set.
   */
  public RenderingHints getRenderingHints(){
    return rasterFilters[rasterFilters.length-1].getRenderingHints();
  }
}
