/*
 * @(#)ColorCompositeContext.java
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
import java.awt.color.*;
import java.net.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * This implementation of the CompositeContext interface does the actual job of 
 * compositing according to the rules defined in the ColorCompositeContext.
 * See the ColorCompositeContext documentation for a definition of the different
 * composition rules.
 * 
 * @author        Vincent Hardy
 * @version       1.0, 01/28/1998
 */
public class ColorCompositeContext implements CompositeContext{
  /**
   * Composition rule
   */
  private ColorCompositeRule rule;
  
  /**
   * Transparency setting for this composite
   */
  private float alpha;

  /**
   * ColorModel for source
   */
  private ColorModel srcColorModel;

  /**
   * ColorModel for destination
   */
  private ColorModel dstColorModel;

  /**
   * Source ColorSpace
   */
  private ColorSpace srcColorSpace;

  /**
   * Destination ColorSpace
   */
  private ColorSpace dstColorSpace;

  /**
   * @param rule ColorCompositeRule definition
   * @param alpha transparency
   * @param srcColorModel color model for source raster
   * @param dstColorModel color model for destination raster
   */
  public ColorCompositeContext(ColorCompositeRule rule,
			       float alpha,
			       ColorModel srcColorModel,
			       ColorModel dstColorModel,
			       RenderingHints hints){
    if(rule==null || srcColorModel==null || dstColorModel==null)
      throw new IllegalArgumentException("No argument should be null");

    this.rule = rule;
    this.alpha = alpha;
    this.srcColorModel = srcColorModel;
    this.dstColorModel = dstColorModel;
    this.srcColorSpace = srcColorModel.getColorSpace();
    this.dstColorSpace = dstColorModel.getColorSpace();
  }

