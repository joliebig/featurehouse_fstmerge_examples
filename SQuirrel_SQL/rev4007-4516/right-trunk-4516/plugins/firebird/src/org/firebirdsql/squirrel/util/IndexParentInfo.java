package org.firebirdsql.squirrel.util;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.firebirdsql.squirrel.IObjectTypes;

public class IndexParentInfo extends RelatedObjectInfo
{
    public IndexParentInfo(IDatabaseObjectInfo relatedObjInfo,
                                SQLDatabaseMetaData md)
    {
        super(relatedObjInfo, "INDEX", IObjectTypes.INDEX_PARENT, md);
    }
}
