

package net.sf.freecol.client.gui.panel;


public class ChoiceItem<T> {

    private String text;
    private T object;

    
    public ChoiceItem(String text, T object) {
        this.text = text;
        this.object = object;
    }


    
    public ChoiceItem(T object) {
        this.text = object.toString();
        this.object = object;
    }


    
    public T getObject() {
        return object;
    }


    
    public int getChoice() {
        return ((Integer) object).intValue();
    }


    
    public String toString() {
        return text;
    }
}
