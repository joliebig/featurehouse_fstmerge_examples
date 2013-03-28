
package org.jmol.export.dialog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;
import org.jmol.viewer.FileManager;


public class FilePreview extends JPanel implements PropertyChangeListener {

  JCheckBox active = null;
  JCheckBox append = null;
  JFileChooser chooser = null;
  private FPPanel display = null;

  
  public FilePreview(JFileChooser fileChooser, JmolAdapter modelAdapter,
      boolean allowAppend, String appletContext) {
    super();
    chooser = fileChooser;

    
    Box box = Box.createVerticalBox();

    
    active = new JCheckBox(GT._("Preview"), false);
    active.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (active.isSelected()) {
          updatePreview(chooser.getSelectedFile());
        } else {
          updatePreview(null);
        }
      }
    });
    box.add(active);

    
    display = new FPPanel(modelAdapter, appletContext);
    display.setPreferredSize(new Dimension(80, 80));
    display.setMinimumSize(new Dimension(50, 50));
    box.add(display);

    if (allowAppend) {
      
      append = new JCheckBox(GT._("Append models"), false);
      box.add(append);
    }

    
    add(box);
    fileChooser.setAccessory(this);
    fileChooser.addPropertyChangeListener(this);
  }

  
  public boolean isAppendSelected() {
    if (append != null) {
      return append.isSelected();
    }
    return false;
  }

  
  public void propertyChange(PropertyChangeEvent evt) {
    if (active.isSelected()) {
      String prop = evt.getPropertyName();
      if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
        updatePreview((File) evt.getNewValue());
      }
    }
  }

  
  void updatePreview(File file) {
    String script;
    if (file == null) {
      script = "zap";
    } else {
      String fileName = file.getAbsolutePath();
      
      String url = FileManager.getLocalUrl(file);
      
      if (url != null)
        fileName = url;
      
      script = " \"" + fileName + "\"";
      if (fileName.indexOf(".spt") >= 0) 
        script = "script " + script;
      else
        script = "zap;set echo top left;echo loading...;refresh;load " + script
            + ";if({1-10000}.size);cartoons only;color structure;endif"; 
    }
    display.getViewer().evalStringQuiet(script);
    
  }

  private static class FPPanel extends JPanel {
    JmolViewer viewer;

    FPPanel(JmolAdapter modelAdapter, String appletContext) {
      viewer = JmolViewer.allocateViewer(this, modelAdapter,
          "", null, null, "#previewOnly " + appletContext, null);
    }

    public JmolViewer getViewer() {
      return viewer;
    }

    final Dimension currentSize = new Dimension();

    public void paint(Graphics g) {
      viewer.setScreenDimension(getSize(currentSize));
      Rectangle rectClip = new Rectangle();
      g.getClipBounds(rectClip);
      viewer.renderScreenImage(g, currentSize, rectClip);
    }
  }
}
