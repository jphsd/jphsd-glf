/*
 * @(#)TextStroke.java
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
import java.awt.font.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * This implementation of the Stroke interface draws a text along the path of 
 * the stroked Shape. Optionally, the text can be cycled along the path. That is,
 * the text is repeats the text along the shape as many times as needed to cover 
 * the full Shape path. Otherwise, the text is drawn only once.
 *
 * @author     Vincent Hardy
 * @version    1.0, 09/09/98
 */
public class TextStroke implements Stroke{
  static final String ERROR_INVALID_TEXT_ARGUMENT = "Error : text should be non null and contain non white-space characters";
  static final FontRenderContext frc = new FontRenderContext(null, true, true);
  static final int FLATTENESS = 1;

  /**
   * 
  /**
   * Holds the description of the Text glyphs which are drawn along the stroked
   * Shapes path.
   */
  private GlyphVector glyphVector;

  /**
   * Ascent for the glyphVector
   */
  private float ascent;

  /**
   * Number of glyphs in Vector
   */
  private int nGlyphs;

  /**
   * Controls whether or not the text should be repeated
   */
  private boolean cycle;

  /**
   * Size of the spacing between two iterations of the text string
   */
  private float cycleGap;

  /**
   * Text drawn along the stroked shapes's path
   */
  private String text;

  /**
   * Font used to draw text along stroked shapes
   */
  private Font font;

  /**
   * @param text the text to be drawn along the path of stroked Shapes.
   * @param font the font used to create the text glyphs.
   * @param cycle true if the text should be repeated as many times as needed along
   *        the stroked paths.
   * @param cycleGap size of the gap between cycles of text. Ignored if cycle is false.
   *        if cycleGap is negative, it defaults to the font size.
   */
  public TextStroke(String text, Font font, boolean cycle, float cycleGap){
    if(text==null || text.trim().length()==0)
      throw new IllegalArgumentException(ERROR_INVALID_TEXT_ARGUMENT);

    if(font==null)
      throw new NullPointerException();

    glyphVector = font.createGlyphVector(frc, text);
    glyphVector.performDefaultLayout();
    nGlyphs = glyphVector.getNumGlyphs();
    LineMetrics metrics = font.getLineMetrics(text, frc);
    this.ascent = metrics.getAscent();
    this.cycle = cycle;
    this.cycleGap = cycleGap;
    if(cycleGap<0)
      this.cycleGap = font.getSize();

    this.text = text;
    this.font = font;
  }

  /**
   * Stroke interface implementation. 
   */
  public Shape createStrokedShape(Shape shape){
    // Flatten path.
    PathIterator pi = new FlatteningPathIterator(shape.getPathIterator(new AffineTransform()), FLATTENESS);

    // Iterate through the path and process each
    // line segment.
    float seg[] = new float[6];
    int segType = 0;
    ProcessSegmentControl ctl = new ProcessSegmentControl();
    boolean stop = false;

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	// Moving to a new start.
	ctl.x = seg[0];
	ctl.y = seg[1];
	ctl.mx = ctl.x;
	ctl.my = ctl.y;
	ctl.start = true;
	break;
      case PathIterator.SEG_LINETO:
	// New line segment, try to add glyphs on the path.
	ctl.dx = seg[0];
	ctl.dy = seg[1];
	stop = processSegment(ctl);
	ctl.start = false;
	break;
	
      case PathIterator.SEG_CLOSE:
	// Closing to the last move to position.
	ctl.dx = ctl.mx;
	ctl.dy = ctl.my;
	stop = processSegment(ctl);
	break;
	
      case PathIterator.SEG_QUADTO:
      case PathIterator.SEG_CUBICTO:
      default:
	// This should never happen because we are using a FlatteningPathIterator
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
      if(stop)
	break;
    }    

