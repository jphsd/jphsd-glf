/*
 * @(#)WaveTransform.java
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

/**
 * Generalizes the notion of Transformation to non affine geometrical
 * transformations. This interface allows for the definition of any arbitrary
 * type of transformation.
 *
 * @author Vincent Hardy
 * @version 1.0, 09.11.1998
 */
public class WaveTransform extends AbstractTransform{
  /**
   * Number of 'waves' to appear in the transformed shape.
   */
  int nWaves;

  /**
   * Period. Length of each wave
   */
  float waveLength;

  /**
   * Wave Height, i.e. maximum offset
   */
  float waveHeight;

  /**
   * Controls whether the transform is based on the number of waves or on the 
   * waveLength
   */
  boolean waveLengthBased;

  /**
   * @param nWaves defines the number of waves accross the width of the transformed 
   *        shape.
   * @param waveHeight height of the wave. Note that a negative value will simply 
   *        change the phase of the wave.
   */
  public WaveTransform(int nWaves, float waveHeight){
    if(nWaves<=0)
      throw new IllegalArgumentException();
    this.nWaves = nWaves;
    this.waveHeight = waveHeight;

    waveLengthBased = false;
  }

  /**
   * @param waveLength defines the length of each wave in the transform.
   */
  public WaveTransform(float waveLength, float waveHeight){
    if(waveLength<=0)
      throw new IllegalArgumentException();

    this.waveLength = waveLength;
    this.waveHeight = waveHeight;
    waveLengthBased = true;
  }

  /**
   * @param shape Shape to be transformed, excluding any pre or post transformation.
   * @see #transform
   */
  public Shape transformImpl(Shape shape){
    // Get path iterator to walk through the shape path
    PathIterator pi = shape.getPathIterator(null);

    // The sinusoidal function parameters depend on the number of 'waves' 
    // desired in the resulting shape. The 'waves' are the number of full
    // periods over the shape's width.
    // Depending on the constructor that was used, the waveLength may
    // have been specified.
    Rectangle bounds = shape.getBounds();
    float period = 0;
    if(!waveLengthBased)
      period = bounds.width/(float)nWaves;
    else
      period = waveLength;

    float x0 = bounds.x;
    
    // Iterate through the path and process each
    // line segment.
    float seg[] = new float[6];
    int segType = 0;
    boolean start = false;
    float twoPi = (float)Math.PI*2f;

    // Transformed path
    GeneralPath transformedPath = new GeneralPath();

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	transformedPath.moveTo(seg[0], seg[1] + waveHeight*(float)Math.sin(((seg[0]-x0)/period*twoPi)));
	break;
      case PathIterator.SEG_LINETO:
	transformedPath.lineTo(seg[0], seg[1] + waveHeight*(float)Math.sin(((seg[0]-x0)/period*twoPi)));
	break;
	
      case PathIterator.SEG_CLOSE:
	transformedPath.closePath();
	break;
	
      case PathIterator.SEG_QUADTO:
	transformedPath.quadTo(seg[0], seg[1] + waveHeight*(float)Math.sin(((seg[0]-x0)/period*twoPi)),
			       seg[2], seg[3] + waveHeight*(float)Math.sin(((seg[2]-x0)/period*twoPi)));
	break;

      case PathIterator.SEG_CUBICTO:
	transformedPath.curveTo(seg[0], seg[1] + waveHeight*(float)Math.sin(((seg[0]-x0)/period*twoPi)),
			       seg[2], seg[3] + waveHeight*(float)Math.sin(((seg[2]-x0)/period*twoPi)),
			       seg[4], seg[5] + waveHeight*(float)Math.sin(((seg[4]-x0)/period*twoPi)));
	break;

      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return transformedPath;
  }
}
