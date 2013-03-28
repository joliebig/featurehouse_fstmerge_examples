


package net.sf.freecol.client.gui.option;

import net.sf.freecol.common.option.Option;



public interface OptionUpdater {



    
    public void updateOption();
    
    
    public void reset();

    
    public void unregister();
    
    
    public void rollback();
}
