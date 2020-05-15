/*
 * @(#)AffineTransformTypes.java
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
import java.awt.font.*;
import java.util.*;

import javax.swing.*;

/**
 * Demonstrates the different types of AffineTransforms.
 *
 * @author         Vincent Hardy
 * @version        1.1, 10/20/1998
 */
public class AffineTransformTypes extends JComponent {  
  /** each array of shape will be displayed on a single line */
  Shape shapes[][];

  /** Dimension into which each shape is displayed */
  static final Dimension CELL_SIZE = new Dimension(100, 100);

  public AffineTransformTypes(){
    // The base shape is the letter J.
    Font font = new Font("serif.bold", Font.PLAIN, 80);
    FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
    Shape baseShape = font.createGlyphVector(frc, "J").getOutline();

    // Let's center the base shape so that it can be easily transformed.
    Rectangle r = baseShape.getBounds();
    AffineTransform centerTransform = AffineTransform.getTranslateInstance(-r.x -r.width/2, -r.y -r.height/2);
    baseShape = centerTransform.createTransformedShape(baseShape);

    shapes = new Shape[5][3]; // 5 arrays of 3 elements
    
    // Scaled versions of the base shape
    shapes[0][0] = baseShape;
    shapes[0][1] = AffineTransform.getScaleInstance(.5, .5).createTransformedShape(baseShape);
    shapes[0][2] = AffineTransform.getScaleInstance(1.5, 1.5).createTransformedShape(baseShape);

    // Rotated versions of the base shape
    shapes[1][0] = AffineTransform.getRotateInstance(-Math.PI/4).createTransformedShape(baseShape);
    shapes[1][1] = AffineTransform.getRotateInstance(-Math.PI/2).createTransformedShape(baseShape);
    shapes[1][2] = AffineTransform.getRotateInstance(3*Math.PI/4).createTransformedShape(baseShape);

    // Sheared versions of the base shape
    shapes[2][0] = AffineTransform.getShearInstance(1, 0).createTransformedShape(baseShape);
    shapes[2][1] = AffineTransform.getShearInstance(0, 1).createTransformedShape(baseShape);
    shapes[2][2] = AffineTransform.getShearInstance(.5, .5).createTransformedShape(baseShape);
    
    // Translated versions of the base shape
    shapes[3][0] = AffineTransform.getTranslateInstance(20, 0).createTransformedShape(baseShape);
    shapes[3][1] = AffineTransform.getTranslateInstance(0, 20).createTransformedShape(baseShape);
    shapes[3][2] = AffineTransform.getTranslateInstance(20, -20).createTransformedShape(baseShape);

    // Reflected versions of the base shape
    shapes[4][0] = AffineTransform.getScaleInstance(-1, 1).createTransformedShape(baseShape);
    shapes[4][1] = AffineTransform.getScaleInstance(1, -1).createTransformedShape(baseShape);
    shapes[4][2] = AffineTransform.getScaleInstance(-1, -1).createTransformedShape(baseShape);

    setPreferredSize(new Dimension(CELL_SIZE.width*3, CELL_SIZE.height*5));

  }  

  public void paint(Graphics _g){  
    // Casting to Graphics2D gives us access to the  
    // new 2D features.  
    Graphics2D g = (Graphics2D)_g;  

    // Control Rendering quality. Here, we set antialiasing  
    // on to get a smoother rendering.  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
			 RenderingHints.VALUE_ANTIALIAS_ON);  

    // Keep a reference to the default transform, so that 
    // we can revert to it later.
    AffineTransform defaultTransform = g.getTransform();

    g.setPaint(getForeground());
    int nRows = shapes.length;
    for(int i=0; i<nRows; i++){
      int nCols = shapes[i].length;
      for(int j=0; j<nCols; j++){
	g.setTransform(defaultTransform);
	g.translate(j*CELL_SIZE.width + CELL_SIZE.width/2, 
		    i*CELL_SIZE.height + CELL_SIZE.height/2);
	g.drawRect(-CELL_SIZE.width/2, -CELL_SIZE.height/2, CELL_SIZE.width, CELL_SIZE.height);
	g.fill(shapes[i][j]);
      }
    }
  }

  public static void main(String args[]){ 
    AffineTransformTypes typesDemo = new AffineTransformTypes();
    typesDemo.setForeground(new Color(103, 103, 138));
    new SnippetFrame(typesDemo);
  }  
} 
 
