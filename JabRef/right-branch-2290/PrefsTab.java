
package net.sf.jabref;


public interface PrefsTab {

    
    public void setValues();

    
    public void storeSettings();

    
    public boolean readyToClose();

    
    public String getTabName();  
}
