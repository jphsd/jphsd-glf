/*
 * @(#)IntegerDocument.java
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

import javax.swing.text.*;

/**
 * Helper class. Only allows an integer value in the document.
 * 
 * @author          Vincent Hardy
 * @version         1.0, 10/16/1998
 */
public class IntegerDocument extends PlainDocument {
  /** 
   * Strip all non digit characters. '-' and '+' are only allowed as the
   * first character.
   **/
  public void insertString(int offs, String str, AttributeSet a) throws BadLocationException { 
    if (str == null) {
      return;
    }

    char[] buffer = str.toCharArray();
    char[] digit = new char[buffer.length];
    int j = 0;

    if(offs==0 && buffer!=null && buffer.length>0 && buffer[0]=='-')
      digit[j++] = buffer[0];

    for (int i = 0; i < buffer.length; i++) {
      if(Character.isDigit(buffer[i]))
	digit[j++] = buffer[i];
    }

    try{
      String added = new String(digit, 0, j);
      StringBuffer val = new StringBuffer(getText(0, getLength()));
      val.insert(offs, added);
      if(!"-".equals(val.toString()))
	Integer.parseInt(val.toString()); // Validate format
      super.insertString(offs, added, a);
    }catch(NumberFormatException e){
      // Ignore insert as value is out of bound or contains invalid 
      // characters
    }
  }
}


