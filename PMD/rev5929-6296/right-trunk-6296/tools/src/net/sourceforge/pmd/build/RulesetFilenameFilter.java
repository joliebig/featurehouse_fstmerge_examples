
package net.sourceforge.pmd.build;

import java.io.File;
import java.io.FilenameFilter;


public class RulesetFilenameFilter implements FilenameFilter {

	
	public boolean accept(File file, String name) {
	    if ( ! name.startsWith("migrating_") && ! name.startsWith("scratchpad") && ! name.startsWith("Favorites") )
	    	return (name.endsWith(".xml"));
	    else
		return false;
	}

}
