/*
 * @(#)ClipAttribute.java
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
 * Illustrates the use of the clipping attribute.
 *
 * @author             Vincent Hardy
 * @version            1.0, 01.03.1999
 */
public class ClipAttribute extends JComponent {
  public ClipAttribute(){
    setPreferredSize(new Dimension(440, 240));
  }

  /**
   * Paint a rectangle after setting the clip to an ellipse Shape
   */
  public void paint(Graphics _g){
    Graphics2D g = (Graphics2D)_g;

    // Set Clipping area
    Ellipse2D clip = new Ellipse2D.Float(120, 70, 400, 400);
    g.clip(clip);

    // Fill rectangle: only the part lying into the clip 
    // area is actally filled.
    g.setPaint(new Color(128, 20, 20));
    Rectangle rect = new Rectangle(20, 20, 400, 200);
    g.fill(rect);
  }

  public static void main(String args[]){
    new SnippetFrame(new ClipAttribute());
  }
}
