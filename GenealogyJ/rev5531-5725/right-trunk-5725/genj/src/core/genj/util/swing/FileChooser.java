
package genj.util.swing;

import genj.util.WordBuffer;

import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class FileChooser extends JFileChooser {

  
  private String command;

  
  private JComponent owner;

  
  public FileChooser(JComponent owner, String title, String command, String extensions, String baseDir) {
    
    super(baseDir!=null?baseDir:".");

    setDialogTitle(title);
    
    this.owner  = owner;
    this.command= command;

    if (extensions!=null) {
      Filter filter = new Filter(extensions);
      addChoosableFileFilter(filter);
      setFileFilter(filter);
    }
  }

  
  public int showDialog() {
    int rc = showDialog(owner,command);
    
    if (rc!=0)
      setSelectedFile(null);
    return rc;
  }


  
  private class Filter extends FileFilter {
    
    
    private String[] exts;
    
    
    private String descr;

    
    private Filter(String extensions) {

      StringTokenizer tokens = new StringTokenizer(extensions, ",");
      exts = new String[tokens.countTokens()];
      if (exts.length==0)
        throw new IllegalArgumentException("extensions required");
        
      WordBuffer buf = new WordBuffer(",");
      for (int i=0; i<exts.length; i++) {
        exts[i] = tokens.nextToken().toLowerCase().trim();
        buf.append("*."+exts[i]);
      }
      descr = buf.toString();
    }

    
    public boolean accept(File f) {

      
      if (f.isDirectory())
        return true;

      
      String name = f.getName();
      int dot = name.lastIndexOf('.');
      if (dot<0)
        return false;
      String ext = name.substring(dot+1); 
        
      
      for (int i=0;i<exts.length;i++) {
        if (exts[i].equalsIgnoreCase(ext))
          return true;
      }
      
      
      return false;
    }

    
    public String getDescription() {
      return descr;
    }

  } 

} 
