


package net.sf.freecol.server.ai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.FreeColGameObjectListener;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;


public class AIMain extends FreeColObject implements FreeColGameObjectListener {
    private static final Logger logger = Logger.getLogger(AIMain.class.getName());


    private FreeColServer freeColServer;
    private int nextID = 1;

    
    private HashMap<String, AIObject> aiObjects = new HashMap<String, AIObject>();




    
    public AIMain(FreeColServer freeColServer) {
        this.freeColServer = freeColServer;
        findNewObjects();
    }
    
    
    public AIMain(FreeColServer freeColServer, Element element) {
        this(freeColServer);
        readFromXMLElement(element);
    }

    
     public AIMain(FreeColServer freeColServer, XMLStreamReader in) throws XMLStreamException {
         this(freeColServer);
         readFromXML(in);
     }
     

    
    public FreeColServer getFreeColServer() {
        return freeColServer;
    }

    
    public String getNextID() {
        String id = "am" + Integer.toString(nextID);
        nextID++;
        return id;
    }

    
    public boolean checkIntegrity() {
        boolean ok = true;
        for (AIObject ao : aiObjects.values()) {
            if (ao.isUninitialized()) {
                logger.warning("Uninitialized object: " + ao.getId() + " (" + ao.getClass() + ")");
                ok = false;
            }
        }
        Iterator<FreeColGameObject> fit = getGame().getFreeColGameObjectIterator();
        while (fit.hasNext()) {
            FreeColGameObject f = fit.next();
            if ((f instanceof Unit || f instanceof Colony ||
                 (f instanceof Player && !((Player)f).getName().equals(Player.UNKNOWN_ENEMY)) )
                    && !aiObjects.containsKey(f.getId())) {
                logger.warning("Missing AIObject for: " + f.getId());
                ok = false;
            }
        }
        if (ok) {
            logger.info("AIMain integrity ok.");
        } else {
            logger.warning("AIMain integrity test failed.");
        }
        return ok;
    }

    
    public Game getGame() {
        return freeColServer.getGame();
    }


    
    public PseudoRandom getRandom() {
        return freeColServer.getPseudoRandom();
    }


    
    private void findNewObjects() {
        findNewObjects(true);
    }


    
    public void findNewObjects(boolean overwrite) {
        Iterator<FreeColGameObject> i = freeColServer.getGame().getFreeColGameObjectIterator();
        while (i.hasNext()) {
            FreeColGameObject fcgo = i.next();
            if (overwrite || getAIObject(fcgo) == null) {
                setFreeColGameObject(fcgo.getId(), fcgo);
            }
        }
    }


    
    public AIObject getAIObject(FreeColGameObject fcgo) {
        return getAIObject(fcgo.getId());
    }


    
    public AIObject getAIObject(String id) {        
        return aiObjects.get(id);
    }


    
    public void addAIObject(String id, AIObject aiObject) {
        if (aiObjects.containsKey(id)) {
            throw new IllegalStateException("AIObject already created: " + id);
        }
        if (aiObject == null) {
            throw new NullPointerException("aiObject == null");
        }
        aiObjects.put(id, aiObject);
    }


    
    public void removeAIObject(String id) {
        aiObjects.remove(id);
    }


    
    public FreeColGameObject getFreeColGameObject(String id) {
        return freeColServer.getGame().getFreeColGameObject(id);
    }

