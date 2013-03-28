


package net.sf.freecol.common.model;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;



public abstract class TradeItem extends FreeColObject {

    
    private Game game;

    
    private Player source;

    
    private Player destination;

    
    public TradeItem(Game game, String id, Player source, Player destination) {
        this.game = game;
        setId(id);
        this.source = source;
        this.destination = destination;
    }

    
    public TradeItem(Game game, XMLStreamReader in) throws XMLStreamException {
        this.game = game;
    }

    
    public final Game getGame() {
        return game;
    }

    
    public final void setGame(final Game newGame) {
        this.game = newGame;
    }

    
    public final Player getSource() {
        return source;
    }

    
    public final void setSource(final Player newSource) {
        this.source = newSource;
    }

    
    public final Player getDestination() {
        return destination;
    }

    
    public final void setDestination(final Player newDestination) {
        this.destination = newDestination;
    }

    
    public abstract boolean isValid();


    
    public abstract boolean isUnique();

    
    public abstract List<FreeColGameObject> makeTrade();

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        String sourceID = in.getAttributeValue(null, "source");
        this.source = (Player) game.getFreeColGameObject(sourceID);
        String destinationID = in.getAttributeValue(null, "destination");
        this.destination = (Player) game.getFreeColGameObject(destinationID);
    }

    
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeAttribute("ID", getId());
        out.writeAttribute("source", this.source.getId());
        out.writeAttribute("destination", this.destination.getId());
    }
    

}

