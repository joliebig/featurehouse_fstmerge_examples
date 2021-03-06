
package net.sf.jabref.imports;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.TreeSet;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;


public class CustomImportList extends TreeSet {

  
  public class Importer implements Comparable {
    
    private String name;
    private String cliId;
    private String className;
    private String basePath;
    
    public Importer() {
      super();
    }
    
    public Importer(String[] data) {
      super();
      this.name = data[0];
      this.cliId = data[1];
      this.className = data[2];
      this.basePath = data[3];
    }
    
    public String getName() {
      return this.name;
    }
    
    public void setName(String name) {
      this.name = name;
    }
    
    public String getClidId() {
      return this.cliId;
    }
    
    public void setCliId(String cliId) {
      this.cliId = cliId;
    }
    
    public String getClassName() {
      return this.className;
    }
    
    public void setClassName(String className) {
      this.className = className;
    }
    
    public void setBasePath(String basePath) {
      this.basePath = basePath;
    }
    
    public File getBasePath() {
      return new File(basePath);
    }
    
    public URL getBasePathUrl() throws MalformedURLException {
      return getBasePath().toURL();
    }
    
    public String[] getAsStringArray() {
      return new String[] {name, cliId, className, basePath};
    }
    
    public boolean equals(Object o) {
      return o != null && o instanceof Importer && this.getName().equals(((Importer)o).getName());
    }
    
    public int hashCode() {
      return name.hashCode();
    }
    
    public int compareTo(Object o) {
      return this.getName().compareTo( ((Importer)o).getName() );
    }
    
    public String toString() {
      return this.name;
    }
    
    public ImportFormat getInstance() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      URLClassLoader cl = new URLClassLoader(new URL[] {getBasePathUrl()});
      Class clazz = Class.forName(className, true, cl);
      ImportFormat importFormat = (ImportFormat)clazz.newInstance();
      importFormat.setIsCustomImporter(true);
      return importFormat;
    }
  }
  
  private JabRefPreferences prefs;

  public CustomImportList(JabRefPreferences prefs) {
    super();
    this.prefs = prefs;
    readPrefs();
  }


  private void readPrefs() {
    int i=0;
    String[] s = null;
    while ((s = prefs.getStringArray("customImportFormat"+i)) != null) {
      try {
        super.add(new Importer(s));
      } catch (Exception e) {
        System.err.println("Warning! Could not load " + s[0] + " from preferences. Will ignore.");
        
      }
      i++;
    }
  }

  public void addImporter(Importer customImporter) {
    super.add(customImporter);
  }
  
  
  public boolean replaceImporter(Importer customImporter) {
    boolean wasContained = this.remove(customImporter);
    this.addImporter(customImporter);
    return wasContained;
  }

  public void store() {
    purgeAll();
    Importer[] importers = (Importer[])this.toArray(new Importer[]{});
    for (int i = 0; i < importers.length; i++) {
      Globals.prefs.putStringArray("customImportFormat"+i, importers[i].getAsStringArray());
    }
  }

  private void purgeAll() {
    for (int i = 0; Globals.prefs.getStringArray("customImportFormat"+i) != null; i++) {
      Globals.prefs.remove("customImportFormat"+i);
    }
  }

}
