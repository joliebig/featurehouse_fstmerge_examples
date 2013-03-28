package net.sourceforge.pmd.lang.rule.properties;

import java.util.HashSet;
import java.util.Set;


public abstract class AbstractPackagedProperty extends AbstractProperty {

	private String[] legalPackageNames;

	
	protected AbstractPackagedProperty(String theName, String theDescription, Object theDefault, String[] theLegalPackageNames, float theUIOrder) {
		this(theName, theDescription, new Object[] {theDefault}, theLegalPackageNames, theUIOrder);
		
	}

	
	protected AbstractPackagedProperty(String theName, String theDescription, Object[] theDefaults, String[] theLegalPackageNames, float theUIOrder) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		checkValidPackages(theDefaults, theLegalPackageNames);
		
		legalPackageNames = theLegalPackageNames;
	}
	
	
	private void checkValidPackages(Object[] items, String[] legalNamePrefixes) {
		
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
