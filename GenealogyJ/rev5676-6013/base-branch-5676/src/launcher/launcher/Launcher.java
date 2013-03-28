package launcher;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.BindException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import launcher.ipc.CallHandler;
import launcher.ipc.Client;
import launcher.ipc.Server;





public class Launcher {

  private final static Logger LOG = Logger.getLogger("launcher");
  
  private static Manifest manifest;
  private static Method main;
  
  public final static String 
    MANIFEST = "META-INF/MANIFEST.MF",
    LAUNCH_CLASSPATH = "Launch-Classpath",
    LAUNCH_CLASS = "Launch-Class",
    LAUNCH_PORT = "Launch-Port";
  
  
  public static void main(String[] args) {
    
    try {
      
      
      if (!setupIPC(args))
        return;
      
      
      cd(Launcher.class);
      
      
      callMain(args);
      
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }

    
  }
  
  
  private static void callMain(String[] args) throws Exception {

    
    if (main==null) {

        
      String[] classpath = getLaunchClasspath();
      
      
      setClasspath(classpath);
      
      
      ClassLoader cl  = getClassLoader(classpath);
  
      
      Thread.currentThread().setContextClassLoader(cl);
      Class clazz = cl.loadClass( getLaunchClass());
      main = clazz.getMethod("main", new Class[]{String[].class});
      
    }
    
    
    main.invoke(null, new Object[]{args});
    
  }
  
  
  private static boolean setupIPC(String[] args) {
    
    final String launchClass = getLaunchClass();
    int port = getLaunchPort();
    
    
    if (port>0) {
      
      
      int published = -1;
      try {
        
        published = Preferences.userNodeForPackage(Launcher.class).getInt(launchClass, 0);
        if (published>0) {
          
          
          if ("OK".equals(new Client().send(published, encode(args)))) {
            LOG.log(Level.FINE, "sent launch to server on port "+published);
            
            return false;
          }
        }
      } catch (Throwable t) {
        LOG.log(Level.FINE, "couldn't send launch to server on port "+published);
      }
      
      
      try {
        
        CallHandler handler = new CallHandler() {
          public String handleCall(String msg) {
            try {
              callMain(decode(msg));
            } catch (Throwable t) {
              return "ERR";
            }
            return "OK";
          }
        };
        
        for (int i=0;i<10;port++,i++) {
          try {
            new Server(port, handler);
            break;
          } catch (BindException e) {
            LOG.log(Level.FINE, "couldn't bind server to port "+port);
          }
        }
        
        Preferences.userNodeForPackage(Launcher.class).putInt(launchClass, port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { 
          public void run() {
            Preferences.userNodeForPackage(Launcher.class).putInt(launchClass, 0);
          }
        }));
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "cannot become launch server", t);
      }
    }    
    
    
    return true;
  }
  
  private static String[] decode(String msg) {
    StringTokenizer lines = new StringTokenizer(msg, "\n");
    String[] args = new String[lines.countTokens()];
    for (int i = 0; i < args.length; i++) 
      args[i] = lines.nextToken();
    return args;
  }
  
  private static String encode(String[] args) {
    StringBuffer msg = new StringBuffer();
    for (int i = 0; i < args.length; i++) {
      if (i>0) msg.append("\n");
      msg.append(args[i]);
    }
    msg.append("\n\n");
    return msg.toString();
  }

  
  private static boolean cd(Class clazz) {
    
    try {         
      
      
      JarURLConnection jarCon = (JarURLConnection)getClassURL(clazz).openConnection();

      
      URL jarUrl = jarCon.getJarFileURL();
  
      
      File jarFile = new File(URLDecoder.decode(jarUrl.getPath(), "UTF-8"));   
      
      
      File jarDir = jarFile.getParentFile();

      
      LOG.info(System.getProperty("user.dir"));
      System.setProperty("user.dir", jarDir.getAbsolutePath());
      LOG.info(System.getProperty("user.dir"));

      
      return true;
      
    } catch (Exception ex) {
      
      LOG.log(Level.WARNING, "couldn't cd into directory with jar containing "+clazz, ex);
      return false;
    }

  }
  
  
  private static URL getClassURL(Class clazz) {
    String resourceName = "/" + clazz.getName().replace('.', '/') + ".class";
    return clazz.getResource(resourceName);
  }
  
  
  private static int getLaunchPort() {
  try {
      return Integer.parseInt(System.getProperty("launch.port"));
    } catch (Throwable t) {
    }
    try {
      return Integer.parseInt(getManifest().getMainAttributes().getValue(LAUNCH_PORT));
    } catch (Throwable t) {
      return 0;
    }
  }  
  
  
 
  private static String getLaunchClass() {

    String clazz = System.getProperty("launch.class");
    if (clazz==null)
      clazz = getManifest().getMainAttributes().getValue(LAUNCH_CLASS);
    if (clazz == null || clazz.length() == 0) 
      throw new Error("No " + LAUNCH_CLASS + " defined in " + MANIFEST);
    
    return clazz;
  }  
  
  
  private static ClassLoader getClassLoader(String[] classpath) throws MalformedURLException {
    
    URL[] urls = new URL[classpath.length];
    for (int i = 0; i < urls.length; i++) {
      urls[i] = new File(classpath[i]).toURI().toURL();
    }
    
    return new URLClassLoader(urls);
  }
  
  
  private static void setClasspath(String[] classpath) {
    
    String separator = System.getProperty("path.separator");
    
    StringBuffer value = new StringBuffer();
    for (int i = 0; i < classpath.length; i++) {
      if (i>0) value.append(separator);
      value.append(classpath[i]);
    }
    
    System.setProperty("java.class.path", value.toString());
  }
  
  
  private static String[] getLaunchClasspath() throws MalformedURLException {

    String classpath = expandSystemProperties(getManifest().getMainAttributes().getValue(LAUNCH_CLASSPATH));
    List result = new ArrayList();
    
    
    StringTokenizer tokens = new StringTokenizer(classpath, ",", false);
    while (tokens.hasMoreTokens()) {
      String token = tokens.nextToken().trim();
      File file = new File(token).getAbsoluteFile();
      if (!file.exists()) 
        continue;
      buildClasspath(file, result);
      
    }

    
    return (String[])result.toArray(new String[result.size()]);
  }
  
  private static void buildClasspath(File file, List result) throws MalformedURLException {
    
    
    if (!file.isDirectory() && file.getName().endsWith(".jar")) {
      result.add(file.getAbsolutePath());
      return;
    }
    
    
    File[] files = file.listFiles();
    if (files!=null) for (int i=0;i<files.length;i++) 
      buildClasspath(files[i], result);

    
  }
  
  
  private static Manifest getManifest() {
    
    
    if (manifest!=null)
      return manifest;
    
    try {

      
      Stack manifests = new Stack();
      for (Enumeration e = Launcher.class.getClassLoader().getResources(MANIFEST); e.hasMoreElements(); )
        manifests.add(e.nextElement());
      
      
      while (!manifests.isEmpty()) {
        URL url = (URL)manifests.pop();
        InputStream in = url.openStream();
        Manifest mf = new Manifest(in);
        in.close();
        
        if (mf.getMainAttributes().getValue(LAUNCH_CLASS)!=null) {
          manifest = mf;
          return manifest;
        }
      }
      
    } catch (Throwable t) {
      LOG.log(Level.SEVERE, "error while loading manifest", t);
    }
      
    
    LOG.warning("no manifest found");
    manifest = new Manifest();
    return manifest;
  }

  
  private static final Pattern PATTERN_KEY = Pattern.compile("\\$\\{[\\.\\w]*\\}");
  private static String expandSystemProperties(String string) {
    
    if (string==null)
      return "";

    StringBuffer result = new StringBuffer();
    Matcher m = PATTERN_KEY.matcher(string);

    int pos = 0;
    while (m.find()) {
      String prefix = string.substring(pos, m.start());
      String key = string.substring(m.start()+2, m.end()-1);
      
      result.append(prefix);
      result.append(System.getProperty(key));
      
      pos = m.end();
    }
    result.append(string.substring(pos));

    return result.toString();
  }

} 
