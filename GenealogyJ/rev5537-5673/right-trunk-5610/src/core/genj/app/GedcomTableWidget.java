
package genj.app;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomDirectory;
import genj.gedcom.GedcomMetaListener;
import genj.gedcom.Grammar;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.SortableTableModel;
import genj.view.ContextProvider;
import genj.view.ContextSelectionEvent;
import genj.view.ViewContext;
import genj.view.ViewManager;
import genj.window.WindowBroadcastEvent;
import genj.window.WindowBroadcastListener;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


 class GedcomTableWidget extends JTable implements ContextProvider, WindowBroadcastListener {
  
  
  private static final int defaultWidths[] = {
    96, 24, 24, 24, 24, 24, 24, 24, 48
  };
  
  private static final Object COLUMNS[] = {
    Resources.get(GedcomTableWidget.class).getString("cc.column_header.name"),
    Gedcom.getEntityImage(Gedcom.INDI),
    Gedcom.getEntityImage(Gedcom.FAM),
    Gedcom.getEntityImage(Gedcom.OBJE), 
    Gedcom.getEntityImage(Gedcom.NOTE), 
    Gedcom.getEntityImage(Gedcom.SOUR), 
    Gedcom.getEntityImage(Gedcom.SUBM), 
    Gedcom.getEntityImage(Gedcom.REPO),
    Grammar.V55.getMeta(new TagPath("INDI:CHAN")).getImage()
  };

  
  private Registry registry;
  
  
  public GedcomTableWidget(ViewManager mgr, Registry reGistry) {

    registry = reGistry;
    
    
    GedcomTableModel model = new GedcomTableModel();
    
    
    TableColumnModel cm = new DefaultTableColumnModel();
    for (int h=0; h<COLUMNS.length; h++) {
      TableColumn col = new TableColumn(h);
      col.setHeaderValue(COLUMNS[h]);
      col.setWidth(defaultWidths[h]);
      col.setPreferredWidth(defaultWidths[h]);
      cm.addColumn(col);
    }
    setModel(new SortableTableModel(model, getTableHeader()));
    setColumnModel(cm);

    
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    getTableHeader().setReorderingAllowed(false);
    
    
    int[] widths = registry.get("columns",new int[0]);
    for (int c=0, max=getColumnModel().getColumnCount(); c<widths.length&&c<max; c++) {
      TableColumn col = getColumnModel().getColumn(c);
      col.setPreferredWidth(widths[c]);
      col.setWidth(widths[c]);
    }    
    
    
    getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        int col = getColumnModel().getColumnIndexAtX(e.getX());
        String tip = col<=0||col>Gedcom.ENTITIES.length ? null : Gedcom.getName(Gedcom.ENTITIES[col-1]);
        getTableHeader().setToolTipText(tip);
      }
    });

    
  }
  
  public Dimension getPreferredScrollableViewportSize() {
    return new Dimension(Math.max(128, getColumnModel().getTotalColumnWidth()), Math.max(4, getModel().getRowCount())*getRowHeight());
  }
  
  
  public ViewContext getContext() {
    int row = getSelectedRow();
    return row<0 ? null : new ViewContext(GedcomDirectory.getInstance().getGedcoms().get(row));
  }

  
  public boolean handleBroadcastEvent(WindowBroadcastEvent event) {
    
    ContextSelectionEvent cse = ContextSelectionEvent.narrow(event);
    if (cse!=null) {
      int row = GedcomDirectory.getInstance().getGedcoms().indexOf(cse.getContext().getGedcom());
      if (row>=0)
        getSelectionModel().setSelectionInterval(row,row);
    }
    
    return true;
  }

  
  public void removeNotify() {
    
    int[] widths = new int[getColumnModel().getColumnCount()];
    for (int c=0; c<widths.length; c++) {
      widths[c] = getColumnModel().getColumn(c).getWidth();
    }
    registry.put("columns", widths);
    
    super.removeNotify();
  }
  
  
  public Gedcom getSelectedGedcom() {
    int row = getSelectedRow();
    return row<0 ? null : GedcomDirectory.getInstance().getGedcoms().get(row);
  }

  
  private class GedcomTableModel extends AbstractTableModel implements GedcomDirectory.Listener, GedcomMetaListener {
    
    @Override
    public void addTableModelListener(TableModelListener l) {
      
      if (getTableModelListeners().length==0)
        GedcomDirectory.getInstance().addListener(this);
      
      super.addTableModelListener(l);
    }
    
    @Override
    public void removeTableModelListener(TableModelListener l) {
      
      super.removeTableModelListener(l);
      
      if (getTableModelListeners().length==0)
        GedcomDirectory.getInstance().removeListener(this);
    }
    
    public Class<?> getColumnClass(int col) {
      return col==0 ? String.class : Integer.class;
    }

    public int getColumnCount() {
      return COLUMNS.length;
    }

    public int getRowCount() {
      return GedcomDirectory.getInstance().getGedcoms().size();
    }

    public Object getValueAt(int row, int col) {
      Gedcom gedcom = GedcomDirectory.getInstance().getGedcoms().get(row);
      switch (col) {
        case 0: return gedcom.getName() + (gedcom.hasChanged() ? "*" : "" );
        case 8: return gedcom.getLastChange();
        default: return new Integer(gedcom.getEntities(Gedcom.ENTITIES[col-1]).size());
      }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      throw new IllegalArgumentException("n/a");
    }
    
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
    }

    public void gedcomHeaderChanged(Gedcom gedcom) {
    }

    public void gedcomWriteLockAcquired(Gedcom gedcom) {
    }

    public void gedcomBeforeUnitOfWork(Gedcom gedcom) {
    }
    
    public void gedcomAfterUnitOfWork(Gedcom gedcom) {
    }

    public void gedcomWriteLockReleased(Gedcom gedcom) {
      int row = GedcomDirectory.getInstance().getGedcoms().indexOf(gedcom);
      if (row>=0) fireTableRowsUpdated(row,row);
    }

    public void gedcomRegistered(int pos, Gedcom gedcom) {
      gedcom.addGedcomListener(this);
      fireTableRowsInserted(pos, pos);
      getSelectionModel().setSelectionInterval(pos, pos);
    }

    public void gedcomUnregistered(int pos, Gedcom gedcom) {
      gedcom.removeGedcomListener(this);
      fireTableRowsDeleted(pos, pos);
    }
  }

}