
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public interface IMergeTableDialogFactory
{

	
	public IMergeTableDialog createDialog(String localTable, TableColumnInfo[] localColumns,
		HashMap<String, TableColumnInfo[]> tables);

}
