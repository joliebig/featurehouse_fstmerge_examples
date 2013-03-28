package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;

public interface IProcedureInfo extends IDatabaseObjectInfo, Serializable
{
	String getRemarks();
	int getProcedureType();
	String getProcedureTypeDescription();
}
