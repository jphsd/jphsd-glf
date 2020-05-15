/*
 * @(#)ImageFilters.java
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
import java.lang.reflect.*;

import com.sun.glf.util.Toolbox;
import javax.swing.*;

/**
 * Illustrates the different BufferedImageOp and RasterOp filters.
 * This application loads an Image and shows a drop down list which
 * the user can select to apply a specific filter.
 *
 * @author           Vincent Hardy
 * @version          1.0, 01/10/1999
 */
public class ImageFilters extends JComponent {
  BufferedImage filteredImage;
  BufferedImage image;

  public ImageFilters(String imageName) throws Exception{
    image = Toolbox.loadImage(imageName, BufferedImage.TYPE_INT_RGB);
    filteredImage = image;

    setPreferredSize(new Dimension(2*image.getWidth(), image.getHeight()));
  }

  public BufferedImage BandCombineFilter(BufferedImage source){
    System.out.println("BandCombineFilter");
    float matrix[][] = { { 0, 0, 1},
			 { 0, 1, 0},
			 { 1, 0, 0} };
    
    BandCombineOp combine = new BandCombineOp(matrix, null);
    Raster imageRaster = source.getRaster();
    WritableRaster dstRaster = imageRaster.createCompatibleWritableRaster();
    
    combine.filter(imageRaster, dstRaster);
    return new BufferedImage(image.getColorModel(),
			     dstRaster,
			     true,
			     null);
  }
    
  public BufferedImage AffineTransformFilter(BufferedImage source){
    System.out.println("AffineTransformFilter");
    /*AffineTransform rotation = new AffineTransform();
    rotation.rotate(Math.PI, source.getWidth()/2, source.getHeight()/2);
    BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    AffineTransformOp rotator = new AffineTransformOp(rotation, AffineTransformOp.TYPE_BILINEAR);
    return rotator.filter(source, destination);*/

    AffineTransform shear = new AffineTransform();
    shear.shear(.2, 0);
    AffineTransformOp skewer = new AffineTransformOp(shear, AffineTransformOp.TYPE_BILINEAR);
    return skewer.filter(source, null);    
  }
    
  public BufferedImage ColorConvertFilter(BufferedImage source){
    System.out.println("ColorConvertFilter");
    BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    ColorConvertOp convert = new ColorConvertOp(source.getColorModel().getColorSpace(), destination.getColorModel().getColorSpace(), null);
    return convert.filter(source, destination);
  }

  public BufferedImage ConvolveFilter(BufferedImage source){
    float k[] = { 1/9f, 1/9f, 1/9f,
		  1/9f, 1/9f, 1/9f,
		  1/9f, 1/9f, 1/9f };

    Kernel kernel = new Kernel(3, 3, k);
    ConvolveOp blur = new ConvolveOp(kernel);
    return blur.filter(source, null);
  }

  public BufferedImage EdgeDetectionFilter(BufferedImage source){
    float k[] = { -1, -1, -1,
                  -1,  8, -1,
                  -1, -1, -1 };

    Kernel kernel = new Kernel(3, 3, k);
    ConvolveOp edgeDetector = new ConvolveOp(kernel);
    return edgeDetector.filter(source, null);
  }

  public BufferedImage SharpenFilter(BufferedImage source){
    float k[] = { -1, -1, -1,
                  -1,  9, -1,
                  -1, -1, -1 };

    Kernel kernel = new Kernel(3, 3, k);
    ConvolveOp sharpen = new ConvolveOp(kernel);
    return sharpen.filter(source, null);
  }

  public BufferedImage EmbossFilter(BufferedImage source){
    float k[] = { 2, 0,  0,
		  0,  1,  0,
		  0,  0,  -2};

    Kernel kernel = new Kernel(3, 3, k);
    ConvolveOp emboss = new ConvolveOp(kernel);
    return emboss.filter(source, null);
  }

  public BufferedImage RescaleFilter(BufferedImage image){
    System.out.println("RescaleFilter");
    RescaleOp brighten = new RescaleOp(1.5f, 0, null);
    return brighten.filter(image, null);
  }

  public BufferedImage LookupFilter(BufferedImage source){
    System.out.println("LookupFilter");
    byte lookup[] = new byte[256];
    for(int i=0; i<256; i++)
      lookup[i] = (byte)(255 - i);
      
    LookupTable table = new ByteLookupTable(0, lookup);
    LookupOp inverter = new LookupOp(table, null);
      
    return inverter.filter(source, null);
  }
    
  public void paint(Graphics g){
    g.drawImage(image, 0, 0, null);
    g.translate(image.getWidth(), 0);
    g.drawImage(filteredImage, 0, 0, null);
  }
    
  static final String USAGE = "java com.sun.glf.snippets.ImageFilters <imageFileName>";

  public static void main(String args[]) throws Exception{
    // Check input
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    String methods[] = { "EdgeDetectionFilter", 
			 "SharpenFilter", 
			 "EmbossFilter", 
			 "LookupFilter", 
			 "RescaleFilter", 
			 "AffineTransformFilter", 
			 "ConvolveFilter", 
			 "ColorConvertFilter", 
			 "BandCombineFilter"};

    final JComboBox combo = new JComboBox(methods);
    final ImageFilters cmp = new ImageFilters(args[0]);
    JFrame frame = new JFrame();
    frame.getContentPane().setBackground(Color.white);
    frame.getContentPane().add(BorderLayout.NORTH, combo);
    frame.getContentPane().add(cmp);
    frame.pack();

    combo.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	String selection = (String)combo.getSelectedItem();
	if(selection != null){
	  try{
	    Method method = cmp.getClass().getMethod(selection, new Class[] { BufferedImage.class });
	    if(method == null)
	      System.out.println("Unknown method : " + selection);
	    else{
	      cmp.filteredImage = (BufferedImage)method.invoke(cmp, new Object[]{cmp.image});
	      cmp.repaint();
	    }
	  }catch(Exception e){
	    System.out.println("Invoking " + selection + " failed: " + e.getMessage());
	    e.printStackTrace();
	  }
	}
      }
    });

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    frame.setVisible(true);
  }
}
