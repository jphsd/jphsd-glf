/*
 * @(#)RenderingHintsAttribute.java
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

import javax.swing.*;

/**
 * Illustrates setting of the RenderingHints attribute.
 *
 * @author         Vincent Hardy
 * @version        1.0, 01.03.1999
 */
public class RenderingHintsAttribute extends JComponent{
  private Font scriptFont = new Font("French Script MT", Font.PLAIN, 40);
  private float margin = 20;
  private float x, y, w, h;
  private int scale = 5;

  public RenderingHintsAttribute(){
    FontRenderContext frc = new FontRenderContext(null, true, false);
    TextLayout layout = new TextLayout("L", scriptFont, frc);
    y = layout.getAscent() + layout.getLeading();
    x = margin;
    w = layout.getAdvance();
    h = y + layout.getDescent() + layout.getLeading();

    System.out.println(x + " " + y + " " + w + " " + h );
    setPreferredSize(new Dimension(scale*(int)(2*w + 3*margin), scale*(int)h));
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    g.scale(scale, scale);
    g.setFont(scriptFont);

    // First, drawString with aliasing
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);
    g.drawString("L", x, y);

    // Second, drawString with antialiasing
    g.translate(margin + w, 0);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawString("L", x, y);

  }

  public static void main(String args[]){
    new SnippetFrame(new RenderingHintsAttribute());
  }

  static {
    com.sun.glf.util.Toolbox.initFonts();
  }
}
