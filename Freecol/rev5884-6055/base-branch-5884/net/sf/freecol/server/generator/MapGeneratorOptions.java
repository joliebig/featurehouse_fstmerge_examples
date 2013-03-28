

package net.sf.freecol.server.generator;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.Specification;
import net.sf.freecol.common.option.OptionMap;

import org.w3c.dom.Element;


public class MapGeneratorOptions extends OptionMap {

    
    public static final String MAP_SIZE = "model.option.mapSize";

    
    public static final int MAP_SIZE_SMALL      = 0,
                            MAP_SIZE_MEDIUM     = 1,
                            MAP_SIZE_LARGE      = 2,
                            MAP_SIZE_VERY_LARGE = 3,
                            MAP_SIZE_HUGE       = 4;

    
    public static final String LAND_MASS = "model.option.landMass";

    
    public static final String RIVER_NUMBER = "model.option.riverNumber";

    
    public static final String MOUNTAIN_NUMBER = "model.option.mountainNumber";

    
    public static final String RUMOUR_NUMBER = "model.option.rumourNumber";

    
    public static final String SETTLEMENT_NUMBER = "model.option.settlementNumber";

    
    public static final String FOREST_NUMBER = "model.option.forestNumber";

    
    public static final String BONUS_NUMBER = "model.option.bonusNumber";

    
    public static final String HUMIDITY = "model.option.humidity";

    
    public static final String TEMPERATURE = "model.option.temperature";

    
    public static final int TEMPERATURE_COLD      = 0,
                            TEMPERATURE_CHILLY    = 1,
                            TEMPERATURE_TEMPERATE = 2,
                            TEMPERATURE_WARM      = 3,
                            TEMPERATURE_HOT       = 4;

    
    public static final String IMPORT_FILE = "model.option.importFile";

    
    public static final String IMPORT_TERRAIN = "model.option.importTerrain";

    
    public static final String IMPORT_BONUSES = "model.option.importBonuses";

    
    public static final String IMPORT_RUMOURS = "model.option.importRumours";

    
    public static final String IMPORT_SETTLEMENTS = "model.option.importSettlements";

    
    public static final String LAND_GEN_TYPE = "model.option.landGeneratorType";

    public static final int LAND_GEN_CLASSIC     = 0,
                            LAND_GEN_CONTINENT   = 1,
                            LAND_GEN_ARCHIPELAGO = 2,
                            LAND_GEN_ISLANDS     = 3;

    
    public MapGeneratorOptions() {
        super(getXMLElementTagName());
    }

    
    public MapGeneratorOptions(Element element) {
        super(element, getXMLElementTagName());
    }

    
    public MapGeneratorOptions(XMLStreamReader in) throws XMLStreamException {
        super(in, getXMLElementTagName());
    }

    
    protected void addDefaultOptions() {
        
        Specification spec = Specification.getSpecification();
        add(spec.getOptionGroup("mapGeneratorOptions.import"));

        
        Map<Integer, String> mapSizeValues = spec.getRangeOption(MAP_SIZE).getItemValues();
        Map<Integer, String> landMassValues = spec.getRangeOption(LAND_MASS).getItemValues();
        if(!mapSizeValues.get(0).substring(0,1).equals("<")) {
            for (int index : mapSizeValues.keySet()) {
                mapSizeValues.put(index, "<html><center>" + mapSizeValues.get(index) + "<br/>(" + getWidth(index)
                        + "\u" + getHeight(index) + ")</center></html>");
            }
            for (int index : landMassValues.keySet()) {
                landMassValues.put(index, "<html><center>" + landMassValues.get(index) + "<br/>(" + index
                        + "%)</center></html>");
            }
        }
        
        add(spec.getOptionGroup("mapGeneratorOptions.landGenerator"));
        add(spec.getOptionGroup("mapGeneratorOptions.terrainGenerator"));
    }

    
    public int getWidth() {
        return getWidth(getInteger(MAP_SIZE));
    }

