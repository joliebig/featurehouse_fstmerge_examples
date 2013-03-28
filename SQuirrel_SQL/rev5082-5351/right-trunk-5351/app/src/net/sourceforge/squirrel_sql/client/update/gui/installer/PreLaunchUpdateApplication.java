
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PreLaunchUpdateApplication
{

	
	private static PreLaunchHelper helper = null;
	
	public static final String PROMPT_MODE = "prompt";
	
	public static final String RESTORE_MODE = "restore";
	
	
	public static void main(String[] args) throws IOException
	{
		ApplicationArguments.initialize(args);
		initializeLogger();
		boolean prompt = getMode(PROMPT_MODE);
		boolean restore = getMode(RESTORE_MODE);
		setupSpringContext();
		if (!restore) {
			helper.installUpdates(prompt);
		} else {
			helper.restoreFromBackup();
		}
	}
	
	
	
	private static void setupSpringContext()
	{
		String[] appCtx = new String[] {
			"classpath:net/sourceforge/squirrel_sql/**/*applicationContext.xml"
		};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(appCtx);
		helper = (PreLaunchHelper)ctx.getBean(PreLaunchHelper.class.getName());
	}
	
	private static boolean getMode(String mode)
	{
		boolean prompt = false;
		if (Boolean.getBoolean(mode)) {
			prompt = true;
		}
		return prompt;
	}
		
	private static void initializeLogger() throws IOException
	{
		String logConfigFileName = ApplicationArguments.getInstance().getLoggingConfigFileName();
		if (logConfigFileName != null) {
			PropertyConfigurator.configure(logConfigFileName);
		} else {
			ApplicationFiles appFiles = new ApplicationFiles();
			
			String logMessagePattern = "%-4r [%t] %-5p %c %x - %m%n";
			Layout layout = new PatternLayout(logMessagePattern);
			
			File logsDir = new File(appFiles.getUserSettingsDirectory(), "logs");
			File updateLogFile = new File(logsDir, "updater.log");
			
			FileAppender appender = new FileAppender(layout, updateLogFile.getAbsolutePath());
			
			LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(appender, false));
		}
	}	
	

}
