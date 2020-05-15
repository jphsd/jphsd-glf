/*
 * @(#)CustomGlyphLayout.java
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
 * Illustrates how to modify the default glyph layout
 *
 * @author       Vincent Hardy
 * @version      1.0, 01/12/1999
 */
public class CustomGlyphLayout extends JComponent {
  GlyphVector glyphVector;
  LineMetrics metrics;
  double maxWidth;

  public CustomGlyphLayout(String fontName, String text){
    // We will be using antialiasing and fractional metrics. Create
    // a FontRenderContext that reflects those options.
    FontRenderContext frc = new FontRenderContext(null, true, true);

    Font font = new Font(fontName, Font.PLAIN, 90);
    metrics = font.getLineMetrics(text, frc);

    glyphVector = font.createGlyphVector(frc, text);

    //
    // Modify the glyph positions: find the largest glyph width
    // and space all glyphs so that they all appear inside a 
    // same size rectangle
    // 
    int n = glyphVector.getNumGlyphs();

    // First, find out the largest glyph
    for(int i=0; i<n; i++){
      Shape gBounds = glyphVector.getGlyphVisualBounds(i);
      Rectangle2D bounds = gBounds.getBounds2D();
      maxWidth = Math.max(maxWidth, bounds.getWidth());
    }

    maxWidth += 10;

    // Now, center each glyph in its allocated maxWidth space
    for(int i=0; i<n; i++){
      Shape gBounds = glyphVector.getGlyphVisualBounds(i);
      Rectangle2D bounds = gBounds.getBounds2D();
      Point2D pos = glyphVector.getGlyphPosition(i);

      pos.setLocation(i*maxWidth + (maxWidth - bounds.getWidth())/2, 
		      pos.getY());

      glyphVector.setGlyphPosition(i, pos);
    }

    // Preprocess preferred size based on metrics
    int width = (int)(maxWidth*n + 40);
    int height = (int)(metrics.getAscent() + metrics.getDescent() + 40);
    setPreferredSize(new Dimension(width, height));
    
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		       RenderingHints.VALUE_ANTIALIAS_ON);

    //
    // Fill background with white
    //
    g.setPaint(Color.white);
    g.fillRect(0, 0, getSize().width, getSize().height);

    //
    // Draw glyphs with gray
    //
    g.setPaint(Color.gray);
    g.drawGlyphVector(glyphVector, 20, 20 + metrics.getAscent());

    //
    // Draw decoration
    //
    int n = glyphVector.getNumGlyphs();
    for(int i=0; i<=n; i++){
      g.draw(new Line2D.Double(20 + i*maxWidth, 20, 20 + i*maxWidth, 20 + metrics.getAscent() + metrics.getDescent()));
    }

    g.draw(new Line2D.Double(20, 20, 20 + n*maxWidth, 20));
    g.draw(new Line2D.Double(20, 20 + metrics.getAscent() + metrics.getDescent(),
			     20 + n*maxWidth, 20 + metrics.getAscent() + metrics.getDescent()));
	   
  }

  static final String USAGE = "java com.sun.glf.snippets.CustomGlyphLayout <fontFaceName> <word>";
  public static void main(String args[]){
    if(args.length<2){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new CustomGlyphLayout(args[0], args[1]));
  }
}
