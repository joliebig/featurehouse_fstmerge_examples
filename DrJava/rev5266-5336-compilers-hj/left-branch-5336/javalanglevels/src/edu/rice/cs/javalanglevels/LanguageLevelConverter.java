

package edu.rice.cs.javalanglevels;

import java.util.*;
import edu.rice.cs.javalanglevels.*;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.util.Log;
import edu.rice.cs.javalanglevels.util.Utilities;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.io.IOUtil;


public class LanguageLevelConverter {
  
  public static final Log _log = new Log("LLConverter.txt", false);
  
  
  public static final Symboltable symbolTable = new Symboltable();
  
  public static Options OPT = Options.DEFAULT;
  
  
  private static final boolean SAFE_SUPPORT_CODE = false;
  public static final int INPUT_BUFFER_SIZE = 8192;  
  
  
  public static final int LINE_NUM_MAPPINGS_PER_LINE = 8;
  
  
  public static final Hashtable<SymbolData, LanguageLevelVisitor> _newSDs = 
    new Hashtable<SymbolData, LanguageLevelVisitor>();
  
  
  private LinkedList<JExprParseException> _parseExceptions = new LinkedList<JExprParseException>();
  
  
  private LinkedList<Pair<String, JExpressionIF>> _visitorErrors = new LinkedList<Pair<String, JExpressionIF>>();
  
  
  private void _addParseException(ParseException pe) {
    JExprParseException jpe;
    if (pe instanceof JExprParseException) { jpe = (JExprParseException) pe; }
    else { jpe = new JExprParseException(pe); }
    _parseExceptions.addLast(jpe);
  }
  
  
  private void _addVisitorError(Pair<String, JExpressionIF> ve) { _visitorErrors.addLast(ve); }
  
  
  public Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>>
    convert(File[] files, Options options) {
    Map<File,Set<String>> sourceToTopLevelClassMap = new Hashtable<File,Set<String>>();
    return convert(files, options, sourceToTopLevelClassMap);
  }
  
  
  
