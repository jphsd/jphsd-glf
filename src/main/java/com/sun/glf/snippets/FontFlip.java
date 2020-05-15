/*
 * @(#)FontFlip.java
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
import java.text.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;

/**
 * Illustrates Font.deriveFont and creates a reflected version of 
 * one of the system Fonts.
 *
 * @author       Vincent Hardy
 * @version      1.1, 10.20.1998
 */
public class FontFlip extends JComponent{
  // Text we render
  String text = "The quick fox";
  
  // Rendering context parameters
  Font font = new Font("serif", Font.BOLD | Font.ITALIC, 120);   

  // The following transform performs a flip along the x axis
  AffineTransform flip = AffineTransform.getScaleInstance(1, -1);

  public FontFlip(){
    //
    // Preprocess size based on text metrics
    //
    FontRenderContext frc = new FontRenderContext(null, true, true);
    TextLayout layout = new TextLayout(text, font, frc);

    // Height: baseline is at 120 on the y-axis. Leave an equal amount of space top and bottom
    // (i.e. (120 - layout.getAscent()). Use getAscent because of the flipped font.
    setPreferredSize(new Dimension((int)(layout.getAdvance() + 40), // Advance plus 20 margin
				   (int)(120 + layout.getAscent() + 2*(120 - layout.getAscent()))));
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		       RenderingHints.VALUE_ANTIALIAS_ON);

    Font flippedFont = font.deriveFont(flip);

    // Use each font in turn
    g.setFont(font);
    g.setPaint(Color.black);
    g.drawString(text, 20, 120);

    g.setFont(flippedFont);
    g.setPaint(new Color(0, 0, 0, 128)); // 50% transparent black
    g.drawString(text, 20, 120);

  }

  public static void main(String args[]){
    new SnippetFrame(new FontFlip());
  }
}
