

package edu.rice.cs.drjava.ui;

import java.io.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.util.swing.Utilities;


public class LessPanel extends AbortablePanel {
  
  public final int BUFFER_SIZE = 10240;
  
  public final int BUFFER_READS_PER_TIMER = 5;
  protected JTextArea _textArea;
  protected File _f = null;
  protected FileReader _fr = null;
  protected JButton _updateNowButton;
  protected JButton _restartButton;
  protected Thread _updateThread;
  private char[] _buf = new char[BUFFER_SIZE];
  private int _red = -1;
  private long _totalRead = 0;

  
  public LessPanel(MainFrame frame, String title, File f) {
    super(frame, title);
    initThread(f);
    updateText();
    
  }

  protected void initThread(File f) {
    try {
      
      
      
      
      if (f.exists() && f.canRead() && f.isFile()) {
        
        
        _f = f;
        _fr = new FileReader(_f);
        _red = -1;
        _totalRead = 0;
        _updateThread = new Thread(new Runnable() {
          public void run() {
            while(_fr != null) {
              try {
                Thread.sleep(edu.rice.cs.drjava.DrJava.getConfig().
                               getSetting(edu.rice.cs.drjava.config.OptionConstants.FOLLOW_FILE_DELAY));
              }
              catch(InterruptedException ie) {  }
              updateText();
            }
          }
        });
        _updateThread.start();
        _updateNowButton.setEnabled(true);
        
      }
    }
    catch(Exception e) {
      _fr = null;
    }
  }
  
  
  protected Component makeLeftPanel() {
    _textArea = new JTextArea();
    _textArea.setEditable(false);
    return _textArea;
  }

  
  protected void abortActionPerformed(ActionEvent e) {
    if (_fr != null) {
      try {
        _fr.close();
      }
      catch(IOException ioe) {  }
      _fr = null;
      updateButtons();
    }
  }
  
  
  protected void updateButtons() {
    _abortButton.setEnabled(_fr != null);
    _updateNowButton.setEnabled(_fr != null);
    _restartButton.setEnabled(_fr == null);
  }  

  
  protected JComponent[] makeButtons() {
    _updateNowButton = new JButton("Update");
    _updateNowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { updateText(); }
    });
    _restartButton = new JButton("Restart");
    _restartButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { restartFollowing(); }
    });
    _restartButton.setEnabled(false);
    return new JComponent[] { _updateNowButton, _restartButton };
  }

  
  protected void restartFollowing() {
    _textArea.setText("");
    initThread(_f);
    updateText();
  }
  
  
  protected void updateText() {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        
        if ((_fr != null) &&
            (_updateNowButton.isEnabled())) {
          _updateNowButton.setEnabled(false);
          int changeCount = 0;
          long newSize = _f.length();
          if (_totalRead!=newSize) {
            if (_totalRead>newSize) {
              
              _textArea.setText("");
              _totalRead = 0;
            }
            StringBuilder sb = new StringBuilder(_textArea.getText());
            
            try {
              _fr.close();
              _fr = new FileReader(_f);
              _fr.skip(_totalRead);
              
              
              
              while((changeCount<=BUFFER_READS_PER_TIMER) && ((_red = _fr.read(_buf)) >= 0)) {
                
                _totalRead += _red;
                sb.append(new String(_buf, 0, _red));
                ++changeCount;
              }
              if ((_red > 0) && (changeCount<BUFFER_READS_PER_TIMER)) {
                _totalRead += _red;
                sb.append(new String(_buf, 0, _red));
                ++changeCount;
              }
            }
            catch(IOException ioe) {
              
              
              sb.append("\n\nI/O Exception reading file "+_f+"\n");
              ++changeCount;
              abortActionPerformed(null);
            }
            finally {
              if (changeCount > 0) {
                
                _textArea.setText(sb.toString());
                int maxLines = edu.rice.cs.drjava.DrJava.getConfig().
                  getSetting(edu.rice.cs.drjava.config.OptionConstants.FOLLOW_FILE_LINES);
                if (maxLines > 0) { 
                  try {
                    int start = 0;
                    int len = _textArea.getText().length();
                    int curLines = _textArea.getLineCount();
                    if (curLines>maxLines) {
                      start = _textArea.getLineStartOffset(curLines-maxLines);
                      len -= start;
                      sb = new StringBuilder(_textArea.getText(start,len));
                      _textArea.setText(sb.toString());
                    }
                  }
                  catch(javax.swing.text.BadLocationException e) {  }
                }
                
              }
            }
          }
        }
        
        updateButtons();
      }
    });
  }
}
