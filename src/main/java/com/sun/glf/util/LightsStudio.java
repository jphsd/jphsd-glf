/*
 * @(#)LightsStudio.java
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

package com.sun.glf.util;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JFrame;

import com.sun.glf.*;
import com.sun.glf.goodies.*;
import com.sun.glf.util.*;

/**
 * This class makes the creation and placement of lights easier by providing
 * a set of predefined light styles and positions.
 * <p>
 * This is a static class with only static member and cannot (and should not)
 * be instantiated.
 *
 * @author           Vincent Hardy
 * @version          1.0, 10/01/1998
 * @see              com.sun.glf.goodies.SpotLight
 * @see              com.sun.glf.goodies.DirectionalLight
 * @see              com.sun.glf.goodies.LitSurface
 */
public final class LightsStudio{
  /**
   * Maps Anchors to angles
   */
  private static final double[] lightAngles = { -Math.PI/4., // Top Left
						0,      // Top
						Math.PI/4.,// Top Right
						Math.PI/2,   // Right
						3*Math.PI/4,   // Bottom Right
						Math.PI,            // Bottom
						-3*Math.PI/4,    // Bottom Left
						-Math.PI/2,    // Left
						0             // Center
  };

  /**
   * Maps Anchors to offsets
   */
  private static final Dimension[] lightOffsets = { new Dimension(0, 0),   // Top Left
						    new Dimension(1, 0),   // Top 
						    new Dimension(2, 0),   // Top Right
						    new Dimension(2, 1),   // Right
						    new Dimension(2, 2),   // Bottom Right
						    new Dimension(1, 2),   // Bottom
						    new Dimension(0, 2),   // Bottom Left
						    new Dimension(0, 1),   // Left
						    new Dimension(1, 1),   // Center
  };

  /**
   * Creates a Directional light for a given light position
   *
   * @param anchor light position. 
   * @param I light intensity
   * @param color the light color
   */
  public static DirectionalLight getSunLight(Anchor anchor, double I, Color color){
    if(color==null)
      color = Color.white;

    double L[] = new double[3];
    switch(anchor.toInt()){
    case Anchor.ANCHOR_TOP:
      L[0] = 0; L[1] = -1; L[2] = 1;
      break;
    case Anchor.ANCHOR_BOTTOM:
      L[0] = 0; L[1] = 1; L[2] = 1;
      break;
    case Anchor.ANCHOR_LEFT:
      L[0] = -1; L[1] = 0; L[2] = 1;
      break;
    case Anchor.ANCHOR_RIGHT:
      L[0] = 1; L[1] = 0; L[2] = 1;
      break; 
   case Anchor.ANCHOR_TOP_LEFT:
      L[0] = -1; L[1] = -1; L[2] = 1;
      break;
    case Anchor.ANCHOR_TOP_RIGHT:
      L[0] = 1; L[1] = -1; L[2] = 1;
      break;
    case Anchor.ANCHOR_BOTTOM_LEFT:
      L[0] = -1; L[1] = 1; L[2] = 1;
      break;
    case Anchor.ANCHOR_BOTTOM_RIGHT:
      L[0] = 1; L[1] = 1; L[2] = 1;
      break;
    case Anchor.ANCHOR_CENTER:
      L[0] = 0; L[1] = 0; L[2] = 1;
      break;
    default:
      throw new IllegalArgumentException("Invalid anchor: " + anchor);
    }

    return new DirectionalLight(L, I, color);
  }

