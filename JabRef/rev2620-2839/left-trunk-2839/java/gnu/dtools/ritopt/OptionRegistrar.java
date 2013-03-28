package gnu.dtools.ritopt;



public interface OptionRegistrar {

    

    public void register( String longName, Option option );

    

    public void register( char shortName, Option option );


    

    public void register( String longOption, char shortOption,
			  Option option );

    

    public void register( String longOption, char shortOption,
			  String description, Option option );

    

    public void register( String longOption, char shortOption,
			  String description, Option option,
			  boolean deprecated );
}
