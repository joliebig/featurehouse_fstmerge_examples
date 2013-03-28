

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
import edu.rice.cs.drjava.model.DrJavaFileUtils;
import edu.rice.cs.util.swing.Utilities;


public class LanguageLevelStackTraceMapper {
  
  
  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("llstm.txt",false);
  
  
  private HashMap<String,TreeMap<Integer,Integer>> cache;
  
  
  private GlobalModel aGModel;
  
  
  public LanguageLevelStackTraceMapper(GlobalModel aGM){
    aGModel = aGM;
    cache = new HashMap<String,TreeMap<Integer,Integer>>();
  }
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s, File d, TreeMap<Integer,Integer> m) {
    if (! matches(d,s)) return s;
    
    StackTraceElement NewS = 
      new StackTraceElement(s.getClassName(), s.getMethodName(),d.getName(), m.get(s.getLineNumber()));
    
    return NewS;
  }
  
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s,
                                                    File d) {
    if (! matches(d, s)) return s;
    String fileName = d.getAbsolutePath();
    if (cache.containsKey(fileName)) return replaceStackTraceElement(s, d, cache.get(fileName));
    
    String dn = d.getName();
    dn = dn.substring(0, dn.lastIndexOf('.')) + edu.rice.cs.drjava.config.OptionConstants.JAVA_FILE_EXTENSION;
    File javaFile = new File(d.getParentFile(), dn);
    
    cache.put(fileName, readLLLineBlock(javaFile));  
    
    return replaceStackTraceElement(s,d,cache.get(fileName));
    
    
  }





  
  
  
  public StackTraceElement replaceStackTraceElement(StackTraceElement s, List<File> ds) {
    for(int i = 0; i < ds.size(); i++) {
      s = replaceStackTraceElement(s, ds.get(i)); 
    }
    return s;
  }


  
  
  public StackTraceElement[] replaceStackTrace(StackTraceElement[] ss, List<File> ds){
    for(int i = 0; i < ss.length; i++) {
      ss[i] = replaceStackTraceElement(ss[i], ds);
    }
    return ss;
  }


  
  
  
  public void clearCache(){
    cache = new HashMap<String,TreeMap<Integer,Integer>>();
  }
  
  
  
  private boolean matches(File f, StackTraceElement s) {
    LOG.log("matches(" + f + ", " + s + ")");
    if (s.getFileName() == null) return false;
    OpenDefinitionsDocument d;      
    try { d = aGModel.getDocumentForFile(f); }
    catch(java.io.IOException e){ return false; }
    
    String dn = d.getRawFile().getName();
    

    if (!DrJavaFileUtils.isLLFile(dn)) return false;
    

    dn = DrJavaFileUtils.getJavaForLLFile(dn);
    

    String dp = d.getPackageName();
    int dotPos = s.getClassName().lastIndexOf('.');
    if ((dp.length() == 0) && (dotPos >= 0)) return false; 
    if ((dp.length() > 0) && (dotPos < 0)) return false; 
    String sp = "";
    if (dotPos >= 0) sp = s.getClassName().substring(0, dotPos);
    if (! dp.equals(sp)) return false; 
    

    return s.getFileName().equals(dn);
  }
  
  private TreeMap<Integer, Integer> createOneToOneMap(BufferedReader bufReader) {
    
    
    
    
    
    
    TreeMap<Integer, Integer> oneToOne = new TreeMap<Integer, Integer>();
    int lineNo = 1;
    oneToOne.put(lineNo,lineNo);
    try {
      String rdLine;
      while((rdLine = bufReader.readLine()) != null) {
        ++lineNo;
        oneToOne.put(lineNo,lineNo);
      }
    }
    catch(java.io.IOException e) {  }
    return oneToOne;
  }
  
  
  
  public TreeMap<Integer, Integer> readLLLineBlock(File LLFile){
    
    BufferedReader bufReader = null;
    String rdLine = "";
    
    try { bufReader = new BufferedReader(new FileReader(LLFile));  } catch(java.io.FileNotFoundException e){ }
    
    try { rdLine = bufReader.readLine();  }  catch(java.io.IOException e){ }
    
    if (!rdLine.startsWith("// Language Level Converter line number map: dj*->java. Entries:")) {
      
      return createOneToOneMap(bufReader);
    }
    
    LOG.log("rdLine = '" + rdLine + "'");
    LOG.log("\tlastIndex = " + rdLine.lastIndexOf(" "));
    Integer mapSize = new Integer (rdLine.substring(rdLine.lastIndexOf(" ") + 1));
    
    try { rdLine = bufReader.readLine();  }  catch(java.io.IOException e){ }
    
    if (rdLine.indexOf("//") != 0) mapSize = 0;  
    
    String temp = "";
    String numRnum = "";
    TreeMap<Integer,Integer> javaDJMap = new TreeMap<Integer,Integer>();
    
    temp = rdLine.substring(2).trim() + " ";
    
    Integer djNum;
    Integer javaNum;
    

    for (int i = 0; i < mapSize; i++) {
      if (temp.length() < 2)  temp = readLLLineBlockHelper(bufReader);
      if (temp == null) break;

      numRnum = temp.substring(0, temp.indexOf(" "));
      
      djNum = new Integer(numRnum.substring(0, numRnum.indexOf("->")));
      javaNum = new Integer(numRnum.substring(numRnum.indexOf("->") + 2));
      
      javaDJMap.put(javaNum,djNum);
      temp = temp.substring(temp.indexOf(" ")).trim() + " ";
    }
    return javaDJMap;
  }
  
  
  
  
  public TreeMap<Integer, Integer> ReadLanguageLevelLineBlockRev(File LLFile) {
    
    BufferedReader bufReader = null;
    String rdLine = "";
    
    try { bufReader = new BufferedReader(new FileReader(LLFile)); } catch(java.io.FileNotFoundException e){ }
    
    try { rdLine = bufReader.readLine(); } catch(java.io.IOException e){ }
    
    if (!rdLine.startsWith("// Language Level Converter line number map: dj*->java. Entries:")) {
      
      return createOneToOneMap(bufReader);
    }
    
    LOG.log("rdLine = '" + rdLine + "'");
    LOG.log("\tlastIndex = " + rdLine.lastIndexOf(" "));
    Integer mapSize = new Integer (rdLine.substring(rdLine.lastIndexOf(" ") + 1));
    
    try{ rdLine = bufReader.readLine(); } catch(java.io.IOException e){ }
    
    if(rdLine.indexOf("//") != 0) mapSize = 0;  

    TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
    
    String temp = rdLine.substring(2).trim() + " ";  
    String numRnum = "";
    
    int djNum;
    int javaNum;

    for(int i = 0; i < mapSize; i++){
      if (temp.length() < 2)  temp = readLLLineBlockHelper(bufReader);
      if (temp == null) break;
      
      numRnum = temp.substring(0, temp.indexOf(" "));
      
      djNum = Integer.parseInt(numRnum.substring(0, numRnum.indexOf("->")), 10);
      javaNum = Integer.parseInt(numRnum.substring(numRnum.indexOf("->") + 2), 10);
      
      map.put(djNum, javaNum);
      temp = temp.substring(temp.indexOf(" ")).trim() + " ";  
      
    }
    return map;
  }
  
  
  private String readLLLineBlockHelper(BufferedReader br) {
    String line = "";
    try { line = br.readLine(); } catch(java.io.IOException e){ }
    
    if (line.indexOf("//") != 0) return null;
    line = line.substring(2).trim() + " ";
    return line;
  }
}