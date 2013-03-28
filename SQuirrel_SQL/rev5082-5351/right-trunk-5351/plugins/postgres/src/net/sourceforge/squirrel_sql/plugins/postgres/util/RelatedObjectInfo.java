package net.sourceforge.squirrel_sql.plugins.postgres.util;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class RelatedObjectInfo extends DatabaseObjectInfo
{
   private static final long serialVersionUID = -6019860536077865903L;

   private final IDatabaseObjectInfo _relatedObjInfo;

    public RelatedObjectInfo(IDatabaseObjectInfo relatedObjInfo,
                                String simpleName,
                                DatabaseObjectType dboType,
                                SQLDatabaseMetaData md)
    {
        super(null, null, simpleName, dboType, md);
        _relatedObjInfo = relatedObjInfo;
    }

    public IDatabaseObjectInfo getRelatedObjectInfo()
    {
        return _relatedObjInfo;
    }
}
