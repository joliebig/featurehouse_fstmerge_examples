

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.NationOptions;
import net.sf.freecol.server.generator.MapGeneratorOptions;

import net.miginfocom.swing.MigLayout;


public final class StartGamePanel extends FreeColPanel implements ActionListener {

    private static final Logger logger = Logger.getLogger(StartGamePanel.class.getName());

    private static final int START = 0, CANCEL = 1,
        READY = 3, CHAT = 4, GAME_OPTIONS = 5, MAP_GENERATOR_OPTIONS = 6;

    private boolean singlePlayerGame;

    private JCheckBox readyBox;

    private JTextField chat;

    private JTextArea chatArea;

    private JButton start;

    private JButton gameOptions;

    private JButton mapGeneratorOptions;

    private PlayersTable table;

    
    public StartGamePanel(final Canvas parent) {
        super(parent);
    }

    public void initialize(boolean singlePlayer) {

        removeAll();
        this.singlePlayerGame = singlePlayer;

        NationOptions nationOptions = getGame().getNationOptions();

        JButton cancel = new JButton(Messages.message("cancel"));

        JScrollPane chatScroll = null, tableScroll;

        setCancelComponent(cancel);

        table = new PlayersTable(getCanvas(), nationOptions, getMyPlayer());
        
        start = new JButton(Messages.message("startGame"));
        gameOptions = new JButton(Messages.message("gameOptions"));
        mapGeneratorOptions = new JButton(Messages.message("mapGeneratorOptions"));
        readyBox = new JCheckBox(Messages.message("iAmReady"));

        if (singlePlayerGame) {
            
            
            getMyPlayer().setReady(false);
            
            readyBox.setSelected(true);
        } else {
            readyBox.setSelected(getMyPlayer().isReady());
            chat = new JTextField();
            chatArea = new JTextArea();
            chatScroll = new JScrollPane(chatArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }

        refreshPlayersTable();
        tableScroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.getViewport().setOpaque(false);

        setLayout(new MigLayout("fill", "", ""));

        add(tableScroll, "width 600:, span 2, grow");
        if (!singlePlayerGame) {
            add(chatScroll, "width 250:, grow");
        }
        add(mapGeneratorOptions, "newline, growx, top");
        add(gameOptions, "growx, top");
        if (!singlePlayerGame) {
            add(chat, "grow, top");
        }
        add(readyBox, "newline, span");
        add(start, "newline, span, split 2, tag ok");
        add(cancel, "tag cancel");

        start.setActionCommand(String.valueOf(START));
        cancel.setActionCommand(String.valueOf(CANCEL));
        readyBox.setActionCommand(String.valueOf(READY));
        gameOptions.setActionCommand(String.valueOf(GAME_OPTIONS));
        mapGeneratorOptions.setActionCommand(String.valueOf(MAP_GENERATOR_OPTIONS));

        if (!singlePlayerGame) {
            chat.setActionCommand(String.valueOf(CHAT));
            chat.addActionListener(this);
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatArea.setText("");
        }

        enterPressesWhenFocused(start);
        enterPressesWhenFocused(cancel);
        
        start.addActionListener(this);
        cancel.addActionListener(this);
        readyBox.addActionListener(this);
        gameOptions.addActionListener(this);
        mapGeneratorOptions.addActionListener(this);

        setSize(getPreferredSize());

        setEnabled(true);

    }

    public void requestFocus() {
        start.requestFocus();
    }

    
    public void updateMapGeneratorOptions() {
        getClient().getPreGameController().getMapGeneratorOptions()
            .getObject(MapGeneratorOptions.MAP_SIZE);
    }

    
    public void updateGameOptions() {
        
    }

    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(enabled);
        }

        if (singlePlayerGame && enabled) {
            readyBox.setEnabled(false);
        }

        if (enabled) {
            start.setEnabled(getClient().isAdmin());
        }

        gameOptions.setEnabled(enabled);
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        try {
            switch (Integer.valueOf(command).intValue()) {
            case START:
                
                
                
                if (singlePlayerGame) {
                    getMyPlayer().setReady(true);
                }

                getClient().getPreGameController().requestLaunch();
                break;
            case CANCEL:
                getClient().getConnectController().quitGame(true);
                getCanvas().remove(this);
                getCanvas().showPanel(new NewPanel(getCanvas()));
                break;
            case READY:
                getClient().getPreGameController().setReady(readyBox.isSelected());
                refreshPlayersTable();
                break;
            case CHAT:
                if (chat.getText().trim().length() > 0) {
                    getClient().getPreGameController().chat(chat.getText());
                    displayChat(getMyPlayer().getName(), chat.getText(), false);
                    chat.setText("");
                }
                break;
            case GAME_OPTIONS:
                getCanvas().showFreeColDialog(new GameOptionsDialog(getCanvas(), getClient().isAdmin()));
                break;
            case MAP_GENERATOR_OPTIONS:
                getCanvas().showMapGeneratorOptionsDialog(getClient().isAdmin());
                break;
            default:
                logger.warning("Invalid Actioncommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }

    
    public void displayChat(String senderName, String message, boolean privateChat) {
        if (privateChat) {
            chatArea.append(senderName + " (private): " + message + '\n');
        } else {
            chatArea.append(senderName + ": " + message + '\n');
        }
    }

    
    public void refreshPlayersTable() {
        table.update();
    }

}