  /** 
   * Creates a single SpotLight, positioned according to the
   * anchor parameter, and with the width and height of dim
   *
   * @param rect the rectangle in which the light is positioned
   * @param anchor spot's position
   * @param dim spot's size, i.e. the extent of its lighting
   * @param I spot intensity
   */
  public static SpotLight getSpotLight(Rectangle rect, Anchor anchor, Dimension dim,
				       double I, Color color){
    double angle = lightAngles[anchor.toInt()];
    int w = (int)Math.min(dim.width, dim.height);
    int h = (int)Math.max(dim.width, dim.height);

    Dimension offsets = lightOffsets[anchor.toInt()];
    Rectangle bounds = new Rectangle(rect.x - w/2 + (offsets.width*rect.width)/2, rect.y + (offsets.height*rect.height)/2,
				     w, h);
    return new SpotLight(bounds, color, I, angle);
  }

  /**
   * Creates a single SpotLight, centered about bounds
   * origin and with a radius of half the largest side
   * of bounds
   *
   * @param bounds the rectangle which will be lit
   * @param I light intensity
   * @param color light color
   * @param overlap should be in the [0-1] range. Defines
   *        by how much the light exceeds the bounds's 
   *        largest side, i.e radius = (largest side)*(1+overlap)
   * @param anchor one of CENTER, TOP, BOTTOM, LEFT, RIGHT
   */
  /*public static SpotLight getFloodLight(Rectangle bounds,
					double I,
					Color color,
					double overlap,
					Anchor anchor){
    if(color == null)
      color = Color.white;

    if(overlap<0 || overlap>1)
      throw new IllegalArgumentException("overlap should be in [0-1] range");

    SpotLight light = null;

    if(anchor.toInt()==Anchor.ANCHOR_CENTER){
      int radius = ((int)Math.max(bounds.width, bounds.height))/2;
      int cx = bounds.x + bounds.width/2;
      int cy = bounds.y + bounds.height/2;
      radius *= (1+overlap);
      light = new SpotLight(new Rectangle(cx-radius, cy-radius, 2*radius, 2*radius), color, I, 0);
    }
    else{
      Point p1 = new Point(), p2 = new Point(), p3 = new Point();

      if(bounds.width>bounds.height){
	switch(anchor.toInt()){
	case Anchor.ANCHOR_LEFT:
	case Anchor.ANCHOR_TOP:
	  p1.x = bounds.x;
	  p1.y = bounds.y + bounds.height;
	  p2.x = bounds.x + bounds.width/2;
	  p2.y = bounds.y;
	  p3.x = bounds.x + bounds.width;
	  p3.y = bounds.y + bounds.height;
	  break;
	case Anchor.ANCHOR_RIGHT:
	case Anchor.ANCHOR_BOTTOM:
	  p1.x = bounds.x;
	  p1.y = bounds.y;
	  p2.x = bounds.x + bounds.width/2;
	  p2.y = bounds.y + bounds .height;
	  p3.x = bounds.x + bounds.width;
	  p3.y = bounds.y;
	  break;
	default:
	  throw new IllegalArgumentException("Invalid anchor : " + anchor);
	}    
      }
      else{
	switch(anchor.toInt()){
	case Anchor.ANCHOR_LEFT:
	case Anchor.ANCHOR_TOP:
	  p1.x = bounds.x + bounds.width;
	  p1.y = bounds.y;
	  p2.x = bounds.x;
	  p2.y = bounds.y + bounds .height/2;
	  p3.x = bounds.x + bounds.width;
	  p3.y = bounds.y + bounds.height;	  
	  break;
	case Anchor.ANCHOR_RIGHT:
	case Anchor.ANCHOR_BOTTOM:
	  p1.x = bounds.x;
	  p1.y = bounds.y;
	  p2.x = bounds.x + bounds.width;
	  p2.y = bounds.y + bounds .height/2;
	  p3.x = bounds.x;
	  p3.y = bounds.y + bounds.height;	  
	  break;
	default:
	  throw new IllegalArgumentException("Invalid anchor : " + anchor);
	}    
      }

      // Process light center from the 3 control points
      double dx1 = p2.x - p1.x;
      double dx2 = p3.x - p2.x;
      double dy1 = p2.y - p1.y;
      double dy2 = p3.y - p2.y;
      double mx1 = (p1.x + p2.x)/2.;
      double my1 = (p1.y + p2.y)/2.;
      double mx2 = (p2.x + p3.x)/2.;
      double my2 = (p2.y + p3.y)/2.;
      double tan1 = dx1/dy1;
      double tan2 = dx2/dy2;
      double cx = (-mx1*tan1 + mx2*tan2 - my1 + my2)/(tan2 - tan1);
      double cy = my1 - (cx - mx1)*tan1;
      
      // Radius is distance to any one of the control points
      double radius = Math.sqrt((cx-p1.x)*(cx-p1.x) + (cy-p1.y)*(cy-p1.y))*(1+overlap);

      light = new SpotLight(new Rectangle2D.Double(cx-radius, cy-radius, 2*radius, 2*radius), color, I, 0.);
    }
    return light;
  }*/

