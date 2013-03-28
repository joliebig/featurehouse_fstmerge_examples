
package genj.io;

import genj.util.Resources;

import java.awt.Component;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;


public class FileAssociation {
  
  private static Logger LOG = Logger.getLogger("genj.io");
  
  
  private static List associations = new LinkedList();
  
  
  private Set suffixes = new HashSet();
  
  
  private String name = "";
  
  
  private String executable = "";
  
  
  public FileAssociation() {
  }
  
  
  public FileAssociation(String s) throws IllegalArgumentException {
    
    StringTokenizer tokens = new StringTokenizer(s,"*");
    if (tokens.countTokens()!=3)
      throw new IllegalArgumentException("need three *-separators");
    
    setSuffixes(tokens.nextToken());
    
    name = tokens.nextToken();
    executable = tokens.nextToken();
    
  }
  
  
  public FileAssociation(String suffixes, String name, String executable) throws IllegalArgumentException {
    setSuffixes(suffixes);
    this.name = name;
    this.executable = executable;
    
  }
  
  
  public String toString() {
    return getSuffixes()+"*"+name+"*"+executable;
  }
  
  
  public void setName(String set) {
    name = set;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public void setExecutable(String set) {
    executable = set;
  }
  
  
  public String getExecutable() {
    return executable;
  }
  
  
  public void setSuffixes(String set) {
    
    StringTokenizer ss = new StringTokenizer(set,",");
    if (ss.countTokens()==0)
      throw new IllegalArgumentException("need at least one suffix");
    suffixes.clear();
    while (ss.hasMoreTokens())
      suffixes.add(ss.nextToken().trim());
  }
  
  
  public String getSuffixes() {
    StringBuffer result = new StringBuffer();
    Iterator it = suffixes.iterator();
    while (it.hasNext()) {
      result.append(it.next());
      if (it.hasNext()) result.append(',');
    }
    return result.toString();
  }
  
  
  public void execute(URL url) {
    new Thread(new Sequence(url.toString())).start();
  }
  
  
  public void execute(File file) {
    
    new Thread(new Sequence(file.getAbsolutePath())).start();
  }
  
  private class Sequence implements Runnable {
    private String file;
    Sequence(String file) {
      this.file = file;
    }
    public void run() {
      runCommands();
    }
    
    private void runCommands() {
      
      StringTokenizer cmds =  new StringTokenizer(getExecutable(), "&");
      while (cmds.hasMoreTokens()) 
        runCommand(cmds.nextToken().trim());
    }      
    
    private void runCommand(String cmd) {
      
      
      if (cmd.indexOf('%')<0) {
        cmd = cmd + " " + (file.indexOf(' ')<0 ?  "%" : "\"%\"");
      }

      
      
      
      
      
      
      String suffix = getSuffix(file);
      String pathRegEx = file.replaceAll("\\\\","\\\\\\\\");
      String pathNoSuffixRegEx = pathRegEx.substring(0, pathRegEx.length()-suffix.length()-1);
      
      
      cmd = Pattern.compile("%(\\.[a-zA-Z]*)").matcher(cmd).replaceAll(pathNoSuffixRegEx+"$1");
      
      cmd = Pattern.compile("%").matcher(cmd).replaceAll(pathRegEx);
      
      
      String[] cmdarray = parse(cmd);
      
      
      LOG.info("Running command: "+Arrays.asList(cmdarray));
      
      try {
        int rc = Runtime.getRuntime().exec(cmdarray).waitFor(); 
        if (rc!=0) 
          LOG.log(Level.INFO, "External returned "+rc);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "External threw "+t.getMessage(), t);
      }
      
    }
    
  } 

  
  public static String[] parse(String cmd) {
    
    List tokens = new ArrayList();
    StringBuffer token = new StringBuffer(32);
    boolean quoted=false;
    for (int i=0;i<cmd.length();i++) {
      char c = cmd.charAt(i);
      switch (c) {
        case ' ': 
        case '\t':
          if (quoted) {
            token.append(c);
          } else {
            if (token.length()>0) tokens.add(token.toString());
            token.setLength(0);
          }
          break;
        case '\"':
          if (quoted) {
            tokens.add(token.toString());
            token.setLength(0);
            quoted = false;
          } else {
            if (token.length()>0) tokens.add(token.toString());
            token.setLength(0);
            quoted = true;
          }
          break;
        default:
          token.append(c);
      }
    }
    if (quoted) {
      LOG.warning("Umatched quotes in "+cmd);
    }
    if (token.length()>0) tokens.add(token.toString());
    
    
    return (String[])tokens.toArray(new String[tokens.size()]);
    
  }
  
  
  public static List<FileAssociation> getAll() {
    return new ArrayList<FileAssociation>(associations);
  }

    public static List<FileAssociation> getAll(String suffix) {
    List result = new ArrayList();
    Iterator it = associations.iterator();
    while (it.hasNext()) {
      FileAssociation fa = (FileAssociation)it.next();
      if (fa.suffixes.contains(suffix))
        result.add(fa);
    }
    return result;
  }
  
  
  public static String getSuffix(File file) {
    return getSuffix(file.getName());
  }
  
  public static String getSuffix(String file) {
    
    
    Matcher m = Pattern.compile(".*\\.(.*)$").matcher(file);
    
    
    return m.matches() ? m.group(1) : "";
  }
  
  
  public static FileAssociation get(File file, String name, Component owner) {
    if (file.isDirectory())
      return get("[dir]", "[dir]", "Directory", owner);
      
    String suffix = getSuffix(file);
    if (suffix.length()==0)
      return null;
    
    if (name==null)
      name = suffix;
    
    return get(suffix, suffix, name, owner);
  }
  
  
  public static void open(URL url, Component owner) {
    
    if ("file".equals(url.getProtocol())) {
      try {
        
        String decodedFileName;
        decodedFileName = URLDecoder.decode(url.getFile(),"UTF-8");
        File file = new File(decodedFileName); 
        FileAssociation fa = FileAssociation.get(file, "Open", owner);
        if (fa!=null)
          fa.execute(file);
      } catch (UnsupportedEncodingException e) { }
    } else {
      
      FileAssociation fa = FileAssociation.get("html", "html, htm, xml", "Browse", owner);
      if (fa!=null)  
        fa.execute(url);
    }
  }
  
  
  public static FileAssociation get(String suffix, String suffixes, String name, Component owner) {
    
    Iterator it = associations.iterator();
    while (it.hasNext()) {
      FileAssociation fa = (FileAssociation)it.next();
      if (fa.suffixes.contains(suffix))
        return fa;
    }
    
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(Resources.get(FileAssociation.class).getString("assocation.choose", suffixes));
    int rc = chooser.showOpenDialog(owner);
    File file = chooser.getSelectedFile(); 
    if (rc!=JFileChooser.APPROVE_OPTION||file==null||!file.exists())
      return null;
    
    String executable =  file.getAbsolutePath();
    if (executable.indexOf(' ')>=0) executable = "\"" +executable + "\"";
    
    FileAssociation association = new FileAssociation(suffixes, name, executable);
    add(association);
    
    return association;
  }
  
  
  public static boolean del(FileAssociation fa) {
    return associations.remove(fa);
  }

    public static FileAssociation add(FileAssociation fa) {
    if (!associations.contains(fa))
      associations.add(fa);
    return fa;
  }
  
} 
