
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Dimension;


public class FamiliesBean extends PropertyBean {

  private final static TagPath 
    PATH_FAM = new TagPath("FAM"),
    PATH_HUSB = new TagPath("FAM:HUSB:*:.."),
    PATH_WIFE = new TagPath("FAM:WIFE:*:.."),
    PATH_HUSB_NAME = new TagPath("FAM:HUSB:*:..:NAME"),
    PATH_WIFE_NAME = new TagPath("FAM:WIFE:*:..:NAME"),
    PATH_MARR_DATE = Fam.PATH_FAMMARRDATE,
    PATH_MARR_PLAC = Fam.PATH_FAMMARRPLAC;

  private PropertyTableWidget table;
  
  private final static String COLS_KEY = "bean.families.cols";

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
  public void setPropertyImpl(Property prop) {

    Indi indi = (Indi)prop;
    
    
    table.setModel(indi!=null?new Families(indi):null);
    
    
  }
  
  public Property getProperty() {
    
    return null;
  }
  
  private class Families extends AbstractPropertyTableModel {
    
    private Indi indi;
    private Fam[] fams;
    
    private Families(Indi indi) {
      this.indi = indi;
      fams = indi.getFamiliesWhereSpouse();
    }
    
    public Gedcom getGedcom() {
      return indi.getGedcom();
    }
    public int getNumCols() {
      return 5;
    }
    public int getNumRows() {
      return fams.length;
    }
    public TagPath getPath(int col) {
      switch (col) {
        default:
        case 0:
          return PATH_FAM;
        case 1:
          return indi.getSex() == PropertySex.FEMALE ? PATH_HUSB : PATH_WIFE;
        case 2:
          return indi.getSex() == PropertySex.FEMALE ? PATH_HUSB_NAME : PATH_WIFE_NAME;
        case 3:
          return PATH_MARR_DATE;
        case 4:
          return PATH_MARR_PLAC;
      }
    }
    public Property getProperty(int row) {
      return fams[row];
    }
  };
  

} 
