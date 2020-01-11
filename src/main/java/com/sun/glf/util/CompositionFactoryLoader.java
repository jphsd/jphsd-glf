/*
 * @(#)CompositionStudio.java
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

import java.beans.*;
import java.io.*;

import javax.swing.*;

/**
 * Helper class, used to load a CompositionFactory beans, either
 * for a serialized file or a ser.txt file.
 *
 * @author          Vincent Hardy
 * @version         1.0, 06/09/1999
 */
public final class CompositionFactoryLoader{
  static final String ERROR_NOT_A_COMPOSITION_FACTORY = "Error: not a CompositionFactory bean";
  static final String ERROR_CANNOT_LOAD_BEAN = "Error. Cannot load bean : ";

  /**
   * Loads requested serialized bean file.
   *
   * @return object if file loaded successfully. Null otherwise
   */
  public static CompositionFactory loadBeanFile(File file){
    CompositionFactory bean = null;
    try{
      // If the extention is that of a text bean (see com.sun.glf.util.TextBeans)
      // the file is expected to be text. Otherwise, it is expected 
      // to be a default serialized format
      FileInputStream fins = new FileInputStream(file);
      if(file.getAbsolutePath().toLowerCase().endsWith(".ser.txt")){
	bean = (CompositionFactory)TextBeans.read(fins);
      }
      else{
	ObjectInputStream oins = new ObjectInputStream(fins);
	bean = (CompositionFactory)oins.readObject();
	oins.close();
      }
      
      fins.close();
    }catch(ClassCastException e){
      JOptionPane.showMessageDialog(null, ERROR_NOT_A_COMPOSITION_FACTORY);
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, ERROR_CANNOT_LOAD_BEAN + e.getMessage());
      e.printStackTrace();
    }

    return bean;
  }
}
