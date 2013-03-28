

package org.jfree.data.xml;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PieDatasetHandler extends RootHandler implements DatasetTags {

    
    private DefaultPieDataset dataset;

    
    public PieDatasetHandler() {
        this.dataset = null;
    }

    
    public PieDataset getDataset() {
        return this.dataset;
    }

    
    public void addItem(Comparable key, Number value) {
        this.dataset.setValue(key, value);
    }

    
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts) throws SAXException {

        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
        }
        else if (qName.equals(PIEDATASET_TAG)) {
            this.dataset = new DefaultPieDataset();
        }
        else if (qName.equals(ITEM_TAG)) {
            ItemHandler subhandler = new ItemHandler(this, this);
            getSubHandlers().push(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        }

    }

    
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {

        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
        }

    }

}
