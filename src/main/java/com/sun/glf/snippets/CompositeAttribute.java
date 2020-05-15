/*
 * @(#)CompositeAttribute.java
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
import java.awt.event.*;

import com.sun.glf.util.Toolbox;
import javax.swing.*;

/**
 * Demonstrates the use of the graphics context Composite attribute
 *
 * @author         Vincent Hardy
 * @version        1.0, 01.03.1999
 */
public class CompositeAttribute extends JComponent{
  private Image image;

  public CompositeAttribute(String imageName){
    image = Toolbox.loadImage(imageName);
    if(image==null)
      throw new Error("Could not load : " + imageName);
    
    // Calculate preferred size
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);

    int width = imageWidth + 20;
    int height = imageHeight + 20;

    setPreferredSize(new Dimension(width, height));
  }
  
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    // Center Image
    int imageWidth = image.getWidth(null);
    int imageHeight = image.getHeight(null);

    int x = (getSize().width - imageWidth)/2;
    int y = (getSize().height - imageHeight)/2;

    g.drawImage(image, x, y, null);

    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .35f));
    g.setPaint(Color.blue);
    g.fillRect(x-10, y-10, imageWidth/2 + 10, imageHeight + 20);
  }

  static final String USAGE = "java com.sun.glf.snippets.CompositeAttribute <imageName>";
  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new CompositeAttribute(args[0]));
  }
}
