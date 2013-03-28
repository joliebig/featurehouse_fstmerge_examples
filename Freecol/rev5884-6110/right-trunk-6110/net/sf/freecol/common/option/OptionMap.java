


package net.sf.freecol.common.option;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.ClientOptions;

import org.w3c.dom.Element;



public abstract class OptionMap extends OptionGroup {
    private static Logger logger = Logger.getLogger(OptionMap.class.getName());



    private String xmlTagName;
    private HashMap<String, Option> values;
   


    
    public OptionMap(String xmlTagName) {
        super(xmlTagName);
        this.xmlTagName = xmlTagName;
        
        values = new HashMap<String, Option>();

        addDefaultOptions();
        addToMap(this);
    }

    
     public OptionMap(XMLStreamReader in, String xmlTagName) throws XMLStreamException {
         this(xmlTagName);
         readFromXML(in);
     }

    
    public OptionMap(Element element, String xmlTagName) {
        this(xmlTagName);
        readFromXMLElement(element);
    }



    
    protected abstract void addDefaultOptions();


    
    public Option getObject(String id) {
        return values.get(id);
    }


    
    public int getInteger(String id) {
        Option o = values.get(id);
        if (o instanceof IntegerOption) {
            return ((IntegerOption) o).getValue();
        } else if (o instanceof SelectOption) {
            return ((SelectOption) o).getValue();
        } else if (o instanceof RangeOption) {
            return ((RangeOption) o).getValue();
        } else {
            throw new IllegalArgumentException("No integer value associated with the specified option.");
        }
    }


    
    public boolean getBoolean(String id) {
        try {
            return ((BooleanOption) values.get(id)).getValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("No boolean value associated with the specified option.");
        }
    }
    
    
    public File getFile(String id) {
        try {
            return ((FileOption) values.get(id)).getValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("No File associated with the specified option.");
        }
    }

    
    public void addToMap(OptionGroup og) {
        Iterator<Option> it = og.iterator();
        while (it.hasNext()) {
            Option option = it.next();
            if (option instanceof OptionGroup) {
                addToMap((OptionGroup) option);
            } else {
                values.put(option.getId(), option);
            }
        }
    }

    public void putOption(Option option) {
        values.put(option.getId(), option);
    }

    
    private static XMLStreamReader createXMLStreamReader(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        
        in.mark(10);
        byte[] buf = new byte[5];
        in.read(buf, 0, 5);
        in.reset();
        if (!(new String(buf)).equals("<?xml")) {
            in = new BufferedInputStream(new InflaterInputStream(in));
        }
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            return xif.createXMLStreamReader(in, "UTF-8");
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        }
    }
    
    abstract protected boolean isCorrectTagName(String tagName);

    
    public void load(File loadFile) {
        if (loadFile == null || !loadFile.exists()) {
            logger.warning("Could not find the client options file.");
            return;
        }
        
        XMLStreamReader in = null;
        try {
            in = createXMLStreamReader(loadFile);
            in.nextTag();
            while (!isCorrectTagName(in.getLocalName())) {
                in.nextTag();
            }
            readFromXML(in);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while loading options.", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception while closing stream.", e);
            }
        }
    }
    
    
    public void save(File saveFile) {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xsw = null;
        try {
            xsw = xof.createXMLStreamWriter(new FileOutputStream(saveFile));
            xsw.writeStartDocument("UTF-8", "1.0");
            toXML(xsw);
            xsw.writeEndDocument();
            xsw.flush();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while storing options.", e);
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
    
    
    public void toXML(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(xmlTagName);

        Iterator<Option> it = values.values().iterator();
        while (it.hasNext()) {
            (it.next()).toXML(out);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        updateFromXML(in);
    }

    
    private void updateFromXML(XMLStreamReader in) throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(OptionGroup.getXMLElementTagName())) {
                updateFromXML(in);
            } else {
                final String idStr = in.getAttributeValue(null, "id");
                if (idStr != null) {
                    
                    Option o = getObject(idStr);

                    if (o != null) {
                        o.readFromXML(in);
                    } else {
                        
                        logger.info("Option \"" + idStr + "\" (" + in.getLocalName() + ") could not be found.");
                        
                        
                        final String ignoredTag = in.getLocalName();
                        while (in.nextTag() != XMLStreamConstants.END_ELEMENT
                                || !in.getLocalName().equals(ignoredTag));
                    }
                } else {
                    Option o = getObject(in.getLocalName());
                    if (o != null) {
                        o.readFromXML(in);
                    } else {
                        
                        logger.info("Option \"" + in.getLocalName() + " not found.");

                        
                        final String ignoredTag = in.getLocalName();
                        while (in.nextTag() != XMLStreamConstants.END_ELEMENT
                                || !in.getLocalName().equals(ignoredTag));
                    }
                }
            }
        }
        
    }


    
    public static String getXMLElementTagName() {
        throw new UnsupportedOperationException();
    }

    
    public void setFile(String id, File newFileValue) {
    	if( id == null || id.trim().length() == 0 )
    		throw new IllegalArgumentException("Requires an ID");
    	if( newFileValue == null )
    		throw new IllegalArgumentException("Requires a File parameter");
    	if( values.get(id) == null )
    		throw new IllegalArgumentException("No option with ID=["+ id +"]");
    	
        try {
            ((FileOption) values.get(id)).setValue(newFileValue);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("No File associated with option ["+ id +"].");
        }
    }
}
