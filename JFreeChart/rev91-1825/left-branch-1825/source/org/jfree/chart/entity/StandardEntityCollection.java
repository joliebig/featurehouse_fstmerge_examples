

package org.jfree.chart.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public class StandardEntityCollection implements EntityCollection,
        Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 5384773031184897047L;

    
    private List entities;

    
    public StandardEntityCollection() {
        this.entities = new java.util.ArrayList();
    }

    
    public int getEntityCount() {
        return this.entities.size();
    }

    
    public ChartEntity getEntity(int index) {
        return (ChartEntity) this.entities.get(index);
    }

    
    public void clear() {
        this.entities.clear();
    }

    
    public void add(ChartEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Null 'entity' argument.");
        }
        this.entities.add(entity);
    }

    
    public void addAll(EntityCollection collection) {
        this.entities.addAll(collection.getEntities());
    }

    
    public ChartEntity getEntity(double x, double y) {
        int entityCount = this.entities.size();
        for (int i = entityCount - 1; i >= 0; i--) {
            ChartEntity entity = (ChartEntity) this.entities.get(i);
            if (entity.getArea().contains(x, y)) {
                return entity;
            }
        }
        return null;
    }

    
    public Collection getEntities() {
        return Collections.unmodifiableCollection(this.entities);
    }

    
    public Iterator iterator() {
        return this.entities.iterator();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StandardEntityCollection) {
            StandardEntityCollection that = (StandardEntityCollection) obj;
            return ObjectUtilities.equal(this.entities, that.entities);
        }
        return false;
    }

    
    public Object clone() throws CloneNotSupportedException {
        StandardEntityCollection clone
                = (StandardEntityCollection) super.clone();
        clone.entities = new java.util.ArrayList(this.entities.size());
        for (int i = 0; i < this.entities.size(); i++) {
            ChartEntity entity = (ChartEntity) this.entities.get(i);
            clone.entities.add(entity.clone());
        }
        return clone;
    }

}
