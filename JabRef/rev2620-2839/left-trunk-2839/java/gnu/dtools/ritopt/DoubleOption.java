package gnu.dtools.ritopt;





public class DoubleOption extends Option {

    

    private double value;

    

    public DoubleOption() {
	this( 0.0 );
    }

    

    public DoubleOption( DoubleOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public DoubleOption( double value ) {
	this( value, null );
    }

    

    public DoubleOption( double value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public DoubleOption( double value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public DoubleOption( double value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Double( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	try {
	    this.value = Double.parseDouble( value );
	}
	catch ( NumberFormatException e ) {
	    throw new OptionModificationException( "Error. A double must be"
						   + " specified, not '"
						   + value + "'." );
	}
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( double value ) {
	this.value = value;
    }

    

    public double getValue() {
	return value;
    }

    

    public String getStringValue() {
	return Double.toString( value );
    }

    

    public String getTypeName() {
	return "DOUBLE";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
