

package org.jfree.chart.renderer.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.RendererUtilities;
import org.jfree.data.DomainOrder;
import org.jfree.data.xy.DefaultXYDataset;


public class RendererUtilitiesTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(RendererUtilitiesTests.class);
    }

    
    public RendererUtilitiesTests(String name) {
        super(name);
    }

    
    public void testFindLiveItemsLowerBoundUnordered() {
        DefaultXYDataset d = new DefaultXYDataset();

        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 0, 10.0,
                11.0));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 2.0,
                3.3));

        
        d.addSeries("S3", new double[][] {{1.0, 2.0}, {2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 2, 3.0,
                4.4));

        
        d.addSeries("S4", new double[][] {{1.0, 2.0, 1.5}, {2.0, 2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 2.0,
                3.3));
        assertEquals(2, RendererUtilities.findLiveItemsLowerBound(d, 3, 3.0,
                4.4));

        
        d.addSeries("S5", new double[][] {{1.0, 2.0, 1.5, 1.8}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 2.0,
                3.3));
        assertEquals(3, RendererUtilities.findLiveItemsLowerBound(d, 4, 3.0,
                4.4));
        assertEquals(3, RendererUtilities.findLiveItemsLowerBound(d, 4, 4.0,
                5.5));
    }

    
    public void testFindLiveItemsLowerBoundAscending() {
        DefaultXYDataset d = new DefaultXYDataset() {
            public DomainOrder getDomainOrder() {
                
                
                return DomainOrder.ASCENDING;
            }
        };
        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 0, 10.0,
                11.1));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 2.0,
                2.2));

        
        d.addSeries("S3", new double[][] {{1.0, 2.0}, {2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 2, 3.0,
                4.4));

        
        d.addSeries("S4", new double[][] {{1.0, 2.0, 3.0}, {2.0, 2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 3, 3.0,
                4.4));

        
        d.addSeries("S5", new double[][] {{1.0, 2.0, 3.0, 4.0}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 1.0,
                2.2));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 4, 3.0,
                4.4));
        assertEquals(2, RendererUtilities.findLiveItemsLowerBound(d, 4, 4.0,
                5.5));

    }

    
    public void testFindLiveItemsLowerBoundDescending() {
        DefaultXYDataset d = new DefaultXYDataset() {
            public DomainOrder getDomainOrder() {
                
                
                return DomainOrder.DESCENDING;
            }
        };
        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 0, 10.0,
                11.0));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 0.0,
                1.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 1, 1.1,
                2.0));

        
        d.addSeries("S3", new double[][] {{2.0, 1.0}, {2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 2, 0.1,
                0.5));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 0.1,
                1.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 1.1,
                2.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 2.2,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 2, 3.3,
                4.0));

        
        d.addSeries("S4", new double[][] {{3.0, 2.0, 1.0}, {2.0, 2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 3, 0.0,
                1.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 1.0,
                2.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 2.0,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 3, 3.0,
                4.0));

        
        d.addSeries("S5", new double[][] {{4.0, 3.0, 2.0, 1.0}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(3, RendererUtilities.findLiveItemsLowerBound(d, 4, 0.1,
                0.5));
        assertEquals(2, RendererUtilities.findLiveItemsLowerBound(d, 4, 0.1,
                1.0));
        assertEquals(1, RendererUtilities.findLiveItemsLowerBound(d, 4, 1.1,
                2.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 2.2,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 3.3,
                4.0));
        assertEquals(0, RendererUtilities.findLiveItemsLowerBound(d, 4, 4.4,
                5.0));
    }

    
    public void testFindLiveItemsUpperBoundUnordered() {
        DefaultXYDataset d = new DefaultXYDataset();

        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 0, 10.0,
                11.0));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 2.0,
                3.3));

        
        d.addSeries("S3", new double[][] {{1.0, 2.0}, {2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 0.0,
                1.1));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 1.0,
                2.2));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 3.0,
                4.4));

        
        d.addSeries("S4", new double[][] {{1.0, 2.0, 1.5}, {2.0, 2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 3, 0.0,
                1.1));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 1.0,
                2.2));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 2.0,
                3.3));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 3.0,
                4.4));

        
        d.addSeries("S5", new double[][] {{1.0, 2.0, 1.5, 1.8}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 4, 0.0,
                1.1));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 1.0,
                2.2));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 2.0,
                3.3));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 3.0,
                4.4));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 4.0,
                5.5));
    }

    
    public void testFindLiveItemsUpperBoundAscending() {
        DefaultXYDataset d = new DefaultXYDataset() {
            public DomainOrder getDomainOrder() {
                
                
                return DomainOrder.ASCENDING;
            }
        };
        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 0, 10.0,
                11.1));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 0.0,
                1.1));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 2.0,
                2.2));

        
        d.addSeries("S3", new double[][] {{1.0, 2.0}, {2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 0.0,
                1.0));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 1.0,
                2.2));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 2.0,
                3.3));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 3.0,
                4.4));

        
        d.addSeries("S4", new double[][] {{1.0, 2.0, 3.0}, {2.0, 2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 3, 0.0,
                1.1));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 1.0,
                2.2));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 2.0,
                3.3));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 3.0,
                4.4));

        
        d.addSeries("S5", new double[][] {{1.0, 2.0, 3.0, 4.0}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 4, 0.0,
                1.1));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 4, 1.0,
                2.2));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 2.0,
                3.3));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 3.0,
                4.4));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 4.0,
                5.5));

    }

    
    public void testFindLiveItemsUpperBoundDescending() {
        DefaultXYDataset d = new DefaultXYDataset() {
            public DomainOrder getDomainOrder() {
                
                
                return DomainOrder.DESCENDING;
            }
        };
        
        d.addSeries("S1", new double[][] {{}, {}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 0, 10.0,
                11.0));

        
        d.addSeries("S2", new double[][] {{1.0}, {2.0}});
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 0.0,
                1.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 1, 1.1,
                2.0));

        
        d.addSeries("S3", new double[][] {{2.0, 1.0}, {2.0, 2.0}});
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 0.1,
                0.5));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 2, 0.1,
                1.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 2, 1.1,
                2.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 2, 2.2,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 2, 3.3,
                4.0));

        
        d.addSeries("S4", new double[][] {{3.0, 2.0, 1.0}, {2.0, 2.0, 2.0}});
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 0.0,
                1.0));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 3, 1.0,
                2.0));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 3, 2.0,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 3, 3.0,
                4.0));

        
        d.addSeries("S5", new double[][] {{4.0, 3.0, 2.0, 1.0}, {2.0, 2.0,
                2.0, 2.0}});
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 0.1,
                0.5));
        assertEquals(3, RendererUtilities.findLiveItemsUpperBound(d, 4, 0.1,
                1.0));
        assertEquals(2, RendererUtilities.findLiveItemsUpperBound(d, 4, 1.1,
                2.0));
        assertEquals(1, RendererUtilities.findLiveItemsUpperBound(d, 4, 2.2,
                3.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 4, 3.3,
                4.0));
        assertEquals(0, RendererUtilities.findLiveItemsUpperBound(d, 4, 4.4,
                5.0));
    }


}
