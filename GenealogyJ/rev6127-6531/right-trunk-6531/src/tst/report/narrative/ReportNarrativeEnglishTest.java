package report.narrative;

import java.io.IOException;
import java.util.Locale;


public class ReportNarrativeEnglishTest extends ReportNarrativeTest {

  public void testAncestorsEn() throws IOException {
    testAncestors(Locale.ENGLISH);
  }
  
  public void testDescendantsEn() throws IOException {
    testDescendants(Locale.ENGLISH);
  }
    
}