/*
 * @(#)LitSurface.java
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

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.JFrame;
import javax.swing.JComponent;

import com.sun.glf.*;
import com.sun.glf.util.*;
import com.sun.glf.util.*;

/**
 * A LitSurface describes a 2D plane lit by any number of either
 * DirectionalLights or SpotLights. Optionally, a LitSurface can
 * have an ElevationMap in case it is not flat.
 * <p>
 * A LitSurface is able to process the light intensity in any point
 * of a surface, taking the diffferent lights and elevation map into 
 * account.
 * <p>
 * A LitSurface is assumed to be of a homogeneous material, therefore
 * it is associated with material quality factors, such as the ambiant
 * light (which depends on the ambiant light diffusion factor), the 
 * diffusion and specular reflection factors.
 *
 * @author          Vincent Hardy
 * @version         1.0, 09/27/1998
 * @see             com.sun.glf.goodies.SpotLight
 * @see             com.sun.glf.goodies.DirectionalLight
 */
public final class LitSurface {
  static final String ERROR_INVALID_AMBIANT_LIGHT = "Ambiant light should be positive";
  static final String ERROR_INVALID_DIFFUSION_FACTOR = "Diffusion factor should be between 0 and 1";
  static final String ERROR_INVALID_SPECULAR_FACTOR = "Specular factor should be between 0 and 1";
  static final String ERROR_INVALID_SPECULAR_EXPONENT = "Specular exponent should be two or more";

  //
  // Surface material description
  //

  /** 
   * Ambiant light. Note that the ambiant light is supposed
   * to be white, i.e. it is uniform in all color directions.
   */
  private double Ia = 0.2;

  /** Specular light reflection factor 0-1 */
  double ks = 1;

  /** Diffusion light reflection factor 0-1*/
  double kd = 1;

  /** Specular reflection exponent 2-infinity. */
  int ns = 2;

  //
  // Optional ElevationMap
  //
  
  /** Should be used for texturizing */
  ElevationMap elevationMap;

  //
  // Lights
  //

  /** Array of SpotLights */
  private SpotLight warnLights[];

  /** Array of DirectionalLights */
  private DirectionalLight dirLights[];

  //
  // Implementation
  //
  
  /** Overall directional light intensity vector */
  private double IDir[] = new double[3];

  /** true if an elevationMap is used */
  private boolean textured;

  /**
   * @param Ia ambiant light constant. 
   */
  public LitSurface(double Ia){
    this(Ia, 1, 1, 2, null);
  }

  /**
   * @param Ia ambiant light constant. 
   * @param kd diffusion coefficient 0-1
   * @param ks specular reflection coefficient 0-1 
   * @param ns specular reflection exponent. The higher the value, 
   *        the shinier surface (i.e. metalic). Greater than 2.
   * @param elevationMap defines height of the surface points
   */
  public LitSurface(double Ia, double kd, double ks, int ns,
		    ElevationMap elevationMap){
    setIa(Ia);
    setKd(kd);
    setKs(ks);
    setNs(ns);
    setElevationMap(elevationMap);
  }

  public double getIa(){
    return Ia;
  }

  public void setIa(double Ia){
    if(Ia<0)
      throw new IllegalArgumentException(ERROR_INVALID_AMBIANT_LIGHT);
    this.Ia = Ia;
  }

  public double getKd(){
    return kd;
  }

  public void setKd(double kd){
    if(kd<0 || kd>1)
      throw new IllegalArgumentException(ERROR_INVALID_DIFFUSION_FACTOR);
    this.kd = kd;
  }

  public double getKs(){
    return ks;
  }

  public void setKs(double ks){
    if(ks<0 || ks>1)
      throw new IllegalArgumentException(ERROR_INVALID_SPECULAR_FACTOR);
    this.ks = ks;
  }

  public int getNs(){
    return ns;
  }

  public void setNs(int ns){
    if(ns < 2)
      throw new IllegalArgumentException(ERROR_INVALID_SPECULAR_EXPONENT);
    this.ns = ns;
  }

