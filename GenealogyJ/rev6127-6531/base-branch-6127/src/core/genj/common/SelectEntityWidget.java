
package genj.common;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyComparator;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;


public class SelectEntityWidget extends JPanel {
  
  private final static Resources RESOURCES = Resources.get(SelectEntityWidget.class);
  private final static Registry REGISTRY = Registry.get(SelectEntityWidget.class);

  
  private String type = Gedcom.INDI;
  
  
  private Entity[] list;
  private Object none;
  
  
  private PopupWidget sortWidget;
  private JComboBox listWidget;
  
  
  private TagPath sort;
  private List<TagPath> sorts;
  
  private final static String[] SORTS = {
    "INDI:NAME",
    "INDI",
    "INDI:BIRT:DATE",
    "INDI:DEAT:DATE",
    "FAM",
    "FAM:MARR:DATE",
    "FAM:HUSB:*:..:NAME",
    "FAM:WIFE:*:..:NAME",
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
    
    Collection<? extends Entity> entities = gedcom.getEntities(type);
    
    list = new Entity[entities.size()];
    int e=0; for (Entity entity : entities) {
      if (!entity.getTag().equals(type))
        throw new IllegalArgumentException("Type of all entities has to be "+type);
      list[e++] = entity;
    }

    
    sorts = new ArrayList<TagPath>(SORTS.length);
    for (int i=0;i<SORTS.length;i++) {
      String path = SORTS[i];
      if (!path.startsWith(type))
        continue;
      TagPath p = new TagPath(path);
      sorts.add(p);
      if (sort==null||path.equals(REGISTRY.get("select.sort."+type, ""))) sort = p;
    }

    
    sortWidget = new PopupWidget();
    for (TagPath sort : sorts)
      sortWidget.addItem(new Sort(sort));
    
    
    listWidget = new JComboBox();
    listWidget.setMaximumRowCount(16); 
    listWidget.setEditable(false);
    listWidget.setRenderer(new Renderer());
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, listWidget);
    add(BorderLayout.WEST  , sortWidget);

    
    sort(sort);
    if (none!=null||list.length>0) listWidget.setSelectedIndex(0);
    
    
  }
  
  
  public void sort(TagPath path) {
    
    sort = path;
    REGISTRY.put("select.sort."+type, path.toString());
    
    PropertyComparator comparator = new PropertyComparator(path);
    Arrays.sort(list, comparator);
    
    Entity selection = getSelection();
    listWidget.setModel(new Model());
    sortWidget.setIcon(getPathImage(path));
    sortWidget.setToolTipText(getPathText(path));
    setSelection(selection);
  }
  
  private class Model extends AbstractListModel implements ComboBoxModel {
    
    private Object selection;

    public Object getSelectedItem() {
      return selection;
    }

    public void setSelectedItem(Object set) {
      selection = set;
    }

    public Object getElementAt(int index) {
      if (none!=null) 
        return index==0 ? none : list[index-1];
      return list[index];
    }

    public int getSize() {
      return list.length + (none!=null?1:0);
    }
    
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
    else if (set.getTag().equals(type))
      listWidget.setSelectedItem(set);
  }
  
  
  public void addActionListener(ActionListener listener) {
    listWidget.addActionListener(listener);
  }
  
  
  public void removeActionListener(ActionListener listener) {
    listWidget.removeActionListener(listener);
  }
  
  private MetaProperty getMeta(TagPath tagPath) {
    MetaProperty meta;
    if (tagPath.length()>1)
      meta = Grammar.V55.getMeta(new TagPath(tagPath, 2));
    else
      meta = Grammar.V55.getMeta(tagPath);
    return meta;
  }
  
  private ImageIcon getPathImage(TagPath tagPath) {
    return getMeta(tagPath).getImage();
  }
  
  private String getPathText(TagPath tagPath) {
    return RESOURCES.getString("select.sort", tagPath.length()==1?"ID":getMeta(tagPath).getName());
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
      
      WordBuffer value = new WordBuffer(", ");
      
      
      value.append(getString(e.getProperty(sort), "?"));
      
      
      for (TagPath other : sorts) {
        if (!other.equals(sort) && other.getFirst().equals(sort.getFirst())) {
          value.append(getString(e.getProperty(other), ""));
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
    
    
    private Sort(TagPath path) {
      tagPath = path;
      setImage(getPathImage(path));
      setTip(getPathText(path));
    }      
    
    
    public void actionPerformed(ActionEvent event) {
      sort(tagPath);
    }
        
  } 
   
} 
