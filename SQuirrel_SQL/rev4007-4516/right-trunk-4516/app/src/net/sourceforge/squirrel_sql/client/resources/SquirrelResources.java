package net.sourceforge.squirrel_sql.client.resources;

import java.net.URL;

import net.sourceforge.squirrel_sql.fw.util.Resources;

public class SquirrelResources extends Resources
{
	
	public final static int S_SPLASH_IMAGE_BACKGROUND = 0xAEB0C5;

	private final String _defaultsPath;

	public interface IMenuResourceKeys
	{
		String ALIASES = "aliases";
		String CLOSE_ALL_SQL_RESULTS = "close_all_sql_results";
		String DRIVERS = "drivers";

		String OSX_FILE = "osxFile";
		String HELP = "help";
		String PLUGINS = "plugins";
		String PLUGIN_CHANGE_LOG = "pluginChangeLog";
		String PLUGIN_HELP = "pluginHelp";
		String PLUGIN_LICENCE = "pluginLicence";
		String SESSION = "session";
		String WINDOWS = "windows";
      String FILE = "file";
      String TRANSACTION = "transaction";
   }

	public interface IImageNames
	{
		String APPLICATION_ICON = "AppIcon";
		String COPY_SELECTED = "CopySelected";
      String SQL_HISTORY = "SQLHistory";
      String EMPTY16 = "Empty16";
		String HELP_TOPIC = "HelpTopic";
		String HELP_TOC_CLOSED = "HelpTocClosed";
		String HELP_TOC_OPEN = "HelpTocOpen";
		String PERFORMANCE_WARNING = "PerformanceWarning";
		String PLUGINS = "Plugins";
		String SPLASH_SCREEN = "SplashScreen";
		String VIEW = "View";

		String TRASH = "trash";

		String GREEN_GEM = "green_gem";
		String YELLOW_GEM = "yellow_gem";
		String RED_GEM = "red_gem";
		String WHITE_GEM = "white_gem";
		String LOGS = "logs";
		String ALIAS_PROPERTIES = "aliasProperties";

      String FIND = "find";

   }

	public SquirrelResources(String rsrcBundleBaseName)
		throws IllegalArgumentException
	{
		super(rsrcBundleBaseName, SquirrelResources.class.getClassLoader());
		_defaultsPath = getBundle().getString("path.defaults");
	}

	public URL getDefaultDriversUrl()
	{
		return getClass().getResource(_defaultsPath + "default_drivers.xml");
	}

	public URL getCreditsURL()
	{
		return getClass().getResource(getBundle().getString("Credits.file"));
	}
}
