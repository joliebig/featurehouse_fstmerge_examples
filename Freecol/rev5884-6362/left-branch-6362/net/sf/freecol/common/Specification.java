

package net.sf.freecol.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.action.ImprovementActionType;
import net.sf.freecol.common.model.Ability;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.DifficultyLevel;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.EuropeanNationType;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianNationType;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.option.AbstractOption;
import net.sf.freecol.common.option.FileOption;
import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.OptionGroup;
import net.sf.freecol.common.option.RangeOption;
import net.sf.freecol.common.option.SelectOption;
import net.sf.freecol.common.option.StringOption;


public final class Specification {

    public static final FreeColGameObjectType MOVEMENT_PENALTY_SOURCE = 
        new FreeColGameObjectType("model.source.movementPenalty");
    public static final FreeColGameObjectType ARTILLERY_PENALTY_SOURCE =
        new FreeColGameObjectType("model.source.artilleryInTheOpen");
    public static final FreeColGameObjectType ATTACK_BONUS_SOURCE =
        new FreeColGameObjectType("model.source.attackBonus");
    public static final FreeColGameObjectType FORTIFICATION_BONUS_SOURCE =
        new FreeColGameObjectType("model.source.fortified");
    public static final FreeColGameObjectType INDIAN_RAID_BONUS_SOURCE =
        new FreeColGameObjectType("model.source.artilleryAgainstRaid");
    public static final FreeColGameObjectType BASE_OFFENCE_SOURCE =
        new FreeColGameObjectType("model.source.baseOffence");
    public static final FreeColGameObjectType BASE_DEFENCE_SOURCE =
        new FreeColGameObjectType("model.source.baseDefence");
    public static final FreeColGameObjectType CARGO_PENALTY_SOURCE = 
        new FreeColGameObjectType("model.source.cargoPenalty");
    public static final FreeColGameObjectType AMBUSH_BONUS_SOURCE = 
        new FreeColGameObjectType("model.source.ambushBonus");
    public static final FreeColGameObjectType IN_SETTLEMENT = 
        new FreeColGameObjectType("model.source.inSettlement");
    public static final FreeColGameObjectType IN_CAPITAL = 
        new FreeColGameObjectType("model.source.inCapital");

    
    public static final FreeColGameObjectType COLONY_GOODS_PARTY =
        new FreeColGameObjectType("model.monarch.colonyGoodsParty");


    
    protected static Specification specification;

    private static final Logger logger = Logger.getLogger(Specification.class.getName());

    private final Map<String, FreeColGameObjectType> allTypes;

    private final Map<String, AbstractOption> allOptions;

    private final Map<String, OptionGroup> allOptionGroups;

    private final Map<GoodsType, UnitType> experts;

    private final Map<String, List<Ability>> allAbilities;

    private final Map<String, List<Modifier>> allModifiers;

    private final List<BuildingType> buildingTypeList;

    private final List<GoodsType> goodsTypeList;
    private final List<GoodsType> farmedGoodsTypeList;
    private final List<GoodsType> foodGoodsTypeList;
    private final List<GoodsType> newWorldGoodsTypeList;
    private final List<GoodsType> libertyGoodsTypeList;
    private final List<GoodsType> immigrationGoodsTypeList;

    private final List<ResourceType> resourceTypeList;

    private final List<TileType> tileTypeList;

    private final List<TileImprovementType> tileImprovementTypeList;

    private final List<ImprovementActionType> improvementActionTypeList;

    private final List<UnitType> unitTypeList;
    private final List<UnitType> unitTypesTrainedInEurope;
    private final List<UnitType> unitTypesPurchasedInEurope;

    private final List<FoundingFather> foundingFathers;

    private final List<Nation> nations;
    private final List<Nation> europeanNations;
    private final List<Nation> REFNations;
    private final List<Nation> indianNations;

    private final List<NationType> nationTypes;
    private final List<EuropeanNationType> europeanNationTypes;
    private final List<EuropeanNationType> REFNationTypes;
    private final List<IndianNationType> indianNationTypes;

    private final List<EquipmentType> equipmentTypes;

    private final List<DifficultyLevel> difficultyLevels;

    private int storableTypes = 0;

