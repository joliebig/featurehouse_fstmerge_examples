

package edu.rice.cs.util.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.table.AbstractTableModel;


public class ScrollableListSelectionDialog extends JDialog {
  
  public enum SelectionState {
    
    SELECTED,
      
      UNSELECTED
  };
  
  
  private static final int DEFAULT_WIDTH = 400;
  
  private static final int DEFAULT_HEIGHT = 450;
  
  
  private static final double WIDTH_RATIO = .75;
  
  private static final double HEIGHT_RATIO = .50;
  
  
  protected final JTable table;
  
  protected final AbstractTableModel tableModel;
  
  
  private static final int NUM_COLUMNS = 2;
  
  private static final int CHECKBOXES_COLUMN_INDEX = 0;
  
  private static final int STRINGS_COLUMN_INDEX = 1;
  
  
  protected final Vector<String> dataAsStrings;
  
  protected final Vector<Boolean> selectedItems;
  
  
  public ScrollableListSelectionDialog(final Frame owner,
                                       final String dialogTitle,
                                       final String leaderText,
                                       final Collection<?> listItems,
                                       final String itemDescription) {
    this(owner, dialogTitle, leaderText, listItems, itemDescription, SelectionState.SELECTED, JOptionPane.PLAIN_MESSAGE);
  }
  
  
  public ScrollableListSelectionDialog(final Frame owner,
                                       final String dialogTitle,
                                       final String leaderText,
                                       final Collection<?> listItems,
                                       final String itemDescription,
                                       final SelectionState defaultSelection,
                                       final int messageType) {
    this(owner,
         dialogTitle,
         leaderText,
         listItems,
         itemDescription,
         defaultSelection,
         messageType,
         DEFAULT_WIDTH,
         DEFAULT_HEIGHT,
         null,
         true);
  }
  
  
  public ScrollableListSelectionDialog(final Frame owner,
                                       final String dialogTitle,
                                       final String leaderText,
                                       final Collection<?> listItems,
                                       final String itemDescription,
                                       final SelectionState defaultSelection,
                                       final int messageType,
                                       final int width,
                                       final int height,
                                       final Icon icon) {
    this(owner,
         dialogTitle,
         leaderText,
         listItems,
         itemDescription,
         defaultSelection,
         messageType,
         width,
         height,
         icon,
         false);
  }
  
  
  private ScrollableListSelectionDialog(final Frame owner,
                                        final String dialogTitle,
                                        final String leaderText,
                                        final Collection<?> listItems,
                                        final String itemDescription,
                                        final SelectionState defaultSelection,
                                        final int messageType,
                                        final int width,
                                        final int height,
                                        final Icon icon,
                                        final boolean fitToScreen) {
    super(owner, dialogTitle, true);
    
    if (!_isknownMessageType(messageType)) {
      throw new IllegalArgumentException("The message type \"" + messageType + "\" is unknown");
    }
    
    if (listItems == null) {
      throw new IllegalArgumentException("listItems cannot be null");
    }
    
    
    JLabel dialogIconLabel = null;
    if (icon != null) {
      
      dialogIconLabel = new JLabel(icon);
    } else {
      
      Icon messageIcon = _getIcon(messageType);
      if (messageIcon != null) {
        dialogIconLabel = new JLabel(messageIcon); 
      }
    }
    
    final JPanel leaderPanel = new JPanel();
    final JLabel leaderLabel = new JLabel(leaderText);
    leaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    if (dialogIconLabel != null) {
      leaderPanel.add(dialogIconLabel);
    }
    leaderPanel.add(leaderLabel);
    
    
    
    dataAsStrings = new Vector<String>(listItems.size());
    for (Object obj : listItems) {
      if (obj != null) {
        final String objAsString = obj.toString();
        dataAsStrings.add(objAsString);
      }
    }
    dataAsStrings.trimToSize();
    
    final int numItems = dataAsStrings.size();
    
    selectedItems = new Vector<Boolean>(numItems);
    synchronized(selectedItems) {
      for (int i = 0; i < numItems; ++i) {
        selectedItems.add(i, defaultSelection == SelectionState.SELECTED);
      }
      selectedItems.trimToSize();
    }
    assert selectedItems.size() == dataAsStrings.size();
    
    tableModel = new AbstractTableModel() {
      
      public int getRowCount() {
        return numItems;
      }
      
      
      public int getColumnCount() {
        return NUM_COLUMNS;
      }
      
      
      public Object getValueAt(int row, int column) {
        if (column == CHECKBOXES_COLUMN_INDEX) {
          assert row >= 0;
          assert row < numItems;
          synchronized(selectedItems) {
            return selectedItems.get(row);
          }
        } else if (column == STRINGS_COLUMN_INDEX) {
          assert row >= 0;
          assert row < numItems;
          return dataAsStrings.get(row);
        } else {
          assert false;
          return null;
        }
      }
      
      @Override
      public String getColumnName(int column) {
        if (column == CHECKBOXES_COLUMN_INDEX) {
          return "";
        } else if (column == STRINGS_COLUMN_INDEX) {
          return itemDescription;
        } else {
          assert false;
          return "";
        }
      }
      
      @Override
      public Class<?> getColumnClass(final int columnIndex) {
        if (columnIndex == CHECKBOXES_COLUMN_INDEX) {
          return Boolean.class;
        } else if (columnIndex == STRINGS_COLUMN_INDEX) {
          return String.class;
        } else {
          assert false;
          return Object.class;
        }
      }
      
      @Override
      public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex == CHECKBOXES_COLUMN_INDEX; 
      }
      
      @Override
      public void setValueAt(final Object newValue, final int rowIndex, final int columnIndex) {
        assert columnIndex == CHECKBOXES_COLUMN_INDEX;
        assert rowIndex >= 0;
        assert rowIndex < numItems;
        assert newValue instanceof Boolean;
        
        final Boolean booleanValue = (Boolean)newValue;
        
        synchronized(selectedItems) {
          selectedItems.set(rowIndex, booleanValue);
        }
      }
    };
    
