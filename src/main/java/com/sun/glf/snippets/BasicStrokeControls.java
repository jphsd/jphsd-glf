/*
 * @(#)BasicStrokeControls.java
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
package com.sun.glf.snippets;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

/**
 * Demonstrates the use of the Stroke interface:
 *  + Graphics2D attribute
 *  + Used in : draw and drawX Graphics and Graphics2D methods
 *  + Used to return the stroked Shape of a Shape.
 * 
 * Also demonstrates the BasicStroke implementation, which
 * provides control over the:
 *  + width of the Stroke
 *  + the style of line segment terminations
 *  + the style of line segment connections
 *  + dashes
 * 
 * @author        Vincent Hardy
 * @version       1.0, 10/22/1998
 */

/**
 * The StrokeSampler component displays a set of shapes drawn
 * with the strokes passed at construction time.
 */
class StrokeSampler extends JComponent {
  static final int SHAPE_SPACING = 15;
  static final int MARGIN = 20;
  static final int SCALE = 1;

  Dimension size;

  BasicStroke strokes[];
  Shape shape;
  int shapeWidth;
  Paint paint;

  public StrokeSampler(BasicStroke strokes[], Shape shape, Paint paint){
    this.strokes = strokes;
    this.paint = paint;

    // Process size : the width depends on the
    // strokes width and includes the margin left between shapes.
    // 
    int width=0; 
    int maxLineWidth=0;
    Rectangle bounds = shape.getBounds();
    for(int i=0; i<strokes.length; i++){
      width += strokes[i].getLineWidth() + bounds.width;
      maxLineWidth = (int)Math.max(maxLineWidth, strokes[i].getLineWidth());
    }

    width += SHAPE_SPACING*(strokes.length-1);
    size = new Dimension(width + MARGIN*2, bounds.height + maxLineWidth + MARGIN*2);

    size.width *= SCALE;
    size.height *= SCALE;

    shapeWidth = bounds.width;

    // Now, position shape so that it is drawn in the right position
    AffineTransform t = AffineTransform.getTranslateInstance(-bounds.x, -bounds.y + maxLineWidth/2);
    this.shape = t.createTransformedShape(shape);
  }

  public void paint(Graphics _g){
    // Casting to Graphics2D gives us access to the  
    // new 2D features.  
    Graphics2D g = (Graphics2D)_g;

    g.scale(SCALE, SCALE);

    // Control Rendering quality. Here, we set antialiasing  
    // on to get a smoother rendering.  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
		       RenderingHints.VALUE_ANTIALIAS_ON); 

    // Draw a white frame around the rendered shapes
    Dimension curSize = getSize();
    g.setPaint(Color.black);
    // g.drawRect(MARGIN, MARGIN, curSize.width-2*MARGIN, curSize.height-2*MARGIN);

    // Set translation so that we center in display area
    g.translate((curSize.width-size.width)/(2*SCALE), (curSize.height-size.height)/(2*SCALE));

    // Set margin by offsetting rendering operations
    g.translate(MARGIN, MARGIN);

    // Set the paint used to draw the shape with the different strokes
    g.setPaint(paint);

    // Draw the shape with each of the strokes.
    int curXPos = 0; // Current x-axis start position for drawing lines.
    for(int i=0; i<strokes.length; i++){
      g.setStroke(strokes[i]);
      g.translate(strokes[i].getLineWidth()/2, 0);
      g.draw(shape);
      g.translate(strokes[i].getLineWidth()/2 + SHAPE_SPACING + shapeWidth, 0);
    }
  }

  public Dimension getPreferredSize(){ return size; }
  public Dimension getMinimumSize() { return size; }
}


public class BasicStrokeControls extends JComponent{
  /*
   * Strokes of varying width
   */
  BasicStroke thickStrokes[] = { new BasicStroke(2.f),
				 new BasicStroke(4.f),
				 new BasicStroke(8.f),
				 new BasicStroke(16.f) };


  /*
   * Strokes of varying termination styles
   */
  BasicStroke termStrokes[] = { new BasicStroke(15.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL), // No decoration
				new BasicStroke(15.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL), // Square end
				new BasicStroke(15.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL) }; // Rounded end


  /*
   * Strokes of varying segment connection styles
   */
  BasicStroke joinStrokes[] = { new BasicStroke(10.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL), // Connected with a straight segment
				new BasicStroke(10.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER), // Extend outlines until they meet
				new BasicStroke(10.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND)}; // Round of corner.

  /**
   * Strokes of varying miterlimits
   */
  BasicStroke miterLimitStrokes[] 
  = { new BasicStroke(6.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1),   // Actually cuts of all angles
      new BasicStroke(6.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 2f), // Cuts off angles less than 60degrees 
      new BasicStroke(6.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f)}; // Cuts off angles less than 11 degrees 
  
  /*
   * Srokes with varying dash styles
   */
  BasicStroke dashedStrokes[] = { new BasicStroke(8.f, 
						  BasicStroke.CAP_BUTT, 
						  BasicStroke.JOIN_BEVEL, 
						  8.f,
						  new float[]{ 6.f, 6.f },
						  0.f),
				  
				  new BasicStroke(8.f, 
						  BasicStroke.CAP_BUTT, 
						  BasicStroke.JOIN_BEVEL, 
						  8.f,
						  new float[]{ 10.f, 4.f },
						  0.f),
				  
				  new BasicStroke(8.f, 
						  BasicStroke.CAP_BUTT, 
						  BasicStroke.JOIN_BEVEL, 
						  8.f,
						  new float[]{ 4.f, 4.f, 10.f, 4.f },
						  0.f),
				  
				  new BasicStroke(8.f, 
						  BasicStroke.CAP_BUTT, 
						  BasicStroke.JOIN_BEVEL, 
						  8.f,
						  new float[]{ 4.f, 4.f, 10.f, 4.f },
						  4.f) };

  /**
   * The BasicStrokeControl contains four StrokeSampler, each illustrating a different 
   * set of BasicStrokes, displayed in a 2x2 grid.
   */
  public BasicStrokeControls(){
    // Set 2x2 grid layout with margins.
    setLayout(new GridLayout(0, 1, 20, 20));

    // Create shapes that will be drawn with the different
    // strokes.
    Line2D line = new Line2D.Float(0, 0, 0, 60);
    GeneralPath needle = new GeneralPath();
    needle.moveTo(0, 60);
    needle.lineTo(10, 0);
    needle.lineTo(20, 60);

    // Create a shape for miter join example
    GeneralPath miterShape = new GeneralPath();
    miterShape.moveTo(0, 0);
    miterShape.lineTo(30, 0);
    miterShape.lineTo(30, 60); // 90 degree elbow
    miterShape.lineTo(0, 30); // 45 degree elbow.

    // Create StrokeSamplers and add them to this container.
    add(new StrokeSampler(thickStrokes, line, Color.black)); 
    add(new StrokeSampler(dashedStrokes, line , Color.black)); 
    add(new StrokeSampler(termStrokes, line, Color.black)); 
    add(new StrokeSampler(joinStrokes, needle, Color.black)); 
    add(new StrokeSampler(miterLimitStrokes, miterShape, Color.black)); 
  }

  public static void main(String args[]){
    new SnippetFrame( new JScrollPane(new BasicStrokeControls()) );
  }

}
