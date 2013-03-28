

package net.sf.freecol;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.plaf.FreeColLookAndFeel;
import net.sf.freecol.client.gui.sound.MusicLibrary;
import net.sf.freecol.client.gui.sound.SfxLibrary;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.io.FreeColDataFile;
import net.sf.freecol.common.io.FreeColSavegameFile;
import net.sf.freecol.common.io.FreeColTcFile;
import net.sf.freecol.common.logging.DefaultHandler;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.util.XMLStream;
import net.sf.freecol.server.FreeColServer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public final class FreeCol {

    public static final String  META_SERVER_ADDRESS = "meta.freecol.org";
    public static final int     META_SERVER_PORT = 3540;
    
    public static final String CLIENT_THREAD = "FreeColClient:";
    public static final String SERVER_THREAD = "FreeColServer:";
    public static final String METASERVER_THREAD = "FreeColMetaServer:";

    
    private static final int     DEFAULT_WINDOW_SPACE = 100;

    private static final Logger logger = Logger.getLogger(FreeCol.class.getName());

    private static final String FREECOL_VERSION = "0.9.0-svn";
    private static String FREECOL_REVISION;
    
    private static final String MIN_JDK_VERSION = "1.5";
    private static final String FILE_SEP = System.getProperty("file.separator");

    private static final String DEFAULT_SPLASH_FILE = "splash.jpg";
    private static final String DEFAULT_TC = "freecol";

    private static boolean  windowed = false,
                            sound = true,
                            javaCheck = true,
                            memoryCheck = true,
                            consoleLogging = false;
    private static Dimension windowSize = new Dimension(-1, -1);
    private static String   dataFolder = "data" + FILE_SEP;
    private static String logFile = null;

    private static FreeColClient freeColClient;

    private static boolean standAloneServer = false;
    private static boolean publicServer = true;
    private static boolean inDebugMode = false;
    private static boolean usesExperimentalAI = false;

    private static int serverPort;
    private static String serverName = null;
    private static final int DEFAULT_PORT = 3541;

    private static File mainUserDirectory = null;

    private static File saveDirectory;
    
    private static File tcUserDirectory;
    
    private static String tc = DEFAULT_TC;
    
    private static File savegameFile = null;
    
    private static File clientOptionsFile = null;

    private static Level logLevel = Level.INFO;

    private static boolean checkIntegrity = false;

    private static final Options options = new Options();
    
    private static String splashFilename = DEFAULT_SPLASH_FILE;
    private static boolean displaySplash = false;



    private FreeCol() {
        
    }

    
    public static void main(String[] args) {

        try {
            Manifest manifest = new Manifest(FreeCol.class.getResourceAsStream("/META-INF/MANIFEST.MF"));
            Attributes attribs = manifest.getMainAttributes();
            String revision = attribs.getValue("Revision");
            FREECOL_REVISION = FREECOL_VERSION + " (Revision: " + revision + ")";
        } catch (Exception e) {
            System.out.println("Unable to load Manifest.");
            FREECOL_REVISION = FREECOL_VERSION;
        }

        
        handleArgs(args);
        
        
        final JWindow splash = (displaySplash) ? displaySplash(splashFilename) : null;

        createAndSetDirectories();
        initLogging();

        Locale locale = getLocale();
        Locale.setDefault(locale);
        Messages.setMessageBundle(locale);
        
        if (javaCheck && !checkJavaVersion()) {
            removeSplash(splash);
            System.err.println("Java version " + MIN_JDK_VERSION +
                               " or better is recommended in order to run FreeCol." +
                               " Use --no-java-check to skip this check.");
            System.exit(1);
        }

        int  minMemory = 128;  
        if (memoryCheck && Runtime.getRuntime().maxMemory() < minMemory * 1000000) {
            removeSplash(splash);
            System.out.println("You need to assign more memory to the JVM. Restart FreeCol with:");
            System.out.println("java -Xmx" + minMemory + "M -jar FreeCol.jar");
            System.exit(1);
        }
        
        if (!initializeResourceFolders()) {
            removeSplash(splash);
            System.exit(1);
        }

        if (standAloneServer) {
            logger.info("Starting stand-alone server.");
            try {
                final FreeColServer freeColServer;
                if (savegameFile != null) {
                    XMLStream xs = null;
                    try {
                        
                        final FreeColSavegameFile fis = new FreeColSavegameFile(savegameFile);
                        xs = FreeColServer.createXMLStreamReader(fis);
                        final XMLStreamReader in = xs.getXMLStreamReader();
                        in.nextTag();
                        final boolean defaultSingleplayer = Boolean.valueOf(in.getAttributeValue(null, "singleplayer")).booleanValue();
                        final boolean defaultPublicServer;
                        final String publicServerStr =  in.getAttributeValue(null, "publicServer");
                        if (publicServerStr != null) {
                            defaultPublicServer = Boolean.valueOf(publicServerStr).booleanValue();
                        } else {
                            defaultPublicServer = false;
                        }
                        xs.close();
                        
                        freeColServer = new FreeColServer(fis, defaultPublicServer, defaultSingleplayer, serverPort, serverName);
                    } catch (Exception e) {
                        removeSplash(splash);
                        System.out.println("Could not load savegame.");
                        System.exit(1);
                        return;
                    } finally {
                        xs.close();
                    }
                } else {
                    try {
                        freeColServer = new FreeColServer(publicServer, false, serverPort, serverName);
                    } catch (NoRouteToServerException e) {
                        removeSplash(splash);
                        System.out.println(Messages.message("server.noRouteToServer"));
                        System.exit(1);
                        return;
                    }
                }

                Runtime runtime = Runtime.getRuntime();
                runtime.addShutdownHook(new Thread() {
                        public void run() {
                            freeColServer.getController().shutdown();
                        }
                    });
                if (checkIntegrity) {
                    System.exit((freeColServer.getIntegrity())
                                ? 0 : 1);
                }
            } catch (IOException e) {
                removeSplash(splash);
                System.err.println("Error while loading server: " + e);
                System.exit(1);
            }
        } else {
            final Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            if (windowSize.width == -1 || windowSize.height == -1) {
                
                windowSize.width = bounds.width - DEFAULT_WINDOW_SPACE;
                windowSize.height = bounds.height - DEFAULT_WINDOW_SPACE;
            }
            final Dimension preloadSize;
            if (windowed) {
                preloadSize = windowSize;
            } else {
                preloadSize = new Dimension(bounds.width, bounds.height);
            }
            try {
                UIManager.setLookAndFeel(new FreeColLookAndFeel(dataFolder, preloadSize));
            } catch (UnsupportedLookAndFeelException e) {
                logger.warning("Could not load the \"FreeCol Look and Feel\"");
            } catch (FreeColException e) {
                removeSplash(splash);
                e.printStackTrace();
                System.out.println("\nThe data files could not be found by FreeCol. Please make sure");
                System.out.println("they are present. If FreeCol is looking in the wrong directory");
                System.out.println("then run the game with a command-line parameter:");
                System.out.println("");
                printUsage();
                System.exit(1);
            }

            
            
            

            logger.info("Now starting to load images.");

            ImageLibrary lib;
            try {
                lib = new ImageLibrary(dataFolder);
            } catch (FreeColException e) {
                removeSplash(splash);
                e.printStackTrace();
                System.out.println("\nThe data files could not be found by FreeCol. Please make sure");
                System.out.println("they are present. If FreeCol is looking in the wrong directory");
                System.out.println("then run the game with a command-line parameter:");
                System.out.println("");
                printUsage();
                System.exit(1);
                return;
            }

            MusicLibrary    musicLibrary = null;
            SfxLibrary      sfxLibrary = null;
            if (sound) {
                try {
                    musicLibrary = new MusicLibrary(dataFolder);
                } catch (FreeColException e) {
                    System.out.println("The music files could not be loaded by FreeCol. Disabling music.");
                }

                try {
                    sfxLibrary = new SfxLibrary(dataFolder);
                } catch (FreeColException e) {
                    System.out.println("The sfx files could not be loaded by FreeCol. Disabling sfx.");
                }
            }

            final boolean loadSavegame = (savegameFile != null);
            freeColClient = new FreeColClient(windowed, preloadSize, lib, musicLibrary, sfxLibrary, !loadSavegame);

            if (loadSavegame) {
                final FreeColClient theFreeColClient = freeColClient;
                final File theSavegameFile = savegameFile;
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            theFreeColClient.getConnectController().loadGame(theSavegameFile);
                        }
                    });
            }
        }
        
        removeSplash(splash);
    }

    
    private static JWindow displaySplash(String filename) {
        try {
            Image im = Toolkit.getDefaultToolkit().getImage(filename);
            JWindow f = new JWindow();
            f.getContentPane().add(new JLabel(new ImageIcon(im)));
            f.pack();
            Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
            f.setLocation(center.x - f.getWidth() / 2, center.y - f.getHeight() / 2);
            f.setVisible(true);
            return f;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while displaying splash screen", e);
            return null;
        }
    }

    
    private static void removeSplash(JWindow splash) {
        if (splash != null) {
            splash.setVisible(false);
            splash.dispose();
        }
    }
    
    
    private static void initLogging() {
        final Logger baseLogger = Logger.getLogger("");
        final Handler[] handlers = baseLogger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            baseLogger.removeHandler(handlers[i]);
        }
        if (logFile == null) {
            logFile = mainUserDirectory.getPath() + File.separator
                + "FreeCol.log";
        }
        try {
            baseLogger.addHandler(new DefaultHandler(consoleLogging, logFile)); 
            Logger freecolLogger = Logger.getLogger("net.sf.freecol");
            freecolLogger.setLevel(logLevel);
        } catch (FreeColException e) {
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable e) {
                    baseLogger.log(Level.WARNING, "Uncaught exception from thread: " + thread, e);
                }
            });
    }
    
    
    public static Locale getLocale() { 
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader in = null;
        try {
            in = xif.createXMLStreamReader(new FileInputStream(getClientOptionsFile()), "UTF-8");
            in.nextTag();
            
            for(int eventid = in.getEventType();eventid != XMLEvent.END_DOCUMENT; eventid = in.getEventType()) { 

                
                if(eventid == XMLEvent.START_ELEMENT) {
                    if (ClientOptions.LANGUAGE.equals(in.getAttributeValue(null, "id"))) {
                        return LanguageOption.getLocale(in.getAttributeValue(null, "value")); 
                    }
                }
                in.nextTag();
            }
            
            logger.log(Level.WARNING, "Language setting not found in client options file.  Using default.");
            return Locale.getDefault();
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while loading options.", e);
            return Locale.getDefault();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception while closing stream.", e);
                return Locale.getDefault();
            }
        }
    }

    
    public static int getDefaultPort() {
        return DEFAULT_PORT;
    }
    
    
    public static File getClientOptionsFile() {
        return clientOptionsFile;
    }
    
    
    public static Specification getSpecification() {
        return Specification.getSpecification();
    }

    
    public static FreeColClient getFreeColClient() {
        return freeColClient;
    }

    
    private static File createAndSetDirectories() {
        
        
        String freeColDirectoryName = "/".equals(System.getProperty("file.separator")) ?
            ".freecol" : "freecol";

        if (mainUserDirectory == null) {
            mainUserDirectory = new File(System.getProperty("user.home"), freeColDirectoryName);
        }
        if (mainUserDirectory.exists()) {
            if (mainUserDirectory.isFile()) {
                System.out.println("Could not create " + freeColDirectoryName + " under "
                                   + System.getProperty("user.home") + " because there "
                                   + "already exists a regular file with the same name.");
                return null;
            }
        } else {
            mainUserDirectory.mkdir();
        }
        if (saveDirectory == null) {
            saveDirectory = new File(mainUserDirectory, "save");
        }
        if (saveDirectory.exists()) {
            if (saveDirectory.isFile()) {
                System.out.println("Could not create freecol/save under "
                                   + System.getProperty("user.home") + " because there "
                                   + "already exists a regular file with the same name.");
                return null;
            }
        } else {
            saveDirectory.mkdir();
        }
        tcUserDirectory = new File(mainUserDirectory, tc);
        if (tcUserDirectory.exists()) {
            if (tcUserDirectory.isFile()) {
                System.out.println("Could not create freecol/" + tc + " under "
                                   + System.getProperty("user.home") + " because there "
                                   + "already exists a regular file with the same name.");
                return null;
            }
        } else {
            tcUserDirectory.mkdir();
        }
        clientOptionsFile = new File(tcUserDirectory, "options.xml");
        return mainUserDirectory;
    }

    
    private static void setSavegame(String name) {
        if(name == null){
            System.out.println("No savegame given with --load-savegame parameter");
            System.exit(1);
        }
        
        savegameFile = new File(name);
        if (!savegameFile.exists() || !savegameFile.isFile()) {
            savegameFile = new File(getSaveDirectory(), name);
            if (!savegameFile.exists() || !savegameFile.isFile()) {
                System.out.println("Could not find savegame file: " + name);
                System.exit(1);
            }
        } else {
            setSaveDirectory(savegameFile.getParentFile());
        }
    }

    
    public static File getSaveDirectory() {
        return saveDirectory;
    }

    
    public static void setSaveDirectory(File saveDirectory) {
        FreeCol.saveDirectory = saveDirectory;
    }

    
    public static File getDataDirectory() {
        if (dataFolder.equals("")) {
            return new File("data");
        } else {        
            return new File(dataFolder);
        }
    }
    
    
    public static File getModsDirectory() {
        return new File(getDataDirectory(), "mods");
    }
    
    
    public static File getAutosaveDirectory() {
        return saveDirectory;
    }

    public static InputStream getSpecificationInputStream() throws IOException {
        final FreeColTcFile tcData = new FreeColTcFile(tc);
        ResourceManager.setTcMapping(tcData.getResourceMapping());
        return tcData.getSpecificationInputStream();
    }
    
    public static boolean initializeResourceFolders() {
        FreeColDataFile baseData = new FreeColDataFile(new File(dataFolder, "base"));
        ResourceManager.setBaseMapping(baseData.getResourceMapping());
        
        
        InputStream si = null;
        try {
            si = getSpecificationInputStream();
            Specification.createSpecification(si);
        } catch (IOException e) {
            System.err.println("Could not load specification.xml for: " + tc);
            return false;
        } finally {
            try {
                si.close();
            } catch (Exception e) {}
        }

        return true;
    }

    
    private static boolean checkJavaVersion() {
        
        
        String version = System.getProperty("java.version");
        boolean success = (version.compareTo(MIN_JDK_VERSION) >= 0);
        return success;
    }



    
    private static void handleArgs(String[] args) {
        
        CommandLineParser parser = new PosixParser();

        
        String localeArg = null;
        String locationArg = null;
        for (int index = 0; index < args.length - 1; index++) {
            if ("--default-locale".equals(args[index])) {
                localeArg = args[++index];
            } else if ("--freecol-data".equals(args[index])) {
                locationArg = args[++index];
            }
        }
        if (locationArg != null) {
            dataFolder = locationArg;
        }
        if (localeArg != null) {
            Locale locale = LanguageOption.getLocale(localeArg);
            Locale.setDefault(locale);
            Messages.setMessageBundle(locale);
        }

        
        options.addOption(OptionBuilder.withLongOpt("freecol-data")
                          .withDescription(Messages.message("cli.freecol-data"))
                          .withArgName(Messages.message("cli.arg.directory"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("tc")
                          .withDescription(Messages.message("cli.tc"))
                          .withArgName(Messages.message("cli.arg.name"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("home-directory")
                          .withDescription(Messages.message("cli.home-directory"))
                          .withArgName(Messages.message("cli.arg.directory"))
                          .withType(new File("dummy"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("log-console")
                          .withDescription(Messages.message("cli.log-console"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("log-file")
                          .withDescription(Messages.message("cli.log-file"))
                          .withArgName(Messages.message("cli.arg.name"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("log-level")
                          .withDescription(Messages.message("cli.log-level"))
                          .withArgName(Messages.message("cli.arg.loglevel"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("no-java-check")
                          .withDescription(Messages.message("cli.no-java-check"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("windowed")
                          .withDescription(Messages.message("cli.windowed"))
                          .withArgName(Messages.message("cli.arg.dimensions"))
                          .hasOptionalArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("default-locale")
                          .withDescription(Messages.message("cli.default-locale"))
                          .withArgName(Messages.message("cli.arg.locale"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("no-memory-check")
                          .withDescription(Messages.message("cli.no-memory-check"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("no-sound")
                          .withDescription(Messages.message("cli.no-sound"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("usage")
                          .withDescription(Messages.message("cli.help"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("help")
                          .withDescription(Messages.message("cli.help"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("version")
                          .withDescription(Messages.message("cli.version"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("debug")
                          .withDescription(Messages.message("cli.debug"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("private")
                          .withDescription(Messages.message("cli.private"))
                          .create());
        options.addOption(OptionBuilder.withLongOpt("server")
                          .withDescription(Messages.message("cli.server"))
                          .withArgName(Messages.message("cli.arg.port"))
                          .hasOptionalArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("load-savegame")
                          .withDescription(Messages.message("cli.load-savegame"))
                          .withArgName(Messages.message("cli.arg.file"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("server-name")
                          .withDescription(Messages.message("cli.server-name"))
                          .withArgName(Messages.message("cli.arg.name"))
                          .hasArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("splash")
                          .withDescription(Messages.message("cli.splash"))
                          .withArgName(Messages.message("cli.arg.file"))
                          .hasOptionalArg()
                          .create());
        options.addOption(OptionBuilder.withLongOpt("check-savegame")
                          .withDescription(Messages.message("cli.check-savegame"))
                          .create());
        
        options.addOption(OptionBuilder.withLongOpt("experimentalAI")
                          .withDescription(Messages.message("cli.experimentalAI"))
                          .create());

        try {
            
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("default-locale")) {
                
                String languageID = line.getOptionValue("default-locale");
                int index = languageID.indexOf('.');
                if (index > 0) {
                    languageID = languageID.substring(0, index);
                }
                Locale newLocale = LanguageOption.getLocale(languageID);
                Locale.setDefault(newLocale);
                Messages.setMessageBundle(newLocale);
            }
            if (line.hasOption("splash")) {
                displaySplash = true;
                final String str = line.getOptionValue("splash");
                if (str != null) {
                    splashFilename = str;
                }
            }
            if (line.hasOption("freecol-data")) {
                dataFolder = line.getOptionValue("freecol-data");
                if (!dataFolder.endsWith(FILE_SEP)) {
                    dataFolder += FILE_SEP;
                }
            }
            if (line.hasOption("tc")) {
                tc = line.getOptionValue("tc");
            }
            if (line.hasOption("home-directory")) {
                String arg = line.getOptionValue("home-directory");
                mainUserDirectory = new File(arg);
                String errMsg = null;
                if(!mainUserDirectory.exists()){
                    errMsg = Messages.message("cli.error.home.notExists", "%string%", arg);
                }
                if(!mainUserDirectory.canRead()){
                    errMsg = Messages.message("cli.error.home.noRead", "%string%", arg);
                }
                if(!mainUserDirectory.canWrite()){
                    errMsg = Messages.message("cli.error.home.noWrite", "%string%", arg);
                }
                if(errMsg != null){
                    System.out.println(errMsg);
                    System.exit(1);
                }
            }
            if (line.hasOption("log-console")) {
                consoleLogging = true;
            }
            if (line.hasOption("log-file")) {
                logFile = line.getOptionValue("log-file");
            }
            if (line.hasOption("log-level")) {
                String logLevelString = line.getOptionValue("log-level").toUpperCase();
                try {
                    logLevel = Level.parse(logLevelString);
                } catch (IllegalArgumentException e) {
                    printUsage();
                    System.exit(1);
                }
            }
            if (line.hasOption("no-java-check")) {
                javaCheck = false;
            }
            if (line.hasOption("windowed")) {
                windowed = true;
                String dimensions = line.getOptionValue("windowed");
                if (dimensions != null) {
                    String[] xy = dimensions.split("[^0-9]");
                    if (xy.length == 2) {
                        windowSize = new Dimension(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                    } else {
                        printUsage();
                        System.exit(1);
                    }
                }
            }
            if (line.hasOption("no-sound")) {
                sound = false;
            }
            if (line.hasOption("no-memory-check")) {
                memoryCheck = false;
            }
            if (line.hasOption("help") || line.hasOption("usage")) {
                printUsage();
                System.exit(0);
            }
            if (line.hasOption("version")) {
                System.out.println("FreeCol " + getVersion());
                System.exit(0);
            }
            if (line.hasOption("debug")) {
                inDebugMode = true;
                
                if(!line.hasOption("log-level")){
                    logLevel = Level.FINEST;
                }
            }
            if (line.hasOption("server")) {
                standAloneServer = true;
                String arg = line.getOptionValue("server");
                try {
                    serverPort = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    System.out.println(Messages.message("cli.error.port", "%string%", arg));
                    System.exit(1);
                }
            }
            if (line.hasOption("private")) {
                publicServer = false;
            }
            if (line.hasOption("check-savegame")) {
                setSavegame(line.getOptionValue("load-savegame"));
                checkIntegrity = true;
                standAloneServer = true;
                serverPort = DEFAULT_PORT;
            }
            if (line.hasOption("load-savegame")) {
                setSavegame(line.getOptionValue("load-savegame"));
            }
            if (line.hasOption("server-name")) {
                serverName = line.getOptionValue("server-name");
            }
            if (line.hasOption("experimentalAI")) {
                usesExperimentalAI = true;
            }
        } catch(ParseException e) {
            System.out.println("\n" + e.getMessage() + "\n");
            printUsage();
            System.exit(1);
        }

    }


    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -Xmx 128M -jar freecol.jar [OPTIONS]", options);
    }

    
    public static String getVersion() {
        return FREECOL_VERSION;
    }

    
    public static String getRevision() {
        return FREECOL_REVISION;
    }

    
    public static boolean isInDebugMode() {
        return inDebugMode;
    }

    
    
    public static void setInDebugMode(boolean debug) {
        inDebugMode = debug;
    }

    
    public static boolean usesExperimentalAI() {
        return usesExperimentalAI;
    }

}
