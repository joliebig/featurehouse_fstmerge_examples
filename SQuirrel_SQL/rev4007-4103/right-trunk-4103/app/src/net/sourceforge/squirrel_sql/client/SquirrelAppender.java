package net.sourceforge.squirrel_sql.client;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.PatternLayout;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

public class SquirrelAppender extends DailyRollingFileAppender
{
	
	public SquirrelAppender() throws IOException
	{
		super(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"),
				getLogFile().getAbsolutePath(), "'.'yyyy-ww");
	}

	
	private static File getLogFile()
	{
		final File logFile = new ApplicationFiles().getExecutionLogFile();
		if (logFile == null)
		{
			throw new IllegalStateException("null ExecutionLogFile in ApplicationFiles");
		}
		return logFile;
	}
}
