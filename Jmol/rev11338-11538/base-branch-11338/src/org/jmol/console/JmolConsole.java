
package org.jmol.console;

import org.jmol.api.*;
import org.jmol.i18n.*;

import java.awt.Component;
import java.awt.event.*;

import javax.swing.*;

public abstract class JmolConsole extends JDialog implements ActionListener, WindowListener {

  public JmolViewer viewer;
  protected Component display;

  
  
  protected ScriptEditor scriptEditor;
  
  void setScriptEditor(ScriptEditor se) {
    scriptEditor = se;
  }
  
  public JmolScriptEditorInterface getScriptEditor() {
    return (scriptEditor == null ? 
        (scriptEditor = new ScriptEditor(viewer, display instanceof JFrame ? (JFrame) display : null, this))
        : scriptEditor);
  }
  
  protected JButton editButton, runButton, historyButton, stateButton;

  JmolViewer getViewer() {
    return viewer;
  }

  
  
  

  public JmolConsole() {
  }
  
  public JmolConsole(JmolViewer viewer, JFrame frame, String _, boolean b) {
    super(frame, GT._("Jmol Script Console"), false);
    this.viewer = viewer;
    display = frame;
  }

  abstract protected void clearContent(String text);
  abstract protected void execute(String strCommand);
  
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == runButton) {
      execute(null);
    } else if (source == editButton) {
      viewer.getProperty("DATA_API","scriptEditor", null);
    } else if (source == historyButton) {
      clearContent(viewer.getSetHistory(Integer.MAX_VALUE));
    } else if (source == stateButton) {
      viewer.getProperty("DATA_API","scriptEditor", new String[] { "current state" , viewer.getStateInfo() });
    }
  }


  
  
  

  public void windowActivated(WindowEvent we) {
  }

  public void windowClosed(WindowEvent we) {
  }

  public void windowClosing(WindowEvent we) {
  }

  public void windowDeactivated(WindowEvent we) {
  }

  public void windowDeiconified(WindowEvent we) {
  }

  public void windowIconified(WindowEvent we) {
  }

  public void windowOpened(WindowEvent we) {
  }

}
