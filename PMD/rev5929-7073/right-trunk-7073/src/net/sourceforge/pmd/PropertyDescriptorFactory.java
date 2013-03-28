package net.sourceforge.pmd;

import java.util.Map;


public interface PropertyDescriptorFactory {

	Class<?> valueType();
	
	Map<String, Boolean> expectedFields();
	
	PropertyDescriptor<?> createWith(Map<String, String> valuesById);
}
