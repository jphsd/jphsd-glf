/*
 * @(#)SimpleTextRendering.java
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
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;

/**
 * Simple text rendering demo.
 *
 * @author        Vincent Hardy
 * @version       12/28/1998
 */
public class SimpleTextRendering extends JComponent {
  String text = "B";
  Font font = new Font("serif", Font.PLAIN, 300);  

  public SimpleTextRendering(){
    // We will render using fractional metrics and antialiasing.
    // Use such a context to preprocess spatial information.
    FontRenderContext frc = new FontRenderContext(null, true, true);
 
    // Preprocess size from font metrics. We use TextLayout
    // as in the HelloRenderingModel example.
    TextLayout layout = new TextLayout(text, font, frc);
    int width = (int)(layout.getAdvance() + 5*2); // Leave a margin of 5 units on each side
    int height = (int)(layout.getAscent() + layout.getDescent() + 5*2); // Leave a 5 unit margin top and bottom.
    
    setPreferredSize(new Dimension(width,height));
  }
    
    
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		       RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
		       RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    g.setPaint(Color.white);
    g.fillRect(0, 0, getSize().width, getSize().height);

    g.setFont(font);
    LineMetrics metrics = font.getLineMetrics(text, 
					      g.getFontRenderContext());

    g.setPaint(Color.black);
    g.setStroke(new BasicStroke(.1f));
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
    g.drawString(text, 5, metrics.getAscent() + 5);
  }

  public static void main(String args[]){
    new SnippetFrame(new SimpleTextRendering());
  }
}
