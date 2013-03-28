package gnu.dtools.ritopt;





public class IntOption extends Option {

    

    private int value;

    

    public IntOption() {
	this( 0 );
    }

    

    public IntOption( IntOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public IntOption( int value ) {
	this( value, null );
    }

    

    public IntOption( int value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public IntOption( int value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public IntOption( int value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Integer( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Integer.parseInt( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. An integer must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( int value ) {
	this.value = value;
    }

    

    public int getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Integer.toString( value );
    }

    

    public String getTypeName() {
	return "INTEGER";
    }

    

    public String toString() {
	return getStringValue();
    }

}
