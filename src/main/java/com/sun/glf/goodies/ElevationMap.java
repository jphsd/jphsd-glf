/*
 * @(#)ElevationMap.java
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
package com.sun.glf.goodies;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;

import com.sun.glf.*;

/**
 * Defines an map of elevations for a LitSurface. An ElevationMap
 * object processes the surface normal on any given point.
 * <p>
 * An elevation map may be used on a surface whose size is bigger
 * or smaller. To control how the two relate, the anchorPoint can be set.
 * The anchorPoint defines where the map starts in the surface.
 *
 * @author          Vincent Hardy
 * @version         1.0, 09/27/1998
 * @see             com.sun.glf.goodies.SpotLight
 * @see             com.sun.glf.goodies.LitSurface
 * @see             com.sun.glf.goodies.DirectionalLight
 */
public class ElevationMap{
  /* Normals are normalized to be NORM is length */
  static final int NORM = 1024;

  /* Map size */
  int w, h;

  /* Surface normal vector */
  int normal[][];

  /** Out of bound normal */
  private int flatNormal[] = { 0, 0, NORM };

  /** Anchor Point : defines where the map starts on the surface */
  private int anchorX = 0;
  private int anchorY = 0;

  /** Defines whether or not the map is repeated along the surface */
  private boolean cyclic = true;

  /**
   * Creates an elevation map from a Shape. The Shape is painted in white on
   * a black background, then blurred and cliped. The resulting image is used
   * as a elevation map. With this constructor, the map size is that of the 
   * Shape's bounds.
   *
   * @param shape the Shape from which the elevation map is created
   * @param blurRadius size of the gaussian blur that is applied to create the
   *        elevation levels.
   * @param whiteIsHigh if true, the brighter the pixel, the higher
   *        the elevation. Otherwise, the reverse is true.
   * @param heightScale Elevation scale factor, when using Math.sin to
   *        smooth curves. The higher the scale, the steeper the 'slopes'
   */
  public ElevationMap(Shape shape, int blurRadius, boolean whiteIsHigh, int heightScale){
    this(shape, shape.getBounds(), Anchor.CENTER, 0, 0, blurRadius, whiteIsHigh, heightScale);
  }

  /**
   * Creates an elevation map from a Shape. The Shape is painted in white on
   * a black background, then blurred and cliped. The resulting image is used
   * as a elevation map. 
   *
   * @param shape the Shape from which the elevation map is created
   * @param mapBounds defines the map extent. Shape is positioned relative to the
   *        mapBounds, according to the anchor value
   * @param anchor defines the shape's location relative to the mapBounds.
   * @param hAdjust x axis anchor adjustment
   * @param vAdjust y axis anchor adjustment
   * @param blurRadius size of the gaussian blur that is applied to create the
   *        elevation levels.
   * @param whiteIsHigh if true, the brighter the pixel, the higher
   *        the elevation. Otherwise, the reverse is true.
   * @param heightScale Elevation scale factor, when using Math.sin to
   *        smooth curves. The higher the scale, the steeper the 'slopes'
   */
  public ElevationMap(Shape shape, Rectangle mapBounds, Anchor anchor, float hAdjust, float vAdjust, 
		      int blurRadius, boolean whiteIsHigh, int heightScale){
    // We are going to position shape relative to a bounding rectangle
    // starting in (0, 0), like the BufferedImage where is will be painted.
    Rectangle bounds = (Rectangle)mapBounds.clone();
    bounds.x = 0;
    bounds.y = 0;
    Position position = new Position(anchor, hAdjust, vAdjust);
    AffineTransform positionTxf = position.getTransform(shape.getBounds(), bounds);

    // Extend the bounds to account for the blur radius
    bounds.width += 2*blurRadius;
    bounds.height += 2*blurRadius;

    // Take extra blurRadius into account
    AffineTransform t = AffineTransform.getTranslateInstance(blurRadius, blurRadius);
    t.concatenate(positionTxf);
    
    // First paint shape in white on a black background
    BufferedImage graphic = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = graphic.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, bounds.width, bounds.height);
    g2d.transform(t);
    g2d.setPaint(Color.white);
    g2d.fill(shape);
		
    // Now, blur the texture
    Kernel gaussianKernel = new GaussianKernel(blurRadius); 
    ConvolveOp blurOp = new ConvolveOp(gaussianKernel,
				       ConvolveOp.EDGE_NO_OP,
				       null);
    BufferedImage texture = blurOp.filter(graphic, null);

    g2d.dispose();

