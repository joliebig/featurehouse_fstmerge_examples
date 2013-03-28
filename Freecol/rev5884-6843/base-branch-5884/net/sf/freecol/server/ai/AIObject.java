


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;


public abstract class AIObject extends FreeColObject {

    private static final Logger logger = Logger.getLogger(FreeColObject.class.getName());
    
    private final AIMain aiMain;
    protected boolean uninitialized = false;

    
    
    public AIObject(AIMain aiMain) {
        this.aiMain = aiMain;
    }

    
    public AIObject(AIMain aiMain, String id) {
        this.aiMain = aiMain;
        setId(id);
        aiMain.addAIObject(id, this);
    }

    
    
    public AIMain getAIMain() {
        return aiMain;
    }

    
    public boolean isUninitialized() {
        return uninitialized;
    }
    
    
    public final void readFromXML(XMLStreamReader in) throws XMLStreamException {
        super.readFromXML(in);
        uninitialized = false;
    }
    
    
    protected PseudoRandom getRandom() {
        return aiMain.getRandom();
    }

    
    
    public void dispose() {
        getAIMain().removeAIObject(getId());
    }
    
        
    
    public Game getGame() {
        return aiMain.getGame();
    }


    
    public static String getXMLElementTagName() {
        return "AIObject";
    }
}
