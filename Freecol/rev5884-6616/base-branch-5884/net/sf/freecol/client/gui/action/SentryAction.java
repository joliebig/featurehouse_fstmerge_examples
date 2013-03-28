


package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.model.Unit.UnitState;


public class SentryAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SentryAction.class.getName());


    
    public static final String id = "sentryAction";
    
    
    public SentryAction(FreeColClient freeColClient) {
        super(freeColClient, "unit.state.3", null, KeyStroke.getKeyStroke('S', 0));
        putValue(BUTTON_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_SENTRY, 0));
        putValue(BUTTON_ROLLOVER_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_SENTRY, 1));
        putValue(BUTTON_PRESSED_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_SENTRY, 2));
        putValue(BUTTON_DISABLED_IMAGE, freeColClient.getImageLibrary().getUnitButtonImageIcon(ImageLibrary.UNIT_BUTTON_SENTRY, 3));        
    }
    
    
    protected boolean shouldBeEnabled() { 
        return super.shouldBeEnabled() 
                && getFreeColClient().getGUI().getActiveUnit() != null;
    }

    
    
    public String getId() {
        return id;
    }

        
    public void actionPerformed(ActionEvent actionEvent) {
        getFreeColClient().getInGameController().changeState(getFreeColClient().getGUI().getActiveUnit(),
                                                             UnitState.SENTRY);
    }
    
}
