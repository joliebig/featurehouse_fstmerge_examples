


package net.sf.freecol.common.model;


public class Turn {

    public static enum Season { YEAR, SPRING, AUTUMN }

    public static final int STARTING_YEAR = 1492;
    public static final int SEASON_YEAR = 1600;
    private static final int OFFSET = SEASON_YEAR - STARTING_YEAR - 1;

    
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
        if (o instanceof Turn) {
            return turn == ((Turn) o).turn;
        } else {
            return false;
        }
    }

    
    
    public static int getYear(int turn) {
        int c = turn - OFFSET;
        if (c < 0) {
            return STARTING_YEAR + turn - 1;
        } else {
            return SEASON_YEAR + c/2 - 1;
        }
    }


    
    public int getYear() {
        return getYear(turn);
    }


    
    public String toString() {
        return toString(turn);
    }

    
    public static String toString(int turn) {
        return getSeason(turn).toString() + " " + Integer.toString(getYear(turn));
    }

    
    public static Season getSeason(int turn) {
        int c = turn - OFFSET;
        if (c <= 1) {
            return Season.YEAR;
        } else if (c % 2 == 0) {
            return Season.SPRING;
        } else {
            return Season.AUTUMN;
        }
    }

    
    public Season getSeason() {
        return getSeason(turn);
    }


    
    public StringTemplate getLabel() {
        return getLabel(turn);
    }

    
    public static StringTemplate getLabel(int turn) {
        return StringTemplate.template("year." + getSeason(turn))
            .addAmount("%year%", getYear(turn));
    }

}
