package gnu.dtools.ritopt;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class OptionMenu implements OptionListener {

    

    public static final String OPTION_COMMAND_CHAR = "-";

    

    public static final String HELP_COMMAND_CHAR = "?";

    

    public static final String RUN_COMMAND_CHAR = "=";

    

    public static final String SHELL_COMMAND_CHAR = "!";

    

    public static final String EXIT_MENU_COMMAND_CHAR = "$";

    

    public static final String LIST_MODULES_COMMAND_CHAR = "+";

    

    public static final String FILE_READ_COMMAND_CHAR = "@";

    

    public static final String FILE_WRITE_COMMAND_CHAR = "%";

    

    public static final String FILE_MODULE_COMMAND_CHAR = ":";

    

    public static final String MENU_PROMPT = "-> ";

    

    private Options options;

    

    private BufferedReader reader;

    

    public OptionMenu( Options options ) {
	this.options = options;
	reader = new BufferedReader( new InputStreamReader( System.in ) );
    }

    

    public void startMenu() {
	String command = "";
	while ( !command.equals( "$" ) ) {
	    System.out.print( MENU_PROMPT );
	    try {
		command = reader.readLine();
	    }
	    catch ( IOException e ) {
		return;
	    }
	    boolean commandEntered = command != null && command.length() > 0;
	    if ( command.equals( "?" ) ) {
		System.err.println( "\t- Options Delimiter" );
		System.err.println( "\t? Help" );
		System.err.println( "\t= Run program and return to menu" );
		System.err.println( "\t! Shell to Operating System" );
		System.err.println( "\t$ Exit menu" );
		System.err.println( "\t+ Additional options" );
		System.err.println( "\t@<filename> Get options from file ["
				    + options.getDefaultOptionFilename()
				    + "]" );
		System.err.println( "\t@@ Get options from file ["
				    + options.getDefaultOptionFilename()
				    + "]" );
                System.err.println( "\t%<filename> Put options in file" );
		System.err.println( "\t%% Put options in file ["
				    + options.getDefaultOptionFilename()
				    + "]" );
                System.err.println( "\t. Quit" );
	    }
	    else if ( commandEntered &&
		      ( command.substring( 0, 1 ).equals(
					    FILE_READ_COMMAND_CHAR )
		      || command.substring( 0, 1 ).equals(
					    FILE_WRITE_COMMAND_CHAR )
		      || command.substring( 0, 1 ).equals(
                                            OPTION_COMMAND_CHAR )
		      || command.substring( 0, 1 ).equals(
                                            FILE_MODULE_COMMAND_CHAR ) ) ) {
		options.process( command );
	    }
	    else if ( commandEntered &&
		      command.substring( 0, 1 ).equals( SHELL_COMMAND_CHAR ) ) {

	    }
	    else if ( commandEntered &&
		      command.substring( 0, 1 ).equals( RUN_COMMAND_CHAR ) ) {
		try {
		    SimpleProcess p
			= new SimpleProcess( Runtime.getRuntime().exec( command.substring( 1 ) ) );
		    System.err.println( "Exit status: " + p.waitFor() );
		}
		catch ( Exception e ) {
		    System.err.println( "ritopt: An Error Occurred During Process Execution" );
		    e.printStackTrace();
		}
		finally {
		    System.out.println( "Press enter to continue..." );
		    try {
			reader.readLine();
		    } catch ( IOException e ) { }
		}
	    }
	    else {
		System.err.println( "(Type ? for Help)" );
	    }
	}
    }

    

    public void optionInvoked( OptionEvent event ) {
	if ( event.getCommand().equals( "menu" ) ) {
	    startMenu();
	}
    }
} 
