
package net.sourceforge.pmd.lang.rule.properties;


public class LongMultiProperty extends AbstractMultiNumericProperty<Long[]> {

	
	public LongMultiProperty(String theName, String theDescription, Long min, Long max, Long[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
	}
	
	
	public Class<Long[]> type() {
		return Long[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Long.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Long[size];
	}
}
