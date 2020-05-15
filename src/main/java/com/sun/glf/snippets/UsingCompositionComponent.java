/*
 * @(#)UsingCompositionComponent.java
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
import javax.swing.*;

import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Illustrates the use of the CompositionComponent class
 *
 * @author            Vincent Hardy
 * @version           1.0, 03/17/1999
 */
public class UsingCompositionComponent {
  public static void main(String args[]){
    // Reuse our HelloLayers example to build a Composition
    HelloLayers helloLayers = new HelloLayers();
    Composition composition = helloLayers.build();

    // Create a CompositionComponents
    CompositionComponent helloA = new CompositionComponent(composition);
    CompositionComponent helloB = new CompositionComponent(composition, true, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    // Create a JFrame to display CompositionComponents
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new GridLayout(1,2));
    frame.getContentPane().add(helloA);
    frame.getContentPane().add(helloB);
    frame.getContentPane().setBackground(Color.white);
    frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    CompositionFrame cmpFrame = new CompositionFrame("Using CompositionFrame");
    cmpFrame.load(helloLayers);
    cmpFrame.setVisible(true);
  }
}
