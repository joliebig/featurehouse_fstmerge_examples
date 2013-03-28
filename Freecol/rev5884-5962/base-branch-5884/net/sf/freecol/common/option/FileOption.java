

package net.sf.freecol.common.option;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class FileOption extends AbstractOption {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(FileOption.class.getName());

    private File value = null;


    
    public FileOption(XMLStreamReader in) throws XMLStreamException {
        super(NO_ID);
        readFromXML(in);
    }

    
    public File getValue() {
        return value;
    }

    
    public void setValue(File value) {
        final File oldValue = this.value;
        this.value = value;

        if (value != oldValue) {
            firePropertyChange("value", oldValue, value);
        }
        isDefined = true;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        if (value != null) {
            out.writeAttribute("value", value.getAbsolutePath());
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        if (id == null && getId().equals("NO_ID")){
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no id attribute found.");
        }

        if(getId() == NO_ID) {
            setId(id);
        }
        if (in.getAttributeValue(null, "value") != null && !in.getAttributeValue(null, "value").equals("")) {
            setValue(new File(in.getAttributeValue(null, "value")));
        }
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "fileOption";
    }
}
