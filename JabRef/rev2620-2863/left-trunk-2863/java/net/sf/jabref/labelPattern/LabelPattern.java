
package net.sf.jabref.labelPattern;

import java.util.ArrayList;
import java.util.Hashtable;


public class LabelPattern extends Hashtable<String, ArrayList<String>> {

    
    protected LabelPattern parent = null;

    public LabelPattern() {
    }

    public LabelPattern(LabelPattern parent) {
        this.parent = parent;
    }

    
    public void setParent(LabelPattern parent) {
        this.parent = parent;
    }

    
    public LabelPattern getParent() {
        return parent;
    }

    public void addLabelPattern(String type, String pattern) {
        put(type, LabelPatternUtil.split(pattern));
    }

    
    public void removeLabelPattern(String type) {
        if (containsKey(type) && parent != null) {
            remove(type);
        }
    }

    public void removeLabelPattern(String type, boolean sure) {

        if (containsKey(type) && sure) {
            remove(type);
        }
    }

    
    public final ArrayList<String> getValue(String key) {
        ArrayList<String> result = get(key); 
                                                
        
        if (result == null) {
            if (parent != null) {
                result = parent.getValue(key);
            }
            if (result == null) {
                
                return LabelPatternUtil.DEFAULT_LABELPATTERN;
            }
        }

        return result;
    }

    
    public final boolean isDefaultValue(String key) {
        Object _obj = get(key);
        return _obj == null;
    }
}
