/*
 * @(#)WaveTransformUsage.java
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
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import com.sun.glf.goodies.*;
import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Illustrates usage of the WaveTransform class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class WaveTransformUsage implements CompositionFactory{
  String text = "Making text waves is indeed very fun! I like it and I hope you do too! This is an example of how we can use non linear transformations to create fancy rendering effects";

  Font textFont = new Font("French Script MT", Font.PLAIN, 45);

  int textWrapWidth = 500;

  float waveLength = 90;

  float waveHeight = 9;

  int waveNumber = 3;

  Color textColor = new Color(7, 7, 49);

  Color backgroundColor = Color.white;

  boolean useWaveLength = true;

  int margins = 10;

  boolean useMunchTransform;

  public boolean getUseMunchTransform(){
    return useMunchTransform;
  }

  public void setUseMunchTransform(boolean useMunchTransform){
    this.useMunchTransform = useMunchTransform;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
  }

  public boolean getUseWaveLength(){
    return useWaveLength;
  }

  public void setUseWaveLength(boolean useWaveLength){
    this.useWaveLength = useWaveLength;
  }

  public String getText(){
    return text;
  }

  public void setText(String text){
    this.text = text;
  }

  public Font getTextFont(){
    return textFont;
  }

  public void setTextFont(Font textFont){
    this.textFont = textFont;
  }

  public int getTextWrapWidth(){
    return textWrapWidth;
  }

  public void setTextWrapWidth(int textWrapWidth){
    this.textWrapWidth = textWrapWidth;
  }

  public float getWaveLength(){
    return waveLength;
  }

  public void setWaveLength(float waveLength){
    this.waveLength = waveLength;
  }

  public float getWaveHeight(){
    return waveHeight;
  }

  public void setWaveHeight(float waveHeight){
    this.waveHeight = waveHeight;
  }

  public int getWaveNumber(){
    return waveNumber;
  }

  public void setWaveNumber(int waveNumber){
    this.waveNumber = waveNumber;
  }

  public Color getTextColor(){
    return textColor;
  }

  public void setTextColor(Color textColor){
    this.textColor = textColor;
  }

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Composition build(){
    Shape textBlock = TextLayer.makeTextBlock(text, textFont, textWrapWidth, TextAlignment.JUSTIFY);

    // Use a WaveTransform to modify textBlock's shape
    WaveTransform transform = null;
    if(useWaveLength)
      transform = new WaveTransform(waveLength, waveHeight);
    else
      transform = new WaveTransform(waveNumber, waveHeight);

    // If using a MunchTransform, concatenate one
    if(useMunchTransform){
      System.out.println("Using munch");
      Transform munchTransform = new MunchTransform(1f);
      transform.concatenate(munchTransform); // This will munch Shape before it is transformed with WaveTransform
    }
    else
      System.out.println("Not using munch");

    textBlock = transform.transform(textBlock);
    
    // Base composition size on textBlock size
    Rectangle bounds = textBlock.getBounds();
    Dimension size = new Dimension(bounds.width + 2*margins, bounds.height + 2*margins);
    
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);
    ShapeLayer textWaves = new ShapeLayer(cmp, textBlock, new FillRenderer(textColor), Position.CENTER);
    cmp.setLayers(new Layer[]{textWaves});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }

  public static void main(String args[]){
    CompositionStudio studio = new CompositionStudio();
    WaveTransformUsage factory = new WaveTransformUsage();
    studio.loadBeans(factory);

    final JFrame frame = new JFrame();
    frame.getContentPane().add(studio);
    frame.pack();
    frame.setVisible(true);

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });
  }

  static {
    Toolbox.initFonts();
  }
}
