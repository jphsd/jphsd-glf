/*
 * @(#)BumpTransform.java
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
 * A BumpTransform modifies the y components of a Shape coordinates by 
 * applying the following mathematical function:
 *
 * 
 *
 * @author Vincent Hardy
 * @version 1.0, 09.11.1998
 */
public class BumpTransform extends AbstractTransform{
  /**
   * The bump's angle
   */
  float angle;

  /**
   * Controls the bump's direction.
   * @see #transformImpl
   */
  int sign = 1;

  /**
   * @param angle bump angle. If positive, the bump is oriented towards the positive
   *        side of the y axis. Otherwise, it is oriented towards the negative side.
   */
  public BumpTransform(float angle){
    if(angle<0)
      sign = -1;

    angle = (float)Math.abs(angle);
    this.angle = angle%(2*(float)Math.PI);
    if(this.angle==0.f)
      this.angle = 2*(float)Math.PI;
  }

  /**
   * Transforms n points into the seg array
   */
  private final void transform(final float xm, final float ym, final float rmin, final float smax, 
			       final float h, final float seg[], final int n, AffineTransform t){
    float deltaX = 0, deltaY = 0, theta = 0, r=0, s=0;
   
    for(int i=0; i<n; i++){
      deltaX = seg[2*i]-xm;
      deltaY = sign*(ym - seg[2*i+1]);
      r = rmin + deltaY;
      s = 1 + (deltaY/h)*(smax-1);
      theta = (s*deltaX)/r;
      seg[2*i] = xm + r*(float)Math.sin(theta);
      seg[2*i+1] += sign*r*(1-(float)Math.cos(theta));
    }
  }

  /**
   * @param shape Shape to be transformed, excluding any pre or post transformation.
   * @see #transform
   */
  public Shape transformImpl(Shape shape){
    // Get path iterator to walk through the shape path
    PathIterator pi = shape.getPathIterator(null);

    // The shape's points and control points are shifted vertically
    // according to the x position.
    // 

    // M is the point at the bottom half of the shape's bounds, and 
    // has the (xm, ym) coordinates.
    //
    Rectangle bounds = shape.getBounds();
    float xm = bounds.x + bounds.width/2f;
    float ym = bounds.y + bounds.height;
    float h = bounds.height;

    if(sign==-1)
      ym = bounds.y;

    // rmin is the radius of the circle on wich the input shape
    // is wrapped.
    float rmin = bounds.width/angle;

    // At the bottom of the shape, there is no scale. Maximum
    // distortion (shrink or stretch) is at the top of the shape
    float smax = (rmin + bounds.height)*angle/bounds.width;

    // Iterate through the path and process each
    // line segment.
    float seg[] = new float[6];
    int segType = 0;
    boolean start = false;

    // Transformed path
    GeneralPath transformedPath = new GeneralPath();
    AffineTransform t = new AffineTransform();

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	transform(xm, ym, rmin, smax, h, seg, 1, t);
	transformedPath.moveTo(seg[0], seg[1]);
	break;

      case PathIterator.SEG_LINETO:
	transform(xm, ym, rmin, smax, h, seg, 1, t);
	transformedPath.lineTo(seg[0], seg[1]);
	break;
	
      case PathIterator.SEG_CLOSE:
	transformedPath.closePath();
	break;
	
      case PathIterator.SEG_QUADTO:
	transform(xm, ym, rmin, smax, h, seg, 2, t);
	transformedPath.quadTo(seg[0], seg[1],
			       seg[2], seg[3]);
	break;

      case PathIterator.SEG_CUBICTO:
	transform(xm, ym, rmin, smax, h, seg, 3, t);
	transformedPath.curveTo(seg[0], seg[1],
				seg[2], seg[3],
				seg[4], seg[5]);
	break;

      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return transformedPath;
  }


}
