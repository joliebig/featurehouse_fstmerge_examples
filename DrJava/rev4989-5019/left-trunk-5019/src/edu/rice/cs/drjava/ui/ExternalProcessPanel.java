

package edu.rice.cs.drjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;

import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.ProcessCreator;
import edu.rice.cs.drjava.ui.predictive.*;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import static edu.rice.cs.drjava.ui.MainFrame.GoToFileListEntry;


public class ExternalProcessPanel extends AbortablePanel {
  
  public final int BUFFER_SIZE = 10240;
  
  public final int BUFFER_READS_PER_TIMER = 5;
  protected JTextArea _textArea;
  protected ProcessCreator _pc = null;
  protected Process _p = null;
  protected InputStreamReader _is = null;
  protected InputStreamReader _erris = null;
  protected JButton _updateNowButton;
  protected JButton _runAgainButton;
  protected Thread _updateThread;
  protected Thread _readThread;
  protected Thread _deathThread;
  protected StringBuilder _sb = new StringBuilder();
  protected volatile int _changeCount = 0;
  private char[] _buf = new char[BUFFER_SIZE];
  private int _red = -1;
  private char[] _errbuf = new char[BUFFER_SIZE];
  private int _errred = -1;
  private int _retVal;
  private String _header;
  protected CompletionMonitor _abortMonitor = new CompletionMonitor();

  
  public ExternalProcessPanel(MainFrame frame, String title, ProcessCreator pc) {
    super(frame, title);
    _sb = new StringBuilder("Command line: ");
    _sb.append(pc.cmdline());
    _sb.append('\n');
    _header = _sb.toString();
    _textArea.setText(_header); 
    initThread(pc);
    _textArea.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if ((SwingUtilities.isLeftMouseButton(e)) &&
            (e.getClickCount() == 2)) {
          doubleClicked(e);
        }
      }
      public void mouseEntered(MouseEvent e) { }
      public void mouseExited(MouseEvent e) { }
      public void mousePressed(MouseEvent e) { }
      public void mouseReleased(MouseEvent e) { }
    });
    EventQueue.invokeLater(new Runnable() {
      public void run() { updateText(); } });
    
  }

  protected void initThread(ProcessCreator pc) {
    _abortMonitor.reset();
    
    try {
      _pc = pc;
      _pc.getPropertyMaps().clearVariables();
      _readThread = new Thread(new Runnable() {
        public void run() {
          while((_is != null) || (_erris != null)) {
            readText(false);
          }
        }
      },"External Process Read Thread");
      _updateThread = new Thread(new Runnable() {
        public void run() {
          while((_is != null) || (_erris != null)) {
            try {
              Thread.sleep(edu.rice.cs.drjava.DrJava.getConfig().
                             getSetting(edu.rice.cs.drjava.config.OptionConstants.FOLLOW_FILE_DELAY));
            }
            catch(InterruptedException ie) {  }
            updateText();
          }
        }
      },"External Process Update Thread");
      _p = _pc.start();
      _sb.append("Evaluated command line: ");
      _sb.append(_pc.evaluatedCommandLine());
      _sb.append('\n');
      _is = new InputStreamReader(_p.getInputStream());
      _erris = new InputStreamReader(_p.getErrorStream());
      _readThread.start();
      _updateThread.start();
      _updateNowButton.setEnabled(true);
      _deathThread = new Thread(new Runnable() {
        public void run() {
          try {
            _retVal = _p.waitFor();
            Utilities.invokeLater(new Runnable() {
              public void run() {
                _sb.append("\n\nProcess returned ");
                _sb.append(_retVal);
                _sb.append("\n");
                _textArea.setText(_sb.toString());
              }
            });
          }
          catch(InterruptedException e) {
            Utilities.invokeLater(new Runnable() {
              public void run() {
                _p.destroy();
                _sb.append("\n\nProcess returned ");
                _sb.append(_retVal);
                _sb.append("\n");
                _textArea.setText(_sb.toString());
              }
            });
          }
          finally {
            abortActionPerformed(null);
          }
        }
      },"External Process Death Thread");
      _deathThread.start();
      
    }
    catch(Exception e) {
      _sb.append("\n\nException from process:\n"+e.toString());
      _textArea.setText(_sb.toString());
      edu.rice.cs.util.GeneralProcessCreator.LOG.log(_sb.toString());
      abortActionPerformed(null);
    }
  }
  
  
  protected Component makeLeftPanel() {
    _textArea = new JTextArea();
    _textArea.setEditable(false);
    return _textArea;
  }

  
  protected void abortActionPerformed(ActionEvent e) {    
    _abortButton.setEnabled(false);
    _updateNowButton.setEnabled(false);
    _runAgainButton.setEnabled(true);
    
    new Thread(new Runnable() {
      public void run() {
        if (_is != null) {
          try {
            
            
            _p.getInputStream().close();
            _is.close();
          }
          catch(IOException ioe) {  }
          _is = null;
          Utilities.invokeLater(new Runnable() { public void run() { updateButtons(); } });          
        }
        if (_erris != null) {
          try {
            
            
            _p.getErrorStream().close();
            _erris.close();
          }
          catch(IOException ioe) {  }
          _erris = null;
          Utilities.invokeLater(new Runnable() { public void run() { updateButtons(); } });
        }
        if (_p != null) {
          _p.destroy();
          _p = null;
        }
        Utilities.invokeLater(new Runnable() { public void run() { updateText(); updateButtons(); } });
        _abortMonitor.signal();
      }
    }).start();
  }

  
  protected void runAgainActionPerformed(ActionEvent e) {
    abortActionPerformed(e);
    _abortButton.setEnabled(false);
    _updateNowButton.setEnabled(false);
    _runAgainButton.setEnabled(false);
    
    new Thread(new Runnable() {
      public void run() {
        _abortMonitor.attemptEnsureSignaled(); 
        _abortMonitor.reset();
        _sb = new StringBuilder("Command line: ");
        _sb.append(_pc.cmdline());
        _sb.append('\n');
        _header = _sb.toString();
        initThread(_pc);
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            updateText();
          } });
      }
    }).start();
  }
  
  
  
  
  public void doubleClicked(MouseEvent e) {
    
    final String t = _textArea.getText();
    int caret = _textArea.getCaretPosition();
    int start = caret;
    int end = start;
    while((start-1 > 0) && (t.charAt(start-1) != '\n')) { --start; }
    while((end >= 0) && (end < t.length()) && (t.charAt(end) != '\n')) { ++end; }

    
    if ((start < 0) || (end < 0) || (start >= t.length()) || (end >= t.length())) return;
    final String line = t.substring(start,end);
    
    caret -= start; 
    if (caret>=line.length()) { caret = line.length()-1; }
    start = end = caret;
    char ch;
    while((end >= 0) && (end<line.length())) {
      ch = line.charAt(end);
      if (ch==':') {
        if ((end+1<line.length()) && (Character.isDigit(line.charAt(end+1)))) {
          
          
          do {
            ++end;
          } while((end<line.length()) && (Character.isDigit(line.charAt(end))));
          break;
        }
        else {
          
          break;
        }
      }
      else if (Character.isJavaIdentifierPart(ch)) {
        
        ++end;
      }
      else if ((ch=='.') || (ch==File.separatorChar)) {
        
        ++end;
      }
      else {
        
        break;
      }
    }

    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    if ((docs == null) || (docs.size() == 0)) return; 

    ArrayList<GoToFileListEntry> list;
    list = new ArrayList<GoToFileListEntry>(docs.size());
    
    for(OpenDefinitionsDocument d: docs) {
      
      try {
        String fullyQualified = d.getPackageName() + "." + d.toString();
        if (fullyQualified.startsWith(".")) { fullyQualified = fullyQualified.substring(1); }
        list.add(new GoToFileListEntry(d, fullyQualified));
        
      }
      catch(IllegalStateException ex) {  }
    }
    PredictiveInputModel<GoToFileListEntry> pim =
      new PredictiveInputModel<GoToFileListEntry>(true, new PredictiveInputModel.PrefixLineNumStrategy<GoToFileListEntry>(), list);
    
    GoToFileListEntry uniqueMatch = null;
    String name, oldName = null, simpleName = null;
    while(start > 0) {
      ch = line.charAt(start);
      while(start > 0) {
        ch = line.charAt(start);
        if ((ch==':') || (ch=='.') || (Character.isJavaIdentifierPart(ch))) { --start; } else { break; }
      }
      
      if ((start >= 0) && (end>=start) && (start<line.length()) && (end<line.length())) {
        name = line.substring(start,end).replace(File.separatorChar,'.');
        if ((name.length() > 0) && (!Character.isJavaIdentifierPart(name.charAt(0)))) { name = name.substring(1); }
        if (simpleName == null) { simpleName = name; }
        if (name.equals(oldName)) { break; }
        if ((name.indexOf(".java") >= 0) ||
            (name.indexOf(".j") >= 0) ||
            (name.indexOf(".dj0") >= 0) ||
            (name.indexOf(".dj1") >= 0) ||
            (name.indexOf(".dj2") >= 0)) {
          
          uniqueMatch = getUniqueMatch(name, pim);
          if (uniqueMatch != null) {
            
            
            final OpenDefinitionsDocument newDoc = pim.getCurrentItem().doc;
            final boolean docChanged = ! newDoc.equals(_model.getActiveDocument());
            final boolean docSwitch = _model.getActiveDocument() != newDoc;
            if (docSwitch) _model.setActiveDocument(newDoc);
            final int curLine = newDoc.getCurrentLine();
            final int last = name.lastIndexOf(':');
            if (last >= 0) {
              try {
                String nend = name.substring(last + 1);
                int val = Integer.parseInt(nend);
                
                final int lineNum = Math.max(1, val);
                Runnable command = new Runnable() {
                  public void run() {
                    try { _frame._jumpToLine(lineNum); }  
                    catch (RuntimeException ex) { _frame._jumpToLine(curLine); }
                  }
                };
                if (docSwitch) {
                  
                  EventQueue.invokeLater(command);
                }
                else command.run();
              }
              catch(RuntimeException ex) {  }
            }
            else if (docChanged) {
              
              EventQueue.invokeLater(new Runnable() { public void run() { _frame.addToBrowserHistory(); } });
            }
            break;
          }
        }
        oldName = name;
      }
      else {
        break;
      }
      if (ch==File.separatorChar) { --start; } 
    }
    if (uniqueMatch == null) {
      
      _frame.gotoFileMatchingMask(simpleName);
    }
  }
  
  
  GoToFileListEntry getUniqueMatch(String mask, PredictiveInputModel<GoToFileListEntry> pim) {        
    pim.setMask(mask);
    
    if (pim.getMatchingItems().size() == 1) {
      
      if (pim.getCurrentItem() != null) { return pim.getCurrentItem(); }
    }
    return null;
  }
  
  
  protected void updateButtons() {
    boolean ended = true;
    if (_p != null) {
      try {
        
        _p.exitValue();
        
        ended = true;
      }
      catch(IllegalThreadStateException e) {
        
        ended = false;
      }
    }
    _abortButton.setEnabled((_is != null) && (_erris != null) && (!ended));
    _updateNowButton.setEnabled((_is != null) && (_erris != null) && (!ended));
    _runAgainButton.setEnabled((_is == null) || (_erris == null) || (ended));
  }  

  
  protected JComponent[] makeButtons() {
    _updateNowButton = new JButton("Update");
    _updateNowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { 
        EventQueue.invokeLater(new Runnable() { public void run() { updateText(); } }); }
    });
    _runAgainButton = new JButton("Run Again");
    _runAgainButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runAgainActionPerformed(e);
      }
    });
    return new JComponent[] { _updateNowButton, _runAgainButton };
  }

  
  protected void readText(final boolean finish) {
    
    if (((_is != null) || (_erris != null))) {
      _changeCount = 0;
      
      try {
        
        
        
        while((_is != null) &&
              (_erris != null) &&
              (_changeCount <= BUFFER_READS_PER_TIMER) &&
              (_erris != null) &&
              ((_red = _is.read(_buf)) >= 0)) {

          
          _sb.append(new String(_buf, 0, _red));
          if (finish) { _changeCount = 1; } else { ++_changeCount; }
        }
        while((_changeCount <= BUFFER_READS_PER_TIMER) &&
              (_erris != null) &&
              ((_errred = _erris.read(_errbuf)) >= 0)) {
          
          _sb.append(new String(_errbuf, 0, _errred));
          if (finish) { _changeCount = 1; } else { ++_changeCount; }
        }
        if ((_red > 0) && (_changeCount < BUFFER_READS_PER_TIMER)) {
          _sb.append(new String(_buf, 0, _red));
          if (finish) { _changeCount = 1; } else { ++_changeCount; }
        }
        if ((_errred > 0) && (_changeCount<BUFFER_READS_PER_TIMER)) {
          _sb.append(new String(_errbuf, 0, _errred));
          if (finish) { _changeCount = 1; } else { ++_changeCount; }
        }
        if ((_p != null) && (_is == null)) {
          try {
            
            _p.exitValue();
            
          }
          catch(IllegalThreadStateException e) {
            
            _sb.append("\nInput stream suddenly became null.");
          }
        }
        if ((_p != null) && (_erris == null)) { 
          try {
            
            _p.exitValue();
            
          }
          catch(IllegalThreadStateException e) {
            
            _sb.append("\nError input stream suddenly became null.");
          }
        }
      }
      catch(IOException ioe) {
        
        
        if (_p != null) {
          try {
            _p.exitValue();
            
          }
          catch(IllegalThreadStateException e) {
            
            _sb.append("\n\nI/O Exception reading from process:\n"+ioe.toString());
            edu.rice.cs.util.GeneralProcessCreator.LOG.log("\n\nI/O Exception reading from process:");
            edu.rice.cs.util.GeneralProcessCreator.LOG.log(ioe.toString(),ioe);
          }
        }
        if (finish) { _changeCount = 1; } else { ++_changeCount; }
        abortActionPerformed(null);
      }
    }
  }
  
  
  protected void updateText() {
    
    if (_updateNowButton.isEnabled()) {

















      if (_changeCount > 0) {
        _changeCount = 0;
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            
            _textArea.setText(_sb.toString());
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
                  _sb = new StringBuilder(_textArea.getText(start,len));
                  _textArea.setText(_sb.toString());
                }
              }
              catch(javax.swing.text.BadLocationException e) {  }
            }
            
          }
        });
      }
      
      updateButtons();
    }
  }
}
