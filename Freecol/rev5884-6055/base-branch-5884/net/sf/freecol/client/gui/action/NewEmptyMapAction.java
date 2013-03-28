

package net.sf.freecol.client.gui.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.FreeColDialog;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.server.generator.MapGenerator;


public class NewEmptyMapAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NewEmptyMapAction.class.getName());

    public static final String id = "newEmptyMapAction";


    
    NewEmptyMapAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.newEmptyMap", null, null);
    }

    
    protected boolean shouldBeEnabled() {
        return freeColClient.isMapEditor();
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        final Canvas canvas = getFreeColClient().getCanvas();
        final Game game = freeColClient.getGame();

        Dimension size = canvas.showFreeColDialog(FreeColDialog.createMapSizeDialog());
        if (size == null) {
            return;
        }
        
        final MapGenerator mapGenerator = (MapGenerator) freeColClient.getFreeColServer().getMapGenerator();
        mapGenerator.getTerrainGenerator().createMap(game, new boolean[size.width][size.height]);        
        
        freeColClient.getGUI().setFocus(1, 1);
        freeColClient.getActionManager().update();
        canvas.refresh();
    }
    
}
