

package edu.rice.cs.util.swing;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



public class ScrollableListDialog<T> extends JDialog {
  
  private static final int DEFAULT_WIDTH = 400;
  
  private static final int DEFAULT_HEIGHT = 450;
  
  
  private static final double WIDTH_RATIO = .75;
  
  private static final double HEIGHT_RATIO = .50;
  
  
  protected final JList list;
  
  
  protected int _buttonPressed = -1;
  
  
  protected List<T> listItems;
  
  
  public static class Builder<T> {
    protected Frame _owner;
    protected String _dialogTitle;
    protected String _leaderText;
    protected List<T> _listItems = new ArrayList<T>();
    protected List<T> _selectedItems = new ArrayList<T>();
    protected int _messageType = JOptionPane.PLAIN_MESSAGE;
    protected int _width = DEFAULT_WIDTH;
    protected int _height = DEFAULT_HEIGHT;
    protected Icon _icon = null;
    protected boolean _fitToScreen = true;
    protected List<JButton> _buttons = new ArrayList<JButton>();
    protected List<JComponent> _additional = new ArrayList<JComponent>();
    protected boolean _selectable = false;
    public Builder() { addOkButton(); }
    public Builder<T> setOwner(Frame owner) { _owner = owner; return this; }
    public Builder<T> setTitle(String dialogTitle) { _dialogTitle = dialogTitle; return this; }
    public Builder<T> setText(String leaderText) { _leaderText = leaderText; return this; }
    public Builder<T> setItems(List<T> listItems) { _listItems = listItems; return this; }
    public Builder<T> setSelectedItems(List<T> selItems) { _selectedItems = selItems; return this; }
    public Builder<T> setMessageType(int messageType) { _messageType = messageType; return this; }
    public Builder<T> setWidth(int width) { _width = width; return this; }
    public Builder<T> setHeight(int height) { _height = height; return this; }
    public Builder<T> setIcon(Icon icon) { _icon = icon; return this; }
    public Builder<T> setFitToScreen(boolean fts) { _fitToScreen = fts; return this; }
    public Builder<T> clearButtons() { _buttons.clear(); return this; }
    public Builder<T> addOkButton() { _buttons.add(new JButton("OK")); return this; }
    public Builder<T> addButton(JButton b) { _buttons.add(b); return this; }
    public Builder<T> addAdditionalComponent(JComponent c) { _additional.add(c); return this; }
    public Builder<T> setSelectable(boolean b) { _selectable = b; return this; }
    public ScrollableListDialog<T> build() {
      return new ScrollableListDialog<T>(_owner, _dialogTitle, _leaderText, _listItems, _selectedItems, _messageType, 
                                         _width, _height, _icon, _fitToScreen, _buttons, _additional, _selectable);
    }
  }  
  
  
  private ScrollableListDialog(Frame owner, String dialogTitle, String leaderText, List<T> listItems, List<T> selItems,
                               int messageType, int width, int height, Icon icon, boolean fitToScreen, 
                               List<JButton> buttons, List<JComponent> additional, boolean selectable) {
    super(owner, dialogTitle, true);
    this.listItems = listItems;
    
    if (!_isknownMessageType(messageType)) {
      throw new IllegalArgumentException("The message type \"" + messageType + "\" is unknown");
    }
    
    if (listItems == null) throw new IllegalArgumentException("listItems cannot be null");
    
    
    JLabel dialogIconLabel = null;
    if (icon != null) { 
      dialogIconLabel = new JLabel(icon);
    } 
    else { 
      Icon messageIcon = _getIcon(messageType);
      if (messageIcon != null) dialogIconLabel = new JLabel(messageIcon); 
    }

    final JLabel leaderLabel = new JLabel(leaderText);
    final JPanel leaderPanel = new JPanel();
    leaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    if (dialogIconLabel != null) leaderPanel.add(dialogIconLabel);
    leaderPanel.add(leaderLabel);
    
    
    
    final Vector<String> dataAsStrings = new Vector<String>(listItems.size());
    
    String longestString = "";
    for (T obj : listItems) {
      if (obj != null) {
        final String objAsString = obj.toString();
        
        if (objAsString.length() > longestString.length()) {
         longestString = objAsString;
        }
        dataAsStrings.add(objAsString);
      }
    }
    
    if (selectable) {
      final Vector<String> selAsStrings = new Vector<String>(selItems.size());
      for (T obj : selItems) {
        if (obj != null) {
          final String objAsString = obj.toString();
          selAsStrings.add(objAsString);
        }
      }
      list = new CheckBoxJList(dataAsStrings, selAsStrings);
      
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    else {
      list = new JList(dataAsStrings);
      
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    list.setPrototypeCellValue(longestString);
    
    
    final JScrollPane scrollPane = new JScrollPane(list);
    
    
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
    _addButtons(buttonPanel, buttons);
    _addAdditionalComponents(buttonPanel, additional);
    
    
    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout(10, 5));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
    
    contentPanel.add(leaderPanel, BorderLayout.NORTH);
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    getContentPane().add(contentPanel);

    
    Dimension dialogSize = new Dimension();
    
    if (fitToScreen) {
      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int screenBasedWidth = (int) (WIDTH_RATIO * screenSize.getWidth());
      int screenBasedHeight = (int) (HEIGHT_RATIO * screenSize.getHeight());
     
      dialogSize.setSize(Math.max(DEFAULT_WIDTH, screenBasedWidth),
                         Math.max(DEFAULT_HEIGHT, screenBasedHeight));
    } else {
      
      dialogSize.setSize(width, height);
    }

    setSize(dialogSize);
  }
  
  
  private boolean _isknownMessageType(int messageType) {
    return messageType == JOptionPane.ERROR_MESSAGE ||
      messageType == JOptionPane.INFORMATION_MESSAGE ||
      messageType == JOptionPane.WARNING_MESSAGE ||
      messageType == JOptionPane.QUESTION_MESSAGE ||
      messageType == JOptionPane.PLAIN_MESSAGE;
  }
  
  
  private Icon _getIcon(int messageType) {
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
  
  
  protected void _addButtons(JPanel buttonPanel, List<JButton> buttons) {
    int i = 0;
    for (JButton b: buttons) {
      final int j = i++;
      b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent notUsed) {
          _buttonPressed = j;
          closeDialog();
        }
      });
      buttonPanel.add(b);
    }
    
