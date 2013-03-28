package net.sourceforge.squirrel_sql.plugins.refactoring;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.SqlGenerationPreferences;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class DBUtil
{

	public static String[] getAlterSQLForColumnChange(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		
		String nameSQL = getColumnNameAlterSQL(from, to, dialect, qualifier, prefs);
		if (nameSQL != null)
		{
			result.add(nameSQL);
		}
		String[] nullSQL = getNullAlterSQL(from, to, dialect, qualifier, prefs);
		if (nullSQL != null)
		{
			result.addAll(Arrays.asList(nullSQL));
		}
		String commentSQL = getCommentAlterSQL(from, to, dialect, qualifier, prefs);
		if (commentSQL != null)
		{
			result.add(commentSQL);
		}
		List<String> typeSQL = getTypeAlterSQL(from, to, dialect);
		if (typeSQL != null)
		{
			result.addAll(typeSQL);
		}
		String defaultSQL = getAlterSQLForColumnDefault(from, to, dialect, qualifier, prefs);
		if (defaultSQL != null)
		{
			result.add(defaultSQL);
		}
		return result.toArray(new String[result.size()]);
	}

	public static List<String> getTypeAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect)
	{
		if (from.getDataType() == to.getDataType() && from.getColumnSize() == to.getColumnSize())
		{
			return null;
		}
		return dialect.getColumnTypeAlterSQL(from, to, null, null);
	}

	public static String getColumnNameAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (from.getColumnName().equals(to.getColumnName()))
		{
			return null;
		}
		return dialect.getColumnNameAlterSQL(from, to, qualifier, prefs);
	}

	public static String[] getNullAlterSQL(TableColumnInfo from, TableColumnInfo to, HibernateDialect dialect,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		if (from.isNullable().equalsIgnoreCase(to.isNullable()))
		{
			return null;
		}
		return dialect.getColumnNullableAlterSQL(to, qualifier, prefs);
	}

	public static String getCommentAlterSQL(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String oldComment = from.getRemarks();
		String newComment = to.getRemarks();
		if (!dialect.supportsColumnComment())
		{
			return null;
		}
		if (oldComment == null && newComment == null)
		{
			return null;
		}
		if (oldComment == null || !oldComment.equals(newComment))
		{
			return dialect.getColumnCommentAlterSQL(to, qualifier, prefs);
		}
		return null;
	}

	public static String getAlterSQLForColumnDefault(TableColumnInfo from, TableColumnInfo to,
		HibernateDialect dialect, DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String oldDefault = from.getDefaultValue();
		String newDefault = to.getDefaultValue();
		
		
		
		if (oldDefault == null)
		{
			oldDefault = "";
		}
		if (newDefault == null)
		{
			newDefault = "";
		}
		if (!oldDefault.equals(newDefault))
		{
			if (!dialect.supportsAlterColumnDefault())
			{
				throw new UnsupportedOperationException(dialect.getDisplayName()
					+ " doesn't support column default value alterations");
			}
			return dialect.getColumnDefaultAlterSQL(to, qualifier, prefs);
		}
		return null;
	}
}
