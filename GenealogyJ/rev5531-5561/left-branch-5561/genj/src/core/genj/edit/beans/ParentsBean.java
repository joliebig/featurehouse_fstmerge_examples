
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyHusband;
import genj.gedcom.PropertyWife;
import genj.gedcom.TagPath;
import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Dimension;


public class ParentsBean extends PropertyBean {
  
  private final static String COLS_KEY = "bean.parents.cols";
  
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
    return prop instanceof Indi;
  }
  
  public void setPropertyImpl(Property indi) {
    table.setModel(indi!=null ? new ParentsOfChild((Indi)indi) : null);
  }
  
  public Property getProperty() {
    
    return null;
  }
  
  private static class ParentsOfChild extends AbstractPropertyTableModel {
    
    private final static TagPath PATHS[] = {
      new TagPath("FAM"),  
      new TagPath("FAM:HUSB:*:..", PropertyHusband.LABEL_FATHER),  
      new TagPath("FAM:HUSB:*:..:NAME"),  
      new TagPath("FAM:WIFE:*:..", PropertyWife.LABEL_MOTHER),
      new TagPath("FAM:WIFE:*:..:NAME")
    };
    
    private Indi child;
    private Fam[] familiesWhereChild;
    
    private ParentsOfChild(Indi child) {
      this.child = child;
      familiesWhereChild = child.getFamiliesWhereChild();
    }
      
    public Gedcom getGedcom() {
      return child.getGedcom();
    }
    public int getNumCols() {
      return PATHS.length;
    }
    public int getNumRows() {
      return familiesWhereChild.length;
    }
    public TagPath getPath(int col) {
      return PATHS[col];
    }
    public Property getProperty(int row) {
      return familiesWhereChild[row];
    }
  }

} 
