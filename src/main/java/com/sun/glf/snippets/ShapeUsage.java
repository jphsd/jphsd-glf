/*
 * @(#)ShapeUsage.java
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
 * Illustrates the different uses for the Shape class
 *
 * @author           Vincent Hardy
 * @version          1.0
 */
public class ShapeUsage extends JComponent {  
  GeneralPath eiffel = new GeneralPath();  
  Stroke dashStroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
				      BasicStroke.JOIN_ROUND, 10f,
				      new float[]{4f, 4f}, 0f);
  int margin = 10; 
  int scale = 3;

  public void paint(Graphics _g){  
    // Casting to Graphics2D gives us access to the  
    // new 2D features.  
    Graphics2D g = (Graphics2D)_g;  

    g.scale(scale, scale);
    
    // Control Rendering quality. Here, we set antialiasing  
    // on to get a smoother rendering.  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
			       RenderingHints.VALUE_ANTIALIAS_ON);  

    // The Shape bounds is used to compute translations
    // for each rendered Shape.
    Rectangle bounds = eiffel.getBounds();  

    // Move to the initial rendering position
    g.translate(margin, margin);

    // Set Stroke for first eiffel drawing
    g.setStroke(dashStroke);  

    // Invoke rendering method: a draw.
    g.draw(eiffel);  

    // Move to the next rendering position
    g.translate(bounds.width + margin, 0);

    // Set Paint for filling Shape
    Color blue = new Color(100, 100, 200);
    Color black = Color.black;
    Paint gradientPaint = new GradientPaint(0, bounds.y, blue,
					    0, bounds.y + bounds.width, black);
    g.setPaint(gradientPaint);
    
    // Invoke rendering method: a fill
    g.fill(eiffel);

    // Move to the next rendering position
    g.translate(bounds.width + margin, 0);

    // Create a rotated Shape from an AffineTransform
    AffineTransform rotation 
      = AffineTransform.getRotateInstance(Math.PI/4.f, 
					  bounds.x + bounds.width/2, // x axis rotation center
					  bounds.y + bounds.height/2); // y axis rotation center

      Shape rotatedEiffel = rotation.createTransformedShape(eiffel);

    // Render rotated Shape
    g.fill(rotatedEiffel);

    //
    // Clipping
    //

    Shape defaultClip = g.getClip();
    
    g.translate(margin + bounds.width, 0);
    g.setClip(eiffel);  
    g.fill(bounds);

    // Restore clip
    g.setClip(defaultClip);

    //
    // AffineTransform usage: Alternate solution: 
    // set the rendering context transform instead of 
    // creating a transformed Shape.
    //
    
    // Move to rendering position
    g.translate(bounds.width + margin, 0);
    
    // Concatenate rotation
    g.transform(rotation);

    // Fill eiffel
    g.fill(eiffel);

  }  

  public ShapeUsage(){
    //
    // First, build an Eiffel Tower like Shape
    //
    eiffel.moveTo(20.f, 0.f);  
    eiffel.lineTo(30.f, 0.f);  
    eiffel.quadTo(30.f, 30.f, 50.f, 60.f);  
    eiffel.lineTo(35.f, 60.f);  
    eiffel.curveTo(35.f, 40.f, 15.f, 40.f, 15.f, 60.f);  
    eiffel.lineTo(0.f, 60.f);  
    eiffel.quadTo(20.f, 30.f, 20.f, 0.f);  

    // Add hit detector to check on rotated eiffel hits 
    addMouseListener(new MouseAdapter(){  
      Rectangle hitRect = new Rectangle(0, 0, 3, 3);  
      public void mouseClicked(MouseEvent evt){  
        hitRect.x = evt.getX()-1;  
        hitRect.y = evt.getY()-1;  
        Graphics2D g = (Graphics2D)getGraphics(); 
	g.scale(scale, scale);
	g.translate(margin, margin);
	g.setStroke(dashStroke);
        if(g.hit(hitRect, eiffel, true))  
	  System.out.println("Rotated eiffel was hit");  
      }  
    });  

    // 
    // Preprocess preferred size
    //
    Rectangle bounds = eiffel.getBounds();
    int width = 5*bounds.width + 6*margin;
    int height = bounds.height + 2*margin;
    setPreferredSize(new Dimension(width*scale, 
				   height*scale));
  }  

  public static void main(String args[]){  
    new SnippetFrame(new ShapeUsage()); 
  }  
} 
 
