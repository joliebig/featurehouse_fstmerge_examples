


package net.sf.freecol.common.model;

import net.sf.freecol.client.gui.i18n.Messages;



public class Turn {

    public static final int STARTING_YEAR = 1492;
    public static final int SEASON_YEAR = 1600;

    private int turn;

    public Turn(int turn) {
        this.turn = turn;
    }

    
    
    public void increase() {
        turn++;
    }


    
    public int getNumber() {
        return turn;
    }

    
    
    public void setNumber(int turn) {
        this.turn = turn;
    }

    
    
    public int getAge() {
        if (getYear() < SEASON_YEAR) {
            return 1;
        } else if (getYear() < 1700) {
            return 2;
        } else {
            return 3;
        }
    }


    
    public boolean equals(Object o) {

        if ( ! (o instanceof Turn) ) { return false; }

        return turn == ((Turn) o).turn;
    }

    
    
    public static int getYear(int turn) {
        if (STARTING_YEAR + turn - 1 < SEASON_YEAR) {
            return STARTING_YEAR + turn - 1;
        }

        int c = turn - (SEASON_YEAR - STARTING_YEAR - 1);
        return SEASON_YEAR + c/2 - 1;
    }


    
    public int getYear() {
        return getYear(turn);
    }


    
    public String toString() {
        return toString(turn);
    }


    
    public static String toString(int turn) {
        if (STARTING_YEAR + turn - 1 < SEASON_YEAR) {
            return Integer.toString(STARTING_YEAR + turn - 1);
        }

        int c = turn - (SEASON_YEAR - STARTING_YEAR - 1);
        return ((c%2==0) ? Messages.message("spring") : Messages.message("autumn"))
            + " " + Integer.toString(SEASON_YEAR + c/2 - 1);
    }

    
    public String toSaveGameString() {
        if (STARTING_YEAR + turn - 1 < SEASON_YEAR) {
            return Integer.toString(STARTING_YEAR + turn - 1);
        }

        int c = turn - (SEASON_YEAR - STARTING_YEAR - 1);
        String result = Integer.toString(SEASON_YEAR + c/2 - 1);
        if (c % 2 == 0) {
            result += "_1_" + Messages.message("spring");
        } else {
            result += "_2_" + Messages.message("autumn");
        }
        return result;
    }
}
