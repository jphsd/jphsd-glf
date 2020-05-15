/*
 * @(#)DemoRunner.java
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

import com.sun.glf.util.GridBagPanel;
import com.sun.glf.util.Toolbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class DemoRunner {
  static final int WAIT_FOR_VM_TO_START = 6500;
  static final String DEFAULT_MESSAGE = "Double click on demo name to start it";

  private static MutableTreeNode buildNode(Properties props, String str) {
    DemoRunner.DemoNode demo = new DemoRunner.DemoNode(str.replace('_', ' '));
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(demo);
    String val = props.getProperty(str);
    if (val != null) {
      StringTokenizer toks = new StringTokenizer(val);

      while(toks.hasMoreTokens()) {
        node.add(buildNode(props, toks.nextToken()));
      }
    }

    String exec = props.getProperty(str + ".exec");
    if (exec != null) {
      String execVal = props.getProperty(exec);
      if (execVal != null) {
        int offs = execVal.indexOf("%SNIPPET%");
        if (offs != -1) {
          execVal = execVal.substring(0, offs) + demo.name + execVal.substring(offs + "%SNIPPET%".length());
        }

        demo.exec = execVal;
      }
    }

    demo.params = props.getProperty(str + ".params", "");
    return node;
  }

  public static void main(String[] args) throws Exception {
    Toolbox.swingDefaultsInit();
    final JFrame frame = new JFrame("\"Java 2D(tm) Graphics\" Demo runner");
    InputStream input = DemoRunner.class.getResourceAsStream("/demos.properties");
    Properties props = new Properties();
    props.load(input);
    String os = System.getProperty("os.name");
    os = os.replace(' ', '_');
    final String snippetExec = props.getProperty("script." + os, "runsnippet");
    System.out.println("Script for " + os + " : " + snippetExec);
    MutableTreeNode root = buildNode(props, "All_Demos");
    final JTree tree = new JTree(root);
    Dimension dim = tree.getPreferredSize();
    dim.height *= 4;
    dim.width *= 4;
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setPreferredSize(dim);
    final JLabel label = new JLabel("Double click on demo name to start it", 2);
    label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLoweredBevelBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    GridBagPanel gbp = new GridBagPanel();
    gbp.add(scrollPane, 0, 0, 1, 1, 10, 1, 1.0, 1.0);
    gbp.add(label, 0, 1, 1, 1, 17, 2, 1.0, 0.0);
    frame.getContentPane().add(gbp);
    frame.pack();
    frame.setLocation(20, 20);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    MouseAdapter ma = new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        int row = tree.getRowForLocation(evt.getX(), evt.getY());
        TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
        if (evt.getClickCount() == 2 && path != null) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
          if (node.isLeaf()) {
            DemoRunner.DemoNode demo = (DemoRunner.DemoNode)node.getUserObject();
            final String demoStr = demo.toString();

            try {
              String cmd = snippetExec + " " + demoStr + " " + demo.params;
              System.out.println(cmd);
              final Process proc = Runtime.getRuntime().exec(cmd);
              Thread thread = new Thread() {
                public void run() {
                  label.setText("Starting " + demoStr + " in separate VM. Please wait");
                  frame.setCursor(Cursor.getPredefinedCursor(3));
                  Color col = label.getForeground();
                  label.setForeground(new Color(128, 0, 0));
                  label.repaint();

                  try {
                    Thread.sleep(6500);
                  } catch (InterruptedException e) {
                  }

                  label.setForeground(col);
                  label.setText("Double click on demo name to start it");
                  label.repaint();
                  frame.setCursor(Cursor.getDefaultCursor());
                }
              };
              thread.start();
              Thread stdOut = new Thread() {
                public void run() {
                  InputStream input = proc.getInputStream();
                  BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                  try {
                    String str = null;

                    while((str = reader.readLine()) != null) {
                      System.out.println(demoStr + " : " + str);
                    }

                    System.out.println(demoStr + " terminated");
                  } catch (IOException e) {
                    System.out.println(demoStr + " terminated");
                  }
                }
              };
              stdOut.start();
              Thread stdErr = new Thread() {
                public void run() {
                  InputStream input = proc.getErrorStream();
                  BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                  try {
                    String str = null;

                    while((str = reader.readLine()) != null) {
                      System.err.println(demoStr + " : " + str);
                    }
                  } catch (IOException e) {
                  }
                }
              };
              stdErr.start();
            } catch (IOException var11) {
              JOptionPane.showMessageDialog((Component)null, "Could not start demo : " + var11.getMessage(), "Error", 0);
            }
          }
        }

      }
    };
    tree.addMouseListener(ma);
    frame.setVisible(true);
  }

  static class DemoNode {
    String name;
    String exec;
    String params;

    public DemoNode(String n) {
      name = n;
    }

    public String toString() {
      return name;
    }
  }
}
