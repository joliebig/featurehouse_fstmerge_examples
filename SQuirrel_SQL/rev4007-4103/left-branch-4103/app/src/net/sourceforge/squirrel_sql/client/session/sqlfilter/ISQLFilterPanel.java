package net.sourceforge.squirrel_sql.client.session.sqlfilter;

import net.sourceforge.squirrel_sql.client.util.IOptionPanel;

public interface ISQLFilterPanel extends IOptionPanel
{
	
	void initialize(SQLFilterClauses sqlFilterClauses);
}