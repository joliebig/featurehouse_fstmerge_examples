package net.sourceforge.squirrel_sql.plugins.sessionscript;

import java.util.Iterator;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import net.sourceforge.squirrel_sql.client.IApplication;

public class SQLALiasesCombo extends JComboBox
{
    private static final long serialVersionUID = 1L;

    
	public SQLALiasesCombo()
	{
		super();
	}

	public ISQLAlias getSelectedSQLAlias()
	{
		return (ISQLAlias)getSelectedItem();
	}

	
	public void load(IApplication app)
	{
		removeAllItems();
		for (Iterator<ISQLAlias> it = app.getDataCache().aliases(); it.hasNext();)
		{
			ISQLAlias alias = it.next();
			addItem(alias);
		}
	}
}
