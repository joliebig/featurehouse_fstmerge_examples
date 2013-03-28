package net.sourceforge.squirrel_sql.fw.util;



import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.lang.StringBuffer;

public class ExtensionFilter extends FileFilter {
    
    
    private ArrayList<String> _descriptions;
    private ArrayList<String> _extensions;
    
    
    public ExtensionFilter() {
        _descriptions = new ArrayList<String>();
        _extensions = new ArrayList<String>();
    }
    
    public void addExtension(String description, String extension) {
        _descriptions.add(description);
        _extensions.add(extension);
    }
    
    public boolean accept(java.io.File f) {
        if (f.isDirectory())
            return true;
        for (int i = 0; i < _extensions.size(); i++) {
            String ext = _extensions.get(i);
            if (f.getName().endsWith("." + ext))
                return true;
        }
        return false;
    }
    
    public String getDescription() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _extensions.size(); i++) {
            buf.append(_descriptions.get(i));
            buf.append(" (*.");
            buf.append(_extensions.get(i));
            buf.append(")");
            if (i < _extensions.size() - 1)
                buf.append("; ");
        }
        return buf.toString();
    }
    
}
