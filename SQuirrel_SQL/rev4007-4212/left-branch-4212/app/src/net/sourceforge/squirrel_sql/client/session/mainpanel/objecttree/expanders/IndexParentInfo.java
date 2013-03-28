package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;


public class IndexParentInfo extends DatabaseObjectInfo
{
    private static final long serialVersionUID = 1L;

    public interface IPropertyNames {
        String SIMPLE_NAME = "simpleName";
        String TABLE_INFO = "tableInfo";
    }
    
	private final IDatabaseObjectInfo _tableInfo;

	public IndexParentInfo(IDatabaseObjectInfo tableInfo, 
                           String schema,
						   SQLDatabaseMetaData md)
		throws SQLException
	{
		super(tableInfo.getCatalogName(), 
              schema, 
              "INDEX", 
              DatabaseObjectType.INDEX_TYPE_DBO, 
              md);
		_tableInfo = tableInfo;
	}

	public IDatabaseObjectInfo getTableInfo()
	{
		return _tableInfo;
	}
}