  /**
   * Creates a set of SpotLights to illuminate the rectangle
   * described by bounds.
   * @param bounds the rectangle which will be lit
   * @param nLights the number of spots which should go on 
   *        the ramp.
   * @param anchor one of TOP, LEFT, BOTTOM or RIGHT
   * @param I lights intensity
   * @param color the lights color. 
   * @param overlap defines by how much the lights overlap. This
   *                is a percentage and should be between 0 and 1.
   *                0 means no overlap and 1 means maximum overlap.
   */
  public static SpotLight[] getLightRamp(Rectangle bounds, 
					 int nLights, 
					 Anchor anchor, 
					 double I,
					 Color color,
					 double overlap){
    double Is[] = new double[nLights];
    Color colors[] = new Color[nLights];
    for(int i=0; i<nLights; i++){
      Is[i] = I;
      colors[i] = color;
    }

    return getLightRamp(bounds, nLights, anchor, Is, colors, overlap);
  }

  /**
   * Creates a set of SpotLights to illuminate the rectangle
   * described by bounds.
   * @param bounds the rectangle which will be lit
   * @param nLights the number of spots which should go on 
   *        the ramp.
   * @param anchor one of TOP, LEFT, BOTTOM or RIGHT
   * @param I lights intensities.
   * @param colors the lights colors. If null, then white is used.
   * @param overlap defines by how much the lights overlap. This
   *                is a percentage and should be between 0 and 1.
   *                0 means no overlap and 1 means maximum overlap.
   */
  public static SpotLight[] getLightRamp(Rectangle bounds, 
					 int nLights, 
					 Anchor anchor, 
					 double[] I,
					 Color[] colors,
					 double overlap){
    if(nLights<1)
      return null;

    if(overlap<0 || overlap>1)
      throw new IllegalArgumentException("overlap should be in [0-1] range");

    // First, process ramp width
    int rampWidth = 0, lightHeight = 0, lightWidth;
    double dx = 0, dy = 0;
    int x = 0, y = 0;
    double theta = 0;
    Rectangle lightBounds = new Rectangle(0, 0, 0, 0);

    switch(anchor.toInt()){
    case Anchor.ANCHOR_TOP:
      rampWidth = bounds.width;
      lightHeight = 2*rampWidth/nLights;
      lightWidth = rampWidth/nLights;
      x = bounds.x;
      y = bounds.y;

      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;
      dx = 1;
      dy = 0;
      theta = 0;
      break;
    case Anchor.ANCHOR_BOTTOM:
      rampWidth = bounds.width;
      lightHeight = 2*rampWidth/nLights;
      lightWidth = rampWidth/nLights;
      x = bounds.x;
      y = bounds.y + bounds.height;

      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;
      dx = 1;
      dy = 0;
      theta = Math.PI;
      break;
    case Anchor.ANCHOR_LEFT:
      rampWidth = bounds.height;
      lightHeight = rampWidth/nLights;
      lightWidth = 2*lightHeight;
      x = bounds.x;
      y = bounds.y;

      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = x;
      lightBounds.y = (int)(y - lightHeight*overlap/2);
      dx = 0;
      dy = 1;
      theta = 0;
      break;
    case Anchor.ANCHOR_RIGHT:
      rampWidth = bounds.height;
      lightHeight = rampWidth/nLights;
      lightWidth = 2*lightHeight;
      x = bounds.x + bounds.width;
      y = bounds.y;

      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = x;
      lightBounds.y = (int)(y - lightHeight*overlap/2);
      dx = 0;
      dy = 1;
      theta = Math.PI;
      break;
    default:
      throw new IllegalArgumentException("Unknown anchor : " + anchor);
    }

    SpotLight warns[] = new SpotLight[nLights];
    for(int i=0; i<nLights; i++){
      // Light Color
      Color color = Color.white;
      if(colors!=null && colors.length>i && colors[i] != null)
	color = colors[i];

      double Il = I!=null && I.length>i? I[i] : 1.;
      warns[i] = new SpotLight(lightBounds, color, Il, theta);
      lightBounds.x += (int)(dx*lightWidth);
      lightBounds.y += (int)(dy*lightHeight);
    }

    return warns;
  }

