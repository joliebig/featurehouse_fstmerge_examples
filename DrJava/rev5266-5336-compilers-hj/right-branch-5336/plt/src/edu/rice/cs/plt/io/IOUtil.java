

package edu.rice.cs.plt.io;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.rice.cs.plt.debug.ThreadSnapshot;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.ReadOnlyIterator;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.tuple.*;
import edu.rice.cs.plt.recur.RecursionStack;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.text.TextUtil;

import static edu.rice.cs.plt.debug.DebugUtil.error;


public final class IOUtil {
  
  
  private IOUtil() {}
  
  
  public static final File WORKING_DIRECTORY = IOUtil.attemptAbsoluteFile(new File(System.getProperty("user.dir", "")));
  
  
  public static final Lambda<String, File> FILE_FACTORY = new FileFactory();
  
  private static class FileFactory implements Lambda<String, File>, Serializable {
    private FileFactory() {}
    public File value(String name) { return new File(name); }
  };
  
  
  public static File attemptAbsoluteFile(File f) {
    try { return f.getAbsoluteFile(); }
    catch (SecurityException e) { return f; }
  }
  
  
  public static SizedIterable<File> getAbsoluteFiles(Iterable<? extends File> files) {
    return IterUtil.mapSnapshot(files, GET_ABSOLUTE_FILE);
  }
  
  private static final Lambda<File, File> GET_ABSOLUTE_FILE = new Lambda<File, File>() {
    public File value(File arg) { return arg.getAbsoluteFile(); }
  };
  
  
  public static SizedIterable<File> attemptAbsoluteFiles(Iterable<? extends File> files) {
    return IterUtil.mapSnapshot(files, ATTEMPT_ABSOLUTE_FILE);
  }
  
  private static final Lambda<File, File> ATTEMPT_ABSOLUTE_FILE = new Lambda<File, File>() {
    public File value(File arg) { return attemptAbsoluteFile(arg); }
  };
  
  
  public static File attemptCanonicalFile(File f) {
    try { return f.getCanonicalFile(); }
    catch (IOException e) { return attemptAbsoluteFile(f); }
    catch (SecurityException e) { return attemptAbsoluteFile(f); }
  }
  
  
  public static SizedIterable<File> getCanonicalFiles(Iterable<? extends File> files) throws IOException {
    try { return IterUtil.mapSnapshot(files, GET_CANONICAL_FILE); }
    catch (WrappedException e) { 
      if (e.getCause() instanceof IOException) { throw (IOException) e.getCause(); }
      else { throw e; }
    }
  }
  
  private static final Lambda<File, File> GET_CANONICAL_FILE = new Lambda<File, File>() {
    public File value(File arg) {
      try { return arg.getCanonicalFile(); }
      catch (IOException e) { throw new WrappedException(e); }
    }
  };
  
  
  public static SizedIterable<File> attemptCanonicalFiles(Iterable<? extends File> files) {
    return IterUtil.mapSnapshot(files, ATTEMPT_CANONICAL_FILE);
  }
  
