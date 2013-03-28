package gnu.dtools.ritopt;





public class OptionRegistrationException extends OptionException {

    

    private Option target;

    


    public OptionRegistrationException( String msg ) {
	super( msg );
    }

    

    public OptionRegistrationException( String msg, Option target ) {
	super( msg );
	this.target = target;
    }

    

    public Option getTarget() {
	return target;
    }


}
