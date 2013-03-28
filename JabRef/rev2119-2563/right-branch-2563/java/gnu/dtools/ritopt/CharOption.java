package gnu.dtools.ritopt;





public class CharOption extends Option {

    

    private char value;

    

    public CharOption() {
	this( ' ' );
    }

    

    public CharOption( CharOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public CharOption( char value ) {
	this( value, null );
    }

    

    public CharOption( char value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public CharOption( char value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public CharOption( char value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return new Character( value );
    }

    

    public void modify( String value ) throws OptionModificationException {
	this.value = ( value.length() > 0 ) ? value.charAt( 0 ) : ' ';
    }

    

    public void setValue( String value ) throws OptionModificationException {
	modify( value );
    }

    

    public void setValue( char value ) {
	this.value = value;
    }

    

    public char getValue() {
	return value;
    }

    

    public String getStringValue() {
	return "" + value;
    }

    

    public String getTypeName() {
	return "CHAR";
    }

    

    public String toString() {
	return getStringValue();
    }

} 
