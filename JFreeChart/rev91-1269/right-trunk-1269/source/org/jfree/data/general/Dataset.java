

package org.jfree.data.general;


public interface Dataset {

    
    public void addChangeListener(DatasetChangeListener listener);

    
    public void removeChangeListener(DatasetChangeListener listener);

    
    public DatasetGroup getGroup();

    
    public void setGroup(DatasetGroup group);

}
