

package edu.rice.cs.drjava.model.compiler;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;




public class LanguageLevelStackTraceMapper {
  
  
  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("llstm.txt",false);
  
  
  private HashMap<String,TreeMap<Integer,Integer>> cache;
  
  
  private GlobalModel AGmodel;
  
  
  public LanguageLevelStackTraceMapper(GlobalModel AGM){
    AGmodel = AGM;
    cache = new HashMap<String,TreeMap<Integer,Integer>>();
  }
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s,
                                                    File d, TreeMap<Integer,Integer> m) {
    if(!matches(d,s)) return s;
    
    StackTraceElement NewS = new StackTraceElement(s.getClassName(),s.getMethodName(),d.getName(),m.get(s.getLineNumber()));
    
    return NewS;
    
    
  }
  
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s,
                                                    File d) {
    if(!matches(d,s)) return s;
    String FileName = d.getAbsolutePath();
    if(cache.containsKey(FileName)) return replaceStackTraceElement(s,d,cache.get(FileName));
    
    String dn = d.getName();
    dn = dn.substring(0, dn.lastIndexOf('.'))+".java";
    File javaFile = new File(d.getParentFile(), dn);
    
    cache.put(FileName,ReadLanguageLevelLineBlock(javaFile));  
    
    return replaceStackTraceElement(s,d,cache.get(FileName));
    
    
  }





  
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s,
                                                    List< File> ds) {
    for(int i=0;i<ds.size();i++) {
      s = replaceStackTraceElement(s,ds.get(i)); 
    }
    return s;
  }


  
  
  
  
  public StackTraceElement[] replaceStackTrace(StackTraceElement[] ss,
                                               List< File> ds){
    for(int i=0;i<ss.length;i++){
      ss[i]=replaceStackTraceElement(ss[i],ds);
    }
    return ss;
  }


  
  
  
  public void clearCache(){
    cache = new HashMap<String,TreeMap<Integer,Integer>>();
  }
  
  
  
  private boolean matches(File f, StackTraceElement s) {
    LOG.log("matches("+f+", "+s+")");
    if (s.getFileName()==null) return false;
    OpenDefinitionsDocument d;      
    try{
      d = AGmodel.getDocumentForFile(f);}
    
    catch(java.io.IOException e){return false;}
    
    String dn = d.getRawFile().getName();
    

    if (!isLLFileName(dn)) return false;
    

    dn = dn.substring(0, dn.lastIndexOf('.'))+".java";
    

    String dp = d.getPackageName();
    int dotPos = s.getClassName().lastIndexOf('.');
    if ((dp.length()==0) && (dotPos>=0)) return false; 
    if ((dp.length()>0) && (dotPos<0)) return false; 
    String sp = "";
    if (dotPos>=0) {
      sp = s.getClassName().substring(0, dotPos);
    }
    if (!dp.equals(sp)) return false; 
    

    return s.getFileName().equals(dn);
  }
  
  
  
  
  public TreeMap<Integer, Integer> ReadLanguageLevelLineBlock(File LLFile){
    
    BufferedReader BReader = null;
    String ReadLine = "";
    
    try{  BReader = new BufferedReader(new FileReader(LLFile));  } catch(java.io.FileNotFoundException e){}
    
    try{  ReadLine = BReader.readLine();  }  catch(java.io.IOException e){}
    
    LOG.log("ReadLine = '"+ReadLine+"'");
    LOG.log("\tlastIndex = "+ReadLine.lastIndexOf(" "));
    Integer MapSize = new Integer (ReadLine.substring(ReadLine.lastIndexOf(" ")+1));
    
    try{  ReadLine = BReader.readLine();  }  catch(java.io.IOException e){}
    
    if(ReadLine.indexOf("//")!=0) MapSize=0;  
    
    
    String temp = "";
    String numRnum = "";
    TreeMap<Integer,Integer> JavaDjMap = new TreeMap<Integer,Integer>();
    
    temp = ReadLine.substring(2);
    temp = temp.trim() + " ";
    
    Integer djNum;
    Integer javaNum;
    
    for(int i=0; i<MapSize; i++){
      if(temp.length()<2)  temp = ReadLanguageLevelLineBlockHelper(BReader);
      if(temp==null) break;
      
      numRnum = temp.substring(0,temp.indexOf(" "));
      
      djNum = new Integer(numRnum.substring(0,numRnum.indexOf("->")));
      javaNum = new Integer(numRnum.substring(numRnum.indexOf("->")+2));
      
      JavaDjMap.put(javaNum,djNum);
      temp = temp.substring(temp.indexOf(" ")).trim() + " ";
    }
    return JavaDjMap;
  }
  
  
  
  
  public TreeMap<Integer, Integer> ReadLanguageLevelLineBlockRev(File LLFile){
    
    BufferedReader BReader = null;
    String ReadLine = "";
    
    try{  BReader = new BufferedReader(new FileReader(LLFile));  } catch(java.io.FileNotFoundException e){}
    
    try{  ReadLine = BReader.readLine();  }  catch(java.io.IOException e){}
    
    LOG.log("ReadLine = '"+ReadLine+"'");
    LOG.log("\tlastIndex = "+ReadLine.lastIndexOf(" "));
    Integer MapSize = new Integer (ReadLine.substring(ReadLine.lastIndexOf(" ")+1));
    
    try{  ReadLine = BReader.readLine();  }  catch(java.io.IOException e){}
    
    if(ReadLine.indexOf("//")!=0) MapSize=0;  
    
    
    String temp = "";
    String numRnum = "";
    TreeMap<Integer,Integer> DjJavaMap = new TreeMap<Integer,Integer>();
    
    temp = ReadLine.substring(2);
    temp = temp.trim() + " ";
    
    Integer djNum;
    Integer javaNum;
    
    for(int i=0; i<MapSize; i++){
      if(temp.length()<2)  temp = ReadLanguageLevelLineBlockHelper(BReader);
      if(temp==null) break;
      
      numRnum = temp.substring(0,temp.indexOf(" "));
      
      djNum = new Integer(numRnum.substring(0,numRnum.indexOf("->")));
      javaNum = new Integer(numRnum.substring(numRnum.indexOf("->")+2));
      
      DjJavaMap.put(djNum,javaNum);
      temp = temp.substring(temp.indexOf(" ")).trim() + " ";
    }
    return DjJavaMap;
  }
  
  
  
  
  private String ReadLanguageLevelLineBlockHelper(BufferedReader BR) {
    String line = "";
    try{  line = BR.readLine(); } catch(java.io.IOException e){}
    
    if(line.indexOf("//")!=0) return null;
    line = line.substring(2).trim();
    return line;
  }
  
  
  public static boolean isLLFileName(String s) {
    return (s.endsWith(".dj0") ||
            s.endsWith(".dj1") ||
            s.endsWith(".dj2"));
  }
  
  
  public static boolean isLLFile(File f) {
    return isLLFileName(f.getName());
  }
  
  
  public static File getJavaFileForLLFile(File llFile) {
    if (!isLLFile(llFile)) throw new AssertionError("File is not a language level file: "+llFile);
    String dn = llFile.getPath();
    dn = dn.substring(0, dn.lastIndexOf('.'))+".java";
    return new File(dn);
  }
}