    table = new JTable(tableModel);
    
    
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(final MouseEvent e) {
        final Point clickPoint = e.getPoint();
        
        final int clickColumn = table.columnAtPoint(clickPoint);
        
        if (clickColumn == STRINGS_COLUMN_INDEX) {
          
          
          final int clickRow = table.rowAtPoint(clickPoint);
          
          if (clickRow >= 0 && clickRow < numItems) {
            synchronized(selectedItems) {
              final boolean currentValue = selectedItems.get(clickRow);
              final boolean newValue = !currentValue;
              
              selectedItems.set(clickRow, newValue);
              
              tableModel.fireTableCellUpdated(clickRow, CHECKBOXES_COLUMN_INDEX);
            }
          }
        }
      }
    });
    
    
    table.getColumnModel().getColumn(CHECKBOXES_COLUMN_INDEX).setMinWidth(15);
    table.getColumnModel().getColumn(CHECKBOXES_COLUMN_INDEX).setMaxWidth(30);
    table.getColumnModel().getColumn(CHECKBOXES_COLUMN_INDEX).setPreferredWidth(20);
    table.getColumnModel().getColumn(CHECKBOXES_COLUMN_INDEX).sizeWidthToFit();
    
    
    final JScrollPane scrollPane = new JScrollPane(table);
    
    
    final JPanel selectButtonsPanel = new JPanel();
    selectButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    _addSelectButtons(selectButtonsPanel);
    
    
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
    _addButtons(buttonPanel);
    
    
    final JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BorderLayout());
    centerPanel.add(selectButtonsPanel, BorderLayout.NORTH);
    centerPanel.add(scrollPane, BorderLayout.CENTER);
    
    
    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout(10, 5));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
    
    contentPanel.add(leaderPanel, BorderLayout.NORTH);
    contentPanel.add(centerPanel, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    getContentPane().add(contentPanel);
    
    
    final Dimension dialogSize = new Dimension();
    
    if (fitToScreen) {
      
      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int screenBasedWidth = (int) (WIDTH_RATIO * screenSize.getWidth());
      int screenBasedHeight = (int) (HEIGHT_RATIO * screenSize.getHeight());
      
      dialogSize.setSize(Math.max(DEFAULT_WIDTH, screenBasedWidth),
                         Math.max(DEFAULT_HEIGHT, screenBasedHeight));
    } else {
      
      dialogSize.setSize(width, height);
    }
    
    setSize(dialogSize);
  }
  
  
  private boolean _isknownMessageType(final int messageType) {
    return messageType == JOptionPane.ERROR_MESSAGE ||
      messageType == JOptionPane.INFORMATION_MESSAGE ||
      messageType == JOptionPane.WARNING_MESSAGE ||
      messageType == JOptionPane.QUESTION_MESSAGE ||
      messageType == JOptionPane.PLAIN_MESSAGE;
  }
  
  
  private Icon _getIcon(final int messageType) {
    assert _isknownMessageType(messageType);
    
    
    if (messageType == JOptionPane.ERROR_MESSAGE) {
      return UIManager.getIcon("OptionPane.errorIcon");
    } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
      return UIManager.getIcon("OptionPane.informationIcon");
    } else if (messageType == JOptionPane.WARNING_MESSAGE) {
      return UIManager.getIcon("OptionPane.warningIcon");
    } else if (messageType == JOptionPane.QUESTION_MESSAGE) {
      return UIManager.getIcon("OptionPane.questionIcon");
    } else if (messageType == JOptionPane.PLAIN_MESSAGE) {
      return null;
    } else {
      
      assert false;
    }
    
    return null;
  }
  
  
  private void _addSelectButtons(final JPanel selectButtonsPanel) {
    final JButton selectAllButton = new JButton("Select All");
    edu.rice.cs.drjava.platform.PlatformFactory.ONLY.setMnemonic(selectAllButton,KeyEvent.VK_A);
    selectAllButton.addActionListener(new SelectAllNoneActionListener(SelectionState.SELECTED));
    selectButtonsPanel.add(selectAllButton);
    
    final JButton selectNoneButton = new JButton("Select None");
    edu.rice.cs.drjava.platform.PlatformFactory.ONLY.setMnemonic(selectNoneButton,KeyEvent.VK_N);
    selectNoneButton.addActionListener(new SelectAllNoneActionListener(SelectionState.UNSELECTED));
    selectButtonsPanel.add(selectNoneButton);
  }
  
  
  protected void _addButtons(final JPanel buttonPanel) {
    final JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent notUsed) {
        closeDialog();
      }
    });
    
    buttonPanel.add(okButton);
    getRootPane().setDefaultButton(okButton);
  }
  
  
  public void showDialog() {
    pack();
    Utilities.setPopupLoc(this, getOwner());
    setVisible(true);
  }
  
  
  protected void closeDialog() {
    setVisible(false);
  }
  
  
  public java.util.List<String> selectedItems() {
    final java.util.List<String> results = new ArrayList<String>();
    
    synchronized(selectedItems) {
      
      for (int i = 0; i < dataAsStrings.size(); ++i) {
        if (selectedItems.get(i)) {
          results.add(dataAsStrings.get(i));
        }
      }
    }
    
    return Collections.unmodifiableList(results);
  }
  
  
  private class SelectAllNoneActionListener implements ActionListener {
    
    private final boolean _setToValue;
    
    
    public SelectAllNoneActionListener(SelectionState setToState) {
      _setToValue = setToState == SelectionState.SELECTED;
    }
    
    
    public void actionPerformed(ActionEvent notUsed) {
      
      synchronized(selectedItems) {
        for (int i = 0; i < selectedItems.size(); ++i) {
          selectedItems.set(i, _setToValue);
        }
        tableModel.fireTableRowsUpdated(0, Math.max(0, selectedItems.size() - 1));
      }
    }
  }
  
  
  public static void main(String args[]) {
    final Collection<String> data = new java.util.ArrayList<String>();
    data.add("how");
    data.add("now");
    data.add("brown");
    data.add("cow");
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        ScrollableListSelectionDialog ld = 
          new ScrollableListSelectionDialog(null, "TITLE", "LEADER", data, "Words", SelectionState.SELECTED, 
                                            JOptionPane.ERROR_MESSAGE) {
          @Override
          protected void closeDialog() {
            super.closeDialog();
            Collection<String> si = selectedItems();
            for (String i : si) {
              System.out.println(i);
            }
          }
        };
        ld.pack();
        ld.setVisible(true);
      }
    });
  }
}