    // Clip : use the graphic again
    g2d = graphic.createGraphics();
    g2d.setComposite(AlphaComposite.Clear);
    g2d.fillRect(0, 0, bounds.width, bounds.height);
    g2d.transform(t);
    g2d.setPaint(Color.white);
    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fill(shape);
    g2d.setTransform(new AffineTransform());
    g2d.setComposite(AlphaComposite.SrcIn);
    g2d.drawImage(texture, 0, 0, null);

    g2d.setComposite(AlphaComposite.DstOver);
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, bounds.width, bounds.height);

    // Do not use the margin we added for the blur effect.
    createMap(graphic.getSubimage(blurRadius, blurRadius, bounds.width-2*blurRadius, bounds.height-2*blurRadius), 
	      whiteIsHigh, heightScale);  

    setAnchorPoint(new Point(mapBounds.x, mapBounds.y));
  }

  /**
   * Creates an gray scale elevation map from a Shape. The Shape is painted in white on
   * a black background, then blurred and cliped. The resulting image can be used
   * as a elevation map. With this method, the map size is that of the 
   * Shape's bounds.
   *
   * @param shape the Shape from which the elevation map is created
   * @param blurRadius size of the gaussian blur that is applied to create the
   *        elevation levels.
   */
  public static BufferedImage makeTexture(Shape shape, int blurRadius){
    return makeTexture(shape, shape.getBounds(), Anchor.CENTER, 0, 0, blurRadius);
  }

  /**
   * Creates an elevation map from a Shape. The Shape is painted in white on
   * a black background, then blurred and cliped. The resulting image can be used
   * as a elevation map. 
   *
   * @param shape the Shape from which the elevation map is created
   * @param mapBounds defines the map extent. Shape is positioned relative to the
   *        mapBounds, according to the anchor value
   * @param anchor defines the shape's location relative to the mapBounds.
   * @param hAdjust x axis anchor adjustment
   * @param vAdjust y axis anchor adjustment
   * @param blurRadius size of the gaussian blur that is applied to create the
   *        elevation levels.
   * @param whiteIsHigh if true, the brighter the pixel, the higher
   *        the elevation. Otherwise, the reverse is true.
   * @param heightScale Elevation scale factor, when using Math.sin to
   *        smooth curves. The higher the scale, the steeper the 'slopes'
   */
  public static BufferedImage makeTexture(Shape shape, Rectangle mapBounds, Anchor anchor, float hAdjust, float vAdjust, 
					  int blurRadius){
    // We are going to position shape relative to a bounding rectangle
    // starting in (0, 0), like the BufferedImage where is will be painted.
    Rectangle bounds = (Rectangle)mapBounds.clone();
    bounds.x = 0;
    bounds.y = 0;
    Position position = new Position(anchor, hAdjust, vAdjust);
    AffineTransform positionTxf = position.getTransform(shape.getBounds(), bounds);

    // Extend the bounds to account for the blur radius
    bounds.width += 2*blurRadius;
    bounds.height += 2*blurRadius;

    // Take extra blurRadius into account
    AffineTransform t = AffineTransform.getTranslateInstance(blurRadius, blurRadius);
    t.concatenate(positionTxf);
    
    // First paint shape in white on a black background
    BufferedImage graphic = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = graphic.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, bounds.width, bounds.height);
    g2d.transform(t);
    g2d.setPaint(Color.white);
    g2d.fill(shape);
		
    // Now, blur the texture
    Kernel gaussianKernel = new GaussianKernel(blurRadius); 
    ConvolveOp blurOp = new ConvolveOp(gaussianKernel,
				       ConvolveOp.EDGE_NO_OP,
				       null);
    BufferedImage texture = blurOp.filter(graphic, null);

    g2d.dispose();

    // Clip : use the graphic again
    g2d = graphic.createGraphics();
    g2d.setComposite(AlphaComposite.Clear);
    g2d.fillRect(0, 0, bounds.width, bounds.height);
    g2d.transform(t);
    g2d.setPaint(Color.white);
    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fill(shape);
    g2d.setTransform(new AffineTransform());
    g2d.setComposite(AlphaComposite.SrcIn);
    g2d.drawImage(texture, 0, 0, null);

    g2d.setComposite(AlphaComposite.DstOver);
    g2d.setPaint(Color.black);
    g2d.fillRect(0, 0, bounds.width, bounds.height);

    // Do not use the margin we added for the blur effect.
    return graphic.getSubimage(blurRadius, blurRadius, 
			       bounds.width-2*blurRadius, bounds.height-2*blurRadius);
  }

  /** 
   * @param texture The pixels brightness is used as the elevation
   * @param whiteIsHigh if true, the brighter the pixel, the higher
   *        the elevation. Otherwise, the reverse is true.
   * @param heightScale Elevation scale factor, when using Math.sin to
   *        smooth curves. The higher the scale, the steeper the 'slopes'
   */
  public ElevationMap(BufferedImage texture, boolean whiteIsHigh, 
		      int heightScale){
    createMap(texture, whiteIsHigh, heightScale);
  }

  /**
   * Sets where the map starts on the surface
   * Remember that the map is cyclic and will repeat along the surface
   */
  public void setAnchorPoint(Point anchor){
    anchor.x %= w;
    anchor.y %= h;

    if(anchor.x>0)
      anchorX = anchor.x - w;
    else
      anchorX = anchor.x;

    if(anchor.y>0)
      anchorY = anchor.y - h;
    else
      anchorY = anchor.y;
  }

  /**
   * @return the map's anchor point
   */
  public Point getAnchorPoint(){
    return new Point(anchorX, anchorY);
  }

  /** 
   * @param texture The pixels brightness is used as the elevation
   * @param whiteIsHigh if true, the brighter the pixel, the higher
   *        the elevation. Otherwise, the reverse is true.
   * @param heightScale Elevation scale factor, when using Math.sin to
   *        smooth curves. The higher the scale, the steeper the 'slopes'
   */
  private final void createMap(BufferedImage texture, boolean whiteIsHigh,
			 int heightScale){
    w = texture.getWidth();
    h = texture.getHeight();
    int p = 0;
    double N = 0.;

    if(texture.getType() != BufferedImage.TYPE_BYTE_GRAY){
      BufferedImage tmp = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D g = tmp.createGraphics();
      g.drawImage(texture, 0, 0, null);
      texture = tmp;
    }

    byte e[] = ((DataBufferByte)texture.getRaster().getDataBuffer()).getData();

    // Create normal vectors
    // int p = 0;
    // double N = 0.;
    int sign = whiteIsHigh?1:-1;
    int gx = 0, gy = 0, sqr = 0, s=sign*heightScale;
    normal = new int[w*h][3];
    int c = 255*255, c2 = 4*c;

    //
    // Process first line
    //

    // First point
    p=0;
    gx = s*((e[1]&0xff)-(e[0]&0xff));
    gy = s*((e[w]&0xff)-(e[0]&0xff));
    sqr = gx*gx + gy*gy + c;
    N = Math.sqrt(sqr);
    normal[p][0] = (int)(-NORM*gx/N);
    normal[p][1] = (int)(-NORM*gy/N);
    normal[p][2] = (int)(NORM*255/N);
    p++;

    // first line bulk
    for(int j=1; j<w-1; j++){
      gx = s*((e[p+1]&0xff) - (e[p-1]&0xff));
      gy = 2*s*((e[p+w]&0xff) - (e[p]&0xff));
      sqr = gx*gx + gy*gy + c2;
      N = Math.sqrt(sqr);
      normal[p][0] = (int)(-NORM*gx/N);
      normal[p][1] = (int)(-NORM*gy/N);
      normal[p][2] = (int)(NORM*510/N);
      p++;
    }

    // First line last point
    gx = s*((e[p]&0xff) - (e[p-1]&0xff));
    gy = s*((e[p+w]&0xff) - (e[p]&0xff));
    sqr = gx*gx + gy*gy + c;
    N = Math.sqrt(sqr);
    normal[p][0] = (int)(-NORM*gx/N);
    normal[p][1] = (int)(-NORM*gy/N);
    normal[p][2] = (int)(NORM*255/N);
    p++;

    //
    // Process bulk
    //
    for(int i=1; i<h-1; i++){
      // First point
      gx = 2*s*((e[p+1]&0xff)-(e[p]&0xff));
      gy = s*((e[p+w]&0xff)-(e[p-w]&0xff));
      sqr = gx*gx + gy*gy + c2;
      N = Math.sqrt(sqr);
      normal[p][0] = (int)(-NORM*gx/N);
      normal[p][1] = (int)(-NORM*gy/N);
      normal[p][2] = (int)(NORM*510/N);
      p++;
      
      for(int j=1; j<w-1; j++){
	gx = s*((e[p+1]&0xff) - (e[p-1]&0xff));
	gy = s*((e[p+w]&0xff) - (e[p-w]&0xff));
	sqr = gx*gx + gy*gy + c2;
	N = Math.sqrt(sqr);
	normal[p][0] = (int)(-NORM*gx/N);
	normal[p][1] = (int)(-NORM*gy/N);
	normal[p][2] = (int)(NORM*510/N);
	p++;
      }

      // Last point in line
      gx = 2*s*((e[p]&0xff) - (e[p-1]&0xff));
      gy = s*((e[p+w]&0xff) - (e[p-w]&0xff));
      sqr = gx*gx + gy*gy + c2;
      N = Math.sqrt(sqr);
      normal[p][0] = (int)(-NORM*gx/N);
      normal[p][1] = (int)(-NORM*gy/N);
      normal[p][2] = (int)(NORM*510/N);
      p++;
    }

    // 
    // Process last line
    // 

    // First point on last line
    gx = s*((e[p+1]&0xff) - (e[p]&0xff));
    gy = s*((e[p]&0xff) - (e[p-w]&0xff));
    sqr = gx*gx + gy*gy + c;
    N = Math.sqrt(sqr);
    normal[p][0] = (int)(-NORM*gx/N);
    normal[p][1] = (int)(-NORM*gy/N);
    normal[p][2] = (int)(NORM*255/N);
    p++;

    // Last line bulk
    for(int j=1; j<w-1; j++){
      gx = s*((e[p+1]&0xff) - (e[p-1]&0xff));
      gy = 2*s*((e[p]&0xff) - (e[p-w]&0xff));
      sqr = gx*gx + gy*gy + c2;
      N = Math.sqrt(sqr);
      normal[p][0] = (int)(-NORM*gx/N);
      normal[p][1] = (int)(-NORM*gy/N);
      normal[p][2] = (int)(NORM*510/N);
      p++;
    }

    // Last line last point
    gx = s*((e[p]&0xff) - (e[p-1]&0xff));
    gy = s*((e[p]&0xff) - (e[p-w]&0xff));
    sqr = gx*gx + gy*gy + c;
    N = Math.sqrt(sqr);
    normal[p][0] = (int)(-NORM*gx/N);
    normal[p][1] = (int)(-NORM*gy/N);
    normal[p][2] = (int)(NORM*255/N);

    //
    // Copy into int array
    //
    /*System.out.println("Copying elevation map into int array");
    int length = normal.length;
    this.normal = new int[normal.length][3];
    for(int i=0; i<length; i++){
      this.normal[i][0] = (int)(normal[i][0]*NORM);
      this.normal[i][1] = (int)(normal[i][1]*NORM);
      this.normal[i][2] = (int)(normal[i][2]*NORM);
    }*/
    
  }

  /**
   * @return x, y and z components of the surface normal in (x,y)
   */
  public final int[] getNormal(int x, int y){
    x = (x-anchorX)%w;
    y = (y-anchorY)%h;
    int p = y*w + x;
    return normal[p];
  }

  /*
   * Unit test
   */
  static final String USAGE = "java com.sun.glf.goodies.ElevationMap <imageName>|<string>";
  
  public static final void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    String arg = args[0];
    File file = new File(arg);
    if(file.exists()){ 
      // The argument is a file name. We assume that
      // it is an image. Try to load it.
      Button cmp = new Button("");
      MediaTracker tracker = new MediaTracker(cmp);
      Image image = Toolkit.getDefaultToolkit().getImage(arg);
      tracker.addImage(image, 0);
      try{
	tracker.waitForAll();
      }catch(InterruptedException e){
	throw new Error();
      }finally{
	if(tracker.isErrorAny())
	  throw new Error("Could not load " + arg + ". Check it is an image file");
      }
      
     int w = image.getWidth(null);
     int h = image.getHeight(null);
     BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
     Graphics2D g = buffer.createGraphics();
     g.drawImage(buffer, 0, 0, null);
     
     System.out.println("\nCreating elevation map from image. Size(" + w + ", " + h + ")");
     long curTime = System.currentTimeMillis();
     ElevationMap map = new ElevationMap(buffer, true, 10);
     System.out.println("Processing took: " + (System.currentTimeMillis()-curTime) + " ms");
    }
    
    else{
      // The argument is not a file name. Assume it is a string 
      // we want to use creating an elevation map.
      String fontName = "Times New Roman Bold";
      int fontSize = 200;
      Font font = new Font(fontName, Font.PLAIN, fontSize);
      Shape shape = TextLayer.makeTextBlock(args[0], font, 400, TextAlignment.CENTER);

      // Move text to upper left 0, 0
      Rectangle bounds = shape.getBounds();
      int w = bounds.width;
      int h = bounds.height;
      AffineTransform t = AffineTransform.getTranslateInstance(-bounds.x, -bounds.y);
      shape = (GeneralPath)t.createTransformedShape(shape);

      System.out.println("\nCreating elevation map from shape");
      long curTime = System.currentTimeMillis();
      ElevationMap map = new ElevationMap(shape, 10, true, 10);
      System.out.println("Processing took: " + (System.currentTimeMillis()-curTime) + " ms");   
    }

    System.exit(0);
  }
}

