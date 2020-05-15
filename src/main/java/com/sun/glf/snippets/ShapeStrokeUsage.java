/*
 * @(#)ShapeStrokeUsage.java
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
 * Illustrates usage of the ShapeStroke class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/21/1999
 */
public class ShapeStrokeUsage extends JComponent{
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    float scale = 1;
    g.scale(scale, scale);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int w = getSize().width;
    int h = getSize().height;
    g.setPaint(new Color(20, 60, 90));

    Shape smallDot = new Ellipse2D.Float(0, 0, 12, 12);
    
    // small rectangle 
    Shape smallRect = new Rectangle(0, 0, 12, 12);

    // Small Triangle
    GeneralPath smallTriangle = new GeneralPath();
    smallTriangle.moveTo(6, 0);
    smallTriangle.lineTo(12, 12);
    smallTriangle.lineTo(0, 12);
    smallTriangle.closePath();

    Shape pattern[] = { smallDot, smallRect, smallTriangle };
    ShapeStroke shapeStroke = new ShapeStroke(pattern, 20);

    // g.setStroke(new BasicStroke(1.5f/scale));
    // g.drawLine(40, 40, 200, 40);

    // g.translate(0, 12);
    g.setStroke(shapeStroke);

    // g.drawLine(40, 40, 200, 40);

    // g.translate(0, 80);

    Rectangle rect = new Rectangle(40, 40, 200, 200);
    g.draw(rect);

    Ellipse2D disc = new Ellipse2D.Double(280, 40, 200, 200);
    g.draw(disc);
  }

  public static void main(String args[]){
    ShapeStrokeUsage cmp = new ShapeStrokeUsage();
    cmp.setPreferredSize(new Dimension(520, 280));
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
