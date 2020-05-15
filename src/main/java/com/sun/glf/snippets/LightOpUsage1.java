/*
 * @(#)LightOpUsage1.java
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
import java.awt.image.*;

import javax.swing.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * Illustrates usage of the LightOp class with different ambient light
 * settings for the associated LitSurface.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class LightOpUsage1 extends JComponent{
  BufferedImage filteredImage;

  public LightOpUsage1(String imageFileName, float ambientLight){
    BufferedImage image = Toolbox.loadImage(imageFileName, BufferedImage.TYPE_INT_RGB);
    if(image==null)
      throw new IllegalArgumentException("Could not load imageFileName");

    LitSurface litSurface = new LitSurface(ambientLight);
    LightOp lightOp = new LightOp(litSurface);
    BufferedImage filteredImage = lightOp.filter(image, null);    

    this.filteredImage = filteredImage;
    setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
  }

  public void paint(Graphics _g){
    _g.drawImage(filteredImage, 0, 0, null);
  }

  static final String USAGE = "java com.sun.glf.snippets.LightOpUsage1 <imageFileName> <ambientLight>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    float ambientLight = 0.5f;
    if(args.length>=2)
      ambientLight = Float.parseFloat(args[1]);

    LightOpUsage1 cmp = new LightOpUsage1(args[0], ambientLight);
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
