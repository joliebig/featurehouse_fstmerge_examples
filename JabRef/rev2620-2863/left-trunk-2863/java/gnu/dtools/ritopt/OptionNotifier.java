package gnu.dtools.ritopt;



public interface OptionNotifier {

    

    public void addOptionListener( OptionListener listener );

    

    public void removeOptionListener( OptionListener listener );

    

    public void setOptionCommand( String command );

}
