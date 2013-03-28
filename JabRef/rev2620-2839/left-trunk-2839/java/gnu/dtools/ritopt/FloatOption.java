package gnu.dtools.ritopt;





public class FloatOption extends Option {

    

    private float value;

    

    public FloatOption() {
	this( 0.0f );
    }

    

    public FloatOption( FloatOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public FloatOption( float value ) {
	this( value, null );
    }

    

    public FloatOption( float value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public FloatOption( float value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public FloatOption( float value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Float( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Float.parseFloat( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. A float must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( float value ) {
	this.value = value;
    }

    

    public float getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Float.toString( value );
    }

    

    public String getTypeName() {
	return "FLOAT";
    }

    

    public String toString() {
	return getStringValue();
    }

}