    return ctl.s;
  }

  /**
   * Process a new segment of the stroked shape
   */
  private boolean processSegment(ProcessSegmentControl ctl){
    boolean stop = false;

    // Process current segment length
    float segLength = (float)Point2D.distance(ctl.x, ctl.y, 
					      ctl.dx, ctl.dy);

    // Segment slope factors
    float dx = (ctl.dx-ctl.x)/segLength;
    float dy = (ctl.dy-ctl.y)/segLength;

    // Adjust start position
    ctl.x -= dx*ctl.startCredit;
    ctl.y -= dy*ctl.startCredit;

    // Initial insert point
    float insertDist = ctl.ngx-ctl.d;
    float x = ctl.x + insertDist*dx;
    float y = ctl.y + insertDist*dy;

    // Angle the current segment makes with the x-axis
    float rotationAngle = (float)Math.atan2(ctl.dy-ctl.y, ctl.dx-ctl.x);

    // Check if this segment overlaps with the previous one.
    float shiftDistance = 0;
    if(!ctl.start){ 
      // Do not perform check in case this is the first segment after a moveTo
      // because the previous segment is considered non-relevant.
      float deltaAngle = rotationAngle - ctl.previousAngle;
      if(deltaAngle<-Math.PI) deltaAngle += 2*(float)Math.PI;
      if(deltaAngle>Math.PI) deltaAngle -= 2*(float)Math.PI;

      if(deltaAngle<0){
	// The current segment overlaps with the previous one
	// Shift starting point to avoid overlapping.
	shiftDistance = Math.abs(ascent*(float)Math.tan(deltaAngle/2));
	segLength -= shiftDistance;
	x += shiftDistance*dx;
	y += shiftDistance*dy;
      }
    }

    // Only process if the next insert point is within the segment
    // and the glyph to insert fits in segment
    if( (ctl.d + ctl.startCredit + segLength)> (ctl.ngx + ctl.glyphWidth) ){  
      // Update the previousAngle if a glyph is actuall inserted.
      ctl.previousAngle = rotationAngle;

      // Process the rotation which should be applied to glyphs
      // prior to inserting them into the stroked path.
      // The rotation is centered about the insert point
      // and its angle is that of the segment with the x axis.
      AffineTransform at = new AffineTransform();

      // Translate glyphs so that the baseline is on the current segment
      at.translate(x-ctl.ngx, y-ctl.ngy);      
      at.rotate(rotationAngle, ctl.ngx, ctl.ngy);
      at.concatenate(ctl.defaultTransform);

      // Used to store the remaining length in current segment
      float remainingSegLength = segLength + ctl.startCredit;

      // Transform used to position glyph
      AffineTransform glyphTransform = new AffineTransform();

      // Controls whether the next glyph fits on current segment
      boolean fits = true;
      while(fits){
	// Add glyph to stroked path
	Point2D glyphPos = glyphVector.getGlyphPosition(ctl.glyphIndex);
	glyphTransform.setToIdentity();
	glyphTransform.concatenate(at);
	glyphTransform.translate(glyphPos.getX(), glyphPos.getY());
	ctl.s.append( glyphTransform.createTransformedShape(glyphVector.getGlyphOutline(ctl.glyphIndex)), false );

	// Update 'used' distance
	// ctl.d += ctl.glyphWidth;
	float newD = (float)ctl.defaultTransform.getTranslateX() + (float)glyphPos.getX() + ctl.glyphWidth;

	// Some of the current segment has been used...
	remainingSegLength -= (newD - ctl.d); // ctl.glyphWidth;

	ctl.d = newD;

	// Update segment starting point
	ctl.x += ctl.glyphWidth*dx;
	ctl.y += ctl.glyphWidth*dy;

	// Update next glyph's index : stop if not cycling
	// and end has been reached.
	ctl.glyphIndex++;
	if(ctl.glyphIndex>=nGlyphs){
	  if(!cycle){
	    stop = true;
	    fits = false;
	  }
	  else{
	    // We have reached the end of the text. We are going to cycle.
	    // Adjust the default transform to take that into account.
	    ctl.defaultTransform.translate((float)glyphVector.getVisualBounds().getWidth() + cycleGap, 0);
	    at.translate((float)glyphVector.getVisualBounds().getWidth() + cycleGap, 0);
	  }
	}

	if(!stop){
	  ctl.glyphIndex %= nGlyphs;
	  
	  // Distance at wich the next insert will happen
	  ctl.ngx = (float)ctl.defaultTransform.getTranslateX() + (float)glyphVector.getGlyphPosition(ctl.glyphIndex).getX();
	  ctl.ngy = (float)glyphVector.getGlyphPosition(ctl.glyphIndex).getY();

	  // Width of the next glyph
	  // ctl.glyphWidth = glyphVector.getGlyphOutline(ctl.glyphIndex).getBounds().width;
	  ctl.glyphWidth = glyphVector.getGlyphLogicalBounds(ctl.glyphIndex).getBounds().width;
	  
	  // Check if next glyph fits
	  fits = (ctl.d + remainingSegLength)> (ctl.ngx + ctl.glyphWidth);
	}
      }

      ctl.x = ctl.dx;
      ctl.y = ctl.dy;
      ctl.startCredit = remainingSegLength;
    }

    return stop;
  }

  public String getText(){
    return text;
  }

  public Font getFont(){
    return font;
  }

  public boolean isCycle(){
    return cycle;
  }

  public float getCycleGap(){
    return cycleGap;
  }

  /*
   * Class used to hold information about the path currently being
   * stroked.
   */
  class ProcessSegmentControl{
    /** Current segment start */
    float x, y;

    /** Last move coordinates */
    float mx, my;

    /** Current segment end */
    float dx, dy;

    /** Used when iterating through a path to store the amount of space not
     * used which can be used in the following segment.
     */
    float startCredit;

    /** Current cumulated distance of 'used' path.*/
    float d;

    /** Next insert can happen at this cummulated distance*/
    float ngx;

    /** Next insert can happen at this elevation */
    float ngy;

    /** Width of the next glyph to be inserted */
    int glyphWidth;

    /** Index of the next glyph to place on path */
    int glyphIndex;

    /** Controls if the current segment is the first one after a moveTo */
    boolean start;

    /** Angle that the previous segment makes with the x-axis. This is used 
     *  to detect situations where the text of one segment might overlap on
     *  the previous one.
     */
    float previousAngle;

    /** Stroked path */
    GeneralPath s = new GeneralPath();

    /** Used to adjust glyphVector position when the text is repeated over
     * the stroked shape.
     */
    AffineTransform defaultTransform = new AffineTransform();

    /**
     * Constructor 
     */
    public ProcessSegmentControl(){
      glyphWidth = glyphVector.getGlyphOutline(0).getBounds().width;
    }
  }

  /**
   * Unit testing
   */
  public static void main(String args[]){
    JFrame frame = new JFrame("TextStroke unit testing");
    frame.getContentPane().setBackground(Color.white);

    GeneralPath triangle = new GeneralPath();
    triangle.moveTo(50, 0);
    triangle.lineTo(100, 100);
    triangle.lineTo(0, 100);
    triangle.closePath();

    Rectangle square = new Rectangle(0, 0, 100, 100);
    Ellipse2D disc = new Ellipse2D.Double(0, 0, 100, 100);
    GeneralPath eiffel = new GeneralPath();  
    eiffel.moveTo(80.f, 40.f);  
    eiffel.lineTo(100.f, 40.f);  
    eiffel.quadTo(100.f, 100.f, 140.f, 160.f); 
    eiffel.lineTo(110.f, 160.f); 
    eiffel.curveTo(110.f, 120.f, 70.f,   
		   120.f, 70.f, 160.f);   
    eiffel.lineTo(40.f, 160.f); 
    eiffel.quadTo(80.f, 100.f, 80.f, 40.f); 

    Shape shapes[] = { triangle, square, disc, eiffel };
    Font font = new Font("dialog", Font.PLAIN, 10);
    Stroke strokes[] = { new BasicStroke(),
			 new TextStroke("Text Strokes are fun", font, false, 0),
			 new TextStroke("Text Strokes are fun", font, true, 10)};

    
    frame.getContentPane().setLayout(new GridLayout(0, strokes.length));
    
    for(int i=0; i<shapes.length; i++){
      Shape shape = shapes[i];
      for(int j=0; j<strokes.length; j++)
	addTestComponent(frame, strokes[j], shape);
    }
    
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    frame.pack();
    frame.setVisible(true);
  }

  private static void addTestComponent(JFrame frame, Stroke stroke, Shape shape){
    Rectangle bounds = shape.getBounds();
    LayerComposition cmp = new LayerComposition(new Dimension(bounds.width + 20, bounds.height + 20));
    ShapeLayer layer = new ShapeLayer(cmp, shape, new StrokeRenderer(Color.black, stroke), Position.CENTER);
    ShapeLayer plainLayer = new ShapeLayer(cmp, shape, new StrokeRenderer(Color.black, new BasicStroke()), Position.CENTER);
    cmp.setBackgroundPaint(Color.white);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setLayers(new Layer[]{layer});
    frame.getContentPane().add(new CompositionComponent(cmp));
  }
}
