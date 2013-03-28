package net.sourceforge.squirrel_sql.fw.sql;


public interface IDatabaseObjectTypes
{
	int GENERIC_LEAF = 0;
	int GENERIC_FOLDER = 1;
	int DATABASE = 2;
	int SCHEMA = 3;
	int CATALOG = 4;
	int TABLE = 5;
	int PROCEDURE = 6;
	int UDT = 7;
	int INDEX = 8;

	
	int SEQUENCE = 8;

	
	int LAST_USED_OBJECT_TYPE = 9999;
}
