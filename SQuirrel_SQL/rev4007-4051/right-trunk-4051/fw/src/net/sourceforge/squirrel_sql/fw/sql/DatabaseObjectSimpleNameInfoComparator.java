package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.util.Comparator;

public class DatabaseObjectSimpleNameInfoComparator 
implements Comparator<IDatabaseObjectInfo>, Serializable
{
    private static final long serialVersionUID = 1L;

    public int compare(IDatabaseObjectInfo o1, IDatabaseObjectInfo o2)
	{
		return o1.getSimpleName().compareToIgnoreCase(o2.getSimpleName());
	}
}
