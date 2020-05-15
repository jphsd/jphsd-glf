/*
 * @(#)CMYKSave.java
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

import java.awt.image.*;
import java.awt.color.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;

import com.sun.glf.util.Toolbox;

/**
 * This program reads an RGB JPEG image and converts it to a CMYK image. Optionally,
 * it can can invert the CMYK components before saving the image as some popular 
 * image tools use inverted value for the CMYK components.
 *
 * @author         Vincent Hardy
 * @version        1.0, 02/01/1999
 */
public class CMYKSave{
  static final String USAGE = "java com.sun.glf.snippets.CMYKSave <cmykProfile> <rgbJPEGFile> <cmykJPEGFile> <invert=yes|no>";

  public static void main(String args[]) throws Exception{
    if(args.length<4){
      System.out.println(USAGE);
      System.exit(0);
    }

    String cmykProfile = args[0];
    String rgbJPEGFile = args[1];
    String cmykJPEGFile = args[2];
    boolean invert = "true".equalsIgnoreCase(args[3]);

    //
    // First, load input image
    //
    BufferedImage rgbImage = ImageIO.read(new FileInputStream(rgbJPEGFile));

    // Create a CMYK Color Space from cmykProfile
    ICC_Profile p = ICC_Profile.getInstance(new FileInputStream(cmykProfile));
    ColorSpace cmykCS = new ICC_ColorSpace(p);

    OutputStream out = new FileOutputStream(cmykJPEGFile);
    
    // ColorConvert rgbImage to CMYK
    ColorSpace rgbCS = rgbImage.getColorModel().getColorSpace();
    ColorConvertOp rgbToCmyk = new ColorConvertOp(rgbCS, cmykCS, null);
    
    // Create a ColorModel for destination CMYK image
    ColorModel cmykModel = new ComponentColorModel(cmykCS,
						   new int[] { 8, 8, 8, 8 }, // 8 bits for each C, M, Y, K
						   false, true, // No alpha
						   Transparency.OPAQUE,
						   DataBuffer.TYPE_BYTE);
    WritableRaster cmykRaster = cmykModel.createCompatibleWritableRaster(rgbImage.getWidth(), 
									 rgbImage.getHeight());

    // Convert to CMYK now
    rgbToCmyk.filter(rgbImage.getRaster(), cmykRaster);

    // Invert components before saving if requested
    if(invert){
      System.out.println("Inverting CMYK bands");
      byte swap[] = new byte[256];
      for(int i=0; i<swap.length; i++){
	swap[i] = (byte)(255-i);
      }
      LookupTable lookup = new ByteLookupTable(0, new byte[][] { swap, swap, swap, swap });
      LookupOp luop = new LookupOp(lookup, null);
      cmykRaster = luop.filter(cmykRaster, cmykRaster.createCompatibleWritableRaster());
    }

    // Finally, save CMYK raster as an image
    BufferedImage cmykImg = new BufferedImage(cmykModel, cmykRaster, false, null);
    ImageIO.write(cmykImg, "jpg", out);
    out.close();
    System.out.println("Image saved successfully in " + cmykJPEGFile);
  }
}