    public static int getWidth(final int size) {
        switch (size) {
        case MAP_SIZE_SMALL:
            return 28;
        case MAP_SIZE_MEDIUM:
            return 40;
        case MAP_SIZE_LARGE:
            return 50;
        case MAP_SIZE_VERY_LARGE:
            return 60;
        case MAP_SIZE_HUGE:
            return 75;
        default:
            throw new IllegalStateException("Invalid map-size: " + size + ".");
        }
    }

    
    public int getHeight() {
        return getHeight(getInteger(MAP_SIZE));
    }

    public static int getHeight(int size) {
        switch (size) {
        case MAP_SIZE_SMALL:
            return 70;
        case MAP_SIZE_MEDIUM:
            return 100;
        case MAP_SIZE_LARGE:
            return 125;
        case MAP_SIZE_VERY_LARGE:
            return 150;
        case MAP_SIZE_HUGE:
            return 190;
        default:
            throw new IllegalStateException("Invalid map-size: " + size + ".");
        }
    }

    
    public int getLandMass() {
        return Specification.getSpecification().getRangeOption(LAND_MASS).getValue();
    }

    
    public int getLand() {
        return getWidth() * getHeight() * getLandMass() / 100;
    }

    
    public int getLandGeneratorType() {
        return Specification.getSpecification().getRangeOption(LAND_GEN_TYPE).getValue();
    }

    
    public int getNumberOfRivers() {
        return getLand()/Specification.getSpecification().getRangeOption(RIVER_NUMBER).getValue();
    }

    
    public int getNumberOfMountainTiles() {
        return getLand()/Specification.getSpecification().getRangeOption(MOUNTAIN_NUMBER).getValue();
    }

    
    public int getNumberOfRumours() {
        return getLand()/Specification.getSpecification().getRangeOption(RUMOUR_NUMBER).getValue();
    }

    
    public int getNumberOfSettlements() {
        return getLand()/Specification.getSpecification().getRangeOption(SETTLEMENT_NUMBER).getValue();
    }

    
    public int getPercentageOfForests() {
        return Specification.getSpecification().getRangeOption(FOREST_NUMBER).getValue();
    }

    
    public int getPercentageOfBonusTiles() {
        return Specification.getSpecification().getRangeOption(BONUS_NUMBER).getValue();
    }

    
    public int getDistLandHighSea() {
        final int size = getInteger(MAP_SIZE);
        switch (size) {
        case MAP_SIZE_SMALL:
            return 4;
        case MAP_SIZE_MEDIUM:
            return 4;
        case MAP_SIZE_LARGE:
            return 4;
        case MAP_SIZE_VERY_LARGE:
            return 4;
        case MAP_SIZE_HUGE:
            return 4;
        default:
            throw new IllegalStateException("Invalid map-size: " + size + ".");
        }
    }

    
    public int getMaxDistToEdge() {
        final int size = getInteger(MAP_SIZE);
        switch (size) {
        case MAP_SIZE_SMALL:
            return 7;
        case MAP_SIZE_MEDIUM:
            return 10;
        case MAP_SIZE_LARGE:
            return 12;
        case MAP_SIZE_VERY_LARGE:
            return 15;
        case MAP_SIZE_HUGE:
            return 20;
        default:
            throw new IllegalStateException("Invalid map-size: " + size + ".");
        }
    }

    
    public int getPrefDistToEdge() {
        final int size = getInteger(MAP_SIZE);
        switch (size) {
        case MAP_SIZE_SMALL:
            return 5;
        case MAP_SIZE_MEDIUM:
            return 5;
        case MAP_SIZE_LARGE:
            return 5;
        case MAP_SIZE_VERY_LARGE:
            return 5;
        case MAP_SIZE_HUGE:
            return 5;
        default:
            throw new IllegalStateException("Invalid map-size: " + size + ".");
        }
    }

    
    public int getHumidity() {
        return Specification.getSpecification().getRangeOption(HUMIDITY).getValue();
    }

    
    public int getTemperature() {
        return Specification.getSpecification().getRangeOption(TEMPERATURE).getValue();
    }

    protected boolean isCorrectTagName(String tagName) {
        return getXMLElementTagName().equals(tagName);
    }

    
    public static String getXMLElementTagName() {
        return "mapGeneratorOptions";
    }

}
