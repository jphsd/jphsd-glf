/*
 * @(#)FontTransform.java
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
 * Illustrates the spatial characteristics of the TRANSFORM
 * Font attribute.
 *
 * @author          Vincent Hardy
 * @version         02.12.1999
 */
public class FontTransform extends JComponent{
  private Font font = new Font("serif", Font.BOLD | Font.ITALIC, 60);   
  private Font flippedFont;
  private Font rotatedFont;
  private AttributedString string;

  public FontTransform(){
    // Two different ways of creating a Font with the TRANSFORM attribute:
    // + derive
    // + map based Font creation

    // Base font
    Font font = new Font("serif", Font.BOLD | Font.ITALIC, 60);   

    // TRANSFORM attribute value
    AffineTransform flip = AffineTransform.getScaleInstance(1, -1);
    AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI/8);

    // First method : derivation
    rotatedFont = font.deriveFont(rotate);

    // Second method : map based creation
    Hashtable map = new Hashtable();
    map.put(TextAttribute.FAMILY, "Times");
    map.put(TextAttribute.SIZE, new Float(60));
    map.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    map.put(TextAttribute.TRANSFORM, flip);
    flippedFont = new Font(map);

    // Build an attributed string with varying FONT attributes
    string = new AttributedString("Rotated, flipped, rotated, Flipped");
    string.addAttribute(TextAttribute.FONT, rotatedFont, 0, 9);
    string.addAttribute(TextAttribute.FONT, flippedFont, 9, 18);
    string.addAttribute(TextAttribute.FONT, rotatedFont, 18, 27);
    string.addAttribute(TextAttribute.FONT, flippedFont, 27, 34);

    // Process preferred size
    FontRenderContext frc = new FontRenderContext(null, true, true);
    TextLayout layout = new TextLayout(string.getIterator(), frc);
    int width = (int)(layout.getAdvance() + 40); // Leave margin of 20 on each side
    int height = (int)(120 + layout.getAdvance()*.15 + (120 - layout.getAscent())); // Approximation
    setPreferredSize(new Dimension(width, height));
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		       RenderingHints.VALUE_ANTIALIAS_ON);

    g.drawString(string.getIterator(), 20, 120);
  }

  public static void main(String args[]){
    new SnippetFrame(new FontTransform());
  }
}
