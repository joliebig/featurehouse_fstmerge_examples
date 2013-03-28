


package net.sf.freecol.server.ai;


import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Locatable;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.server.ai.mission.TransportMission;



public interface Transportable {


    
    public Location getTransportSource();
    

    
    public Location getTransportDestination();


    
    public int getTransportPriority();
    

    
    public void increaseTransportPriority();

    
    
    public Locatable getTransportLocatable();
    
    
    
    public AIUnit getTransport();
    
    
    
    public void setTransport(AIUnit transport);
    
    
    public void abortWish(Wish w);
    
        
    public String getId();
}
