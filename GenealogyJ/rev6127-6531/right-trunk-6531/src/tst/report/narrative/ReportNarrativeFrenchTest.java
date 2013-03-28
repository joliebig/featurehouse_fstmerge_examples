package report.narrative;

import java.io.IOException;
import java.util.Locale;


public class ReportNarrativeFrenchTest extends ReportNarrativeTest {

  public void testAncestorsFr() throws IOException {
    testAncestors(Locale.FRANCE);
  }

  public void testDescendantsFr() throws IOException {
    testDescendants(Locale.FRANCE);
  }

}