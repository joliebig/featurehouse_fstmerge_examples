

package edu.rice.cs.util;

import java.io.*;
import java.util.*;

import edu.rice.cs.drjava.config.FileOption;


public abstract class FileOps {
  
  
  public static final File NONEXISTENT_FILE = new File("") {
    public String getAbsolutePath() { return ""; }
    public String getName() { return ""; }
    public String toString() { return ""; }
    public boolean exists() { return false; }
  };
  
  
  public static boolean inFileTree(File f, File root) {
    if (root == null || f == null) return false;
    try {
      if (! f.isDirectory()) f = f.getParentFile();
      String filePath = f.getCanonicalPath() + File.separator;
      String projectPath = root.getCanonicalPath() + File.separator;
      return (filePath.startsWith(projectPath));
    }
    catch(IOException e) { return false; }
  }
  
  
  
  public static File makeRelativeTo(File f, File b) throws IOException, SecurityException {
    File base = b.getCanonicalFile();
    File abs  = f.getCanonicalFile();  
    if (! base.isDirectory()) base = base.getParentFile();
    
    String last = "";
    if (! abs.isDirectory()) {
      String tmp = abs.getPath();
      last = tmp.substring(tmp.lastIndexOf(File.separator) + 1);
      abs = abs.getParentFile();
    }
    

    String[] basParts = splitFile(base);
    String[] absParts = splitFile(abs);
    
    StringBuffer result = new StringBuffer();
    
    
    int diffIndex = -1;
    boolean different = false;
    for (int i = 0; i < basParts.length; i++) {
      if (!different && ((i >= absParts.length) || !basParts[i].equals(absParts[i]))) {
        different = true;
        diffIndex = i;
      }
      if (different) result.append("..").append(File.separator);
    }
    if (diffIndex < 0) diffIndex = basParts.length;
    for (int i = diffIndex; i < absParts.length; i++) { 
      result.append(absParts[i]).append(File.separator);
    }
    result.append(last);

    return new File(result.toString());
  }
  
  
  public static String[] splitFile(File fileToSplit) {
    String path = fileToSplit.getPath();
    ArrayList<String> list = new ArrayList<String>();
    while (! path.equals("")) {
      int idx = path.indexOf(File.separator);
      if (idx < 0) {
        list.add(path);
        path = "";
      }
      else {
        list.add(path.substring(0,idx));
        path = path.substring(idx+1);
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
  
  public static ArrayList<File> getFilesInDir(File d, boolean recur, FileFilter f){
    ArrayList<File> l = new ArrayList<File>();
    getFilesInDir(d, l, recur, f);
    return l;
  }
  
  
  private static void getFilesInDir(File d, List<File> acc, boolean recur, FileFilter filter) {
    if (d.isDirectory()) {
      File[] files = d.listFiles(filter);
      if (files!=null) { 
        for (File f: files) {
          if (f.isDirectory() && recur) getFilesInDir(f, acc, recur, filter);
          else if (f.isFile()) acc.add(f);
        }
      }
    }      
  }
  
  
  public static File getCanonicalFile(File f) {
    if (f == null) return f;
    try { return f.getCanonicalFile(); }
    catch (IOException e) {  }
    catch (SecurityException e) {  }
    return f.getAbsoluteFile();
  }
  
  
  public static String getCanonicalPath(File f) { return getCanonicalFile(f).getPath(); }
    
  
  public static File validate(File f) {
    if (f.exists()) return f;
    return FileOption.NULL_FILE;  
  }
  
  
  public static final FileFilter JAVA_FILE_FILTER = new FileFilter() {
    public boolean accept(File f){
      
      
      
      StringBuffer name = new StringBuffer(f.getAbsolutePath());
      String shortName = f.getName();
      if (shortName.length() < 6) return false;
      name.delete(name.length() - 5, name.length());
      name.append(".java");
      File test = new File(new String(name));
      return (test.equals(f));
    }
    public String getDescription() { return "Java Source Files (*.java)"; }
  };
  
  
  public static byte[] readStreamAsBytes(final InputStream stream) throws IOException {
    BufferedInputStream buffered;

    if (stream instanceof BufferedInputStream) buffered = (BufferedInputStream) stream;
    else  buffered = new BufferedInputStream(stream);

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    int readVal = buffered.read();
    while (readVal != -1) {
      out.write(readVal);
      readVal = buffered.read();
    }

    stream.close();
    return out.toByteArray();
  }

  
  public static String readFileAsString(final File file) throws IOException {
    FileReader reader = new FileReader(file);
    StringBuffer buf = new StringBuffer();

    while (reader.ready()) {
      char c = (char) reader.read();
      buf.append(c);
    }

    reader.close();
    return buf.toString();
  }
  
  
  public static void copyFile(File source, File dest) throws IOException {
    String text = readFileAsString(source);
    writeStringToFile(dest, text);
  }

  
  public static File writeStringToNewTempFile(final String prefix, final String suffix, final String text)
    throws IOException {
    
    File file = File.createTempFile(prefix, suffix);
    file.deleteOnExit();
    writeStringToFile(file, text);
    return file;
  }

  
  public static void writeStringToFile(File file, String text) throws IOException {
    writeStringToFile(file, text, false);
  }
  
  
  public static void writeStringToFile(File file, String text, boolean append) throws IOException {
    FileWriter writer = new FileWriter(file, append);
    writer.write(text);
    writer.close();
  }
  
  
  public static boolean writeIfPossible(File file, String text, boolean append) {
    try {
      writeStringToFile(file, text, append);
      return true;
    }
    catch(IOException e) { return false; }
  }
  
  
  public static File createTempDirectory(final String name) throws IOException {
    return createTempDirectory(name, null);
  }

  
  public static File createTempDirectory(final String name, final File parent) throws IOException {
    File file =  File.createTempFile(name, "", parent);
    file.delete();
    file.mkdir();
    file.deleteOnExit();

    return file;
  }

  
  public static boolean deleteDirectory(final File dir) {

    if (! dir.isDirectory()) { 
      boolean res;
      res = dir.delete();

      return res;
    }

    boolean ret = true;
    File[] childFiles = dir.listFiles();
    if (childFiles!=null) { 
      for (File f: childFiles) { ret = ret && deleteDirectory(f); }
    }
    
    
    ret = ret && dir.delete();

    return ret;
  }
  
  
  public static void deleteDirectoryOnExit(final File dir) {
    
    dir.deleteOnExit();

    
    
    
    if (dir.isDirectory()) {
      File[] childFiles = dir.listFiles();
      if (childFiles!=null) { 
        for (File f: childFiles) { deleteDirectoryOnExit(f); }
      }
    }
  }

  
  public static LinkedList<String> packageExplore(String prefix, File root) {
    
    class PrefixAndFile {
      public String prefix;
      public File root;
      public PrefixAndFile(String prefix, File root) {
        this.root = root;
        this.prefix = prefix;
      }
    }
    
    
    
    final Set<File> exploredDirectories = new HashSet<File>();

    LinkedList<String> output = new LinkedList<String>();
    Stack<PrefixAndFile> working = new Stack<PrefixAndFile>();
    working.push(new PrefixAndFile(prefix, root));
    exploredDirectories.add(root);

    
    FileFilter directoryFilter = new FileFilter(){
      public boolean accept(File f){
        boolean toReturn = f.isDirectory() && !exploredDirectories.contains(f);
        exploredDirectories.add(f);
        return toReturn;
      }
      public String getDescription() { return "All Folders"; }
    };

    
    
    while (! working.empty()) {
      PrefixAndFile current = working.pop();
      File [] subDirectories = current.root.listFiles(directoryFilter);
      if (subDirectories!=null) { 
        for (File dir: subDirectories) {
          PrefixAndFile paf;

          if (current.prefix.equals("")) paf = new PrefixAndFile(dir.getName(), dir);
          else  paf = new PrefixAndFile(current.prefix + "." + dir.getName(), dir);
          working.push(paf);
        }
      }
      File [] javaFiles = current.root.listFiles(JAVA_FILE_FILTER);

      if (javaFiles!=null) { 
        
        if (javaFiles.length != 0 && !current.prefix.equals("")) {
          output.add(current.prefix);

        }
      }
    }
    return output;
  }

  
  public static boolean renameFile(File file, File dest) {
    if (dest.exists()) dest.delete();
    return file.renameTo(dest);
  }

  
  public static void saveFile(FileSaver fileSaver) throws IOException {
    


    boolean makeBackup = fileSaver.shouldBackup();
    boolean success = false;
    File file = fileSaver.getTargetFile();
    File backup = null;
    boolean tempFileUsed = true;
    
    
    
    if (file.exists() && !file.canWrite()) throw new IOException("Permission denied");
    
    if (makeBackup) {
      backup = fileSaver.getBackupFile();
      if (!renameFile(file, backup)){
        throw new IOException("Save failed. Could not create backup file "
                                + backup.getAbsolutePath() +
                              "\nIt may be possible to save by disabling file backups\n");
      }
      fileSaver.backupDone();
    }
    


    
    
    
    
    File parent = file.getParentFile();
    File tempFile = File.createTempFile("drjava", ".temp", parent);
    


    
    try {
      
      FileOutputStream fos;
      try {
        
        fos = new FileOutputStream(tempFile);
      } 
      catch (FileNotFoundException fnfe) {
        if (fileSaver.continueWhenTempFileCreationFails()) {
          fos = new FileOutputStream(file);
          tempFileUsed = false;
        } 
        else throw new IOException("Could not create temp file " + tempFile + " in attempt to save " + file);
      }
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      fileSaver.saveTo(bos);
      bos.close();
      fos.close();

      if (tempFileUsed && !renameFile(tempFile, file))
        throw new IOException("Save failed. Another process may be using " + file + ".");

      success = true;
    } 
    finally {


    
      if (tempFileUsed) tempFile.delete(); 
        
      if (makeBackup) {
        
        if (success) fileSaver.backupDone();
        else  renameFile(backup, file);
      }
    }
  }

  public interface FileSaver {
    
    
    public abstract File getBackupFile() throws IOException;
    
    
    public abstract boolean shouldBackup() throws IOException;

    
    public abstract boolean continueWhenTempFileCreationFails();
    
    
    public abstract void backupDone();

    
    public abstract void saveTo(OutputStream os) throws IOException;

    
    public abstract File getTargetFile() throws IOException;
  }

  
  public abstract static class DefaultFileSaver implements FileSaver {

    private File outputFile = null;
    private static Set<File> filesNotNeedingBackup = new HashSet<File>();
    private static boolean backupsEnabled = true;

    
    private boolean isCanonical = false;
    
    
    public static void setBackupsEnabled(boolean isEnabled) { backupsEnabled = isEnabled; }
    
    public DefaultFileSaver(File file){ outputFile = file.getAbsoluteFile(); }
    
    public boolean continueWhenTempFileCreationFails(){ return true; }
    
    public File getBackupFile() throws IOException{ return new File(getTargetFile().getPath() + "~"); }

    public boolean shouldBackup() throws IOException{
      if (!backupsEnabled) return false;
      if (!getTargetFile().exists()) return false;
      if (filesNotNeedingBackup.contains(getTargetFile())) return false;
      return true;
    }
    
    public void backupDone() {
      try { filesNotNeedingBackup.add(getTargetFile()); } 
      catch (IOException ioe) { throw new UnexpectedException(ioe, "getTargetFile should fail earlier"); }
    }

    public File getTargetFile() throws IOException{
      if (!isCanonical) {
        outputFile = outputFile.getCanonicalFile();
        isCanonical = true;
      }
      return outputFile;
    }
  }
  
  
  public static String convertToAbsolutePathEntries(String path) {
    String pathSep = System.getProperty("path.separator");
    
    
    
    
    path += pathSep + "x";
    
    
    
    
    
    
    String[] pathEntries = path.split(pathSep);
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<pathEntries.length-1; ++i) { 
      File f = new File(pathEntries[i]);
      sb.append(f.getAbsolutePath());
      sb.append(pathSep);
    }
    String reconstructedPath = sb.toString();
    
    
    
    if (reconstructedPath.length()!=0) {
      reconstructedPath = reconstructedPath.substring(0, reconstructedPath.length()-1);
    }
    
    return reconstructedPath;
  }
}
