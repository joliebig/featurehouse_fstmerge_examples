

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.resources.ResourceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public abstract class FreeColAction extends AbstractAction implements Option {

    private static final Logger logger = Logger.getLogger(FreeColAction.class.getName());

    public static final String ACTION_ID = "ACTION_ID";
    public static final String BUTTON_IMAGE = "BUTTON_IMAGE";
    public static final String BUTTON_ROLLOVER_IMAGE = "BUTTON_ROLLOVER_IMAGE";
    public static final String BUTTON_PRESSED_IMAGE = "BUTTON_PRESSED_IMAGE";
    public static final String BUTTON_DISABLED_IMAGE = "BUTTON_DISABLED_IMAGE";
    public static final Integer NO_MNEMONIC = null;

    protected final FreeColClient freeColClient;


    
    protected FreeColAction(FreeColClient freeColClient, String id) {
        super(Messages.message(id + ".name"));

        this.freeColClient = freeColClient;

        putValue(SHORT_DESCRIPTION, Messages.message(id + ".shortDescription"));
        putValue(ACTION_ID, id);
    }

    
    protected FreeColAction(FreeColClient freeColClient, String name, String shortDescription) {
        super(Messages.message(name));

        this.freeColClient = freeColClient;

        putValue(SHORT_DESCRIPTION, shortDescription);
    }

    
    protected FreeColAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic) {
        super(Messages.message(name));

        this.freeColClient = freeColClient;

        putValue(SHORT_DESCRIPTION, shortDescription);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
    }

    
    protected FreeColAction(FreeColClient freeColClient, String name, String shortDescription, KeyStroke accelerator) {
        super(Messages.message(name));

        this.freeColClient = freeColClient;

        putValue(SHORT_DESCRIPTION, shortDescription);
        putValue(ACCELERATOR_KEY, accelerator);
    }

    
    protected FreeColAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic,
            KeyStroke accelerator) {
       this(freeColClient, name, shortDescription, mnemonic, accelerator, true);
    }
    
    
    protected FreeColAction(FreeColClient freeColClient, String name, String shortDescription, int mnemonic,
            KeyStroke accelerator, boolean localize) {
        super((localize) ? Messages.message(name) : name);

        this.freeColClient = freeColClient;

        putValue(SHORT_DESCRIPTION, (localize) ? shortDescription : shortDescription);
        putValue(MNEMONIC_KEY, new Integer(mnemonic));
        putValue(ACCELERATOR_KEY, accelerator);
    }

    
    public Integer getMnemonic() {
        return (Integer) getValue(MNEMONIC_KEY);
    }

    
    public void setMnemonic(int mnemonic) {
        putValue(MNEMONIC_KEY, mnemonic);
    }

    
    protected FreeColClient getFreeColClient() {
        return freeColClient;
    }

    protected void addImageIcons(String key) {
        putValue(BUTTON_IMAGE,
                 new ImageIcon(ResourceManager.getImage("orderButton.normal." + key)));
        putValue(BUTTON_ROLLOVER_IMAGE,
                 new ImageIcon(ResourceManager.getImage("orderButton.highlighted." + key)));
        putValue(BUTTON_PRESSED_IMAGE,
                 new ImageIcon(ResourceManager.getImage("orderButton.pressed." + key)));
        putValue(BUTTON_DISABLED_IMAGE,
                 new ImageIcon(ResourceManager.getImage("orderButton.disabled." + key)));
    }

    
    public void update() {
        boolean b = shouldBeEnabled();
        if (isEnabled() != b) {
            setEnabled(b);
        }
    }

    
    protected boolean shouldBeEnabled() {
        return freeColClient.getCanvas() != null
                && !freeColClient.getCanvas().isClientOptionsDialogShowing();
    }

    
    public void setAccelerator(KeyStroke accelerator) {
        putValue(ACCELERATOR_KEY, accelerator);
    }

    
    public KeyStroke getAccelerator() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    
    public String getShortDescription() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    
    public String toString() {
        return getName();
    }

    
    public String getId() {
        return (String) getValue(ACTION_ID);
    }

    
    public String getName() {
        return (String) getValue(NAME);
    }

    
    public static String getKeyStrokeText(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return "";
        } else
            return keyStroke.toString();
    }
    
    
    public boolean isPreviewEnabled() {
        return false;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("accelerator", getKeyStrokeText(getAccelerator()));

        out.writeEndElement();
   }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        String id = in.getAttributeValue(null, "id");
        String acc = in.getAttributeValue(null, "accelerator");

        if (id == null){
            
            id = in.getLocalName();
        }

        if (!acc.equals("")) {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(acc));
        } else {
            putValue(ACCELERATOR_KEY, null);
        }
        in.nextTag();
    }

    
    public void toXML(XMLStreamWriter out) throws XMLStreamException {
        toXMLImpl(out);
    }

    
    public void readFromXML(XMLStreamReader in) throws XMLStreamException {
        readFromXMLImpl(in);
    }

    
    public Element toXMLElement(Document document) {
        try {
            StringWriter sw = new StringWriter();
            XMLOutputFactory xif = XMLOutputFactory.newInstance();
            XMLStreamWriter xsw = xif.createXMLStreamWriter(sw);
            toXML(xsw);
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

    public MenuKeyListener getMenuKeyListener() {
        return new InnerMenuKeyListener();
    }


    
    public class InnerMenuKeyListener implements MenuKeyListener {

        int mnemonic;


        public InnerMenuKeyListener() {
            mnemonic = ((Integer) getValue(MNEMONIC_KEY)).intValue();
        }

        public void menuKeyPressed(MenuKeyEvent e) {

            if (e.getKeyCode() == mnemonic) {
                ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), (String) getValue(Action.NAME),
                                                 e.getModifiers());
                actionPerformed(ae);

                e.consume();
            }
        }

        public void menuKeyReleased(MenuKeyEvent e) {
            
        }

        public void menuKeyTyped(MenuKeyEvent e) {
            
        }

    }

    
     public static String getXMLElementTagName() {
         return "action";
     }

}
