/*
 * @(#)AdjustedAnchorPlacement.java
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
import java.awt.event.*;
import java.awt.image.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * Illustrates the use of Anchors for placing Shapes relative the the 
 * LayerComposition's bounding Rectangle.
 *
 * @author             Vincent Hardy
 * @version            1.0, 03.07.1999
 */
public class AdjustedAnchorPlacement{
  public static final String USAGE = "java com.sun.glf.snippets.AdjustedAnchorPlacement <backgroundImage>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    // Load background image
    BufferedImage background = Toolbox.loadImage(args[0], BufferedImage.TYPE_INT_RGB);
    if(background==null)
      throw new IllegalArgumentException("Could not load : " + args[0]);
    Graphics2D g = background.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    AffineTransform defaultTransform = g.getTransform();
    
    // Rectangle where shapes will be positioned
    Rectangle rect = new Rectangle(0, 0, background.getWidth(), background.getHeight());

    GeneralPath eiffel = new GeneralPath();  
    eiffel.moveTo(40.f, 20.f); 
    eiffel.lineTo(50.f, 20.f);  
    eiffel.quadTo(50.f, 50.f, 70.f, 80.f); 
    eiffel.lineTo(55.f, 80.f); 
    eiffel.curveTo(55.f, 60.f, 35.f,   
		   60.f, 35.f, 80.f);   
    eiffel.lineTo(20.f, 80.f); 
    eiffel.quadTo(40.f, 50.f, 40.f, 20.f); 

    // Create a GradientPaint that will be used to fill the Eiffel
    // Tower. The tip will be white and the bottom a pale orange.
    Rectangle bounds = eiffel.getBounds();
    GradientPaint gradient = new GradientPaint(0, bounds.y, Color.white, 
					       0, bounds.y + bounds.height, new Color(255, 185, 60));


    Anchor anchors[] = { Anchor.TOP_LEFT, Anchor.TOP, Anchor.TOP_RIGHT,
			 Anchor.RIGHT,
			 Anchor.BOTTOM_RIGHT, Anchor.BOTTOM, Anchor.BOTTOM_LEFT,
			 Anchor.LEFT, Anchor.CENTER };

    int n = anchors.length;
    g.setPaint(gradient);

    for(int i=0; i<n; i++){
      Position position = new Position(anchors[i], 40, 60);      // Create new Position.
      g.transform(position.getTransform(eiffel, rect));  // Set Transform computed by Position
      g.fill(eiffel);                                    // Render Shape
      g.setTransform(defaultTransform);                  // Restore default transform.
    }

    LayerComposition cmp = new LayerComposition(new Dimension(rect.width, rect.height));
    ImageLayer image = new ImageLayer(cmp, background);
    cmp.setLayers(new Layer[]{image});

    CompositionFrame frame = new CompositionFrame("Positioning Shapes with Anchors");
    frame.setComposition(cmp);
    frame.pack();
    
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	System.exit(0);
      }
    });

    frame.setVisible(true);
  }
}

