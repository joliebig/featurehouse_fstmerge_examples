

package net.sf.freecol.common.networking;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Message {

    protected static final Logger logger = Logger.getLogger(Message.class.getName());

    private static final String FREECOL_PROTOCOL_VERSION = "0.1.6";

    private static final String INVALID_MESSAGE = "invalid";

    
    protected Document document;


    protected Message() {
        
    }

    
    public Message(String msg) throws SAXException, IOException {
        this(new InputSource(new StringReader(msg)));
    }

    
    public Message(InputStream inputStream) throws SAXException, IOException {
        this(new InputSource(inputStream));
    }

    
    private Message(InputSource inputSource) throws SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document tempDocument = null;

        boolean dumpMsgOnError = FreeCol.isInDebugMode();
        dumpMsgOnError = true;
        if (dumpMsgOnError) {
            
            inputSource.setByteStream(new BufferedInputStream(inputSource.getByteStream()));

            inputSource.getByteStream().mark(1000000);
        }

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            tempDocument = builder.parse(inputSource);
        } catch (ParserConfigurationException pce) {
            
            StringWriter sw = new StringWriter();
            pce.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
        } catch (SAXException se) {
            throw se;
        } catch (IOException ie) {
            throw ie;
        } catch (ArrayIndexOutOfBoundsException e) {
            
            
            
            if (dumpMsgOnError) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                inputSource.getByteStream().reset();
                while (true) {
                    int i = inputSource.getByteStream().read();
                    if (-1 == i) {
                        break;
                    }
                    baos.write(i);
                }
                logger.severe(baos.toString());
            }
            throw e;
        }

        document = tempDocument;
    }

    
    public Message(Document document) {
        this.document = document;
    }

    
    public static String getFreeColProtocolVersion() {
        return FREECOL_PROTOCOL_VERSION;
    }

    
    public static Document createNewDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.newDocument();
        } catch (ParserConfigurationException pce) {
            
            pce.printStackTrace();
            return null;
        }
    }

    
    public static Element createNewRootElement(String tagName) {
        return createNewDocument().createElement(tagName);
    }

    
    public static Element createError(String messageID, String message) {
        Element errorElement = createNewRootElement("error");

        if (messageID != null && !messageID.equals("")) {
            errorElement.setAttribute("messageID", messageID);
        }

        if (message != null && !message.equals("")) {
            errorElement.setAttribute("message", message);
        }

        return errorElement;
    }

    
    public static void createError(XMLStreamWriter out, String messageID, String message) {
        try {
            out.writeStartElement("error");

            if (messageID != null && !messageID.equals("")) {
                out.writeAttribute("messageID", messageID);
            }

            if (message != null && !message.equals("")) {
                out.writeAttribute("message", message);
            }
            out.writeEndElement();
        } catch (XMLStreamException e) {
            logger.warning("Could not send error message.");
        }
    }

    
    public static Element clientError(String message) {
        Element errorElement = createNewRootElement("error");
        errorElement.setAttribute("messageID", "server.reject");
        errorElement.setAttribute("message", message);
        return errorElement;
    }

    
    public Document getDocument() {
        return document;
    }

    
    public String getType() {

        if (document != null && document.getDocumentElement() != null) {

            return document.getDocumentElement().getTagName();
        }

        return INVALID_MESSAGE;
    }

    
    public boolean isType(String type) {

        return getType().equals(type);
    }

    
    public void setAttribute(String key, String value) {
        document.getDocumentElement().setAttribute(key, value);
    }

    
    public void setAttribute(String key, int value) {
        document.getDocumentElement().setAttribute(key, (new Integer(value)).toString());
    }

    
    public String getAttribute(String key) {
        return document.getDocumentElement().getAttribute(key);
    }

    
    public boolean hasAttribute(String attribute) {
        return document.getDocumentElement().hasAttribute(attribute);
    }

    
    public void insertAsRoot(Element newRoot) {
        Element oldRoot = document.getDocumentElement();

        if (oldRoot != null) {
            document.removeChild(oldRoot);
            newRoot.appendChild(oldRoot);
        }

        document.appendChild(newRoot);
    }

    
    public static Element getChildElement(Element element, String tagName) {
        NodeList n = element.getChildNodes();
        for (int i = 0; i < n.getLength(); i++) {
            if (n.item(i) instanceof Element && ((Element) n.item(i)).getTagName().equals(tagName)) {
                return (Element) n.item(i);
            }
        }

        return null;
    }


    public Element toXMLElement() {
        
        return null;
    }

    
    @Override
    public String toString() {
        return document.getDocumentElement().toString();
    }
}
