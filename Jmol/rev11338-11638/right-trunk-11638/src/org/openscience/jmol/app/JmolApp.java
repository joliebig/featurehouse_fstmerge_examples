
package org.openscience.jmol.app;

import org.jmol.api.*;
import org.jmol.export.history.HistoryFile;
import org.jmol.i18n.GT;
import org.jmol.api.JmolViewer;
import org.jmol.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

public class JmolApp {

  

  public int startupWidth, startupHeight;
  public Point border;
  public boolean haveBorder;

  public File userPropsFile;
  public HistoryFile historyFile;

  public String menuFile;
  
  public boolean splashEnabled = true;
  public boolean useIndependentCommandThread;
  public boolean transparentBackground;
  public boolean checkScriptNoFiles;
  public boolean checkScriptAndOpenFiles;
  public boolean exitUponCompletion;

  public boolean haveConsole = true;
  public boolean haveDisplay = true;
  public boolean isDataOnly;
  public boolean isPrintOnly;
  public boolean isSilent;
  public boolean listCommands;
  
  public String commandOptions = "";
  public String modelFilename;
  public String scriptFilename;
  public String script1 = "";
  public String script;
  public String script2 = "";
  public Point jmolPosition;
  
  public JmolViewer viewer;
  public JmolAdapter modelAdapter;

  public JmolApp() {
    
  }
  
  
  public JmolApp(String[] args) {
    
    if (System.getProperty("javawebstart.version") != null) {

      
      
      System.setSecurityManager(null);
    }
    if (System.getProperty("user.home") == null) {
      System.err.println(GT
          ._("Error starting Jmol: the property 'user.home' is not defined."));
      System.exit(1);
    }
    File ujmoldir = new File(new File(System.getProperty("user.home")), ".jmol");
    ujmoldir.mkdirs();
    userPropsFile = new File(ujmoldir, "properties");
    historyFile = new HistoryFile(new File(ujmoldir, "history"),
        "Jmol's persistent values");

    
    parseCommandLine(args);
  }

  public void parseCommandLine(String[] args) {

    Options options = getOptions(args);

    CommandLine line = null;
    try {
      CommandLineParser parser = new PosixParser();
      line = parser.parse(options, args);
    } catch (ParseException exception) {
      System.err.println("Unexpected exception: " + exception.toString());
    }
    
    args = line.getArgs();
    if (args.length > 0) {
      modelFilename = args[0];
    }

    checkOptions(line, options);

  }

