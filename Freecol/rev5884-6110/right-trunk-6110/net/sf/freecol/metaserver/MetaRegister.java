


package net.sf.freecol.metaserver;


import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.server.FreeColServer;

import org.w3c.dom.Element;




public final class MetaRegister {
    private static Logger logger = Logger.getLogger(MetaRegister.class.getName());

    
    private ArrayList<MetaItem> items = new ArrayList<MetaItem>();
    
    
    
    private MetaItem getItem(String address, int port) {
        int index = indexOf(address, port);
        if (index >= 0) {
            return items.get(index);
        } else {
            return null;
        }
    }
    

    
    private int indexOf(String address, int port) {
        for (int i=0; i<items.size(); i++) {
            if (address.equals(items.get(i).getAddress()) && port == items.get(i).getPort()) {
                return i;
            }
        }

        return -1;
    }


    
    public synchronized void removeDeadServers() {
        logger.info("Removing dead servers.");

        long time = System.currentTimeMillis() - MetaServer.REMOVE_OLDER_THAN;
        for (int i=0; i<items.size(); i++) {
            if (items.get(i).getLastUpdated() < time) {
                logger.info("Removing: " + items.get(i));
                items.remove(i);
            }
        }
    }


    
    public synchronized void addServer(String name, String address, int port, int slotsAvailable,
                int currentlyPlaying, boolean isGameStarted, String version, int gameState)
                throws IOException {
        MetaItem mi = getItem(address, port);
        if (mi == null) {
            
            Connection mc = null;
            try {                
                mc = new Connection(address, port, null, FreeCol.METASERVER_THREAD);
                Element element = Message.createNewRootElement("disconnect");
                mc.send(element);
            } catch (IOException e) {
                logger.info("Server rejected (no route to destination):" + address + ":" + port);
                throw e;
            } finally {
                try {
                    if (mc != null) {
                        mc.close();
                    }
                } catch (IOException e) {}
            }
            items.add(new MetaItem(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState));
            logger.info("Server added:" + address + ":" + port);
        } else {
            updateServer(mi, name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        }
    }


    
    public synchronized void updateServer(String name, String address, int port, int slotsAvailable,
            int currentlyPlaying, boolean isGameStarted, String version, int gameState)
            throws IOException {
        MetaItem mi = getItem(address, port);
        if (mi == null) {
            addServer(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        } else {
            updateServer(mi, name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        }
    }


    
    public synchronized void removeServer(String address, int port) {
        int index = indexOf(address, port);
        if (index >= 0) {
            items.remove(index);
            logger.info("Removing server:" + address + ":" + port);
        } else {
            logger.info("Trying to remove non-existing server:" + address + ":" + port);
        }
    }

    
    
    public synchronized Element createServerList() {
        Element element = Message.createNewRootElement("serverList");
        for (int i=0; i<items.size(); i++) {
            element.appendChild(items.get(i).toXMLElement(element.getOwnerDocument()));
        }
        return element;
    }


    
    private void updateServer(MetaItem mi, String name, String address, int port, int slotsAvailable,
            int currentlyPlaying, boolean isGameStarted, String version, int gameState) {
        mi.update(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        logger.info("Server updated:" + mi.toString());
    }
}

