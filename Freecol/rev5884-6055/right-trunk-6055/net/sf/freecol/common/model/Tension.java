


package net.sf.freecol.common.model;

import net.sf.freecol.client.gui.i18n.Messages;

public class Tension {

    
    
    public static final int TENSION_ADD_MINOR = 100;
    
    public static final int TENSION_ADD_NORMAL = 200;
    
    public static final int TENSION_ADD_MAJOR = 300;
    
    public static final int TENSION_ADD_LAND_TAKEN = 200;
    
    public static final int TENSION_ADD_UNIT_DESTROYED = 400;
    
    public static final int TENSION_ADD_SETTLEMENT_ATTACKED = 500;
    
    public static final int TENSION_ADD_CAPITAL_ATTACKED = 600;

    
    public static final int TENSION_ADD_DECLARE_WAR_FROM_PEACE = 1000;

    
    public static final int TENSION_ADD_DECLARE_WAR_FROM_CEASE_FIRE = 750;
    
    
    public static final int PEACE_TREATY_MODIFIER = -250;

    
    public static final int CEASE_FIRE_MODIFIER = -250;
    
    
    public static final int ALLIANCE_MODIFIER = -500;
    
    
    public static enum Level { 
        HAPPY(100),
        CONTENT(600), 
        DISPLEASED(700),
        ANGRY(800), 
        HATEFUL(1000);

        private int limit;

        Level(int limit) {
            this.limit = limit;
        }

        public int getLimit() {
            return limit;
        }
    }
    
    static int SURRENDED = (Level.CONTENT.limit + Level.HAPPY.limit) / 2;
    private int value;

    
    public Tension() {
        setValue(Level.HAPPY.getLimit());
    }

    public Tension(int newTension) {
        setValue(newTension);
    }

    
    public int getValue() {
        return this.value;
    }

    
    public void setValue(int newValue) {
        if (newValue < 0) {
            value = 0;
        } else if (newValue > Level.HATEFUL.getLimit()) {
            value = Level.HATEFUL.getLimit();
        } else {
            value = newValue;
        }
    }

    
    public Level getLevel() {
        for (Level level : Level.values()) {
            if (value <= level.getLimit())
                return level;
        }
        return Level.HATEFUL;
   }

    public void setLevel(Level level) {
        if (level != getLevel()) {
            setValue(level.getLimit());
        }
    }

    
    public void modify(int newTension) {
        setValue(value + newTension);
    }

    
    public String toString() {
        return Messages.message(getLevel().toString().toLowerCase());
    }    

}



