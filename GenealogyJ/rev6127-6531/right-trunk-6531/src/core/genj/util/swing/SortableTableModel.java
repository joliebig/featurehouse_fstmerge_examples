
package genj.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SortableTableModel extends AbstractTableModel {
  
    protected TableModel tableModel;

    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;

    private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

    private Row[] viewToModel;
    private int[] modelToView;

    private JTableHeader tableHeader;
    private MouseListener mouseListener;
    private TableModelListener tableModelListener;
    private List<Directive> sortingColumns = new ArrayList<Directive>();

    public SortableTableModel() {
        this.mouseListener = new MouseHandler();
        this.tableModelListener = new TableModelHandler();
    }

    public SortableTableModel(TableModel tableModel) {
        this();
        setTableModel(tableModel);
    }

    public SortableTableModel(TableModel tableModel, JTableHeader tableHeader) {
        this();
        setTableHeader(tableHeader);
        setTableModel(tableModel);
    }

    private void clearSortingState() {
        viewToModel = null;
        modelToView = null;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
        }

        this.tableModel = tableModel;
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
        }

        clearSortingState();
        fireTableStructureChanged();
    }

    public JTableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(JTableHeader tableHeader) {
        if (this.tableHeader != null) {
            this.tableHeader.removeMouseListener(mouseListener);
            TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
            if (defaultRenderer instanceof SortableHeaderRenderer) {
                this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
            }
        }
        this.tableHeader = tableHeader;
        if (this.tableHeader != null) {
            this.tableHeader.addMouseListener(mouseListener);
            this.tableHeader.setDefaultRenderer(
                    new SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
        }
    }

    
    public List<Directive> getDirectives() {
      return Collections.unmodifiableList(sortingColumns);
    }
    
    public boolean isSorting() {
        return sortingColumns.size() != 0;
    }

    private Directive getDirective(int column) {
        for (int i = 0; i < sortingColumns.size(); i++) {
            Directive directive = (Directive)sortingColumns.get(i);
            if (directive.column == column) {
                return directive;
            }
        }
        return EMPTY_DIRECTIVE;
    }

    public int getSortingStatus(int column) {
        return getDirective(column).direction;
    }

    private void sortingStatusChanged() {
        clearSortingState();
        fireTableDataChanged();
        if (tableHeader != null) {
            tableHeader.repaint();
        }
    }

    public void setSortingStatus(int column, int status) {
        Directive directive = getDirective(column);
        if (directive != EMPTY_DIRECTIVE) {
            sortingColumns.remove(directive);
        }
        if (status != NOT_SORTED) {
            sortingColumns.add(new Directive(column, status));
        }
        sortingStatusChanged();
    }

    protected Icon getHeaderRendererIcon(int column, int size) {
        Directive directive = getDirective(column);
        if (directive == EMPTY_DIRECTIVE) {
            return null;
        }
        return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
    }

    public void cancelSorting() {
        sortingColumns.clear();
        sortingStatusChanged();
    }

    private Row[] getViewToModel() {
        if (viewToModel == null) {
            int tableModelRowCount = tableModel.getRowCount();
            viewToModel = new Row[tableModelRowCount];
            for (int row = 0; row < tableModelRowCount; row++) {
                viewToModel[row] = new Row(row);
            }

            if (isSorting()) {
                Arrays.sort(viewToModel);
            }
        }
        return viewToModel;
    }

    public int modelIndex(int viewIndex) {
        return getViewToModel()[viewIndex].modelIndex;
    }
    
    public int viewIndex(int modelIndex) {
      return getModelToView()[modelIndex];
    }

    private int[] getModelToView() {
        if (modelToView == null) {
            int n = getViewToModel().length;
            modelToView = new int[n];
            for (int i = 0; i < n; i++) {
                modelToView[modelIndex(i)] = i;
            }
        }
        return modelToView;
    }

    

    public int getRowCount() {
        return (tableModel == null) ? 0 : tableModel.getRowCount();
    }

    public int getColumnCount() {
        return (tableModel == null) ? 0 : tableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return tableModel.getColumnName(column);
    }

    public Class<?> getColumnClass(int column) {
        return tableModel.getColumnClass(column);
    }

    public boolean isCellEditable(int row, int column) {
        return tableModel.isCellEditable(modelIndex(row), column);
    }

    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(modelIndex(row), column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        tableModel.setValueAt(aValue, modelIndex(row), column);
    }

    
    
    private class Row implements Comparable<Row> {
        private int modelIndex;

        public Row(int index) {
            this.modelIndex = index;
        }

        public int compareTo(Row other) {
            int row1 = modelIndex;
            int row2 = other.modelIndex;
            
            for (Directive directive : sortingColumns) {
                int column = directive.column;
                Object o1 = tableModel.getValueAt(row1, column);
                Object o2 = tableModel.getValueAt(row2, column);

                int comparison = 0;
                
                if (o1 == null && o2 == null) {
                    comparison = 0;
                } else if (o1 == null) {
                    comparison = -1;
                } else if (o2 == null) {
                    comparison = 1;
                } else {
                  if (tableModel instanceof RowComparator) 
                    comparison = ((RowComparator)tableModel).compare(o1, o2, column);
                  else
                    comparison = compare(o1,o2);
                }
                if (comparison != 0) 
                  return directive.direction == DESCENDING ? -comparison : comparison;
            }
            return 0;
        }
    }

    @SuppressWarnings(value = "unchecked")
    private int compare(Object o1, Object o2) {
      if (o1 instanceof Comparable) try {
        return ((Comparable)o1).compareTo(o2);
      } catch (Throwable t) {
      }
      return 0;
    }

    private class TableModelHandler implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            
            if (!isSorting()) {
                clearSortingState();
                fireTableChanged(e);
                return;
            }
                
            
            
            
            if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                cancelSorting();
                fireTableChanged(e);
                return;
            }

            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow()
                    && column != TableModelEvent.ALL_COLUMNS
                    && getSortingStatus(column) == NOT_SORTED
                    && viewToModel != null) {
                int viewIndex = getModelToView()[e.getFirstRow()];
                fireTableChanged(new TableModelEvent(SortableTableModel.this, viewIndex, viewIndex, column, e.getType()));
                return;
            }

            
            clearSortingState();
            fireTableDataChanged();
            return;
        }
    }

    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            if (viewColumn<0)
              return;
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column<0) 
              return;
              int status = getSortingStatus(column);
              if (!e.isControlDown()) 
                sortingColumns.clear();
              
              if (e.isShiftDown()) 
                
                status = (status +1 + 4) % 3 - 1; 
              else 
                
                status = status==0 ? 1 : status*-1;
              
              setSortingStatus(column, status);
        }
    }

    private static class Arrow implements Icon {
        private boolean descending;
        private int size;
        private int priority;

        public Arrow(boolean descending, int size, int priority) {
            this.descending = descending;
            this.size = size;
            this.priority = priority;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color color = c == null ? Color.GRAY : c.getBackground();             
            
            
            int dx = (int)(Math.pow(0.8, priority)*size/2);
            int dy = descending ? dx : -dx;
            
            y = y + 5*size/6 + (descending ? -dy : 0);
            int shift = descending ? 1 : -1;
            g.translate(x, y);

            
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);
            
            
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);
            
            
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }
            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }

    private class SortableHeaderRenderer implements TableCellRenderer {
        private TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value,
                                                       boolean isSelected, 
                                                       boolean hasFocus,
                                                       int row, 
                                                       int column) {
            Component c = tableCellRenderer.getTableCellRendererComponent(table, 
                    value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                
                if (value instanceof ImageIcon) {
                  l.setIcon((ImageIcon)value);
                  l.setText(null);
                  l.setHorizontalAlignment(JLabel.CENTER);
                } else {
                  l.setIcon(null);
                  l.setText(value.toString());
                  l.setHorizontalAlignment(JLabel.LEFT);
                }

                int modelColumn = table.convertColumnIndexToModel(column);
                Icon indicator = getHeaderRendererIcon(modelColumn, l.getFont().getSize());
                if (indicator!=null) {
                  l.setHorizontalTextPosition(JLabel.LEFT);
                  l.setIcon(indicator);
                }
            }
            return c;
        }
    }

    public static class Directive {
        private int column;
        private int direction;

        private Directive(int column, int direction) {
            this.column = column;
            this.direction = direction;
        }
        
        public int getColumn() {
          return column;
        }
        
        public int getDirection() {
          return direction;
        }
    }
    
    public static interface RowComparator {
      public int compare(Object valueA, Object valueB, int col);
    }
}
