package net.sourceforge.squirrel_sql.plugins.dataimport.gui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ProgressBarDialog {

    private static JProgressBar progressBar = null;
    private static JLabel message = null;
    private static JButton cancelButton = null;
    
    private static JDialog dialog = null;
    
    
    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(ProgressBarDialog.class);    
    
    
    private final static ILogger log = 
        LoggerController.createLogger(ProgressBarDialog.class);
    
   
    
    public static JDialog getDialog(final Frame owner, 
                                    final String title,
                                    final boolean modal,
                                    final ActionListener listener) {
        if (SwingUtilities.isEventDispatchThread()) {
            _getDialog(owner, title, modal, listener);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        _getDialog(owner, title, modal, listener);
                    }
                });
            } catch (Exception e) {
                
                log.error(stringMgr.getString("ProgressBarDialog.error.getdialog"), e);
            }
        }
        
        return dialog;
    }
    
    private static void _getDialog(Frame owner, 
                                   String title,
                                   boolean modal,
                                   ActionListener listener) 
    {
        dialog = new JDialog(owner, title, modal);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(buildPanel(), BorderLayout.CENTER);
        dialog.getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        dialog.setSize(250,135);
        dialog.setLocationRelativeTo(owner);
        cancelButton.addActionListener(listener);
        dialog.setVisible(true);
    }
    
    private static JPanel buildPanel() {
        JPanel dataPanel = new JPanel();
        dataPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        GridBagLayout gl = new GridBagLayout();
        dataPanel.setLayout(gl);
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        
        String topLabelText = stringMgr.getString("ProgressBarDialog.insertingRecordsLabel");
        message = new JLabel(topLabelText);
        dataPanel.add(message, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        c.weightx = 1.0;
        progressBar = new JProgressBar(0,10);
        dataPanel.add(progressBar, c);
        
        return dataPanel;
    }    
    
    private static JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        String buttonText = stringMgr.getString("ProgressBarDialog.cancelButtonLabel");
        cancelButton = new JButton(buttonText);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }
        
    
    public static void setMessage(final String msg) {
        if (SwingUtilities.isEventDispatchThread()) {
            message.setText(msg);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    message.setText(msg);
                }
            });                    
        }
    }
    
    
    public static void setIndeterminate() {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setIndeterminate(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setIndeterminate(true);
                }
            });                    
        }
    }
    
    
    public static void setBarMinMax(final int min, final int max) {
        if (progressBar.getMinimum() == min 
                && progressBar.getMaximum() == max) 
        {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setMinimum(min);
            progressBar.setMaximum(max);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setMinimum(min);
                    progressBar.setMaximum(max);
                }
            });                    
        }
    }
    
    
    public static void setBarValue(final int value) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(value);            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(value);
                }
            });                    
        }
    }

    
    public static void incrementBar(final int value) {
        final int newValue = progressBar.getValue() + value;
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(newValue);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   progressBar.setValue(newValue);
               }
            });
        }
    }

    
    public static void setVisible(final boolean visible) {
        if (dialog == null) {
            return;
        }
        if (dialog.isVisible() != visible) {
            if (SwingUtilities.isEventDispatchThread()) {
                dialog.setVisible(visible);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setVisible(visible);
                    }
                });                    
            }
        }
    }
        
    
    public static void dispose() {
        if (dialog == null) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            dialog.dispose();            
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dialog.dispose();
                }
            });                    
        }
    }
}
