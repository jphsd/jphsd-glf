/*
 * @(#)ShapeClasses.java
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

import javax.swing.*;

/**  
 * Displays a Shape by drawing its outline with the default  
 * stroke, filling it with the fillPaint passed as   
 * an argument.  
 *
 * @author            Vincent Hardy
 */  
class ShapeComponent extends JComponent{  
  Shape shape;  
  Paint fillPaint;  

  public ShapeComponent(Shape shape, Paint fillPaint){  
    if(shape==null || fillPaint==null)  
      throw new IllegalArgumentException();  

    AffineTransform scale = AffineTransform.getScaleInstance(3, 3);
    this.shape = scale.createTransformedShape(shape); // shape;  
    this.fillPaint = fillPaint;  

    // Preprocess preferredSize
    Rectangle bounds = this.shape.getBounds();
    setPreferredSize(new Dimension(bounds.width, bounds.height));
  }  

  public void paint(Graphics _g){  
    Graphics2D g = (Graphics2D)_g;  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
			      RenderingHints.VALUE_ANTIALIAS_ON);  

    g.setPaint(fillPaint);  
    g.fill(shape);  
       
    g.setPaint(getForeground());  
    g.draw(shape);  
  }  
}  

/**  
 * Demonstrates the use of the different shapes:   
 * creates a ShapeComponent for each type of Shape    
 * implementation, in a 3x3 grid.  
 *  - Arc  
 *  - Rectangle,   
 *  - Line  
 *  - Ellipse  
 *  - RoundRectangle  
 *  - QuadCurve  
 *  - CubicCurve  
 *  - Area.   
 *  - GeneralPath.  
 *  - Polygon
 */  
public class ShapeClasses{  
  public static void main(String args[]){  
    JFrame frame = new JFrame("Shape types in Java2D");  
    frame.getContentPane().setLayout(new GridLayout(3,3));  
    
    Color paleBlue = new Color(174, 174, 232);  

    // Line  
    Line2D line = new Line2D.Double(10., 10., 50., 50.);  
    frame.getContentPane().add(new ShapeComponent(line, paleBlue));  
       
    // Rectangle   
    Rectangle rect = new Rectangle(10, 10, 50, 40);  
    frame.getContentPane().add(new ShapeComponent(rect, paleBlue));  

    // RoundRectangle  
    RoundRectangle2D rect2 =   
      new RoundRectangle2D.Double(10., 10.,   
				  50., 40.,   
				  20., 15.);  
    frame.getContentPane().add(new ShapeComponent(rect2, paleBlue));  

    // Ellipse  
    Ellipse2D ellipse = new Ellipse2D.Double(10., 10.,   
					     50., 40.);  
    frame.getContentPane().add(new ShapeComponent(ellipse, paleBlue));  

    // Arc  
    Arc2D arc = new Arc2D.Float(10.f, 10.f,    
				50.f, 40.f,  
				45.f, 105.f,    
				Arc2D.PIE);   
    frame.getContentPane().add(new ShapeComponent(arc, paleBlue));  

    // QuadCurve  
    QuadCurve2D quad =   
      new QuadCurve2D.Double(10., 10.,   
			     80., 25.,   
			     10., 50.);  
    frame.getContentPane().add(new ShapeComponent(quad, paleBlue));  

    // CubicCurve  
    CubicCurve2D wave = new CubicCurve2D.Double(10., 30.,   
						35., -30.,  
						30., 90.,   
						60., 30.);  
    frame.getContentPane().add(new ShapeComponent(wave, paleBlue));  

    // Area  
    Area cheese = new Area(rect);  
    cheese.subtract(new Area(new Ellipse2D.Double(5., 15.,   
						  20., 20.)));  
    cheese.subtract(new Area(new Ellipse2D.Double(30., 20.,  
						  15., 15.)));  
    cheese.subtract(new Area(new Ellipse2D.Double(45., 35.,   
						  20., 20.)));  
    frame.getContentPane().add(new ShapeComponent(cheese,   
				 new Color(255, 236, 102)));  

    // General Path  
    GeneralPath eiffel = new GeneralPath();  
    eiffel.moveTo(20.f, 0.f);  
    eiffel.lineTo(30.f, 0.f);  
    eiffel.quadTo(30.f, 30.f, 50.f, 60.f);  
    eiffel.lineTo(35.f, 60.f);  
    eiffel.curveTo(35.f, 40.f, 15.f, 40.f, 15.f, 60.f);  
    eiffel.lineTo(0.f, 60.f);  
    eiffel.quadTo(20.f, 30.f, 20.f, 0.f);  
    frame.getContentPane().add(new ShapeComponent(eiffel, paleBlue));  

    // Polygon
    Polygon polygon = new Polygon();
    polygon.addPoint(20, 0);
    polygon.addPoint(40, 0);
    polygon.addPoint(60, 20);
    polygon.addPoint(60, 40);
    polygon.addPoint(40, 60);
    polygon.addPoint(20, 60);
    polygon.addPoint(0, 40);
    polygon.addPoint(0, 20);
    polygon.addPoint(20, 0);
    frame.getContentPane().add(new ShapeComponent(polygon, paleBlue));

    // Set frame background to white
    frame.getContentPane().setBackground(Color.white);

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });
    
    frame.pack();
    frame.setVisible(true);
  }  
}





