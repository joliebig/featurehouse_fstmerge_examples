package gnu.dtools.ritopt;

import java.util.List;





public abstract class ArrayOption extends Option implements OptionArrayable {

	

	public ArrayOption() {
		super();
	}

	

	public abstract Object[] getObjectArray();

	

	public List<Object> getObjectList() {
		return java.util.Arrays.asList(getObjectArray());
	}

}