  private Options getOptions(String[] args) {
    Options options = new Options();
    options.addOption("b", "backgroundtransparent", false, GT
        ._("transparent background"));
    options.addOption("h", "help", false, GT._("give this help page"));
    options.addOption("n", "nodisplay", false, GT
        ._("no display (and also exit when done)"));
    options.addOption("c", "check", false, GT
        ._("check script syntax only - no file loading"));
    options.addOption("C", "checkload", false, GT
        ._("check script syntax only - with file loading"));
    options.addOption("d", "debug", false, GT._("debug"));
    options.addOption("i", "silent", false, GT._("silent startup operation"));
    options.addOption("l", "list", false, GT
        ._("list commands during script execution"));
    options.addOption("L", "nosplash", false, GT
        ._("start with no splash screen"));
    options.addOption("o", "noconsole", false, GT
        ._("no console -- all output to sysout"));
    options.addOption("p", "printOnly", false, GT
        ._("send only output from print messages to console (implies -i)"));
    options.addOption("t", "threaded", false, GT
        ._("independent commmand thread"));
    options.addOption("x", "exit", false, GT
        ._("exit after script (implicit with -n)"));

    OptionBuilder.withLongOpt("script");
    OptionBuilder.withDescription(GT
        ._("script file to execute or '-' for System.in"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("s"));

    OptionBuilder.withLongOpt("jmolscript1");
    OptionBuilder.withDescription(GT
        ._("Jmol script to execute BEFORE -s option"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("J"));

    OptionBuilder.withLongOpt("jmolscript2");
    OptionBuilder.withDescription(GT
        ._("Jmol script to execute AFTER -s option"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("j"));

    OptionBuilder.withLongOpt("menu");
    OptionBuilder.withDescription("menu file to use");
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("m"));

    OptionBuilder.withArgName(GT._("property=value"));
    OptionBuilder.hasArg();
    OptionBuilder.withValueSeparator();
    OptionBuilder.withDescription(GT._("supported options are given below"));
    options.addOption(OptionBuilder.create("D"));

    OptionBuilder.withLongOpt("geometry");
    
    
    OptionBuilder.withDescription(GT._("window width x height, e.g. {0}",
        "-g500x500"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("g"));

    OptionBuilder.withLongOpt("quality");
    
    
    OptionBuilder
        .withDescription(GT
            ._("JPG image quality (1-100; default 75) or PNG image compression (0-9; default 2, maximum compression 9)"));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("q"));

    OptionBuilder.withLongOpt("write");
    OptionBuilder.withDescription(GT._("{0} or {1}:filename", new Object[] {
        "CLIP", "GIF|JPG|JPG64|PNG|PPM" }));
    OptionBuilder.hasArg();
    options.addOption(OptionBuilder.create("w"));
    return options;
  }
  
  private void checkOptions(CommandLine line, Options options) {
    if (line.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Jmol", options);

      
      System.out.println();
      System.out.println(GT._("For example:"));
      System.out.println();
      System.out
          .println("Jmol -ions myscript.spt -w JPEG:myfile.jpg > output.txt");
      System.out.println();
      System.out.println(GT
          ._("The -D options are as follows (defaults in parenthesis):"));
      System.out.println();
      System.out.println("  cdk.debugging=[true|false] (false)");
      System.out.println("  cdk.debug.stdout=[true|false] (false)");
      System.out.println("  display.speed=[fps|ms] (ms)");
      System.out.println("  JmolConsole=[true|false] (true)");
      System.out.println("  logger.debug=[true|false] (false)");
      System.out.println("  logger.error=[true|false] (true)");
      System.out.println("  logger.fatal=[true|false] (true)");
      System.out.println("  logger.info=[true|false] (true)");
      System.out.println("  logger.logLevel=[true|false] (false)");
      System.out.println("  logger.warn=[true|false] (true)");
      System.out.println("  plugin.dir (unset)");
      System.out.println("  user.language=[CA|CS|DE|EN|ES|FR|NL|PT|TR] (EN)");

      System.exit(0);
    }

    
    

    
    if (line.hasOption("d")) {
      Logger.setLogLevel(Logger.LEVEL_DEBUG);
    }

    
    
    
    
    

    commandOptions = (isDataOnly ? "JmolData " : "Jmol ");
    if (line.hasOption("p"))
      isPrintOnly = true;
    if (isPrintOnly) {
      commandOptions += "-p";
      isSilent = true;
    }

    
    if (line.hasOption("i"))
      isSilent = true;
    if (isSilent)
      commandOptions += "-i";

    
    if (line.hasOption("o"))
      haveConsole = false;
    if (!haveConsole)
      commandOptions += "-o";

    
    if (line.hasOption("b"))
      transparentBackground = true;
    if (transparentBackground)
      commandOptions += "-b";

    
    if (line.hasOption("t"))
      useIndependentCommandThread = true;
    if (useIndependentCommandThread)
      commandOptions += "-t";

    
    if (line.hasOption("l"))
      listCommands = true;
    if (listCommands)
      commandOptions += "-l";

    
    if (line.hasOption("L"))
      splashEnabled = false;
    if (!splashEnabled)
      commandOptions += "-L";

    
    if (line.hasOption("c"))
      checkScriptNoFiles = true;
    else if (line.hasOption("C"))
      checkScriptAndOpenFiles = true;
    
    if (checkScriptNoFiles)
      commandOptions += "-c";
    else if (checkScriptAndOpenFiles)
      commandOptions += "-c";
      
    
    if (line.hasOption("m")) {
      menuFile = line.getOptionValue("m");
    }

    
    if (line.hasOption("J")) {
      commandOptions += "-J";
      script1 = line.getOptionValue("J");
    }

    
    if (line.hasOption("s")) {
      commandOptions += "-s";
      scriptFilename = line.getOptionValue("s");
    }

    
    if (line.hasOption("j")) {
      commandOptions += "-j";
      script2 = line.getOptionValue("j");
    }

    Point b = null;    
    if (haveDisplay) {
      Dimension size;
      String vers = System.getProperty("java.version");
      if (vers.compareTo("1.1.2") < 0) {
        System.out.println("!!!WARNING: Swing components require a "
            + "1.1.2 or higher version VM!!!");
      }

      size = historyFile.getWindowSize("Jmol");
      if (size != null) {
        startupWidth = size.width;
        startupHeight = size.height;
      }
      historyFile.getWindowBorder("Jmol");
      
      
      
      if (b == null || b.x > 50)
        border = new Point(12, 116);
      else
        border = new Point(b.x, b.y);
      
      
    }
    int width = 500;
    int height = 500;
    
    if (line.hasOption("g")) {
      String geometry = line.getOptionValue("g");
      int indexX = geometry.indexOf('x');
      if (indexX > 0) {
        width = Parser.parseInt(geometry.substring(0, indexX));
        height = Parser.parseInt(geometry.substring(indexX + 1));
      } else {
        width = height = Parser.parseInt(geometry);
      }
      startupWidth = -1;
    }

    if (startupWidth <= 0 || startupHeight <= 0) {
      if (haveDisplay) {
        startupWidth = width + border.x;
        startupHeight = height + border.y;
      } else {
        startupWidth = width;
        startupHeight = height;
      }
    }

    
    if (line.hasOption("w")) {
      int quality = -1;
      if (line.hasOption("q"))
        quality = Parser.parseInt(line.getOptionValue("q"));
      String type_name = line.getOptionValue("w");
      if (type_name != null) {
        if (type_name.length() == 0)
          type_name = "JPG:jpg";
        if (type_name.indexOf(":") < 0)
          type_name += ":jpg";
        int i = type_name.indexOf(":");
        String type = type_name.substring(0, i).toUpperCase();
        type_name = " \"" + type_name.substring(i + 1) + "\"";
        if (type.indexOf(" ") < 0)
          type += " " + quality;
        script2 += ";write image " + width + " " + height + " " + type
            + type_name;
      }
    }

    
    
    
    
    
    if (line.hasOption("n")) {
       
      haveDisplay = false;
      exitUponCompletion = true;
    }
    if (line.hasOption("x"))
      
      exitUponCompletion = true;

    if (!haveDisplay)
      commandOptions += "-n";
    if (exitUponCompletion) {
      commandOptions += "-x";
      script2 += ";exitJmol // " + commandOptions;
    }
    
  }

  public void startViewer(JmolViewer viewer, SplashInterface splash) {  
    this.viewer = viewer;
    try {
    } catch (Throwable t) {
      System.out.println("uncaught exception: " + t);
      t.printStackTrace();
    }
    
    
    
    if (modelFilename != null) {
      viewer.openFileAsynchronously(modelFilename);
    }

    

    
    if (script1 != null && script1.length() > 0) {
      if (!isSilent)
        Logger.info("Executing script: " + script1);
      if (splash != null)
        splash.showStatus(GT._("Executing script 1..."));
      viewer.script(script1);
    }

    

    if (scriptFilename != null) {
      if (!isSilent)
        Logger.info("Executing script from file: " + scriptFilename);
      if (splash != null)
        splash.showStatus(GT._("Executing script file..."));
      if (scriptFilename.equals("-")) {

        

        Scanner scan = new Scanner(System.in);
        String linein = "";
        StringBuffer script = new StringBuffer();
        while (scan.hasNextLine() && (linein = scan.nextLine()) != null
            && !linein.equals("!quit"))
          script.append(linein).append("\n");
        viewer.script(script.toString());
      } else {
        viewer.evalFile(scriptFilename);
      }
    }
    
    if (script2 != null && script2.length() > 0) {
      if (!isSilent)
        Logger.info("Executing script: " + script2);
      if (splash != null)
        splash.showStatus(GT._("Executing script 2..."));
      viewer.script(script2);
    }    
  }
  
}