  private static final Lambda<File, File> ATTEMPT_CANONICAL_FILE = new Lambda<File, File>() {
    public File value(File arg) { return attemptAbsoluteFile(arg); }
  };
    
  
  public static boolean attemptCanRead(File f) {
    try { return f.canRead(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptCanWrite(File f) {
    try { return f.canWrite(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptExists(File f) {
    try { return f.exists(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptIsDirectory(File f) {
    try { return f.isDirectory(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptIsFile(File f) {
    try { return f.isFile(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptIsHidden(File f) {
    try { return f.isHidden(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static long attemptLastModified(File f) {
    try { return f.lastModified(); }
    catch (SecurityException e) { return 0l; }
  }
  
  
  public static long attemptLength(File f) {
    try { return f.length(); }
    catch (SecurityException e) { return 0l; }
  }
  
  
  public static boolean attemptCreateNewFile(File f) {
    try { return f.createNewFile(); }
    catch (IOException e) { return false; }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptDelete(File f) {
    try { return f.delete(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static void attemptDeleteOnExit(File f) {
    try { f.deleteOnExit(); }
    catch (SecurityException e) {  }
  }
  
  
  public static File[] attemptListFiles(File f) {
    try { return f.listFiles(); }
    catch (SecurityException e) { return null; }
  }
  
  
  public static File[] attemptListFiles(File f, FileFilter filter) {
    try { return f.listFiles(filter); }
    catch (SecurityException e) { return null; }
  }
  
  
  public static File[] attemptListFiles(File f, Predicate<? super File> filter) {
    return attemptListFiles(f, (FileFilter) asFilePredicate(filter));
  }
  
  
  public static File[] attemptListFiles(File f, FilePredicate filter) {
    return attemptListFiles(f, (FileFilter) filter);
  }
  
  
  public static SizedIterable<File> attemptListFilesAsIterable(File f) {
    File[] result = attemptListFiles(f);
    if (result == null) { return IterUtil.empty(); }
    else { return IterUtil.asIterable(result); }
  }
  
  
  public static SizedIterable<File> attemptListFilesAsIterable(File f, FileFilter filter) {
    File[] result = attemptListFiles(f, filter);
    if (result == null) { return IterUtil.empty(); }
    else { return IterUtil.asIterable(result); }
  }
  
  
  public static SizedIterable<File> attemptListFilesAsIterable(File f, Predicate<? super File> filter) {
    return attemptListFilesAsIterable(f, (FileFilter) asFilePredicate(filter));
  }
  
  
  public static SizedIterable<File> attemptListFilesAsIterable(File f, FilePredicate filter) {
    return attemptListFilesAsIterable(f, (FileFilter) filter);
  }
  
  
  public static boolean attemptMkdir(File f) {
    try { return f.mkdir(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptMkdirs(File f) {
    try { return f.mkdirs(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptRenameTo(File f, File dest) {
    try { return f.renameTo(dest); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptMove(File f, File dest) {
    attemptDelete(dest);
    return attemptRenameTo(f, dest);
  }
  
  
  public static boolean attemptSetLastModified(File f, long time) {
    try { return f.setLastModified(time); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static boolean attemptSetReadOnly(File f) {
    try { return f.setReadOnly(); }
    catch (SecurityException e) { return false; }
  }
  
  
  public static File canonicalCase(File f) {
    File lowered = new File(f.getPath().toLowerCase());
    if (f.equals(lowered)) { return lowered; }
    else { return f; }
  }
  
  
  public static SizedIterable<File> canonicalCases(Iterable<? extends File> files) {
    return IterUtil.mapSnapshot(files, CANONICAL_CASE);
  }
  
  private static final Lambda<File, File> CANONICAL_CASE = new Lambda<File, File>() {
    public File value(File arg) { return canonicalCase(arg); }
  };
  
  
  public static boolean isMember(File f, File ancestor) {
    File parent = f;
    while (parent != null) {
      if (parent.equals(ancestor)) { return true; }
      parent = parent.getParentFile();
    }
    return false;
  }
  
  
  public static SizedIterable<File> fullPath(File f) {
    SizedIterable<File> result = IterUtil.singleton(f);
    File parent = f.getParentFile();
    while (parent != null) {
      result = IterUtil.compose(parent, result);
      parent = parent.getParentFile();
    }
    return result;
  }
  
  
  public static boolean deleteRecursively(File f) {
    return deleteRecursively(f, new RecursionStack<File>(Wrapper.<File>factory()));
  }
  
  
  private static boolean deleteRecursively(File f, final RecursionStack<File> stack) {
    if (f.isDirectory()) {
      try {
        final File canonicalF = f.getCanonicalFile();
        Runnable deleteMembers = new Runnable() {
          public void run() {
            for (File child : attemptListFilesAsIterable(canonicalF)) { deleteRecursively(child, stack); }
          }
        };
        stack.run(deleteMembers, canonicalF);
      }
      catch (IOException e) {  }
      catch (SecurityException e) {  }
    }
    return attemptDelete(f);
  }
  
  
  public static void deleteOnExitRecursively(File f) {
    deleteOnExitRecursively(f, new RecursionStack<File>(Wrapper.<File>factory()));
  }
  
  
  private static void deleteOnExitRecursively(File f, final RecursionStack<File> stack) {
    attemptDeleteOnExit(f);
    if (f.isDirectory()) {
      
      try {
        final File canonicalF = f.getCanonicalFile();
        Runnable markMembers = new Runnable() {
          public void run() {
            for (File child : attemptListFilesAsIterable(canonicalF)) { deleteOnExitRecursively(child, stack); }
          }
        };
        stack.run(markMembers, canonicalF);
      }
      catch (IOException e) {  }
      catch (SecurityException e) {  }
    }
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f) {
    return listFilesRecursively(f, ALWAYS_ACCEPT, ALWAYS_ACCEPT);
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, FileFilter filter) {
    return listFilesRecursively(f, filter, ALWAYS_ACCEPT);
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, Predicate<? super File> filter) {
    return listFilesRecursively(f, asFilePredicate(filter), ALWAYS_ACCEPT);
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, FilePredicate filter) {
    return listFilesRecursively(f, filter, ALWAYS_ACCEPT);
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, FileFilter filter, FileFilter recursionFilter) {
    return listFilesRecursively(f, filter, recursionFilter, new RecursionStack<File>(Wrapper.<File>factory()));
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, Predicate<? super File> filter, 
                                                         Predicate<? super File> recursionFilter) {
    return listFilesRecursively(f, asFilePredicate(filter), asFilePredicate(recursionFilter),
                                new RecursionStack<File>(Wrapper.<File>factory()));
  }
  
  
  public static SizedIterable<File> listFilesRecursively(File f, FilePredicate filter, FilePredicate recursionFilter) {
    return listFilesRecursively(f, filter, recursionFilter, new RecursionStack<File>(Wrapper.<File>factory()));
  }
  
  
  private static SizedIterable<File> listFilesRecursively(final File f, final FileFilter filter, 
                                                          final FileFilter recursionFilter, 
                                                          final RecursionStack<File> stack) {
    SizedIterable<File> result = (filter.accept(f)) ? IterUtil.singleton(f) : IterUtil.<File>empty();
    if (f.isDirectory() && recursionFilter.accept(f)) {
      Thunk<Iterable<File>> getMembers = new Thunk<Iterable<File>>() {
        public Iterable<File> value() {
          Iterable<File> dirFiles = IterUtil.empty();
          for (File child : attemptListFilesAsIterable(f)) {
            dirFiles = IterUtil.compose(dirFiles, listFilesRecursively(child, filter, recursionFilter, stack));
          }
          return dirFiles;
        }
      };
      try {
        result = IterUtil.compose(result, stack.apply(getMembers, IterUtil.<File>empty(), f.getCanonicalFile()));
      }
      catch (IOException e) {  }
      catch (SecurityException e) {  }
    }
    return result;
  }
  
  
  public static byte[] toByteArray(File file) throws IOException {
    FileInputStream input = new FileInputStream(file);
    try { return toByteArray(input); }
    finally { input.close(); }
  }
  
  
  public static StringBuffer toStringBuffer(File file) throws IOException {
    FileReader reader = new FileReader(file);
    try { return toStringBuffer(reader); }
    finally { reader.close(); }
  }
  
  
  public static String toString(File file) throws IOException {
    FileReader reader = new FileReader(file);
    try { return toString(reader); }
    finally { reader.close(); }
  }

    
  public static Iterator<String> readLines(File file) throws IOException {
    FileReader reader = new FileReader(file);
    return readLines(reader);
  }
  
  
  public static int adler32Hash(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    try { return adler32Hash(input); }
    finally { input.close(); }
  }
  
  
  public static int crc32Hash(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    try { return crc32Hash(input); }
    finally { input.close(); }
  }
  
  
  public static byte[] md5Hash(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    try { return md5Hash(input); }
    finally { input.close(); }
  }
  
  
  public static byte[] sha1Hash(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    try { return sha1Hash(input); }
    finally { input.close(); }
  }
  
  
  public static byte[] sha256Hash(File file) throws IOException {
    InputStream input = new FileInputStream(file);
    try { return sha256Hash(input); }
    finally { input.close(); }
  }
  
  
  public static void copyFile(File source, File dest) throws IOException {
    FileInputStream in = new FileInputStream(source);
    try {
      FileOutputStream out = new FileOutputStream(dest);
      try { copyInputStream(in, out); }
      finally { out.close(); }
    }
    finally { in.close(); }
  }

  
  public static void copyFile(File source, File dest, byte[] buffer) throws IOException {
    FileInputStream in = new FileInputStream(source);
    try {
      FileOutputStream out = new FileOutputStream(dest);
      try { copyInputStream(in, out, buffer); }
      finally { out.close(); }
    }
    finally { in.close(); }
  }

  
  public static void writeStringToFile(File file, String text) throws IOException {
    writeStringToFile(file, text, false);
  }
  
  
  public static boolean attemptWriteStringToFile(File file, String text) {
    try { writeStringToFile(file, text); return true; }
    catch (IOException e) { return false; }
    catch (SecurityException e) { return false; }
  }
  
  
  public static void writeStringToFile(File file, String text, boolean append) throws IOException {
    FileWriter writer = new FileWriter(file, append);
    try { writer.write(text); }
    finally { writer.close(); }
  }
  
  
  public static boolean attemptWriteStringToFile(File file, String text, boolean append) {
    try { writeStringToFile(file, text, append); return true; }
    catch (IOException e) { return false; }
    catch (SecurityException e) { return false; }
  }
  
  
  public static File createAndMarkTempFile(String prefix, String suffix) throws IOException {
    return createAndMarkTempFile(prefix, suffix, null);
  }
  
  
  public static File createAndMarkTempFile(String prefix, String suffix, File location) throws IOException {
    File result = File.createTempFile(prefix, suffix, location);
    attemptDeleteOnExit(result);
    return result;
  }
  
  
  public static File createAndMarkTempDirectory(String prefix, String suffix) throws IOException {
    return createAndMarkTempDirectory(prefix, suffix, null);
  }
  
  
  public static File createAndMarkTempDirectory(String prefix, String suffix, File location) throws IOException {
    File result = File.createTempFile(prefix, suffix, location);
    boolean success = result.delete();
    success = success && result.mkdir();
    if (!success) { throw new IOException("Attempt to create directory failed"); }
    attemptDeleteOnExit(result);
    return result;
  }
  
  
  public static SizedIterable<File> parsePath(String path) {
    String[] filenames = path.split(TextUtil.regexEscape(File.pathSeparator));
    return IterUtil.mapSnapshot(IterUtil.asIterable(filenames), FILE_FACTORY);
  }
      
  
  public static String pathToString(Iterable<? extends File> path) {
    return IterUtil.toString(path, "", File.pathSeparator, "");
  }

  
  
  
  public static int copyReader(Reader source, Writer dest) throws IOException {
    return WrappedDirectReader.makeDirect(source).readAll(dest);
  }
  
  
  public static int copyReader(Reader source, Writer dest, char[] buffer) throws IOException {
    return WrappedDirectReader.makeDirect(source).readAll(dest, buffer);
  }
  
  
  public static int writeFromReader(Reader source, Writer dest, int chars) throws IOException {
    return WrappedDirectReader.makeDirect(source).read(dest, chars);
  }
  
  
  public static int writeFromReader(Reader source, Writer dest, int chars, char[] buffer) throws IOException {
    return WrappedDirectReader.makeDirect(source).read(dest, chars, buffer);
  }
  
  
  public static int copyInputStream(InputStream source, OutputStream dest) throws IOException {
    return WrappedDirectInputStream.makeDirect(source).readAll(dest);
  }
  
  
  public static int copyInputStream(InputStream source, OutputStream dest, byte[] buffer) throws IOException {
    return WrappedDirectInputStream.makeDirect(source).readAll(dest, buffer);
  }
  
  
  public static int writeFromInputStream(InputStream source, OutputStream dest, int bytes) throws IOException {
    return WrappedDirectInputStream.makeDirect(source).read(dest, bytes);
  }
  
  
  public static int writeFromInputStream(InputStream source, OutputStream dest, int bytes,
                                         byte[] buffer) throws IOException {
    return WrappedDirectInputStream.makeDirect(source).read(dest, bytes, buffer);
  }
  
  
  protected static int doCopyReader(Reader r, Writer w, char[] buffer) throws IOException {
    if (buffer.length == 0) { throw new IllegalArgumentException(); }
    int charsRead = r.read(buffer);
    if (charsRead == -1) { return -1; }
    else {
      int totalCharsRead = 0;
      do {
        totalCharsRead += charsRead;
        if (totalCharsRead < 0) { totalCharsRead = Integer.MAX_VALUE; }
        w.write(buffer, 0, charsRead);
        charsRead = r.read(buffer);
      } while (charsRead != -1);
      return totalCharsRead;
    }
  }
  
  
  protected static int doCopyInputStream(InputStream in, OutputStream out, byte[] buffer) throws IOException {
    if (buffer.length == 0) { throw new IllegalArgumentException(); }
    int charsRead = in.read(buffer);
    if (charsRead == -1) { return -1; }
    else {
      int totalCharsRead = 0;
      do {
        totalCharsRead += charsRead;
        if (totalCharsRead < 0) { totalCharsRead = Integer.MAX_VALUE; }
        out.write(buffer, 0, charsRead);
        charsRead = in.read(buffer);
      } while (charsRead != -1);
      return totalCharsRead;
    }
  }
  
  
  protected static int doWriteFromReader(Reader r, Writer w, int chars, char[] buffer) throws IOException {
    if (buffer.length == 0 && chars > 0) { throw new IllegalArgumentException(); }
    int charsRead = r.read(buffer, 0, (chars < buffer.length) ? chars : buffer.length);
    if (charsRead == -1) { return -1; }
    else {
      int totalCharsRead = 0;
      while (chars > 0 && charsRead > 0) {
        totalCharsRead += charsRead;
        chars -= charsRead;
        w.write(buffer, 0, charsRead);
        if (chars > 0) { charsRead = r.read(buffer, 0, (chars < buffer.length) ? chars : buffer.length); }
      }
      return totalCharsRead;
    }
  }
  
  
  protected static int doWriteFromInputStream(InputStream in, OutputStream out, int bytes, 
                                              byte[] buffer) throws IOException {
    if (buffer.length == 0 && bytes > 0) { throw new IllegalArgumentException(); }
    int bytesRead = in.read(buffer, 0, (bytes < buffer.length) ? bytes : buffer.length);
    if (bytesRead == -1) { return -1; }
    else {
      int totalBytesRead = 0;
      while (bytes > 0 && bytesRead > 0) {
        totalBytesRead += bytesRead;
        bytes -= bytesRead;
        out.write(buffer, 0, bytesRead);
        if (bytes > 0) { bytesRead = in.read(buffer, 0, (bytes < buffer.length) ? bytes : buffer.length); }
      }
      return totalBytesRead;
    }
  }
  
  
  public static byte[] toByteArray(InputStream stream) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      copyInputStream(stream, out);
      return out.toByteArray();
    }
    finally { out.close(); }
  }

  
  public static StringBuffer toStringBuffer(Reader r) throws IOException {
    StringWriter out = new StringWriter();
    try {
      copyReader(r, out);
      return out.getBuffer();
    }
    finally { out.close(); }
  }

  
  public static String toString(Reader r) throws IOException {
    return toStringBuffer(r).toString();
  }
  
  
  public static Iterator<String> readLines(Reader r) throws IOException {
    final BufferedReader br = asBuffered(r);
    final String firstLine = br.readLine();
    if (firstLine == null) { br.close(); }
    return new ReadOnlyIterator<String>() {
      String lookahead = firstLine;
      public boolean hasNext() { return lookahead != null; }
      public String next() {
        if (lookahead == null) { throw new NoSuchElementException(); }
        try {
          String result = lookahead;
          lookahead = br.readLine();
          if (lookahead == null) { br.close(); }
          return result;
        }
        catch (IOException e) { throw new WrappedException(e); }
      }
    };
  }
  
  
  public static int adler32Hash(InputStream stream) throws IOException {
    ChecksumOutputStream out = ChecksumOutputStream.makeAdler32();
    try {
      copyInputStream(stream, out);
      return (int) out.getValue();
    }
    finally { out.close(); }
  }
  
  
  public static int crc32Hash(InputStream stream) throws IOException {
    ChecksumOutputStream out = ChecksumOutputStream.makeCRC32();
    try {
      copyInputStream(stream, out);
      return (int) out.getValue();
    }
    finally { out.close(); }
  }
  
  
  public static byte[] md5Hash(InputStream stream) throws IOException {
    MessageDigestOutputStream out = MessageDigestOutputStream.makeMD5();
    try {
      copyInputStream(stream, out);
      return out.digest();
    }
    finally { out.close(); }
  }
  
  
  public static byte[] sha1Hash(InputStream stream) throws IOException {
    MessageDigestOutputStream out = MessageDigestOutputStream.makeSHA1();
    try {
      copyInputStream(stream, out);
      return out.digest();
    }
    finally { out.close(); }
  }
  
  
  public static byte[] sha256Hash(InputStream stream) throws IOException {
    MessageDigestOutputStream out = MessageDigestOutputStream.makeSHA256();
    try {
      copyInputStream(stream, out);
      return out.digest();
    }
    finally { out.close(); }
  }
  
  
  public static BufferedReader asBuffered(Reader r) {
    if (r instanceof BufferedReader) { return (BufferedReader) r; }
    else { return new BufferedReader(r); }
  }
  
  
  public static BufferedWriter asBuffered(Writer w) {
    if (w instanceof BufferedWriter) { return (BufferedWriter) w; }
    else { return new BufferedWriter(w); }
  }
  
  
  public static BufferedInputStream asBuffered(InputStream in) {
    if (in instanceof BufferedInputStream) { return (BufferedInputStream) in; }
    else { return new BufferedInputStream(in); }
  }
  
  
  public static BufferedOutputStream asBuffered(OutputStream out) {
    if (out instanceof BufferedOutputStream) { return (BufferedOutputStream) out; }
    else { return new BufferedOutputStream(out); }
  }
  
  
  private static final Thunk<List<Closeable>> TO_CLOSE = LazyThunk.make(new Thunk<List<Closeable>>() {
    public List<Closeable> value() {
      
      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          for (Closeable c : TO_CLOSE.value()) { attemptClose(c); }
        }
      });
      return new LinkedList<Closeable>();
    }
  });
  
  
  public static void closeOnExit(Closeable c) {
    TO_CLOSE.value().add(c);
  }
  
  
  public static void attemptClose(Closeable c) {
    try { c.close(); }
    catch (IOException e) {  }
  }
  
  
  public static FilePredicate asFilePredicate(Predicate<? super File> p) {
    return new PredicateFilePredicate(p);
  }
  
  private static final class PredicateFilePredicate implements FilePredicate, Serializable {
    private final Predicate<? super File> _p;
    public PredicateFilePredicate(Predicate<? super File> p) { _p = p; }
    public boolean accept(File f) { return _p.contains(f); }
    public boolean contains(File f) { return _p.contains(f); }
  }
  
  
  public static FilePredicate asFilePredicate(FileFilter filter) {
    return new FileFilterFilePredicate(filter);
  }
  
  private static final class FileFilterFilePredicate implements FilePredicate, Serializable {
    private final FileFilter _filter;
    public FileFilterFilePredicate(FileFilter filter) { _filter = filter; }
    public boolean accept(File f) { return _filter.accept(f); }
    public boolean contains(File f) { return _filter.accept(f); }
  }
  
  
  public static FilePredicate regexFilePredicate(String regex) {
    return new RegexFilePredicate(regex);
  }
  
  
  public static FilePredicate regexFilePredicate(Pattern regex) {
    return new RegexFilePredicate(regex);
  }
  
  private static final class RegexFilePredicate implements FilePredicate, Serializable {
    private final Pattern _regex;
    public RegexFilePredicate(String regex) { _regex = Pattern.compile(regex); }
    public RegexFilePredicate(Pattern regex) { _regex = regex; }
    public boolean accept(File f) { return _regex.matcher(f.getName()).matches(); }
    public boolean contains(File f) { return _regex.matcher(f.getName()).matches(); }
  }
  
  
  public static FilePredicate regexCanonicalCaseFilePredicate(String regex) {
    return new RegexCanonicalCaseFilePredicate(regex);
  }
  
  private static final class RegexCanonicalCaseFilePredicate implements FilePredicate, Serializable {
    private final Pattern _regex;
    public RegexCanonicalCaseFilePredicate(String regex) { _regex = Pattern.compile(regex); }
    public RegexCanonicalCaseFilePredicate(Pattern regex) { _regex = regex; }
    public boolean accept(File f) { return _regex.matcher(canonicalCase(f).getName()).matches(); }
    public boolean contains(File f) { return _regex.matcher(canonicalCase(f).getName()).matches(); }
  }
  
  
  public static FilePredicate extensionFilePredicate(String extension) {
    return new RegexCanonicalCaseFilePredicate(".*\\." + canonicalCase(new File(extension)).getName());
  }
  
  
  public static FilePredicate sameNameFilePredicate(String name) {
    return new SamePathFilePredicate(new File(name));
  }
  
  
  public static FilePredicate samePathFilePredicate(File path) {
    return new SamePathFilePredicate(path);
  }
  
  private static final class SamePathFilePredicate implements FilePredicate, Serializable {
    private final File _f;
    public SamePathFilePredicate(File f) { _f = canonicalCase(f); }
    public boolean accept(File f) {
      File candidate = canonicalCase(attemptAbsoluteFile(f));
      for (File compareTo = _f; compareTo != null; compareTo = compareTo.getParentFile()) {
        if (candidate == null || !compareTo.getName().equals(candidate.getName())) {
          return false;
        }
        candidate = candidate.getParentFile();
      }
      return true;
    }
    public boolean contains(File f) { return accept(f); }
  }
  
  
  public static FilePredicate sameAttributesFilePredicate(File f) throws FileNotFoundException {
    return new SameAttributesFilePredicate(f);
  }
  
  private static final class SameAttributesFilePredicate implements FilePredicate, Serializable {
    private final long _lastModified;
    private final long _length;
    private final boolean _canRead;
    private final boolean _canWrite;
    
    public SameAttributesFilePredicate(File f) throws FileNotFoundException {
      try {
        if (!f.isFile()) { throw new FileNotFoundException(f + " is not a valid file"); }
        _lastModified = f.lastModified();
        if (_lastModified == 0l) {
          throw new FileNotFoundException("Can't get valid modification date for " + f);
        }
        _length = f.length();
        _canRead = f.canRead();
        _canWrite = f.canWrite();
      }
      catch (SecurityException e) { throw new FileNotFoundException(e.getMessage()); }
    }
    
    public boolean accept(File f) {
      try {
        return f.isFile() && f.lastModified() == _lastModified && f.length() == _length &&
               f.canRead() == _canRead && f.canWrite() == _canWrite;
      }
      catch (SecurityException e) { return false; }
    }
    
    public boolean contains(File f) { return accept(f); }
  }
  
  
  public static FilePredicate sameContentsFilePredicate(File f) throws IOException {
    return new SameContentsFilePredicate(f);
  }
  
  private static final class SameContentsFilePredicate implements FilePredicate, Serializable {
    private final long _length;
    private final int _hash;
    public SameContentsFilePredicate(File f) throws IOException {
      
      
      _length = attemptLength(f);
      _hash = crc32Hash(f);
    }
    public boolean accept(File f) {
      long fLength = attemptLength(f);
      if (fLength > 0l && _length > 0l && fLength != _length) { return false; }
      try { return _hash == crc32Hash(f); }
      catch (IOException e) { return false; }
    }
    public boolean contains(File f) { return accept(f); }
  }
  
  
  public static final FilePredicate IS_FILE = new IsFileFilePredicate();
  
  private static final class IsFileFilePredicate implements FilePredicate, Serializable {
    public boolean accept(File f) { return attemptIsFile(f); }
    public boolean contains(File f) { return attemptIsFile(f); }
  }
  
  
  public static final FilePredicate IS_DIRECTORY = new IsDirectoryFilePredicate();
  
  private static final class IsDirectoryFilePredicate implements FilePredicate, Serializable {
    public boolean accept(File f) { return attemptIsDirectory(f); }
    public boolean contains(File f) { return attemptIsDirectory(f); }
  }
  
  
  public static final FilePredicate ALWAYS_ACCEPT = asFilePredicate(LambdaUtil.TRUE);

  
  public static final FilePredicate ALWAYS_REJECT = asFilePredicate(LambdaUtil.FALSE);

  
  public static FilePredicate and(FileFilter... filters) {
    return new AndFilePredicate(IterUtil.asIterable(filters));
  }
  
  
  public static FilePredicate and(Iterable<? extends FileFilter> filters) {
    return new AndFilePredicate(filters);
  }
  
  private static final class AndFilePredicate implements FilePredicate, Serializable {
    private final Iterable<? extends FileFilter> _filters;
    public AndFilePredicate(Iterable<? extends FileFilter> filters) { _filters = filters; }
    public boolean accept(File f) {
      for (FileFilter filter : _filters) {
        if (!filter.accept(f)) { return false; }
      }
      return true;
    }
    public boolean contains(File f) { return accept(f); }
  }
  
  
  public static FilePredicate or(FileFilter... filters) {
    return new OrFilePredicate(IterUtil.asIterable(filters));
  }
  
  
  public static FilePredicate or(Iterable<? extends FileFilter> filters) {
    return new OrFilePredicate(filters);
  }
  
  private static final class OrFilePredicate implements FilePredicate, Serializable {
    private final Iterable<? extends FileFilter> _filters;
    public OrFilePredicate(Iterable<? extends FileFilter> filters) { _filters = filters; }
    public boolean accept(File f) {
      for (FileFilter filter : _filters) {
        if (filter.accept(f)) { return true; }
      }
      return false;
    }
    public boolean contains(File f) { return accept(f); }
  }
  
  
  public static FilePredicate negate(FileFilter filter) {
    return new NegationFilePredicate(filter);
  }
  
  private static final class NegationFilePredicate implements FilePredicate, Serializable {
    private final FileFilter _filter;
    public NegationFilePredicate(FileFilter filter) { _filter = filter; }
    public boolean accept(File f) { return !_filter.accept(f); }
    public boolean contains(File f) { return !_filter.accept(f); }
  }
  
  
  public static FilePredicate fileKey(File f) throws IOException {
    return and(samePathFilePredicate(attemptAbsoluteFile(f)),
               sameAttributesFilePredicate(f),
               sameContentsFilePredicate(f));
  }

  
  private static final LinkedList<PrintStream> SYSTEM_OUT_STACK = new LinkedList<PrintStream>();
  private static final LinkedList<PrintStream> SYSTEM_ERR_STACK = new LinkedList<PrintStream>();
  private static final LinkedList<InputStream> SYSTEM_IN_STACK = new LinkedList<InputStream>();
  
  
  public static void replaceSystemOut(OutputStream substitute) {
    SYSTEM_OUT_STACK.addLast(System.out);
    if (substitute instanceof PrintStream) { System.setOut((PrintStream) substitute); }
    else { System.setOut(new PrintStream(substitute)); }
  }
  
  
  public static void ignoreSystemOut() {
    replaceSystemOut(VoidOutputStream.INSTANCE);
  }
  
  
  public static void revertSystemOut() {
    if (SYSTEM_OUT_STACK.isEmpty()) { error.logStack("Unbalanced call to revertSystemOut"); }
    else { System.setOut(SYSTEM_OUT_STACK.removeLast()); }
  }
    
  
  public static void replaceSystemErr(OutputStream substitute) {
    SYSTEM_ERR_STACK.addLast(System.err);
    if (substitute instanceof PrintStream) { System.setErr((PrintStream) substitute); }
    else { System.setErr(new PrintStream(substitute)); }
  }
    
  
  public static void ignoreSystemErr() {
    replaceSystemErr(VoidOutputStream.INSTANCE);
  }
  
  
  public static void revertSystemErr() {
    if (SYSTEM_ERR_STACK.isEmpty()) { error.logStack("Unbalanced call to revertSystemErr"); }
    else { System.setErr(SYSTEM_ERR_STACK.removeLast()); }
  }
  
  
  public static void replaceSystemIn(InputStream substitute) {
    SYSTEM_IN_STACK.addLast(System.in);
    System.setIn(substitute);
  }
  
  
  public static void revertSystemIn() {
    if (SYSTEM_IN_STACK.isEmpty()) { error.logStack("Unbalanced call to revertSystemIn"); }
    else { System.setIn(SYSTEM_IN_STACK.removeLast()); }
  }
  
  
  
  private static final Set<Class<?>> SERIALIZABLE_CLASSES = new HashSet<Class<?>>();
  static {
    
    SERIALIZABLE_CLASSES.add(String.class);
    SERIALIZABLE_CLASSES.add(Boolean.class);
    SERIALIZABLE_CLASSES.add(Character.class);
    SERIALIZABLE_CLASSES.add(Byte.class);
    SERIALIZABLE_CLASSES.add(Short.class);
    SERIALIZABLE_CLASSES.add(Integer.class);
    SERIALIZABLE_CLASSES.add(Long.class);
    SERIALIZABLE_CLASSES.add(Float.class);
    SERIALIZABLE_CLASSES.add(Double.class);
    SERIALIZABLE_CLASSES.add(Date.class);
    SERIALIZABLE_CLASSES.add(File.class);
    SERIALIZABLE_CLASSES.add(StackTraceElement.class);
    
    
    SERIALIZABLE_CLASSES.add(ThreadSnapshot.class);
    SERIALIZABLE_CLASSES.add(Null.class);
    
    
    SERIALIZABLE_CLASSES.add(boolean[].class);
    SERIALIZABLE_CLASSES.add(char[].class);
    SERIALIZABLE_CLASSES.add(byte[].class);
    SERIALIZABLE_CLASSES.add(short[].class);
    SERIALIZABLE_CLASSES.add(int[].class);
    SERIALIZABLE_CLASSES.add(long[].class);
    SERIALIZABLE_CLASSES.add(float[].class);
    SERIALIZABLE_CLASSES.add(double[].class);
  }
  
  
  
  public static Object ensureSerializable(Object obj) {
    if (obj == null) { return null; }
    else if (SERIALIZABLE_CLASSES.contains(obj.getClass())) { return obj; }
    else if (obj instanceof Object[]) { return ensureSerializable((Object[]) obj); }
    else if (obj instanceof Iterable<?>) { return ensureSerializable((Iterable<?>) obj); }
    else if (obj instanceof Throwable) { return ensureSerializable((Throwable) obj); }
    else if (obj instanceof Tuple) { return ensureSerializable((Tuple) obj); }
    else { return obj.toString(); }
  }
  
  
  public static Object[] ensureSerializable(Object[] arr) {
    Class<?> base = ReflectUtil.arrayBaseClass(arr.getClass());
    if (SERIALIZABLE_CLASSES.contains(base) && Modifier.isFinal(base.getModifiers())) {
      
      
      return arr;
    }
    else {
      boolean keep = true;
      Object[] result = new Object[arr.length];
      for (int i = 0; i < arr.length; i++) {
        result[i] = ensureSerializable(arr[i]);
        keep &= (result[i] == arr[i]);
      }
      return keep ? arr : result;
    }
  }
  
  
  public static Iterable<?> ensureSerializable(Iterable<?> iter) {
    if (IterUtil.isInfinite(iter)) { iter = IterUtil.compose(IterUtil.truncate(iter, 8), "..."); }
    
    
    boolean keep = iter.getClass().equals(ArrayList.class);
    List<Object> result = new ArrayList<Object>();
    for (Object elt : iter) {
      Object safe = ensureSerializable(elt);
      keep &= (elt == safe);
      result.add(safe);
    }
    return keep ? iter : result;
  }
  
  
  public static Throwable ensureSerializable(Throwable t) {
    Throwable safeCause = (t.getCause() == null) ? null : ensureSerializable(t.getCause());
    if (t.getCause() == safeCause && isSafeThrowableClass(t.getClass())) { return t; }
    else { return new SerializableException(t, safeCause); }
  }
    
  
  public static Exception ensureSerializable(Exception e) {
    Throwable safeCause = (e.getCause() == null) ? null : ensureSerializable(e.getCause());
    if (e.getCause() == safeCause && isSafeThrowableClass(e.getClass())) { return e; }
    else { return new SerializableException(e, safeCause); }
  }
    
  
  public static RuntimeException ensureSerializable(RuntimeException e) {
    Throwable safeCause = (e.getCause() == null) ? null : ensureSerializable(e.getCause());
    if (e.getCause() == safeCause && isSafeThrowableClass(e.getClass())) { return e; }
    else { return new SerializableException(e, safeCause); }
  }
  
  
  private static boolean isSafeThrowableClass(Class<?> c) {
    try {
      if (!c.getMethod("getCause").getDeclaringClass().equals(Throwable.class)) {
        
        
        
        
        
        
        return false;
      }
    }
    catch (NoSuchMethodException e) { return false; }
    catch (SecurityException e) { return false; }
    
    Class<?> parent = c;
    while (!parent.equals(Throwable.class) && parent != null) {
      for (Field f : parent.getDeclaredFields()) {
        Class<?> fType = f.getType();
        if (!fType.isPrimitive() && !SERIALIZABLE_CLASSES.contains(f.getType())) { return false; }
      }
      parent = parent.getSuperclass();
    }
    return true;
  }
  
  
  public static Tuple ensureSerializable(Tuple t) {
    if (t instanceof Null) { return t; } 
    else if (t instanceof Wrapper<?>) { return ensureSerializable((Wrapper<?>) t); }
    else if (t instanceof Pair<?,?>) { return ensureSerializable((Pair<?,?>) t); }
    else if (t instanceof Triple<?,?,?>) { return ensureSerializable((Triple<?,?,?>) t); }
    else if (t instanceof Quad<?,?,?,?>) { return ensureSerializable((Quad<?,?,?,?>) t); }
    else if (t instanceof Quint<?,?,?,?,?>) { return ensureSerializable((Quint<?,?,?,?,?>) t); }
    else if (t instanceof Sextet<?,?,?,?,?,?>) { return ensureSerializable((Sextet<?,?,?,?,?,?>) t); }
    else if (t instanceof Septet<?,?,?,?,?,?,?>) { return ensureSerializable((Septet<?,?,?,?,?,?,?>) t); }
    else if (t instanceof Octet<?,?,?,?,?,?,?,?>) { return ensureSerializable((Octet<?,?,?,?,?,?,?,?>) t); }
    else { throw new IllegalArgumentException("Unrecognized tuple type: " + t.getClass().getName()); }
  }
  
  
  public static Option<?> ensureSerializable(Option<?> opt) {
    if (opt instanceof Null) { return opt; } 
    else if (opt instanceof Wrapper<?>) { return ensureSerializable((Wrapper<?>) opt); }
    else { throw new IllegalArgumentException("Unrecognized option type: " + opt.getClass().getName()); }
  }
  
  
  public static Wrapper<?> ensureSerializable(Wrapper<?> w) {
    Object safeVal = ensureSerializable(w.value());
    if (w.getClass().equals(Wrapper.class) && w.value() == safeVal) { return w; }
    else { return Wrapper.make(safeVal); }
  }
  
  
  public static Pair<?,?> ensureSerializable(Pair<?,?> p) {
    Object safeFirst = ensureSerializable(p.first());
    Object safeSecond = ensureSerializable(p.second());
    if (p.getClass().equals(Pair.class) && p.first() == safeFirst && p.second() == safeSecond) { return p; }
    else { return Pair.make(safeFirst, safeSecond); }
  }
  
  
  public static Triple<?,?,?> ensureSerializable(Triple<?,?,?> t) {
    Object safeFirst = ensureSerializable(t.first());
    Object safeSecond = ensureSerializable(t.second());
    Object safeThird = ensureSerializable(t.third());
    if (t.getClass().equals(Triple.class) &&
        t.first() == safeFirst &&
        t.second() == safeSecond &&
        t.third() == safeThird) { 
      return t;
    }
    else { return Triple.make(safeFirst, safeSecond, safeThird); }
  }
  
  
  public static Quad<?,?,?,?> ensureSerializable(Quad<?,?,?,?> q) {
    Object safeFirst = ensureSerializable(q.first());
    Object safeSecond = ensureSerializable(q.second());
    Object safeThird = ensureSerializable(q.third());
    Object safeFourth = ensureSerializable(q.fourth());
    if (q.getClass().equals(Quad.class) &&
        q.first() == safeFirst &&
        q.second() == safeSecond &&
        q.third() == safeThird && 
        q.fourth() == safeFourth) { 
      return q;
    }
    else { return Quad.make(safeFirst, safeSecond, safeThird, safeFourth); }
  }
  
  
  public static Quint<?,?,?,?,?> ensureSerializable(Quint<?,?,?,?,?> q) {
    Object safeFirst = ensureSerializable(q.first());
    Object safeSecond = ensureSerializable(q.second());
    Object safeThird = ensureSerializable(q.third());
    Object safeFourth = ensureSerializable(q.fourth());
    Object safeFifth = ensureSerializable(q.fifth());
    if (q.getClass().equals(Quint.class) &&
        q.first() == safeFirst &&
        q.second() == safeSecond &&
        q.third() == safeThird && 
        q.fourth() == safeFourth && 
        q.fifth() == safeFifth) { 
      return q;
    }
    else { return Quint.make(safeFirst, safeSecond, safeThird, safeFourth, safeFifth); }
  }
  
  
  public static Sextet<?,?,?,?,?,?> ensureSerializable(Sextet<?,?,?,?,?,?> s) {
    Object safeFirst = ensureSerializable(s.first());
    Object safeSecond = ensureSerializable(s.second());
    Object safeThird = ensureSerializable(s.third());
    Object safeFourth = ensureSerializable(s.fourth());
    Object safeFifth = ensureSerializable(s.fifth());
    Object safeSixth = ensureSerializable(s.sixth());
    if (s.getClass().equals(Quint.class) &&
        s.first() == safeFirst &&
        s.second() == safeSecond &&
        s.third() == safeThird && 
        s.fourth() == safeFourth && 
        s.fifth() == safeFifth &&
        s.sixth() == safeSixth) { 
      return s;
    }
    else { return Sextet.make(safeFirst, safeSecond, safeThird, safeFourth, safeFifth, safeSixth); }
  }
  
  
  public static Septet<?,?,?,?,?,?,?> ensureSerializable(Septet<?,?,?,?,?,?,?> s) {
    Object safeFirst = ensureSerializable(s.first());
    Object safeSecond = ensureSerializable(s.second());
    Object safeThird = ensureSerializable(s.third());
    Object safeFourth = ensureSerializable(s.fourth());
    Object safeFifth = ensureSerializable(s.fifth());
    Object safeSixth = ensureSerializable(s.sixth());
    Object safeSeventh = ensureSerializable(s.seventh());
    if (s.getClass().equals(Quint.class) &&
        s.first() == safeFirst &&
        s.second() == safeSecond &&
        s.third() == safeThird && 
        s.fourth() == safeFourth && 
        s.fifth() == safeFifth &&
        s.sixth() == safeSixth &&
        s.seventh() == safeSeventh) { 
      return s;
    }
    else { return Septet.make(safeFirst, safeSecond, safeThird, safeFourth, safeFifth, safeSixth, safeSeventh); }
  }
  
  
  public static Octet<?,?,?,?,?,?,?,?> ensureSerializable(Octet<?,?,?,?,?,?,?,?> o) {
    Object safeFirst = ensureSerializable(o.first());
    Object safeSecond = ensureSerializable(o.second());
    Object safeThird = ensureSerializable(o.third());
    Object safeFourth = ensureSerializable(o.fourth());
    Object safeFifth = ensureSerializable(o.fifth());
    Object safeSixth = ensureSerializable(o.sixth());
    Object safeSeventh = ensureSerializable(o.seventh());
    Object safeEighth = ensureSerializable(o.eighth());
    if (o.getClass().equals(Quint.class) &&
        o.first() == safeFirst &&
        o.second() == safeSecond &&
        o.third() == safeThird && 
        o.fourth() == safeFourth && 
        o.fifth() == safeFifth &&
        o.sixth() == safeSixth &&
        o.seventh() == safeSeventh &&
        o.eighth() == safeEighth) { 
      return o;
    }
    else {
      return Octet.make(safeFirst, safeSecond, safeThird, safeFourth, safeFifth, safeSixth, safeSeventh, safeEighth);
    }
  }
  
}
