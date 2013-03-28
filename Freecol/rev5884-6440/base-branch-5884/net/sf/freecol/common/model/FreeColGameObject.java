

package net.sf.freecol.common.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.util.Utils;
import net.sf.freecol.server.model.ServerGame;
import net.sf.freecol.common.util.Introspector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


abstract public class FreeColGameObject extends FreeColObject {

    private static final Logger logger = Logger.getLogger(FreeColGameObject.class.getName());

    private Game game;
    private boolean disposed = false;
    private boolean uninitialized;

    protected FreeColGameObject() {    
        logger.info("FreeColGameObject without ID created.");
        uninitialized = false;
    }
    

    
    public FreeColGameObject(Game game) {
        this.game = game;

        if (game != null && game instanceof ServerGame) {
            setId(getRealXMLElementTagName() + ":" + ((ServerGame)game).getNextID());
        } else if (this instanceof Game) {
            setId("0");
        } else {
            logger.warning("Created 'FreeColGameObject' with 'game == null': " + this);
        }
        
        uninitialized = false;
    }
    
    
    
    public FreeColGameObject(Game game, XMLStreamReader in) throws XMLStreamException {
        this.game = game;

        if (game == null && !(this instanceof Game)) {
            logger.warning("Created 'FreeColGameObject' with 'game == null': " + this);
        }

        uninitialized = false;
    }

    
    public FreeColGameObject(Game game, Element e) {
        this.game = game;

        if (game == null && !(this instanceof Game)) {
            logger.warning("Created 'FreeColGameObject' with 'game == null': " + this);
        }

        uninitialized = false;
    }

    
    public FreeColGameObject(Game game, String id) {
        this.game = game;

        if (game == null && !(this instanceof Game)) {
            logger.warning("Created 'FreeColGameObject' with 'game == null': " + this);
        }

        setId(id);
        
        uninitialized = true;
    }
    
    
    
    public Game getGame() {
        return game;
    }


    
    public GameOptions getGameOptions() {
        return game.getGameOptions();
    }

    
    
