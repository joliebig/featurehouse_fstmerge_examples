package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

public class DataSetViewerPropertyPanel extends BaseDataSetViewerDestination
{
	
	private PropertyPanel _comp = new PropertyPanel();

	public DataSetViewerPropertyPanel() {
		super();
	}

	protected void addRow(Object[] row) {
		
		
		JLabel left = new JLabel(row[0].toString());
		JLabel right = new JLabel(row[1].toString());
		_comp.add(left, right);
	}

	protected void allRowsAdded() {
		
	}

	public Component getComponent() {
		return _comp;
	}

	public void moveToTop() {
	}

	public void clear() {
		_comp.removeAll();
	}


	
	public int getRowCount() {
		return _comp.getComponentCount();
	}

}