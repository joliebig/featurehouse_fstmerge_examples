package net.sourceforge.squirrel_sql.plugins.syntax;



public interface IConstants
{
	
	static final String USER_PREFS_FILE_NAME = "prefs.xml";

	
	interface ISessionKeys
	{
		
		String PREFS = "prefs";

		
		String SQL_ENTRY_CONTROL = "sqlentry";
	}

	interface IStyleNames
	{
		String COLUMN = "columnName";
		String COMMENT = "comment";
		String DATA_TYPE = "datatype";
		String ERROR = "error";
		String FUNCTION = "function";
		String IDENTIFIER = "identifier";
		String LITERAL = "literal";
		String OPERATOR = "operator";
		String RESERVED_WORD = "reservedWord";
		String SEPARATOR = "separator";
		String TABLE = "tableName";
		String WHITESPACE = "whitespace";
	}
}
