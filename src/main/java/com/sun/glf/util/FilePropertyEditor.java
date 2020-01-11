/*
 * @(#)FilePropertyEditor.java
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
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.sun.glf.util.*;

/**
 * Provides a UI Component to configure a File
 * property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class FilePropertyEditor extends PropertyEditorSupport{
  static final String BROWSE = "...";

  /** File Chooser Dialog */
  static JFileChooser fileChooser;
  
  /** File editor */
  GridBagPanel editor;

  /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
  public String getJavaInitializationString() {
    File file = (File)getValue();
    return "new File(" + file.getPath() + ")";
  }

  /**
   * Gets the property value as a string suitable for presentation
   * to a human to edit.
   *
   * @return The property value as a string suitable for presentation
   *       to a human to edit.
   * <p>   Returns "null" is the value can't be expressed as a string.
   * <p>   If a non-null value is returned, then the PropertyEditor should
   *	     be prepared to parse that string back in setAsText().
   */
  public String getAsText() {
    return null;
  }

  /**
   * Sets the property value by parsing a given String.  May raise
   * java.lang.IllegalArgumentException if either the String is
   * badly formatted or if this kind of property can't be expressed
   * as text.
   *
   * @param text  The string to be parsed.
   */
  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    throw new IllegalArgumentException();
  }

  /**
   * The File Property editor displays two text fields, one for the 
   * width, one for the height.
   *
   * @return A java.awt.Component that will allow a human to directly
   *      edit the current property value.  May be null if this is
   *	    not supported.
   */
  public java.awt.Component getCustomEditor() {
    if(editor == null){
      editor = new GridBagPanel();
      
      PlainDocument pathDoc = new PlainDocument();
      final JTextField path = new JTextField(pathDoc, "", 20);
      final JButton browse = new JButton(BROWSE);
      Dimension pathDim = path.getPreferredSize();
      Dimension browseDim = browse.getPreferredSize();
      browseDim.height = pathDim.height;
      browse.setPreferredSize(browseDim);

      editor.add(path, 0, 0, 1, 1, editor.CENTER, editor.NONE, 0, 0);
      editor.add(browse, 1, 0, 1, 1, editor.CENTER, editor.NONE, 0, 0);
      editor.add(Box.createHorizontalGlue(), 2, 1, 1, 1, editor.CENTER, editor.HORIZONTAL, 1, 0);

      class PathChangeListener implements PropertyChangeListener, DocumentListener, ActionListener{
	boolean settingValue = false;

	public void propertyChange(PropertyChangeEvent evt){
	  if(!settingValue){
	    File file = (File)getValue();
	    path.setText(file.getAbsolutePath());
	  }
	}

	public void changedUpdate(DocumentEvent evt){
	  onChange();
	}

	public void insertUpdate(DocumentEvent evt){
	  onChange();
	}

	public void removeUpdate(DocumentEvent evt){
	  onChange();
	}

	public void onChange(){
	  settingValue = true;
	  setValue(new File(path.getText()));
	  settingValue = false;
	}

	public void actionPerformed(ActionEvent evt){
	  if(fileChooser==null){
	    fileChooser = new JFileChooser();
	    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	  }
	  if(getValue()!=null){
	    File curFile = (File)getValue();
	    fileChooser.setSelectedFile((File)getValue());
	  }

	  int choice = fileChooser.showDialog(null, null);
	  if(choice == JFileChooser.APPROVE_OPTION) {
	    File file = fileChooser.getSelectedFile();
	    setValue(file);
	  }
	  
	}
      };
      
      PathChangeListener listener = new PathChangeListener();
      
      // Synchronize UI with value
      addPropertyChangeListener(listener);
      
      // Synchronize value with UI
      pathDoc.addDocumentListener(listener);

      browse.addActionListener(listener);
    }
    
    return editor;
  }

  /**
   * Determines whether the propertyEditor can provide a custom editor.
   *
   * @return  True if the propertyEditor can provide a custom editor.
   */
  public boolean supportsCustomEditor() {
    return true;
  }
  
}

