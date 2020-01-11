/*
 * @(#)DefaultFileFilter.java
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

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Default implementation of the FileFilter interface
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/22/1998
 */
public class DefaultFileFilter extends FileFilter {
  private String ext, desc;

  /**
   * @param ext the file extention for this filter
   * @param desc the description for this filter
   */
  public DefaultFileFilter(String ext, String desc){
    if(ext==null)
      ext = "";
    if(desc==null)
      desc = "";

    this.ext = ext;
    this.desc = desc;
  }

  public String getExtention(){
    return ext;
  }

  /**
   * Whether the given file is accepted by this filter.
   */
  public boolean accept(File f){
    boolean accept = false;
    if(f!=null){
      String fileName = f.getPath().toLowerCase();
      if(fileName != null && fileName.endsWith(ext))
	accept = true;
      if(f.isDirectory())
	accept = true;
    }

    return accept;
  }

  /**
   * The description of this filter. 
   */
  public String getDescription(){
    return desc;
  }
}
