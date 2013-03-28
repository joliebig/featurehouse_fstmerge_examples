package net.sf.freecol.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class XMLStream implements Closeable {

    private static final Logger logger = Logger.getLogger(XMLStream.class.getName());
    
    private InputStream inputStream;
    private XMLStreamReader xmlStreamReader;
    
    
    public XMLStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.xmlStreamReader = createXMLStreamReader(inputStream);
    }

    
    public XMLStreamReader getXMLStreamReader() {
        return xmlStreamReader;
    }
    
    
    public void close() {
        try {
            xmlStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private XMLStreamReader createXMLStreamReader(InputStream inputStream) throws IOException{
        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();        
            return xif.createXMLStreamReader(inputStream, "UTF-8");
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        } catch (NullPointerException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new NullPointerException("NullPointerException.");
        }
    }
}
