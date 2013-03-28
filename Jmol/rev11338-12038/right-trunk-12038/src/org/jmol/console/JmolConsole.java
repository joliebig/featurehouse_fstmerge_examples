
package org.jmol.console;

import org.jmol.api.*;
import org.jmol.i18n.*;
import org.jmol.script.ScriptCompiler;
import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.TextFormat;
import org.jmol.viewer.FileManager;

import java.awt.Component;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

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
  
  public int nTab = 0;
  private String incompleteCmd;
  
  protected String completeCommand(String thisCmd) {
    if (thisCmd.length() == 0)
      return null;
    String strCommand = (nTab <= 0 || incompleteCmd == null ? thisCmd
        : incompleteCmd);
    incompleteCmd = strCommand;
    String[] splitCmd = ScriptCompiler.splitCommandLine(thisCmd);
    if (splitCmd == null)
      return null;
    boolean asCommand = splitCmd[2] == null;
    String notThis = splitCmd[asCommand ? 1 : 2];
    if (notThis.length() == 0)
      return null;
    splitCmd = ScriptCompiler.splitCommandLine(strCommand);
    String cmd = null;
    if (!asCommand && (notThis.charAt(0) == '"' || notThis.charAt(0) == '\'')) {
      char q = notThis.charAt(0);
      notThis = TextFormat.trim(notThis, "\"\'");
      String stub = TextFormat.trim(splitCmd[2], "\"\'");
      cmd = nextFileName(stub, nTab);
      if (cmd != null)
        cmd = splitCmd[0] + splitCmd[1] + q + (cmd == null ? notThis : cmd) + q;
    } else {
      cmd = Token.completeCommand(null, asCommand, asCommand ? splitCmd[1]
          : splitCmd[2], nTab);
      cmd = splitCmd[0]
          + (cmd == null ? notThis : asCommand ? cmd : splitCmd[1] + cmd);
    }
    return (cmd == null || cmd.equals(strCommand) ? null : cmd);
  }

  private String nextFileName(String stub, int nTab) {
    String sname = FileManager.getLocalPathForWritingFile(viewer, stub);
    String root = sname.substring(0, sname.lastIndexOf("/") + 1);
    if (sname.startsWith("file:/"))
      sname = sname.substring(6);
    if (sname.indexOf("/") >= 0) {
      if (root.equals(sname)) {
        stub = "";
      } else {
        File dir = new File(sname);
        sname = dir.getParent();
        stub = dir.getName();
      }
    }
    FileChecker fileChecker = new FileChecker(stub);
    try {
      (new File(sname)).list(fileChecker);
      return root + fileChecker.getFile(nTab);
    } catch (Exception e) {
      
    }
    return null;
  }

  protected class FileChecker implements FilenameFilter {
    private String stub;
    private Vector v = new Vector();
    
    protected FileChecker(String stub) {
      this.stub = stub.toLowerCase();
    }

    public boolean accept(File dir, String name) {
      name = name.toLowerCase();
      if (!name.toLowerCase().startsWith(stub))
        return false;
      v.add(name); 
      return true;
    }
    
    protected String getFile(int n) {
      return ArrayUtil.sortedItem(v, n);
    }
  }
  
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
