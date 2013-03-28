

package net.sf.freecol.client.gui.panel;


import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Player;




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
        this(Messages.message(object.toString()), object, true);
        
        
        if(object instanceof AbstractGoods) {
            this.text = ((AbstractGoods)object).getAmount() + " " + Messages.message(((AbstractGoods)object).getNameKey());
        } else if (object instanceof AbstractUnit) {
            this.text = Messages.message(((AbstractUnit)object).getId());
        } else if (object instanceof Player) {
            this.text = Messages.message(((Player)object).getRulerNameKey()) + " (" + Messages.message(((Player)object).getNationName()) + ")";
        }
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
