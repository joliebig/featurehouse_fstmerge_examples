package gnu.dtools.ritopt;



public class NotifyOption extends Option implements OptionNotifier {

    

    private String value = "";

    

    private String command = "Default";

    

    private java.util.List<OptionListener> listeners;

    

    public NotifyOption() {
	this( "" );
    }

    

    public NotifyOption( OptionListener listener ) {
	this( listener, "Default" );
    }

    

    public NotifyOption( OptionListener listener, String command ) {
	this( listener, command, "" );
    }

    

    public NotifyOption( OptionListener listener, String command,
			 String value ) {
	this( value );
	this.command = command;
	listeners.add( listener );
    }

    

    public NotifyOption( NotifyOption op ) {
	super( op );
	op.value = op.getValue();
	listeners = new java.util.ArrayList<OptionListener>( op.listeners );
    }

    

    public NotifyOption( String value ) {
	this( value, null );
    }

    

    public NotifyOption( String value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public NotifyOption( String value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public NotifyOption( String value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
	listeners = new java.util.ArrayList<OptionListener>();
    }

    

    public Object getObject() {
	return value;
    }

    

    public void modify( String value ) throws OptionModificationException {
	this.value = value;
	java.util.Iterator<OptionListener> iterator = listeners.iterator();
	OptionEvent event = new OptionEvent( command, value, this );
	while ( iterator.hasNext() ) {
	    OptionListener listener = iterator.next();
	    listener.optionInvoked( event );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public String getValue() {
	return value;
    }

    

    public String getStringValue() {
	return value;
    }

    

    public String getTypeName() {
	return "NOTIFY";
    }

    

    public void addOptionListener( OptionListener listener ) {
	listeners.add( listener );
    }

    

    public void removeOptionListener( OptionListener listener ) {
	listeners.remove( listener );
    }

    

    public void setOptionCommand( String command ) {
	this.command = command;
    }

    

    public String toString() {
	return value;
    }
} 
