
package genj.edit.beans;

import genj.edit.BeanPanel;
import genj.edit.ChoosePropertyBean;
import genj.edit.EditView;
import genj.edit.Images;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Grammar;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyNote;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.DialogHelper.ComponentVisitor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class EventsBean extends PropertyBean {
  
  private Col[] COLS = {
      new EventCol(),
      new DetailCol(),
      new ValueCol("DATE"),
      new ValueCol("PLAC"),
      new NoteCol(),
      new SourceCol(),
      new EditCol(),
      new DelCol()
  };
  
  private final static ImageIcon 
    SOURCE = Grammar.V551.getMeta(new TagPath("SOUR")).getImage(),
    NOSOURCE = SOURCE.getTransparent(64),
    NOTE = Grammar.V551.getMeta(new TagPath("NOTE")).getImage(),
    NONOTE = NOTE.getTransparent(64);

  private JTable table;
  private Runnable commit;
  private Mouser mouser = new Mouser();
  
  public EventsBean() {
    
    
    table = new JTable(new Events(null), columns()) {
      @Override
      public String getToolTipText(MouseEvent event) {
        Col col = mouser.getColumn(event);
        return col!=null ? col.getTip() : null;
      }
      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component result = super.prepareRenderer(renderer, row, column);
        if (result instanceof JComponent) ((JComponent)result).setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        return result;
      }
    };
    table.setPreferredScrollableViewportSize(new Dimension(32,32));
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.addMouseListener(mouser);
    table.addMouseMotionListener(mouser);
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(table));

  }
  
  private Events getModel() {
    return (Events)table.getModel();
  }
  
  private TableColumnModel columns() {
    DefaultTableColumnModel result = new DefaultTableColumnModel();
    for (int i=0; i<COLS.length; i++) {
      TableColumn c = new TableColumn(i);
      c.setMaxWidth(COLS[i].max);
      c.setHeaderValue(COLS[i].getName());
      result.addColumn(c);
    }
    return result;
  }
  
  @Override
  public List<? extends Action> getActions() {
    return Collections.singletonList(new Add());
  }
  
  @Override
  protected void commitImpl(Property property) {
    
    if (commit!=null) {
      Runnable r = commit;
      commit = null;
      r.run();
    }
    
  }
  
  private void commit(final Runnable commit) {
    
    changeSupport.fireChangeEvent();
    
    EditView view = (EditView)DialogHelper.visitContainers(this, new ComponentVisitor() {
      @Override
      public Component visit(Component component, Component child) {
        return component instanceof EditView ? component : null;
      }
    });
    
    if (view!=null) {
      this.commit = commit;
      view.commit(false);
    } else {
      if (root!=null)
        root.getGedcom().doMuteUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) throws GedcomException {
            commit.run();
          }
        });
    }
    
  }
  
  private boolean isEvent(MetaProperty meta) {
    
    
    while (meta!=null) {
      if (PropertyEvent.class.isAssignableFrom(meta.getType()))
        return true;
      meta = meta.getSuper();
    }
    return false;
  }

  private boolean isEditable(Property property) {
    return isEditable(property.getMetaProperty());
  }
  private boolean isEditable(MetaProperty meta) {
    
    
    for (PropertyBean bean : session) {
      if (bean.path.contains(meta.getTag()))
        return false;
    }

    return true;
  }

  @Override
  protected void setPropertyImpl(Property prop) {
    
    commit = null;
    
    table.setModel(new Events(prop));
  }
  
  private class Mouser extends MouseAdapter implements MouseMotionListener {
    
    private Property getProperty(MouseEvent e) {
      int row = table.rowAtPoint(e.getPoint());
      return row<0 ? null : getModel().rows.get(row);
    }
    
    private Col getColumn(MouseEvent e) {
      int col = table.columnAtPoint(e.getPoint());
      return col<0 ? null : COLS[col];
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
      Cursor cursor = null;
      Col col = getColumn(e);
      Property prop = getProperty(e);
      if (prop!=null && col instanceof ActionCol && ((ActionCol)col).performs(prop)) 
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
      table.setCursor(cursor);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
      Col col = getColumn(e);
      Property prop = getProperty(e);
      if (prop!=null && col instanceof ActionCol && ((ActionCol)col).performs(prop))
        ((ActionCol)col).perform(prop);
    }
  }
  
  private class Events extends AbstractTableModel {
    
    private List<Property> rows = new ArrayList<Property>();
    
    Events(Property root) {
      
      
      if (root!=null) for (Property child : root.getProperties()) {

        if (!isEvent(child.getMetaProperty()))
          continue;
        
        
        rows.add(child);
      }

      sort();
    }
    
    void sort() {
      if (rows.isEmpty())
        return;
      Collections.sort(rows, new PropertyComparator(".:DATE"));
      fireTableRowsUpdated(0, rows.size()-1);
    }
    
    void add(Property event) {
      rows.add(0, event);
      fireTableRowsInserted(0, 0);
    }
    
    void del(Property event) {
      for (int i=0;i<rows.size();i++) {
        if (rows.get(i)==event) {
          rows.remove(i);
          fireTableRowsDeleted(i, i);
          return;
        }
      }
      throw new IllegalArgumentException("no such "+event);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return COLS[columnIndex].getType();
    }
    
    @Override
    public int getColumnCount() {
      return COLS.length;
    }
    
    @Override
    public int getRowCount() {
      return rows.size();
    }
    
    @Override
    public String getColumnName(int column) {
      return COLS[column].getName();
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return COLS[columnIndex].getValue(rows.get(rowIndex));
    }
  }
  
  
  private class Add extends Action2 {
    
    private Property added;
    
    Add() {
      setImage(PropertyEvent.IMG.getOverLayed(Images.imgNew));
      setTip(RESOURCES.getString("even.add"));
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
      
      final Property root = getProperty();
      
      MetaProperty[] metas = root.getNestedMetaProperties(MetaProperty.WHERE_NOT_HIDDEN | MetaProperty.WHERE_CARDINALITY_ALLOWS);
      List<MetaProperty> choices = new ArrayList<MetaProperty>(metas.length);
      for (MetaProperty meta : metas) {
        if (isEvent(meta) && isEditable(meta))
          choices.add(meta);
      }
      final ChoosePropertyBean choose = new ChoosePropertyBean(choices.toArray(new MetaProperty[choices.size()]));
      choose.setSingleSelection(true);
      if (0!=DialogHelper.openDialog(getTip(), DialogHelper.QUESTION_MESSAGE, 
          choose, Action2.okCancel(), EventsBean.this))
        return;
      
      final String add = choose.getSelectedTags()[0];
      
      commit(new Runnable() {
        public void run() {
          added = root.addProperty(add, "");
          getModel().add(added);
        }
      });

      if (added!=null)
        new EditCol().perform(added);
            
    }
    
  } 

  private abstract class Col {
    protected Class<?> type;
    protected int max = Integer.MAX_VALUE;
    Col() {
      type = String.class;
    }
    Class<?> getType() {
      return type;
    }
    String getName() {
      return "";
    }
    abstract Object getValue(Property event);
    int getMax() {
      return max;
    }
    String getTip() {
      return null;
    }
  }
  
  private class EventCol extends Col {
    @Override
    String getName() {
      return Gedcom.getName("EVEN");
    }
    @Override
    Object getValue(Property prop) {
      return prop.getPropertyName();
    }
  }
  
  private class ValueCol extends Col {
    private String tag, name;
    ValueCol(String tag) {
      this(tag, Gedcom.getName(tag));
    }
    ValueCol(String tag, String name) {
      this.tag = tag;
      this.name = name;
    }
    @Override
    String getName() {
      return name;
    }
    @Override
    Object getValue(Property event) {
      return event.getPropertyDisplayValue(tag);
    }
    
  }
  
  private class DetailCol extends Col {
    @Override
    String getName() {
      return RESOURCES.getString("even.detail");
    }
    @Override
    Object getValue(Property event) {
      String val = event.getDisplayValue();
      if (val.length()>0)
        return val;
      String type = event.getPropertyDisplayValue("TYPE");
      if (type.length()>0)
        return type;
      String plac = event.getPropertyDisplayValue("PLAC");
      if (plac.length()>0)
        return plac;
      Property addr = event.getProperty("ADDR");
      if (addr!=null) {
        WordBuffer buf = new WordBuffer(",");
        buf.append(addr.getDisplayValue());
        buf.append(addr.getPropertyDisplayValue("CITY"));
        buf.append(addr.getPropertyDisplayValue("POST"));
        buf.append(addr.getPropertyDisplayValue("CTRY"));
        buf.append(addr.getPropertyDisplayValue("STAE"));
        if (buf.length()>0)
          return buf.toString();
      }
      return "";
    }
  }
  
  private abstract class ActionCol extends Col {
    private String tip;
    ActionCol(String tip) {
      this.tip = tip;
      this.type = ImageIcon.class; 
      this.max = Gedcom.getImage().getIconWidth();
    }
    abstract void perform(Property property);
    boolean performs(Property property) {
      return true;
    }
    @Override
    String getTip() {
      return tip;
    }
  }
  
  private class NoteCol extends ActionCol {
    public NoteCol() {
      super(Gedcom.getName("NOTE"));
    }
    @Override
    Object getValue(Property event) {
      for (Property note : event.getProperties("NOTE")) {
        if (note instanceof PropertyNote)
          return NOTE;
        if (note.getValue().length()>0)
          return NOTE;
      }
      return NONOTE;
    }
    @Override
    void perform(Property property) {
    }
  }
 
  private class SourceCol extends ActionCol {
    public SourceCol() {
      super(Gedcom.getName("SOUR"));
    }
    @Override
    Object getValue(Property event) {
      for (Property source : event.getProperties("SOUR")) {
        return SOURCE;
      }
      return NOSOURCE;
    }
    @Override
    void perform(Property property) {
    }
  }
  
  private class EditCol extends ActionCol {
    public EditCol() {
      super(RESOURCES.getString("even.edit"));
    }
    @Override
    Object getValue(Property event) {
      return isEditable(event) ? Images.imgView : null;
    }
    @Override
    void perform(Property property) {
      
      final BeanPanel panel = new BeanPanel();
      panel.setRoot(property);
      
      final Action[] actions = Action2.okCancel();
      actions[0].setEnabled(false);
      
      panel.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          actions[0].setEnabled(true);
        }
      });
      
      if (0==DialogHelper.openDialog(RESOURCES.getString("even.edit"), DialogHelper.QUESTION_MESSAGE, panel, actions, EventsBean.this)) {
        commit(new Runnable() {
          public void run() {
            panel.commit();
            getModel().sort();
          }
        });
      }
      
      panel.setRoot(null);
      
    }
    @Override
    boolean performs(Property event) {
      return isEditable(event);
    }
  }
  
  private class DelCol extends ActionCol {
    public DelCol() {
      super(RESOURCES.getString("even.del"));
    }
    @Override
    Object getValue(Property event) {
      return isEditable(event) ? Images.imgDel : null;
    }
    @Override
    void perform(final Property property) {

      if (0!=DialogHelper.openDialog(RESOURCES.getString("even.del"), DialogHelper.QUESTION_MESSAGE, 
          RESOURCES.getString("even.del.confirm", property),
          Action2.okCancel(), EventsBean.this))
        return;

      commit(new Runnable() {
        public void run() {
          if (property.getParent()!=null) {
            property.getParent().delProperty(property);
            getModel().del(property);
          }
        }
      });
    }
    @Override
    boolean performs(Property event) {
      return isEditable(event);
    }
  }
}
