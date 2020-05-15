/*
 * @(#)AlphaCompositeRules.java
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
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

/**
 * Illustrates the use of the AlphaComposite class by showing the output created
 * with different settings for that class's constructors. The code shows the result
 * for the different Porter-Duff composition rules, with different alpha values.
 * 
 * This example uses offscreen rendering
 *
 * @author    Vincent Hardy
 * @version   1.0, 12/30/1998
 */
public class AlphaCompositeRules extends JComponent{
  // List of each AlphaCompositeRule, using two different alpha values
  static AlphaComposite alphaComposites[] = {
    AlphaComposite.Clear,
    AlphaComposite.getInstance(AlphaComposite.CLEAR, .5f),
    AlphaComposite.DstIn,
    AlphaComposite.getInstance(AlphaComposite.DST_IN, .5f),
    AlphaComposite.DstOut,
    AlphaComposite.getInstance(AlphaComposite.DST_OUT, .5f),
    AlphaComposite.DstOver,
    AlphaComposite.getInstance(AlphaComposite.DST_OVER, .5f),
    AlphaComposite.Src,
    AlphaComposite.getInstance(AlphaComposite.SRC, .5f),
    AlphaComposite.SrcIn,
    AlphaComposite.getInstance(AlphaComposite.SRC_IN, .5f),
    AlphaComposite.SrcOut,
    AlphaComposite.getInstance(AlphaComposite.SRC_OUT, .5f),
    AlphaComposite.SrcOver,
    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f),
  };

  BufferedImage buf;
  Shape blueDisc, redRect;
  Rectangle bounds;

  public AlphaCompositeRules(){
    redRect = new Rectangle(3, 3, 50, 25);
    blueDisc = new Ellipse2D.Float(30, 0, 40, 40);

    /*AffineTransform scale = AffineTransform.getScaleInstance(8, 8);
    redRect = scale.createTransformedShape(redRect);
    blueDisc = scale.createTransformedShape(blueDisc);*/

    bounds = redRect.getBounds();
    bounds.add(blueDisc.getBounds());
    bounds.width += 10;
    bounds.height += 10;
    buf = new BufferedImage((bounds.x + bounds.width)*2,
			    (bounds.y + bounds.height)*alphaComposites.length/2,
			    BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g = buf.createGraphics();
    setPreferredSize(new Dimension(buf.getWidth(), buf.getHeight()));

    paintGraphics(g);
  }

  public void paintGraphics(Graphics2D g){
    // g.scale(.25, .25);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 
    // Iterate through the list of alphaComposites
    //
    Color red = new Color(200, 50, 50);
    Color blue = new Color(50, 50, 120);

    BufferedImage blueDiscBuf = new BufferedImage(bounds.x + bounds.width, bounds.y + bounds.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = blueDiscBuf.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setPaint(blue);
    g2.fill(blueDisc);
    g2.dispose();

    int nComposites = alphaComposites.length;
    for(int i=0; i<nComposites; i++){
      // Set default composite and paint a red square
      g.setPaint(red);
      g.setComposite(AlphaComposite.SrcOver);
      g.fill(redRect);

      // Set current composite and blue paint
      g.setPaint(blue);
      g.setComposite(alphaComposites[i]);
      g.fill(blueDisc);
      // g.drawImage(blueDiscBuf, 0, 0, null);

      if((i%2)==0)
	g.translate(bounds.width, 0);
      else
	g.translate(-bounds.width, bounds.height);
    }

  }

  /**
   * Note the different behavior if use use the commented line
   * instead of the offscreen buffer. The reason is that the 
   * offscreen buffer has an alpha channel.
   */
  public void paint(Graphics _g){
    // Use offscreen
    _g.drawImage(buf, 0, 0, null);

    // Alternate, direct rendering. Uncomment to see
    // difference.
    // paintGraphics((Graphics2D)_g);
  }

  public static void main(String args[]){
    new SnippetFrame(new AlphaCompositeRules());
  }
}
