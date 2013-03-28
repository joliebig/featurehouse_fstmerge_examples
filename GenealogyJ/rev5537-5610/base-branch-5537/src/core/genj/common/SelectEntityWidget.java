
package genj.common;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyDate;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.PopupWidget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;


public class SelectEntityWidget extends JPanel {
  
  private final static Resources RESOURCES = Resources.get(SelectEntityWidget.class);

  
  private String type = Gedcom.INDI;
  
  
  private Object[] list;
  private Object none;
  
  
  private PopupWidget sortWidget;
  private JComboBox listWidget;
  
  
  private Registry registry = Registry.lookup("genj", null);
  
  
  private Sort sort;
  private List<Sort> sorts;
  
  private final static String[] SORTS = {
    "INDI:NAME",
    "INDI",
    "INDI:BIRT:DATE",
    "INDI:DEAT:DATE",
    "FAM",
    "FAM:MARR:DATE",
    "OBJE", 
    "OBJE:TITL", 
    "NOTE", 
    "NOTE:NOTE", 
    "SOUR", 
    "SOUR:TITL", 
    "SOUR:AUTH", 
    "SOUR:REPO", 
    "SUBM", 
    "REPO",
    "REPO:NAME",
    "REPO:REFN",
    "REPO:RIN"
  };
  
  
  public SelectEntityWidget(Gedcom gedcom, String type, String none) {

    
    this.type = type;
    this.none = none;
    
    Collection entities = gedcom.getEntities(type);

    
    if (none!=null) {
      list = new Object[entities.size()+1];
      list[0] = none;
    } else {
      list = new Object[entities.size()];
    }
    Iterator es=entities.iterator();
    for (int e= none!=null ? 1 : 0;e<list.length;e++) {
      Entity ent = (Entity)es.next();
      if (!ent.getTag().equals(type))
        throw new IllegalArgumentException("Type of all entities has to be "+type);
      list[e] = ent;
    }

    
    sorts = new ArrayList<Sort>(SORTS.length);
    for (int i=0;i<SORTS.length;i++) {
      String path = SORTS[i];
      if (!path.startsWith(type))
        continue;
      Sort s = new Sort(path);
      sorts.add(s);
      if (sort==null||path.equals(registry.get("select.sort."+type, ""))) sort = s;
    }

    
    sortWidget = new PopupWidget();
    sortWidget.setActions(sorts);
    
    
    listWidget = new JComboBox();
    listWidget.setMaximumRowCount(16); 
    listWidget.setEditable(false);
    listWidget.setRenderer(new Renderer());
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, listWidget);
    add(BorderLayout.WEST  , sortWidget);

    
    if (sort!=null) 
      sort.trigger();
    if (list.length>0) listWidget.setSelectedIndex(0);
    
    
  }
  
  
  public Dimension getMaximumSize() {
    return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(128, super.getPreferredSize().height);
  }
  
  
  public int getEntityCount() {
    return listWidget.getItemCount()-1;
  }
  
  
  public Entity getSelection() {
    
    Object item = listWidget.getSelectedItem();
    if (!(item instanceof Entity))
      return null;
    
    return (Entity)item;
  }
  
  
  public void setSelection(Entity set) {
    
    if (set==null)
      listWidget.setSelectedItem(none!=null ? none : null);
    
    if (!(set instanceof Entity)||!set.getTag().equals(type))
      return;
    
    listWidget.setSelectedItem(set);
  }
  
  
  public void addActionListener(ActionListener listener) {
    listWidget.addActionListener(listener);
  }
  
  
  public void removeActionListener(ActionListener listener) {
    listWidget.removeActionListener(listener);
  }
  
  
  private class Renderer extends DefaultListCellRenderer {
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      
      String txt;
      if (value instanceof Entity) {
        txt = getString((Entity)value);
      } else {
        txt = value!=null ? value.toString() : "";
      }

      return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
    }

    
    private String getString(Entity e) {
      
      if (sort==null)
        return e.toString();
      
      Property p = e.getProperty(sort.tagPath);
      
      WordBuffer value = new WordBuffer(", ");
      
      
      value.append(getString(e.getProperty(sort.tagPath), "?"));
      
      
      for (Sort other : sorts) {
        if (other!=sort && other.tagPath.getFirst().equals(sort.tagPath.getFirst())) {
          value.append(getString(e.getProperty(other.tagPath), ""));
        }
      }
      
      
      return value.toString(); 
    }
    
    private String getString(Property p, String fallback) {
      if (p instanceof Entity)
        return ((Entity)p).getId();
      else
        return p!=null&&p.isValid() ? p.getDisplayValue() : fallback;
    }

  } 
  
  
  private class Sort extends Action2 {

    
    private TagPath tagPath;
    
    
    private Sort(String path) {
      
      
      tagPath = new TagPath(path);

      
      MetaProperty meta;
      if (tagPath.length()>1&&tagPath.getLast().equals(PropertyDate.TAG))
        meta = Grammar.V55.getMeta(new TagPath(tagPath, tagPath.length()-1));
      else
        meta = Grammar.V55.getMeta(tagPath);
      setImage(meta.getImage());
      
      
      setText(RESOURCES.getString("select.sort", tagPath.length()==1?"ID":meta.getName()));
      
      
    }      
    
    
    protected void execute() {
      
      sort = this;
      registry.put("select.sort."+type, tagPath.toString());
      
      Comparator comparator = new PropertyComparator(tagPath);
      Arrays.sort(list, none!=null ? 1 : 0, list.length, comparator);
      
      Entity selection = getSelection();
      listWidget.setModel(new DefaultComboBoxModel(list));
      sortWidget.setIcon(getImage());
      sortWidget.setToolTipText(getText());
      setSelection(selection);
    }
        
  } 
   
} 
