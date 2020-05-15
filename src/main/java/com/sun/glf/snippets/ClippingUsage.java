/*
 * @(#)ClippingUsage.java
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
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import com.sun.glf.util.Toolbox;

/**
 * Demonstrates the usage of the clipping feature in the AWT package.
 *
 * @author        Vincent Hardy
 * @version       1.0, 12/30/1998
 */
public class ClippingUsage extends JComponent{
  /**
   * Base image, used for demonstrating the different clipping 
   * strategies.
   */
  Image image;
  
  /**
   * Component's size, based on image size
   */
  Dimension size;

  /**
   * Width and height of the base image
   */
  int w, h;

  BufferedImage buf;

  public ClippingUsage(String imageName){
    image = Toolbox.loadImage(imageName);
    
    w = image.getWidth(null);
    h = image.getHeight(null);   
    size = new Dimension(6*w, h);
    setPreferredSize(size);

    buf = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = buf.createGraphics();
    g.setPaint(Color.white);
    g.fillRect(0, 0, buf.getWidth(), buf.getHeight());
    paintBuf(g);
  }

  public void paint(Graphics g){
    g.drawImage(buf, 0, 0, null);
  }

  public void paintBuf(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    
    // Save original clip
    Shape clipShape = g.getClip();
    AffineTransform transform = g.getTransform();

    // Set simple clip : does not modify the output
    g.clipRect(0, 0, size.width, size.height);
    g.drawImage(image, 0, 0, null);

    g.translate(w, 0);

    // Intersect current clip with a smaller clip : show only 
    // the top right corner of the image 
    g.clipRect(w/2, 0, w/2, h/2);
    g.drawImage(image, 0, 0, null);

    // Restore
    g.setTransform(transform);
    g.setClip(clipShape);
    g.translate(2*w, 0);

    // Scale before setting the same clip
    g.scale(.5, .5);
    g.clipRect(w/2, 0, w/2, h/2);
    g.drawImage(image, 0, 0, null);

    // Restore
    g.setTransform(transform);
    g.setClip(clipShape);

    g.translate(3*w, 0);

    // Use a non-rectangle clipping area
    Shape circle = new Ellipse2D.Float(0, 0, w, h);
    g.clip(circle);
    g.drawImage(image, 0, 0, null);

    // Restore
    g.setTransform(transform);
    g.setClip(clipShape);

    g.translate(4*w, 0);

    // Use a non-rectangle clipping area again, 
    // after setting a scale transform
    g.scale(.5, .5);
    g.clip(circle);
    g.drawImage(image, 0, 0, null);

    // Restore
    g.setTransform(transform);
    g.setClip(clipShape);
    g.translate(5*w, 0);

    // Use a non-rectangle clipping area again, 
    // before setting a scale transform
    g.clip(circle);
    g.scale(.5, .5);
    g.drawImage(image, 0, 0, null);

    // Restore
    g.setTransform(transform);
    g.setClip(clipShape);
    g.translate(6*w, 0);

    //
    // Do a masking operation for better results
    //
    /*BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D bg = buffer.createGraphics();
    bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    bg.setPaint(Color.white);
    bg.fill(circle);
    bg.setComposite(AlphaComposite.SrcIn);
    bg.drawImage(image, 0, 0, null);
    bg.dispose();

    g.drawImage(buffer, 0, 0, null);*/
  }

  static final String USAGE = "java com.sun.glf.snippets.ClippingUsage <imageFileName>";
  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new ClippingUsage(args[0]));
  }
}
