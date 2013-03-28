


package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class StringTemplate extends FreeColObject {

    
    public static enum TemplateType { NAME, KEY, TEMPLATE, LABEL }

    
    private TemplateType templateType = TemplateType.KEY;

    
    private List<String> keys;

    
    private List<StringTemplate> replacements;



    protected StringTemplate() {
        
    }

    public StringTemplate(String id, StringTemplate template) {
        setId(id);
        this.templateType = template.templateType;
        this.keys = template.keys;
        this.replacements = template.replacements;
    }

    
    protected StringTemplate(String template, TemplateType templateType) {
	setId(template);
        this.templateType = templateType;
        switch (templateType) {
        case TEMPLATE:
            keys = new ArrayList<String>();
        case LABEL:            
            replacements = new ArrayList<StringTemplate>();
        }
    }

    

    public static StringTemplate name(String value) {
        return new StringTemplate(value, TemplateType.NAME);
    }

    public static StringTemplate key(String value) {
        return new StringTemplate(value, TemplateType.KEY);
    }

    public static StringTemplate template(String value) {
        return new StringTemplate(value, TemplateType.TEMPLATE);
    }

    public static StringTemplate label(String value) {
        return new StringTemplate(value, TemplateType.LABEL);
    }


    
    public final TemplateType getTemplateType() {
        return templateType;
    }

    
    public final List<String> getKeys() {
        return keys;
    }

    
    public final List<StringTemplate> getReplacements() {
        return replacements;
    }

    
    public StringTemplate add(String key, String value) {
        if (templateType == TemplateType.TEMPLATE) {
            keys.add(key);
            replacements.add(new StringTemplate(value, TemplateType.KEY));
        } else {
            throw new IllegalArgumentException("Cannot add key-value pair to StringTemplate type "
                                               + templateType.toString());
        }
        return this;
    }

    
    public StringTemplate add(String value) {
	if (templateType == TemplateType.LABEL) {
	    replacements.add(new StringTemplate(value, TemplateType.KEY));
	} else {
	    throw new IllegalArgumentException("Cannot add a single string to StringTemplate type "
                                               + templateType.toString());
	}
	return this;
    }

    
    public StringTemplate addName(String key, String value) {
        if (templateType == TemplateType.TEMPLATE) {
            keys.add(key);
            replacements.add(new StringTemplate(value, TemplateType.NAME));
        } else {
            throw new IllegalArgumentException("Cannot add key-value pair to StringTemplate type "
                                               + templateType.toString());
        }
        return this;
    }

    
    public StringTemplate addName(String value) {
	if (templateType == TemplateType.LABEL) {
	    replacements.add(new StringTemplate(value, TemplateType.NAME));
	} else {
	    throw new IllegalArgumentException("Cannot add a single string to StringTemplate type "
                                               + templateType.toString());
	}
	return this;
    }

    
    public StringTemplate addAmount(String key, int amount) {
        addName(key, Integer.toString(amount));
        return this;
    }

    
    public StringTemplate addStringTemplate(String key, StringTemplate template) {
        if (templateType == TemplateType.TEMPLATE) {
            keys.add(key);
            replacements.add(template);
        } else {
            throw new IllegalArgumentException("Cannot add a key-template pair to a StringTemplate type "
                                               + templateType.toString());
        }
	return this;
    }

    
    public StringTemplate addStringTemplate(StringTemplate template) {
        if (templateType == TemplateType.LABEL) {
            replacements.add(template);
        } else {
	    throw new IllegalArgumentException("Cannot add a StringTemplate to StringTemplate type "
                                               + templateType.toString());
        }            
	return this;
    }

    public String toString() {
        String result = templateType.toString() + ": ";
        switch (templateType) {
        case LABEL:
            if (replacements == null) {
                result += getId();
            } else {
                for (StringTemplate object : replacements) {
                    result += object + getId();
                }
            }
            break;
        case TEMPLATE:
            result += getId() + " [";
            for (int index = 0; index < keys.size(); index++) {
                result += "[" + keys.get(index) + ": "
                    + replacements.get(index).toString() + "]";
            }
            result += "]";
            break;
        case KEY:
        case NAME:
        default:
            result += getId();
        }
        return result;
    }

    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    public static String getXMLElementTagName() {
        return "stringTemplate";
    }

    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        out.writeAttribute(ID_ATTRIBUTE_TAG, getId());
        out.writeAttribute("templateType", templateType.toString());
    }


    public void writeChildren(XMLStreamWriter out) throws XMLStreamException {
        if (keys != null) {
            for (String key : keys) {
                out.writeStartElement("key");
                out.writeAttribute("value", key);
                out.writeEndElement();
            }
        }
        if (replacements != null) {
            for (StringTemplate replacement : replacements) {
                replacement.toXMLImpl(out);
            }
        }
    }

    public void readAttributes(XMLStreamReader in) throws XMLStreamException {
        
        String id = in.getAttributeValue(null, ID_ATTRIBUTE_TAG);
        if (id == null) {
            id = in.getAttributeValue(null, ID_ATTRIBUTE);
        }
        setId(id);
        String typeString = in.getAttributeValue(null, "templateType");
        if (typeString == null) {
            templateType = TemplateType.TEMPLATE;
        } else {
            templateType = Enum.valueOf(TemplateType.class, typeString);
        }
        
        switch (templateType) {
        case TEMPLATE:
            keys = new ArrayList<String>();
        case LABEL:            
            replacements = new ArrayList<StringTemplate>();
        }
    }


    public void readChildren(XMLStreamReader in) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if ("key".equals(in.getLocalName())) {
                keys.add(in.getAttributeValue(null, "value"));
                in.nextTag();
            } else if (getXMLElementTagName().equals(in.getLocalName())) {
                StringTemplate replacement = new StringTemplate();
                replacement.readFromXMLImpl(in);
                replacements.add(replacement);
            } else if ("data".equals(in.getLocalName())) {
                
                String[] data = readFromArrayElement("data", in, new String[0]);
                for (int index = 0; index < data.length; index += 2) {
                    keys.add(data[index]);
                    replacements.add(new StringTemplate(data[index + 1], TemplateType.NAME));
                }
                
            }
        }
    }


}