    public void setGame(Game game) {
        this.game = game;
    }    


    
    public void dispose() {
        disposed = true;
        getGame().removeFreeColGameObject(getId());
    }
    

    
    public boolean isDisposed() {
        return disposed;
    }

    
    public boolean isUninitialized() {
        return uninitialized;
    }

    
    public void updateID() {
        
    }
    
    
    public void toSavedXML(XMLStreamWriter out) throws XMLStreamException {
        toXML(out, null, true, true);
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        toXMLImpl(out, null, false, false);
    }
            
        
    abstract protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, 
                                      boolean toSavedGame) throws XMLStreamException;


        
    public final void toXML(XMLStreamWriter out, Player player, boolean showAll,
                            boolean toSavedGame) throws XMLStreamException {
        if (toSavedGame && !showAll) {
            throw new IllegalArgumentException("'showAll' should be true when saving a game.");
        }
        toXMLImpl(out, player, showAll, toSavedGame);
    }
    
    
    public final void readFromXML(XMLStreamReader in) throws XMLStreamException {
        uninitialized = false;
        super.readFromXML(in);
    }

    
    public Integer getIntegerID() {
        String stringPart = getRealXMLElementTagName() + ":";
        return new Integer(getId().substring(stringPart.length()));
    }

    private String getRealXMLElementTagName() {
        String tagName = "";
        try {
            Method m = getClass().getMethod("getXMLElementTagName", (Class[]) null);
            tagName = (String) m.invoke((Object) null, (Object[]) null);
        } catch (Exception e) {}
        return tagName;
    }

    
    public final void setId(String newID) {
        if (game != null && !(this instanceof Game)) {
            if (!newID.equals(getId())) {
                if (getId() != null) {
                    game.removeFreeColGameObject(getId());
                }

                super.setId(newID);
                game.setFreeColGameObject(newID, this);
            }
        } else {
            super.setId(newID);
        }
    }

    
    
    public void setFakeID(String newID) {
        super.setId(newID);
    }

    
        
    protected void addModelMessage(FreeColGameObject source, ModelMessage.MessageType type,
                                   String messageID, String... data) {
        addModelMessage(source, type, null, messageID, data);
    }

        
    protected void addModelMessage(FreeColGameObject source, ModelMessage.MessageType type, 
                                   FreeColObject display, String messageID, String... data) {
        ModelMessage message = new ModelMessage(source, type, display, messageID, data);
        
        addModelMessage(message);
    }

    
    protected void addModelMessage(ModelMessage message){
    	if (message.getSource() == null) {
    		logger.warning("ModelMessage with ID " + message.getId() + " has null source.");
    	} else if (message.getOwner() == null) {
    		logger.warning("ModelMessage with ID " + message.getId() + " has null owner.");
    	}
    	message.getOwner().addModelMessage(message);
    }
    
    
    public boolean hasID(String id) {
        return getId().equals(id);
    }

    
    public boolean equals(FreeColGameObject o) {
        if (o != null) {
            return Utils.equals(this.getGame(), o.getGame()) && getId().equals(o.getId());
        } else {
            return false;
        }
    }
    
    
    public boolean equals(Object o) {
        return (o instanceof FreeColGameObject) ? equals((FreeColGameObject) o) : false;
    }
        
    public int hashCode() {
        return getId().hashCode();
    }

    
    
    public String toString() {
        return getClass().getName() + ": " + getId() + " (super's hash code: " +
            Integer.toHexString(super.hashCode()) + ")";
    }

    public <T extends FreeColGameObject> T getFreeColGameObject(XMLStreamReader in, String attributeName,
                                                                Class<T> returnClass) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString == null) {
            return null;
        } else {
            T returnValue = returnClass.cast(getGame().getFreeColGameObject(attributeString));
            try {
                if (returnValue == null) {
                    Constructor<T> c = returnClass.getConstructor(Game.class, String.class);
                    returnValue = returnClass.cast(c.newInstance(getGame(), attributeString));
                }
                return returnValue;
            } catch(Exception e) {
                logger.warning("Failed to create FreeColGameObject with ID " + attributeString);
                return null;
            }
        }
    }

    public <T extends FreeColGameObject> T getFreeColGameObject(XMLStreamReader in, String attributeName,
                                                                Class<T> returnClass, T defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return returnClass.cast(getGame().getFreeColGameObject(attributeString));
        } else {
            return defaultValue;
        }
    }

    public <T extends FreeColGameObject> T updateFreeColGameObject(XMLStreamReader in, Class<T> returnClass) {
        final String attributeString = in.getAttributeValue(null, ID_ATTRIBUTE);
        if (attributeString == null) {
            return null;
        } else {
            T returnValue = returnClass.cast(getGame().getFreeColGameObject(attributeString));
            try {
                if (returnValue == null) {
                    Constructor<T> c = returnClass.getConstructor(Game.class, XMLStreamReader.class);
                    returnValue = returnClass.cast(c.newInstance(getGame(), in));
                } else {
                    returnValue.readFromXML(in);
                }
                return returnValue;
            } catch(Exception e) {
                logger.warning("Failed to update FreeColGameObject with ID " + attributeString);
                e.printStackTrace();
                return null;
            }
        }
    }

    
    protected void toXMLPartialByClass(XMLStreamWriter out,
                                       Class<?> theClass, String[] fields)
        throws XMLStreamException {
        
        try {
            Introspector tag = new Introspector(theClass, "XMLElementTagName");
            out.writeStartElement(tag.getter(this));
        } catch (IllegalArgumentException e) {
            logger.warning("Could not get tag field: " + e.toString());
        }

        
        out.writeAttribute(ID_ATTRIBUTE, getId());
        out.writeAttribute(PARTIAL_ATTRIBUTE, String.valueOf(true));

        
        for (int i = 0; i < fields.length; i++) {
            try {
                Introspector intro = new Introspector(theClass, fields[i]);
                out.writeAttribute(fields[i], intro.getter(this));
            } catch (IllegalArgumentException e) {
                logger.warning("Could not get field " + fields[i]
                               + ": " + e.toString());
            }
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLPartialByClass(XMLStreamReader in,
                                             Class<?> theClass)
        throws XMLStreamException {
        int n = in.getAttributeCount();

        setId(in.getAttributeValue(null, ID_ATTRIBUTE));

        for (int i = 0; i < n; i++) {
            String name = in.getAttributeLocalName(i);

            if (name.equals(ID_ATTRIBUTE)
                || name.equals(PARTIAL_ATTRIBUTE)) continue;

            try {
                Introspector intro = new Introspector(theClass, name);
                intro.setter(this, in.getAttributeValue(i));
            } catch (IllegalArgumentException e) {
                logger.warning("Could not set field " + name
                               + ": " + e.toString());
            }
        }

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT);
    }

    
    public void addToRemoveElement(Element removeElement) {
        Document doc = removeElement.getOwnerDocument();
        removeElement.appendChild(this.toXMLElementPartial(doc));
    }


    
    public static String getXMLElementTagName() {
        return "unknown";
    }

}
