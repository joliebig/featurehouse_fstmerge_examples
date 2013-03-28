package org.jmol;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractCellEditor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.util.Logger;



public class JmolTable {

    static int STRUCTURE_COL = 0;

    public static void main(String[] args) {

    if (args.length == 0)
      args = new String[] {"caffeine.xyz", "C6H6.smol" };
    int nobject = args.length;
    int ncol = 2;
    
    Object[][] data = new Object[nobject][ncol];
    for (int i = 0; i < nobject; i++) {
      data[i][0] = new JmolPanel();
      JmolViewer v = (JmolViewer) ((JmolPanel) data[i][0]).getViewer();
      String strError = v.openFile(args[i]);
      if (strError != null) {
        Logger.error(strError);
      }
      data[i][1] = args[i];
    }
    String[] colNames = { "Structure", "Filename" };

    showMolecules(colNames, data, nobject);
  }

    public static void showMolecules(String[] colNames, Object[][] data, int nmol) {

        
        JFrame frame = new JFrame("Structure Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
        
        JTable mtable = new JTable( new JmolPanelJTableModel(data, colNames) );
        mtable.setShowGrid(true);

        
        
        mtable.getColumnModel().addColumnModelListener( new JmolColumnModelListener(mtable) );
        

        
        mtable.setCellSelectionEnabled(true);

        
        
        mtable.getTableHeader().setReorderingAllowed(false);
            

        
        JScrollPane scrollpane = new JScrollPane(mtable);
        mtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        frame.getContentPane().add(scrollpane);

        
        
        
        TableColumn col = mtable.getColumnModel().getColumn( STRUCTURE_COL);
        col.setCellRenderer( new JmolPanelCellRenderer() );
        col.setCellEditor( new JmolPanelCellEditor() );

        
        frame.pack();
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
       
    static class JmolColumnModelListener implements TableColumnModelListener {
        JTable table;
        public JmolColumnModelListener(JTable t) {
            this.table = t;
        }
        public void columnAdded(TableColumnModelEvent e) {
        }
        public void columnRemoved(TableColumnModelEvent e) {
        }
        public void columnMoved(TableColumnModelEvent e) {
        }
        public void columnMarginChanged(ChangeEvent e) {
            int colwidth = this.table.getColumnModel().getColumn(STRUCTURE_COL).getWidth();
            for (int i = 0; i < this.table.getRowCount(); i++) {
                this.table.setRowHeight(i, colwidth);
            }

        }
        public void columnSelectionChanged(ListSelectionEvent e) {
        }
    }

    static class JmolPanelJTableModel extends AbstractTableModel {
        private Object[][] rows;
        private String[] columns;

        public JmolPanelJTableModel(Object[][] objs, String[] cols) {
            rows = objs;
            columns = cols;
        }

        public String getColumnName(int column) { 
            return columns[column];
        }

        public int getRowCount() {
            return rows.length;
        }

        public int getColumnCount() {
            return columns.length;
        }

        public Object getValueAt(int row, int column) { 
            return rows[row][column];
        }

        public boolean isCellEditable(int row, int column) {
            if (column == STRUCTURE_COL) {
                return true;
            }
            return false;
        }

        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }
    
            
                
    static class JmolPanelCellRenderer extends JmolPanel implements TableCellRenderer{
        public Component getTableCellRendererComponent( 
                JTable table,  Object value, boolean isSelected, 
                boolean hasFocus, int rowIndex, int vColIndex ) {
           return (JmolPanel)value;
                }

        
        public void validate() {}
        public void revalidate() {}
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    }

    static class JmolPanelCellEditor extends AbstractCellEditor implements TableCellEditor{

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
            return (JmolPanel)value;
                }
        public Object getCellEditorValue() {
            return new Object();
        }
        public boolean isCellEditable(int row, int column) {
            if (column == STRUCTURE_COL) {
                return true;
            }
            return false;
        }
        public boolean stopCellEditing() {
            return true;
        }
    }


}


class JmolPanel extends JPanel {
  JmolViewer viewer;
  JmolAdapter adapter;

  JmolPanel() {
    adapter = new SmarterJmolAdapter();
    viewer = JmolViewer.allocateViewer(this, adapter);
    viewer
        .scriptWait("frank off;set defaultDirectory 'http://chemapps.stolaf.edu/jmol/docs/examples-11/data/'");
  }

  public JmolViewer getViewer() {
    return viewer;
  }

  final Dimension currentSize = new Dimension();

  public void paint(Graphics g) {
    viewer.setScreenDimension(getSize(currentSize));
    Rectangle rectClip = new Rectangle();
    g.getClipBounds(rectClip);
    viewer.renderScreenImage(g, currentSize, rectClip);
  }

}


