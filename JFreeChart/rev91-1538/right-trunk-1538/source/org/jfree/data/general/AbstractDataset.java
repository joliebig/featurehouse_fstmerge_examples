

package org.jfree.data.general;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;


public abstract class AbstractDataset implements Dataset, Cloneable,
        Serializable, ObjectInputValidation {

    
    private static final long serialVersionUID = 1918768939869230744L;

    
    private DatasetGroup group;

    
    private transient EventListenerList listenerList;

    
    protected AbstractDataset() {
        this.group = new DatasetGroup();
        this.listenerList = new EventListenerList();
    }

    
    public DatasetGroup getGroup() {
        return this.group;
    }

    
    public void setGroup(DatasetGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("Null 'group' argument.");
        }
        this.group = group;
    }

    
    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    
    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
    }

    
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    
    protected void fireDatasetChanged() {
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    
    protected void notifyListeners(DatasetChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DatasetChangeListener.class) {
                ((DatasetChangeListener) listeners[i + 1]).datasetChanged(
                        event);
            }
        }

    }

    
    public Object clone() throws CloneNotSupportedException {
        AbstractDataset clone = (AbstractDataset) super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);  
                                              
    }

    
    public void validateObject() throws InvalidObjectException {
        fireDatasetChanged();
    }

}