  /**
   * Creates a set of SpotLights to illuminate the rectangle
   * described by bounds.
   * @param bounds the rectangle which will be lit
   * @param nLights the number of spots which should go on 
   *        the ramp.
   * @param anchor one of TOP, LEFT, BOTTOM or RIGHT
   * @param I lights intensity.
   * @param color the lights color.
   * @param hotSpot defines the point towards which the lights in the
   *                ramp should point.
   * @param overlap defines by how much the lights overlap. This
   *                is a percentage and should be between 0 and 1.
   *                0 means no overlap and 1 means maximum overlap.
   */
  public static SpotLight[] getHotSpotLightRamp(Rectangle bounds, 
						int nLights, 
						Anchor anchor,
						double I,
						Color color,
						double overlap,
						Point hotSpot){
    double Is[] = new double[nLights];
    Color colors[] = new Color[nLights];
    for(int i=0; i<nLights; i++){
      Is[i] = I;
      colors[i] = color;
    }

    return getHotSpotLightRamp(bounds, nLights, anchor, Is, colors, overlap, hotSpot);
  }

  /**
   * Creates a set of SpotLights to illuminate the rectangle
   * described by bounds.
   * @param bounds the rectangle which will be lit
   * @param nLights the number of spots which should go on 
   *        the ramp.
   * @param anchor one of TOP, LEFT, BOTTOM or RIGHT
   * @param I lights intensities.
   * @param colors the lights colors. If null, then white is used.
   * @param hotSpot defines the point towards which the lights in the
   *                ramp should point.
   * @param overlap defines by how much the lights overlap. This
   *                is a percentage and should be between 0 and 1.
   *                0 means no overlap and 1 means maximum overlap.
   */
  public static SpotLight[] getHotSpotLightRamp(Rectangle bounds, 
						int nLights, 
						Anchor anchor,
						double I[],
						Color[] colors,
						double overlap,
						Point hotSpot){
    if(nLights<1)
      return null;

    if(overlap<0 || overlap>1)
      throw new IllegalArgumentException("overlap should be in [0-1] range");

    // First, process ramp width
    int rampWidth = 0;
    double dx = 0, dy = 0;
    int x = 0, y = 0;
    double theta = 0;
    Rectangle lightBounds = new Rectangle(0, 0, 0, 0);
    Point ph = new Point();
    int lightHeight = 0, lightWidth;

    switch(anchor.toInt()){
    case Anchor.ANCHOR_TOP:
      rampWidth = bounds.width;
      lightHeight = 2*rampWidth/nLights;
      lightWidth = rampWidth/nLights;
      x = bounds.x;
      y = bounds.y;
      dx = 1;
      dy = 0;
      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;

      ph.x = bounds.x + lightWidth/2;
      ph.y = bounds.y;
      break;
    case Anchor.ANCHOR_BOTTOM:
      rampWidth = bounds.width;
      lightHeight = 2*rampWidth/nLights;
      lightWidth = rampWidth/nLights;
      x = bounds.x;
      y = bounds.y + bounds.height;
      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;
      dx = 1;
      dy = 0;
      ph.x = bounds.x + lightWidth/2;
      ph.y = bounds.y + bounds.height;
      break;
    case Anchor.ANCHOR_LEFT:
      rampWidth = bounds.height;
      lightWidth = rampWidth/nLights;
      lightHeight = 2*lightWidth;
      x = bounds.x - lightWidth/2;
      y = bounds.y + lightWidth/2;
      dx = 0;
      dy = 1;
      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;
      ph.x = bounds.x;
      ph.y = bounds.y + lightWidth/2;
      break;
    case Anchor.ANCHOR_RIGHT:
      rampWidth = bounds.height;
      lightWidth = rampWidth/nLights;
      lightHeight = 2*lightWidth;
      x = bounds.x + bounds.width - lightWidth/2;
      y = bounds.y + lightWidth/2;
      dx = 0;
      dy = 1;
      lightBounds.width = (int)(lightWidth*(1 + overlap));
      lightBounds.height = (int)(lightHeight*(1 + overlap));
      lightBounds.x = (int)(x - lightWidth*overlap/2);
      lightBounds.y = y;
      ph.x = bounds.x + bounds.width;
      ph.y = bounds.y + lightWidth/2;
      break;
    default:
      throw new IllegalArgumentException("Invalid anchor : " + anchor);
    }

    SpotLight warns[] = new SpotLight[nLights];

    double lx = 0, ly = 1;
    for(int i=0; i<nLights; i++){
      // Light Color
      Color color = Color.white;
      if(colors!=null && colors.length>i && colors[i] != null)
	color = colors[i];
      
      double Il = I!=null && I.length>i?I[i]:1;

      // Extra angle by which the light should be tilted to point
      // to the hot spot
      double px = hotSpot.x-ph.x;
      double py = hotSpot.y-ph.y;
      double pn = Math.sqrt(px*px + py*py);
      if(pn!=0){
	px /= pn;
	py /= pn;
      }
      double delta = Math.atan2( -px*ly + py*lx, px*lx + py*ly);
      warns[i] = new SpotLight(lightBounds, color, Il, delta);
      lightBounds.x += (int)(dx*lightWidth);
      lightBounds.y += (int)(dy*lightWidth);
      ph.x += (int)(dx*lightWidth);
      ph.y += (int)(dy*lightWidth);
    }

    return warns;
  }

