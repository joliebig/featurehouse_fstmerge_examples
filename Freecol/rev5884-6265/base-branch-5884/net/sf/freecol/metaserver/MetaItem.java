


package net.sf.freecol.metaserver;

import java.util.logging.Logger;

import net.sf.freecol.common.ServerInfo;



public class MetaItem extends ServerInfo {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(MetaItem.class.getName());

    

    private long lastUpdated;


    
    public MetaItem(String name, String address, int port, int slotsAvailable,
                    int currentlyPlaying, boolean isGameStarted, String version,
                    int gameState) {
        super();
        update(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
    }


    
    public void update(String name, String address, int port, int slotsAvailable,
                       int currentlyPlaying, boolean isGameStarted, String version,
                       int gameState) {
        super.update(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
        lastUpdated = System.currentTimeMillis();
    }
    
    
    
    public long getLastUpdated() {
        return lastUpdated;
    }
}
