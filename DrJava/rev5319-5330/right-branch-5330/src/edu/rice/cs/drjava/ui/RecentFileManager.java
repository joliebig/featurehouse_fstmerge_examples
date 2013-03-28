

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.HashSet;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.plt.lambda.Runnable1;

import edu.rice.cs.util.FileOpenSelector;


public class RecentFileManager implements OptionConstants {
  
  protected int _initPos;

  
  protected int _pos;
  
  
  protected Vector<File> _recentFiles;
  
  
  protected int MAX = DrJava.getConfig().getSetting(RECENT_FILES_MAX_SIZE).intValue();
  
  
  protected JMenu _fileMenu;
  
  
  protected HashSet<JMenu> _mirroredMenus = new HashSet<JMenu>();
  
  
  protected VectorOption<File> _settingConfigConstant;
  
  
  protected RecentFileAction _recentFileAction;
  
  
  public RecentFileManager(int pos, JMenu fileMenu, RecentFileAction action, VectorOption<File> settingConfigConstant) {
    _initPos = _pos = pos;
    _fileMenu = fileMenu;
    _recentFileAction = action;
    _recentFiles = new Vector<File>();
    _settingConfigConstant = settingConfigConstant;
    
    
    Vector<File> files = DrJava.getConfig().getSetting(_settingConfigConstant);
    
    for (int i = files.size() - 1; i >= 0; i--) {
      File f = files.get(i);
      if (f.exists()) updateOpenFiles(f); 
    }
  }
  
  
  public void addMirroredMenu(JMenu mirroredMenu) {
    
    if (_recentFiles.size()>0) {
      mirroredMenu.insertSeparator(_initPos);  
    }
    for(int i=0; i<_recentFiles.size(); ++i) {
      final File file = _recentFiles.get(i);
      final FileOpenSelector recentSelector = new FileOpenSelector() {
        public File[] getFiles() { return new File[] { file }; }
      };
      JMenuItem newItem = new JMenuItem((i+1) + ". " + file.getName());
      newItem.addActionListener(new AbstractAction("Open " + file.getName()) {
        public void actionPerformed(ActionEvent ae) {
          if (_recentFileAction != null) {
            _recentFileAction.actionPerformed(recentSelector);
          }
        }
      });
      try { newItem.setToolTipText(file.getCanonicalPath()); }
      catch(IOException e) {
        
      }
      mirroredMenu.insert(newItem,_initPos+i+1);
    }
    numberItems();
    _mirroredMenus.add(mirroredMenu);
  }
  
  
  public void removeMirroredMenu(JMenu mirroredMenu) {
    _mirroredMenus.remove(mirroredMenu);
  }
  
  
  public Vector<File> getFileVector() { return _recentFiles; }
  
  
  public void updateMax(int newMax) { MAX = newMax; }
  
  
  public void saveRecentFiles() {
    DrJava.getConfig().setSetting(_settingConfigConstant, _recentFiles);
  }
  
  
  public void updateOpenFiles(final File file) {
    
    if (_recentFiles.size() == 0) {
      _insertSeparator(_pos);  
      _pos++;
    }
    
    final FileOpenSelector recentSelector = new FileOpenSelector() {
      public File[] getFiles() { return new File[] { file }; }
    };
    
    removeIfInList(file);
    _recentFiles.add(0,file);

    _do(new Runnable1<JMenu>() {
      public void run(JMenu fileMenu) {
    JMenuItem newItem = new JMenuItem("");
    newItem.addActionListener(new AbstractAction("Open " + file.getName()) {
      public void actionPerformed(ActionEvent ae) {
        if (_recentFileAction != null) {
          _recentFileAction.actionPerformed(recentSelector);
        }
      }
    });
    try { newItem.setToolTipText(file.getCanonicalPath()); }
    catch(IOException e) {
      
    }
        fileMenu.insert(newItem,_pos);
      }
    });
    numberItems();
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
        _remove(_initPos+i+1);
        break;
      }
    }
  }
  
  
  public void numberItems() {
    int delPos = _recentFiles.size();
    boolean wasEmpty = (delPos == 0);
    while (delPos > MAX) {
      _recentFiles.remove(delPos - 1);
      _remove(_initPos+1-(delPos-1));
      
      delPos = _recentFiles.size();
    }
    for (int i = 0; i < _recentFiles.size(); i++ ) {
      final int fi = i;
      _do(new Runnable1<JMenu>() {
        public void run(JMenu fileMenu) {
          JMenuItem currItem = fileMenu.getItem(_initPos+fi+1);
          currItem.setText((fi+1) + ". " + _recentFiles.get(fi).getName());
        }
      });
    }
    
    if (MAX == 0 && !wasEmpty) { _remove(--_pos); }
  }
  
  
  public interface RecentFileAction {    
    public void actionPerformed(FileOpenSelector selector);
  }

  public void _do(Runnable1<JMenu> r) {
    r.run(_fileMenu);
    for(JMenu fileMenu: _mirroredMenus) {
      r.run(fileMenu);
    }
  }
  
  public void _insertSeparator(final int pos) {
    _do(new Runnable1<JMenu>() {
      public void run(JMenu fileMenu) {
        fileMenu.insertSeparator(pos);  
      }
    });
  }
  
  public void _remove(final int pos) {
    _do(new Runnable1<JMenu>() {
      public void run(JMenu fileMenu) {
        fileMenu.remove(pos);
      }
    });
  }
}
