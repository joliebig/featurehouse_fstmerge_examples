package gnu.dtools.ritopt;



import java.util.*;



public abstract class Option implements OptionModifiable {

    

    public static final int DEFAULT_HELP_OPTION_SIZE = 22;

    

    public static final int DEFAULT_HELP_TYPENAME_SIZE = 10;

    

    public static final int DEFAULT_HELP_DESCRIPTION_SIZE = 48;

    

    public static final int DEFAULT_HELP_DEPRECATED_SIZE = 3;

    

    public static final int DEFAULT_MENU_OPTION_SIZE = 15;

    

    public static final int DEFAULT_MENU_TYPENAME_SIZE = 10;

    

    public static final int DEFAULT_MENU_DESCRIPTION_SIZE = 48;

    

    public static final int DEFAULT_MENU_DEPRECATED_SIZE = 3;

    

    public static final int DEFAULT_FILE_COMPLETE_OPTION_SIZE = 60;

    

    public static final int DEFAULT_FILE_COMMENT_SIZE = 16;

    

    private String longOption;

    

    private char shortOption;

    

    private String description;

    

    private boolean deprecated;

    

    private static int helpOptionSpecificationSize = DEFAULT_HELP_OPTION_SIZE;

    

    private static int helpTypenameSize = DEFAULT_HELP_TYPENAME_SIZE;

    

    private static int helpDescriptionSize = DEFAULT_HELP_DESCRIPTION_SIZE;

    

    private static int helpDeprecatedSize = DEFAULT_HELP_DEPRECATED_SIZE;

    

    private static int menuOptionSpecificationSize = DEFAULT_MENU_OPTION_SIZE;

    

    private static int menuTypenameSize = DEFAULT_MENU_TYPENAME_SIZE;

    

    private static int menuDescriptionSize = DEFAULT_MENU_DESCRIPTION_SIZE;

    

    private static int menuDeprecatedSize = DEFAULT_MENU_DEPRECATED_SIZE;

    

    private static int fileCompleteOptionSize =
                                            DEFAULT_FILE_COMPLETE_OPTION_SIZE;

    

    private static int fileCommentSize = DEFAULT_FILE_COMMENT_SIZE;

    

    protected boolean invoked;

    

    public abstract Object getObject();

    

    public abstract String getStringValue();

    

    public Option() {
	super();
	description = "";
    }

    

    public Option( Option option ) {
	longOption = option.getLongOption();
	shortOption = option.getShortOption();
	description = option.getDescription();
	deprecated = option.isDeprecated();
    }

    

    public Option( String longOption ) {
	this.longOption = longOption;
	this.shortOption = '\0';
	description = "";
    }

    

    public Option( char shortOption ) {
	this.shortOption = shortOption;
	this.longOption = null;
	description = "";
    }

    

    public Option( String longOption, char shortOption ) {
	this.longOption = longOption;
	this.shortOption = shortOption;
	description = "";
    }

    

    public void setKey( String longOption ) {
	this.longOption = longOption;
    }

    

    public void setKey( char shortOption ) {
	this.shortOption = shortOption;
    }

    

    public void setShortOption( char shortOption ) {
	setKey( shortOption );
    }

    

    public void setLongOption( String longOption ) {
	setKey( longOption );
    }

    

    public void setDescription( String description ) {
	this.description = description;
    }

    

    public void setDeprecated( boolean deprecated ) {
	this.deprecated = deprecated;
    }

    

    public static void setHelpOptionSpecificationSize( int newSize ) {
	helpOptionSpecificationSize = newSize;
    }

    

    public static void setHelpTypenameSize( int newSize ) {
	helpTypenameSize = newSize;
    }

    

    public static void setHelpDescriptionSize( int newSize ) {
	helpDescriptionSize = newSize;
    }

    

    public static void setHelpDeprecatedSize( int newSize ) {
	helpDeprecatedSize = newSize;
    }

    

    public static void setMenuOptionSpecificationSize( int newSize ) {
	menuOptionSpecificationSize = newSize;
    }

    

    public static void setMenuTypenameSize( int newSize ) {
	menuTypenameSize = newSize;
    }

    

    public static void setMenuDescriptionSize( int newSize ) {
	menuDescriptionSize = newSize;
    }

    

    public static void setMenuDeprecatedSize( int newSize ) {
	menuDeprecatedSize = newSize;
    }

    

    public static void setFileCompleteOptionSize( int newSize ) {
	fileCompleteOptionSize = newSize;
    }

    

    public static void setFileCommentSize( int newSize ) {
	fileCommentSize = newSize;
    }

    

    public void setInvoked( boolean b ) {
	invoked = b;
    }

    

    public void deprecate() {
	setDeprecated( true );
    }

    

    public String getName() {
	return longOption;
    }

    

    public char getShortOption() {
	return shortOption;
    }

    