  public Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>>
    convert(File[] files, Options options, Map<File,Set<String>> sourceToTopLevelClassMap) {
    
    _log.log("LanguageLevelConverter.convert called on files:  " + Arrays.toString(files));
    OPT = options;
    assert symbolTable != null;
    symbolTable.clear();
    _newSDs.clear();
    
    
    
    LinkedList<Pair<String, JExpressionIF>> languageLevelVisitorErrors = new LinkedList<Pair<String, JExpressionIF>>();
    
    
    Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations = 
      new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
    
    
    LinkedList<Pair<LanguageLevelVisitor, SourceFile>> languageLevelVisitedFiles =  
      new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>();
    
    
    Hashtable<Integer, Pair<SourceFile, LanguageLevelVisitor>> mediator = 
      new Hashtable<Integer, Pair<SourceFile, LanguageLevelVisitor>>();
    





    





    
    
    
    
    LinkedList<Triple<LanguageLevelVisitor, SourceFile, File>> visited = 
      new LinkedList<Triple<LanguageLevelVisitor, SourceFile, File>>();
    
    
    LinkedList<Triple<LanguageLevelVisitor, SourceFile, File>> toAugment = 
      new LinkedList<Triple<LanguageLevelVisitor, SourceFile, File>>();
    
    
    LinkedList<File> advanced = new LinkedList<File>();
    
    
    LinkedList<File> javaFiles = new LinkedList<File>();
    
    
    for (File f : files) {
      
      try {

        
        
        BufferedReader tempBr = new BufferedReader(new FileReader(f));
        String firstLine = tempBr.readLine();
        tempBr.close();
        if (firstLine == null) continue;
        
        if (isAdvancedFile(f))  advanced.addLast(f);
        else if (isFullJavaFile(f)) javaFiles.addLast(f);
        
        if (isJavaFile(f)) {  
          System.out.flush();
          SourceFile sf;
          JExprParser jep = new JExprParser(f);
          try { 

            _log.log("Parsing " + f);
            sf = jep.SourceFile();

            final Set<String> topLevelClasses = new HashSet<String>();
            for (TypeDefBase t: sf.getTypes()) {
              t.visit(new JExpressionIFAbstractVisitor<Void>() {
                public Void forClassDef(ClassDef that) { topLevelClasses.add(that.getName().getText()); return null; }
                public Void defaultCase(JExpressionIF that) { return null; }
              });
            }
            sourceToTopLevelClassMap.put(f, topLevelClasses);
          } 
          catch (ParseException pe) {
            
            _addParseException(pe);
            _log.log("GENERATED ParseException for file " + f);
            continue;
          }
          
          
          LanguageLevelVisitor llv;
          if (isLanguageLevelFile(f)) {
            llv = new IntermediateVisitor(f, new LinkedList<Pair<String, JExpressionIF>>(),
                                          new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(), 
                                          languageLevelVisitedFiles);
          }
          else {
            assert isAdvancedFile(f) || isFullJavaFile(f);
            llv = new FullJavaVisitor(f, new LinkedList<Pair<String, JExpressionIF>>(),
                                      new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>(), 
                                      languageLevelVisitedFiles);
          }
          
          
          sf.visit(llv);
          _log.log("\nDUMPING SYMBOLTABLE AFTER PHASE 1 PROCESSING OF " + f + "\n\n" + symbolTable + "\n");
          visited.add(new Triple<LanguageLevelVisitor, SourceFile, File>(llv, sf, f));
          _log.log("\nCONTINUATIONS AFTER PHASE 1 PROCESSING OF " + f + "\n\n" + llv.continuations + "\n");
          _log.log("\nERRORS AFTER PHASE 1 PROCESSING OF " + f + "\n\n" + llv.errors + "\n");

          
          continuations.putAll(llv.continuations);
          languageLevelVisitorErrors.addAll(llv.errors);
        }
      }
      catch (IOException ioe) {
        
        _addVisitorError(new Pair<String, JExpressionIF>(ioe.getMessage(), new NullLiteral(SourceInfo.NO_INFO)));
      }
    }

    
    LanguageLevelVisitor.errors = new LinkedList<Pair<String, JExpressionIF>>(); 
    
    _log.log("\nDUMPING SYMBOLTABLE BEFORE CONTINUATION RESOLUTION\n\n" + symbolTable + "\n");

    _log.log("Resolving continuations: " + continuations + "\n");
    while (! continuations.isEmpty()) {
      Enumeration<String> en = continuations.keys();
      
      while (en.hasMoreElements()) {
        String className = en.nextElement();
        Pair<SourceInfo, LanguageLevelVisitor> pair = continuations.remove(className);
        SymbolData returnedSd = pair.getSecond().getSymbolData(className, pair.getFirst(), true);
        _log.log("Attempting to resolve " + className + "\n  Result = " + returnedSd);

        if (returnedSd == null) {



          LanguageLevelVisitor.errors.add(new Pair<String, JExpressionIF>("Converter could not resolve " + className, 
                                                                          new NullLiteral(pair.getFirst())));
        }
      }
    }
    
    _log.log("\nDUMPING SYMBOLTABLE AFTER PASS 1\n\n" + symbolTable + "\n");
    
    
    Enumeration<SymbolData> keys = _newSDs.keys();
    while (keys.hasMoreElements()) {
      SymbolData key = keys.nextElement();
      LanguageLevelVisitor sdlv = _newSDs.get(key);   
      if (sdlv != null) sdlv.createConstructor(key);  
    } 
    

    
    
    languageLevelVisitorErrors.addAll(LanguageLevelVisitor.errors); 
    
    
    
    
    if (languageLevelVisitorErrors.size() > 0)  _visitorErrors.addAll(languageLevelVisitorErrors);
    
    else  { 
      for (Triple<LanguageLevelVisitor, SourceFile, File> triple: visited) {
        
        LanguageLevelVisitor llv = triple.getFirst();
        SourceFile sf = triple.getSecond();
        File f = triple.getThird();
        
        if (isAdvancedFile(f)) { toAugment.addLast(triple); }
        else if (isLanguageLevelFile(f)) {
          
          
          
          
          
          
          
          if (symbolTable.get("java.lang.Integer") == null) llv.getSymbolData("java.lang.Integer", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Double") == null)  llv.getSymbolData("java.lang.Double", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Boolean") == null) llv.getSymbolData("java.lang.Boolean", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Long") == null)    llv.getSymbolData("java.lang.Long", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Byte") == null)    llv.getSymbolData("java.lang.Byte", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Short") == null)   llv.getSymbolData("java.lang.Short", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Float") == null)   llv.getSymbolData("java.lang.Float", SourceInfo.NO_INFO);
          if (symbolTable.get("java.lang.Character") == null) 
            llv.getSymbolData("java.lang.Character", SourceInfo.NO_INFO);
          
          
          TypeChecker btc = 
            new TypeChecker(llv._file, llv._package, llv.errors, symbolTable, llv._importedFiles, llv._importedPackages);

          sf.visit(btc);
          toAugment.addLast(triple);
          if (btc.errors.size() > 0) _visitorErrors.addAll(btc.errors);
        }
      }
      



      




















    }
    



    
    
    if (_parseExceptions.size() > 0 || _visitorErrors.size() > 0) {
      return new Pair<LinkedList<JExprParseException>, 
        LinkedList<Pair<String, JExpressionIF>>>(_parseExceptions, _visitorErrors);
    }


    
       
    for (Triple<LanguageLevelVisitor, SourceFile, File> triple: toAugment) 
      try {
      
      LanguageLevelVisitor llv = triple.getFirst();
      SourceFile sf = triple.getSecond();
      File f = triple.getThird();
      
      File augmentedFile = getJavaForLLFile(f); 
      
      if (isAdvancedFile(f)) { Utilities.copyFile(f, augmentedFile); }
      else {
        BufferedReader tempBr = new BufferedReader(new FileReader(f));
        String firstLine = tempBr.readLine();
        tempBr.close(); 
        if (firstLine == null) continue;
        
        
        if (isLanguageLevelFile(f)) {
          if (triple != null) {  
            
            
            BufferedReader br = new BufferedReader(new FileReader(f), INPUT_BUFFER_SIZE);
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            
            

            Augmentor a = new Augmentor(SAFE_SUPPORT_CODE, br, bw, llv);
            sf.visit(a);
            
            br.close();
            bw.close();
            
            
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(augmentedFile)));
            SortedMap<Integer,Integer> lineNumberMap = a.getLineNumberMap();
            pw.println("// Language Level Converter line number map: dj*->java. Entries: "+lineNumberMap.size());
            
            
            
            int numLines = (int)Math.ceil(((double)lineNumberMap.size())/LINE_NUM_MAPPINGS_PER_LINE);
            int mapCount = 0;
            for(Map.Entry<Integer,Integer> e: lineNumberMap.entrySet()) {
              
              if (mapCount%LINE_NUM_MAPPINGS_PER_LINE==0) pw.print("//");
              pw.printf(" %5d->%-5d", e.getKey(), (e.getValue()+numLines+1));
              if (mapCount%LINE_NUM_MAPPINGS_PER_LINE==LINE_NUM_MAPPINGS_PER_LINE-1) pw.println();
              ++mapCount;
            }
            if (mapCount%LINE_NUM_MAPPINGS_PER_LINE!=0) pw.println(); 
            
            String augmented = sw.toString();
            pw.write(augmented, 0, augmented.length());
            pw.close();
          }
        }
      }
    }
    catch (Augmentor.Exception ae) {
      
      _addVisitorError(new Pair<String, JExpressionIF>(ae.getMessage(), new NullLiteral(SourceInfo.NO_INFO)));
    }
    catch (IOException ioe) {
      
      _addVisitorError(new Pair<String, JExpressionIF>(ioe.getMessage(), new NullLiteral(SourceInfo.NO_INFO)));
    }
    return new Pair<LinkedList<JExprParseException>, 
      LinkedList<Pair<String, JExpressionIF>>>(_parseExceptions, _visitorErrors);
  }
  
  
  public static boolean isElementaryFile(File f) { return f.getPath().endsWith(".dj0"); } 
  
  public static boolean isIntermediateFile(File f) { return f.getPath().endsWith(".dj1"); }
  
  public static boolean isAdvancedFile(File f) { return f.getPath().endsWith(".dj2"); }
  
  public static boolean isFunctionalJavaFile(File f) { return f.getPath().endsWith(".dj"); }
  
  public static boolean isFullJavaFile(File f) { return f.getPath().endsWith(".java"); }
  
  
  private static boolean isLanguageLevelFile(File f) {
    return isElementaryFile(f) || isIntermediateFile(f) || isFunctionalJavaFile(f);
  }
  
  private static boolean isJavaFile(File f) {
    return isLanguageLevelFile(f) || isAdvancedFile(f) || isFullJavaFile(f);
  }
  
  private static File getJavaForLLFile(File f) {
    String augmentedFilePath = f.getAbsolutePath();
    int dotPos = augmentedFilePath.lastIndexOf('.');
    augmentedFilePath = augmentedFilePath.substring(0, dotPos); 
    return new File(augmentedFilePath + ".java"); 
  }
  
  
  public static boolean versionSupportsAutoboxing(JavaVersion version) {
    return version.supports(JavaVersion.JAVA_5);
    
  }
  
  
  public static boolean versionSupportsGenerics(JavaVersion version) {
    return version.supports(JavaVersion.JAVA_5);
    
  }
  
  
  public static boolean versionSupportsForEach(JavaVersion version) {
    return version.supports(JavaVersion.JAVA_5);
  }
  
  
  public static boolean versionIs15(JavaVersion version) { return version.supports(JavaVersion.JAVA_5); }
  
  
  public static void main(String[] args) {
    LanguageLevelConverter llc = new LanguageLevelConverter();
    
    if (args.length == 0) {
      System.out.println("Java Language Level Converter");
      System.out.println("Please pass file names (*.dj, *.dj0, *.dj1, *.dj2) as arguments.");
      System.out.println("Note: The converter will use Java's classpath to resolve classes.");
      System.out.println("      If classes are not found, use java -cp <classpath> to set the classpath.");
      return;
    }
    
    File[] files = new File[args.length];
    for (int i = 0; i < args.length; i++) {
      files[i] = new File(args[i]);
    }
    
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result = 
      llc.convert(files, new Options(JavaVersion.JAVA_5,
                                     IOUtil.parsePath(System.getProperty("java.class.path", ""))));
    System.out.println(result.getFirst().size() + result.getSecond().size() + " errors.");
    for(JExprParseException p : result.getFirst()) {
      System.out.println(p);
    }
    for(Pair<String, JExpressionIF> p : result.getSecond()) {
      System.out.println(p.getFirst() + " " + p.getSecond().getSourceInfo());
    }
  }
}
