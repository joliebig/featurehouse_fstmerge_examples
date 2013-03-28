
package genj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EnvironmentChecker {
  
  private static Logger LOG = Logger.getLogger("genj.util");

  private final static String[] SYSTEM_PROPERTIES = {
        "java.vendor", "java.vendor.url",
        "java.version", "java.class.version",
        "os.name", "os.arch", "os.version",
        "browser", "browser.vendor", "browser.version",
        "user.name", "user.dir", "user.home", "all.home", "user.home.genj", "all.home.genj"
  };
  
  private final static Set NOOVERRIDE = new HashSet();
  
  
  public static boolean isJava14(Object receipient) {
    String version = getProperty(receipient, "java.version", "", "Checking Java VM version");
    
    if (version.startsWith("1.1") || version.startsWith("1.2")  || version.startsWith("1.3"))
      return false;
    
    return true;
  }
  
  
  public static boolean isJava15(Object receipient) {
    String version = getProperty(receipient, "java.version", "", "Checking Java VM version");
    
    return version.startsWith("1.5") || version.startsWith("1.6");
  }

  
  public static boolean isJava16(Object receipient) {
    String version = getProperty(receipient, "java.version", "", "Checking Java VM version");
    return version.startsWith("1.6");
  }
  
  
  public static boolean isMac() {
    return getProperty(EnvironmentChecker.class, "mrj.version", null, "isMac()")!=null;
  }
  
  
  public static boolean isWindows() {
    return getProperty(EnvironmentChecker.class, "os.name", "", "isWindows()").indexOf("Windows")>-1;
  }
  
  private static String getDatePattern(int format) {
    try {
      return ((SimpleDateFormat)DateFormat.getDateInstance(format)).toPattern();
    } catch (Throwable t) {
      return "?";
    }
  }

  
  public static void log() {
    
    
    for (int i=0; i<SYSTEM_PROPERTIES.length; i++) {
      String key = SYSTEM_PROPERTIES[i];
      String msg = key + " = "+getProperty(EnvironmentChecker.class, SYSTEM_PROPERTIES[i], "", "check system props");
      if (NOOVERRIDE.contains(key))
        msg += " (no override)";
      LOG.info(msg);
    }
    
    
    LOG.info("Locale = "+Locale.getDefault());
    LOG.info("DateFormat (short) = "+getDatePattern(DateFormat.SHORT));
    LOG.info("DateFormat (medium) = "+getDatePattern(DateFormat.MEDIUM));
    LOG.info("DateFormat (long) = "+getDatePattern(DateFormat.LONG));
    LOG.info("DateFormat (full) = "+getDatePattern(DateFormat.FULL));

      try {
        
      
      String cpath = getProperty(EnvironmentChecker.class, "java.class.path", "", "check classpath");
      StringTokenizer tokens = new StringTokenizer(cpath,System.getProperty("path.separator"),false);
      while (tokens.hasMoreTokens()) {
        String entry = tokens.nextToken();
        String stat = checkClasspathEntry(entry) ? " (does exist)" : "";
        LOG.info("Classpath = "+entry+stat);
      }
      
      
      ClassLoader cl = EnvironmentChecker.class.getClassLoader();
      while (cl!=null) {
        if (cl instanceof URLClassLoader) {
          LOG.info("URLClassloader "+cl + Arrays.asList(((URLClassLoader)cl).getURLs()));
        } else {
          LOG.info("Classloader "+cl);
        }
        cl = cl.getParent();
      }
      
      
      Runtime r = Runtime.getRuntime();
      LOG.log(Level.INFO, "Memory Max={0}/Total={1}/Free={2}", new Long[]{ new Long(r.maxMemory()), new Long(r.totalMemory()), new Long(r.freeMemory()) });

      
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "unexpected exception in log()", t);
    }
  }

  
  private static boolean checkClasspathEntry(String entry) {
    try {
      return new File(entry).exists();
    } catch (Throwable t) {
    }
    return false;
  }

  
  public static String getProperty(Object receipient, String key, String fallback, String msg) {
    return getProperty(receipient, new String[]{key}, fallback, msg);
  }

  
  public static String getProperty(Object receipient, String[] keys, String fallback, String msg) {
    
    String key = null, val, postfix;
    try {
      for (int i=0; i<keys.length; i++) {
        
        key = keys[i];
        
        int pf = key.indexOf('/');
        if (pf<0) pf = key.length();
        postfix = key.substring(pf);
        key = key.substring(0,pf);
        
        val = System.getProperty(key);
        
        if (val!=null) {
          LOG.finer("Using system-property "+key+'='+val+" ("+msg+')');
          return val+postfix;
        }
      }
    } catch (Throwable t) {
      LOG.log(Level.INFO, "Couldn't access system property "+key+" ("+t.getMessage()+")");
    }
    
    if (fallback!=null)
      LOG.fine("Using fallback for system-property "+key+'='+fallback+" ("+msg+')');
    return fallback;
  }
  
  
  public static void loadSystemProperties(InputStream in) throws IOException {
    try {
      Properties props = new Properties();
      props.load(in);
      for (Iterator keys = props.keySet().iterator(); keys.hasNext(); ) {
        String key = keys.next().toString();
        if (System.getProperty(key)==null)
          setProperty(key, props.getProperty(key));
      }
    } catch (Throwable t) {
      if (t instanceof IOException)
        throw (IOException)t;
      throw new IOException("unexpected throwable "+t.getMessage());
    }
  }
  
  
  private static void setProperty(String key, String val) {
    String old = System.getProperty(key); 
    if (old==null) {
      System.setProperty(key, val);
    } else {
      NOOVERRIDE.add(key);
    }
  }
  
  
  static {
    try {
      EnvironmentChecker.loadSystemProperties(new FileInputStream(new File("system.properties")));
    } catch (IOException e) {
    }
  }

  
  static {
    
    
    if (isWindows()) {
      
      String QUERY = "reg query \"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\ProfileList\"";
      Pattern PATTERN  = Pattern.compile(".*AllUsersProfile\tREG_SZ\t(.*)");
      String value = null;
      try {
        Process process = Runtime.getRuntime().exec(QUERY);
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (true) {
          String line = in.readLine();
          if (line==null) break;
          Matcher match = PATTERN.matcher(line);
          if (match.matches()) {
            File home = new File(new File(System.getProperty("user.home")).getParent(), match.group(1));
            if (home.isDirectory())
              setProperty("all.home", home.getAbsolutePath());
            break;
          }
        }
        in.close();
      } catch (Throwable t) {
      }
    }
    
  }
  
  
  static {

    try {
      File user_home_genj;
      File home = new File(System.getProperty("user.home"));
      File dotgenj = new File(home, ".genj3");
      File appdata = new File(home, "Application Data");
      if (!isWindows() || dotgenj.isDirectory() || !appdata.isDirectory())
        user_home_genj = dotgenj;
      else
        user_home_genj = new File(appdata, "GenJ3");
      
      setProperty("user.home.genj", user_home_genj.getAbsolutePath());

    } catch (Throwable t) {
      
    }
    
  }
  
  
  static {

    try {
      if (isWindows()) {
        File app_data = new File(System.getProperty("all.home"), "Application Data");
        if (app_data.isDirectory())
          setProperty("all.home.genj", new File(app_data, "GenJ").getAbsolutePath());
      }
    } catch (Throwable t) {
      
    }
    
  }
  
}