    private boolean initialized = false;

    
    protected Specification(InputStream in) {
        logger.info("Initializing Specification");
        initialized = false;

        allTypes = new HashMap<String, FreeColGameObjectType>();
        allOptions = new HashMap<String, AbstractOption>();
        allOptionGroups = new HashMap<String, OptionGroup>();
        experts = new HashMap<GoodsType, UnitType>();

        allAbilities = new HashMap<String, List<Ability>>();
        allModifiers = new HashMap<String, List<Modifier>>();

        buildingTypeList = new ArrayList<BuildingType>();

        goodsTypeList = new ArrayList<GoodsType>();
        foodGoodsTypeList = new ArrayList<GoodsType>();
        farmedGoodsTypeList = new ArrayList<GoodsType>();
        newWorldGoodsTypeList = new ArrayList<GoodsType>();
        libertyGoodsTypeList = new ArrayList<GoodsType>();
        immigrationGoodsTypeList = new ArrayList<GoodsType>();

        resourceTypeList = new ArrayList<ResourceType>();
        tileTypeList = new ArrayList<TileType>();
        tileImprovementTypeList = new ArrayList<TileImprovementType>();
        improvementActionTypeList = new ArrayList<ImprovementActionType>();

        unitTypeList = new ArrayList<UnitType>();
        unitTypesPurchasedInEurope = new ArrayList<UnitType>();
        unitTypesTrainedInEurope = new ArrayList<UnitType>();

        foundingFathers = new ArrayList<FoundingFather>();

        nations = new ArrayList<Nation>();
        europeanNations = new ArrayList<Nation>();
        REFNations = new ArrayList<Nation>();
        indianNations = new ArrayList<Nation>();

        nationTypes = new ArrayList<NationType>();
        europeanNationTypes = new ArrayList<EuropeanNationType>();
        REFNationTypes = new ArrayList<EuropeanNationType>();
        indianNationTypes = new ArrayList<IndianNationType>();

        equipmentTypes = new ArrayList<EquipmentType>();
        difficultyLevels = new ArrayList<DifficultyLevel>();

        for (FreeColGameObjectType source : new FreeColGameObjectType[] {
                MOVEMENT_PENALTY_SOURCE,
                ARTILLERY_PENALTY_SOURCE,
                ATTACK_BONUS_SOURCE,
                FORTIFICATION_BONUS_SOURCE,
                INDIAN_RAID_BONUS_SOURCE,
                BASE_OFFENCE_SOURCE,
                BASE_DEFENCE_SOURCE, 
                CARGO_PENALTY_SOURCE,
                AMBUSH_BONUS_SOURCE,
                IN_SETTLEMENT,
                IN_CAPITAL,
                COLONY_GOODS_PARTY
            }) {
            allTypes.put(source.getId(), source);
        }

        try {
            XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(in);
            xsr.nextTag();
            while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                String childName = xsr.getLocalName();
                logger.finest("Found child named " + childName);

                if ("modifiers".equals(childName)) {

                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        Modifier modifier = new Modifier(xsr, this);
                        addModifier(modifier);
                    }

                } else if ("goods-types".equals(childName)) {

                    int goodsIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        GoodsType goodsType = new GoodsType(goodsIndex++);
                        goodsType.readFromXML(xsr, this);
                        goodsTypeList.add(goodsType);
                        allTypes.put(goodsType.getId(), goodsType);
                        if (goodsType.isFarmed()) {
                            farmedGoodsTypeList.add(goodsType);
                        }
                        if (goodsType.isFoodType()) {
                            foodGoodsTypeList.add(goodsType);
                        }
                        if (goodsType.isNewWorldGoodsType()) {
                            newWorldGoodsTypeList.add(goodsType);
                        }
                        if (goodsType.isLibertyGoodsType()) {
                            libertyGoodsTypeList.add(goodsType);
                        }
                        if (goodsType.isImmigrationGoodsType()) {
                            immigrationGoodsTypeList.add(goodsType);
                        }
                        if (goodsType.isStorable()) {
                            storableTypes++;
                        }
                    }

                } else if ("building-types".equals(childName)) {

                    int buildingIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        BuildingType buildingType = new BuildingType(buildingIndex++);
                        buildingType.readFromXML(xsr, this);
                        allTypes.put(buildingType.getId(), buildingType);
                        buildingTypeList.add(buildingType);
                    }

                } else if ("resource-types".equals(childName)) {

                    int resIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        ResourceType resourceType = new ResourceType(resIndex++);
                        resourceType.readFromXML(xsr, this);
                        allTypes.put(resourceType.getId(), resourceType);
                        resourceTypeList.add(resourceType);
                    }

                } else if ("tile-types".equals(childName)) {

                    int tileIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        TileType tileType = new TileType(tileIndex++);
                        tileType.readFromXML(xsr, this);
                        allTypes.put(tileType.getId(), tileType);
                        tileTypeList.add(tileType);
                    }

                } else if ("tileimprovement-types".equals(childName)) {

                    int impIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        TileImprovementType tileImprovementType = new TileImprovementType(impIndex++);
                        tileImprovementType.readFromXML(xsr, this);
                        allTypes.put(tileImprovementType.getId(), tileImprovementType);
                        tileImprovementTypeList.add(tileImprovementType);
                    }

                } else if ("improvementaction-types".equals(childName)) {

                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        ImprovementActionType impActionType = new ImprovementActionType();
                        impActionType.readFromXML(xsr, this);
                        allTypes.put(impActionType.getId(), impActionType);
                        improvementActionTypeList.add(impActionType);
                    }

                } else if ("unit-types".equals(childName)) {

                    int unitIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        UnitType unitType = getType(xsr.getAttributeValue(null, FreeColObject.ID_ATTRIBUTE_TAG),
                                                    UnitType.class);
                        if (unitType.getIndex() < 0) {
                            unitType.setIndex(unitIndex++);
                        }
                        unitType.readFromXML(xsr, this);
                        unitTypeList.add(unitType);
                        if (unitType.getExpertProduction() != null) {
                            experts.put(unitType.getExpertProduction(), unitType);
                        }
                        if (unitType.hasPrice()) {
                            if (unitType.getSkill() > 0) {
                                unitTypesTrainedInEurope.add(unitType);
                            } else if (!unitType.hasSkill()) {
                                unitTypesPurchasedInEurope.add(unitType);
                            }
                        }
                    }

                } else if ("founding-fathers".equals(childName)) {

                    int fatherIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        FoundingFather foundingFather = new FoundingFather(fatherIndex++);
                        foundingFather.readFromXML(xsr, this);
                        allTypes.put(foundingFather.getId(), foundingFather);
                        foundingFathers.add(foundingFather);
                    }

                } else if ("nation-types".equals(childName)) {

                    int nationIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        NationType nationType;
                        if ("european-nation-type".equals(xsr.getLocalName())) {
                            nationType = new EuropeanNationType(nationIndex++);
                            nationType.readFromXML(xsr, this);
                            if (nationType.isREF()) {
                                REFNationTypes.add((EuropeanNationType) nationType);
                            } else {
                                europeanNationTypes.add((EuropeanNationType) nationType);
                            }
                        } else {
                            nationType = new IndianNationType(nationIndex++);
                            nationType.readFromXML(xsr, this);
                            indianNationTypes.add((IndianNationType) nationType);
                        }
                        allTypes.put(nationType.getId(), nationType);
                        nationTypes.add(nationType);

                    }

                } else if ("nations".equals(childName)) {

                    int nationIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        Nation nation = getType(xsr.getAttributeValue(null, FreeColObject.ID_ATTRIBUTE_TAG),
                                                Nation.class);
                        if (nation.getIndex() < 0) {
                            nation.setIndex(nationIndex++);
                        }
                        nation.readFromXML(xsr, this);
                        nations.add(nation);

                        if (nation.getType().isEuropean()) {
                            if (nation.getType().isREF()) {
                                REFNations.add(nation);
                            } else {
                                europeanNations.add(nation);
                            }
                        } else {
                            indianNations.add(nation);
                        }
                    }

                } else if ("equipment-types".equals(childName)) {

                    int equipmentIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        EquipmentType equipmentType = new EquipmentType(equipmentIndex++);
                        equipmentType.readFromXML(xsr, this);
                        allTypes.put(equipmentType.getId(), equipmentType);
                        equipmentTypes.add(equipmentType);
                    }

                } else if ("difficultyLevels".equals(childName)) {

                    int levelIndex = 0;
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        DifficultyLevel level = new DifficultyLevel(levelIndex++);
                        level.readFromXML(xsr, this);
                        allTypes.put(level.getId(), level);
                        difficultyLevels.add(level);
                    }

                } else if ("options".equals(childName)) {

                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        AbstractOption option = null;
                        String optionType = xsr.getLocalName();
                        if (OptionGroup.getXMLElementTagName().equals(optionType)) {
                            option = new OptionGroup(xsr);
                        } else if (IntegerOption.getXMLElementTagName().equals(optionType)
                                   || "integer-option".equals(optionType)) {
                            option = new IntegerOption(xsr);
                        } else if (BooleanOption.getXMLElementTagName().equals(optionType)
                                   || "boolean-option".equals(optionType)) {
                            option = new BooleanOption(xsr);
                        } else if (StringOption.getXMLElementTagName().equals(optionType)
                                   || "string-option".equals(optionType)) {
                            option = new StringOption(xsr);
                        } else if (RangeOption.getXMLElementTagName().equals(optionType)
                                   || "range-option".equals(optionType)) {
                            option = new RangeOption(xsr);
                        } else if (SelectOption.getXMLElementTagName().equals(optionType)
                                   || "select-option".equals(optionType)) {
                            option = new SelectOption(xsr);
                        } else if (LanguageOption.getXMLElementTagName().equals(optionType)
                                   || "language-option".equals(optionType)) {
                            option = new LanguageOption(xsr);
                        } else if (FileOption.getXMLElementTagName().equals(optionType)
                                   || "file-option".equals(optionType)) {
                            option = new FileOption(xsr);
                        } else {
                            logger.finest("Parsing of " + optionType + " is not implemented yet");
                            xsr.nextTag();
                        }

                        
                        if (option != null) {
                            if(option instanceof OptionGroup) {
                                this.addOptionGroup((OptionGroup) option);
                            } else {
                                this.addAbstractOption(option);
                            }
                        }
                    }

                } else {
                    throw new RuntimeException("unexpected: " + childName);
                }
            }

            
            
            
            Goods.initialize(getGoodsTypeList(), numberOfGoodsTypes());

            initialized = true;
            logger.info("Specification initialization complete");
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new RuntimeException("Error parsing specification");
        }
    }

    
    

    
    public void addAbility(Ability ability) {
        String id = ability.getId();
        addAbility(id);
        allAbilities.get(id).add(ability);
    }

    
    public void addAbility(String id) {
        if (!allAbilities.containsKey(id)) {
            allAbilities.put(id, new ArrayList<Ability>());
        }
    }

    
    public List<Ability> getAbilities(String id) {
        return allAbilities.get(id);
    }

    
    public List<FreeColGameObjectType> getTypesProviding(String id, boolean value) {
        List<FreeColGameObjectType> result = new ArrayList<FreeColGameObjectType>();
        for (Ability ability : getAbilities(id)) {
            if (ability.getValue() == value && ability.getSource() != null) {
                result.add(ability.getSource());
            }
        }
        return result;
    }

    
    public void addModifier(Modifier modifier) {
        String id = modifier.getId();
        if (!allModifiers.containsKey(id)) {
            allModifiers.put(id, new ArrayList<Modifier>());
        }
        allModifiers.get(id).add(modifier);
    }

    
    public List<Modifier> getModifiers(String id) {
        return allModifiers.get(id);
    }

    
    public <T extends FreeColGameObjectType> T getType(String Id, Class<T> type)
        throws IllegalArgumentException {
        if (Id == null) {
            throw new IllegalArgumentException("Trying to retrieve FreeColGameObjectType" + " with ID 'null'.");
        } else if (allTypes.containsKey(Id)) {
            return type.cast(allTypes.get(Id));
        } else if (initialized) {
            throw new IllegalArgumentException("Undefined FreeColGameObjectType" + " with ID '" + Id + "'.");
        } else {
            
            try {
                T result = type.newInstance();
                allTypes.put(Id, result);
                return result;
            } catch(Exception e) {
                logger.warning(e.toString());
                return null;
            }
        }
    }

    public FreeColGameObjectType getType(String Id) throws IllegalArgumentException {
        return getType(Id, FreeColGameObjectType.class);
    }


    
    public <T extends FreeColGameObjectType> List<T>
                      getTypesWithAbility(Class<T> resultType, String... abilities) {
        ArrayList<T> result = new ArrayList<T>();
        for (FreeColGameObjectType type : allTypes.values()) {
            if (resultType.isInstance(type)) {
                for (String ability : abilities) {
                    if (type.hasAbility(ability)) {
                        result.add(resultType.cast(type));
                        break;
                    }
                }
            }
        }
        return result;
    }

    
    public boolean hasOption(String Id) {
        return Id != null && allOptions.containsKey(Id);
    }

    
    public AbstractOption getOption(String Id) throws IllegalArgumentException {
        if (Id == null) {
            throw new IllegalArgumentException("Trying to retrieve AbstractOption" + " with ID 'null'.");
        } else if (!allOptions.containsKey(Id)) {
            throw new IllegalArgumentException("Trying to retrieve AbstractOption" + " with ID '" + Id
                    + "' returned 'null'.");
        } else {
            return allOptions.get(Id);
        }
    }

    
    public OptionGroup getOptionGroup(String Id) throws IllegalArgumentException {
        if (Id == null) {
            throw new IllegalArgumentException("Trying to retrieve OptionGroup" + " with ID 'null'.");
        } else if (!allOptionGroups.containsKey(Id)) {
            throw new IllegalArgumentException("Trying to retrieve OptionGroup" + " with ID '" + Id
                    + "' returned 'null'.");
        } else {
            return allOptionGroups.get(Id);
        }
    }

    
    public void addOptionGroup(OptionGroup optionGroup) {
        
        allOptionGroups.put(optionGroup.getId(), optionGroup);

        
        Iterator<Option> iter = optionGroup.iterator();

        while(iter.hasNext()){
            Option option = iter.next();
            addAbstractOption((AbstractOption) option);
        }
    }

    
    public void addAbstractOption(AbstractOption abstractOption) {
        
        allOptions.put(abstractOption.getId(), abstractOption);
    }


    
    public IntegerOption getIntegerOption(String Id) {
        return (IntegerOption) getOption(Id);
    }

    
    public RangeOption getRangeOption(String Id) {
        return (RangeOption) getOption(Id);
    }

    
    public BooleanOption getBooleanOption(String Id) {
        return (BooleanOption) getOption(Id);
    }

    
    public StringOption getStringOption(String Id) {
        return (StringOption) getOption(Id);
    }

    
    public List<BuildingType> getBuildingTypeList() {
        return buildingTypeList;
    }

    
    public int numberOfBuildingTypes() {
        return buildingTypeList.size();
    }

    
    public BuildingType getBuildingType(int buildingTypeIndex) {
        return buildingTypeList.get(buildingTypeIndex);
    }

    public BuildingType getBuildingType(String id) {
        return getType(id, BuildingType.class);
    }

    
    public List<GoodsType> getGoodsTypeList() {
        return goodsTypeList;
    }

    
    public int numberOfGoodsTypes() {
        return goodsTypeList.size();
    }

    public int numberOfStoredGoodsTypes() {
        return storableTypes;
    }

    public List<GoodsType> getFarmedGoodsTypeList() {
        return farmedGoodsTypeList;
    }

    public List<GoodsType> getNewWorldGoodsTypeList() {
        return newWorldGoodsTypeList;
    }

    public List<GoodsType> getLibertyGoodsTypeList() {
        return libertyGoodsTypeList;
    }

    public List<GoodsType> getImmigrationGoodsTypeList() {
        return immigrationGoodsTypeList;
    }

    
    public int numberOfFarmedGoodsTypes() {
        return farmedGoodsTypeList.size();
    }

    
    public GoodsType getGoodsType(String id) {
        return getType(id, GoodsType.class);
    }

    public List<GoodsType> getGoodsFood() {
        return foodGoodsTypeList;
    }

    
    public List<ResourceType> getResourceTypeList() {
        return resourceTypeList;
    }

    public int numberOfResourceTypes() {
        return resourceTypeList.size();
    }

    public ResourceType getResourceType(String id) {
        return getType(id, ResourceType.class);
    }

    
    public List<TileType> getTileTypeList() {
        return tileTypeList;
    }

    public int numberOfTileTypes() {
        return tileTypeList.size();
    }

    public TileType getTileType(String id) {
        return getType(id, TileType.class);
    }

    
    public List<TileImprovementType> getTileImprovementTypeList() {
        return tileImprovementTypeList;
    }

    public TileImprovementType getTileImprovementType(String id) {
        return getType(id, TileImprovementType.class);
    }

    
    public List<ImprovementActionType> getImprovementActionTypeList() {
        return improvementActionTypeList;
    }

    public ImprovementActionType getImprovementActionType(String id) {
        return getType(id, ImprovementActionType.class);
    }

    
    public List<UnitType> getUnitTypeList() {
        return unitTypeList;
    }

    public int numberOfUnitTypes() {
        return unitTypeList.size();
    }

    public UnitType getUnitType(String id) {
        return getType(id, UnitType.class);
    }

    public UnitType getExpertForProducing(GoodsType goodsType) {
        return experts.get(goodsType);
    }

    
    public List<UnitType> getUnitTypesWithAbility(String... abilities) {
        return getTypesWithAbility(UnitType.class, abilities);
    }

    
    public List<UnitType> getUnitTypesTrainedInEurope() {
        return unitTypesTrainedInEurope;
    }

    
    public List<UnitType> getUnitTypesPurchasedInEurope() {
        return unitTypesPurchasedInEurope;
    }

    

    public List<FoundingFather> getFoundingFathers() {
        return foundingFathers;
    }

    public int numberOfFoundingFathers() {
        return foundingFathers.size();
    }

    public FoundingFather getFoundingFather(String id) {
        return getType(id, FoundingFather.class);
    }

    

    public List<NationType> getNationTypes() {
        return nationTypes;
    }

    public List<EuropeanNationType> getEuropeanNationTypes() {
        return europeanNationTypes;
    }

    public List<EuropeanNationType> getREFNationTypes() {
        return REFNationTypes;
    }

    public List<IndianNationType> getIndianNationTypes() {
        return indianNationTypes;
    }

    public int numberOfNationTypes() {
        return nationTypes.size();
    }

    public NationType getNationType(String id) {
        return getType(id, NationType.class);
    }

    

    public List<Nation> getNations() {
        return nations;
    }

    public Nation getNation(String id) {
        return getType(id, Nation.class);
    }

    public List<Nation> getEuropeanNations() {
        return europeanNations;
    }

    public List<Nation> getIndianNations() {
        return indianNations;
    }

    public List<Nation> getREFNations() {
        return REFNations;
    }

    
    public List<EquipmentType> getEquipmentTypeList() {
        return equipmentTypes;
    }

    public EquipmentType getEquipmentType(String id) {
        return getType(id, EquipmentType.class);
    }

    
    public List<DifficultyLevel> getDifficultyLevels() {
        return difficultyLevels;
    }

    
    public DifficultyLevel getDifficultyLevel(String id) {
        return getType(id, DifficultyLevel.class);
    }

    
    public DifficultyLevel getDifficultyLevel(int level) {
        return difficultyLevels.get(level);
    }

    
    public void applyDifficultyLevel(int difficultyLevel) {
        for (String key : difficultyLevels.get(difficultyLevel).getOptions().keySet()) {
            allOptions.put(key, difficultyLevels.get(difficultyLevel).getOptions().get(key));
        }
        
        if (FreeCol.isInDebugMode()) {
            getIntegerOption(GameOptions.STARTING_MONEY).setValue(10000);
        }
    }

    
    
    


    
    public static void createSpecification(InputStream is) {
        specification = new Specification(is);
    }

    
    public static Specification getSpecification() {
        if (specification == null) {
            try {
                specification = new Specification(new FileInputStream("data/freecol/specification.xml"));
                logger.info("getSpecification()");
            } catch (Exception e) {
            }
        }
        return specification;
    }

    
    public <T extends FreeColGameObjectType> T getType(XMLStreamReader in, String attributeName,
                                                       Class<T> returnClass, T defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return getType(attributeString, returnClass);
        } else {
            return defaultValue;
        }
    }
}
