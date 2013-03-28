package net.sourceforge.pmd.lang.rule.properties;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class AbstractPackagedProperty<T> extends AbstractProperty<T> {

	private String[] legalPackageNames;

	private static final char PACKAGE_NAME_DELIMITER = ' ';
	
	
	protected AbstractPackagedProperty(String theName, String theDescription, T theDefault, String[] theLegalPackageNames, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
		
		checkValidPackages(theDefault, theLegalPackageNames);
		
		legalPackageNames = theLegalPackageNames;
	}
	
    
    protected void addAttributesTo(Map<String, String> attributes) {
        super.addAttributesTo(attributes);
        
        attributes.put("legalPackageNames", delimitedPackageNames());
    }
	
    
    private final String delimitedPackageNames() {
        
        if (legalPackageNames == null || legalPackageNames.length == 0) { return ""; }
        if (legalPackageNames.length == 1) { return legalPackageNames[0];  }
        
        StringBuilder sb = new StringBuilder();
        sb.append(legalPackageNames[0]);
        for (int i=1; i<legalPackageNames.length; i++) {
            sb.append(PACKAGE_NAME_DELIMITER).append(legalPackageNames[i]);
        }
        return sb.toString();
    }
    
	
	private void checkValidPackages(Object item, String[] legalNamePrefixes) {
	    Object[] items;
	    if (item.getClass().isArray()) {
		items = (Object[])item;
	    } else{
		items = new Object[]{item};
	    }
		
		String[] names = new String[items.length];
		Set<String> nameSet = new HashSet<String>(items.length);
		String name = null;
		
		for (int i=0; i<items.length; i++) {
			name = packageNameOf(items[i]);
			names[i] = name;
			nameSet.add(name);
		}

		for (int i=0; i<names.length; i++) {
			for (int l=0; l<legalNamePrefixes.length; l++) {
				if (names[i].startsWith(legalNamePrefixes[l])) {
					nameSet.remove(names[i]);
					break;
				}
			}
		}
		if (nameSet.isEmpty()) { return; }
		
		throw new IllegalArgumentException("Invalid items: " + nameSet);
	}
	
	
	abstract protected String itemTypeName();
	
	
	protected String valueErrorFor(Object value) {
		
		if (value == null) {
			String err = super.valueErrorFor(null);
			if (err != null) { return err; }
			}
		
		if (legalPackageNames == null) {
			return null;	
		}
		
		String name = packageNameOf(value);
		
		for (int i=0; i<legalPackageNames.length; i++) {
			if (name.startsWith(legalPackageNames[i])) {
				return null;
			}
		}
		
		return "Disallowed " + itemTypeName() + ": " + name;
	}
	
	
	abstract protected String packageNameOf(Object item);
	
	
	public String[] legalPackageNames() {
		return legalPackageNames;
	}
	
}
