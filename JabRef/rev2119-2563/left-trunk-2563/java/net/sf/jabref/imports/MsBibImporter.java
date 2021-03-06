package net.sf.jabref.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.msbib.MSBibDatabase;

import org.w3c.dom.Document;



public class MsBibImporter extends ImportFormat {

    public boolean isRecognizedFormat(InputStream in) throws IOException {

        
    	Document docin = null;
    	try {
    	DocumentBuilder dbuild = DocumentBuilderFactory.
    								newInstance().
    								newDocumentBuilder();
   		docin = dbuild.parse(in);   		
    	} catch (Exception e) {
	   		return false;
    	}
    	if(docin!= null && docin.getDocumentElement().getTagName().contains("Sources") == false)
    		return false;





    	
        return true;
    }

    
	public String getCLIid() {
		return "msbib";
	}

    public List<BibtexEntry> importEntries(InputStream in) throws IOException {

        MSBibDatabase dbase = new MSBibDatabase();

        List<BibtexEntry> entries = dbase.importEntries(in);

        return entries;
    }

    public String getFormatName() {
        
        return "MSBib";
    }

}
