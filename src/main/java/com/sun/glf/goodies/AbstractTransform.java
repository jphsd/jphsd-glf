/*
 * @(#)AbstractTransform.java
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
 * Generalizes the notion of Transformation to non affine geometrical
 * transformations. This interface allows for the definition of any arbitrary
 * type of transformation.
 *
 * @author Vincent Hardy
 * @version 1.0, 09.11.1998
 */
public abstract class AbstractTransform implements Transform{
  /** 
   * Transform to apply before this one
   */
  Transform preTransform;

  /**
   * Transform to apply after this one
   */
  Transform postTransform;

  /**
   * Concatenates t with this transform. In effect, t will be 
   * applied first, then this transform.
   *
   * @param transform transform to concatenate with this one.
   * @see #preConcatenate
   */
  final public void concatenate(Transform t){
    if(preTransform==null)
      preTransform = t;
    else
      preTransform.concatenate(t);
  }

  /**
   * Preconcatenates t with this transform. In effect, t will be applied
   * after this transform is applied.
   *
   * @param t transform to preconcatenate.
   * @see #concatenate
   */
  final public void preConcatenate(Transform t){
    if(postTransform==null)
      postTransform = t;
    else
      postTransform.preConcatenate(t);
  }

  /**
   * transform implementation. Takes care of applying any pre or post
   * transform if needed. 
   * @see #transformImpl
   */
  final public Shape transform(Shape shape){
    if(preTransform!=null)
      shape = preTransform.transform(shape);
    
    shape = transformImpl(shape);

    if(postTransform!=null)
      shape = postTransform.transform(shape);

    return shape;
  }

  /**
   * @param shape Shape to be transformed, excluding any pre or post transformation.
   * @see #transform
   */
  public abstract Shape transformImpl(Shape shape);
}
