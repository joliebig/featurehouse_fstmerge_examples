

package net.sf.freecol.common.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.option.AbstractOption;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.StringOption;



public class DifficultyLevel extends FreeColGameObjectType {

    private static int nextIndex = 0;

    private final Map<String, AbstractOption> levelOptions =
        new LinkedHashMap<String, AbstractOption>();


    public DifficultyLevel() {
        setIndex(nextIndex++);
    }

    public AbstractOption getOption(String Id) throws IllegalArgumentException {
        if (Id == null) {
            throw new IllegalArgumentException("Trying to retrieve AbstractOption" + " with ID 'null'.");
        } else if (!levelOptions.containsKey(Id)) {
            throw new IllegalArgumentException("Trying to retrieve AbstractOption" + " with ID '" + Id
                    + "' returned 'null'.");
        } else {
            return levelOptions.get(Id);
        }
    }

    public Map<String, AbstractOption> getOptions() {
        return levelOptions;
    }
    
    public void readFromXML(XMLStreamReader in, Specification specification)
        throws XMLStreamException {

        final String id = in.getAttributeValue(null, "id");
        
        if (id == null){
            throw new XMLStreamException("invalid <" + getXMLElementTagName() +
                                         "> tag : no id attribute found.");
        }

        setId(id);

        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            String optionType = in.getLocalName();
            if (IntegerOption.getXMLElementTagName().equals(optionType) ||
                "integer-option".equals(optionType)) {
                IntegerOption option = new IntegerOption(in);
                levelOptions.put(option.getId(), option);
            } else if (BooleanOption.getXMLElementTagName().equals(optionType) ||
                       "boolean-option".equals(optionType)) {
                BooleanOption option = new BooleanOption(in);
                levelOptions.put(option.getId(), option);
            } else if (StringOption.getXMLElementTagName().equals(optionType) ||
                       "string-option".equals(optionType)) {
                StringOption option = new StringOption(in);
                levelOptions.put(option.getId(), option);
            } else {
                logger.finest("Parsing of " + optionType + " is not implemented yet");
                in.nextTag();
            }
        }

    }

    public void toXML(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        out.writeAttribute(ID_ATTRIBUTE_TAG, getId());

        for (AbstractOption option : levelOptions.values()) {
            option.toXML(out);
        }

        out.writeEndElement();

    }

    
    public static String getXMLElementTagName() {
        return "difficultyLevel";
    }
    

}
