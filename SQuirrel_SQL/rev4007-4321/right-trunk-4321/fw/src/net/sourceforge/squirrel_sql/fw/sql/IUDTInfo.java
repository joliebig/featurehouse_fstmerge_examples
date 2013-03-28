package net.sourceforge.squirrel_sql.fw.sql;

public interface IUDTInfo extends IDatabaseObjectInfo
{
	String getJavaClassName();
	String getDataType();
	String getRemarks();
}
