


package net.sf.freecol.common.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;

import org.w3c.dom.Element;


public class ColonyTile extends FreeColGameObject implements WorkLocation, Ownable {

    private static final Logger logger = Logger.getLogger(ColonyTile.class.getName());

    public static final String UNIT_CHANGE = "UNIT_CHANGE";

    private Colony colony;
    private Tile workTile;
    private Unit unit;
    private boolean colonyCenterTile;

    
    public ColonyTile(Game game, Colony colony, Tile workTile) {
        super(game);

        this.colony = colony;
        this.workTile = workTile;

        if (colony.getTile() == workTile) {
            colonyCenterTile = true;
        } else {
            colonyCenterTile = false;
        }
    }

    
    public ColonyTile(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);

        readFromXML(in);
    }
    
    
    public ColonyTile(Game game, Element e) {
        super(game, e);

        readFromXMLElement(e);
    }

    
    public ColonyTile(Game game, String id) {
        super(game, id);
    }

    
    public StringTemplate getLocationName() {
        String name = getColony().getName();
        if (isColonyCenterTile()) {
            return StringTemplate.name(name);
        } else {
            return StringTemplate.template("nearLocation")
                .addName("%location%", name);
        }
    }
    
    
    public StringTemplate getLabel() {
        return workTile.getLabel();
    }


    
    public Player getOwner() {
        return colony.getOwner();
    }

    
    public void setOwner(Player p) {
        throw new UnsupportedOperationException();
    }

    
    public boolean isColonyCenterTile() {
        return colonyCenterTile;
    }


    
    public Tile getWorkTile() {
        return workTile;
    }


    
    public Tile getTile() {
        return colony.getTile();
    }

    
    public GoodsContainer getGoodsContainer() {
        return null;
    }
    

    
    public Unit getUnit() {
        return unit;
    }
    
    
    public Colony getColony() {
        return colony;
    }

    
    public void setUnit(Unit unit) {
        Unit oldUnit = getUnit();
        this.unit = unit;
        if (oldUnit != null) {
            GoodsType workType = oldUnit.getWorkType();
            firePropertyChange(workType.getId(), getProductionOf(oldUnit, workType), null);
        }
        if (unit != null) {
            GoodsType workType = unit.getWorkType();
            
            if (workType != null) {
                firePropertyChange(workType.getId(), null, getProductionOf(unit, workType));
            }
        }
    }


    
    public int getUnitCount() {
        return (getUnit() != null) ? 1 : 0;
    }

    
    public void relocateWorkers() {
        if (getUnit() != null) {
            for (WorkLocation wl : getColony().getWorkLocations()) {
                if (wl != this && wl.canAdd(getUnit())) {
                    getUnit().work(wl);
                    break;
                }
            }
        }
    }

    
    public boolean canBeWorked() {
        Player player = getOwner();

        
        
        Tile tile = getWorkTile();
        if (tile.getSettlement() != null
            || tile.getOccupyingUnit() != null
            || !(tile.isLand() || colony.hasAbility("model.ability.produceInWater"))
            || (player.isEuropean() && tile.hasLostCityRumour())) {
            return false;
        }

        
        Settlement settlement = tile.getOwningSettlement();
        if (settlement == null) {
            ; 
        } else if (settlement instanceof Colony) {
            
            Colony otherColony = (Colony) settlement;
            if (otherColony != colony) {
                if (otherColony.getOwner() != player
                    || otherColony.getColonyTile(tile).getUnit() != null) {
                    return false;
                }
            }
        } else if (settlement instanceof IndianSettlement) {
            
            if (player.getLandPrice(tile) > 0) {
                return false;
            }
        } else {
            throw new IllegalStateException("Bogus settlement");
        }

        return true;
    }


    
    public boolean canAdd(Locatable locatable) {
        if (!canBeWorked()) {
            return false;
        }
        if (!(locatable instanceof Unit)) {
            return false;
        }
        Unit unit = (Unit) locatable;
        if (!unit.getType().hasSkill()) {
            return false;
        }
        return getUnit() == null || unit == getUnit();
    }


    
    public void add(Locatable locatable) {
        if (isColonyCenterTile() || unit != null) {
            throw new IllegalStateException("Other unit present while adding a unit to ColonyTile:" + getId());
        }

        if (!canAdd(locatable)) {
            if (getWorkTile().getOwningSettlement() != null && getWorkTile().getOwningSettlement() != getColony()) {
                throw new IllegalArgumentException("Cannot add locatable to this location: another colony claims this land!");
            }
            throw new IllegalArgumentException("Cannot add locatable to this location: there is a unit here already!");
        }

        Unit u = (Unit) locatable;
        u.removeAllEquipment(false);
        u.setState(Unit.UnitState.IN_COLONY);

        
        Unit potentialTeacher = getColony().findTeacher(u);
        if (potentialTeacher != null) {
            potentialTeacher.setStudent(u);
            u.setTeacher(potentialTeacher);
        }

        setUnit(u);
    }
    

    
    public void remove(Locatable locatable) {
        if (getUnit() == null) {
            return;
        }

        if (!getUnit().equals(locatable)) {
            return;
        }

        Unit oldUnit = getUnit();
        oldUnit.setMovesLeft(0);
        setUnit(null);
    }

    public List<Unit> getUnitList() {
        if(getUnit() == null) {
            return new ArrayList<Unit>();
        } else {
            return Collections.singletonList(getUnit());
        }
    }

    public Iterator <Unit> getUnitIterator() {
        return getUnitList().iterator();
    }


    
    public boolean contains(Locatable locatable) {
        return (locatable == unit) ? true:false;
    }


    
    public Unit getFirstUnit() {
        return getUnit();
    }


    
    public Unit getLastUnit() {
        return getUnit();
    }

    
    public Unit getOccupyingUnit() {
        return workTile.getOccupyingUnit();
    }

    
    public boolean isOccupied() {
        return workTile.isOccupied();
    }
    
    
    public void newTurn() {
        if (isColonyCenterTile()) {
            produceGoodsCenterTile();
        } else if (getUnit() != null && !isOccupied()) {
            produceGoods();
            workTile.expendResource(getUnit().getWorkType(), getUnit().getType(), colony);
        }
    }

    private void produceGoods() {
        int amount = getProductionOf(getUnit().getWorkType());

        if (amount > 0) {
            colony.addGoods(getUnit().getWorkType(), amount);
            unit.modifyExperience(amount);
        }
    }

    private void produceGoodsCenterTile() {

        if (workTile.getType().getPrimaryGoods() != null) {
            GoodsType goodsType = workTile.getType().getPrimaryGoods().getType();
            colony.addGoods(goodsType, workTile.getPrimaryProduction());
        }
        if (workTile.getType().getSecondaryGoods() != null) {
            GoodsType goodsType = workTile.getType().getSecondaryGoods().getType();
            colony.addGoods(goodsType, workTile.getSecondaryProduction());
        }

    }
   
    
    public GoodsType getWorkType(Unit unit) {
        GoodsType workType = unit.getWorkType();
        int amount = getProductionOf(unit, workType);
        if (amount == 0) {
            List<GoodsType> farmedGoodsTypes = FreeCol.getSpecification().getFarmedGoodsTypeList();
            for(GoodsType farmedGoods : farmedGoodsTypes) {
                int newAmount = getProductionOf(unit, farmedGoods);
                if (newAmount > amount) {
                    amount = newAmount;
                    workType = farmedGoods;
                }
            }
        }
        return workType;
    }
    
    
    public int getProductionOf(GoodsType goodsType) {
        if (isColonyCenterTile()) {
            if (workTile.getType().getPrimaryGoods() != null
                && workTile.getType().getPrimaryGoods().getType() == goodsType) {
                return workTile.getPrimaryProduction();
            } else if (workTile.getType().getSecondaryGoods() != null
                       && workTile.getType().getSecondaryGoods().getType() == goodsType) {
                return workTile.getSecondaryProduction();
            } else {
                return 0;
            }
        } else if (goodsType == null) {
            throw new IllegalArgumentException("GoodsType must not be 'null'.");
        } else if (getUnit() == null) {
            return 0;
        } else if (goodsType.equals(getUnit().getWorkType())) {
            return getProductionOf(getUnit(), goodsType);
        } else {
            return 0;
        }
    }

    
    public Set<Modifier> getProductionModifiers(GoodsType goodsType, UnitType unitType) {
        if (goodsType == null) {
            throw new IllegalArgumentException("GoodsType must not be 'null'.");
        } else {
            Set<Modifier> result = new HashSet<Modifier>();
            if (getUnit() == null) {
                if (isColonyCenterTile() &&
                    (workTile.getType().isPrimaryGoodsType(goodsType)
                     || workTile.getType().isSecondaryGoodsType(goodsType))) {
                    result.addAll(workTile.getProductionBonus(goodsType, null));
                    result.addAll(getColony().getFeatureContainer().getModifierSet(goodsType.getId()));
                }
            } else if (goodsType.equals(getUnit().getWorkType())) {
                result.addAll(workTile.getProductionBonus(goodsType, unitType));
                result.addAll(getUnit().getModifierSet(goodsType.getId()));
            }
            return result;
        }
    }

    
    public int getProductionOf(Unit unit, GoodsType goodsType) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be 'null'.");
        } else if (workTile.isLand() || colony.hasAbility("model.ability.produceInWater")) {
            Set<Modifier> modifiers = workTile.getProductionBonus(goodsType, unit.getType());
            if (FeatureContainer.applyModifierSet(0f, getGame().getTurn(), modifiers) > 0) {
                modifiers.addAll(unit.getModifierSet(goodsType.getId()));
                modifiers.add(colony.getProductionModifier(goodsType));
                modifiers.addAll(getColony().getModifierSet(goodsType.getId()));
                List<Modifier> modifierList = new ArrayList<Modifier>(modifiers);
                Collections.sort(modifierList);
                return Math.max(1, (int) FeatureContainer.applyModifiers(0, getGame().getTurn(), modifierList));
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


    
    public List<FreeColGameObject> disposeList() {
        List<FreeColGameObject> objects = new ArrayList<FreeColGameObject>();
        if (unit != null) {
            objects.addAll(unit.disposeList());
        }
        objects.addAll(super.disposeList());
        return objects;
    }

    
    public void dispose() {
        disposeList();
    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());
        
        
        out.writeAttribute("ID", getId());
        out.writeAttribute("colony", colony.getId());
        out.writeAttribute("workTile", workTile.getId());

        writeFreeColGameObject(unit, out, player, showAll, toSavedGame);

        
        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        colony = getFreeColGameObject(in, "colony", Colony.class);
        workTile = getFreeColGameObject(in, "workTile", Tile.class);
        colonyCenterTile = (colony.getTile() == workTile);
        
        unit = null;
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(Unit.getXMLElementTagName())) {
                setUnit(updateFreeColGameObject(in, Unit.class));
            }
        }

    }

    
    @Override
    protected void toXMLPartialImpl(XMLStreamWriter out, String[] fields)
        throws XMLStreamException {
        toXMLPartialByClass(out, getClass(), fields);
    }

    
    @Override
    protected void readFromXMLPartialImpl(XMLStreamReader in)
        throws XMLStreamException {
        readFromXMLPartialByClass(in, getClass());
    }

    
    public String toString() {
        return "ColonyTile " + getWorkTile().getPosition().toString()
            + " in '" + getColony().getName() + "'";
    }

    
    public static String getXMLElementTagName() {
        return "colonyTile";
    }
}
