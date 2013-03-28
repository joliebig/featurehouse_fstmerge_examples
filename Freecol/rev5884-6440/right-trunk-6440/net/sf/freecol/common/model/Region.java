


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;


public class Region extends FreeColGameObject implements Nameable {

    public static final String PACIFIC_NAME_KEY = "model.region.pacific";

    public static enum RegionType { OCEAN, COAST, LAKE, RIVER, LAND, MOUNTAIN, DESERT }

    
    private String name;

    
    private String nameKey;

    
    private Region parent;

    
    private boolean claimable = false;

    
    private boolean discoverable = false;

    
    private Turn discoveredIn;

    
    private Player discoveredBy;

    
    private boolean prediscovered = false;

    
    private int scoreValue = 0;

    
    private RegionType type;

    
    private List<Region> children;


    
    public Region(Game game) {
        super(game);
    }

    
    public Region(Game game, String id) {
        super(game, id);
    }

    
    public Region(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
        readFromXMLImpl(in);
    }

    
    public final String getNameKey() {
        return nameKey;
    }

    
    public final void setNameKey(final String newNameKey) {
        this.nameKey = newNameKey;
    }

    
    public boolean isPacific() {
        if (PACIFIC_NAME_KEY.equals(nameKey)) {
            return true;
        } else if (parent != null) {
            return parent.isPacific();
        } else {
            return false;
        }
    }

    
    public final String getName() {
        return name;
    }

    
    public final void setName(final String newName) {
        this.name = newName;
    }

    
    public String getDisplayName() {
        if (prediscovered || isPacific()) {
            return Messages.message(nameKey);
        } else if (name == null) {
            return Messages.message("model.region." + type.toString().toLowerCase() + ".unknown");
        } else {
            return name;
        }
    }

    public String getTypeName() {
        return Messages.message("model.region." + type.toString().toLowerCase() + ".name");
    }

    
    public final Region getParent() {
        return parent;
    }

    
    public final void setParent(final Region newParent) {
        this.parent = newParent;
    }

    
    public final List<Region> getChildren() {
        return children;
    }

    
    public final void setChildren(final List<Region> newChildren) {
        this.children = newChildren;
    }

    
    public final boolean isClaimable() {
        return claimable;
    }

    
    public final void setClaimable(final boolean newClaimable) {
        this.claimable = newClaimable;
    }

    
    public final boolean isDiscoverable() {
        return discoverable;
    }

    
    public final void setDiscoverable(final boolean newDiscoverable) {
        this.discoverable = newDiscoverable;
        if (discoverable) {
            prediscovered = false;
        }
    }

    
    public final boolean isPrediscovered() {
        return prediscovered;
    }

    
    public final void setPrediscovered(final boolean newPrediscovered) {
        this.prediscovered = newPrediscovered;
    }

    
    public final int getScoreValue() {
        return scoreValue;
    }

    
    public final void setScoreValue(final int newScoreValue) {
        this.scoreValue = newScoreValue;
    }

    
    public final RegionType getType() {
        return type;
    }

    
    public final void setType(final RegionType newType) {
        this.type = newType;
    }

    
    public boolean isRoot() {
        return parent == null;
    }

    
    public boolean isLeaf() {
        return children == null;
    }

    
    public Region getDiscoverableRegion() {
        if (isDiscoverable()) {
            return this;
        } else if (parent != null) {
            return parent.getDiscoverableRegion();
        } else {
            return null;
        }
    }

    
    public final Turn getDiscoveredIn() {
        return discoveredIn;
    }

    
    public final void setDiscoveredIn(final Turn newDiscoveredIn) {
        this.discoveredIn = newDiscoveredIn;
    }

    
    public final Player getDiscoveredBy() {
        return discoveredBy;
    }

    
    public final void setDiscoveredBy(final Player newDiscoveredBy) {
        this.discoveredBy = newDiscoveredBy;
    }

    
    public HistoryEvent discover(Player player, Turn turn, String newName) {
        discoveredBy = player;
        discoveredIn = turn;
        name = newName;
        discoverable = false;
        if (getGame().getGameOptions().getBoolean(GameOptions.EXPLORATION_POINTS) ||
            isPacific()) {
            player.modifyScore(getScoreValue());
        }
        HistoryEvent h = new HistoryEvent(turn.getNumber(),
                                          HistoryEvent.Type.DISCOVER_REGION,
                                          "%region%", Messages.message(newName));
        player.getHistory().add(h);
        return h;
    }

    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute("ID", getId());
        out.writeAttribute("nameKey", nameKey);
        out.writeAttribute("type", type.toString());
        if (name != null) {
            out.writeAttribute("name", name);
        }
        if (prediscovered) {
            out.writeAttribute("prediscovered", Boolean.toString(prediscovered));
        }
        if (claimable) {
            out.writeAttribute("claimable", Boolean.toString(claimable));
        }
        if (discoverable) {
            out.writeAttribute("discoverable", Boolean.toString(discoverable));
        }
        if (parent != null) {
            out.writeAttribute("parent", parent.getId());
        }
        if (discoveredIn != null) {
            out.writeAttribute("discoveredIn", String.valueOf(discoveredIn.getNumber()));
        }
        if (discoveredBy != null) {
            out.writeAttribute("discoveredBy", discoveredBy.getId());
        }
        if (scoreValue > 0) {
            out.writeAttribute("scoreValue", String.valueOf(scoreValue));
        }
        if (children != null) {
            String[] childArray = new String[children.size()];
            for (int index = 0; index < childArray.length; index++) {
                childArray[index] = children.get(index).getId();
            }
            toArrayElement("children", childArray, out);
        }
        out.writeEndElement();
    }
    
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        nameKey = in.getAttributeValue(null, "nameKey");
        name = in.getAttributeValue(null, "name");
        claimable = getAttribute(in, "claimable", false);
        discoverable = getAttribute(in, "discoverable", false);
        prediscovered = getAttribute(in, "prediscovered", false);
        scoreValue = getAttribute(in, "scoreValue", 0);
        type = Enum.valueOf(RegionType.class, in.getAttributeValue(null, "type"));
        int turn = getAttribute(in, "discoveredIn", -1);
        if (turn > 0) {
            discoveredIn = new Turn(turn);
        }
        discoveredBy = getFreeColGameObject(in, "discoveredBy", Player.class, null);
        parent = getFreeColGameObject(in, "parent", Region.class);

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals("children")) {
                String[] childArray = readFromArrayElement("children", in, new String[0]);
                children = new ArrayList<Region>();
                for (String child : childArray) {
                    children.add(getGame().getMap().getRegion(child));
                }
            }
        }

    }            

    
    public static String getXMLElementTagName() {
        return "region";
    }

    public String toString() {
        return nameKey;
    }
}