  //
  // Unit testing
  //

  public static final String USAGE = "java com.sun.glf.goodies.LightsStudio textureFile width height";

  public static void main(String args[]){
    if(args.length<3){
      System.out.println(USAGE);
      System.exit(0);
    }

    String textureFileName = args[0];
    int w = Integer.parseInt(args[1]);
    int h = Integer.parseInt(args[2]);
    
    Color testColor = new Color(153, 153, 204);

    // 
    // Create a frame to display the different types
    // of lightings created by the factory
    //
    JFrame frame = new JFrame("LightsStudio Unit Testing");
    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    frame.getContentPane().setLayout(new GridLayout(0, screenWidth/w));

    // Load texture
    BufferedImage texture = Toolbox.loadImage(textureFileName, BufferedImage.TYPE_INT_RGB);
    ElevationMap textureMap = new ElevationMap(texture, true, 10);

    // Create a flat and textured surfaces
    LitSurface flatSurface = new LitSurface(0, LitSurfaceType.NORMAL, null);
    LitSurface texturedSurface = new LitSurface(0, LitSurfaceType.NORMAL, textureMap);

    // Now, test the different lightings
    Rectangle bounds = new Rectangle(0, 0, w, h);

    // Create a based image for filtering
    BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = src.createGraphics();
    g.setPaint(Color.white);
    g.fillRect(0, 0, w, h);

    Layer layer = null;
    LayerComposition comp = null;
    Dimension dim = new Dimension(w, h);

    //
    // Directional light test
    //
    String dirs[] = {"Top", "Bottom", "Right", "Left", "Top Left", "Top Right", "Bottom Left", "Bottom Right", "Center"};
    Anchor anchors[] = {Anchor.TOP, Anchor.BOTTOM, Anchor.RIGHT, Anchor.LEFT, Anchor.TOP_LEFT, Anchor.TOP_RIGHT, 
			Anchor.BOTTOM_LEFT, Anchor.BOTTOM_RIGHT, Anchor.CENTER};

    for(int i=0; i<dirs.length; i++){
      texturedSurface.removeAllLights();
      texturedSurface.addLight( LightsStudio.getSunLight(anchors[i], 1, testColor) );
      frame.getContentPane().add(makeNewComponent(dim, (new LightOp(texturedSurface)).filter(src, null), "Sun Light " + dirs[i]));
    }

    //
    // Light Ramp test
    //
    SpotLight warns[] = null;

    // 3 spot ramp
    warns = LightsStudio.getLightRamp(bounds, 3, Anchor.TOP, 
				       new double[] { 4, 4, 4 },
				       new Color[]{ Color.yellow, Color.white, testColor },
				       0.);
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim, (new LightOp(flatSurface)).filter(src, null), "Light Ramp. Orientation = TOP"));

    // 5 spot ramp
    warns = LightsStudio.getLightRamp(bounds, 5, Anchor.BOTTOM, 
				       new double[] {4, 4, 4, 4, 4},
				       new Color[]{ Color.yellow, Color.white, testColor, Color.white, Color.yellow },
				       .25);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Light Ramp. Orientation = BOTTOM"));

    // 2 spot ramp
    warns = LightsStudio.getLightRamp(bounds, 2, Anchor.LEFT, 
				       new double[] { 4, 4 },
				       new Color[] { Color.white, Color.yellow },
				       .5);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Light Ramp. Orientation = LEFT"));

    // 3 spot ramp
    warns = LightsStudio.getLightRamp(bounds, 3, Anchor.RIGHT, 
				       new double[] { 4, 4 },
				       new Color[] { Color.yellow, Color.white, testColor },
				       1);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Light Ramp. Orientation = RIGHT"));
    
    //
    // Hot Spot Light Ramp test
    //

    // 3 spot ramp
    Point hotSpot = new Point(w/2, h/2);
    warns = LightsStudio.getHotSpotLightRamp(bounds, 3, Anchor.TOP, 
					      new double[] { 4, 4, 4},
					      new Color[]{ Color.yellow, Color.white, testColor },
					      0., hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = TOP"));

    // 5 spot ramp
    warns = LightsStudio.getHotSpotLightRamp(bounds, 5, Anchor.BOTTOM, 
					      new double[] { 4, 4, 4, 4, 4 },
					      new Color[]{ Color.yellow, Color.white, testColor, Color.white, Color.yellow },
					      .25, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = BOTTOM"));

    // 2 spot ramp
    warns = LightsStudio.getHotSpotLightRamp(bounds, 2, Anchor.LEFT, 
					      new double[] { 4, 4},
					      new Color[] { Color.white, Color.yellow },
					      .5, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);   
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = LEFT"));

    // 3 spot ramp
    warns = LightsStudio.getHotSpotLightRamp(bounds, 3, Anchor.RIGHT, 
					      new double[]{4, 4, 4},
					      new Color[] { Color.yellow, Color.white, testColor },
					      1, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
   
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = RIGHT"));

    //
    // Hot Spot Light Ramp test, varying hotSpot
    //

    // 3 spot ramp
    hotSpot = new Point(w/4, h/2);
    warns = LightsStudio.getHotSpotLightRamp(bounds, 3, Anchor.TOP, 
					      new double[]{4, 4, 4},
					      new Color[]{ Color.yellow, Color.white, testColor },
					      0., hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
   
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = TOP. Hot spot w/4 h/2"));

    // 5 spot ramp
    hotSpot = new Point(3*w/4, h/2);
    warns = LightsStudio.getHotSpotLightRamp(bounds, 5, Anchor.BOTTOM, 
					      new double[]{4, 4, 4},
					      new Color[]{ Color.yellow, Color.white, testColor, Color.white, Color.yellow },
					      .25, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = BOTTOM. Hot spot 3*w/4 h/2"));

    // 2 spot ramp
    hotSpot = new Point(w/2, 0);
    warns = LightsStudio.getHotSpotLightRamp(bounds, 2, Anchor.LEFT, 
					      null,
					      new Color[] { Color.white, Color.yellow },
					      .5, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = LEFT. Hot spot w/2 0"));

    // 3 spot ramp
    hotSpot = new Point(w/2, 0);
    warns = LightsStudio.getHotSpotLightRamp(bounds, 3, Anchor.RIGHT, 
					      null,
					      new Color[] { Color.yellow, Color.white, testColor },
					      1, hotSpot);
    flatSurface.removeAllLights();
    flatSurface.addLights(warns);
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null) , "Hot Spot Light Ramp. Orientation = RIGHT. Hot spot w/2 0"));

    //
    // Spot Light test
    //
    Dimension spotSize = new Dimension(w, h);
    
    for(int i=0; i<=Anchor.ANCHOR_CENTER; i++){
      Anchor anchor = Anchor.enumValues[i];
      flatSurface.removeAllLights();
      flatSurface.addLight( LightsStudio.getSpotLight(bounds, anchor, spotSize, 1., Color.white) );
      frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Spot light " + anchor));      
    }
    
    
    //
    // Flood light test
    //

    // Centered
    /////////////
    /*
    // No overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, 0., Anchor.CENTER) );
    frame.getContentPane().add(makeNewComponent(dim,   (new LightOp(flatSurface)).filter(src, null), "Flood light. CENTER, overlap = 0"));

    // Medium overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, .5, Anchor.CENTER) );
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Flood light. CENTER, overlap = .5"));

    // Maximum overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, 1., Anchor.CENTER) );
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Flood light. CENTER, overlat = 1"));

    // Top 
    //////////////
    // Small overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, .25, Anchor.TOP) );
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Flood light. TOP,  overlap = .25"));

    // Medium overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, .5, Anchor.TOP) );
    frame.getContentPane().add(makeNewComponent(dim,   (new LightOp(flatSurface)).filter(src, null), "Flood light. TOP, overlap = .5"));

    // Bottom
    //////////////
    // Small overlap
    flatSurface.removeAllLights();
    flatSurface.addLight( LightsStudio.getFloodLight(bounds, 2, testColor, 0.25, Anchor.BOTTOM) );
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Flood light. BOTTOM, overlap = 0.25"));

    // Medium overlap
    flatSurface.removeAllLights();
    flatSurface.addLight(LightsStudio.getFloodLight(bounds, 2, testColor, .5, Anchor.BOTTOM) );
    frame.getContentPane().add(makeNewComponent(dim,  (new LightOp(flatSurface)).filter(src, null), "Flood light. BOTTOM, overlap = .5"));
    */

    frame.pack();
    frame.setVisible(true);
  }

  private static Component makeNewComponent(Dimension dim, Image image, String toolTip){
    LayerComposition cmp = new LayerComposition(dim);
    ImageLayer layer = new ImageLayer(cmp, image, null);
    cmp.setLayers(new Layer[]{layer});
    CompositionComponent comp = new CompositionComponent(cmp);
    comp.setToolTipText(toolTip);
    return comp;
  }
}

