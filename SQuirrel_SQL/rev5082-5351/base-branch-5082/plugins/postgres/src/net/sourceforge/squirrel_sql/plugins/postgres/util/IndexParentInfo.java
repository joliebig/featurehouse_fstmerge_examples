package net.sourceforge.squirrel_sql.plugins.postgres.util;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.postgres.IObjectTypes;


public class IndexParentInfo extends DatabaseObjectInfo
{
    
    private final IDatabaseObjectInfo _tableInfo;
    
    public IndexParentInfo(IDatabaseObjectInfo tableInfo,
                           SQLDatabaseMetaData md)
    {
        super(tableInfo.getCatalogName(), 
              tableInfo.getSchemaName(),
              "INDEX",
              IObjectTypes.INDEX_PARENT,
              md);    
        _tableInfo = tableInfo;
    }
    
    public IDatabaseObjectInfo getTableInfo()
    {
        return _tableInfo;
    }
    
}
