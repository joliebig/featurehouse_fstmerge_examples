

package org.jfree.experimental.chart.swt.editor;

import java.awt.Stroke;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.experimental.swt.SWTUtils;


class SWTPlotEditor extends Composite {
    
    
    private SWTAxisEditor domainAxisPropertyPanel;

    
    private SWTAxisEditor rangeAxisPropertyPanel;

    private SWTPlotAppearanceEditor plotAppearance;
    
    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    public SWTPlotEditor(Composite parent, int style, Plot plot) {
        super(parent, style);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 4;
        this.setLayout(layout);

        Group plotType = new Group(this, SWT.NONE);
        FillLayout plotTypeLayout = new FillLayout();
        plotTypeLayout.marginHeight = plotTypeLayout.marginWidth = 4;
        plotType.setLayout(plotTypeLayout);
        plotType.setText(plot.getPlotType() + localizationResources.getString(
                ":"));
        
        TabFolder tabs = new TabFolder(plotType, SWT.NONE);
        
        
        TabItem item1 = new TabItem(tabs, SWT.NONE);
        item1.setText(localizationResources.getString("Domain_Axis"));
        Axis domainAxis = null;
        if (plot instanceof CategoryPlot) {
            domainAxis = ((CategoryPlot) plot).getDomainAxis();
        }
        else if (plot instanceof XYPlot) {
            domainAxis = ((XYPlot) plot).getDomainAxis();
        }
        this.domainAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, 
                SWT.NONE, domainAxis);
        item1.setControl(this.domainAxisPropertyPanel);
        
        
        TabItem item2 = new TabItem(tabs, SWT.NONE);
        item2.setText(localizationResources.getString("Range_Axis"));
        Axis rangeAxis = null;
        if (plot instanceof CategoryPlot) {
            rangeAxis = ((CategoryPlot) plot).getRangeAxis();
        }
        else if (plot instanceof XYPlot) {
            rangeAxis = ((XYPlot) plot).getRangeAxis();
        }
        this.rangeAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, SWT.NONE,
                rangeAxis);
        item2.setControl(this.rangeAxisPropertyPanel);
        
        
        TabItem item3 = new TabItem(tabs, SWT.NONE);
        item3.setText(localizationResources.getString("Appearance"));
        this.plotAppearance = new SWTPlotAppearanceEditor(tabs, SWT.NONE, plot);
        item3.setControl(this.plotAppearance);
    }

    
    public Color getBackgroundPaint() {
        return this.plotAppearance.getBackGroundPaint();
    }

    
    public Color getOutlinePaint() {
        return this.plotAppearance.getOutlinePaint();
    }

    
    public Stroke getOutlineStroke() {
        return this.plotAppearance.getStroke();
    }


    
    public void updatePlotProperties(Plot plot) {
        
        plot.setBackgroundPaint(SWTUtils.toAwtColor(getBackgroundPaint()));
        plot.setOutlinePaint(SWTUtils.toAwtColor(getOutlinePaint()));
        plot.setOutlineStroke(getOutlineStroke());
        
        
        if (this.domainAxisPropertyPanel != null) {
            Axis domainAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                domainAxis = p.getDomainAxis();
            }
            if (domainAxis != null)
                this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
        }
        if (this.rangeAxisPropertyPanel != null) {
            Axis rangeAxis = null;
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                rangeAxis = p.getRangeAxis();
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = ( XYPlot ) plot;
                rangeAxis = p.getRangeAxis();
            }
            if (rangeAxis != null)
                this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
        }
        if (this.plotAppearance.getPlotOrientation() != null) {
            if (plot instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) plot;
                p.setOrientation(this.plotAppearance.getPlotOrientation() );
            }
            else if (plot instanceof XYPlot) {
                XYPlot p = (XYPlot) plot;
                p.setOrientation(this.plotAppearance.getPlotOrientation());
            }
        }
    }
}
