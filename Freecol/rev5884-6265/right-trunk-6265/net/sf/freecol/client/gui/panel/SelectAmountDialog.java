

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.GoodsType;

import net.miginfocom.swing.MigLayout;



public final class SelectAmountDialog extends FreeColDialog<Integer> implements ActionListener {

    private static Logger logger = Logger.getLogger(SelectAmountDialog.class.getName());

    private static final int SELECT_CANCEL = -1;

    private static final int[] amounts = {20, 40, 50, 60, 80, 100};

    private final JButton cancel, ok;

    private final JTextArea question;

    private final JComboBox comboBox;

    
    public SelectAmountDialog(Canvas parent, GoodsType goodsType, int available, boolean needToPay) {
        super(parent);

        setFocusCycleRoot(true);

        question = getDefaultTextArea(Messages.message("goodsTransfer.text"));

        if (needToPay) {
            int gold = getMyPlayer().getGold();
            int price = getMyPlayer().getMarket().costToBuy(goodsType);
            available = Math.min(available, gold/price);
        }

        Vector<Integer> values = new Vector<Integer>();
        for (int index = 0; index < amounts.length; index++) {
            if (amounts[index] < available) {
                values.add(amounts[index]);
            } else {
                values.add(available);
                break;
            }
        }

        comboBox = new JComboBox(values);
        comboBox.setEditable(true);
        comboBox.addActionListener(this);

        ok = new JButton(Messages.message("ok"));
        enterPressesWhenFocused(ok);
        ok.addActionListener(this);

        cancel = new JButton(Messages.message("cancel"));
        enterPressesWhenFocused(cancel);
        cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setResponse(new Integer(SELECT_CANCEL));
                }
            });
        setCancelComponent(cancel);

        setLayout(new MigLayout("wrap 1", "", ""));

        add(question);
        add(comboBox, "wrap 20, growx");
        add(ok, "span, split 2, tag ok");
        add(cancel, "tag cancel");
        
        setSize(getPreferredSize());

    }

    public void requestFocus() {
        cancel.requestFocus();
    }

    
    public void actionPerformed(ActionEvent event) {
        Object item = comboBox.getSelectedItem();
        if (item instanceof Integer) {
            setResponse((Integer) item);
        } else if (item instanceof String) {
            try {
                setResponse(Integer.valueOf((String) item));
            } catch (NumberFormatException e) {
                
            }
        }
    }
}
