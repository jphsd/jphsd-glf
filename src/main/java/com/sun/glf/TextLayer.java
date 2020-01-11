/*
 * @(#)TextLayer.java
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

package com.sun.glf;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.JFrame;

import com.sun.glf.util.*;

/**
 * <code>TextLayer</code> extends <code>ShapeLayer</code> because
 * text is actually rendered as <code>Shape</code> objects. The <code>TextLayer
 * </code> provides convenience constructors to ease justification and placement 
 * of text in a <code>LayerComposition</code>.
 * <p>
 * There are two important parameters which define the text placement in a 
 * <code>Composition</code>: the anchor and the justification. The anchor describes 
 * where and how the overall text block should be positioned in the composition (e.g.
 * Centered, Top Left, etc...). The justification describes how the text block
 * itself is aligned: right, left or center. The justification describes the 
 * text block lines placement relative to each other. For example, the center
 * justification means that lines are aligned along the same vertical axis.
 * <p>
 * A TextLayer can optionally use AttributedStrings for TextLayers which should
 * displayed text with mixed attributes (e.g. different fonts).
 * 
 * @author        Vincent Hardy
 * @version       1.0, 10/09/1998
 * @see           com.sun.glf.Layer
 * @see           #makeTextBlock
 */
public class TextLayer extends ShapeLayer{
  /**
   * @see #makeTextBlock
   */
  private static final FontRenderContext frc = new FontRenderContext(null, true, true);

  /**
   * This is the simplest TextLayer constructor. The text will by both centered
   * in its parent composition and have a center alignment. 
   *
   * @param parent the parent composition into which this layer is displayed.
   * @param text the text which should be displayed in the layer
   * @param font the font which this text should use for rendering
   * @param renderer will perform the text rendering, such as filling or stroking.
   */
  public TextLayer(LayerComposition parent, 
		   String text, 
		   Font font, 
		   Renderer renderer){
    super(parent, makeTextBlock(text, font, parent.getSize().width, TextAlignment.CENTER), renderer, Position.CENTER);
  }

  /**
   * Creates a text block whose overall position is controlled by the Position value
   * <br>
   * The text block justification is controlled by the alignment value, and can be
   * right or left justified or centered.<br>
   * The text to be displayed is itself described by the array of AttributedStrings. Each element
   * of the array in interpreted as a separate paragraph and will appear on a separate line.
   * <br>
   *
   * @param parent the parent composition into which this layer is displayed.
   * @param attributedStrings the set of paragraphs wich will be displayed in the layer. Note that the
   *        BACKGROUND and FOREGROUND attributes, if set, will be ignored and superseded by the
   *        renderer. In other words, the way the attributedStrings are rendered, depends on the renderer
   *        and not on the BACKGROUND and FOREGROUND settings.
   * @param renderer defines how the text block is rendered.
   * @param position text block position in the composition
   * @param wrapWidth defines the maximum width for each line in the text block. Lines which exceed the 
   *        wrapWidth will be wrapped in several lines.
   * @param justification the text block justification. One of TextAlignment.LEFT, TextAlignment.RIGHT, TextAlignment.CENTER,
   *        or TextAlignment.JUSTIFY.
   */
  public TextLayer(LayerComposition parent, 
		   AttributedString attributedStrings[],
		   Renderer renderer,
		   Position position,
		   float wrapWidth,
		   TextAlignment justification
		   ){
    super(parent, makeTextBlock(attributedStrings, wrapWidth, justification ), renderer, position);
  }