    public String getLongOption() {
	return longOption;
    }


    

    public String getHelp() {
	return getHelpOptionSpecification() + " " + getHelpTypeName() + " "
	    + getHelpDescription() + " " + getHelpDeprecated();
    }

    

    public String getHelpOptionSpecification() {
	return Utility.expandString(
	    ( ( ( shortOption != '\0' ) ? ( "-" + getShortOption() ) : "  " )
	    + ( ( longOption != null && shortOption != '\0' ) ? ", " : "  " )
	    + ( ( longOption != null ) ? "--" + getLongOption(): "" ) ),
			     helpOptionSpecificationSize );
    }

    

    public String getHelpTypeName() {
	return Utility.expandString( "<" + getTypeName() + ">",
				     helpTypenameSize );
    }

    

    public String getHelpDescription() {
	return Utility.expandString( getDescription(),
			     helpDescriptionSize );
    }

    

    public String getHelpDeprecated() {
	return Utility.expandString( isDeprecated() ? "[d]" : "",
			     helpDeprecatedSize );
    }

    

    public static String getHelpHeader() {
	return Utility.expandString( "Option Name",
				     helpOptionSpecificationSize ) + " "
	    + Utility.expandString( "Type", helpTypenameSize ) + " "
	    + Utility.expandString( "Description", helpDescriptionSize );
    }

    

    public String getDescription() {
	return description;
    }

    

    public String getHashKey() {
	return Option.getHashKey( longOption, shortOption );
    }

    

    public static String getHashKey( String longOption ) {
	return "," + ( ( longOption != null ) ? longOption : "" );
    }

    

    public static String getHashKey( char shortOption ) {
	return "" + ( shortOption != '\0' ) + ",";
    }

    

    public static String getHashKey( String longOption, char shortOption ) {
	return ( ( shortOption == '\0' ) ? "" : "" + shortOption ) +
	    ( ( longOption == null ) ? "," : "," + longOption );
    }

    

    public boolean isDeprecated() {
	return deprecated;
    }

    

    public boolean isInvoked() {
	return invoked;
    }

    

    public String getOptionFileLine() {
	boolean descriptionPrinted = false;
	String retval = "";
	String optionText = "";
	String strval = getStringValue();
	if ( longOption != null ) {
	    optionText += "--" + longOption;
	}
	else if ( shortOption != '\0' ) {
	    optionText += "-" + shortOption;
	}
	if ( optionText.length() > 0
	     && Utility.trim( strval ).length() >= 0 ) {
	    optionText += "=" + strval;
	}
	if ( optionText.length() <= fileCompleteOptionSize ) {
	    retval += Utility.expandString( optionText,
					    fileCompleteOptionSize );
	}
	else {
	    retval += "; " + description + "\n";
	    retval += optionText;
	    descriptionPrinted = true;
	}
	if ( !descriptionPrinted ) { 
	    StringBuffer descsplit = new StringBuffer( description );
	    boolean tmp = false;
	    while ( descsplit.length() > 0 ) {
		String st = "";
		int size = 0;
		if ( tmp ) {
		    st += Utility.getSpaces( fileCompleteOptionSize );
		}
		size = ( descsplit.length() >= fileCommentSize )
		    ? fileCommentSize : descsplit.length();
		st += "; " + descsplit.substring( 0, size );
		descsplit.delete( 0, size );
		retval += st + "\n";
		tmp = true;
	    }
	    descriptionPrinted = true;
	}
	return retval;
    }

    

    public static int getHelpOptionSpecificationSize() {
	return helpOptionSpecificationSize;
    }

    

    public static int getHelpTypenameSize() {
	return helpTypenameSize;
    }

    

    public static int getHelpDescriptionSize() {
	return helpDescriptionSize;
    }

    

    public static int getHelpDeprecatedSize() {
	return helpDeprecatedSize;
    }

    

    public static int getMenuOptionSpecificationSize() {
	return menuOptionSpecificationSize;
    }

    

    public static int getMenuTypenameSize() {
	return menuTypenameSize;
    }

    

    public static int getMenuDescriptionSize() {
	return menuDescriptionSize;
    }

    

    public static int getMenuDeprecatedSize() {
	return menuDeprecatedSize;
    }

    

    public static int getFileCompleteOptionSize() {
	return fileCompleteOptionSize;
    }

    

    public static int getFileCommentSize() {
	return fileCommentSize;
    }

    

    public abstract String getTypeName();

    

    public void action() {
	if ( deprecated ) {
	    System.err.print( "Warning: " );
	    if ( longOption != null ) {
		System.err.print( "--" + longOption );
	    }
	    if ( shortOption != '\0' && longOption != null ) {
		System.err.print( " or " );
	    }
	    if ( shortOption != '\0' ) {
		System.err.println( "-" + shortOption + " is deprecated." );
	    }
	}
    }

} 





