

package net.sf.freecol.client.gui.panel;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.WorkLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class LabourData {
    private static final LocationData.Getter UNITS_IN_EUROPE_GETTER = new LocationData.Getter() {
        public LocationData getLocationData(UnitData unitData) {
            return unitData.unitsInEurope;
        }
    };
    private static final LocationData.Getter UNITS_AT_SEA_GETTER = new LocationData.Getter() {
        public LocationData getLocationData(UnitData unitData) {
            return unitData.unitsAtSea;
        }
    };
    private static final LocationData.Getter UNITS_ON_LAND_GETTER = new LocationData.Getter() {
        public LocationData getLocationData(UnitData unitData) {
            return unitData.unitsOnLand;
        }
    };

    public static class ProductionData {
        
        private int colonists;

        
        private int production;

        public void addProduction(int production) {
            colonists = getColonists() + 1;
            this.production = this.getProduction() + production;
        }

        public int getColonists() {
            return colonists;
        }

        public int getProduction() {
            return production;
        }

        private void add(ProductionData data) {
            colonists += data.colonists;
            production += data.production;
        }
    }

    public static class LocationData {

        public interface Getter {
            LocationData getLocationData(UnitData unitData);
        }

        
        private UnitData unitData;

        
        private boolean isTotal;

        
        private ProductionData workingProfessionals = new ProductionData();

        
        private int workingAmateurs;

        
        private ProductionData otherWorkingAmateurs = new ProductionData();

        
        private int netProduction;

        
        private int teachers;

        
        private int otherStudents;

        
        private String otherStudentsName;

        
        private int students;

        
        private int notWorking;

        public LocationData(UnitData unitData) {
            this(unitData, false);
        }

        public LocationData(UnitData unitData, boolean total) {
            this.unitData = unitData;
            isTotal = total;
        }

        public int getOtherStudents() {
            return otherStudents;
        }

        public String getOtherStudentsName() {
            return otherStudentsName;
        }

        public void addOtherStudent(String name) {
            otherStudents++;
            otherStudentsName = name;
        }

        public ProductionData getWorkingProfessionals() {
            return workingProfessionals;
        }

        public ProductionData getOtherWorkingAmateurs() {
            return otherWorkingAmateurs;
        }

        public int getNetProduction() {
            return netProduction;
        }

        public int getTotalColonists() {
            
            
            return workingAmateurs + workingProfessionals.getColonists() + notWorking +
                teachers + otherStudents - students;
        }

        public int getTotalProduction() {
            return workingProfessionals.getProduction() + otherWorkingAmateurs.getProduction();
        }

        
        public int getRowCount() {
            boolean isSummary = getUnitData().isSummary();

            int rows = 0;
            if (workingProfessionals.getColonists() > 0) rows++;
            if (workingAmateurs > 0) rows++;
            if (!isSummary && otherWorkingAmateurs.getColonists() > 0) rows++;
            if (teachers > 0) rows++;
            if (students > 0) rows++;
            if (!isSummary && otherStudents > 0) rows++;
            if (notWorking > 0) rows++;

            return rows;
        }

        public boolean isTraining() {
            return teachers > 0 || students > 0 || otherStudents > 0;
        }

        public int getWorkingAmateurs() {
            return workingAmateurs;
        }

        public int getTeachers() {
            return teachers;
        }

        public int getStudents() {
            return students;
        }

        public int getNotWorking() {
            return notWorking;
        }

        public UnitData getUnitData() {
            return unitData;
        }

        public boolean isTotal() {
            return isTotal;
        }

        private void add(LocationData data) {
            workingProfessionals.add(data.workingProfessionals);
            workingAmateurs += data.workingAmateurs;
            otherWorkingAmateurs.add(data.otherWorkingAmateurs);

            teachers += data.teachers;
            students += data.students;
            otherStudents += data.otherStudents;
            notWorking += data.notWorking;

            if (data.otherStudents > 0) {
                otherStudentsName = data.otherStudentsName;
            }
            
        }
    }

    public static class UnitData {

        private UnitType unitType;

        private boolean summary = false;

        
        private Map<Colony, LocationData> details = new LinkedHashMap<Colony, LocationData>();

        private LocationData total = new LocationData(this, true);
        private LocationData unitsAtSea = new LocationData(this);
        private LocationData unitsOnLand = new LocationData(this);
        private LocationData unitsInEurope = new LocationData(this);

        public UnitData(UnitType unitType) {
            this.unitType = unitType;

            if (unitType == null) {
                summary = true;
            }
        }

        
        private LocationData getLocationData(Colony colony) {
            LocationData colonyData = details.get(colony);
            if (colonyData == null) {
                colonyData = new LocationData(this);
                details.put(colony, colonyData);
            }
            return colonyData;
        }

        public String getUnitName() {
            if (isSummary()) {
                return null;
            }
            return Messages.message(unitType.getNameKey());
        }

        public boolean hasDetails() {
            return getTotal().getRowCount() > 0;
        }

        public int getUnitSummaryRowCount() {
            
            return Math.max(1, getTotal().getRowCount());
        }

        public UnitType getUnitType() {
            return unitType;
        }

        public LocationData getTotal() {
            return total;
        }

        public LocationData getUnitsAtSea() {
            return unitsAtSea;
        }

        public LocationData getUnitsOnLand() {
            return unitsOnLand;
        }

        public LocationData getUnitsInEurope() {
            return unitsInEurope;
        }

        public Map<Colony, LocationData> getDetails() {
            return details;
        }

        public boolean isSummary() {
            return summary;
        }

        public boolean showProduction() {
            return !summary && unitType.getExpertProduction() != null;
        }

        public boolean showNetProduction() {
            return showProduction() && unitType.getExpertProduction().isStorable();
        }

        public GoodsType getExpertProduction() {
            if (summary) {
                return null;
            }
            return getUnitType().getExpertProduction();
        }
    }

    private Map<GoodsType, UnitData> experts = new LinkedHashMap<GoodsType, UnitData>();

    private Map<String, UnitData> unitDataMap = new LinkedHashMap<String, UnitData>();

    private UnitData summary = new UnitData(null);

    private UnitData missionary;

    private UnitData pioneer;

    private UnitData soldier;

    private UnitData scout;

    public LabourData(FreeColClient client) {
        Specification spec = FreeCol.getSpecification();
        for (UnitType unitType : spec.getUnitTypeList()) {
            if (!unitType.isAvailableTo(client.getMyPlayer())) {
                continue;
            }
            GoodsType production = unitType.getExpertProduction();
            if (production != null) {
                experts.put(production, getUnitData(unitType));
            }

            if (unitType.hasAbility("model.ability.expertPioneer")) {
                pioneer = getUnitData(unitType);
            } else if (unitType.hasAbility("model.ability.expertSoldier")) {
                soldier = getUnitData(unitType);
            } else if (unitType.hasAbility("model.ability.expertScout")) {
                scout = getUnitData(unitType);
            } else if (unitType.hasAbility("model.ability.expertMissionary")) {
                missionary = getUnitData(unitType);
            }
        }

        gatherData(client.getMyPlayer());
    }

    private void gatherData(Player player) {
        List<UnitType> labourTypes = getLabourTypes(player);

        Iterator<Unit> units = player.getUnitIterator();
        while (units.hasNext()) {
            Unit unit = units.next();
            if (!labourTypes.contains(unit.getType())) {
                continue;
            }

            Location location = unit.getLocation();

            UnitData data = getUnitData(unit.getType());

            if (location instanceof WorkLocation) {
                incrementColonyCount(location.getColony(), unit, data);
            } else if (location instanceof Europe) {
                incrementOutsideWorker(data, unit, UNITS_IN_EUROPE_GETTER);
            } else if (location instanceof Tile && ((Tile) location).getSettlement() != null) {
                incrementColonyCount((Colony) ((Tile) location).getSettlement(), unit, data);
            } else if (location instanceof Unit) {
                incrementOutsideWorker(data, unit, UNITS_AT_SEA_GETTER);
            } else {
                incrementOutsideWorker(data, unit, UNITS_ON_LAND_GETTER);
            }
        }
        summarize();

        for (UnitData unitData : unitDataMap.values()) {
            LocationData total = unitData.getTotal();

            GoodsType expertProduction = unitData.getUnitType().getExpertProduction();
            if (expertProduction != null) {
                for (Colony colony : player.getColonies()) {
                    LocationData data = unitData.details.containsKey(colony) ? unitData.getLocationData(colony) : null;

                    int netProduction = colony.getProductionNetOf(expertProduction);
                    if (data != null) {
                        data.netProduction = netProduction;
                    }
                    total.netProduction += netProduction;
                }
            }
        }
    }

    public static List<UnitType> getLabourTypes(Player player) {
        List<UnitType> unitTypes = FreeCol.getSpecification().getUnitTypeList();
        ArrayList<UnitType> labourTypes = new ArrayList<UnitType>();
        for (UnitType unitType : unitTypes) {
            if (unitType.hasSkill() && unitType.isAvailableTo(player)) {
                labourTypes.add(unitType);
            }
        }
        return labourTypes;
    }

    private void summarize() {
        for (UnitData unitData : unitDataMap.values()) {
            summarize(unitData, UNITS_IN_EUROPE_GETTER);
            summarize(unitData, UNITS_AT_SEA_GETTER);
            summarize(unitData, UNITS_ON_LAND_GETTER);

            for (final Colony colony : unitData.details.keySet()) {
                summarize(unitData, new LocationData.Getter() {
                    public LocationData getLocationData(UnitData data) {
                        return data.getLocationData(colony);
                    }
                });
            }
        }
    }

    private void summarize(UnitData data, LocationData.Getter getter) {
        LocationData unitLocation = getter.getLocationData(data);
        LocationData summaryLocation = getter.getLocationData(summary);

        data.total.add(unitLocation);
        summaryLocation.add(unitLocation);
        summary.total.add(unitLocation);
    }

    private void incrementOutsideWorker(UnitData unitData, Unit unit, LocationData.Getter getter) {
        switch (unit.getRole()) {
            case DRAGOON:
            case SOLDIER:
                incrementOutsideWorker(unitData, unit, soldier, getter);
                break;
            case MISSIONARY:
                incrementOutsideWorker(unitData, unit, missionary, getter);
                break;
            case PIONEER:
                incrementOutsideWorker(unitData, unit, pioneer, getter);
                break;
            case SCOUT:
                incrementOutsideWorker(unitData, unit, scout, getter);
                break;
            default:
                getter.getLocationData(unitData).notWorking++;
                break;
        }
    }

    private void incrementOutsideWorker(UnitData expert, Unit unit, UnitData workingAs, LocationData.Getter getter) {
        if (unit.getType() == workingAs.unitType) {
            getter.getLocationData(expert).workingProfessionals.colonists++;
        } else {
            getter.getLocationData(expert).workingAmateurs++;

            getter.getLocationData(workingAs).otherWorkingAmateurs.colonists++;
        }
    }

    private void incrementColonyCount(final Colony colony, Unit unit, UnitData unitData) {

        Location location = unit.getLocation();
        if (!(location instanceof WorkLocation)) {
            incrementOutsideWorker(unitData, unit, new LocationData.Getter() {
                public LocationData getLocationData(UnitData data) {
                    return data.getLocationData(colony);
                }
            });
            return;
        }

        LocationData colonyData = unitData.getLocationData(colony);
        Unit teacher = unit.getTeacher();
        if (teacher != null) {
            colonyData.students++;

            UnitData learning = getUnitData(Unit.getUnitTypeTeaching(teacher.getType(), unit.getType()));
            learning.getLocationData(colony).addOtherStudent(unitData.getUnitName());
        }

        GoodsType currentlyWorking = unit.getWorkType();
        int production;
        if (location instanceof Building) {
            Building building = (Building) location;

            if (building.getType().hasAbility("model.ability.teach")) {
                colonyData.teachers++;
                return;
            }

            production = building.applyModifiers(building.getUnitProductivity(unit));
        } else {
            production = ((ColonyTile) location).getProductionOf(unit, currentlyWorking);
        }

        UnitData workingAs = experts.get(currentlyWorking);

        if (workingAs.getUnitType() == unit.getType()) {
            colonyData.getWorkingProfessionals().addProduction(production);
        } else {
            colonyData.workingAmateurs++;

            workingAs.getLocationData(colony).otherWorkingAmateurs.addProduction(production);
        }
    }

    
    public UnitData getUnitData(UnitType unitType) {
        UnitData data = unitDataMap.get(unitType.getId());
        if (data == null) {
            data = new UnitData(unitType);
            unitDataMap.put(unitType.getId(), data);
        }
        return data;
    }

    public UnitData getSummary() {
        return summary;
    }
}
