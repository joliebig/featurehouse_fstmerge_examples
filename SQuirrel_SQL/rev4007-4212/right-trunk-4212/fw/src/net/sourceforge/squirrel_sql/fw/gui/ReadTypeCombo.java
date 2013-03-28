package net.sourceforge.squirrel_sql.fw.gui;

 import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
 
public class ReadTypeCombo extends JComboBox
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ReadTypeCombo.class);

	static final int READ_ALL_IDX = 1;
	static final int READ_PARTIAL_IDX = 0;

	public ReadTypeCombo()
	{
		addItem(s_stringMgr.getString("ReadTypeCombo.onlyFirst"));
		addItem(s_stringMgr.getString("ReadTypeCombo.all"));
	}
}