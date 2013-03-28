



package net.sf.freecol.client.gui.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;



public class MiniMapChangeBackgroundAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(MiniMapChangeBackgroundAction.class.getName());

    public static final String id = "miniMapChangeBackgroundAction";


    
    MiniMapChangeBackgroundAction(FreeColClient freeColClient) {
        super(freeColClient, "black", null);
    }
    
    
    
    public String getId() {
        return id;
    }
    

    
    protected boolean shouldBeEnabled() {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        return super.shouldBeEnabled()
                && mca.getMapControls() != null;
    }  
    
        
    public void actionPerformed(ActionEvent ae) {
        MapControlsAction mca = (MapControlsAction) getFreeColClient().getActionManager().getFreeColAction(MapControlsAction.id);
        final String whichColor = ae.getActionCommand();
        Color toColor = Color.BLACK;
        if( whichColor != null ) {
        	if( whichColor.equalsIgnoreCase("gray.light")) {
        		toColor = new Color(220,220,220);
        	} else if( whichColor.equalsIgnoreCase("gray.dark")) {
            		toColor = new Color(100,100,100);
        	} else if( whichColor.equalsIgnoreCase("gray")) {
        		toColor = new Color(160,160,160);
        	} else if( whichColor.equalsIgnoreCase("blue.light")) {
        		toColor = new Color(255,255,200);
        	}
        }
        mca.getMapControls().changeBackgroundColor(toColor);
        update();
        getFreeColClient().getActionManager().getFreeColAction(MiniMapChangeBackgroundAction.id).update();
    }

    
    public static Color interpretIndex(int index) {
        Color toColor = Color.BLACK;
        switch( index ) {
        case 1:
        	return new Color(48,48,48);
        case 2:
        	return new Color(96,96,96);
        case 3:
        	return new Color(128,128,128);
        case 4:
        	return new Color(176,176,176);
        case 5:
        	return new Color(224,224,224);
        case 6:
        	return new Color(200,200,255);
        }
    	return Color.BLACK;











    }
}
