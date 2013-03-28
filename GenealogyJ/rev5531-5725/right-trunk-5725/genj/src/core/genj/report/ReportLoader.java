
package genj.report;

import genj.util.EnvironmentChecker;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class ReportLoader {

  
  private List instances = new ArrayList(10);
  
  
  private Map file2reportclass = new HashMap(10);
  
  
  private List classpath = new ArrayList(10);
  
  
  private boolean isReportsInClasspath = false;
  
  
  private static ReportLoader singleton;
  
  
   static void clear() {
    singleton = null;
  }
  
  
  public static ReportLoader getInstance() {
    
    
    if (singleton==null) {
      synchronized (ReportLoader.class) {
        if (singleton==null) {
          singleton = new ReportLoader();
        }
      }
    }
      
    
    return singleton;
      
  }
  
  
  public static File getReportDirectory() {
    
    
    return new File(EnvironmentChecker.getProperty(ReportLoader.class,
      new String[]{ "genj.report.dir", "user.dir/report"},
      "report",
      "find report class-files"
    ));
  }
  
  
  private ReportLoader() {

    File base = getReportDirectory();
    ReportView.LOG.info("Reading reports from "+base);
      
    
    try {
      classpath.add(base.toURI().toURL());
    } catch (MalformedURLException e) {
      
    }
    parseDir(base, null);
    
    
    URLClassLoader cl = new URLClassLoader((URL[])classpath.toArray(new URL[classpath.size()]), getClass().getClassLoader());
    
    
    for (Iterator files = file2reportclass.keySet().iterator(); files.hasNext(); ) {
      File file = (File)files.next();
      String clazz = (String)file2reportclass.get(file); 
      try {
        Report r = (Report)cl.loadClass(clazz).newInstance();
        r.putFile(file);
        if (!isReportsInClasspath&&r.getClass().getClassLoader()!=cl) {
          ReportView.LOG.warning("Reports are in classpath and can't be reloaded");
          isReportsInClasspath = true;
        }
        instances.add(r);
      } catch (Throwable t) {
        ReportView.LOG.log(Level.WARNING, "Failed to instantiate "+clazz, t);
      }
    }
    
    
    Collections.sort(instances, new Comparator() { 
      public int compare(Object a, Object b) {
        
        try {
          return ((Report)a).getName().compareTo(((Report)b).getName());
        } catch (Throwable t) {
          return 0;
        }
      }
    });
    
    
  }
  
  
  private void parseDir(File dir, String pkg) { 

    
    if (!dir.isDirectory())
      return;

    
    String[] files = dir.list();
    for (int i=0;i<files.length;i++) {
      File file = new File(dir, files[i]);
      
      
      if (file.isDirectory()) {
        parseDir(file, (pkg==null?"":pkg+".")+file.getName());
        continue;
      }

      
      String report = isReport(file, pkg);
      if (report!=null) {
        file2reportclass.put(file, report);
        continue;
      } 
      
      
      if (isLibrary(file)) {
        try {
          ReportView.LOG.info("report library "+file.toURI().toURL());
          classpath.add(file.toURI().toURL());
        } catch (MalformedURLException e) {
          
        }
      }
      
      
    }
      
    
  }
  
  
  private boolean isLibrary(File file) {
    return 
      !file.isDirectory() &&
      (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"));
  }
  
  
  private String isReport(File file, String pkg) {
    if ( (pkg!=null&&pkg.startsWith("genj")) || 
         file.isDirectory() ||
         !file.getName().endsWith(".class") ||
         !file.getName().startsWith("Report") ||
         file.getName().indexOf("$")>0 )
      return null;
    String name = file.getName();
    return (pkg==null?"":pkg+".") + name.substring(0, name.length()-".class".length());
  }

  
  public Report[] getReports() {
    return (Report[])instances.toArray(new Report[instances.size()]);
  }

  
   void saveOptions() {
    Report[] rs = getReports();
    for (int r=0;r<rs.length;r++)
      rs[r].saveOptions();
  }
  
  
   boolean isReportsInClasspath() {
    return isReportsInClasspath;
  }

} 
