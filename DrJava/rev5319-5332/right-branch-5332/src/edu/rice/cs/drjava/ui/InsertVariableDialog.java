

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.PropertyMaps;
import edu.rice.cs.drjava.config.DrJavaProperty;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class InsertVariableDialog extends SwingFrame implements OptionConstants {
  
  JTabbedPane _tabbedPane = new JTabbedPane();
  
  
  private Map<String, JTable> _varTable = new HashMap<String, JTable>();
  
  
  private Map<String, DefaultTableModel> _varTableModel = new HashMap<String, DefaultTableModel>();
  
  
  private JTextField _varValueField;
  
  
  private JTextPane _helpPane;
  
  
  private JButton _okBtn;
  
  
  private JButton _cancelBtn;
  
  
  private MainFrame _mainFrame;
  
  
  private Pair<String,DrJavaProperty> _selected = null;
  
  
  private CompletionMonitor _cm;
  
  
  public InsertVariableDialog(MainFrame mf, CompletionMonitor cm) {
    super("Insert Variable");
    _mainFrame = mf;
    _cm = cm;
    initComponents();
    initDone();  
    pack();        
    Utilities.setPopupLoc(InsertVariableDialog.this, _mainFrame);
  }
  
  
  private void initComponents() {
    super.getContentPane().setLayout(new GridLayout(1,1));
    
    Action okAction = new AbstractAction("Select") {
      public void actionPerformed(ActionEvent e) {
        _okCommand();
      }
    };
    _okBtn = new JButton(okAction);
    
    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed(ActionEvent e) {
        _cancelCommand();
      }
    };
    _cancelBtn = new JButton(cancelAction);
    
    JPanel buttons = new JPanel();
    buttons.add(_okBtn);
    buttons.add(_cancelBtn);
    
    _helpPane = new JTextPane();
    _helpPane.setToolTipText("Description of the variable.");
    _helpPane.setEditable(false);
    _helpPane.setPreferredSize(new Dimension(500,150));
    _helpPane.setBorder(new javax.swing.border.EmptyBorder(0,10,0,10));
    JScrollPane helpPaneSP = new JScrollPane(_helpPane);
    helpPaneSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      
    _varValueField = new JTextField();
    updatePanes();
    _tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (_tabbedPane.getSelectedIndex() < 0) { return; }
        String category = _tabbedPane.getTitleAt(_tabbedPane.getSelectedIndex());
        Map<String, DrJavaProperty> properties = PropertyMaps.TEMPLATE.getProperties(category);
        int row = _varTable.get(category).getSelectedRow();
        if (row < 0) { return; }
        String key = _varTableModel.get(category).getValueAt(row,0).toString();
        DrJavaProperty value = properties.get(key);
        _varValueField.setText(value.toString());
        _helpPane.setText(value.getHelp());
        _helpPane.setCaretPosition(0);
        _selected = Pair.make(key, value);
      }
    });
    
    JPanel main = new JPanel(new BorderLayout());
    
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.add(_varValueField, BorderLayout.CENTER);    
    bottom.add(buttons, BorderLayout.SOUTH);
    main.add(bottom, BorderLayout.SOUTH);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    JPanel top = new JPanel(gridbag);
    Insets insets = new Insets(0, 10, 5, 10);

    
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1.0;
    c.weighty = 3.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(_tabbedPane, c);
    top.add(_tabbedPane);

    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1.0;
    c.insets = insets;
    gridbag.setConstraints(helpPaneSP, c);
    top.add(helpPaneSP);
    main.add(top, BorderLayout.CENTER);
    
    
    _tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    
    
    _tabbedPane.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        if (e.getOppositeComponent() == _varValueField) {
          _tabbedPane.getSelectedComponent().requestFocus();
        }
      }
    });
    
    super.getContentPane().add(main);
    super.setResizable(false);
  }
  
  
  protected JScrollPane createPane(final String category, final Map<String, DrJavaProperty> props) {
    _varTableModel.put(category,new DefaultTableModel(0,1) {
      public String getColumnName(int column) {
        switch(column) {
          case 0: return "Variable";
          default: return super.getColumnName(column);
        }
      }
      
      public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
          case 0: return String.class;
          default: return super.getColumnClass(columnIndex);
        }
      }
      public boolean isCellEditable(int row, int column) { return false; }
    });
    
    _varTable.put(category, new JTable(_varTableModel.get(category)));
    JScrollPane varTableSP = new JScrollPane(_varTable.get(category));
    varTableSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    varTableSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    _varTable.get(category).setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _varTable.get(category).setDragEnabled(false);
    _varTable.get(category).setPreferredScrollableViewportSize(new Dimension(500,250));
    _varTable.get(category).putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
    ListSelectionModel lsm = _varTable.get(category).getSelectionModel();
    lsm.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        int row = _varTable.get(category).getSelectedRow();
        if (row < 0) { return; }
        String key = _varTableModel.get(category).getValueAt(row,0).toString();
        DrJavaProperty value = PropertyMaps.TEMPLATE.getProperty(category,key);
        _selected = Pair.make(key, value);
        _varValueField.setText(value.getLazy(PropertyMaps.TEMPLATE));
        _helpPane.setText(value.getHelp());
        _helpPane.setCaretPosition(0);
      }
    });
    _varTable.get(category).setSelectionModel(lsm);
    
    TreeSet<String> sorted = new TreeSet<String>();
    for(DrJavaProperty p: PropertyMaps.TEMPLATE.getProperties(category).values()) {
      sorted.add(p.getName());
    }
    
    for(String key: sorted) {
      Vector<String> row = new Vector<String>();  
      row.add(key);
      _varTableModel.get(category).addRow(row);
    }
    
    _varTable.get(category).setRowSelectionInterval(0,0);
    
    return varTableSP;
  }
  
  
  protected void _okCommand() {
    setVisible(false);
    _cm.signal();
  }
  
  
  protected void _cancelCommand() {
    _selected = null;
    setVisible(false);
    _cm.signal();
  }
  
  
  protected void updatePanes() {
    Pair<String,DrJavaProperty> sel = getSelected();
    String selCategory = null;
    if (sel != null) {
      selCategory = _tabbedPane.getTitleAt(_tabbedPane.getSelectedIndex());
    }
    _tabbedPane.removeAll();
    for (String category: PropertyMaps.TEMPLATE.getCategories()) {
      _tabbedPane.addTab(category, createPane(category, PropertyMaps.TEMPLATE.getProperties(category)));
    }
    if (sel != null) {
      if (selCategory == null) { sel = null; } else {
        int i;
        for (i = 0; i < _tabbedPane.getTabCount(); ++i) {
          if (_tabbedPane.getTitleAt(i).equals(selCategory)) { _tabbedPane.setSelectedIndex(i); break; }
        }
        if (i == _tabbedPane.getTabCount()) { sel = null; } else {
          DefaultTableModel tm = _varTableModel.get(selCategory);
          for (i = 0; i < tm.getRowCount(); ++i) {
            String key = tm.getValueAt(i,0).toString();
            if (key.equals(sel.second().getName())) {
              _varTable.get(selCategory).getSelectionModel().setSelectionInterval(i,i);
              break;
            }
          }
          if (i==tm.getRowCount()) {
            
            _varTable.get(selCategory).getSelectionModel().setSelectionInterval(0,0);
          }
          _varValueField.setText(sel.second().toString());
          _helpPane.setText(sel.second().getHelp());
          _helpPane.setCaretPosition(0);
          _selected = sel;
        }
      }
    }
    if (sel == null) {
      _tabbedPane.setSelectedIndex(0);
      String category = _tabbedPane.getTitleAt(_tabbedPane.getSelectedIndex());
      Map<String, DrJavaProperty> properties = PropertyMaps.TEMPLATE.getProperties(category);
      _varTable.get(category).getSelectionModel().setSelectionInterval(0,0);
      int row = _varTable.get(category).getSelectedRow();
      if (row >= 0) {
        String key = _varTableModel.get(category).getValueAt(row,0).toString();
        DrJavaProperty value = properties.get(key);
        _varValueField.setText(value.toString());
        _helpPane.setText(value.getHelp());
        _helpPane.setCaretPosition(0);
        _selected = Pair.make(key, value);
      }
    }
  }

  
  public Pair<String,DrJavaProperty> getSelected() { return _selected; }
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { _cancelCommand(); }
  };

  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      updatePanes();
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
}
