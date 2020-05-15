/*
 * @(#)HelloRenderingModel.java
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
import java.awt.font.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * First introduction to the Java 2D rendering model. Shows the different steps
 * in the rendering model:
 * + set graphics context attribute
 * + select graphic object to render
 * + invoke rendering on Graphics2D
 *
 * @author          Vincent Hardy
 * @version         1.0, 01.03.1999
 */
public class HelloRenderingModel extends JComponent {
  public HelloRenderingModel(){
    // Preprocess size
    // Text will be rendered with fractional metrics and
    // antialiasing on.
    FontRenderContext frc = new FontRenderContext(null, true, true);

    //
    // Take into account that the text string is drawn at (40, 80).
    // See chapter 5 on Font to get details on Font metrics and
    // what ascent and descent mean.
    //
    TextLayout textLayout = new TextLayout("Hello Rendering Model", 
					   new Font("Curlz MT", Font.PLAIN, 60),
					   frc);
    int width = (int)(textLayout.getVisibleAdvance() + 40*2); // Margins of 40 on each right and left sides
    int height = (int)(textLayout.getAscent() + textLayout.getDescent() + (80 - textLayout.getAscent())*2); // Margins on top and bottom
    setPreferredSize(new Dimension(width, height));
  }

  public void paint(Graphics _g){
    // Acquire Graphics2D object
    Graphics2D g = (Graphics2D)_g;
    
    render(g);
  }
  
  private void render(Graphics2D g){
    //
    // Step A: set rendering context attributes
    //
    // Black will be used to render text and Shapes
    g.setPaint(Color.black); 

    // The "Curlz MT" Font will be used to render text
    g.setFont(new Font("Curlz MT", Font.PLAIN, 60)); 
    
    // A special technique will be used to render text and Shapes so make them appear smoother.
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);     

    //
    // Step B: Select graphic object to render: a string of character.
    //
    String text = "Hello Rendering Model";

    //
    // Step C: Invoke rendering method.
    //
    g.drawString(text, 40, 80);
  }

  // Note: later examples replace the following main code
  // by the SnippetFrame use. The following code is equivalent
  // to:
  //     new SnippetFrame(new HelloRenderingModel());
  //
  public static void main(String args[]){
    // Build a top level Frame where our HelloRenderingModel 
    // will be displayed.
    JFrame frame = new JFrame();

    // Add HelloRenderingModel component to the frame's
    // interior.
    frame.getContentPane().add(new HelloRenderingModel());
    frame.getContentPane().setBackground(Color.white);

    // Pack to fit the HelloRenderingModel's size
    frame.pack();

    // Add a listener to close the application when 
    // user closes the frame.
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    // Show frame
    frame.setVisible(true);

  }

  static {
    // Work around font loading issues
    com.sun.glf.util.Toolbox.initFonts();
  }
}
