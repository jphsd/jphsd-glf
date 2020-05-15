/*
 * @(#)TextLayerControls.java
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
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.awt.font.*;
import java.text.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * Demonstrates the purpose and effect of the different TextLayer
 * controls:<br>
 * + Text
 * + Font
 * + Wrapping width
 * + Positioning
 * + TextAlignment
 *
 * @author             Vincent Hardy
 * @version            1.1, 03/29/1999
 */
public class TextLayerControls implements CompositionFactory{
  Font textFont = new Font("serif", Font.PLAIN, 30);

  Color textColor = Color.black;

  Anchor textAnchor = Anchor.CENTER;

  float vAdjust = 0;

  float hAdjust = 0;

  TextAlignment textAlignment = TextAlignment.CENTER;

  File textFile = new File("");

  float wrapWidth = 300;

  double rotationAngle = 0;

  Color backgroundColor = Color.white;

  Dimension size = new Dimension(600, 400);

  boolean useAttributedStrings;

  public boolean getUseAttributedStrings(){
    return useAttributedStrings;
  }

  public void setUseAttributedStrings(boolean useAttributedStrings){
    this.useAttributedStrings = useAttributedStrings;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Anchor getTextAnchor(){
    return textAnchor;
  }

  public void setTextAnchor(Anchor textAnchor){
    this.textAnchor = textAnchor;
  }

  public float getVAdjust(){
    return vAdjust;
  }

  public void setVAdjust(float vAdjust){
    this.vAdjust = vAdjust;
  }

  public float getHAdjust(){
    return hAdjust;
  }

  public void setHAdjust(float hAdjust){
    this.hAdjust = hAdjust;
  }

  public TextAlignment getTextAlignment(){
    return textAlignment;
  }

  public void setTextAlignment(TextAlignment textAlignment){
    this.textAlignment = textAlignment;
  }

  public File getTextFile(){
    return textFile;
  }

  public void setTextFile(File textFile){
    this.textFile = textFile;
  }

  public float getWrapWidth(){
    return wrapWidth;
  }

  public void setWrapWidth(float wrapWidth){
    this.wrapWidth = wrapWidth;
  }

  public double getRotationAngle(){
    return rotationAngle;
  }

  public void setRotationAngle(double rotationAngle){
    this.rotationAngle = rotationAngle;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Dimension getSize(){
    return size;
  }

  public void setSize(Dimension size){
    this.size = size;
  }

  public Composition build(){
    // Create LayerComposition with desired size
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);

    Renderer textPainter = new FillRenderer(textColor);

    // Build text Position
    AffineTransform transform = AffineTransform.getRotateInstance(rotationAngle);
    Position textPosition = new Position(textAnchor, hAdjust, vAdjust, transform, false);

    if(!useAttributedStrings){
      String text = readFileAsString(textFile);

      // Build layer
      TextLayer textLayer = new TextLayer(cmp, 
					  text,
					  textFont,
					  textPainter,
					  textPosition,
					  wrapWidth,
					  textAlignment);
      
      cmp.setLayers(new Layer[]{ textLayer });
      textLayer.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				 RenderingHints.VALUE_ANTIALIAS_ON);
      
      cmp.setLayers(new Layer[]{ textLayer });
      
      return cmp;
    }
    else{
      AttributedString paragraphs[] = readFileAsAttributedString(textFile);
      
      // Builds layer
      TextLayer textLayer = new TextLayer(cmp, 
					  paragraphs,
					  textPainter,
					  textPosition,
					  wrapWidth,
					  textAlignment);
      
      cmp.setLayers(new Layer[]{ textLayer });
      textLayer.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				 RenderingHints.VALUE_ANTIALIAS_ON);
      
      cmp.setLayers(new Layer[]{ textLayer });
      
      return cmp;
    }

  }

  private String readFileAsString(File file){
    StringBuffer buf = new StringBuffer();
    try{
      BufferedReader in = new BufferedReader(new FileReader(file));
      String line = "";
      while((line = in.readLine())!=null)
	buf.append(line + "\n");
      
      in.close();
    }catch(IOException e){
      throw new IllegalArgumentException("Could not read : " + file + " : " + e.getMessage());
    }

    String text = buf.toString();
    return text;
  }
    
    
  protected AttributedString[] readFileAsAttributedString(File textFile){
    Font fonts[] = { textFont.deriveFont(Font.ITALIC), textFont.deriveFont(Font.PLAIN) };
    int iCurFont = 0;
    Vector strings = new Vector();
    try{
      BufferedReader in = new BufferedReader(new FileReader(textFile));
      String line = "";
      while((line = in.readLine())!=null){
	AttributedString str = new AttributedString(line);
	str.addAttribute(TextAttribute.FONT, fonts[iCurFont]);
	iCurFont++;
	iCurFont %= 2;
	strings.addElement(str);
      }
      in.close();
    }catch(IOException e){
      throw new IllegalArgumentException("Could not read : " + textFile + " : " + e.getMessage());
    }

    AttributedString result[] = new AttributedString[strings.size()];
    strings.copyInto(result);
    return result;
  }

  
}
