package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import net.sourceforge.squirrel_sql.plugins.oracle.IObjectTypes;

public class TriggerParentInfo extends DatabaseObjectInfo
{
	private final IDatabaseObjectInfo _tableInfo;

	public TriggerParentInfo(IDatabaseObjectInfo tableInfo, String schema,
								SQLDatabaseMetaData md)
	{
		super(null, schema, "TRIGGER", IObjectTypes.TRIGGER_PARENT, md);
		_tableInfo = tableInfo;
	}

	public IDatabaseObjectInfo getTableInfo()
	{
		return _tableInfo;
	}
}
