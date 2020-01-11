/*
 * @(#)MunchTransform.java
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
 * This implementation breaks the input Shape into line segments or 
 * arbitrary small size. This can be used to easily approximate non-linear
 * transforms. For example, the munch transform could be used in conjunction 
 * with a WaveTransform.
 *
 * @author Vincent Hardy
 * @version 1.0, 09.11.1998
 */
public class MunchTransform extends AbstractTransform{
  /**
   * Maximum distance between any control points, including line ends.
   */
  public float munchSize = 1;
  public float munchSizeSqr = 1;

  /* 
   * @param munchSize the maximum distance between any two control points
   */
  public MunchTransform(float munchSize){
    this.munchSize = munchSize;
    munchSizeSqr = munchSize*munchSize;
  }

  /**
   * @param shape Shape to be transformed, excluding any pre or post transformation.
   * @see #transform
   */
  public Shape transformImpl(Shape shape){
    // Get path iterator to walk through the shape path
    PathIterator pi = shape.getPathIterator(null, munchSize);

    // Iterate through the path and process each
    // line segment.
    float seg[] = new float[6];
    int segType = 0;
    boolean start = false;
    float twoPi = (float)Math.PI*2f;
    float x=0, y=0; // Current position

    // Transformed path
    GeneralPath transformedPath = new GeneralPath();

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	x = seg[0]; y = seg[1];
	transformedPath.moveTo(x, y);
	break;
      case PathIterator.SEG_LINETO:
	addMunchedLine(transformedPath, x, y, seg[0], seg[1]);
	x = seg[0]; y = seg[1];
	break;
	
      case PathIterator.SEG_CLOSE:
	transformedPath.closePath();
	break;
	
      case PathIterator.SEG_QUADTO:
      case PathIterator.SEG_CUBICTO:
      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return transformedPath;
  }

  private void addMunchedLine(GeneralPath p, float x1, float y1, float x2, float y2){
    float dx = x2-x1;
    float dy = y2-y1;
    float dxSqr = dx*dx;
    float dySqr = dy*dy;
    if((dxSqr + dySqr)>=munchSizeSqr){
      float length = (float)Math.sqrt(dxSqr + dySqr);
      float ratio = munchSize/length;
      dx *= ratio;
      dy *= ratio;
      int n = (int)(1/ratio);
      for(int i=0; i<n; i++){
	x1 += dx;
	y1 += dy;
	p.lineTo(x1, y1);
      }
    }


    p.lineTo(x2, y2);
  }
}
