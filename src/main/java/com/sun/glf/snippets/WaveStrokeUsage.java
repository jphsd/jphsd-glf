/*
 * @(#)WaveStrokeUsage.java
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
 * Demonstrates the use of the WaveStroke custom Stroke implementation.
 *
 * @author        Vincent Hardy
 * @version       1.0, 10/22/1998
 */

/**
 * The StrokeSampler component displays a set of shapes drawn
 * with the strokes passed at construction time.
 */
public class WaveStrokeUsage extends JComponent {
  private float scale = 4;

  public WaveStrokeUsage(){
    setPreferredSize(new Dimension((int)(120*scale), (int)(40*scale)));
  }

  public void paint(Graphics _g){
    Stroke stroke = new WaveStroke(3, 10, 4); // 12 point length, 4 in amplitude and 3 points thick.
    Graphics2D g = (Graphics2D)_g;
    g.scale(scale, scale);
    g.setPaint(Color.gray);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setStroke(stroke);
    g.drawLine(20, 20, 100, 20);

    g.setPaint(Color.black);

    stroke = new WaveStroke(1.5f/scale, 10, 4);
    g.setStroke(stroke);
    g.drawLine(20, 20, 100, 20);

    /*BasicStroke b = new BasicStroke(1.5f/scale);
    g.setStroke(b);

    g.drawLine(20, 14, 100, 14);
    g.drawLine(20, 26, 32, 26);
    // g.drawLine(104, 18, 104, 22);

    BasicStroke ds = new BasicStroke(1.5f/scale, b.CAP_ROUND, b.JOIN_MITER, 1, new float[]{6f/scale, 6f/scale}, 0);
    g.setStroke(ds);

    // Wave Length
    g.drawLine(20, 12, 20, 28);
    g.drawLine(32, 12, 32, 28);

    // Line end
    g.drawLine(100, 12, 100, 28);

    // Wave amplitude
    g.drawLine(87, 18, 106, 18);
    g.drawLine(87, 22, 106, 22);

    // Width
    g.draw(new Line2D.Float(14, 16.5f, 33, 16.5f));
    g.draw(new Line2D.Float(14, 19.5f, 33, 19.5f));*/

    

  }

  public static void main(String args[]){
    new SnippetFrame(new WaveStrokeUsage());
  }

}