  /**
   * Creates a text block whose overall position is controlled by the Position value
   * <br>
   * The text block justification is controlled by the alignment value, and can be
   * right or left justified or centered.<br>
   * The text to be displayed is itself described by the array of AttributedStrings. Each element
   * of the array in interpreted as a separate paragraph and will appear on a separate line.
   * <br>
   *
   * @param parent the parent composition into which this layer is displayed.
   * @param text the text which should be displayed in the layer
   * @param font the font which this text should use for rendering
   * @param renderer defines how the text block is rendered.
   * @param position text block position in the composition
   * @param wrapWidth defines the maximum width for each line in the text block. Lines which exceed the 
   *        wrapWidth will be wrapped in several lines.
   * @param justification the text block justification. One of TextAlignment.LEFT, TextAlignment.RIGHT, TextAlignment.CENTER,
   *        or TextAlignment.JUSTIFY.
   */
  public TextLayer(LayerComposition parent, 
		   String text,
		   Font font,
		   Renderer renderer,
		   Position position,
		   float wrapWidth,
		   TextAlignment justification
		   ){
    super(parent, makeTextBlock(text, font, wrapWidth, justification ), renderer, position);
  }

  /**
   * Creates a Shape representing the text block described by the input parameters.
   * This creates an array of AttributedStrings from the text, considering line
   * separators as paragraph breaks. Then, it uses the other version of the makeTextBlock
   * method.
   * @param text the text which should be displayed in the layer
   * @param font the font which this text should use for rendering
   */
  public static Shape makeTextBlock(String text,
				    Font font){
    return makeTextBlock(text, font, -1, TextAlignment.CENTER);
  }

  /**
   * Creates a Shape representing the text block described by the input parameters.
   * This creates an array of AttributedStrings from the text, considering line
   * separators as paragraph breaks. Then, it uses the other version of the makeTextBlock
   * method.
   * @param text the text which should be displayed in the layer
   * @param font the font which this text should use for rendering
   * @param justification one of TextAlignment.LEFT, TextAlignment.RIGHT or TextAlignment.CENTER.
   * @param wrapWidth defines the maximum width for each line in the text block. Lines which exceed the 
   *        wrapWidth will be wrapped in several lines. If the wrapWidth is negative, no wrapping will be done.
   */
  public static Shape makeTextBlock(String text,
				    Font font,
				    float wrapWidth,
				    TextAlignment justification){
    
    //
    // First, break input text into separate paragraphs. Paragraphs are separated 
    // by line breaks.
    //
    String lineSeparator = System.getProperty("line.separator", "\n");
    StringTokenizer st = new StringTokenizer(text, lineSeparator);
    int nParagraphs = st.countTokens();
    String paragraphs[] = new String[nParagraphs];
    for(int i=0; i<nParagraphs; i++)
      paragraphs[i] = (String)st.nextToken();

    //
    // Now, create one AttributedString for each paragraph
    //
    Hashtable stringAttrs = new Hashtable();
    stringAttrs.put(TextAttribute.FONT, font);
    AttributedString attributedStrings[] = new AttributedString[nParagraphs];
    for(int i=0; i<nParagraphs; i++){
      attributedStrings[i] = new AttributedString(paragraphs[i], stringAttrs);
    }
    
    return makeTextBlock(attributedStrings, wrapWidth, justification);
  }

