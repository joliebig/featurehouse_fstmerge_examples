

package net.sf.freecol.client.gui.action;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.TileImprovementType;


public final class ImprovementActionType extends FreeColGameObjectType {

    private char accelerator;
    
    private final List<String> names;
    private final List<TileImprovementType> impTypes;
    private final List<String> imageIDs;
    
    
    
    public ImprovementActionType() {
        names = new ArrayList<String>();
        impTypes = new ArrayList<TileImprovementType>();
        imageIDs = new ArrayList<String>();
    }

    

    public char getAccelerator() {
        return accelerator;
    }

    public List<String> getNames() {
        return names;
    }

    public List<TileImprovementType> getImpTypes() {
        return impTypes;
    }
    
    public List<String> getImageIDs() {
        return imageIDs;
    }

    

    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        throw new UnsupportedOperationException("Call 'readFromXML' instead.");
    }

    public void readFromXML(XMLStreamReader in, Specification specification)
           throws XMLStreamException {
        setId(in.getAttributeValue(null, "id"));
        accelerator = in.getAttributeValue(null, "accelerator").charAt(0);

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            names.add(in.getAttributeValue(null, "name"));
            String t = in.getAttributeValue(null, "tileimprovement-type");
            impTypes.add(specification.getTileImprovementType(t));
            imageIDs.add(in.getAttributeValue(null, "image-id"));
            in.nextTag(); 
        }
    }   
}
