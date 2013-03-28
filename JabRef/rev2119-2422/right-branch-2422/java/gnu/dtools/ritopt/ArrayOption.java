package gnu.dtools.ritopt;





public abstract class ArrayOption extends Option implements OptionArrayable {

    

    public ArrayOption() {
	super();
    }

    

    public abstract Object[] getObjectArray();

    

    public java.util.List getObjectList() {
	return java.util.Arrays.asList( getObjectArray() );
    }
} 