  /**
   * @param Ia ambiant light
   * @param type surface type
   * @see com.sun.glf.goodies.LitSurfaceType
   */
  public LitSurface(double Ia, LitSurfaceType type, ElevationMap map){
    if(Ia<0 || type==null)
      throw new IllegalArgumentException();

    this.Ia = Ia;
    this.elevationMap = map;
    if(elevationMap!=null)
      textured = true;

    kd = type.getKd();
    ks = type.getKs();
    ns = type.getNs();
  }

  /**
   * @param elevationMap the new elevationMap which this surface should use
   */
  public void setElevationMap(ElevationMap elevationMap){
    this.elevationMap = elevationMap;
    textured = (elevationMap!=null);
  }


  /**
   * @return true if this surface has an associated ElevationMap.
   */
  public boolean isTextured(){
    return elevationMap!=null;
  }

  /**
   * @param light new DirectionalLight to be added on the surface
   */
  public void addLight(DirectionalLight light){
    if(light == null)
      throw new IllegalArgumentException();

    if(dirLights!=null){
      DirectionalLight tmp[] = new DirectionalLight[dirLights.length+1];
      System.arraycopy(dirLights, 0, tmp, 0, dirLights.length);
      dirLights = tmp;
    }
    else
      dirLights = new DirectionalLight[1];

    dirLights[dirLights.length-1] = light;
    double Il[] = new double[3];
    light.getIntensity(this, Il);
    IDir[0] += Il[0];
    IDir[1] += Il[1];
    IDir[2] += Il[2];
  }

  /**
   * @param light new SpotLight to be added on the surface
   */
  public void addLight(SpotLight light){
    if(light == null)
      throw new IllegalArgumentException();

    if(warnLights != null){
      SpotLight tmp[] = new SpotLight[warnLights.length+1];
      System.arraycopy(warnLights, 0, tmp, 0, warnLights.length);
      warnLights = tmp;
    }
    else
      warnLights = new SpotLight[1];

    warnLights[warnLights.length-1] = light;
  }

  /**
   * @param lights new SpotLights to be added on the surface
   */
  public void addLights(SpotLight lights[]){
    if(lights == null)
      throw new IllegalArgumentException();

    int n = warnLights!=null?warnLights.length:0;

    if(warnLights != null){
      SpotLight tmp[] = new SpotLight[warnLights.length+lights.length];
      System.arraycopy(warnLights, 0, tmp, 0, warnLights.length);
      warnLights = tmp;
    }
    else
      warnLights = new SpotLight[lights.length];

    System.arraycopy(lights, 0, warnLights, n, lights.length);
  }

  /**
   * Removes all lights for the surface 
   */
  public void removeAllLights(){
    warnLights = null;
    dirLights = null;
    IDir[0] = 0;
    IDir[1] = 0;
    IDir[2] = 0;
  }

  /**
   * Processed intensity on surface, not using any texture map.
   *
   * @param x the x-axis coordinate of the point
   * @param y the y-axis coordinate of the point
   * @param I the intensity array into which result is stored. Should 
   *        have length 3 at least. Intensity are stored in Red, Green,
   *        Blue order.
   * @param Il working intensity array
   */
  public final void getFlatIntensity(final int x, final int y, final double I[],
				 final double Il[]){
    // Reset intensity vector
    I[0] = Ia;
    I[1] = Ia;
    I[2] = Ia;

    // Take SpotLights into account
    int nWarns = warnLights!=null?warnLights.length:0;
    for(int i=0; i<nWarns; i++){
      warnLights[i].getFlatIntensity(this, x, y, Il);
      I[0] += Il[0];
      I[1] += Il[1];
      I[2] += Il[2];
    }

    // Take DirectionalLights into account
    // This is a flat surface (i.e. no elevation map)
    // so the total directional light contribution
    // is constant over the surface. This constant has
    // been preprocessed into IDir.
    I[0] += IDir[0];
    I[1] += IDir[1];
    I[2] += IDir[2];
  }

