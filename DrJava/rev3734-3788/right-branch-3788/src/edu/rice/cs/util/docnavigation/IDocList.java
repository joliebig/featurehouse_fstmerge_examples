

package edu.rice.cs.util.docnavigation;


import javax.swing.AbstractListModel;
import java.util.*;

class IDocList<ItemT extends INavigatorItem> extends AbstractListModel {
    private Vector<ItemT> _docs = new Vector<ItemT>();

    public Enumeration<ItemT> elements() {
        return _docs.elements();
    }

    public void clear() { _docs.clear(); }

    public boolean isEmpty() { return _docs.isEmpty(); }

    public ItemT get(int index) {
        return _docs.get(index);
    }

    public Object getElementAt(int i) {
        return _docs.get(i); 
    }

    public int size() { return _docs.size(); }
    public int getSize() { return size(); }

    public void add(ItemT d) {
        _docs.addElement(d);
        fireIntervalAdded(this, size() - 1, size() - 1);
    }

    
    public INavigatorItem remove(ItemT doc) {
        int index = _docs.indexOf(doc);
        if( index == -1 ) {
            return null;
        }

        ItemT ret = _docs.remove(index);
        fireIntervalRemoved(this, index, index);
        return ret;
    }
}
