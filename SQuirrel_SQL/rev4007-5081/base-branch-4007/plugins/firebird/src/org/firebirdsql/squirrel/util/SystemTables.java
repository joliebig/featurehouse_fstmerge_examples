package org.firebirdsql.squirrel.util;


public class SystemTables
{
	
	public interface IIndexTable
	{
		String TABLE_NAME = "RDB$INDICES";

		String COL_ID = "RDB$INDEX_ID";
		String COL_NAME = "RDB$INDEX_NAME";
		String COL_DESCRIPTION = "RDB$DESCRIPTION";
		String COL_EXPRESSION_SOURCE = "RDB$EXPRESSION_SOURCE";
		String COL_FOREIGN_KEY = "RDB$FOREIGN_KEY";
		String COL_INACTIVE = "RDB$INDEX_INACTIVE";
		String COL_RELATION_NAME = "RDB$RELATION_NAME";
		String COL_SEGMENT_COUNT = "RDB$SEGMENT_COUNT";
		String COL_UNIQUE = "RDB$UNIQUE_FLAG";
		String COL_SYSTEM = "RDB$SYSTEM_FLAG";
	}

	private SystemTables()
	{
		super();
	}
}
