
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.gui.db.IDisposableDialog;

public interface IMergeTableDialog extends IDisposableDialog
{

	String getReferencedTable();

	Vector<String[]> getWhereDataColumns();

	Vector<String> getMergeColumns();

	boolean isMergeData();
	
	void setVisible(boolean val);

	void addShowSQLListener(ActionListener listener);
	
	void addEditSQLListener(ActionListener listener);
	
	void addExecuteListener(ActionListener listener);
	
	void setLocationRelativeTo(Component c);
}