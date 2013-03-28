package gnu.dtools.ritopt;





public class ByteOption extends Option {

    

    private byte value;

    

    public ByteOption() {
	this( (byte)0 );
    }

    

    public ByteOption( ByteOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public ByteOption( byte value ) {
	this( value, null );
    }

    

    public ByteOption( byte value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public ByteOption( byte value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public ByteOption( byte value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Byte( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Byte.parseByte( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. A byte must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( byte value ) {
	this.value = value;
    }

    

    public byte getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Byte.toString( value );
    }

    

    public String getTypeName() {
	return "BYTE";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
