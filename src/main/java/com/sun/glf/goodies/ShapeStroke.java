/*
 * @(#)ShapeStroke.java
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
 * This Stroke iterates through the input Shape object's outline and 
 * 'draws' the pattern Shape it has been initialized with along that path.
 *
 * @author      Vincent Hardy
 * @version     1.0, 09/15/1998
 * @see         java.awt.Stroke
 */
public class ShapeStroke implements Stroke{
  /** pattern Shapes are repeated along the path being stroked */
  private Shape pattern[];

  /** 
   * Implementation.
   * @see #addSegment
   */
  private AffineTransform t = new AffineTransform();

  /** Gap between Shapes */
  private float gap;

  private static final int GAP_ADJUST_LIMIT_DEFAULT = 5;
  /** 
   * Number of patterns beyond which gap is adjusted so
   * as to fall on even boundaries 
   */
  private int gapAdjustLimit = GAP_ADJUST_LIMIT_DEFAULT;

  /** 
   * Used by the FlatteningPathIterator 
   * @see #createStrokedShape
   */
  private static final float FLATNESS = 1;

  /**
   * @return pattern of shapes used by this ShapeStroke instance
   */
  public Shape[] getPattern(){
    Shape pattern[] = new Shape[this.pattern.length];
    System.arraycopy(this.pattern, 0, pattern, 0, this.pattern.length);
    return pattern;
  }

  /**
   * @return gap between pattern shapes
   */
  public float getGap(){
    return gap;
  }

  /**
   * @param pattern the Shape that should be used as a pattern.
   * @param gap the gap between the pattern Shapes.
   */
  public ShapeStroke(Shape pattern, float gap){
    this(new Shape[]{pattern}, gap, GAP_ADJUST_LIMIT_DEFAULT);
  }

  /**
   * @param pattern the Shapes that should be used as a pattern. This array is copied.
   * @param gap the gap between the pattern Shapes.
   */
  public ShapeStroke(Shape pattern[], float gap){
    this(pattern, gap, GAP_ADJUST_LIMIT_DEFAULT);
  }

  /**
   * @param pattern the Shapes that should be used as a pattern. This array is copied.
   * @param gap the gap between the pattern Shapes.
   */
  public ShapeStroke(Shape pattern[], float gap, int gapAdjustLimit){
    // Synchronize the pattern array until we are done copying it.
    synchronized(pattern){
      if(pattern==null || pattern.length<1)
	throw new IllegalArgumentException("pattern array should have at least one element");
      
      this.gap = gap;
      this.pattern = new Shape[pattern.length];
      System.arraycopy(pattern, 0, this.pattern, 0, pattern.length);
    }

    // Center pattern Shapes in (0,0)
    for(int i=0; i<this.pattern.length; i++){
      Rectangle bounds = this.pattern[i].getBounds();
      t.setToTranslation(-bounds.x - bounds.width/2, -bounds.y - bounds.height/2);
      this.pattern[i] = t.createTransformedShape(this.pattern[i]);
    }
  }

  /**
   * Implementation of the Stroke interface. The input Shape is flattened
   * by a FlatteningPathIterator. The resulting path is iterated through and
   * pattern Shapes are inserted at regularly spaced intervals, controlled
   * by the gap member variable.
   * @see #gap
   * @see #addSegment
   * @see java.awt.geom.PathIterator
   * @see java.awt.geom.FlatteningPathIterator
   */
  public Shape createStrokedShape(Shape shape){
    GeneralPath strokedShape = new GeneralPath(); // Return value
    
    // Flatten path.
    PathIterator pi = new FlatteningPathIterator(shape.getPathIterator(new AffineTransform()), FLATNESS);

    // Keep track of the current pattern index.
    int curPattern = 0;

    // Iterate through the path and process each
    // line segment.
    float x=0, y=0, dx=0, dy=0, mx=0, my=0;
    float seg[] = new float[6];
    int segType = 0;
    boolean start = false;

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	x = seg[0];
	y = seg[1];
	mx = x;
	my = y;
	start = true;
	break;
      case PathIterator.SEG_LINETO:
	dx = seg[0];
	dy = seg[1];
	curPattern = addSegment(x, y, dx, dy, strokedShape, start, (dx==mx && dy==my), curPattern);
	start = false;
	x = dx;
	y = dy;
	break;
	
      case PathIterator.SEG_CLOSE:
	curPattern = addSegment(x, y, mx, my, strokedShape, start, true, curPattern);
	start = false;
	x = mx;
	y = my;
	break;
	
      case PathIterator.SEG_QUADTO:
      case PathIterator.SEG_CUBICTO:
      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return strokedShape;
  }

  /**
   * Adds patterns to s along the path from (x,y) to (dx, dy). One is added
   * in (x, y) if start is true.
   * @see #createStrokedShape
   * @return currentPattern index
   */
  float startCredit;
  private int addSegment(float x, float y, float dx, float dy, GeneralPath s, boolean start, boolean isClose, int curPattern){
    if(start)
      startCredit = gap;

    // First, adjust the starting point to take any 'left over' 
    // from previous segment into account.
    float deltaX = (dx-x);
    float deltaY = (dy-y);

    if(deltaX!=0.){
      float slope = deltaY/deltaX;
      float slopeSq = slope*slope;
      if(x>=dx){
	x += startCredit/Math.sqrt(1+slopeSq);
	y += slope*startCredit/Math.sqrt(1+slopeSq);
      }
      else{
	x -= startCredit/Math.sqrt(1+slopeSq);
	y -= slope*startCredit/Math.sqrt(1+slopeSq);
      }
    }
    else{
      if(dy>y)
	y -= startCredit;
      else
	y += startCredit;
    }

    t.setToTranslation(x, y);

    // Now, add a base shape at each gap interval end
    float dist = (float)Math.sqrt( (dx-x)*(dx-x) + (dy-y)*(dy-y) );
    float ratio = dist/gap;
    int n = (int)Math.floor(ratio);
    float xShift=(dx-x)/ratio;
    float yShift=(dy-y)/ratio;

    // If the number of gaps is more than the limit, then adjust Shape
    // gaps so as to place an even number of pattern Shapes
    if(n>=gapAdjustLimit){
      float adjust = 1 + (ratio - n)/(float)n;
      xShift *= adjust;
      yShift *= adjust;
    }

    // If this is a closing segment, we do not include
    // the terminating shape, and we space the shapes evenly
    // to avoid creating a visual gap.
    if(isClose && n>0){
      xShift=(dx-x)/n;
      yShift=(dy-y)/n;
      n--; // to avoid overlaps
    }

    for(int i=0; i<n; i++){
      t.translate(xShift, yShift);
      s.append(t.createTransformedShape(pattern[curPattern++]), false);
      curPattern %= pattern.length;
    }

    if(n<gapAdjustLimit)
      startCredit = gap*(ratio-n);
    else
      startCredit = 0;

    return curPattern;
  }
}
