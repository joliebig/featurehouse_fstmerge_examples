

package net.sf.freecol.common.option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;



public class OptionGroup extends AbstractOption {

    private static Logger logger = Logger.getLogger(OptionGroup.class.getName());

    private ArrayList<Option> options;


    
    public OptionGroup() {
        this(NO_ID);
    }

    
    public OptionGroup(String id) {
        super(id);
        options = new ArrayList<Option>();
    }
    
    
     public OptionGroup(XMLStreamReader in) throws XMLStreamException {
         this(NO_ID);
         readFromXML(in);
     }

    
    public void add(Option option) {
        options.add(option);
    }


    
    public void removeAll() {
        options.clear();
    }


    
    public Iterator<Option> iterator() {
        return options.iterator();
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        Iterator<Option> oi = options.iterator();
        while (oi.hasNext()) {
            (oi.next()).toXML(out);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        if(id != null){
            setId(id);
        }
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            AbstractOption option = null;
            String optionType = in.getLocalName();
            if (IntegerOption.getXMLElementTagName().equals(optionType) || "integer-option".equals(optionType)) {
                option = new IntegerOption(in);
            } else if (BooleanOption.getXMLElementTagName().equals(optionType) || "boolean-option".equals(optionType)) {
                option = new BooleanOption(in);
            } else if (RangeOption.getXMLElementTagName().equals(optionType) || "range-option".equals(optionType)) {
                option = new RangeOption(in);
            } else if (SelectOption.getXMLElementTagName().equals(optionType) || "select-option".equals(optionType)) {
                option = new SelectOption(in);
            } else if (LanguageOption.getXMLElementTagName().equals(optionType) || "language-option".equals(optionType)) {
                option = new LanguageOption(in);
            } else if (FileOption.getXMLElementTagName().equals(optionType) || "file-option".equals(optionType)) {
                option = new FileOption(in);
            } else {
                logger.finest("Parsing of " + optionType + " is not implemented yet");
                in.nextTag();
            }

            if (option != null) {
                add(option);
                option.setGroup(this.getId());
            }
        }
    }


    
    public static String getXMLElementTagName() {
        return "optionGroup";
    }
    
    
    public String getName() {
        return Messages.message(getId() + ".name");
    }
    
    
    public String getShortDescription() {
        return Messages.message(getId() + ".shortDescription");
    }

}
