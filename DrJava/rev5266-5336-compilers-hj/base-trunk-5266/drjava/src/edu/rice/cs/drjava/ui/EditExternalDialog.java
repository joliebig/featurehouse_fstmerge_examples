

package edu.rice.cs.drjava.ui;

import java.awt.EventQueue;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.DropDownButton;
import edu.rice.cs.util.swing.SwingFrame;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;



public class EditExternalDialog extends SwingFrame implements OptionConstants {
  private static final int FRAME_WIDTH = 503;
  private static final int FRAME_HEIGHT = 318;
  
  
  public static class FrameState {
    private Point _loc;
    public FrameState(Point l) {
      _loc = l;
    }
    public FrameState(String s) {
      StringTokenizer tok = new StringTokenizer(s);
      try {
        int x = Integer.valueOf(tok.nextToken());
        int y = Integer.valueOf(tok.nextToken());
        _loc = new Point(x, y);
      }
      catch(NoSuchElementException nsee) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nsee);
      }
      catch(NumberFormatException nfe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nfe);
      }
    }
    public FrameState(EditExternalDialog comp) {
      _loc = comp.getLocation();
    }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(_loc.x);
      sb.append(' ');
      sb.append(_loc.y);
      return sb.toString();
    }
    public Point getLocation() { return _loc; }
  }
  
  
  private JButton _editButton;
  
  private JButton _removeButton;
  
  private JButton _upButton;
  
  private JButton _downButton;
  
  private JButton _importButton;
  
  private JButton _exportButton;
  
  private Action _upAction;
  
  private Action _downAction;
  
  private Action _importAction;
  
  private Action _exportAction;
  
  private DropDownButton _dropDownButton;
  
  private JButton _okButton;
  
  private JList _list;
  
  protected CompletionMonitor _editExternalDialogMonitor = new CompletionMonitor();
  
  
  private final FileFilter _extProcFilter = new javax.swing.filechooser.FileFilter() {
    public boolean accept(File f) {
      return f.isDirectory() || 
        f.getPath().endsWith(OptionConstants.EXTPROCESS_FILE_EXTENSION);
    }
    public String getDescription() { 
      return "DrJava External Process Files (*" + EXTPROCESS_FILE_EXTENSION + ")";
    }
  };

  
  private final FileFilter _saveExtProcFilter = new javax.swing.filechooser.FileFilter() {
    public boolean accept(File f) {
      return f.isDirectory() || 
        f.getPath().endsWith(OptionConstants.EXTPROCESS_FILE_EXTENSION);
    }
    public String getDescription() { 
      return "DrJava External Process Files (*" + PROJECT_FILE_EXTENSION + ")";
    }
  };

  
  private JFileChooser _importChooser;

  
  private JFileChooser _exportChooser;

  
  protected MainFrame _mainFrame;

  
  protected FrameState _lastState = null;
  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState != null) {
      setLocation(_lastState.getLocation());
      validate();
    }
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
    if (_lastState != null) setLocation(_lastState.getLocation());
    else edu.rice.cs.util.swing.Utilities.setPopupLoc(this, _mainFrame);
    validate();
  }

  
  public EditExternalDialog(MainFrame mf) {
    super("Edit External Processes");
    _mainFrame = mf;
    initComponents();
    initDone();   
  }

  
  private void initComponents() {
    super.getContentPane().setLayout(new GridLayout(1,1));

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    
    Action editAction = new AbstractAction("Edit") {
      public void actionPerformed(ActionEvent e) {
        _edit();
      }
    };
    _editButton = new JButton(editAction);
    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent e) {
        _remove();
      }
    };
    _removeButton = new JButton(removeAction);

    _dropDownButton = new DropDownButton();
    _upAction = new AbstractAction("Move Up") {
      public void actionPerformed(ActionEvent e) {
        _up();
      }
    };
    _downAction = new AbstractAction("Move Down") {
      public void actionPerformed(ActionEvent e) {
        _down();
      }
    };
    
    _importAction = new AbstractAction("Import...") {
      public void actionPerformed(ActionEvent e) {
        _import();
      }
    };
    _exportAction = new AbstractAction("Export...") {
      public void actionPerformed(ActionEvent e) {
        _export();
      }
    };
    
    _importButton = new JButton(_importAction);
    _exportButton = new JButton(_exportAction);
    _upButton = new JButton(_upAction);
    _downButton = new JButton(_downAction);

    _dropDownButton.getPopupMenu().add(_importAction);
    _dropDownButton.getPopupMenu().add(_exportAction);
    
    _dropDownButton.setIcon(new ImageIcon(getClass().getResource("/edu/rice/cs/drjava/ui/icons/Down16.gif")));

    Action okAction = new AbstractAction("OK") {
      public void actionPerformed(ActionEvent e) {
        _ok();
      }
    };
    _okButton = new JButton(okAction);
    
    JPanel bottom = new JPanel();
    bottom.setBorder(new EmptyBorder(5, 5, 5, 5));
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.add(Box.createHorizontalGlue());
    bottom.add(_editButton);
    bottom.add(_removeButton);
    bottom.add(_upButton);
    bottom.add(_downButton);
    bottom.add(_dropDownButton);
    bottom.add(_okButton);
    bottom.add(Box.createHorizontalGlue());
    mainPanel.add(bottom, BorderLayout.SOUTH);

    _list = new JList();
    _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        _upButton.setEnabled(_list.getSelectedIndex() > 0);
        _upAction.setEnabled(_list.getSelectedIndex() > 0);
        _downButton.setEnabled(_list.getSelectedIndex() < _list.getModel().getSize());
        _downAction.setEnabled(_list.getSelectedIndex() < _list.getModel().getSize());
      }
    });
    JScrollPane sp = new JScrollPane(_list);
    sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    mainPanel.add(sp, BorderLayout.CENTER);
    updateList(0);
    
    super.getContentPane().add(mainPanel);
    super.setResizable(false);
    
    setSize(FRAME_WIDTH, FRAME_HEIGHT);
    edu.rice.cs.util.swing.Utilities.setPopupLoc(this, _mainFrame);
    
    _importChooser = new JFileChooser() {
      public void setCurrentDirectory(File dir) {
        
        super.setCurrentDirectory(dir);
        setDialogTitle("Import:  " + getCurrentDirectory());
      }
    };
    _importChooser.setPreferredSize(new Dimension(650, 410));
    _importChooser.setFileFilter(_extProcFilter);
    _importChooser.setMultiSelectionEnabled(false);

    _exportChooser = new JFileChooser() {
      public void setCurrentDirectory(File dir) {
        
        super.setCurrentDirectory(dir);
        setDialogTitle("Export:  " + getCurrentDirectory());
      }
    };
    _exportChooser.setPreferredSize(new Dimension(650, 410));
    _exportChooser.setFileFilter(_saveExtProcFilter);
  }

  
  private void _ok() {
    _lastState = new FrameState(this);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_COUNT,
                                  DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT));
    this.setVisible(false);
  }
  
  
  private void _edit() {
    final int selectedIndex = _list.getSelectedIndex();
    if ((selectedIndex < 0) || (selectedIndex>=DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT))) {
      return;
    }
    _mainFrame.removeModalWindowAdapter(this);
    _editExternalDialogMonitor.reset();
    final ExecuteExternalDialog dialog = new ExecuteExternalDialog(_mainFrame,true,selectedIndex,_editExternalDialogMonitor);
    dialog.setVisible(true);
    
    
    new Thread(new Runnable() {
      public void run() {
        _editExternalDialogMonitor.attemptEnsureSignaled();
        
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            EventQueue.invokeLater(new Runnable() { public void run() { EditExternalDialog.this.toFront(); } });
            _mainFrame.installModalWindowAdapter(EditExternalDialog.this, LambdaUtil.NO_OP, OK);
            updateList(selectedIndex);
          }
        });
      }
    }).start();
  }

  
  private void _remove() {
    int count = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT);
    final int selectedIndex = _list.getSelectedIndex();
    if ((selectedIndex < 0) ||
        (selectedIndex>=DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT)) ||
        (count<=0)) {
      _removeButton.setEnabled(false);
      return;
    }

    Vector<String> v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    v.remove(selectedIndex);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_NAMES,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    v.remove(selectedIndex);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    v.remove(selectedIndex);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    v.remove(selectedIndex);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES,v);

    --count;
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_COUNT, count);
    updateList(Math.max(0, selectedIndex-1));
  }

  
  private void _up() {
    final int count = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT);
    final int selectedIndex = _list.getSelectedIndex();
    if ((selectedIndex<1) ||
        (selectedIndex>=DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT)) ||
        (count<=0)) {
      _removeButton.setEnabled(false);
      return;
    }

    Vector<String> v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    String s = v.remove(selectedIndex);
    v.add(selectedIndex-1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_NAMES,v);
    
    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    s = v.remove(selectedIndex);
    v.add(selectedIndex-1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    s = v.remove(selectedIndex);
    v.add(selectedIndex-1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    s = v.remove(selectedIndex);
    v.add(selectedIndex-1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES,v);

    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_COUNT, count);
    updateList(Math.max(0,selectedIndex-1));
  }

  
  private void _down() {
    final int count = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT);
    final int selectedIndex = _list.getSelectedIndex();
    if ((selectedIndex < 0) ||
        (selectedIndex>=DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT)-1) ||
        (count<=0)) {
      _removeButton.setEnabled(false);
      return;
    }

    Vector<String> v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    String s = v.remove(selectedIndex);
    v.add(selectedIndex+1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_NAMES,v);
    
    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    s = v.remove(selectedIndex);
    v.add(selectedIndex+1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    s = v.remove(selectedIndex);
    v.add(selectedIndex+1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS,v);

    v = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    s = v.remove(selectedIndex);
    v.add(selectedIndex+1,s);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES,v);

    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_COUNT, count);
    updateList(Math.max(0,selectedIndex+1));
  }
  
  
  public void _import() {
    _mainFrame.removeModalWindowAdapter(this);
    int rc = _importChooser.showOpenDialog(this);
    _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, OK);
    switch (rc) {
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION: {
        return;
      }
      
      case JFileChooser.APPROVE_OPTION: {
        File[] chosen = _importChooser.getSelectedFiles();
        if (chosen == null) {
          return;
        } 
        
        
        if (chosen.length == 0) {
          File f = _importChooser.getSelectedFile();
          MainFrame.openExtProcessFile(f);
          updateList(DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT)-1);
        }
        return;
      }
        
      default: 
        throw new UnexpectedException();
    }
  }
  
  
  public void _export() {
    
    _exportChooser.setMultiSelectionEnabled(false);
    _mainFrame.removeModalWindowAdapter(this);
    int rc = _exportChooser.showSaveDialog(this);
    _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, OK);
    switch (rc) {
      case JFileChooser.CANCEL_OPTION:
      case JFileChooser.ERROR_OPTION: {
        
        return;
      }
      
      case JFileChooser.APPROVE_OPTION: {
        
        File[] chosen = _exportChooser.getSelectedFiles();
        if (chosen == null) {
          
          return;
        } 
        
        
        
        if (chosen.length == 0) {
          File f = _exportChooser.getSelectedFile();
          if (!f.getName().endsWith(OptionConstants.EXTPROCESS_FILE_EXTENSION)) {
            f = new File(f.getAbsolutePath()+OptionConstants.EXTPROCESS_FILE_EXTENSION);
          }
          
          ExecuteExternalDialog.saveToFile(_list.getSelectedIndex(), f);
        }
        return;
      }
        
      default: 
        throw new UnexpectedException();
    }
  }
  
  
  public void updateList(int selectedIndex) {
    final Vector<String> names = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    _list.setListData(names);
    _editButton.setEnabled(names.size() > 0);
    _removeButton.setEnabled(names.size() > 0);
    if (names.size() > 0) {
      _list.setSelectedIndex(selectedIndex);
    }
    else {
      _list.clearSelection();
    }
    _upButton.setEnabled((_list.getModel().getSize() > 0) &&
                         (_list.getSelectedIndex() > 0));
    _upAction.setEnabled((_list.getModel().getSize() > 0) &&
                         (_list.getSelectedIndex() > 0));
    _downButton.setEnabled((_list.getModel().getSize() > 0) &&
                           (_list.getSelectedIndex() < _list.getModel().getSize()-1));
    _downAction.setEnabled((_list.getModel().getSize() > 0) &&
                           (_list.getSelectedIndex() < _list.getModel().getSize()-1));
    _exportButton.setEnabled(names.size() > 0);
    _exportAction.setEnabled(names.size() > 0);
  }

  
  protected final Runnable1<WindowEvent> OK = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { _ok(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      updateList(0);
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, OK);
      toFront();
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
}
