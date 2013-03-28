package gnu.dtools.ritopt;





public class StringOption extends Option {

    

    private String value = "";

    

    public StringOption() {
	this( "" );
    }

    

    public StringOption( StringOption op ) {
	super( op );
	op.value = op.getValue();
    }

    

    public StringOption( String value ) {
	this( value, null );
    }

    

    public StringOption( String value, String longOption ) {
	this( value, longOption, '\0' );
    }

    

    public StringOption( String value, char shortOption ) {
	this( value, null, shortOption );
    }

    

    public StringOption( String value, String longOption, char shortOption ) {
	super( longOption, shortOption );
	this.value = value;
    }

    

    public Object getObject() {
	return value;
    }

    

    public void modify( String value ) throws OptionModificationException {
	this.value = value;
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
	return "STRING";
    }

    

    public String toString() {
	return value;
    }

}
