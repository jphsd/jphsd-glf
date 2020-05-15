/*
 * @(#)RadialGradientPaintExtUsage.java
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
import javax.swing.*;

import com.sun.glf.goodies.*;
/**
 * Illustrates usage of the RadialGradientPaintExt class.
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/21/1999
 */
public class RadialGradientPaintExtUsage extends JComponent{
  private static final int margin = 20;

  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;
    int w = getSize().width;
    int h = getSize().height;

    
    Rectangle gradientBounds = new Rectangle(margin, margin, w - 2*margin, h - 2*margin);
    float I[] = {3, 2, 1};
    Color darkGreen = new Color(30, 120, 80);
    Color darkBlue = new Color(10, 20, 40);
    Color colors[] = {Color.white, Color.yellow, darkGreen, darkBlue};

    Paint paint = new RadialGradientPaintExt(gradientBounds, colors, I);
    g.setPaint(paint);
    g.fillRect(0, 0, w, h);
  }

  public static void main(String args[]){
    RadialGradientPaintExtUsage cmp = new RadialGradientPaintExtUsage();
    cmp.setPreferredSize(new Dimension(500, 500));
    SnippetFrame frame = new SnippetFrame(cmp);
  }
}
