package gnu.dtools.ritopt;



public interface OptionArrayable extends OptionModifiable {

   

    void modify( String value[] ) throws OptionModificationException;
}
