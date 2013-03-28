package net.sourceforge.squirrel_sql.plugins.postgres.gui;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MessageDialog extends JDialog {
    
    protected JTextArea _messageTextArea;
    protected JScrollPane scrollPane;
    
    protected JButton _closeButton;

    protected boolean _autoScrolling = true;


    
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractPostgresDialog.class);

    protected interface i18n {
        String CLOSE_BUTTON_LABEL = s_stringMgr.getString("MessageDialog.closeButtonLabel");
    }


    public MessageDialog() {
        init();
    }


    
    protected void init() {
        setModal(true);
        setSize(500, 250);

        Container contentPane = super.getContentPane();
        contentPane.setLayout(new BorderLayout());

        _messageTextArea = new JTextArea();
        _messageTextArea.setLineWrap(true);
        _messageTextArea.setEditable(false);
        scrollPane = new JScrollPane(_messageTextArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        _closeButton = new JButton(i18n.CLOSE_BUTTON_LABEL);
        _closeButton.setEnabled(false);
        replaceCloseListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        contentPane.add(_closeButton, BorderLayout.SOUTH);
    }


    public void replaceCloseListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("ActionListener cannot be null");
        for (ActionListener l : _closeButton.getListeners(ActionListener.class)) {
            _closeButton.removeActionListener(l);
        }
        _closeButton.addActionListener(listener);
    }


    public void writeLine(String text) {
        _messageTextArea.append(text + "\n");
        if (_autoScrolling) scrollToBottom();
    }


    public void writeEmptyLine() {
        _messageTextArea.append("\n");
        if (_autoScrolling) scrollToBottom();
    }


    public void enableCloseButton() {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                _closeButton.setEnabled(true);
            }
        });
    }


    public void setAutoScrolling(boolean autoScrolling) {
        _autoScrolling = autoScrolling;
    }


    public void scrollToBottom() {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                _messageTextArea.setCaretPosition(_messageTextArea.getText().length());
            }
        });
    }


    public static void main(String[] args) {
        final MessageDialog dialog = new MessageDialog();
        dialog.replaceCloseListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setVisible(true);
            }
        });

        dialog.setAutoScrolling(false);
        for (int i = 1; i <= 15; i++) {
            dialog.writeLine("A line of text. (" + i + ")");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dialog.scrollToBottom();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dialog.setAutoScrolling(true);
        for (int i = 16; i <= 20; i++) {
            dialog.writeLine("A line of text. (" + i + ")");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dialog.enableCloseButton();
    }
}
