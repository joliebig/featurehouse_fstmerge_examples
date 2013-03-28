package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.LayoutFormatter;
import net.sf.jabref.gui.FileListTableModel;
import net.sf.jabref.Globals;
import net.sf.jabref.Util;
import net.sf.jabref.GUIGlobals;

import java.io.File;


public class FileLink implements LayoutFormatter {


    public String format(String field) {
        FileListTableModel tableModel = new FileListTableModel();
        if (field == null)
            return "";
        tableModel.setContent(field);
        String link;
        if (tableModel.getRowCount() > 0)
            link = tableModel.getEntry(0).getLink();
        else
            link = null;

        if (link == null)
            return "";
        
        
        String dir = Globals.prefs.get(GUIGlobals.FILE_FIELD+"Directory");
		File f = Util.expandFilename(link, new String[] { dir, "." });

        
		if (f != null) {
			return f.toURI().toString();
		} else {
			return field;
		}
    }
}
