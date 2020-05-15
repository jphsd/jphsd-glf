/*
 * @(#)ScreenRendering.java
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

/**
 * Demonstrates how to access a Graphics2D that renders to a screen 
 * output device.
 *
 * @author        Vincent Hardy
 * @version       1.0, 12.26.1998
 */
public class ScreenRendering extends Component {
  /**
   * Use a SimplePainter to do the rendering
   */
  private SimplePainter painter;

  /**
   * @param imageName name of the image to display
   */
  public ScreenRendering(String imageName){
    painter = new SimplePainter(imageName);
  }

  public Dimension getMinimumSize(){
    return painter.getSize();
  }

  public Dimension getPreferredSize(){
    return painter.getSize();
  }

  public void paint(Graphics _g){
    //
    // Cast to Graphics2D to get access to Java 2D features
    //
    Graphics2D g = (Graphics2D)_g;

    // 
    // Delegate rendering to painter
    //
    painter.render(g);
  }

  public static final String USAGE = "java com.sun.glf.snippets.ScreenRendering <imageFileName>";

  public static void main(String args[]){
    // Check input arguments
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    // Create a new Frame object that will appear on the screen
    // as any other Frame of the window manager.
    Frame frame = new Frame();

    // Set the Color used to fill the frame's background.
    frame.setBackground(Color.white);

    // Add a ScreenRendering component to the Frame. This
    // means the ScreenRendering component will be displayed in
    // the Frame's center.
    frame.add(new ScreenRendering(args[0]));

    // Pack frame to fit content. 
    frame.pack();

    // Show the frame
    frame.setVisible(true);

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });
  }
}
