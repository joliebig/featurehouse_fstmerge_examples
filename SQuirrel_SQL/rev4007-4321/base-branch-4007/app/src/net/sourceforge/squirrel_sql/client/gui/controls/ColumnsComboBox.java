package net.sourceforge.squirrel_sql.client.gui.controls;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class ColumnsComboBox extends JComboBox
{
	
	public ColumnsComboBox(ISQLConnection conn, ITableInfo ti)
		throws SQLException
	{
		super(getData(conn, ti));
		setRenderer(new CellRenderer());
	}

	public TableColumnInfo getSelectedColumn()
	{
		return (TableColumnInfo)getSelectedItem();
	}

	private static TableColumnInfo[] getData(ISQLConnection conn, ITableInfo ti)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}

		return conn.getSQLMetaData().getColumnInfo(ti);
	}

	
	private static final class CellRenderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus)
		{
			setOpaque(true);
			setText(((TableColumnInfo)value).getColumnName());
			return this;
		}
	}
}
