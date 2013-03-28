

package net.sf.freecol.common.networking;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.server.ai.AIMain;

public class StatisticsMessage extends Message {
    
    private HashMap<String, Long> memoryStats = null;
    private HashMap<String, Long> gameStats = null;
    private HashMap<String, Long> aiStats = null;

    public StatisticsMessage(Game game, AIMain aiMain) {
        
        memoryStats = game.getMemoryStatistics();
        
        gameStats = game.getGameStatistics();
        
        if (aiMain!=null) {
            aiStats = aiMain.getAIStatistics();
        }
    }

    public StatisticsMessage(Element element) {
        readFromXML(element);
    }
    

    
    public final HashMap<String, Long> getGameStatistics() {
        return gameStats;
    }

    
    public final HashMap<String, Long> getAIStatistics() {
        return aiStats;
    }
    
    
    public final HashMap<String, Long> getMemoryStatistics() {
        return memoryStats;
    }

    public void readFromXML(Element element) {
        if (!element.getTagName().equals(getXMLElementTagName())) {
            return;
        }
        Element memoryElement = (Element)element.getElementsByTagName("memoryStatistics").item(0);
        if (memoryElement != null) {
            memoryStats = new HashMap<String, Long>();
            NamedNodeMap atts = memoryElement.getAttributes();
            for (int i=0; i<atts.getLength(); i++) {
                memoryStats.put(atts.item(i).getNodeName(), new Long(atts.item(i).getNodeValue()));
            }
        }
        Element gameElement = (Element)element.getElementsByTagName("gameStatistics").item(0);
        if (gameElement != null) {
            gameStats = new HashMap<String, Long>();
            NamedNodeMap atts = gameElement.getAttributes();
            for (int i=0; i<atts.getLength(); i++) {
                gameStats.put(atts.item(i).getNodeName(), new Long(atts.item(i).getNodeValue()));
            }
        }
        Element aiElement = (Element)element.getElementsByTagName("aiStatistics").item(0);
        if (aiElement != null) {
            aiStats = new HashMap<String, Long>();
            NamedNodeMap atts = aiElement.getAttributes();
            for (int i=0; i<atts.getLength(); i++) {
                aiStats.put(atts.item(i).getNodeName(), new Long(atts.item(i).getNodeValue()));
            }
        }
    }
    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        
        Element memoryElement = result.getOwnerDocument().createElement("memoryStatistics");
        result.appendChild(memoryElement);
        for (String s : memoryStats.keySet()) {
            memoryElement.setAttribute(s, memoryStats.get(s).toString());
        }
        
        Element gameElement = result.getOwnerDocument().createElement("gameStatistics");
        result.appendChild(gameElement);
        for (String s : gameStats.keySet()) {
            gameElement.setAttribute(s, gameStats.get(s).toString());
        }
        
        Element aiElement = result.getOwnerDocument().createElement("aiStatistics");
        result.appendChild(aiElement);
        for (String s : aiStats.keySet()) {
            aiElement.setAttribute(s, aiStats.get(s).toString());
        }
        return result;
    }

    public static String getXMLElementTagName() {
        return "statistics";
    }

}
