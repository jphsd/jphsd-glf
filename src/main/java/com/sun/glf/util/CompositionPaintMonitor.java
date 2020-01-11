/*
 * @(#)CompositionPaintMonitor.java
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
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;

import com.sun.glf.util.*;
import com.sun.glf.*;

/**
 * Provides a simple GUI displaying progress of a Composition's paint
 * operation.
 *
 * @author         Vincent Hardy
 * @version        1.0, 12/06/1998
 * @see            com.sun.glf.util.CompositionStudio
 */
public class CompositionPaintMonitor extends GridBagPanel implements PaintProgressListener{
  //
  // Messages
  //
  static final String PAINT_STARTED = "Paint started";
  static final String PAINT_FINISHED = "Paint finished";

  /** 
   * Progress bar showing currently monitored Composition's paint progress
   */
  private JProgressBar progressBar;

  /*
   * Currently observed Composition
   */
  private Composition monitored;

  /**
   * Keeps a log of time it took for layers to paint
   */
  private Vector paintLog;

  /**
   * Used to time paint steps
   */
  private TimeProbe probe = new TimeProbe();

  /**
   * Description of the current step 
   */
  private String stepDescription;

  /**
   * Builds the monitor's user interface
   */
  public CompositionPaintMonitor(){
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true); // Displays the progress value in the bar.
    add(progressBar, 0, 0, 1, 1, CENTER, HORIZONTAL, 0, 0);
  }

  /**
   * @param cmp Composition whose paint progress should be monitored
   */
  public void monitor(Composition monitored){
    if(this.monitored!=null)
      this.monitored.removePaintProgressListener(this);

    this.monitored = monitored;
    if(this.monitored!=null)
      this.monitored.addPaintProgressListener(this);
    paintLog = new Vector();

    progressBar.setEnabled(false);
    repaint();
  }

  /**
   * Called when paint operation started.
   * @param paintSteps describes the number of paint steps in the begining paint operation.
   */
  public void paintStarted(Composition cmp, int paintSteps){
    progressBar.setValue(0);
    progressBar.setMinimum(0);
    progressBar.setMaximum(paintSteps);
    progressBar.setEnabled(true);
    progressBar.invalidate();
    validate();
  }

  /**
   * Called when a paint step completes
   * @param describes the step which is about to start.
   */
  public void paintStepStarted(Composition cmp, String stepDescription){
    if(progressBar.getValue()>0)
      paintLog.addElement(probe.traceTime("" + progressBar.getValue() + " (" + this.stepDescription + ") : "));
    
    this.stepDescription = stepDescription;
    progressBar.setValue(progressBar.getValue() + 1);
    progressBar.invalidate();
    validate();
    probe.startTiming();
  }

  /**
   * Called when paint operation terminates. Note that this may be called before all the
   * steps are completed, for example if an exception occurs during the paint process.
   */
  public void paintFinished(Composition cmp){
    paintLog.addElement(probe.traceTime("" + progressBar.getValue()  + " (" + this.stepDescription + " : " ));
    progressBar.setValue(progressBar.getMaximum());
    progressBar.invalidate();
    validate();

    // Dump log
    boolean dumpLog = System.getProperty("com.sun.glf.debug", "false").equalsIgnoreCase("true");
    if(dumpLog){
      int n = paintLog.size();
      for(int i=0; i<n; i++)
	System.out.println(paintLog.elementAt(i));
      
      System.out.println("Paint finished...");
    }
  }

}
