package gnu.dtools.ritopt;



import java.util.*;
import java.io.*;
import net.sf.jabref.*;



public class Options implements OptionRegistrar, OptionModuleRegistrar,
                                OptionListener {

    

    public static final int DEFAULT_VERBOSITY = 3;

    

    public static final boolean DEFAULT_DEPRECATED = false;

    

    public static final String DEFAULT_REASON = "No reason given.";

    

    public static final String DEFAULT_GENERAL_MODULE_NAME = "General";

    

    public static final boolean DEFAULT_DISPLAY_USAGE = false; 

    

    public static final boolean DEFAULT_USE_MENU = false; 

    

    public static final String DEFAULT_PROGRAM_NAME = "java program";

    

    public static final String DEFAULT_OPTION_FILENAME = "default.opt";

    

    private int verbosity;

    

    private String usageProgram;

    

    private String version;

    

    private String defaultOptionFilename;

    

    private boolean displayUsage;

    

    private boolean useMenu;

    

    private boolean debugFlag;

    

    private OptionModule currentModule;

    

    private OptionModule generalModule;

    

    private java.util.HashMap modules;

    

    private NotifyOption helpOption;

    

    private NotifyOption menuOption;

    

    private NotifyOption versionOption;

    

    private OptionMenu menu;

    

    public Options() {
        this( DEFAULT_PROGRAM_NAME );
    }

    

    public Options( String programName ) {
        verbosity = DEFAULT_VERBOSITY;
        displayUsage = DEFAULT_DISPLAY_USAGE;
        useMenu = DEFAULT_USE_MENU;
        defaultOptionFilename = DEFAULT_OPTION_FILENAME;
        usageProgram = programName;
        modules = new HashMap();
        menu = new OptionMenu( this );
        helpOption = new NotifyOption( this, "help", "" );
        versionOption = new NotifyOption( this, "version", "" );
        version = "Version 1.0";
        menuOption = new NotifyOption( menu, "menu", "" );
        generalModule = new OptionModule( DEFAULT_GENERAL_MODULE_NAME );
        currentModule = generalModule;

        
        register( "version", 'v',
                  "Displays version information.", versionOption );
        
        
    }

    

    public String getHelp() {
        String retval = (displayUsage ? getUsage() + "\n\n" : "" ) +
            
            
            Option.getHelpHeader() + "\n\n" + generalModule.getHelp();
        Iterator it = modules.values().iterator();
        while ( it.hasNext() ) {
            OptionModule module = (OptionModule)it.next();
            retval += "\n\nOption Listing for " + module.getName() + "\n";
            retval += module.getHelp() + "\n";
        }
        return retval;
    }

    

    public String getUsage() {
        return getUsageProgram()
            + " @optionfile :module: OPTIONS ... :module: OPTIONS";
    }

    

    public String getUsageProgram() {
        return usageProgram;
    }

    

    public String getVersion() {
        return version;
    }

    

    public String getDefaultOptionFilename() {
        return defaultOptionFilename;
    }

    

    public boolean getDebugFlag() {
        return debugFlag;
    }

    

    public boolean shouldDisplayUsage() {
        return displayUsage;
    }

    

    public boolean shouldUseMenu() {
        return useMenu;
    }

    

    public void setDisplayUsage( boolean b ) {
        displayUsage = b;
    }

    

    public void setUseMenu( boolean b ) {
        useMenu = b;
    }

    

    public void setUsageProgram( String program ) {
        usageProgram = program;
    }

    

    public void setVersion( String version ) {
        this.version = version;
    }

    

    public void setDefaultOptionFilename( String fn ) {
        defaultOptionFilename = fn;
    }


    

    public void displayHelp() {
        System.err.println( getHelp() );
    }

    

    public void displayVersion() {
        System.err.println( getVersion() +" (build " +Globals.BUILD +")");
    }

    

    public void register( String longOption, Option option ) {
        generalModule.register( longOption, option );
    }

    

    public void register( char shortOption, Option option ) {
        generalModule.register( shortOption, option );
    }

    

    public void register( String longOption, char shortOption,
                          Option option ) {
        generalModule.register( longOption, shortOption, option );
    }

    

    public void register( String longOption, char shortOption,
                          String description, Option option ) {
        generalModule.register( longOption, shortOption, description, option );
    }

    

    public void register( String longOption, char shortOption,
                          String description, Option option,
                          boolean deprecated ) {
        generalModule.register( longOption, shortOption, description, option,
                                deprecated );
    }

    

    public void register( OptionModule module ) {
        register( module.getName(), module );
    }

    

    public void register( String name, OptionModule module ) {
        modules.put( name.toLowerCase(), module );
    }

    

    public String[] process( String args[] )
    {
        String []retval = new String[0];
        try {
            retval = processOptions( args );
        }
        catch ( OptionException e ) {
            System.err.println( "Error: " + e.getMessage() );
        }
        
        return retval;
    }

    

    public OptionModule getModule( String name ) {
        return (OptionModule)modules.get( name.toLowerCase() );
    }

    

    public boolean moduleExists( String name ) {
        return getModule( name ) != null;
    }

    

    public void optionInvoked( OptionEvent event ) {
        if ( event.getCommand().equals( "help" ) ) {
            displayHelp();
        }
        else if ( event.getCommand().equals( "version" ) ) {
            displayVersion();
        }
    }

    

    public String[] process( String str ) {
        return process( split( str ) );
    }

    

    public String[] split( String str ) {
        StringBuffer buf = new StringBuffer( str.length() );
        java.util.List l = new java.util.ArrayList();
        int scnt = Utility.count( str, '"' );
        boolean q = false;
        if ( ((double)scnt) / 2.0 != (double)(scnt / 2) ) {
            throw new OptionProcessingException( "Expecting an end quote." );
        }
        for ( int n = 0; n < str.length(); n++ ) {
            if ( str.charAt( n ) == '"' ) {
                q = !q;
            }
            else if ( str.charAt( n ) == ' ' && !q ) {
                l.add( buf.toString() );
                buf = new StringBuffer( str.length() );
            }
            else {
                buf.append( str.charAt( n ) );
            }
        }
        if ( buf.length() != 0 ) {
            l.add( buf.toString() );
        }
        Iterator it = l.iterator();
        String retval[] = new String[ l.size() ];
        int n = 0;
        while ( it.hasNext() ) {
            retval[ n++ ] = (String)it.next();
        }
        return retval;
    }

    

    public void writeOptionFile( String filename ) {
        BufferedOutputStream writer = null;
        String line = null;
        Iterator it = null;
        currentModule = generalModule;
        try {
            writer =
                new BufferedOutputStream( new FileOutputStream( filename ) );
            PrintStream ps = new PrintStream( writer );
            generalModule.writeFileToPrintStream( ps );
            it = modules.values().iterator();
            while ( it.hasNext() ) {
                OptionModule module = (OptionModule)it.next();
                module.writeFileToPrintStream( ps );
            }
        }
        catch ( IOException e ) {
            throw new OptionProcessingException( e.getMessage() );
        }
        finally {
            try {
                if ( writer != null )
                    writer.close();
            }
            catch( IOException e ) {
                throw new OptionProcessingException( e.getMessage() );
            }
        }
    }

    

    public void loadOptionFile( String filename ) {
        BufferedReader reader = null;
        String line = null;
        currentModule = generalModule;
        try {
            reader = new BufferedReader( new FileReader( filename ) );
            while ( ( line = reader.readLine() ) != null ) {
                line = Utility.stripComments( line, '\"', ';' );
                process( line );
            }
        }
        catch ( IOException e ) {
            throw new OptionProcessingException( e.getMessage() );
        }
        finally {
            try {
                if ( reader != null )
                    reader.close();
            }
            catch( IOException e ) {
                throw new OptionProcessingException( e.getMessage() );
            }
        }
    }

    

    private String[] processOptions( String args[] ) {
        String retval[] = null;
        String moduleName = "general";
        String optionFile = "";
        char shortOption = '\0';
        String longOption = "";
        for ( int n = 0; n < args.length && retval == null; n++ ) {
            boolean moduleInvoked = false;
            boolean shortOptionInvoked = false;
            boolean longOptionInvoked = false;
            boolean readOptionFileInvoked = false;
            boolean writeOptionFileInvoked = false;
            if ( args[ n ].length() >= 1 ) {
                char fc = args[ n ].charAt( 0 );
                moduleInvoked = fc == ':';
                readOptionFileInvoked = fc == '@';
                writeOptionFileInvoked = fc == '%';
            }
            if ( args[ n ].length() >= 2 ) {
                String s = args[ n ].substring( 0, 2 );
                shortOptionInvoked = ( !s.equals( "--" ) &&
                           s.charAt( 0 ) == '-' );
                longOptionInvoked = ( s.equals( "--" ) );
            }
            if ( debugFlag ) {
                System.err.println( "Short Option: " + shortOptionInvoked );
                System.err.println( "Long Option: " + longOptionInvoked );
                System.err.println( "Module: " + moduleInvoked );
                System.err.println( "Load Option File: " +
                                    readOptionFileInvoked );
                System.err.println( "Write Option File: "
                                    + writeOptionFileInvoked );
            }
            if ( moduleInvoked ) {
                if (  args[ n ].charAt( args[ n ].length() - 1 ) != ':' ) {
                    System.err.println( args[ n ] );
                    throw new
                        OptionProcessingException(
                                                  "Module arguments must start"
                                                  + " with : and end with :."
                                                  );
                }
                else {
                    moduleName = args[n].substring( 1,
                                                    args[n].length() - 1
                                                    ).toLowerCase();
                    if ( moduleName.length() == 0
                         || moduleName.equals( "general" ) ) {
                        moduleName = "general";
                        currentModule = generalModule;
                    }
                    else {
                        currentModule = getModule( moduleName );
                    }
                    if ( currentModule == null )
                        throw new OptionProcessingException( "Module '" +
                                                             moduleName +
                                                         "' does not exist." );
                    if ( debugFlag ) {
                        System.err.println( "Module: " + moduleName );
                    }
                }
                moduleInvoked = false;
            }
            else if ( readOptionFileInvoked ) {
                optionFile = Utility.trim( args[ n ].substring( 1 ) );
                if ( optionFile.equals( "@" )
                     || optionFile.length() == 0 )
                    optionFile = defaultOptionFilename;
                if ( debugFlag ) {
                    System.err.println( "Option file: '" + optionFile + "'." );
                }
                loadOptionFile( optionFile );
            }
            else if ( shortOptionInvoked ) {
                shortOption = args[ n ].charAt( 1 );
                if ( !Utility.isAlphaNumeric( shortOption ) ) {
                    throw new OptionProcessingException(
                      "A short option must be alphanumeric. -" + shortOption
                      + " is not acceptable." );
                }
                if ( debugFlag ) {
                    System.err.println( "Short option text: " + shortOption );
                }
                char delim = ( args[ n ].length() >= 3 ) ?
                    args[ n ].charAt( 2 ) : '\0';
                if ( delim == '+' || delim == '-' ) {
                    currentModule.action( shortOption, delim );
                }
                else if ( delim == '=' ) {
                    currentModule.action( shortOption,
                                          args[ n ].substring( 3 ) );
                }
                else if ( delim == '\0' ) {
                    String dtext = "+";
                    char dpeek = '\0';
                    if ( n < args.length - 1 ) {
                        dpeek = args[ n + 1 ].charAt( 0 );
                        if ( !Utility.contains( args[ n + 1 ].charAt( 0 ),
                                                "-[@" ) ) {
                            dtext = args[ n + 1 ];
                            n++;
                        }
                    }
                    currentModule.action( shortOption, dtext );
                }
                else if ( Utility.isAlphaNumeric( delim ) ) {
                    for ( int j = 1; j < args[ n ].length(); j++ ) {
                        if ( Utility.isAlphaNumeric( args[ n ].charAt( j ) ) ) {
                            currentModule.action( shortOption, "+" );
                        }
                        else {
                            throw new OptionProcessingException(
                              "A short option must be alphanumeric. -"
                              + shortOption + " is not acceptable." );
                        }
                    }
                }
            }
            else if ( longOptionInvoked ) {
                char lastchar = args[ n ].charAt( args[ n ].length() - 1 );
                int eqindex = args[ n ].indexOf( "=" );
                if ( eqindex != -1 ) {
                    longOption = args[ n ].substring( 2, eqindex );
                    String value = args[ n ].substring( eqindex + 1 );
                    currentModule.action( longOption, value );
                }
                else if ( Utility.contains( lastchar, "+-" ) ) {
                    longOption = args[ n ].substring( 2,
                                                      args[ n ].length() - 1 );
                    currentModule.action( longOption, lastchar );
                }
                else {
                    longOption = args[ n ].substring( 2 );
                    String dtext = "+";
                    char dpeek = '\0';
                    if ( n < args.length - 1 && args[ n + 1 ].length() > 0 ) {
                        dpeek = args[ n + 1 ].charAt( 0 );
                        if ( !Utility.contains( args[ n + 1 ].charAt( 0 ),
                                                "-[@" ) ) {
                            dtext = args[ n + 1 ];
                            n++;
                        }
                    }
                    currentModule.action( longOption, dtext );
                }
                if ( debugFlag ) {
                    System.err.println( "long option: " + longOption );
                }
            }
            else if ( writeOptionFileInvoked ) {
                optionFile = Utility.trim( args[ n ].substring( 1 ) );
                if ( optionFile.equals( "%" )
                     || optionFile.length() == 0 )
                    optionFile = defaultOptionFilename;
                if ( debugFlag ) {
                    System.err.println( "Option file: '" + optionFile + "'." );
                }
                writeOptionFile( optionFile );
            }
            else {
                retval = new String[ args.length - n ];
                for ( int j = n; j < args.length; j++ ) {
                    retval[ j - n ] = args[ j ];
                }
            }
        }
        if ( retval == null ) retval = new String[ 0 ];
        return retval;
    }
}
