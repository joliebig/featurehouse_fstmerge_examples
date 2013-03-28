

package org.jfree.chart;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import java.io.Serializable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;


class MouseWheelHandler implements MouseWheelListener, Serializable {

    
    private ChartPanel chartPanel;

    
    double zoomFactor;

    
    public MouseWheelHandler(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.zoomFactor = 0.10;
        this.chartPanel.addMouseWheelListener(this);
    }

    
    public double getZoomFactor() {
        return this.zoomFactor;
    }

    
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    
    public void mouseWheelMoved(MouseWheelEvent e) {
        JFreeChart chart = this.chartPanel.getChart();
        if (chart == null) {
            return;
        }
        Plot plot = chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable zoomable = (Zoomable) plot;
            handleZoomable(zoomable, e);
        }
        
        
        
    }

    
    private void handleZoomable(Zoomable zoomable, MouseWheelEvent e) {
        Plot plot = (Plot) zoomable;
        ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
        PlotRenderingInfo pinfo = info.getPlotInfo();
        Point2D p = this.chartPanel.translateScreenToJava2D(e.getPoint());
        if (!pinfo.getDataArea().contains(p)) {
            return;
        }
        int clicks = e.getWheelRotation();
        int direction = 0;
        if (clicks < 0) {
            direction = -1;
        }
        else if (clicks > 0) {
            direction = 1;
        }

        boolean old = plot.isNotify();

        
        plot.setNotify(false);
        double increment = 1.0 + this.zoomFactor;
        if (direction > 0) {
            zoomable.zoomDomainAxes(increment, pinfo, p, true);
            zoomable.zoomRangeAxes(increment, pinfo, p, true);
        }
        else if (direction < 0) {
            zoomable.zoomDomainAxes(1.0 / increment, pinfo, p, true);
            zoomable.zoomRangeAxes(1.0 / increment, pinfo, p, true);
        }
        
        plot.setNotify(old);

    }

}
