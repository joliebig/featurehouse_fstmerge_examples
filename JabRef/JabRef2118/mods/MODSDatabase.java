
package net.sf.jabref.mods; 
import net.sf.jabref.*; 
import java.util.*; 
import javax.xml.parsers.*; 
import org.w3c.dom.*; 
import java.util.HashSet; 
import java.util.Iterator; 
import java.util.Set; 

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 

import net.sf.jabref.BibtexDatabase; 
import net.sf.jabref.BibtexEntry; 

import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 


public  class  MODSDatabase {
	
	

	
	
	public MODSDatabase() {
		
		entries = new HashSet<MODSEntry>();
	}


	
	
	<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_72841
public MODSDatabase() {
		
		entries = new HashSet<MODSEntry>();
	}
=======
public MODSDatabase(BibtexDatabase bibtex) {
		Set keySet = bibtex.getKeySet();
        addEntries(bibtex, keySet);
    }
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_72843


	
	
	<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_72844
public MODSDatabase() {
		
		entries = new HashSet<MODSEntry>();
	}
=======
public MODSDatabase(BibtexDatabase bibtex, Set keySet) {
        if (keySet == null)
            keySet = bibtex.getKeySet();
        addEntries(bibtex, keySet);
    }
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_72846


	


    


	
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
	   		
	   		for(Iterator<MODSEntry> iter = entries.iterator(); iter.hasNext(); ) {
	   			MODSEntry entry = iter.next();
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


	
	protected Set<MODSEntry> entries;

	

    private void addEntries(BibtexDatabase database, Set<String> keySet) {
        entries = new HashSet<MODSEntry>();
        for(Iterator<String> iter = keySet.iterator(); iter.hasNext(); ) {
			BibtexEntry entry = database.getEntryById(iter.next());
			MODSEntry newMods = new MODSEntry(entry);
			entries.add(newMods);
		}
	}


}
