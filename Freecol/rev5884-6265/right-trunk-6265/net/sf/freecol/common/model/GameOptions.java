


package net.sf.freecol.common.model;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.option.OptionMap;
import org.w3c.dom.Element;



public class GameOptions extends OptionMap {

    
    public static final String STARTING_MONEY = "model.option.startingMoney";

    
    
    

    
    public static final String CUSTOM_IGNORE_BOYCOTT = "model.option.customIgnoreBoycott";

    
    public static final String EXPERTS_HAVE_CONNECTIONS = "model.option.expertsHaveConnections";

    public static final String SAVE_PRODUCTION_OVERFLOW = "model.option.saveProductionOverflow";

    
    public static final String EXPLORATION_POINTS = "model.option.explorationPoints";

    
    public static final String FOG_OF_WAR = "model.option.fogOfWar";

    
    public static final String VICTORY_DEFEAT_REF = "model.option.victoryDefeatREF";
    
    
    public static final String VICTORY_DEFEAT_EUROPEANS = "model.option.victoryDefeatEuropeans";    

    
    public static final String VICTORY_DEFEAT_HUMANS = "model.option.victoryDefeatHumans";

    
    public static final String EDUCATE_LEAST_SKILLED_UNIT_FIRST =
        "model.option.educateLeastSkilledUnitFirst";

    
    public static final String DIFFICULTY = "model.option.difficulty";
    
    
    public GameOptions() {
        super(getXMLElementTagName());
    }


    
    public GameOptions(XMLStreamReader in) throws XMLStreamException {
        super(in, getXMLElementTagName());
    }
    
    
    public GameOptions(Element e) {
        super(e, getXMLElementTagName());
    }




    
    protected void addDefaultOptions() {
        Specification spec = Specification.getSpecification();
        
        
        add(spec.getOptionGroup("gameOptions.map"));
        
        add(spec.getOptionGroup("gameOptions.colony"));
        
        add(spec.getOptionGroup("gameOptions.victoryConditions"));
        
        add(spec.getOptionGroup("gameOptions.difficultySettings"));

    }

    protected boolean isCorrectTagName(String tagName) {
        return getXMLElementTagName().equals(tagName);
    }

    
    public static String getXMLElementTagName() {
        return "gameOptions";
    }

}
