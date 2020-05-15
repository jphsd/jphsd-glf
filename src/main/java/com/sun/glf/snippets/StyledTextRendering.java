/*
 * @(#)StyledTextRendering.java
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
import java.text.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import java.io.*;

public class StyledTextRendering extends JComponent {
  BufferedImage buf;

  public StyledTextRendering(String fontName, int fontSize){
    FontRenderContext frc = new FontRenderContext(null, true, true);

    String text = "Attributed Strings are fun !";
    AttributedString styledText = new AttributedString(text);

    //
    // Set font family for the whole string
    //
    Font font = new Font(fontName, Font.PLAIN, fontSize);
    styledText.addAttribute(TextAttribute.FAMILY, font.getFamily());
    styledText.addAttribute(TextAttribute.SIZE, new Float(font.getSize()));
    styledText.addAttribute(TextAttribute.FOREGROUND, Color.black);

    //
    // Set font style attributes for different part of the string
    //

    // "Attributed" is in Bold
    styledText.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, 10);

    // "String" is italic
    // styledText.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, 11, 18);

    // fun is Bold and underlined
    styledText.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 23, 28);
    // styledText.addAttribute(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
    // styledText.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 23, 28);

    /*TextLayout aLayout = new TextLayout("A", font, frc);
    Shape aShape = aLayout.getOutline(null);
    
    ShapeGraphicAttribute aReplacement = new ShapeGraphicAttribute(aShape, GraphicAttribute.ROMAN_BASELINE, true);
    styledText.addAttribute(TextAttribute.CHAR_REPLACEMENT, aReplacement, 0, 1);
    

    // Create a BufferedImage to decorate the Shape
    {
    TextLayout aLayout = new TextLayout("A", font, frc);
    Shape aShape = aLayout.getOutline(null);
    Rectangle bounds = aShape.getBounds();

    int blurWidth = 6;
    BufferedImage image = new BufferedImage(bounds.width + blurWidth*4, bounds.height + blurWidth*4,
					    BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    int w = image.getWidth(), h = image.getHeight();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setPaint(Color.black); 
    g2.translate(-bounds.x + (w - bounds.width)/2, -bounds.y + (h - bounds.height)/2);
    g2.fill(aShape);
    g2.setStroke(new BasicStroke(blurWidth/2));
    g2.draw(aShape);
    g2.dispose();

    float k[] = new float[blurWidth*blurWidth];
    for(int i=0; i<k.length; i++) k[i] = 1/(float)k.length;
    Kernel kernel = new Kernel(blurWidth, blurWidth, k);
    ConvolveOp blur = new ConvolveOp(kernel);
    image = blur.filter(image, null);
    g2 = image.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.translate(-bounds.x + (w - bounds.width)/2, -bounds.y + (h - bounds.height)/2);
    g2.setComposite(AlphaComposite.Clear);
    g2.fill(aShape);

    image = image.getSubimage(blurWidth, blurWidth, image.getWidth() - 2*blurWidth, image.getHeight() - 2*blurWidth);

    ImageGraphicAttribute aImageReplacement = new ImageGraphicAttribute(image, GraphicAttribute.ROMAN_BASELINE, blurWidth, 
									blurWidth + bounds.height);
    styledText.addAttribute(TextAttribute.CHAR_REPLACEMENT, aImageReplacement, 0, 1);
    }
    */

    //
    // Set text color
    //
    
    // "Attributed" is in dard red
    styledText.addAttribute(TextAttribute.FOREGROUND, new Color(128, 0, 0), 0, 10);
    
    // "String" is blue
    styledText.addAttribute(TextAttribute.FOREGROUND, new Color(70, 107, 132), 11, 18);

    // "fun" is yellow on blue background
    styledText.addAttribute(TextAttribute.FOREGROUND, new Color(236, 214, 70), 23, 28);
    styledText.addAttribute(TextAttribute.BACKGROUND, new Color(70, 107, 132), 23, 28);

    AttributedCharacterIterator iter = styledText.getIterator();
    TextLayout layout = new TextLayout(iter, frc);

    Rectangle bounds = layout.getBounds().getBounds();
    bounds.width += 50;
    bounds.height += 50;

    setPreferredSize(new Dimension(bounds.width, bounds.height));

    buf = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setPaint(Color.white);
    g.fillRect(0, 0, buf.getWidth(), buf.getHeight());
    
    layout.draw(g, 25, layout.getAscent() + 25);

    // Draw outlines
    /*int n = text.length();
    g.translate(25, layout.getAscent() + 25);
    g.setPaint(Color.black);
    Rectangle cb = null;
    g.setStroke(new BasicStroke(3));
    for(int i=0; i<n; i++){
      cb = layout.getLogicalHighlightShape(i, i+1).getBounds();
      g.drawLine(cb.x, cb.y, cb.x, cb.y + cb.height);
      g.drawLine(cb.x, cb.y, cb.x + cb.width, cb.y);
      g.drawLine(cb.x, cb.y + cb.height, cb.x + cb.width, cb.y + cb.height);
      // if(i%2 == 0)
      //	g.translate(0, -5);
      // else
      //	g.translate(0, 5);
    }

    g.drawLine(cb.x + cb.width, cb.y, cb.x + cb.width, cb.y + cb.height);*/

  }

  public void paint(Graphics g){
    g.drawImage(buf, 0, 0, null);
  }

  static final String USAGE = "java com.sun.glf.priv.snipets.StyledTextRendering <fontFaceName> <fontSize>";
  public static void main(String args[]){
    if(args.length<2){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new StyledTextRendering(args[0], Integer.parseInt(args[1])));
  }
}
