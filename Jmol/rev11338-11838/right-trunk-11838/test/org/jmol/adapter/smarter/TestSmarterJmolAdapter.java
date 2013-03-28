

package org.jmol.adapter.smarter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.jmol.util.JUnitLogger;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSmarterJmolAdapter extends TestSuite {

  private String datafileDirectory = "../Jmol-datafiles";

  public TestSmarterJmolAdapter() {
    super();
  }

  public TestSmarterJmolAdapter(Class theClass, String name) {
    super(theClass, name);
  }

  public TestSmarterJmolAdapter(Class theClass) {
    super(theClass);
  }

  public TestSmarterJmolAdapter(String name) {
    super(name);
  }

  String testOne;
  
  
  public static Test suite() {
    TestSmarterJmolAdapter result = new TestSmarterJmolAdapter("Test for org.jmol.adapter.smarter.SmarterJmolAdapter");
    result.datafileDirectory = System.getProperty("test.datafile.directory", result.datafileDirectory);
    
    
    
    
    
    
    result.addDirectory(false, "adf", "adf", "Adf");
    result.addDirectory(false, "aminoacids", "mol");
    result.addDirectory(false, "aminoacids", "pdb");
    result.addDirectory(false, "animations", "cml", "cml(xml)");
    result.addDirectory(false, "animations", "pdb");
    result.addDirectory(true,  "animations", "pdb.gz");
    result.addDirectory(false, "animations", "xyz");
    result.addDirectory(false, "cif", "cif");
    result.addDirectory(false, "c3xml", "c3xml", "chem3d(xml)");
    result.addDirectory(false, "cml", "cml", "cml(xml)");
    result.addDirectory(false, "crystals", "mol");
    result.addDirectory(false, "crystals", "pdb");
    result.addDirectory(false, "csf", "csf", "Csf");
    result.addDirectory(true,  "cube", "cub.gz");
    result.addDirectory(true,  "cube", "cube.gz");
    result.addDirectory(false, "folding", "xyz", "FoldingXyz");
    result.addDirectory(true,  "folding", "xyz.gz", "FoldingXyz");
    result.addDirectory(false, "../Jmol-FAH/projects", "xyz", "FoldingXyz");
    result.addDirectory(true,  "../Jmol-FAH/projects", "xyz.gz", "FoldingXyz");
    result.addDirectory(false, "gamess", "log", ";Gamess;GamessUS;GamessUK;");
    result.addDirectory(false, "gamess", "out", ";Gamess;GamessUS;GamessUK;");
    result.addDirectory(false, "gaussian", "log");
    result.addDirectory(false, "gaussian", "out");
    result.addDirectory(false, "ghemical", "gpr", "GhemicalMM");
    result.addDirectory(false, "gpt2", "gpt2","MopacGraphf");
    result.addDirectory(false, "hin", "hin");
    result.addDirectory(false, "jaguar", "out");
    result.addDirectory(false, "modifiedGroups", "cif");
    result.addDirectory(false, "modifiedGroups", "pdb");
    result.addDirectory(false, "mol", "mol");
    result.addDirectory(false, "mol", "sdf");
    result.addDirectory(false, "mol2", "mol2");
    result.addDirectory(false, "molpro", "xml", "molpro(xml)");
    result.addDirectory(false, "mopac", "out");
    result.addDirectory(false, "nwchem", "nwo", "NWChem");
    result.addDirectory(false, "pdb", "pdb");
    result.addDirectory(true,  "pdb", "pdb.gz");
    
    result.addDirectory(false, "psi3", "out", "Psi");
    result.addDirectory(false, "qchem", "out");
    result.addDirectory(false, "shelx", "res");
    result.addDirectory(false, "spartan", "smol", "SpartanSmol");
    result.addDirectory(false, "spartan", "txt", "Spartan");
    result.addDirectory(false, "sparchive", "sparchive", "Spartan");
    result.addDirectory(false, "spinput", "spinput", "Odyssey");
    result.addDirectory(false, "v3000", "sdf");
    result.addDirectory(false, "wien2k", "struct");
    result.addDirectory(false, "xyz", "xyz");
    return result;
  }

  
  private void addDirectory(boolean gzipped, String directory, String ext) {
    if (testOne != null && !directory.equals(testOne))
      return;
    String type = ext;
    if (type.indexOf(".") >=0 )
      type = type.substring(0, type.indexOf("."));
    if (!Parser.isOneOf(type, "cif;pdb;xyz;mol;"))
      type = directory;
    type = type.substring(0,1).toUpperCase() + type.substring(1);
    addDirectory(gzipped, directory, ext, type);
  }

  
  private void addDirectory(boolean gzipped,
                            String directory,
                            final String ext,
                            String typeAllowed) {

    
    if (testOne != null && !directory.equals(testOne))
      return;
    File dir = new File(datafileDirectory, directory);
    String[] files = dir.list(new FilenameFilter() {

      public boolean accept(File dir, String name) {
        if (name.endsWith("." + ext)) {
          return true;
        }
        return false;
      }

    });
    if (files == null) {
      Logger.warn("No files in directory [" + directory + "] for extension [" + ext + "]");
    } else {
      for (int i = 0; i < files.length; i++) {
        addFile(gzipped, directory, files[i], typeAllowed);
      }
    }
  }

  
  private void addFile(boolean gzipped,
                       String directory,
                       String filename,
                       String typeAllowed) {

    File file = new File(new File(datafileDirectory, directory), filename);
    Test test = new TestSmarterJmolAdapterImpl(file, gzipped, typeAllowed);
    addTest(test);
  }
}


