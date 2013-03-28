

package edu.rice.cs.drjava.config;
import java.io.*;
import edu.rice.cs.util.FileOps;


public class FileConfiguration extends SavableConfiguration {  
  
  protected final File file;
  
  
  public FileConfiguration(File f) {
    super(new DefaultOptionMap());
    file = f.getAbsoluteFile();
  }
  
  
  public File getFile() { return file; }
  
  
  public void loadConfiguration() throws IOException {
    loadConfiguration(new BufferedInputStream(new FileInputStream(file)));
  }
  
  
  public void saveConfiguration() throws IOException {
    saveConfiguration("DrJava configuration file");
  }
  
  
  public void saveConfiguration(final String header) throws IOException {
    FileOps.saveFile(new FileOps.DefaultFileSaver(file) {
      public void saveTo(OutputStream os) throws IOException { saveConfiguration(os,header); }
      public boolean shouldBackup() { return false; }
    });
  }
}
