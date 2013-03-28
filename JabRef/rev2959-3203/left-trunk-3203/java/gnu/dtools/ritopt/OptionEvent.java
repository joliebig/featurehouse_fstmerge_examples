package gnu.dtools.ritopt;




public class OptionEvent {

    

    private String command;

    

    private String value;

    

    private Option target;

    

    public OptionEvent() {
	this( "Default", "", null );
    }

    

    public OptionEvent( String command ) {
	this( command, "", null );
    }

    

    public OptionEvent( String command, String value ) {
	this( command, value, null );
    }

    

    public OptionEvent( Option option ) {
	this.target = option;
	this.value = option.getStringValue();
	String longOption = option.getLongOption();
	char shortOption = option.getShortOption();
						      
        if ( longOption != null ) {
	    command = longOption;
	}
	else if ( shortOption != '\0' ) {
	    command = new Character( shortOption ).toString();
	}
	else {
	    command = "Default";
	}
    }

    

    public OptionEvent( String command, String value, Option target ) {
	this.command = command;
	this.value = value;
	this.target = target;
    }

    

    public String getCommand() {
	return command;
    }

    

    public String getValue() {
	return value;
    }

    

    public Option getTarget() {
	return target;
    }

    

    public void setCommand( String command ) {
	this.command = command;
    }

    

    public void setValue( String value ) {
	this.value = value;
    }

    

    public void setTarget( Option target ) {
	this.target = target;
    }

}
