package net.sourceforge.squirrel_sql.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.Log4jLoggerFactory;

public class SquirrelLoggerFactory extends Log4jLoggerFactory
{
	public SquirrelLoggerFactory() throws IllegalArgumentException
	{
		super(false);
		String configFileName = ApplicationArguments.getInstance().getLoggingConfigFileName();
		if (configFileName != null)
		{
			PropertyConfigurator.configure(configFileName);
		}
		else
		{
			Category.getRoot().removeAllAppenders();
			try
			{




				SquirrelAppender fa = new SquirrelAppender();

				BasicConfigurator.configure(fa);
				final ILogger log = createLogger(getClass());
				log.info("No logger configuration file passed on command line arguments. Using default log file: "	+ fa.getFile() );
			}
			catch (IOException ex)
			{
				final ILogger log = createLogger(getClass());
				log.error("Error occured configuring logging. Now logging to standard output", ex);
				BasicConfigurator.configure();
			}
		}
		doStartupLogging();
	}

	private void doStartupLogging()
	{
		final ILogger log = createLogger(getClass());
		log.info("#############################################################################################################");
		log.info("# Starting " + Version.getVersion()  + " at " + DateFormat.getInstance().format(new Date()));
		log.info("#############################################################################################################");
		log.info(Version.getVersion() + " started: " + Calendar.getInstance().getTime());
		log.info(Version.getCopyrightStatement());
		log.info("java.vendor: " + System.getProperty("java.vendor"));
		log.info("java.version: " + System.getProperty("java.version"));
		log.info("java.runtime.name: " + System.getProperty("java.runtime.name"));
		log.info("os.name: " + System.getProperty("os.name"));
		log.info("os.version: " + System.getProperty("os.version"));
		log.info("os.arch: " + System.getProperty("os.arch"));
		log.info("user.dir: " + System.getProperty("user.dir"));
		log.info("user.home: " + System.getProperty("user.home"));
		log.info("java.home: " + System.getProperty("java.home"));
		log.info("java.class.path: " + System.getProperty("java.class.path"));
	}
}