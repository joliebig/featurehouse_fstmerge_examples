

package edu.rice.cs.drjava.ui.config;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.*;

import java.util.*;

import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.CheckBoxJList;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;


public class VectorKeyStrokeOptionComponent extends VectorOptionComponent<KeyStroke> implements OptionConstants, Comparable<VectorKeyStrokeOptionComponent> {
  private static final int DIALOG_HEIGHT = 185;
  public static final HashMap<KeyStroke, VectorKeyStrokeOptionComponent> _keyToKSOC =
    new HashMap<KeyStroke, VectorKeyStrokeOptionComponent>();

  public VectorKeyStrokeOptionComponent (VectorOption<KeyStroke> opt, String text, SwingFrame parent) {
    this(opt, text, parent, null);
  }
  
  
  public VectorKeyStrokeOptionComponent (VectorOption<KeyStroke> opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent, description, false);
  }

  
  public VectorKeyStrokeOptionComponent (VectorOption<KeyStroke> opt, String text, SwingFrame parent,
                                         String description, boolean moveButtonEnabled) {
    super(opt, text, parent, new String[] { }, description, moveButtonEnabled);  
    for(KeyStroke k: getKeyStrokes()) _keyToKSOC.put(k, this);
  }
  
  
  protected AbstractTableModel _makeTableModel() {
    return new AbstractTableModel() {
      public int getRowCount() { return _data.size(); }
      public int getColumnCount() { return 1; }
      public Object getValueAt(int row, int col) {
        switch(col) {
          case 0: return KeyStrokeOption.formatKeyStroke(_data.get(row));
        }
        throw new IllegalArgumentException("Illegal column");
      }
      public Class<?> getColumnClass(int col) {
        switch(col) {
          case 0: return String.class;
        }
        throw new IllegalArgumentException("Illegal column");
      }
    };
  }
  
  
  public int compareTo(VectorKeyStrokeOptionComponent o) {
    return this.getLabelText().compareTo(o.getLabelText());
  }

  Vector<KeyStroke> getKeyStrokes() { return new Vector<KeyStroke>(_data); }
  
  
  public void chooseKeyStroke() {    
    _table.getSelectionModel().clearSelection();
    GetKeyDialog getKeyDialog = new GetKeyDialog(_parent, "Specify Shortcut", true);
    getKeyDialog.promptKey(KeyStrokeOption.NULL_KEYSTROKE);
  }
  
  
  protected void _removeIndex(int i) {
    _keyToKSOC.remove(_data.get(i));
    super._removeIndex(i);
  }
  
  protected Action _getAddAction() {
    return new AbstractAction("Add") {
      public void actionPerformed(ActionEvent ae) {
        chooseKeyStroke();
      }
    };
  }
  
  
  public class GetKeyDialog extends JDialog {
    private InputField _inputField;
    private JButton _cancelButton;
    private JButton _okButton;
    private JLabel _instructionLabel;
    private JLabel _currentLabel;
    private JLabel _actionLabel;
    private JPanel _inputAndClearPanel;
    private JPanel _cancelAndOKPanel;
    private KeyStroke _currentKeyStroke;

    public GetKeyDialog(SwingFrame f, String title, boolean modal) {
      super(f, title, modal);
      
      _inputField = new InputField();
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
          if ((_currentKeyStroke!=KeyStrokeOption.NULL_KEYSTROKE) &&
              (!getKeyStrokes().contains(_currentKeyStroke))) {
            VectorKeyStrokeOptionComponent conflict = _keyToKSOC.get(_currentKeyStroke);
            if (conflict != null) {
              
              
              Vector<KeyStroke> v = conflict.getKeyStrokes();
              v.removeElement(_currentKeyStroke);
              conflict.setValue(v);
            }
            
            _keyToKSOC.put(_currentKeyStroke, VectorKeyStrokeOptionComponent.this);
            _addValue(_currentKeyStroke);
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

    public void promptKey(KeyStroke initial) {
      _instructionLabel.setText("Type in the keystroke you want to use for \"" +
                                getLabelText() +
                                "\" and click \"OK\"");
      _currentKeyStroke = initial;
      _actionLabel.setText(getLabelText());
      _inputField.setText(KeyStrokeOption.formatKeyStroke(_currentKeyStroke));
      this.setSize((int)_instructionLabel.getPreferredSize().getWidth() + 30, DIALOG_HEIGHT);
      Utilities.setPopupLoc(this, getOwner());
      this.setVisible(true);
    }

    
    private class InputField extends JTextField {
      public void processKeyEvent(KeyEvent e) {
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
        if (e.getID() == KeyEvent.KEY_PRESSED) {
          this.setText(KeyStrokeOption.formatKeyStroke(ks));
          VectorKeyStrokeOptionComponent configKs = _keyToKSOC.get(ks);
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
