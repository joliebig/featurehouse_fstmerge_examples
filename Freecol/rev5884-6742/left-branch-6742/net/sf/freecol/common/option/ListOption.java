package net.sf.freecol.common.option;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class ListOption<T> extends AbstractOption {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ListOption.class.getName());

    private ListOptionSelector<T> selector;
    private List<T> value;

    
    public ListOption(ListOptionSelector<T> selector, XMLStreamReader in) throws XMLStreamException {
        super(NO_ID);
        value = new ArrayList<T>();
        this.selector = selector;
        readFromXML(in);
    }

    
    public ListOption(ListOptionSelector<T> selector, String id, T... defaultValues) {
        this(selector, id, null, defaultValues);
    }

    
    public ListOption(ListOptionSelector<T> selector, String id, OptionGroup optionGroup, T... defaultValues) {
        super(id, optionGroup);
        value = new ArrayList<T>();
        this.selector = selector;
        for (T s : defaultValues) {
            value.add(s);
        }
    }

    
    public ListOptionSelector<T> getListOptionSelector() {
        return selector;
    }

    
    public List<T> getValue() {
        return new ArrayList<T>(value);
    }
    
    
    
    public void setValue(List<T> value) {
        final List<T> oldValue = this.value;
        this.value = value;
        
        if (value != oldValue && isDefined) {
            firePropertyChange("value", oldValue, value);
        }
        isDefined = true;
    }

    private List<String> getValueIds() {
        final List<String> ids = new ArrayList<String>(value.size());
        for (T t : value) {
            ids.add(selector.getId(t));
        }
        return ids;
    }
    
    private void setValueIds(final List<String> ids) {
        final List<T> value = new ArrayList<T>(ids.size());
        for (String id : ids) {
            value.add(selector.getObject(id));
        }
        setValue(value);
    }
    
    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        toListElement("value", getValueIds(), out);

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");

        if (id == null && getId().equals("NO_ID")){
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no id attribute found.");
        }
        
        setValueIds(readFromListElement("value", in, String.class));
        
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "listOption";
    }
}
