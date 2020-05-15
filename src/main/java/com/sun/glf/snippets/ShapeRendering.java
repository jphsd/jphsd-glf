/*
 * @(#)ShapeRendering.java
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
import java.awt.event.*;

import javax.swing.*;

/**
 * Illustrates generic Shape rendering
 *
 * @author          Vincent Hardy
 * @version         1.0
 */
public class ShapeRendering extends JComponent {
  // Prepare graphic object to render : a Shape
  Shape shape = new Rectangle(40, 40, 200, 100);

  public ShapeRendering(){
    Rectangle bounds = shape.getBounds();

    int x = bounds.x>0?bounds.x:0;
    int y = bounds.y>0?bounds.y:0;

    setPreferredSize(new Dimension(x*2 + bounds.width, y*2 + bounds.height));
  }

  public void paint(Graphics _g){
    // Acquire Graphics2D object
    Graphics2D g = (Graphics2D)_g;
    
    render(g);
  }
  
  private void render(Graphics2D g){
    // Setup rendering context
    g.setPaint(Color.orange);

    // Invoke rendering method
    g.fill(shape);

    // Setup rendering context
    g.setStroke(new BasicStroke(10)); // Set a thick Stroke
    g.setPaint(new Color(128, 0, 0)); // Set a dark red as the current Paint

    // Invoke rendering methods
    g.draw(shape);  

  }

  public static void main(String args[]){
    new SnippetFrame(new ShapeRendering());
  }
}
