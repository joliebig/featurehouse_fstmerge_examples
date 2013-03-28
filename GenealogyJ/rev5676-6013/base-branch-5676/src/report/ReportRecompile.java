
import genj.gedcom.Gedcom;
import genj.report.Report;
import genj.report.ReportLoader;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class ReportRecompile extends Report {

  private final static String[] OPTIONS = {
      "-g", 
      "-nowarn",
      "-encoding", "utf8",
  };
  
  
  public void start(Gedcom gedcom) {
    
    
    List args = new ArrayList();
    
    for (int i = 0; i < OPTIONS.length; i++) 
      args.add(OPTIONS[i]);

    File reports = ReportLoader.getReportDirectory();
    args.add("-d");
    args.add(reports.getAbsolutePath());
    
    
    int sources = findSources(reports, args);
    if (sources==0) {
      println(translate("nosources", reports));
      return;
    }
    
    
    Object rc = null;
    try {
      Object javac = Class.forName("com.sun.tools.javac.Main").newInstance();
      rc = javac.getClass().getMethod("compile", new Class[]{ new String[0].getClass(), PrintWriter.class } )
            .invoke(javac, new Object[]{ args.toArray(new String[args.size()]), out });
    } catch (Exception e) {
      println(translate("javac.jre", System.getProperty("java.home")));
      return;
    }
    
    
    if (new Integer(0).equals(rc))
      println(translate("javac.success", new Object[]{ ""+sources, reports}));
    else {
      println("---");
      println(translate("javac.error"));
    }
    
  }
  
  
  private int findSources(File dir, List args) {
    File[] files = dir.listFiles();
    if (files==null||files.length==0)
      return 0;
    int found = 0;
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.isDirectory()) {
        found += findSources(file, args);
      } else if (file.getName().endsWith(".java")) {
        args.add(file.toString());
        found++;
      }
    }
    return found;
  }
  
}


