

package net.sf.freecol.client.gui.panel;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;



public final class VictoryPanel extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(VictoryPanel.class.getName());

    private static final String CONTINUE = "CONTINUE";

    private JButton continueButton = new JButton(Messages.message("victory.continue"));

    
    public VictoryPanel(Canvas parent) {

        super(parent);
        
        okButton.setText(Messages.message("victory.yes"));

        setLayout(new MigLayout("wrap 1", "", ""));

        add(getDefaultHeader(Messages.message("victory.text")), "align center, wrap 20");

        Image tempImage = ResourceManager.getImage("VictoryImage");
        if (tempImage != null) {
            add(new JLabel(new ImageIcon(tempImage)), "align center");
        }

        continueButton.setActionCommand(CONTINUE);
        continueButton.addActionListener(this);
        enterPressesWhenFocused(continueButton);

        if (parent.getClient().isSingleplayer()) {
            add(okButton, "newline 20, split 2, tag ok");
            add(continueButton);
        } else {
            add(okButton, "newline 20, tag ok");
        }
        setSize(getPreferredSize());
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            if(getClient().retire()){
                getCanvas().showInformationMessage("highscores.new");
                getCanvas().showPanel(new ReportHighScoresPanel(getCanvas()));
            }
            getCanvas().quit();
        } else {
            if(getClient().retire()){
                getCanvas().showInformationMessage("highscores.new");
                getCanvas().showPanel(new ReportHighScoresPanel(getCanvas()));
            }
            getClient().continuePlaying();
            getCanvas().remove(this);
        }
    }
}
