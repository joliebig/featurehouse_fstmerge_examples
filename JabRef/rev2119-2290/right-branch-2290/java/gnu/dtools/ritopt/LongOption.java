package gnu.dtools.ritopt;





public class LongOption extends Option {

    

    private long value;

    

    public LongOption() {
	this( (long)0 );
    }

    

    public LongOption( LongOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public LongOption( long value ) {
	this( value, null );
    }

    

    public LongOption( long value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public LongOption( long value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public LongOption( long value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Long( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Long.parseLong( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. A long must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( long value ) {
	this.value = value;
    }

    

    public long getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Long.toString( value );
    }

    

    public String getTypeName() {
	return "LONG";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
