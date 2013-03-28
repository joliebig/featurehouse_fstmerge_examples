
package net.sourceforge.squirrel_sql.fw.dialects;

import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;


public class MySQL5DialectExt extends MySQLDialectExt
{

	
	@Override
	public String getCreateViewSQL(String viewName, String definition, String checkOption,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{

		StringTemplate st = new StringTemplate(ST_CREATE_VIEW_STYLE_ONE);

		
		
		HashMap<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put(ST_VIEW_NAME_KEY, viewName);
		valuesMap.put(ST_SELECT_STATEMENT_KEY, definition);
		

		return DialectUtils.bindTemplateAttributes(this, st, valuesMap, qualifier, prefs);
	}

	
	@Override
	public DialectType getDialectType()
	{
		return DialectType.MYSQL5;
	}

	
	@Override
	public String getDropViewSQL(String viewName, boolean cascade, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		return DialectUtils.getDropViewSQL(viewName, cascade, qualifier, prefs, this);
	}

	
	@Override
	public String[] getRenameViewSQL(String oldViewName, String newViewName,
		DatabaseObjectQualifier qualifier, SqlGenerationPreferences prefs)
	{
		String renameClause = DialectUtils.RENAME_CLAUSE;
		String commandPrefix = DialectUtils.ALTER_TABLE_CLAUSE;
		return new String[] { DialectUtils.getRenameViewSQL(commandPrefix,
			renameClause,
			oldViewName,
			newViewName,
			qualifier,
			prefs,
			this) };
	}

	
	@Override
	public String getViewDefinitionSQL(String viewName, DatabaseObjectQualifier qualifier,
		SqlGenerationPreferences prefs)
	{
		StringBuilder result = new StringBuilder();
		result.append("SELECT view_definition ");
		result.append("FROM information_schema.views");
		result.append("WHERE table_name = '");
		result.append(viewName);
		result.append("' ");
		result.append("AND table_schema = '");
		result.append(qualifier.getCatalog());
		result.append("'");
		return result.toString();
	}

	
	@Override
	public boolean supportsCreateView()
	{
		return true;
	}

	
	@Override
	public boolean supportsDropView()
	{
		return true;
	}

	
	@Override
	public boolean supportsProduct(String databaseProductName, String databaseProductVersion)
	{
		if (databaseProductName == null || databaseProductVersion == null)
		{
			return false;
		}
		if (!databaseProductName.trim().toLowerCase().startsWith("mysql"))
		{
			return false;
		}
		return databaseProductVersion.startsWith("5");
	}

	
	@Override
	public boolean supportsRenameView()
	{
		return true;
	}

	
	@Override
	public boolean supportsViewDefinition()
	{
		return true;
	}

	
	@Override
	public boolean supportsCheckOptionsForViews()
	{
		return true;
	}
	
	
	@Override
	public String getDisplayName()
	{
		return "MySQL5";
	}
	
}

