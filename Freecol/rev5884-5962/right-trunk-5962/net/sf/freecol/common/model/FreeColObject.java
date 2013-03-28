

package net.sf.freecol.common.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class FreeColObject {

    protected static Logger logger = Logger.getLogger(FreeColObject.class.getName());

    
    protected static final String ID_ATTRIBUTE = "ID";

    
    
    public static final String ID_ATTRIBUTE_TAG = "id";

    
    protected static final String ARRAY_SIZE = "xLength";

    
    protected static final String PARTIAL_ATTRIBUTE = "PARTIAL";

    
    private String id;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    
    public String getId() {
        return id;
    }

    
    protected void setId(final String newId) {
        this.id = newId;
    }

    
    public boolean hasAbility(String id) {
        return false;
    }

    
    public void dumpObject() {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xsw = null;
        try {
            xsw = xof.createXMLStreamWriter(System.err, "UTF-8");
            this.toXML(xsw, null, true, true);
            System.err.println();
            xsw.flush();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to dump object", e);
        } finally {
            try {
                if (xsw != null) {
                    xsw.close();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception while closing stream.", e);
            }
        }
    }

    
    public Element toXMLElement(Document document) {
        
        return toXMLElement(null, document, true, false);
    }

    
    public Element toXMLElement(Player player, Document document) {
        return toXMLElement(player, document, true, false);
    }

    
    public Element toXMLElement(Player player, Document document, boolean showAll, boolean toSavedGame) {
        return toXMLElement(player, document, showAll, toSavedGame, null);
    }

    
    public Element toXMLElementPartial(Document document, String... fields) {
        return toXMLElement(null, document, true, false, fields);
    }

    
    public Element toXMLElement(Player player, Document document, boolean showAll, boolean toSavedGame, String[] fields) {
        try {
            StringWriter sw = new StringWriter();
            XMLOutputFactory xif = XMLOutputFactory.newInstance();
            XMLStreamWriter xsw = xif.createXMLStreamWriter(sw);
            if (fields == null) {
                toXML(xsw, player, showAll, toSavedGame);
            } else {
                toXMLPartialImpl(xsw, fields);
            }
            xsw.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document tempDocument = null;
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                tempDocument = builder.parse(new InputSource(new StringReader(sw.toString())));
                return (Element) document.importNode(tempDocument.getDocumentElement(), true);
            } catch (ParserConfigurationException pce) {
                
                StringWriter swe = new StringWriter();
                pce.printStackTrace(new PrintWriter(swe));
                logger.warning(swe.toString());
                throw new IllegalStateException("ParserConfigurationException");
            } catch (SAXException se) {
                StringWriter swe = new StringWriter();
                se.printStackTrace(new PrintWriter(swe));
                logger.warning(swe.toString());
                throw new IllegalStateException("SAXException");
            } catch (IOException ie) {
                StringWriter swe = new StringWriter();
                ie.printStackTrace(new PrintWriter(swe));
                logger.warning(swe.toString());
                throw new IllegalStateException("IOException");
            }
        } catch (XMLStreamException e) {
            logger.warning(e.toString());
            throw new IllegalStateException("XMLStreamException");
        }
    }

    
    public void toXML(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame) throws XMLStreamException {
        toXMLImpl(out);
    }

    
    public void toXML(XMLStreamWriter out, Player player) throws XMLStreamException {
        toXML(out, player, false, false);
    }

    
    public void toXML(XMLStreamWriter out) throws XMLStreamException {
        toXML(out, null, true, false);
    }

    
    public void readFromXMLElement(Element element) {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            try {
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer xmlTransformer = factory.newTransformer();
                StringWriter stringWriter = new StringWriter();
                xmlTransformer.transform(new DOMSource(element), new StreamResult(stringWriter));
                String xml = stringWriter.toString();
                XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xml));
                xsr.nextTag();
                readFromXML(xsr);
            } catch (TransformerException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.warning(sw.toString());
                throw new IllegalStateException("TransformerException");
            }
        } catch (XMLStreamException e) {
            logger.warning(e.toString());
            throw new IllegalStateException("XMLStreamException");
        }
    }

    
    public void readFromXML(XMLStreamReader in) throws XMLStreamException {
        if (in.getAttributeValue(null, PARTIAL_ATTRIBUTE) == null) {
            readFromXMLImpl(in);
        } else {
            readFromXMLPartialImpl(in);
        }
    }

    
    protected int[] readFromArrayElement(String tagName, XMLStreamReader in, int[] arrayType)
        throws XMLStreamException {
        if (!in.getLocalName().equals(tagName)) {
            in.nextTag();
        }

        int[] array = new int[Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE))];

        for (int x=0; x<array.length; x++) {
            array[x] = Integer.parseInt(in.getAttributeValue(null, "x" + Integer.toString(x)));
        }

        in.nextTag();
        return array;
    }

    
    @SuppressWarnings("unchecked")
        protected <T> T[] readFromArrayElement(String tagName, XMLStreamReader in, Class<T> type)
        throws XMLStreamException {
        if (!in.getLocalName().equals(tagName)) {
            in.nextTag();
        }

        final int size = Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE));
        T[] array = (T[]) Array.newInstance(type, size);

        for (int x=0; x<array.length; x++) {
            try {
                final String value = in.getAttributeValue(null, "x" + Integer.toString(x));
                final T object;
                if (value != null) {
                    Constructor<T> c = type.getConstructor(type);
                    object = c.newInstance(new Object[] {value});
                } else {
                    object = null;
                }
                array[x] = object;
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        in.nextTag();
        return array;
    }

    
    protected <T> void toArrayElement(String tagName, T[] array, XMLStreamWriter out)
        throws XMLStreamException {
        out.writeStartElement(tagName);

        out.writeAttribute(ARRAY_SIZE, Integer.toString(array.length));
        for (int x=0; x < array.length; x++) {
            out.writeAttribute("x" + Integer.toString(x), array[x].toString());
        }

        out.writeEndElement();
    }

    
    protected void toArrayElement(String tagName, int[] array, XMLStreamWriter out)
        throws XMLStreamException {
        out.writeStartElement(tagName);

        out.writeAttribute(ARRAY_SIZE, Integer.toString(array.length));
        for (int x=0; x < array.length; x++) {
            out.writeAttribute("x" + Integer.toString(x), Integer.toString(array[x]));
        }

        out.writeEndElement();
    }

    
    protected <T> void toListElement(String tagName, List<T> array, XMLStreamWriter out)
        throws XMLStreamException {
        out.writeStartElement(tagName);

        out.writeAttribute(ARRAY_SIZE, Integer.toString(array.size()));
        for (int x=0; x < array.size(); x++) {
            out.writeAttribute("x" + Integer.toString(x), array.get(x).toString());
        }

        out.writeEndElement();
    }

    
    protected <T> List<T> readFromListElement(String tagName, XMLStreamReader in, Class<T> type)
        throws XMLStreamException {
        if (!in.getLocalName().equals(tagName)) {
            in.nextTag();
        }
        final int length = Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE));
        List<T> list = new ArrayList<T>(length);
        for (int x=0; x<length; x++) {
            try {
                final String value = in.getAttributeValue(null, "x" + Integer.toString(x));
                final T object;
                if (value != null) {
                    Constructor<T> c = type.getConstructor(type);
                    object = c.newInstance(new Object[] {value});
                } else {
                    object = null;
                }
                list.add(object);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        in.nextTag();
        return list;
    }

    
    protected String[] readFromArrayElement(String tagName, XMLStreamReader in, String[] arrayType)
        throws XMLStreamException {
        if (!in.getLocalName().equals(tagName)) {
            in.nextTag();
        }
        String[] array = new String[Integer.parseInt(in.getAttributeValue(null, ARRAY_SIZE))];
        for (int x=0; x<array.length; x++) {
            array[x] = in.getAttributeValue(null, "x" + Integer.toString(x));
        }

        in.nextTag();
        return array;
    }

    
    public boolean hasAttribute(XMLStreamReader in, String attributeName) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        return attributeString != null;
    }

    
    public int getAttribute(XMLStreamReader in, String attributeName, int defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return Integer.parseInt(attributeString);
        } else {
            return defaultValue;
        }
    }

    
    public float getAttribute(XMLStreamReader in, String attributeName, float defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return Float.parseFloat(attributeString);
        } else {
            return defaultValue;
        }
    }

    
    public boolean getAttribute(XMLStreamReader in, String attributeName, boolean defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return Boolean.parseBoolean(attributeString);
        } else {
            return defaultValue;
        }
    }

    
    public String getAttribute(XMLStreamReader in, String attributeName, String defaultValue) {
        final String attributeString = in.getAttributeValue(null, attributeName);
        if (attributeString != null) {
            return attributeString;
        } else {
            return defaultValue;
        }
    }

    
    public void writeAttribute(XMLStreamWriter out, String attributeName, FreeColObject object)
        throws XMLStreamException {
        if (object != null) {
            out.writeAttribute(attributeName, object.getId());
        }
    }

    public void writeFreeColGameObject(FreeColGameObject object, XMLStreamWriter out, Player player,
                                       boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        if (object != null) {
            object.toXMLImpl(out, player, showAll, toSavedGame);
        }
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
        
        if (getId() == null) {
            setId(in.getAttributeValue(null, ID_ATTRIBUTE));
        }
        readAttributes(in);
        readChildren(in);
    }

    protected void readAttributes(XMLStreamReader in) throws XMLStreamException {
        
    }

    protected void readChildren(XMLStreamReader in) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            
        }
    }

    
    protected void readFromXMLPartialImpl(XMLStreamReader in) throws XMLStreamException {
        throw new UnsupportedOperationException("Partial update of unsupported type");
    }

    
    abstract protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException;

    
    protected void toXMLPartialImpl(XMLStreamWriter out, String[] fields)
        throws XMLStreamException {
        throw new UnsupportedOperationException("Partial update of unsupported type.");
    }

    
    protected void writeAttributes(XMLStreamWriter out) throws XMLStreamException {}
    protected void writeChildren(XMLStreamWriter out) throws XMLStreamException {}


    

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    public boolean hasListeners(String propertyName) {
        return pcs.hasListeners(propertyName);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }


    
    public static String getXMLElementTagName() {
        return null;
    }
}
