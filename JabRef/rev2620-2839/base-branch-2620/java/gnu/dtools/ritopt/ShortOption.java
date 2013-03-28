package gnu.dtools.ritopt;





public class ShortOption extends Option {

    

    private short value;

    

    public ShortOption() {
	this( (short)0 );
    }

    

    public ShortOption( ShortOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public ShortOption( short value ) {
	this( value, null );
    }

    

    public ShortOption( short value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public ShortOption( short value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public ShortOption( short value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Short( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Short.parseShort( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. A short must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( short value ) {
	this.value = value;
    }

    

    public short getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Short.toString( value );
    }

    

    public String getTypeName() {
	return "SHORT";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
