
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Gedcom;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyChild;
import genj.gedcom.PropertyFamilyChild;
import genj.gedcom.PropertyFamilySpouse;
import genj.gedcom.PropertyHusband;
import genj.gedcom.PropertyWife;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;


public class ReferencesBean extends PropertyBean {

  public static Icon IMG = MetaProperty.IMG_LINK;

  private PropertyTableWidget table;
  
  public ReferencesBean() {
    
    
    table = new PropertyTableWidget();
    table.setVisibleRowCount(2);
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, table);

  }
  
  @Override
  public void removeNotify() {
    REGISTRY.put("refcols", table.getColumnLayout());
    super.removeNotify();
  }
  
  @Override
  protected void commitImpl(Property property) {
    
  }

  
  protected void setPropertyImpl(Property prop) {

    Model model = null;
    
    if (prop!=null)
      model = getModel(prop);
      
    table.setModel(model);
    table.setColumnLayout(REGISTRY.get("refcols",""));
  }
  
  private Model getModel(Property root) {
    
    List<PropertyXRef> rows = new ArrayList<PropertyXRef>();
    
    
    for (PropertyXRef ref : root.getProperties(PropertyXRef.class)) {
      
      if (ref instanceof PropertyHusband || ref instanceof PropertyWife || ref instanceof PropertyChild 
          || ref instanceof PropertyFamilyChild || ref instanceof PropertyFamilySpouse || !ref.isValid())
        continue;
      rows.add(ref);
    }
    
    return new Model(root, rows);
  }
  
  private class Model extends AbstractPropertyTableModel {
    
    private List<PropertyXRef> rows;
    private TagPath[] columns = new TagPath[] {
        new TagPath("..", Property.LABEL), 
        new TagPath(".", Gedcom.getName("REFN")), 
        new TagPath("*:..:..", "*"), 
      };
        
    Model(Property root, List<PropertyXRef> rows) {
      super(root.getGedcom());
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

      switch (col) {
        
        case 0: 
          return property!=getProperty() ? property.getPropertyName() : ""; 
        
        
        case 1:
          if (property instanceof PropertyXRef) {
            PropertyXRef ref = (PropertyXRef)property;
            if (ref.isTransient())
              property = ref.getTarget().getParent();
          }
          return property.getPropertyName();

        
        default:
          return property.toString();
      }
    }
    
    @Override
    public int getCellAlignment(Property property, int row, int col) {
      return LEFT;
    }
  }
} 
