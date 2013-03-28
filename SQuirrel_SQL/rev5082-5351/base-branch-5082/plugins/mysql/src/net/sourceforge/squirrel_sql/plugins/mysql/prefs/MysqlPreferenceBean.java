package net.sourceforge.squirrel_sql.plugins.mysql.prefs;


import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;


public class MysqlPreferenceBean extends BaseQueryTokenizerPreferenceBean implements Cloneable, Serializable
{

	static final long serialVersionUID = 5818886723165356478L;

	static final String UNSUPPORTED = "Unsupported";

	public MysqlPreferenceBean()
	{
		super();
		statementSeparator = ";";
		procedureSeparator = "|";
		lineComment = "--";
		removeMultiLineComments = false;
		installCustomQueryTokenizer = true;
	}

}
