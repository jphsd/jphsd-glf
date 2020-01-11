/*
 * @(#)CompositionShow.java
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
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import com.sun.glf.*;
import com.sun.glf.util.*;

/**
 * Utility class. Provides a way to load several Composition beans and
 * 'browse' through them.
 *
 * @author         Vincent Hardy
 * @version        1.0, 11/06/1998
 */
public class CompositionShow extends JFrame implements GridBagConstants{
  static final String COMPOSITION_BROWSER = "Composition Browser";
  static final String NEXT = "\u25b6";
  static final String PREV = "\u25c0";
  static final String LOAD = "Load...";
  static final String COMPOSITION_SHOW = "Composition Browser";
  static final String ERROR_CANNOT_LOAD_BEAN = "Error. Cannot load bean : ";
  static final String ERROR_NOT_A_COMPOSITION_BUILDER = "Error: not a CompositionBuilder bean";

  /** Font used to get Symbols for Next and Prev */
  Font SYMBOL_FONT = new Font("Lucida Sans Unicode", Font.PLAIN, 12);

  /** Composition ComboBox */
  JComboBox cmpList;

  /** File Chooser for compositions files */
  static JFileChooser fileChooser;

  /** Frame where compositions are displayed */
  CompositionFrame viewFrame;

  static class CompositionDescriptor {
    CompositionFactory factory;
    String desc;

    public CompositionDescriptor(String desc, CompositionFactory factory){
      this.desc = desc;
      this.factory = factory;
    }

    public String toString(){
      return desc;
    }
  }

  /** Moves to the next Composition */
  private JButton nextButton;

  /** Moves to the previous Composition */
  private JButton prevButton;

  /** Opens a load dialog */
  private JButton loadButton;

  /** Holds the list of CompositionFactories */
  DefaultComboBoxModel cmpListModel = new DefaultComboBoxModel();

