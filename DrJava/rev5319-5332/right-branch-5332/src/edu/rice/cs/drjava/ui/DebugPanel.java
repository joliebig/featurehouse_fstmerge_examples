

package edu.rice.cs.drjava.ui;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.drjava.ui.avail.*;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.debug.*;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.swing.RightClickMouseAdapter;


public class DebugPanel extends JPanel implements OptionConstants {

  private JSplitPane _tabsPane;
  private JTabbedPane _leftPane;
  private JTabbedPane _rightPane;
  private JPanel _tabsAndStatusPane;

  private JTable _watchTable;
  private JTable _stackTable;
  private JTable _threadTable;
  private long _currentThreadID;

  
  private JPopupMenu _threadSuspendedPopupMenu;
  private JPopupMenu _stackPopupMenu;
  private JPopupMenu _watchPopupMenu;
  private DebugThreadData _threadInPopup;

  private final SingleDisplayModel _model;
  private final MainFrame _frame;
  private final Debugger _debugger;

  private JPanel _buttonPanel;
  private JButton _closeButton;
  private JButton _resumeButton;
  private JButton _automaticTraceButton;
  private JButton _stepIntoButton;
  private JButton _stepOverButton;
  private JButton _stepOutButton;
  private JLabel _statusBar;

  private ArrayList<DebugWatchData> _watches;
  private ArrayList<DebugThreadData> _threads;
  private ArrayList<DebugStackData> _stackFrames;
  
  


  
  public DebugPanel(MainFrame frame) {

    this.setLayout(new BorderLayout());

    _frame = frame;
    _model = frame.getModel();
    _debugger = _model.getDebugger();

    _watches = new ArrayList<DebugWatchData>();
    _threads = new ArrayList<DebugThreadData>();
    _stackFrames = new ArrayList<DebugStackData>();
    _leftPane = new JTabbedPane();
    _rightPane = new JTabbedPane();

    _setupTabPanes();

    _tabsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, _leftPane, _rightPane);
    _tabsPane.setOneTouchExpandable(true);
    _tabsPane.setDividerLocation((int)(_frame.getWidth()/2.5));

    _tabsAndStatusPane = new JPanel(new BorderLayout());
    _tabsAndStatusPane.add(_tabsPane, BorderLayout.CENTER);

    _statusBar = new JLabel("");
    _statusBar.setForeground(Color.blue.darker());

    _tabsAndStatusPane.add(_statusBar, BorderLayout.SOUTH);

    this.add(_tabsAndStatusPane, BorderLayout.CENTER);

    _buttonPanel = new JPanel(new BorderLayout());
    _setupButtonPanel();
    this.add(_buttonPanel, BorderLayout.EAST);

