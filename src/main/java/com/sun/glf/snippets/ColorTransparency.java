/*
 * @(#)ColorTransparency.java
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

import com.sun.glf.util.Toolbox;
import javax.swing.*;

/**
 * Demonstrates usage of the alpha value in the Color class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 02/14/1999
 */
public class ColorTransparency extends JComponent {
  private BufferedImage image;
  private int margin;

  public ColorTransparency(String imageFileName){
    // Load background image
    image = Toolbox.loadImage(imageFileName, BufferedImage.TYPE_INT_RGB);
    if(image==null){
      throw new Error("Could not load : " + imageFileName);
    }

    // Set preferred size based on image
    setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

    // Base margin size on image size
    margin = (int)(image.getWidth()*.05); // 5% of image width
    margin = (int)Math.min(margin,
			   image.getHeight()*.05); // 5% of image height
  }

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    // Paint background 
    g.drawImage(image, 0, 0, null);

    // Now, paint rectangular rectangles with white paint
    // of varying transparency
    Rectangle rect = new Rectangle(margin, margin,
				   (image.getWidth() - 5*margin)/4,
				   image.getHeight() - 2*margin);

    g.setPaint(new Color(1f, 1f, 1f, 1f)); // Fully opaque
    g.fill(rect);

    g.translate(margin + rect.width, 0);
    g.setPaint(new Color(1f, 1f, 1f, .75f));
    g.fill(rect);

    g.translate(margin + rect.width, 0);
    g.setPaint(new Color(1f, 1f, 1f, .5f));
    g.fill(rect);

    g.translate(margin + rect.width, 0);
    g.setPaint(new Color(1f, 1f, 1f, .25f));
    g.fill(rect);
  }
  
  static final String USAGE = "java com.sun.glf.snippets.ColorTransparency <backgroundImage>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new ColorTransparency(args[0]));
  }
}
