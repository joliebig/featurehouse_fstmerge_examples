package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

public interface IPluginDatabaseObject extends IDatabaseObjectInfo {
	IPluginDatabaseObjectType getType();

	
}

