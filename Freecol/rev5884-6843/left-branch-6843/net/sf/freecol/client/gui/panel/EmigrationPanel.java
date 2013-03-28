

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;



public final class EmigrationPanel extends FreeColDialog<Integer> {

    private static final Logger logger = Logger.getLogger(EmigrationPanel.class.getName());

    private static final int NUMBER_OF_PERSONS = 3;

    private static final JButton[] person = new JButton[NUMBER_OF_PERSONS];

    private JTextArea question = getDefaultTextArea(Messages.message("chooseImmigrant"));


    
    public EmigrationPanel(Canvas parent) {
        super(parent);
        for (int index = 0; index < NUMBER_OF_PERSONS; index++) {
            person[index] = new JButton();
            person[index].setActionCommand(String.valueOf(index));
            person[index].addActionListener(this);
            enterPressesWhenFocused(person[index]);
        }
    }

    public void requestFocus() {
        person[0].requestFocus();
    }

    
    public void initialize(Europe europe, boolean fountainOfYouth) {

        if (fountainOfYouth) {
            question.insert(Messages.message("lostCityRumour.FountainOfYouth") + "\n\n", 0);
        }

        setLayout(new MigLayout("wrap 1", "[fill]", ""));

        add(question, "wrap 20");

        for (int index = 0; index < NUMBER_OF_PERSONS; index++) {
            UnitType unitType = europe.getRecruitable(index);
            ImageIcon unitIcon = getLibrary().getUnitImageIcon(unitType);
            person[index].setText(Messages.getName(unitType));
            person[index].setIcon(getLibrary().getScaledImageIcon(unitIcon, 0.66f));

            add(person[index]);
        }

        setSize(getPreferredSize());
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            int action = Integer.valueOf(command).intValue();
            if (action >= 0 && action < NUMBER_OF_PERSONS) {
                setResponse(new Integer(action));
            } else {
                logger.warning("Invalid Actioncommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }

}
