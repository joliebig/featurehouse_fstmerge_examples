package genj.reportrunner;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jvyaml.YAML;


public class ReportRunner
{

    private static final String REPORTS_HEADING = "reports";
    private static final String LANGUAGE_ARGUMENT = "language";
    private static final String CONFIG_ARGUMENT = "config";
    private static final String HELP_ARGUMENT = "help";
    private static final String HELP_REPORTS_ARGUMENT = "help-reports";

    public static final Logger LOG = Logger.getLogger("genj.reportrunner");

    
    public static void main(String[] args) throws Exception
    {
        
        Handler[] handlers = Logger.getLogger("").getHandlers();
        if (handlers.length > 0)
            handlers[0].setFormatter(new LogFormatter());

        Map<String, String> arguments = getOptions(args);

        String language = arguments.get(LANGUAGE_ARGUMENT);
        if (language != null)
        {
        	LOG.info("Setting language to: " + language);
        	
            Locale.setDefault(new Locale(language));
        }

        ReportLauncher launcher = new ReportLauncher();

        
        if (arguments.containsKey(HELP_ARGUMENT))
        {
            if (arguments.get(HELP_ARGUMENT) == null)
                printHelp();
            else
                launcher.printOptions(arguments.get(HELP_ARGUMENT));
            return;
        }

        if (arguments.containsKey(HELP_REPORTS_ARGUMENT))
        {
            launcher.printReports();
            return;
        }

        Map<String, String> defaults = new HashMap<String, String>();

        
        if (arguments.containsKey(ReportLauncher.GEDCOM_OPTION))
        	defaults.put(ReportLauncher.GEDCOM_OPTION, arguments.get(ReportLauncher.GEDCOM_OPTION));
        if (arguments.containsKey(ReportLauncher.INDIVIDUAL_OPTION))
        	defaults.put(ReportLauncher.INDIVIDUAL_OPTION, arguments.get(ReportLauncher.INDIVIDUAL_OPTION));
        if (arguments.containsKey(ReportLauncher.OUTPUT_OPTION))
            defaults.put(ReportLauncher.OUTPUT_OPTION, arguments.get(ReportLauncher.OUTPUT_OPTION));
        if (arguments.containsKey(ReportLauncher.OUTPUT_DIR_OPTION))
            defaults.put(ReportLauncher.OUTPUT_DIR_OPTION, arguments.get(ReportLauncher.OUTPUT_DIR_OPTION));

        
        List<Map<String, String>> optionsList = getOptionsList(new FileReader(arguments.get(CONFIG_ARGUMENT)), defaults);

        
        LOG.info("Executing " + optionsList.size() + " reports");
        for (Map<String, String> options : optionsList)
            launcher.runReport(options);
    }

    
    static List<Map<String, String>> getOptionsList(Reader reader, Map<String, String> defaults)
    {
        Object config = YAML.load(reader);
        return getOptionsList(config, defaults);
    }

    
    static List<Map<String, String>> getOptionsList(Object config, Map<String, String> defaults)
    {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (config instanceof List) 
        {
            @SuppressWarnings("unchecked")
            List<Object> configList = (List<Object>)config;
            for (Object o : configList)
                list.addAll(getOptionsList(o, defaults));
        }
        else 
        {
        	Map<String, String> newDefaults = new HashMap<String, String>();
        	if (defaults != null)
        		newDefaults.putAll(defaults);

            @SuppressWarnings("unchecked")
            Map<Object, Object> configMap = (Map<Object, Object>)config;

            Object reports = null;
            for (Map.Entry<Object, Object> entry : (configMap).entrySet())
            {
            	if (entry.getKey().equals(REPORTS_HEADING)) 
            		reports = entry.getValue();
            	else 
            	{
            	    String key = entry.getKey().toString();
            	    String value = entry.getValue().toString();

            	    
            	    if (key.equals(ReportLauncher.OUTPUT_DIR_OPTION) || key.equals(ReportLauncher.OUTPUT_OPTION))
            	    {
                        String outputDir = newDefaults.get(ReportLauncher.OUTPUT_DIR_OPTION);
                        if (outputDir != null && !(new File(value).isAbsolute()))
                            value = new File(outputDir).getPath() + File.separatorChar + value;
            	    }

            		newDefaults.put(key, value);
            	}
            }
            if (reports == null)
            	list.add(newDefaults);
            else
            	list = getOptionsList(reports, newDefaults);
        }
        return list;
    }

    
    static Map<String, String> getOptions(String[] args) throws ParseException
    {
        Map<String, String> options = new HashMap<String, String>();

        Options cliOptions = getCmdOptions();

        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(cliOptions, args);

        for (Option op : commandLine.getOptions())
            options.put(op.getLongOpt(), op.getValue());

        for (String s : commandLine.getArgs())
            options.put(CONFIG_ARGUMENT, s);

        return options;
    }

    
    private static void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("reportrunner [options] config_file", getCmdOptions());
    }

    
    private static Options getCmdOptions()
    {
        Options cliOptions = new Options();

        Option opt = new Option("g", ReportLauncher.GEDCOM_OPTION, true, "Gedcom file name");
        opt.setArgName("file");
        cliOptions.addOption(opt);

        opt = new Option("i", ReportLauncher.INDIVIDUAL_OPTION, true, "Starting individual");
        opt.setArgName("indi");
        cliOptions.addOption(opt);

        opt = new Option("o", ReportLauncher.OUTPUT_OPTION, true, "Output file name");
        opt.setArgName("file");
        cliOptions.addOption(opt);

        opt = new Option("d", ReportLauncher.OUTPUT_DIR_OPTION, true, "Output directory");
        opt.setArgName("dir");
        cliOptions.addOption(opt);

        opt = new Option("l", LANGUAGE_ARGUMENT, true, "Language");
        opt.setArgName("lang");
        cliOptions.addOption(opt);

        opt = new Option(null, HELP_REPORTS_ARGUMENT, false, "Display a list of available reports");
        cliOptions.addOption(opt);

        opt = new Option("h", HELP_ARGUMENT, true, "Help (this screen) or display options of a report");
        opt.setArgName("report");
        opt.setOptionalArg(true);
        cliOptions.addOption(opt);
        return cliOptions;
    }

    
    private static class LogFormatter extends Formatter
    {
        static final String LINE_SEPARATOR = System.getProperty("line.separator");

        public String format(LogRecord record)
        {
            StringBuilder result = new StringBuilder();
            result.append(record.getLevel());
            result.append(": ");
            result.append(formatMessage(record));
            result.append(LINE_SEPARATOR);

            if (record.getThrown() != null)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                try
                {
                    record.getThrown().printStackTrace(pw);
                }
                catch (Throwable t)
                {
                }
                pw.close();
                result.append(sw.toString());
            }

            return result.toString();
        }
    }
}
