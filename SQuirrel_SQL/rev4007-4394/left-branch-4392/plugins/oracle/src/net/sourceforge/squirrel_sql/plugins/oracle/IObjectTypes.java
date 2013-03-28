package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

public interface IObjectTypes
{
	DatabaseObjectType CONSUMER_GROUP_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Consumer Groups");
	DatabaseObjectType FUNCTION_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Functions");
	DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Indexes");
	DatabaseObjectType INSTANCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Instances");
	DatabaseObjectType LOB_PARENT = DatabaseObjectType.createNewDatabaseObjectType("LOBS");
	DatabaseObjectType PACKAGE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Packages");
	DatabaseObjectType SEQUENCE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Sequences");
	DatabaseObjectType SESSION_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Sessions");
	DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Triggers");
	DatabaseObjectType TYPE_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Types");
	DatabaseObjectType USER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Users");

	DatabaseObjectType CONSUMER_GROUP = DatabaseObjectType.createNewDatabaseObjectType("Consumer Group");
	DatabaseObjectType INSTANCE = DatabaseObjectType.createNewDatabaseObjectType("Instance");
	DatabaseObjectType LOB = DatabaseObjectType.createNewDatabaseObjectType("LOB");
	DatabaseObjectType PACKAGE = DatabaseObjectType.createNewDatabaseObjectType("Package");
	DatabaseObjectType SESSION = DatabaseObjectType.createNewDatabaseObjectType("Session");
	DatabaseObjectType TYPE = DatabaseObjectType.createNewDatabaseObjectType("Type");
}
