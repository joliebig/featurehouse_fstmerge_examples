

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.option.BooleanOption;


public class DisplayGridAction extends SelectableAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DisplayGridAction.class.getName());




    public static final String id = "displayGridAction";


    
    DisplayGridAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.displayGrid", null, KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        setSelected(freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_GRID));
    }

    
    protected boolean shouldBeEnabled() {
        return true;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        boolean b = ((JCheckBoxMenuItem) e.getSource()).isSelected();
        ((BooleanOption) freeColClient.getClientOptions().getObject(ClientOptions.DISPLAY_GRID)).setValue(b);
        freeColClient.getCanvas().refresh();
    }
}
