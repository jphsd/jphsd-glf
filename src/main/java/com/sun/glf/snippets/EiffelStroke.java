/*
 * @(#)EiffelStroke.java
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
import java.awt.event.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * Illustrates the use of the FillRenderer class.
 *
 * @author             Vincent Hardy
 * @version            1.0, 03.07.1999
 */
public class EiffelStroke{
  public static void main(String args[]){
    //
    // Create two different Shapes : an Eiffel tower and an Ellipse
    //
    GeneralPath eiffel = new GeneralPath();  
    eiffel.moveTo(40.f, 20.f);  
    eiffel.lineTo(50.f, 20.f);  
    eiffel.quadTo(50.f, 50.f, 70.f, 80.f); 
    eiffel.lineTo(55.f, 80.f); 
    eiffel.curveTo(55.f, 60.f, 35.f,   
		   60.f, 35.f, 80.f);   
    eiffel.lineTo(20.f, 80.f); 
    eiffel.quadTo(40.f, 50.f, 40.f, 20.f); 

    AffineTransform scale = AffineTransform.getScaleInstance(5, 5);
    Shape eiffelXfmd = scale.createTransformedShape(eiffel);
    Rectangle bounds = eiffelXfmd.getBounds();
    int margins = (int)(bounds.height*0.10); // Margins are 10% of the Shape's height
    Ellipse2D ellipse = new Ellipse2D.Float(bounds.x,
					    bounds.y,
					    bounds.width, bounds.height);

    //
    // Create a GradientPaint from top of the Shapes to their bottom
    //
    Color blue = new Color(100, 100, 200);
    Color black = Color.black;
    Paint gradientPaint = new GradientPaint(0, bounds.y, black,
					    0, bounds.y + bounds.width, blue);
    
    //
    // Create a StrokeRenderer
    //
    Stroke stroke = new WaveStroke(6); // new BasicStroke(6); 
    StrokeRenderer outline = new StrokeRenderer(gradientPaint, stroke);

    //
    // Create a LayerComposition whose size is based on the place taken 
    // by the two Shapes.
    //
    Dimension size = new Dimension(bounds.width + 2*margins,      // Left and right margins
				   2*bounds.height + 3*margins);  // Top, center and bottom margins
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(Color.white);

    //
    // Create two ShapeLayers
    //
    Position eiffelPosition = new Position(Anchor.TOP, 0, margins);
    ShapeLayer eiffelLayer = new ShapeLayer(cmp, eiffelXfmd, outline, eiffelPosition);
    Position ellipsePosition = new Position(Anchor.BOTTOM, 0, margins);
    ShapeLayer ellipseLayer = new ShapeLayer(cmp, ellipse, outline, ellipsePosition);

    //
    // Stack up Layers
    //
    cmp.setLayers(new Layer[]{ eiffelLayer, ellipseLayer });
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    CompositionFrame frame = new CompositionFrame("Stroking Shapes with a StrokeRenderer");
    frame.setComposition(cmp);
    frame.pack();
    
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    frame.setVisible(true);
  }
}