    _debugger.addListener(new DebugPanelListener());

    
    _setColors(_watchTable);
    _setColors(_stackTable);
    _setColors(_threadTable);
  }

  
  private static void _setColors(Component c) {
    new ForegroundColorListener(c);
    new BackgroundColorListener(c);
  }

  
  public void updateData() {
    assert EventQueue.isDispatchThread();
    if (_debugger.isReady()) {
      try {
        _watches = _debugger.getWatches();
        
        if (_debugger.isCurrentThreadSuspended())  _stackFrames = _debugger.getCurrentStackFrameData();
        else  _stackFrames = new ArrayList<DebugStackData>();
        
        _threads = _debugger.getCurrentThreadData();
      }
      catch (DebugException de) {
        
        MainFrameStatics.showDebugError(_frame, de);
      }
    }
    else {
      
      _watches = new ArrayList<DebugWatchData>();
      _threads = new ArrayList<DebugThreadData>();
      _stackFrames = new ArrayList<DebugStackData>();
    }

    ((AbstractTableModel)_watchTable.getModel()).fireTableDataChanged();
    ((AbstractTableModel)_stackTable.getModel()).fireTableDataChanged();
    ((AbstractTableModel)_threadTable.getModel()).fireTableDataChanged();
  }


  
  private void _setupTabPanes() {

    
    _initWatchTable();

    
    _stackTable = new JTable( new StackTableModel());
    _stackTable.addMouseListener(new StackMouseAdapter());

    _rightPane.addTab("Stack", new JScrollPane(_stackTable));

    
    _initThreadTable();

    
    TableColumn methodColumn;
    TableColumn lineColumn;
    methodColumn = _stackTable.getColumnModel().getColumn(0);
    lineColumn = _stackTable.getColumnModel().getColumn(1);
    methodColumn.setPreferredWidth(7*lineColumn.getPreferredWidth());

    _initPopup();
  }

  private void _initWatchTable() {
    _watchTable = new JTable( new WatchTableModel());
    _watchTable.setDefaultEditor(_watchTable.getColumnClass(0), new WatchEditor());
    _watchTable.setDefaultRenderer(_watchTable.getColumnClass(0), new WatchRenderer());

    _leftPane.addTab("Watches", new JScrollPane(_watchTable));
  }

  private void _initThreadTable() {
    _threadTable = new JTable(new ThreadTableModel());
    _threadTable.addMouseListener(new ThreadMouseAdapter());
    _rightPane.addTab("Threads", new JScrollPane(_threadTable));

    
    TableColumn nameColumn;
    TableColumn statusColumn;
    nameColumn = _threadTable.getColumnModel().getColumn(0);
    statusColumn = _threadTable.getColumnModel().getColumn(1);
    nameColumn.setPreferredWidth(2*statusColumn.getPreferredWidth());

    
    _currentThreadID = 0;
    TableCellRenderer threadTableRenderer = new DefaultTableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        Component renderer =
          super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        _setThreadCellFont(row);

        return renderer;
      }

      
      private void _setThreadCellFont(int row) {
        DebugThreadData currThread = _threads.get(row);
        if (currThread.getUniqueID() == _currentThreadID &&
            currThread.isSuspended()) {
          setFont(getFont().deriveFont(Font.BOLD));
        }
      }
    };
    _threadTable.getColumnModel().getColumn(0).setCellRenderer(threadTableRenderer);
    _threadTable.getColumnModel().getColumn(1).setCellRenderer(threadTableRenderer);
  }

  
  private static class WatchEditor extends DefaultCellEditor {

    WatchEditor() { super(new JTextField()); }

    
    public Component getTableCellEditorComponent
        (JTable table, Object value, boolean isSelected, int row, int column) {
      Component editor = super.getTableCellEditorComponent
        (table, value, isSelected, row, column);
      _setColors(editor);
      return editor;
    }
  }

  
  private class WatchRenderer extends DefaultTableCellRenderer {

    
    public Component getTableCellRendererComponent
        (JTable table, Object value, boolean isSelected, boolean hasFocus,
         int row, int column) {
      Component renderer = super.getTableCellRendererComponent
        (table, value, isSelected, hasFocus, row, column);
      _setColors(renderer);
      _setWatchCellFont(row);
      return renderer;
    }

    
    private void _setWatchCellFont(int row) {
      int numWatches = _watches.size();
      if (row < numWatches) {
        DebugWatchData currWatch = _watches.get(row);
        if (currWatch.isChanged()) {
          setFont(getFont().deriveFont(Font.BOLD));
        }
      }
    }
  }

  
  public class WatchTableModel extends AbstractTableModel {

    private String[] _columnNames = {"Name", "Value", "Type"};

    public String getColumnName(int col) { return _columnNames[col]; }
    public int getRowCount() { return _watches.size() + 1; }
    public int getColumnCount() { return _columnNames.length; }
    public Object getValueAt(int row, int col) {
      if (row < _watches.size()) {
        DebugWatchData watch = _watches.get(row);
        switch(col) {
          case 0: return watch.getName();
          case 1: return watch.getValue();
          case 2: return watch.getType();
        }
        fireTableRowsUpdated(row, _watches.size()-1);
        return null;
      }
      else {
        fireTableRowsUpdated(row, _watches.size()-1);
        
        return "";
      }
    }
    public boolean isCellEditable(int row, int col) {
      
      if (col == 0) return true;
      return false;
    }
    public void setValueAt(Object value, int row, int col) {
      try {
        if ((value == null) || (value.equals(""))) {
          
          _debugger.removeWatch(row);
        }
        else {
          if (row < _watches.size())
            _debugger.removeWatch(row);
          
          _debugger.addWatch(String.valueOf(value));
        }
        
        fireTableRowsUpdated(row, _watches.size()-1);
      }
      catch (DebugException de) { MainFrameStatics.showDebugError(_frame, de); }
    }
  }

  
  public class StackTableModel extends AbstractTableModel {

    private String[] _columnNames = {"Method", "Line"};  

    public String getColumnName(int col) { return _columnNames[col]; }
    
    public int getRowCount() {
      if (_stackFrames == null)  return 0;
      return _stackFrames.size();
    }
    public int getColumnCount() { return _columnNames.length; }

    public Object getValueAt(int row, int col) {
      DebugStackData frame = _stackFrames.get(row);
      switch(col) {
        case 0: return frame.getMethod();
        case 1: return Integer.valueOf(frame.getLine());
      }
      return null;
    }
    public boolean isCellEditable(int row, int col) {
      return false;
    }
  }

  
  public class ThreadTableModel extends AbstractTableModel {

    private String[] _columnNames = {"Name", "Status"};

    public String getColumnName(int col) { return _columnNames[col]; }

    public int getRowCount() {
      if (_threads == null) return 0;
      return _threads.size();
    }

    public int getColumnCount() {
      return _columnNames.length;
    }

    public Object getValueAt(int row, int col) {
      DebugThreadData threadData  = _threads.get(row);
      switch(col) {
        case 0: return threadData.getName();
        case 1: return threadData.getStatus();
        default: return null;
      }

    }

    public boolean isCellEditable(int row, int col) { return false; }
  }

  
  private void _setupButtonPanel() {
    JPanel mainButtons = new JPanel();
    JPanel emptyPanel = new JPanel();
    JPanel closeButtonPanel = new JPanel(new BorderLayout());
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    mainButtons.setLayout(gbLayout);
    
    Action resumeAction = new AbstractAction("Resume") {
      public void actionPerformed(ActionEvent ae) {
        try { _frame.debuggerResume(); }
        catch (DebugException de) { MainFrameStatics.showDebugError(_frame, de); }
      }
    };
    _resumeButton = new JButton(resumeAction);
    _frame._addGUIAvailabilityListener(_resumeButton,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER_SUSPENDED);
    
    Action automaticTrace = new AbstractAction("Automatic Trace") {
      public void actionPerformed(ActionEvent ae) {
        _frame.debuggerAutomaticTrace();
      }
    };
    _automaticTraceButton = new JButton(automaticTrace);
    _frame._addGUIAvailabilityListener(_automaticTraceButton,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER_SUSPENDED);
    
    Action stepIntoAction = new AbstractAction("Step Into") {
      public void actionPerformed(ActionEvent ae) {
        _frame.debuggerStep(Debugger.StepType.STEP_INTO);
      }
    };
    _stepIntoButton = new JButton(stepIntoAction);
    _frame._addGUIAvailabilityListener(_stepIntoButton,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER_SUSPENDED);

    Action stepOverAction = new AbstractAction("Step Over") {
      public void actionPerformed(ActionEvent ae) {
        _frame.debuggerStep(Debugger.StepType.STEP_OVER);
      }
    };
    _stepOverButton = new JButton(stepOverAction);
    _frame._addGUIAvailabilityListener(_stepOverButton,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER_SUSPENDED);

    Action stepOutAction = new AbstractAction( "Step Out" ) {
      public void actionPerformed(ActionEvent ae) {
        _frame.debuggerStep(Debugger.StepType.STEP_OUT);
      }
    };
    _stepOutButton = new JButton(stepOutAction);
    _frame._addGUIAvailabilityListener(_stepOutButton,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER,
                                      GUIAvailabilityListener.ComponentType.DEBUGGER_SUSPENDED);
    
    ActionListener closeListener =
      new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        _frame.debuggerToggle();
      }
    };

    _closeButton = new CommonCloseButton(closeListener);

    closeButtonPanel.add(_closeButton, BorderLayout.NORTH);
    mainButtons.add(_resumeButton);
    mainButtons.add(_automaticTraceButton);
    mainButtons.add(_stepIntoButton);
    mainButtons.add(_stepOverButton);
    mainButtons.add(_stepOutButton);
    mainButtons.add(emptyPanel);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    
    gbLayout.setConstraints(_resumeButton, c);
    gbLayout.setConstraints(_automaticTraceButton, c);
    gbLayout.setConstraints(_stepIntoButton, c);
    gbLayout.setConstraints(_stepOverButton, c);
    gbLayout.setConstraints(_stepOutButton, c);
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    updateButtons();
    _buttonPanel.add(mainButtons, BorderLayout.CENTER);
    _buttonPanel.add(closeButtonPanel, BorderLayout.EAST);
  }

  
  private void _initPopup() {
    
    
















    Action selectAction = new AbstractAction("Select Thread") {
      public void actionPerformed(ActionEvent e) { _selectCurrentThread(); }
    };

    _threadSuspendedPopupMenu = new JPopupMenu("Thread Selection");
    _threadSuspendedPopupMenu.add(selectAction);
    _threadSuspendedPopupMenu.add(new AbstractAction("Resume Thread") {
      public void actionPerformed(ActionEvent e) {
        try {
          if (_threadInPopup.isSuspended()) _debugger.resume(_threadInPopup);
        }
        catch (DebugException dbe) { MainFrameStatics.showDebugError(_frame, dbe); }
      }
    });

    _stackPopupMenu = new JPopupMenu("Stack Selection");
    _stackPopupMenu.add(new AbstractAction("Scroll to Source") {
      public void actionPerformed(ActionEvent e) {
        try {
          _debugger.scrollToSource(getSelectedStackItem());
        }
        catch (DebugException de) { MainFrameStatics.showDebugError(_frame, de); }
      }
    });

    _watchPopupMenu = new JPopupMenu("Watches");
    _watchPopupMenu.add(new AbstractAction("Remove Watch") {
      public void actionPerformed(ActionEvent e) {
        try {
          _debugger.removeWatch(_watchTable.getSelectedRow());
          _watchTable.revalidate();
          _watchTable.repaint();
        }
        catch (DebugException de) { MainFrameStatics.showDebugError(_frame, de); }
      }
    });
    _watchTable.addMouseListener(new DebugTableMouseAdapter(_watchTable) {
      protected void _showPopup(MouseEvent e) {
        if (_watchTable.getSelectedRow() < _watchTable.getRowCount() - 1) {
          _watchPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
      protected void _action() {
      }
    });
  }

  
  private void _selectCurrentThread() {
    if (_threadInPopup.isSuspended()) {
      try { _debugger.setCurrentThread(_threadInPopup); }
      catch(DebugException de) { MainFrameStatics.showDebugError(_frame, de); }
    }
  }

  
  public DebugThreadData getSelectedThread() {
    int row = _threadTable.getSelectedRow();
    if (row == -1) {
      row = 0;  
    }
    return _threads.get(row);
  }

  
  public DebugStackData getSelectedStackItem() {
    return _stackFrames.get(_stackTable.getSelectedRow());
  }

  
  public DebugWatchData getSelectedWatch() {
    return _watches.get(_watchTable.getSelectedRow());
  }

  
  class DebugPanelListener implements DebugListener {
    
    public void currThreadSuspended() {
      
      Utilities.invokeLater(new Runnable() { public void run() { updateData(); } });
    }

    
    public void currThreadResumed() {
      
      Utilities.invokeLater(new Runnable() { public void run() { updateData(); } });
    }

    
    public void threadStarted() { updateData(); }

    
    public void currThreadDied() { updateData(); }

    
    public void nonCurrThreadDied() { updateData(); }

    
    public void currThreadSet(DebugThreadData thread) {
      _currentThreadID = thread.getUniqueID();

      
      Utilities.invokeLater(new Runnable() { public void run() { updateData(); } });
    }
    
    public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) { }
    public void debuggerStarted() { }
    public void debuggerShutdown() { }
    public void breakpointReached(final Breakpoint bp) { }
    public void watchSet(final DebugWatchData w) { }
    public void watchRemoved(final DebugWatchData w) { }
    public void stepRequested() { }
    public void regionAdded(Breakpoint r) { }
    public void regionChanged(Breakpoint r) { }
    public void regionRemoved(Breakpoint r) { }
  }

  public void updateButtons() {

  }

  
  public void setAutomaticTraceButtonText() {
    if(_model.getDebugger().isAutomaticTraceEnabled()) {
      _automaticTraceButton.setText("Disable Trace"); 
    }
    else { 
      _automaticTraceButton.setText("Automatic Trace");
    }
    _frame.setAutomaticTraceMenuItemStatus();
  }
  
  public void setStatusText(String text) { _statusBar.setText(text); }

  public String getStatusText() { return _statusBar.getText(); }

  

  
  private class ThreadMouseAdapter extends DebugTableMouseAdapter {
    public ThreadMouseAdapter() {
      super(_threadTable);
    }

    protected void _showPopup(MouseEvent e) {
      _threadInPopup = _threads.get(_lastRow);
      if (_threadInPopup.isSuspended()) {
         _threadSuspendedPopupMenu.show(e.getComponent(), e.getX(), e.getY());
      }



    }

    protected void _action() {
      _threadInPopup = _threads.get(_lastRow);
      _selectCurrentThread();
    }
  }

  
  private class StackMouseAdapter extends DebugTableMouseAdapter {
    public StackMouseAdapter() {
      super(_stackTable);
    }

    protected void _showPopup(MouseEvent e) {
      _stackPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    protected void _action() {
      try {
        _debugger.scrollToSource(_stackFrames.get(_lastRow));
      }
      catch (DebugException de) {
        MainFrameStatics.showDebugError(_frame, de);
      }
    }
  }

  
  private abstract class DebugTableMouseAdapter extends RightClickMouseAdapter {
    protected JTable _table;
    protected int _lastRow;

    public DebugTableMouseAdapter(JTable table) {
      _table = table;
      _lastRow = -1;
    }

    protected abstract void _showPopup(MouseEvent e);
    protected abstract void _action();

    protected void _popupAction(MouseEvent e) {
      _lastRow = _table.rowAtPoint(e.getPoint());
      _table.setRowSelectionInterval(_lastRow, _lastRow);
      _showPopup(e);
    }

    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);

      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
        _lastRow = _table.rowAtPoint(e.getPoint());
        _action();
      }
    }
  }
  

}
