/*
 * @(#)BumpTransformUsage.java
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
 * Illustrates usage of the BumpTransform class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/22/1999
 */
public class BumpTransformUsage implements CompositionFactory{
  String text = "Making text bumps is also very fun! How do you like it?";

  Font textFont = new Font("Curlz MT", Font.PLAIN, 50);

  Color textColor = new Color(7, 7, 49);

  Color backgroundColor = Color.white;

  boolean useWaveLength = true;

  int margins = 10;

  double bumpAngle = 8*Math.PI/5;

  boolean useMunch = true;

  float munchSize = 1;

  public boolean getUseMunch(){
    return useMunch;
  }

  public void setUseMunch(boolean useMunch){
    this.useMunch = useMunch;
  }

  public float getMunchSize(){
    return munchSize;
  }

  public void setMunchSize(float munchSize){
    this.munchSize = munchSize;
  }

  public double getBumpAngle(){
    return bumpAngle;
  }

  public void setBumpAngle(double bumpAngle){
    this.bumpAngle = bumpAngle;
  }

  public int getMargins(){
    return margins;
  }

  public void setMargins(int margins){
    this.margins = margins;
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
    Shape textBlock = TextLayer.makeTextBlock(text, textFont, -1, TextAlignment.CENTER);

    // Use a BumpTransform to modify textBlock's shape
    BumpTransform transform = new BumpTransform((float)bumpAngle);

    if(useMunch){
      System.out.println("Using munch");
      MunchTransform muncher = new MunchTransform(munchSize);
      transform.concatenate(muncher);
    }
    else
      System.out.println("Not using munch");
 
    textBlock = transform.transform(textBlock);
    
    // Base composition size on textBlock size
    Rectangle bounds = textBlock.getBounds();
    Dimension size = new Dimension(bounds.width + 4*margins, bounds.height + 4*margins);
    
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);
    ShapeLayer bumpedText = new ShapeLayer(cmp, textBlock, new FillRenderer(textColor), Position.CENTER);
    cmp.setLayers(new Layer[]{bumpedText});
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    return cmp;
  }

  public static void main(String args[]){
    CompositionStudio studio = new CompositionStudio();
    BumpTransformUsage factory = new BumpTransformUsage();
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
