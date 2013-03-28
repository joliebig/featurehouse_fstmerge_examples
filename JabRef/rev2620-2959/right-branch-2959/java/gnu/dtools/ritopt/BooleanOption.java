package gnu.dtools.ritopt;





public class BooleanOption extends Option {

    

    private boolean value;

    

    public BooleanOption() {
	this( false );
    }

    

    public BooleanOption( BooleanOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public BooleanOption( boolean value ) {
	this( value, null );
    }

    

    public BooleanOption( boolean value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public BooleanOption( boolean value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public BooleanOption( boolean value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return Boolean.valueOf(value);
    }

    

    public void modify( String value ) throws OptionModificationException {
	String val = value.toUpperCase();
	this.value = false;
	if ( val.equals( "+" ) || val.equals( "TRUE" ) ||
	     val.equals( "YES" ) || val.equals( "ON" ) ||
	     val.equals( "ACTIVATED" ) || val.equals( "ACTIVE" ) ) {
	    this.value = true;
	}
	else if ( val.equals( "-" ) || val.equals( "FALSE" ) ||
		  val.equals( "NO" ) || val.equals( "OFF" ) ||
		  val.equals( "NOT ACTIVATED" ) ||
		  val.equals( "INACTIVE" ) ) {
	    this.value = false;
	}
	else {
	    throw new OptionModificationException( "Error. A boolean value of\n+/-/true/false/yes/no/on/off/activated/not activated/active/inactive must be\nspecified, not '" + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( boolean value ) {
	this.value = value;
    }

    

    public boolean getValue() {
	return value;
    }

    

    public String getStringValue() {
	return ( value ) ? "TRUE" : "FALSE";
    }

    

    public String getTypeName() {
	return "BOOLEAN";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
