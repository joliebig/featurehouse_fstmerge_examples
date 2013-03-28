
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.Indi;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyChild;
import genj.gedcom.PropertyHusband;
import genj.gedcom.PropertyWife;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;


public class RelationshipsBean extends PropertyBean {

  public static Icon IMG = Grammar.V55.getMeta(new TagPath("FAM")).getImage().getOverLayed(MetaProperty.IMG_LINK);
    
  private PropertyTableWidget table;
  private Map<Property,String> relationships = new HashMap<Property,String>();
  
  public RelationshipsBean() {
    
    
    table = new PropertyTableWidget();
    table.setVisibleRowCount(5);
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, table);

  }
  
  @Override
  public void removeNotify() {
    REGISTRY.put("relcols", table.getColumnLayout());
    super.removeNotify();
  }
  
  @Override
  protected void commitImpl(Property property) {
    
  }

  
  protected void setPropertyImpl(Property prop) {
    
    relationships.clear();

    Model model = null;
    if (prop instanceof Indi)
      model = getModel((Indi)prop);
    if (prop instanceof Fam)
      model = getModel((Fam)prop);
      
    table.setModel(model);
    table.setColumnLayout(REGISTRY.get("relcols",""));
  }
  
  private Model getModel(Fam fam) {
    
    TagPath[] columns = new TagPath[] {
      new TagPath(".", RESOURCES.getString("relationship")), 
      new TagPath("*:..", Gedcom.getName("INDI")), 
      new TagPath("*:..:NAME"), 
      new TagPath("*:..:BIRT:DATE") 
    };
    
    List<Property> rows = new ArrayList<Property>();
    
    
    Property husband = fam.getProperty("HUSB");
    if (husband instanceof PropertyXRef && husband.isValid()) {
      relationships.put(husband, PropertyHusband.LABEL_FATHER);
      rows.add(husband);
    }
    Property wife = fam.getProperty("WIFE");
    if (wife instanceof PropertyWife && wife.isValid()) {
      relationships.put(wife, PropertyWife.LABEL_MOTHER);
      rows.add(wife);
    }
    
    for (Property child : fam.getProperties("CHIL")) {
      if (child instanceof PropertyXRef && child.isValid()) {
        relationships.put(child, child.getPropertyName());
        rows.add(child);
      }
    }
    
    return new Model(fam.getGedcom(), columns, rows);
  }
  
  private Model getModel(Indi indi) {
    
    TagPath[] columns = new TagPath[] {
      new TagPath(".", RESOURCES.getString("relationship")), 
      new TagPath("*:..", Gedcom.getName("INDI")), 
      new TagPath("*:..:NAME"), 
      new TagPath("*:..:BIRT:DATE"), 
      new TagPath("..", Gedcom.getName("FAM")), 
      new TagPath("..:MARR:DATE") 
    };
    
    List<Property> rows = new ArrayList<Property>();
    
    
    Fam parental = indi.getFamilyWhereBiologicalChild();
    if (parental!=null) {
      Property husband = parental.getProperty("HUSB");
      if (husband instanceof PropertyXRef && husband.isValid()) {
        relationships.put(husband, PropertyHusband.LABEL_FATHER);
        rows.add(husband);
      }
      Property wife = parental.getProperty("WIFE");
      if (wife instanceof PropertyWife && wife.isValid()) {
        relationships.put(wife, PropertyWife.LABEL_MOTHER);
        rows.add(wife);
      }
    }
      
    
    for (Fam spousal : indi.getFamiliesWhereSpouse()) {
      Property spouse = spousal.getProperty("HUSB");
      if (spouse instanceof PropertyXRef && spouse.isValid() && ((PropertyXRef)spouse).getTargetEntity()!=indi) {
        relationships.put(spouse, spouse.getPropertyName());
        rows.add(spouse);
      } else {
        spouse = spousal.getProperty("WIFE");
        if (spouse instanceof PropertyXRef && spouse.isValid() && ((PropertyXRef)spouse).getTargetEntity()!=indi) {
          relationships.put(spouse, spouse.getPropertyName());
          rows.add(spouse);
        }
      }
      for (PropertyChild child : spousal.getProperties(PropertyChild.class)) {
        if (child.isValid()) {
          relationships.put(child, child.getPropertyName());
          rows.add(child);
        }
      }
    }

    return new Model(indi.getGedcom(), columns, rows);
  }
  
  private class Model extends AbstractPropertyTableModel {
    
    private TagPath[] columns;
    private List<Property> rows;
    
    public Model(Gedcom gedcom, TagPath[] columns, List<Property> rows) {
      super(gedcom);
      this.columns = columns;
      this.rows = rows;
    }
    
    public int getNumCols() {
      return columns.length;
    }

    public int getNumRows() {
      return rows.size();
    }
    
    public TagPath getColPath(int col) {
      return columns[col];
    }

    public Property getRowRoot(int row) {
      return rows.get(row);
    }
    
    @Override
    public String getCellValue(Property property, int row, int col) {
      String relationship = relationships.get(property);
      return relationship!=null ? relationship : super.getCellValue(property, row, col);
    }
  }
} 
