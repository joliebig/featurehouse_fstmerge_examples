

package org.jfree.chart.demo;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.ApplicationFrame;
import org.jfree.chart.util.RefineryUtilities;
import org.jfree.data.pie.DefaultPieDataset;
import org.jfree.data.pie.PieDataset;


public class PieChartDemo1 extends ApplicationFrame {

    
    public PieChartDemo1(String title) {
        super(title);
        setContentPane(createDemoPanel());
    }

    
    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", new Double(43.2));
        dataset.setValue("Two", new Double(10.0));
        dataset.setValue("Three", new Double(27.5));
        dataset.setValue("Four", new Double(17.5));
        dataset.setValue("Five", new Double(11.0));
        dataset.setValue("Six", new Double(19.4));
        return dataset;
    }

    
    private static JFreeChart createChart(PieDataset dataset) {

        JFreeChart chart = ChartFactory.createPieChart("Pie Chart Demo 1",  
                dataset, true);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setNoDataMessage("No data available");
        return chart;

    }

    
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    
    public static void main(String[] args) {

        
        
        
        
        
        
        

        PieChartDemo1 demo = new PieChartDemo1("Pie Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
