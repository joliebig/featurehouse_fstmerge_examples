


package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.resources.ResourceManager;


public final class MainPanel extends FreeColPanel implements ActionListener {

    private static final Logger logger = Logger.getLogger(MainPanel.class.getName());
    
    public static final int     NEW = 0,
                                OPEN = 1,
                                MAP_EDITOR = 2,
                                OPTIONS = 3,
                                QUIT = 4;

    private JButton newButton;
    

    
    public MainPanel(Canvas parent) {
        super(parent, new BorderLayout());

        JButton openButton = new JButton( Messages.message("menuBar.game.open") );
        JButton mapEditorButton = new JButton( Messages.message("mainPanel.editor") );
        JButton optionsButton = new JButton( Messages.message("mainPanel.options") );
        JButton quitButton = new JButton( Messages.message("menuBar.game.quit") );
        
        setCancelComponent(quitButton);
        newButton = new JButton( Messages.message("menuBar.game.new") );

        newButton.setActionCommand(String.valueOf(NEW));
        mapEditorButton.setActionCommand(String.valueOf(MAP_EDITOR));
        openButton.setActionCommand(String.valueOf(OPEN));
        optionsButton.setActionCommand(String.valueOf(OPTIONS));
        quitButton.setActionCommand(String.valueOf(QUIT));

        newButton.addActionListener(this);
        mapEditorButton.addActionListener(this);
        openButton.addActionListener(this);
        optionsButton.addActionListener(this);
        quitButton.addActionListener(this);
        
        enterPressesWhenFocused(newButton);
        enterPressesWhenFocused(mapEditorButton);
        enterPressesWhenFocused(openButton);
        enterPressesWhenFocused(optionsButton);
        enterPressesWhenFocused(quitButton);

        Image tempImage = ResourceManager.getImage("TitleImage");

        if (tempImage != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(tempImage));
            logoLabel.setBorder(new CompoundBorder(new EmptyBorder(2,2,0,2), new BevelBorder(BevelBorder.LOWERED)));
            add(logoLabel, BorderLayout.CENTER);
        }

        JPanel buttons = new JPanel(new GridLayout(0, 1, 50, 10));

        buttons.add(newButton);
        buttons.add(openButton);
        buttons.add(mapEditorButton);
        buttons.add(optionsButton);
        buttons.add(quitButton);

        buttons.setBorder(new EmptyBorder(25, 25, 25, 25));        
        buttons.setOpaque(false);

        add(buttons, BorderLayout.SOUTH);

        setSize(getPreferredSize());
    }

    public void requestFocus() {
        newButton.requestFocus();
    }


    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(enabled);
        }
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
                case NEW:
                    getCanvas().remove(this);                
                    getCanvas().showPanel(new NewPanel(getCanvas()));
                    break;
                case OPEN:
                    getClient().getConnectController().loadGame();
                    break;
                case MAP_EDITOR:
                    getClient().getMapEditorController().startMapEditor();
                    break;
                case OPTIONS:
                    getCanvas().showClientOptionsDialog();
                    break;
                case QUIT:
                    getCanvas().quit();
                    break;
                default:
                    logger.warning("Invalid Actioncommand: invalid number.");
            }
        }
        catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }
}
