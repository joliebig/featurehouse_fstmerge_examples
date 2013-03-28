

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitTypeChange.ChangeType;


public class SimpleCombatModel implements CombatModel {

    private static final Logger logger = Logger.getLogger(SimpleCombatModel.class.getName());

    
    public static final int MAXIMUM_BOMBARD_POWER = 48;

    public static final String SMALL_MOVEMENT_PENALTY =
        "model.modifier.smallMovementPenalty";
    public static final String BIG_MOVEMENT_PENALTY =
        "model.modifier.bigMovementPenalty";
    public static final String ARTILLERY_IN_THE_OPEN =
        "model.modifier.artilleryInTheOpen";
    public static final String ATTACK_BONUS =
        "model.modifier.attackBonus";
    public static final String FORTIFIED =
        "model.modifier.fortified";
    public static final String ARTILLERY_AGAINST_RAID =
        "model.modifier.artilleryAgainstRaid";


    private PseudoRandom random;

    
    public CombatOdds calculateCombatOdds(Unit attacker, Unit defender) {
        if (attacker == null || defender == null) {
            return new CombatOdds(CombatOdds.UNKNOWN_ODDS);    
        }
        
        float attackPower = getOffencePower(attacker, defender);
        float defencePower = getDefencePower(attacker, defender);
        if (attackPower == 0.0f && defencePower == 0.0f) {
            return new CombatOdds(CombatOdds.UNKNOWN_ODDS);
        }
        
        float victory = attackPower / (attackPower + defencePower);
        
        return new CombatOdds(victory);
    }

