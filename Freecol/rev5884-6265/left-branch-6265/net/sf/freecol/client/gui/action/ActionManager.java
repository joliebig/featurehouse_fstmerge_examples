

package net.sf.freecol.client.gui.action;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.OptionGroup;


public class ActionManager extends OptionGroup {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ActionManager.class.getName());

    private FreeColClient freeColClient;

    
    public ActionManager(FreeColClient freeColClient) {
        super("actionManager");

        this.freeColClient = freeColClient;

        freeColClient.getClientOptions().add(this);
        freeColClient.getClientOptions().addToMap(this);
    }


    public void initializeActions() {
        removeAll();
        
        
        add(new AboutAction(freeColClient));
        add(new AssignTradeRouteAction(freeColClient));
        add(new BuildColonyAction(freeColClient));
        add(new ChangeAction(freeColClient));
        add(new ChangeWindowedModeAction(freeColClient));
        add(new ChatAction(freeColClient));
        add(new ClearOrdersAction(freeColClient));
        add(new ColopediaBuildingAction(freeColClient));
        add(new ColopediaFatherAction(freeColClient));
        add(new ColopediaGoodsAction(freeColClient));
        add(new ColopediaNationAction(freeColClient));
        add(new ColopediaNationTypeAction(freeColClient));
        add(new ColopediaResourceAction(freeColClient));
        add(new ColopediaSkillAction(freeColClient));
        add(new ColopediaTerrainAction(freeColClient));
        add(new ColopediaUnitAction(freeColClient));
        add(new DeclareIndependenceAction(freeColClient));
        add(new DetermineHighSeasAction(freeColClient));
        add(new DisbandUnitAction(freeColClient));
        add(new DisplayBordersAction(freeColClient));
        add(new DisplayGridAction(freeColClient));
        add(new DisplayTileEmptyAction(freeColClient));
        add(new DisplayTileNamesAction(freeColClient));
        add(new DisplayTileOwnersAction(freeColClient));
        add(new DisplayTileRegionsAction(freeColClient));
        add(new EndTurnAction(freeColClient));
        add(new EuropeAction(freeColClient));
        add(new ExecuteGotoOrdersAction(freeColClient));
        add(new FindSettlementAction(freeColClient));
        add(new FortifyAction(freeColClient));
        add(new GotoAction(freeColClient));
        add(new GotoTileAction(freeColClient));
        
        for (ImprovementActionType ia : FreeCol.getSpecification().getImprovementActionTypeList()) {
            add(new ImprovementAction(freeColClient, ia));
        }
        add(new LoadAction(freeColClient));
        add(new MapControlsAction(freeColClient));
        add(new MiniMapChangeBackgroundAction(freeColClient));
        add(new MiniMapZoomInAction(freeColClient));
        add(new MiniMapZoomOutAction(freeColClient));
        add(new NewAction(freeColClient));
        add(new NewEmptyMapAction(freeColClient));
        add(new OpenAction(freeColClient));
        add(new PreferencesAction(freeColClient));
        add(new SaveAndQuitAction(freeColClient));
        add(new QuitAction(freeColClient));
        add(new ReconnectAction(freeColClient));
        add(new RenameAction(freeColClient));
        add(new ReportCargoAction(freeColClient));
        add(new ReportContinentalCongressAction(freeColClient));
        add(new ReportColonyAction(freeColClient));
        add(new ReportExplorationAction(freeColClient));
        add(new ReportForeignAction(freeColClient));
        add(new ReportHighScoresAction(freeColClient));
        add(new ReportHistoryAction(freeColClient));
        add(new ReportIndianAction(freeColClient));
        add(new ReportLabourAction(freeColClient));
        add(new ReportMilitaryAction(freeColClient));
        add(new ReportNavalAction(freeColClient));
        add(new ReportProductionAction(freeColClient));
        add(new ReportReligionAction(freeColClient));
        add(new ReportRequirementsAction(freeColClient));
        add(new ReportTradeAction(freeColClient));
        add(new ReportTurnAction(freeColClient));
        add(new RetireAction(freeColClient));
        add(new SaveAction(freeColClient));
        add(new ScaleMapAction(freeColClient));
        add(new SentryAction(freeColClient));
        add(new ShowMainAction(freeColClient));
        add(new SkipUnitAction(freeColClient));
        add(new ToggleViewModeAction(freeColClient));
        add(new TradeRouteAction(freeColClient));
        add(new UnloadAction(freeColClient));
        add(new WaitAction(freeColClient));
        add(new ZoomInAction(freeColClient));
        add(new ZoomOutAction(freeColClient));
    }

    
    public void add(FreeColAction freeColAction) {
        super.add(freeColAction);
    }

    
    public FreeColAction getFreeColAction(String id) {
        Iterator<Option> it = iterator();
        while (it.hasNext()) {
            FreeColAction fa = (FreeColAction) it.next();
            if (fa.getId().equals(id)) {
                return fa;
            }
        }

        return null;
    }

    
    public void update() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("update() should only be called from the " + 
                    "event dispatcher thread.");
        }
        Iterator<Option> it = iterator();
        while (it.hasNext()) {
            FreeColAction fa = (FreeColAction) it.next();
            fa.update();
        }
    }
}
