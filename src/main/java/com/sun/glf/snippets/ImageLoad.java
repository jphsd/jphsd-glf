/*
 * @(#)ImageLoad.java
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
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.imageio.ImageIO;

/**
 * Illustrates two methods for loading a JPEG image: 
 * + MediaTracker
 * + com.sun.image.codec.jpeg.JPEGDecoder
 *
 * @author             Vincent Hardy
 * @version            1.0, 02.12.1999
 */
public class ImageLoad extends JComponent {
  // Image loaded with MediaTracker
  private Image defaultLoadImage;

  // Image loaded with JPEGDecoder
  private BufferedImage jpegLoadImage;

  public ImageLoad(String imageName){

    //
    // Traditional method to load images: MediaTracker
    //
    MediaTracker tracker = new MediaTracker(this);
    defaultLoadImage = Toolkit.getDefaultToolkit().getImage(imageName);
    tracker.addImage(defaultLoadImage, 0);
    try{
      tracker.waitForAll();
    }catch(InterruptedException e){
      throw new Error("Could not load " + imageName);
    }

    // 
    // Alternate method for JPEG Images: JPEGImageDecoder
    //
    try{
      InputStream in = new FileInputStream(imageName);
      jpegLoadImage = ImageIO.read(in);
    }catch(IOException e){
      e.printStackTrace();
      throw new Error("Could not load " + imageName + " with ImageIO");
    }

    setPreferredSize(new Dimension(jpegLoadImage.getWidth()*2,
				   jpegLoadImage.getHeight()));
  }

  public void paint(Graphics _g){
    _g.drawImage(jpegLoadImage, 0, 0, null);
    _g.drawImage(defaultLoadImage, jpegLoadImage.getWidth(), 0, null);
  }

  static final String USAGE = "java com.sun.glf.snippets.ImageLoad <imageName>";
  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new ImageLoad(args[0]));
  }

}

