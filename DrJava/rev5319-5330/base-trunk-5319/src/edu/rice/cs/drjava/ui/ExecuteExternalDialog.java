

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.PropertyMaps;
import edu.rice.cs.drjava.config.*;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.util.BalancingStreamTokenizer;
import edu.rice.cs.util.ProcessCreator;
import edu.rice.cs.util.GeneralProcessCreator;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.XMLConfig;
import edu.rice.cs.util.swing.DirectoryChooser;
import edu.rice.cs.util.swing.FileChooser;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;

public class ExecuteExternalDialog extends SwingFrame implements OptionConstants {
  private static final int FRAME_WIDTH = 750;
  private static final int FRAME_HEIGHT = 500;
  
  
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
    public FrameState(ExecuteExternalDialog comp) {
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
  
  
  private JButton _runCommandButton;
  
  private JButton _saveCommandButton;
  
  private JButton _insertCommandButton;
  
  private JButton _cancelCommandButton;
  
  private JTextPane _commandLine;
  
  private JLabel _commandLinePreviewLabel;
  
  private JTextPane _commandLinePreview;
  
  private StyledDocument _commandLineDoc;
  
  private JTextPane _commandWorkDirLine;
  
  private JTextPane _commandWorkDirLinePreview;
  
  private StyledDocument _commandWorkDirLineDoc;
  
  private JButton _commandWorkDirBtn;
  
  private JTextPane _commandEnclosingFileLine;
  
  private JTextPane _commandEnclosingFileLinePreview;
  
  private StyledDocument _commandEnclosingFileLineDoc;
  
  private JButton _commandEnclosingFileBtn;
  
  private JTextPane _lastCommandFocus;  

  
  SimpleAttributeSet _varCommandLineCmdStyle;
  
  SimpleAttributeSet _varErrorCommandLineCmdStyle;
  
  SimpleAttributeSet _commandLineCmdAS;  
  
  
  JPanel _commandPanel;
  
  DocumentListener _documentListener;
  
  DocumentListener _workDirDocumentListener;
  
  DocumentListener _enclosingFileDocumentListener;
  
  
  protected DirectoryChooser _dirChooser;
  
  protected FileChooser _fileChooser;
  
  
  protected InsertVariableDialog _insertVarDialog;
  
  
  protected CompletionMonitor _insertVarDialogMonitor = new CompletionMonitor();
  
  
  private CompletionMonitor _cm;
  
  
  protected MainFrame _mainFrame;
  
  protected FrameState _lastState = null;
  
  
  protected boolean _editMode = false;
  
  
  protected int _editIndex = -1;
  
  
  protected PropertyMaps _props;
  
  public static final String STALE_TOOLTIP = "<html>Note: Values of variables might not be current for<br>" + 
    "performance reasons. They will be current when executed.</html>";
  
  
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
    else Utilities.setPopupLoc(this, _mainFrame);
    validate();
  }
  
  
  public ExecuteExternalDialog(MainFrame mf, boolean editMode, int editIndex, CompletionMonitor cm) {
    super("Execute External Process");
    try { _props = PropertyMaps.TEMPLATE.clone(); } catch(CloneNotSupportedException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
    _mainFrame = mf;
    _editMode = editMode;
    _editIndex = editIndex;
    _cm = cm;
    initComponents();
    if (editMode) {
      if (editIndex>=DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT)) {
        throw new IllegalArgumentException("Trying to edit saved external process that does not exist");
      }
      final String cmdline = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES).get(editIndex);
      final String workdir = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS).get(editIndex);
      final String enclosingFile = 
        DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES).get(editIndex);
      _commandLine.setText(cmdline);
      _commandWorkDirLine.setText(workdir);
      _commandEnclosingFileLine.setText(enclosingFile);
    }
    initDone(); 
  }
  
  
  public ExecuteExternalDialog(MainFrame mf) {
    this(mf, false, -1, null);
  }
  
  
  private void initComponents() {
    _dirChooser = new DirectoryChooser(this);
    _dirChooser.setDialogTitle("Select Work Directory");
    _dirChooser.setApproveButtonText("Select");
    _fileChooser = new FileChooser(null);
    _fileChooser.setDialogTitle("Select Enclosing .djapp File");
    _fileChooser.setApproveButtonText("Select");
    
    super.getContentPane().setLayout(new GridLayout(1,1));
    
    if (_editMode) {
      Action saveCommandAction = new AbstractAction("Save") {
        public void actionPerformed(ActionEvent e) {
          _saveCommand();
        }
      };
      _saveCommandButton = new JButton(saveCommandAction);
    }
    else {
      Action runCommandAction = new AbstractAction("Run Command Line") {
        public void actionPerformed(ActionEvent e) {
          _runCommand();
        }
      };
      _runCommandButton = new JButton(runCommandAction);
      _runCommandButton.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          _insertCommandButton.setEnabled(false);
        }
        public void focusLost(FocusEvent e) {
          if ((e.getOppositeComponent() == _commandLinePreview) || 
              (e.getOppositeComponent() == _commandWorkDirLinePreview) ||
              (e.getOppositeComponent() == _commandEnclosingFileLinePreview)) {
            _runCommandButton.requestFocus();
          }
        }
      });
      
      Action saveCommandAction = new AbstractAction("Save to Menu...") {
        public void actionPerformed(ActionEvent e) {
          _saveCommand();
        }
      };
      _saveCommandButton = new JButton(saveCommandAction);
    }
    
    _insertVarDialog = new InsertVariableDialog(_mainFrame, _insertVarDialogMonitor);
    Action insertCommandAction = new AbstractAction("Insert Variable...") {
      public void actionPerformed(ActionEvent e) {
        _insertVariableCommand();
      }
    };
    _insertCommandButton = new JButton(insertCommandAction);
    _insertCommandButton.setEnabled(false);
    
    Action cancelAction = new AbstractAction("Cancel") {
      public void actionPerformed(ActionEvent e) {
        _cancel();
      }
    };
    _cancelCommandButton = new JButton(cancelAction);
    
    
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        _commandPanel = makeCommandPane(); 
        
        getContentPane().add(_commandPanel);
        setResizable(false);
        
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Utilities.setPopupLoc(ExecuteExternalDialog.this, _mainFrame);
        _commandLine.requestFocus();
      }
    });
  }
  
  private JPanel makeCommandPane() {
    JPanel panel = new JPanel(new BorderLayout());
    GridBagLayout gridbag = new GridBagLayout();
    JPanel main = new JPanel(gridbag);
    GridBagConstraints c = new GridBagConstraints();
    main.setLayout(gridbag);
    c.fill = GridBagConstraints.BOTH;
    Insets labelInsets = new Insets(5, 10, 0, 0);
    Insets compInsets  = new Insets(5, 5, 0, 10);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.insets = labelInsets;
    JLabel commandLineLabel = new JLabel("Command line:");
    gridbag.setConstraints(commandLineLabel, c);
    main.add(commandLineLabel);
    
    c.weightx = 1.0;
    c.weighty = 32.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandLine = new JTextPane();
    
    _commandLine.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) e.consume();
        else if (e.getKeyCode() == KeyEvent.VK_TAB) {
          e.consume();
          if (e.isShiftDown()) {
            _insertCommandButton.setEnabled(false);
            _cancelCommandButton.requestFocus();
          }
          else _commandWorkDirLine.requestFocus();
        }
      }
      public void keyReleased(KeyEvent e) { }
      public void keyTyped(KeyEvent e) { }
    });
    JScrollPane commandLineSP = new JScrollPane(_commandLine);
    commandLineSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandLineSP, c);
    main.add(commandLineSP);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    _commandLinePreviewLabel = new JLabel("<html>Command line preview:<br>(0 characters)</html>");
    _commandLinePreviewLabel.setToolTipText(STALE_TOOLTIP);
    gridbag.setConstraints(_commandLinePreviewLabel, c);
    main.add(_commandLinePreviewLabel);
    
    c.weightx = 1.0;
    c.weighty = 32.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandLinePreview = new JTextPane();
    _commandLinePreview.setToolTipText(STALE_TOOLTIP);
    _commandLineDoc = (StyledDocument)_commandLinePreview.getDocument();
    
    
    _varCommandLineCmdStyle = new SimpleAttributeSet();
    StyleConstants.setBackground(_varCommandLineCmdStyle, DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR));
    
    _commandLineCmdAS = new SimpleAttributeSet();
    StyleConstants.setForeground(_commandLineCmdAS, DrJava.getConfig().getSetting(DEFINITIONS_NORMAL_COLOR));
    _varCommandLineCmdStyle = new SimpleAttributeSet();
    StyleConstants.setBackground(_varCommandLineCmdStyle, DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR));
    _varErrorCommandLineCmdStyle = new SimpleAttributeSet();
    StyleConstants.setBackground(_varErrorCommandLineCmdStyle, DrJava.getConfig().getSetting(DEBUG_BREAKPOINT_COLOR));
    _varCommandLineCmdStyle = new SimpleAttributeSet();
    StyleConstants.setBackground(_varCommandLineCmdStyle, DrJava.getConfig().getSetting(DEFINITIONS_MATCH_COLOR));
    
    _commandLinePreview.setEditable(false);
    _commandLinePreview.setBackground(Color.LIGHT_GRAY);
    _commandLinePreview.setSelectedTextColor(Color.LIGHT_GRAY);
    JScrollPane commandLinePreviewSP = new JScrollPane(_commandLinePreview);
    commandLinePreviewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandLinePreviewSP, c);
    main.add(commandLinePreviewSP);

    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    JLabel workDirLabel = new JLabel("Work directory:");
    gridbag.setConstraints(workDirLabel, c);
    main.add(workDirLabel);
    
    c.weightx = 1.0;
    c.weighty = 8.0;
    c.gridwidth = GridBagConstraints.RELATIVE;
    c.insets = compInsets;
    
    _commandWorkDirLine = new JTextPane();
    
    _commandWorkDirLine.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) e.consume();
        else if (e.getKeyCode() == KeyEvent.VK_TAB) {
          e.consume();
          if (e.isShiftDown()) {
            _commandLine.requestFocus();
          }
          else _commandEnclosingFileLine.requestFocus();
        }
      }
      public void  keyReleased(KeyEvent e) { }
      public void  keyTyped(KeyEvent e) { }
    });
    JScrollPane commandWorkDirLineSP = new JScrollPane(_commandWorkDirLine);
    commandWorkDirLineSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandWorkDirLineSP, c);
    main.add(commandWorkDirLineSP);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandWorkDirBtn = new JButton("...");
    _commandWorkDirBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { chooseFile(_commandWorkDirLine); }
    });
    gridbag.setConstraints(_commandWorkDirBtn, c);
    main.add(_commandWorkDirBtn);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    JLabel commandWorkDirLinePreviewLabel = new JLabel("Work directory preview:");
    commandWorkDirLinePreviewLabel.setToolTipText(STALE_TOOLTIP);
    gridbag.setConstraints(commandWorkDirLinePreviewLabel, c);
    main.add(commandWorkDirLinePreviewLabel);
    
    c.weightx = 1.0;
    c.weighty = 8.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandWorkDirLinePreview = new JTextPane();
    _commandWorkDirLinePreview.setToolTipText(STALE_TOOLTIP);
    _commandWorkDirLineDoc = (StyledDocument)_commandWorkDirLinePreview.getDocument();
    
    _commandWorkDirLinePreview.setEditable(false);
    _commandWorkDirLinePreview.setBackground(Color.LIGHT_GRAY);
    _commandWorkDirLinePreview.setSelectedTextColor(Color.LIGHT_GRAY);
    JScrollPane commandWorkDirLinePreviewSP = new JScrollPane(_commandWorkDirLinePreview);
    commandWorkDirLinePreviewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandWorkDirLinePreviewSP, c);
    main.add(commandWorkDirLinePreviewSP);

    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    JLabel enclosingFileLabel = new JLabel("Enclosing .djapp file:");
    gridbag.setConstraints(enclosingFileLabel, c);
    main.add(enclosingFileLabel);
    
    c.weightx = 1.0;
    c.weighty = 8.0;
    c.gridwidth = GridBagConstraints.RELATIVE;
    c.insets = compInsets;
    
    _commandEnclosingFileLine = new JTextPane();
    
    _commandEnclosingFileLine.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)  e.consume();
        else if (e.getKeyCode() == KeyEvent.VK_TAB) {
          e.consume();
          if (e.isShiftDown()) _commandWorkDirLine.requestFocus();
          else {
            _insertCommandButton.setEnabled(false);
            if (_editMode) {
              _saveCommandButton.requestFocus();
            }
            else _runCommandButton.requestFocus();
          }
        }
      }
      public void  keyReleased(KeyEvent e) { }
      public void  keyTyped(KeyEvent e) { }
    });
    JScrollPane commandEnclosingFileLineSP = new JScrollPane(_commandEnclosingFileLine);
    commandEnclosingFileLineSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandEnclosingFileLineSP, c);
    main.add(commandEnclosingFileLineSP);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandEnclosingFileBtn = new JButton("...");
    _commandEnclosingFileBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { chooseFile(_commandEnclosingFileLine); }
    });
    gridbag.setConstraints(_commandEnclosingFileBtn, c);
    main.add(_commandEnclosingFileBtn);
    
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    c.insets = labelInsets;
    JLabel commandEnclosingFileLinePreviewLabel = new JLabel("Enclosing .djapp file preview:");
    commandEnclosingFileLinePreviewLabel.setToolTipText(STALE_TOOLTIP);
    gridbag.setConstraints(commandEnclosingFileLinePreviewLabel, c);
    main.add(commandEnclosingFileLinePreviewLabel);
    
    c.weightx = 1.0;
    c.weighty = 8.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = compInsets;
    
    _commandEnclosingFileLinePreview = new JTextPane();
    _commandEnclosingFileLinePreview.setToolTipText(STALE_TOOLTIP);
    _commandEnclosingFileLineDoc = (StyledDocument)_commandEnclosingFileLinePreview.getDocument();
    
    _commandEnclosingFileLinePreview.setEditable(false);
    _commandEnclosingFileLinePreview.setBackground(Color.LIGHT_GRAY);
    _commandEnclosingFileLinePreview.setSelectedTextColor(Color.LIGHT_GRAY);
    JScrollPane commandEnclosingFileLinePreviewSP = new JScrollPane(_commandEnclosingFileLinePreview);
    commandEnclosingFileLinePreviewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    gridbag.setConstraints(commandEnclosingFileLinePreviewSP, c);
    main.add(commandEnclosingFileLinePreviewSP);
    
    
    panel.add(main, BorderLayout.CENTER);
    JPanel bottom = new JPanel();
    bottom.setBorder(new EmptyBorder(5, 5, 5, 5));
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
    bottom.add(Box.createHorizontalGlue());
    if (!_editMode) {
      bottom.add(_runCommandButton);
    }
    bottom.add(_saveCommandButton);
    bottom.add(_insertCommandButton);
    bottom.add(_cancelCommandButton);
    bottom.add(Box.createHorizontalGlue());
    panel.add(bottom, BorderLayout.SOUTH);
    
    
    _documentListener = new DocumentListener() {
      public void update(DocumentEvent e) {
        assert EventQueue.isDispatchThread();
        try {
          
          _commandLineDoc.remove(0,_commandLineDoc.getLength());
          String text = StringOps.replaceVariables(_commandLine.getText(), _props, PropertyMaps.GET_LAZY);
          _commandLineDoc.insertString(_commandLineDoc.getLength(), StringOps.unescapeFileName(text), null);
          
          
          colorVariables(_commandLine,
                         _props,
                         this,
                         _commandLineCmdAS,
                         _varCommandLineCmdStyle,
                         _varErrorCommandLineCmdStyle);
          _commandLinePreviewLabel.setText("<html>Command line preview:<br>(" + _commandLinePreview.getText().length()+
                                           " characters)</html>");
        }
        catch(BadLocationException ble) { _commandLinePreview.setText("Error."); }
      }
      public void changedUpdate(DocumentEvent e) { update(e); }
      public void insertUpdate(DocumentEvent e) { update(e); }
      public void removeUpdate(DocumentEvent e)  { update(e); }
    };
    _commandLine.getDocument().addDocumentListener(_documentListener);
    _documentListener.changedUpdate(null);
    
    
    _workDirDocumentListener = new DocumentListener() {
      public void update(DocumentEvent e) {
        assert EventQueue.isDispatchThread();
        try {
          
          _commandWorkDirLineDoc.remove(0,_commandWorkDirLineDoc.getLength());
          String text = StringOps.replaceVariables(_commandWorkDirLine.getText(), _props, PropertyMaps.GET_LAZY);
          _commandWorkDirLineDoc.insertString(0, StringOps.unescapeFileName(text), null);
          
          
          colorVariables(_commandWorkDirLine,
                         _props,
                         this,
                         _commandLineCmdAS,
                         _varCommandLineCmdStyle,
                         _varErrorCommandLineCmdStyle);
        }
        catch(BadLocationException ble) { _commandLinePreview.setText("Error: " + ble); }
      }
      public void changedUpdate(DocumentEvent e) { update(e); }
      public void insertUpdate(DocumentEvent e) { update(e); }
      public void removeUpdate(DocumentEvent e)  { update(e); }
    };
    _commandWorkDirLine.getDocument().addDocumentListener(_workDirDocumentListener);
    _commandWorkDirLine.setText("${drjava.working.dir}");
    _workDirDocumentListener.changedUpdate(null);
    
    
    _enclosingFileDocumentListener = new DocumentListener() {
      public void update(DocumentEvent e) {
        assert EventQueue.isDispatchThread();
        try {
          
          _commandEnclosingFileLineDoc.remove(0,_commandEnclosingFileLineDoc.getLength());
          String text = StringOps.replaceVariables(_commandEnclosingFileLine.getText(), _props, PropertyMaps.GET_LAZY);
          _commandEnclosingFileLineDoc.insertString(0, StringOps.unescapeFileName(text), null);
          
          
          colorVariables(_commandEnclosingFileLine,
                         _props,
                         this,
                         _commandLineCmdAS,
                         _varCommandLineCmdStyle,
                         _varErrorCommandLineCmdStyle);
        }
        catch(BadLocationException ble) {
          _commandLinePreview.setText("Error: " + ble);
        }
      }
      public void changedUpdate(DocumentEvent e) { update(e); }
      public void insertUpdate(DocumentEvent e) { update(e); }
      public void removeUpdate(DocumentEvent e)  { update(e); }
    };
    _commandEnclosingFileLine.getDocument().addDocumentListener(_enclosingFileDocumentListener);
    _commandEnclosingFileLine.setText("");
    _enclosingFileDocumentListener.changedUpdate(null);
    
    _lastCommandFocus = _commandLine;
    
    _commandLine.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        _lastCommandFocus = (JTextPane)e.getComponent();
        _insertCommandButton.setEnabled(true);
      }
      public void focusLost(FocusEvent e) {
        if ((e.getOppositeComponent() == _commandLinePreview) || 
            (e.getOppositeComponent() == _commandWorkDirLinePreview)) {
          _commandLine.requestFocus();
        }
      }
    });
    _commandWorkDirLine.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        _lastCommandFocus = (JTextPane)e.getComponent();
        _insertCommandButton.setEnabled(true);
      }
      public void focusLost(FocusEvent e) {
        if ((e.getOppositeComponent() == _commandLinePreview) || 
            (e.getOppositeComponent() == _commandWorkDirLinePreview)) {
          _commandWorkDirLine.requestFocus();
        }
      }
    });
    
    return panel;
  }
  
  
  protected void colorVariables(final JTextPane pane,
                                final PropertyMaps props,
                                final DocumentListener dl,
                                final SimpleAttributeSet normal,
                                final SimpleAttributeSet variable,
                                final SimpleAttributeSet error) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        StyledDocument doc = (StyledDocument)pane.getDocument();
        doc.removeDocumentListener(dl);
        String str = pane.getText();
        BalancingStreamTokenizer tok = new BalancingStreamTokenizer(new StringReader(str), '$');
        tok.wordRange(0,255);
        tok.addQuotes("${", "}");
        
        int pos = 0;
        doc.setCharacterAttributes(0,str.length(),normal,true);
        String next = null;
        try {
          while((next=tok.getNextToken()) != null) {
            if ((tok.token() == BalancingStreamTokenizer.Token.QUOTED) && (next.startsWith("${"))) {
              if (next.endsWith("}")) {
                String key;
                String attrList = "";
                int firstCurly = next.indexOf('}');
                int firstSemi = next.indexOf(';');
                if (firstSemi < 0) {
                  key = next.substring(2,firstCurly);
                }
                else {
                  key = next.substring(2,firstSemi);
                  attrList = next.substring(firstSemi+1,next.length()-1).trim();
                }
                boolean found = false;
                for(String category: props.getCategories()) {
                  DrJavaProperty p = props.getProperty(category, key);
                  if (p != null) {
                    found = true;
                    doc.setCharacterAttributes(pos,pos+next.length(),variable,true);
                    
                    
                    
                    if (attrList.length() > 0) {
                      
                      int subpos = pos + 2 + key.length() + 1;
                      int added = 0;
                      BalancingStreamTokenizer atok = new BalancingStreamTokenizer(new StringReader(attrList), '$');
                      atok.wordRange(0,255);
                      atok.addQuotes("${", "}");
                      atok.addQuotes("\"", "\"");
                      atok.addKeyword(";");
                      atok.addKeyword("=");
                      
                      String n = null;
                      while((n=atok.getNextToken()) != null) {
                        if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.NORMAL) ||
                            n.trim().equals(";") || n.trim().equals("=") || n.trim().startsWith("\"")) {
                          doc.setCharacterAttributes(subpos,pos+next.length(),error,true);
                          break;
                        }
                        added += n.length();
                        String name = n.trim();
                        n = atok.getNextToken();
                        if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.KEYWORD) || 
                            (!n.trim().equals("="))) {
                          doc.setCharacterAttributes(subpos,pos+next.length(),error,true);
                          break;
                        }
                        added += n.length();
                        n = atok.getNextToken();
                        if ((n == null) || (atok.token() != BalancingStreamTokenizer.Token.QUOTED) || 
                            (!n.trim().startsWith("\""))) {
                          doc.setCharacterAttributes(subpos,pos+next.length(),error,true);
                          break;
                        }
                        added += n.length();
                        n = atok.getNextToken();
                        if ((n != null && (atok.token() != BalancingStreamTokenizer.Token.KEYWORD || ! n.equals(";"))) ||
                            (n == null && atok.token() != BalancingStreamTokenizer.Token.END)) {
                          doc.setCharacterAttributes(subpos,pos+next.length(),error,true);
                          break;
                        }
                        if (n != null) { added += n.length(); }
                        try { p.getAttribute(name); }
                        catch(IllegalArgumentException e) { 
                          doc.setCharacterAttributes(subpos, subpos + added, error, true); 
                        }
                        subpos += added;
                      }
                    }
                  }
                  if (found) break;
                }
                if (!found) doc.setCharacterAttributes(pos,pos+next.length(),error,true);
              }
              else doc.setCharacterAttributes(pos,pos+next.length(),error,true);
            }
            else doc.setCharacterAttributes(pos,pos+next.length(),normal,true);
            pos += next.length();
          }
        }
        catch(IOException e) {  }
        finally { doc.addDocumentListener(dl); }
      }
    });
  }
  
  
  private void _cancel() {
    _lastState = new FrameState(this);
    this.setVisible(false);
    if (_cm != null) { _cm.signal(); }
  }
  
  
  
  
  public ExternalProcessPanel runCommand(String name, String cmdline, String workdir,
                                         String enclosingFile, PropertyMaps pm) {
    ((MutableFileProperty)pm.getProperty("enclosing.djapp.file")).setFile(enclosingFile.length() > 0?
                                                                            new File(enclosingFile):null);
    ProcessCreator pc = new GeneralProcessCreator(cmdline, workdir.trim(), pm);
    String label = "External";
    if (!name.equals("")) { label += ": " + name; }
    final ExternalProcessPanel panel = new ExternalProcessPanel(_mainFrame, label, pc);
    _mainFrame._tabs.addLast(panel);
    panel.getMainPanel().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) { _mainFrame._lastFocusOwner = panel; }
    });
    panel.setVisible(true);
    _mainFrame.showTab(panel, true);
    _mainFrame._tabbedPane.setSelectedComponent(panel);
    
    EventQueue.invokeLater(new Runnable() { public void run() { panel.requestFocusInWindow(); } });
    return panel;
  }
  
  
  private void _runCommand() {
    _mainFrame.updateStatusField("Executing external process...");
    GeneralProcessCreator.LOG.log("_runCommand(): ${enclosing.djapp.file} = " + _commandEnclosingFileLine.getText());
    
    _mainFrame.removeModalWindowAdapter(this);
    if (_commandLinePreview.getText().length() > 0) {
      try { 
        _props = PropertyMaps.TEMPLATE.clone();
        PropertyMaps pm = _props.clone();
        runCommand("", _commandLine.getText(), _commandWorkDirLine.getText(), 
                   _commandEnclosingFileLine.getText().trim(), pm);
      } 
      catch(CloneNotSupportedException e) { throw new edu.rice.cs.util.UnexpectedException(e); }
    }
    
    
    _saveSettings();
    this.setVisible(false);
    if (_cm != null) { _cm.signal(); }    
  }

  
  private void _saveCommand() {          
    if (_editMode) {
      final Vector<String> names = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
      _mainFrame.removeModalWindowAdapter(this);
      String name = JOptionPane.showInputDialog(this, "Name for saved process:", names.get(_editIndex));
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      if (name == null) {
        
        _saveSettings();
        this.setVisible(false);
        if (_cm != null) { _cm.signal(); }
        return;
      }
      editInMenu(_editIndex, name, _commandLine.getText(), _commandWorkDirLine.getText(), 
                 _commandEnclosingFileLine.getText());
    }
    else {
      int count = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT);
      _mainFrame.removeModalWindowAdapter(this);
      String name = JOptionPane.showInputDialog(this, "Name for saved process:", "External Java " + (count+1));
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      if (name == null) {
        
        _saveSettings();
        this.setVisible(false);
        if (_cm != null) { _cm.signal(); }
        return;
      }
      addToMenu(name, _commandLine.getText(), _commandWorkDirLine.getText(), _commandEnclosingFileLine.getText());
    }
    
    
    _saveSettings();
    this.setVisible(false);
    if (_cm != null) { _cm.signal(); }
  }  

  
  public static int addToMenu(String name, String cmdline, String workdir, String enclosingFile) {
    GeneralProcessCreator.LOG.log("addToMenu(): enclosingFile = " + enclosingFile);
    int count = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_COUNT);
    ++count;
    final Vector<String> names = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    final Vector<String> cmdlines = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    final Vector<String> workdirs = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    final Vector<String> enclosingFiles = 
      DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    
    names.add(name);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_NAMES,names);
    
    cmdlines.add(cmdline);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES,cmdlines);
    
    workdirs.add(workdir);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS,workdirs);
    
    enclosingFiles.add(enclosingFile);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES,enclosingFiles);
    
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_COUNT,count);
    
    return count;
  }
  
  
  public static void editInMenu(int editIndex, String name, String cmdline, String workdir, String enclosingFile) {
    GeneralProcessCreator.LOG.log("editInMenu(): enclosingFile = " + enclosingFile);
    final Vector<String> names = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    final Vector<String> cmdlines = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    final Vector<String> workdirs = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    final Vector<String> enclosingFiles = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    
    names.set(editIndex,name);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_NAMES,names);
    
    cmdlines.set(editIndex,cmdline);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES,cmdlines);
    
    workdirs.set(editIndex,workdir);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS,workdirs);
    
    enclosingFiles.set(editIndex,enclosingFile);
    DrJava.getConfig().setSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES,enclosingFiles);
  }
  
  
  public static void saveToFile(int index, File f) {
    final Vector<String> names = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_NAMES);
    final Vector<String> cmdlines = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_CMDLINES);
    final Vector<String> workdirs = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_WORKDIRS);
    final Vector<String> enclosingFiles = DrJava.getConfig().getSetting(OptionConstants.EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES);
    
    XMLConfig xc = new XMLConfig();


    xc.set("drjava/extprocess/name", names.get(index));
    xc.set("drjava/extprocess/cmdline", cmdlines.get(index));
    xc.set("drjava/extprocess/workdir", workdirs.get(index));
    xc.set("drjava/extprocess/enlcosingfile", enclosingFiles.get(index));
    xc.save(f);
  }
  
  
  private boolean _saveSettings() {
    _lastState = new FrameState(this);
    return true;
  }
  
  
  private void _insertVariableCommand() {
    _props.clearVariables();
    _mainFrame.removeModalWindowAdapter(this);
    _insertVarDialogMonitor.reset();
    _insertVarDialog.setVisible(true);
    
    
    new Thread(new Runnable() {
      public void run() {
        _insertVarDialogMonitor.attemptEnsureSignaled();
        
        EventQueue.invokeLater(new Runnable() {
          public void run() {
            EventQueue.invokeLater(new Runnable() { public void run() { ExecuteExternalDialog.this.toFront(); } });
            _mainFrame.installModalWindowAdapter(ExecuteExternalDialog.this, LambdaUtil.NO_OP, CANCEL);

            edu.rice.cs.plt.tuple.Pair<String,DrJavaProperty> selected = _insertVarDialog.getSelected();
            if (selected != null) {
              String text = _lastCommandFocus.getText();
              Caret caret = _lastCommandFocus.getCaret();
              int min = Math.min(caret.getDot(), caret.getMark());
              int max = Math.max(caret.getDot(), caret.getMark());
              if (min != max) { text = text.substring(0, min) + text.substring(max); }
              text = text.substring(0,min) + "${" + selected.first() + "}" + text.substring(min);
              _lastCommandFocus.setText(text);
              caret.setDot(min+selected.first().length()+2);
              _lastCommandFocus.setCaret(caret);
            }
          }
        });
      }
    }).start();
  }  
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { _cancel(); }
  };
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      try { _props = PropertyMaps.TEMPLATE.clone(); } catch(CloneNotSupportedException e) {
        throw new edu.rice.cs.util.UnexpectedException(e);
      }
      _mainFrame.hourglassOn();
      _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      _documentListener.changedUpdate(null);
      _workDirDocumentListener.changedUpdate(null);
      toFront();
    }
    else {
      _mainFrame.removeModalWindowAdapter(this);
      _mainFrame.hourglassOff();
      _mainFrame.toFront();
    }
    super.setVisible(vis);
  }
  
  
  protected void chooseDir(JTextPane pane) {
    
    File wd = new File(StringOps.replaceVariables(pane.getText().trim(), _props, PropertyMaps.GET_CURRENT));
    if ((pane.getText().equals("")) ||
        (!wd.exists()) &&
        (!wd.isDirectory())) {
      wd = new File(System.getProperty("user.dir"));
    }
    
    _dirChooser.setSelectedFile(wd);
    _mainFrame.removeModalWindowAdapter(this);
    int returnValue = _dirChooser.showDialog(wd);
    _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);      
    if (returnValue == DirectoryChooser.APPROVE_OPTION) {
      File chosen = _dirChooser.getSelectedDirectory();
      if (chosen != null) { pane.setText(chosen.toString()); };
    }
  }
  
  
  protected void chooseFile(JTextPane pane) {
    
    File wd = new File(StringOps.replaceVariables(pane.getText().trim(), _props, PropertyMaps.GET_CURRENT));
    if ((pane.getText().equals("")) ||
        (!wd.exists()) &&
        (!wd.isFile())) {
      wd = null;
    }
    
    _fileChooser.setSelectedFile(wd);
    _mainFrame.removeModalWindowAdapter(this);
    int returnValue = _fileChooser.showOpenDialog(this);
    _mainFrame.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);      
    if (returnValue == DirectoryChooser.APPROVE_OPTION) {
      File chosen = _fileChooser.getSelectedFile();
      if (chosen != null) { pane.setText(chosen.toString()); } else { pane.setText(""); }
    }
  }
}
