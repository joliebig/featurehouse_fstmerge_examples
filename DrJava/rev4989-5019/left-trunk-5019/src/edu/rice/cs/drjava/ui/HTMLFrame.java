

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.html.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;


public class HTMLFrame extends SwingFrame {
  
  private static final int FRAME_WIDTH = 750;
  private static final int FRAME_HEIGHT = 600;
  private static final int LEFT_PANEL_WIDTH = 250;
  private JEditorPane _mainDocPane;
  private JScrollPane _mainScroll;
  private JSplitPane _splitPane;
  private JPanel _splitPaneHolder;
  private JEditorPane _contentsDocPane;
  private JPanel _closePanel;
  private JButton _closeButton;
  private JButton _backButton;
  private JButton _forwardButton;
  protected URL _baseURL;
  private ArrayList<HyperlinkListener> _hyperlinkListeners;
  private boolean _linkError;
  private URL _lastURL;
  
  private JPanel _navPane;
  
  protected HistoryList _history;
  
  private HyperlinkListener _resetListener = new HyperlinkListener() {
    public void hyperlinkUpdate(HyperlinkEvent e) {
      if (_linkError && e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        _resetMainPane();
      }
    }
  };
  
  protected static class HistoryList {
    private HistoryList next = null;
    private final HistoryList prev;
    protected final URL contents;
    private HistoryList(URL contents) {
      this.contents = contents;
      this.prev = null;
    }
    private HistoryList(URL contents, HistoryList prev) {
      this.contents = contents;
      this.prev = prev;
      prev.next = this;
    }
  }
  
  public static abstract class ResourceAction extends AbstractAction {
    public ResourceAction(String name, String iconName) {
      super(name,MainFrame.getIcon(iconName));
    }
  }
  
  private static abstract class ConsolidatedAction extends ResourceAction {
    private ConsolidatedAction(String name) {
      super(name,name+"16.gif");
    }
  }
  
  private Action _forwardAction = new ConsolidatedAction("Forward") {
    public void actionPerformed(ActionEvent e) {
      _history = _history.next;
      
      
      _backAction.setEnabled(true);
      
      if (_history.next == null) {
        
        _forwardAction.setEnabled(false);
      }
      _displayPage(_history.contents);
    }
  };
  
  private Action _backAction = new ConsolidatedAction("Back") {
    public void actionPerformed(ActionEvent e) {
      _history = _history.prev;
      
      
      _forwardAction.setEnabled(true);
      
      if (_history.prev == null) {
        
        _backAction.setEnabled(false);
      }
      _displayPage(_history.contents);
    }
  };
  
