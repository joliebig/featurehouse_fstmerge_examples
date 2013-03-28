package org.firebirdsql.squirrel.util;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

public class RelatedObjectInfo extends DatabaseObjectInfo
{
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
