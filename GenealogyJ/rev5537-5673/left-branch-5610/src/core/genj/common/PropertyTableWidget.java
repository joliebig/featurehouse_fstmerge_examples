
package genj.common;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyName;
import genj.gedcom.TagPath;
import genj.renderer.Options;
import genj.renderer.PropertyRenderer;
import genj.util.Dimension2d;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.HeadlessLabel;
import genj.util.swing.LinkWidget;
import genj.util.swing.SortableTableModel;
import genj.util.swing.SortableTableModel.Directive;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class PropertyTableWidget extends JPanel  {
  
  private final static Logger LOG = Logger.getLogger("genj.common");
  
  
  private Table table;
  
  
  private JPanel panelShortcuts;
  
  private boolean ignoreSelection  = false;
  
  
  public PropertyTableWidget() {
    this(null);
  }
  
  
  public PropertyTableWidget(PropertyTableModel propertyModel) {
    
    
    table = new Table();
    setModel(propertyModel);
    
    
    panelShortcuts = new JPanel();
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, new JScrollPane(table));
    add(BorderLayout.EAST, panelShortcuts);
    
    
  }
  
  
  public TableModel getTableModel() {
    return table.getModel();
  }
  
  
  public void setModel(PropertyTableModel set) {
    table.setPropertyTableModel(set);
  }
  
  
  public PropertyTableModel getModel() {
    return table.getPropertyTableModel();
  }
  
  
  public void setAutoResize(boolean on) {
    table.setAutoResizeMode(on ? JTable.AUTO_RESIZE_ALL_COLUMNS : JTable.AUTO_RESIZE_OFF);
  }
  
  
  public int getRow(Property property) {
    return table.getRow(property);
  }
  
  
  public void select(Context context) {
    
    if (ignoreSelection)
      return;
    
    if (context.getGedcom()!=getModel().getGedcom())
      throw new IllegalArgumentException("select on wrong gedcom");
    
    
    try {
      ignoreSelection = true;
      
      
      List<? extends Property> props = context.getProperties();
      
      
      if (props.isEmpty()) {
        List<Property> ps = new ArrayList<Property>(context.getProperties());
        for (Entity ent : context.getEntities())
          if (!ps.contains(ent))
            ps.add(ent);
        props = ps;
      }
        









      
      ListSelectionModel rows = table.getSelectionModel();
      ListSelectionModel cols = table.getColumnModel().getSelectionModel();
      table.clearSelection();
      
      int r=-1,c=-1;
      for (Property prop : props) {
  
        r = getRow(prop.getEntity());
        if (r<0)
          continue;
        c = table.getCol(r, prop);

        
        rows.addSelectionInterval(r,r);
        if (c>=0)
          cols.addSelectionInterval(c,c);
      }
      
      
      if (r>=0) {
        Rectangle visible = table.getVisibleRect();
        Rectangle scrollto = table.getCellRect(r,c,true);
        if (c<0) scrollto.x = visible.x;
        table.scrollRectToVisible(scrollto);
      }

    } finally {
      ignoreSelection = false;
    }
    
  }
  
  
  public String getColumnLayout() {
    
    
    
    
    SortableTableModel model = (SortableTableModel)table.getModel();
    TableColumnModel columns = table.getColumnModel();
    List<Directive> directives = model.getDirectives();

    WordBuffer result = new WordBuffer(",");
    result.append(columns.getColumnCount());
    
    for (int c=0; c<columns.getColumnCount(); c++) 
      result.append(columns.getColumn(c).getWidth());
    
    for (int d=0;d<directives.size();d++) {
      SortableTableModel.Directive dir = (SortableTableModel.Directive)directives.get(d);
      result.append(dir.getColumn());
      result.append(dir.getDirection());
    }
    
    return result.toString();
  }
  
  
  public void setColumnLayout(String layout) {
    
    SortableTableModel model = (SortableTableModel)table.getModel();
    TableColumnModel columns = table.getColumnModel();

    try {
      StringTokenizer tokens = new StringTokenizer(layout, ",");
      int n = Integer.parseInt(tokens.nextToken());
      if (n!=model.getColumnCount())
        return;
  
      for (int i=0;i<n;i++) {
        TableColumn col = columns.getColumn(i);
        int w = Integer.parseInt(tokens.nextToken());
        col.setWidth(w);
        col.setPreferredWidth(w);
      }
      
      model.cancelSorting();
      while (tokens.hasMoreTokens()) {
        int c = Integer.parseInt(tokens.nextToken());
        int d = Integer.parseInt(tokens.nextToken());
        model.setSortingStatus(c, d);
      }

    } catch (Throwable t) {
      
    }
  }
  
  
  public int[] getColumnDirections() {
    SortableTableModel model = (SortableTableModel)table.getModel();
    int[] result = new int[model.getColumnCount()];
    for (int i = 0; i < result.length; i++) {
      result[i] = model.getSortingStatus(i);
    }
    return result;
  }

  
  public void setColumnDirections(int[] set) {
    SortableTableModel model = (SortableTableModel)table.getModel();
    for (int i = 0; i<set.length && i<model.getColumnCount(); i++) {
      model.setSortingStatus(i, set[i]);
    }
  }
  
  
  private class Table extends JTable implements ContextProvider {
    
    private PropertyTableModel propertyModel;
    private SortableTableModel sortableModel = new SortableTableModel();
    
    
    Table() {

      setPropertyTableModel(null);
      setDefaultRenderer(Object.class, new Renderer());
      getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      getColumnModel().setColumnSelectionAllowed(true);
      getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      getTableHeader().setReorderingAllowed(false);
      
      setRowHeight((int)Math.ceil(Options.getInstance().getDefaultFont().getLineMetrics("", new FontRenderContext(null,false,false)).getHeight())+getRowMargin());
      
      getColumnModel().getSelectionModel().addListSelectionListener(this);
      
      
      
      
      setModel(sortableModel);
      sortableModel.setTableHeader(getTableHeader());
      
      
      setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
      setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
      
      
      addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          
          int row = rowAtPoint(e.getPoint());
          int col = columnAtPoint(e.getPoint());
          if (row<0||col<0)
            clearSelection();
          else {
            if (!isCellSelected(row, col)) {
              getSelectionModel().setSelectionInterval(row, row);
              getColumnModel().getSelectionModel().setSelectionInterval(col, col);
            }
          }
        }
      });
      
      
      sortableModel.addTableModelListener(new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
          if (e.getLastRow()==Integer.MAX_VALUE) createShortcuts();
        }
      });
      addComponentListener(new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
          createShortcuts();
        }
      });
      
      
    }
    
    
    Action2 createShortcut(String txt, final int y, final Container container) {
      
      Action2 shortcut = new Action2(txt.toUpperCase()) {
        public void actionPerformed(ActionEvent event) {
          int x = 0;
          try { x = ((JViewport)table.getParent()).getViewPosition().x; } catch (Throwable t) {};
          table.scrollRectToVisible(new Rectangle(x, y, 1, getParent().getHeight()));
        }
      };
      
      LinkWidget link = new LinkWidget(shortcut);
      link.setAlignmentX(0.5F);
      link.setBorder(new EmptyBorder(0,1,0,1));
      container.add(link);
      
      return shortcut;
    }
    
    
    void createShortcuts(int col, JComponent container) {
      
      TableModel model = getModel();
      Collator collator = propertyModel.getGedcom().getCollator();

      
      String cursor = "";
      for (int r=0;r<model.getRowCount();r++) {
        Property prop = (Property)model.getValueAt(r, col);
        if (prop instanceof PropertyDate)
          break;
        if (prop==null)
          continue;
        
        String value = prop instanceof PropertyName ? ((PropertyName)prop).getLastName().trim() : prop.getDisplayValue().trim(); 
        if (value.length()==0)
          continue;
        value = value.substring(0,1).toLowerCase();
        
        if (collator.compare(cursor, value)>=0)
          continue;
        cursor = value;
        
        Action2 shortcut = createShortcut(value, table.getCellRect(r, col, true).y, container);
      
        
        InputMap imap = container.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap amap = container.getActionMap();
        imap.put(KeyStroke.getKeyStroke(value.charAt(0)), shortcut);
        amap.put(shortcut, shortcut);
      }
      
      
    }
    
    
    void createShortcuts() {
      
      
      panelShortcuts.removeAll();
      panelShortcuts.setLayout(new BoxLayout(panelShortcuts, BoxLayout.Y_AXIS));
      panelShortcuts.getInputMap(WHEN_IN_FOCUSED_WINDOW).clear();
      panelShortcuts.getActionMap().clear();
      panelShortcuts.revalidate();
      panelShortcuts.repaint();
      
      
      if (!sortableModel.isSorting())
        return;
      SortableTableModel.Directive directive = (SortableTableModel.Directive)sortableModel.getDirectives().get(0);
      if (directive.getDirection()<=0)
        return;
      
      createShortcuts(directive.getColumn(), panelShortcuts);

      
    }
    
    
    int getCol(int row, Property property) {
      
      
      TableModel model = getModel();
      for (int i=0, j=model.getColumnCount(); i<j; i++) {
        if (model.getValueAt(row,i)==property)
          return i;
      }
      
      
      return -1;
    }
    
    
    int getRow(Property property) {
      if (propertyModel==null)
        return -1;
      SortableTableModel model = (SortableTableModel)getModel();
      for (int i=0;i<model.getRowCount();i++) {
        if (propertyModel.getProperty(model.modelIndex(i))==property)
          return i;
      }
      return -1;
    }

    
    void setPropertyTableModel(PropertyTableModel propertyModel) {
      
      this.propertyModel = propertyModel;
      
      sortableModel.setTableModel(new Model(propertyModel));

    }
    
    
    PropertyTableModel getPropertyTableModel() {
      return propertyModel;
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
      
      
      List<? extends Property> before = getContext().getProperties();
      
      
      super.changeSelection(rowIndex, columnIndex, toggle, extend);
      
      
      if (ignoreSelection)
        return;

      List<Property> properties = new ArrayList<Property>();
      ListSelectionModel rows = getSelectionModel();
      ListSelectionModel cols  = getColumnModel().getSelectionModel();
      
      for (int r=rows.getMinSelectionIndex() ; r<=rows.getMaxSelectionIndex() ; r++) {
        for (int c=cols.getMinSelectionIndex(); c<=cols.getMaxSelectionIndex(); c++) {
          
          if (!rows.isSelectedIndex(r)||!cols.isSelectedIndex(c))
            continue;
          
          SortableTableModel model = (SortableTableModel)getModel();
          if (r<0||r>=model.getRowCount()||c<0||c>=model.getColumnCount())
            continue;
          Property prop = (Property)getValueAt(r,c);
          if (prop==null)
            prop = propertyModel.getProperty(model.modelIndex(r));
          
          if (before.contains(prop)) 
            properties.add(prop);
          else
            properties.add(0, prop);
        }
      }
      
      
      if (!properties.isEmpty()) {
        ignoreSelection = true;
        SelectionSink.Dispatcher.fireSelection(PropertyTableWidget.this, new Context(properties.get(0).getGedcom(), new ArrayList<Entity>(), properties), false);	
        ignoreSelection = false;
      }
      
      
    }
    
     
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }
    
    
    public ViewContext getContext() {
      
      
      Gedcom ged = propertyModel.getGedcom();
      if (ged==null)
        return null;
      SortableTableModel model = (SortableTableModel)getModel();
      
      
      List<Property> properties = new ArrayList<Property>();
      int[] rows = getSelectedRows();
      if (rows.length>0) {
        int[] cols = getSelectedColumns();

        
        for (int r=0;r<rows.length;r++) {
          
          
          boolean rowRepresented = false;
          for (int c=0;c<cols.length;c++) {
            
            Property p = (Property)getValueAt(rows[r], cols[c]);
            if (p!=null) {
              properties.add(p);
              rowRepresented = true;
            }
            
          }
          
          
          if (!rowRepresented)
            properties.add(propertyModel.getProperty(model.modelIndex(rows[r])));
          
          
        }
      }
      
      
      return new ViewContext(ged, new ArrayList<Entity>(), properties);
    }
    
    
    private class Model extends AbstractTableModel implements PropertyTableModelListener {
      
      
      private PropertyTableModel model;
      
      
      private Property cells[][];
      
      
      private Model(PropertyTableModel set) {
        
        model = set;
        
      }
      
      private Gedcom getGedcom() {
        return model!=null ? model.getGedcom() : null;
      }
      
      public void handleRowsAdded(PropertyTableModel model, int rowStart, int rowEnd) {
        
        
        int 
          rows = model.getNumRows(), 
          cols = model.getNumCols();
        cells = new Property[rows][cols];
        
        
        fireTableRowsInserted(rowStart, rowEnd);
      }
      
      public void handleRowsDeleted(PropertyTableModel model, int rowStart, int rowEnd) {
        
        int 
          rows = model.getNumRows(), 
          cols = model.getNumCols();
        cells = new Property[rows][cols];
        
        
        fireTableRowsDeleted(rowStart, rowEnd);
      }
      
      public void handleRowsChanged(PropertyTableModel model, int rowStart, int rowEnd, int col) {
        
        for (int i=rowStart; i<=rowEnd; i++) 
          cells[i][col] = null;
        
        fireTableChanged(new TableModelEvent(this, rowStart, rowEnd, col));
      }
      
      
      public void addTableModelListener(TableModelListener l) {
        super.addTableModelListener(l);
        
        if (model!=null&&getListeners(TableModelListener.class).length==1)
          model.addListener(this);
      }
      
      
      public void removeTableModelListener(TableModelListener l) {
        super.removeTableModelListener(l);
        
        if (model!=null&&getListeners(TableModelListener.class).length==0)
          model.removeListener(this);
      }
      
      
      public String getColumnName(int col) {
        return model!=null ? model.getName(col) : "";
      }
      
      
      public int getColumnCount() {
        return model!=null ? model.getNumCols() : 0;
      }
      
      
      public int getRowCount() {
        return model!=null ? model.getNumRows() : 0;
      }
      
      
      private TagPath getPath(int col) {
        return model!=null ? model.getPath(col) : null;
      }
      
      
      private Context getContextAt(int row, int col) {
        
        if (model==null)
          return null;
        
        Property prop = getPropertyAt(row, col);
        if (prop!=null)
          return new Context(prop);
        
        
        Property root = model.getProperty(row);
        if (root!=null)
          return new Context(root.getEntity());

        
        return new Context(model.getGedcom());
      }
      
      
      private Property getPropertyAt(int row, int col) {
        
        
        if (cells==null) 
          cells = new Property[model.getNumRows()][model.getNumCols()];
        
        Property prop = cells[row][col];
        if (prop==null) {
          prop = model.getProperty(row).getProperty(model.getPath(col));
          cells[row][col] = prop;
        }
        return prop;
      }
      
      
      public Object getValueAt(int row, int col) {
        return getPropertyAt(row, col);
      }
      
      
      private Property getProperty(int row) {
        return model.getProperty(row);
      }
      
    } 
    
    
    private class Renderer extends HeadlessLabel implements TableCellRenderer {
      
      
      private Property curProp;
      
      
      private JTable curTable;
      
      
      private boolean isSelected;
      
      
       Renderer() {
        setFont(Options.getInstance().getDefaultFont());
      }
      
      
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focs, int row, int col) {
        
        curProp = (Property)value;
        curTable = table;
        
        isSelected = selected;
        
        return this;
      }
      
      
      public Dimension getPreferredSize() {
        if (curProp==null)
          return new Dimension(0,0);
        return Dimension2d.getDimension(PropertyRenderer.get(curProp).getSize(getFont(), new FontRenderContext(null, false, false), curProp, new HashMap<String,String>(), Options.getInstance().getDPI()));
      }
      
      
      public void paint(Graphics g) {
        Graphics2D graphics = (Graphics2D)g;
        
        Rectangle bounds = getBounds();
        bounds.x=0; bounds.y=0;
        
        if (isSelected) {
          g.setColor(curTable.getSelectionBackground());
          g.fillRect(0,0,bounds.width,bounds.height);
          g.setColor(curTable.getSelectionForeground());
        } else {
          g.setColor(curTable.getForeground());
        }
        
        if (curProp==null) 
          return;
        
        g.setFont(getFont());
        
        PropertyRenderer proxy = PropertyRenderer.get(curProp);
        
        bounds.x += 1;
        bounds.width -= 2;
        
        proxy.render(graphics, bounds, curProp, new HashMap<String,String>(), Options.getInstance().getDPI());
        
      }
      
    } 
    
  } 

  
  
  



































































  
} 
