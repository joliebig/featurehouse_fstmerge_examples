package net.sourceforge.squirrel_sql.fw.datasetviewer;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class DataSetViewerEditableTablePanel extends DataSetViewerTablePanel
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerEditableTablePanel.class);


	
	TablePopupMenu cellPopupMenu = null;
	
	
	public void init(IDataSetUpdateableModel updateableModel)
	{
		super.init(updateableModel);
		setUpdateableModelReference(updateableModel);
	}
	
	
	
	
	
	
	
	
	
	public  boolean isTableEditable()
	{
		return true;
	}
	
	
	public boolean isColumnEditable(int col, Object originalValue)
	{
		if (_colDefs == null)
			return false;	

		if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == col)
			return false;

		
		if ( ((IDataSetUpdateableTableModel)getUpdateableModel()).getRowidCol() == col)
			return false;

		return CellComponentFactory.isEditableInCell(_colDefs[col], originalValue);
	}
	
	
	public void setCellEditors(JTable table)
	{
		
		
		cellPopupMenu = new TablePopupMenu(getUpdateableModel(), this, table);
		
		for (int i=0; i < _colDefs.length; i++) {
			
			DefaultCellEditor editor =
				CellComponentFactory.getInCellEditor(table, _colDefs[i]);
			
			
			editor.getComponent().addMouseListener(
				new MouseAdapter()
				{
					public void mousePressed(MouseEvent evt)
					{
						if (evt.isPopupTrigger())
						{
							DataSetViewerEditableTablePanel.this.cellPopupMenu.show(
								evt.getComponent(), evt.getX(), evt.getY());
						}
					}
					public void mouseReleased(MouseEvent evt)
					{
						if (evt.isPopupTrigger())
						{
							DataSetViewerEditableTablePanel.this.cellPopupMenu.show(
								evt.getComponent(), evt.getX(), evt.getY());
						}
					}
				});

			
			getColumnForModelIndex(i, table.getColumnModel()).setCellEditor(editor);
		}
	}

	private TableColumn getColumnForModelIndex(int modelIndex, TableColumnModel columnModel)
	{
		for (int i = 0; i < columnModel.getColumnCount(); i++)
		{
			if(columnModel.getColumn(i).getModelIndex() == modelIndex)
			{
				return columnModel.getColumn(i);
			}
		}

		throw new IllegalArgumentException("No column for model index " + modelIndex);
	}

	
	public int[] changeUnderlyingValueAt(
		int row,
		int col,
		Object newValue,
		Object oldValue)
	{
		String message = null;

		
		
		

		
		
		if (getUpdateableModelReference() == null)
			return new int[0];	


		
		
		
		
		
		
		
		
		
		
		
		

		
		if (newValue == oldValue)
			return new int[0];	

		
		
		
		if (oldValue != null && newValue != null) {
			
			if (CellComponentFactory.areEqual( _colDefs[col], oldValue, newValue))
				return new int[0];	

			
			
		}

		
		
		if (getUpdateableModelReference() != null)
			message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getWarningOnCurrentData(getRow(row), _colDefs, col, oldValue);

		if (message != null) {
			
			
			
			
			
			int option = JOptionPane.showConfirmDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.warning"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return new int[0];	
			}
		}

		
		
		if (getUpdateableModelReference() != null)
			message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getWarningOnProjectedUpdate(getRow(row), _colDefs, col, newValue);

		if (message != null) {
			
			
			
			
			
			int option = JOptionPane.showConfirmDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.warning2"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION)
			{
				return new int[0];	
			}
		}

		
		
		
		
		
		

		message = ((IDataSetUpdateableTableModel)getUpdateableModelReference()).
			updateTableComponent(getRow(row), _colDefs, col, oldValue, newValue);

		if (message != null) {
			
			
			JOptionPane.showMessageDialog(null, message, s_stringMgr.getString("baseDataSetViewerDestination.error"),
				JOptionPane.ERROR_MESSAGE);

			
			
			return new int[0];
		}


		
		
		
		
		ArrayList<Integer> buf = new ArrayList<Integer>();
		for (int i = 0; i < _colDefs.length; i++)
		{
			if(_colDefs[i].getFullTableColumnName().equalsIgnoreCase(_colDefs[col].getFullTableColumnName()))
			{
				buf.add(Integer.valueOf(i));
			}
		}

		int[] ret = new int[buf.size()];

		for (int i = 0; i < ret.length; i++)
		{
			ret[i] = buf.get(i);
		}

		return ret;
	}
	
	
	public void deleteRows(int[] rows)
	{
		
		
		if (rows.length == 0) {
			JOptionPane.showMessageDialog(null,
			   
				s_stringMgr.getString("dataSetViewerEditableTablePanel.selectionNeeded"));
			return;
		}


		
		String msg = s_stringMgr.getString("dataSetViewerEditableTablePanel.deleteRosQuestion", rows.length);

		
		int option = JOptionPane.showConfirmDialog(
			null,
			msg,
			
			s_stringMgr.getString("dataSetViewerEditableTablePanel.warning"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);


		if ( option != JOptionPane.YES_OPTION)
		{
			return;	
		}
		
		
		if (currentCellEditor != null) {
			currentCellEditor.cancelCellEditing();
			currentCellEditor = null;
		}
		
		
		
		
		SortableTableModel tableModel = (SortableTableModel)((JTable)getComponent()).getModel();

		Object[][] rowData = new Object[rows.length][_colDefs.length];
		for (int i=0; i<rows.length; i++) {
			for (int j=0; j<_colDefs.length; j++)
				rowData[i][j] = tableModel.getValueAt(rows[i],j);
		}
		
		
		String message = 
			((IDataSetUpdateableTableModel)getUpdateableModel()).deleteRows(rowData, _colDefs);

		if (message != null)
		{
			
			JOptionPane.showMessageDialog(null,
				
				s_stringMgr.getString("dataSetViewerEditableTablePanel.noRowsDeleted", message),
				
				s_stringMgr.getString("dataSetViewerEditableTablePanel.error"),
				JOptionPane.ERROR_MESSAGE);

			return;
		}

		
		
		
		
		
		((SortableTableModel)((MyJTable)getComponent()).getModel()).deleteRows(rows);
        ((MyJTable)getComponent()).clearSelection();
	}

	
	public void insertRow() {
		JTable table = (JTable)getComponent();
		
		
		Point pt = new Point(10, 200);

		Component comp = SwingUtilities.getRoot(table);
		Component newComp = null;
		
		
		String[] dbDefaultValues = 
			((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				getDefaultValues(_colDefs);
		
		
		
		Object[] initialValues = new Object[dbDefaultValues.length];
		for (int i=0; i< initialValues.length; i++) {
			initialValues[i] = CellComponentFactory.getDefaultValue(
				_colDefs[i], dbDefaultValues[i]);
		}

		
		
		
		
		RowDataInputFrame rdif = new RowDataInputFrame(_colDefs, initialValues, this);
		((IMainFrame)comp).addInternalFrame(rdif, false);
		rdif.setLayer(JLayeredPane.POPUP_LAYER);
		rdif.pack();
		newComp = rdif;

		Dimension dim = newComp.getSize();
		boolean dimChanged = false;
		if (dim.width < 300)
		{
			dim.width = 300;
			dimChanged = true;
		}

		if (dimChanged)
		{
			newComp.setSize(dim);
		}

			
		
		
		
		
		
		int fudgeFactor = 100;
		Rectangle parentBounds = comp.getBounds();
		if (parentBounds.width <= (dim.width + fudgeFactor))
		{
			dim.width = parentBounds.width - fudgeFactor;
			pt.x = fudgeFactor / 2;
			newComp.setSize(dim);
		}
		else 
		{
			if ((pt.x + dim.width + fudgeFactor) > (parentBounds.width))
			{
				pt.x -= (pt.x + dim.width + fudgeFactor) - parentBounds.width;
			}
		}

		newComp.setLocation(pt);
		newComp.setVisible(true);
	}
	
	
	protected String insertRow(Object[] values) {

		String message = 
			((IDataSetUpdateableTableModel)getUpdateableModelReference()).
				insertRow(values, _colDefs);
		
		if (message != null) {
			
			JOptionPane.showMessageDialog(null,
				
				message, s_stringMgr.getString("dataSetViewereditableTablePanel.error2"),
				JOptionPane.ERROR_MESSAGE);
				
			return "Error";	
		}

		
		
		
		
		
		
		SortableTableModel sortedModel =
			(SortableTableModel)((JTable)getComponent()).getModel();
			
		sortedModel.insertRow(values);
		
		
		return null;
	}
}
