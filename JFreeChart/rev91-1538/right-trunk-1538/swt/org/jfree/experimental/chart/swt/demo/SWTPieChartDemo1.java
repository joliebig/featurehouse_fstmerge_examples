

package org.jfree.experimental.chart.swt.demo;

import java.awt.Font;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;


public class SWTPieChartDemo1 {

    
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

        JFreeChart chart = ChartFactory.createPieChart(
            "Pie Chart Demo 1",  
            dataset,             
            true,               
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
    }

    
    public static void main( String[] args ) {
        final JFreeChart chart = createChart(createDataset());
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setSize(600, 400);
        shell.setLayout(new FillLayout());
        shell.setText("Test for jfreechart running with SWT");
        final ChartComposite frame = new ChartComposite(shell, SWT.NONE, chart, true);
        
        frame.addChartMouseListener(new ChartMouseListener() {
              String slice;
            public void chartMouseClicked(ChartMouseEvent event) {
                PieSectionEntity entity = (PieSectionEntity) event.getEntity();
                if (entity != null) {
                    String slice = (String) entity.getSectionKey();
                    PiePlot plot = (PiePlot) chart.getPlot();
                    if (this.slice != null) {
                        plot.setExplodePercent(this.slice, 0.0);
                    }
                    if (slice == this.slice) {
                        this.slice = null;
                    } else {
                        plot.setExplodePercent(slice, 0.25);
                        this.slice = slice;
                    }
                }
            }

            public void chartMouseMoved(ChartMouseEvent event) {}
        });

        frame.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

}

