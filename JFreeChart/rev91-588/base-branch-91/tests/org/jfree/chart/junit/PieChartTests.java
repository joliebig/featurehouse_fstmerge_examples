

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;


public class PieChartTests extends TestCase {

    
    private JFreeChart pieChart;

    
    public static Test suite() {
        return new TestSuite(PieChartTests.class);
    }

    
    public PieChartTests(String name) {
        super(name);
    }

    
    protected void setUp() {

        this.pieChart = createPieChart();

    }

    
    public void testReplaceDatasetOnPieChart() {
        LocalListener l = new LocalListener();
        this.pieChart.addChangeListener(l);
        PiePlot plot = (PiePlot) this.pieChart.getPlot();
        plot.setDataset(null);
        assertEquals(true, l.flag);
        assertNull(plot.getDataset());
    }

    
    private static JFreeChart createPieChart() {
        
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Java", new Double(43.2));
        data.setValue("Visual Basic", new Double(0.0));
        data.setValue("C/C++", new Double(17.5));

        
        return ChartFactory.createPieChart("Pie Chart",  
                                           data,         
                                           true,         
                                           true,
                                           false
                                           );
    }

    
    static class LocalListener implements ChartChangeListener {

        
        private boolean flag = false;

        
        public void chartChanged(ChartChangeEvent event) {
            this.flag = true;
        }

    }

}
