

package net.sf.freecol.common.model;

import java.util.Iterator;
import java.util.List;


public interface Location {


    
    public Tile getTile();

    
    public String getLocationName();

    
    public void add(Locatable locatable);

    
    public void remove(Locatable locatable);

    
    public boolean contains(Locatable locatable);

    
    public boolean canAdd(Locatable locatable);

    
    public int getUnitCount();

    
    public List<Unit> getUnitList();

    
    public Iterator<Unit> getUnitIterator();

    
    public String getId();

    
    public GoodsContainer getGoodsContainer();

    
     public Colony getColony();
}
