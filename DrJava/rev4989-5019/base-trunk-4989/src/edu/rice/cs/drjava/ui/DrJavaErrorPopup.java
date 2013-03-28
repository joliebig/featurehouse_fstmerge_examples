

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.swing.Utilities;


public class DrJavaErrorPopup extends JDialog {
  
  private JComponent _errorInfo;
  
  private JCheckBox _keepDisplaying;
  
  private JPanel _bottomPanel;
  
  private JPanel _buttonPanel;
  
  private JButton _okButton;
  
  private JButton _moreButton;
  
  private Throwable _error;


  
  
  public DrJavaErrorPopup(JFrame parent, Throwable error) {
    super(parent, "DrJava Error");
    

    _error = error;

    this.setSize(500,150);

    
    
    _keepDisplaying = new JCheckBox("Keep showing this notification",
                                    DrJava.getConfig().getSetting(OptionConstants.DIALOG_DRJAVA_ERROR_POPUP_ENABLED).booleanValue());
    _keepDisplaying.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        DrJava.getConfig().setSetting(OptionConstants.DIALOG_DRJAVA_ERROR_POPUP_ENABLED, _keepDisplaying.isSelected());
      }
    });

    _moreButton = new JButton(_moreAction);
    _okButton = new JButton(_okAction);

    _bottomPanel = new JPanel(new BorderLayout());
    _buttonPanel = new JPanel();
    _buttonPanel.add(_moreButton);
    _buttonPanel.add(_okButton);
    _bottomPanel.add(_keepDisplaying, BorderLayout.WEST);
    _bottomPanel.add(_buttonPanel, BorderLayout.EAST);

    if (_error instanceof DrJavaErrorHandler.LoggedCondition) { msg[1] = "Logged condition: " + _error.getMessage(); }
    else { msg[1] = _error.toString(); }
    _errorInfo = new JOptionPane(msg,JOptionPane.ERROR_MESSAGE,
                                 JOptionPane.DEFAULT_OPTION,null,
                                 new Object[0]);      

    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(_errorInfo, BorderLayout.CENTER);
    cp.add(_bottomPanel, BorderLayout.SOUTH);    
    getRootPane().setDefaultButton(_okButton);
  }
  
  
  private Action _okAction = new AbstractAction("OK") {
    public void actionPerformed(ActionEvent e) {
      DrJavaErrorPopup.this.dispose();
      if (DrJavaErrorHandler.getButton() == null) { System.exit(1); }
    }
  };

  
  private Action _moreAction = new AbstractAction("More Information") {
    public void actionPerformed(ActionEvent e) {
      if (! Utilities.TEST_MODE) {
        DrJavaErrorPopup.this.dispose();
        Utilities.setPopupLoc(DrJavaErrorWindow.singleton(), DrJavaErrorWindow.getFrame());
        DrJavaErrorWindow.singleton().setVisible(true);
      }
    }
  };

  
  private final String[] msg = {
    "An error occurred in DrJava:",
    "",
    "You may wish to save all your work and restart DrJava."};
}