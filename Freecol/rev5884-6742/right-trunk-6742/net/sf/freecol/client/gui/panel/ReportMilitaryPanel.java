

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

import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.TypeCountMap;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;



public final class ReportMilitaryPanel extends ReportPanel {

    

    private static final UnitType defaultType =
        Specification.getSpecification().getUnitType("model.unit.freeColonist");

    private List<String> colonyNames;
    private List<String> otherNames;

    
    private TypeCountMap<UnitType> soldiers = new TypeCountMap<UnitType>();
    private TypeCountMap<UnitType> dragoons = new TypeCountMap<UnitType>();
    private TypeCountMap<UnitType> scouts = new TypeCountMap<UnitType>();
    private TypeCountMap<UnitType> others = new TypeCountMap<UnitType>();

    private Map<String, ArrayList<Unit>> locations;

    
    public ReportMilitaryPanel(Canvas parent) {

        super(parent, Messages.message("menuBar.report.military"));

        gatherData();

        Player player = getMyPlayer();

        reportPanel.setLayout(new MigLayout("fillx, wrap 12", "", ""));

        reportPanel.add(new JLabel(Messages.message(player.getNation().getRefNation().getId() + ".name")),
                        "span, split 2");
        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "growx");

        List<AbstractUnit> refUnits = getController().getREFUnits();
        if (refUnits != null) {
            for (AbstractUnit unit : refUnits) {
                if (!unit.getUnitType().hasAbility("model.ability.navalUnit")) {
                    reportPanel.add(createUnitTypeLabel(unit.getUnitType(), unit.getRole(), unit.getNumber()),
                                    "sg");
                }
            }
        }

        reportPanel.add(localizedLabel(StringTemplate.template("report.military.forces")
                                       .addStringTemplate("%nation%", player.getNationName())),
                        "newline, span, split 2");
        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "growx");

        List<AbstractUnit> units = new ArrayList<AbstractUnit>();
        List<AbstractUnit> scoutUnits = new ArrayList<AbstractUnit>();
        List<AbstractUnit> dragoonUnits = new ArrayList<AbstractUnit>();
        List<AbstractUnit> soldierUnits = new ArrayList<AbstractUnit>();
        for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
            if (unitType.isAvailableTo(player) &&
                !unitType.hasAbility("model.ability.navalUnit") && 
                (unitType.hasAbility("model.ability.expertSoldier") ||
                 unitType.getOffence() > 0)) {
                if (unitType.hasAbility("model.ability.canBeEquipped")) {
                    scoutUnits.add(new AbstractUnit(unitType, Role.SCOUT, scouts.getCount(unitType)));
                    dragoonUnits.add(new AbstractUnit(unitType, Role.DRAGOON, dragoons.getCount(unitType)));
                    soldierUnits.add(new AbstractUnit(unitType, Role.SOLDIER, soldiers.getCount(unitType)));
                } else {
                    units.add(new AbstractUnit(unitType, Role.DEFAULT, others.getCount(unitType)));
                }
            }
        }
        dragoonUnits.add(new AbstractUnit(defaultType, Role.DRAGOON, dragoons.getCount(defaultType)));
        soldierUnits.add(new AbstractUnit(defaultType, Role.SOLDIER, soldiers.getCount(defaultType)));
        scoutUnits.add(new  AbstractUnit(defaultType, Role.SCOUT, scouts.getCount(defaultType)));
        units.addAll(dragoonUnits);
        units.addAll(soldierUnits);
        units.addAll(scoutUnits);

        for (AbstractUnit unit : units) {
            reportPanel.add(createUnitTypeLabel(unit), "sg");
        }

        reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "newline, span, growx, wrap 40");

        
        for (String locationName : colonyNames) {
            handleLocation(locationName, true);
        }

        
        if (player.getEurope() != null) {
            String europeName = Messages.message(player.getEurope().getNameKey());
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

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(750, 600);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    private void gatherData() {
        Player player = getMyPlayer();
        locations = new HashMap<String, ArrayList<Unit>>();
        List<Colony> colonies = new ArrayList<Colony>(player.getColonies());
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());
        colonyNames = new ArrayList<String>();
        for (Colony colony : colonies) {
            colonyNames.add(colony.getName());
        }
        otherNames = new ArrayList<String>();
        if (player.getEurope() != null) {
            otherNames.add(Messages.message(player.getEurope().getNameKey()));
        }

        for (Unit unit : player.getUnits()) {
            if (unit.isOffensiveUnit() && !unit.isNaval()) {
                UnitType unitType = defaultType;
                if (unit.getType().getOffence() > 0 ||
                    unit.hasAbility("model.ability.expertSoldier")) {
                    unitType = unit.getType();
                }
                switch(unit.getRole()) {
                case DRAGOON:
                    dragoons.incrementCount(unitType, 1);
                    break;
                case SOLDIER:
                    soldiers.incrementCount(unitType, 1);
                    break;
                default:
                    others.incrementCount(unitType, 1);
                }
            } else {
                continue;
            }


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
                reportPanel.add(unitLabel, "sg");
            }
        }
    }

}
