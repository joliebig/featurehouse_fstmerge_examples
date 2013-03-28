

package net.sf.freecol.client.gui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.MapControls;


public class MapControlsAction extends SelectableAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MapControlsAction.class.getName());

    public static final String id = "mapControlsAction";

    private MapControls mapControls;


    
    MapControlsAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.mapControls", null, KeyStroke.getKeyStroke('M', Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));

        setSelected(freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_MAP_CONTROLS));
    }

    
    public void update() {
        super.update();

        showMapControls(enabled && isSelected());
    }

    
    public String getId() {
        return id;
    }

    
    public MapControls getMapControls() {
        return mapControls;
    }

    
    public void actionPerformed(ActionEvent e) {
        selected = ((AbstractButton) e.getSource()).isSelected();
        showMapControls(enabled && selected);
    }

    private void showMapControls(boolean value) {
        if (value && getFreeColClient().getGUI().isInGame()) {
            if (mapControls == null) {
                mapControls = new MapControls(getFreeColClient());
            }
            mapControls.update();
        }
        if (mapControls != null) {
            if (value) {
                if (!mapControls.isShowing()) {
                    mapControls.addToComponent(getFreeColClient().getCanvas());
                }
                mapControls.update();
            } else {
                if (mapControls.isShowing()) {
                    mapControls.removeFromComponent(getFreeColClient().getCanvas());
                }
            }
        }
    }
}
