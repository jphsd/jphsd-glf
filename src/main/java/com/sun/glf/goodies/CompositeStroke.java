/*
 * @(#)CompositeStroke.java
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
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JComponent;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * Sample implementation of the Stroke interface. Takes two Stroke objects
 * as an input. This implementation simply composes the two strokes, i.e.
 * it uses one stroke to stroke the stroked shape created by the other one.
 *
 * @author     Vincent Hardy
 * @version    1.0, 09/09/98
 */
public class CompositeStroke implements Stroke{
  private Stroke strokeA, strokeB;

  /**
   * @return array of strokes this composite combines
   */
  public Stroke[] getComponentStrokes(){
    return new Stroke[]{strokeA, strokeB};
  }

  /**
   * @param strokeA used to initialy stroke input shape objects.
   * @param strokeB used to stroke the stroked shapes created by strokeA
   * @see #createStrokedShape
   */
  public CompositeStroke(Stroke strokeA, Stroke strokeB){
    if(strokeA==null || strokeB==null)
      throw new IllegalArgumentException();

    this.strokeA = strokeA;
    this.strokeB = strokeB;
  }

  public Shape createStrokedShape(Shape shape){
    return strokeB.createStrokedShape(strokeA.createStrokedShape(shape));
  }

  public static final String USAGE = "java com.sun.glf.goodies.CompositeStroke <ellipse|triangle|square|textShape> <gap>";

  public static void main(String args[]){
    //
    // Initialize a set of predifined Shapes which
    // the user can choose from with a command line argument.
    //
    Hashtable shapes = new Hashtable();
    
    // Ellipse
    Shape ellipse = new Ellipse2D.Float(0, 0, 200, 200);
    shapes.put("ellipse", ellipse);

    // Triangle
    GeneralPath triangle = new GeneralPath();
    triangle.moveTo(100, 0);
    triangle.lineTo(200, 200);
    triangle.lineTo(0, 200);
    triangle.closePath();
    shapes.put("triangle", triangle);

    // Square 
    shapes.put("square", new Rectangle(0, 0, 200, 200));

    // Text
    Font font = new Font("Times New Roman", Font.PLAIN, 200);
    FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
    Shape textShape = font.createGlyphVector(frc, "Hi!").getOutline();
    shapes.put("textShape", textShape);

    //
    // Initialize a set of predefined pattern Shapes
    //

    // small dot
    Shape smallDot = new Ellipse2D.Float(0, 0, 8, 8);
    
    // small rectangle 
    Shape smallRect = new Rectangle(0, 0, 8, 8);

    // Small Triangle
    GeneralPath smallTriangle = new GeneralPath();
    smallTriangle.moveTo(4, 0);
    smallTriangle.lineTo(8, 8);
    smallTriangle.lineTo(0, 8);
    smallTriangle.closePath();

    // Parse command line
    Shape shape = null;
    int gap = 0;
    try{
      shape = (Shape)shapes.get(args[0]);
      gap = Integer.parseInt(args[1]);
    }catch(Exception e){
      System.out.println(USAGE);
      System.exit(0);
    }

    // Create demo
    JFrame frame = new JFrame("ShapeStroke unit testing");
    frame.getContentPane().setBackground(Color.white);
    frame.getContentPane().setLayout(new GridLayout(0, 2));
    Rectangle bounds = shape.getBounds();
    AffineTransform t = AffineTransform.getTranslateInstance(-bounds.x + 20, -bounds.y + 20);
    shape = t.createTransformedShape(shape);
    Dimension dim = new Dimension(bounds.width+40, bounds.height+40);

    Shape pattern[] = new Shape[]{smallTriangle, smallDot, smallRect};
    Stroke shapeStroke = new ShapeStroke(pattern, gap);
    BasicStroke basicStroke = new BasicStroke(1.f);
    Stroke waveStroke = new WaveStroke(4);
    Stroke compositeShapeStroke = new CompositeStroke(shapeStroke, basicStroke);
    Stroke compositeWaveStroke = new CompositeStroke(waveStroke, basicStroke);

    //
    // Basic Stroke
    //
    Renderer strokeRenderer = new StrokeRenderer(Color.black, basicStroke);
    frame.getContentPane().add(makeNewComponent(dim, strokeRenderer, shape, "BasicStroke"));

    //
    // ShapeStroke and CompositeStroke
    //
    Color testColor = new Color(153, 153, 204);
    strokeRenderer = new CompositeRenderer(new Renderer[]{  new StrokeRenderer(testColor, shapeStroke),
							    new StrokeRenderer(Color.black, compositeShapeStroke),
							      });
    frame.getContentPane().add(makeNewComponent(dim, strokeRenderer, shape, "ShapeStroke & CompositeStroke"));

    //
    // WaveStroke
    //
    strokeRenderer = new StrokeRenderer(testColor, waveStroke);
    frame.getContentPane().add(makeNewComponent(dim, strokeRenderer, shape, "WaveStroke"));

    //
    // CompositionStroke again
    //
    strokeRenderer = new StrokeRenderer(Color.black, compositeWaveStroke);
    frame.getContentPane().add(makeNewComponent(dim, strokeRenderer, shape, "CompositeStroke : WaveStroke -> BasicStroke"));

    frame.pack();
    frame.setVisible(true);
  }

  private static JComponent makeNewComponent(Dimension dim, Renderer renderer, Shape shape, String toolTip){
    LayerComposition cmp = new LayerComposition(dim);
    RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    cmp.setRenderingHints(rh);
    Layer layer = new ShapeLayer(cmp, shape, renderer);
    cmp.setLayers( new Layer[]{layer});
    CompositionComponent comp = new CompositionComponent(cmp);
    comp.setToolTipText(toolTip);
    return comp;
  }
}
