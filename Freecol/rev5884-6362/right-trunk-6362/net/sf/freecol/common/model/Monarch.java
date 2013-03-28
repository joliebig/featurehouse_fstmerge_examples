


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.util.RandomChoice;

import org.w3c.dom.Element;


public final class Monarch extends FreeColGameObject {

    
    private String name;

    
    private Player player;

    private static final Logger logger = Logger.getLogger(Monarch.class.getName());

    private static final EquipmentType muskets = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
    private static final EquipmentType horses = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");

    
    public static enum MonarchAction {
        NO_ACTION,
            RAISE_TAX,
            ADD_TO_REF,
            DECLARE_WAR,
            SUPPORT_SEA,
            SUPPORT_LAND,
            OFFER_MERCENARIES,
            LOWER_TAX,
            WAIVE_TAX,
            ADD_UNITS }

    
    int spaceRequired;

    
    int capacity;

    
    private List<AbstractUnit> landUnits = new ArrayList<AbstractUnit>();

    
    private List<AbstractUnit> navalUnits = new ArrayList<AbstractUnit>();

    
    public static final int MINIMUM_PRICE = 100;

    
    public static final int MAXIMUM_TAX_RATE = 75;

    
    public static final int MINIMUM_TAX_RATE = 20;
    
    
    private boolean supportSea = false;

    
    public Monarch(Game game, Player player, String name) {
        super(game);

        if (player == null) {
            throw new IllegalStateException("player == null");
        }

        this.player = player;
        this.name = name;

        
        int number = game.getGameOptions().getInteger(GameOptions.DIFFICULTY) * 2 + 3;

        for (UnitType unitType : FreeCol.getSpecification().getUnitTypeList()) {
            if (unitType.hasAbility("model.ability.refUnit")) {
                if (unitType.hasAbility("model.ability.navalUnit")) {
                    navalUnits.add(new AbstractUnit(unitType, Role.DEFAULT, number));
                    if (unitType.canCarryUnits()) {
                        capacity += unitType.getSpace() * number;
                    }
                } else if (unitType.hasAbility("model.ability.canBeEquipped")) {
                    landUnits.add(new AbstractUnit(unitType, Role.SOLDIER, number));
                    landUnits.add(new AbstractUnit(unitType, Role.DRAGOON, number));
                    spaceRequired += unitType.getSpaceTaken() * 2 * number;
                } else {
                    landUnits.add(new AbstractUnit(unitType, Role.DEFAULT, number));
                    spaceRequired += unitType.getSpaceTaken() * number;
                }
            }
        }
    }


    
    public Monarch(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXML(in);
    }

    
    public Monarch(Game game, Element e) {
        super(game, e);
        readFromXMLElement(e);
    }

    
    public Monarch(Game game, String id) {
        super(game, id);
    }


    public String getName() {
        return name;
    }

    
    public MonarchAction getAction() {
        
        List<RandomChoice<MonarchAction>> choices = getActionChoices();
        if (choices == null) {
            return MonarchAction.NO_ACTION;
        } else {
            return RandomChoice.getWeightedRandom(getGame().getModelController().getPseudoRandom(),
                                                  choices);
        }                                              
    }