  private Action _closeAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent e) {
      HTMLFrame.this.setVisible(false);
    }
  };
  
  private static JButton makeButton(Action a, int horTextPos, int left, int right) {
    JButton j = new JButton(a);
    j.setHorizontalTextPosition(horTextPos);
    j.setVerticalTextPosition(JButton.CENTER);
    
    
    j.setMargin(new Insets(3,left+3,3,right+3));
    return j;
  }
  
  public void addHyperlinkListener(HyperlinkListener linkListener) {
    _hyperlinkListeners.add(linkListener);
    _contentsDocPane.addHyperlinkListener(linkListener);
    _mainDocPane.addHyperlinkListener(linkListener);
  }
  
  
  public HTMLFrame(String frameName, URL introUrl, URL indexUrl, String iconString) {
    this(frameName, introUrl, indexUrl, iconString, null);
  }
  
  
  public HTMLFrame(String frameName, URL introUrl, URL indexUrl, String iconString, File baseDir) {
    super(frameName);
    
    _contentsDocPane = new JEditorPane();
    _contentsDocPane.setEditable(false);
    JScrollPane contentsScroll = new BorderlessScrollPane(_contentsDocPane);
    
    _mainDocPane = new JEditorPane();
    _mainDocPane.setEditable(false);
    _mainScroll = new BorderlessScrollPane(_mainDocPane);
    
    _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, contentsScroll, _mainScroll);
    _splitPane.setDividerLocation(LEFT_PANEL_WIDTH);
    _splitPaneHolder = new JPanel(new GridLayout(1,1));
    _splitPaneHolder.setBorder(new EmptyBorder(0,5,0,5));
    _splitPaneHolder.add(_splitPane);
    
    _closeButton = new JButton(_closeAction);
    _backButton = makeButton(_backAction,JButton.RIGHT,0,3);
    _forwardButton = makeButton(_forwardAction,JButton.LEFT,3,0);
    _backAction.setEnabled(false);
    _forwardAction.setEnabled(false);
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.EAST);
    _closePanel.setBorder(new EmptyBorder(5,5,5,5)); 
    _navPane = new JPanel();
    _navPane.setBackground(new Color(0xCCCCFF));
    _navPane.setLayout(new BoxLayout(_navPane,BoxLayout.X_AXIS));
    JLabel icon = new JLabel(MainFrame.getIcon(iconString));
    _navPane.add(icon);
    _navPane.add(Box.createHorizontalStrut(8));
    _navPane.add(Box.createHorizontalGlue());
    _navPane.add(_backButton);
    _navPane.add(Box.createHorizontalStrut(8));
    _navPane.add(_forwardButton);
    _navPane.add(Box.createHorizontalStrut(3));
    _navPane.setBorder(new EmptyBorder(0,0,0,5));
    JPanel navContainer = new JPanel(new GridLayout(1,1));
    navContainer.setBorder(new CompoundBorder(new EmptyBorder(5,5,5,5), new EtchedBorder()));
    
    navContainer.add(_navPane);
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(navContainer, BorderLayout.NORTH);
    cp.add(_splitPaneHolder, BorderLayout.CENTER);
    cp.add(_closePanel, BorderLayout.SOUTH);
    
    _linkError = false;
    _hyperlinkListeners = new ArrayList<HyperlinkListener>();
    _hyperlinkListeners.add(_resetListener);
    _mainDocPane.addHyperlinkListener(_resetListener);
    
    if (baseDir == null) _baseURL = null;
    else
      try { _baseURL = FileOps.toURL(baseDir); }
    catch(MalformedURLException ex) {
      throw new UnexpectedException(ex);
    }
    
    
    if (indexUrl == null) _displayContentsError(null);
    else
      try {
      _contentsDocPane.setPage(indexUrl);
      if (_baseURL != null) ((HTMLDocument)_contentsDocPane.getDocument()).setBase(_baseURL);
    }
    catch (IOException ioe) {
      
      _displayContentsError(indexUrl, ioe);
    }
    
    if (introUrl == null) _displayMainError(null);
    else {
      _history = new HistoryList(introUrl);
      _displayPage(introUrl);
      _displayPage(introUrl);
    }
    
    
    setSize(FRAME_WIDTH, FRAME_HEIGHT);
    Utilities.setPopupLoc(this, null);
    
    initDone(); 
  }
  
  
  protected void _hideNavigationPane() {
    _splitPaneHolder.remove(_splitPane);
    _splitPaneHolder.add(_mainScroll);
  }
  
  private void _resetMainPane() {
    _linkError = false;
    
    _mainDocPane = new JEditorPane();
    _mainDocPane.setEditable(false);
    for (int i = 0; i < _hyperlinkListeners.size(); i++) {
      _mainDocPane.addHyperlinkListener(_hyperlinkListeners.get(i));
    }
    _displayPage(_lastURL);
    
    _splitPane.setRightComponent(new BorderlessScrollPane(_mainDocPane));
    _splitPane.setDividerLocation(LEFT_PANEL_WIDTH);
  }
  
  
  private void _displayPage(URL url) {
    if (url == null) return;
    try {
      _mainDocPane.setPage(url);
      if (_baseURL != null) {
        ((HTMLDocument)_contentsDocPane.getDocument()).setBase(_baseURL);
      }
      _lastURL = url;
    }
    catch (IOException ioe) {
      String path = url.getPath();
      try {
        URL newURL = new URL(_baseURL + path);
        _mainDocPane.setPage(newURL);
        if (_baseURL != null) {
          ((HTMLDocument)_contentsDocPane.getDocument()).setBase(_baseURL);
        }
        _lastURL = newURL;
      }
      catch (IOException ioe2) {
        
        _displayMainError(url, ioe2);
        
      }
    }
  }
  
  
  private void _displayMainError(URL url) {
    if (!_linkError) {
      _linkError = true;
      _mainDocPane.setText(getErrorText(url));
    }
    else _resetMainPane();
  }
  
  
  private void _displayMainError(URL url, Exception ex) {
    if (!_linkError) {
      _linkError = true;
      _mainDocPane.setText(getErrorText(url) + "\n" + ex);
    }
    else _resetMainPane();
  }
  
  
  private void _displayContentsError(URL url) {
    _contentsDocPane.setText(getErrorText(url));
  }
  
  
  private void _displayContentsError(URL url, Exception ex) {
    _contentsDocPane.setText(getErrorText(url) + "\n" + ex);
  }
  
  
  protected String getErrorText(URL url) {
    return "Could not load the specified URL: " + url;
  }
  
  public void jumpTo(URL url) {
    _history = new HistoryList(url,_history); 
    
    _backAction.setEnabled(true); 
    _forwardAction.setEnabled(false); 
    
    _displayPage(url);
  }
}