class TestSmarterJmolAdapterImpl extends TestCase {

  private File file;
  private boolean gzipped;
  private String typeAllowed;

  public TestSmarterJmolAdapterImpl(File file, boolean gzipped, String typeAllowed) {
    super("testFile");
    this.file = file;
    this.gzipped = gzipped;
    this.typeAllowed = typeAllowed;
  }

  
  public void runTest() throws Throwable {
    testFile();
  }

  
  public void testFile() throws FileNotFoundException, IOException {
    JUnitLogger.setInformation(file.getPath());
    InputStream iStream = new FileInputStream(file);
    iStream = new BufferedInputStream(iStream);
    if (gzipped) {
      iStream = new GZIPInputStream(iStream);
    }
    Logger.info(file.getPath());
    BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
    SmarterJmolAdapter adapter = new SmarterJmolAdapter();
    if (typeAllowed != null) {
      String fileType = adapter.getFileTypeName(bReader);
      if (!typeAllowed.equals(fileType) && typeAllowed.indexOf(";"+ fileType + ";") < 0) {
        fail("Wrong type for " + file.getPath() + ": " + fileType + " instead of " + typeAllowed);
      }
    }
    Object result = adapter.getAtomSetCollectionFromReader(file.getName(), null, bReader, null);
    assertNotNull("Nothing read for " + file.getPath(), result);
    assertFalse("Error returned for " + file.getPath() + ": " + result, result instanceof String);
    assertTrue("Not an AtomSetCollection for " + file.getPath(), result instanceof AtomSetCollection);
    AtomSetCollection collection = (AtomSetCollection) result;
    assertTrue("No atoms loaded for " + file.getPath(), collection.getAtomCount() > 0);
  }

  
  public String getName() {
    if (file != null) {
      return super.getName() + " [" + file.getPath() + "]";
    }
    return super.getName();
  }

  
  protected void setUp() throws Exception {
    super.setUp();
    JUnitLogger.activateLogger();
    JUnitLogger.setInformation(null);
  }

  
  protected void tearDown() throws Exception {
    super.tearDown();
    JUnitLogger.setInformation(null);
    file = null;
    typeAllowed = null;
  }
}