  public CompositionShow(){
    super(COMPOSITION_BROWSER);

    // Browsing button
    prevButton = new JButton(PREV);
    nextButton = new JButton(NEXT);
    loadButton = new JButton(LOAD);
    prevButton.setFont(SYMBOL_FONT);
    nextButton.setFont(SYMBOL_FONT);
    prevButton.setRolloverEnabled(true);
    nextButton.setRolloverEnabled(true);
    prevButton.setBorderPainted(false);
    nextButton.setBorderPainted(false);

    // Composition List
    cmpList = new JComboBox(cmpListModel);

    // Adjust sizes
    Dimension minSize = cmpList.getMinimumSize();
    cmpList.setMinimumSize(new Dimension(nextButton.getPreferredSize().width*4,
					 minSize.height));
    prevButton.setPreferredSize(new Dimension(prevButton.getPreferredSize().width,
					      cmpList.getPreferredSize().height));
    nextButton.setPreferredSize(new Dimension(nextButton.getPreferredSize().width,
					      cmpList.getPreferredSize().height));
    
    // Layout
    GridBagPanel content = new GridBagPanel();
    content.add(prevButton, 0, 0, 1, 1, CENTER, NONE, 0, 0);
    content.add(cmpList, 1, 0, 1, 1, CENTER, HORIZONTAL, 1, 0);
    content.add(nextButton, 2, 0, 1, 1, CENTER, NONE, 0, 0);
    content.add(loadButton, 3, 0, 1, 1, CENTER, NONE, 0, 0);

    //
    // Event Handling
    // 

    // File Load
    loadButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onFileLoad();
      }
    });

    // Next
    nextButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onNext();
      }
    });

    // Previous
    prevButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent evt){
	onPrev();
      }
    });

    // Selection change
    cmpList.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent evt){
	onNewSelection();
      }
    });

    // Key listener
    /*cmpList.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent evt){
	if(evt.getKeyCode()==KeyEvent.VK_DELETE)
	  onDelete();
      }
    });*/

    // Window listener
    addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
	setVisible(false);
	dispose();
	System.exit(0);
      } 
    });
    
    getContentPane().add(content);
    setComponentStates();
    pack();
  }

  /**
   * Loads selected beans into list. If there are already beans loaded,
   * this inserts the new ones after the current selection.
   */
  private void onFileLoad(){
    //
    // Build file dialog only the first time it is used.
    //
    if(fileChooser==null){
      fileChooser = new JFileChooser();
      fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      javax.swing.filechooser.FileFilter serFilter = new SerializedBeanFileFilter();
      javax.swing.filechooser.FileFilter serTxtFilter = new TextBeanFileFilter();
      fileChooser.addChoosableFileFilter(serFilter);
      fileChooser.addChoosableFileFilter(serTxtFilter);
      fileChooser.setFileSelectionMode(fileChooser.FILES_AND_DIRECTORIES);
      // fileChooser.setMultiSelectionEnabled(true);
    }

    // 
    // Get User selection
    //
    int choice = fileChooser.showDialog(null, null);
    if(choice == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      File files[] = null;
      if(file.isDirectory()){
	files = file.listFiles();
      }
      else
	files = new File[]{file};

      loadBeanFiles(files);
    }            
  }

  /**
   * Loads the list of files into the cmpList, inserting them after
   * the current selection
   */
  private void loadBeanFiles(File files[]){
    Vector beans = new Vector();
    int n = files!=null?files.length:0;
    for(int i=0; i<n; i++){
      CompositionFactory bean = null;
      FileInputStream fins = null;
      ObjectInputStream oins = null;
      String beanName = null;

      try{
	File file = files[i];
	beanName = file.getName();

	// If the extention is that of a text bean (see com.sun.glf.util.TextBeans)
	// the file is expected to be text. Otherwise, it is expected 
	// to be a default serialized format
	fins = new FileInputStream(file);
	if(file.getAbsolutePath().toLowerCase().endsWith(".ser.txt")){
	  bean = (CompositionFactory)TextBeans.read(fins);
	}
	else{
	  oins = new ObjectInputStream(fins);
	  bean = (CompositionFactory)oins.readObject();
	}      
	
	beans.addElement(new CompositionDescriptor(beanName, bean));
      }catch(ClassCastException e){
	JOptionPane.showMessageDialog(null, beanName + " : " + ERROR_NOT_A_COMPOSITION_BUILDER);
      }catch(Exception e){
	JOptionPane.showMessageDialog(null, beanName + " : " + ERROR_CANNOT_LOAD_BEAN + e.getMessage());
      }finally{
	try{
	  if(fins!=null)
	    fins.close();
	}catch(IOException e){}
	try{
	  if(oins!=null)
	    oins.close();
	}catch(IOException e){}
      }
    }

    // Now, that the beans have been loaded, insert after the current selection
    int curSel = cmpList.getSelectedIndex();
    curSel++;
    int nBeans = beans.size();
    if(nBeans>0){
      if(curSel<cmpListModel.getSize())
	cmpListModel.insertElementAt(beans.elementAt(nBeans-1), curSel);
      else
	cmpListModel.addElement(beans.elementAt(nBeans-1));
	
      for(int i=nBeans-2; i>=0; i--)
	cmpListModel.insertElementAt(beans.elementAt(i), curSel);
    }

    setComponentStates();

  }

  /**
   * Sets the buttons state depending on the cmpList content and selection
   */
  private void setComponentStates(){
    boolean live = cmpListModel.getSize()>0;
    int curSel = cmpList.getSelectedIndex();
    prevButton.setEnabled(live&&(curSel>0));
    nextButton.setEnabled(live&&(curSel<(cmpListModel.getSize()-1)));
    cmpList.setEnabled(live);
    repaint();
  }

  /**
   * Handles the 'delete selection' request
   */
  private void onDelete(){
    int curSel = cmpList.getSelectedIndex();
    if(curSel!=-1){
      cmpListModel.removeElementAt(curSel);
    }
  }

  /**
   * Handle the 'show next' request
   */
  private void onNext(){
    int curSel = cmpList.getSelectedIndex();
    int nextSel = curSel+1;
    if(nextSel<cmpListModel.getSize())
      cmpList.setSelectedIndex(nextSel);
  }

  /**
   * Handle the 'show previous' request
   */
  private void onPrev(){
    int curSel = cmpList.getSelectedIndex();
    int prevSel = curSel-1;
    if(prevSel>0 && prevSel<cmpListModel.getSize())
      cmpList.setSelectedIndex(prevSel);
  }

  /**
   * Handle the 'new selection' request
   */
  private void onNewSelection(){
    int curSel = cmpList.getSelectedIndex();
    CompositionFactory factory = ((CompositionDescriptor)cmpListModel.getElementAt(curSel)).factory;
    if(viewFrame==null){
      viewFrame = new CompositionFrame("");
    }
    setComponentStates();
    viewFrame.load(factory);
  }

  /**
   * Entry point
   */
  public static void main(String args[]){
    // Set proper default fonts, etc...
    Toolbox.swingDefaultsInit();

    // WORK AROUND : TO BE REMOVED WHEN FONT SERIALIZATION BUG FIXED
    Toolbox.initFonts();
    // END WORK AROUND

    (new CompositionShow()).setVisible(true);
  }
}

