package gnu.dtools.ritopt;





public interface OptionModuleRegistrar {

    

    public void register( OptionModule module );

    

    public void register( String name, OptionModule module );
}
