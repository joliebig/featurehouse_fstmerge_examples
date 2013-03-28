

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.Nation;

import net.miginfocom.swing.MigLayout;


public final class MonarchPanel extends FreeColDialog<Boolean> implements ActionListener {

    private static final Logger logger = Logger.getLogger(MonarchPanel.class.getName());

    private static final int OK = 0;

    private static final int CANCEL = 1;

    private final JButton okButton;

    
    public MonarchPanel(Canvas parent, MonarchAction action, String... replace) {

        super(parent);

        okButton = new JButton(Messages.message("ok"));
        okButton.setActionCommand(String.valueOf(OK));
        okButton.addActionListener(this);

        JButton cancelButton = new JButton();
        cancelButton.setActionCommand(String.valueOf(CANCEL));
        cancelButton.addActionListener(this);

        setLayout(new MigLayout("wrap 2", "", ""));

        JLabel header = new JLabel(Messages.message("aMessageFromTheCrown"));
        header.setFont(mediumHeaderFont);
        add(header, "span, align center, wrap 20");

        Nation nation = getMyPlayer().getNation();
        add(new JLabel(getLibrary().getMonarchImageIcon(nation)));

        String messageID;
        String okText = "ok";
        String cancelText = null;
        switch (action) {
        case RAISE_TAX:
            messageID = "model.monarch.raiseTax";
            okText = "model.monarch.acceptTax";
            cancelText = "model.monarch.rejectTax";
            break;
        case ADD_TO_REF:
            messageID = "model.monarch.addToREF";
            break;
        case DECLARE_WAR:
            messageID = "model.monarch.declareWar";
            break;
        case SUPPORT_SEA:
            messageID = "model.monarch.supportSea";
            cancelText = "display";
            break;
        case SUPPORT_LAND:
            messageID = "model.monarch.supportLand";
            cancelText = "display";
            break;
        case LOWER_TAX:
            messageID = "model.monarch.lowerTax";
            break;
        case WAIVE_TAX:
            messageID = "model.monarch.waiveTax";
            break;
        case OFFER_MERCENARIES:
            messageID = "model.monarch.offerMercenaries";
            okText = "model.monarch.acceptMercenaries";
            cancelText = "model.monarch.rejectMercenaries";
            break;
        default:
            messageID = "Unknown monarch action: " + action;
        }

        add(getDefaultTextArea(Messages.message(messageID, replace)));

        okButton.setText(Messages.message(okText));
        if (cancelText == null) {
            add(okButton, "newline 20, span, tag ok");
        } else {
            add(okButton, "newline 20, span, tag ok, split 2");
            cancelButton.setText(Messages.message(cancelText));
            add(cancelButton, "tag cancel");
        }

        setSize(getPreferredSize());
    }

    public void requestFocus() {
        okButton.requestFocus();
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
            case OK:
                setResponse(new Boolean(true));
                break;
            case CANCEL:
                setResponse(new Boolean(false));
                break;
            default:
                logger.warning("Invalid Actioncommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }

}
