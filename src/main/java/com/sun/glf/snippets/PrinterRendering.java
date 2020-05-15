/*
 * @(#)PrinterRendering.java
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
import java.awt.print.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * Demonstrates how to access a Graphics2D that renders to a printer 
 * output device.
 *
 * @author        Vincent Hardy
 * @version       1.0, 12.26.1998
 */
public class PrinterRendering implements Printable{
  /**
   * Delegates rendering to a SimplePainter
   */
  private SimplePainter painter;

  /**
   * @param imageName image the painter should render
   */
  public PrinterRendering(String imageFileName){
    painter = new SimplePainter(imageFileName);
  }

  /**
   * Printable interface implementation
   */
  public int print(Graphics _g, PageFormat pageFormat, int pageIndex){
    if(pageIndex == 0){
      System.out.println("Print called on page 0");
      // Cast to Graphics2D to access Java 2D features
      Graphics2D g = (Graphics2D)_g;

      AffineTransform t = g.getTransform();
      // System.out.println("scaleX = " + t.getScaleX() + " scaleY = " + t.getScaleY());

      //
      // Modify output resolution to 4*72 = 288 dpi
      //
      double xScale = 1/4.; 
      double yScale = 1/4.;
 
      // 
      // Modify user space to device space transform so that output is centered
      //
      double xMargin = (pageFormat.getImageableWidth() - painter.getSize().width*xScale)/2;
      double yMargin = (pageFormat.getImageableHeight() - painter.getSize().height*yScale)/2;
      g.translate(pageFormat.getImageableX() + xMargin, 
		  pageFormat.getImageableY() + yMargin);
      g.scale(xScale, yScale);

      //
      // Delegate rendering to painter
      //
      painter.render(g);

      //
      // Return status indicated that we did paint a page
      //
      return PAGE_EXISTS;
    }
    else
      return NO_SUCH_PAGE;
  }

  public static final String USAGE = "java com.sun.glf.snippets.PrinterRendering <imageFileName>";

  public static void main(String args[]) throws Exception{
    // Check input arguments
    if(args.length<1){
      System.out.println(USAGE);
      System.exit(0);
    }

    // First, get a new PrinterJob object
    PrinterJob printerJob = PrinterJob.getPrinterJob();

    //
    // Get page property settings
    //
    PageFormat pageFormat = printerJob.defaultPage();
    // if(printerJob.pageDialog(pageFormat) != pageFormat){      
      // User has okayed his settings
      
      // A PrinterRendering object will render the page
      printerJob.setPrintable(new PrinterRendering(args[0]));
      
      // 
      // Let the user select the print options
      //
      if(printerJob.printDialog()){
	// User decided to print
	printerJob.print();
      }
      else
	// User decided not to print
	System.out.println("PrintDialog was cancelled");
      // }
  }

}
