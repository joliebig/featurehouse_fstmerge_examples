package net.sourceforge.squirrel_sql.fw.dialects;




public class SqlGenerationPreferences
{
	private boolean qualifyTableNames = true;

	private boolean quoteColumnNames = true;

	private boolean quoteConstraintNames = true;
	
	
	public boolean isQuoteConstraintNames()
	{
		return quoteConstraintNames;
	}

	
	public void setQuoteConstraintNames(boolean quoteConstraintNames)
	{
		this.quoteConstraintNames = quoteConstraintNames;
	}

	
	public boolean isQuoteColumnNames()
	{
		return quoteColumnNames;
	}

	
	public void setQuoteColumnNames(boolean quoteColumnNames)
	{
		this.quoteColumnNames = quoteColumnNames;
	}

	private boolean quoteIdentifiers = true;

	private String sqlStatementSeparator = ";";

	
	public void setQualifyTableNames(boolean qualifyTableNames)
	{
		this.qualifyTableNames = qualifyTableNames;
	}

	
	public boolean isQualifyTableNames()
	{
		return qualifyTableNames;
	}

	
	public void setQuoteIdentifiers(boolean quoteIdentifiers)
	{
		this.quoteIdentifiers = quoteIdentifiers;
	}

	
	public boolean isQuoteIdentifiers()
	{
		return quoteIdentifiers;
	}

	
	public void setSqlStatementSeparator(String sqlStatementSeparator)
	{
		this.sqlStatementSeparator = sqlStatementSeparator;
	}

	
	public String getSqlStatementSeparator()
	{
		return sqlStatementSeparator;
	}
}
