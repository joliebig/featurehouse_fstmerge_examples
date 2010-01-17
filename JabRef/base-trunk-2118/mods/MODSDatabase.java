
package net.sf.jabref.mods;
import net.sf.jabref.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class MODSDatabase {
	protected Set entries;
	
	public MODSDatabase() {
		
		entries = new HashSet();
	}
	
	public MODSDatabase(BibtexDatabase bibtex) {
		Set keySet = bibtex.getKeySet();
        addEntries(bibtex, keySet);
    }

    public MODSDatabase(BibtexDatabase bibtex, Set keySet) {
        if (keySet == null)
            keySet = bibtex.getKeySet();
        addEntries(bibtex, keySet);
    }


    private void addEntries(BibtexDatabase database, Set keySet) {
        entries = new HashSet();
        for(Iterator iter = keySet.iterator(); iter.hasNext(); ) {
			BibtexEntry entry = database.getEntryById((String)iter.next());
			MODSEntry newMods = new MODSEntry(entry);
			entries.add(newMods);
		}
	}
	public Document getDOMrepresentation() {
		Document result = null;
	   	try {
	   		DocumentBuilder dbuild = DocumentBuilderFactory.
														newInstance().
														newDocumentBuilder();
	   		result = dbuild.newDocument();
	   		Element modsCollection = result.createElement("modsCollection");
	   		modsCollection.setAttribute("xmlns", "http://www.loc.gov/mods/v3");
	   		modsCollection.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	   		modsCollection.setAttribute("xsi:schemaLocation", "http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-0.xsd");
	   		
	   		for(Iterator iter = entries.iterator(); iter.hasNext(); ) {
	   			MODSEntry entry = (MODSEntry) iter.next();
	   			Node node = entry.getDOMrepresentation(result);
	   			modsCollection.appendChild(node);
	   		}
	   		
	   		result.appendChild(modsCollection);	   		
	   	}
	   	catch (Exception e)
		{
	   		System.out.println("Exception caught..." + e);
	   		e.printStackTrace();
		}
	   	return result;
	   }
}
