package net.sourceforge.squirrel_sql.client;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class SquirrelFileSizeRollingAppender extends RollingFileAppender
{
	
	public SquirrelFileSizeRollingAppender() throws IOException
	{
		super(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"),
				getLogFile().getAbsolutePath(), true);
		super.setMaxFileSize("1MB");
		super.setMaxBackupIndex(10);
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
