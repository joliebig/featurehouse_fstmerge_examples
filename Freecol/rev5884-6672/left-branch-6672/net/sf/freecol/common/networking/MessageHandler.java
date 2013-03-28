


package net.sf.freecol.common.networking;

import net.sf.freecol.common.FreeColException;

import org.w3c.dom.Element;




public interface MessageHandler {
    
    
    public Element handle(Connection connection, Element element) throws FreeColException;

}