  /**
   * Creates a Shape representing the text block described by the input parameters.
   */
  public static Shape makeTextBlock(AttributedString[] attributedStrings, 
				   float wrapWidth, 
				   TextAlignment justification){
    
    //
    // First, iterate through the attributedStrings, and wrap lines
    // as needed (this is done by the LineBreakMeasurer class).
    //
    int nParagraphs = attributedStrings.length;
    AttributedCharacterIterator iter = null;
    TextLayout layout = null;
    Shape lineShape = null;
    AffineTransform t = new AffineTransform();
    Vector vlines = new Vector();
    int textBlockWidth = 0;
    Vector vlineBounds = new Vector();
    Rectangle bounds = null;

    if(wrapWidth<=0)
      // No wrapping should be done : set wrapWidth to maximum value: this
      // will prevent any wrapping
      wrapWidth = Float.MAX_VALUE;

    for(int i=0; i<nParagraphs; i++){
      // Current string
      AttributedString attributedString = attributedStrings[i];

      // Set a JUSTIFICATION attribute coherent with TextAlignment policy
      if(justification.equals(TextAlignment.JUSTIFY))
	attributedString.addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
      else
	attributedString.addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_NONE);
	
      iter = attributedString.getIterator();
      LineBreakMeasurer measurer = new LineBreakMeasurer(iter, frc);
      int limit = iter.getEndIndex();
      Vector vLayouts = new Vector();
      while (measurer.getPosition() < limit) {
	vLayouts.addElement(measurer.nextLayout(wrapWidth));
      }

      int nLayouts = vLayouts.size();
      for(int j=0; j<nLayouts-1; j++){
	layout = (TextLayout)vLayouts.elementAt(j);
	layout = layout.getJustifiedLayout(wrapWidth);
	t.translate(0, layout.getAscent());
	lineShape = layout.getOutline(t);
	vlines.addElement(lineShape);
	t.translate(0, layout.getDescent() + layout.getLeading());
	
	bounds = lineShape.getBounds();
	textBlockWidth = (int)Math.max(textBlockWidth, bounds.width);
	vlineBounds.addElement(bounds);
      }

      // Do no justify last line
      if(nLayouts>0){
	layout = (TextLayout)vLayouts.elementAt(nLayouts-1);
	t.translate(0, layout.getAscent());
	lineShape = layout.getOutline(t);
	vlines.addElement(lineShape);
	t.translate(0, layout.getDescent() + layout.getLeading());
	
	bounds = lineShape.getBounds();
	textBlockWidth = (int)Math.max(textBlockWidth, bounds.width);
	vlineBounds.addElement(bounds);
      }

    }

    //
    // Now, apply justification rule.
    //
    int nLines = vlines.size();
    Shape lineShapes[] = new Shape[nLines];
    Rectangle lineBounds[] = new Rectangle[nLines];
    vlineBounds.copyInto(lineBounds);

    vlines.copyInto(lineShapes);

    switch(justification.toInt()){
    case TextAlignment.JUSTIFICATION_LEFT:
    case TextAlignment.JUSTIFICATION_JUSTIFY:
      break;
    case TextAlignment.JUSTIFICATION_CENTER:
      for(int i=0; i<nLines; i++){
	t.setToTranslation((textBlockWidth-lineBounds[i].width)/2, 0);
	lineShapes[i] = t.createTransformedShape(lineShapes[i]);
      }
      break;
    case TextAlignment.JUSTIFICATION_RIGHT:
      for(int i=0; i<nLines; i++){
	t.setToTranslation(textBlockWidth-lineBounds[i].width, 0);
	lineShapes[i] = t.createTransformedShape(lineShapes[i]);
      }
      break;
    default:
      throw new IllegalArgumentException("Illegal alignment: " + justification);
    }

    // 
    // Concatenate lineShapes into a single Shape
    //
    GeneralPath textBlock = new GeneralPath();
    for(int i=0; i<nLines; i++)
      textBlock.append(lineShapes[i], false); // Do not connect lines

