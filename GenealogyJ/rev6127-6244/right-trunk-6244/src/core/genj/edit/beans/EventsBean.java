
package genj.edit.beans;

import genj.common.AbstractPropertyTableModel;
import genj.common.PropertyTableWidget;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyEvent;
import genj.gedcom.TagPath;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ListSelectionModel;


public class EventsBean extends PropertyBean {

  private static TagPath[] COLUMNS = {
    new TagPath("."),
    new TagPath(".:DATE"),
    new TagPath(".:PLAC")
  };

  private Set<Property> deletes = new HashSet<Property>();
  private Set<Property> adds = new HashSet<Property>();
  
  private Model model;
  private PropertyTableWidget table;
  
  private List<Action> actions = new ArrayList<Action>();
  
  public EventsBean() {
    
    
    table = new PropertyTableWidget();
    table.setVisibleRowCount(5);
    
    actions.add(new Edit());
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, table);

  }
  
  @Override
  public List<Action> getActions() {
    return actions;
  }
  
  @Override
  public void removeNotify() {
    REGISTRY.put("eventcols", table.getColumnLayout());
    super.removeNotify();
  }
  
  
  private class Edit extends Action2 {
    Edit() {
      setImage(PropertyEvent.IMG);
      setTip(RESOURCES.getString("even.edit"));
      table.setColSelection(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      table.setRowSelection(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      
      Property event = table.getSelectedRow();
      
      if (0!=DialogHelper.openDialog(getTip(), DialogHelper.QUESTION_MESSAGE, ""+event, Action2.okOnly(), EventsBean.this))
        return;
      
      
      EventsBean.this.changeSupport.fireChangeEvent();
      
    }
  } 

  @Override
  protected void commitImpl(Property property) {
  }

  @Override
  protected void setPropertyImpl(Property prop) {

    deletes.clear();
    adds.clear();
    
    model = prop==null ? null : new Model(prop);
    
    table.setModel(model);
    
    table.setColumnLayout(REGISTRY.get("eventcols",""));
  }
  
  private class Model extends AbstractPropertyTableModel {
    
    private List<Property> events = new ArrayList<Property>();
    
    Model(Property root) {
      
      super(root.getGedcom());
      
      
      for (Property child : root.getProperties()) {
        if (child.getMetaProperty().allows("DATE"))
          events.add(child);
      }
      
      
    }
    
    private List<Property> getEvents(int[] indices) {
      List<Property> result = new ArrayList<Property>(indices.length);
      for (int i=0;i<indices.length;i++)
        result.add(events.get(indices[i]));
      return result;
    }
    
    private void remove(Property event) {
      for (int i=0;i<events.size();i++) {
        if (events.get(i)==event) {
          events.remove(i);
          fireRowsDeleted(i, i);
          return;
        }
      }
      throw new IllegalArgumentException("no such event to remove");
    }
    
    @Override
    public String getColName(int col) {
      if (col==0)
        return Gedcom.getName("EVEN");
      return super.getColName(col);
    }
    
    @Override
    public int getNumCols() {
      return COLUMNS.length;
    }

    @Override
    public int getNumRows() {
      return events.size();
    }

    @Override
    public TagPath getColPath(int col) {
      return COLUMNS[col];
    }

    @Override
    public Property getRowRoot(int row) {
      return events.get(row);
    }
    
    @Override
    public int compare(Property valueA, Property valueB, int col) {
      if (col==0) 
        return toString(valueA).compareTo(toString(valueB));
      return super.compare(valueA, valueB, col);
    }
    
    private String toString(Property prop) {
      String result = prop.getPropertyName();
      String val = prop.getDisplayValue();
      if (val.length()==0)
        val = prop.getPropertyValue("TYPE");
      if (val.length()>0)
        result += " ("+val+")";
      return  result;
    }
    
    @Override
    public String getCellValue(Property property, int row, int col) {
      if (col==0) 
        return toString(property);
      return super.getCellValue(property, row, col);
    }
  }

}
