/*
 * @(#)HelloLayerReuse.java
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
import java.io.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * Illustrates how a Composition can be reused in a LayerComposition,
 * using the CompositionProxyLayer class.
 *
 * @author             Vincent Hardy
 * @version            1.0, 07.14.1999
 */
public class HelloLayersReuse implements CompositionFactory{
  Color backgroundColor = Color.white;

  public Color getBackgroundColor(){
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor){
    this.backgroundColor = backgroundColor;
  }

  public Composition build(){
    // 
    // Create a Composition object from HelloLayer
    //
    HelloLayers compositionFactory = new HelloLayers();
    Composition proxied = compositionFactory.build();

    // 
    // Create a LayerComposition to display several layer compositions
    //
    Dimension proxiedSize = proxied.getSize();
    int w = proxiedSize.width;
    int h = proxiedSize.height;
    Dimension size = new Dimension(w*3, h*3);
    LayerComposition cmp = new LayerComposition(size);
    cmp.setBackgroundPaint(backgroundColor);
    Rectangle cmpRect = cmp.getBounds();
    
    //
    // Create CompositionProxyLayers that render the same composition, but with 
    // different Positions, filters, mask and attributes
    //
    Position positions[] = { Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT,
			     Position.RIGHT, Position.BOTTOM_RIGHT, Position.BOTTOM,
			     Position.BOTTOM_LEFT, Position.LEFT, Position.CENTER };

    Shape mask = new Ellipse2D.Double(0, 0, w, h);

    Shape masks[] = { positions[0].createTransformedShape(mask, cmpRect), 
		      null, 
		      positions[2].createTransformedShape(mask, cmpRect), 
		      null,
		      positions[4].createTransformedShape(mask, cmpRect), 
		      null, 
		      positions[6].createTransformedShape(mask, cmpRect), 
		      null,
		      positions[8].createTransformedShape(mask, cmpRect) };

    AlphaComposite halfTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    Composite composites[] = { null, halfTransparent, null,
			       null, halfTransparent, null,
			       null, halfTransparent, null };

    byte lookup[] = new byte[256];
    for(int i=0; i<256; i++)
      lookup[i] = (byte)(255 - i);
      
    LookupTable table = new ByteLookupTable(0, lookup);
    LookupOp inverter = new LookupOp(table, null);
    
    BufferedImageOp filters[] = { inverter, null, inverter, 
				  null, inverter, null,
				  inverter, null, inverter };

    int n = positions.length;
    Layer layers[] = new Layer[n];

    for(int i=0; i<n; i++){
      layers[i] = new CompositionProxyLayer(cmp, proxied, positions[i]);

      if(masks[i] != null)
	layers[i].setLayerMask(masks[i]);

      if(composites[i] != null)
	layers[i].setComposite(composites[i]);

      if(filters[i] != null)
	layers[i].setImageFilter(filters[i]);      
    }

    cmp.setLayers(layers);
    cmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Dimension halfSize = new Dimension(size.width/2, size.height/2);
    LayerComposition halfCmp = new LayerComposition(halfSize);
    Position halfLayersPosition = new Position(Anchor.CENTER, 0, 0, AffineTransform.getScaleInstance(0.5f, 0.5f));
    Layer halfLayers = new CompositionProxyLayer(halfCmp, cmp, halfLayersPosition);
    
    halfCmp.setLayers(new Layer[]{halfLayers});
 
    return halfCmp;
  }
}

