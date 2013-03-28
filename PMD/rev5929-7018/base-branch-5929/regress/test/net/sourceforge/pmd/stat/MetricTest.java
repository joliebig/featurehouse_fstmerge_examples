
package test.net.sourceforge.pmd.stat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.stat.Metric;

import java.util.Random;

public class MetricTest  {
    private String testName = "";
    private Random random = new Random();

    @Test
    public void testGetMetricName() {
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, 0.0);

        assertEquals(testName, IUT.getMetricName());
    }

    @Test
    public void testGetCount() {
        int count = random.nextInt();
        Metric IUT = new Metric(testName, count, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertEquals(count, IUT.getCount());
    }

    @Test
    public void testGetTotal() {
        double total = random.nextDouble();
        Metric IUT = new Metric(testName, 0, total, 0.0, 0.0, 0.0, 0.0);
        assertEquals(total, IUT.getTotal(), 0.05);
    }

    @Test
    public void testGetLowValue() {
        double low = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, low, 0.0, 0.0, 0.0);
        assertEquals(low, IUT.getLowValue(), 0.05);
    }

    @Test
    public void testGetHighValue() {
        double high = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, high, 0.0, 0.0);
        assertEquals(high, IUT.getHighValue(), 0.05);
    }

    @Test
    public void testGetAverage() {
        double mean = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, mean, 0.0);
        assertEquals(mean, IUT.getAverage(), 0.05);
    }

    @Test
    public void testGetStandardDeviation() {
        double stdev = random.nextDouble();
        Metric IUT = new Metric(testName, 0, 0.0, 0.0, 0.0, 0.0, stdev);
        assertEquals(stdev, IUT.getStandardDeviation(), 0.05);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MetricTest.class);
    }
}