    public void ownerChanged(FreeColGameObject source, Player oldOwner, Player newOwner) {
        AIObject ao = getAIObject(source);
        if (ao != null) {
            ao.dispose();
            setFreeColGameObject(source.getId(), source);
        }
    }

    
    public void setFreeColGameObject(String id, FreeColGameObject freeColGameObject) {
        if (aiObjects.containsKey(id)) {
            return;
        }
        if (!id.equals(freeColGameObject.getId())) {
            throw new IllegalArgumentException("!id.equals(freeColGameObject.getId())");
        }
        if (freeColGameObject instanceof Unit) {
            new AIUnit(this, (Unit) freeColGameObject);
        } else if (freeColGameObject instanceof ServerPlayer) {
            if (FreeCol.usesExperimentalAI()) {
                ServerPlayer p = (ServerPlayer) freeColGameObject;
                if (!p.isREF() && !p.isIndian() && p.isEuropean()) {
                    logger.info("Using experimental ColonialAIPlayer for "+p.getName());
                    new ColonialAIPlayer(this, p);
                } else {
                    new StandardAIPlayer(this, p);
                }
            } else {
                new StandardAIPlayer(this, (ServerPlayer) freeColGameObject);
            }
        } else if (freeColGameObject instanceof Colony) {
            new AIColony(this, (Colony) freeColGameObject);
        }
    }


    
    public void removeFreeColGameObject(String id) {
        AIObject o = getAIObject(id);
        if (o != null) {
            o.dispose();
        }
        removeAIObject(id);
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("nextID", Integer.toString(nextID));

        Iterator<AIObject> i = aiObjects.values().iterator();
        while (i.hasNext()) {
            AIObject aio = i.next();
            
            if ((aio instanceof Wish) && !((Wish) aio).shouldBeStored()) {
                continue;
            }

            try {
                if (aio.getId() != null) {
                    aio.toXML(out);
                } else {
                    logger.warning("aio.getId() == null, for: " + aio.getClass().getName());
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.warning(sw.toString());
            }
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        aiObjects.clear();
        
        if (!in.getLocalName().equals(getXMLElementTagName())) {
            logger.warning("Expected element name, got: " + in.getLocalName());
        }
        final String nextIDStr = in.getAttributeValue(null, "nextID");
        if (nextIDStr != null) {
            nextID = Integer.parseInt(nextIDStr);
        }
        
        String lastTag = "";
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            final String tagName = in.getLocalName();
            try {         
                final String oid = in.getAttributeValue(null, "ID");
                if (oid != null && aiObjects.containsKey(oid)) {
                    getAIObject(oid).readFromXML(in);
                } else if (tagName.equals(AIUnit.getXMLElementTagName())) {
                    new AIUnit(this, in);
                } else if (tagName.equals(AIPlayer.getXMLElementTagName())) {
                    new StandardAIPlayer(this, in);
                } else if (tagName.equals(ColonialAIPlayer.getXMLElementTagName())) {
                    new ColonialAIPlayer(this, in);
                } else if (tagName.equals(AIColony.getXMLElementTagName())) {
                    new AIColony(this, in);
                } else if (tagName.equals(AIGoods.getXMLElementTagName())) {
                    new AIGoods(this, in);
                } else if (tagName.equals(WorkerWish.getXMLElementTagName())) {
                    new WorkerWish(this, in);
                } else if (tagName.equals(GoodsWish.getXMLElementTagName())) {
                    new GoodsWish(this, in);
                } else if (tagName.equals(TileImprovementPlan.getXMLElementTagName())) {
                    new TileImprovementPlan(this, in);                
                } else {
                    logger.warning("Unkown AI-object read: " + tagName + "(" + lastTag + ")");
                }
                lastTag = in.getLocalName();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.warning("Exception while reading an AIObject: " + sw.toString());
                while (!in.getLocalName().equals(tagName) && !in.getLocalName().equals(getXMLElementTagName())) {
                    in.nextTag();                    
                }
                if (!in.getLocalName().equals(getXMLElementTagName())) {
                    in.nextTag();
                }
            }
        }
        
        if (!in.getLocalName().equals(getXMLElementTagName())) {
            logger.warning("Expected element name (2), got: " + in.getLocalName());
        }
        
        
        findNewObjects(false);
    }


    
    public static String getXMLElementTagName() {
        return "aiMain";
    }
    
    
    public HashMap<String, Long> getAIStatistics() {
        
        HashMap<String, Long> map = new HashMap<String, Long>();
        Iterator<AIObject> iter = aiObjects.values().iterator();
        while (iter.hasNext()) {
            AIObject obj = iter.next();
            String className = obj.getClass().getSimpleName();
            if (map.containsKey(className)) {
                Long count = map.get(className);
                count++;
                map.put(className, count);
            } else {
                Long count = new Long(1);
                map.put(className, count);
            }
        }
        
        return map;
    }
}
