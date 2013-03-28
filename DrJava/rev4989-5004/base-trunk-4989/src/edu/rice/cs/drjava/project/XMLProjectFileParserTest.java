

package edu.rice.cs.drjava.project;

import java.io.*;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.project.ProjectFileParserFacade;
import edu.rice.cs.drjava.project.ProjectFileIR;


public class XMLProjectFileParserTest extends DrJavaTestCase {
  public void testXMLParse() throws IOException, MalformedProjectFileException, java.text.ParseException {
    String xml = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"+
      "<drjava version=\""+edu.rice.cs.drjava.Version.getVersionString()+"\">\n"+
      "  <project root=\"src\" build=\"classes\" work=\"\" main=\"some.main.ClassName\">\n"+
      "    <createjar file=\"drjava-15.jar\" flags=\"3\"/>\n"+
      "    <source>\n"+
      
      "      <file name=\"edu/rice/cs/drjava/DrJava.java\" package=\"edu.rice.cs.drjava\" timestamp=\"27-Mar-2008 15:05:07\" active=\"true\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "      <file name=\"edu/rice/cs/drjava/config/FileProperty.java\" package=\"edu.rice.cs.drjava.config\" timestamp=\"14-Mar-2008 14:18:43\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "      <file name=\"edu/rice/cs/drjava/ui/config/BooleanOptionComponent.java\" package=\"edu.rice.cs.drjava.ui.config\" timestamp=\"30-Mar-2008 09:27:01\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "      <file name=\"edu/rice/cs/drjava/model/FindReplaceMachine.java\" package=\"edu.rics.cs.drjava.model\" timestamp=\"02-Apr-2008 10:58:15\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "      <file name=\"edu/rice/cs/drjava/project/XMLProjectFileParser.java\" package=\"edu.rics.cs.drjava.project\" timestamp=\"01-Apr-2008 14:30:25\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "      <file name=\"edu/rice/cs/drjava/ui/BackgroundColorListener.java\" package=\"edu.rics.cs.drjava.ui\" timestamp=\"10-Mar-2008 11:03:08\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+

      "    </source>\n"+
      "    <included>\n"+

      "      <file name=\""+System.getProperty("user.dir")+"/testFiles/sample-project-file.xml\" package=\"\" timestamp=\"13-Sep-2004 06:03:06\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+
      
      "    </included>\n"+
      
      "    <excluded>\n"+
      "      <file name=\"edu/rice/cs/drjava/ui/config/ColorOptionComponent.java\" package=\"edu.rice.cs.drjava.ui.config\" timestamp=\"30-Mar-2008 09:27:01\">\n"+
      "        <select from=\"0\" to=\"0\"/>\n"+
      "        <scroll column=\"0\" row=\"0\"/>\n"+
      "      </file>\n"+
      "    </excluded>\n"+
      
      "    <collapsed>\n"+

      "      <path name=\"./[ Source Files ]/parser/\"/>\n"+
      "      <path name=\"./[ Source Files ]/grammar/util/\"/>\n"+

      "    </collapsed>\n"+
      "    <classpath>\n"+
      
      "      <file name=\""+System.getProperty("user.dir")+"/lib\"/>\n"+
      "      <file name=\""+System.getProperty("user.dir")+"/lib/buildlib\"/>\n"+
      
      "    </classpath>\n"+
      "    <breakpoints>\n"+

      "      <breakpoint file=\"edu/rice/cs/drjava/DrJava.java\" line=\"50\" enabled=\"true\"/>\n"+
      "      <breakpoint file=\"edu/rice/cs/drjava/DrJava.java\" line=\"55\" enabled=\"true\"/>\n"+
      "      <breakpoint file=\"edu/rice/cs/drjava/DrJava.java\" line=\"53\" enabled=\"true\"/>\n"+

      "    </breakpoints>\n"+
      "    <watches>\n"+
      
      "      <watch name=\"args[0]\"/>\n"+
      "      <watch name=\"e\"/>\n"+
      
      "    </watches>\n"+
      "    <bookmarks>\n"+
      
      "      <bookmark file=\"edu/rice/cs/drjava/DrJava.java\" from=\"851\" to=\"900\"/>\n"+
      "      <bookmark file=\"edu/rice/cs/drjava/DrJava.java\" from=\"959\" to=\"1071\"/>\n"+
      
      "    </bookmarks>\n"+
      "  </project>\n"+
      "</drjava>\n";
    
    
    File f = File.createTempFile("project", ".xml", new File(System.getProperty("user.dir")));
    f.deleteOnExit();
    IOUtil.writeStringToFile(f,xml);
    
    ProjectFileIR pfir = ProjectFileParserFacade.ONLY.parse(f);
    
    assertEquals("number of source files", 6, pfir.getSourceFiles().length);
    assertEquals("number of aux files", 1, pfir.getAuxiliaryFiles().length);
    assertEquals("number of excluded files", 1, pfir.getExcludedFiles().length);
    assertEquals("number of collapsed", 2, pfir.getCollapsedPaths().length);
    assertEquals("number of classpaths", 2, IterUtil.sizeOf(pfir.getClassPaths()));
    File base = new File(f.getParent());
    File root = new File(base, "src");
    assertEquals("first source filename", new File(root,"edu/rice/cs/drjava/DrJava.java").getCanonicalPath(),
                 pfir.getSourceFiles()[0].getCanonicalPath());
    assertEquals("timestamp value", 
                 ProjectProfile.MOD_DATE_FORMAT.parse("27-Mar-2008 15:05:07").getTime(),
                 pfir.getSourceFiles()[0].getSavedModDate());
    assertEquals("last source filename", new File(root,"edu/rice/cs/drjava/ui/BackgroundColorListener.java").getCanonicalPath(), 
                 pfir.getSourceFiles()[5].getCanonicalPath());
    assertEquals("first aux filename", new File(System.getProperty("user.dir"),"/testFiles/sample-project-file.xml").getCanonicalPath(), 
                 pfir.getAuxiliaryFiles()[0].getCanonicalPath());
    assertEquals("last collapsed path", "./[ Source Files ]/grammar/util/", pfir.getCollapsedPaths()[1]);
    assertEquals("build-dir name", new File(base, "classes").getCanonicalPath(), 
                 pfir.getBuildDirectory().getCanonicalPath());
    assertEquals("work-dir name", new File(base, ".").getCanonicalPath(), 
                 pfir.getWorkingDirectory().getCanonicalPath());
    assertEquals("classpath name", new File(System.getProperty("user.dir"),"lib").getCanonicalPath(), 
                 IterUtil.first(pfir.getClassPaths()).getCanonicalPath());
    assertEquals("main-class name", "some.main.ClassName", 
                 pfir.getMainClass());
  }
}
