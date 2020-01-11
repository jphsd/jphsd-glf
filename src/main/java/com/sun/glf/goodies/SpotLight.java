/*
 * @(#)SpotLight.java
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
 * Defines the characteristics of a SpotLight. 
 * 
 * @author          Vincent Hardy
 * @version         1.2, 09/27/1998
 * @see             com.sun.glf.goodies.LitSurface
 */
public class SpotLight {
  /** Constant used for the light vector norm, and other integer based calculations */
  static final int LIGHT_PRECISION = 1024;
  // static final double LIGHT_PRECISION_SQUARE = LIGHT_PRECISION*LIGHT_PRECISION;

  /** Constant used in getTexturedLightIntensity */
  // private double LIGHT_PRECISION_NP = 0;

  //
  // Light definition
  // 

  /** 
   * Light intensity. One component for Red, Green and Blue.
   * Default is white light.
   */
  double Iw[] = {1., 1., 1.}; 

  /** 
   * Warn light exponent. The higher the value, the 
   * more focused the light is.
   */
  int np = 4;

  /** Intensity value on the bounding cone's perihelion */
  double Imin = .9;

  /*
   * Light source point 
   */
  int Rx, Ry, Rz;

  /*
   * Light vector
   */
  int Lx, Ly, Lz;

  /** Limiting cone cosine */
  double cosLimit;

  /**
   * 'indirect' light definition, through the ellipse defining
   * the intersection of the minimal light intensity cone and
   * the projection plane.
   *
   * @param a the ellipse's semi-major axis.
   * @param f1 the ellipse's first focal point
   * @param f2 the ellipse's second focal point
   */
  public SpotLight(double a, Point2D f1, Point2D f2){
    processLigthFromEllipse(a, f1, f2, 1., Color.white);
  }

  /**
   * Processes the light position and vector from the ellipse defining
   * the intersection of the minimal light intensity cone and
   * the projection plane.
   *
   * @param a the ellipse's semi-major axis
   * @param f1 the ellipse's first focal point
   * @param f2 the ellipse's second focal point
   * @param I the light's intensity
   * @param color the light's color
   */
  private void processLigthFromEllipse(double a, Point2D f1, Point2D f2, double I, Color color){
    if(I<0)
      throw new IllegalArgumentException("Negative intensity not allowed");
    if(a<=0)
      throw new IllegalArgumentException();

    /*this.f1 = f1; 
    this.f2 = f2; 
    this.a = a;*/

    // Excentricity
    double e = f1.distance(f2)/(2*a);

    // Process light cone width
    double gamma = Math.acos(Imin); 

    // Process light angle with the surface
    double theta = Math.asin(e*Math.cos(gamma));

    // Process light elevation 
    double cosTheta = Math.cos(theta);
    double sinTheta = Math.sin(theta);
    double tanTheta = Math.tan(theta);
    double cosGamma = Math.cos(gamma);
    double sinGamma = Math.sin(gamma);
    double tanGamma = Math.tan(gamma);
    double cosThetaSq = cosTheta*cosTheta;
    double sinThetaSq = sinTheta*sinTheta;
    double cosGammaSq = cosGamma*cosGamma;
    double sinGammaSq = sinGamma*sinGamma;
    double tanGammaSq = tanGamma*tanGamma;
    double limit = gamma;
    this.cosLimit = Math.cos(limit);

    // 
    // Light elevation
    //
    double D = a*(cosThetaSq - tanGammaSq*sinThetaSq)/tanGamma;

    //
    // Now, process light position
    //

    // Ellipse center : middle of (f1, f2)
    double cx = (f1.getX() + f2.getX())/2.;
    double cy = (f1.getY() + f2.getY())/2.;

    // cd is the distance between the center and
    // the light position projection on the ellipse plane
    double d = f1.distance(f2);
    double dx = d!=0?(f1.getX() - f2.getX())/d:0.;
    double dy = d!=0?(f1.getY() - f2.getY())/d:0;
    double cd = (D*cosTheta*sinTheta)/(cosGammaSq*cosThetaSq - sinGammaSq*sinThetaSq);
    
    // Light source point
    Rx = (int)(cx + cd*dx);
    Ry = (int)(cy + cd*dy);
    Rz = (int)D;

    if(Rz==0) Rz = 1;
    
    //
    // Light vector
    //

    double Lx = dx*D*tanTheta;
    double Ly = dy*D*tanTheta;
    double Lz = D;

    // Normalize vector
    double Ln = Math.sqrt(Lx*Lx + Ly*Ly + Lz*Lz);
    Lx /= Ln;
    Ly /= Ln;
    Lz /= Ln;

    this.Lx = (int)(LIGHT_PRECISION*Lx);
    this.Ly = (int)(LIGHT_PRECISION*Ly);
    this.Lz = (int)(LIGHT_PRECISION*Lz);

    // System.out.println("Light vector : " + Lx + "/" + Ly + "/" + Lz);
    // System.out.println("Light source : " + Rx + "/" + Ry + "/" + Rz);
    // System.out.println("Light angle : " + Math.acos(cosTheta)/Math.PI);

    Iw[0] = I*color.getRed()/255.;
    Iw[1] = I*color.getGreen()/255.;
    Iw[2] = I*color.getBlue()/255.;
  }