  /**
   * Processed intensity on surface, assuming there is an elevation map.
   * If there is no elevation map, a NullPointerException will be thrown.
   *
   * @param x the x-axis coordinate of the point
   * @param y the y-axis coordinate of the point
   * @param I the intensity array into which result is stored. Should 
   *        have length 3 at least. Intensity are stored in Red, Green,
   *        Blue order.
   * @param Il working intensity array
   */
  public final void getTexturedIntensity(final int x, final int y, final double I[],
					 final double Il[]){
    // Reset intensity vector
    I[0] = Ia;
    I[1] = Ia;
    I[2] = Ia;

    // Take SpotLights into account
    int nWarns = warnLights!=null?warnLights.length:0;
    for(int i=0; i<nWarns; i++){
      warnLights[i].getTexturedIntensity(this, x, y, Il);
      I[0] += Il[0];
      I[1] += Il[1];
      I[2] += Il[2];
    }

    // Take DirectionalLights into account
    int nDirs = dirLights!=null?dirLights.length:0;
    for(int i=0; i<nDirs; i++){
      dirLights[i].getTexturedIntensity(this, x, y, Il);
      I[0] += Il[0];
      I[1] += Il[1];
      I[2] += Il[2];
    }
  }

  //
  // Unit testing
  //
  static final String USAGE = "java com.sun.glf.goodies.LitSurface <textureFile>. Test Surface types";
  public static void main(String args[]){
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    String textureFileName = args[0];

    JFrame frame = new JFrame("LitSurface unit testing");
    frame.getContentPane().setLayout(new GridLayout(0, 1));
    Component dc = new Component(){};

    BufferedImage textureImage = Toolbox.loadImage(textureFileName, BufferedImage.TYPE_INT_RGB);
    ElevationMap textureMap = new ElevationMap(textureImage, true, 10);

    // Create a base image for filtering
    int w = textureImage.getWidth();
    int h = textureImage.getHeight();
    BufferedImage src = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = src.createGraphics();
    g.setPaint(new Color(153, 153, 204));
    g.fillRect(0, 0, w, h);

    //
    // Create one surface of each type
    // 
    LitSurface normalSurface = new LitSurface(.2, LitSurfaceType.NORMAL, textureMap);
    LitSurface mattSurface = new LitSurface(.2, LitSurfaceType.MATTE, textureMap);
    LitSurface shinySurface = new LitSurface(.2, LitSurfaceType.SHINY, textureMap);
    LitSurface metallicSurface = new LitSurface(.2, LitSurfaceType.METALLIC, textureMap);

    // Add a directional light for each surface type
    normalSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, 1, Color.white));
    mattSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, 1, Color.white));
    shinySurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, 1, Color.white));
    metallicSurface.addLight(new DirectionalLight(new double[]{-40, -40, 40}, 1, Color.white));
    
    // Create a surface of each type
    long start = System.currentTimeMillis();
    BufferedImage normal = (new LightOp(normalSurface)).filter(src, null);
    System.out.println("Surface processing took : " + (System.currentTimeMillis()-start));

    start = System.currentTimeMillis();
    BufferedImage matt = (new LightOp(mattSurface)).filter(src, null);
    System.out.println("Surface processing took : " + (System.currentTimeMillis()-start));

    start = System.currentTimeMillis();
    BufferedImage shiny = (new LightOp(shinySurface)).filter(src, null);
    System.out.println("Surface processing took : " + (System.currentTimeMillis()-start));

    start = System.currentTimeMillis();
    BufferedImage metallic = (new LightOp(metallicSurface)).filter(src, null);
    System.out.println("Surface processing took : " + (System.currentTimeMillis()-start));

    frame.getContentPane().add(makeNewComponent(normal, "Default surface settings"));
    frame.getContentPane().add(makeNewComponent(matt, "Matt surface"));
    frame.getContentPane().add(makeNewComponent(shiny, "Shiny surface"));
    frame.getContentPane().add(makeNewComponent(metallic, "Metallic surface"));

    frame.pack();
    frame.setVisible(true);

  }

  private static JComponent makeNewComponent(Image image, String toolTip){
    Dimension dim = new Dimension(image.getWidth(null),
				  image.getHeight(null));

    LayerComposition cmp = new LayerComposition(dim);
    ImageLayer layer = new ImageLayer(cmp, image, null);
    cmp.setLayers(new Layer[]{layer});

    CompositionComponent comp = new CompositionComponent(cmp);
    comp.setToolTipText(toolTip);
    return comp;
  }
}