    public List<RandomChoice<MonarchAction>> getActionChoices() {
        
        int dx = getGame().getGameOptions().getInteger(GameOptions.DIFFICULTY) + 1; 
        int turn = getGame().getTurn().getNumber();
        int grace = (6 - dx) * 10; 

        
        
        if (turn < grace || player.getPlayerType() != PlayerType.COLONIAL) {
            return null;
        }

        boolean canDeclareWar = false;
        boolean atWar = false;
        
        if (!player.hasAbility("model.ability.ignoreEuropeanWars")) {
            for (Player enemy : getGame().getPlayers()) {
                if (!enemy.isEuropean() || enemy.isREF()) {
                    continue;
                }
                switch (player.getStance(enemy)) {
                case UNCONTACTED:
                    break;
                case WAR:
                    atWar = true;
                    break;
                case PEACE: case CEASE_FIRE:
                    canDeclareWar = true;
                    break;
                case ALLIANCE:
                    
                    break;
                }
            }
        }

        
        List<RandomChoice<MonarchAction>> choices = new ArrayList<RandomChoice<MonarchAction>>();

        
        
        choices.add(new RandomChoice<MonarchAction>(MonarchAction.NO_ACTION, Math.max(200 - turn, 100)));

        if (player.getTax() < MAXIMUM_TAX_RATE) {
            choices.add(new RandomChoice<MonarchAction>(MonarchAction.RAISE_TAX, 10 + dx));
        }

        choices.add(new RandomChoice<MonarchAction>(MonarchAction.ADD_TO_REF, 10 + dx));

        if (canDeclareWar) {
            choices.add(new RandomChoice<MonarchAction>(MonarchAction.DECLARE_WAR, 5 + dx));
        }

        
        if (player.hasBeenAttackedByPrivateers() && !supportSea) {
            choices.add(new RandomChoice<MonarchAction>(MonarchAction.SUPPORT_SEA, 6 - dx));
        }

        if (atWar) {
            
            
            if (player.getGold() > MINIMUM_PRICE) {
                choices.add(new RandomChoice<MonarchAction>(MonarchAction.OFFER_MERCENARIES, 6 - dx));
            }
        }

        if (player.getTax() > MINIMUM_TAX_RATE + 10) {
            
            
            
            choices.add(new RandomChoice<MonarchAction>(MonarchAction.LOWER_TAX, 10 - dx));
        }

        return choices;
    }

    
    public List<AbstractUnit> getREF() {
        List<AbstractUnit> result = new ArrayList<AbstractUnit>(landUnits);
        result.addAll(navalUnits);
        return result;
    }

    
    public List<AbstractUnit> getNavalUnits() {
        return navalUnits;
    }

    
    public List<AbstractUnit> getLandUnits() {
        return landUnits;
    }

    
    public int getNewTax(MonarchAction taxChange) {
    	
    	int newTax = 110; 
        int adjustment = 0;
        
        switch(taxChange){
        case RAISE_TAX:
            int turn = getGame().getTurn().getNumber();
            adjustment = (6 - player.getDifficulty().getIndex()) * 10; 
            
            int increase = getGame().getModelController().getPseudoRandom().nextInt(5 + turn/adjustment) + 1;
            newTax = player.getTax() + increase;
            newTax = Math.min(newTax, MAXIMUM_TAX_RATE);
            break;
        case LOWER_TAX:
            adjustment = 10 - player.getDifficulty().getIndex(); 
            int decrease = getGame().getModelController().getPseudoRandom().nextInt(adjustment) + 1;
            newTax = player.getTax() - decrease;
            newTax = Math.max(newTax, MINIMUM_TAX_RATE);
            break;
        default:
            logger.warning("Wrong tax change type");
            return newTax;
        }
        
        return newTax;
    }

    
    public List<AbstractUnit> getMercenaries() {
        List<AbstractUnit> mercenaries = new ArrayList<AbstractUnit>();
        List<UnitType> unitTypes = new ArrayList<UnitType>();

        for (UnitType unitType : FreeCol.getSpecification().getUnitTypeList()) {
            if (unitType.hasAbility("model.ability.mercenaryUnit")) {
                unitTypes.add(unitType);
            }
        }
        int gold = player.getGold();
        int price = 0;
        int limit = unitTypes.size();
        UnitType unitType = null;
        for (int count = 0; count < limit; count++) {
            int index = getGame().getModelController().getPseudoRandom().nextInt(unitTypes.size());
            unitType = unitTypes.get(index);
            if (unitType.hasAbility("model.ability.canBeEquipped")) {
                int newPrice = getPrice(unitType, Role.DRAGOON);
                for (int number = 3; number > 0; number--) {
                    if (price + newPrice * number <= gold) {
                        mercenaries.add(new AbstractUnit(unitType, Role.DRAGOON, number));
                        price += newPrice * number;
                        break;
                    }
                }
                newPrice = getPrice(unitType, Role.SOLDIER);
                for (int number = 3; number > 0; number--) {
                    if (price + newPrice * number <= gold) {
                        mercenaries.add(new AbstractUnit(unitType, Role.SOLDIER, number));
                        price += newPrice * number;
                        break;
                    }
                }
            } else {
                int newPrice = getPrice(unitType, Role.DEFAULT);
                for (int number = 3; number > 0; number--) {
                    if (price + newPrice * number <= gold) {
                        mercenaries.add(new AbstractUnit(unitType, Role.DEFAULT, number));
                        price += newPrice * number;
                        break;
                    }
                }
            }
            unitTypes.remove(index);
        }

        if (price == 0 && unitType != null) {
            if (unitType.hasAbility("model.ability.canBeEquipped")) {
                mercenaries.add(new AbstractUnit(unitType, Role.SOLDIER, 1));
            } else {
                mercenaries.add(new AbstractUnit(unitType, Role.DEFAULT, 1));
            }
        }

        return mercenaries;
    }



    
    public List<AbstractUnit> addToREF() {
        ArrayList<AbstractUnit> result = new ArrayList<AbstractUnit>();
        if (capacity < spaceRequired) {
            AbstractUnit unit = navalUnits.get(getGame().getModelController().getPseudoRandom().nextInt(navalUnits.size()));
            result.add(new AbstractUnit(unit.getUnitType(), unit.getRole(), 1));
        } else {
            int number = getGame().getModelController().getPseudoRandom().nextInt(3) + 1;
            AbstractUnit unit = landUnits.get(getGame().getModelController().getPseudoRandom().nextInt(landUnits.size()));
            result.add(new AbstractUnit(unit.getUnitType(), unit.getRole(), number));
        }
        return result;
    }

    
    public void addToREF(List<AbstractUnit> units) {
        for (AbstractUnit unitToAdd : units) {
            UnitType unitType = unitToAdd.getUnitType();
            if (unitType.hasAbility("model.ability.navalUnit")) {
                for (AbstractUnit refUnit : navalUnits) {
                    if (refUnit.getUnitType().equals(unitType)) {
                        refUnit.setNumber(refUnit.getNumber() + unitToAdd.getNumber());
                        if (unitType.canCarryUnits()) {
                            capacity += unitType.getSpace() * unitToAdd.getNumber();
                        }
                    }
                }
            } else {
                for (AbstractUnit refUnit : landUnits) {
                    if (refUnit.getUnitType().equals(unitType) &&
                        refUnit.getRole().equals(unitToAdd.getRole())) {
                        refUnit.setNumber(refUnit.getNumber() + unitToAdd.getNumber());
                        spaceRequired += unitType.getSpaceTaken() * unitToAdd.getNumber();
                    }
                }
            }
        }
    }

    
    public int getPrice(List<AbstractUnit> units, boolean rebate) {
        int price = 0;
        for (AbstractUnit unit : units) {
            int newPrice = getPrice(unit.getUnitType(), unit.getRole());
            price += newPrice * unit.getNumber();
        }
        if (price > player.getGold() && rebate) {
            return player.getGold();
        } else {
            return price;
        }
    }

