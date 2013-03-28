
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;


public class ReplacementManager
{

	
	private File replacementFile;

	private ArrayList<Replacement> replacements = new ArrayList<Replacement>();

	IMessageHandler mpan;

	private final static ILogger log = LoggerController.createLogger(SQLReplacePlugin.class);

	
	public ReplacementManager(SQLReplacePlugin _plugin)
	{
		try
		{
			replacementFile = new File(_plugin.getPluginUserSettingsFolder(), "sqlreplacement.xml");
			mpan = _plugin.getApplication().getMessageHandler();
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	
	protected void load() throws IOException
	{
		replacements.clear();
		try
		{
			final XMLBeanReader xmlin = new XMLBeanReader();

			if (replacementFile.exists())
			{
				xmlin.load(replacementFile, getClass().getClassLoader());
				for (final Object bean : xmlin)
				{
					if (bean instanceof Replacement)
					{
						replacements.add((Replacement) bean);
					}
				}
			}
		}
		catch (final XMLException e)
		{
			throw new RuntimeException(e);
		}
	}

	
	protected void save()
	{
		try
		{
			final XMLBeanWriter xmlout = new XMLBeanWriter();

			for (final Replacement rep : replacements)
			{
				xmlout.addToRoot(rep);
			}

			xmlout.save(replacementFile);
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	protected Iterator<Replacement> iterator()
	{
		return replacements.iterator();
	}

	public void removeAll()
	{
		replacements = new ArrayList<Replacement>();
	}

	
	public void setContentFromEditor(String content)
	{
		final String cont = content;
		final String[] lines = cont.split("\n");
		replacements.clear();
		for (final String line : lines)
		{
			if (line != null && line.length() != 0)
			{
				final String[] s = line.split("=");
				if (s[0] != null && s[0].length() > 0 && s[1] != null && s[1].length() > 0)
				{
					final Replacement ro = new Replacement(s[0].trim(), s[1].trim());
					replacements.add(ro);
				}
			}
		}
		this.save();
	}

	
	public String getContent()
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<Replacement> it = replacements.iterator();
		while (it.hasNext())
		{
			final Replacement r = it.next();
			sb.append(r.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	
	public String replace(StringBuffer buffer)
	{
		String toReplace = buffer.toString();
		final Iterator<Replacement> it = replacements.iterator();
		while (it.hasNext())
		{
			final Replacement r = it.next();
			if (toReplace.indexOf(r.getVariable()) > -1)
			{
				String replacementMsg = "Replace-Rule: " + r.toString();
				if (log.isInfoEnabled()) {
					log.info(replacementMsg);
				}
				mpan.showMessage(replacementMsg);
				
				toReplace = toReplace.replace(r.getVariable(), r.getValue());
			}
		}

		return toReplace;
	}
}
