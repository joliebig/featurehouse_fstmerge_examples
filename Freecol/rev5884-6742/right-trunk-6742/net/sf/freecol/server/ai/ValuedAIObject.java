


package net.sf.freecol.server.ai;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ValuedAIObject extends AIObject implements Comparable<ValuedAIObject> {

    
    private int value;

    public ValuedAIObject(AIMain aiMain) {
        super(aiMain);
    }

    public ValuedAIObject(AIMain aiMain, String id) {
        super(aiMain, id);
    }

    
    public final int getValue() {
        return value;
    }

    
    public final void setValue(final int newValue) {
        this.value = newValue;
    }


    public final int compareTo(ValuedAIObject other) {
        return other.value - this.value;
    }

    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
    }

}