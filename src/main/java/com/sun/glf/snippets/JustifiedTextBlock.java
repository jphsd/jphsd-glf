/*
 * @(#)JustifiedTextBlock.java
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
import java.io.*;
import javax.swing.*;
import java.util.*;

/**
 * Illustrates several features for advanced text rendering:
 * + TextLayout
 * + LineBreakMeasurer
 * + Styled text (AttributedString)
 * + TextAlignment
 *
 * @author            Vincent Hardy
 * @version           1.0, 02.12.1999
 */
public class JustifiedTextBlock extends JComponent {
  String text;
  String fontName;
  int fontSize;
  static final int MARGIN = 40;
  BufferedImage buf;
  TextLayout layouts[];
  Point2D.Float penPositions[];

  public JustifiedTextBlock(String text, String fontName, int fontSize, int w){
    this.text = text;
    this.fontName = fontName;
    this.fontSize = fontSize;

    //
    // First, create an AttributedString from the text and set the default font.
    // 
    Font font = new Font(fontName, Font.PLAIN, fontSize);
    AttributedString str = new AttributedString(text);
    str.addAttribute(TextAttribute.FONT, font);
    str.addAttribute(TextAttribute.FOREGROUND, Color.black);

    //
    // Search for all instances of "TextLayout" in input string
    // and set a different font and color for those instances
    //
    Font tlFont = new Font("monospaced", Font.PLAIN, fontSize);
    Color tlColor = new Color(0, 128, 0);
    int tlLength = "TextLayout".length();
    int i = text.indexOf("TextLayout");
    while(i != -1){
      str.addAttribute(TextAttribute.FONT, tlFont, i, i+tlLength);
      str.addAttribute(TextAttribute.FOREGROUND, tlColor, i, i+tlLength);
      i = text.indexOf("TextLayout", i+1);
    }

    //
    // Now, use a LineBreakMeasurer to break the AttributedString into
    // lines that fit in the wrapwidth
    //
    AttributedCharacterIterator iter = str.getIterator();
    FontRenderContext frc = new FontRenderContext(null, true, true);
    LineBreakMeasurer measurer = new LineBreakMeasurer(iter, frc);

    float wrappingWidth = w - 2*MARGIN;
    float curY = MARGIN;
    Vector vLayouts = new Vector();
    Vector vPenPositions = new Vector();
    TextLayout layout = null;
    Point2D.Float penPosition = null;

    while (measurer.getPosition() < iter.getEndIndex()) {
      layout = measurer.nextLayout(wrappingWidth);

      // Adjust current elevation
      curY += (layout.getAscent());

      // Justify previous line. All lines will
      // be justified, but the last one.
      if(vLayouts.size()>0){
          TextLayout previousLine = (TextLayout)vLayouts.elementAt(vLayouts.size()-1);
          previousLine = previousLine.getJustifiedLayout(wrappingWidth);
          vLayouts.setElementAt(previousLine, vLayouts.size()-1);
      }

      // Store layout position so that text block appears centered
      penPosition = new Point2D.Float(MARGIN, curY);
      vPenPositions.addElement(penPosition);

      // Store layout
      vLayouts.addElement(layout);

      // Move to next line
      curY += layout.getDescent() + layout.getLeading();
    }

    //
    // Store layouts and pen positions in arrays for faster access
    //
    layouts = new TextLayout[vLayouts.size()];
    vLayouts.copyInto(layouts);
    penPositions = new Point2D.Float[vPenPositions.size()];
    vPenPositions.copyInto(penPositions);

    //
    // Draw Offscreen buffer
    //
    Point2D.Float lastPosition = penPositions[penPositions.length-1];
    TextLayout lastLayout = layouts[layouts.length-1];
    int bw = w;
    int bh = (int)(lastPosition.y + lastLayout.getDescent() + MARGIN);
    buf = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = buf.createGraphics();
    
    for(i=0; i<layouts.length; i++)
      layouts[i].draw(g, penPositions[i].x, penPositions[i].y);

    g.dispose();

    setPreferredSize(new Dimension(bw, bh));
  }


  public void paint(Graphics _g){
    _g.drawImage(buf, 0, 0, null);
  }

  static final String USAGE = "java com.sun.glf.snippets.JustifiedTextBlock <textFile> <fontName> <fontSize> <width>";

  public static void main(String args[]) throws Exception{
    if(args.length<4){
      System.out.println(USAGE);
      System.exit(0);
    }

    // Read input file
    File file = new File(args[0]);
    BufferedReader in = new BufferedReader(new FileReader(file));
    StringBuffer buf = new StringBuffer();
    String line = "";
    while((line = in.readLine())!=null)
      buf.append(line + "\n");

    in.close();
    String text = buf.toString();
    
    // Display frame
    new SnippetFrame(new JustifiedTextBlock(text, 
					    args[1], 
					    Integer.parseInt(args[2]),
					    Integer.parseInt(args[3])));
  }
}
