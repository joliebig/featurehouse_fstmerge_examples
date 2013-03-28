
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Fam;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Dimension;


public class SpousesBean extends PropertyBean {
  
  private final static String COLS_KEY = "bean.spouses.cols";
  
  private PropertyTableWidget table;
  
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
    return prop instanceof Fam;
  }

  public void setPropertyImpl(Property fam) {
    table.setModel(fam!=null?new SpousesInFamily((Fam)fam):null);
  }
  
  public Property getProperty() {
    
    return null;
  }
  
  private static class SpousesInFamily extends AbstractPropertyTableModel {
    
    private final static TagPath[] PATHS = {




        new TagPath("INDI"),
        new TagPath("INDI:NAME"),
        new TagPath("INDI:BIRT:DATE"),
        new TagPath("INDI:BIRT:PLAC"),
    };
    
    private Fam fam;
    
    private SpousesInFamily(Fam fam) {
      super(fam.getGedcom());
      this.fam = fam;
    }
    public int getNumCols() {
      return PATHS.length;
    }
    public int getNumRows() {
      return fam.getNoOfSpouses();
    }
    public TagPath getPath(int col) {
      return PATHS[col];
    }
    public Property getProperty(int row) {
      return fam.getSpouse(row);
    }
  }

} 
