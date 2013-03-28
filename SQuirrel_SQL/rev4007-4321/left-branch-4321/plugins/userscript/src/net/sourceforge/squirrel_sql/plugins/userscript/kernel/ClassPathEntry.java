package net.sourceforge.squirrel_sql.plugins.userscript.kernel;


public class ClassPathEntry
{
	private String m_entry;

	public ClassPathEntry(String entry)
	{
		m_entry = entry;
	}

	public ClassPathEntry()
	{
	}

	public String getEntry()
	{
		return m_entry;
	}

	public void setEntry(String entry)
	{
		m_entry = entry;
	}

}
