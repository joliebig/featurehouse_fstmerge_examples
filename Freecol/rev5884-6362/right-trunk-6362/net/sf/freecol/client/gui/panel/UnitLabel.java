

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.control.InGameController;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;


public final class UnitLabel extends JLabel implements ActionListener {

    private static Logger logger = Logger.getLogger(UnitLabel.class.getName());

    public static enum UnitAction { ASSIGN,
            CLEAR_SPECIALITY, ACTIVATE_UNIT, FORTIFY, SENTRY,
            COLOPEDIA, LEAVE_TOWN, WORK_TILE, WORK_BUILDING }

    private final Unit unit;

    private final Canvas parent;

    private boolean selected;

    private boolean ignoreLocation;

    private InGameController inGameController;


    
    public UnitLabel(Unit unit, Canvas parent) {
        ImageLibrary lib = parent.getImageLibrary();
        setIcon(lib.getUnitImageIcon(unit));
        setDisabledIcon(lib.getUnitImageIcon(unit, true));
        this.unit = unit;
        setDescriptionLabel(unit.getName());
        this.parent = parent;
        selected = false;

        setSmall(false);
        setIgnoreLocation(false);

        this.inGameController = parent.getClient().getInGameController();
    }

    
    public UnitLabel(Unit unit, Canvas parent, boolean isSmall) {
        this(unit, parent);
        setSmall(isSmall);
        setIgnoreLocation(false);
    }

    
    public UnitLabel(Unit unit, Canvas parent, boolean isSmall, boolean ignoreLocation) {
        this(unit, parent);
        setSmall(isSmall);
        setIgnoreLocation(ignoreLocation);
    }

    
    public Canvas getCanvas() {
        return parent;
    }

    
    public Unit getUnit() {
        return unit;
    }

    
    public void setSelected(boolean b) {
        selected = b;
    }

    
    public void setIgnoreLocation(boolean b) {
        ignoreLocation = b;
    }

    
    public void setSmall(boolean isSmall) {
        ImageIcon imageIcon = parent.getImageLibrary().getUnitImageIcon(unit);
        ImageIcon disabledImageIcon = parent.getImageLibrary().getUnitImageIcon(unit, true);
        if (isSmall) {
            setPreferredSize(null);
            
            
            
            setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance((imageIcon.getIconWidth() / 3) * 2,
                    (imageIcon.getIconHeight() / 3) * 2, Image.SCALE_SMOOTH)));

