/**
 * @(#)ShapeCastShadowComposition.java
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

package com.sun.glf.demos;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.font.*;
import java.awt.color.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.goodies.*;

/**
 * This example provides a common roof for illustrating three different
 * shadow techniques illustrated by :<br>
 * + TextRecessedShadowComposition<br>
 * + ShapeCastShadowComposition<br>
 * + ImageDropShadowComposition<br>
 * <br>
 * 
 * The example uses three beans, one for each of the above types.
 * This composition only modifies the color attributes and shadow
 * offsets to show three states for the beans, corresponding to 
 * button states:<br>
 * a. Up. Shows how a button would look in the up state.<br>
 * b. Hot. Shows how a button would look in the hot state (i.e. when
 *    the cursor rolls over it.<br>
 * c. Down. Show how a button would look in the down state.<br>
 * 
 *
 * @author         Vincent Hardy
 * @version        1.0, 06/04/1999
 */
public class ShadowsComposition implements CompositionFactory{
  static final String INSPECTING_BEANS = "Inspecting beans: ";
  static final String ERROR_NOT_A_COMPOSITION_FACTORY = "Error: not a CompositionFactory";
  static final String ERROR_CANNOT_LOAD_BEAN = "Error: cannot load bean. ";

  File dropShadowBean = new File("");

  File castShadowBean = new File("");

  File recessedShadowBean = new File("");

  Color buttonUpColor = new Color(120, 120, 160);

  Color buttonHotColor = new Color(180, 180, 130);

  Dimension shadowOffsetUp = new Dimension(10, 10);

  Dimension shadowOffsetDown = new Dimension(0, 0);

  Color backgroundTopColor = Color.white;

  Color backgroundBottomColor = Color.black;

  public Color getBackgroundTopColor(){
    return backgroundTopColor;
  }

  public void setBackgroundTopColor(Color backgroundTopColor){
    this.backgroundTopColor = backgroundTopColor;
  }

  public Color getBackgroundBottomColor(){
    return backgroundBottomColor;
  }

  public void setBackgroundBottomColor(Color backgroundBottomColor){
    this.backgroundBottomColor = backgroundBottomColor;
  }

  public File getDropShadowBean(){
    return dropShadowBean;
  }

  public void setDropShadowBean(File dropShadowBean){
    this.dropShadowBean = dropShadowBean;
  }

  public File getCastShadowBean(){
    return castShadowBean;
  }

  public void setCastShadowBean(File castShadowBean){
    this.castShadowBean = castShadowBean;
  }

  public File getRecessedShadowBean(){
    return recessedShadowBean;
  }

  public void setRecessedShadowBean(File recessedShadowBean){
    this.recessedShadowBean = recessedShadowBean;
  }

  public Color getButtonUpColor(){
    return buttonUpColor;
  }

  public void setButtonUpColor(Color buttonUpColor){
    this.buttonUpColor = buttonUpColor;
  }

  public Color getButtonHotColor(){
    return buttonHotColor;
  }

  public void setButtonHotColor(Color buttonHotColor){
    this.buttonHotColor = buttonHotColor;
  }

  public Dimension getShadowOffsetUp(){
    return shadowOffsetUp;
  }

  public void setShadowOffsetUp(Dimension shadowOffsetUp){
    this.shadowOffsetUp = shadowOffsetUp;
  }

  public Dimension getShadowOffsetDown(){
    return shadowOffsetDown;
  }

  public void setShadowOffsetDown(Dimension shadowOffsetDown){
    this.shadowOffsetDown = shadowOffsetDown;
  }

  public Composition build(){
    //
    // First, load the base Composition factories
    //
    ImageDropShadowComposition dropShadowFactory 
      = (ImageDropShadowComposition)CompositionFactoryLoader.loadBeanFile(dropShadowBean);
    ShapeCastShadowComposition castShadowFactory 
      = (ShapeCastShadowComposition)CompositionFactoryLoader.loadBeanFile(castShadowBean);
    TextRecessedShadowComposition recessedShadowFactory 
      = (TextRecessedShadowComposition)CompositionFactoryLoader.loadBeanFile(recessedShadowBean);

    if(dropShadowFactory==null ||
       castShadowFactory==null ||
       recessedShadowFactory==null)
      throw new IllegalArgumentException();

    //
    // Now, generate compositions for the different buttons state, each time
    // setting the right configuration
    //

    // Up buttons
    dropShadowFactory.setImageTint(buttonUpColor);
    dropShadowFactory.setShadowOffset(shadowOffsetUp);
    castShadowFactory.setGlyphColor(buttonUpColor);
    castShadowFactory.setShadowOffset(shadowOffsetUp);
    recessedShadowFactory.setLabelColor(buttonUpColor);
    recessedShadowFactory.setShadowOffset(shadowOffsetUp);

    Composition dropShadowUp = dropShadowFactory.build();
    Composition castShadowUp = castShadowFactory.build();
    Composition recessedShadowUp = recessedShadowFactory.build();

    // Hot buttons
    dropShadowFactory.setImageTint(buttonHotColor);
    castShadowFactory.setGlyphColor(buttonHotColor);
    recessedShadowFactory.setLabelColor(buttonHotColor);
    
    Composition dropShadowHot = dropShadowFactory.build();
    Composition castShadowHot = castShadowFactory.build();
    Composition recessedShadowHot = recessedShadowFactory.build();

    // Down buttons
    dropShadowFactory.setShadowOffset(shadowOffsetDown);
    castShadowFactory.setShadowOffset(shadowOffsetDown);
    recessedShadowFactory.setShadowOffset(shadowOffsetDown);
    
    Composition dropShadowDown = dropShadowFactory.build();
    Composition castShadowDown = castShadowFactory.build();
    Composition recessedShadowDown = recessedShadowFactory.build();

    Composition compositions[] = {  dropShadowUp, dropShadowHot, dropShadowDown,
				    castShadowUp, castShadowHot, castShadowDown,
				    recessedShadowUp, recessedShadowHot, recessedShadowDown };
    Anchor anchors[] = { Anchor.TOP_LEFT, Anchor.TOP, Anchor.TOP_RIGHT,
			 Anchor.LEFT, Anchor.CENTER, Anchor.RIGHT,
			 Anchor.BOTTOM_LEFT, Anchor.BOTTOM, Anchor.BOTTOM_RIGHT };

    // 
    // Process size based on the largest composition
    //
    Dimension cellSize = new Dimension(0, 0);
    for(int i=0; i<compositions.length; i++){
      Dimension cmpSize = compositions[i].getSize();
      cellSize.width = cellSize.width<cmpSize.width?cmpSize.width:cellSize.width;
      cellSize.height = cellSize.height<cmpSize.height?cmpSize.height:cellSize.height;
    }
    Dimension size = new Dimension(cellSize.width*3, cellSize.height*3);
    LayerComposition cmp = new LayerComposition(size);
    GradientPaint backgroundPaint = new GradientPaint(0, 0, backgroundTopColor,
						      size.width, size.height, backgroundBottomColor);
    cmp.setBackgroundPaint(backgroundPaint);

    Layer layerStack[] = new Layer[compositions.length];
    for(int i=0; i<compositions.length; i++){
      Dimension cmpSize = compositions[i].getSize();
      Position position = new Position(anchors[i], (cellSize.width - cmpSize.width)/2,
				       (cellSize.height - cmpSize.height)/2);
      layerStack[i] = new CompositionProxyLayer(cmp, compositions[i], position);
    }

    cmp.setLayers(layerStack);
    return cmp;
      
  }

}

