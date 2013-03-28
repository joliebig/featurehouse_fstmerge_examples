
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


public class ReferencesBean extends PropertyBean {

  public static Icon IMG = MetaProperty.IMG_LINK;

  private PropertyTableWidget table;
  
  public ReferencesBean() {
    
    
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
    
    if (prop!=null)
      model = getModel(prop);
      
    table.setModel(model);
    table.setRendererFactory(model);
  }
  
  private Model getModel(Property root) {
    
    List<PropertyXRef> rows = new ArrayList<PropertyXRef>();
    Map<Property,String> p2t = new HashMap<Property, String>();
    
    
    for (PropertyXRef ref : root.getProperties(PropertyXRef.class)) {
      
      if (ref instanceof PropertyHusband || ref instanceof PropertyWife || ref instanceof PropertyChild 
          || ref instanceof PropertyFamilyChild || ref instanceof PropertyFamilySpouse || !ref.isValid())
        continue;
      rows.add(ref);
      if (ref.isTransient())
        ref = ref.getTarget();
      p2t.put(ref, ref.getPropertyName());
    }
    
    return new Model(root, rows, p2t);
  }
  
  private class Model extends AbstractPropertyTableModel implements PropertyRendererFactory {
    
    private Map<Property,String> property2text;
    private List<PropertyXRef> rows;
    private TagPath[] columns = new TagPath[] {
        new TagPath(".", Gedcom.getName("REFN")), 
        new TagPath("*:..:..", "*"), 
      };
    private PropertyRenderer renderer = new Renderer();
    
    Model(Property root, List<PropertyXRef> rows, Map<Property,String> p2t) {
      super(root.getGedcom());
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
    }
  }
} 
