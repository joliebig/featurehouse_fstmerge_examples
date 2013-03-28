

package net.sf.freecol.client.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.action.*;
import net.sf.freecol.client.gui.action.DisplayTileTextAction.DisplayText;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.menu.DebugMenu;
import net.sf.freecol.common.model.Map.Direction;


public class InGameMenuBar extends FreeColMenuBar {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(InGameMenuBar.class.getName());

    public static final int UNIT_ORDER_WAIT = 0;
    public static final int UNIT_ORDER_FORTIFY = 1;
    public static final int UNIT_ORDER_SENTRY = 2;
    public static final int UNIT_ORDER_CLEAR_ORDERS = 3;
    public static final int UNIT_ORDER_BUILD_COL = 5;
    public static final int UNIT_ORDER_SKIP = 9;
    public static final int UNIT_ORDER_DISBAND = 11;

    private JMenuItem reportsTradeMenuItem = null;


    
    public InGameMenuBar(FreeColClient f) {

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        super(f);

        reset();
    }


    
    public void reset() {
        removeAll();

        buildGameMenu();
        buildViewMenu();
        buildOrdersMenu();
        buildReportMenu();
        buildColopediaMenu();

        
        if (FreeCol.isInDebugMode()) {
            add(new DebugMenu(freeColClient));
        }

        update();
    }

    private void buildGameMenu() {
        
        JMenu menu = new JMenu(Messages.message("menuBar.game"));
        menu.setOpaque(false);
        menu.setMnemonic(KeyEvent.VK_G);

        menu.add(getMenuItem(NewAction.id));
        menu.add(getMenuItem(OpenAction.id));
        menu.add(getMenuItem(SaveAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(PreferencesAction.id));
        menu.add(getMenuItem(ReconnectAction.id));
        menu.add(getMenuItem(DebugAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(ChatAction.id));
        menu.add(getMenuItem(DeclareIndependenceAction.id));
        menu.add(getMenuItem(EndTurnAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(ShowMainAction.id));
        menu.add(getMenuItem(ReportHighScoresAction.id));
        menu.add(getMenuItem(RetireAction.id));
        menu.add(getMenuItem(SaveAndQuitAction.id));


        add(menu);
    }

    private void buildViewMenu() {
        

        JMenu menu = new JMenu(Messages.message("menuBar.view"));
        menu.setOpaque(false);
        menu.setMnemonic(KeyEvent.VK_V);

        menu.add(getCheckBoxMenuItem(MapControlsAction.id));
        menu.add(getCheckBoxMenuItem(DisplayGridAction.id));
        menu.add(getCheckBoxMenuItem(DisplayBordersAction.id));
        menu.add(getMenuItem(ToggleViewModeAction.id));
        menu.add(getCheckBoxMenuItem(ChangeWindowedModeAction.id));

        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        for (DisplayText type : DisplayText.values()) {
            menu.add(getRadioButtonMenuItem(DisplayTileTextAction.id + type, group));
        }

        menu.addSeparator();
        menu.add(getMenuItem(ZoomInAction.id));
        menu.add(getMenuItem(ZoomOutAction.id));
        menu.add(getMenuItem(CenterAction.id));
        menu.add(getMenuItem(TilePopupAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(EuropeAction.id));
        menu.add(getMenuItem(TradeRouteAction.id));
        menu.add(getMenuItem(FindSettlementAction.id));

        add(menu);
    }

    private void buildOrdersMenu() {
        
        JMenu menu = new JMenu(Messages.message("menuBar.orders"));
        menu.setOpaque(false);
        menu.setMnemonic(KeyEvent.VK_O);

        menu.add(getMenuItem(SentryAction.id));
        menu.add(getMenuItem(FortifyAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(GotoAction.id));
        menu.add(getMenuItem(GotoTileAction.id));
        menu.add(getMenuItem(ExecuteGotoOrdersAction.id));
        menu.add(getMenuItem(AssignTradeRouteAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(BuildColonyAction.id));
        
        for (ImprovementActionType iaType : FreeCol.getSpecification().getImprovementActionTypeList()) {
            menu.add(getMenuItem(iaType.getId()));
        }

        menu.addSeparator();

        menu.add(getMenuItem(LoadAction.id));
        menu.add(getMenuItem(UnloadAction.id));

        menu.addSeparator();

        menu.add(getMenuItem(WaitAction.id));
        menu.add(getMenuItem(SkipUnitAction.id));
        menu.add(getMenuItem(ChangeAction.id));
 
        menu.addSeparator();

        menu.add(getMenuItem(ClearOrdersAction.id));
        menu.add(getMenuItem(RenameAction.id));
        menu.add(getMenuItem(DisbandUnitAction.id));

        menu.addSeparator();

        for (Direction d : Direction.values()) {
            menu.add(getMenuItem(MoveAction.id + d));
        }

        add(menu);
    }

    private void buildReportMenu() {
        

        JMenu menu = new JMenu(Messages.message("menuBar.report"));
        menu.setOpaque(false);
        menu.setMnemonic(KeyEvent.VK_R);

        menu.add(getMenuItem(ReportReligionAction.id));
        menu.add(getMenuItem(ReportLabourAction.id));
        menu.add(getMenuItem(ReportColonyAction.id));
        menu.add(getMenuItem(ReportForeignAction.id));
        menu.add(getMenuItem(ReportIndianAction.id));
        menu.add(getMenuItem(ReportContinentalCongressAction.id));
        menu.add(getMenuItem(ReportMilitaryAction.id));
        menu.add(getMenuItem(ReportNavalAction.id));
        menu.add(getMenuItem(ReportTradeAction.id));
        menu.add(getMenuItem(ReportTurnAction.id));
        menu.add(getMenuItem(ReportRequirementsAction.id));
        menu.add(getMenuItem(ReportCargoAction.id));
        menu.add(getMenuItem(ReportExplorationAction.id));
        menu.add(getMenuItem(ReportHistoryAction.id));
        menu.add(getMenuItem(ReportProductionAction.id));

        add(menu);

    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (freeColClient != null && freeColClient.getMyPlayer() != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                 RenderingHints.VALUE_RENDER_QUALITY);

            String displayString =
                Messages.message("menuBar.statusLine",
                                 "%gold%", Integer.toString(freeColClient.getMyPlayer().getGold()),
                                 "%tax%", Integer.toString(freeColClient.getMyPlayer().getTax()),
                                 "%score%", Integer.toString(freeColClient.getMyPlayer().getScore()),
                                 "%year%", freeColClient.getGame().getTurn().toString());
            Rectangle2D displayStringBounds = g2d.getFontMetrics().getStringBounds(displayString, g);
            int y = 15 + getInsets().top;
            g2d.drawString(displayString, getWidth() - 10 - (int) displayStringBounds.getWidth(), y);
        }
    }

}
