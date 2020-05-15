/*
 * @(#)SimplePainter.java
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
import java.awt.print.*;
import java.awt.image.*;
import java.io.*;

import com.sun.glf.util.Toolbox;


/**
 * The Painter class renders into a Graphics2D, independant of the 
 * output device.
 * 
 * @author         Vincent Hardy
 * @version        1.0, 02/13/1999
 */
public class SimplePainter {
  /**
   * Image drawn in render
   */
  private BufferedImage image;

  /**
   * Margin size
   */
  private static int MARGIN = 10;

  /**
   * Size of the area drawn by this class (in user space
   */
  private Dimension size;

  /**
   * Background is first filled with GradientPaint
   */
  private Paint gradientPaint;

  /**
   * Constructor
   */
  public SimplePainter(String imageName){
    //
    // Load image
    //
    image = Toolbox.loadImage(imageName, BufferedImage.TYPE_INT_RGB);
    if(image==null){
      String message = "Could not load : " + imageName; 
      System.out.println(message);
      System.out.flush();
      throw new Error(message);
    }

    //
    // Size is based on image size plus margin
    //
    int w = image.getWidth() + 2*MARGIN;
    int h = image.getHeight() + 2*MARGIN;
    size = new Dimension(w, h);
    
    //
    // Create Gradient Paint for border
    //
    gradientPaint = new GradientPaint(0, 0, new Color(63, 64, 124),
				      0, h, new Color(112, 128, 154));

  }

  public Dimension getSize(){
    return size;
  }

  public void render(Graphics2D g){
    //
    // First, fill background with gradientPaint
    //
    g.setPaint(gradientPaint);
    g.fillRect(0, 0, size.width, size.height);

    //
    // Now, paint image
    //
    g.drawImage(image, MARGIN, MARGIN, null);
  }
}

