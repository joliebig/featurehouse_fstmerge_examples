

package net.sf.freecol.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;

import org.w3c.dom.Element;


public class HighScore extends FreeColObject {

    
    public static enum Level {
        CONTINENT(40000),
        COUNTRY(35000),
        STATE(30000),
        CITY(25000),
        MOUNTAIN_RANGE(20000),
        RIVER(15000),
        INSTITUTE(12000),
        UNIVERSITY(10000),
        STREET(8000),
        SCHOOL(7000),
        BIRD_OF_PREY(6000),
        TREE(5000),
        FLOWER(4000),
        RODENT(3200),
        FOUL_SMELLING_PLANT(2400),
        POISONOUS_PLANT(1600),
        SLIME_MOLD_BEETLE(800),
        BLOOD_SUCKING_INSECT(400),
        INFECTIOUS_DISEASE(200),
        PARASITIC_WORM(0);

        private int minimumScore;

        Level(int minimumScore) {
            this.minimumScore = minimumScore;
        }

        public int getMinimumScore() {
            return minimumScore;
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    
    private int independenceTurn = -1;

    
    private String playerName;

    
    private String nationID;

    
    private String nationTypeID;

    
    private int score;

    
    private Level level;

    
    private String nationName;

    
    private String difficulty;

    
    private int units;

    
    private int colonies;

    
    private String newLandName;

    
    private Date date;


    public HighScore(Player player, Date theDate) {
        date = theDate;
        score = player.getScore();
        for (Level someLevel : Level.values()) {
            if (score >= someLevel.getMinimumScore()) {
                level = someLevel;
                break;
            }
        }
        playerName = player.getName();
        nationID = player.getNationID();
        nationTypeID = player.getNationType().getId();
        colonies = player.getColonies().size();
        units = player.getUnits().size();
        if (player.getPlayerType() == Player.PlayerType.INDEPENDENT) {
            independenceTurn = player.getGame().getTurn().getNumber();
            nationName = player.getIndependentNationName();
        } else {
            independenceTurn = -1;
        }
        difficulty = player.getDifficulty().getId();
        newLandName = player.getNewLandName();
    }

    public HighScore(XMLStreamReader in) throws XMLStreamException {
        readFromXMLImpl(in);
    }

    public HighScore(Element element) throws XMLStreamException {
        readFromXMLElement(element);
    }


    
    public final int getIndependenceTurn() {
        return independenceTurn;
    }

    
    public final void setIndependenceTurn(final int newIndependenceTurn) {
        this.independenceTurn = newIndependenceTurn;
    }

    
    public final String getPlayerName() {
        return playerName;
    }

    
    public final void setPlayerName(final String newPlayerName) {
        this.playerName = newPlayerName;
    }

    
    public final String getNationID() {
        return nationID;
    }

    
    public final void setNationID(final String newNationID) {
        this.nationID = newNationID;
    }

    
    public final String getNationTypeID() {
        return nationTypeID;
    }

    
    public final void setNationTypeID(final String newNationTypeID) {
        this.nationTypeID = newNationTypeID;
    }

    
    public final int getScore() {
        return score;
    }

    
    public final void setScore(final int newScore) {
        this.score = newScore;
    }

    
    public final Level getLevel() {
        return level;
    }

    
    public final void setLevel(final Level newLevel) {
        this.level = newLevel;
    }

    
    public final String getOldNationName() {
        return Messages.message(nationID + ".name");
    }

    
    public final String getNationName() {
        return nationName;
    }

    
    public final void setNationName(final String newNationName) {
        this.nationName = newNationName;
    }

    
    public final String getNewLandName() {
        return newLandName;
    }

    
    public final void setNewLandName(final String newNewLandName) {
        this.newLandName = newNewLandName;
    }

    
    public final String getDifficulty() {
        return difficulty;
    }

    
    public final void setDifficulty(final String newDifficulty) {
        this.difficulty = newDifficulty;
    }

    
    public final int getUnits() {
        return units;
    }

    
    public final void setUnits(final int newUnits) {
        this.units = newUnits;
    }

    
    public final int getColonies() {
        return colonies;
    }

    
    public final void setColonies(final int newColonies) {
        this.colonies = newColonies;
    }

    
    public final Date getDate() {
        return date;
    }

    
    public final void setDate(final Date newDate) {
        this.date = newDate;
    }

   
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("date", dateFormat.format(date));
        out.writeAttribute("independenceTurn", Integer.toString(independenceTurn));
        out.writeAttribute("playerName", playerName);
        out.writeAttribute("nationID", nationID);
        out.writeAttribute("nationTypeID", nationTypeID);
        out.writeAttribute("score", Integer.toString(score));
        out.writeAttribute("level", level.toString());
        if (nationName != null) {
            out.writeAttribute("nationName", nationName);
        }
        out.writeAttribute("newLandName", newLandName);
        out.writeAttribute("difficulty", difficulty);
        out.writeAttribute("units", Integer.toString(units));
        out.writeAttribute("colonies", Integer.toString(colonies));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {

        try {
            date = dateFormat.parse(getAttribute(in, "date", "2008-01-01 00:00:00+0000"));
        } catch (Exception e) {
            logger.warning(e.toString());
            date = new Date();
        }
        independenceTurn = getAttribute(in, "independenceTurn", 0);
        playerName = getAttribute(in, "playerName", "");
        nationID = getAttribute(in, "nationID", "model.nation.dutch");
        nationTypeID = getAttribute(in, "nationTypeID", "model.nationType.trade");
        score = getAttribute(in, "score", 0);
        level = Enum.valueOf(Level.class, getAttribute(in, "level", "PARASITIC_WORM"));
        nationName = getAttribute(in, "nationName", null);
        newLandName = getAttribute(in, "nationName", "New World");
        difficulty = getAttribute(in, "difficulty", "model.difficulty.medium");
        units = getAttribute(in, "units", 0);
        colonies = getAttribute(in, "colonies", 0);

        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "highScore";
    }

}