    return textBlock;
  }

  /*
   * Unit testing
   */
  public static void main(String args[]){
    //
    // Create a frame to display the different compositions
    //
    final JFrame frame = new JFrame("TextLayer unit testing");
    frame.getContentPane().setLayout(new GridLayout(0, 3));
    frame.getContentPane().setBackground(Color.white);

    //
    // Create different text layers, using the various alignment and justification
    // styles.
    //
    Dimension dim = new Dimension(200, 100);
    String ls = System.getProperty("line.separator", null);
    String text = "TextLayer extends ShapLayer." + ls + "it supports various justification and anchor styles ";
    text += "and is able to wrap lines which are too long to fit in the composition widht, such as this one ";
    text += "when using a typical font, i.e one which is not too small";
    Font font = new Font("Dialog", Font.PLAIN, 10);
    Renderer renderer = new FillRenderer(Color.black);
    Renderer pencil = new StrokeRenderer(Color.black, 1);
    RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //
    // TextAlignment 
    //

    // Default : Centered
    LayerComposition comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    Layer boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    TextLayer layer = new TextLayer(comp, text, font, renderer);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Defaults. Anchor = Center, TextAlignment = Center");}});
    
    // Right justified
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, Position.CENTER, dim.width, TextAlignment.RIGHT);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Right");}});
    
    // Left justified
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, Position.CENTER, dim.width, TextAlignment.LEFT);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Left");}});    

    // Justify Justified
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, Position.CENTER, dim.width, TextAlignment.JUSTIFY);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Justify");}});    

    //
    // Positions
    //
    
    // Anchor.TOP_LEFT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.TOP_LEFT, 20, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor= Top Left, TextAlignment = Center");}});    
    
    // Anchor.TOP
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.TOP, 2000, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Top, TextAlignment = Center");}});    
    
    // Anchor.TOP_RIGHT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.TOP_RIGHT, 20, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Top Right, TextAlignment = Center");}});    
    
    // Anchor.RIGHT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.RIGHT, 20, 2000), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Right, TextAlignment = Center");}});    
    
    // Anchor.BOTTOM_RIGHT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.BOTTOM_RIGHT, 20, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Bottom Right, TextAlignment = Center");}});    
    
    // Anchor.BOTTOM
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.BOTTOM, 2000, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Bottom, TextAlignment = Center");}});    
    
    // Anchor.BOTTOM_LEFT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.BOTTOM_LEFT, 20, 20), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Bottom Left, TextAlignment = Center");}});    
    
    // Anchor.LEFT
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, text, font, renderer, new Position(Anchor.LEFT, 20, 2000), 60, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Left, TextAlignment = Center");}});    
    
    //
    // Styled Strings
    //
    String styledString = "Arts, Graphics & Fun";
    Font dianeFont = new Font("Dialog", Font.BOLD, 20);
    Font romainFont = new Font("Times New Roman", Font.BOLD|Font.ITALIC, 20);
    Font anneFont = new Font("Dialog", Font.BOLD|Font.ITALIC, 30);
    AttributedString attributedString = new AttributedString(styledString);
    attributedString.addAttribute(TextAttribute.FONT, dianeFont);
    attributedString.addAttribute(TextAttribute.FONT, romainFont, 5, 16);
    attributedString.addAttribute(TextAttribute.FONT, anneFont, 16, 20);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    layer = new TextLayer(comp, new AttributedString[]{attributedString}, renderer,
                          new Position(Anchor.CENTER, 2000, 2000), dim.width,  TextAlignment.CENTER);
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("AttributedString and Default settings.");}});    
    
    // 
    // Transforms
    //

    // Rotate
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    Position position = new Position(Anchor.CENTER, 0, 0, AffineTransform.getRotateInstance(Math.PI/4.));
    layer = new TextLayer(comp, text, font, renderer, position, dim.width, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Center, Transform = rotate");}});
    
    // Shear
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    position = new Position(Anchor.CENTER, 0, 0, AffineTransform.getShearInstance(.2, .2));
    layer = new TextLayer(comp, text, font, renderer, position, dim.width, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Center, Transform = shear");}});
    
    // Scale
    comp = new LayerComposition(dim);
    comp.setRenderingHints(rh);
    boundsLayer = new ShapeLayer(comp, new Rectangle(-1, -1, dim.width, dim.height), pencil);
    position = new Position(Anchor.CENTER, 0, 0, AffineTransform.getScaleInstance(1.5, -2));
    layer = new TextLayer(comp, text, font, renderer, position, dim.width, TextAlignment.CENTER);
    comp.setLayers(new Layer[]{layer, boundsLayer});
    frame.getContentPane().add(new CompositionComponent(comp){{setToolTipText("Anchor = Center, TextAlignment = Center, Transform = scale");}});
    
    frame.pack();

    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	frame.setVisible(false);
	frame.dispose();
	System.exit(0);
      }
    });

    frame.setVisible(true);
  }
}

