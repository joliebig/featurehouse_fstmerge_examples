


package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.FreeColMenuBar;
import net.sf.freecol.client.gui.action.MapControlsAction;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.option.OptionMapUI;



public final class ClientOptionsDialog extends FreeColDialog<Boolean>  {

    private static final Logger logger = Logger.getLogger(ClientOptionsDialog.class.getName());


    private static final int    OK = 0,
                                CANCEL = 1,
                                RESET = 2;

    private JPanel buttons = new JPanel(new FlowLayout());
    private JLabel header;
    private OptionMapUI ui;


    
    public ClientOptionsDialog(Canvas parent) {
        super(parent);
        setLayout(new BorderLayout());

        buttons.add(okButton);
        okButton.setActionCommand(String.valueOf(OK));

        JButton reset = new JButton(Messages.message("reset"));
        reset.setActionCommand(String.valueOf(RESET));
        reset.addActionListener(this);
        reset.setMnemonic('R');
        buttons.add(reset);
        
        JButton cancel = new JButton(Messages.message("cancel"));
        cancel.setActionCommand(String.valueOf(CANCEL));
        cancel.addActionListener(this);
        cancel.setMnemonic('C');
        buttons.add(cancel);

        setCancelComponent(cancel);

        setSize(780, 540);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(780, 540);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public void initialize() {
        removeAll();

        
        header = new JLabel(getClient().getClientOptions().getName(), JLabel.CENTER);
        header.setFont(((Font) UIManager.get("HeaderFont")).deriveFont(0, 48));
        header.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(header, BorderLayout.NORTH);

        
        JPanel uiPanel = new JPanel(new BorderLayout());
        uiPanel.setOpaque(false);
        ui = new OptionMapUI(getClient().getClientOptions());
        uiPanel.add(ui, BorderLayout.CENTER);
        uiPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(uiPanel, BorderLayout.CENTER);

        
        add(buttons, BorderLayout.SOUTH);
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
                case OK:
                    ui.unregister();
                    ui.updateOption();
                    getCanvas().remove(this);
                    getClient().saveClientOptions();
                    getClient().getActionManager().update();
                    JMenuBar menuBar = getClient().getFrame().getJMenuBar();
                    if (menuBar != null) {
                        ((FreeColMenuBar) menuBar).reset();
                    }
                    setResponse(Boolean.TRUE);
                    
                    
                    MapControlsAction mca = (MapControlsAction) getClient()
                        .getActionManager().getFreeColAction(MapControlsAction.id);
                    if(mca.getMapControls() != null) {
                        mca.getMapControls().update();                        
                    }
                    break;
                case CANCEL:
                    ui.rollback();
                    ui.unregister();
                    getCanvas().remove(this);
                    setResponse(Boolean.FALSE);
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
