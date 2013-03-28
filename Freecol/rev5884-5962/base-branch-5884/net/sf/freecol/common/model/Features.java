

package net.sf.freecol.common.model;

public interface Features {

    
    public boolean hasAbility(String id);

    
    public Modifier getModifier(String id);

    
    public boolean addFeature(Feature feature);

}
