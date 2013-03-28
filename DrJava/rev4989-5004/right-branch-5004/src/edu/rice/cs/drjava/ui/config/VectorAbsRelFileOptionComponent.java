

package edu.rice.cs.drjava.ui.config;


import java.awt.event.*;
import java.io.File;
import edu.rice.cs.util.AbsRelFile;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;
import javax.swing.table.*;

import java.util.Vector;
import java.util.List;

import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.CheckBoxJList;


public class VectorAbsRelFileOptionComponent extends VectorOptionComponent<AbsRelFile> implements OptionConstants {
  private FileFilter _fileFilter;
  private JFileChooser _jfc;
  protected File _baseDir = null;
  
  public VectorAbsRelFileOptionComponent (VectorOption<AbsRelFile> opt, String text, SwingFrame parent) {
    this(opt, text, parent, null);
  }
  
  
  public VectorAbsRelFileOptionComponent (VectorOption<AbsRelFile> opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent, description, false);
  }

  
  public VectorAbsRelFileOptionComponent (VectorOption<AbsRelFile> opt, String text, SwingFrame parent,
                                          String description, boolean moveButtonEnabled) {
    super(opt, text, parent, new String[] { "File", "Absolute" }, description, moveButtonEnabled);  

    
    _table.getColumnModel().getColumn(1).setMinWidth(80);
    _table.getColumnModel().getColumn(1).setMaxWidth(80);
    
    
    File workDir = new File(System.getProperty("user.home"));

    _jfc = new JFileChooser(workDir);
    _jfc.setDialogTitle("Select");
    _jfc.setApproveButtonText("Select");
    _jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    _jfc.setMultiSelectionEnabled(true);
    _fileFilter = ClassPathFilter.ONLY;
    
    final TableCellRenderer renderer = _table.getTableHeader().getDefaultRenderer();
    int w = renderer.getTableCellRendererComponent(_table,_table.getModel().getColumnName(1), false, false, 0, 1).getPreferredSize().width;
    _table.getColumnModel().getColumn(1).setPreferredWidth(w);
  }
  
  
  protected AbstractTableModel _makeTableModel() {
    return new AbstractTableModel() {
      public String getColumnName(int col) { return (_columnNames.length==0)?super.getColumnName(col):_columnNames[col]; }
      public int getRowCount() { return _data.size(); }
      public int getColumnCount() { return 2; }
      public Object getValueAt(int row, int col) {
        switch(col) {
          case 0: return _data.get(row);
          case 1: return _data.get(row).keepAbsolute();
        }
        throw new IllegalArgumentException("Illegal column");
      }
      public Class<?> getColumnClass(int col) {
        switch(col) {
          case 0: return String.class;
          case 1: return Boolean.class;
        }
        throw new IllegalArgumentException("Illegal column");
      }
      public boolean isCellEditable(int row, int col) {
        if (col<1) {
          return false;
        } else {
          return true;
        }
      }
      public void setValueAt(Object value, int row, int col) {
        AbsRelFile f = _data.get(row);
        switch(col) {
          case 1:
            f.keepAbsolute((Boolean)value);
            break;
          default:
            throw new IllegalArgumentException("Illegal column");
        }
        fireTableCellUpdated(row, col);
      }
    };
  }

  
  public void setFileFilter(FileFilter fileFilter) {
    _fileFilter = fileFilter;
  }
  
  
  public void setBaseDir(File f) {
    if (f.isDirectory()) { _baseDir = f; }
  }
  
  
  public void chooseFile() {
    int[] rows = _table.getSelectedRows();
    File selection = (rows.length==1)?_data.get(rows[0]):null;
    if (selection != null) {
      File parent = selection.getParentFile();
      if (parent != null) {
        _jfc.setCurrentDirectory(parent);
      }
    }
    else {
      if (_baseDir!=null) { _jfc.setCurrentDirectory(_baseDir); }
    }

    _jfc.setFileFilter(_fileFilter);

    File[] c = null;
    int returnValue = _jfc.showDialog(_parent, null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      c = _jfc.getSelectedFiles();
    }
    if (c != null) {
      _table.getSelectionModel().clearSelection();
      for(int i = 0; i < c.length; i++) {
        _addValue(new AbsRelFile(c[i]));
      }
    }
  }
  
  protected Action _getAddAction() {
    return new AbstractAction("Add") {
      public void actionPerformed(ActionEvent ae) {
        chooseFile();
      }
    };
  }
}
