/*
 * @(#)StringPropertyEditor.java
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
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.sun.glf.util.*;

/**
 * Provides a UI Component to configure a String
 * property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class StringPropertyEditor extends PropertyEditorSupport{
  static final String EDIT = "...";
  GridBagPanel editor;
  LargeEditDialog editDialog;

  /**
   * This method is intended for use when generating Java code to set
   * the value of the property.  It should return a fragment of Java code
   * that can be used to initialize a variable with the current property
   * value.
   */
  public String getJavaInitializationString() {
    return "new String(" + (String)getValue() + ")";
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
    return (String)getValue();
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
    setValue(text);
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
      final PlainDocument stringDoc = new PlainDocument();
      final JTextArea tf = new JTextArea(stringDoc, "", 1, 20);
      tf.setLineWrap(true);

      final JButton largeEdit = new JButton(EDIT);
      Dimension tfDim = tf.getPreferredSize();
      Dimension largeEditDim = largeEdit.getPreferredSize();
      largeEditDim.height = tfDim.height;
      largeEdit.setPreferredSize(largeEditDim);

      editor.add(new JScrollPane(tf), 0, 0, 1, 1, editor.CENTER, editor.NONE, 0, 0);
      editor.add(largeEdit, 1, 0, 1, 1, editor.CENTER, editor.NONE, 0, 0);
      editor.add(Box.createHorizontalGlue(), 2, 1, 1, 1, editor.CENTER, editor.HORIZONTAL, 1, 0);

      largeEdit.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent evt){
	  if(editDialog==null)
	    editDialog = new LargeEditDialog();
	  
	  if(editDialog.show(tf.getText()) == JOptionPane.OK_OPTION)
	    tf.setText(editDialog.textArea.getText());
	}
      });

      class StringChangeListener implements PropertyChangeListener, DocumentListener{
	boolean settingValue = false;

	public void propertyChange(PropertyChangeEvent evt){
	  if(!settingValue){
	    String val = (String)getValue();
	    tf.setText(val);
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
	  setValue(tf.getText());
	  settingValue = false;
	}
      };
      
      StringChangeListener listener = new StringChangeListener();
      
      // Synchronize UI with value
      addPropertyChangeListener(listener);
      
      // Synchronize value with UI
      stringDoc.addDocumentListener(listener);
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

class LargeEditDialog extends JDialog{
  static final String OK = "Ok";
  static final String CANCEL = "Cancel";

  JTextArea textArea = new JTextArea(new PlainDocument(), "", 10, 75);

  int choice;

  public LargeEditDialog(){
    super();

    setModal(true);

    GridBagPanel content = new GridBagPanel();
    textArea.setLineWrap(true);
    content.add(new JScrollPane(textArea), 0, 0, 1, 1, content.CENTER, content.BOTH, 1, 1);
    
    JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
    JButton okButton = new JButton(OK);
    JButton cancelButton = new JButton(CANCEL);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    GridBagPanel bp = new GridBagPanel();
    bp.add(Box.createHorizontalGlue(), 0, 0, 1, 1, bp.EAST, bp.HORIZONTAL, 1, 0);
    bp.add(buttonPanel, 1, 0, 1, 1, bp.CENTER, bp.NONE, 0, 0);

    content.add(bp, 0, 1, 1, 1, content.CENTER, content.HORIZONTAL, 1, 0);
    getContentPane().add(content);

    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	choice = JOptionPane.OK_OPTION;
	setVisible(false);
      }
    });

    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	choice = JOptionPane.CANCEL_OPTION;
	setVisible(false);
      }
    });
  }

  public int show(String text){
    textArea.setText(text);
    pack();

    setVisible(true);
    return choice;
  }
}


