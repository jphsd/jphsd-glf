/*
 * @(#)WaveStroke.java
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
import java.awt.event.*;

import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * This Stroke iterates through the input Shape object's outline and
 * replaces all the segments with a 'wave' pattern. The resulting
 * Shape is then stroked by a regular BasicStroke.
 *
 * @author      Vincent Hardy
 * @version     1.0, 09/15/1998
 * @see         java.awt.Stroke
 */
public class WaveStroke implements Stroke{
  /** 
   * Implementation.
   * @see #addSegment
   */
  private AffineTransform t = new AffineTransform();

  /** 
   * Used by the FlatteningPathIterator 
   * @see #createStrokedShape
   */
  private static final float FLATNESS = 1;

  /**
   * Default wave constants
   */
  private static final float DEFAULT_WAVE_LENGTH = 12;
  private static final float DEFAULT_WAVE_AMPLITUDE = 4;
  private static final float DEFAULT_WIDTH = 1;

  /*
   * Coordinates of the base pattern we use to 
   * reproduce along the path of the stroked Shape
   */
  private float positivePhasePoints[] = { 1, 2, 3, 2,
					  5, 2, 6, 0,
					  7, -2, 9, -2, 
					  11, -2, 12, 0 };
  private float negativePhasePoints[] = { 1, -2, 3, -2,
					  5, -2, 6, 0,
					  7, 2, 9, 2, 
					  11, 2, 12, 0 };

  private float waveLength = DEFAULT_WAVE_LENGTH;
  private float waveAmplitude = DEFAULT_WAVE_AMPLITUDE;
  private float width = DEFAULT_WIDTH;

  /**
   * BasicStroke used to stroke the outline of the Shape
   * after we replaced line segments with wave patterns
   */
  private BasicStroke basicStroke;

  /**
   * Used to bundle parameters to addSegment
   * @see #addSegment
   */
  static class AddSegmentControl{
    float x, y, dx, dy, mx, my, startX, startY;
    GeneralPath s;
    boolean start, negativePhase, startOverride, isClose;
  }

  /**
   * Constructor
   */
  public WaveStroke(float width){
    this(width, DEFAULT_WAVE_LENGTH, DEFAULT_WAVE_AMPLITUDE);
  }

  /**
   * @param waveLength lenght of each wave pattern. Should be more than zero.
   * @param waveAmplitude amplitude of each wave pattern. Should be more than zero.
   * @param width width of the wave
   */
  public WaveStroke(float width, float waveLength, float waveAmplitude){
    if(waveLength<=0 || waveAmplitude<=0 || width<=0)
      throw new IllegalArgumentException("Arguments should be greater than zero");

    // basicStroke is used to provide the appropriate thickness to the stroked path.
    // see createStrokedShape
    basicStroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);

    // Transform phase points according to length and amplitude
    AffineTransform scale = new AffineTransform();
    scale.setToScale(waveLength/DEFAULT_WAVE_LENGTH, waveAmplitude/DEFAULT_WAVE_AMPLITUDE);
    scale.transform(positivePhasePoints, 0, positivePhasePoints, 0, positivePhasePoints.length/2);
    scale.transform(negativePhasePoints, 0, negativePhasePoints, 0, negativePhasePoints.length/2);

