

package org.jfree.experimental.chart.swt.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;


public class SWTBarChartDemo1 {

    
    private static CategoryDataset createDataset() {

        
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";

        
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";

        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);

        return dataset;

    }

    
    private static JFreeChart createChart(CategoryDataset dataset) {

        
        JFreeChart chart = ChartFactory.createBarChart(
            "SWTBarChartDemo1",       
            "Category",               
            "Value",                  
            dataset,                  
            PlotOrientation.VERTICAL, 
            true,                     
            true,                     
            false                     
        );

        

        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        
        
        
        renderer.setBarPainter(new StandardBarPainter());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        

        return chart;

    }

    
    public static void main(String[] args) {
        JFreeChart chart = createChart(createDataset());
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setSize(600, 300);
        shell.setLayout(new FillLayout());
        shell.setText("Test for jfreechart running with SWT");
        ChartComposite frame = new ChartComposite(shell, SWT.NONE, chart,
                true);
        frame.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

}

