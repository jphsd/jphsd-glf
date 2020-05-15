/*
 * @(#)FontAttribute.java
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
 * Illustrates how to set the rendering context attribute
 *
 * @author             Vincent Hardy
 * @version            1.0, 01.03.1999
 */
public class FontAttribute extends JComponent{
  Font scriptFont = new Font("French Script MT", Font.PLAIN, 40);
  Font funFont = new Font("Curlz MT", Font.PLAIN, 40);

  public FontAttribute(){
    FontRenderContext frc = new FontRenderContext(null, true, true);
  
    // Process preferred size
    TextLayout scriptLayout = new TextLayout("Dear Sir", scriptFont, frc);
    TextLayout funLayout = new TextLayout("You are too serious!", funFont, frc);

    int width = (int)Math.max(scriptLayout.getAdvance(), funLayout.getAdvance()) + 40*2;
    // Second line has its baseline at y=80. Add descent a margin (20) to 
    // prcess height.
    int height = (int)(80 + funLayout.getDescent() + 20); 

    setPreferredSize(new Dimension(width, height));
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    //
    // Set context attributes: hints and font
    //
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);

    g.setFont(scriptFont);

    // First drawString invocation
    g.drawString("Dear Sir", 20, 40);

    // Modify context and invoke drawString again
    g.setFont(funFont);
    g.drawString("You are too serious!", 20, 80);
  }

  public static void main(String args[]){
    new SnippetFrame(new FontAttribute());
  }

  static {
    // Work around font loading issues
    com.sun.glf.util.Toolbox.initFonts();
  }
}
