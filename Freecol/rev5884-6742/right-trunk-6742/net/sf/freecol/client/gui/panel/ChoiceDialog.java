

package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.sf.freecol.client.gui.Canvas;

import net.miginfocom.swing.MigLayout;



public final class ChoiceDialog<T> extends FreeColDialog<T> {

    private static final Logger logger = Logger.getLogger(ChoiceDialog.class.getName());

    private JList choiceList;


    
    public ChoiceDialog(Canvas parent, String text, String cancelText, List<ChoiceItem<T>> choices) {
        super(parent);

        MigLayout layout = new MigLayout("wrap 1, fill", "[align center]", "[]30[]30[]");
        setLayout(layout);

        add(getDefaultTextArea(text));

        DefaultListModel model = new DefaultListModel();
        for (ChoiceItem<T> item : choices) {
            model.addElement(item);
        }
        choiceList = new JList(model);
        JScrollPane listScroller = new JScrollPane(choiceList);
        listScroller.setPreferredSize(new Dimension(250, 250));

        Action selectAction = new AbstractAction() {
                @SuppressWarnings("unchecked")
                public void actionPerformed(ActionEvent e) {
                    setResponse(((ChoiceItem<T>) choiceList.getSelectedValue()).getObject());
                }
            };

        Action quitAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    getCanvas().remove(ChoiceDialog.this);
                }
            };

        choiceList.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "select");
        choiceList.getActionMap().put("select", selectAction);
        choiceList.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "quit");
        choiceList.getActionMap().put("quit", quitAction);

        MouseListener mouseListener = new MouseAdapter() {
                @SuppressWarnings("unchecked")
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setResponse(((ChoiceItem<T>) choiceList.getSelectedValue()).getObject());
                    }
                }
            };
        choiceList.addMouseListener(mouseListener);

        add(listScroller, "growx, growy");

        if (cancelText != null) {
            cancelButton.setText(cancelText);
            add(okButton, "split 2, tag ok");
            add(cancelButton, "tag cancel");
        } else {
            add(okButton, "tag ok");
        }

        okButton.addActionListener(this);

        setSize(getPreferredSize());
    }


    @Override
    public void requestFocus() {
        choiceList.requestFocus();
    }

    
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            setResponse(((ChoiceItem<T>) choiceList.getSelectedValue()).getObject());
        } else {
            super.actionPerformed(event);
        }
    }


} 

