
package net.sourceforge.squirrel_sql.fw.preferences;


public class BaseQueryTokenizerPreferenceBean implements IQueryTokenizerPreferenceBean
{

	
	protected String clientName;

	
	protected String clientVersion;

	protected String statementSeparator = ";";

	protected String procedureSeparator = "/";

	protected String lineComment = "--";

	protected boolean removeMultiLineComments = false;

	protected boolean installCustomQueryTokenizer = true;

	
	@Override
	public String getClientName()
	{
		return clientName;
	}

	
	@Override
	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	
	@Override
	public String getClientVersion()
	{
		return clientVersion;
	}

	
	@Override
	public void setClientVersion(String clientVersion)
	{
		this.clientVersion = clientVersion;
	}

	
	@Override
	public String getStatementSeparator()
	{
		return statementSeparator;
	}

	
	@Override
	public void setStatementSeparator(String statementSeparator)
	{
		this.statementSeparator = statementSeparator;
	}

	
	@Override
	public String getProcedureSeparator()
	{
		return procedureSeparator;
	}

	
	@Override
	public void setProcedureSeparator(String procedureSeparator)
	{
		this.procedureSeparator = procedureSeparator;
	}

	
	@Override
	public String getLineComment()
	{
		return lineComment;
	}

	
	@Override
	public void setLineComment(String lineComment)
	{
		this.lineComment = lineComment;
	}

	
	@Override
	public boolean isRemoveMultiLineComments()
	{
		return removeMultiLineComments;
	}

	
	@Override
	public void setRemoveMultiLineComments(boolean removeMultiLineComments)
	{
		this.removeMultiLineComments = removeMultiLineComments;
	}

	@Override
	public boolean isInstallCustomQueryTokenizer()
	{
		return installCustomQueryTokenizer;
	}

	@Override
	public void setInstallCustomQueryTokenizer(boolean installCustomQueryTokenizer)
	{
		this.installCustomQueryTokenizer = installCustomQueryTokenizer;
		
	}

}
