

package edu.rice.cs.javalanglevels;

import org.objectweb.asm.*;
import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.tree.Type; 
import edu.rice.cs.javalanglevels.parser.JExprParser;
import edu.rice.cs.javalanglevels.parser.ParseException;
import edu.rice.cs.javalanglevels.util.Log;
import edu.rice.cs.javalanglevels.util.Utilities;
import java.util.*;
import java.io.*;
import java.lang.reflect.Modifier;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.reflect.PathClassLoader;
import edu.rice.cs.plt.reflect.EmptyClassLoader;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.io.IOUtil;

import junit.framework.TestCase;


public class LanguageLevelVisitor extends JExpressionIFPrunableDepthFirstVisitor {
  
  
  protected static LinkedList<Pair<String, JExpressionIF>> errors;
  
  
  public final Symboltable symbolTable;
  
  
  static Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations;

  
  
  static LinkedList<Pair<LanguageLevelVisitor, SourceFile>> visitedFiles;
  
  
  static boolean _errorAdded;
  
  
  static Hashtable<String, TypeDefBase> _hierarchy;
  
  
  File _file;
  
  
  String _package;
  
  
  String _enclosingClassName;
  
  
  LinkedList<String> _importedFiles;
  
  
  LinkedList<String> _importedPackages;
  
  
  LinkedList<String> _classNamesInThisFile;
  
  
  Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>> _classesToBeParsed;
  LinkedList<String> _innerClassesToBeParsed;
  
  protected static final Log _log = new Log("LLConverter.txt", false);
  
  
  public LanguageLevelVisitor(File file, 
                              String packageName, 
                              LinkedList<String> importedFiles, 
                              LinkedList<String> importedPackages, 
                              LinkedList<String> classNamesInThisFile, 
                              Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    _file = file;
    _enclosingClassName = null;
    _package = packageName;
    _importedFiles = importedFiles;
    _importedPackages = importedPackages;
    _classNamesInThisFile = classNamesInThisFile;
    _classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
    _innerClassesToBeParsed = new LinkedList<String>();
    this.continuations = continuations;
    symbolTable = LanguageLevelConverter.symbolTable;
  }
  
  
  public LanguageLevelVisitor(File file, 
                              String packageName, 
                              LinkedList<String> importedFiles, 
                              LinkedList<String> importedPackages, 
                              LinkedList<String> classNamesInThisFile, 
                              Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>> classesToBeParsed,
                              Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>> continuations) {
    this(file, packageName, importedFiles, importedPackages, classNamesInThisFile, continuations);
    _classesToBeParsed = classesToBeParsed;
  }
  
  
  protected void _resetNonStaticFields() {
    _file = new File("");
    _enclosingClassName = null;
    _package = "";
    _importedFiles = new LinkedList<String>();
    _importedPackages = new LinkedList<String>();
    _classNamesInThisFile = new LinkedList<String>();
  }
  
  
  public static String getFieldAccessorName(String name) { return name; }
  
  
  public File getFile() { return _file; }
  
  
  protected boolean isConstructor(Data d) {
    if (!(d instanceof MethodData)) return false;
    MethodData md = (MethodData) d;
    
    return (md.getReturnType() != null) && (md.getSymbolData() != null) &&
      (md.getReturnType().getName().indexOf(md.getName()) != -1) && 
      (md.getReturnType() == md.getSymbolData());
  }
  
  
  public static String getUnqualifiedClassName(String className) { 
    int lastIndexOfDot = className.lastIndexOf(".");
    if (lastIndexOfDot != -1) {
      className = className.substring(lastIndexOfDot + 1);
    }
    int lastIndexOfDollar = className.lastIndexOf("$");
    if (lastIndexOfDollar != -1) {
      className = className.substring(lastIndexOfDollar + 1);
    }
    
    while (className.length() > 0 && Character.isDigit(className.charAt(0))) { 
      className = className.substring(1, className.length());
    }
    return className;
  }
  
  
  protected String[] referenceType2String(ReferenceType[] rts) {
    String[] throwStrings = new String[rts.length];
    for (int i = 0; i < throwStrings.length; i++) {
      throwStrings[i] = rts[i].getName();
    }
    return throwStrings;
  }
  
  
  private static final Thunk<ClassLoader> RESOURCES = new Thunk<ClassLoader>() {
    private Options _cachedOptions = null;
    private ClassLoader _cachedResult = null;
    public ClassLoader value() {
      if (LanguageLevelConverter.OPT != _cachedOptions) {
        _cachedOptions = LanguageLevelConverter.OPT;
        Iterable<File> searchPath = IterUtil.<File>compose(LanguageLevelConverter.OPT.bootClassPath(),
                                                     LanguageLevelConverter.OPT.classPath());
        _cachedResult = new PathClassLoader(EmptyClassLoader.INSTANCE, searchPath);
      }
      return _cachedResult;
    }
  };
  
  
  private SymbolData _classFile2SymbolData(String qualifiedClassName, String programRoot) {
    ClassReader reader = null;
    try {
      String fileName = qualifiedClassName.replace('.', '/') + ".class";
      InputStream stream = RESOURCES.value().getResourceAsStream(fileName);
      if (stream == null && programRoot != null) {
        stream = PathClassLoader.getResourceInPathAsStream(fileName, new File(programRoot));
      }
      if (stream == null) { return null; }
      
      reader = new ClassReader(IOUtil.toByteArray(stream));
    }
    catch (IOException e) { return null; }
    
    
    final SymbolData sd;
    SymbolData sdLookup = symbolTable.get(qualifiedClassName); 
    if (sdLookup == null)  { 
      sd = new SymbolData(qualifiedClassName);
      symbolTable.put(qualifiedClassName, sd);
    }
    else { sd = sdLookup; }
    
    
    sd.setIsContinuation(false);
    
    final SourceInfo lookupInfo = _makeSourceInfo(qualifiedClassName);
    final String unqualifiedClassName = getUnqualifiedClassName(qualifiedClassName);
    
    ClassVisitor extractData = new ClassVisitor() {
      
      public void visit(int version, int access, String name, String sig, String sup, String[] interfaces) {
        sd.setMav(_createMav(access));
        sd.setInterface(Modifier.isInterface(access));
        
        int slash = name.lastIndexOf('/');
        if (slash == -1) { sd.setPackage(""); }
        else { sd.setPackage(name.substring(0, slash).replace('/', '.')); }
        
        if (sup == null) { sd.setSuperClass(null); }
        else { sd.setSuperClass(getSymbolDataForClassFile(sup.replace('/', '.'), lookupInfo)); }
        
        if (interfaces != null) {
          for (String iName : interfaces) {
            SymbolData superInterface = getSymbolDataForClassFile(iName.replace('/', '.'), lookupInfo);
            if (superInterface != null) { sd.addInterface(superInterface); }
          }
        }
      }
      
      public FieldVisitor visitField(int access, String name, String desc, String sig, Object value) {
        
        String typeString = org.objectweb.asm.Type.getType(desc).getClassName();
        SymbolData type = getSymbolDataForClassFile(typeString, lookupInfo);
        if (type != null) { sd.addVar(new VariableData(name, _createMav(access), type, true, sd)); }
        return null;
      }
      
      public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
        if (Modifier.isPrivate(access)) return null; 
        boolean valid = true;
        String methodName;
        SymbolData returnType;
        if (name.equals("<init>")) {
          methodName = unqualifiedClassName;
          returnType = sd;
        }
        else {
          methodName = name;
          String returnString = org.objectweb.asm.Type.getReturnType(desc).getClassName();
          returnType = getSymbolDataForClassFile(returnString, lookupInfo);
          valid = valid && (returnType != null);
        }
        org.objectweb.asm.Type[] argTypes = org.objectweb.asm.Type.getArgumentTypes(desc);
        VariableData[] args = new VariableData[argTypes.length]; 
        for (int i = 0; i < argTypes.length; i++) {
          SymbolData argType = getSymbolDataForClassFile(argTypes[i].getClassName(), lookupInfo);
          if (argType == null) { valid = false; }
          else { args[i] = new VariableData(argType); }
        }
        if (exceptions == null) { exceptions = new String[0]; }
        for (int i = 0; i < exceptions.length; i++) { exceptions[i] = exceptions[i].replace('/', '.'); }
        
        if (valid) {
          MethodData m = 
            MethodData.make(methodName, _createMav(access), new TypeParameter[0], returnType, args, exceptions, sd, null);
          for (VariableData arg : args) { arg.setEnclosingData(m); }
          sd.addMethod(m, false, true);
        }
        return null;
      }
      
      public void visitSource(String source, String debug) {}
      public void visitOuterClass(String owner, String name, String desc) {}
      public AnnotationVisitor visitAnnotation(String desc, boolean visible) { return null; }
      public void visitAttribute(Attribute attr) {}
      public void visitInnerClass(String name, String outerName, String innerName, int access) {}
      public void visitEnd() {}
      
    };
    reader.accept(extractData, ClassReader.SKIP_CODE);
    
    
    
    continuations.remove(qualifiedClassName);
    