    public SimpleCombatModel(PseudoRandom pseudoRandom) {
        this.random = pseudoRandom;
    }

    
    public CombatResult generateAttackResult(Unit attacker, Unit defender) {

        float attackPower = getOffencePower(attacker, defender);
        float defencePower = getDefencePower(attacker, defender);
        float victory = attackPower / (attackPower + defencePower);
        int damage = 0;
        int r = random.nextInt(100);
        
        CombatResultType result = CombatResultType.EVADES;
        if (r <= victory * 20) {
            
            result = CombatResultType.GREAT_WIN;
            damage = defender.getHitpoints();
        } else if (r <= 100 * victory) {
            
            result = CombatResultType.WIN;
            damage = defender.getHitpoints() - 1;
        } else if (defender.isNaval()
                && r <= (80 * victory) + 20) {
            
            result = CombatResultType.EVADES;
        } else if (r <= (10 * victory) + 90) {
            
            result = CombatResultType.LOSS;
            damage = attacker.getHitpoints() - 1;
        } else {
            
            result = CombatResultType.GREAT_LOSS;
            damage = attacker.getHitpoints();
        }

        
        
        if( defender.isNaval() &&
         	result == CombatResultType.WIN &&
            defender.getOwner().getRepairLocation(defender) == null){
             	result = CombatResultType.GREAT_WIN;
             	damage = defender.getHitpoints();
        }
        
        
        if(attacker.isNaval() &&
           result == CombatResultType.LOSS &&
           attacker.getOwner().getRepairLocation(attacker) == null){
             result = CombatResultType.GREAT_LOSS;
             damage = defender.getHitpoints();
        }
        
        
        if (result.compareTo(CombatResultType.WIN) >= 0 &&
            defender.getTile().getSettlement() != null) {
            if (defender.getTile().getSettlement() instanceof Colony) {
                if (!defender.isDefensiveUnit() &&
                    defender.getAutomaticEquipment() == null) {
                    result = CombatResultType.DONE_SETTLEMENT;
                }
            } else if (defender.getTile().getSettlement() instanceof IndianSettlement) {
                if (defender.getTile().getUnitCount() + defender.getTile().getSettlement().getUnitCount() <= 1) {
                    result = CombatResultType.DONE_SETTLEMENT;
                }
            } else {
                throw new IllegalStateException("Unknown Settlement.");
            }
        }
        return new CombatResult(result, damage);
    }

    
    public CombatResult generateAttackResult(Colony colony, Unit defender) {

        float attackPower = getOffencePower(colony, defender);
        float defencePower = getDefencePower(colony, defender);
        float totalProbability = attackPower + defencePower;
        int damage = 0;
        CombatResultType result = CombatResultType.EVADES;
        int r = random.nextInt(Math.round(totalProbability) + 1);
        if (r < attackPower) {
            int diff = Math.round(defencePower * 2 - attackPower);
            int r2 = random.nextInt((diff < 3) ? 3 : diff);
            if (r2 == 0) {
                result = CombatResultType.GREAT_WIN;
                damage = defender.getHitpoints();
            } else {
                result = CombatResultType.WIN;
                damage = defender.getHitpoints() - 1;
                
                
                if(defender.isNaval() && defender.getOwner().getRepairLocation(defender) == null){
                    result = CombatResultType.GREAT_WIN;
                    damage = defender.getHitpoints();
                }
            }
        }
        return new CombatResult(result, damage);
    }

    
    public float getOffencePower(Colony colony, Unit defender) {
        float attackPower = 0;
        if (defender.isNaval() &&
            colony.hasAbility("model.ability.bombardShips")) {
            for (Unit unit : colony.getTile().getUnitList()) {
                if (unit.hasAbility("model.ability.bombard")) {
                    attackPower += unit.getType().getOffence();
                    if (attackPower >= MAXIMUM_BOMBARD_POWER) {
                        return MAXIMUM_BOMBARD_POWER;
                    }
                }
            }
        }
        return attackPower;
    }
    
    
    public float getOffencePower(Unit attacker, Unit defender) {
        return FeatureContainer.applyModifierSet(0, attacker.getGame().getTurn(),
                                                 getOffensiveModifiers(attacker, defender));
    }

    
    public Set<Modifier> getOffensiveModifiers(Colony colony, Unit defender) {
        Set<Modifier> result = new HashSet<Modifier>();
        result.add(new Modifier("model.modifier.bombardModifier", 
                                getOffencePower(colony, defender),
                                Modifier.Type.ADDITIVE));
        return result;
    }

    
    public Set<Modifier> getOffensiveModifiers(Unit attacker, Unit defender) {
        Specification spec = Specification.getSpecification();
        Set<Modifier> result = new LinkedHashSet<Modifier>();

        result.add(new Modifier(Modifier.OFFENCE, spec.BASE_OFFENCE_SOURCE,
                                attacker.getType().getOffence(),
                                Modifier.Type.ADDITIVE));

        result.addAll(attacker.getType().getFeatureContainer()
                      .getModifierSet(Modifier.OFFENCE));

        result.addAll(attacker.getOwner().getFeatureContainer()
                      .getModifierSet(Modifier.OFFENCE, attacker.getType()));

        if (attacker.isNaval()) {
            int goodsCount = attacker.getGoodsCount();
            if (goodsCount > 0) {
                
                
                result.add(new Modifier(Modifier.OFFENCE, spec.CARGO_PENALTY_SOURCE,
                                        -12.5f * goodsCount,
                                        Modifier.Type.PERCENTAGE));
            }
        } else {
            for (EquipmentType equipment : attacker.getEquipment().keySet()) {
                result.addAll(equipment.getFeatureContainer().getModifierSet(Modifier.OFFENCE));
            }
            
            result.addAll(spec.getModifiers(ATTACK_BONUS));
            
            int movesLeft = attacker.getMovesLeft();
            if (movesLeft == 1) {
                result.addAll(spec.getModifiers(BIG_MOVEMENT_PENALTY));
            } else if (movesLeft == 2) {
                result.addAll(spec.getModifiers(SMALL_MOVEMENT_PENALTY));
            }

            if (defender != null && defender.getTile() != null) {

                if (defender.getTile().getSettlement() == null) {
                    

                    
                    if (attacker.hasAbility("model.ability.ambushBonus") ||
                        defender.hasAbility("model.ability.ambushPenalty")) {
                        Set<Modifier> ambushModifiers = defender.getTile().getType()
                            .getModifierSet(Modifier.DEFENCE);
                        for (Modifier modifier : ambushModifiers) {
                            Modifier ambushModifier = new Modifier(modifier);
                            ambushModifier.setId(Modifier.OFFENCE);
                            ambushModifier.setSource(spec.AMBUSH_BONUS_SOURCE);
                            result.add(ambushModifier);
                        }
                    }

                    
                    if (attacker.hasAbility("model.ability.bombard") &&
                        attacker.getTile().getSettlement() == null) {
                        result.addAll(spec.getModifiers(ARTILLERY_IN_THE_OPEN));
                    }
                } else {
                    
                    
                    result.addAll(attacker.getModifierSet("model.modifier.bombardBonus"));
                }
            }
        }

        return result;
    }

    
    public float getDefencePower(Colony colony, Unit defender) {
        return defender.getType().getDefence();
    }

    
    public float getDefencePower(Unit attacker, Unit defender) {
        return FeatureContainer.applyModifierSet(0, attacker.getGame().getTurn(),
                                                 getDefensiveModifiers(attacker, defender));
    }

    
    public Set<Modifier> getDefensiveModifiers(Colony colony, Unit defender) {
        Set<Modifier> result = new LinkedHashSet<Modifier>();
        result.add(new Modifier("model.modifier.defenceBonus",
                                defender.getType().getDefence(),
                                Modifier.Type.ADDITIVE));
        return result;
    }

    
    public Set<Modifier> getDefensiveModifiers(Unit attacker, Unit defender) {
        Specification spec = Specification.getSpecification();
        Set<Modifier> result = new LinkedHashSet<Modifier>();
        if (defender == null) {
            return result;
        }

        result.add(new Modifier(Modifier.DEFENCE, spec.BASE_DEFENCE_SOURCE,
                                defender.getType().getDefence(),
                                Modifier.Type.ADDITIVE));
        result.addAll(defender.getType().getFeatureContainer()
                      .getModifierSet(Modifier.DEFENCE));


        if (defender.isNaval()) {
            int goodsCount = defender.getVisibleGoodsCount();
            if (goodsCount > 0) {
                
                
                result.add(new Modifier(Modifier.DEFENCE, spec.CARGO_PENALTY_SOURCE,
                                        -12.5f * goodsCount,
                                        Modifier.Type.PERCENTAGE));
            }
        } else {
            
            
            
            
            TypeCountMap<EquipmentType> autoEquipList = defender.getAutomaticEquipment();
            if (autoEquipList != null) {
                
                for(EquipmentType equipment : autoEquipList.keySet()){
                    result.addAll(equipment.getModifierSet("model.modifier.defence"));
                }
            }

            for (EquipmentType equipment : defender.getEquipment().keySet()) {
                result.addAll(equipment.getFeatureContainer().getModifierSet(Modifier.DEFENCE));
            }
            
            if (defender.getState() == UnitState.FORTIFIED) {
                result.addAll(spec.getModifiers(FORTIFIED));
            }

            if (defender.getTile() != null) {
                Tile tile = defender.getTile();
                if (tile.getSettlement() == null) {
                    
                    if (!(attacker.hasAbility("model.ability.ambushBonus") ||
                          defender.hasAbility("model.ability.ambushPenalty"))) {
                        
                        result.addAll(tile.getType().getDefenceBonus());
                    }
                    if (defender.hasAbility("model.ability.bombard") &&
                        defender.getState() != UnitState.FORTIFIED) {
                        
                        result.addAll(spec.getModifiers(ARTILLERY_IN_THE_OPEN));
                    }
                } else {
                    result.addAll(tile.getSettlement().getOwner().getFeatureContainer()
                                  .getModifierSet(Modifier.SETTLEMENT_DEFENCE));
                    if (tile.getSettlement().isCapital()) {
                        result.addAll(tile.getSettlement().getOwner().getFeatureContainer()
                                      .getModifierSet(Modifier.CAPITAL_DEFENCE));
                    }
                    if (defender.hasAbility("model.ability.bombard") &&
                        attacker.getOwner().isIndian()) {
                        
                        result.addAll(spec.getModifiers(ARTILLERY_AGAINST_RAID));
                    }
                }
            }

        }
        return result;
    }

    
    public void attack(Unit attacker, Unit defender, CombatResult result, int plunderGold, Location repairLocation) {
        Player attackingPlayer = attacker.getOwner();
        Player defendingPlayer = defender.getOwner();

        
        if (attacker.hasAbility("model.ability.piracy")) {
            defendingPlayer.setAttackedByPrivateers();
        } else if (!defender.hasAbility("model.ability.piracy")) {
            attackingPlayer.changeRelationWithPlayer(defendingPlayer, Stance.WAR);
        }

        
        
        
        attacker.setState(UnitState.ACTIVE);
        
        
        
        if (!attacker.hasAbility("model.ability.multipleAttacks")) {
            attacker.setMovesLeft(0);
        } else {
            
            int movecost = attacker.getMoveCost(defender.getTile());
            attacker.setMovesLeft(attacker.getMovesLeft()-movecost);
        }

        Tile newTile = defender.getTile();
        
        Settlement settlement = newTile.getSettlement();

        
        TypeCountMap<EquipmentType> autoEquipList = defender.getAutomaticEquipment();
        if (autoEquipList != null) {
            defendingPlayer.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                             "model.unit.automaticDefence",
                                                             defender)
                                     .addStringTemplate("%unit%", defender.getLabel())
                                     .addName("%colony%", settlement.getName()));
        }

        switch (result.type) {
        case EVADES:
            if (attacker.isNaval()) {
                evade(defender, null, attacker);
            } else {
                logger.warning("Non-naval unit evades!");
            }
            break;
        case LOSS:
            if (attacker.isNaval()) {
                damageShip(attacker, null, defender, repairLocation);
            } else {
                loseCombat(attacker, defender);
                if (defendingPlayer.hasAbility("model.ability.automaticPromotion")) {
                    promote(defender);
                }
            }
            break;
        case GREAT_LOSS:
            if (attacker.isNaval()) {
                sinkShip(attacker, null, defender);
            } else {
                loseCombat(attacker, defender);
                promote(defender);
            }
            break;
        case DONE_SETTLEMENT:
            if (settlement instanceof IndianSettlement) {
                defender.dispose();
                destroySettlement(attacker, (IndianSettlement) settlement);
            } else if (settlement instanceof Colony) {
                captureColony(attacker, (Colony) settlement, plunderGold, repairLocation);
            } else {
                throw new IllegalStateException("Unknown type of settlement.");
            }
            promote(attacker);
            break;
        case WIN:
            if (attacker.isNaval()) {
                attacker.captureGoods(defender);
                damageShip(defender, null, attacker, repairLocation);
            } else if (attacker.hasAbility("model.ability.pillageUnprotectedColony") && 
                       !defender.isDefensiveUnit() &&
                       defender.getColony() != null &&
                       !defender.getColony().hasStockade() &&
                       defender.getAutomaticEquipment() == null) {
                pillageColony(attacker, defender.getColony(), repairLocation);
            } else {
                if (!defender.isNaval()) {
                    loseCombat(defender, attacker);
                    if (settlement instanceof IndianSettlement) {
                        getConvert(attacker, (IndianSettlement) settlement);
                    }
                }
                if (attacker.hasAbility("model.ability.automaticPromotion")) {
                    promote(attacker);
                }
            }
            break;
        case GREAT_WIN:
            if (attacker.isNaval()) {
                attacker.captureGoods(defender);
                sinkShip(defender, null, attacker);
            } else {
                if (!defender.isNaval()) {
                    loseCombat(defender, attacker);
                    if (settlement instanceof IndianSettlement) {
                        getConvert(attacker, (IndianSettlement) settlement);
                    }
                }
                promote(attacker);
            }
            break;
        default:
            logger.warning("Illegal result of attack!");
            throw new IllegalArgumentException("Illegal result of attack!");
        }
    }

    
    public void bombard(Colony colony, Unit defender, CombatResult result, Location repairLocation) {
        switch (result.type) {
        case EVADES:
            evade(defender, colony, null);
            break;
        case WIN:
            damageShip(defender, colony, null, repairLocation);
            break;
        case GREAT_WIN:
            sinkShip(defender, colony, null);
            break;
        case DONE_SETTLEMENT:
            
            assert false;
            break;
        case GREAT_LOSS:
        case LOSS:
            
            break;
        }
    }

    
    public void captureColony(Unit attacker, Colony colony, int plunderGold,
                              Location repairLocation) {
        logger.finest("Entering captureColony()");
        Player defendingPlayer = colony.getOwner();
        Player attackingPlayer = attacker.getOwner();

        defendingPlayer.modifyTension(attacker.getOwner(), Tension.TENSION_ADD_MAJOR);
        if (attackingPlayer.isEuropean()) {
            attackingPlayer.getHistory()
                .add(new HistoryEvent(attackingPlayer.getGame().getTurn().getNumber(),
                                      HistoryEvent.EventType.CONQUER_COLONY)
                     .addStringTemplate("%nation%", defendingPlayer.getNationName())
                     .addName("%colony%", colony.getName()));
            defendingPlayer.getHistory()
                .add(new HistoryEvent(defendingPlayer.getGame().getTurn().getNumber(),
                                      HistoryEvent.EventType.COLONY_CONQUERED)
                     .addName("%colony%", colony.getName())
                     .addStringTemplate("%nation%", attackingPlayer.getNationName()));
            defendingPlayer.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                             "model.unit.colonyCapturedBy", defendingPlayer)
                                            .addName("%colony%", colony.getName())
                                            .addAmount("%amount%", plunderGold)
                                            .addStringTemplate("%player%", attackingPlayer.getNationName()));
            damageAllShips(colony, attacker, repairLocation);

            attackingPlayer.modifyGold(plunderGold);
            defendingPlayer.modifyGold(-plunderGold);
            defendingPlayer.divertModelMessages(colony, defendingPlayer);

            
            colony.changeOwner(attackingPlayer);
            
            for (Unit capturedUnit : colony.getUnitList()) {
                defendingPlayer.divertModelMessages(capturedUnit, defendingPlayer);
                if (!capturedUnit.getType().isAvailableTo(attackingPlayer)) {
                    UnitType downgrade = capturedUnit.getType().getUnitTypeChange(ChangeType.CAPTURE, attackingPlayer);
                    if (downgrade != null) {
                        capturedUnit.setType(downgrade);
                    } else {
                        capturedUnit.dispose();
                    }
                }
            }                    

            attackingPlayer.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                             "model.unit.colonyCaptured", colony)
                                            .addName("%colony%", colony.getName())
                                            .addAmount("%amount%", plunderGold));

            
            for (Unit capturedUnit : colony.getTile().getUnitList()) {
                defendingPlayer.divertModelMessages(capturedUnit, defendingPlayer);
                if (attacker.isUndead()) {
                    capturedUnit.setType(attacker.getType());
                } else {
                    UnitType downgrade = capturedUnit.getType().getUnitTypeChange(ChangeType.CAPTURE, attackingPlayer);
                    if (downgrade != null) {
                        capturedUnit.setType(downgrade);
                    }
                }
                capturedUnit.setState(UnitState.ACTIVE);
            }

            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                colony.getExportData(goodsType).setExported(false);
            }                                 

            if (attacker.isUndead()) {
                for (Unit capturedUnit : colony.getUnitList()) {
                    capturedUnit.setType(attacker.getType());
                }
            }
            attacker.setLocation(colony.getTile());
        } else {
            
            
            if (colony.getUnitCount() <= 1) {
                defendingPlayer.getHistory()
                    .add(new HistoryEvent(defendingPlayer.getGame().getTurn().getNumber(),
                                          HistoryEvent.EventType.COLONY_DESTROYED)
                         .addStringTemplate("%nation%", attackingPlayer.getNationName())
                         .addName("%colony%", colony.getName()));
                defendingPlayer
                    .addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                      "model.unit.colonyBurning", defendingPlayer)
                                     .addName("%colony%", colony.getName())
                                     .addAmount("%amount%", plunderGold)
                                     .addStringTemplate("%nation%", attackingPlayer.getNationName())
                                     .addStringTemplate("%unit%", attacker.getLabel()));
                attackingPlayer.modifyGold(plunderGold);
                defendingPlayer.modifyGold(-plunderGold);
                damageAllShips(colony, attacker, repairLocation);
                defendingPlayer.divertModelMessages(colony, defendingPlayer);
                for (Unit victim : colony.getUnitList()) {
                    defendingPlayer.divertModelMessages(victim, defendingPlayer);
                    victim.dispose();
                }
                colony.dispose();
                attacker.setLocation(colony.getTile());
            } else {
                Unit victim = colony.getRandomUnit();
                if (victim == null) {
                    logger.warning("could not find colonist to slaughter");
                } else {
                    defendingPlayer.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                                     "model.unit.colonistSlaughtered", colony)
                                                    .addName("%colony%", colony.getName())
                                                    .addStringTemplate("%unit%", victim.getLabel())
                                                    .addStringTemplate("%nation%", attackingPlayer.getNationName())
                                                    .addStringTemplate("%enemyUnit%", attacker.getLabel()));
                    defendingPlayer.divertModelMessages(victim, defendingPlayer);
                    victim.dispose();
                }
            }
        }

    }


    
    private void damageAllShips(Colony colony, Unit attacker, Location repairLocation) {
        
        
        List<Unit> navalUnitsOutsideColony = new ArrayList<Unit>();
        for (Unit unit : colony.getTile().getUnitList()) {
            if (unit.isNaval()) {
                navalUnitsOutsideColony.add(unit);
            }
        }
        
        for (Unit unit : navalUnitsOutsideColony)
                damageShip(unit, null, attacker, repairLocation);
    }

    
    private void pillageColony(Unit attacker, Colony colony, Location repairLocation) {
        ArrayList<Building> buildingList = new ArrayList<Building>();
        ArrayList<Unit> shipList = new ArrayList<Unit>();
        List<Goods> goodsList = colony.getGoodsContainer().getCompactGoods();
        
        for (Building building : colony.getBuildings()) {
            if (building.canBeDamaged()) {
                buildingList.add(building);
            }
        }
        
        List<Unit> unitList = colony.getTile().getUnitList();
        for (Unit unit : unitList) {
            if (unit.isNaval()) {
                shipList.add(unit);
            }
        }
        
        StringTemplate nation = attacker.getOwner().getNationName();
        StringTemplate unitName = attacker.getLabel();
        String colonyName = colony.getName();
        Player owner = colony.getOwner();
        int limit = buildingList.size() + goodsList.size() + shipList.size() + 1;
        int random = attacker.getGame().getModelController().getRandom(attacker.getId() + "pillageColony", limit);
                                                                       
        if (random < buildingList.size()) {
            Building building = buildingList.get(random);
            owner.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.buildingDamaged",
                                                   colony)
                                   .add("%building%", building.getNameKey())
                                   .addName("%colony%", colonyName)
                                   .addStringTemplate("%enemyNation%", nation)
                                   .addStringTemplate("%enemyUnit%", unitName));
            building.damage();
        } else if (random < buildingList.size() + goodsList.size()) {
            Goods goods = goodsList.get(random - buildingList.size());
            goods.setAmount(Math.min(goods.getAmount() / 2, 50));
            colony.removeGoods(goods);
            if (attacker.getSpaceLeft() > 0) {
                attacker.add(goods);
            }
            owner.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.goodsStolen",
                                                   colony, goods)
                                   .addAmount("%amount%", goods.getAmount())
                                   .add("%goods%", goods.getNameKey())
                                   .addName("%colony%", colonyName)
                                   .addStringTemplate("%enemyNation%", nation)
                                   .addStringTemplate("%enemyUnit%", unitName));
        } else if (random < buildingList.size() + goodsList.size() + shipList.size()) {
            Unit ship = shipList.get(random - buildingList.size() - goodsList.size());
            damageShip(ship, null, attacker, repairLocation);
        } else { 
            int gold = colony.getOwner().getGold() / 10;
            colony.getOwner().modifyGold(-gold);
            attacker.getOwner().modifyGold(gold);
            owner.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.indianPlunder",
                                                   colony)
                                   .addAmount("%amount%", gold)
                                   .addName("%colony%", colonyName)
                                   .addStringTemplate("%enemyNation%", nation)
                                   .addStringTemplate("%enemyUnit%", unitName));
        }
    }

    
    private void destroySettlement(Unit attacker, IndianSettlement settlement) {
        Player player = attacker.getOwner();
        Player enemy = settlement.getOwner();
        boolean wasCapital = settlement.isCapital();
        Tile newTile = settlement.getTile();
        ModelController modelController = attacker.getGame().getModelController();
        SettlementType settlementType = ((IndianNationType) enemy.getNationType()).getTypeOfSettlement();
        String settlementName = settlement.getName();
        settlement.dispose();

        enemy.modifyTension(attacker.getOwner(), Tension.TENSION_ADD_MAJOR);

        List<UnitType> treasureUnitTypes = FreeCol.getSpecification()
            .getUnitTypesWithAbility("model.ability.carryTreasure");
        if (treasureUnitTypes.size() > 0) {
            int randomTreasure = modelController.getRandom(attacker.getId() + "indianTreasureRandom" + 
                                                           attacker.getId(), 11);
            int random = modelController.getRandom(attacker.getId() + "newUnitForTreasure" +
                                                   attacker.getId(), treasureUnitTypes.size());
            Unit tTrain = modelController.createUnit(attacker.getId() + "indianTreasure" +
                                                     attacker.getId(), newTile, attacker.getOwner(),
                                                     treasureUnitTypes.get(random));

            
            Set<Modifier> modifierSet = attacker.getModifierSet("model.modifier.nativeTreasureModifier");
            randomTreasure = (int) FeatureContainer.applyModifierSet(randomTreasure, attacker.getGame().getTurn(),
                                                                     modifierSet);
            if (settlementType == SettlementType.INCA_CITY ||
                settlementType == SettlementType.AZTEC_CITY) {
                tTrain.setTreasureAmount(randomTreasure * 500 + 1000);
            } else {
                tTrain.setTreasureAmount(randomTreasure * 50  + 300);
            }

            
            if (wasCapital) {
                tTrain.setTreasureAmount((tTrain.getTreasureAmount() * 3) / 2);
            }

            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                    "model.unit.indianTreasure",
                                                    attacker)
                                     .addName("%settlement%", settlementName)
                                     .addAmount("%amount%", tTrain.getTreasureAmount()));
        }
        int atrocities = Player.SCORE_SETTLEMENT_DESTROYED;
        if (settlementType == SettlementType.INCA_CITY ||
            settlementType == SettlementType.AZTEC_CITY) {
            atrocities *= 2;
        }
        if (wasCapital) {
            atrocities = (atrocities * 3) / 2;
        }
        attacker.getOwner().modifyScore(atrocities);
        attacker.setLocation(newTile);
        attacker.getOwner().getHistory()
            .add(new HistoryEvent(attacker.getGame().getTurn().getNumber(),
                                  HistoryEvent.EventType.DESTROY_SETTLEMENT)
                 .addStringTemplate("%nation%", enemy.getNationName())
                 .addName("%settlement%", settlementName));
        if (enemy.getSettlements().isEmpty()) {
            attacker.getOwner().getHistory()
                .add(new HistoryEvent(attacker.getGame().getTurn().getNumber(),
                                      HistoryEvent.EventType.DESTROY_NATION)
                     .addStringTemplate("%nation%", enemy.getNationName()));
        }
    }

    
    private void getConvert(Unit attacker, IndianSettlement indianSettlement) {
        ModelController modelController = attacker.getGame().getModelController();
        int random = modelController.getRandom(attacker.getId() + "getConvert", 100);
        int convertProbability = (int) FeatureContainer.applyModifierSet(Specification.getSpecification()
                .getIntegerOption("model.option.nativeConvertProbability").getValue(), attacker.getGame().getTurn(),
                attacker.getModifierSet("model.modifier.nativeConvertBonus"));
        
        int burnProbability = Specification.getSpecification().getIntegerOption("model.option.burnProbability")
                .getValue();
        
        if (random < convertProbability) {
            Unit missionary = indianSettlement.getMissionary();
            if (missionary != null && missionary.getOwner() == attacker.getOwner() &&
                attacker.getGame().getViewOwner() == null && indianSettlement.getUnitCount() > 1) {
                List<UnitType> converts = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.convert");
                if (converts.size() > 0) {
                    indianSettlement.getFirstUnit().dispose();
                    random = modelController.getRandom(attacker.getId() + "getConvertType", converts.size());
                    modelController.createUnit(attacker.getId() + "indianConvert", attacker.getLocation(),
                                               attacker.getOwner(), converts.get(random));
                }
            }
        } else if (random >= 100 - burnProbability) {
            boolean burn = false;
            List<Settlement> settlements = indianSettlement.getOwner().getSettlements();
            for (Settlement settlement : settlements) {
                IndianSettlement indian = (IndianSettlement) settlement;
                Unit missionary = indian.getMissionary();
                if (missionary != null && missionary.getOwner() == attacker.getOwner()) {
                    burn = true;
                    indian.setMissionary(null);
                }
            }
            if (burn) {
                Player player = attacker.getOwner();
                player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                        "model.unit.burnMissions",
                                                        attacker, indianSettlement)
                                         .addStringTemplate("%nation%", attacker.getOwner().getNationName())
                                         .addStringTemplate("%enemyNation%", indianSettlement.getOwner().getNationName()));
            }
        }
    }

    
    private void evade(Unit defender, Colony attackerColony, Unit attackerUnit) {
        Player player = defender.getOwner();
        StringTemplate nation = defender.getApparentOwnerName();
        if (attackerColony != null) {
            Player enemy = attackerColony.getOwner();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.shipEvadedBombardment",
                                                   attackerColony)
                                           .addName("%colony%", attackerColony.getName())
                                           .addStringTemplate("%unit%", defender.getLabel())
                                           .addStringTemplate("%nation%", nation));
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                    "model.unit.shipEvadedBombardment",
                                                    defender)
                                     .addName("%colony%", attackerColony.getName())
                                     .addStringTemplate("%unit%", defender.getLabel())
                                     .addStringTemplate("%nation%", nation));
        } else if (attackerUnit != null) {
            Player enemy = attackerUnit.getOwner();
            StringTemplate attackerNation = attackerUnit.getApparentOwnerName();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.enemyShipEvaded",
                                                   attackerUnit)
                                         .addStringTemplate("%unit%", attackerUnit.getLabel())
                                         .addStringTemplate("%enemyUnit%", defender.getLabel())
                                         .addStringTemplate("%enemyNation%", nation));
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                    "model.unit.shipEvaded",
                                                    defender)
                                     .addStringTemplate("%unit%", defender.getLabel())
                                     .addStringTemplate("%enemyUnit%", attackerUnit.getLabel())
                                     .addStringTemplate("%enemyNation%", attackerNation));
        }
    }

    
    private void damageShip(Unit damagedShip, Colony attackerColony, Unit attackerUnit, Location repairLocation) {
        Player player = damagedShip.getOwner();
        StringTemplate nation = damagedShip.getApparentOwnerName();
        StringTemplate repairLocationName = (repairLocation == null)
            ? StringTemplate.name("")
            : repairLocation.getLocationName();

        if (attackerColony != null) {
            Player enemy = attackerColony.getOwner();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.enemyShipDamagedByBombardment",
                                                   attackerColony)
                                           .addName("%colony%", attackerColony.getName())
                                           .addStringTemplate("%nation%", nation)
                                           .addStringTemplate("%unit%", damagedShip.getLabel()));
        		
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                    "model.unit.shipDamagedByBombardment",
                                                    damagedShip)
                                        .addName("%colony%", attackerColony.getName())
                                        .addStringTemplate("%unit%", damagedShip.getLabel())
                                        .addStringTemplate("%repairLocation%", repairLocationName));
        } else if (attackerUnit != null) {
            Player enemy = attackerUnit.getOwner();
            StringTemplate attackerNation = attackerUnit.getApparentOwnerName();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.enemyShipDamaged",
                                                   attackerUnit)
                                         .addStringTemplate("%unit%", attackerUnit.getLabel())
                                         .addStringTemplate("%enemyNation%", nation)
                                         .addStringTemplate("%enemyUnit%", damagedShip.getLabel()));
            
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                    "model.unit.shipDamaged",
                                                    damagedShip)
                                        .addStringTemplate("%unit%", damagedShip.getLabel())
                                        .addStringTemplate("%enemyUnit%", attackerUnit.getLabel())
                                        .addStringTemplate("%enemyNation%", attackerNation)
                                        .addStringTemplate("%repairLocation%", repairLocationName));
        }
        damagedShip.setHitpoints(1);
        damagedShip.disposeAllUnits();
        damagedShip.getGoodsContainer().removeAll();
        damagedShip.setDestination(null);
        damagedShip.sendToRepairLocation(repairLocation);
    }

    
    private void sinkShip(Unit sinkingShip, Colony attackerColony, Unit attackerUnit) {
        Player player = sinkingShip.getOwner();
        StringTemplate nation = sinkingShip.getApparentOwnerName();

        if (attackerColony != null) {
            Player enemy = attackerColony.getOwner();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.shipSunkByBombardment",
                                                   attackerColony)
                                           .addName("%colony%", attackerColony.getName())
                                           .addStringTemplate("%unit%", sinkingShip.getLabel())
                                           .addStringTemplate("%nation%", nation));
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                         "model.unit.shipSunkByBombardment",
                                                         sinkingShip)
                                        .addName("%colony%", attackerColony.getName())
                                        .addStringTemplate("%unit%", sinkingShip.getLabel()));
        } else if (attackerUnit != null) {
            Player enemy = attackerColony.getOwner();
            StringTemplate attackerNation = attackerUnit.getApparentOwnerName();
            enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                   "model.unit.enemyShipSunk",
                                                   attackerUnit)
                                         .addStringTemplate("%unit%", attackerUnit.getLabel())
                                         .addStringTemplate("%enemyUnit%", sinkingShip.getLabel())
                                         .addStringTemplate("%enemyNation%", nation));
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                         "model.unit.shipSunk",
                                                         sinkingShip)
                                        .addStringTemplate("%unit%", sinkingShip.getLabel())
                                        .addStringTemplate("%enemyUnit%", attackerUnit.getLabel())
                                        .addStringTemplate("%enemyNation%", attackerNation));
        }
        player.divertModelMessages(sinkingShip, sinkingShip.getTile());
        sinkingShip.dispose();
    }

    
    private EquipmentType findEquipmentTypeToLose(Unit victim) {
        EquipmentType toLose = null;
        int combatLossPriority = 0;
        
        TypeCountMap<EquipmentType> equipmentList = victim.getEquipment();
        if(equipmentList.isEmpty()){
            TypeCountMap<EquipmentType> autoEquipment = victim.getAutomaticEquipment(); 
            if(autoEquipment != null){
                equipmentList = autoEquipment;
            }
        }

        for (EquipmentType equipmentType : equipmentList.keySet()) {
            if (equipmentType.getCombatLossPriority() > combatLossPriority) {
                toLose = equipmentType;
                combatLossPriority = equipmentType.getCombatLossPriority();
            }
        }
        return toLose;
    }

    
    private void loseCombat(Unit unit, Unit enemyUnit) {
        if (unit.hasAbility("model.ability.disposeOnCombatLoss")) {
            slaughterUnit(unit, enemyUnit);
            return;
        }
        
        EquipmentType typeToLose = findEquipmentTypeToLose(unit);
        boolean hasEquipToLose = typeToLose != null;
        if(hasEquipToLose){
            if(losingEquipDiscardsUnit(unit,typeToLose)){
                slaughterUnit(unit, enemyUnit);
            }
            else{
                disarmUnit(unit, typeToLose, enemyUnit);
            }
            return;
        }
        
        UnitType downgrade = unit.getType().getUnitTypeChange(ChangeType.DEMOTION, unit.getOwner()); 
        if (downgrade != null) {
            demoteUnit(unit, downgrade, enemyUnit);
            return;
        }
        
        boolean unitCanBeCaptured = unit.hasAbility("model.ability.canBeCaptured")
                                    && enemyUnit.hasAbility("model.ability.captureUnits");
        if(unitCanBeCaptured){
            captureUnit(unit, enemyUnit);
        }
        else{
            slaughterUnit(unit, enemyUnit);
        }        
    }

    private boolean losingEquipDiscardsUnit(Unit unit, EquipmentType typeToLose) {
        if(!unit.hasAbility("model.ability.disposeOnAllEquipLost")){
            return false;
        }
        
        for(EquipmentType equip : unit.getEquipment().keySet()){
            if(equip != typeToLose){
                return false;
            }
        }
        return true;
    }

    
    private void captureUnit(Unit unit, Unit enemyUnit) {
        StringTemplate locationName = unit.getLocation().getLocationName();
        Player loser = unit.getOwner();
        StringTemplate nation = loser.getNationName();
        StringTemplate oldName = unit.getLabel();
        Player enemy = enemyUnit.getOwner();
        StringTemplate enemyNation = enemy.getNationName();
        String messageID = unit.getType().getId() + ".captured";

        
        loser.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, unit)
                             .setDefaultId("model.unit.unitCaptured")
                             .addStringTemplate("%nation%", nation)
                             .addStringTemplate("%unit%", oldName)
                             .addStringTemplate("%enemyNation%", enemyNation)
                             .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                             .addStringTemplate("%location%", locationName));
        loser.divertModelMessages(unit, unit.getTile());
        unit.setLocation(enemyUnit.getTile());
        unit.setOwner(enemyUnit.getOwner());
        if (enemyUnit.isUndead()) {
            unit.setType(enemyUnit.getType());
        } else {
            UnitType downgrade = unit.getType().getUnitTypeChange(ChangeType.CAPTURE, unit.getOwner());
            if (downgrade != null) unit.setType(downgrade);
        }
        enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, enemyUnit)
                                  .setDefaultId("model.unit.unitCaptured")
                                  .addStringTemplate("%nation%", nation)
                                  .addStringTemplate("%unit%", oldName)
                                  .addStringTemplate("%enemyNation%", enemyNation)
                                  .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                                  .addStringTemplate("%location%", locationName));
    }

    
    private void demoteUnit(Unit unit, UnitType downgrade, Unit enemyUnit) {
        StringTemplate locationName = unit.getLocation().getLocationName();
        Player loser = unit.getOwner();
        StringTemplate nation = loser.getNationName();
        StringTemplate oldName = unit.getLabel();
        Player enemy = enemyUnit.getOwner();
        StringTemplate enemyNation = enemy.getNationName();
        String messageID = unit.getType().getId() + ".demoted";

        unit.setType(downgrade);
        enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, enemyUnit)
                                  .setDefaultId("model.unit.unitDemoted")
                                  .addStringTemplate("%nation%", nation)
                                  .addStringTemplate("%oldName%", oldName)
                                  .addStringTemplate("%unit%", unit.getLabel())
                                  .addStringTemplate("%enemyNation%", enemyNation)
                                  .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                                  .addStringTemplate("%location%", locationName));
        loser.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, unit)
                             .setDefaultId("model.unit.unitDemoted")
                             .addStringTemplate("%nation%", nation)
                             .addStringTemplate("%oldName%", oldName)
                             .addStringTemplate("%unit%", unit.getLabel())
                             .addStringTemplate("%enemyNation%", enemyNation)
                             .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                             .addStringTemplate("%location%", locationName));
    }

    
    private void disarmUnit(Unit unit, EquipmentType typeToLose, Unit enemyUnit) {
        StringTemplate locationName = unit.getLocation().getLocationName();
        Player loser = unit.getOwner();
        StringTemplate nation = loser.getNationName();
        StringTemplate oldName = unit.getLabel();
        Player enemy = enemyUnit.getOwner();
        StringTemplate enemyNation = enemy.getNationName();
        String messageID = unit.getType().getId() + ".demoted";

        boolean hasAutoEquipment = unit.getEquipment().isEmpty() && unit.getAutomaticEquipment() != null;

        if(hasAutoEquipment){
            
            
            
            Settlement settlement = null;
            if(unit.getLocation() instanceof IndianSettlement){
                settlement = unit.getIndianSettlement();
            }
            else{
                settlement = unit.getColony();
            }
            for(AbstractGoods goods : typeToLose.getGoodsRequired()){
                settlement.removeGoods(goods);
            }
        }
        else{
            unit.removeEquipment(typeToLose, 1, true);
            if (unit.getEquipment().isEmpty()) {
                messageID = "model.unit.unitDemotedToUnarmed";
            }
        }
        enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, enemyUnit)
                                  .setDefaultId("model.unit.unitDemoted")
                                  .addStringTemplate("%nation%", nation)
                                  .addStringTemplate("%oldName%", oldName)
                                  .addStringTemplate("%unit%", unit.getLabel())
                                  .addStringTemplate("%enemyNation%", enemyNation)
                                  .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                                  .addStringTemplate("%location%", locationName));
        loser.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, unit)
                             .setDefaultId("model.unit.unitDemoted")
                             .addStringTemplate("%nation%", nation)
                             .addStringTemplate("%oldName%", oldName)
                             .addStringTemplate("%unit%", unit.getLabel())
                             .addStringTemplate("%enemyNation%", enemyNation)
                             .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                             .addStringTemplate("%location%", locationName));
               
        enemyCapturesEquipment(unit,enemyUnit,typeToLose);
    }

    private void enemyCapturesEquipment(Unit unit, Unit enemyUnit, EquipmentType typeToLose) {
        if (!enemyUnit.hasAbility("model.ability.captureEquipment")){
            return;
        }
        
        
        
        
        
        EquipmentType newEquipType = typeToLose;
        boolean defenderIsIndian = unit.getOwner().isIndian();
        boolean attackerIsIndian = enemyUnit.getOwner().isIndian();
        if(!defenderIsIndian && attackerIsIndian){
            if(typeToLose == FreeCol.getSpecification().getEquipmentType("model.equipment.horses")){
                newEquipType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.horses");
            }
            if(typeToLose == FreeCol.getSpecification().getEquipmentType("model.equipment.muskets")){
                newEquipType = FreeCol.getSpecification().getEquipmentType("model.equipment.indian.muskets");
            }
        }
        if(defenderIsIndian && !attackerIsIndian){
            if(typeToLose == FreeCol.getSpecification().getEquipmentType("model.equipment.indian.horses")){
                newEquipType = FreeCol.getSpecification().getEquipmentType("model.equipment.horses");
            }
            if(typeToLose == FreeCol.getSpecification().getEquipmentType("model.equipment.indian.muskets")){
                newEquipType = FreeCol.getSpecification().getEquipmentType("model.equipment.muskets");
            }
        }
        
        if(!enemyUnit.canBeEquippedWith(newEquipType)){
            return;
        }
        
        enemyUnit.equipWith(newEquipType, 1, true);
        unit.getOwner().addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                         "model.unit.equipmentCaptured",
                                                         unit)
                             .addStringTemplate("%nation%", enemyUnit.getOwner().getNationName())
                             .add("%equipment%", typeToLose.getNameKey()));
        IndianSettlement settlement = enemyUnit.getIndianSettlement();
        if (settlement != null) {
            for (AbstractGoods goods : typeToLose.getGoodsRequired()) {
                settlement.addGoods(goods);
            }
        }
    }

    
    private void slaughterUnit(Unit unit, Unit enemyUnit) {
        StringTemplate locationName = enemyUnit.getLocation().getLocationName();
        Player loser = unit.getOwner();
        StringTemplate nation = loser.getNationName();
        Player enemy = enemyUnit.getOwner();
        StringTemplate enemyNation = enemy.getNationName();
        String messageID = unit.getType().getId() + ".destroyed";

        enemy.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, enemyUnit)
                                  .setDefaultId("model.unit.unitSlaughtered")
                                  .addStringTemplate("%nation%", nation)
                                  .addStringTemplate("%unit%", unit.getLabel())
                                  .addStringTemplate("%enemyNation%", enemyNation)
                                  .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                                  .addStringTemplate("%location%", locationName));
        loser.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                               messageID, unit)
                             .setDefaultId("model.unit.unitSlaughtered")
                             .addStringTemplate("%nation%", nation)
                             .addStringTemplate("%unit%", unit.getLabel())
                             .addStringTemplate("%enemyNation%", enemyNation)
                             .addStringTemplate("%enemyUnit%", enemyUnit.getLabel())
                             .addStringTemplate("%location%", locationName));
        
        for(EquipmentType equip : unit.getEquipment().keySet()){
            enemyCapturesEquipment(unit, enemyUnit, equip);
        }
        
        loser.divertModelMessages(unit, unit.getTile());
        unit.dispose();
    }

    
    private void promote(Unit unit) {
        StringTemplate oldName = unit.getLabel();
        Player player = unit.getOwner();
        StringTemplate nation = player.getNationName();
        UnitType newType = unit.getType().getUnitTypeChange(ChangeType.PROMOTION, unit.getOwner());
        
        if (newType != null && newType.isAvailableTo(unit.getOwner())) {
            unit.setType(newType);
            if (unit.getType().equals(newType)) {
                
                player.addModelMessage(new ModelMessage(ModelMessage.MessageType.COMBAT_RESULT,
                                                        "model.unit.unitPromoted",
                                                        unit)
                                     .addStringTemplate("%oldName%", oldName)
                                     .addStringTemplate("%unit%", unit.getLabel())
                                     .addStringTemplate("%nation%", nation));
            }
        }
    }
}
