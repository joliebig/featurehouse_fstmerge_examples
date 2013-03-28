

package org.jfree.chart;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import java.io.Serializable;
import org.jfree.chart.plot.PiePlot;
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
        else if (plot instanceof PiePlot) {
            PiePlot pp = (PiePlot) plot;
            pp.handleMouseWheelRotation(e.getWheelRotation());
        }
    }

    
    private void handleZoomable(Zoomable zoomable, MouseWheelEvent e) {
        
        ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
        PlotRenderingInfo pinfo = info.getPlotInfo();
        Point2D p = this.chartPanel.translateScreenToJava2D(e.getPoint());
        if (!pinfo.getDataArea().contains(p)) {
            return;
        }

        Plot plot = (Plot) zoomable;
        
        boolean notifyState = plot.isNotify();
        plot.setNotify(false);
        int clicks = e.getWheelRotation();
        double zf = 1.0 + this.zoomFactor;
        if (clicks < 0) {
            zf = 1.0 / zf;
        }
        if (chartPanel.isDomainZoomable()) {
            zoomable.zoomDomainAxes(zf, pinfo, p, true);
        }
        if (chartPanel.isRangeZoomable()) {
            zoomable.zoomRangeAxes(zf, pinfo, p, true);
        }
        plot.setNotify(notifyState);  
    }

}
