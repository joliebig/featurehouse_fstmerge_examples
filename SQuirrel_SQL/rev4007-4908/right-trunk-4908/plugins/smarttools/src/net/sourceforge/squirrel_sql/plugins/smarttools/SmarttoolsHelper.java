
package net.sourceforge.squirrel_sql.plugins.smarttools;

import java.awt.print.PrinterException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SmarttoolsHelper
{
	
	private final static ILogger log = LoggerController.createLogger(SmarttoolsHelper.class);

	
	public final static String CR = System.getProperty("line.separator", "\n");

	private SmarttoolsHelper()
	{
	}

	
	public static ImageIcon loadIcon(String imageIconName)
	{
		URL imgURL = SmarttoolsPlugin.class.getResource("images/" + imageIconName);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL);
		}
		else
		{
			log.error("Couldn't find file: images/" + imageIconName);
			return null;
		}
	}

	
	public static int convertStringToIntDef(String string, int defaultValue)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}

	
	public static void printTable(JTable table, String headerText, String footerText)
	{
		try
		{
			MessageFormat headerFormat = new MessageFormat(headerText);
			MessageFormat footerFormat = new MessageFormat(footerText);
			table.print(PrintMode.FIT_WIDTH, headerFormat, footerFormat);
		}
		catch (PrinterException e)
		{
			log.error(e.getLocalizedMessage());
		}
	}

	
	public static List<STDataType> getListSmarttoolsDataType(boolean withGroupNull)
	{
		ArrayList<STDataType> listSmarttoolsDataType = new ArrayList<STDataType>();

		listSmarttoolsDataType.add(new STDataType(-1, "Group integer", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(-1, "Group char", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(-1, "Group numeric", STDataType.GROUP_NUMERIC));

		if (withGroupNull)
		{
			listSmarttoolsDataType.add(new STDataType(Types.NULL, "NULL", STDataType.GROUP_NULL));
		}
		listSmarttoolsDataType.add(new STDataType(Types.BIGINT, "BIGINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.CHAR, "CHAR", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(Types.DATE, "DATE", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.DECIMAL, "DECIMAL", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.DOUBLE, "DOUBLE", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.FLOAT, "FLOAT", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.INTEGER, "INTEGER", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.LONGVARCHAR, "LONGVARCHAR", STDataType.GROUP_CHAR));
		listSmarttoolsDataType.add(new STDataType(Types.NUMERIC, "NUMERIC", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.REAL, "REAL", STDataType.GROUP_NUMERIC));
		listSmarttoolsDataType.add(new STDataType(Types.SMALLINT, "SMALLINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.TIME, "TIME", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.TIMESTAMP, "TIMESTAMP", STDataType.GROUP_DATE));
		listSmarttoolsDataType.add(new STDataType(Types.TINYINT, "TINYINT", STDataType.GROUP_INT));
		listSmarttoolsDataType.add(new STDataType(Types.VARCHAR, "VARCHAR", STDataType.GROUP_CHAR));

		return listSmarttoolsDataType;
	}

	
	public static void fillOperatorTypes(JComboBox cbOperator, int usedGroup)
	{
		cbOperator.removeAllItems();

		cbOperator.addItem("is null");
		cbOperator.addItem("is not null");
		cbOperator.addItem("=");
		cbOperator.addItem("<>");
		cbOperator.addItem(">");
		cbOperator.addItem("<");
		if (usedGroup == STDataType.GROUP_CHAR)
		{
			cbOperator.addItem("like");
			cbOperator.addItem("not like");
		}
		cbOperator.setSelectedIndex(0);
	}

	
	public static boolean isDataTypeString(int dataType)
	{
		return dataType == Types.CHAR || dataType == Types.LONGVARCHAR || dataType == Types.VARCHAR;
	}

	
	public static boolean isDataTypeInt(int dataType)
	{
		return dataType == Types.BIGINT || dataType == Types.INTEGER || dataType == Types.SMALLINT
			|| dataType == Types.TINYINT;
	}

	
	public static boolean isDataTypeNumeric(int dataType)
	{
		return dataType == Types.DECIMAL || dataType == Types.DOUBLE || dataType == Types.FLOAT
			|| dataType == Types.NUMERIC || dataType == Types.REAL;
	}

	
	public static boolean isDataTypeDate(int dataType)
	{
		return dataType == Types.DATE || dataType == Types.TIMESTAMP || dataType == Types.TIME;
	}

	
	public static String getDataTypeForDisplay(TableColumnInfo tableColumnInfo)
	{
		StringBuilder buf = new StringBuilder();

		buf.append(tableColumnInfo.getTypeName());
		if (SmarttoolsHelper.isDataTypeString(tableColumnInfo.getDataType()))
		{
			buf.append(" (" + tableColumnInfo.getColumnSize() + ")");
		}
		else if (SmarttoolsHelper.isDataTypeNumeric(tableColumnInfo.getDataType()))
		{
			buf.append(" (" + tableColumnInfo.getColumnSize() + "," + tableColumnInfo.getDecimalDigits() + ")");
		}

		return buf.toString();
	}

	
	public static int checkColumnData(Statement stmt, String sql) throws SQLException
	{
		int resultFound = 0;
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next())
		{
			resultFound = rs.getInt(1);
		}
		rs.close();
		return resultFound;
	}

	
	public static void markAllRows(JTable tbl, int col, boolean mark)
	{
		for (int row = 0; row < tbl.getRowCount(); row++)
		{
			tbl.setValueAt(mark, row, col);
		}
	}

	
	public static void setColumnWidth(JTable table, int[] colWidth)
	{
		TableColumnModel tcm = table.getColumnModel();
		for (int col = 0; col < tcm.getColumnCount(); col++)
		{
			if (col < colWidth.length)
			{
				tcm.getColumn(col).setPreferredWidth(colWidth[col]);
			}
		}
		table.doLayout();
	}

	
	public static int getRowCount(Statement stmt, String tableName) throws SQLException
	{
		int rowCount = 0;
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
		if (rs.next())
		{
			rowCount = rs.getInt(1);
		}
		rs.close();

		return rowCount;
	}
}
