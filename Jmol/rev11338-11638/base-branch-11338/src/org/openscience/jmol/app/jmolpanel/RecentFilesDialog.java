
package org.openscience.jmol.app.jmolpanel;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jmol.i18n.GT;
import org.openscience.jmol.app.jmolpanel.JmolPanel;


class RecentFilesDialog extends JDialog implements ActionListener,
    WindowListener {

  String selectedFileName = null;
  private static final int MAX_FILES = 10;
  private JButton okButton;
  private JButton cancelButton;
  String[] files = new String[MAX_FILES];
  JList fileList;
  java.util.Properties props;

  
  public RecentFilesDialog(java.awt.Frame boss) {

    super(boss, GT._("Recent Files"), true);
    props = new java.util.Properties();
    getFiles();
    getContentPane().setLayout(new java.awt.BorderLayout());
    JPanel buttonPanel = new JPanel();
    okButton = new JButton(GT._("Open"));
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    cancelButton =
        new JButton(GT._("Cancel"));
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);
    getContentPane().add("South", buttonPanel);

    fileList = new JList(files);
    fileList.setSelectedIndex(0);
    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MouseListener dblClickListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int dblClickIndex = fileList.locationToIndex(e.getPoint());
            if (dblClickIndex >= 0 &&
                dblClickIndex < files.length &&
                files[dblClickIndex] != null) {
              selectedFileName = files[dblClickIndex];
              close();
            }
          }
        }
      };
    fileList.addMouseListener(dblClickListener);

    getContentPane().add("Center", fileList);
    
    setLocation(100, 100);
    pack();
  }

  private void getFiles() {

    props = JmolPanel.historyFile.getProperties();
    for (int i = 0; i < MAX_FILES; i++) {
      files[i] = props.getProperty("recentFilesFile" + i);
    }
  }

  
  public void addFile(String name) {

    int currentPosition = -1;

    
    for (int i = 0; i < MAX_FILES; i++) {
      if ((files[i] != null) && files[i].equals(name)) {
        currentPosition = i;
      }
    }

    
    if (currentPosition == 0) {
      return;
    }

    
    
    if (currentPosition > 0) {
      for (int i = currentPosition; i < MAX_FILES - 1; i++) {
        files[i] = files[i + 1];
      }
    }

    
    for (int j = MAX_FILES - 2; j >= 0; j--) {
      files[j + 1] = files[j];
    }

    
    files[0] = name;
    fileList.setListData(files);
    fileList.setSelectedIndex(0);
    pack();
    saveList();
  }

  
  public void saveList() {

    for (int i = 0; i < 10; i++) {
      if (files[i] != null) {
        props.setProperty("recentFilesFile" + i, files[i]);
      }
    }

    JmolPanel.historyFile.addProperties(props);
  }

  
  public String getFile() {
    return selectedFileName;
  }

  public void windowClosing(java.awt.event.WindowEvent e) {
    cancel();
    close();
  }

  void cancel() {
    selectedFileName = null;
  }

  void close() {
    hide();
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {

    if (e.getSource() == okButton) {
      int fileIndex = fileList.getSelectedIndex();
      if (fileIndex < files.length) {
        selectedFileName = files[fileIndex];
        close();
      }
    } else if (e.getSource() == cancelButton) {
      cancel();
      close();
    }
  }

  public void windowClosed(java.awt.event.WindowEvent e) {
  }

  public void windowOpened(java.awt.event.WindowEvent e) {
  }

  public void windowIconified(java.awt.event.WindowEvent e) {
  }

  public void windowDeiconified(java.awt.event.WindowEvent e) {
  }

  public void windowActivated(java.awt.event.WindowEvent e) {
  }

  public void windowDeactivated(java.awt.event.WindowEvent e) {
  }

  public void notifyFileOpen(String fullPathName) {
    if (fullPathName != null)
      addFile(fullPathName);
  }
}
