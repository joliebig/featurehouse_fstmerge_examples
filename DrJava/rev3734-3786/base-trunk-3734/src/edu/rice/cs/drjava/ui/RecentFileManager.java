

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.config.*;

import edu.rice.cs.util.FileOpenSelector;


public class RecentFileManager implements OptionConstants {
  
  protected int _pos;

  
  protected Vector<File> _recentFiles;

  
  protected Vector<JMenuItem> _recentMenuItems;

  
  protected int MAX = DrJava.getConfig().getSetting(RECENT_FILES_MAX_SIZE).intValue();

  
  protected JMenu _fileMenu;

  
  protected boolean _isProject;

  
  MainFrame _frame;

  
  public RecentFileManager(int pos, JMenu fileMenu, MainFrame frame, boolean isProject) {
    _pos = pos;
    _fileMenu = fileMenu;
    _frame = frame;
    _recentFiles = new Vector<File>();
    _recentMenuItems = new Vector<JMenuItem>();
    _isProject = isProject;

    
    Vector<File> files;
    if (! _isProject) files = DrJava.getConfig().getSetting(RECENT_FILES);
    else files = DrJava.getConfig().getSetting(RECENT_PROJECTS);
    
    for (int i = files.size() - 1; i >= 0; i--) { updateOpenFiles(files.get(i)); }
  }

  
  public Vector<File> getFileVector() { return _recentFiles; }

  
  public void updateMax(int newMax) { MAX = newMax; }

  
  public void saveRecentFiles() {
    if (! _isProject) DrJava.getConfig().setSetting(RECENT_FILES,_recentFiles);
    else DrJava.getConfig().setSetting(RECENT_PROJECTS,_recentFiles);
  }

  
  public void updateOpenFiles(final File file) {

    if (_recentMenuItems.size() == 0) {
      _fileMenu.insertSeparator(_pos);  
      _pos++;
    }


    final FileOpenSelector _recentSelector = new FileOpenSelector() {
      public File[] getFiles() { return new File[] { file }; }
    };

    JMenuItem newItem = new JMenuItem("");
    newItem.addActionListener(new AbstractAction("Open " + file.getName()) {
      public void actionPerformed(ActionEvent ae) {
        if (file.getName().endsWith(".pjt")) _frame.openProject(_recentSelector);
        else _frame.open(_recentSelector);
        
        
        


      }
    });
    try { newItem.setToolTipText(file.getCanonicalPath()); }
    catch(IOException e) {
      
    }
    removeIfInList(file);
    _recentMenuItems.add(0,newItem);
    _recentFiles.add(0,file);
    numberItems();
    _fileMenu.insert(newItem,_pos);
  }

  
  public void removeIfInList(File file) {
    
    File canonical = null;
    try { canonical = file.getCanonicalFile(); }
    catch (IOException ioe) {
      
    }

    for (int i = 0; i < _recentFiles.size(); i++) {
      File currFile = _recentFiles.get(i);
      boolean match;
      if (canonical != null) {
        try { match = currFile.getCanonicalFile().equals(canonical); }
        catch (IOException ioe) {
          
          match = currFile.equals(file);
        }
      }
      else {
        
        match = currFile.equals(file);
      }

      if (match) {
        _recentFiles.remove(i);
        JMenuItem menuItem = _recentMenuItems.get(i);
        _fileMenu.remove(menuItem);
        _recentMenuItems.remove(i);
        break;
      }
    }
  }

  
  public void numberItems() {
    int delPos = _recentMenuItems.size();
    boolean wasEmpty = (delPos == 0);
    while (delPos > MAX) {
      JMenuItem delItem = _recentMenuItems.get(delPos - 1);
      _recentMenuItems.remove(delPos - 1);
      _recentFiles.remove(delPos - 1);
      _fileMenu.remove(delItem);

      delPos = _recentMenuItems.size();
    }
    JMenuItem currItem;
    for (int i=0; i< _recentMenuItems.size(); i++ ) {
      currItem = _recentMenuItems.get(i);
      currItem.setText((i+1) + ". " + _recentFiles.get(i).getName());
    }
    
    if (MAX == 0 && !wasEmpty) { _fileMenu.remove(--_pos); }
  }
}
