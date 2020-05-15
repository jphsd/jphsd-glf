/*
 * @(#)PaintTypes.java
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
import java.awt.image.*;
import java.awt.geom.*;

import com.sun.glf.util.Toolbox;
import javax.swing.*;

/**
 * Illustrate the different types of paints:
 *  + Colors: they implement the Paint interface and have alpha values
 *  + GradientPaint : they provide a sophisticated painting mechanism.
 *  + TexturePaint : they provide 'shape texture mapping'
 * 
 * Note that Shape is an interface which makes is possible for all those
 * Paint implementations to be used the same way in Graphics2D methods and
 * also to create new Shape implementations (see next example).
 *
 * @author        Vincent Hardy
 * @version       1.0, 02.11.99
 *
 */
public class PaintTypes extends JComponent {
  static final int CELL_WIDTH = 102;
  static final int MARGIN = 20;

  /*
   * Color Paints
   */
  protected Color redColor = new Color(160, 0, 0);
  protected Color whiteColor = Color.white;
  protected Color blueColor = new Color(153, 153, 204); // new Color(0, 0, 128);
  protected Color transparentColor = new Color(0, 0, 0, 0); // Fourth value is alpha

  /*
   * Gradient Paints
   */
  protected Paint redGradient = new GradientPaint(0, 0, redColor, 
						  CELL_WIDTH/2, 0, whiteColor);
  protected Paint blueGradient = new GradientPaint(CELL_WIDTH/2, 0, transparentColor,
						   CELL_WIDTH, 0, blueColor);

  /*
   * Texture Paint
   */
  protected Paint texturePaint;
  
  /** Shape used for filling and drawing */
  protected GeneralPath shape;

  /** Stroke used for drawing shape */
  protected Stroke stroke = new BasicStroke(5.f);

  public PaintTypes(String textureImageName){
    // Load textedImageName into a BufferedImage with an alpha channel.
    BufferedImage textureImage = Toolbox.loadImage(textureImageName, BufferedImage.TYPE_INT_ARGB);

    if(textureImage==null){
      throw new Error("Could not load " + textureImageName);
    }

    // Use the full image as the texture base.
    texturePaint = new TexturePaint(textureImage, new Rectangle(0, 0, textureImage.getWidth(), textureImage.getHeight()));

    // Create the Shape object we will use for fills and draws
    shape = new GeneralPath();  
    shape.moveTo(43.f, 5.f);  
    shape.lineTo(58.f, 5.f);  
    shape.quadTo(58.f, 50.f, 88.f, 95.f);  
    shape.lineTo(65.f, 95.f);  
    shape.curveTo(65.f, 65.f, 35.f, 65.f, 35.f, 95.f);  
    shape.lineTo(13.f, 95.f);  
    shape.quadTo(43.f, 50.f, 43.f, 5.f);  

    // Size is based on margins and cell width 
    setPreferredSize(new Dimension((int)(3*CELL_WIDTH + 4*MARGIN),
					   (int)(3*CELL_WIDTH + 4*MARGIN)));

  }

  /*
   * Paints the component. The painting is a 3x3 grid where the first column shows 
   * what the Paints are, the second their use with Graphics2D.fill and the third
   * their use with Graphics2D.draw
   */
  public void paint(Graphics _g){
    // Casting to Graphics2D gives us access to the  
    // new 2D features.  
    Graphics2D g = (Graphics2D)_g;  
    
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  
		       RenderingHints.VALUE_INTERPOLATION_BILINEAR);  

    // Control Rendering quality. Here, we set antialiasing  
    // on to get a smoother rendering.  
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
		       RenderingHints.VALUE_ANTIALIAS_ON);  

    //
    // First Column. Paint 3 rectangles, one on top of the other
    //
    
    // First, use Color Paints.
    // The first cell is painted in three strips, one for each of 
    // the Color Paints.
    
    // Setup g to use the redColor and fill first third of the cell
    g.setPaint(redColor);
    g.translate(MARGIN, MARGIN);
    g.fillRect(0, 0, CELL_WIDTH/3, CELL_WIDTH);

    // Setup g to use whiteColor and fill second strip of the first cell
    g.setPaint(whiteColor);
    g.fillRect(CELL_WIDTH/3, 0, CELL_WIDTH/3, CELL_WIDTH);

    // Setup g to use blueColor and fill third strip of the first cell
    g.setPaint(blueColor);
    g.fillRect(2*CELL_WIDTH/3, 0, CELL_WIDTH/3, CELL_WIDTH);

    // Now, use GradientPaints.
    // The second cell in painted in two strips, one for each gradient.
    
    // Setup g to use the redGradient and fill the first strip
    g.setPaint(redGradient);
    g.translate(0, MARGIN + CELL_WIDTH);
    g.fillRect(0, 0, CELL_WIDTH/2, CELL_WIDTH);

    // Setup g to use the blueGradient and fill the second strip
    g.setPaint(blueGradient);
    g.fillRect(CELL_WIDTH/2, 0, CELL_WIDTH/2, CELL_WIDTH);

    // Finaly, use texturePaint to fill last cell in the first column
    //

    g.setPaint(texturePaint);
    g.translate(0, MARGIN + CELL_WIDTH); // move to the top left of the last cell.
    g.fillRect(0, 0, CELL_WIDTH, CELL_WIDTH);

    //
    // Second solumn. Stroke a Shape with each type of Paint
    //
    g.translate(CELL_WIDTH + MARGIN, -2*MARGIN -2*CELL_WIDTH);  // Move to the top left of the second column's first cell
                                             // from the first column's last cell.
    
    // Set thick stroke to be used in draw calls
    g.setStroke(stroke);

    // Stroke shape with regular Color Paint
    g.setPaint(redColor);
    g.draw(shape);

    // Stroke shape with GradientPaint
    g.translate(0, MARGIN + CELL_WIDTH); // Move to the top left of the column's second cell
    g.setPaint(redGradient);
    g.draw(shape);
    g.setPaint(blueGradient);
    g.draw(shape);

    // Stroke shape with TexturePaint
    g.translate(0, MARGIN + CELL_WIDTH); // Move to the top of the column's last cell
    g.setPaint(texturePaint);
    g.draw(shape);


    //
    // Third column. Fill a Shape with each type of Paint
    //

    // Fill shape with regular Color Paint
    g.translate(MARGIN + CELL_WIDTH, -2*MARGIN -2*CELL_WIDTH); // Move to the top left of the third column's first cell
                                            // from the second column's last cell.
    g.setPaint(redColor);
    g.fill(shape);

    // Fill shape with GradientPaint
    g.translate(0, MARGIN + CELL_WIDTH); // Move to the top left of the column's second cell
    g.setPaint(redGradient);
    g.fill(shape);
    g.setPaint(blueGradient);
    g.fill(shape);
    

    // Stroke Shape with TexturePaint
    g.translate(0, MARGIN + CELL_WIDTH); // Move to the top of the column's last cell
    g.setPaint(texturePaint);
    g.fill(shape);
  }

  static final String USAGE = "java com.sun.glf.snippets.PaintTypes <textureFileName>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new PaintTypes(args[0]));
  }
}
