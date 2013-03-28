

package edu.rice.cs.util.swing;

import edu.rice.cs.util.swing.Utilities;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;



public class ScrollableDialog  {
  
  public static final int DEFAULT_WIDTH = 500;
  
  public static final int DEFAULT_HEIGHT = 400;
  
  protected JDialog _dialog;
  
  protected JTextArea _textArea;
  
  protected JPanel _buttonPanel;
  
  protected JScrollPane _textScroll;

  
  public ScrollableDialog(JFrame parent, String title, String header, String text) {
    this(parent, title, header, text, DEFAULT_WIDTH, DEFAULT_HEIGHT, false);
  }
  
  
  public ScrollableDialog(JFrame parent, String title, String header, String text, int width, int height) {
    this(parent, title, header, text, width, height, false);
  }
  
  
  public ScrollableDialog(JFrame parent, String title, String header, String text, boolean wrap) {
    this(parent, title, header, text, DEFAULT_WIDTH, DEFAULT_HEIGHT, wrap);
  }
  
  
  public ScrollableDialog(JFrame parent, String title, String header, String text, int width, int height, boolean wrap) {
    _dialog = new JDialog(parent, title, true);    
    Container content = _dialog.getContentPane();

    content.setLayout(new BorderLayout());

    
    _textArea = new JTextArea();
    _textArea.setEditable(false);
    _textArea.setText(text);
    _textArea.setLineWrap(wrap);
    _textArea.setWrapStyleWord(true);
    
    
    _dialog.setSize(width, height);
    
    
    _textScroll = new BorderlessScrollPane(_textArea,
                                           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                           wrap?JScrollPane.HORIZONTAL_SCROLLBAR_NEVER:JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    JPanel scrollWrapper = new JPanel(new BorderLayout(0,5));
    scrollWrapper.setBorder(new EmptyBorder(5,5,0,5));
    scrollWrapper.add(new JLabel(header),BorderLayout.NORTH);
    scrollWrapper.add(_textScroll,BorderLayout.CENTER);
    JPanel bottomPanel = new JPanel(new BorderLayout());
    _buttonPanel = new JPanel(new GridLayout(1,0,5,5));
    bottomPanel.add(_buttonPanel,BorderLayout.EAST);
    bottomPanel.setBorder(new EmptyBorder(5,5,5,5));
    _addButtons();
    
    content.add(scrollWrapper, BorderLayout.CENTER);
    content.add(bottomPanel, BorderLayout.SOUTH);
    
    
    
    
    
  }

  
  protected void _addButtons() {
    _buttonPanel.add(new JButton(_okAction));
  }

  
  private Action _okAction = new AbstractAction("OK") {
    public void actionPerformed(ActionEvent e) {
      _dialog.dispose();
    }
  };

  
  public void setTextFont(Font f) {
    _textArea.setFont(f);
  }
  
  
  public void show() {
    Utilities.setPopupLoc(_dialog, _dialog.getOwner());
    _textArea.setCaretPosition(0);
    _textScroll.getHorizontalScrollBar().setValue(_textScroll.getHorizontalScrollBar().getMinimum());
    _textScroll.getVerticalScrollBar().setValue(_textScroll.getVerticalScrollBar().getMinimum());
    _dialog.setVisible(true);
  }
}
