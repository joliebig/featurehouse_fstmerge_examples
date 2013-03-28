

package org.jfree.chart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;


public class ChartFrame extends JFrame {

    
    private ChartPanel chartPanel;

    
    public ChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    
    public ChartFrame(String title, JFreeChart chart, boolean scrollPane) {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.chartPanel = new ChartPanel(chart);
        if (scrollPane) {
            setContentPane(new JScrollPane(this.chartPanel));
        }
        else {
            setContentPane(this.chartPanel);
        }
    }

    
    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

}
