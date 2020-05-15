/*
 * @(#)LookupParts.java
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

package com.sun.glf.demos.gallery;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 *
 * @author             Vincent Hardy
 * @version            1.0, 12/03/1998
 */
public class LookupParts implements CompositionFactory{
  File imageFile = new File("AnneSmall.jpg");

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public Composition build(){
    //
    // Load image first : the composition size will be based 
    // on the image size. 
    //
    BufferedImage imageBase = Toolbox.loadImage(imageFile.getAbsolutePath(), BufferedImage.TYPE_INT_RGB);

    //
    // Create a Composition whose size depends on that of the image. 
    // This compositions displays the same 4 times, on two rows.
    //
    Dimension size = new Dimension(imageBase.getWidth()*6, imageBase.getHeight()*6);
    LayerComposition cmp = new LayerComposition(size);
    int w = size.width;
    int h = size.height;

    //
    // Create a LookupOp which will threshold color components so as to limit
    // color usage to 8, with either 0 or 255 for each or the 3 color components.
    //
    byte threshold[] = new byte[256];
    for(int i=128; i<256; i++)
      threshold[i] = (byte)255;
    ByteLookupTable thresholdLookup = new ByteLookupTable(0, threshold);
    LookupOp thresholder = new LookupOp(thresholdLookup, null);

    //
    // Top Left Layer : threshold color components
    //
    BufferedImage thresholdedImage = thresholder.filter(imageBase, null);
    // ImageLayer topLeft = new ImageLayer(cmp, thresholdedImage, Position.TOP_LEFT);
    Layer topLeft = makeFadeOutBlock(cmp, Position.TOP_LEFT, thresholdedImage);

    //
    // Top Right Layer : invert the red and blue components
    //
    
    // First, get image Raster, because the BandCombineOp is not
    // a BufferedImageOp
    Raster imageRaster = imageBase.getRaster();
    
    // Create a compatible destination raster, which needs to be writable
    // to hold the result of the filtering
    WritableRaster rbInvertedRaster = imageRaster.createCompatibleWritableRaster();

    // Now, create a BandCombineOp that inverts the red and green components
    float redBlueInvert[][] = { {0, 0, 1}, // Red = Blue
				{0, 1, 0}, // Green = Green
				{1, 0, 0}  // Blue = Red
    };
    BandCombineOp redBlueCombine = new BandCombineOp(redBlueInvert, null);
    
    // Filter input
    redBlueCombine.filter(imageRaster, rbInvertedRaster);

    // Create a BufferedImage from the result.
    BufferedImage combinedImage = new BufferedImage(imageBase.getColorModel(),
						    rbInvertedRaster,
						    true,
						    null);

    // 
    // Create an image layer from the filtered Raster
    //
    BufferedImage thresholdedCombinedImage = thresholder.filter(combinedImage, null);
    // ImageLayer topRight = new ImageLayer(cmp, thresholdedCombinedImage, Position.TOP_RIGHT);
    Layer topRight = makeFadeOutBlock(cmp, Position.TOP_RIGHT, thresholdedCombinedImage);

    //
    // Bottom Right Layer : invert the red and green components
    //
    
    // Create a compatible destination raster, which needs to be writable
    // to hold the result of the filtering
    WritableRaster rgInvertedRaster = imageRaster.createCompatibleWritableRaster();

    // Now, create a BandCombineOp that inverts the red and green components
    float redGreenInvert[][] = { {0, 1, 0}, // Red = Green
				{1, 0, 0}, // Green = Red
				{0, 0, 1}  // Blue = Blue
    };
    BandCombineOp redGreenCombine = new BandCombineOp(redGreenInvert, null);
    
    // Filter input
    redGreenCombine.filter(imageRaster, rgInvertedRaster);

    // Create a BufferedImage from the result.
    combinedImage = new BufferedImage(imageBase.getColorModel(),
				      rgInvertedRaster,
				      true,
				      null);

    // 
    // Create an image layer from the filtered Raster
    //
    BufferedImage thresholdedCombinedImage2 = thresholder.filter(combinedImage, null);
    // ImageLayer bottomRight = new ImageLayer(cmp, thresholdedCombinedImage2, Position.BOTTOM_RIGHT);
    Layer bottomRight = makeFadeOutBlock(cmp, Position.BOTTOM_RIGHT, thresholdedCombinedImage2);
						        
    //
    // Bottom Left Layer : swap the red, blue and green components
    //
    
    // Create a compatible destination raster, which needs to be writable
    // to hold the result of the filtering
    WritableRaster rgbInvertedRaster = imageRaster.createCompatibleWritableRaster();

    // Now, create a BandCombineOp that inverts the blue and green components
    float redBlueGreenInvert[][] = { {0, 1, 0}, // Red = Green
				     {0, 0, 1}, // Green = Blue
				     {1, 0, 0}  // Blue = Red
    };
    BandCombineOp redBlueGreenCombine = new BandCombineOp(redBlueGreenInvert, null);
    
    // Filter input
    redBlueGreenCombine.filter(imageRaster, rgbInvertedRaster);

    // Create a BufferedImage from the result.
    combinedImage = new BufferedImage(imageBase.getColorModel(),
				      rgbInvertedRaster,
				      true,
				      null);

    // 
    // Create an image layer from the filtered Raster
    //
    BufferedImage thresholdedCombinedImage3 = thresholder.filter(combinedImage, null);
    // ImageLayer bottomLeft = new ImageLayer(cmp, thresholdedCombinedImage3, Position.BOTTOM_LEFT);
    Layer bottomLeft = makeFadeOutBlock(cmp, Position.BOTTOM_LEFT, thresholdedCombinedImage3);


    //
    // Set composition layers
    //
    cmp.setLayers(new Layer[] { /*bottomRight}); {*/ topLeft , topRight , bottomRight , bottomLeft });
    cmp.setBackgroundPaint(Color.white);
    return cmp;
  }

  private Layer makeFadeOutBlock(LayerComposition cmp, Position layerPos, BufferedImage img){
    Dimension size = new Dimension(img.getWidth()*3, img.getHeight()*3);
    
    LayerComposition blockCmp = new LayerComposition(size);
    
    float iWeights[] = {1, 0.75f, 0.5f};
    float jWeights[] = {1, 0.75f, 0.5f};

    Position pos[] = { Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT,
		       Position.LEFT, Position.CENTER, Position.RIGHT,
		       Position.BOTTOM_LEFT, Position.BOTTOM, Position.BOTTOM_RIGHT };

    Layer layers[] = new Layer[9];

    for(int i=0; i<3; i++){
      for(int j=0; j<3; j++){
	layers[i*3 + j] = new ImageLayer(blockCmp, img, pos[i*3 + j]);
	float w = iWeights[i]*jWeights[j];
	layers[i*3 + j].setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, w));
      }
    }

    blockCmp.setLayers(layers);
    return new CompositionProxyLayer(cmp, blockCmp, layerPos);
  }
}
