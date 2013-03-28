

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

import java.io.Serializable;

import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.ConsoleDocument;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.platform.PlatformFactory;


public abstract class AbstractConsoleController implements Serializable {
  
  protected InteractionsDJDocument _adapter;

  
  protected InteractionsPane _pane;

  
  protected SimpleAttributeSet _defaultStyle;

  
  protected final SimpleAttributeSet _systemOutStyle;

  
  protected final SimpleAttributeSet _systemErrStyle;

  

  
  Action switchToPrevPaneAction;

  
  Action switchToNextPaneAction;

  
  protected AbstractConsoleController(InteractionsDJDocument adapter, InteractionsPane pane) {
    _adapter = adapter;
    _pane = pane;
    _defaultStyle = new SimpleAttributeSet();
    _systemOutStyle = new SimpleAttributeSet();
    _systemErrStyle = new SimpleAttributeSet();
  }

  
  public abstract ConsoleDocument getConsoleDoc();

  
  protected void _init() {
    _addDocumentStyles();
    _setupModel();
    _setupView();
  }

  
  protected void _addDocumentStyles() {
    
    _adapter.setDocStyle(ConsoleDocument.DEFAULT_STYLE, _defaultStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_NORMAL_COLOR,
                                         new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        setDefaultFont(oe.value);
      }
    });

    
    _systemOutStyle.addAttributes(_defaultStyle);
    _systemOutStyle.addAttribute(StyleConstants.Foreground,
                                 DrJava.getConfig().getSetting(OptionConstants.SYSTEM_OUT_COLOR));
    _adapter.setDocStyle(ConsoleDocument.SYSTEM_OUT_STYLE, _systemOutStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.SYSTEM_OUT_COLOR,
                                         new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _systemOutStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });

    
    _systemErrStyle.addAttributes(_defaultStyle);
    _systemErrStyle.addAttribute(StyleConstants.Foreground,
                                 DrJava.getConfig().getSetting(OptionConstants.SYSTEM_ERR_COLOR));
    _adapter.setDocStyle(ConsoleDocument.SYSTEM_ERR_STYLE, _systemErrStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.SYSTEM_ERR_COLOR,
                                         new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _systemErrStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });
  }

  
  public void setDefaultFont(Font f) {
    Color c = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR);
    setDefaultFont(f, c);
  }

  
  public void setDefaultFont(Color c) {
    Font f = DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN);
    setDefaultFont(f, c);
  }

  
  public void setDefaultFont(Font f, Color c) {
    if (PlatformFactory.ONLY.isMacPlatform()) {
      SimpleAttributeSet fontSet = new SimpleAttributeSet();
      StyleConstants.setFontFamily(fontSet, f.getFamily());
      StyleConstants.setFontSize(fontSet, f.getSize());
      StyleConstants.setBold(fontSet, f.isBold());
      StyleConstants.setItalic(fontSet, f.isItalic());
      if (c != null) {
        StyleConstants.setForeground(fontSet, c);
      }
      _adapter.setCharacterAttributes(0, _adapter.getLength()+1, fontSet, false);
      _pane.setCharacterAttributes(fontSet, false);
      _updateStyles(fontSet);
    }
  }

  
  protected void _updateStyles(AttributeSet newSet) {
    _defaultStyle.addAttributes(newSet);
    _systemOutStyle.addAttributes(newSet);
    _systemErrStyle.addAttributes(newSet);
  }

  
  protected abstract void _setupModel();

  
  class CaretUpdateListener implements DocumentListener {
    public void insertUpdate(final DocumentEvent e) {
      Utilities.invokeLater(new Runnable() {
        public void run() {
          
          ConsoleDocument doc = getConsoleDoc();
          int caretPos = _pane.getCaretPosition();
          int promptPos = doc.getPromptPos();
          int length = doc.getLength();
          

          
          
          int prevPromptPos = promptPos;
          if (e.getOffset() < promptPos) {
            
            
            prevPromptPos = promptPos - e.getLength();
          }
          
          if (! doc.hasPrompt()) {

            
            moveToEnd();
          }
          
          
          else if (promptPos <= length) {
            if (caretPos < prevPromptPos) {
              
              
              moveToPrompt();
            }
            else {
              
              
              int size = promptPos - prevPromptPos;
              if (size > 0)  moveTo(caretPos + size);
            }
          }
        }
      });
    }

    public void removeUpdate(DocumentEvent e) { _ensureLegalCaretPos(); }
    public void changedUpdate(DocumentEvent e) { _ensureLegalCaretPos(); }
    
    protected void _ensureLegalCaretPos() {
      Utilities.invokeLater(new Runnable() {
        public void run() { 
          int length = getConsoleDoc().getLength();
          if (_pane.getCaretPosition() > length) _pane.setCaretPosition(length);
        }
      });
    }
  }


  
  protected void _setupView() {
    KeyStroke beginLineKey = DrJava.getConfig().getSetting(OptionConstants.KEY_BEGIN_LINE);
    _pane.addActionForKeyStroke(beginLineKey, gotoPromptPosAction);
    _pane.addActionForKeyStroke(KeyBindingManager.Singleton.addShiftModifier(beginLineKey),
                                selectToPromptPosAction);
    KeyStroke endLineKey = DrJava.getConfig().getSetting(OptionConstants.KEY_END_LINE);
    _pane.addActionForKeyStroke(endLineKey, gotoEndAction);
    _pane.addActionForKeyStroke(KeyBindingManager.Singleton.addShiftModifier(endLineKey),
                                selectToEndAction);

    DrJava.getConfig().addOptionListener(OptionConstants.KEY_BEGIN_LINE,
                                         new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(oe.value, gotoPromptPosAction);
        _pane.addActionForKeyStroke(KeyBindingManager.Singleton.addShiftModifier(oe.value),
                                    selectToPromptPosAction);
     }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_END_LINE,
                                         new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(oe.value, gotoEndAction);
        _pane.addActionForKeyStroke(KeyBindingManager.Singleton.addShiftModifier(oe.value),
                                    selectToEndAction);
     }
    });
  }

  
  public InteractionsDJDocument getDocumentAdapter() { return _adapter; }

  
  public InteractionsPane getPane() { return _pane; }

  
  protected boolean _busy() { return ! getConsoleDoc().hasPrompt(); }

  
  AbstractAction newLineAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { getConsoleDoc().insertNewLine(_pane.getCaretPosition()); }
  };

  
  AbstractAction clearCurrentAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { getConsoleDoc().clearCurrentInput(); }
  };

  
  AbstractAction gotoEndAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { moveToEnd(); }
  };

  
  AbstractAction selectToEndAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { _pane.moveCaretPosition(getConsoleDoc().getLength()); }
  };

  
  AbstractAction gotoPromptPosAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { moveToPrompt(); }
  };

  
  AbstractAction selectToPromptPosAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      
      _pane.moveCaretPosition(getConsoleDoc().getPromptPos());
    }
  };

  
  void moveToEnd() { moveTo(getConsoleDoc().getLength()); }
  
  
  void moveToPrompt() { moveTo(getConsoleDoc().getPromptPos()); }
  
  
  void moveTo(int pos) {
    
    if (pos < 0) pos = 0;
    else {
      int maxLen = getConsoleDoc().getLength(); 
      if (pos > maxLen) pos = maxLen;
    }
    _pane.setCaretPosition(pos);
  }

  public void setPrevPaneAction(Action a) {
    switchToPrevPaneAction = a;

    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_PANE),
                                switchToPrevPaneAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_PREVIOUS_PANE, new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_PANE),
                                    switchToPrevPaneAction);
      }
    });
  }

  public void setNextPaneAction(Action a) {
    switchToNextPaneAction = a;

    
    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_PANE),
                                switchToNextPaneAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_NEXT_PANE, new OptionListener<KeyStroke>() {
      public void optionChanged(OptionEvent<KeyStroke> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_PANE),
                                    switchToNextPaneAction);
      }
    });
  }
}
