package net.sourceforge.squirrel_sql.plugins.postgres;


import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public interface IObjectTypes {

    DatabaseObjectType TRIGGER_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Trigger");
    DatabaseObjectType VIEW_PARENT = DatabaseObjectType.createNewDatabaseObjectType("View");
    DatabaseObjectType INDEX_PARENT = DatabaseObjectType.createNewDatabaseObjectType("Indices");    
}
