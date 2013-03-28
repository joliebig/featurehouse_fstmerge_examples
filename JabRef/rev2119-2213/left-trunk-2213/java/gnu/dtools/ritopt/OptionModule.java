package gnu.dtools.ritopt;



import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;



public class OptionModule implements OptionRegistrar {

    

    private java.util.HashMap options;

    

    private String name;

    

    private boolean deprecated;

    

    public static final char DEFAULT_SHORT_OPTION = '\0';

    

    public static final String DEFAULT_LONG_OPTION = null;

    

    public static final String DEFAULT_DESCRIPTION = "No description given";

    

    public static final boolean DEFAULT_DEPRECATED = false;

    

    public static final String DEFAULT_MODULE_NAME = "Special";

    

    public OptionModule() {
	this( DEFAULT_MODULE_NAME );
    }

    

    public OptionModule( String name ) {
	options = new java.util.HashMap();
	this.name = name;
	deprecated = false;
    }

    

    public void register( String longOption, Option option ) {
	register( longOption, DEFAULT_SHORT_OPTION, option );
    }

    

    public void register( char shortOption, Option option ) {
	register( DEFAULT_LONG_OPTION, shortOption, option );

    }

    

    public void register( String longOption, char shortOption,
			  Option option ) {
	register( longOption, shortOption, DEFAULT_DESCRIPTION, option );
    }

    

    public void register( String longOption, char shortOption,
			  String description, Option option ) {
	register( longOption, shortOption, description, option,
	      DEFAULT_DEPRECATED );
    }

    

    public void register( String longOption, char shortOption,
			  String description, Option option,
			  boolean deprecated ) {
	if ( optionExists( option ) ) {
	    throw new OptionRegistrationException( "Option Already Registered",
						   option );
	}
	option.setLongOption( longOption );
	option.setShortOption( shortOption );
	option.setDeprecated( deprecated );
	option.setDescription( description );
	options.put( option.getHashKey(), option );
    }

    

    public boolean optionExists( Option option ) {
	return optionExists( option.getShortOption() ) ||
	       optionExists( option.getLongOption() );
    }

    

    public boolean optionExists( char shortOption ) {
	Collection col = options.values();
	Iterator it = col.iterator();
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    char c = next.getShortOption();
	    if ( c != 0 && c == shortOption ) return true;
	}
	return false;
    }

    

    public boolean optionExists( String longOption ) {
	Collection col = options.values();
	Iterator it = col.iterator();
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    String s = next.getLongOption();
	    if ( s != null && s.equals( longOption ) ) return true;
	}
	return false;
    }

    

    public Iterator getOptionIterator() {
	return options.values().iterator();
    }

    

    public Option getOption( char shortOption ) {
	Option retval = null;
	Collection col = options.values();
	Iterator it = col.iterator();
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    char c = next.getShortOption();
	    if ( c != '\0' && c == shortOption ) retval = next;
	}
	return retval;
    }

    

    public Option getOption( String longOption ) {
	Option retval = null;
	Collection col = options.values();
	Iterator it = col.iterator();
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    String s = next.getLongOption();
	    if ( s != null && s.equals( longOption ) ) retval = next;
	}
	return retval;
    }

    

    public String getHelp() {
	String retval = "";
	Collection col = options.values();
	Iterator it = col.iterator();
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    retval += next.getHelp() + "\n";
	}
	return retval;
    }

    

    public void writeFileToPrintStream( PrintStream ps ) {
	Collection col = options.values();
	Iterator it = col.iterator();
	ps.println( ":" + name + ":" );
	while ( it.hasNext() ) {
	    Option next = (Option)it.next();
	    ps.println( next.getOptionFileLine() );
	}
    }

    

    public boolean isDeprecated() {
	return deprecated;
    }

    

    public void setDeprecated( boolean deprecated ) {
	this.deprecated = deprecated;
    }

    

    public void action( char shortOption, char text ) {
	action( shortOption, "" + text );
    }

    

    public void action( String longOption, char text ) {
	action( longOption, "" + text );
    }

    

    public void action( char shortOption, String text ) {
	Option op = getOption( shortOption );
	if ( op == null )
	    throw new OptionProcessingException( "Option -" + shortOption +
						 " does not"
						 + " exist in module '"
						 + name + "'." );
	op.setInvoked( true );
	op.action();
	op.modify( text );
    }

    


    public void action( String longOption, String text ) {
	Option op = getOption( longOption );
	if ( op == null )
	    throw new OptionProcessingException( "Option --" + longOption +
						 " does not"
						 + " exist in module '"
						 + name + "'." );
	op.setInvoked( true );
	op.action();
	op.modify( text );
    }

    

    public void setName( String name ) {
	this.name = name;
    }

    

    public String getName() {
	return name;
    }

} 




