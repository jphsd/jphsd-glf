/*
 * @(#)DukeShapeDemo.java
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
 *
 * Java marks and logos are trademarks or registered trademarks of Sun Microsystems, Inc. 
 * in the United States and other countries.  The Duke logo and character is used 
 * herein for demonstration purposes only, and may not be copied or reproduced by 
 * you in any way without first obtaining written permission from Sun 
 * Microsystems.
 *
 */
package com.sun.glf.snippets;

import java.awt.*;  
import java.awt.geom.*;  

import javax.swing.*;
/**
 * Illustrates how different types of Shapes can be combined to create a sophisticated
 * line art figure.
 *
 * @author            Vincent Hardy
 * @version           1.0
 */
class DukeShape{  
  static public final Paint headPaint = Color.black;  
  static public final Paint nosePaint = new Color(148, 8, 57);  
  static public final Paint noseShinePaint = Color.white; 
  static public final Paint background = Color.white;  

  static Shape shape = null;  
  static Shape body = null;  
  static Shape nose = null;  
  static Shape noseShine = null; 
  static Shape head = null;  
  static Shape arms = null;  

  /**  
   * Nose is an Ellipse2D.  
   */  
  public static Shape getNose(){  
    if(nose==null)  
      nose = new Ellipse2D.Double(70, 75, 60, 50);  
    return nose;  
  }  

  /** 
   * Nose shine is an Ellipse2D 
   */ 
  public static Shape getNoseShine(){ 
    if(noseShine==null){ 
      AffineTransform t = new AffineTransform(); 
      t.setToTranslation(90, 90); 
      t.rotate(-Math.PI/10); 
      noseShine = t.createTransformedShape(new Ellipse2D.Double(-12, -9, 24, 18)); 
    } 
    return noseShine; 
  } 

  /**  
   * Arms are the concatenation of two cubic curves in a  
   * GeneralPath  
   */  
  public static Shape getArms(){  
    if(arms==null){  
      CubicCurve2D leftArm = new CubicCurve2D.Double(138, 100,  
						     170, 135,  
						     180, 120,  
						     150, 140); 

      CubicCurve2D rightArm = new CubicCurve2D.Double(63, 100,  
						      33, 135,  
						      23, 120,  
						      53, 140); 

      GeneralPath a = new GeneralPath(leftArm);  
      a.append(rightArm, false);  
      arms = a;  
    }  
    return arms;  
  }  

  /**  
   * Body is composed of 4 cubic curves.  
   */  
  public static Shape getBody(){  
    if(shape==null){  
      GeneralPath s = new GeneralPath();  
      s.moveTo(100, 25);  
      s.curveTo(140, 60, 170, 200, 140, 200); 
      s.curveTo(130, 200, 120, 175, 90, 175); 
      s.curveTo(70, 175, 70, 200, 60, 200); 
      s.curveTo(40, 200, 60, 60, 100, 25); 
      shape = s;  
    }  
    return shape;  
  }   

  /**  
   * Head = Body - Nose - Ellipse2D   
   * The ellipse part clipping out the body bottom  
   */  
  public static Shape getHead(){  
    if(head==null){  
      Area h = new Area(getBody());  
      h.subtract(new Area(getNose()));  
      h.subtract(new Area(new Ellipse2D.Double(-50, 100, 300, 250)));  
      head = h;  
    }  
    return head;  
  }  
}  

public class DukeShapeDemo extends JComponent{  
  private BasicStroke outlineStroke = new BasicStroke(4.f);  
  private int scale = 3;

  /**
   * Preferred size is based on body size
   */
  public DukeShapeDemo(){
    Rectangle bounds = DukeShape.getBody().getBounds();
    setPreferredSize(new Dimension(scale*((bounds.width*3)/2 + 2*bounds.x), scale*(bounds.height + 2*bounds.y)));
  }

  /**  
   * Painting's composition is:  
   *  + Background filled with a gradient paint.  
   *  + Character's dropping shadow is painted transparently on background  
   *  + Character is painted with usual colors and a thick stroke.  
   */  
  public void paint(Graphics _g){  
    Graphics2D g = (Graphics2D)_g;  

    g.scale(scale, scale);

    // Control Quality  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   
			      RenderingHints.VALUE_ANTIALIAS_ON);  

    Rectangle bounds = DukeShape.getBody().getBounds();  

    // Fill background with white
    Paint bp = Color.white;
    g.setPaint(bp);
    g.fillRect(0, 0, getSize().width, getSize().height);  

    // Draw shadow : Fill the outline with gray Paint
    AffineTransform transform = new AffineTransform();   
    transform.setToTranslation(10, 10 + bounds.y + bounds.height);  
    transform.scale(1.0, 0.6);  
    transform.shear(-0.5, 0.);  
    transform.translate(0., -bounds.y -bounds.height);  

    // Keep a reference to the default
    // transform and composite so that it can be restored.
    AffineTransform graphicsDefaultTransform = g.getTransform();

    // Set rendering attributes.  
    g.setPaint(Color.gray);  
    g.transform(transform);  
    g.setStroke(outlineStroke);  

    // Rendering will be done with attributes we set  
    g.fill(DukeShape.getBody());  
    g.draw(DukeShape.getArms());  
     

    // Paint character with usual colors.   
    // First, reset rendering parameters.  
    g.setTransform(graphicsDefaultTransform); // Restores default transform
    g.setPaint(DukeShape.background);  

    // Render the different parts, simply changing  
    // the paint used to render.  
    g.fill(DukeShape.getBody());  
     
    g.setPaint(DukeShape.headPaint);  
    g.fill(DukeShape.getHead());  
     
    g.setPaint(DukeShape.nosePaint);  
    g.fill(DukeShape.getNose()); 

    g.setPaint(DukeShape.noseShinePaint); 
    g.fill(DukeShape.getNoseShine()); 

    g.setPaint(getForeground());  
    g.draw(DukeShape.getBody());  
    g.draw(DukeShape.getNose());
    g.draw(DukeShape.getArms());  
  }  

  public static void main(String args[]){  
    new SnippetFrame(new DukeShapeDemo());
  }  
}
