

package edu.rice.cs.drjava.model.compiler;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.plt.reflect.JavaVersion;


public abstract class JavacCompiler implements CompilerInterface {
  
  protected final JavaVersion.FullVersion _version;
  protected final String _location;
  protected List<? extends File> _defaultBootClassPath;
  
  protected JavacCompiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    _version = version;
    _location = location;
    _defaultBootClassPath = defaultBootClassPath;
  }
  
  public abstract boolean isAvailable();
  
  public abstract List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                                        List<? extends File> sourcePath, File destination, 
                                                        List<? extends File> bootClassPath, String sourceVersion, 
                                                        boolean showWarnings);
  
  public JavaVersion version() { return _version.majorVersion(); } 
 
  public String getName() { return "JDK " + _version.versionString(); }
  
  public String getDescription() { return getName() + " from " + _location; }
  
  public String toString() { return getName(); }
  
  
  public List<File> additionalBootClassPathForInteractions() { return Arrays.<File>asList(); }

  
  public String transformCommands(String interactionsString) {
    if (interactionsString.startsWith("java ")) {
      interactionsString = transformJavaCommand(interactionsString);
    }
    else if (interactionsString.startsWith("applet ")) {
        interactionsString = transformAppletCommand(interactionsString);
    }
    else if (interactionsString.startsWith("run ")) {
        interactionsString = transformRunCommand(interactionsString);
    }
    return interactionsString;
  }
  
  public static String transformJavaCommand(String s) {
    
    String command = 
      "try '{'\n" +
      "  java.lang.reflect.Method m = {0}.class.getMethod(\"main\", java.lang.String[].class);\n" +
      "  if (!m.getReturnType().equals(void.class)) throw new java.lang.NoSuchMethodException();\n" +
      "'}'\n" +
      "catch (java.lang.NoSuchMethodException e) '{'\n" +
      "  throw new java.lang.NoSuchMethodError(\"main\");\n" +
      "'}'\n" +
      "{0}.main(new String[]'{'{1}'}');";
    return _transformCommand(s, command);
  }
  
  public static String transformAppletCommand(String s) {
    return _transformCommand(s,"edu.rice.cs.plt.swing.SwingUtil.showApplet(new {0}({1}), 400, 300);");
  }

  
  
  
  
  
  
  
  
  public static String transformRunCommand(String s) {
    
    String command = 
      "'{' boolean isProgram = false; boolean isApplet = false; Class c = {0}.class;\n" +
      
      "while(c != null) '{'\n" +
      "  if (\"acm.program.Program\".equals(c.getName())) '{' isProgram = true; break; '}'\n" +
      "  c = c.getSuperclass();\n" +
      "'}'\n" +
      "if (!isProgram) '{'\n" +
      "  try '{'\n" +
      
      "    {0}.class.asSubclass(java.applet.Applet.class);\n" +
      "    isApplet = true;\n" +
      "  '}' catch(ClassCastException cce) '{' '}'\n" +
      "'}'\n" +
      "if (isApplet) '{'\n" +
      "  edu.rice.cs.plt.swing.SwingUtil.showApplet(java.applet.Applet.class.cast(new {0}({1})), 400, 300);\n" +
      "'}'\n" +
      "else '{'" +
      "  java.lang.reflect.Method m = null;\n" +
      "  try '{'\n" +
      "    m = {0}.class.getMethod(\"main\", java.lang.String[].class);\n" +
      "    if (!m.getReturnType().equals(void.class)) throw new java.lang.NoSuchMethodException();\n" +
      "  '}'\n" +
      "  catch (java.lang.NoSuchMethodException e) '{'\n" +
      "    throw new java.lang.NoSuchMethodError(\"main\");\n" +
      "  '}'\n" +
      "  String[] args = new String[]'{'{1}'}';\n" +
      "  if (isProgram) '{'\n" +
      "    String[] newArgs = new String[args.length+1];\n" +
      "    newArgs[0] = \"code={0}\";\n" +
      "    System.arraycopy(args, 0, newArgs, 1, args.length);\n" +
      "    args = newArgs;\n" +
      "  '}'\n" +
      "  m.invoke(null, new Object[] '{' args '}');\n"+
      "'}' '}'";
    return _transformCommand(s, command);
  }

  
  protected static String _transformCommand(String s, String command) {
    if (s.endsWith(";"))  s = _deleteSemiColon(s);
    List<String> args = ArgumentTokenizer.tokenize(s, true);
    final String classNameWithQuotes = args.get(1); 
    final String className = classNameWithQuotes.substring(1, classNameWithQuotes.length() - 1); 
    final StringBuilder argsString = new StringBuilder();
    boolean seenArg = false;
    for (int i = 2; i < args.size(); i++) {
      if (seenArg) argsString.append(",");
      else seenArg = true;
      argsString.append(args.get(i));
    }
    return java.text.MessageFormat.format(command, className, argsString.toString());
  }
  
  
  protected static String _deleteSemiColon(String s) { return  s.substring(0, s.length() - 1); }
}
