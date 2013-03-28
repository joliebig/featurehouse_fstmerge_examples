

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import java.awt.*;
import java.awt.event.*;


public class ToolbarOptionComponent extends OptionComponent<Boolean> {

  private JRadioButton _noneButton;
  private JRadioButton _textButton;
  private JRadioButton _iconsButton;
  private JRadioButton _textAndIconsButton;
  private ButtonGroup _group;
  private JPanel _buttonPanel;

  
  public static final String NONE = "none";
  public static final String TEXT_ONLY = "text only";
  public static final String ICONS_ONLY= "icons only";
  public static final String TEXT_AND_ICONS = "text and icons";

  
  public ToolbarOptionComponent(String title, Frame parent) {
    super(title, parent);

    _noneButton = new JRadioButton(NONE);
    _noneButton.setActionCommand(NONE);
    _noneButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });
    
    _textButton = new JRadioButton(TEXT_ONLY);
    _textButton.setActionCommand(TEXT_ONLY);
    _textButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });

    _iconsButton = new JRadioButton(ICONS_ONLY);
    _iconsButton.setActionCommand(ICONS_ONLY);
    _iconsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });

    _textAndIconsButton = new JRadioButton(TEXT_AND_ICONS);
    _textAndIconsButton.setActionCommand(TEXT_AND_ICONS);
    _textAndIconsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        notifyChangeListeners();
      }
    });

    resetToCurrent();

    _group = new ButtonGroup();
    _group.add(_noneButton);
    _group.add(_textButton);
    _group.add(_iconsButton);
    _group.add(_textAndIconsButton);

    _buttonPanel = new JPanel();
    _buttonPanel.setLayout(new GridLayout(0,1));
    _buttonPanel.setBorder(BorderFactory.createEtchedBorder());
    _buttonPanel.add(_noneButton);
    _buttonPanel.add(_textButton);
    _buttonPanel.add(_iconsButton);
    _buttonPanel.add(_textAndIconsButton);

    DrJava.getConfig().addOptionListener(OptionConstants.TOOLBAR_TEXT_ENABLED,
                                         new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oe) {
        resetToCurrent();
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.TOOLBAR_ICONS_ENABLED,
                                         new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oe) {
        resetToCurrent();
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.TOOLBAR_ENABLED,
                                         new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oe) { 
        resetToCurrent();
      }
    });
      
  }

  
  public ToolbarOptionComponent(String title, Frame parent, String description) {
    this(title, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _buttonPanel.setToolTipText(description);
    _noneButton.setToolTipText(description);
    _textButton.setToolTipText(description);
    _iconsButton.setToolTipText(description);
    _textAndIconsButton.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public void resetToCurrent() {
    _setSelected(DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_TEXT_ENABLED).booleanValue(),
                 DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_ICONS_ENABLED).booleanValue(),
                 DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_ENABLED).booleanValue());
  }

  
  public void resetToDefault() {
    _setSelected(OptionConstants.TOOLBAR_TEXT_ENABLED.getDefault().booleanValue(),
                 OptionConstants.TOOLBAR_ICONS_ENABLED.getDefault().booleanValue(),
                 OptionConstants.TOOLBAR_ENABLED.getDefault().booleanValue());
  }

  
  private void _setSelected(boolean textEnabled, boolean iconsEnabled, boolean isEnabled) {
    if (! isEnabled) {
      _noneButton.setSelected(true);
    }
    else if (textEnabled && iconsEnabled) {
      _textAndIconsButton.setSelected(true);
    }
    else {
      if (textEnabled) _textButton.setSelected(true);
      else if (iconsEnabled) _iconsButton.setSelected(true);
    }
  }

  
  public JComponent getComponent() {
    return _buttonPanel;
  }

  
  public boolean updateConfig() {
    String btnIdent = _group.getSelection().getActionCommand();
    boolean textWasEnabled = DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_TEXT_ENABLED).booleanValue();
    boolean iconsWereEnabled = DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_ICONS_ENABLED).booleanValue();
    boolean wasEnabled = DrJava.getConfig().getSetting(OptionConstants.TOOLBAR_ENABLED).booleanValue();
    
    if (btnIdent.equals(NONE)) {
      if (wasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ENABLED, Boolean.FALSE);      
      }
    }
    if (btnIdent.equals(TEXT_ONLY)) {
      if (!textWasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_TEXT_ENABLED, Boolean.TRUE);
      }
      if (iconsWereEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ICONS_ENABLED, Boolean.FALSE);
      }
      if (!wasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ENABLED, Boolean.TRUE);   
      }
    }

    if (btnIdent.equals(ICONS_ONLY)) {
      if (!iconsWereEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ICONS_ENABLED, Boolean.TRUE);
      }
      if (textWasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_TEXT_ENABLED, Boolean.FALSE);
      }
      if (!wasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ENABLED, Boolean.TRUE);  
      }
    }

    if (btnIdent.equals(TEXT_AND_ICONS)) {
      if (!textWasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_TEXT_ENABLED, Boolean.TRUE);
      }
      if (!iconsWereEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ICONS_ENABLED, Boolean.TRUE);
      }
      if (!wasEnabled) {
        DrJava.getConfig().setSetting(OptionConstants.TOOLBAR_ENABLED, Boolean.TRUE);        
      }
    }

    return true;
  }


  
  public void setValue(Boolean value) {
    resetToCurrent();
  }

}