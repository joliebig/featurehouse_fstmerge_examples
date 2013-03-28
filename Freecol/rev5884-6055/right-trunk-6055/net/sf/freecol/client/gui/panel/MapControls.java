


package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ViewMode;
import net.sf.freecol.client.gui.action.ActionManager;
import net.sf.freecol.client.gui.action.BuildColonyAction;
import net.sf.freecol.client.gui.action.DisbandUnitAction;
import net.sf.freecol.client.gui.action.FortifyAction;
import net.sf.freecol.client.gui.action.ImprovementActionType;
import net.sf.freecol.client.gui.action.SentryAction;
import net.sf.freecol.client.gui.action.SkipUnitAction;
import net.sf.freecol.client.gui.action.WaitAction;
import net.sf.freecol.client.gui.panel.MapEditorTransformPanel.MapTransform;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.resources.ResourceManager;



public final class MapControls {

    private final FreeColClient freeColClient;

    private final InfoPanel        infoPanel;
    private final MiniMap          miniMap;
    private final UnitButton[]     unitButton;   
    private final JLabel compassRose;

    private static final int CONTROLS_LAYER = JLayeredPane.MODAL_LAYER;

    
    public MapControls(final FreeColClient freeColClient) {
        this.freeColClient = freeColClient;

        
        
        

        infoPanel = new InfoPanel(freeColClient);
        miniMap = new MiniMap(freeColClient);
        compassRose = new JLabel(ResourceManager.getImageIcon("compass.image"));
        
        final ActionManager am = freeColClient.getActionManager();
        
        List<UnitButton> ubList = new ArrayList<UnitButton>();
        ubList.add(new UnitButton(am.getFreeColAction(WaitAction.id)));
        ubList.add(new UnitButton(am.getFreeColAction(SkipUnitAction.id)));
        ubList.add(new UnitButton(am.getFreeColAction(SentryAction.id)));
        ubList.add(new UnitButton(am.getFreeColAction(FortifyAction.id)));
        for (ImprovementActionType iaType : FreeCol.getSpecification().getImprovementActionTypeList()) {
            ubList.add(new UnitButton(am.getFreeColAction(iaType.getId())));
        }
        ubList.add(new UnitButton(am.getFreeColAction(BuildColonyAction.id)));
        ubList.add(new UnitButton(am.getFreeColAction(DisbandUnitAction.id)));
        unitButton = (ubList.toArray(new UnitButton[0]));

        
        
        
        infoPanel.setFocusable(false);
        miniMap.setFocusable(false);
        compassRose.setFocusable(false);

        for(int i=0; i<unitButton.length; i++) {
            unitButton[i].setFocusable(false);
        }

        compassRose.setSize(compassRose.getPreferredSize());
        compassRose.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() - compassRose.getWidth()/2;
                    int y = e.getY() - compassRose.getHeight()/2;
                    double theta = Math.atan2(y, x) + Math.PI/2 + Math.PI/8;
                    if (theta < 0) {
                        theta += 2*Math.PI;
                    }
                    Direction direction = Direction.values()[(int) Math.floor(theta / (Math.PI/4))];
                    freeColClient.getInGameController().moveActiveUnit(direction);
                }
            });

    }

    
    public void update(MapTransform mapTransform) {
        if (infoPanel != null) {
            infoPanel.update(mapTransform);
        }
    }

    
    public void addToComponent(Canvas component) {
        if (freeColClient.getGame() == null
            || freeColClient.getGame().getMap() == null) {
            return;
        }
        
        
        
        

        infoPanel.setLocation(component.getWidth() - infoPanel.getWidth(), component.getHeight() - infoPanel.getHeight());
        miniMap.setLocation(0, component.getHeight() - miniMap.getHeight());
        compassRose.setLocation(component.getWidth() - compassRose.getWidth() - 20, 20);
        
        final int SPACE = unitButton[0].getWidth() + 5;
        for(int i=0; i<unitButton.length; i++) {            
            unitButton[i].setLocation(miniMap.getWidth() +
                                      (infoPanel.getX() - miniMap.getWidth() - 
                                       unitButton.length*SPACE)/2 +
                                      i*SPACE,
                                      component.getHeight() - 40);
        }

        
        
        
        component.add(infoPanel, CONTROLS_LAYER, false);
        component.add(miniMap, CONTROLS_LAYER, false);
        if (freeColClient.getClientOptions().getBoolean(ClientOptions.DISPLAY_COMPASS_ROSE)) {
            component.add(compassRose, CONTROLS_LAYER, false);
        }

        if (!freeColClient.isMapEditor()) {
            for(int i=0; i<unitButton.length; i++) {
                component.add(unitButton[i], CONTROLS_LAYER, false);
                Action a = unitButton[i].getAction();
                unitButton[i].setAction(null);
                unitButton[i].setAction(a);
            }
        }
    }

    
    public int getInfoPanelWidth() {
        return infoPanel.getWidth();
    }

    
    public int getInfoPanelHeight() {
        return infoPanel.getHeight();
    }


    
    public void removeFromComponent(Canvas canvas) {
        canvas.remove(infoPanel, false);
        canvas.remove(miniMap, false);
        canvas.remove(compassRose, false);

        for(int i=0; i<unitButton.length; i++) {
            canvas.remove(unitButton[i], false);
        }
    }


    public boolean isShowing() {
        return infoPanel.getParent() != null;
    }


    
    public void zoomIn() {
        miniMap.zoomIn();
    }


    
    public void zoomOut() {
        miniMap.zoomOut();
    }
    
    public boolean canZoomIn() {
        return miniMap.canZoomIn();
    }

    public boolean canZoomOut() {
        return miniMap.canZoomOut();
    }

    
    public void changeBackgroundColor(Color newColor) {
    	miniMap.setBackgroundColor(newColor);
    }

    
    public void update() {
        GUI gui = freeColClient.getGUI();
        int viewMode = gui.getViewMode().getView();
        switch (viewMode) {
        case ViewMode.MOVE_UNITS_MODE:
            infoPanel.update(gui.getActiveUnit());
            break;
        case ViewMode.VIEW_TERRAIN_MODE:
            if (gui.getSelectedTile() != null) {
                Tile selectedTile = freeColClient.getGame().getMap().getTile(gui.getSelectedTile());
                if (infoPanel.getTile() != selectedTile) {
                    infoPanel.update(selectedTile);
                }
            }
            break;
        }
    }
}
