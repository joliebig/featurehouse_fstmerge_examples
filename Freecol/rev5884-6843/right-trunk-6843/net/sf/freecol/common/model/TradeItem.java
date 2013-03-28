


package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Player.Stance;



public abstract class TradeItem extends FreeColObject {

    
    protected Game game;

    
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

    
    public abstract void makeTrade();

    
    public Colony getColony() { return null; }

    
    public void setColony(Colony colony) {}

    
    public Goods getGoods() { return null; }

    
    public void setGoods(Goods goods) {}

    
    public int getGold() { return 0; }

    
    public void setGold(int gold) {}

    
    public Stance getStance() { return null; }

    
    public void setStance(Stance stance) {}

    
    public Unit getUnit() { return null; }

    
    public void setUnit(Unit unit) {}


    
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

