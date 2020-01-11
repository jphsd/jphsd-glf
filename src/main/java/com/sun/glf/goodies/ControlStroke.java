/*
 * @(#)ControlStroke.java
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
 * This Stroke iterates through the input Shape object's path and 
 * 'marks' each control point with a control element.
 *
 * @author      Vincent Hardy
 * @version     1.0, 09/15/1998
 * @see         java.awt.Stroke
 */
public class ControlStroke implements Stroke{
  /** Default control Shape */
  static Shape defaultControl = new Rectangle(0, 0, 3, 3);

  /** Used to stroke the control points */
  BasicStroke basicStroke;

  /** Control Shape */
  Shape controlShape;

  /** Control Shape bounds */
  Rectangle2D controlBounds;

  /**
   * Constructor.
   *
   * @param width stroke width
   */
  public ControlStroke(float width){
    this(width, defaultControl);
  }

  /**
   * Constructor
   *
   * @param width stroke width
   * @param controlShape Shape that should be used to mark the control points
   */
  public ControlStroke(float width, Shape controlShape){
    basicStroke = new BasicStroke(width);
    this.controlShape = new GeneralPath(controlShape);
    this.controlBounds = this.controlShape.getBounds2D();
  }

  /**
   * Implementation of the Stroke interface. Lines are replaced by longer lines,
   * 
   * @see java.awt.geom.PathIterator
   */
  public Shape createStrokedShape(Shape shape){
    GeneralPath strokedShape = new GeneralPath(); // Return value
    
    // Get path iterator to walk through the shape path
    PathIterator pi = shape.getPathIterator(null);

    // Iterate through the path and process each
    // line segment.
    AffineTransform t = new AffineTransform();
    float seg[] = new float[6];
    int segType=0;
    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	addControl(seg[0], seg[1], strokedShape, t);
	break;
      case PathIterator.SEG_LINETO:
	addControl(seg[0], seg[1], strokedShape, t);
	break;
	
      case PathIterator.SEG_CLOSE:
	break;
	
      case PathIterator.SEG_QUADTO:
	addControl(seg[0], seg[1], strokedShape, t);
	addControl(seg[2], seg[3], strokedShape, t);
	break;

      case PathIterator.SEG_CUBICTO:
	addControl(seg[0], seg[1], strokedShape, t);
	addControl(seg[2], seg[3], strokedShape, t);
	addControl(seg[4], seg[5], strokedShape, t);
	break;

      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return basicStroke.createStrokedShape(strokedShape);
  }

  private void addControl(float x, float y, GeneralPath path, AffineTransform t){
    t.setToTranslation((float)-controlBounds.getX() + x - (float)controlBounds.getWidth()/2,
		       (float)-controlBounds.getY() + y - (float)controlBounds.getHeight()/2);

    path.append(t.createTransformedShape(controlShape), false);
  }

}
