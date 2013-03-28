

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;


public class StandardChartTheme implements ChartTheme, Cloneable,
        PublicCloneable, Serializable {

    
    private String name;

    
    private Font extraLargeFont;

    
    private Font largeFont;

    
    private Font regularFont;

    
    private Font smallFont;

    
    private transient Paint titlePaint;

    
    private transient Paint subtitlePaint;

    
    private transient Paint chartBackgroundPaint;

    
    private transient Paint legendBackgroundPaint;

    
    private transient Paint legendItemPaint;

    
    private DrawingSupplier drawingSupplier;

    
    private transient Paint plotBackgroundPaint;

    
    private transient Paint plotOutlinePaint;

    
    private PieLabelLinkStyle labelLinkStyle;

    
    private transient Paint labelLinkPaint;

    
    private transient Paint domainGridlinePaint;

    
    private transient Paint rangeGridlinePaint;

    
    private transient Paint crosshairPaint;

    
    private RectangleInsets axisOffset;

    
    private transient Paint axisLabelPaint;

    
    private transient Paint tickLabelPaint;

    
    private transient Paint itemLabelPaint;

    
    private boolean shadowVisible;

    
    private transient Paint shadowPaint;

    
    private BarPainter barPainter;

    
    private XYBarPainter xyBarPainter;

    
    private transient Paint thermometerPaint;

    
    private transient Paint wallPaint;

    
    private transient Paint errorIndicatorPaint;

    
    private transient Paint gridBandPaint = SymbolAxis.DEFAULT_GRID_BAND_PAINT;

    
    private transient Paint gridBandAlternatePaint
            = SymbolAxis.DEFAULT_GRID_BAND_ALTERNATE_PAINT;

    
    public static ChartTheme createJFreeTheme() {
        return new StandardChartTheme("JFree");
    }

    
    public static ChartTheme createDarknessTheme() {
        StandardChartTheme theme = new StandardChartTheme("Darkness");
        theme.titlePaint = Color.white;
        theme.subtitlePaint = Color.white;
        theme.legendBackgroundPaint = Color.black;
        theme.legendItemPaint = Color.white;
        theme.chartBackgroundPaint = Color.black;
        theme.plotBackgroundPaint = Color.black;
        theme.plotOutlinePaint = Color.yellow;
        theme.crosshairPaint = Color.red;
        theme.labelLinkPaint = Color.lightGray;
        theme.tickLabelPaint = Color.white;
        theme.axisLabelPaint = Color.white;
        theme.shadowPaint = Color.darkGray;
        theme.itemLabelPaint = Color.white;
        theme.drawingSupplier = new DefaultDrawingSupplier(
                new Paint[] { Color.decode("0xFFFF00"),
                        Color.decode("0x0036CC"), Color.decode("0xFF0000"),
                        Color.decode("0xFFFF7F"), Color.decode("0x6681CC"),
                        Color.decode("0xFF7F7F"), Color.decode("0xFFFFBF"),
                        Color.decode("0x99A6CC"), Color.decode("0xFFBFBF"),
                        Color.decode("0xA9A938"), Color.decode("0x2D4587")},
                new Paint[] { Color.decode("0xFFFF00"),
                        Color.decode("0x0036CC")},
                new Stroke[] { new BasicStroke(2.0f)},
                new Stroke[] { new BasicStroke(0.5f)},
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        theme.wallPaint = Color.darkGray;
        theme.errorIndicatorPaint = Color.lightGray;
        theme.gridBandPaint = new Color(255, 255, 255, 20);
        theme.gridBandAlternatePaint = new Color(255, 255, 255, 40);
        return theme;
    }
    
    public static ChartTheme createLegacyTheme() {
        StandardChartTheme theme = new StandardChartTheme("Legacy") {
            public void apply(JFreeChart chart) {
                
            }
        };
        return theme;
    }

    
    public StandardChartTheme(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null 'name' argument.");
        }
        this.name = name;
        this.extraLargeFont = new Font("Tahoma", Font.BOLD, 20);
        this.largeFont = new Font("Tahoma", Font.BOLD, 14);
        this.regularFont = new Font("Tahoma", Font.PLAIN, 12);
        this.smallFont = new Font("Tahoma", Font.PLAIN, 10);
        this.titlePaint = Color.black;
        this.subtitlePaint = Color.black;
        this.legendBackgroundPaint = Color.white;
        this.legendItemPaint = Color.darkGray;
        this.chartBackgroundPaint = Color.white;
        this.drawingSupplier = new DefaultDrawingSupplier();
        this.plotBackgroundPaint = Color.lightGray;
        this.plotOutlinePaint = Color.black;
        this.labelLinkPaint = Color.black;
        this.labelLinkStyle = PieLabelLinkStyle.CUBIC_CURVE;
        this.axisOffset = new RectangleInsets(4, 4, 4, 4);
        this.domainGridlinePaint = Color.white;
        this.rangeGridlinePaint = Color.white;
        this.crosshairPaint = Color.blue;
        this.axisLabelPaint = Color.darkGray;
        this.tickLabelPaint = Color.darkGray;
        this.barPainter = new GradientBarPainter();
        this.xyBarPainter = new GradientXYBarPainter();
        this.shadowVisible = true;
        this.shadowPaint = Color.gray;
        this.itemLabelPaint = Color.black;
        this.thermometerPaint = Color.white;
        this.wallPaint = BarRenderer3D.DEFAULT_WALL_PAINT;
        this.errorIndicatorPaint = Color.black;
    }

    
    public Font getExtraLargeFont() {
        return this.extraLargeFont;
    }

    
    public void setExtraLargeFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.extraLargeFont = font;
    }

    
    public Font getLargeFont() {
        return this.largeFont;
    }

    
    public void setLargeFont(Font font) {
        this.largeFont = font;
    }

    
    public Font getRegularFont() {
        return this.regularFont;
    }

    
    public void setRegularFont(Font font) {
        this.regularFont = font;
    }

    
    public Paint getTitlePaint() {
        return this.titlePaint;
    }

    
    public void setTitlePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.titlePaint = paint;
    }

    
    public Paint getSubtitlePaint() {
        return this.subtitlePaint;
    }

    
    public void setSubtitlePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.subtitlePaint = paint;
    }

    
    public Paint getChartBackgroundPaint() {
        return this.chartBackgroundPaint;
    }

    
    public void setChartBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.chartBackgroundPaint = paint;
    }

    
    public Paint getLegendBackgroundPaint() {
        return this.legendBackgroundPaint;
    }

    
    public void setLegendBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.legendBackgroundPaint = paint;
    }

    
    public Paint getLegendItemPaint() {
        return this.legendItemPaint;
    }

    
    public void setLegendItemPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.legendItemPaint = paint;
    }

    
    public Paint getPlotBackgroundPaint() {
        return this.plotBackgroundPaint;
    }

    
    public void setPlotBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.plotBackgroundPaint = paint;
    }

    
    public Paint getPlotOutlinePaint() {
        return this.plotOutlinePaint;
    }

    
    public void setPlotOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.plotOutlinePaint = paint;
    }

    
    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    
    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Null 'style' argument.");
        }
        this.labelLinkStyle = style;
    }

    
    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    
    public void setLabelLinkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelLinkPaint = paint;
    }

    
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    
    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
    }

    
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    
    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
    }

    
    public Paint getCrosshairPaint() {
        return this.crosshairPaint;
    }

    
    public void setCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.crosshairPaint = paint;
    }

    
    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    
    public void setAxisOffset(RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.axisOffset = offset;
    }

    
    public Paint getAxisLabelPaint() {
        return this.axisLabelPaint;
    }

    
    public void setAxisLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.axisLabelPaint = paint;
    }

    
    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    
    public void setTickLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.tickLabelPaint = paint;
    }

    
    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;
    }

    
    public void setItemLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.itemLabelPaint = paint;
    }

    
    public boolean isShadowVisible() {
        return this.shadowVisible;
    }

    
    public void setShadowVisible(boolean visible) {
        this.shadowVisible = visible;
    }

    
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    
    public void setShadowPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.shadowPaint = paint;
    }

    
    public BarPainter getBarPainter() {
        return this.barPainter;
    }

    
    public void setBarPainter(BarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        this.barPainter = painter;
    }

    
    public XYBarPainter getXYBarPainter() {
        return this.xyBarPainter;
    }

    
    public void setXYBarPainter(XYBarPainter painter) {
        if (painter == null) {
            throw new IllegalArgumentException("Null 'painter' argument.");
        }
        this.xyBarPainter = painter;
    }

    
    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    
    public void setThermometerPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.thermometerPaint = paint;
    }

    
    public Paint getWallPaint() {
        return this.wallPaint;
    }

    
    public void setWallPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.wallPaint = paint;
    }

    
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }

    
    public void setErrorIndicatorPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.errorIndicatorPaint = paint;
    }

    
    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    
    public void setGridBandPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandPaint = paint;
    }

    
    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    
    public void setGridBandAlternatePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.gridBandAlternatePaint = paint;
    }

    
    public String getName() {
        return this.name;
    }

    
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        if (this.drawingSupplier instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.drawingSupplier;
              try {
                result = (DrawingSupplier) pc.clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    
    public void setDrawingSupplier(DrawingSupplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Null 'supplier' argument.");
        }
        this.drawingSupplier = supplier;
    }

    
    public void apply(JFreeChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");
        }
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(this.extraLargeFont);
            title.setPaint(this.titlePaint);
        }

        int subtitleCount = chart.getSubtitleCount();
        for (int i = 0; i < subtitleCount; i++) {
            applyToTitle(chart.getSubtitle(i));
        }

        chart.setBackgroundPaint(this.chartBackgroundPaint);

        
        Plot plot = chart.getPlot();
        if (plot != null) {
            applyToPlot(plot);
        }
    }

    
    protected void applyToTitle(Title title) {
        if (title instanceof TextTitle) {
            TextTitle tt = (TextTitle) title;
            tt.setFont(this.largeFont);
            tt.setPaint(this.subtitlePaint);
        }
        else if (title instanceof LegendTitle) {
            LegendTitle lt = (LegendTitle) title;
            if (lt.getBackgroundPaint() != null) {
                lt.setBackgroundPaint(this.legendBackgroundPaint);
            }
            lt.setItemFont(this.regularFont);
            lt.setItemPaint(this.legendItemPaint);
            if (lt.getWrapper() != null) {
                applyToBlockContainer(lt.getWrapper());
            }
        }
        else if (title instanceof PaintScaleLegend) {
            PaintScaleLegend psl = (PaintScaleLegend) title;
            psl.setBackgroundPaint(this.legendBackgroundPaint);
            ValueAxis axis = psl.getAxis();
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }
        else if (title instanceof CompositeTitle) {
            CompositeTitle ct = (CompositeTitle) title;
            BlockContainer bc = ct.getContainer();
            List blocks = bc.getBlocks();
            Iterator iterator = blocks.iterator();
            while (iterator.hasNext()) {
                Block b = (Block) iterator.next();
                if (b instanceof Title) {
                    applyToTitle((Title) b);
                }
            }
        }
    }

    
    protected void applyToBlockContainer(BlockContainer bc) {
        Iterator iterator = bc.getBlocks().iterator();
        while (iterator.hasNext()) {
            Block b = (Block) iterator.next();
            applyToBlock(b);
        }
    }

    
    protected void applyToBlock(Block b) {
        if (b instanceof Title) {
            applyToTitle((Title) b);
        }
        else if (b instanceof LabelBlock) {
            LabelBlock lb = (LabelBlock) b;
            lb.setFont(this.regularFont);
            lb.setPaint(this.legendItemPaint);
        }
    }

    
    protected void applyToPlot(Plot plot) {
        if (plot == null) {
            throw new IllegalArgumentException("Null 'plot' argument.");
        }
        if (plot.getDrawingSupplier() != null) {
            plot.setDrawingSupplier(getDrawingSupplier());
        }
        if (plot.getBackgroundPaint() != null) {
            plot.setBackgroundPaint(this.plotBackgroundPaint);
        }
        plot.setOutlinePaint(this.plotOutlinePaint);

        
        
        
        
        if (plot instanceof PiePlot) {
            applyToPiePlot((PiePlot) plot);
        }
        else if (plot instanceof MultiplePiePlot) {
            applyToMultiplePiePlot((MultiplePiePlot) plot);
        }
        else if (plot instanceof CategoryPlot) {
            applyToCategoryPlot((CategoryPlot) plot);
        }
        else if (plot instanceof XYPlot) {
            applyToXYPlot((XYPlot) plot);
        }
        else if (plot instanceof FastScatterPlot) {
            applyToFastScatterPlot((FastScatterPlot) plot);
        }
        else if (plot instanceof MeterPlot) {
            applyToMeterPlot((MeterPlot) plot);
        }
        else if (plot instanceof ThermometerPlot) {
            applyToThermometerPlot((ThermometerPlot) plot);
        }
        else if (plot instanceof SpiderWebPlot) {
            applyToSpiderWebPlot((SpiderWebPlot) plot);
        }
        else if (plot instanceof PolarPlot) {
            applyToPolarPlot((PolarPlot) plot);
        }
    }

    
    protected void applyToPiePlot(PiePlot plot) {
        plot.setLabelLinkPaint(this.labelLinkPaint);
        plot.setLabelLinkStyle(this.labelLinkStyle);
        plot.setLabelFont(this.regularFont);

        
        
        if (plot.getAutoPopulateSectionPaint()) {
            plot.clearSectionPaints(false);
        }
        if (plot.getAutoPopulateSectionOutlinePaint()) {
            plot.clearSectionOutlinePaints(false);
        }
        if (plot.getAutoPopulateSectionOutlineStroke()) {
            plot.clearSectionOutlineStrokes(false);
        }
    }

    
    protected void applyToMultiplePiePlot(MultiplePiePlot plot) {
        apply(plot.getPieChart());
    }

    
    protected void applyToCategoryPlot(CategoryPlot plot) {
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);

        
        int domainAxisCount = plot.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            CategoryAxis axis = (CategoryAxis) plot.getDomainAxis(i);
            if (axis != null) {
                applyToCategoryAxis(axis);
            }
        }

        
        int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        
        int rendererCount = plot.getRendererCount();
        for (int i = 0; i < rendererCount; i++) {
            CategoryItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToCategoryItemRenderer(r);
            }
        }

        if (plot instanceof CombinedDomainCategoryPlot) {
            CombinedDomainCategoryPlot cp = (CombinedDomainCategoryPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                CategoryPlot subplot = (CategoryPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeCategoryPlot) {
            CombinedRangeCategoryPlot cp = (CombinedRangeCategoryPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                CategoryPlot subplot = (CategoryPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
    }

    
    protected void applyToXYPlot(XYPlot plot) {
        plot.setAxisOffset(this.axisOffset);
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        plot.setDomainCrosshairPaint(this.crosshairPaint);
        plot.setRangeCrosshairPaint(this.crosshairPaint);
        
        int domainAxisCount = plot.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getDomainAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        
        int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = (ValueAxis) plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        
        int rendererCount = plot.getRendererCount();
        for (int i = 0; i < rendererCount; i++) {
            XYItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToXYItemRenderer(r);
            }
        }

        
        Iterator iter = plot.getAnnotations().iterator();
        while (iter.hasNext()) {
            XYAnnotation a = (XYAnnotation) iter.next();
            applyToXYAnnotation(a);
        }

        if (plot instanceof CombinedDomainXYPlot) {
            CombinedDomainXYPlot cp = (CombinedDomainXYPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
        if (plot instanceof CombinedRangeXYPlot) {
            CombinedRangeXYPlot cp = (CombinedRangeXYPlot) plot;
            Iterator iterator = cp.getSubplots().iterator();
            while (iterator.hasNext()) {
                XYPlot subplot = (XYPlot) iterator.next();
                if (subplot != null) {
                    applyToPlot(subplot);
                }
            }
        }
    }

    
    protected void applyToFastScatterPlot(FastScatterPlot plot) {
        plot.setDomainGridlinePaint(this.domainGridlinePaint);
        plot.setRangeGridlinePaint(this.rangeGridlinePaint);
        ValueAxis xAxis = plot.getDomainAxis();
        if (xAxis != null) {
            applyToValueAxis(xAxis);
        }
        ValueAxis yAxis = plot.getRangeAxis();
        if (yAxis != null) {
            applyToValueAxis(yAxis);
        }

    }

    
    protected void applyToPolarPlot(PolarPlot plot) {
        plot.setAngleLabelFont(this.regularFont);
        plot.setAngleLabelPaint(this.tickLabelPaint);
        plot.setAngleGridlinePaint(this.domainGridlinePaint);
        plot.setRadiusGridlinePaint(this.rangeGridlinePaint);
        ValueAxis axis = plot.getAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    
    protected void applyToSpiderWebPlot(SpiderWebPlot plot) {
        plot.setLabelFont(this.regularFont);
        plot.setLabelPaint(this.axisLabelPaint);
        plot.setAxisLinePaint(this.axisLabelPaint);
    }

    
    protected void applyToMeterPlot(MeterPlot plot) {
        plot.setDialBackgroundPaint(this.plotBackgroundPaint);
        plot.setValueFont(this.largeFont);
        plot.setValuePaint(this.axisLabelPaint);
        plot.setDialOutlinePaint(this.plotOutlinePaint);
        plot.setNeedlePaint(this.thermometerPaint);
        plot.setTickLabelFont(this.regularFont);
        plot.setTickLabelPaint(this.tickLabelPaint);
    }

    
    protected void applyToThermometerPlot(ThermometerPlot plot) {
        plot.setValueFont(this.largeFont);
        plot.setThermometerPaint(this.thermometerPaint);
        ValueAxis axis = plot.getRangeAxis();
        if (axis != null) {
            applyToValueAxis(axis);
        }
    }

    
    protected void applyToCategoryAxis(CategoryAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SubCategoryAxis) {
            SubCategoryAxis sca = (SubCategoryAxis) axis;
            sca.setSubLabelFont(this.regularFont);
            sca.setSubLabelPaint(this.tickLabelPaint);
        }
    }

    
    protected void applyToValueAxis(ValueAxis axis) {
        axis.setLabelFont(this.largeFont);
        axis.setLabelPaint(this.axisLabelPaint);
        axis.setTickLabelFont(this.regularFont);
        axis.setTickLabelPaint(this.tickLabelPaint);
        if (axis instanceof SymbolAxis) {
            applyToSymbolAxis((SymbolAxis) axis);
        }
        if (axis instanceof PeriodAxis) {
            applyToPeriodAxis((PeriodAxis) axis);
        }
    }

    
    protected void applyToSymbolAxis(SymbolAxis axis) {
        axis.setGridBandPaint(this.gridBandPaint);
        axis.setGridBandAlternatePaint(this.gridBandAlternatePaint);
    }

    
    protected void applyToPeriodAxis(PeriodAxis axis) {
        PeriodAxisLabelInfo[] info = axis.getLabelInfo();
        for (int i = 0; i < info.length; i++) {
            PeriodAxisLabelInfo e = info[i];
            PeriodAxisLabelInfo n = new PeriodAxisLabelInfo(e.getPeriodClass(),
                    e.getDateFormat(), e.getPadding(), this.regularFont,
                    this.tickLabelPaint, e.getDrawDividers(),
                    e.getDividerStroke(), e.getDividerPaint());
            info[i] = n;
        }
        axis.setLabelInfo(info);
    }

    
    protected void applyToAbstractRenderer(AbstractRenderer renderer) {
        if (renderer.getAutoPopulateSeriesPaint()) {
            renderer.clearSeriesPaints(false);
        }
        if (renderer.getAutoPopulateSeriesStroke()) {
            renderer.clearSeriesStrokes(false);
        }
    }

    
    protected void applyToCategoryItemRenderer(CategoryItemRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Null 'renderer' argument.");
        }

        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }

        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);

        

        
        if (renderer instanceof BarRenderer) {
            BarRenderer br = (BarRenderer) renderer;
            br.setBarPainter(this.barPainter);
            br.setShadowVisible(this.shadowVisible);
            br.setShadowPaint(this.shadowPaint);
        }

        
        if (renderer instanceof BarRenderer3D) {
            BarRenderer3D br3d = (BarRenderer3D) renderer;
            br3d.setWallPaint(this.wallPaint);
        }

        
        if (renderer instanceof LineRenderer3D) {
            LineRenderer3D lr3d = (LineRenderer3D) renderer;
            lr3d.setWallPaint(this.wallPaint);
        }

        
        if (renderer instanceof StatisticalBarRenderer) {
            StatisticalBarRenderer sbr = (StatisticalBarRenderer) renderer;
            sbr.setErrorIndicatorPaint(this.errorIndicatorPaint);
        }

        
        if (renderer instanceof MinMaxCategoryRenderer) {
            MinMaxCategoryRenderer mmcr = (MinMaxCategoryRenderer) renderer;
            mmcr.setGroupPaint(this.errorIndicatorPaint);
        }
    }

    
    protected void applyToXYItemRenderer(XYItemRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Null 'renderer' argument.");
        }
        if (renderer instanceof AbstractRenderer) {
            applyToAbstractRenderer((AbstractRenderer) renderer);
        }
        renderer.setBaseItemLabelFont(this.regularFont);
        renderer.setBaseItemLabelPaint(this.itemLabelPaint);
        if (renderer instanceof XYBarRenderer) {
            XYBarRenderer br = (XYBarRenderer) renderer;
            br.setBarPainter(this.xyBarPainter);
            br.setShadowVisible(this.shadowVisible);
        }
    }

    
    protected void applyToXYAnnotation(XYAnnotation annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        if (annotation instanceof XYTextAnnotation) {
            XYTextAnnotation xyta = (XYTextAnnotation) annotation;
            xyta.setFont(this.smallFont);
            xyta.setPaint(this.itemLabelPaint);
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardChartTheme)) {
            return false;
        }
        StandardChartTheme that = (StandardChartTheme) obj;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.extraLargeFont.equals(that.extraLargeFont)) {
            return false;
        }
        if (!this.largeFont.equals(that.largeFont)) {
            return false;
        }
        if (!this.regularFont.equals(that.regularFont)) {
            return false;
        }
        if (!this.smallFont.equals(that.smallFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.titlePaint, that.titlePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.subtitlePaint, that.subtitlePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.chartBackgroundPaint,
                that.chartBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.legendBackgroundPaint,
                that.legendBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.legendItemPaint, that.legendItemPaint)) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;
        }
        if (!PaintUtilities.equal(this.plotBackgroundPaint,
                that.plotBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.plotOutlinePaint,
                that.plotOutlinePaint)) {
            return false;
        }
        if (!this.labelLinkStyle.equals(that.labelLinkStyle)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint,
                that.domainGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint,
                that.rangeGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.crosshairPaint, that.crosshairPaint)) {
            return false;
        }
        if (!this.axisOffset.equals(that.axisOffset)) {
            return false;
        }
        if (!PaintUtilities.equal(this.axisLabelPaint, that.axisLabelPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        if (this.shadowVisible != that.shadowVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (!this.barPainter.equals(that.barPainter)) {
            return false;
        }
        if (!this.xyBarPainter.equals(that.xyBarPainter)) {
            return false;
        }
        if (!PaintUtilities.equal(this.thermometerPaint,
                that.thermometerPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.wallPaint, that.wallPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.errorIndicatorPaint,
                that.errorIndicatorPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.gridBandPaint, that.gridBandPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.gridBandAlternatePaint,
                that.gridBandAlternatePaint)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.titlePaint, stream);
        SerialUtilities.writePaint(this.subtitlePaint, stream);
        SerialUtilities.writePaint(this.chartBackgroundPaint, stream);
        SerialUtilities.writePaint(this.legendBackgroundPaint, stream);
        SerialUtilities.writePaint(this.legendItemPaint, stream);
        SerialUtilities.writePaint(this.plotBackgroundPaint, stream);
        SerialUtilities.writePaint(this.plotOutlinePaint, stream);
        SerialUtilities.writePaint(this.labelLinkPaint, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writePaint(this.crosshairPaint, stream);
        SerialUtilities.writePaint(this.axisLabelPaint, stream);
        SerialUtilities.writePaint(this.tickLabelPaint, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
        SerialUtilities.writePaint(this.thermometerPaint, stream);
        SerialUtilities.writePaint(this.wallPaint, stream);
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
        SerialUtilities.writePaint(this.gridBandPaint, stream);
        SerialUtilities.writePaint(this.gridBandAlternatePaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.titlePaint = SerialUtilities.readPaint(stream);
        this.subtitlePaint = SerialUtilities.readPaint(stream);
        this.chartBackgroundPaint = SerialUtilities.readPaint(stream);
        this.legendBackgroundPaint = SerialUtilities.readPaint(stream);
        this.legendItemPaint = SerialUtilities.readPaint(stream);
        this.plotBackgroundPaint = SerialUtilities.readPaint(stream);
        this.plotOutlinePaint = SerialUtilities.readPaint(stream);
        this.labelLinkPaint = SerialUtilities.readPaint(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.crosshairPaint = SerialUtilities.readPaint(stream);
        this.axisLabelPaint = SerialUtilities.readPaint(stream);
        this.tickLabelPaint = SerialUtilities.readPaint(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
        this.thermometerPaint = SerialUtilities.readPaint(stream);
        this.wallPaint = SerialUtilities.readPaint(stream);
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
        this.gridBandPaint = SerialUtilities.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtilities.readPaint(stream);
    }

}
