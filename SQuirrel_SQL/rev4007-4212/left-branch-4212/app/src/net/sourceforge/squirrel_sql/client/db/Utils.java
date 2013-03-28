package net.sourceforge.squirrel_sql.client.db;

import java.sql.DatabaseMetaData;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class Utils
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Utils.class);

	private Utils()
	{
		super();
	}

	public static String getNullableDescription(int type)
	{
		switch (type)
		{
			case DatabaseMetaData.typeNoNulls :
				return s_stringMgr.getString("Utils.no");
			case DatabaseMetaData.typeNullable :
				return s_stringMgr.getString("Utils.yes");
			default :
				return s_stringMgr.getString("Utils.unknown");
		}
	}

	public static String getSearchableDescription(int type)
	{
		switch (type)
		{
			case DatabaseMetaData.typePredNone :
				return s_stringMgr.getString("Utils.no");
			case DatabaseMetaData.typePredChar :
				return s_stringMgr.getString("Utils.onlywherelike");
			case DatabaseMetaData.typePredBasic :
				return s_stringMgr.getString("Utils.notwherelike");
			case DatabaseMetaData.typeSearchable :
				return s_stringMgr.getString("Utils.yes");
			default :
				return s_stringMgr.getString("Utils.unknown", Integer.valueOf(type));
		}
	}
}