    return sd;
  }
  
  
  protected SourceInfo _makeSourceInfo(String qualifiedClassName) {
    return new SourceInfo(new File(qualifiedClassName), -1, -1, -1, -1);
  }
  
  
  private SymbolData _lookupFromClassesToBeParsed(String qualifiedClassName, SourceInfo si, boolean resolve) {
    if (resolve) {
      Pair<TypeDefBase, LanguageLevelVisitor> p = _classesToBeParsed.get(qualifiedClassName);
      if (p == null) {
        
        
        return null;
      }
      
      TypeDefBase cd = p.getFirst();
      LanguageLevelVisitor llv = p.getSecond();
      cd.visit(llv);
      return symbolTable.get(qualifiedClassName);
    }
    else {
      
      
      

      SymbolData sd = addSymbolData(si, qualifiedClassName);
      return sd;
    }
  }
  
  
  public static boolean isJavaLibraryClass(String className) {
    return className.startsWith("java.") ||
      className.startsWith("javax.") ||
      className.startsWith("org.ietf.") ||
      className.startsWith("org.omg.") ||
      className.startsWith("org.w3c.") ||
      className.startsWith("org.xml.") ||
      className.startsWith("sun.") ||
      className.startsWith("junit.framework."); 
  }
  
  
  public static boolean isDuplicateVariableData(LinkedList<VariableData> vds, VariableData toInsert) {
    for (int i = 0; i<vds.size(); i++) {
      VariableData temp = vds.get(i);
      if (temp.getName().equals(toInsert.getName())) {
        return true;
      }
    }
    return false;
  }
  
  
  protected SymbolData getSymbolDataForClassFile(String className, SourceInfo si) {
    SymbolData sd = getSymbolDataHelper(className, si, true, true, true, false);
    
    if (sd == null) {
      
      
      _addAndIgnoreError("Class " + className + " not found.", new NullLiteral(si));
      return null;
    }
    sd.setIsContinuation(false);
    
    continuations.remove(sd.getName());
    return sd;
  }
  
  
  private SymbolData _getSymbolData_Primitive(String className) {
    if (className.equals("boolean")) {
      return SymbolData.BOOLEAN_TYPE;
    }
    else if (className.equals("char")) {
      return SymbolData.CHAR_TYPE;
    }
    else if (className.equals("byte")) {
      return SymbolData.BYTE_TYPE;
    }
    else if (className.equals("short")) {
      return SymbolData.SHORT_TYPE;
    }
    else if (className.equals("int")) {
      return SymbolData.INT_TYPE;
    }
    else if (className.equals("long")) {
      return SymbolData.LONG_TYPE;
    }
    else if (className.equals("float")) {
      return SymbolData.FLOAT_TYPE;
    }
    else if (className.equals("double")) {
      return SymbolData.DOUBLE_TYPE;
    }
    else if (className.equals("void")) {
      return SymbolData.VOID_TYPE;
    }
    else if (className.equals("null")) {
      return SymbolData.NULL_TYPE;
    }
    return null;
  }
  
   
  private SymbolData _getQualifiedSymbolData(String className, SourceInfo si, boolean resolve, boolean fromClassFile, 
                                             boolean addError) {
    _log.log("_getQualifiedSymbolData called on '" + className + "'");
   
    
    
    
    
    SymbolData sd = symbolTable.get(className);


    
    _log.log("Corresponding symbolTable entry = " + sd);
    if (sd != null && (! resolve || ! sd.isContinuation() || fromClassFile)) { 
      _log.log("Returning " + sd);
      return sd; 
    }
    
    
    if (isJavaLibraryClass(className)) {
      _log.log("Calling  _classFile2SymbolData");
      return _classFile2SymbolData(className, null);
    }
    else if (resolve) {  
      SymbolData newSd = _getSymbolData_FromFileSystem(className, si, resolve, addError);
      if (newSd != SymbolData.NOT_FOUND) {
        _log.log("Returning " + sd + " from file system");
        return newSd;
      }
      else if (sd != null && sd.isContinuation()) return sd;
      if (addError) {
        _addAndIgnoreError("The class " + className + " was not found.", new NullLiteral(si));
      }
    }
    _log.log("Returning null");
    return null;
  }
  
  
  public ArrayData defineArraySymbolData(SymbolData eltSd, LanguageLevelVisitor llv, SourceInfo si) {
    ArrayData arraySd = new ArrayData(eltSd, llv, si);
    symbolTable.put(arraySd.getName(), arraySd);
    return arraySd;
  }
        
  
  private SymbolData _getArraySymbolData(String className, SourceInfo si, boolean resolve, boolean fromClassFile, 
                                         boolean addError, boolean checkImportedPackages) {
    
    SymbolData innerSd = getSymbolDataHelper(className.substring(0, className.length() - 2), si, resolve, fromClassFile, 
                                             addError, checkImportedPackages);
    if (innerSd != null) {
      SymbolData sd = symbolTable.get(innerSd.getName() + "[]");
      if (sd != null) { return sd; }
      else { return defineArraySymbolData(innerSd, this, si); }
    }
    else { return null; }
  }
  
  
  private SymbolData _getSymbolData_FromCurrFile(String qualifiedClassName, SourceInfo si, boolean resolve) {
    SymbolData sd = symbolTable.get(qualifiedClassName);
    if (sd == null || (sd.isContinuation() && resolve)) {
      
      return _lookupFromClassesToBeParsed(qualifiedClassName, si, resolve);
    }
    else return sd;
  }
  
  
  private SymbolData _getSymbolData_FromFileSystem(final String qualifiedClassName, SourceInfo si, boolean resolve,
                                                   boolean addError) {
    
    SymbolData sd = symbolTable.get(qualifiedClassName);
    if (sd != null && (! resolve || ! sd.isContinuation())) { return sd; }
    
    
    Pair<TypeDefBase, LanguageLevelVisitor> pair = _classesToBeParsed.get(qualifiedClassName);
    if (pair != null) return _lookupFromClassesToBeParsed(qualifiedClassName, si, resolve);
    
    
    String qualifiedClassNameWithSlashes = 
      qualifiedClassName.replace('.', System.getProperty("file.separator").charAt(0));
    File _fileParent = _file.getParentFile();
    
    String programRoot = (_fileParent == null) ? "" : _fileParent.getAbsolutePath();  
    assert (programRoot != null); 
    
    final String path;  

    if (programRoot.length() > 0) {      
      String packageWithSlashes = _package.replace('.', System.getProperty("file.separator").charAt(0));
      
      int indexOfPackage = programRoot.lastIndexOf(packageWithSlashes); 
      if (indexOfPackage < 0) path = qualifiedClassName;
      else {
        programRoot = programRoot.substring(0, indexOfPackage);
        path = programRoot + System.getProperty("file.separator") + qualifiedClassNameWithSlashes;
      }
    }
    else {
      path = qualifiedClassNameWithSlashes;  
    }
    
    String dirPath; 
    String newPackage = ""; 
    int lastSlashIndex = qualifiedClassNameWithSlashes.lastIndexOf(System.getProperty("file.separator"));
    if (lastSlashIndex != -1) {
      String newPackageWithSlashes = qualifiedClassNameWithSlashes.substring(0, lastSlashIndex);
      dirPath = programRoot + System.getProperty("file.separator") + newPackageWithSlashes;
      newPackage = newPackageWithSlashes.replace(System.getProperty("file.separator").charAt(0), '.');
    }
    else {
      int lastPathSlashIndex = path.lastIndexOf(System.getProperty("file.separator"));
      if (lastPathSlashIndex != -1) dirPath = path.substring(0, lastPathSlashIndex);
      else dirPath = "";
    }
    
    
    
    File classFile = new File(path + ".class");  
    
    
    File[] sourceFiles = new File(dirPath).listFiles(new FileFilter() {
      public boolean accept(File f) {
        try {
          f = f.getCanonicalFile();
          return new File(path + ".dj0").getCanonicalFile().equals(f) ||
            new File(path + ".dj1").getCanonicalFile().equals(f) ||
            new File(path + ".dj2").getCanonicalFile().equals(f) ||
            new File(path + ".java").getCanonicalFile().equals(f);
        }
        catch (IOException e) { return false; }
      }});
    
    File sourceFile = null; 
    if (sourceFiles != null) {
      long mostRecentTime = 0;
      for (File f : sourceFiles) {
        long currentLastModified = f.lastModified();
        if (f.exists() && mostRecentTime < currentLastModified) {
          mostRecentTime = currentLastModified;
          sourceFile = f;
        }
      }
    }

    
    
    
    
    
    

    if (sourceFile != null) {
      
      if (! resolve) { 
        assert sd == null;
        sd = addSymbolData(si, qualifiedClassName); 
        return sd;





        }
      
      long classModTime = classFile.lastModified();
      if (classModTime == 0L) return null;  
      
      if (sourceFile.lastModified() > classModTime) { 
        if (addError) {
          _addAndIgnoreError("The file " + sourceFile.getAbsolutePath() + 
                             " needs to be recompiled; it's class files either do not exist or are out of date.",
                             new NullLiteral(si));
        }
        return null;
      }
    }
    
    
    if (classFile.exists()) {
      
     _log.log("Reading classFile " + qualifiedClassName);
      sd = _classFile2SymbolData(qualifiedClassName, programRoot);
      if (sd == null) {
        if (addError) {
          _addAndIgnoreError("File " + classFile + " is not a valid class file.", null);
        }
        return null;
      }
      _log.log("Returning symbol constructed by loading class file");
      return sd;
    }
    return SymbolData.NOT_FOUND;
  }
 
  
  protected SymbolData getSymbolData(String className, SourceInfo si) {
    return getSymbolData(className, si, false, false, true, true);
  }
  
    
  public SymbolData getSymbolData(String className, SourceInfo si, boolean resolve) {
    SymbolData sd = getSymbolData(className, si, resolve, false, true, true);
    return sd;
  }
  
    
  protected SymbolData getSymbolData(String className, SourceInfo si, boolean resolve, boolean fromClassFile) {
    return getSymbolData(className, si, resolve, fromClassFile, true, true);
  }
  
    
  protected SymbolData getSymbolData(String className, SourceInfo si, boolean resolve, boolean fromClassFile, 
                                     boolean addError) {
    return this.getSymbolData(className, si, resolve, fromClassFile, addError, true);
  }
  
  
  protected SymbolData getSymbolData(String className, SourceInfo si, boolean resolve, boolean fromClassFile, 
                                     boolean addError, boolean checkImportedStuff) {
 
    if (className.endsWith("[]")) { 
      String rawClassName = className.substring(0, className.length() - 2);
      SymbolData sd = getSymbolData(rawClassName, si, resolve, fromClassFile, addError, checkImportedStuff);
      if (sd == null) return null;   
      ArrayData ad = new ArrayData(sd, this, si);
      symbolTable.put(ad.getName(), ad);
      return ad;
    }
    
    
    int indexOfNextDot = className.indexOf(".");
    int indexOfNextDollar = className.indexOf("$");   
    if (indexOfNextDot == -1 && indexOfNextDollar == -1)
      return getSymbolDataHelper(className, si, resolve, fromClassFile, addError, checkImportedStuff);
    
    
    indexOfNextDot = 0;   
    SymbolData sd;
    int length = className.length();
    while (indexOfNextDot != length) {
      indexOfNextDot = className.indexOf(".", indexOfNextDot + 1);
      if (indexOfNextDot == -1) { indexOfNextDot = length; }
      String prefix = className.substring(0, indexOfNextDot);
      

      sd = getSymbolDataHelper(prefix, si, resolve, fromClassFile, false, checkImportedStuff);


      if (sd != null) { 
        String outerClassName = prefix;
        String innerClassName = "";
        if (indexOfNextDot != length) {
          SymbolData outerClassSD = sd;
          innerClassName = className.substring(indexOfNextDot + 1);

          sd = outerClassSD.getInnerClassOrInterface(innerClassName);

          if (sd == null) { 
            sd = addInnerSymbolData(si, outerClassName + "." + innerClassName, outerClassSD);
          }
          return sd;
        }
        else if (sd == SymbolData.AMBIGUOUS_REFERENCE) {
          _addAndIgnoreError("Ambiguous reference to class or interface " + className, new NullLiteral(si));
          return null;
        }
        else if (sd != null && sd != SymbolData.NOT_FOUND) { return sd; }
      }
      
    }
    
    
    if (! fromClassFile && addError) {
      
      String newName = className;
      int lastDollar = newName.lastIndexOf("$");
      newName = newName.substring(lastDollar + 1, newName.length());

      _addAndIgnoreError("Invalid class name " + newName, new NullLiteral(si));

    }
    return null;
  }
  
  
  protected SymbolData getSymbolData(TypeData lhs, String name, SourceInfo si, boolean addError) {
    
    boolean resolve = false;
    boolean fromClassFile = false;
    boolean checkImportedStuff = false;
    
    if (lhs == null) {return null;}
    
    else if (lhs instanceof PackageData) {
      String className = lhs.getName() + "." + name;
      return getSymbolDataHelper(className, si, resolve, fromClassFile, addError, checkImportedStuff);
    }
    
    else { 
      SymbolData result = lhs.getInnerClassOrInterface(name);
      if (result == SymbolData.AMBIGUOUS_REFERENCE) {
        if (addError) { _addAndIgnoreError("Ambiguous reference to class or interface " + name, new NullLiteral(si)); }
        return null;
      }
      return result;
    }
  }
  
  
  protected SymbolData getSymbolDataHelper(String className, SourceInfo si, boolean resolve, boolean fromClassFile, 
                                           boolean addError, boolean checkImportedStuff) {
    
    SymbolData sd = _getSymbolData_Primitive(className);
    if (sd != null) { return sd; }
    
    
    if (className.endsWith("[]")) {
      return _getArraySymbolData(className, si, resolve, fromClassFile, addError, checkImportedStuff);
    }
    
    
    if (className.indexOf(".") != -1) return _getQualifiedSymbolData(className, si, resolve, fromClassFile, addError);
    
    String name = null; 
    String qualifiedClassName = getQualifiedClassName(className);  



    
    if (_classNamesInThisFile.contains(qualifiedClassName)) {
      return _getSymbolData_FromCurrFile(qualifiedClassName, si, resolve);
    }
   
    
    
    
    Iterator<String> iter = _importedFiles.iterator();
    if (checkImportedStuff) {
      while (iter.hasNext()) {
        String s = iter.next();
        if (s.endsWith(className)) {
          
          SymbolData tempSd = symbolTable.get(s);
          

          if (resolve && tempSd != null && tempSd.isContinuation()) {

            return getSymbolData(s, si, resolve, fromClassFile, addError, false);  
          }
          else return tempSd;
        }
      }
    }
    
    
    
    
    if (className.indexOf(".") == -1 || (!_package.equals("") && className.startsWith(_package))) {
      sd = symbolTable.get(qualifiedClassName);


      if (sd == null || (sd.isContinuation() && resolve)) {
        sd = _getSymbolData_FromFileSystem(qualifiedClassName, si, resolve, addError);


        if (sd != null && sd != SymbolData.NOT_FOUND) return sd;
      }      
      else {
        
        
        return sd;
      }
    }

    SymbolData resultSd = null;
    
    if (checkImportedStuff) {

      iter = _importedPackages.iterator();
      while (iter.hasNext()) {
        String s = iter.next() + "." + className;

        SymbolData tempSd;
        tempSd = getSymbolDataHelper(s, si, resolve, fromClassFile, false, false);


        if (tempSd != null) {
          if (resultSd == null) resultSd = tempSd;
          else {  
            if (addError) {
              _addAndIgnoreError("The class name " + className + " is ambiguous.  It could be " + resultSd.getName() + 
                                 " or " + tempSd.getName(), new NullLiteral(si));
              return null;
            }
          }
        }
      }
    }
    return resultSd;
  }
  
  
  private ModifiersAndVisibility _createMav(int flags) {
    LinkedList<String> strings = new LinkedList<String>();
    if (Modifier.isAbstract(flags)) { strings.addLast("abstract"); }
    if (Modifier.isFinal(flags)) { strings.addLast("final"); }
    if (Modifier.isNative(flags)) { strings.addLast("native"); }
    if (Modifier.isPrivate(flags)) { strings.addLast("private"); }
    if (Modifier.isProtected(flags)) { strings.addLast("protected"); }
    if (Modifier.isPublic(flags)) { strings.addLast("public"); }
    if (Modifier.isStatic(flags)) { strings.addLast("static"); }
    if (Modifier.isStrict(flags)) { strings.addLast("strictfp"); }
    if (Modifier.isSynchronized(flags)) { strings.addLast("synchronized"); }
    if (Modifier.isTransient(flags)) { strings.addLast("transient"); }
    if (Modifier.isVolatile(flags)) { strings.addLast("volatile"); }
    return new ModifiersAndVisibility(SourceInfo.NO_INFO, strings.toArray(new String[strings.size()]));
  }
  
  
  protected String getQualifiedClassName(String className) {

    if (!_package.equals("") && ! className.startsWith(_package)) return _package + "." + className;
    else return className;
  }
  
  
  protected SymbolData addInnerSymbolData(SourceInfo si, String qualifiedTypeName, Data enclosing) {
    SymbolData sd = new SymbolData(qualifiedTypeName); 
    SymbolData enclosingSD = enclosing.getSymbolData();
    
    symbolTable.put(qualifiedTypeName, sd);  
    enclosing.getSymbolData().addInnerClass(sd);
    sd.setOuterData(enclosingSD);
    continuations.put(qualifiedTypeName, new Pair<SourceInfo, LanguageLevelVisitor>(si, this));

    return sd;
  }
  





















































































































  
  
  protected SymbolData addSymbolData(SourceInfo si, String qualifiedClassName) {
    SymbolData sd = new SymbolData(qualifiedClassName);  
    continuations.put(qualifiedClassName, new Pair<SourceInfo, LanguageLevelVisitor>(si, this));
    symbolTable.put(qualifiedClassName, sd);

    return sd;
  }
  
  
  protected SymbolData defineSymbolData(TypeDefBase typeDefBase, String qualifiedClassName) {
    String name = qualifiedClassName;  
    SymbolData sd = symbolTable.get(name);
    if (sd != null && ! sd.isContinuation()) {
      _addAndIgnoreError("The class or interface " + name + " has already been defined.", typeDefBase);
      return null;
    }

    
    
    LinkedList<SymbolData> interfaces = new LinkedList<SymbolData>();
    SymbolData tempSd;
    
    
    ReferenceType[] rts = typeDefBase.getInterfaces();
    for (ReferenceType rt: rts) {
      tempSd = getSymbolData(rt.getName(), rt.getSourceInfo(), false, false, false);
      
      if (tempSd != null) interfaces.addLast(tempSd);  
      else if (qualifiedClassName.indexOf(".") != -1) { 
        
        String qualifyingPart = qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf("."));
        tempSd = getSymbolData(qualifyingPart + "." + rt.getName(), rt.getSourceInfo(), false, false, false);
        if (tempSd == null) {
          String tempName = qualifyingPart + "." + rt.getName();
          tempSd = new SymbolData(tempName);
          tempSd.setInterface(true);

          continuations.put(tempName, new Pair<SourceInfo, LanguageLevelVisitor>(rt.getSourceInfo(), this));          
        }
        interfaces.addLast(tempSd);
      }
      else if (tempSd == null) {  
        String tempName = rt.getName();
        _log.log("CREATING continuation " + tempName + " with SourceInfo " + rt.getSourceInfo());

        tempSd = new SymbolData(tempName);
        tempSd.setInterface(true);
        continuations.put(tempName, new Pair<SourceInfo, LanguageLevelVisitor>(rt.getSourceInfo(), this));    
      }
    }
    
    if (sd == null) { 
      sd = new SymbolData(name);
      symbolTable.put(name, sd);
    }
    
    
    sd.setPackage(_package);
     
    SymbolData superClass = null;
    
    
    
    if (typeDefBase instanceof InterfaceDef) {
      
      superClass = getSymbolData("Object", typeDefBase.getSourceInfo(), false);
      sd.setInterface(true);
    }
    
    else if (typeDefBase instanceof ClassDef) {
      ClassDef cd = (ClassDef) typeDefBase;
      ReferenceType rt = cd.getSuperclass();
      
      
      
      String superClassName = rt.getName();




      SourceInfo si = rt.getSourceInfo();
      
      
      superClass = getSymbolData(superClassName, si, false); 
      
      if (superClass == null) {
        
        superClass = addSymbolData(si, superClassName);
      }
      sd.setInterface(false);
    }
    
    else { throw new RuntimeException("Internal Program Error: typeDefBase was not a ClassDef or InterfaceDef." + 
                                      "  Please report this bug."); }
    
    
    
    
    
    sd.setMav(typeDefBase.getMav());
    sd.setTypeParameters(typeDefBase.getTypeParameters());
    sd.setSuperClass(superClass);
    sd.setInterfaces(interfaces);
    sd.setIsContinuation(false);
    _log.log("REMOVING continuation " + sd.getName());
    continuations.remove(sd.getName());
    
    if (! sd.isInterface()) { LanguageLevelConverter._newSDs.put(sd, this); }
    return sd;
  }
  
  
  protected SymbolData defineInnerSymbolData(TypeDefBase typeDefBase, String qualifiedTypeName, Data enclosing) { 
        
    String name = qualifiedTypeName;  
    SymbolData sd = symbolTable.get(name);
    if (sd != null && ! sd.isContinuation()) {
      _addAndIgnoreError("The class or interface " + name + " has already been defined.", typeDefBase);
      return null;
    }
    
    
    LinkedList<SymbolData> interfaces = new LinkedList<SymbolData>();
    SymbolData tempSd;
    ReferenceType[] rts = typeDefBase.getInterfaces();
    for (int i = 0; i < rts.length; i++) {
      SourceInfo si = rts[i].getSourceInfo();
      String tempName = rts[i].getName();
      tempSd = getSymbolData(tempName, si, false, false, false);
      
      if (tempSd != null) { interfaces.addLast(tempSd); }
      
      else if (enclosing instanceof SymbolData) {
        
        tempSd = enclosing.getInnerClassOrInterface(tempName);
        if (tempSd == null) {
          String qualifyingPart = qualifiedTypeName.substring(0, qualifiedTypeName.lastIndexOf("."));
          String qualifiedTempName = qualifyingPart + "." + tempName;
          
          tempSd = new SymbolData(qualifiedTempName);
          tempSd.setInterface(true);
          enclosing.getSymbolData().addInnerInterface(tempSd); 
          tempSd.setOuterData(enclosing);

          continuations.put(qualifiedTempName, new Pair<SourceInfo, LanguageLevelVisitor>(si, this));          
        }
        interfaces.addLast(tempSd);
      }
      
      else {
        _addAndIgnoreError("Cannot resolve interface " + rts[i].getName(), rts[i]);
        return null;
      }
    }
        
    
    if (sd == null) { 
      sd = new SymbolData(qualifiedTypeName);
      sd.setOuterData(enclosing);
      if (typeDefBase instanceof ClassDef) { enclosing.getSymbolData().addInnerClass(sd); }
      else { 
        enclosing.getSymbolData().addInnerInterface(sd); 
      }
    }
    
    sd.setPackage(_package);
    
    SymbolData superClass = null;
    
    if (typeDefBase instanceof InterfaceDef) {
      
      superClass = getSymbolData("Object", typeDefBase.getSourceInfo(), false);
      sd.setInterface(true);
    }
    else if (typeDefBase instanceof ClassDef) {
      ClassDef cd = (ClassDef) typeDefBase;
      ReferenceType rt = cd.getSuperclass();
      String superClassName = rt.getName();
      superClass = getSymbolData(superClassName, rt.getSourceInfo(), false, false, false);
      
      if (superClass == null) {  
        superClass = enclosing.getInnerClassOrInterface(superClassName);
        if (superClass == null) {
          String qualifyingPart = qualifiedTypeName.substring(0, qualifiedTypeName.lastIndexOf("."));
          superClass = new SymbolData(qualifyingPart + "." + superClassName);
          enclosing.addInnerClass(superClass);
          superClass.setOuterData(enclosing);

          continuations.put(superClassName, new Pair<SourceInfo, LanguageLevelVisitor>(rt.getSourceInfo(), this)); 
        }
      }
      sd.setInterface(false);
    }
    
    else throw new RuntimeException("Internal Program Error: typeDefBase was not a ClassDef or InterfaceDef." + 
                                     "  Please report this bug.");
    
    
    
    
    
    sd.setMav(typeDefBase.getMav());
    sd.setTypeParameters(typeDefBase.getTypeParameters());
    sd.setSuperClass(superClass);
    sd.setInterfaces(interfaces);
    sd.setIsContinuation(false);
    _log.log("REMOVING continuation " + sd.getName());
    continuations.remove(sd.getName());
    if (! sd.isInterface()) { LanguageLevelConverter._newSDs.put(sd, this); }
    return sd;
  }
    
  
  protected VariableData[] formalParameters2VariableData(FormalParameter[] fps, Data enclosing) {

    
    VariableData[] varData = new VariableData[fps.length];
    VariableDeclarator vd;
    String[] mav = new String[] {"final"};
        
    for (int i = 0; i < varData.length; i++) {
      vd = fps[i].getDeclarator();
      String name = vd.getName().getText();  
      String typeName = vd.getType().getName();
      SourceInfo si = vd.getType().getSourceInfo();
      SymbolData type = getSymbolData(typeName, si);
      
      if (type == null) {
        
        type = enclosing.getInnerClassOrInterface(typeName);

      }
      
      if (type == null) { 
        String qualifiedTypeName = enclosing.getSymbolData().getName() + "." + typeName;
        if (_innerClassesToBeParsed.contains(qualifiedTypeName)) {  
          type = addInnerSymbolData(si, qualifiedTypeName, enclosing);
        }
        else { 
          type = addSymbolData(si, typeName);
        }
      }
      
      varData[i] = new VariableData(name, new ModifiersAndVisibility(SourceInfo.NO_INFO, mav), type, true, enclosing);
      varData[i].gotValue();
      varData[i].setIsLocalVariable(true);
    }
   
    return varData;
  }
  
  
  protected MethodData createMethodData(MethodDef that, SymbolData sd) {


    that.getMav().visit(this);
    that.getName().visit(this);
    
    
    String[] throwStrings = referenceType2String(that.getThrows());
    
    
    String rtString = that.getResult().getName();
    SymbolData returnType;
    
    if (rtString.equals("void"))  returnType = SymbolData.VOID_TYPE;
    else returnType = getSymbolData(rtString, that.getResult().getSourceInfo());
    
    MethodData md = MethodData.make(that.getName().getText(), that.getMav(), that.getTypeParams(), returnType, 
                                    new VariableData[0], throwStrings, sd, that);

    
    VariableData[] vds = formalParameters2VariableData(that.getParams(), md);
    
    if (_checkError()) {  
      return md;
    }
    
    md.setParams(vds);
    
    
    if (! md.addVars(vds)) { 
      _addAndIgnoreError("You cannot have two method parameters with the same name", that);      
    }
    return md;
  }
  
  
  protected VariableData[] _variableDeclaration2VariableData(VariableDeclaration vd, Data enclosing) {
    LinkedList<VariableData> vds = new LinkedList<VariableData>();
    ModifiersAndVisibility mav = vd.getMav();
    VariableDeclarator[] declarators = vd.getDeclarators();
    for (VariableDeclarator declarator: declarators) {
      declarator.visit(this); 
      Type type = declarator.getType();
      String name = declarator.getName().getText();
      SymbolData sd = handleDeclarator(type, name, enclosing);
      if (sd != null) {
        boolean initialized = declarator instanceof InitializedVariableDeclarator;
        

        VariableData vdata = new VariableData(name, mav, sd, initialized, enclosing); 
        vdata.setHasInitializer(initialized);

        vds.addLast(vdata); 
      }
      else {

        _addAndIgnoreError("Class or Interface " + name + " not found", type);
      }
    }

    return vds.toArray(new VariableData[vds.size()]);
  }
  
  
  SymbolData handleDeclarator(Type type, String name, Data enclosing) {

    String typeName = type.getName();
    SourceInfo si = type.getSourceInfo();
    SymbolData sd = getSymbolData(typeName, si);
    
    if (sd == null) {
      
      sd = enclosing.getInnerClassOrInterface(typeName);
    }
    
    if (sd == null) { 
      String qualifiedTypeName = enclosing.getSymbolData().getName() + "." + typeName;
      if (_innerClassesToBeParsed.contains(qualifiedTypeName)) {  
        
        sd = addInnerSymbolData(si, qualifiedTypeName, enclosing);
      }
      else { 
        sd = addSymbolData(si, typeName);
      }
    }
    return sd;
  }
                               
  
  protected static void _addError(String message, JExpressionIF that) {

    _errorAdded = true;
    Pair<String, JExpressionIF> p = new Pair<String, JExpressionIF>(message, that);
    if (! errors.contains(p)) errors.addLast(p);
  }
  
  
  protected static void _addAndIgnoreError(String message, JExpressionIF that) {

    if (_errorAdded) {
      throw new RuntimeException("Internal Program Error: _addAndIgnoreError called while _errorAdded was true." + 
                                 "  Please report this bug.");
    }
    _errorAdded = false;
    errors.addLast(new Pair<String, JExpressionIF>(message, that));
  }
  
  protected boolean prune(JExpressionIF node) { return _checkError(); }
  
  
  protected static boolean _checkError() {
    if (_errorAdded) {
      _errorAdded = false;
      return true;
    }
    else return false;
  }
  
  
  public void _badModifiers(String first, String second, JExpressionIF that) {
    _addError("Illegal combination of modifiers. Can't use " + first + " and " + second + " together.", that);
  }
  
  
  public Void forModifiersAndVisibilityDoFirst(ModifiersAndVisibility that) {
    String[] modifiersAndVisibility = that.getModifiers();
    Arrays.sort(modifiersAndVisibility);
    if (modifiersAndVisibility.length > 0) {
      String s = modifiersAndVisibility[0];
      
      for (int i = 1; i < modifiersAndVisibility.length; i++) {
        if (s.equals(modifiersAndVisibility[i])) {
          _addError("Duplicate modifier: " + s, that);
        }
        s = modifiersAndVisibility[i];
      }
      
      
      String visibility = "package";
      boolean isAbstract = false;
      boolean isStatic = false;
      boolean isFinal = false;
      boolean isSynchronized = false;
      boolean isStrictfp = false;
      boolean isTransient = false;
      boolean isVolatile = false;
      boolean isNative = false;
      for (int i = 0; i < modifiersAndVisibility.length; i++) {
        s = modifiersAndVisibility[i];
        if (s.equals("public") || s.equals("protected") || s.equals("private")) {
          if (! visibility.equals("package")) _badModifiers(visibility, s, that);
          else if (s.equals("private") && isAbstract) _badModifiers("private", "abstract", that);
          else visibility = s;
        }
        else if (s.equals("abstract")) isAbstract = true;
        else if (s.equals("final")) { 
          isFinal = true;
          if (isAbstract) _badModifiers("final", "abstract", that);
        }
        else if (s.equals("native")) { 
          isNative = true;
          if (isAbstract) _badModifiers("native", "abstract", that);
        }
        else if (s.equals("synchronized")) { 
          isSynchronized = true;
          if (isAbstract) _badModifiers("synchronized", "abstract", that);
        }
        else if (s.equals("volatile")) { 
          isVolatile = true;
          if (isFinal) _badModifiers("final", "volatile", that);
        }
      }
      return forJExpressionDoFirst(that);  
    }
    return null;
  }
  
  
  public Void forClassDefDoFirst(ClassDef that) {
    String name = that.getName().getText();  
    Iterator<String> iter = _importedFiles.iterator();
    while (iter.hasNext()) {
      String s = iter.next();
      if (s.endsWith(name) && ! s.equals(getQualifiedClassName(name))) {
        _addAndIgnoreError("The class " + name + " was already imported.", that);
      }
    }
    
    
    String[] mavStrings = that.getMav().getModifiers();
    if (! (that instanceof InnerClassDef)) {
      for (int i = 0; i < mavStrings.length; i++) {
        if (mavStrings[i].equals("private")) {
          _addAndIgnoreError("Top level classes cannot be private", that);
        }
      }
    }
    
    
    SymbolData javaLangClass = 
      getSymbolData("java.lang." + that.getName().getText(), that.getSourceInfo(), true, false, false, false);
    if (that.getName().getText().equals("TestCase") || (javaLangClass != null && ! javaLangClass.isContinuation())) {
      _addError("You cannot define a class with the name " + that.getName().getText() + 
                " because that class name is reserved." +
                "  Please choose a different name for this class", that);
    }
    return forTypeDefBaseDoFirst(that);
  }
  
  
  public Void forInterfaceDefDoFirst(InterfaceDef that) {
    
    String[] mavStrings = that.getMav().getModifiers();
    for (int i = 0; i < mavStrings.length; i++) {
      if (mavStrings[i].equals("private")) {
        _addAndIgnoreError("Top level interfaces cannot be private", that);
      }
      if (mavStrings[i].equals("final")) {
        _addAndIgnoreError("Interfaces cannot be final", that);
      }
    }
    return forTypeDefBaseDoFirst(that);
  }
  
  
  public Void forInnerInterfaceDefDoFirst(InnerInterfaceDef that) {
    String[] mavStrings = that.getMav().getModifiers();
    for (int i = 0; i < mavStrings.length; i++) {
      if (mavStrings[i].equals("final")) {
        _addAndIgnoreError("Interfaces cannot be final", that);
      }
    }
    return forTypeDefBaseDoFirst(that);  
  }
  
  
  public Void forPackageStatementOnly(PackageStatement that) {
    CompoundWord cWord = that.getCWord();
    Word[] words = cWord.getWords();
    String newPackage;
    String separator = System.getProperty("file.separator");
    if (words.length > 0) {
      _package = words[0].getText();
      newPackage = _package;
      for (int i = 1; i < words.length; i++) {
        String temp = words[i].getText();
        newPackage = newPackage + separator + temp;
        _package = _package + "." + temp;
      }    
      String directory = _file.getParent();
      if (directory == null || !directory.endsWith(newPackage)) {
        _addAndIgnoreError("The package name must mirror your file's directory.", that);
      }
    }
    
    
    
    
    getSymbolData(_package, that.getSourceInfo(), false, false, false);
    return forJExpressionOnly(that);
  }
  
  
  
  public Void forClassImportStatementOnly(ClassImportStatement that) {
    CompoundWord cWord = that.getCWord();
    Word[] words = cWord.getWords();
    
    
    for (int i = 0; i<_importedFiles.size(); i++) {
      String name = _importedFiles.get(i);
      int indexOfLastDot = name.lastIndexOf(".");
      if (indexOfLastDot != -1 && 
          (words[words.length-1].getText()).equals(name.substring(indexOfLastDot + 1, name.length()))) {
        _addAndIgnoreError("The class " + words[words.length-1].getText() + " has already been imported.", that);
        return null;
      }
    }
    
    StringBuilder nameBuff = new StringBuilder(words[0].getText());
    for (int i = 1; i < words.length; i++) {nameBuff.append("." + words[i].getText());}
    
    String qualifiedTypeName = nameBuff.toString();
    
    
    
    int indexOfLastDot = qualifiedTypeName.lastIndexOf(".");
    if (indexOfLastDot != -1) {
      if (_package.equals(qualifiedTypeName.substring(0, indexOfLastDot))) {
        _addAndIgnoreError("You do not need to import " + qualifiedTypeName 
                             + ".  It is in your package so it is already visible", 
                           that);
        return null;
      }
    }
    
    
    _importedFiles.addLast(qualifiedTypeName);  
    
    
    SymbolData sd = symbolTable.get(qualifiedTypeName);
    if (sd == null) {
      
      

      sd = addSymbolData(that.getSourceInfo(), qualifiedTypeName);
    }
    return forImportStatementOnly(that);
  }
  
  
  public Void forPackageImportStatementOnly(PackageImportStatement that) { 
    CompoundWord cWord = that.getCWord();
    Word[] words = cWord.getWords();
    StringBuilder tempBuff = new StringBuilder(words[0].getText());
    for (int i = 1; i < words.length; i++) { tempBuff.append("." + words[i].getText()); }
    String temp = tempBuff.toString();
    
    
    
    if (_package.equals(temp)) {
      _addAndIgnoreError("You do not need to import package " + temp + 
                         ". It is your package so all public classes in it are already visible.", that);
      return null;
    }

    _importedPackages.addLast(temp);
    
    return forImportStatementOnly(that);
  }
  
  
  public Void forConcreteMethodDefDoFirst(ConcreteMethodDef that) {
    ModifiersAndVisibility mav = that.getMav();
    String[] modifiers = mav.getModifiers();
    
    for (int i = 0; i < modifiers.length; i++) {
      if (modifiers[i].equals("abstract")) {
        _addError("Methods that have a braced body cannot be declared \"abstract\"", that);
        break;
      }
    }
    return super.forConcreteMethodDefDoFirst(that);
  }
  
  
  public Void forAbstractMethodDefDoFirst(AbstractMethodDef that) {
    ModifiersAndVisibility mav = that.getMav();
    String[] modifiers = mav.getModifiers();
    
    if (Utilities.isStatic(modifiers)) _badModifiers("static", "abstract", that);
    return super.forAbstractMethodDefDoFirst(that);
  }
  
  
  public Void forShiftAssignmentExpressionDoFirst(ShiftAssignmentExpression that) { return null; }
  public Void forBitwiseAssignmentExpressionDoFirst(BitwiseAssignmentExpression that) { return null; }
  public Void forBitwiseBinaryExpressionDoFirst(BitwiseBinaryExpression that) { return null; }
  public Void forBitwiseOrExpressionDoFirst(BitwiseOrExpression that) { return null; }
  public Void forBitwiseXorExpressionDoFirst(BitwiseXorExpression that) { return null; }
  public Void forBitwiseAndExpressionDoFirst(BitwiseAndExpression that) { return null; }
  public Void forBitwiseNotExpressionDoFirst(BitwiseNotExpression that) { return null; }
  public Void forShiftBinaryExpressionDoFirst(ShiftBinaryExpression that) { return null; }
  public Void forBitwiseNotExpressionDoFirst(ShiftBinaryExpression that) { return null; }
  
  
  public Void forEmptyExpressionDoFirst(EmptyExpression that) {
    _addAndIgnoreError("You appear to be missing an expression here", that);
    return null;
  }
  
  
  public Void forNoOpExpressionDoFirst(NoOpExpression that) {
    _addAndIgnoreError("You are missing a binary operator here", that);
    return null;
  }
  
  
  public Void forSourceFileDoFirst(SourceFile that) {
    
    for (int i = 0; i < that.getTypes().length; i++) {
      if (that.getTypes()[i] instanceof ClassDef) {
        ClassDef c = (ClassDef) that.getTypes()[i];
        String superName = c.getSuperclass().getName();
        if (superName.equals("TestCase") || superName.equals("junit.framework.TestCase")) {
          
          if (that.getTypes().length > 1) {
            _addAndIgnoreError("TestCases must appear in files by themselves in functional code", c);
          }
        }
      }
    }
    return null; 
  }
  
  
  public Void forSourceFile(SourceFile that) {

    forSourceFileDoFirst(that);  
    if (prune(that)) return null;
    
    
    for (int i = 0; i < that.getPackageStatements().length; i++) that.getPackageStatements()[i].visit(this);
    for (int i = 0; i < that.getImportStatements().length; i++) that.getImportStatements()[i].visit(this);
    if (! _importedPackages.contains("java.lang")) _importedPackages.addFirst("java.lang");
    
    TypeDefBase[] types = that.getTypes();
    
    _classNamesInThisFile = new LinkedList<String>();
    for (int i = 0; i < types.length; i++) {
      
      
      String qualifiedClassName = getQualifiedClassName(types[i].getName().getText());
      _classNamesInThisFile.addFirst(qualifiedClassName);

      _log.log("Adding " + qualifiedClassName + " to _classesToBeParsed");
      _classesToBeParsed.put(qualifiedClassName, new Pair<TypeDefBase, LanguageLevelVisitor>(types[i], this));
    }
    
    for (int i = 0; i < types.length; i++) {
      
      String qualifiedClassName = getQualifiedClassName(types[i].getName().getText());
      
      
      if (_classesToBeParsed.containsKey(qualifiedClassName)) {
        types[i].visit(this);
      }
    }
    
    return forSourceFileOnly(that);
  }
  
  
  public Void forSimpleNameReference(SimpleNameReference that) {
    that.visit(new ResolveNameVisitor());
    return null;
  }
  
  
  public Void forComplexNameReference(ComplexNameReference that) {
    that.visit(new ResolveNameVisitor());
    return null;
  }
  
  
  public Void forVariableDeclaration(VariableDeclaration that) {
    forVariableDeclarationDoFirst(that);
    if (prune(that)) return null;
    that.getMav().visit(this);
    return forVariableDeclarationOnly(that);
  }
  
  
  protected static void addGeneratedMethod(SymbolData sd, MethodData md) {
    MethodData rmd = SymbolData.repeatedSignature(sd.getMethods(), md);
    if (rmd == null) {
      sd.addMethod(md, true);
      md.setGenerated(true);
    }
    
    else if (!(getUnqualifiedClassName(sd.getName()).equals(md.getName()))) {
      
      _addAndIgnoreError("The method " + md.getName() + " is automatically generated, and thus you cannot override it", 
                         rmd.getJExpression());
    }
  }
  
  
  public void createConstructor(SymbolData sd) {
    if (LanguageLevelConverter.isAdvancedFile(_file)) return;
    
    SymbolData superSd = sd.getSuperClass();
    
    
    if (sd.isContinuation()) return;
    
    LinkedList<MethodData> superMethods = superSd.getMethods();
    String superUnqualifiedName = getUnqualifiedClassName(superSd.getName());
    
    LanguageLevelVisitor sslv = LanguageLevelConverter._newSDs.remove(superSd);
    if (sslv != null) {sslv.createConstructor(superSd);}
    
    
    MethodData superConstructor = null;
    Iterator<MethodData> iter = superMethods.iterator();
    while (iter.hasNext()) {
      MethodData superMd = iter.next();
      if (superMd.getName().equals(superUnqualifiedName)) {
        if (superConstructor == null || superMd.getParams().length < superConstructor.getParams().length) {
          superConstructor = superMd;
        }
      }
    }
    
    String name = getUnqualifiedClassName(sd.getName());
    MethodData md = new MethodData(name,
                                   new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}), 
                                   new TypeParameter[0], 
                                   sd, 
                                   new VariableData[0], 
                                   new String[0], 
                                   sd,
                                   null);
    
    LinkedList<VariableData> params = new LinkedList<VariableData>();
    if (superConstructor != null) {
      for (VariableData superParam : superConstructor.getParams()) {
        String paramName = md.createUniqueName("super_" + superParam.getName());
        VariableData newParam = 
          new VariableData(paramName, new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]), 
                           superParam.getType().getSymbolData(), true, md);
        newParam.setGenerated(true);
        params.add(newParam);
        
        md.addVar(newParam); 
      }
    }
    
    
    boolean hasOtherConstructor = sd.hasMethod(name);
    
    for (VariableData field : sd.getVars()) {
      
      if (! field.hasInitializer() && ! field.hasModifier("static")) {
        if (! hasOtherConstructor) { field.gotValue(); } 
        
        
        params.add(field);
      }
    }
    md.setParams(params.toArray(new VariableData[params.size()]));
    md.setVars(params);
    
    addGeneratedMethod(sd, md);
    LanguageLevelConverter._newSDs.remove(sd); 
  }
  
  
  protected static void createAccessors(SymbolData sd, File file) {
    if (LanguageLevelConverter.isAdvancedFile(file)) return;
    LinkedList<VariableData> fields = sd.getVars();
    Iterator<VariableData> iter = fields.iterator();
    while (iter.hasNext()) {
      VariableData vd = iter.next();      
      if (!vd.hasModifier("static")) { 
        String name = getFieldAccessorName(vd.getName());
        String[] mavStrings;
        mavStrings = new String[] {"public"};
        MethodData md = new MethodData(name,
                                       new ModifiersAndVisibility(SourceInfo.NO_INFO, mavStrings), 
                                       new TypeParameter[0], 
                                       vd.getType().getSymbolData(), 
                                       new VariableData[0],
                                       new String[0], 
                                       sd,
                                       null); 
        addGeneratedMethod(sd, md);
      }
    }
  }
  
   
  protected void createToString(SymbolData sd) {
    String name = "toString";
    String[] mavStrings;
    mavStrings = new String[] {"public"};
    
    MethodData md = new MethodData(name,
                                   new ModifiersAndVisibility(SourceInfo.NO_INFO, mavStrings), 
                                   new TypeParameter[0], 
                                   getSymbolData("String", _makeSourceInfo("java.lang.String")), 
                                   new VariableData[0],
                                   new String[0], 
                                   sd,
                                   null); 
    addGeneratedMethod(sd, md);    
  }
  
   
  protected void createHashCode(SymbolData sd) {    
    String name = "hashCode";
    String[] mavStrings;
    mavStrings = new String[] {"public"};
    MethodData md = new MethodData(name,
                                   new ModifiersAndVisibility(SourceInfo.NO_INFO, mavStrings), 
                                   new TypeParameter[0], 
                                   SymbolData.INT_TYPE, 
                                   new VariableData[0],
                                   new String[0], 
                                   sd,
                                   null); 
    addGeneratedMethod(sd, md);
  }
  
   
  protected void createEquals(SymbolData sd) {    
    String name = "equals";
    String[] mavStrings;
    mavStrings = new String[] {"public"};
    SymbolData type = getSymbolData("java.lang.Object", _makeSourceInfo("java.lang.Object"));
    VariableData param = new VariableData(type);
    MethodData md = new MethodData(name,
                                   new ModifiersAndVisibility(SourceInfo.NO_INFO, mavStrings), 
                                   new TypeParameter[0], 
                                   SymbolData.BOOLEAN_TYPE, 
                                   new VariableData[] {param},
                                   new String[0], 
                                   sd,
                                   null); 
    param.setEnclosingData(md);
    addGeneratedMethod(sd, md);
  }
  
  
  public Void forMemberType(MemberType that) {
    forMemberTypeDoFirst(that);
    if (prune(that)) return null;
    return forMemberTypeOnly(that);
  }
  
  
  public Void forStringLiteralOnly(StringLiteral that) {
    getSymbolData("String", that.getSourceInfo(), true);
    return null;
  }
  
  
  public Void forSimpleNamedClassInstantiation(SimpleNamedClassInstantiation that) {
    forSimpleNamedClassInstantiationDoFirst(that);
    if (prune(that)) return null;
    that.getType().visit(this);
    that.getArguments().visit(this);
    
    
    
    getSymbolData(that.getType().getName(), that.getSourceInfo());
    
    return forSimpleNamedClassInstantiationOnly(that);
  }
  
  
   
  public static boolean arrayEquals(Object[] array1, Object[] array2) { return Arrays.equals(array1, array2); }
  
  
  private class ResolveNameVisitor extends JExpressionIFAbstractVisitor<TypeData> {
    
    public ResolveNameVisitor() { }
    
    
    public TypeData defaultCase(JExpressionIF that) {
      that.visit(LanguageLevelVisitor.this);
      return null;
    }
    
    
    public TypeData forSimpleNameReference(SimpleNameReference that) {
      SymbolData result = getSymbolData(that.getName().getText(), that.getSourceInfo());
      
      if (result == SymbolData.NOT_FOUND) {
        return new PackageData(that.getName().getText());
      }
      return result;
    }
    
    
    public TypeData forComplexNameReference(ComplexNameReference that) {
      TypeData lhs = that.getEnclosing().visit(this);
      SymbolData result = getSymbolData(lhs, that.getName().getText(), that.getSourceInfo(), true);
      
      if (result == SymbolData.NOT_FOUND) { 
        if (lhs instanceof PackageData) {
          return new PackageData((PackageData) lhs, that.getName().getText());
        }
        return null;
      }
      
      return result;
    }
  }
  
  
  public static class LanguageLevelVisitorTest extends TestCase {
    
    private LanguageLevelVisitor testLLVisitor;
    private Hashtable<SymbolData, LanguageLevelVisitor> testNewSDs;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _finalMav = 
      new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[]{"final"});
    
    public LanguageLevelVisitorTest() { this(""); }
    public LanguageLevelVisitorTest(String name) { super(name); }
    
    public void setUp() {
      testLLVisitor = new LanguageLevelVisitor(new File(""), 
                                               "", 
                                               new LinkedList<String>(), 
                                               new LinkedList<String>(), 
                                               new LinkedList<String>(), 
                                               new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      _errorAdded=false;
      LanguageLevelConverter.symbolTable.clear();
      
      testLLVisitor.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>();      
      _hierarchy = new Hashtable<String, TypeDefBase>();
      testLLVisitor._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();
      testLLVisitor._resetNonStaticFields();
      testLLVisitor._importedPackages.add("java.lang");

      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
    }
    
    
    public void testGetUnqualifiedClassName() {
      assertEquals("getUnqualifiedClassName with a qualified name with an inner class", "innermonkey", 
                   testLLVisitor.getUnqualifiedClassName("i.like.monkey$innermonkey"));
      assertEquals("getUnqualifiedClassName with a qualified name", "monkey", 
                   testLLVisitor.getUnqualifiedClassName("i.like.monkey"));
      assertEquals("getUnqualifiedClassName with an unqualified name", "monkey", 
                   testLLVisitor.getUnqualifiedClassName("monkey"));
      assertEquals("getUnqualifiedClassName with an empty string", "", testLLVisitor.getUnqualifiedClassName(""));
    }
    
    public void testClassFile2SymbolData() {
      
      
      SymbolData objectSD = testLLVisitor._classFile2SymbolData("java.lang.Object", "");
      SymbolData stringSD = testLLVisitor._classFile2SymbolData("java.lang.String", "");
      MethodData md = new MethodData("substring", _publicMav, new TypeParameter[0], stringSD, 
                                     new VariableData[] {new VariableData(SymbolData.INT_TYPE)},
                                     new String[0], stringSD, null);
      assertTrue("java.lang.String should have been converted successfully", 
                 stringSD.getName().equals("java.lang.String"));
      assertEquals("java.lang.String's superClass should should be java.lang.Object", 
                   objectSD,
                   stringSD.getSuperClass());
      
      LinkedList<MethodData> methods = stringSD.getMethods();
      Iterator<MethodData> iter = methods.iterator();
      boolean found = false;
      
      while (iter.hasNext()) {
        MethodData currMd = iter.next();
        if (currMd.getName().equals("substring") && currMd.getParams().length == 1 && 
            currMd.getParams()[0].getType() == SymbolData.INT_TYPE.getInstanceData()) {
          found = true;
          md.getParams()[0].setEnclosingData(currMd);
          break;
        }
      }
      
      assertTrue("Should have found method substring(int) in java.lang.String", found);
      
      assertEquals("java.lang.String should be packaged correctly", "java.lang", 
                   testLLVisitor.getSymbolData("java.lang.String", SourceInfo.NO_INFO).getPackage());
      
      
      SymbolData newStringSD = testLLVisitor._classFile2SymbolData("java.lang.String", "");
      assertTrue("Second call to classFileToSymbolData should not change sd in hash table.", 
                 stringSD == testLLVisitor.symbolTable.get("java.lang.String"));
      assertTrue("Second call to classFileToSymbolData should return same SD.", 
                 newStringSD == testLLVisitor.symbolTable.get("java.lang.String"));      
      
      
      SymbolData bartSD = testLLVisitor._classFile2SymbolData("Bart", "testFiles");
      assertFalse("bartSD should not be null", bartSD == null);
      assertFalse("bartSD should not be a continuation", bartSD.isContinuation());
      MethodData md1 = 
        new MethodData("myMethod", _protectedMav, 
                       new TypeParameter[0], SymbolData.BOOLEAN_TYPE, 
                       new VariableData[] { new VariableData(SymbolData.INT_TYPE) }, 
                       new String[] {"java.lang.Exception"}, bartSD, null);
      
      md1.getParams()[0].setEnclosingData(bartSD.getMethods().getLast());
      MethodData md2 = new MethodData("Bart", _publicMav, new TypeParameter[0], bartSD,
                                      new VariableData[0], new String[0], bartSD, null);
      
      VariableData vd1 = new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, bartSD);
      
      LinkedList<MethodData> bartsMD = new LinkedList<MethodData>();
      bartsMD.addFirst(md1);
      bartsMD.addFirst(md2);
      
      LinkedList<VariableData> bartsVD = new LinkedList<VariableData>();
      bartsVD.addLast(vd1);
      
      assertEquals("Bart's super class should be java.lang.Object: errors = " + errors, objectSD, 
                   bartSD.getSuperClass());
      assertEquals("Bart's Variable Data should be a linked list containing only vd1", bartsVD, bartSD.getVars());
      assertEquals("The first method data of bart's should be correct", md2, bartSD.getMethods().getFirst());
      
      assertEquals("The second method data of bart's should be correct", md1, bartSD.getMethods().getLast());
      assertEquals("Bart's Method Data should be a linked list containing only md1", bartsMD, bartSD.getMethods());
    }
    
    public void testLookupFromClassesToBeParsed() {
      
      
      
      ClassDef cd = 
        new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                     new Word(SourceInfo.NO_INFO, "Lisa"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      IntermediateVisitor bv = new IntermediateVisitor(new File(""), 
                                                       errors, 
                                                       continuations, 
                                                       new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
      
      
      assertTrue("Should return a continuation", 
                 testLLVisitor._lookupFromClassesToBeParsed("Lisa", SourceInfo.NO_INFO, false).isContinuation());
      
      








      
      
      testLLVisitor._classesToBeParsed.put("Lisa", new Pair<TypeDefBase, LanguageLevelVisitor>(cd, bv));
      assertFalse("Should return a non-continuation", 
                  testLLVisitor._lookupFromClassesToBeParsed("Lisa", 
                                                    SourceInfo.NO_INFO,
                                                    true).isContinuation());
    }
    
    public void testGetSymbolDataForClassFile() {
      
      assertFalse("Should return a non-continuation", 
                  testLLVisitor.getSymbolDataForClassFile("java.lang.String", SourceInfo.NO_INFO).isContinuation());
      
      
      assertEquals("Should return null with a user class that can't be found",
                   null,
                   testLLVisitor.getSymbolDataForClassFile("Marge", SourceInfo.NO_INFO));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "Class Marge not found.", 
                   errors.get(0).getFirst());
    }
    
    
    public void testGetSymbolData_Primitive() {
      assertEquals("should be boolean type", SymbolData.BOOLEAN_TYPE, 
                   testLLVisitor._getSymbolData_Primitive("boolean"));
      assertEquals("should be char type", SymbolData.CHAR_TYPE, testLLVisitor._getSymbolData_Primitive("char"));
      assertEquals("should be byte type", SymbolData.BYTE_TYPE, testLLVisitor._getSymbolData_Primitive("byte"));
      assertEquals("should be short type", SymbolData.SHORT_TYPE, testLLVisitor._getSymbolData_Primitive("short"));
      assertEquals("should be int type", SymbolData.INT_TYPE, testLLVisitor._getSymbolData_Primitive("int"));
      assertEquals("should be long type", SymbolData.LONG_TYPE, testLLVisitor._getSymbolData_Primitive("long"));
      assertEquals("should be float type", SymbolData.FLOAT_TYPE, testLLVisitor._getSymbolData_Primitive("float"));
      assertEquals("should be double type", SymbolData.DOUBLE_TYPE, testLLVisitor._getSymbolData_Primitive("double"));
      assertEquals("should be void type", SymbolData.VOID_TYPE, testLLVisitor._getSymbolData_Primitive("void"));
      assertEquals("should be null type", SymbolData.NULL_TYPE, testLLVisitor._getSymbolData_Primitive("null"));
      assertEquals("should return null--not a primitive", null, 
                   testLLVisitor._getSymbolData_Primitive("java.lang.String"));
    }
    
    public void testGetQualifiedSymbolData() {
      testLLVisitor._file = new File("testFiles/Fake.dj0");
      SymbolData sd = new SymbolData("testPackage.File");
      testLLVisitor._package = "testPackage";
      LanguageLevelConverter.symbolTable.put("testPackage.File", sd);
      
      SymbolData sd1 = new SymbolData("java.lang.String");
      LanguageLevelConverter.symbolTable.put("java.lang.String", sd1);
      
      
      assertEquals("should the continuation symbol", sd, 
                   testLLVisitor._getQualifiedSymbolData("testPackage.File", SourceInfo.NO_INFO, true, false, true));

      
      
      SymbolData sd2 = testLLVisitor._getQualifiedSymbolData("java.lang.Integer", SourceInfo.NO_INFO, true, true, true);
      assertEquals("should return non-continuation java.lang.Integer", "java.lang.Integer", sd2.getName());
      assertFalse("should not be a continuation.", sd2.isContinuation());
      
      SymbolData sd3 = testLLVisitor._getQualifiedSymbolData("Wow", SourceInfo.NO_INFO, true, true, true);
      assertEquals("search should fail", null, sd3);


      
      




      
      sd.setIsContinuation(false);
      assertEquals("should return non-continuation sd", sd, 
                   testLLVisitor._getQualifiedSymbolData("testPackage.File", SourceInfo.NO_INFO, true, false,  true));
      
      
      assertEquals("Should return sd1.", sd1, 
                   testLLVisitor._getQualifiedSymbolData("java.lang.String", SourceInfo.NO_INFO, true, false, true));
      assertFalse("sd1 should no longer be a continuation.", sd1.isContinuation());
      
      
      
      
      assertEquals("should return null-because it's not a valid class", null, 
                   testLLVisitor._getQualifiedSymbolData("testPackage.not.in.symboltable", 
                                                   SourceInfo.NO_INFO, true, false, true));
      
      assertEquals("should be two errors so far.", 2, errors.size());
      assertNull("should return null", 
                 testLLVisitor._getQualifiedSymbolData("testPackage.not.in.symboltable", 
                                                 SourceInfo.NO_INFO, false, false, false));
      
      assertNull("should return null.", 
                 testLLVisitor._getQualifiedSymbolData("notRightPackage", SourceInfo.NO_INFO, false, false, false));
      assertEquals("should still be two errors.", 2, errors.size());
    }
    
    public void testGetArraySymbolData() {
      
      assertEquals("Should return null, because inner sd is null.", null, 
                   testLLVisitor._getArraySymbolData("TestFile[]", SourceInfo.NO_INFO, false, false, false, false));
      
      
      SymbolData sd = new SymbolData("Iexist");
      LanguageLevelConverter.symbolTable.put("Iexist", sd);
      testLLVisitor._getArraySymbolData("Iexist[]", SourceInfo.NO_INFO, false, false, false, false).getName();
      assertTrue("Should have created an array data and add it to symbol table.", 
                 LanguageLevelConverter.symbolTable.containsKey("Iexist[]"));
      SymbolData ad = LanguageLevelConverter.symbolTable.get("Iexist[]");
      
      
      assertEquals("Should only have field 'length'", 1, ad.getVars().size());
      assertNotNull("Should contain field 'length'", ad.getVar("length"));
      
      assertEquals("Should only have one method-clone", 1, ad.getMethods().size());
      assertTrue("Should contain method clone", ad.hasMethod("clone"));
      
      assertEquals("Should have Object as super class", 
                   LanguageLevelConverter.symbolTable.get("java.lang.Object"), 
                   ad.getSuperClass());
      assertEquals("Should have 2 interfaces", 2, ad.getInterfaces().size());
      assertEquals("Interface 1 should be java.lang.Cloneable", "java.lang.Cloneable", 
                   ad.getInterfaces().get(0).getName());
      assertEquals("Interface 2 should be java.io.Serializable", "java.io.Serializable", 
                   ad.getInterfaces().get(1).getName());
      
      
      
      assertEquals("Since it's already in symbol table now, should just return it.", ad, 
                   testLLVisitor._getArraySymbolData("Iexist[]", SourceInfo.NO_INFO, false, false, false, false));
      
      
      testLLVisitor._getArraySymbolData("Iexist[][]", SourceInfo.NO_INFO, false, false, false, false);
      assertTrue("Should have added a multidimensional array to the table.", 
                 LanguageLevelConverter.symbolTable.containsKey("Iexist[][]"));
      
      SymbolData sd2 = new SymbolData("String");
      LanguageLevelConverter.symbolTable.put("String", sd2);
      testLLVisitor._getArraySymbolData("String[][]", SourceInfo.NO_INFO, false, false, false, false);
      assertTrue("Should have added String[] to table", LanguageLevelConverter.symbolTable.containsKey("String[]"));
      assertTrue("Should have added String[][] to table", LanguageLevelConverter.symbolTable.containsKey("String[][]"));
    }
    
    public void testGetSymbolData_FromCurrFile() {
      _sd4.setIsContinuation(false);
      _sd6.setIsContinuation(true);
      LanguageLevelConverter.symbolTable.put("u.like.emu", _sd4);
      LanguageLevelConverter.symbolTable.put("cebu", _sd6);
      
      
      
      
      
      assertEquals("symbol data is a continuation, but resolve is false, so should just be returned.", _sd4, 
                   testLLVisitor._getSymbolData_FromCurrFile("u.like.emu", SourceInfo.NO_INFO, false));
      
      
      ClassDef cd = 
        new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                     new Word(SourceInfo.NO_INFO, "Lisa"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      IntermediateVisitor bv = new IntermediateVisitor(new File(""));
      
      testLLVisitor. _classesToBeParsed.put("Lisa", new Pair<TypeDefBase, LanguageLevelVisitor>(cd, bv));
      assertFalse("Should return a non-continuation", 
                  testLLVisitor._getSymbolData_FromCurrFile("Lisa", SourceInfo.NO_INFO, true).isContinuation());
    }
    
    public void testGetSymbolData_FromFileSystem() {
      
      
      
      testLLVisitor._package="fully.qualified";
      testLLVisitor._file = new File("testFiles/fully/qualified/Fake.dj0");
      SymbolData sd2 = new SymbolData("fully.qualified.Woah");  
      testLLVisitor.symbolTable.put("fully.qualified.Woah", sd2);
      
      SymbolData result = 
        testLLVisitor._getSymbolData_FromFileSystem("fully.qualified.Woah", SourceInfo.NO_INFO, false, true);
      
      assertEquals("Should return sd2, unresolved.", sd2, result);
      assertTrue("sd2 should still be unresolved", sd2.isContinuation());
      assertEquals("Should be no errors", 0, errors.size());
      




      




      
      
      
      testLLVisitor.symbolTable.remove("fully.qualified.Woah");
      testLLVisitor.visitedFiles.clear();
      testLLVisitor._package="another.package";
      testLLVisitor._file = new File("testFiles/another/package/Wowsers.dj0");
      sd2 = new SymbolData("fully.qualified.Woah");
      testLLVisitor.symbolTable.put("fully.qualified.Woah", sd2);
      
      result = testLLVisitor._getSymbolData_FromFileSystem("fully.qualified.Woah", SourceInfo.NO_INFO, false, true);
      
      assertEquals("Should return sd2, unresolved.", sd2, result);
      assertTrue("sd2 should still be unresolved", sd2.isContinuation());
      assertEquals("Should be no errors", 0, errors.size());
      


      


      
      
      
      testLLVisitor._package = "";
      testLLVisitor._file = new File ("testFiles/Cool.dj0");  
      
      
      SymbolData sd1 = new SymbolData("Wow");
      SymbolData obj = testLLVisitor._getSymbolData_FromFileSystem("java.lang.Object", SourceInfo.NO_INFO, true, true);
      sd1.setSuperClass(obj);
      testLLVisitor.symbolTable.put("Wow", sd1);
      
      result = testLLVisitor._getSymbolData_FromFileSystem("Wow", SourceInfo.NO_INFO, false, true);
      assertEquals("Should return sd1, unresolved.", sd1, result);
      assertTrue("sd1 should still be unresolved.", sd1.isContinuation());
      assertEquals("Should be no errors", 0, errors.size());
      
      result = testLLVisitor._getSymbolData_FromFileSystem("Wow", SourceInfo.NO_INFO, true, true);
      assertEquals("Should return sd1, resolved.", sd1, result);
      assertFalse("sd1 should be resolved.", sd1.isContinuation());
      assertEquals("Should be no errors", 0, errors.size());
      
      result = testLLVisitor._getSymbolData_FromFileSystem("Wow", SourceInfo.NO_INFO, true, true);
      assertEquals("Should return sd1.", sd1, result);
      assertFalse("sd1 should still be resolved.", sd1.isContinuation());
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      result = testLLVisitor._getSymbolData_FromFileSystem("James", SourceInfo.NO_INFO, true, true);
      assertEquals("Search for James should fail", null, result);


      
      
      testLLVisitor._package = "myPackage";
      assertEquals("Should return NOT_FOUND-does not exist.", 
                   SymbolData.NOT_FOUND, 
                   testLLVisitor._getSymbolData_FromFileSystem("WrongPackage.className", 
                                                               SourceInfo.NO_INFO, true, false));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      testLLVisitor._package = "";
      testLLVisitor._file = new File("testFiles/Fake.dj0");
      LinkedList<VariableData> vds = new LinkedList<VariableData>();
      result = testLLVisitor._getSymbolData_FromFileSystem("Doh", SourceInfo.NO_INFO, true, true);
      vds.addLast(new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, result));
      vds.addLast(new VariableData("o", _packageMav, obj, true, result));



      
      
      
    }
    
    public void testGetSymbolData() {
      testLLVisitor._package="";
      testLLVisitor._file = new File("testFiles/akdjskj");
      
      
      SymbolData sd1 = new SymbolData("Wow");
      testLLVisitor.symbolTable.put("Wow", sd1);
      assertEquals("Should return an equal SymbolData", 
                   sd1, testLLVisitor.getSymbolData("Wow", SourceInfo.NO_INFO, true, false));
      assertFalse("Should not be a continuation", sd1.isContinuation());  
      
      
      SymbolData result = testLLVisitor.getSymbolData("ima.invalid", SourceInfo.NO_INFO, true, false);
      assertEquals("Should return null-invalid class name", null, result);
      assertEquals("There should be one error", 1, testLLVisitor.errors.size());
      assertEquals("The error message should be correct", "Invalid class name ima.invalid", errors.get(0).getFirst());
      
      
      testLLVisitor._package="fully.qualified";
      testLLVisitor._file = new File("testFiles/fully/qualified/Fake.dj0");
      SymbolData sd2 = new SymbolData("fully.qualified.Symbol");
      testLLVisitor.symbolTable.put("fully.qualified.Symbolh", sd2);
      
      result = testLLVisitor.getSymbolData("fully.qualified.Symbol", SourceInfo.NO_INFO, true, false);
      
      assertEquals("Should return sd2, resolved.", sd2, result);
      assertTrue("sd2 should be resolved", sd2.isContinuation());
      
      
      sd1.setName("fully.qualified.Woah.Wow");
      sd2.addInnerClass(sd1);
      sd1.setOuterData(sd2);
      testLLVisitor.symbolTable.put("fully.qualified.Woah.Wow", sd1);
      testLLVisitor.symbolTable.remove("Wow");
      sd1.setIsContinuation(false);
      result = testLLVisitor.getSymbolData("fully.qualified.Woah.Wow", SourceInfo.NO_INFO, true, false);
      assertEquals("Should return sd1 (the inner class!)", sd1, result);
      
      
      SymbolData sd3 = new SymbolData("fully.qualified.Woah.Wow.James");
      sd1.addInnerClass(sd3);


      sd3.setOuterData(sd1);

      result = testLLVisitor.getSymbolData("fully.qualified.Woah.Wow.James", SourceInfo.NO_INFO, true, false);
      assertEquals("Should return sd3", sd3, result);
    }
    
    
    public void testGetSymbolDataHelper() {
      
      assertEquals("should return the int SymbolData", SymbolData.INT_TYPE, 
                   testLLVisitor.getSymbolDataHelper("int", SourceInfo.NO_INFO, true, true, true, true));
      assertEquals("should return the byte SymbolData", SymbolData.BYTE_TYPE, 
                   testLLVisitor.getSymbolDataHelper("byte", SourceInfo.NO_INFO, false, false, false, true));
      
      
      ArrayData ad = new ArrayData(SymbolData.INT_TYPE, testLLVisitor, SourceInfo.NO_INFO);
      SymbolData result = testLLVisitor.getSymbolDataHelper("int[]", SourceInfo.NO_INFO, true, true, true, true);
      ad.getVars().get(0).setEnclosingData(result);  
      ad.getMethods().get(0).setEnclosingData(result.getMethods().get(0).getEnclosingData()); 
      assertEquals("should return the array type", ad, result);
      
      
      SymbolData sd = new SymbolData("java.lang.System");
      LanguageLevelConverter.symbolTable.put("java.lang.System", sd);
      assertEquals("should return the same sd", sd, 
                   testLLVisitor.getSymbolDataHelper("java.lang.System", SourceInfo.NO_INFO, false, true, true, true));
      assertTrue("should be a continuation", sd.isContinuation());
      assertEquals("should return the now resolved sd", sd, 
                   testLLVisitor.getSymbolDataHelper("java.lang.System", SourceInfo.NO_INFO, true, false, true, true));
      assertFalse("should not be a continuation", sd.isContinuation());
      
      
      sd = new SymbolData("fully.qualified.Qwerty");
      LanguageLevelConverter.symbolTable.put("fully.qualified.Qwerty", sd);
      testLLVisitor._classNamesInThisFile.addLast("fully.qualified.Qwerty");
      
      IntermediateVisitor bv = new IntermediateVisitor(new File(""), 
                                                       errors, 
                                                       continuations, 
                                                       new LinkedList<Pair<LanguageLevelVisitor, SourceFile>>());
      bv._package = "fully.qualified";
      bv._file = new File("testFiles/fully/qualified/Fake.dj0");
      ClassDef cd = new ClassDef(SourceInfo.NO_INFO, 
                                 _packageMav, 
                                 new Word(SourceInfo.NO_INFO, "Qwerty"),
                                 new TypeParameter[0],
                                 new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]),
                                 new ReferenceType[0], 
                                 new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      bv._classesToBeParsed.put("fully.qualified.Qwerty", new Pair<TypeDefBase, LanguageLevelVisitor>(cd, bv));
      assertEquals("should return sd the continuation", sd, 
                   bv.getSymbolDataHelper("Qwerty", SourceInfo.NO_INFO, false, true, true, true));
      assertTrue("should be a continuation", sd.isContinuation());
      assertEquals("should return sd, now resolved", sd, 
                   bv.getSymbolDataHelper("Qwerty", SourceInfo.NO_INFO, true, true, true, true));
      assertFalse("should not be a continuation", sd.isContinuation());
      
      
      testLLVisitor._importedFiles.addLast("a.b.c");
      sd = new SymbolData("a.b.c");
      LanguageLevelConverter.symbolTable.put("a.b.c.", sd);
      assertEquals("should find the continuation in the symbol table", sd, 
                   testLLVisitor.getSymbolDataHelper("c", SourceInfo.NO_INFO, false, true, true, true));
      assertTrue("should be a continuation", sd.isContinuation());
      
      testLLVisitor._package="fully.qualified";
      testLLVisitor._file = new File("testFiles/fully/qualified/Fake.dj0");
      testLLVisitor._importedFiles.addLast("fully.qualified.Woah");
      SymbolData sd2 = new SymbolData("fully.qualified.Woah");
      sd2.setIsContinuation(false);
      LanguageLevelConverter.symbolTable.put("fully.qualified.Woah", sd2);
      result = testLLVisitor.getSymbolDataHelper("Woah", SourceInfo.NO_INFO, true, false, true, true);

      assertEquals("should find the resolved symbol data in the symbol table", sd2, result);
      assertFalse("should not be a continuation", sd2.isContinuation());
      
      
      testLLVisitor._importedFiles.clear();
      testLLVisitor.visitedFiles.clear();
      LanguageLevelConverter.symbolTable.remove("fully.qualified.Woah");
      sd2 = new SymbolData("fully.qualified.Woah");
      LanguageLevelConverter.symbolTable.put("fully.qualified.Woah", sd2);
      



     
      result = testLLVisitor.getSymbolDataHelper("Woah", SourceInfo.NO_INFO, false, false, true, true);
      
      assertEquals("Should return sd2, unresolved.", sd2, result);
      assertTrue("sd2 should still be unresolved", sd2.isContinuation());
      
      result = testLLVisitor.getSymbolDataHelper("Woah", SourceInfo.NO_INFO, false, false, true, true);
      assertEquals("Should return sd2, now unresolved.", sd2, result);
      assertTrue("sd2 should not be resolved", sd2.isContinuation());
      
      
      



      
      
      LanguageLevelConverter.symbolTable.remove("fully.qualified.Woah");
      testLLVisitor.visitedFiles.clear();
      testLLVisitor._file = new File("testFiles/Fake.dj0");
      testLLVisitor._package = "";
      testLLVisitor._importedPackages.addLast("fully.qualified");
      sd2 = new SymbolData("fully.qualified.Woah");
      LanguageLevelConverter.symbolTable.put("fully.qualified.Woah", sd2);
      assertEquals("should find the unresolved symbol data in the symbol table", sd2, 
                   testLLVisitor.getSymbolDataHelper("Woah", SourceInfo.NO_INFO, false, false, true, true));
      assertTrue("should not be a continuation", sd2.isContinuation());
      
      sd2.setIsContinuation(false);
      result = testLLVisitor.getSymbolDataHelper("Woah", SourceInfo.NO_INFO, true, false, true, true);
      assertEquals("should find the resolved symbol data in the symbol table", sd2, result);
      assertFalse("should not be a continuation", sd2.isContinuation());
      
      
      
      SymbolData stringSD = new SymbolData("java.lang.String");
      SymbolData newsd1 = testLLVisitor.getSymbolDataHelper("String", SourceInfo.NO_INFO, true, true, true, true);
      assertEquals("should have correct name.", stringSD.getName(), newsd1.getName());
      assertFalse("should not be a continuation", newsd1.isContinuation());
      
      
      LanguageLevelConverter.symbolTable.put("random.package.String", new SymbolData("random.package.String"));
      LanguageLevelConverter.symbolTable.put("java.lang.Object", new SymbolData("java.lang.Object"));
      testLLVisitor._importedPackages.addLast("random.package");
      result = testLLVisitor.getSymbolDataHelper("String", SourceInfo.NO_INFO, true, true, true, true);
      assertEquals("Result should be null", null, result);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "The class name String is ambiguous." + 
                   "  It could be java.lang.String or random.package.String", 
                   errors.get(0).getFirst());
      
      LanguageLevelConverter.symbolTable.remove("random.package.String");
      
    }
    
    public void test_forModifiersAndVisibility() {
      
      testLLVisitor.forModifiersAndVisibility(_publicMav);
      testLLVisitor.forModifiersAndVisibility(_protectedMav);
      testLLVisitor.forModifiersAndVisibility(_privateMav);
      testLLVisitor.forModifiersAndVisibility(_packageMav);
      
      
      assertEquals("There should be no errors.", 0, errors.size());
      
      
      ModifiersAndVisibility testMav = 
        new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "private"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", "Illegal combination of modifiers." + 
                   " Can't use private and public together.", errors.get(0).getFirst());
      
      
      testMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "abstract"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("Still only one error.", 1, errors.size());
      
      
      testMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract", "final"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("There should be two errors.", 2, errors.size());
      assertEquals("The error message should be correct.", "Illegal combination of modifiers." + 
                   " Can't use final and abstract together.", errors.get(1).getFirst());
      
      
      testMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final", "abstract"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("There should still be two errors.", 2, errors.size());  
      assertEquals("The error message should be correct.", "Illegal combination of modifiers." + 
                   " Can't use final and abstract together.", errors.get(1).getFirst());
      
      
      testMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"volatile", "final"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("There should be three errors.", 3, errors.size());  
      assertEquals("The error message should be correct.", "Illegal combination of modifiers." + 
                   " Can't use final and volatile together.", errors.get(2).getFirst());
      
      
      testMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"static", "final", "static"});
      testLLVisitor.forModifiersAndVisibility(testMav);
      assertEquals("There should be four errors.", 4, errors.size());  
      assertEquals("The error message should be correct.", "Duplicate modifier: static", errors.get(3).getFirst());
    }
    
    public void testGetQualifiedClassName() {
      
      testLLVisitor._package="";
      assertEquals("Should not change qualified name.", "simpson.Bart", 
                   testLLVisitor.getQualifiedClassName("simpson.Bart"));
      assertEquals("Should not change unqualified name.", "Lisa", testLLVisitor.getQualifiedClassName("Lisa"));
      
      
      testLLVisitor._package="myPackage";
      assertEquals("Should not change properly packaged qualified name.", "myPackage.Snowball", 
                   testLLVisitor.getQualifiedClassName("myPackage.Snowball"));
      assertEquals("Should append package to front of not fully packaged name", "myPackage.simpson.Snowball", 
                   testLLVisitor.getQualifiedClassName("simpson.Snowball"));
      assertEquals("Should append package to front of unqualified class name.", "myPackage.Grandpa", 
                   testLLVisitor.getQualifiedClassName("Grandpa"));
    }
    
    public void testAddSymbolData() {
      
      SymbolData obj = new SymbolData("java.lang.Object");
      obj.setIsContinuation(false);
      LanguageLevelConverter.symbolTable.put("java.lang.Object", obj);
      
      ClassDef cd = 
        new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Awesome"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      SymbolData sd = new SymbolData("Awesome"); 
      sd.setSuperClass(LanguageLevelConverter.symbolTable.get("java.lang.Object"));
      LanguageLevelConverter.symbolTable.put("Awesome", sd);
      SymbolData result = testLLVisitor.defineSymbolData(cd, "Awesome");
      assertFalse("result should not be a continuation.", result.isContinuation());
      assertFalse("sd should also no longer be a continuation.", sd.isContinuation());
      assertEquals("result and sd should be equal.", sd, result);
      
      
      assertEquals("hierarchy should be empty", 0, _hierarchy.size());
      
      
      assertEquals("Should return null, because it is already in the SymbolTable.", null, 
                   testLLVisitor.defineSymbolData(cd, "Awesome"));
      assertEquals("Length of errors should now be 1.", 1, errors.size());
      assertEquals("Error message should be correct.", "The class or interface Awesome has already been defined.", 
                   errors.get(0).getFirst());
      assertEquals("hierarchy should be empty.", 0, _hierarchy.size());
      
    }
    
    public void test_variableDeclaration2VariableData() {
      VariableDeclarator[] d1 = {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word(SourceInfo.NO_INFO, "i")) };
      VariableDeclaration vd1 = new VariableDeclaration(SourceInfo.NO_INFO,_publicMav, d1); 
      VariableData[] vdata1 = { new VariableData("i", _publicMav, SymbolData.INT_TYPE, false, _sd1) };
      
      assertTrue("Should properly recognize a basic VariableDeclaration", 
                 arrayEquals(vdata1, testLLVisitor._variableDeclaration2VariableData(vd1, _sd1)));
      
      VariableDeclarator[] d2 = {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                            new Word(SourceInfo.NO_INFO, "i")), 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                                          new Word(SourceInfo.NO_INFO, "b"), 
                                          new BooleanLiteral(SourceInfo.NO_INFO, true)) };
      VariableDeclaration vd2 = new VariableDeclaration(SourceInfo.NO_INFO,_privateMav, d2); 
      VariableData bData = new VariableData("b", _privateMav, SymbolData.BOOLEAN_TYPE, true, _sd1);
      bData.setHasInitializer(true);
      VariableData[] vdata2 = {new VariableData("i", _privateMav, SymbolData.INT_TYPE, false, _sd1),
        bData};
      
      assertTrue("Should properly recognize a more complicated VariableDeclaration", 
                 arrayEquals(vdata2, testLLVisitor._variableDeclaration2VariableData(vd2, _sd1)));
      
      
      VariableDeclarator[] d3 = { 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new ClassOrInterfaceType(SourceInfo.NO_INFO, "LinkedList", new Type[0]), 
                                            new Word(SourceInfo.NO_INFO, "myList"))};
      VariableDeclaration vd3 = new VariableDeclaration(SourceInfo.NO_INFO, _privateMav, d3);
      testLLVisitor._variableDeclaration2VariableData(vd3, _sd1);
      assertEquals("There should now be no errors", 0, errors.size());


      
    }
    
    public void test_addError() {
      LinkedList<Pair<String, JExpressionIF>> e = new LinkedList<Pair<String, JExpressionIF>>();
      
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);
      NullLiteral nl2 = new NullLiteral(SourceInfo.NO_INFO);
      
      e.addLast(new Pair<String,JExpressionIF>("Boy, is this an error!", nl));
      _addError("Boy, is this an error!", nl);
      
      assertTrue("An error should have been added.", _errorAdded);
      assertEquals("The errors list should be correct.", e, errors);
      
      e.addLast(new Pair<String,JExpressionIF>("Error again!", nl2));
      _addError("Error again!", nl2);
      
      assertTrue("Another error should have been aded.", _errorAdded);
      assertEquals("The new errors list should be correct.", e, errors);
    }
    
    public void test_addAndIgnoreError() {
      LinkedList<Pair<String, JExpressionIF>> e = new LinkedList<Pair<String, JExpressionIF>>();
      
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);
      NullLiteral nl2 = new NullLiteral(SourceInfo.NO_INFO);
      
      _errorAdded = false;
      
      e.addLast(new Pair<String,JExpressionIF>("Nobody pays attention to me!", nl));
      _addAndIgnoreError("Nobody pays attention to me!", nl);
      
      assertFalse("_errorAdded should be false.", _errorAdded);
      assertEquals("The errors list should be correct.", e, errors);
      
      e.addLast(new Pair<String,JExpressionIF>("Cellophane, I'm Mr. Cellophane", nl2));
      _addAndIgnoreError("Cellophane, I'm Mr. Cellophane", nl2);
      
      assertFalse("errorAdded should still be false.", _errorAdded);
      assertEquals("The new errors list should be correct.", e, errors);
      
      _errorAdded = true;
      try {
        _addAndIgnoreError("This should throw an exception, because _errorAdded is true.", nl);
        assertTrue("An error should have been thrown!", false);
      }
      catch (RuntimeException exc) {
        assertEquals("Make sure runtime exception message is correct.", 
                     "Internal Program Error: _addAndIgnoreError called while _errorAdded was true." + 
                     "  Please report this bug.",
                     exc.getMessage());
      }
      _errorAdded = false;
    }
    
    public void test_checkError() {
      _errorAdded = false;
      assertFalse("_checkError should return false", _checkError());
      
      _errorAdded = true;
      assertTrue("_checkError should return true", _checkError());
      assertFalse("_checkError should have set _errorAdded to false.", _errorAdded);
    }    
    
    public void testForClassDefDoFirst() {      
      ClassDef cd = 
        new ClassDef(SourceInfo.NO_INFO, _publicMav, 
                     new Word(SourceInfo.NO_INFO, "Awesome"),
                     new TypeParameter[0], 
                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                     new ReferenceType[0], 
                     new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      testLLVisitor.forClassDefDoFirst(cd);
      assertEquals("There should be no errors.", 0, errors.size());
      testLLVisitor._importedFiles.addLast(new File("Awesome").getAbsolutePath());
      testLLVisitor.forClassDefDoFirst(cd);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", "The class Awesome was already imported.", 
                   errors.get(0).getFirst());
      
      ClassDef cd2 = new ClassDef(SourceInfo.NO_INFO, _privateMav, 
                                  new Word(SourceInfo.NO_INFO, "privateClass"),
                                  new TypeParameter[0], 
                                  new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                                  new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      testLLVisitor.forClassDefDoFirst(cd2);
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("The 2nd error message should be correct", "Top level classes cannot be private", 
                   errors.get(1).getFirst());
      
    }
    
    public void testForInterfaceDefDoFirst() {
      InterfaceDef id = new InterfaceDef(SourceInfo.NO_INFO, _publicMav, 
                                         new Word(SourceInfo.NO_INFO, "Awesome"),
                                         new TypeParameter[0], new ReferenceType[0], 
                                         new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      testLLVisitor.forInterfaceDefDoFirst(id);
      assertEquals("There should be no errors.", 0, errors.size());
      
      InterfaceDef id2 = new InterfaceDef(SourceInfo.NO_INFO, _privateMav, 
                                          new Word(SourceInfo.NO_INFO, "privateinterface"),
                                          new TypeParameter[0], new ReferenceType[0], 
                                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      testLLVisitor.forInterfaceDefDoFirst(id2);
      assertEquals("There should be 1 errors", 1, errors.size());
      assertEquals("The error message should be correct", "Top level interfaces cannot be private", 
                   errors.get(0).getFirst());
      
      InterfaceDef id3 = new InterfaceDef(SourceInfo.NO_INFO, _finalMav, 
                                          new Word(SourceInfo.NO_INFO, "finalinterface"),
                                          new TypeParameter[0], new ReferenceType[0], 
                                          new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      testLLVisitor.forInterfaceDefDoFirst(id3);
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "Interfaces cannot be final", errors.get(1).getFirst());      
    }
    
    public void testForInnerInterfaceDefDoFirst() {
      InterfaceDef id = new InterfaceDef(SourceInfo.NO_INFO, _publicMav, 
                                         new Word(SourceInfo.NO_INFO, "Awesome"),
                                         new TypeParameter[0], new ReferenceType[0], 
                                         new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      id.visit(testLLVisitor);
      assertEquals("There should be no errors.", 0, errors.size());
      
      InnerInterfaceDef id2 = 
        new InnerInterfaceDef(SourceInfo.NO_INFO, _finalMav, new Word(SourceInfo.NO_INFO, "finalinterface"),
                              new TypeParameter[0], new ReferenceType[0], 
                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      id2.visit(testLLVisitor);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "Interfaces cannot be final", errors.get(0).getFirst());   
    }
    
    public void testForPackageStatementOnly() {
      Word[] words = new Word[] {new Word(SourceInfo.NO_INFO, "alpha"),
        new Word(SourceInfo.NO_INFO, "beta")};
      CompoundWord cw = new CompoundWord(SourceInfo.NO_INFO, words);
      PackageStatement ps = new PackageStatement(SourceInfo.NO_INFO, cw);
      testLLVisitor._file = new File("alpha/beta/delta");
      testLLVisitor.forPackageStatementOnly(ps);
      assertEquals("_package should be set correctly.", "alpha.beta", testLLVisitor._package);
      assertEquals("There should be no errors.", 0, errors.size());
      testLLVisitor._file = new File("alpha/beta/beta/delta");
      testLLVisitor.forPackageStatementOnly(ps);
      assertEquals("_package should be set correctly.", "alpha.beta", testLLVisitor._package);
      assertEquals("There should be one error.", 1, errors.size());
      assertEquals("The error message should be correct.", "The package name must mirror your file's directory.", 
                   errors.get(0).getFirst());
    }
    
    public void testForClassImportStatementOnly() {
      
      
      Word[] words = new Word[] {new Word(SourceInfo.NO_INFO, "alpha"),
        new Word(SourceInfo.NO_INFO, "beta")};
      CompoundWord cw = new CompoundWord(SourceInfo.NO_INFO, words);
      ClassImportStatement cis = new ClassImportStatement(SourceInfo.NO_INFO, cw);
      SymbolData sd = new SymbolData("alpha.beta");
      testLLVisitor.forClassImportStatementOnly(cis);
      assertTrue("imported files should contain alpha.beta", testLLVisitor._importedFiles.contains("alpha.beta"));
      assertEquals("There should be a continuation.", sd, LanguageLevelConverter.symbolTable.get("alpha.beta"));
      assertTrue("It should be in continuations.", testLLVisitor.continuations.containsKey("alpha.beta"));
      
      
      
      Word[] words2 = new Word[] {new Word(SourceInfo.NO_INFO, "gamma"),
        new Word(SourceInfo.NO_INFO, "beta")};
      CompoundWord cw2 = new CompoundWord(SourceInfo.NO_INFO, words2);
      ClassImportStatement cis2 = new ClassImportStatement(SourceInfo.NO_INFO, cw2);
      cis2.visit(testLLVisitor);
      
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The class beta has already been imported.", 
                   errors.get(0).getFirst());
      
      
      testLLVisitor._package = "myPackage";
      Word[] words3 = 
        new Word[] { new Word(SourceInfo.NO_INFO, "myPackage"), new Word(SourceInfo.NO_INFO, "cookie")};
      CompoundWord cw3 = new CompoundWord(SourceInfo.NO_INFO, words3);
      ClassImportStatement cis3 = new ClassImportStatement(SourceInfo.NO_INFO, cw3);
      cis3.visit(testLLVisitor);
      
      assertEquals("There should now be 2 errors", 2, errors.size());
      assertEquals("The second error message should be correct", "You do not need to import myPackage.cookie." + 
                   "  It is in your package so it is already visible", errors.get(1).getFirst());
      
      
    }
    
    public void testForPackageImportStatementOnly() {
      
      Word[] words = new Word[] {new Word(SourceInfo.NO_INFO, "alpha"),
        new Word(SourceInfo.NO_INFO, "beta")};
      CompoundWord cw = new CompoundWord(SourceInfo.NO_INFO, words);
      PackageImportStatement cis = new PackageImportStatement(SourceInfo.NO_INFO, cw);
      SymbolData sd = new SymbolData("alpha.beta");
      testLLVisitor.forPackageImportStatementOnly(cis);
      assertEquals("There should be no errorrs", 0, errors.size());
      assertTrue("Imported Packages should now contain alpha.beta", 
                 testLLVisitor._importedPackages.contains("alpha.beta"));
      
      
      testLLVisitor._package = "myPackage";
      Word[] words3 = new Word[] {new Word(SourceInfo.NO_INFO, "myPackage"), new Word(SourceInfo.NO_INFO, 
                                                                                              "cookie")};
      CompoundWord cw3 = new CompoundWord(SourceInfo.NO_INFO, words3);
      PackageImportStatement pis = new PackageImportStatement(SourceInfo.NO_INFO, cw3);
      pis.visit(testLLVisitor);
      
      assertEquals("There should be no errors", 0, errors.size());
      assertTrue("Imported Packages should now contain myPackage.cookie", 
                 testLLVisitor._importedPackages.contains("myPackage.cookie"));
      
      
      
      
      Word[] words2 = new Word[] {new Word(SourceInfo.NO_INFO, "myPackage")};
      CompoundWord cw2 = new CompoundWord(SourceInfo.NO_INFO, words2);
      PackageImportStatement pis2 = new PackageImportStatement(SourceInfo.NO_INFO, cw2);
      pis2.visit(testLLVisitor);
      
      assertEquals("There should now be 1 errors", 1, errors.size());
      assertEquals("The error message should be correct", "You do not need to import package myPackage." + 
                   " It is your package so all public classes in it are already visible.", errors.get(0).getFirst());
      
    }
    
    public void testForSourceFile() {
      ClassDef cd = new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Awesome"),
                                 new TypeParameter[0], 
                                 new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Object", new Type[0]), 
                                 new ReferenceType[0], 
                                 new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      ClassDef cd2 = new ClassDef(SourceInfo.NO_INFO, _publicMav, new Word(SourceInfo.NO_INFO, "Gnarly"),
                                  new TypeParameter[0], 
                                  new ClassOrInterfaceType(SourceInfo.NO_INFO, "Awesome", new Type[0]), 
                                  new ReferenceType[0], 
                                  new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      InterfaceDef id = new InterfaceDef(SourceInfo.NO_INFO, _publicMav, 
                                         new Word(SourceInfo.NO_INFO, "NiftyWords"),
                                         new TypeParameter[0], 
                                         new ReferenceType[0], 
                                         new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      SourceFile sf = new SourceFile(SourceInfo.NO_INFO,
                                     new PackageStatement[0],
                                     new ImportStatement[0],
                                     new TypeDefBase[] {cd, cd2, id});
      testLLVisitor.forSourceFile(sf);
      
      assertTrue("_classNamesInThisFile should contain the two ClassDefs.", 
                 testLLVisitor._classNamesInThisFile.contains("Awesome"));
      assertTrue("_classNamesInThisFile should contain the two ClassDefs.", 
                 testLLVisitor._classNamesInThisFile.contains("Gnarly"));
      
      assertTrue("_classNamesInThisFile should contain the InterfaceDef", 
                 testLLVisitor._classNamesInThisFile.contains("NiftyWords"));
      assertTrue("_classesToBeParsed should contain the two ClassDefs.", 
                 testLLVisitor._classesToBeParsed.containsKey("Awesome"));
      assertTrue("_classesToBeParsed should contain the two ClassDefs.", 
                 testLLVisitor._classesToBeParsed.containsKey("Gnarly"));
      assertTrue("_classesToBeParsed should contain the InterfaceDef", 
                 testLLVisitor._classesToBeParsed.containsKey("NiftyWords"));
      
    }
    
    public void testReferenceType2String() {
      
      TypeVariable tv = new TypeVariable(SourceInfo.NO_INFO, "T");
      String[] result = testLLVisitor.referenceType2String(new ReferenceType[] { tv });
      assertEquals("There should not be any errors.", 0, errors.size());
      assertEquals("Results should have one String.", 1, result.length);
      assertEquals("The String should be \"T\".", "T", result[0]);
      
      
      ClassOrInterfaceType coit = new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                           "MyClass", 
                                                           new Type[] { new TypeVariable(SourceInfo.NO_INFO, "T"),
        new TypeVariable(SourceInfo.NO_INFO, "U")}
      );
      result = testLLVisitor.referenceType2String(new ReferenceType[] { tv, coit });
      assertEquals("There should not be any errors.", 0, errors.size());
      assertEquals("Results should have two Strings.", 2, result.length);
      assertEquals("The first String should be \"T\".", "T", result[0]);
      assertEquals("The second String should be \"MyClass\".", "MyClass", result[1]);
      
      
      MemberType mt = new MemberType(SourceInfo.NO_INFO,
                                     "MyClass.MyClass2",
                                     coit,
                                     new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                              "MyClass2", 
                                                              new Type[0]));
      result = testLLVisitor.referenceType2String(new ReferenceType[] { mt });
      assertEquals("There should not be any errors.", 0, errors.size());
      assertEquals("Results should have one String.", 1, result.length);
      assertEquals("The first String should be \"MyClass.MyClass2\".", "MyClass.MyClass2", result[0]);
    }
    
    
    public void testExceptionsInSymbolTable() {
            
      
      ClassOrInterfaceType exceptionType = new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                    "java.util.prefs.BackingStoreException", 
                                                                    new Type[0]);
      ParenthesizedExpressionList expList = new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]);
      
      BracedBody bb = 
        new BracedBody(SourceInfo.NO_INFO, 
                       new BodyItemI[] { new ThrowStatement(SourceInfo.NO_INFO, 
                                                            new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                                                                              exceptionType, expList))});
      bb.visit(testLLVisitor);
      assertFalse("The SymbolTable should have java.util.prefs.BackingStoreException", 
                  LanguageLevelConverter.symbolTable.get("java.util.prefs.BackingStoreException") == null);
      
    }
    
    public void testShouldBreak() {
      
      LeftShiftAssignmentExpression shift1 = 
        new LeftShiftAssignmentExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                          new NullLiteral(SourceInfo.NO_INFO));
      RightUnsignedShiftAssignmentExpression shift2 = 
        new RightUnsignedShiftAssignmentExpression(SourceInfo.NO_INFO, 
                                                   new NullLiteral(SourceInfo.NO_INFO), 
                                                   new NullLiteral(SourceInfo.NO_INFO));
      RightSignedShiftAssignmentExpression shift3 = 
        new RightSignedShiftAssignmentExpression(SourceInfo.NO_INFO, 
                                                 new NullLiteral(SourceInfo.NO_INFO), 
                                                 new NullLiteral(SourceInfo.NO_INFO));
      
      
      shift1.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());



      
      shift2.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());



      
      shift3.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());



    
      
      BitwiseAndAssignmentExpression bit1 = 
        new BitwiseAndAssignmentExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                           new NullLiteral(SourceInfo.NO_INFO));
      BitwiseOrAssignmentExpression bit2 = 
        new BitwiseOrAssignmentExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                          new NullLiteral(SourceInfo.NO_INFO));
      BitwiseXorAssignmentExpression bit3 = 
        new BitwiseXorAssignmentExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                           new NullLiteral(SourceInfo.NO_INFO));
      
      bit1.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      bit2.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      bit3.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      
      BitwiseAndExpression bit4 = 
        new BitwiseAndExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                 new NullLiteral(SourceInfo.NO_INFO));
      BitwiseOrExpression bit5 = 
        new BitwiseOrExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                new NullLiteral(SourceInfo.NO_INFO));
      BitwiseXorExpression bit6 = 
        new BitwiseXorExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                 new NullLiteral(SourceInfo.NO_INFO));
      BitwiseNotExpression bit7 = 
        new BitwiseNotExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
      
      
      bit4.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());




      
      bit5.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());




      
      bit6.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      bit7.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());




      
      
      LeftShiftExpression shift4 = 
        new LeftShiftExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                new NullLiteral(SourceInfo.NO_INFO));
      RightUnsignedShiftExpression shift5 = 
        new RightUnsignedShiftExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                         new NullLiteral(SourceInfo.NO_INFO));
      RightSignedShiftExpression shift6 = 
        new RightSignedShiftExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                       new NullLiteral(SourceInfo.NO_INFO));
      
      shift4.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      shift5.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      shift6.visit(testLLVisitor);
      assertEquals("Should be no errors", 0, errors.size());


      
      
      EmptyExpression e = new EmptyExpression(SourceInfo.NO_INFO);
      e.visit(testLLVisitor);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You appear to be missing an expression here", 
                   errors.getLast().getFirst());
      
      
      NoOpExpression noop = 
        new NoOpExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                           new NullLiteral(SourceInfo.NO_INFO));
      noop.visit(testLLVisitor);
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "You are missing a binary operator here", 
                   errors.getLast().getFirst());
    }
    
    public void testIsConstructor() {
      MethodData constr = 
        new MethodData("monkey", _publicMav, new TypeParameter[0], _sd1, new VariableData[0], new String[0], _sd1, 
                       new NullLiteral(SourceInfo.NO_INFO));
      MethodData notRightOuter = 
        new MethodData("monkey", _publicMav, new TypeParameter[0], _sd1, new VariableData[0], new String[0], _sd2, 
                       new NullLiteral(SourceInfo.NO_INFO));
      _sd2.setOuterData(_sd1);
      _sd1.addInnerClass(_sd2);
      MethodData notRightName = 
        new MethodData("chimp", _publicMav, new TypeParameter[0], _sd1, new VariableData[0], new String[0], _sd1, 
                       new NullLiteral(SourceInfo.NO_INFO));
      MethodData notRightReturnType = 
        new MethodData("monkey", _publicMav, new TypeParameter[0], _sd2, new VariableData[0], new String[0], _sd1, 
                       new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertTrue(testLLVisitor.isConstructor(constr));
      
      
      assertFalse(testLLVisitor.isConstructor(notRightOuter));
      
      
      assertFalse(testLLVisitor.isConstructor(notRightName));
      
      
      assertFalse(testLLVisitor.isConstructor(notRightReturnType));
      
      
      assertFalse(testLLVisitor.isConstructor(_sd1));
    } 
  }
}