  /**
   * Releases resources allocated for a context.
   */
  public void dispose(){
  }

  
  /**
   * Alternate method for Compositing HSV components, which works in sRGB.
   * The process is as follows:
   * a. Convert src and destination to sRGB DirectColorModel if needed
   * b. Process each pixel:
   *      + Convert src and dst to hsv
   *      + Combine pixels
   *      + Convert back to hsv
   * c. Convert destination to color model if needed.
   * 
   */
  public void compose(Raster src,
		      Raster dstIn,
		      WritableRaster dstOut){
    //
    // Raster data. For clarity
    // Choose the minimum width/height to copy
    //
    int x = src.getMinX();
    int y = src.getMinY();
    int w = src.getWidth();
    if (w > dstIn.getWidth()) w = dstIn.getWidth();
    int h = src.getHeight();
    if (h > dstIn.getHeight()) h = dstIn.getHeight();

    //
    // First, convert to sRGB if necessary
    //
    ColorModel srgbCM = ColorModel.getRGBdefault();
    boolean srcNeedsConvert = !srcColorModel.equals(srgbCM);
    if(srcNeedsConvert){
      System.out.println("Converting Source");
      WritableRaster newSrc = srgbCM.createCompatibleWritableRaster(w, h);
      ColorConvertOp srcTosRGB = new ColorConvertOp(srcColorSpace, srgbCM.getColorSpace(), null);
      srcTosRGB.filter(src, newSrc);
      src = newSrc.createWritableTranslatedChild(x, y);
    }

    boolean dstNeedsConvert = !dstColorModel.equals(srgbCM);
    WritableRaster dstOutSav = dstOut;
    if(dstNeedsConvert){
      System.out.println("Converting Destination");
      WritableRaster newDstIn = srgbCM.createCompatibleWritableRaster(w, h);
      ColorConvertOp dstTosRGB = new ColorConvertOp(dstColorSpace, srgbCM.getColorSpace(), null);
      dstTosRGB.filter(dstIn, newDstIn);
      dstIn = newDstIn.createWritableTranslatedChild(x, y);

      WritableRaster newDstOut = srgbCM.createCompatibleWritableRaster(w, h);
      dstTosRGB.filter(dstOut, newDstOut);
      dstOut = newDstOut.createWritableTranslatedChild(x, y);
    }

    TimeProbe probe = new TimeProbe();

    //
    // Now, access the data banks for faster processing. Because we are sure
    // that we are using the DirectColorModel, we know that the internal DataBuffers
    // are DataBufferInt and have a single bank.
    // Because the relevant buffers may not start at index 0, take the (x, y)
    // starting offset into account. Furthermore, the scanline stride may be bigger than
    // the rasters width (in case the raster is a child raster, for example). Therefore,
    // we remember to use the scanline stride to adjust offsets.
    //
    DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
    DataBufferInt dstInDB = (DataBufferInt)dstIn.getDataBuffer();
    DataBufferInt dstOutDB = (DataBufferInt)dstOut.getDataBuffer();
    int srcRGB[] = srcDB.getBankData()[0];
    int dstInRGB[] = dstInDB.getBankData()[0];
    int dstOutRGB[] = dstOutDB.getBankData()[0];
    int srcOffset = srcDB.getOffset();
    int dstInOffset = dstInDB.getOffset();
    int dstOutOffset = dstOutDB.getOffset();
    int srcScanStride = ((SinglePixelPackedSampleModel)src.getSampleModel()).getScanlineStride();
    int dstInScanStride = ((SinglePixelPackedSampleModel)dstIn.getSampleModel()).getScanlineStride();
    int dstOutScanStride = ((SinglePixelPackedSampleModel)dstOut.getSampleModel()).getScanlineStride();
    int srcAdjust = srcScanStride-w;
    int dstInAdjust = dstInScanStride-w;
    int dstOutAdjust = dstOutScanStride-w;
    
    //
    // Now, iterate through the buffer data.
    // 
    int sr=0, sg=0, sb=0, sa=0; // src rgb pixel values
    float sHsv[] = new float[3]; // src hsv pixel values
    int dir=0, dig=0, dib=0, dia=0; // dstIn rgb pixel values
    float diHsv[] = new float[3]; // dstIn hsv pixel values
    int dor=0, dog=0, dob=0; // dstOut rgb pixel values
    float doHsv[] = new float[3]; // dstOut hsv pixel values
    int sRGB=0, diRGB=0, doRGB=0;
    int si = srcOffset;
    int dii = dstInOffset;
    int doi = dstOutOffset;
    int ruleValue = rule.toInt();
    float a=0, ac=0; // Alpha values
    float alpha = this.alpha; // Stack variables provide faster access than member variables
    for(int i=0; i<h; i++){ // Line iteration
      for(int j=0; j<w; j++){ // Column iteration
	sRGB = srcRGB[si];
	diRGB = dstInRGB[dii];

	// Extract Source RGB
	sa = (sRGB >> 24) & 0xff;
	sr = (sRGB >> 16) & 0xff;
	sg = (sRGB >>  8) & 0xff;
	sb = (sRGB      ) & 0xff;

	// Extract Destination RGB
	dia = diRGB & 0xff000000;
	dir = (diRGB >> 16) & 0xff;
	dig = (diRGB >>  8) & 0xff;
	dib = (diRGB      ) & 0xff;

	// Sourcr RGB -> HSV
	Color.RGBtoHSB(sr, sg, sb, sHsv);

	// Destination RGB -> HSV
	Color.RGBtoHSB(dir, dig, dib, diHsv);

	// Do compositing here
	switch(ruleValue){
	case ColorCompositeRule.COLOR_COMPOSITE_HUE:
	  doHsv[0] = sHsv[0];
	  doHsv[1] = diHsv[1];
	  doHsv[2] = diHsv[2];
	  break;
	case ColorCompositeRule.COLOR_COMPOSITE_SATURATION:
	  doHsv[0] = diHsv[0];
	  doHsv[1] = sHsv[1];
	  doHsv[2] = diHsv[2];
	  break;
	case ColorCompositeRule.COLOR_COMPOSITE_BRIGHTNESS:
	  doHsv[0] = diHsv[0];
	  doHsv[1] = diHsv[1];
	  doHsv[2] = sHsv[2];
	  break;
	case ColorCompositeRule.COLOR_COMPOSITE_COLOR: 
	  doHsv[0] = sHsv[0];
	  doHsv[1] = sHsv[1];
	  doHsv[2] = diHsv[2];

	  /*
	  if(diHsv[1]>.5f)
	    doHsv[1] = 2*sHsv[1] + 2*diHsv[1] -2*diHsv[1]*sHsv[1] -1;
	  else
	    doHsv[1] = 2*diHsv[1]*sHsv[1];

	  if(diHsv[2]>.5f)
	    doHsv[2] = 2*sHsv[2] + 2*diHsv[2] -2*diHsv[2]*sHsv[2] -1;
	  else
	    doHsv[2] = 2*diHsv[2]*sHsv[2];
	    */
	  break;
	default:
	  throw new Error("Unknown ColorCompositeRule: " + rule);
	}

	// Get dstOut rgb values
	doRGB = Color.HSBtoRGB(doHsv[0], doHsv[1], doHsv[2]);
	dor = (doRGB&0xff0000)>>16;
	dog = (doRGB&0xff00)>>8;
	dob = (doRGB&0xff);

	// Now, take alpha into account
	a = alpha*sa/255f;
	ac = 1-a;
	dor = (int)(a*dor + ac*dir); 
	dog = (int)(a*dog + ac*dig); 
	dob = (int)(a*dob + ac*dib); 
	doRGB = ((dor<<16)&0xff0000) | ((dog<<8)&0xff00) | dob;

	// Restaure alpha: dstOut alpha is unchanged
	doRGB |= dia;

	// Write dstOut pixel value
	dstOutRGB[doi] = doRGB;

	// Move to next pixel
	si++;
	dii++;
	doi++;
      }

      si += srcAdjust;
      dii += dstInAdjust;
      doi += dstOutAdjust;
    }

    // System.out.println(probe.traceTime("Compositing main loop took: " ));

    //
    // Convert to dstOut ColorModel if necessary
    //
    if(dstNeedsConvert){
      System.out.println("Converting Destination");
      ColorConvertOp srgbToDst = new ColorConvertOp(srgbCM.getColorSpace(), dstColorSpace, null);
      srgbToDst.filter(dstOut, dstOutSav);
    }
  }


}
