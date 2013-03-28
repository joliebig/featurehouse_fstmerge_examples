package net.sourceforge.squirrel_sql.client;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.Log4jLoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SquirrelLoggerFactory extends Log4jLoggerFactory
{

	public SquirrelLoggerFactory(FileAppender fa, boolean doStartupLogging)
	{
		super(false);
		initialize(fa, doStartupLogging);
	}

	public SquirrelLoggerFactory(boolean doStartupLogging)
	{
		try
		{ 
			SquirrelAppender fa = new SquirrelAppender();
			initialize(fa, doStartupLogging);
		}
		catch (Exception e)
		{
			final ILogger log = createLogger(getClass());
			log.error("Error occured configuring logging. Now logging to standard output", e);
			BasicConfigurator.configure();
		}

	}

	private void initialize(FileAppender fa, boolean doStartupLogging)
	{
		String configFileName = ApplicationArguments.getInstance().getLoggingConfigFileName();
		if (configFileName != null)
		{
			PropertyConfigurator.configure(configFileName);
		}
		else
		{
			Logger.getRootLogger().removeAllAppenders();
			BasicConfigurator.configure(fa);
			final ILogger log = createLogger(getClass());
			if (log.isInfoEnabled()) {
				log.info("No logger configuration file passed on command line arguments. Using default log file: "
					+ fa.getFile());
			}
		}
		if (doStartupLogging)
		{
			doStartupLogging();
		}
	}

	private void doStartupLogging()
	{
		final ILogger log = createLogger(getClass());
		log.info("#############################################################################################################");
		log.info("# Starting " + Version.getVersion() + " at " + DateFormat.getInstance().format(new Date()));
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