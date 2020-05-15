/*
 * @(#)TextStrokeUsage.java
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
public class TextStrokeUsage extends JComponent{
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int w = getSize().width;
    int h = getSize().height;
    g.scale(.5f, .5f);

    g.setPaint(new Color(20, 60, 90));

    Rectangle rect = new Rectangle(40, 40, 200, 200);
    Ellipse2D disc = new Ellipse2D.Double(280, 40, 200, 200);
    GeneralPath triangle = new GeneralPath();
    triangle.moveTo(620, 40);
    triangle.lineTo(720, 240);
    triangle.lineTo(520, 240);
    triangle.lineTo(620, 40);

    Font font = new Font("dialog", Font.PLAIN, 20);
    Stroke cycleTextStroke = new TextStroke("Text Strokes are fun", font, true, 10);
    g.setStroke(cycleTextStroke);
    
    g.draw(rect);
    g.draw(disc);
    g.draw(triangle);

    g.setStroke(new BasicStroke());
    g.translate(0, 240);
    
    g.draw(rect);
    g.draw(disc);
    g.draw(triangle);

    Stroke textStroke = new TextStroke("Text Strokes are fun", font, false, 0);
    g.setStroke(textStroke);

    g.translate(0, 240);
    g.draw(rect);
    g.draw(disc);
    g.draw(triangle);


  }

  public static void main(String args[]){
    TextStrokeUsage cmp = new TextStrokeUsage();
    cmp.setPreferredSize(new Dimension(420, 420));
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
