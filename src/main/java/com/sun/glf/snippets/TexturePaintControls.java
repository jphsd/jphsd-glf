/*
 * @(#)TexturePaintControls.java
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

import com.sun.glf.util.Toolbox;
import com.sun.glf.snippets.SnippetFrame;

import javax.swing.*;

/**
 * Illustrates the different settings for the TexturePaint anchor
 *
 * @author          Vincent Hardy
 * @version         1.0, 01/02/1999
 */
public class TexturePaintControls extends JComponent{
  TexturePaint paint;

  public TexturePaintControls(String textureFileName){
    BufferedImage texture = Toolbox.loadImage(textureFileName, BufferedImage.TYPE_INT_RGB);
    if(texture==null)
      throw new Error("Could not load : " + textureFileName);

    // Rectangle anchor = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
    
    // Rectangle anchor = new Rectangle(texture.getWidth()/2, texture.getHeight()/2, texture.getWidth(), texture.getHeight());
    
    Rectangle anchor = new Rectangle(0, 0, texture.getWidth()/2, texture.getHeight()/2);
    
    paint = new TexturePaint(texture, anchor);
    setPreferredSize(new Dimension(texture.getWidth()*3, texture.getHeight()*3));
  }

  public void paint(Graphics _g){
    Dimension dim = getSize();

    Graphics2D g = (Graphics2D)_g;
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    g.setPaint(paint);
    g.fillRect(0, 0, dim.width, dim.height);
  }

  static final String USAGE = "java com.sun.glf.priv.snipets.TexturePaintControls <textureFileName>";

  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    new SnippetFrame(new TexturePaintControls(args[0]));
  }
}