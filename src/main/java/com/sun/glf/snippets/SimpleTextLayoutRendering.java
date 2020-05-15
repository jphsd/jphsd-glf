/*
 * @(#)SimpleTextLayoutRendering.java
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
import java.awt.image.*;
import java.text.*;

import javax.swing.*;

/**
 * Simple text rendering demo using TextLayout.
 * The paint method illustrates how the TextLayout can 
 * be used for simple text rendering as it contains all the 
 * text metrics and can also render text.
 *
 * @author        Vincent Hardy
 * @version       12/28/1998
 */
public class SimpleTextLayoutRendering extends JComponent {
  // Default margin around text
  static final int MARGIN = 10;

  // Font used to display text
  private Font font = new Font("serif", Font.PLAIN, 60);

  public SimpleTextLayoutRendering(){
    FontRenderContext frc = new FontRenderContext(null, true, true);
    TextLayout layout = new TextLayout("Simple TextLayout usage", font, frc);
    
    int w = (int)layout.getAdvance();
    int h = (int)(layout.getAscent() + layout.getDescent() + layout.getLeading());
    w += 2*MARGIN;
    h += 2*MARGIN;
    setPreferredSize(new Dimension(w, h));
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    int w = getSize().width;

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		       RenderingHints.VALUE_ANTIALIAS_ON);

    TextLayout layout = new TextLayout("Simple TextLayout usage", font, g.getFontRenderContext());

    // Horizontally center the text using the layout metrics.
    layout.draw(g, (w - layout.getAdvance())/2, layout.getAscent());
  }

  public static void main(String args[]){
    new SnippetFrame(new SimpleTextLayoutRendering());
  }
}
