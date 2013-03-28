

package edu.rice.cs.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.Log;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.text.TextUtil;

import static edu.rice.cs.drjava.config.OptionConstants.*;


public abstract class FileOps {
  
  private static Log _log = new Log("FileOpsTest.txt", false);
  
  
  public static class NullFile extends File {
    
    public static final NullFile ONLY = new NullFile();
    
    private NullFile() { super(""); }
    public boolean canRead() { return false; }
    public boolean canWrite() { return false; }
    public int compareTo(File f) { return (f == this) ? 0 : -1; }
    public boolean createNewFile() { return false; }
    public boolean delete() { return false; }
    public void deleteOnExit() { }
    public boolean equals(Object o) { return o == this; }
    public boolean exists() { return false; }
    public int hashCode() { return getClass().hashCode(); }
    public File getAbsoluteFile() { return this; }
    public String getAbsolutePath() { return ""; }
    public File getCanonicalFile() { return this; }
    public String getCanonicalPath() { return ""; }
    public String getName() { return ""; }
    public String getParent() { return null; }
    public File getParentFile() { return null; }
    public String getPath() { return ""; }
    public boolean isAbsolute() { return false; }
    public boolean isDirectory() { return false; }
    public boolean isFile() { return false; }
    public boolean isHidden() { return false; }
    public long lastModified() { return 0L; }
    public long length() { return 0L; }
    public String[] list() { return null; }
    public String[] list(FilenameFilter filter) { return null; }
    public File[] listFiles() { return null; }
    public File[] listFiles(FileFilter filter) { return null; }
    public File[] listFiles(FilenameFilter filter) { return null; }
    public boolean mkdir() { return false; }
    public boolean mkdirs() { return false; }
    public boolean renameTo(File dest) { return false; }
    public boolean setLastModified(long time) { return false; }
    public boolean setReadOnly() { return false; }
    public String toString() { return ""; }
    
    
  };
  
  
  public static final File NULL_FILE = NullFile.ONLY;
  
  
  @Deprecated public static File makeFile(String path) { 
    File f = new File(path);
    try { return f.getCanonicalFile(); }
    catch(IOException e) { return f; }
  }
  
  
  @Deprecated public static File makeFile(File parentDir, String child) { 
    File f = new File(parentDir, child);
    try { return f.getCanonicalFile(); }
    catch(IOException e) { return f; }
  }
  
  
  @Deprecated public static boolean inFileTree(File f, File root) {
    if (root == null || f == null) return false;
    try {
      if (! f.isDirectory()) f = f.getParentFile();
      String filePath = f.getCanonicalPath() + File.separator;
      String projectPath = root.getCanonicalPath() + File.separator;
      return (filePath.startsWith(projectPath));
    }
    catch(IOException e) { return false; }
  }
  
  
  public static boolean isAncestorOf(File ancestor, File f) {
    ancestor = ancestor.getAbsoluteFile();
    f = f.getAbsoluteFile();
    _log.log("ancestor = " +ancestor + "     f = " + f);
    while ((!ancestor.equals(f)) && (f != null)) {
      f = f.getParentFile();
    }
    return (ancestor.equals(f));
  }

  
  public static File makeRelativeTo(File f, File b) throws IOException, SecurityException {
    return new File(b, stringMakeRelativeTo(f,b));
  }
    
  
  public static String stringMakeRelativeTo(File f, File b) throws IOException  {
    try {
      
      File[] roots = File.listRoots();
      File fRoot = null;
      File bRoot = null;
      for(File r: roots) {
        if (isAncestorOf(r, f)) { fRoot = r; }
        if (isAncestorOf(r, b)) { bRoot = r; }
        if ((fRoot != null) && (bRoot != null)) { break; }
      }
      
      
      if (((fRoot == null) || (!fRoot.equals(bRoot))) && (!f.getAbsoluteFile().getCanonicalFile().toString().startsWith(File.separator + File.separator))) {
        
        return f.getAbsoluteFile().getCanonicalFile().toString();
      }
    }
    catch(Exception e) {  }
    
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
    
    final StringBuilder result = new StringBuilder();
    
    
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

    return result.toString();
  }
  
  
  @Deprecated public static String[] splitFile(File fileToSplit) {
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
        path = path.substring(idx + 1);
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
  
  @Deprecated public static ArrayList<File> getFilesInDir(File d, boolean recur, FileFilter f) {
    ArrayList<File> l = new ArrayList<File>();
    getFilesInDir(d, l, recur, f);
    return l;
  }
  
  
  private static void getFilesInDir(File d, List<File> acc, boolean recur, FileFilter filter) {
    if (d.isDirectory()) {
      File[] files = d.listFiles(filter);
      if (files != null) { 
        for (File f: files) {
          if (f.isDirectory() && recur) getFilesInDir(f, acc, recur, filter);
          else if (f.isFile()) acc.add(f);
        }
      }
    }      
  }
  
  
  @Deprecated public static File getCanonicalFile(File f) {
    if (f == null) return f;
    try { return f.getCanonicalFile(); }
    catch (IOException e) {  }
    catch (SecurityException e) {  }
    return f.getAbsoluteFile();
  }
  
  
  @Deprecated public static String getCanonicalPath(File f) { return getCanonicalFile(f).getPath(); }
  
  
  public static File validate(File f) {
    if (f.exists()) return f;
    return FileOps.NULL_FILE;  
  }
  
  
  @Deprecated public static final FileFilter JAVA_FILE_FILTER = new FileFilter() {
    public boolean accept(File f){
      
      
      
      final StringBuilder name = new StringBuilder(f.getAbsolutePath());
      String shortName = f.getName();
      if (shortName.length() < 6) return false;
      name.delete(name.length() - 5, name.length());
      name.append(".java");
      File test = new File(name.toString());
      return (test.equals(f));
    }
    

  };
  
  
  @Deprecated public static byte[] readStreamAsBytes(final InputStream stream) throws IOException {
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
  
  
  public static String readFileAsSwingText(final File file) throws IOException {
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      final StringBuilder buf = new StringBuilder();
      
      char pred = (char) 0; 
      while (reader.ready()) {
        char c = (char) reader.read();
        
        if (c == '\n' && pred == '\r') { } 
        else if (c == '\r') buf.append('\n');
        else if ((c < 32) && (c != '\n')) buf.append(' ');
        else buf.append(c);
        
        pred = c;
      }
      return buf.toString();
    }
    finally { if (reader != null) reader.close(); }
  }
  
  
  @Deprecated public static String readFileAsString(final File file) throws IOException {
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      final StringBuilder buf = new StringBuilder();
      
      while (reader.ready()) {
        char c = (char) reader.read();
        buf.append(c);
      }
      return buf.toString();
    }
    finally { if (reader != null) reader.close(); }
  }
  
  
  @Deprecated public static void copyFile(File source, File dest) throws IOException {
    String text = readFileAsString(source);
    writeStringToFile(dest, text);
  }
  
  
  @Deprecated public static File writeStringToNewTempFile(final String prefix, final String suffix, final String text)
  throws IOException {
    
    File file = File.createTempFile(prefix, suffix);
    file.deleteOnExit();
    writeStringToFile(file, text);
    return file;
  }
  
  
  @Deprecated public static void writeStringToFile(File file, String text) throws IOException {
    writeStringToFile(file, text, false);
  }
  
  
  @Deprecated public static void writeStringToFile(File file, String text, boolean append) throws IOException {
    FileWriter writer = null;
    try {
      writer = new FileWriter(file, append);
      writer.write(text);
    }
    finally { if (writer != null) writer.close(); }
  }
  
  
  @Deprecated public static boolean writeIfPossible(File file, String text, boolean append) {
    try {
      writeStringToFile(file, text, append);
      return true;
    }
    catch(IOException e) { return false; }
  }
  
  


  
   public static File createTempDirectory(final String name) throws IOException {
    return createTempDirectory(name, null);
  }
  
  


  
   public static File createTempDirectory( String name,  File parent) throws IOException {
    File result = File.createTempFile(name, "", parent);
    boolean success = result.delete();
    success = success && result.mkdir();
    if (! success) { throw new IOException("Attempt to create directory failed"); }
    IOUtil.attemptDeleteOnExit(result);
    return result;






  }
  
  
  @Deprecated public static boolean deleteDirectory(final File dir) {

    if (! dir.isDirectory()) { 
      boolean res;
      res = dir.delete();

      return res;
    }
    
    boolean ret = true;
    File[] childFiles = dir.listFiles();
    if (childFiles != null) { 
      for (File f: childFiles) { ret = ret && deleteDirectory(f); }
    }
    
    
    ret = ret && dir.delete();

    return ret;
  }
  
  
  @Deprecated public static void deleteDirectoryOnExit(final File dir) {
    
    
    _log.log("Deleting file/directory " + dir + " on exit");
    dir.deleteOnExit(); 
    
    
    
    if (dir.isDirectory()) {
      File[] childFiles = dir.listFiles();
      if (childFiles != null) { 
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
        boolean toReturn = f.isDirectory() && ! exploredDirectories.contains(f);
        exploredDirectories.add(f);
        return toReturn;
      }
      

    };
    
    
    
    while (! working.empty()) {
      PrefixAndFile current = working.pop();
      File [] subDirectories = current.root.listFiles(directoryFilter);
      if (subDirectories != null) { 
        for (File dir: subDirectories) {
          PrefixAndFile paf;

          if (current.prefix.equals("")) paf = new PrefixAndFile(dir.getName(), dir);
          else  paf = new PrefixAndFile(current.prefix + "." + dir.getName(), dir);
          working.push(paf);
        }
      }
      File [] javaFiles = current.root.listFiles(JAVA_FILE_FILTER);
      
      if (javaFiles != null) { 
        
        if (javaFiles.length != 0 && !current.prefix.equals("")) {
          output.add(current.prefix);

        }
      }
    }
    return output;
  }
  
  
  @Deprecated public static boolean renameFile(File file, File dest) {
    if (dest.exists()) dest.delete();
    return file.renameTo(dest);
  }
  
  
  public static void saveFile(FileSaver fileSaver) throws IOException {
    
    
    boolean makeBackup = fileSaver.shouldBackup();
    boolean success = false;
    File file = fileSaver.getTargetFile();

    File backup = null;
    boolean tempFileUsed = true;
    
    if (file.exists() && ! file.canWrite()) throw new IOException("Permission denied");
    
    if (makeBackup) {
      backup = fileSaver.getBackupFile();
      if (! renameFile(file, backup)) {
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

      

      if (tempFileUsed && ! renameFile(tempFile, file))
        throw new IOException("Save failed. Another process may be using " + file + ".");


      success = true;
    } 
    finally {


      
      if (tempFileUsed) tempFile.delete(); 
      
      if (makeBackup) {
        
        if (success) fileSaver.backupDone();
        else {
          renameFile(backup, file);

        }
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
    
    private File outputFile = FileOps.NULL_FILE;
    private static Set<File> filesNotNeedingBackup = new HashSet<File>();
    private boolean backupsEnabled = DrJava.getConfig().getSetting(BACKUP_FILES);  
    
    
    private boolean isCanonical = false;
    


    
    public DefaultFileSaver(File file){ outputFile = file.getAbsoluteFile(); }
    
    public boolean continueWhenTempFileCreationFails(){ return true; }
    
    public File getBackupFile() throws IOException{ return new File(getTargetFile().getPath() + "~"); }
    
    public boolean shouldBackup() throws IOException{
      if (! backupsEnabled) return false;
      if (! getTargetFile().exists()) return false;
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
  
  
  @Deprecated public static String convertToAbsolutePathEntries(String path) {
    String pathSep = System.getProperty("path.separator");
    
    
    
    
    path += pathSep + "x";
    
    
    
    
    
    
    String[] pathEntries = path.split(pathSep);
    final StringBuilder sb = new StringBuilder();
    for(int i = 0; i < pathEntries.length - 1; ++i) { 
      File f = new File(pathEntries[i]);
      sb.append(f.getAbsolutePath());
      sb.append(pathSep);
    }
    String reconstructedPath = sb.toString();
    
    
    
    if (reconstructedPath.length() != 0) {
      reconstructedPath = reconstructedPath.substring(0, reconstructedPath.length() - 1);
    }
    
    return reconstructedPath;
  }
  
  
  public static File getValidDirectory(final File origFile) {
    File file = origFile;
    
    
    if ((file == FileOps.NULL_FILE) || (file == null)) {
      file = new File(System.getProperty("user.home"));
    }
    assert file != null;    
    
    while (file != null && ! file.exists()) {
      
      
      file = file.getParentFile();
    }
    if (file == null) {
      
      file = new File(System.getProperty("user.home"));
    }
    assert file != null;
    
    
    if (! file.isDirectory()) {
      if (file.getParent() != null) { 
        file = file.getParentFile();
        
        if (file == null) {
          
          file = new File(System.getProperty("user.home"));
        }
        assert file != null;
      }
    }
    
    
    if (file.exists() && file.isDirectory()) return file;
    
    
    
    throw new UnexpectedException(new IOException(origFile.getPath()
                                                    + " is not a valid directory, and all attempts "
                                                    + "to locate a valid directory have failed. "
                                                    + "Check your configuration."));
  }
  
  
  public static URL toURL(File f) throws MalformedURLException { return f.toURI().toURL(); }
  
  public static boolean makeWritable(File roFile) throws IOException {
    
    boolean shouldBackup = edu.rice.cs.drjava.DrJava.getConfig().
      getSetting(edu.rice.cs.drjava.config.OptionConstants.BACKUP_FILES);
    boolean madeBackup = false;
    File backup = new File(roFile.getAbsolutePath() + "~");
    try {
      boolean noBackup = true;
      if (backup.exists()) {
        try { noBackup = backup.delete(); }
        catch(SecurityException se) { noBackup = false; }
      }
      if (noBackup) {
        try {
          noBackup = roFile.renameTo(backup);
          madeBackup = true;
          roFile.createNewFile();
        }
        catch(SecurityException se) { noBackup = false; }
        catch(IOException ioe) { }
        try { roFile.createNewFile(); }
        catch(SecurityException se) { }
        catch(IOException ioe) { }
      }
      if (! noBackup) {
        try { roFile.delete(); }
        catch(SecurityException se) { return false; }
      }
      try { edu.rice.cs.plt.io.IOUtil.copyFile(backup, roFile);}
      catch(SecurityException se) { return false; }
      catch(IOException ioe) { return false; }
      return true;
    }
    finally {
      if (! shouldBackup && madeBackup) {
        try { backup.delete(); }
        catch(Exception e) {  }
      }
    }
  }
  
  
  public static boolean moveRecursively(File f, File n) {
    boolean res = true;
    try {
      if (!f.exists()) { return false; }
      if (f.isFile()) { return edu.rice.cs.plt.io.IOUtil.attemptMove(f,n); }
      else {
        
        
        if (!n.mkdir()) { return false; }
        
        for(String child: f.list()) {
          File oldChild = new File(f, child);
          File newChild = new File(n, child);
          res = res && moveRecursively(oldChild, newChild);
        }
        if (! f.delete()) { return false; }
      }
    }
    catch(Exception e) { return false; }
    return res;
  }

  
  public static File generateNewFileName(File base) throws IOException {
    return generateNewFileName(base.getParentFile(), base.getName());
  }

  
  public static File generateNewFileName(File dir, String name) throws IOException {
    return generateNewFileName(dir, name, "", 100);
  }

  
  public static File generateNewFileName(File dir, String prefix, String suffix) throws IOException {
    return generateNewFileName(dir, prefix, suffix, 100);
  }

  
  public static File generateNewFileName(File dir, String prefix, String suffix, int max) throws IOException {
    File temp = new File(dir, prefix+suffix);
    if (temp.exists()) {
      int count = 2;
      do {
        temp = new File(dir, prefix + "-" + count+suffix);
        ++count;
      } while(temp.exists() && (count<max));
      if (temp.exists()) { throw new IOException("Could not generate a file name that did not already exist."); }
    }
    return temp;
  }
  

  
  
  public static File getShortFile(File f) throws IOException {
    if (!edu.rice.cs.drjava.platform.PlatformFactory.ONLY.isWindowsPlatform()) { return f; }
    
    String s = "";
    File parent = f.getParentFile();
    
    
    File[] roots = f.listRoots();
    File root = new File(File.separator);
    for(File r: roots) {
      if (f.getCanonicalPath().startsWith(r.getAbsolutePath())) {
        root = r;
        break;
      }
    }
    
    
    while(parent != null) {

      try {
        
        Process p = new ProcessBuilder("cmd", "/C", "dir", "/X", "/A").directory(parent).redirectErrorStream(true).start();
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        boolean found = false;
        
        
        while((line = br.readLine()) != null) {
          if (!found) {

            
            
            
            
            
            
            
            
            
            
            
            
            if (line.trim().length() == 0) continue;
            
            
            if (line.startsWith(" ")) continue;
            
            
            int pos = line.indexOf("  ");
            if (pos == -1) continue;
            pos = line.indexOf("  ", pos+2);
            if (pos == -1) continue;
            line = line.substring(pos).trim();

            
            
            
            
            
            
            
            pos = line.indexOf(' ');
            if (pos == -1) continue;
            line = line.substring(pos).trim();

            
            File shortF = null;
            
            
            
            if (line.toLowerCase().equals(f.getName().toLowerCase())) {
              
              shortF = new File(parent, line);

              if (f.getCanonicalFile().equals(shortF.getCanonicalFile())) {
                

                found = true;
              }
            }
            else if (line.toLowerCase().startsWith(f.getName().toLowerCase()) && f.getName().contains("~")) {
              
              shortF = new File(parent, f.getName());

              if (f.getCanonicalFile().equals(shortF.getCanonicalFile())) {
                

                found = true;
              }
            }
            else if (line.toLowerCase().endsWith(" "+f.getName().toLowerCase())) {
              
              
              
              
              
              String shortLine = line.substring(0, line.length() - f.getName().length()).trim();

              
              if (line.length() == 0) {
                
                found = true;
                shortF = f;

              }
              else {
                shortF = new File(parent, shortLine);

                
                
                if (shortF.exists()) {
                  if (f.getCanonicalFile().equals(shortF.getCanonicalFile())) {
                    
                    
                    
                    found = true;
                  }
                }
              }
            }
            if (found && (shortF != null)) {
              

              s = shortF.getName()+((s.length()==0)?"":(File.separator+s));

            }
          }
        }

        try {
          
          p.waitFor();
        }
        catch(InterruptedException ie) {
          throw new IOException("Could not get short windows file name: "+ie);
        }
        if (!found) {
          throw new IOException("Could not get short windows file name: "+f.getAbsolutePath()+" not found");
        }
      }
      catch(IOException ioe) {
          throw new IOException("Could not get short windows file name: "+ioe);
      }
      f = parent;
      parent = parent.getParentFile();
    }
    
    File shortF = new File(root, s);
    if (!shortF.exists()) {
      throw new IOException("Could not get short windows file name: "+shortF.getAbsolutePath()+" not found");
    }
    return shortF;
  }

  
  public static File getDrJavaFile() {
    String[] cps = System.getProperty("java.class.path").split(TextUtil.regexEscape(File.pathSeparator),-1);
    File found = null;
    for(String cp: cps) {
      try {
        File f = new File(cp);
        if (!f.exists()) { continue; }
        if (f.isDirectory()) {
          
          File cf = new File(f, edu.rice.cs.drjava.DrJava.class.getName().replace('.', File.separatorChar) + ".class");
          if (cf.exists() && cf.isFile()) {
            found = f;
            break;
          }
        }
        else if (f.isFile()) {
          
          JarFile jf = new JarFile(f);
          
          
          
          if (jf.getJarEntry(edu.rice.cs.drjava.DrJava.class.getName().replace('.', '/') + ".class") != null) {
            found = f;
            break;
          }
        }
      }
      catch(IOException e) {  }
    }
    return found.getAbsoluteFile();
  }
  
  
  public static File getDrJavaApplicationFile() {
    File found = FileOps.getDrJavaFile();
    if (found != null) {
      if (edu.rice.cs.drjava.platform.PlatformFactory.ONLY.isMacPlatform()) {
        
        String s = found.getAbsolutePath();
        if (s.endsWith(".app/Contents/Resources/Java/drjava.jar")) {
          found = new File(s.substring(0, s.lastIndexOf("/Contents/Resources/Java/drjava.jar")));
        }
      }
    }
    return found.getAbsoluteFile();
  }
}
