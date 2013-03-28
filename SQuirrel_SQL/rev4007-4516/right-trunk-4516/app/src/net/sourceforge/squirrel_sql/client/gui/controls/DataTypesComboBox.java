package net.sourceforge.squirrel_sql.client.gui.controls;

import java.sql.SQLException;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

public class DataTypesComboBox extends JComboBox
{
	
	public DataTypesComboBox(ISQLConnection conn)
		throws SQLException
	{
		super(getData(conn));

	}

	public DataTypeInfo getDataTypeAt(int idx)
	{
		return (DataTypeInfo)getItemAt(idx);
	}

	private static DataTypeInfo[] getData(ISQLConnection conn)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		return conn.getSQLMetaData().getDataTypes();
	}

	









}
