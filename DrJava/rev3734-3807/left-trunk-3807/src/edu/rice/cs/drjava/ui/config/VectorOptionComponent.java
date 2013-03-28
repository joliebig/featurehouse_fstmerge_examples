

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Vector;



public abstract class VectorOptionComponent<T> extends OptionComponent<Vector<T>> implements OptionConstants {
  protected JScrollPane _listScrollPane;
  protected JPanel _panel;
  protected JList _list;
  protected JPanel _buttonPanel;
  protected JButton _addButton;
  protected JButton _removeButton;
  protected JButton _moveUpButton;   
  protected JButton _moveDownButton; 
  protected DefaultListModel _listModel;
  protected static final int NUM_ROWS = 5;
  protected static final int PIXELS_PER_ROW = 18;

  
  public VectorOptionComponent(VectorOption<T> opt, String text, Frame parent) {
    super(opt, text, parent);

    
    _listModel = new DefaultListModel();
    _list = new JList(_listModel);
    _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    resetToCurrent();

    

    _addButton = new JButton(_getAddAction());
    _removeButton = new JButton(new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) {
        if (!_list.isSelectionEmpty()) {
          int index = _list.getSelectedIndex();
          _listModel.remove(index);
          if (index == _listModel.getSize()) { 
            if (index > 0) 
            _list.setSelectedIndex(index - 1);
            notifyChangeListeners();
          }
          else {
            _list.setSelectedIndex(index);
            notifyChangeListeners();
          }
        }
      }
    });
    
    
    _moveUpButton = new JButton(new AbstractAction("Move Up") {
      public void actionPerformed(ActionEvent ae) {
        if (!_list.isSelectionEmpty()) {
          int index = _list.getSelectedIndex();
          if (index > 0) {
            Object o = _listModel.getElementAt(index);
            _listModel.remove(index);
            _listModel.insertElementAt(o, index - 1);
            _list.setSelectedIndex(index - 1);
            notifyChangeListeners();
          }
        }
      }
    });

    
    _moveDownButton = new JButton(new AbstractAction("Move Down") {
      public void actionPerformed(ActionEvent ae) {
        if (!_list.isSelectionEmpty()) {
          int index = _list.getSelectedIndex();
          if (index < _listModel.getSize() - 1) {
            Object o = _listModel.getElementAt(index);
            _listModel.remove(index);
            _listModel.insertElementAt(o, index + 1);
            _list.setSelectedIndex(index + 1);
            notifyChangeListeners();
          }
        }
      }
    });
    
    
    _buttonPanel = new JPanel();
    _buttonPanel.setBorder(new EmptyBorder(5,5,5,5));
    _buttonPanel.setLayout(new BoxLayout(_buttonPanel, BoxLayout.X_AXIS));
    
    _buttonPanel.add(Box.createHorizontalGlue());
    _addButtons(); 
    _buttonPanel.add(Box.createHorizontalGlue());

    _listScrollPane = new JScrollPane(_list,
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    _panel = new JPanel(new BorderLayout());
    _panel.add(_listScrollPane, BorderLayout.CENTER);
    _panel.add(_buttonPanel, BorderLayout.SOUTH);

    _listScrollPane.setPreferredSize(new Dimension(0, NUM_ROWS * PIXELS_PER_ROW));
  }

  
  protected void _addButtons() {
    _buttonPanel.add(_addButton);
    _buttonPanel.add(_removeButton);
  }
  
  
  public VectorOptionComponent(VectorOption<T> opt, String text,
                               Frame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _listScrollPane.setToolTipText(description);
    _list.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public boolean updateConfig() {
    Vector<T> current = getValue();
    DrJava.getConfig().setSetting(_option, current);
    resetToCurrent();
    return true;
  }
  
  
  public Vector<T> getValue() {
    Vector<T> current = new Vector<T>();
    for (int i = 0; i < _listModel.getSize(); i++) {
      
      @SuppressWarnings("unchecked") T element = (T) _listModel.getElementAt(i);
      current.add(element);
    }
    return current;
  }

  
  public void setValue(Vector<T> value) {
    _listModel.clear();
    for (int i = 0; i < value.size(); i++) {
      _listModel.addElement(value.elementAt(i));
    }
  }

  
  public JComponent getComponent() { return _panel; }

  
  protected abstract Action _getAddAction();
}
