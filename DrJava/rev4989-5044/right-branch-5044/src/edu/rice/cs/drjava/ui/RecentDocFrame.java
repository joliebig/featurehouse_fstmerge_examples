

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.DisplayManager;
import edu.rice.cs.util.swing.Utilities;


public class RecentDocFrame extends JWindow {
  
  MainFrame _frame;
  
  
  DisplayManager<OpenDefinitionsDocument> _displayManager = MainFrame.getOddDisplayManager30();
  
  
  JLabel _label;
  
  JPanel _panel;
  
  JTextPane _textpane;
  
  JScrollPane _scroller;
  
  int _current = 0;
  
  int _padding = 4;
  
  LinkedList<OpenDefinitionsDocument> _docs = new LinkedList<OpenDefinitionsDocument>();
  
  private OptionListener<Color> _colorListener = new OptionListener<Color>() {
    public void optionChanged(OptionEvent<Color> oce) { updateFontColor(); }
  };
  
  private OptionListener<Font> _fontListener = new OptionListener<Font>() {
    public void optionChanged(OptionEvent<Font> oce) { updateFontColor(); }
  };
  
  private OptionListener<Boolean> _antialiasListener = new OptionListener<Boolean>() {
    public void optionChanged(OptionEvent<Boolean> oce) { updateFontColor(); }
  };
  
  private OptionListener<Boolean> _showSourceListener = new OptionListener<Boolean>() {
    public void optionChanged(OptionEvent<Boolean> oce) { _showSource = oce.value; }
  };
  
  
  boolean _antiAliasText = false;
  
  
  boolean _showSource;
  
  public RecentDocFrame(MainFrame f) {
    super();
    _frame = f;
    _current = 0;
    _label = new JLabel("...") {
      
      protected void paintComponent(Graphics g) {
        if (_antiAliasText && g instanceof Graphics2D) {
          Graphics2D g2d = (Graphics2D)g;
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        super.paintComponent(g);
      }
    };
    _panel = new JPanel();
    _scroller = new JScrollPane();
    _textpane = new JTextPane() {
      
      protected void paintComponent(Graphics g) {
        if (_antiAliasText && g instanceof Graphics2D) {
          Graphics2D g2d = (Graphics2D)g;
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        super.paintComponent(g);
      }
    };
    
    _textpane.setText("...");
    _scroller.getViewport().add(_textpane);
    _scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    _scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    _scroller.setMaximumSize(new Dimension(300,200));
    
    _panel.setLayout(new BorderLayout());
    _panel.add(_label, BorderLayout.NORTH);
    _panel.add(_scroller, BorderLayout.SOUTH);
    
    getContentPane().add(_panel);
    pack();
    updateFontColor();
    _showSource = DrJava.getConfig().getSetting(OptionConstants.SHOW_SOURCE_WHEN_SWITCHING);
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_BACKGROUND_COLOR, _colorListener);
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_NORMAL_COLOR, _colorListener);
    DrJava.getConfig().addOptionListener(OptionConstants.FONT_MAIN, _fontListener);
    DrJava.getConfig().addOptionListener(OptionConstants.TEXT_ANTIALIAS, _antialiasListener);
    DrJava.getConfig().addOptionListener(OptionConstants.SHOW_SOURCE_WHEN_SWITCHING, _showSourceListener);
  }
  
  private void updateFontColor() {
    Font  mainFont = DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN);
    Color backColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_BACKGROUND_COLOR);
    Color fontColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR);
    
    Font titleFont = mainFont.deriveFont((float) (mainFont.getSize() + 3));
    _antiAliasText = DrJava.getConfig().getSetting(OptionConstants.TEXT_ANTIALIAS).booleanValue();
    
    _label.setForeground(fontColor);
    _panel.setBackground(backColor);
    _label.setFont(titleFont);
    _textpane.setForeground(fontColor);
    _textpane.setFont(mainFont);;
    _textpane.setBackground(backColor);
    _scroller.setBackground(backColor);
    _scroller.setBorder(new EmptyBorder(0,0,0,0));
    _panel.setBorder(new LineBorder(fontColor, 1));
  }
  
  public void pokeDocument(OpenDefinitionsDocument d) {
    if (_docs.contains(d)) {
      _current = _docs.indexOf(d);
      reset();
    }
    else _docs.addFirst(d);
  }
  
  
  public void closeDocument(OpenDefinitionsDocument d) { _docs.remove(d); }
  
  private void show(int _current) {
    OpenDefinitionsDocument doc = _docs.get(_current);
    
    String text = getTextFor(doc);
    
    _label.setText(_displayManager.getName(doc));
    _label.setIcon(_displayManager.getIcon(doc));
    
    if (text.length() > 0) {
      
      _textpane.setText(text);
      _scroller.setPreferredSize(_textpane.getPreferredScrollableViewportSize());
      if (_scroller.getPreferredSize().getHeight() > 200)
        _scroller.setPreferredSize(new Dimension((int)_scroller.getPreferredSize().getWidth(), 200));
      
      _scroller.setVisible(_showSource);
    }
    else _scroller.setVisible(false);
    
    Dimension d = _label.getMinimumSize();
    d.setSize(d.getWidth() + _padding*2, d.getHeight() + _padding*2);
    _label.setPreferredSize(d);
    _label.setHorizontalAlignment(SwingConstants.CENTER);
    _label.setVerticalAlignment(SwingConstants.CENTER);
    pack();
    centerH();
  }
  
  
  public void next() {
    if (_docs.size() > 0) {
      _current++;
      if (_current >= _docs.size()) _current = 0;
      show(_current);
    }
  }
  
  
  public void prev() {
    if (_docs.size() > 0) {
      _current--;
      if (_current < 0) _current = _docs.size() - 1;
      show(_current);
    }
  }
  
  private String getTextFor(OpenDefinitionsDocument doc) {
    DefinitionsPane pane = _frame.getDefPaneGivenODD(doc);
    String endl = "\n"; 
    int loc = pane.getCaretPosition();
    int start = loc;
    int end = loc;
    String text;
    text = doc.getText();
    
    
    for (int i = 0; i < 4; i++) {
      if (start > 0) start = text.lastIndexOf(endl, start-endl.length());
    }
    if (start == -1) start = 0;
    
    


    if (doc.getLength() >= endl.length() && text.substring(start, start+endl.length()).equals(endl)) 
      start += endl.length();
    
    int index;
    for (int i = 0; i < 4; i++) {
      if (end < doc.getLength()) {
        index = text.indexOf(endl, end + endl.length());
        if (index != -1) end = index;
      }
    }
    if (end < start) end = start;
    text = text.substring(start, end);
    return text;
  }
  
  
  public void first() {
    _current = 0;
    next();
  }
  
  public void refreshColor() { }
  
  
  public void setVisible(boolean v) {
    centerH();
    if (_docs.size() > 0) {
      if (v) { 
        centerV();
        refreshColor();
        first();
      }
      else reset();
      super.setVisible(v);
    }
  }
  
  
  private void centerH() { Utilities.setPopupLoc(this, _frame); }
  
  
  private void centerV() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    setLocation((int)getLocation().getX(), (screenSize.height - frameSize.height) / 2);
  }
  
  
  public void reset() {
    if (_current < _docs.size()) _docs.addFirst(_docs.remove(_current));
  }
  
  
  public OpenDefinitionsDocument getDocument() {
    if (_docs.size() > 0) return _docs.getFirst();
    return null;
  }
  





  
}
