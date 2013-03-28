package net.sourceforge.squirrel_sql.client.session.parser.kernel;




public class ErrorInfo
{
	public String message;
	public int beginPos;
	public int endPos;

	private String key;

	public ErrorInfo(String message, int beginPos, int endPos)
	{
		this.message = message;
		this.beginPos = beginPos;
		this.endPos = endPos;

		key = message + "_" + beginPos + "_" + endPos;
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		ErrorInfo other = (ErrorInfo) obj;
		if (key == null)
		{
			if (other.key != null) { return false; }
		}
		else if (!key.equals(other.key)) { return false; }
		return true;
	}
	
	
}
