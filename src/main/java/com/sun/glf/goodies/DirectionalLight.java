/*
 * @(#)DirectionalLight.java
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

/**
 * Defines the characteristics of a DirectionalLight
 *
 * @author           Vincent Hardy
 * @version          1.0, 09/27/1998
 * @see              com.sun.glf.goodies.LitSurface
 */
public class DirectionalLight{
  //
  // Light definition
  //

  /** Light intensity. One component for Red, Green and Blue */
  private double Il[] = new double[3];

  /*
   * Light vector 
   */
  private double Lx, Ly, Lz;

  /**
   * @param L light vector
   * @param I light intensity
   * @param color light color
   */
  public DirectionalLight(double L[], double I, Color color){
    if(I<0)
      throw new IllegalArgumentException("Negative intensity not allowed");

    if(L==null || L.length!=3)
      throw new IllegalArgumentException();

    double N = Math.sqrt(L[0]*L[0] + L[1]*L[1] + L[2]*L[2]);
    if(N==0.)
      throw new IllegalArgumentException();

    Lx = L[0]/N;
    Ly = L[1]/N;
    Lz = L[2]/N;

    Il[0] = I*(color.getRed()/255.);
    Il[1] = I*(color.getGreen()/255.);
    Il[2] = I*(color.getBlue()/255.);
  }

  /**
   * @param s the lit surface.
   * @param I the intensity vector into which the light at point (x,y) is returned.
   * @param x x coordinate of surface point
   * @param y y coordinate of surface point
   */
  public final void getTexturedIntensity(final LitSurface s, final int x, final int y, final double I[]){
    // Surface normal in (x,y)
    final int N[] = s.elevationMap.getNormal(x, y);

    // Light is attenuated by angle with surface
    double NxL = (N[0]*Lx + N[1]*Ly + N[2]*Lz)/ElevationMap.NORM;
    NxL = NxL>0?NxL:0;

    // General intensity variation
    final double Ivar = s.kd*NxL +  s.ks*Math.pow(NxL, s.ns);

    // RGB variations
    I[0] = Il[0]*Ivar; // Red
    I[1] = Il[1]*Ivar; // Green
    I[2] = Il[2]*Ivar; // Blue
  }

  /**
   * Used  on 'flat' surfaces, i.e. when no elevationMap is used.
   * @param I the intensity vector into which the light intensities are stored
   */
  public void getIntensity(LitSurface s, double I[]){
    // Light is attenuated by angle with surface
    double NxL = Lz;

    // General intensity variation
    double Ivar = Math.max(0, s.kd*NxL) +  Math.max(0, s.ks*Math.pow(NxL, s.ns));

    // RGB variations
    I[0] = Il[0]*Ivar; // Red
    I[1] = Il[1]*Ivar; // Green
    I[2] = Il[2]*Ivar; // Blue
  }

}
