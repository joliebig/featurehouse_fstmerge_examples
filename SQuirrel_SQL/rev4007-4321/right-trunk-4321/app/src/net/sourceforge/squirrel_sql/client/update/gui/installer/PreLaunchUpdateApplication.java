
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PreLaunchUpdateApplication
{

	private static PreLaunchHelper helper = null;
	
	
	public static void main(String[] args) throws IOException
	{
		ApplicationArguments.initialize(args);
		initializeLogger();
		boolean prompt = getPromptMode();
		setupSpringContext();
		helper.installUpdates(prompt);
	}
	
	
	
	private static void setupSpringContext()
	{
		String[] appCtx = new String[] {
			"classpath:net/sourceforge/squirrel_sql/**/*applicationContext.xml"
		};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(appCtx);
		helper = (PreLaunchHelper)ctx.getBean(PreLaunchHelper.class.getName());
	}
	
	private static boolean getPromptMode()
	{
		boolean prompt = false;
		if (Boolean.getBoolean("prompt")) {
			prompt = true;
		}
		return prompt;
	}
	
	private static void initializeLogger() throws IOException
	{
		ApplicationFiles appFiles = new ApplicationFiles();
		
		String logMessagePattern = "%-4r [%t] %-5p %c %x - %m%n";
		Layout layout = new PatternLayout(logMessagePattern);
		
		File logsDir = new File(appFiles.getUserSettingsDirectory(), "logs");
		File updateLogFile = new File(logsDir, "updater.log");
		
		FileAppender appender = new FileAppender(layout, updateLogFile.getAbsolutePath());
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(appender, false));
	}	
	

}
