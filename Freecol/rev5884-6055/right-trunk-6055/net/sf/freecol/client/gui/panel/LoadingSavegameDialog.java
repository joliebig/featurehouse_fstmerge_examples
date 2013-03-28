

package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;


public final class LoadingSavegameDialog extends FreeColDialog<Boolean> implements ActionListener {
    private static final Logger logger = Logger.getLogger(LoadingSavegameDialog.class.getName());




    private static final int OK = 0, CANCEL = 1;

    private JButton ok;

    private JPanel buttons = new JPanel(new FlowLayout());

    private JLabel header;

    private JRadioButton singleplayer;

    private JRadioButton privateMultiplayer;

    private JRadioButton publicMultiplayer;

    private JTextField serverNameField;

    private JSpinner portField;


    
    public LoadingSavegameDialog(Canvas parent) {
        super(parent);
        setLayout(new BorderLayout());

        ok = new JButton(Messages.message("ok"));
        ok.setActionCommand(String.valueOf(OK));
        ok.addActionListener(this);
        ok.setMnemonic('O');
        buttons.add(ok);

        JButton cancel = new JButton(Messages.message("cancel"));
        cancel.setActionCommand(String.valueOf(CANCEL));
        cancel.addActionListener(this);
        cancel.setMnemonic('C');
        buttons.add(cancel);

        FreeColPanel.enterPressesWhenFocused(ok);
        setCancelComponent(cancel);

        
        header = new JLabel(Messages.message("LoadingSavegame.title"), JLabel.CENTER);
        header.setFont(mediumHeaderFont);
        header.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(header, BorderLayout.NORTH);

        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.add(new JLabel(Messages.message("LoadingSavegame.serverName"), JLabel.LEFT));
        panel.add(p1);
        serverNameField = new JTextField();
        panel.add(serverNameField);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2.add(new JLabel(Messages.message("LoadingSavegame.port"), JLabel.LEFT));
        panel.add(p2);
        portField = new JSpinner(new SpinnerNumberModel(FreeCol.getDefaultPort(), 1, 65536, 1));
        panel.add(portField);

        ButtonGroup bg = new ButtonGroup();
        singleplayer = new JRadioButton(Messages.message("LoadingSavegame.singleplayer"));
        bg.add(singleplayer);
        panel.add(singleplayer);
        privateMultiplayer = new JRadioButton(Messages.message("LoadingSavegame.privateMultiplayer"));
        bg.add(privateMultiplayer);
        panel.add(privateMultiplayer);
        publicMultiplayer = new JRadioButton(Messages.message("LoadingSavegame.publicMultiplayer"));
        bg.add(publicMultiplayer);
        panel.add(publicMultiplayer);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        
        add(buttons, BorderLayout.SOUTH);

        setSize(getPreferredSize());

    }

    public boolean isSingleplayer() {
        return singleplayer.isSelected();
    }

    public boolean isPublic() {
        return publicMultiplayer.isSelected();
    }

    public int getPort() {
        return ((Integer) portField.getValue()).intValue();
    }

    public String getName() {
        return serverNameField.getName();
    }

    public void initialize(boolean publicServer, boolean singleplayer) {

        this.singleplayer.setSelected(false);
        this.privateMultiplayer.setSelected(false);
        this.publicMultiplayer.setSelected(false);

        if (singleplayer) {
            this.singleplayer.setSelected(true);
        } else if (publicServer) {
            this.publicMultiplayer.setSelected(true);
        } else {
            this.privateMultiplayer.setSelected(true);
        }

        this.serverNameField.setText("");
    }

    public void requestFocus() {
        ok.requestFocus();
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
            case OK:
                getCanvas().remove(this);
                setResponse(Boolean.TRUE);
                break;
            case CANCEL:
                getCanvas().remove(this);
                setResponse(Boolean.FALSE);
                break;
            default:
                logger.warning("Invalid ActionCommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }
}
