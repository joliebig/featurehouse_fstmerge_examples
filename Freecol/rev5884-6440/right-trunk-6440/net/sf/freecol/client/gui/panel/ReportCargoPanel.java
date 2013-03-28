

package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.TypeCountMap;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;



public final class ReportCargoPanel extends ReportPanel {

    private List<String> colonyNames;
    private List<String> otherNames;

    
    private TypeCountMap<UnitType> carriers = new TypeCountMap<UnitType>();

    private Map<String, ArrayList<Unit>> locations;

    
    int capacity = 0;


    
    public ReportCargoPanel(Canvas parent) {
        super(parent, Messages.message("menuBar.report.cargo"));

        gatherData();

        Player player = getMyPlayer();

        reportPanel.setLayout(new MigLayout("fillx, wrap 12", "", ""));

        reportPanel.add(localizedLabel(StringTemplate.template("report.military.forces")
                                       .addStringTemplate("%nation%", player.getNationName())),
                        "newline, span, split 2");
        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "growx");

        List<AbstractUnit> cargoTypes = new ArrayList<AbstractUnit>();
        for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
            if (unitType.isAvailableTo(player)
                && (unitType.canCarryUnits() || unitType.canCarryGoods())) {
                cargoTypes.add(new AbstractUnit(unitType, Role.DEFAULT, carriers.getCount(unitType)));
            }
        }

        for (AbstractUnit unit : cargoTypes) {
            reportPanel.add(createUnitTypeLabel(unit), "sg");
        }

        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "newline, span, growx, wrap 40");

        
        for (String locationName : colonyNames) {
            handleLocation(locationName, true);
        }

        
        if (player.getEurope() != null) {
            String europeName = player.getEurope().getName();
            handleLocation(europeName, true);
            otherNames.remove(europeName);
        }

        
        Collections.sort(otherNames);
        for (String locationName : otherNames) {
            handleLocation(locationName, false);
        }

        revalidate();
        repaint();
    }

    private void gatherData() {
        locations = new HashMap<String, ArrayList<Unit>>();
        Player player = getMyPlayer();
        List<Colony> colonies = player.getColonies();
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());
        colonyNames = new ArrayList<String>();
        for (Colony colony : colonies) {
            colonyNames.add(colony.getName());
        }
        otherNames = new ArrayList<String>();
        if (player.getEurope() != null) {
            otherNames.add(player.getEurope().getName());
        }

        for (Unit unit : player.getUnits()) {
            if (unit.isCarrier()) {
                carriers.incrementCount(unit.getType(), 1);
                capacity += unit.getType().getSpace();

                String locationName = Messages.message(unit.getLocation().getLocationName());
                if (unit.getState() == UnitState.TO_AMERICA) {
                    locationName = Messages.message("goingToAmerica");
                } else if (unit.getState() == UnitState.TO_EUROPE) {
                    locationName = Messages.message("goingToEurope");
                }
            
                ArrayList<Unit> unitList = locations.get(locationName);
                if (unitList == null) {
                    unitList = new ArrayList<Unit>();
                    locations.put(locationName, unitList);
                }
                unitList.add(unit);
                if (!(colonyNames.contains(locationName) || otherNames.contains(locationName))) {
                    otherNames.add(locationName);
                }
            }
        }
    }

    private void handleLocation(String location, boolean makeButton) {
        List<Unit> unitList = locations.get(location);
        JComponent component;
        if (makeButton) {
            JButton button = FreeColPanel.getLinkButton(location, null, location);
            button.addActionListener(this);
            component = button;
        } else {
            component = new JLabel(location);
        }
        reportPanel.add(component, "newline, span, split 2");
        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "growx");

        if (unitList == null) {
            reportPanel.add(new JLabel(Messages.message("none")), "sg");
        } else {
            Collections.sort(unitList, ReportPanel.getUnitTypeComparator());
            for (Unit unit : unitList) {
                UnitLabel unitLabel = new UnitLabel(unit, getCanvas(), true);
                if (unit.getDestination() != null) {
                    String destination = Messages.message(unit.getDestination().getLocationName());
                    unitLabel.setToolTipText("<html>" + unitLabel.getToolTipText() + "<br>" +
                                             Messages.message("goingTo", "%location%", destination) +
                                             "</html>");
                }
                
                unitLabel.setSelected(true);
                reportPanel.add(unitLabel, "newline, sg");
                for (Goods goods : unit.getGoodsList()) {
                    GoodsLabel goodsLabel = new GoodsLabel(goods, getCanvas());
                    reportPanel.add(goodsLabel);
                }
                for (Unit unitLoaded : unit.getUnitList()) {
                    UnitLabel unitLoadedLabel = new UnitLabel(unitLoaded, getCanvas(), true);
                    reportPanel.add(unitLoadedLabel);
                }
            }
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(750, 600);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
}
