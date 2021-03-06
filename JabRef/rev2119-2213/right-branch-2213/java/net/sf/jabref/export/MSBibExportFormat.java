package net.sf.jabref.export;

import net.sf.jabref.Globals;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.msbib.*;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import java.util.Set;
import java.io.IOException;
import java.io.File;



class MSBibExportFormat extends ExportFormat {
    public MSBibExportFormat() {
        super(Globals.lang("MS Office 2007"), "MSBib", null, null, ".xml");

    }

    public void performExport(final BibtexDatabase database, final String file, final String encoding, Set keySet) throws IOException {
    	
    	
        SaveSession ss = getSaveSession("UTF8", new File(file));
        VerifyingWriter ps = ss.getWriter();
        MSBibDatabase md = new MSBibDatabase(database, keySet);

        

        try {
            DOMSource source = new DOMSource(md.getDOMrepresentation());
            StreamResult result = new StreamResult(ps);
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(source, result);
        }
        catch (Exception e) {
            throw new Error(e);
        }

        try {
            finalizeSaveSession(ss);
        } catch (SaveException ex) {
            throw new IOException(ex.getMessage());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return;
    }
}
