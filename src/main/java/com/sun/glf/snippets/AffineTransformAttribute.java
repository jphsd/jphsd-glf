/*
 * @(#)AffineTransformAttribute.java
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
import java.awt.geom.*;

import com.sun.glf.util.Toolbox;
import javax.swing.*;

/**
 * Illustrates the use of the graphic context AffineTransform attribute.
 *
 * @author         Vincent Hardy
 * @version        1.0, 01.03.1999
 */
public class AffineTransformAttribute extends JComponent{
  // Image drawn after AffineTransform set to translation
  private Image image;

  public AffineTransformAttribute(String imageName){
    image = Toolbox.loadImage(imageName);

    if(image == null)
      throw new Error("Could not load : " + imageName);

    // Size is based on image size. 
    // We also take the 100 by 100 translation into 
    // account
    int iw = image.getWidth(null);
    int ih = image.getHeight(null);
    Dimension size = new Dimension( iw + 100, ih + 100);
    setPreferredSize(size);
  }

  public void paint(Graphics _g){
    // Cast to Graphics2D to access Java 2D features
    Graphics2D g = (Graphics2D)_g;

    // Turn antialiasing on.
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		       RenderingHints.VALUE_ANTIALIAS_ON);

    AffineTransform t = AffineTransform.getTranslateInstance(100, 100);
    g.transform(t);
    g.drawImage(image, 0, 0, null);
  }

  static final String USAGE = "java com.sun.glf.snippets.AffineTransformAttribute <imageFile>";
  public static void main(String args[]){
    // Check input argument
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new AffineTransformAttribute(args[0]));
  }
}