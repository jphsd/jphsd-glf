/*
 * @(#)GradientPaintExtUsage.java
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
 * Illustrates usage of the GradientPaintExt class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/21/1999
 */
public class GradientPaintExtUsage extends JComponent{
  private boolean cycleThrough = false; 

  public GradientPaintExtUsage(boolean cycleThrough){
    this.cycleThrough = cycleThrough;
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    int w = getSize().width;
    int h = getSize().height;
    // g.scale(1.5, 1.5);

    Point start = new Point(40, 40);
    Point end = new Point(300, 300);
    float I[] = {3, 2, 1};
    Color darkGreen = new Color(30, 120, 80);
    Color darkBlue = new Color(10, 20, 40);
    Color colors[] = {Color.white, Color.yellow, darkGreen, darkBlue};
    GradientPaintExt paint = new GradientPaintExt(start, end, I, colors, cycleThrough);
    Rectangle rect = new Rectangle(0, 0, w, h);
    g.setPaint(paint);
    g.fill(rect);

    /*g.setPaint(Color.gray);
    g.setStroke(new BasicStroke(2));
    float dx = (end.x - start.x)/6f;
    float dy = (end.y - start.y)/6f;
    g.drawLine(start.x, start.y,  end.x, end.y);
    
    rect = new Rectangle(start.x - 6, start.y - 6, 12, 12);
    g.draw(rect);
    g.translate(3*dx, 3*dy);
    g.draw(rect);
    g.translate(2*dx, 2*dy);
    g.draw(rect);
    g.translate(dx, dy);
    g.draw(rect);*/
  }

  public static void main(String args[]){
    boolean cycleThrough = args.length<1?false:args[0].equalsIgnoreCase("true");
    GradientPaintExtUsage cmp = new GradientPaintExtUsage(cycleThrough);
    cmp.setPreferredSize(new Dimension(400, 200));
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