    public int getPrice(UnitType unitType, Role role) {
        if (unitType.hasPrice()) {
            int price = player.getEurope().getUnitPrice(unitType);
            if (Role.SOLDIER.equals(role)) {
                price += getEquipmentPrice(muskets);
            } else if (Role.DRAGOON.equals(role)) {
                price += getEquipmentPrice(muskets);
                price += getEquipmentPrice(horses);
            }
            return price / 10 + 25 * player.getDifficulty().getIndex();
        } else {
            return 1000000;
        }
    }

    private int getEquipmentPrice(EquipmentType equipment) {
        int price = 0;
        for (AbstractGoods goods : equipment.getGoodsRequired()) {
            price += player.getMarket().getBidPrice(goods.getType(), goods.getAmount());
        }
        return price;
    }


    
    public Player declareWar() {
        ArrayList<Player> europeanPlayers = new ArrayList<Player>();
        for (Player enemy : getGame().getPlayers()) {
            if (enemy == player) {
                continue;
            } else if (!player.hasContacted(enemy)) {
                continue;
            } else if (!enemy.isEuropean() || enemy.isREF()) {
                continue;
            }
            Stance stance = player.getStance(enemy);
            if (stance == Stance.PEACE || stance == Stance.CEASE_FIRE) {
                europeanPlayers.add(enemy);
            }
        }
        if (europeanPlayers.size() > 0) {
            int randomInt = getGame().getModelController().getPseudoRandom().nextInt(europeanPlayers.size());
            Player enemy = europeanPlayers.get(randomInt);
            return enemy;
        }
        return null;
    }

    
    


    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("player", this.player.getId());
        out.writeAttribute("name", name);
        out.writeAttribute("supportSea", String.valueOf(supportSea));

        out.writeStartElement("navalUnits");
        for (AbstractUnit unit : navalUnits) {
            unit.toXMLImpl(out);
        }
        out.writeEndElement();

        out.writeStartElement("landUnits");
        for (AbstractUnit unit : landUnits) {
            unit.toXMLImpl(out);
        }
        out.writeEndElement();

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        player = (Player) getGame().getFreeColGameObject(in.getAttributeValue(null, "player"));
        if (player == null) {
            player = new Player(getGame(), in.getAttributeValue(null, "player"));
        }
        name = getAttribute(in, "name", player.getNation().getRulerName());
        supportSea = Boolean.valueOf(in.getAttributeValue(null, "supportSea")).booleanValue();

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String childName = in.getLocalName();
            if ("navalUnits".equals(childName)) {
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    AbstractUnit newUnit = new AbstractUnit(in);
                    navalUnits.add(newUnit);
                }
            } else if ("landUnits".equals(childName)) {
                while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
                    AbstractUnit newUnit = new AbstractUnit(in);
                    landUnits.add(newUnit);
                }
            }
        }
        
        
        if (!in.getLocalName().equals(Monarch.getXMLElementTagName())) {
            logger.warning("Error parsing xml: expecting closing tag </" + Monarch.getXMLElementTagName() + "> "+
                           "found instead: " +in.getLocalName());
        }
    }


    
    public static String getXMLElementTagName() {
        return "monarch";
    }


}

