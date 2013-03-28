

package  edu.rice.cs.drjava.model.definitions;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.GlobalEventNotifier;

import javax.swing.text.BadLocationException;


public final class CommentTest extends DrJavaTestCase {
  protected DefinitionsDocument doc;
  private Integer _indentLevel = Integer.valueOf(2);
  private GlobalEventNotifier _notifier;

  
  public void setUp() throws Exception {
    super.setUp();
    DrJava.getConfig().resetToDefaults();
    _notifier = new GlobalEventNotifier();
    doc = new DefinitionsDocument(_notifier);
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL,_indentLevel);
  }

  
  public void testCommentOutSingleLine() throws BadLocationException {
    String text =
      "Here is some abritrary text that should be commented.\n" +
      "/* It is on multiple lines, and contains slashes // and other\n" +
      "various */ obnoxious characters.\n";

    String commented =
      "Here is some abritrary text that should be commented.\n" +
      "///* It is on multiple lines, and contains slashes // and other\n" +
      "various */ obnoxious characters.\n";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.commentLines(70, 75);
    _assertContents("Only the second line should be wing-commented!", commented, doc);
  }

  
  public void testCommentOutMultipleLines() throws BadLocationException {
    String text =
      "Here is some abritrary text that should be commented.\n" +
      "/* It is on multiple lines, and contains slashes // and other\n" +
      "various */ obnoxious characters.\n";

    String commented =
      "//Here is some abritrary text that should be commented.\n" +
      "///* It is on multiple lines, and contains slashes // and other\n" +
      "//various */ obnoxious characters.\n";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.commentLines(0, doc.getLength());
    _assertContents("These lines should be wing-commented!", commented, doc);
  }

  
  public void testUncommentIgnoreSingleLine() throws BadLocationException {
    String text =
      "Here is some abritrary text that should not be uncommented.\n" +
      "/* It is on multiple lines, and contains slashes // and other\n" +
      "* various */ obnoxious characters,\n" +
      "sometimes // in block comments and sometimes not.";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.uncommentLines(70, 75);
    _assertContents("These lines should be unchanged by uncomment!", text, doc);
  }

  
  public void testUncommentIgnoreMultipleLines() throws BadLocationException {
    String text =
      "Here is some abritrary text that should not be uncommented.\n" +
      "/* It is on multiple lines, and contains slashes // and other\n" +
      "* various */ obnoxious characters,\n" +
      "sometimes // in block comments and sometimes not.";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.uncommentLines(0, doc.getLength());
    _assertContents("These lines should be unchanged by uncomment!", text, doc);
  }

  
  public void testUncommentSingleLine() throws BadLocationException {
    String text =
      "// // Here is some abritrary text that should be uncommented.\n" +
      "// /* along with a little bit of code, just to spice\n" +
      "      //* things up.\n" +
      "//                    */ \n" +
      "//         System.out.println(\"Aren't comments fun? // (yeah!)\")";

    String uncommented =
      "// // Here is some abritrary text that should be uncommented.\n" +
      "// /* along with a little bit of code, just to spice\n" +
      "      //* things up.\n" +
      "//                    */ \n" +
      "         System.out.println(\"Aren't comments fun? // (yeah!)\")";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.uncommentLines(doc.getLength()-1, doc.getLength());
    _assertContents("The last line should have no commenting!",
                    uncommented, doc);
  }

  
  public void testUncommentMultipleLines() throws BadLocationException {
    String text =
      "//// Here is some abritrary text that should be uncommented.\n" +
      "// /* along with a little bit of code, just to spice\n" +
      "//  * things up.\n" +
      "//  */ \n" +
      "// System.out.println(\"Aren't comments fun? // (yeah!)\")";

    String uncommented =
      "// Here is some abritrary text that should be uncommented.\n" +
      " /* along with a little bit of code, just to spice\n" +
      "  * things up.\n" +
      "  */ \n" +
      " System.out.println(\"Aren't comments fun? // (yeah!)\")";

    doc.insertString(0, text, null);
    _assertContents("Sample text is inserted improperly.", text, doc);
    doc.uncommentLines(0, doc.getLength());
    _assertContents("These lines should have at most one level of commenting!", uncommented, doc);
  }

  private static void _assertContents(String msg, String expected, DJDocument document)
    throws BadLocationException {
    assertEquals(msg, expected, document.getText());
  }
}
