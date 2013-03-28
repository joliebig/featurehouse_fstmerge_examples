package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public class ObjectType
{
	
	public final DatabaseObjectType _dboType;

	
	public final String _objectTypeColumnData;

	
	public final DatabaseObjectType _childDboType;

	
	public ObjectType(DatabaseObjectType dboType, String objectTypeColumnData,
				DatabaseObjectType childDboType)
	{
		super();
		_dboType = dboType;
		_objectTypeColumnData = objectTypeColumnData;
		_childDboType = childDboType;
	}
}
