
package genj.plugin.sosa;

import genj.option.OptionProvider;
import genj.option.PropertyOption;

import java.util.List;


public class SosaOptions extends OptionProvider {

	
	private static SosaOptions instance = new SosaOptions();

	
	public boolean isExtendSosaIndexation=true;

	
	public List getOptions() {

		
		return PropertyOption.introspect(getInstance());
	}

	
	public static SosaOptions getInstance() {
		return instance;
	}

} 
