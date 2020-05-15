/*
 * @(#)OffscreenBufferRendering.java
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
import java.io.*;

import javax.imageio.ImageIO;

/**
 * Demonstrates how to access a Graphics2D that renders to a printer 
 * output device.
 *
 * @author        Vincent Hardy
 * @version       1.0, 12.26.1998
 */
public class OffscreenBufferRendering{
  static final String USAGE = "java com.sun.glf.snippets.OffscreenBuffereRendering <inputImageFileName> <outputImageFileName>";
  public static void main(String args[]) throws Exception{
    if(args.length<2){
      System.out.println(USAGE);
      System.out.flush();
      System.exit(0);
    }

    //
    // First, create a SimplePainter
    //
    SimplePainter painter = new SimplePainter(args[0]);
    
    //
    // Now, create an offscreen buffer to draw into.
    // Here, we create a binary image
    //
    BufferedImage image = new BufferedImage(painter.getSize().width,
					    painter.getSize().height, 
					    BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    //
    // Delegate rendering to painter
    //
    painter.render(g);

    //
    // Save result as a JPEG image into outputImageFileName
    //
    File outputFile = new File(args[1]);
    FileOutputStream out = new FileOutputStream(outputFile);
    ImageIO.write(image, "jpg", out);
    out.close();

    System.out.println("Done writing " + outputFile.getAbsolutePath());
  }

}