            setDisabledIcon(new ImageIcon(disabledImageIcon.getImage().getScaledInstance(
                    (imageIcon.getIconWidth() / 3) * 2, (imageIcon.getIconHeight() / 3) * 2, Image.SCALE_SMOOTH)));
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        } else {
            if (unit.getLocation() instanceof ColonyTile) {
                TileType tileType = ((ColonyTile) unit.getLocation()).getTile().getType();
                setSize(new Dimension(parent.getImageLibrary().getTerrainImageWidth(tileType) / 2,
                                      imageIcon.getIconHeight()));
            } else {
                setPreferredSize(null);
            }

            setIcon(imageIcon);
            setDisabledIcon(disabledImageIcon);
            if (unit.getLocation() instanceof ColonyTile) {
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            } else {
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            }
        }

    }

    
    public String getDescriptionLabel() {
        return getToolTipText();
    }

    
    public void setDescriptionLabel(String label) {
        setToolTipText(label);

    }

    
    public void paintComponent(Graphics g) {

        String name = unit.getName();
        String equipmentLabel = unit.getEquipmentLabel();
        if (equipmentLabel != null) {
            name = name + " (" + equipmentLabel + ")";
        }
        setToolTipText(name);

        if (ignoreLocation || selected || (!unit.isCarrier() && unit.getState() != UnitState.SENTRY)) {
            setEnabled(true);
        } else if (unit.getOwner() != parent.getClient().getMyPlayer() && unit.getColony() == null) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }

        super.paintComponent(g);
        if (ignoreLocation)
            return;

        if (unit.getLocation() instanceof ColonyTile) {
            GoodsType workType = unit.getWorkType();
            int production = ((ColonyTile) unit.getLocation()).getProductionOf(workType);

            ProductionLabel pl = new ProductionLabel(workType, production, getCanvas());
            g.translate(0, 10);
            pl.paintComponent(g);
            g.translate(0, -10);
        } else if (getParent() instanceof ColonyPanel.OutsideColonyPanel ||
                   getParent() instanceof ColonyPanel.InPortPanel ||
                   getParent() instanceof EuropePanel.InPortPanel ||
                   getParent() instanceof EuropePanel.DocksPanel ||
                   getParent().getParent() instanceof ReportPanel) {
            int x = (getWidth() - getIcon().getIconWidth()) / 2;
            int y = (getHeight() - getIcon().getIconHeight()) / 2;
            parent.getGUI().displayOccupationIndicator(g, unit, x, y);

            if (unit.isUnderRepair()) {
                String underRepair = Messages.message("underRepair",
                                                      "%turns%", Integer.toString(unit.getTurnsForRepair()));
                String underRepair1 = underRepair.substring(0, underRepair.indexOf('(')).trim();
                String underRepair2 = underRepair.substring(underRepair.indexOf('(')).trim();
                BufferedImage repairImage1 = parent.getGUI()
                    .createStringImage((Graphics2D)g, underRepair1, Color.RED, getWidth(), 14);
                BufferedImage repairImage2 = parent.getGUI()
                    .createStringImage((Graphics2D)g, underRepair2, Color.RED, getWidth(), 14);
                int textHeight = repairImage1.getHeight() + repairImage2.getHeight();
                int leftIndent = Math.min(5, Math.min(getWidth() - repairImage1.getWidth(),
                                                      getWidth() - repairImage2.getWidth()));
                g.drawImage(repairImage1,
                            leftIndent, 
                            ((getHeight() - textHeight) / 2),
                            null);
                g.drawImage(repairImage2, 
                            leftIndent, 
                            ((getHeight() - textHeight) / 2) + repairImage1.getHeight(), 
                            null);
            }
        }
    }

    
    public void actionPerformed(ActionEvent event) {
        String commandString = event.getActionCommand();
	String arg = null;
	int index = commandString.indexOf(':');
	if (index > 0) {
	    arg = commandString.substring(index + 1);
	    commandString = commandString.substring(0, index);
	}
	UnitAction command = Enum.valueOf(UnitAction.class, commandString.toUpperCase());
	switch(command) {
	case ASSIGN:
	    Unit teacher = (Unit) unit.getGame().getFreeColGameObject(arg);
	    inGameController.assignTeacher(unit, teacher);
	    Component uc = getParent();
	    while (uc != null) {
                
		uc = uc.getParent();
	    }
	    break;
	case WORK_TILE:
	    GoodsType goodsType = FreeCol.getSpecification().getGoodsType(arg);
	    
	    inGameController.changeWorkType(unit, goodsType);
	    
	    ColonyTile bestTile = unit.getColony().getVacantColonyTileFor(unit, false, goodsType);
            if (bestTile != unit.getLocation()) {
                inGameController.work(unit, bestTile);
            }
	    break;
	case WORK_BUILDING:
	    BuildingType buildingType = FreeCol.getSpecification().getBuildingType(arg);
	    Building building = unit.getColony().getBuilding(buildingType);
	    inGameController.work(unit, building);
	    break;
	case ACTIVATE_UNIT:
            inGameController.changeState(unit, UnitState.ACTIVE);
	    parent.getGUI().setActiveUnit(unit);
	    break;
	case FORTIFY:
	    inGameController.changeState(unit, UnitState.FORTIFYING);
	    break;
        case SENTRY:
	    inGameController.changeState(unit, UnitState.SENTRY);
	    break;
        case COLOPEDIA:
	    getCanvas().showPanel(new ColopediaPanel(getCanvas(), ColopediaPanel.PanelType.UNITS, unit.getType()));
	    break;
	case LEAVE_TOWN:
	    inGameController.putOutsideColony(unit);
	    break;
	case CLEAR_SPECIALITY:
	    inGameController.clearSpeciality(unit);
	    break;
	}
	updateIcon();
    }


    public void updateIcon() {
        setIcon(parent.getImageLibrary().getUnitImageIcon(unit));
        setDisabledIcon(parent.getImageLibrary().getUnitImageIcon(unit, true));

        Component uc = getParent();
        while (uc != null) {
            if (uc instanceof ColonyPanel) {
                if (unit.getColony() == null) {
                    parent.remove(uc);
                    parent.getClient().getActionManager().update();
                } else {
                    
                }

                break;
            } else if (uc instanceof EuropePanel) {
                break;
            }

            uc = uc.getParent();
        }

        
        
    }
    
    public boolean canUnitBeEquipedWith(JLabel data){
        if(!getUnit().hasAbility("model.ability.canBeEquipped")){
            return false;
        }
        
        if(data instanceof GoodsLabel && ((GoodsLabel)data).isToEquip()){
            return true;
        }
                
        if(data instanceof MarketLabel && ((MarketLabel)data).isToEquip()){
            return true;
        }
        
        return false;
    }
}
