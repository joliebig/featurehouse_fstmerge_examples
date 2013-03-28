
package test.net.sourceforge.pmd.stat;

import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.MINIMUM_DESCRIPTOR;
import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.SIGMA_DESCRIPTOR;
import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.TOP_SCORE_DESCRIPTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.AssertionFailedError;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.Metric;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.TestDescriptor;


public class StatisticalRuleTest  {

    private static final int POINTS = 100;

    private DataPoint points[] = new DataPoint[POINTS];
    private MockStatisticalRule IUT = null;
    private String testName = "";
    private Random random = new Random();

    public static final double MAX_MINIMUM = POINTS;
    public static final double NO_MINIMUM = -1.0;
    public static final double MAX_SIGMA = 5.0;
    public static final double NO_SIGMA = -1.0;
    public static final int MIN_TOPSCORE = 0;
    public static final int NO_TOPSCORE = -1;


    public static final double MEAN = 49.5;
    public static final double SIGMA = 29.0115;
    public static final int NUM_TESTS = 1;

    public static final double DELTA = 0.005;


    @Before
    public void setUp() {
        IUT = new MockStatisticalRule();
        if (testName.endsWith("0")) {
            for (int i = 0; i < POINTS; i++) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyJavaNode s = new DummyJavaNode(1);
                s.setScope(new SourceFileScope("foo"));
                s.testingOnly__setBeginLine(i);
                s.testingOnly__setBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                IUT.addDataPoint(points[i]);
            }
        } else if (testName.endsWith("1")) {
            for (int i = POINTS - 1; i >= 0; i--) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyJavaNode s = new DummyJavaNode(1);
                s.setScope(new SourceFileScope("foo"));
                s.testingOnly__setBeginLine(i);
                s.testingOnly__setBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                IUT.addDataPoint(points[i]);
            }
        } else {
            List<DataPoint> lPoints = new ArrayList<DataPoint>();
            for (int i = 0; i < POINTS; i++) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyJavaNode s = new DummyJavaNode(1);
                s.setScope(new SourceFileScope("foo"));
                s.testingOnly__setBeginLine(i);
                s.testingOnly__setBeginColumn(1);
                s.testingOnly__setBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                lPoints.add(points[i]);
            }

            Collections.shuffle(lPoints);
            for (int i = 0; i < POINTS; i++) {
                IUT.addDataPoint(lPoints.get(i));
            }
        }

    }

    
    @Test
    public void testMetrics() throws Throwable {
        Report report = makeReport(IUT);
        Iterator metrics = report.metrics();

        assertTrue(metrics.hasNext());
        Object o = metrics.next();

        assertTrue(o instanceof Metric);
        Metric m = (Metric) o;

        assertEquals("test.net.sourceforge.pmd.stat.MockStatisticalRule", m.getMetricName());

        assertEquals(0.0, m.getLowValue(), 0.05);
        assertEquals(POINTS - 1.0, m.getHighValue(), 0.05);
        assertEquals(MEAN, m.getAverage(), 0.05);
        assertEquals(SIGMA, m.getStandardDeviation(), 0.05);
    }

    
    public double randomSigma() {
        return random.nextDouble() * 1.0;
    }

    
    public double randomSigma(int minimum) {
        double minSigma = ((POINTS - 1 - minimum) - MEAN) / SIGMA;

        if ((minSigma <= 0) || (minSigma > 2))
            return randomSigma();

        return minSigma + (random.nextDouble() * (2 - minSigma));
    }

    
    public int expectedSigma(double sigma) {
        long expectedMin = Math.round(MEAN + (sigma * SIGMA));

        if (((POINTS - 1) - expectedMin) < 0)
            return 0;
        return (POINTS - 1) - (int) expectedMin;
    }

    
    public double randomMinimum() {
        return random.nextDouble() * (POINTS - 1);
    }

    
    public double randomMinimum(int minimum) {
        double diffTarget = 1.0 * (POINTS - 1 - minimum);
        return (random.nextDouble() * minimum) + diffTarget;
    }

    
    public int expectedMinimum(double minimum) {
        Double d = Double.valueOf(minimum);
        return POINTS - 1 - d.intValue();
    }

    @Test
    public void testExpectedMinimum() {
        for (int i = 0; i < POINTS - 1; i++) {
            assertEquals("Integer Min", POINTS - 1 - i, expectedMinimum(i * 1.0));
            assertEquals("Double Min", POINTS - 1 - i, expectedMinimum((i * 1.0) + 0.5));
        }
    }

    
    public int randomTopScore() {
        return random.nextInt(POINTS - 1);
    }

    
    public int randomTopScore(double target) {
        if (target < 0)
            return 0;

        return random.nextInt(Double.valueOf(target).intValue());
    }

    
    public int expectedTopScore(int target) {
        return target;
    }

    
    @Test
    public void testSingleDatapoint() {
        StatisticalRule IUT = new MockStatisticalRule();

        DataPoint point = new DataPoint();
        point.setScore(POINTS + 1.0);
        DummyJavaNode s = new DummyJavaNode(1);
        s.setScope(new SourceFileScope("foo"));
        s.testingOnly__setBeginLine(POINTS + 1);
        s.testingOnly__setBeginColumn(1);
        point.setNode(s);
        point.setMessage("SingleDataPoint");

        IUT.setProperty(MINIMUM_DESCRIPTOR, (double)POINTS);

        IUT.addDataPoint(point);

        Report report = makeReport(IUT);

        assertEquals("Expecting only one result", 1, report.size());
    }

    
    
    
    
    
    
    
    
    
    
    
    

    @Test
    public void testS() throws Throwable {
        verifyResults(MAX_SIGMA, NO_MINIMUM, NO_TOPSCORE, 0, 2);

        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            verifyResults(sigma, -1.0, -1, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testS1() throws Throwable {
        testS();
    }

    @Test
    public void testS2() throws Throwable {
        testS();
    }

    @Test
    public void testS3() throws Throwable {
        testS();
    }

    @Test
    public void testS4() throws Throwable {
        testS();
    }

    @Test
    public void testS5() throws Throwable {
        testS();
    }


    @Test
    public void testT() throws Throwable {
        verifyResults(NO_SIGMA, NO_MINIMUM, MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            verifyResults(-1.0, -1.0, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testT1() throws Throwable {
        testT();
    }

    @Test
    public void testT2() throws Throwable {
        testT();
    }

    @Test
    public void testT3() throws Throwable {
        testT();
    }

    @Test
    public void testT4() throws Throwable {
        testT();
    }

    @Test
    public void testT5() throws Throwable {
        testT();
    }

    @Test
    public void testM() throws Throwable {
        verifyResults(NO_SIGMA, MAX_MINIMUM, NO_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            verifyResults(-1.0, minimum, -1, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testM1() throws Throwable {
        testM();
    }

    @Test
    public void testM2() throws Throwable {
        testM();
    }

    @Test
    public void testM3() throws Throwable {
        testM();
    }

    @Test
    public void testM4() throws Throwable {
        testM();
    }

    @Test
    public void testM5() throws Throwable {
        testM();
    }

    @Test
    public void testST() throws Throwable {
        verifyResults(randomSigma(), NO_MINIMUM, MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            int topScore = randomTopScore(expectedSigma(sigma));

            verifyResults(sigma, NO_MINIMUM, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testST1() throws Throwable {
        testST();
    }

    @Test
    public void testST2() throws Throwable {
        testST();
    }

    @Test
    public void testST3() throws Throwable {
        testST();
    }

    @Test
    public void testST4() throws Throwable {
        testST();
    }

    @Test
    public void testST5() throws Throwable {
        testST();
    }

    @Test
    public void testTS() throws Throwable {
        verifyResults(MAX_SIGMA, NO_MINIMUM, randomTopScore(), 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            double sigma = randomSigma(expectedTopScore(topScore));

            verifyResults(sigma, -1.0, topScore, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testTS1() throws Throwable {
        testTS();
    }

    @Test
    public void testTS2() throws Throwable {
        testTS();
    }

    @Test
    public void testTS3() throws Throwable {
        testTS();
    }

    @Test
    public void testTS4() throws Throwable {
        testTS();
    }

    @Test
    public void testTS5() throws Throwable {
        testTS();
    }

    @Test
    public void testSM() throws Throwable {
        verifyResults(randomSigma(), MAX_MINIMUM, NO_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            double minimum = randomMinimum(expectedSigma(sigma));

            verifyResults(sigma, minimum, -1, expectedMinimum(minimum), 0);
        }

    }

    @Test
    public void testSM1() throws Throwable {
        testSM();
    }

    @Test
    public void testSM2() throws Throwable {
        testSM();
    }

    @Test
    public void testSM3() throws Throwable {
        testSM();
    }

    @Test
    public void testSM4() throws Throwable {
        testSM();
    }

    @Test
    public void testSM5() throws Throwable {
        testSM();
    }


    @Test
    public void testMS() throws Throwable {
        verifyResults(MAX_SIGMA, randomMinimum(), NO_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            double sigma = randomSigma(expectedMinimum(minimum));

            verifyResults(sigma, minimum, -1, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testMS1() throws Throwable {
        testMS();
    }

    @Test
    public void testMS2() throws Throwable {
        testMS();
    }

    @Test
    public void testMS3() throws Throwable {
        testMS();
    }

    @Test
    public void testMS4() throws Throwable {
        testMS();
    }

    @Test
    public void testMS5() throws Throwable {
        testMS();
    }


    @Test
    public void testTM() throws Throwable {
        verifyResults(NO_SIGMA, MAX_MINIMUM, randomTopScore(), 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            double minimum = randomMinimum(expectedTopScore(topScore));

            verifyResults(NO_SIGMA, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testTM1() throws Throwable {
        testTM();
    }

    @Test
    public void testTM2() throws Throwable {
        testTM();
    }

    @Test
    public void testTM3() throws Throwable {
        testTM();
    }

    @Test
    public void testTM4() throws Throwable {
        testTM();
    }

    @Test
    public void testTM5() throws Throwable {
        testTM();
    }


    @Test
    public void testMT() throws Throwable {
        verifyResults(NO_SIGMA, randomMinimum(), MIN_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            int topScore = randomTopScore(expectedMinimum(minimum));

            verifyResults(NO_SIGMA, minimum, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testMT1() throws Throwable {
        testMT();
    }

    @Test
    public void testMT2() throws Throwable {
        testMT();
    }

    @Test
    public void testMT3() throws Throwable {
        testMT();
    }

    @Test
    public void testMT4() throws Throwable {
        testMT();
    }

    @Test
    public void testMT5() throws Throwable {
        testMT();
    }


    @Test
    public void testSTM() throws Throwable {
        double sigma = randomSigma();
        verifyResults(sigma, MAX_MINIMUM, randomTopScore(expectedSigma(sigma)), 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            sigma = randomSigma();
            int topScore = randomTopScore(expectedSigma(sigma));
            double minimum = randomMinimum(expectedTopScore(topScore));

            verifyResults(sigma, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testSTM1() throws Throwable {
        testSTM();
    }

    @Test
    public void testSTM2() throws Throwable {
        testSTM();
    }

    @Test
    public void testSTM3() throws Throwable {
        testSTM();
    }

    @Test
    public void testSTM4() throws Throwable {
        testSTM();
    }

    @Test
    public void testSTM5() throws Throwable {
        testSTM();
    }

    @Test
    public void testSMT() throws Throwable {
        double sigma = randomSigma();
        verifyResults(sigma, randomMinimum(expectedSigma(sigma)), MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            sigma = randomSigma();
            double minimum = randomMinimum(expectedSigma(sigma));
            int topScore = randomTopScore(expectedMinimum(minimum));

            verifyResults(sigma, minimum, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testSMT1() throws Throwable {
        testSMT();
    }

    @Test
    public void testSMT2() throws Throwable {
        testSMT();
    }

    @Test
    public void testSMT3() throws Throwable {
        testSMT();
    }

    @Test
    public void testSMT4() throws Throwable {
        testSMT();
    }

    @Test
    public void testSMT5() throws Throwable {
        testSMT();
    }

    @Test
    public void testTSM() throws Throwable {
        if (TestDescriptor.inRegressionTestMode()) {
            
            
            
            return;
        }
        int topScore = randomTopScore();
        verifyResults(randomSigma(expectedTopScore(topScore)), MAX_MINIMUM, topScore, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            topScore = randomTopScore();
            double sigma = randomSigma(expectedTopScore(topScore));
            double minimum = randomMinimum(expectedSigma(sigma));

            verifyResults(sigma, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testTSM1() throws Throwable {
        testTSM();
    }

    @Test
    public void testTSM2() throws Throwable {
        testTSM();
    }

    @Test
    public void testTSM3() throws Throwable {
        testTSM();
    }

    @Test
    public void testTSM4() throws Throwable {
        testTSM();
    }

    @Test
    public void testTSM5() throws Throwable {
        testTSM();
    }

    @Test
    public void testTMS() throws Throwable {
        int topScore = randomTopScore();
        verifyResults(MAX_SIGMA, randomMinimum(expectedTopScore(topScore)), topScore, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            topScore = randomTopScore();
            double minimum = randomMinimum(expectedTopScore(topScore));
            double sigma = randomSigma(expectedMinimum(minimum));

            verifyResults(sigma, minimum, topScore, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testTMS1() throws Throwable {
        testTMS();
    }

    @Test
    public void testTMS2() throws Throwable {
        testTMS();
    }

    @Test
    public void testTMS3() throws Throwable {
        testTMS();
    }

    @Test
    public void testTMS4() throws Throwable {
        testTMS();
    }

    @Test
    public void testTMS5() throws Throwable {
        testTMS();
    }

    

    public void verifyResults(double sigma, double minimum, int topScore, int expected, int delta) {
        try {
            setUp();
            if (sigma >= 0) {
            	IUT.setProperty(SIGMA_DESCRIPTOR, sigma);
            }

            if (minimum >= 0) {
            	IUT.setProperty(MINIMUM_DESCRIPTOR, minimum);
            }

            if (topScore >= 0) {
                IUT.setProperty(TOP_SCORE_DESCRIPTOR, topScore);
            }

            Report report = makeReport(IUT);
            if (delta == 0) {
                assertEquals("Unexpected number of results: sigma= " + Double.toString(sigma) + " min= " + Double.toString(minimum) + " topscore= " + Integer.toString(topScore), expected, report.size());
            } else {
                String assertStr = "Unexpected number of results: sigma= " + Double.toString(sigma) + " min= " + Double.toString(minimum) + " topscore= " + Integer.toString(topScore) + " expected= " + Integer.toString(expected) + " +/- " + Integer.toString(delta) + " actual-result= " + report.size();

                assertTrue(assertStr, report.size() >= (expected - delta));
                assertTrue(assertStr, report.size() <= (expected + delta));
            }
        } catch (AssertionFailedError afe) {
            System.err.println("******** " + testName + " ***********");
            if (sigma != NO_SIGMA) {
                System.err.println("SIGMA: " + Double.toString(sigma) + " EXPECT: " + Integer.toString(expectedSigma(sigma)));
            }

            if (minimum != NO_MINIMUM) {
                System.err.println("MIN: " + Double.toString(minimum) + " EXPECT: " + Integer.toString(expectedMinimum(minimum)));
            }

            if (topScore != NO_TOPSCORE) {
                System.err.println("TOP: " + Integer.toString(topScore) + " EXPECT: " + Integer.toString(expectedTopScore(topScore)));
            }

            throw afe;

        }
    }

    public Report makeReport(Rule IUT) {
        List list = new ArrayList();
        Report report = new Report();

        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename(testName);
        ctx.setLanguageVersion(Language.JAVA.getDefaultVersion());

        IUT.apply(list, ctx);

        return report;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StatisticalRuleTest.class);
    }
}
