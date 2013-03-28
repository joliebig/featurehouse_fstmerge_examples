

package net.sf.freecol.common.model;

import java.util.Set;


public interface CombatModel {

    public static enum CombatResultType {
        GREAT_LOSS(false),
        LOSS(false),
        EVADES(false),
        WIN(true),
        GREAT_WIN(true),
        DONE_SETTLEMENT(true);
        
        private final boolean success;
        
        private CombatResultType(boolean success) {
            this.success = success;
        }
        
        
        public boolean isSuccess() {
            return success;
        }
    }

    public class CombatResult {
        public CombatResultType type;
        public int damage;
        public CombatResult(CombatResultType type, int damage) {
            this.type = type;
            this.damage = damage;
        }
    }

    
    public class CombatOdds {
        public static final float UNKNOWN_ODDS = -1.0f;
        
        public float win;
        
        
        
        
        public CombatOdds(float win) {
            this.win = win;
        }
    }


    
    public CombatOdds calculateCombatOdds(Unit attacker, Unit defender);

    
    public CombatOdds calculateCombatOdds(Colony attacker, Unit defender);


    
    public CombatResult generateAttackResult(Unit attacker, Unit defender);

    
    public CombatResult generateAttackResult(Colony attacker, Unit defender);


    
    public float getOffencePower(Unit attacker, Unit defender);

    
    public float getOffencePower(Colony attacker, Unit defender);

    
    public float getDefencePower(Unit attacker, Unit defender);

    
    public float getDefencePower(Colony attacker, Unit defender);


    
    public Set<Modifier> getOffensiveModifiers(Unit attacker, Unit defender);

    
    public Set<Modifier> getOffensiveModifiers(Colony attacker, Unit defender);

    
    public Set<Modifier> getDefensiveModifiers(Unit attacker, Unit defender);

    
    public Set<Modifier> getDefensiveModifiers(Colony attacker, Unit defender);


    
    public void attack(Unit attacker, Unit defender, CombatResult result,
                       int plunderGold, Location repairLocation);

    
    public void bombard(Colony colony, Unit defender, CombatResult result,
                        Location repairLocation);

}