    getRootPane().setDefaultButton(buttons.get(0));
  }
  
  
  protected void _addAdditionalComponents(JPanel buttonPanel, List<JComponent> additional) {
    int i = 0;
    for (JComponent c: additional) {
      buttonPanel.add(c);
    }
  }
  
  
  public void showDialog() {
    pack();
    Utilities.setPopupLoc(this, getOwner());
    setVisible(true);
  }
  
  
  protected void closeDialog() {
    setVisible(false);
  }
  
  
  public int getButtonPressed() { return _buttonPressed; }
  
  
  public List<T> getSelectedItems() {
    ArrayList<T> l = new ArrayList<T>();
    for (int i: list.getSelectedIndices())  l.add(listItems.get(i));

    return l;
  }
  
  
  public static void main(String args[]) {
    final List<String> data = new java.util.ArrayList<String>();
    data.add("how");
    data.add("now");
    data.add("brown");
    data.add("cow");
    
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        ScrollableListDialog<String> ld = new ScrollableListDialog.Builder<String>()
          .setOwner(null)
          .setTitle("TITLE")
          .setText("LEADER")
          .setItems(data)
          .setMessageType(JOptionPane.ERROR_MESSAGE)
          .setSelectable(true)
          .setSelectedItems(data.subList(0,2))
          .build();
        ld.pack();
        ld.setVisible(true);
      }
    });
  }
}