  /**
   * 'indirect' light definition, through the bounding box 
   * of the  intersection of the projection plan and the light 
   * 'minimal intensity cone'. Note that this is limited to 
   * vertical or horizontal ellipses.
   * 
   * @param rect the light's bounding ellipse
   */
  public SpotLight(Rectangle2D boundingEllipse){
    this(boundingEllipse, Color.white, 1., 0.);
  }

  /**
   * 'indirect' light definition, through the bounding box 
   * of the  intersection of the projection plan and the light 
   * 'minimal intensity cone'. Note that this is limited to 
   * vertical or horizontal ellipses.
   * 
   * @param rect the light's bounding ellipse
   * @param angle the angle, in radians, by which the light should be rotated
   */
  public SpotLight(Rectangle2D boundingEllipse, double angle){
    this(boundingEllipse, Color.white, 1., angle);
  }

  /**
   * 'indirect' light definition, through the bounding box 
   * of the  intersection of the projection plan and the light 
   * 'minimal intensity cone'. Note that this is limited to 
   * vertical or horizontal ellipses.
   * 
   * @param rect the light's bounding ellipse
   * @param color the light's color
   */
  public SpotLight(Rectangle2D boundingEllipse, Color color){
    this(boundingEllipse, color, 1., 0.);
  }

  /**
   * 'indirect' light definition, through the bounding box 
   * of the  intersection of the projection plan and the light 
   * 'minimal intensity cone'. Note that this is limited to 
   * vertical or horizontal ellipses.
   * 
   * @param rect the light's bounding ellipse
   * @param color the light's color
   * @param I the light's intensity
   * @param angle the angle, in radians, by which the light should be rotated
   */
  public SpotLight(Rectangle2D boundingEllipse, Color color, double I, double angle){
    this(boundingEllipse, color, I, angle, 4);
  }

  /**
   * 'indirect' light definition, through the bounding box 
   * of the  intersection of the projection plan and the light 
   * 'minimal intensity cone'. Note that this is limited to 
   * vertical or horizontal ellipses.
   * 
   * @param rect the light's bounding ellipse
   * @param color the light's color
   * @param I the light's intensity
   * @param angle the angle, in radians, by which the light should be rotated
   * @param np focus factor. Should be greater than one.
   */
  public SpotLight(Rectangle2D boundingEllipse, Color color, double I, double angle, int np){
    if(np<=0)
      throw new IllegalArgumentException("np should be 1 or more");

    this.np = np;
    // this.LIGHT_PRECISION_NP = Math.pow(LIGHT_PRECISION, np);
    boundingEllipse = (Rectangle2D)boundingEllipse.clone();

    double x0 = boundingEllipse.getX();
    double y0 = boundingEllipse.getY();
    double w = boundingEllipse.getWidth();
    double h = boundingEllipse.getHeight();
    boolean horizontal = true;

    // Take care of both directions.
    if(w<h){
      double tmp = w;
      w = h;
      h = tmp;
      horizontal = false;
    }

    // Process ellipse excentricity
    double a = w/2;
    double b = h/2;
    double e = Math.sqrt(1 - (b*b)/(a*a));

    Point2D.Double f1 = new Point2D.Double();
    Point2D.Double f2 = new Point2D.Double();

    if(horizontal){
      f2.x = x0 + a*(1+e);
      f2.y = y0 + b;
      f1.x = x0 + a*(1-e);
      f1.y = y0 + b;
    }
    else{
      f2.y = y0 + a*(1+e);
      f2.x = x0 + b;
      f1.y = y0 + a*(1-e);
      f1.x = x0 + b;
    }

    if(angle!=0.){
      AffineTransform r = null;
      if(horizontal)
	// r = AffineTransform.getRotateInstance(angle, x0+2*a, y0 + b);
	r = AffineTransform.getRotateInstance(angle, x0, y0 + b); // Left anchor
      else
	// r = AffineTransform.getRotateInstance(angle, x0 + b, y0 + 2*a);
	r = AffineTransform.getRotateInstance(angle, x0 + b, y0); // Top anchor

      r.transform(f1, f1);
      r.transform(f2, f2);
    }

    processLigthFromEllipse(a, f1, f2, I, color);
  }

