

package edu.rice.cs.drjava.ui.config;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;


public class KeyStrokeOptionComponent extends OptionComponent<KeyStroke>
                                      implements Comparable<KeyStrokeOptionComponent> {
  private static final int DIALOG_HEIGHT = 185;

  public static final HashMap<KeyStroke, KeyStrokeOptionComponent> _keyToKSOC =
    new HashMap<KeyStroke, KeyStrokeOptionComponent>();
  private JButton _button;
  private JPanel _panel;
  private static GetKeyDialog _getKeyDialog =  null;

  private KeyStroke _key;

  public KeyStrokeOptionComponent(KeyStrokeOption opt, String text, final SwingFrame parent) {
    super(opt, text, parent);

    _key = DrJava.getConfig().getSetting(opt);

    _button = new JButton();
    _button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

        if (_getKeyDialog == null) { _getKeyDialog = new GetKeyDialog(parent, "Specify Shortcut", true); }

        String oldText = _button.getText();
        _getKeyDialog.promptKey(KeyStrokeOptionComponent.this);
        if (!_button.getText().equals(oldText)) { notifyChangeListeners(); }
      }
    });
    _button.setText(_option.format(_key));

    _panel = new JPanel(new BorderLayout());
    _panel.add(_button, BorderLayout.CENTER);

    GridLayout gl = new GridLayout(1,0);
    gl.setHgap(15);
    _keyToKSOC.put(_key, this);
  }

  
  public KeyStrokeOptionComponent(KeyStrokeOption opt, String text,
                                  SwingFrame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _panel.setToolTipText(description);
    _button.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  public String toString() {
    return "<KSOC>label:" + getLabelText() + "ks: " +
      getKeyStroke() + "jb: " + _button.getText() + "</KSOC>\n";
  }

  
  public boolean updateConfig() {
    if (!_key.equals(getConfigKeyStroke())) {
      DrJava.getConfig().setSetting(_option, _key);
      setValue(_key);
    }
    return true;
  }

  
  public void setValue(KeyStroke value) {
    _key = value;
    _button.setText(_option.format(value));
  }

  
  public int compareTo(KeyStrokeOptionComponent other) {
    return this.getLabelText().compareTo(other.getLabelText());
  }

  
  public KeyStroke getKeyStroke() {
    return _key;
  }

  
  public KeyStroke getConfigKeyStroke() {
    return DrJava.getConfig().getSetting(_option);
  }

  
  public JComponent getComponent() { return _panel; }

  
  private class GetKeyDialog extends JDialog {
    private InputField _inputField;
    private JButton _clearButton;
    private JButton _cancelButton;
    private JButton _okButton;
    private JLabel _instructionLabel;
    private JLabel _currentLabel;
    private JLabel _actionLabel;
    private JPanel _inputAndClearPanel;

    private JPanel _cancelAndOKPanel;
    private KeyStroke _currentKeyStroke;
    private KeyStrokeOptionComponent _ksoc;


    public GetKeyDialog(SwingFrame f, String title, boolean modal) {
      super(f, title, modal);

      
      _inputField = new InputField();
      _clearButton = new JButton("Clear");
      _clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          _inputField.setText("");
          _actionLabel.setText("<none>");
          _currentKeyStroke = KeyStrokeOption.NULL_KEYSTROKE;
          _inputField.requestFocusInWindow();
        }
      });
      _cancelButton = new JButton("Cancel");
      _cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          _inputField.requestFocusInWindow();
          GetKeyDialog.this.dispose();
        }
      });
      _okButton = new JButton("OK");
      _okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          if (!_ksoc.getKeyStroke().equals(_currentKeyStroke)) {
            _keyToKSOC.remove(_ksoc.getKeyStroke());

            KeyStrokeOptionComponent conflict = _keyToKSOC.get(_currentKeyStroke);

            if (conflict != null) {
              _keyToKSOC.remove(_currentKeyStroke);
              conflict.setValue(KeyStrokeOption.NULL_KEYSTROKE);
            }
            _keyToKSOC.put(_currentKeyStroke, _ksoc);
            _ksoc.setValue(_currentKeyStroke);
          }
          _inputField.requestFocusInWindow();
          GetKeyDialog.this.dispose();
        }
      });
      _instructionLabel = new JLabel("Type in the keystroke you want to use " +
                                     "and click \"OK\"");
      _currentLabel = new JLabel("Current action bound to the keystroke:");
      _actionLabel = new JLabel("<none>");

      _inputAndClearPanel = new JPanel(new BorderLayout());
      _inputAndClearPanel.add(_inputField, BorderLayout.CENTER);
      _inputAndClearPanel.add(_clearButton, BorderLayout.EAST);

      
      
      

      _cancelAndOKPanel = new JPanel(new GridLayout(1,0));
      _cancelAndOKPanel.add(_okButton);
      _cancelAndOKPanel.add(_cancelButton);

      JPanel panel = (JPanel)this.getContentPane();

      panel.setLayout(new GridLayout(0, 1));
      panel.add(_instructionLabel);
      panel.add(_inputAndClearPanel);
      
      panel.add(_currentLabel);
      panel.add(_actionLabel);
      panel.add(_cancelAndOKPanel);
      setSize((int)_instructionLabel.getPreferredSize().getWidth() + 30, DIALOG_HEIGHT);
      
      EventQueue.invokeLater(new Runnable() { public void run() { GetKeyDialog.this.pack(); } });
    }

    public void promptKey(KeyStrokeOptionComponent k) {
      _ksoc = k;
      _instructionLabel.setText("Type in the keystroke you want to use for \"" +
                                k.getLabelText() +
                                "\" and click \"OK\"");
      _currentKeyStroke = k.getKeyStroke();
      _actionLabel.setText(k.getLabelText());
      _inputField.setText(_option.format(_currentKeyStroke));
      
      this.setSize((int)_instructionLabel.getPreferredSize().getWidth() + 30, DIALOG_HEIGHT);
      Utilities.setPopupLoc(this, getOwner());
      this.setVisible(true);
    }

    
    private class InputField extends JTextField {
      

      public void processKeyEvent(KeyEvent e) {
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
        if (e.getID() == KeyEvent.KEY_PRESSED) {
          this.setText(_option.format(ks));
          KeyStrokeOptionComponent configKs = _keyToKSOC.get(ks);
          if (configKs == null)
            _actionLabel.setText("<none>");
          else {
            String name = configKs.getLabelText();
            _actionLabel.setText(name);
          }
          _currentKeyStroke = ks;
        }
      }
    }
  }

}
