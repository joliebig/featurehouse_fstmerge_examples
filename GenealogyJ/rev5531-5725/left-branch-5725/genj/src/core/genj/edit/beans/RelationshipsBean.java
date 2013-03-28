
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
import genj.gedcom.PropertyWife;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.renderer.PropertyRenderer;
import genj.renderer.PropertyRendererFactory;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;


public class RelationshipsBean extends PropertyBean {

  public static Icon IMG = Grammar.V55.getMeta(new TagPath("FAM")).getImage().getOverLayed(MetaProperty.IMG_LINK);
    
  private PropertyTableWidget table;
  
  public RelationshipsBean() {
    
    
    table = new PropertyTableWidget();
    table.setVisibleRowCount(5);
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, table);

  }
  
  @Override
  protected void commitImpl(Property property) {
    
  }

  
  protected void setPropertyImpl(Property prop) {

    Model model = null;
    
    if (prop instanceof Indi)
      model = getModel((Indi)prop);
    if (prop instanceof Fam)
      model = getModel((Fam)prop);
      
    table.setModel(model);
    table.setRendererFactory(model);
  }
  
  private Model getModel(Fam fam) {
    
    TagPath[] columns = new TagPath[] {
      new TagPath(".", RESOURCES.getString("relationship")), 
      new TagPath("*:..", Gedcom.getName("INDI")), 
      new TagPath("*:..:NAME"), 
      new TagPath("*:..:BIRT:DATE") 
    };
    
    Map<Property,String> p2t = new HashMap<Property, String>();
    List<Property> rows = new ArrayList<Property>();
    
    
    Property husband = fam.getProperty("HUSB");
    if (husband instanceof PropertyXRef && husband.isValid()) {
      p2t.put(husband, "Father");
      rows.add(husband);
    }
    Property wife = fam.getProperty("WIFE");
    if (wife instanceof PropertyWife && wife.isValid()) {
      p2t.put(wife, "Mother");
      rows.add(wife);
    }
    
    for (Property child : fam.getProperties("CHIL")) {
      if (child instanceof PropertyXRef && child.isValid()) {
        p2t.put(child, "Child");
        rows.add(child);
      }
    }
    
    return new Model(fam.getGedcom(), columns, rows, p2t);
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
    
    Map<Property,String> p2t = new HashMap<Property, String>();
    List<Property> rows = new ArrayList<Property>();
    
    
    Fam parental = indi.getFamilyWhereBiologicalChild();
    if (parental!=null) {
      Property husband = parental.getProperty("HUSB");
      if (husband instanceof PropertyXRef && husband.isValid()) {
        p2t.put(husband, "Father");
        rows.add(husband);
      }
      Property wife = parental.getProperty("WIFE");
      if (wife instanceof PropertyWife && wife.isValid()) {
        p2t.put(wife, "Mother");
        rows.add(wife);
      }
    }
      
    
    for (Fam spousal : indi.getFamiliesWhereSpouse()) {
      Property spouse = spousal.getProperty("HUSB");
      if (spouse instanceof PropertyXRef && spouse.isValid() && ((PropertyXRef)spouse).getTargetEntity()!=indi) {
        p2t.put(spouse, "Husband");
        rows.add(spouse);
      } else {
        spouse = spousal.getProperty("WIFE");
        if (spouse instanceof PropertyXRef && spouse.isValid() && ((PropertyXRef)spouse).getTargetEntity()!=indi) {
          p2t.put(spouse, "Wife");
          rows.add(spouse);
        }
      }
      for (PropertyChild child : spousal.getProperties(PropertyChild.class)) {
        if (child.isValid()) {
          p2t.put(child, "Child");
          rows.add(child);
        }
      }
    }
    
    





    return new Model(indi.getGedcom(), columns, rows, p2t);
  }
  
  private class Model extends AbstractPropertyTableModel implements PropertyRendererFactory  {
    
    private TagPath[] columns;
    private Map<Property, String> property2text;
    private List<Property> rows;
    private PropertyRenderer renderer = new Renderer();
    
    public Model(Gedcom gedcom, TagPath[] columns, List<Property> rows, Map<Property,String> p2t) {
      super(gedcom);
      this.columns = columns;
      this.rows = rows;
      this.property2text = p2t;
    }
    
    public int getNumCols() {
      return columns.length;
    }

    public int getNumRows() {
      return rows.size();
    }
    
    public TagPath getPath(int col) {
      return columns[col];
    }

    public Property getProperty(int row) {
      return rows.get(row);
    }
    
    public PropertyRenderer getRenderer(TagPath path, Property prop) {
      return getRenderer(prop);
    }
    public PropertyRenderer getRenderer(Property prop) {
      if (property2text.containsKey(prop))
        return renderer ;
      return PropertyRendererFactory.DEFAULT.getRenderer(prop);
    }
    
    private class Renderer extends PropertyRenderer {
      @Override
      protected void renderImpl(Graphics2D g, Rectangle bounds, Property prop, Map<String,String> attributes, Point dpi) {
        super.renderImpl(g, bounds, property2text.get(prop), attributes);
      }
      @Override
      protected Dimension2D getSizeImpl(Font font, FontRenderContext context, Property prop, Map<String,String> attributes, Point dpi) {
        return super.getSizeImpl(font, context, prop, property2text.get(prop), attributes, dpi);
      }
    };
  }

} 
