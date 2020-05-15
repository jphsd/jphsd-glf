/*
 * @(#)CompositeStrokeUsage.java
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

import com.sun.glf.goodies.*;
/**
 * Illustrates usage of the RadialGradientPaintExt class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/21/1999
 */
public class CompositeStrokeUsage extends JComponent{
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int w = getSize().width;
    int h = getSize().height;

    Stroke dashStroke 
      = new BasicStroke(3f, 
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND,
			0f, new float[]{0, 5}, 0);
    BasicStroke simpleOutline = new BasicStroke(); // 1 point wide solid stroke
    CompositeStroke compositeStroke = new CompositeStroke(dashStroke, simpleOutline);
    
    GeneralPath eiffel = new GeneralPath();  
    eiffel.moveTo(40.f, 20.f);  
    eiffel.lineTo(50.f, 20.f);  
    eiffel.quadTo(50.f, 50.f, 70.f, 80.f); 
    eiffel.lineTo(55.f, 80.f); 
    eiffel.curveTo(55.f, 60.f, 35.f,   
		   60.f, 35.f, 80.f);   
    eiffel.lineTo(20.f, 80.f); 
    eiffel.quadTo(40.f, 50.f, 40.f, 20.f); 

    AffineTransform scale = AffineTransform.getScaleInstance(3, 3);
    g.transform(scale);

    // Draw the Shape with the default stroke
    g.setPaint(new Color(60, 60, 80));
    g.draw(eiffel);
    
    // Create a border rectangle from the shape's bounds
    Rectangle border = eiffel.getBounds();
    border.x -= 10;
    border.y -= 10;
    border.width += 20;
    border.height += 20;
    
    g.setStroke(compositeStroke);
    g.draw(border); // This actually draws the 'dots' outline with blue\
  }

  public static void main(String args[]){
    CompositeStrokeUsage cmp = new CompositeStrokeUsage();
    cmp.setPreferredSize(new Dimension(400, 400));
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
