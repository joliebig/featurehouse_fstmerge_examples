

package edu.rice.cs.plt.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.text.TextUtil;

import static edu.rice.cs.plt.io.IOUtil.attemptAbsoluteFiles;
import static edu.rice.cs.plt.iter.IterUtil.snapshot;
import static edu.rice.cs.plt.collect.CollectUtil.snapshot;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class JVMBuilder implements Lambda2<String, Iterable<? extends String>, Process> {
  
  private static final String DEFAULT_JAVA_COMMAND = findJavaCommand(System.getProperty("java.home", ""));
  private static final SizedIterable<String> DEFAULT_JVM_ARGS = IterUtil.empty();
  private static final SizedIterable<File> DEFAULT_CLASS_PATH = attemptAbsoluteFiles(ReflectUtil.SYSTEM_CLASS_PATH);
  private static final File DEFAULT_DIR = IOUtil.WORKING_DIRECTORY;
  private static final Map<String, String> DEFAULT_PROPERTIES = Collections.emptyMap();
  private static final Map<String, String> DEFAULT_ENVIRONMENT = null;
  
  public static final JVMBuilder DEFAULT = new JVMBuilder();
  
  private final String _javaCommand;
  private final SizedIterable<String> _jvmArgs;
  private final SizedIterable<File> _classPath;
  private final File _dir;
  private final Map<String, String> _properties;
  private final Map<String, String> _environment;
  
  private JVMBuilder() {
    this(DEFAULT_JAVA_COMMAND, DEFAULT_JVM_ARGS, DEFAULT_CLASS_PATH, DEFAULT_DIR, DEFAULT_PROPERTIES,
         DEFAULT_ENVIRONMENT, true);
  }
  
  public JVMBuilder(String javaCommand) {
    this(findJavaCommand(javaCommand), DEFAULT_JVM_ARGS, DEFAULT_CLASS_PATH, DEFAULT_DIR,
         DEFAULT_PROPERTIES, DEFAULT_ENVIRONMENT, true);
  }
  
  public JVMBuilder(String javaCommand, Iterable<? extends String> jvmArgs) {
    this(findJavaCommand(javaCommand), snapshot(jvmArgs), DEFAULT_CLASS_PATH, DEFAULT_DIR,
         DEFAULT_PROPERTIES, DEFAULT_ENVIRONMENT, true);
  }
  
  public JVMBuilder(Iterable<? extends File> classPath) {
    this(DEFAULT_JAVA_COMMAND, DEFAULT_JVM_ARGS, attemptAbsoluteFiles(classPath), DEFAULT_DIR,
         DEFAULT_PROPERTIES, DEFAULT_ENVIRONMENT, true);
  }
  
  public JVMBuilder(File dir) {
    this(DEFAULT_JAVA_COMMAND, DEFAULT_JVM_ARGS, DEFAULT_CLASS_PATH, dir, DEFAULT_PROPERTIES,
         DEFAULT_ENVIRONMENT, true);
  }
  
  public JVMBuilder(String javaCommand, Iterable<? extends String> jvmArgs, Iterable<? extends File> classPath,
                    File dir, Map<? extends String, ? extends String> properties,
                    Map<? extends String, ? extends String> environment) {
    this(findJavaCommand(javaCommand), snapshot(jvmArgs), attemptAbsoluteFiles(classPath), dir,
         snapshot(properties), (environment == null) ? null : snapshot(environment), true);
  }
  
  
  private JVMBuilder(String javaCommand, SizedIterable<String> jvmArgs, SizedIterable<File> classPath,
                     File dir, Map<String, String> properties, Map<String, String> environment, boolean dummy) {
    _javaCommand = javaCommand;
    _jvmArgs = jvmArgs;
    _classPath = classPath;
    _dir = dir;
    _properties = properties;
    _environment = environment;
  }
  
  public String javaCommand() { return _javaCommand; }
  
  public JVMBuilder javaCommand(String javaCommand) {
    return new JVMBuilder(findJavaCommand(javaCommand), _jvmArgs, _classPath, _dir, _properties, _environment, true);
  }
  
  public JVMBuilder javaCommand(File javaCommand) {
    return new JVMBuilder(findJavaCommand(javaCommand), _jvmArgs, _classPath, _dir, _properties, _environment, true);
  }
  
  public SizedIterable<String> jvmArguments() { return _jvmArgs; }
  
  public JVMBuilder jvmArguments(Iterable<? extends String> jvmArgs) {
    return new JVMBuilder(_javaCommand, IterUtil.snapshot(jvmArgs), _classPath, _dir, _properties, _environment, true);
  }
  
  
  public JVMBuilder jvmArguments(String... jvmArgs) {
    return new JVMBuilder(_javaCommand, IterUtil.make(jvmArgs), _classPath, _dir, _properties, _environment, true);
  }
  
  public SizedIterable<File> classPath() { return _classPath; }
  
  public JVMBuilder classPath(Iterable<? extends File> classPath) {
    return new JVMBuilder(_javaCommand, _jvmArgs, attemptAbsoluteFiles(classPath), _dir,
                          _properties, _environment, true);
  }
  
  public JVMBuilder classPath(String classPath) {
    return new JVMBuilder(_javaCommand, _jvmArgs, attemptAbsoluteFiles(IOUtil.parsePath(classPath)), _dir,
                          _properties, _environment, true);
  }
  
  
  public JVMBuilder classPath(File... classPath) {
    return new JVMBuilder(_javaCommand, _jvmArgs, attemptAbsoluteFiles(IterUtil.asIterable(classPath)), _dir,
                          _properties, _environment, true);
  }
  
  public File directory() { return _dir; }
  
  public JVMBuilder directory(File dir) {
    return new JVMBuilder(_javaCommand, _jvmArgs, _classPath, dir, _properties, _environment, true);
  }
  
  public JVMBuilder directory(String dir) {
    return new JVMBuilder(_javaCommand, _jvmArgs, _classPath, new File(dir), _properties, _environment, true);
  }
  
  
  public Map<String, String> properties() { return CollectUtil.immutable(_properties); }
  
  
  public Map<String, String> propertiesCopy() { return snapshot(_properties); }
  
  
  public JVMBuilder properties(Properties ps) {
    return new JVMBuilder(_javaCommand, _jvmArgs, _classPath, _dir, copyProps(ps), _environment, true);
  }
  
  
  public JVMBuilder properties(Map<? extends String, ? extends String> ps) {
    return new JVMBuilder(_javaCommand, _jvmArgs, _classPath, _dir, snapshot(ps), _environment, true);
  }
  
  
  public JVMBuilder addProperty(String key, String value) {
    Map<String, String> newProps = propertiesCopy();
    newProps.put(key, value);
    return properties(newProps);
  }
  
  
  public JVMBuilder addDefaultProperties(Properties ps) { return addDefaultProperties(copyProps(ps)); }
  
  
  public JVMBuilder addDefaultProperties(Map<? extends String, ? extends String> ps) {
    if (_properties.keySet().containsAll(ps.keySet())) { return this; }
    else {
      Map<String, String> newProps = propertiesCopy();
      for (Map.Entry<? extends String, ? extends String> entry : ps.entrySet()) {
        if (!newProps.containsKey(entry.getKey())) { newProps.put(entry.getKey(), entry.getValue()); }
      }
      return properties(newProps);
    }
  }
  
  
  public JVMBuilder addDefaultProperty(String key, String value) {
    return _properties.containsKey(key) ? this : addProperty(key, value);
  }
  
  
  public Map<String, String> environment() {
    return (_environment == null) ? null : CollectUtil.immutable(_environment);
  }
  
  
  public Map<String, String> environmentCopy() {
    return snapshot((_environment == null) ? System.getenv() : _environment);
  }
  
  
  public JVMBuilder environment(Map<? extends String, ? extends String> env) {
    return new JVMBuilder(_javaCommand, _jvmArgs, _classPath, _dir, _properties,
                          (_environment == null) ? null : snapshot(env), true);
  }
  
  
  public JVMBuilder addEnvironmentVar(String key, String value) {
    Map<String, String> newEnv = environmentCopy();
    newEnv.put(key, value);
    return environment(newEnv);
  }
  
  
  public JVMBuilder addDefaultEnvironmentVars(Map<? extends String, ? extends String> env) {
    if (_environment != null && _environment.keySet().containsAll(env.keySet())) { return this; }
    else {
      Map<String, String> newEnv = environmentCopy();
      for (Map.Entry<? extends String, ? extends String> entry : env.entrySet()) {
        if (!newEnv.containsKey(entry.getKey())) { newEnv.put(entry.getKey(), entry.getValue()); }
      }
      return environment(newEnv);
    }
  }
  
  
  public JVMBuilder AddDefaultEnvironmentVar(String key, String value) {
    if (_environment != null && _environment.containsKey(key)) { return this; }
    else { return addEnvironmentVar(key, value); }
  }
  
  
  
  public Process start(String mainClass, String... mainParams) throws IOException {
    return start(mainClass, IterUtil.asIterable(mainParams));
  }

  
  public Process start(String mainClass, Iterable<? extends String> mainParams) throws IOException {
    List<String> commandL = new LinkedList<String>();
    commandL.add(_javaCommand);
    CollectUtil.addAll(commandL, _jvmArgs);
    commandL.add("-classpath");
    commandL.add(IOUtil.pathToString(_classPath));
    for (Map.Entry<String, String> prop : _properties.entrySet()) {
      commandL.add("-D" + prop.getKey() + "=" + prop.getValue());
    }
    commandL.add(mainClass);
    CollectUtil.addAll(commandL, mainParams);
    String[] command = IterUtil.toArray(commandL, String.class);
    
    String[] env;
    if (_environment == null) { env = null; }
    else {
      List<String> envL = new LinkedList<String>();
      for (Map.Entry<String, String> binding : _environment.entrySet()) {
        envL.add(binding.getKey() + "=" + binding.getValue());
      }
      env = IterUtil.toArray(envL, String.class);
    }
    
    
    
    return Runtime.getRuntime().exec(command, env, _dir);
  }
  
  public Process value(String mainClass, Iterable<? extends String> mainParams) {
    try { return start(mainClass, mainParams); }
    catch (IOException e) { throw new WrappedException(e); }
  }

  private static String findJavaCommand(String command) {
    return findJavaCommand(new File(command));
  }
  
  
  private static String findJavaCommand(File f) {
    if (IOUtil.attemptIsFile(f)) { return f.getPath(); }
    else if (IOUtil.attemptIsDirectory(f)) {
      
      f = IOUtil.attemptAbsoluteFile(f);
      String os = System.getProperty("os.name", "");
      File[] candidates = new File[]{ new File(f, "../bin"), new File(f, "bin"), f };
      
      if (!TextUtil.containsIgnoreCase(os, "netware")) { 
        if (TextUtil.containsIgnoreCase(os, "windows")) {
          for (File dir : candidates) {
            File result = new File(dir, "javaw.exe");
            if (IOUtil.attemptExists(result)) { return result.getPath(); }
            result = new File(dir, "java.exe");
            if (IOUtil.attemptExists(result)) { return result.getPath(); }
          }
        }
        else {
          for (File dir : candidates) {
            File result = new File(dir, "java");
            if (IOUtil.attemptExists(result)) { return result.getPath(); }
          }
        }
      }
    }
    
    return f.toString();
  }
  
  
  
  private static Map<String, String> copyProps(Properties p) {
    Map<String, String> result = new HashMap<String, String>();
    @SuppressWarnings("unchecked") Enumeration<String> names = (Enumeration<String>) p.propertyNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      result.put(name, p.getProperty(name));
    }
    return result;
  }
  
}