  public int getNp(){
    return np;
  }

  public void setNp(int np){
    if(np<=0)
      throw new IllegalArgumentException();

    this.np = np;
  }

  /**
   * Returns intensity, assuming a flat surface
   *
   * @param s the surface onto which this light is projected
   * @param I the intensity vector into which the light at point (x,y) is returned.
   * @param x x coordinate of surface point
   * @param y y coordinate of surface point
   */
  public final void getFlatIntensity(LitSurface s, int x, int y, double I[]){
    // Reflected light vector
    double RLx = Rx-x;
    double RLy = Ry-y;
    double RLz = Rz;
    double RLn = Math.sqrt(RLx*RLx + RLy*RLy + RLz*RLz);
    RLx /= RLn; RLy /= RLn; RLz /= RLn;

    double cosAngle = (RLx*Lx + RLy*Ly + RLz*Lz)/LIGHT_PRECISION;

    if(cosAngle>cosLimit){ // Angle is less than limiting cone
      // Surface normal in (x,y) is (0, 0, 1)

      // Modulate on the ellipse boundary, so that the intensity
      // smoothly drops to 0.
      double Iatt = cosLimit/(double)cosAngle;
      Iatt *= Iatt;
      Iatt *= Iatt;
      Iatt *= Iatt;
      // Iatt *= Iatt; // akin Math.pow(Iatt, 16);

      // Light intensity depends on angle with light's projection axis
      double Il = Math.pow(cosAngle, np);
      
      Iatt = Il*(1 - Iatt);

      double Idiff = s.kd*RLz;
      Idiff = Idiff>0?Idiff:0;
      double NxRL = RLz/(ElevationMap.NORM);
      double Ispec = s.ks*Math.pow(RLz, s.ns);
      Ispec = Ispec>0?Ispec:0;
      Iatt = Iatt*(Idiff + Ispec);

      I[0] = Iw[0]*Iatt;
      I[1] = Iw[1]*Iatt;
      I[2] = Iw[2]*Iatt;
    }
    else{
      I[0] = 0;
      I[1] = 0;
      I[2] = 0;
    }
  }

  /**
   * Returns intensity, assuming a textured surface
   *
   * @param s the surface onto which this light is projected
   * @param I the intensity vector into which the light at point (x,y) is returned.
   * @param x x coordinate of surface point
   * @param y y coordinate of surface point
   */
  public final void getTexturedIntensity(LitSurface s, int x, int y, double I[]){
    // Reflected light vector
    double RLx = Rx-x;
    double RLy = Ry-y;
    double RLz = Rz;
    double RLn = Math.sqrt(RLx*RLx + RLy*RLy + RLz*RLz);
    RLx /= RLn; RLy /= RLn; RLz /= RLn;

    double cosAngle = (RLx*Lx + RLy*Ly + RLz*Lz)/LIGHT_PRECISION;

    if(cosAngle>cosLimit){ // Angle is less than limiting cone
      // Light intensity depends on angle with light's projection axis
      // Modulate on the ellipse boundary, so that the intensity
      // smoothly drops to 0.
      double Iatt = cosLimit/(double)cosAngle;
      Iatt *= Iatt;
      Iatt *= Iatt;
      Iatt *= Iatt;
      // Iatt *= Iatt; // akin Math.pow(Iatt, 16);
      Iatt = Math.pow(cosAngle, np)*(1 - Iatt);

      // Surface normal in (x,y)
      int N[] = s.elevationMap.getNormal(x, y);

      double NxRL = (N[0]*RLx + N[1]*RLy + N[2]*RLz)/ElevationMap.NORM;
      NxRL = NxRL>0?NxRL:0;

      double Ispec = s.ks*Math.pow(2*N[2]*NxRL/ElevationMap.NORM - RLz, s.ns);
      Ispec = Ispec>0?Ispec:0;

      Iatt = Iatt*(s.kd*NxRL + Ispec);

      I[0] = Iw[0]*Iatt;
      I[1] = Iw[1]*Iatt;
      I[2] = Iw[2]*Iatt;
    }
    else{
      I[0] = 0;
      I[1] = 0;
      I[2] = 0;
    }
  }

