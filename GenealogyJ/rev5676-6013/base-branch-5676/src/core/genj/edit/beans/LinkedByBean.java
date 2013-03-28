
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LinkedByBean extends PropertyBean {

  private PropertyTableWidget table;
  
  private final static String COLS_KEY = "bean.linkedby.cols";

  void initialize(Registry setRegistry) {
    super.initialize(setRegistry);
    
    
    table = new PropertyTableWidget();
    table.setPreferredSize(new Dimension(64,64));
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, table);
    
  }

  
  public void addNotify() {
    
    super.addNotify();
    
    table.setColumnLayout(registry.get(COLS_KEY, (String)null));
  }
  
  
  public void removeNotify() {
    registry.put(COLS_KEY, table.getColumnLayout());
    
    super.removeNotify();
  }
  
  
  boolean accepts(Property prop) {
    return prop instanceof Entity;
  }
  public void setPropertyImpl(Property property) {

    
    
    
    
    table.setModel(property!=null ? new Model((Entity)property) : null);
    
    
  }
  
  private final static TagPath 
    DOT = new TagPath(".");
  
  private class Model extends AbstractPropertyTableModel {
    
    private List xrefs = new ArrayList();
    private Entity entity;
    
    private Model(Entity entity) {
      this.entity = entity;
      for (Iterator ps = entity.getProperties(PropertyXRef.class).iterator(); ps.hasNext(); ) {
        PropertyXRef p = (PropertyXRef)ps.next();
        if (p.getTarget()!=null)
          xrefs.add(p);
      }
    }
    
    public Gedcom getGedcom() {
      return entity.getGedcom();
    }
    
    public int getNumCols() {
      return 1;
    }
    
    public int getNumRows() {
      return xrefs.size();
    }
    
    public TagPath getPath(int col) {
      return DOT;
    }
    
    public String getName(int col) {
      return entity.getPropertyName();
    }
    
    public Property getProperty(int row) {
      return (Property)xrefs.get(row);
    }
  };
  

} 
