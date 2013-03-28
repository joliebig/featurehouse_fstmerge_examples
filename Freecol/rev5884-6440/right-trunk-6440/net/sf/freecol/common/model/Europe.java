

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
import org.w3c.dom.Element;


public final class Europe extends FreeColGameObject implements Location, Ownable, Named {

    private static final Logger logger = Logger.getLogger(Europe.class.getName());

    private static final int RECRUIT_PRICE_INITIAL = 200;

    private static final int LOWER_CAP_INITIAL = 80;

    public static final String UNITS_TAG_NAME = "units";

    public static final String UNIT_CHANGE = "unitChange";

    
    private UnitType[] recruitables = { null, null, null };
    public static final int RECRUIT_COUNT = 3;

    private java.util.Map<UnitType, Integer> unitPrices = new HashMap<UnitType, Integer>();

    private int recruitPrice;

    private int recruitLowerCap;

    
    private List<Unit> units = Collections.emptyList();

    private Player owner;


    
    public Europe(Game game, Player owner) {
        super(game);
        this.owner = owner;

        recruitPrice = RECRUIT_PRICE_INITIAL;
        recruitLowerCap = LOWER_CAP_INITIAL;
    }

    
    public Europe(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        readFromXML(in);
    }

    
    public Europe(Game game, Element e) {
        super(game, e);

        readFromXMLElement(e);
    }

    
    public Europe(Game game, String id) {
        super(game, id);
    }

    
    public boolean canBuildEquipment(EquipmentType equipmentType) {
        for (AbstractGoods requiredGoods : equipmentType.getGoodsRequired()) {
            GoodsType goodsType = requiredGoods.getType();
            if (!(getOwner().canTrade(goodsType) &&
                  getOwner().getGold() >= getOwner().getMarket().getBidPrice(goodsType, requiredGoods.getAmount()))) {
                return false;
            }
        }
        return true;
    }


    
    public boolean recruitablesDiffer() {
        return !(recruitables[0].equals(recruitables[1]) && recruitables[0].equals(recruitables[2]));
    }

    
    public UnitType getRecruitable(int slot) {
        if ((slot >= 0) && (slot < RECRUIT_COUNT)) {
            return recruitables[slot];
        }
        throw new IllegalArgumentException("Wrong recruitement slot: " + slot);
    }

    
    public void setRecruitable(int slot, UnitType type) {
        
        if (slot >= 0 && slot < RECRUIT_COUNT) {
            recruitables[slot] = type;
        } else {
            logger.warning("setRecruitable: invalid slot(" + slot + ") given.");
        }
    }

    
    public void recruit(int slot, Unit unit, UnitType newRecruitable) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        } else if (getRecruitPrice() > unit.getOwner().getGold()) {
            throw new IllegalStateException("Not enough gold to recruit " + unit.getName() + ".");
        }

        unit.getOwner().modifyGold(-getRecruitPrice());
        incrementRecruitPrice();
        unit.setLocation(this);
        firePropertyChange(UNIT_CHANGE, getUnitCount() - 1, getUnitCount());
        unit.getOwner().updateImmigrationRequired();
        unit.getOwner().reduceImmigration();

        setRecruitable(slot, newRecruitable);
    }

    
    public Tile getTile() {
        return null;
    }

    
    public Colony getColony() {
        return null;
    }

    
    public void add(Locatable locatable) {
        if (!(locatable instanceof Unit)) {
            throw new IllegalArgumentException("Only units can be added to Europe.");
        } else if (!units.contains(locatable)) {
            if (units.equals(Collections.emptyList())) {
                units = new ArrayList<Unit>();
            }
            Unit newUnit = (Unit) locatable;
            units.add(newUnit);
            if (!(newUnit.getState() == Unit.UnitState.TO_EUROPE
                  || newUnit.getState() == Unit.UnitState.TO_AMERICA)) {
                newUnit.setState(Unit.UnitState.SENTRY);
            }
            firePropertyChange(UNIT_CHANGE, getUnitCount() - 1, getUnitCount());
        }
    }

    
    public void remove(Locatable locatable) {
        if (locatable instanceof Unit) {
            units.remove(locatable);
            firePropertyChange(UNIT_CHANGE, getUnitCount() + 1, getUnitCount());
        } else {
            logger.warning("Tried to remove an unrecognized 'Locatable' from a europe.");
        }
    }

    
    public boolean contains(Locatable locatable) {
        if (locatable instanceof Unit) {
            return units.contains(locatable);
        }

        return false;
    }

    public GoodsContainer getGoodsContainer() {
        return null;
    }

    
    public boolean canAdd(Locatable locatable) {
        return true;
    }

    
    public int getUnitCount() {
        return units.size();
    }

    
    public List<Unit> getUnitList() {
        return units;
    }

    
    public void disposeUnitList() {
        while (!units.isEmpty()) {
            Unit unit = units.remove(0);
            unit.dispose();
        }
        units = null;
    }

    
    public Iterator<Unit> getUnitIterator() {
        return units.iterator();
    }

    
    public Unit getFirstUnit() {
        if (units.isEmpty()) {
            return null;
        } else {
            return units.get(0);
        }
    }

    
    public Unit getLastUnit() {
        if (units.isEmpty()) {
            return null;
        } else {
            return units.get(units.size() - 1);
        }
    }

    
    public int getUnitPrice(UnitType unitType) {
        Integer price = unitPrices.get(unitType);
        if (price != null) {
            return price.intValue();
        } else {
            return unitType.getPrice();
        }
    }

    
    public void train(Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        }

        int price = getUnitPrice(unit.getType());
        if (price <= 0) {
            throw new IllegalArgumentException("Unit price must be a positive integer.");
        } else if (getUnitPrice(unit.getType()) > unit.getOwner().getGold()) {
            throw new IllegalStateException("Not enough gold to train " + unit.getName() + ".");
        }

        unit.getOwner().modifyGold(-price);
        increasePrice(unit, price);
        unit.setLocation(this);
        firePropertyChange(UNIT_CHANGE, getUnitCount() - 1, getUnitCount());
    }

    
    private void increasePrice(Unit unit, int price) {
        Specification spec = Specification.getSpecification();
        String baseOption = "model.option.priceIncreasePerType";
        String name = unit.getType().getId().substring(unit.getType().getId().lastIndexOf('.'));
        String option = (spec.getBooleanOption(baseOption).getValue()) 
            ? "model.option.priceIncrease" + name
            : "model.option.priceIncrease";
        int increase = (spec.hasOption(option)) 
            ? spec.getIntegerOption(option).getValue()
            : 0;
        if (increase != 0) {
            unitPrices.put(unit.getType(), new Integer(price + increase));
        }
    }

    
    public int getRecruitPrice() {
        int required = owner.getImmigrationRequired();
        int immigration = owner.getImmigration();
        int difference = Math.max(required - immigration, 0);
        return Math.max((recruitPrice * difference) / required, recruitLowerCap);
    }

    private void incrementRecruitPrice() {
        recruitPrice += Specification.getSpecification().getIntegerOption("model.option.recruitPriceIncrease").getValue();
        recruitLowerCap += Specification.getSpecification().getIntegerOption("model.option.lowerCapIncrease")
                .getValue();
    }

    
    public Player getOwner() {
        return owner;
    }

    
    public void setOwner(Player p) {
        throw new UnsupportedOperationException();
    }

    
    public void newTurn() {
        
        for (Unit unit : getUnitList()) {
            if (unit.isNaval() && unit.isUnderRepair()) {
                unit.setHitpoints(unit.getHitpoints() + 1);
                if (!unit.isUnderRepair()) {
                    addModelMessage(new ModelMessage("model.unit.shipRepaired", this, unit)
                                    .addName("%unit%", unit.getName())
                                    .addStringTemplate("%repairLocation%", getLocationName()));
                }
            }
        }
    }

    
    public StringTemplate getLocationName() {
        return StringTemplate.name(getName());
    }

    
    public String getName() {
        return getOwner().getEuropeName();
    }

    
    public String toString() {
        return "Europe";
    }

    private void unitsToXML(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
            throws XMLStreamException {
        if (!units.isEmpty()) {
            out.writeStartElement(UNITS_TAG_NAME);
            for (Unit unit : units) {
                unit.toXML(out, player, showAll, toSavedGame);
            }
            out.writeEndElement();
        }
    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
            throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute(ID_ATTRIBUTE, getId());
        for (int index = 0; index < recruitables.length; index++) {
            if (recruitables[index] != null) {
                out.writeAttribute("recruit" + index, recruitables[index].getId());
            }
        }
        out.writeAttribute("recruitPrice", Integer.toString(recruitPrice));
        out.writeAttribute("recruitLowerCap", Integer.toString(recruitLowerCap));
        out.writeAttribute("owner", owner.getId());

        for (Entry<UnitType, Integer> entry : unitPrices.entrySet()) {
            out.writeStartElement("unitPrice");
            out.writeAttribute("unitType", entry.getKey().getId());
            out.writeAttribute("price", entry.getValue().toString());
            out.writeEndElement();
        }

        unitsToXML(out, player, showAll, toSavedGame);

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE));

        Specification spec = FreeCol.getSpecification();
        for (int index = 0; index < recruitables.length; index++) {
            String unitTypeId = in.getAttributeValue(null, "recruit" + index);
            if (unitTypeId != null) {
                recruitables[index] = spec.getUnitType(unitTypeId);
            }
        }

        owner = getFreeColGameObject(in, "owner", Player.class);

        recruitPrice = getAttribute(in, "recruitPrice", RECRUIT_PRICE_INITIAL);
        recruitLowerCap = getAttribute(in, "recruitLowerCap", LOWER_CAP_INITIAL);

        units.clear();
        unitPrices.clear();
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(UNITS_TAG_NAME)) {
                units = new ArrayList<Unit>();
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    if (in.getLocalName().equals(Unit.getXMLElementTagName())) {
                        units.add(updateFreeColGameObject(in, Unit.class));
                    }
                }
            } else if (in.getLocalName().equals("unitPrice")) {
                String unitTypeId = in.getAttributeValue(null, "unitType");
                Integer price = new Integer(in.getAttributeValue(null, "price"));
                unitPrices.put(spec.getUnitType(unitTypeId), price);
                in.nextTag(); 
            }
        }
    }

    
    public static String getXMLElementTagName() {
        return "europe";
    }

}