  /**
   * Returns intensity, assuming a textured surface
   *
   * @param s the surface onto which this light is projected
   * @param I the intensity vector into which the light at point (x,y) is returned.
   * @param x x coordinate of surface point
   * @param y y coordinate of surface point
   */
  /*
  public final void getTexturedIntensity(LitSurface s, int x, int y, double I[]){
    // Surface normal in (x,y)
    double N[] = s.elevationMap.getNormal(x, y);

    // Reflected light vector
    double RLx = Rx-x;
    double RLy = Ry-y;
    double RLz = Rz;
    double RLnSq = RLx*RLx + RLy*RLy + RLz*RLz;
    double cosAngleRLn = RLx*Lx + RLy*Ly + RLz*Lz;
    // double RLn = Math.sqrt(RLnSq);
    // RLx /= RLn; RLy /= RLn; RLz /= RLn;
    // double cosAngle = RLx*Lx + RLy*Ly + RLz*Lz;

    double r = cosAngleRLn/cosLimit;
    if(r*r > RLnSq){ // Angle is less than limiting cone
      // Light intensity depends on angle with light's projection axis
      // Modulate on the ellipse boundary, so that the intensity
      // smoothly drops to 0.
      double Iatt = 1/r;
      Iatt *= Iatt;    // akin Iatt square
      Iatt *= RLnSq;   // Divide RLnSq out
      Iatt *= Iatt;
      Iatt *= Iatt;
      // Iatt *= Iatt; // akin Math.pow(Iatt, 16);

      // Iatt = Math.pow(cosAngle, np)*(1 - Iatt);
      int n = 4;
      double Itmp = cosAngleRLn*cosAngleRLn; 
      Itmp /= RLnSq;
      while(n<=np){
	Itmp *= Itmp;    // Itmp square
	n <<= 1;         // n square
      }
      Iatt = Itmp*(1-Iatt);

      double NxRLxRLn = N[0]*RLx + N[1]*RLy + N[2]*RLz;
      NxRLxRLn = NxRLxRLn>0?NxRLxRLn:0;

      // double Ispec = s.ks*Math.pow(2*N[2]*NxRL - RLz, s.ns);
      double Ispec = 2*N[2]*NxRLxRLn - RLz;
      Ispec *= Ispec;
      Ispec /= RLnSq;
      n = 4;
      while(n<=s.ns){
	Ispec *= Ispec;  // Ispec square
	n <<= 1;         // n square 
      }
      Ispec = Ispec>0?Ispec:0;

      double NxRLSq = NxRLxRLn*NxRLxRLn/RLnSq;
      Iatt *= (s.kd*Math.sqrt(NxRLSq) + Ispec);

      I[0] = Iw[0]*Iatt;
      I[1] = Iw[1]*Iatt;
      I[2] = Iw[2]*Iatt;
    }
    else{
      I[0] = 0;
      I[1] = 0;
      I[2] = 0;
    }
  }*/

  //
  // Unit testing.
  //

  public static final String USAGE = "java com.sun.glf.goodies.SpotLight x0 y0 w h";
  public static final int N_ARGS = 4;
  public static void main(String args[]){
    // Check we have enough input arguments.
    if(args.length < N_ARGS){
      System.out.println(USAGE);
      System.exit(0);
    }
    
    // Parse input arguments
    int i=0;
    double x0 = Integer.parseInt(args[i++]);
    double y0 = Integer.parseInt(args[i++]);
    double w = Integer.parseInt(args[i++]);
    double h = Integer.parseInt(args[i++]);
    
    System.out.println("=====================================================");
    System.out.println("Bounding ellipse : " + x0 + "/" + y0 + "/" + w + "/" + h);
    System.out.println("=====================================================");
    System.out.println();
    
    Rectangle2D bounds = new Rectangle2D.Double(x0, y0, w, h);

    SpotLight L = new SpotLight(bounds);
  }
}

