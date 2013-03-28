

package net.sf.freecol.common.option;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class RangeOption extends SelectOption {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(RangeOption.class.getName());


    
    public RangeOption(XMLStreamReader in) throws XMLStreamException {
        super(in);
    }

    
    public int getValueRank() {
        int rank = 0;
        Iterator<Integer> iterator = getItemValues().keySet().iterator();
        while (iterator.hasNext() && iterator.next() != getValue()) {
            rank++;
        }
        return rank;
    }

    
    public void setValueRank(int rank) {
        int curValue = Integer.MIN_VALUE;
        Iterator<Integer> iterator = getItemValues().keySet().iterator();

        while (rank >= 0) {
            curValue = iterator.next();
            rank--;
        }

        setValue(curValue);
    }

    
    public static String getXMLElementTagName() {
        return "rangeOption";
    }

    
    public String getXMLItemElementTagName() {
        return "rangeValue";
    }

}
