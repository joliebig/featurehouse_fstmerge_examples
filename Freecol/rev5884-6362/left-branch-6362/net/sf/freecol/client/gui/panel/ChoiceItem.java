

package net.sf.freecol.client.gui.panel;


public class ChoiceItem<T> {

    private String text;
    private T object;
    private boolean enabled;

    
    public ChoiceItem(String text, T object, boolean enable) {
        this.text = text;
        this.object = object;
        this.enabled = enable;
    }
    
    
    
    public ChoiceItem(String text, T object) {
    	this(text, object, true);
    }


    
    public ChoiceItem(T object) {
    	this(object.toString(), object, true);
    }


    
    public T getObject() {
        return object;
    }


    
    public int getChoice() {
        return ((Integer) object).intValue();
    }
    
    
    public boolean isEnabled(){
    	return this.enabled;
    }

    
    public String toString() {
        return text;
    }
}
