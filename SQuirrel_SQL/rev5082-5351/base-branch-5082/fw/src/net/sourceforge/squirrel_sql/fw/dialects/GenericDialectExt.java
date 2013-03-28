
package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;

public class GenericDialectExt extends CommonHibernateDialect
{

	private class GenericDialectHelper extends Dialect
	{
		public GenericDialectHelper()
		{
			registerColumnType(Types.BIGINT, "integer");
			registerColumnType(Types.CHAR, "char($l)");
			registerColumnType(Types.DATE, "date");
			registerColumnType(Types.INTEGER, "integer");
			registerColumnType(Types.LONGVARCHAR, "varchar($l)");
			registerColumnType(Types.SMALLINT, "integer");
			registerColumnType(Types.TIME, "time");
			registerColumnType(Types.TIMESTAMP, "timestamp");
			registerColumnType(Types.TINYINT, "integer");
			registerColumnType(Types.VARCHAR, "varchar($l)");
		}
	}

	
	private GenericDialectHelper _dialect = new GenericDialectHelper();

	
	@Override
	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException
	{
		return _dialect.getTypeName(code, length, precision, scale);
	}
	
	
	@Override
	public String getDisplayName()
	{
		return "Generic";
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.GENERIC;
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		return true;
	}
	
	
	
}
