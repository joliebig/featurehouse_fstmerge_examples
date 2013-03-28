package net.sourceforge.squirrel_sql.plugins.refactoring.gui;


import net.sourceforge.squirrel_sql.client.gui.db.IDisposableDialog;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


public abstract class AbstractRefactoringTabbedDialog extends JDialog implements IDisposableDialog {

    protected JButton executeButton = null;
    protected JButton editSQLButton = null;
    protected JButton showSQLButton = null;
    protected JButton cancelButton = null;

    
    protected GridBagConstraints c = null;

    protected final Dimension mediumField = new Dimension(126, 20);

    
    protected final JTabbedPane pane = new JTabbedPane();

    protected final EmptyBorder emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));

    
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(AbstractRefactoringTabbedDialog.class);


    protected interface i18n {
        
        String CANCEL_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.cancelButtonLabel");

        
        String EDIT_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.editButtonLabel");

        
        String EXECUTE_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.executeButtonLabel");

        
        String SHOWSQL_BUTTON_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.showButtonLabel");

        
        String TABLE_NAME_LABEL =
                s_stringMgr.getString("AbstractRefactoringDialog.tableNameLabel");

    }


    public AbstractRefactoringTabbedDialog(Dimension size) {
        defaultInit(size);
    }


    public void addShowSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        showSQLButton.addActionListener(listener);
    }


    public void addEditSQLListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        editSQLButton.addActionListener(listener);
    }


    public void addExecuteListener(ActionListener listener) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        executeButton.addActionListener(listener);
    }


    protected GridBagConstraints getLabelConstraints(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        return c;
    }


    protected GridBagConstraints getFieldConstraints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }


    protected JLabel getBorderedLabel(String text, Border border) {
        JLabel result = new JLabel(text);
        result.setBorder(border);
        result.setPreferredSize(new Dimension(115, 20));
        result.setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }


    
    protected void defaultInit(Dimension size) {
        super.setModal(true);
        setSize(size);

        pane.setBorder(new EmptyBorder(10, 10, 0, 10));

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = -1;


        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);

        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
        getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
        getRootPane().getActionMap().put("CloseAction", new AbstractAction() {
			   private static final long serialVersionUID = -2305467371279192850L;
				public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
                dispose();
            }
        });
    }


    protected JPanel getButtonPanel() {
        JPanel result = new JPanel();
        executeButton = new JButton(AbstractRefactoringTabbedDialog.i18n.EXECUTE_BUTTON_LABEL);
        result.add(executeButton);

        editSQLButton = new JButton(AbstractRefactoringTabbedDialog.i18n.EDIT_BUTTON_LABEL);
        result.add(editSQLButton);
        showSQLButton = new JButton(AbstractRefactoringTabbedDialog.i18n.SHOWSQL_BUTTON_LABEL);
        result.add(showSQLButton);
        cancelButton = new JButton(AbstractRefactoringTabbedDialog.i18n.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        result.add(cancelButton);
        return result;
    }


    protected void enable(JButton button) {
        if (button != null) {
            button.setEnabled(true);
        }
    }


    protected void disable(JButton button) {
        if (button != null) {
            button.setEnabled(false);
        }
    }


    protected void setAllButtonEnabled(boolean enable) {
        executeButton.setEnabled(enable);
        editSQLButton.setEnabled(enable);
        showSQLButton.setEnabled(enable);
    }

}
