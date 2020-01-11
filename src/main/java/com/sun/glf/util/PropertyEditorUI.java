/*
 * @(#)PropertyEditorUI.java
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
import java.lang.reflect.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import com.sun.glf.util.*;

/**
 * Encapsulate a Component object used to configure a Beans property value.
 * There are 3 possibilities:<p>
 * 1. The property supports a custom editor. In this case, the editor UI
 *    simply behaves as a container for the custom editor.
 * 2. The property is paintable. In this case, the editor UI creates a 
 *    PaintableComponent which will delegate painting to the property editor.
 * 3. The property is text based. In this case, the editor UI creates a 
 *    text field for the property.
 *
 * @author       Vincent Hardy
 * @version      1.0, 10/13/1998
 */
public class PropertyEditorUI extends JPanel{
  /*
   * Methods to access the beans property
   */
  private Method getMethod, setMethod;

  /** Property editor */
  private PropertyEditor editor;

  /** Propertye editor type */
  private int editorType;

  /** Component displaying the value */
  private Component component;

  /*
   * Editor types
   */
  static final int EDITOR_CUSTOM = 0;
  static final int EDITOR_PAINTABLE = 1;
  static final int EDITOR_STRING = 2;

  /**
   * @param editor the property editor for which a UI is provided.
   * @param getMethod method to use to get the property value from a beans
   * @param setMethod method to use to save the property value into a beans
   */
  public PropertyEditorUI(PropertyEditor editor, Method getMethod, Method setMethod){
    if(editor==null || getMethod==null || setMethod==null)
      throw new IllegalArgumentException();

    this.editor = editor;
    this.getMethod = getMethod;
    this.setMethod = setMethod;
    
    if(editor.supportsCustomEditor())
      editorType = EDITOR_CUSTOM;
    else if(editor.isPaintable())
      editorType = EDITOR_PAINTABLE;
    else 
      editorType = EDITOR_STRING;

    switch(editorType){
    case EDITOR_CUSTOM:
      component = editor.getCustomEditor();
      break;
    case EDITOR_PAINTABLE:
      component = new PaintableEditor(editor);
      break;
    case EDITOR_STRING:
      component = new JTextField();
      break;
    }
      
    setLayout(new BorderLayout());
    add(component);
  }

  public int getEditorType(){
    return editorType;
  }

  /**
   * @param beans object whose property is to be displayed by this editor.
   */
  public void load(Object beans){
    try{
      Object value = getMethod.invoke(beans, null);
      editor.setValue(value);
      
      if(editorType==EDITOR_STRING){
	((JTextField)component).setText(editor.getAsText());
      }
      
      component.repaint();
    }catch(IllegalAccessException e1){
      throw new Error();
    }catch(IllegalArgumentException e2){
      throw new Error();
    }catch(InvocationTargetException e3){
      throw new Error();
    }
  }

  /**
   * @param beans object into which the property value displayed
   *        in UI component should be saved.
   */
  public void save(Object beans) throws InvocationTargetException{
    if(editorType==EDITOR_STRING)
      editor.setAsText(((JTextField)component).getText());

    Object value = editor.getValue();

    try{
      setMethod.invoke(beans, new Object[]{value});
    }catch(IllegalAccessException e1){
      throw new Error();
    }catch(IllegalArgumentException e2){
      throw new Error();
    }
  }
}

class PaintableEditor extends Component{
  /** Paintable PropertyEditor */
  PropertyEditor editor;

  /**
   * Rectangle where value is painted
   */
  Rectangle rect = new Rectangle(0, 0, 0, 0);

  /** 
   * Default Constructor 
   */
  public PaintableEditor(PropertyEditor editor){
    this.editor = editor;
  }

  public void paint(Graphics g){
    Dimension size = getSize();
    rect.width = size.width;
    rect.height = rect.height;
    editor.paintValue(g, rect);
  }
}
