

package edu.rice.cs.drjava.project;

import java.io.*;
import edu.rice.cs.drjava.project.MalformedProjectFileException;


public class ProjectFileParserFacade {  
  
  public static final ProjectFileParserFacade ONLY = new ProjectFileParserFacade();
  protected ProjectFileParserFacade() { }
  
  protected File _projectFile;
  protected boolean _xmlProjectFile;
  
  
  public ProjectFileIR parse(File projFile) throws IOException, FileNotFoundException, MalformedProjectFileException {
    FileReader fr = new FileReader(projFile);
    int read = fr.read();
    if (read==-1) {
      
      throw new MalformedProjectFileException("Empty project file.");
    }
    if (((char)read) != ';') {
      
      
      fr.close();
      return fixup(XMLProjectFileParser.ONLY.parse(projFile));
    }
    read = fr.read();
    if (read==-1) {
      
      throw new MalformedProjectFileException("Incomplete project file.");
    }
    if (((char)read) != ';') {
      
      
      fr.close();
      return fixup(XMLProjectFileParser.ONLY.parse(projFile));
    }
    fr.close();
    
    return fixup(ProjectFileParser.ONLY.parse(projFile));
  }
  
  private static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("ParserFacadeFixup.txt", false);
  
  
  protected ProjectFileIR fixup(ProjectFileIR pfir){
    boolean doFixup = false;
    
    String version = pfir.getDrJavaVersion();
    
    if(version.equals("unknown"))
      doFixup = true;
    
    if(!doFixup){
      int i = version.indexOf("-r");
      
      if(i == -1){
        doFixup = true;
      }else{
        try{
          if(Integer.parseInt(version.substring(i+2).trim()) < 4782)
            doFixup = true;
        }catch(NumberFormatException e){
          doFixup = true;
        }
      }
    }
    
    LOG.log("DoFixup? "+doFixup);
    
    if(!doFixup || pfir.getMainClass() == null) return pfir;
    
    String mainClass = pfir.getMainClass();
    
    LOG.log("\tmainClass = \""+mainClass+"\"");
    
    String qualifiedName = mainClass;
    
    
    if(qualifiedName.startsWith(""+File.separatorChar))
      qualifiedName = qualifiedName.substring(1);
    
    
    if(qualifiedName.toLowerCase().endsWith(".java"))
      qualifiedName = qualifiedName.substring(0, qualifiedName.length() - 5);
    
    LOG.log("\tsetMainClass = \""+qualifiedName+"\"");
    
    
    pfir.setMainClass(qualifiedName.replace(File.separatorChar, '.'));
    
    return pfir;
  }
}
