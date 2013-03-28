

package edu.rice.cs.drjava.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.text.*;

import java.util.Vector;

import edu.rice.cs.util.text.ConsoleDocument;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.model.ClipboardHistoryModel;


public abstract class AbstractConsoleController  {
  
  
  protected final InteractionsDJDocument _interactionsDJDocument;

  
  protected final InteractionsPane _pane;

  
  protected final SimpleAttributeSet _defaultStyle;

  
  protected final SimpleAttributeSet _systemOutStyle;

  
  protected final SimpleAttributeSet _systemErrStyle;

  
  volatile Action switchToPrevPaneAction;

  
  volatile Action switchToNextPaneAction;

  
  protected AbstractConsoleController(InteractionsDJDocument doc, InteractionsPane pane) {
    _interactionsDJDocument = doc;
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
    
    _interactionsDJDocument.setDocStyle(ConsoleDocument.DEFAULT_STYLE, _defaultStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_NORMAL_COLOR,
                                         new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        setDefaultFont(oe.value);
      }
    });

    
    _systemOutStyle.addAttributes(_defaultStyle);
    _systemOutStyle.addAttribute(StyleConstants.Foreground,
                                 DrJava.getConfig().getSetting(OptionConstants.SYSTEM_OUT_COLOR));
    _interactionsDJDocument.setDocStyle(ConsoleDocument.SYSTEM_OUT_STYLE, _systemOutStyle);
    DrJava.getConfig().addOptionListener(OptionConstants.SYSTEM_OUT_COLOR,
                                         new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _systemOutStyle.addAttribute(StyleConstants.Foreground, oe.value);
      }
    });

    
    _systemErrStyle.addAttributes(_defaultStyle);
    _systemErrStyle.addAttribute(StyleConstants.Foreground,
                                 DrJava.getConfig().getSetting(OptionConstants.SYSTEM_ERR_COLOR));
    _interactionsDJDocument.setDocStyle(ConsoleDocument.SYSTEM_ERR_STYLE, _systemErrStyle);
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
      _interactionsDJDocument.setCharacterAttributes(0, _interactionsDJDocument.getLength()+1, fontSet, false);
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
  
  
  protected void _setupView() {
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_BEGIN_LINE), gotoPromptPosAction);
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_BEGIN_LINE_SELECT), selectToPromptPosAction);
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_END_LINE), gotoEndAction);
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_END_LINE_SELECT), selectToEndAction);

    DrJava.getConfig().addOptionListener(OptionConstants.KEY_BEGIN_LINE, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(oe.value, gotoPromptPosAction);
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_BEGIN_LINE_SELECT, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(oe.value, selectToPromptPosAction);
     }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_END_LINE, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(oe.value, gotoEndAction);
     }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_END_LINE_SELECT, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(oe.value, selectToEndAction);
     }
    });
    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_CUT), cutAction);
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_COPY), copyAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_CUT, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_CUT), cutAction);
     }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_COPY, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_COPY), copyAction);
     }
    });
  }
  
  
  public void resetView() {


  }
  
  
  Action cutAction = new DefaultEditorKit.CutAction() {
    public void actionPerformed(ActionEvent e) {
      
      if (_pane.getSelectedText() != null) {
        super.actionPerformed(e);
        String s = edu.rice.cs.util.swing.Utilities.getClipboardSelection(_pane);
        if (s != null && s.length() != 0) { ClipboardHistoryModel.singleton().put(s); }
      }
    }
  };
  
  
  Action copyAction = new DefaultEditorKit.CopyAction() {
    public void actionPerformed(ActionEvent e) {
      if (_pane.getSelectedText() != null) {
        super.actionPerformed(e);
        String s = edu.rice.cs.util.swing.Utilities.getClipboardSelection(_pane);
        if (s != null && s.length() != 0) { ClipboardHistoryModel.singleton().put(s); }
      }
    }
  };

  
  public InteractionsDJDocument getDocumentAdapter() { return _interactionsDJDocument; }

  
  public InteractionsPane getPane() { return _pane; }

  
  protected boolean _busy() { return ! getConsoleDoc().hasPrompt(); }

  
  AbstractAction newLineAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { 
      ConsoleDocument doc = getConsoleDoc();
      doc.insertNewline(_pane.getCaretPosition()); 
    }
  };

  
  AbstractAction clearCurrentAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { getConsoleDoc().clearCurrentInput(); }
  };

  
  AbstractAction gotoEndAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { moveToEnd(); }
  };

  
  AbstractAction selectToEndAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { 
      ConsoleDocument doc = getConsoleDoc();
      _pane.moveCaretPosition(doc.getLength()); 
    }
  };

  
  AbstractAction gotoPromptPosAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) { moveToPrompt(); }
  };

  
  AbstractAction selectToPromptPosAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      assert EventQueue.isDispatchThread();
      ConsoleDocument doc = getConsoleDoc();
      
      _pane.moveCaretPosition(doc.getPromptPos());
    }
  };

  
  void moveToEnd() { 
    assert EventQueue.isDispatchThread();
    int len = getConsoleDoc().getLength();
    _pane.setCaretPosition(len);

  }
  
  
  void moveToPrompt() { 
    assert EventQueue.isDispatchThread();
    int pos = getConsoleDoc().getPromptPos();
    _pane.setCaretPosition(pos);
  }

  public void setPrevPaneAction(Action a) {
    switchToPrevPaneAction = a;

    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_PANE),
                                switchToPrevPaneAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_PREVIOUS_PANE, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_PREVIOUS_PANE), switchToPrevPaneAction);
      }
    });
  }

  public void setNextPaneAction(Action a) {
    switchToNextPaneAction = a;

    
    
    _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_PANE),
                                switchToNextPaneAction);
    DrJava.getConfig().addOptionListener(OptionConstants.KEY_NEXT_PANE, new OptionListener<Vector<KeyStroke>>() {
      public void optionChanged(OptionEvent<Vector<KeyStroke>> oe) {
        _pane.addActionForKeyStroke(DrJava.getConfig().getSetting(OptionConstants.KEY_NEXT_PANE), switchToNextPaneAction);
      }
    });
  }
}
