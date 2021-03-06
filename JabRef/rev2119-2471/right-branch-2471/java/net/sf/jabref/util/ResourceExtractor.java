

package net.sf.jabref.util;

import java.awt.Component;
import java.net.URL;
import java.io.*;
import net.sf.jabref.*;
import net.sf.jabref.net.URLDownload;

public class ResourceExtractor implements Worker {
    
    final URL resource;
    final Component parent;
    final File destination;
    
    
    public ResourceExtractor(final Component parent, final String filename, File destination) {
         resource = JabRef.class.getResource(filename);
         
         this.parent = parent;
         this.destination = destination;
    }
    
    public void run() {
        URLDownload ud = new URLDownload(parent, resource, destination);
        try {
            ud.download();
        } catch (IOException ex) {
            Globals.logger("Error extracting resource: "+ex.getMessage());            
        }
    }
}
