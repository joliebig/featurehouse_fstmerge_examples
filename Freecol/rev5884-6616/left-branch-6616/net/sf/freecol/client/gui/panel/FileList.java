


package net.sf.freecol.client.gui.panel;

import java.io.File;
import java.io.FileFilter;

import javax.swing.JList;



public class FileList extends JList {

    
    public FileList(File directory) {        
        super();       
        setListData(getEntries(directory, getDefaultFileFilter()));        
    }
    
    
        
    public FileList(File directory, FileFilter fileFilter) {
        super();        
        setListData(getEntries(directory, fileFilter));
    }
    
    
    private FileListEntry[] getEntries(File directory, FileFilter fileFilter) {
        if (directory == null) {
            throw new NullPointerException();
        }
        
        if (fileFilter == null) {
            throw new NullPointerException();
        }
                
        File[] files = directory.listFiles(fileFilter);
        FileListEntry[] fileListEntries;
        if (files != null) {
            fileListEntries = new FileListEntry[files.length];
        } else {
            fileListEntries = new FileListEntry[0];
        }
        
        for (int i=0; i<fileListEntries.length; i++) {
            fileListEntries[i] = new FileListEntry(files[i]);
        }
        
        return fileListEntries;
    }

    
    
    public FileFilter getDefaultFileFilter() {
        FileFilter ff = new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                return (name.length() >= 4 && name.substring(name.length()-4).equals(".fsg"));
            }
        };
        
        return ff;
    }

        
    
    public class FileListEntry {
        private File file;
        
        public FileListEntry(File file) {
            this.file = file;
        }
        
        
                
        public String toString() {
            String name = file.getName();
            return name.substring(0, name.length()-4);
        }
        
        
        
        public File getFile() {
            return file;
        }
    }
}
