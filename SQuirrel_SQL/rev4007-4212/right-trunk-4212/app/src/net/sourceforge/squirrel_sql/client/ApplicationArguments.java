package net.sourceforge.squirrel_sql.client;



import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ApplicationArguments
{
	
	private interface IOptions
	{
		String[] HELP = { "h", "help", "Display Help and exit"};
		String[] SQUIRREL_HOME = { "home", "squirrel-home",
									"SQuirreL home directory"};
		String[] LOG_FILE = { "l", "log-config-file",
											"Logging configuration file"};
		String[] USE_DEFAULT_METAL_THEME = { "m", "use-default-metal-theme",
											"Use default metal theme"};
		String[] USE_NATIVE_LAF = { "n", "native-laf",
									"Use native look and feel"};
		String[] NO_PLUGINS = {"nop", "no-plugins", "Don't load plugins"};
		String[] NO_SPLASH = { "nos", "no-splash", "Don't display splash screen"};
		String[] USER_SETTINGS_DIR = { "userdir", "user-settings-dir",
								"User settings directory"};
	}

	
	private static ApplicationArguments s_instance;

	
	private final Options _options = new Options();

	
	private CommandLine _cmdLine;

	
	private String[] _rawArgs;

	
	private String _squirrelHome = null;

	
	private String _userSettingsDir = null;

	
	private String _loggingConfigFile = null;

	
	private ApplicationArguments(String[] args)
		throws ParseException
	{
		super();
		createOptions();

        
        _rawArgs = args;        

		final CommandLineParser parser = new GnuParser();
		try
		{
			_cmdLine = parser.parse(_options, args);
		}
		catch(ParseException ex)
		{
			System.err.println("Parsing failed. Reason: " + ex.getMessage());
			printHelp();
			throw ex;
		}

		if (_cmdLine.hasOption(IOptions.SQUIRREL_HOME[0]))
		{
			_squirrelHome = _cmdLine.getOptionValue(IOptions.SQUIRREL_HOME[0]);
		}
		if (_cmdLine.hasOption(IOptions.USER_SETTINGS_DIR[0]))
		{
			_userSettingsDir = _cmdLine.getOptionValue(IOptions.USER_SETTINGS_DIR[0]);
		}
		if (_cmdLine.hasOption(IOptions.LOG_FILE[0]))
		{
			_loggingConfigFile = _cmdLine.getOptionValue(IOptions.LOG_FILE[0]);
		}
	}

	
	public synchronized static boolean initialize(String[] args)
	{
		if (s_instance == null)
		{
			try
			{
				s_instance = new ApplicationArguments(args);
			}
			catch (ParseException ex)
			{
				return false;
			}
		}
		else
		{
			System.out.println("ApplicationArguments.initialize() called twice");
		}
		return true;
	}

	
	public static ApplicationArguments getInstance()
	{
		if (s_instance == null)
		{
			throw new IllegalStateException("ApplicationArguments.getInstance() called before ApplicationArguments.initialize()");
		}
		return s_instance;
	}

	
	public String getSquirrelHomeDirectory()
	{
		return _squirrelHome;
	}

	
	public String getUserSettingsDirectoryOverride()
	{
		return _userSettingsDir;
	}

	
	public boolean getShowSplashScreen()
	{
		return !_cmdLine.hasOption(IOptions.NO_SPLASH[0]);
	}

	
	public boolean getShowHelp()
	{
		return _cmdLine.hasOption(IOptions.HELP[0]);
	}

	
	public String getLoggingConfigFileName()
	{
		return _loggingConfigFile;
	}

	
	public boolean getLoadPlugins()
	{
		return !_cmdLine.hasOption(IOptions.NO_PLUGINS[0]);
	}

	
	public boolean useDefaultMetalTheme()
	{
		return _cmdLine.hasOption(IOptions.USE_DEFAULT_METAL_THEME[0]);
	}

	
	public boolean useNativeLAF()
	{
		return _cmdLine.hasOption(IOptions.USE_NATIVE_LAF[0]);
	}

	
	public String[] getRawArguments()
	{
		return _rawArgs;
	}

	void printHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("squirrel-sql", _options);
	}

	
	private void createOptions()
	{
		Option opt;

		opt = createAnOption(IOptions.NO_SPLASH);
		_options.addOption(opt);

		opt = createAnOption(IOptions.HELP);
		_options.addOption(opt);

		opt = createAnOption(IOptions.NO_PLUGINS);
		_options.addOption(opt);

		opt = createAnOption(IOptions.USE_DEFAULT_METAL_THEME);
		_options.addOption(opt);

		opt = createAnOption(IOptions.USE_NATIVE_LAF);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.SQUIRREL_HOME);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.USER_SETTINGS_DIR);
		_options.addOption(opt);

		opt = createAnOptionWithArgument(IOptions.LOG_FILE);
		_options.addOption(opt);
	}

	private Option createAnOption(String[] argInfo)
	{
		Option opt = new Option(argInfo[0], argInfo[2]);
		if (!isStringEmpty(argInfo[1]))
		{
			opt.setLongOpt(argInfo[1]);
		}

		return opt;
	}

	private Option createAnOptionWithArgument(String[] argInfo)
	{
		OptionBuilder.withArgName(argInfo[0]);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(argInfo[2]);
		Option opt = OptionBuilder.create( argInfo[0]);
		if (!isStringEmpty(argInfo[1]))
		{
			opt.setLongOpt(argInfo[1]);
		}
		return opt;
	}

	private static boolean isStringEmpty(String str)
	{
		return str == null || str.length() == 0;
	}
    
    
    static final void reset() {
        s_instance = null;
    }
}
