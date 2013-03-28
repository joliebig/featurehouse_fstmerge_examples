

package edu.rice.cs.drjava.project;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.Pair;
import edu.rice.cs.util.FileOps;

import static edu.rice.cs.util.StringOps.convertToLiteral;

import edu.rice.cs.util.sexp.SEList;
import edu.rice.cs.util.sexp.SExpParseException;
import edu.rice.cs.util.sexp.SExpParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class ProjectTest extends DrJavaTestCase {
  
  File base;
  File parent;
  File buildDir;
  File srcDir;

  private String absp; 
  public void setUp() throws Exception {
    super.setUp();
    try { 
      base = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
      parent = FileOps.createTempDirectory("proj", base);
      buildDir = new File(parent, "built");
      buildDir.mkdir();  
      srcDir = new File(parent, "src");
      srcDir.mkdir(); 
      absp = parent.getCanonicalPath() + File.separator; 
      FileOps.deleteDirectoryOnExit(parent);
    }
    catch(IOException e) { fail("could not initialize temp path string"); }
  }

  
  public void testLegacyParseProject() throws IOException, MalformedProjectFileException, java.text.ParseException {
    String proj1 =
      ";; DrJava project file.  Written with build: 20040623-1933\n" +
      "(source ;; comment\n" +
      "   (file (name \"src/sexp/Atom.java\")(select 32 32)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"src/sexp/BoolAtom.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"src/sexp/Cons.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"src/sexp/Empty.java\")(select 24 28)(mod-date \"16-Jul-2004 03:45:23\")(active))\n" +
      "   (file (name \"src/sexp/Lexer.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"src/sexp/NumberAtom.java\")(select 12 12)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"src/sexp/SEList.java\")(select 0 0)))\n" + 
      "(auxiliary ;; absolute file names\n" +
      "   (file (name " + convertToLiteral(new File(parent, "junk/sexp/Tokens.java").getCanonicalPath()) + 
         ")(select 32 32)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name " + convertToLiteral(new File(parent, "jdk1.5.0/JScrollPane.java").getCanonicalPath()) + 
         ")(select 9086 8516)(mod-date \"16-Jul-2004 03:45:23\")))\n" +
      "(collapsed ;; relative paths\n" +
      "   (path \"./[ Source Files ]/sexp/\")\n" +
      "   (path \"./[ External ]/\"))\n" +
      "(build-dir ;; absolute path\n" +
      "   (file (name "+ convertToLiteral(new File(parent,"built").getCanonicalPath()) + ")))\n" +
      "(work-dir ;; absolute path\n" +
      "   (file (name "+ convertToLiteral(new File(parent,"src").getCanonicalPath()) + ")))\n" +
      "(proj-root ;; absolute path\n" +
      "   (file (name "+ convertToLiteral(new File(parent,"src").getCanonicalPath()) + ")))\n" +
      "(classpaths\n" +
      "   (file (name "+ convertToLiteral(new File(parent,"src/edu/rice/cs/lib").getCanonicalPath()) + ")))\n" +
      "(main-class\n" +
      "   (file (name \"src/sexp/SEList.java\")))";
    
    File f = new File(parent, "test1.pjt");

    FileOps.writeStringToFile(f, proj1);



    ProjectFileIR pfir = ProjectFileParser.ONLY.parse(f);

    assertEquals("number of source files", 7, pfir.getSourceFiles().length);
    assertEquals("number of aux files", 2, pfir.getAuxiliaryFiles().length);
    assertEquals("number of collapsed", 2, pfir.getCollapsedPaths().length);
    assertEquals("number of classpaths", 1, pfir.getClassPaths().length);
    File base = f.getParentFile();
    assertEquals("first source filename", new File(base,"src/sexp/Atom.java").getPath(), pfir.getSourceFiles()[0].getPath());
    assertEquals("mod-date value", 
                 new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("16-Jul-2004 03:45:23").getTime(),
                 pfir.getSourceFiles()[0].getSavedModDate());
    assertEquals("last source filename", new File(base,"src/sexp/SEList.java").getPath(), 
                 pfir.getSourceFiles()[6].getPath());
    assertEquals("first aux filename", new File(base,"junk/sexp/Tokens.java").getPath(), 
                 pfir.getAuxiliaryFiles()[0].getCanonicalPath());
    assertEquals("last collapsed path", "./[ External ]/", pfir.getCollapsedPaths()[1]);
    assertEquals("build-dir name", new File(base, "built").getCanonicalPath(), 
                 pfir.getBuildDirectory().getCanonicalPath());
    assertEquals("work-dir name", new File(base, "src").getCanonicalPath(), 
                 pfir.getWorkingDirectory().getCanonicalPath());
    assertEquals("classpath name", new File(base, "src/edu/rice/cs/lib").getCanonicalPath(), 
                 pfir.getClassPaths()[0].getCanonicalPath());
    assertEquals("main-class name", new File(base, "src/sexp/SEList.java").getCanonicalPath(), 
                 pfir.getMainClass().getCanonicalPath());
  }

   
  public void testParseProject() throws IOException, MalformedProjectFileException, java.text.ParseException {
    String proj1 =
      ";; DrJava project file.  Written with build: 2006??\n" +
      "(proj-root-and-base (file (name \"src\")))\n" +
      "(source-files ;; comment\n" +
      "   (file (name \"sexp/Atom.java\")(select 32 32)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"sexp/BoolAtom.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"sexp/Cons.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"sexp/Empty.java\")(select 24 28)(mod-date \"16-Jul-2004 03:45:23\")(active))\n" +
      "   (file (name \"sexp/Lexer.java\")(select 0 0)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"sexp/NumberAtom.java\")(select 12 12)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name \"sexp/SEList.java\")(select 0 0)))\n" + 
      "(auxiliary ;; absolute file names\n" +
      "   (file (name " + convertToLiteral(new File(parent,"junk/sexp/Tokens.java").getCanonicalPath()) +
          ")(select 32 32)(mod-date \"16-Jul-2004 03:45:23\"))\n" +
      "   (file (name " + convertToLiteral(new File(parent,"jdk1.5.0/JScrollPane.java").getCanonicalPath()) +
          ")(select 9086 8516)(mod-date \"16-Jul-2004 03:45:23\")))\n" +
      "(collapsed ;; relative paths\n" +
      "   (path \"./[ Source Files ]/sexp/\")\n" +
      "   (path \"./[ External ]/\"))\n" +
      "(build-dir ;; absolute path\n" +
      "   (file (name "+ convertToLiteral(new File(parent, "built").getCanonicalPath()) + ")))\n" +
      "(work-dir (file (name \"src\")))\n" +
      "(classpaths\n" +
      "   (file (name "+ convertToLiteral(new File(parent, "src/edu/rice/cs/lib").getCanonicalPath()) + ")))\n" +
      "(main-class\n" +
      "   (file (name \"src/sexp/SEList.java\")))";
    
    File f = new File(parent, "test1.pjt");

    FileOps.writeStringToFile(f, proj1);



    ProjectFileIR pfir = ProjectFileParser.ONLY.parse(f);

    assertEquals("number of source files", 7, pfir.getSourceFiles().length);
    assertEquals("number of aux files", 2, pfir.getAuxiliaryFiles().length);
    assertEquals("number of collapsed", 2, pfir.getCollapsedPaths().length);
    assertEquals("number of classpaths", 1, pfir.getClassPaths().length);
    File base = f.getParentFile();
    File root = new File(base, "src");
    assertEquals("proj-root-and-base", root.getPath(), pfir.getProjectRoot().getPath());
    assertEquals("first source filename", new File(base,"src/sexp/Atom.java").getPath(), pfir.getSourceFiles()[0].getPath());
    assertEquals("mod-date value", 
                 new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse("16-Jul-2004 03:45:23").getTime(),
                 pfir.getSourceFiles()[0].getSavedModDate());
    assertEquals("last source filename", new File(root, "sexp/SEList.java").getPath(), 
                 pfir.getSourceFiles()[6].getPath());
    assertEquals("first aux filename", new File(base,"junk/sexp/Tokens.java").getPath(), 
                 pfir.getAuxiliaryFiles()[0].getCanonicalPath());
    assertEquals("last collapsed path", "./[ External ]/", pfir.getCollapsedPaths()[1]);
    assertEquals("build-dir name", new File(base, "built").getCanonicalPath(), 
                 pfir.getBuildDirectory().getCanonicalPath());
    assertEquals("work-dir name", new File(base, "src").getCanonicalPath(), 
                 pfir.getWorkingDirectory().getCanonicalPath());
    assertEquals("classpath name", new File(base, "src/edu/rice/cs/lib").getCanonicalPath(), 
                 pfir.getClassPaths()[0].getCanonicalPath());
    assertEquals("main-class name", new File(root, "sexp/SEList.java").getCanonicalPath(), 
                 pfir.getMainClass().getCanonicalPath());
  }
  
  public void testParseFile() throws SExpParseException {
    SEList c = SExpParser.parse("(file (name \"file-name\") (select 1 2))").get(0);
    DocFile df = ProjectFileParser.ONLY.parseFile(c,null);
    Pair<Integer,Integer> p = df.getSelection();
    assertEquals("First int should be a 1", 1, (int)p.getFirst()); 
    assertEquals("Second int should be a 2", 2, (int)p.getSecond());
    assertEquals("Name should have been file-name", "file-name", df.getPath());
  }

  public void testWriteFile() throws IOException, MalformedProjectFileException {
    File pf = new File(parent, "test2.pjt");
    FileOps.writeStringToFile(pf, "");
    ProjectProfile fb = new ProjectProfile(pf);
    String sr = pf.getCanonicalFile().getParent();

    fb.addSourceFile(makeGetter(0, 0, 0, 0,  "dir1/testfile1.java", "dir1", false, false, pf));
    fb.addSourceFile(makeGetter(1, 1, 0, 0,  "dir1/testfile2.java", "dir1", false, false, pf));
    fb.addSourceFile(makeGetter(20, 22, 0, 0, "dir2/testfile3.java", "dir2", false, false, pf));
    fb.addSourceFile(makeGetter(1, 1, 0, 0,  "dir2/testfile4.java", "dir2", true, false, pf));
    fb.addSourceFile(makeGetter(0, 0, 0, 0,  "dir3/testfile5.java", "", false, false, pf));
    fb.addAuxiliaryFile(makeGetter(1, 1, 0, 0, absp + "test/testfile6.java", "/home/javaplt", false, false, null));
    fb.addAuxiliaryFile(makeGetter(1, 1, 0, 0, absp + "test/testfile7.java", "/home/javaplt", false, false, null));
    fb.addCollapsedPath("./[ Source Files ]/dir1/");
    fb.addClassPathFile(new File(parent, "lib"));
    fb.setBuildDirectory(new File(parent, "built"));
    fb.setWorkingDirectory(new File(parent, "src"));
    fb.setMainClass(new File(pf.getParentFile(), "dir1/testfile1.java"));

    String expected = "";
    String received = "";
    fb.write();

    FileReader fr = new FileReader(pf);
    int c = fr.read();
    while (c >= 0) {
      received += (char) c;
      c = fr.read();
    }




    
    ProjectFileIR pfir = null;
    try { pfir = ProjectFileParser.ONLY.parse(pf); }
    catch(MalformedProjectFileException e) {
      throw new MalformedProjectFileException(e.getMessage() + ", file: " + pf);
    }
    assertEquals("number of source files", 5, pfir.getSourceFiles().length);
    assertEquals("number of aux files", 2, pfir.getAuxiliaryFiles().length);
    assertEquals("number of collapsed", 1, pfir.getCollapsedPaths().length);
    assertEquals("number of classpaths", 1, pfir.getClassPaths().length);

    String base = pf.getParent();
    
    assertEquals("first source filename", new File(parent,"/dir1/testfile1.java").getPath(), 
                 pfir.getSourceFiles()[0].getPath());
    assertEquals("last source filename", new File(parent,"/dir3/testfile5.java").getPath(), 
                 pfir.getSourceFiles()[4].getPath());
    assertEquals("first aux filename", new File(parent,"test/testfile6.java").getPath(), 
                 pfir.getAuxiliaryFiles()[0].getPath());
    assertEquals("last collapsed path", "./[ Source Files ]/dir1/", pfir.getCollapsedPaths()[0]);
    assertEquals("build-dir name", buildDir, pfir.getBuildDirectory());
    assertEquals("work-dir name", srcDir, pfir.getWorkingDirectory());
    assertEquals("classpath name", new File(parent,"lib"), pfir.getClassPaths()[0]);
    assertEquals("main-class name", new File(parent,"/dir1/testfile1.java"), pfir.getMainClass());
    pf.delete();
  }

  private DocumentInfoGetter makeGetter(final int sel1, final int sel2, final int scrollv,
                                        final int scrollh, final String fname, final String pack,
                                        final boolean active, final boolean isUntitled, final File pf) {
    return new DocumentInfoGetter() {
      public Pair<Integer,Integer> getSelection() { 
        return new Pair<Integer,Integer>(new Integer(sel1),new Integer(sel2)); 
      }
      public Pair<Integer,Integer> getScroll() { 
        return new Pair<Integer,Integer>(new Integer(scrollv),new Integer(scrollh)); 
      }
      public File getFile() {
        if (pf == null) return new File(fname);
        else return new File(pf.getParentFile(),fname);
      }
      public String getPackage() { return pack; }
      public boolean isActive() { return active; }
      public boolean isUntitled() { return isUntitled; }
    };

  }
}
