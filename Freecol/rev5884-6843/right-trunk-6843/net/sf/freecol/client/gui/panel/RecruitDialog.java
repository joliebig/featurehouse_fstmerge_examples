

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;


public final class RecruitDialog extends FreeColDialog<Integer> implements ActionListener {

    private static Logger logger = Logger.getLogger(RecruitDialog.class.getName());

    private static final int RECRUIT_CANCEL = -1;

    private static final int NUMBER_OF_PERSONS = 3;

    private final JButton[] person = new JButton[NUMBER_OF_PERSONS];

    private final JButton cancel;

    private final JTextArea question;

    
    public RecruitDialog(Canvas parent) {
        super(parent);

        setFocusCycleRoot(true);

        question = getDefaultTextArea(Messages.message("recruitDialog.clickOn"));

        for (int index = 0; index < NUMBER_OF_PERSONS; index++) {
            person[index] = new JButton();
            person[index].setActionCommand(String.valueOf(index));
            enterPressesWhenFocused(person[index]);
            person[index].addActionListener(this);
            person[index].setIconTextGap(margin);
        }

        cancel = new JButton(Messages.message("cancel"));
        cancel.setActionCommand(String.valueOf(RECRUIT_CANCEL));
        enterPressesWhenFocused(cancel);
        cancel.addActionListener(this);
        setCancelComponent(cancel);

        initialize();

    }

    public void requestFocus() {
        cancel.requestFocus();
    }

    
    public void initialize() {

        setLayout(new MigLayout("wrap 1", "", ""));

        add(question, "wrap 20");

        int recruitPrice = 0;
        Player player = getMyPlayer();
        if ((getGame() != null) && (player != null)) {

            int production = 0;
            for (Colony colony : player.getColonies()) {
                production += colony.getProductionOf(Specification.getSpecification()
                                                     .getGoodsType("model.goods.crosses"));
            }
            int turns = 100;
            if (production > 0) {
                int immigrationRequired = (player.getImmigrationRequired() - player.getImmigration());
                turns = immigrationRequired / production;
                if (immigrationRequired % production > 0) {
                    turns++;
                }
            }
            recruitPrice = player.getRecruitPrice();

            question.setText(Messages.message("recruitDialog.clickOn",
                                              "%money%", String.valueOf(recruitPrice),
                                              "%number%", String.valueOf(turns)));

            for (int index = 0; index < NUMBER_OF_PERSONS; index++) {
                UnitType unitType = player.getEurope().getRecruitable(index);
                ImageIcon unitIcon = getLibrary().getUnitImageIcon(unitType, 0.66);
                person[index].setText(Messages.message(unitType.getNameKey()));
                person[index].setIcon(unitIcon);

                if (recruitPrice > player.getGold()) {
                    person[index].setEnabled(false);
                } else {
                    person[index].setEnabled(true);
                }

                add(person[index], "growx");
            }
        }

        add(cancel, "newline 20, tag cancel");

        setSize(getPreferredSize());

    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            int action = Integer.valueOf(command).intValue();
            if (action == RECRUIT_CANCEL) {
                setResponse(new Integer(-1));
            } else if (action >= 0 && action < NUMBER_OF_PERSONS) {
                getController().recruitUnitInEurope(action);
                setResponse(new Integer(0));
            } else {
                logger.warning("Invalid action command");
                setResponse(new Integer(-1));
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid action number");
            setResponse(new Integer(-1));
        }
    }
}
