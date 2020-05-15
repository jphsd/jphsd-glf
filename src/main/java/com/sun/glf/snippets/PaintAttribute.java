/*
 * @(#)PaintAttribute.java
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

/**
 * Illustrates setting of the Paint graphic context attribute
 *
 * @author         Vincent Hardy
 * @version        1.0, 12/27/1998
 */
public class PaintAttribute extends JComponent{
  private GeneralPath upTriangle = new GeneralPath();
  private GeneralPath downTriangle = new GeneralPath();
  private int scale = 30;

  public PaintAttribute(){
   // Create two  triangular Shapes : one pointing up containing
   // a smaller one pointing down.
   upTriangle.moveTo(10, 0);
   upTriangle.lineTo(20, 20);
   upTriangle.lineTo(0, 20);
   upTriangle.closePath();

   downTriangle.moveTo(5, 10);
   downTriangle.lineTo(15, 10);
   downTriangle.lineTo(10, 20);
   downTriangle.closePath();

   setPreferredSize(new Dimension(scale*20, scale*20));
  }
    
 public void paint(Graphics _g){
   int w = getSize().width, h = getSize().height;

   // Casting to Graphics2D gives us access to the  
   // new 2D features.  
   Graphics2D g = (Graphics2D)_g;
   
   g.scale(scale, scale);

   // Control Rendering quality. Here, we set antialiasing  
   // on to get a smoother rendering.  
   g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
		      RenderingHints.VALUE_ANTIALIAS_ON); 
   // Fill background with GradientPaint
   Color blue = new Color(20, 100, 190);
   GradientPaint bkgPaint = new GradientPaint(0, 0, Color.black, 0, 20, blue);
   g.setPaint(bkgPaint);
   g.fillRect(0, 0, w, h);

   // Create two Gradient Paints
   GradientPaint blueToBlack = new GradientPaint(0, 0, blue, 0, 20, Color.black);
   GradientPaint blackToBlue = new GradientPaint(0, 10, Color.black, 0, 20, blue);

    // Paint big triangle
   g.setPaint(blueToBlack);
   g.fill(upTriangle);
    
    // Paint small triangle
   g.setPaint(blackToBlue);
   g.fill(downTriangle);
    
 }
  public static void main(String args[]){
    new SnippetFrame(new PaintAttribute());
  }
}

