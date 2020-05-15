/*
 * @(#)StrokeAttribute.java
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
 * Illustrates setting of the Stroke graphic context attribute
 *
 * @author         Vincent Hardy
 * @version        1.0
 */
public class StrokeAttribute extends JComponent{
  private GeneralPath eiffel = new GeneralPath(); 
  private int scale = 5;
  private float strokeWidth = 3f;

  public StrokeAttribute(){
    // Create an Eiffel tower Shape
    eiffel.moveTo(43.f, 5.f);  
    eiffel.lineTo(58.f, 5.f);  
    eiffel.quadTo(58.f, 50.f, 88.f, 95.f);  
    eiffel.lineTo(65.f, 95.f);  
    eiffel.curveTo(65.f, 65.f, 35.f, 65.f, 35.f, 95.f);  
    eiffel.lineTo(13.f, 95.f);  
    eiffel.quadTo(43.f, 50.f, 43.f, 5.f); 

    // Position Shape in (10, 10)
    Rectangle bounds = eiffel.getBounds();
    AffineTransform t = AffineTransform.getTranslateInstance(-bounds.x + 10,
							     -bounds.y + 10);
    eiffel = new GeneralPath(t.createTransformedShape(eiffel));

    int width = (int)(bounds.width + 20 + strokeWidth*2);
    int height = (int)(bounds.height + 20 + strokeWidth*2);
    System.out.println(width + " " + height);

    setPreferredSize(new Dimension(width*scale, height*scale));
  }
  public void paint(Graphics _g){
    // Casting to Graphics2D gives us access to the  
    // new 2D features.  
    Graphics2D g = (Graphics2D)_g;
    
    // Control Rendering quality. Here, we set antialiasing  
    // on to get a smoother rendering.  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
		       RenderingHints.VALUE_ANTIALIAS_ON); 
    g.scale(scale, scale);
    g.translate(strokeWidth, strokeWidth);

    //
    // Draw the Shape with the default stroke
    //
    g.setPaint(new Color(60, 60, 80));
    g.draw(eiffel);

    // Create a border rectangle from the shape's bounds
    Rectangle border = eiffel.getBounds();
    border.x -= 10;
    border.y -= 10;
    border.width += 20;
    border.height += 20;

    //
    // Draw the border with a dash Stroke
    //
    Stroke dashStroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[]{0, 5}, 0);
    g.setStroke(dashStroke);
    g.draw(border); // This actually fills the 'dots' with blue
    
  }
  public static void main(String args[]){
    new SnippetFrame(new StrokeAttribute());
  }
}

