
package org.openscience.jmol.app.webexport;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.*;

import org.jmol.util.TextFormat;
import org.jmol.viewer.*;

public class Test extends JPanel implements ActionListener {

  
  
  private static final long serialVersionUID = 1L;
  
  JButton StateButton, FileButton, PathButton, movetoTime,
      StringtoScriptButton;
  JTextField appletPath;
  ArrayListTransferHandler arrayListHandler;
  JFileChooser fc;
  Viewer viewer;

  Test(Viewer viewer) {
    this.viewer = viewer;
  }

  
  public JComponent panel() {

    
    JLabel Description = new JLabel(
        "Buttons to test getting info from Jmol Application");

    

    
    StateButton = new JButton("Get Application State...");
    StateButton.addActionListener(this);

    
    FileButton = new JButton("Get name of open file...");
    FileButton.addActionListener(this);

    
    PathButton = new JButton("Get Path to open file...");
    PathButton.addActionListener(this);

    
    movetoTime = new JButton("Insert 5 seconds for moveto, rotate and zoom...");
    movetoTime.addActionListener(this);

    
    StringtoScriptButton = new JButton("Save a string as a script");
    StringtoScriptButton.addActionListener(this);

    
    JPanel ButtonPanel1 = new JPanel();
    ButtonPanel1.add(StateButton);
    ButtonPanel1.add(FileButton);
    ButtonPanel1.add(PathButton);

    
    JPanel ButtonPanel2 = new JPanel();
    ButtonPanel2.add(movetoTime);
    ButtonPanel2.add(StringtoScriptButton);

    
    JPanel TestPanel = new JPanel();
    TestPanel.setLayout(new GridLayout(10, 1));

    
    TestPanel.add(Description);
    TestPanel.add(ButtonPanel1);
    TestPanel.add(ButtonPanel2);

    return (TestPanel);
  }

  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == StateButton) { 
      String Str = null;
      Str = viewer.getStateInfo();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the State Button in Test module");
      }
      LogPanel.log(Str);
    }
    if (e.getSource() == FileButton) { 
      String Str = null;
      Str = viewer.getFileName();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the file button in Test module");
      } else {
        LogPanel.log(Str);
      }
    }
    if (e.getSource() == PathButton) {
      String Str = null;
      Str = viewer.getFullPathName();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the Path button in Test module");
      } else {
        LogPanel.log(Str);
      }
    }
    if (e.getSource() == movetoTime) {
      String statestr = null;
      statestr = viewer.getStateInfo();
      if (statestr == null) {
        LogPanel
            .log("Something didn't work when reading the state while trying to add a moveto time.");
      }
      
      
      statestr = TextFormat.simpleReplace(statestr, "set refreshing false;",
          "set refreshing true;");
      statestr = TextFormat.simpleReplace(statestr,
          "moveto /* time, axisAngle */ 0.0",
          "moveto /* time, axisAngle */ 5.0");
      LogPanel.log("The state below should have a 5 second moveto time...");
      LogPanel.log(statestr);
    }
    if (e.getSource() == StringtoScriptButton) {
      String Str = "This is a test string to stand in for the script;";
      PrintStream out = null;
      try {
        out = new PrintStream(new FileOutputStream("Test.scpt"));
      } catch (FileNotFoundException IOe) {
        LogPanel.log("Open file error in StringtoScriptButton"); 
      }
      out.print(Str);
      out.close();
      LogPanel
          .log("The file Test.scpt should have been written to the default directory.");
    }
  }
}
