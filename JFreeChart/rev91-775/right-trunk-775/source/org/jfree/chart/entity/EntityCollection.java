

package org.jfree.chart.entity;

import java.util.Collection;
import java.util.Iterator;


public interface EntityCollection {

    
    public void clear();

    
    public void add(ChartEntity entity);

    
    public void addAll(EntityCollection collection);
    
    
    public ChartEntity getEntity(double x, double y);

    
    public ChartEntity getEntity(int index);
    
    
    public int getEntityCount();
    
    
    public Collection getEntities();
    
    
    public Iterator iterator();

}
