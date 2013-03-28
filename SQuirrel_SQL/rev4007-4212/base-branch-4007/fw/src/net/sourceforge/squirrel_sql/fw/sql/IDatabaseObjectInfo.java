package net.sourceforge.squirrel_sql.fw.sql;

public interface IDatabaseObjectInfo extends Comparable<IDatabaseObjectInfo>
{
	String getCatalogName();
	String getSchemaName();
	String getSimpleName();
	String getQualifiedName();

	
	DatabaseObjectType getDatabaseObjectType();
}
