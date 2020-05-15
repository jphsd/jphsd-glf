/*
 * @(#)UsingLayers.java
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
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This example illustrates the process of using a LayerComposition 
 * and related LayerStack: <br>
 * + Create a LayerComposition of a given size.
 * + Create the differnt Layers that will make the stack.
 * + Set the Layer mementos
 * + Set the LayerComposition RenderingHints
 * + Set the LayerComposition stack.
 *
 * @author         Vincent Hardy
 * @version        1.0, 03/04/1999
 */
public class UsingLayers implements CompositionFactory{
  File maskImageFile = new File("");
  File imageFile = new File("");
  String text = "Frame your pictures";
  Color textColor = Color.black;
  Font textFont = new Font("serif", Font.PLAIN, 40);
  int textMargins = 40;
  int textOutlineWidth = 2;
  Color textOutlineColor = Color.black;

  public File getMaskImageFile(){
    return maskImageFile;
  }

  public void setMaskImageFile(File maskImageFile){
    this.maskImageFile = maskImageFile;
  }

  public File getImageFile(){
    return imageFile;
  }

  public void setImageFile(File imageFile){
    this.imageFile = imageFile;
  }

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public int getTextMargins(){
    return textMargins;
  }

  public void setTextMargins(int textMargins){
    this.textMargins = textMargins;
  }

  public Color getTextOutlineColor(){
    return textOutlineColor;
  }

  public void setTextOutlineColor(Color textOutlineColor){
    this.textOutlineColor = textOutlineColor;
  }

  public int getTextOutlineWidth(){
    return textOutlineWidth;
  }

  public void setTextOutlineWidth(int textOutlineWidth){
    this.textOutlineWidth = textOutlineWidth;
  }

  public Composition build(){
    //
    // Create a Composition of specific size.
    // The size is based on the mask Image size.
    //
    BufferedImage mask = Toolbox.loadImage(maskImageFile, BufferedImage.TYPE_BYTE_GRAY);
    if(mask==null)
      throw new IllegalArgumentException("Cannot load : " + maskImageFile.getAbsolutePath());
    Dimension size = new Dimension(mask.getWidth(), mask.getHeight());
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(Color.white);

    //
    // Create an ImageLayer and set its mask memento
    //
    BufferedImage image = Toolbox.loadImage(imageFile, BufferedImage.TYPE_INT_ARGB);
    if(image==null)
      throw new IllegalArgumentException("Cannot load : " + imageFile.getAbsolutePath());
    ImageLayer imageLayer = new ImageLayer(cmp, image);
    imageLayer.setLayerMask(mask);

    //
    // Create a TextLayer and set its Composite memento
    //
    Renderer textFill = new FillRenderer(textColor);
    TextLayer textLayer = new TextLayer(cmp, text, textFont, textFill, 
					new Position(Anchor.BOTTOM, textMargins, textMargins), 
					size.width,
					TextAlignment.CENTER);
    textLayer.setComposite(ColorComposite.Hs); // Hue and saturation of source. Brightness of destination

    //
    // Create another layer for the textOutline
    //
    Renderer textOutliner = new StrokeRenderer(textOutlineColor, textOutlineWidth);
    Shape textBlock = textLayer.createTransformedShape();
    ShapeLayer textOutlineLayer = new ShapeLayer(cmp, textBlock, textOutliner);
    
    //
    // Stack up layers
    //
    cmp.setLayers(new Layer[]{ imageLayer, textLayer, textOutlineLayer});

    //
    // Set LayerComposition RenderingHints. Consequently, antialiasing will
    // be used on all layers.
    //
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			 RenderingHints.VALUE_ANTIALIAS_ON);

    return cmp;
  }
}
