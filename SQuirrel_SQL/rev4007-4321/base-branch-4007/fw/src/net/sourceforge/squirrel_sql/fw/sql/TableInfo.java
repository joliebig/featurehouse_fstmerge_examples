package net.sourceforge.squirrel_sql.fw.sql;

import java.util.SortedSet;
import java.util.TreeSet;

public class TableInfo extends DatabaseObjectInfo implements ITableInfo
{
    static final long serialVersionUID = -3184857504910012169L;

    
	private final String _tableType;

	
	private final String _remarks;

	private SortedSet<ITableInfo> _childList; 
	private ITableInfo[] _childs; 

    ForeignKeyInfo[] exportedKeys = null;
    ForeignKeyInfo[] importedKeys = null;
    
	public TableInfo(String catalog, String schema, String simpleName,
					 String tableType, String remarks,
					 ISQLDatabaseMetaData md)
	{
		super(catalog, schema, simpleName, getTableType(tableType), md);
		_remarks = remarks;
		_tableType = tableType;
	}

   private static DatabaseObjectType getTableType(String tableType)
   {
      if(null == tableType)
      {
         return DatabaseObjectType.TABLE;
      }
      else if(false == tableType.equalsIgnoreCase("TABLE") && false == tableType.equalsIgnoreCase("VIEW"))
      {
         return DatabaseObjectType.TABLE;
      }
      else
      {
         return tableType.equalsIgnoreCase("VIEW") ? DatabaseObjectType.VIEW : DatabaseObjectType.TABLE;
      }
   }

   
   public String getType()
   {
      return _tableType;
   }

	public String getRemarks()
	{
		return _remarks;
	}

	public boolean equals(Object obj)
	{
		if (super.equals(obj) && obj instanceof TableInfo)
		{
			TableInfo info = (TableInfo) obj;
			if ((info._tableType == null && _tableType == null)
				|| ((info._tableType != null && _tableType != null)
					&& info._tableType.equals(_tableType)))
			{
				return (
					(info._remarks == null && _remarks == null)
						|| ((info._remarks != null && _remarks != null)
							&& info._remarks.equals(_remarks)));
			}
		}
		return false;
	}

	void addChild(ITableInfo tab)
	{
		if (_childList == null)
		{
			_childList = new TreeSet<ITableInfo>();
		}
		_childList.add(tab);
	}

	public ITableInfo[] getChildTables()
	{
		if (_childs == null && _childList != null)
		{
			_childs = _childList.toArray(new ITableInfo[_childList.size()]);
			_childList = null;
		}
		return _childs;
	}


    
    public ForeignKeyInfo[] getExportedKeys() {
        return exportedKeys;
    }

    public void setExportedKeys(ForeignKeyInfo[] foreignKeys) {
        exportedKeys = foreignKeys;
    }
    
    
    public ForeignKeyInfo[] getImportedKeys() {
        return importedKeys;
    }

    public void setImportedKeys(ForeignKeyInfo[] foreignKeys) {
        importedKeys = foreignKeys;
    }
    


}
