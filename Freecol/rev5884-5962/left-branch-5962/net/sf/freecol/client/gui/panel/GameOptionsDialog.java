

package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.option.BooleanOptionUI;
import net.sf.freecol.client.gui.option.OptionMapUI;
import net.sf.freecol.common.model.GameOptions;


public final class GameOptionsDialog extends FreeColDialog<Boolean> implements ActionListener {

    private static final Logger logger = Logger.getLogger(GameOptionsDialog.class.getName());

    private static final int OK = 0, CANCEL = 1, SAVE = 2, LOAD = 3, RESET = 4;

    private JButton ok, load, save, cancel;

    private JPanel buttons = new JPanel(new FlowLayout());

    private JLabel header;

    private OptionMapUI ui;


    
    public GameOptionsDialog(Canvas parent, boolean editable) {
        super(parent);
        setLayout(new BorderLayout());

        ok = new JButton(Messages.message("ok"));
        ok.setActionCommand(String.valueOf(OK));
        ok.addActionListener(this);
        ok.setMnemonic('O');
        buttons.add(ok);

        load = new JButton(Messages.message("load"));
        load.setActionCommand(String.valueOf(LOAD));
        load.addActionListener(this);
        load.setMnemonic('L');
        buttons.add(load);

        save = new JButton(Messages.message("save"));
        save.setActionCommand(String.valueOf(SAVE));
        save.addActionListener(this);
        save.setMnemonic('S');
        buttons.add(save);
        
        JButton reset = new JButton(Messages.message("reset"));
        reset.setActionCommand(String.valueOf(RESET));
        reset.addActionListener(this);
        reset.setMnemonic('R');
        buttons.add(reset);

        cancel = new JButton(Messages.message("cancel"));
        cancel.setActionCommand(String.valueOf(CANCEL));
        cancel.addActionListener(this);
        cancel.setMnemonic('C');
        buttons.add(cancel);

        FreeColPanel.enterPressesWhenFocused(ok);
        setCancelComponent(cancel);

        
        header = getDefaultHeader(getGame().getGameOptions().getName());
        add(header, BorderLayout.NORTH);

        
        JPanel uiPanel = new JPanel(new BorderLayout());
        uiPanel.setOpaque(false);
        ui = new OptionMapUI(getGame().getGameOptions(), editable);
        uiPanel.add(ui, BorderLayout.CENTER);
        uiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(uiPanel, BorderLayout.CENTER);

        
        add(buttons, BorderLayout.SOUTH);

        ok.setEnabled(editable);
        save.setEnabled(editable);
        load.setEnabled(editable);
        
        
        
        
        
        if (editable && getClient().isSingleplayer()){
            BooleanOptionUI comp = (BooleanOptionUI) ui.getOptionUI(GameOptions.VICTORY_DEFEAT_HUMANS);

            comp.setValue(false);
            comp.setEnabled(false);
        }
        setSize(640, 480);

    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(640, 480);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    public void requestFocus() {
        if (ok.isEnabled()) {
            ok.requestFocus();
        } else {
            cancel.requestFocus();
        }
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
            case OK:
                ui.unregister();
                ui.updateOption();
                getClient().getPreGameController().sendGameOptions();
                getCanvas().remove(this);
                setResponse(Boolean.TRUE);
                break;
            case CANCEL:
                ui.rollback();
                ui.unregister();
                getCanvas().remove(this);
                setResponse(Boolean.FALSE);
                break;
            case SAVE:
                FileFilter[] filters = new FileFilter[] { FreeColDialog.getFGOFileFilter(),
                                                          FreeColDialog.getFSGFileFilter(), 
                                                          FreeColDialog.getGameOptionsFileFilter() };
                File saveFile = getCanvas().showSaveDialog(FreeCol.getSaveDirectory(), ".fgo", filters, "");
                if (saveFile != null) {
                    ui.updateOption();
                    getGame().getGameOptions().save(saveFile);
                }
                break;
            case LOAD:
                File loadFile = getCanvas().showLoadDialog(FreeCol.getSaveDirectory(),
                                                           new FileFilter[] {
                                                               FreeColDialog.getFGOFileFilter(),
                                                               FreeColDialog.getFSGFileFilter(),
                                                               FreeColDialog.getGameOptionsFileFilter()
                                                           });
                if (loadFile != null) {
                    getGame().getGameOptions().load(loadFile);
                }
                break;
            case RESET:
                ui.reset();
                break;
            default:
                logger.warning("Invalid ActionCommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }
}