    this.waveLength = waveLength;
    this.waveAmplitude = waveAmplitude;
    this.width = width;
  }

  /**
   * Implementation of the Stroke interface. The input Shape is flattened
   * by a FlatteningPathIterator. The resulting path is iterated through and
   * line segments are replaced by a wave curve.
   * @see #addSegment
   * @see java.awt.geom.PathIterator
   * @see java.awt.geom.FlatteningPathIterator
   */
  public Shape createStrokedShape(Shape shape){
    // Flatten path.
    MunchTransform shapeMuncher = new MunchTransform(FLATNESS);
    PathIterator pi = shapeMuncher.transform(shape).getPathIterator(null, FLATNESS);
    
    // PathIterator pi = shape.getPathIterator(null, FLATNESS);

    // Iterate through the path and process each
    // line segment.
    float seg[] = new float[6];
    int segType = 0;
    boolean start = false;
    boolean negativePhase = false;
    AddSegmentControl ctl = new AddSegmentControl();
    GeneralPath strokedShape = new GeneralPath(); // Return value
    ctl.s = strokedShape;

    while(!pi.isDone()){
      segType = pi.currentSegment(seg);
      switch(segType){
      case PathIterator.SEG_MOVETO:
	ctl.x = seg[0];
	ctl.y = seg[1];
	ctl.mx = ctl.x;
	ctl.my = ctl.y;
	ctl.start = true;
	break;
      case PathIterator.SEG_LINETO:
	ctl.dx = seg[0];
	ctl.dy = seg[1];
	ctl.isClose = (ctl.dx==ctl.mx && ctl.dy==ctl.my);
	addSegment(ctl);
	ctl.start = false;
	ctl.x = ctl.dx;
	ctl.y = ctl.dy;
	break;
	
      case PathIterator.SEG_CLOSE:
	ctl.isClose = true;
	ctl.dx = ctl.mx;
	ctl.dy = ctl.my;
	addSegment(ctl);
	ctl.start = false;
	ctl.x = ctl.mx;
	ctl.y = ctl.my;
	break;
	
      case PathIterator.SEG_QUADTO:
      case PathIterator.SEG_CUBICTO:
      default:
	throw new Error("Illegal seg type : " + segType);
      }
      pi.next();
    }

    return basicStroke.createStrokedShape(strokedShape);
  }

  /**
   * Replaces the (x,y) to (dx, dy) segment by a succession of wave patterns.
   * This will fit as many full waves as possible. The remaining space is filled 
   * with a quad curve to (dx, dy)
   * @see #createStrokedShape
   * @return whether or not next segment should be a negative phase segment.
   */
  private void addSegment(AddSegmentControl ctl){
    // For readibility
    float x = ctl.x, y = ctl.y, dx = ctl.dx, dy = ctl.dy;
    GeneralPath s = ctl.s;

    // Check if there is a start override
    if(!ctl.start && ctl.startOverride){
      x = ctl.startX;
      y = ctl.startY;
    }

    if(ctl.start)
      ctl.s.moveTo(x, y);

    // Use set of points depending on the phase
    float wavePoints[] = positivePhasePoints;
    if(ctl.negativePhase)
      wavePoints = negativePhasePoints;

    // Do not process if this is an empty segment
    if(x==dx && y==dy)
      return;

    // Process segment length
    float d = (float)Point2D.distance(x, y, dx, dy);

    // Wave rotation angle
    double theta = Math.atan2((dy-y), (dx-x));

    // Number of waves which fit onto the segment
    float ratio = d/waveLength;
    int n = (int)Math.floor(ratio);

    // Reset transform
    t.setToScale(1, 1);

    // Translate the wave to its initial position
    t.translate(x, y);

    // First, rotate the wave
    float working[] = new float[wavePoints.length];
    t.rotate(theta);

    if(n>1 && (ratio-n)<0.5){
      float scale = ratio/(float)n;
      t.scale(scale, 1);  
      ratio = n;
    }

    t.transform(wavePoints, 0, working, 0, wavePoints.length/2);

    // Repeat the wave n times along the path
    float xShift=(dx-x)/ratio;
    float yShift=(dy-y)/ratio;
    
    int nQuads = working.length/4;
    t.setToTranslation(xShift, yShift);

    for(int i=0; i<n; i++){
      for(int j=0; j<nQuads; j++)
	s.quadTo(working[4*j], working[4*j+1], 
		 working[4*j+2], working[4*j+3]);
      t.transform(working, 0, working, 0, working.length/2); 
    }

    // Finish up with an ad-hoc quad curve

    // Squeeze a half wave if there is enough space
    if(ctl.isClose){
      if(ratio-n > 0.75){
	s.quadTo(working[0], working[1], working[2], working[3]);
	s.quadTo(working[4], working[5], working[6], working[7]);
	s.quadTo(working[8], working[9], working[10], working[11]);
	s.quadTo(working[12], working[13], dx, dy);
	ctl.startOverride = false;
      }
      else if(ratio-n > 0.25){
	s.quadTo(working[0], working[1], working[2], working[3]);
	s.quadTo(working[4], working[5], dx, dy);
	ctl.negativePhase = !ctl.negativePhase;
	ctl.startOverride = false;
      }
      else{
	s.lineTo(dx, dy);
	ctl.startOverride = false;	
      }
    }
    else{
      if(ratio-n >= 0.5){
	s.quadTo(working[0], working[1], working[2], working[3]);
	s.quadTo(working[4], working[5], working[6], working[7]);
	ctl.negativePhase = !ctl.negativePhase;
	ctl.startOverride = true;
	ctl.startX = x + (n + 0.5f)*xShift;
	ctl.startY = y + (n + 0.5f)*yShift;
      }
      else{
	ctl.startOverride = true;
	ctl.startX = x + n*xShift;
	ctl.startY = y + n*yShift;
      }
    }
  }

  public float getWidth(){
    return width;
  }
  public float getWaveLength(){
    return waveLength;
  }

  public float getWaveAmplitude(){
    return waveAmplitude;
  }

  /**
   * Unit testing
   */
  public static void main(String args[]){
    JFrame frame = new JFrame("WaveStroke unit testing");
    frame.getContentPane().setBackground(Color.white);

    Line2D line = new Line2D.Double(0, 0, 100, 0);
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

    GeneralPath triangle = new GeneralPath();
    triangle.moveTo(50, 0);
    triangle.lineTo(100, 100);
    triangle.lineTo(0, 100);
    triangle.closePath();

    Shape shapes[] = { line, square, disc, eiffel, triangle };

    Stroke strokes[] = { new BasicStroke(),
			 new WaveStroke(3),
			 new WaveStroke(1),
			 new WaveStroke(1, 24, 4),
			 new WaveStroke(3, 12, 8),
			 new WaveStroke(1, 12, 8),
			 new WaveStroke(3, 6, 4) };

    
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
    ShapeLayer layer = new ShapeLayer(cmp, shape, new StrokeRenderer(Color.gray, stroke), Position.CENTER);
    ShapeLayer plainLayer = new ShapeLayer(cmp, shape, new StrokeRenderer(Color.black, new BasicStroke()), Position.CENTER);
    cmp.setBackgroundPaint(Color.white);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setLayers(new Layer[]{layer});
    frame.getContentPane().add(new CompositionComponent(cmp));
  }
}
