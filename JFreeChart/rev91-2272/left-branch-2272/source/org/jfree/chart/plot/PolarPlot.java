

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import java.util.TreeMap;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable,
        RendererChangeListener, Cloneable, Serializable {

    
    private static final long serialVersionUID = 3794383185924179525L;

    
    private static final int DEFAULT_MARGIN = 20;
   
    
    private static final double ANNOTATION_MARGIN = 7.0;

    
    public static final double DEFAULT_ANGLE_TICK_UNIT_SIZE = 45.0;

    
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(
            0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0.0f, new float[]{2.0f, 2.0f}, 0.0f);

    
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.gray;

    
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.plot.LocalizationBundle");

    
    private List angleTicks;

    
    private ObjectList axes;

    
    private ObjectList axisLocations;

    
    private ObjectList datasets;

    
    private ObjectList renderers;

    
    private TickUnit angleTickUnit;

    
    private boolean angleLabelsVisible = true;

    
    private Font angleLabelFont = new Font("SansSerif", Font.PLAIN, 12);

    
    private transient Paint angleLabelPaint = Color.black;

    
    private boolean angleGridlinesVisible;

    
    private transient Stroke angleGridlineStroke;

    
    private transient Paint angleGridlinePaint;

    
    private boolean radiusGridlinesVisible;

    
    private transient Stroke radiusGridlineStroke;

    
    private transient Paint radiusGridlinePaint;

    
    private List cornerTextItems = new ArrayList();

    
    private int margin;
    
    
    private LegendItemCollection fixedLegendItems;

    
    private Map datasetToAxesMap;

    
    public PolarPlot() {
        this(null, null, null);
    }

   
    public PolarPlot(XYDataset dataset, ValueAxis radiusAxis,
                PolarItemRenderer renderer) {

        super();

        this.datasets = new ObjectList();
        this.datasets.set(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.angleTickUnit = new NumberTickUnit(DEFAULT_ANGLE_TICK_UNIT_SIZE);

        this.axes = new ObjectList();
        this.axes.set(0, radiusAxis);
        if (radiusAxis != null) {
            radiusAxis.setPlot(this);
            radiusAxis.addChangeListener(this);
        }

        this.datasetToAxesMap = new TreeMap();

        
        this.axisLocations = new ObjectList();
        this.axisLocations.set(0, PolarAxisLocation.EAST_ABOVE);
        this.axisLocations.set(1, PolarAxisLocation.NORTH_LEFT);
        this.axisLocations.set(2, PolarAxisLocation.WEST_BELOW);
        this.axisLocations.set(3, PolarAxisLocation.SOUTH_RIGHT);
        this.axisLocations.set(4, PolarAxisLocation.EAST_BELOW);
        this.axisLocations.set(5, PolarAxisLocation.NORTH_RIGHT);
        this.axisLocations.set(6, PolarAxisLocation.WEST_ABOVE);
        this.axisLocations.set(7, PolarAxisLocation.SOUTH_LEFT);
        
        this.renderers = new ObjectList();
        this.renderers.set(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }

        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.radiusGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.margin = DEFAULT_MARGIN;
    }

    
    public String getPlotType() {
       return PolarPlot.localizationResources.getString("Polar_Plot");
    }

    
    public ValueAxis getAxis() {
        return getAxis(0);
    }

    
    public ValueAxis getAxis(int index) {
        ValueAxis result = null;
        if (index < this.axes.size()) {
            result = (ValueAxis) this.axes.get(index);
        }
        return result;
    }

    
    public void setAxis(ValueAxis axis) {
        setAxis(0, axis);
    }

    
    public void setAxis(int index, ValueAxis axis) {
        setAxis(index, axis, true);
    }

    
    public void setAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.axes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public PolarAxisLocation getAxisLocation() {
        return getAxisLocation(0);
    }

    
    public PolarAxisLocation getAxisLocation(int index) {
        PolarAxisLocation result = null;
        if (index < this.axisLocations.size()) {
            result = (PolarAxisLocation) this.axisLocations.get(index);
        }
        return result;
    }

    
    public void setAxisLocation(PolarAxisLocation location) {
        
        setAxisLocation(0, location, true);
    }

    
    public void setAxisLocation(PolarAxisLocation location, boolean notify) {
        
        setAxisLocation(0, location, notify);
    }

    
    public void setAxisLocation(int index, PolarAxisLocation location) {
        
        setAxisLocation(index, location, true);
    }

    
    public void setAxisLocation(int index, PolarAxisLocation location,
            boolean notify) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        this.axisLocations.set(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public XYDataset getDataset() {
        return getDataset(0);
    }

    
    public XYDataset getDataset(int index) {
        XYDataset result = null;
        if (index < this.datasets.size()) {
            result = (XYDataset) this.datasets.get(index);
        }
        return result;
    }

    
    public void setDataset(XYDataset dataset) {
        setDataset(0, dataset);
    }

    
    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = getDataset(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }

    
    public int indexOf(XYDataset dataset) {
        int result = -1;
        for (int i = 0; i < this.datasets.size(); i++) {
            if (dataset == this.datasets.get(i)) {
                result = i;
                break;
            }
        }
        return result;
    }

    
    public PolarItemRenderer getRenderer() {
        return getRenderer(0);
    }

    
    public PolarItemRenderer getRenderer(int index) {
        PolarItemRenderer result = null;
        if (index < this.renderers.size()) {
            result = (PolarItemRenderer) this.renderers.get(index);
        }
        return result;
    }

    
    public void setRenderer(PolarItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    
    public void setRenderer(int index, PolarItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    
    public void setRenderer(int index, PolarItemRenderer renderer,
                            boolean notify) {
        PolarItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public TickUnit getAngleTickUnit() {
        return this.angleTickUnit;
    }

    
    public void setAngleTickUnit(TickUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Null 'unit' argument.");
        }
        this.angleTickUnit = unit;
        fireChangeEvent();
    }

    
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }

    
    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }

    
    public void setAngleLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.angleLabelFont = font;
        fireChangeEvent();
    }

    
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }

    
    public void setAngleLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.angleLabelPaint = paint;
        fireChangeEvent();
    }

    
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }

    
    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }

    
    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }

    
    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }

    
    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }

    
    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }

    
    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        fireChangeEvent();
    }
    
    
    public int getMargin() {
        return this.margin;
    }

    
    public void setMargin(int margin) {
        this.margin = margin;
        fireChangeEvent();
    }

    
    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    
    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
        fireChangeEvent();
    }

    
    public void addCornerTextItem(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.cornerTextItems.add(text);
        fireChangeEvent();
    }

    
    public void removeCornerTextItem(String text) {
        boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            fireChangeEvent();
        }
    }

    
    public void clearCornerTextItems() {
        if (this.cornerTextItems.size() > 0) {
            this.cornerTextItems.clear();
            fireChangeEvent();
        }
    }

    
    protected List refreshAngleTicks() {
        List ticks = new ArrayList();
        for (double currentTickVal = 0.0; currentTickVal < 360.0;
                currentTickVal += this.angleTickUnit.getSize()) {
            TextAnchor ta = TextAnchor.CENTER;
            if (currentTickVal == 0.0 || currentTickVal == 360.0) {
                ta = TextAnchor.BOTTOM_CENTER;
            }
            else if (currentTickVal > 0.0 && currentTickVal < 90.0) {
                ta = TextAnchor.BOTTOM_LEFT;
            }
            else if (currentTickVal == 90.0) {
                ta = TextAnchor.CENTER_LEFT;
            }
            else if (currentTickVal > 90.0 && currentTickVal < 180.0) {
                ta = TextAnchor.TOP_LEFT;
            }
            else if (currentTickVal == 180) {
                ta = TextAnchor.TOP_CENTER;
            }
            else if (currentTickVal > 180.0 && currentTickVal < 270.0) {
                ta = TextAnchor.TOP_RIGHT;
            }
            else if (currentTickVal == 270) {
                ta = TextAnchor.CENTER_RIGHT;
            }
            else if (currentTickVal > 270.0 && currentTickVal < 360.0) {
                ta = TextAnchor.BOTTOM_RIGHT;
            }

            NumberTick tick = new NumberTick(new Double(currentTickVal),
                this.angleTickUnit.valueToString(currentTickVal),
                ta, TextAnchor.CENTER, 0.0);
            ticks.add(tick);
        }
        return ticks;
    }

    
    public void mapDatasetToAxis(int index, int axisIndex) {
        List axisIndices = new java.util.ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToAxes(index, axisIndices);
    }

    
    public void mapDatasetToAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        Integer key = new Integer(index);
        this.datasetToAxesMap.put(key, new ArrayList(axisIndices));
        
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    
    private void checkAxisIndices(List indices) {
        
        
        
        if (indices == null) {
            return;  
        }
        int count = indices.size();
        if (count == 0) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        HashSet set = new HashSet();
        for (int i = 0; i < count; i++) {
            Object item = indices.get(i);
            if (!(item instanceof Integer)) {
                throw new IllegalArgumentException(
                        "Indices must be Integer instances.");
            }
            if (set.contains(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(item);
        }
    }

    
    public ValueAxis getAxisForDataset(int index) {
        ValueAxis valueAxis = null;
        List axisIndices = (List) this.datasetToAxesMap.get(
                new Integer(index));
        if (axisIndices != null) {
            
            Integer axisIndex = (Integer) axisIndices.get(0);
            valueAxis = getAxis(axisIndex.intValue());
        }
        else {
            valueAxis = getAxis(0);
        }
        return valueAxis;
    }
    
    
    public int getIndexOf(PolarItemRenderer renderer) {
        return this.renderers.indexOf(renderer);
    }

    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
            PlotState parentState, PlotRenderingInfo info) {

        
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        
        if (info != null) {
            info.setPlotArea(area);
        }

        
        RectangleInsets insets = getInsets();
        insets.trim(area);

        Rectangle2D dataArea = area;
        if (info != null) {
            info.setDataArea(dataArea);
        }

        
        drawBackground(g2, dataArea);
        int axisCount = this.axes.size();
        AxisState state = null;
        for (int i = 0; i < axisCount; i++) {
            ValueAxis axis = getAxis(i);
            if (axis != null) {
                PolarAxisLocation location
                        = (PolarAxisLocation) this.axisLocations.get(i);
                AxisState s = this.drawAxis(axis, location, g2, dataArea);
                if (i == 0) {
                    state = s;
                }
            }
        }

        
        
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));
        this.angleTicks = refreshAngleTicks();
        drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
        render(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
        drawCornerTextItems(g2, dataArea);
    }

    
    protected void drawCornerTextItems(Graphics2D g2, Rectangle2D area) {
        if (this.cornerTextItems.isEmpty()) {
            return;
        }

        g2.setColor(Color.black);
        double width = 0.0;
        double height = 0.0;
        for (Iterator it = this.cornerTextItems.iterator(); it.hasNext();) {
            String msg = (String) it.next();
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D bounds = TextUtilities.getTextBounds(msg, g2, fm);
            width = Math.max(width, bounds.getWidth());
            height += bounds.getHeight();
        }

        double xadj = ANNOTATION_MARGIN * 2.0;
        double yadj = ANNOTATION_MARGIN;
        width += xadj;
        height += yadj;

        double x = area.getMaxX() - width;
        double y = area.getMaxY() - height;
        g2.drawRect((int) x, (int) y, (int) width, (int) height);
        x += ANNOTATION_MARGIN;
        for (Iterator it = this.cornerTextItems.iterator(); it.hasNext();) {
            String msg = (String) it.next();
            Rectangle2D bounds = TextUtilities.getTextBounds(msg, g2,
                    g2.getFontMetrics());
            y += bounds.getHeight();
            g2.drawString(msg, (int) x, (int) y);
        }
    }

    
    protected AxisState drawAxis(ValueAxis axis, PolarAxisLocation location,
            Graphics2D g2, Rectangle2D plotArea) {

        double centerX = plotArea.getCenterX();
        double centerY = plotArea.getCenterY();
        double r = Math.min(plotArea.getWidth() / 2.0,
                plotArea.getHeight() / 2.0) - margin;
        double x = centerX - r;
        double y = centerY - r;

        Rectangle2D dataArea = null;
        AxisState result = null;
        if (location == PolarAxisLocation.NORTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea,
                    RectangleEdge.RIGHT, null);
        }
        else if (location == PolarAxisLocation.NORTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea,
                    RectangleEdge.LEFT, null);
        }
        else if (location == PolarAxisLocation.SOUTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea,
                    RectangleEdge.LEFT, null);
        }
        else if (location == PolarAxisLocation.SOUTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea,
                    RectangleEdge.RIGHT, null);
        }
        else if (location == PolarAxisLocation.EAST_ABOVE) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea,
                    RectangleEdge.TOP, null);
        }
        else if (location == PolarAxisLocation.EAST_BELOW) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea,
                    RectangleEdge.BOTTOM, null);
        }
        else if (location == PolarAxisLocation.WEST_ABOVE) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea,
                    RectangleEdge.TOP, null);
        }
        else if (location == PolarAxisLocation.WEST_BELOW) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea,
                    RectangleEdge.BOTTOM, null);
        }
       
        return result;
    }

    
    protected void render(Graphics2D g2, Rectangle2D dataArea,
            PlotRenderingInfo info) {

        
        
        boolean hasData = false;
        int datasetCount = this.datasets.size();
        for (int i = datasetCount - 1; i >= 0; i--) {
            XYDataset dataset = getDataset(i);
            if (dataset == null) {
                continue;
            }
            PolarItemRenderer renderer = getRenderer(i);
            if (renderer == null) {
                continue;
            }
            if (!DatasetUtilities.isEmptyOrNull(dataset)) {
                hasData = true;
                int seriesCount = dataset.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    renderer.drawSeries(g2, dataArea, info, this, dataset,
                            series);
                }
            }
        }
        if (!hasData) {
            drawNoDataMessage(g2, dataArea);
        }
    }

    
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea,
                                 List angularTicks, List radialTicks) {

        PolarItemRenderer renderer = getRenderer();
        
        if (renderer == null) {
            return;
        }

        
        if (isAngleGridlinesVisible()) {
            Stroke gridStroke = getAngleGridlineStroke();
            Paint gridPaint = getAngleGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                renderer.drawAngularGridLines(g2, this, angularTicks,
                        dataArea);
            }
        }

        
        if (isRadiusGridlinesVisible()) {
            Stroke gridStroke = getRadiusGridlineStroke();
            Paint gridPaint = getRadiusGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                renderer.drawRadialGridLines(g2, this, getAxis(),
                        radialTicks, dataArea);
            }
        }
    }

    
    public void zoom(double percent) {
        
        if (percent > 0.0) {
            double radius = getAxis().getUpperBound();
            double scaledRadius = radius * percent;
            getAxis().setUpperBound(scaledRadius);
            getAxis().setAutoRange(false);
        }
        else {
            getAxis().setAutoRange(true);
        }
    }

    
    public Range getDataRange(ValueAxis axis) {
        
        Range result = null;
        if (getDataset() != null) {
            result = Range.combine(result,
                    DatasetUtilities.findRangeBounds(getDataset()));
        }
        return result;
    }

    
    public void datasetChanged(DatasetChangeEvent event) {
        
        if (getAxis() != null) {
            getAxis().configure();
        }
        if (getParent() != null) {
            getParent().datasetChanged(event);
        }
        else {
            super.datasetChanged(event);
        }
    }

    
    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }

    
    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        int count = this.datasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
            XYDataset dataset = getDataset(datasetIndex);
            PolarItemRenderer renderer = getRenderer(datasetIndex);
            if (dataset != null && renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = renderer.getLegendItem(i);
                    result.add(item);
                }
            }
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        PolarPlot that = (PolarPlot) obj;
        if (!this.axes.equals(that.axes)) {
            return false;
        }
        if (!this.axisLocations.equals(that.axisLocations)) {
            return false;
        }
        if (!this.renderers.equals(that.renderers)) {
            return false;
        }
        if (!this.angleTickUnit.equals(that.angleTickUnit)) {
            return false;
        }
        if (this.angleGridlinesVisible != that.angleGridlinesVisible) {
            return false;
        }
        if (this.angleLabelsVisible != that.angleLabelsVisible) {
            return false;
        }
        if (!this.angleLabelFont.equals(that.angleLabelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.angleLabelPaint, that.angleLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.angleGridlineStroke,
                that.angleGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.angleGridlinePaint, that.angleGridlinePaint
        )) {
            return false;
        }
        if (this.radiusGridlinesVisible != that.radiusGridlinesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.radiusGridlineStroke,
                that.radiusGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.radiusGridlinePaint,
                that.radiusGridlinePaint)) {
            return false;
        }
        if (!this.cornerTextItems.equals(that.cornerTextItems)) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (!ObjectUtilities.equal(this.fixedLegendItems,
                that.fixedLegendItems)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {

        PolarPlot clone = (PolarPlot) super.clone();
        clone.axes = (ObjectList) ObjectUtilities.clone(this.axes);
        for (int i = 0; i < this.axes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.axes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.axes.set(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }

        
        clone.datasets = (ObjectList) ObjectUtilities.clone(this.datasets);
        for (int i = 0; i < clone.datasets.size(); ++i) {
            XYDataset d = getDataset(i);
            if (d != null) {
                d.addChangeListener(clone);
            }
        }

        clone.renderers = (ObjectList) ObjectUtilities.clone(this.renderers);
        for (int i = 0; i < this.renderers.size(); i++) {
            PolarItemRenderer renderer2 = (PolarItemRenderer) this.renderers.get(i);
            if (renderer2 instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) renderer2;
                PolarItemRenderer rc = (PolarItemRenderer) pc.clone();
                clone.renderers.set(i, rc);
                rc.setPlot(clone);
                rc.addChangeListener(clone);
            }
        }

        clone.cornerTextItems = new ArrayList(this.cornerTextItems);
       
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.angleGridlineStroke, stream);
        SerialUtilities.writePaint(this.angleGridlinePaint, stream);
        SerialUtilities.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtilities.writePaint(this.radiusGridlinePaint, stream);
        SerialUtilities.writePaint(this.angleLabelPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.angleGridlineStroke = SerialUtilities.readStroke(stream);
        this.angleGridlinePaint = SerialUtilities.readPaint(stream);
        this.radiusGridlineStroke = SerialUtilities.readStroke(stream);
        this.radiusGridlinePaint = SerialUtilities.readPaint(stream);
        this.angleLabelPaint = SerialUtilities.readPaint(stream);

        int rangeAxisCount = this.axes.size();
        for (int i = 0; i < rangeAxisCount; i++) {
            Axis axis = (Axis) this.axes.get(i);
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        int datasetCount = this.datasets.size();
        for (int i = 0; i < datasetCount; i++) {
            Dataset dataset = (Dataset) this.datasets.get(i);
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        int rendererCount = this.renderers.size();
        for (int i = 0; i < rendererCount; i++) {
            PolarItemRenderer renderer = (PolarItemRenderer) this.renderers.get(i);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo state,
                               Point2D source) {
        
    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo state,
                               Point2D source, boolean useAnchor) {
        
    }

    
    public void zoomDomainAxes(double lowerPercent, double upperPercent,
                               PlotRenderingInfo state, Point2D source) {
        
    }

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo state,
                              Point2D source) {
        zoom(factor);
    }

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo info,
                              Point2D source, boolean useAnchor) {
        
        if (useAnchor) {
            
            
            double sourceX = source.getX();
            double anchorX = getAxis().java2DToValue(sourceX,
                    info.getDataArea(), RectangleEdge.BOTTOM);
            getAxis().resizeRange(factor, anchorX);
        }
        else {
            getAxis().resizeRange(factor);
        }

    }

    
    public void zoomRangeAxes(double lowerPercent, double upperPercent,
                              PlotRenderingInfo state, Point2D source) {
        zoom((upperPercent + lowerPercent) / 2.0);
    }

    
    public boolean isDomainZoomable() {
        return false;
    }

    
    public boolean isRangeZoomable() {
        return true;
    }

    
    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }

    
    public Point translateToJava2D(double angleDegrees, double radius,
            ValueAxis axis, Rectangle2D dataArea) {

        double radians = Math.toRadians(angleDegrees - 90.0);

        double minx = dataArea.getMinX() + this.margin;
        double maxx = dataArea.getMaxX() - this.margin;
        double miny = dataArea.getMinY() + this.margin;
        double maxy = dataArea.getMaxY() - this.margin;

        double lengthX = maxx - minx;
        double lengthY = maxy - miny;
        double length = Math.min(lengthX, lengthY);

        double midX = minx + lengthX / 2.0;
        double midY = miny + lengthY / 2.0;

        double axisMin = axis.getLowerBound();
        double axisMax =  axis.getUpperBound();
        double adjustedRadius = Math.max(radius, axisMin);

        double xv = length / 2.0 * Math.cos(radians);
        double yv = length / 2.0 * Math.sin(radians);

        float x = (float) (midX + (xv * (adjustedRadius - axisMin)
                / (axisMax - axisMin)));
        float y = (float) (midY + (yv * (adjustedRadius - axisMin)
                / (axisMax - axisMin)));

        int ix = Math.round(x);
        int iy = Math.round(y);

        Point p = new Point(ix, iy);
        return p;

    }

    
    public Point translateValueThetaRadiusToJava2D(double angleDegrees,
            double radius, Rectangle2D dataArea) {

        double radians = Math.toRadians(angleDegrees - 90.0);

        double minx = dataArea.getMinX() + this.margin;
        double maxx = dataArea.getMaxX() - this.margin;
        double miny = dataArea.getMinY() + this.margin;
        double maxy = dataArea.getMaxY() - this.margin;

        double lengthX = maxx - minx;
        double lengthY = maxy - miny;
        double length = Math.min(lengthX, lengthY);

        double midX = minx + lengthX / 2.0;
        double midY = miny + lengthY / 2.0;

        double axisMin = getAxis().getLowerBound();
        double axisMax =  getAxis().getUpperBound();
        double adjustedRadius = Math.max(radius, axisMin);

        double xv = length / 2.0 * Math.cos(radians);
        double yv = length / 2.0 * Math.sin(radians);

        float x = (float) (midX + (xv * (adjustedRadius - axisMin)
                / (axisMax - axisMin)));
        float y = (float) (midY + (yv * (adjustedRadius - axisMin)
                / (axisMax - axisMin)));

        int ix = Math.round(x);
        int iy = Math.round(y);

        Point p = new Point(ix, iy);
        return p;

    }

    
    public double getMaxRadius() {
        return getAxis().getUpperBound();
    }

    
    public int getSeriesCount() {
        int result = 0;
        XYDataset dataset = getDataset(0);
        if (dataset != null) {
            result = dataset.getSeriesCount();
        }
        return result;
    }

    
    protected AxisState drawAxis(Graphics2D g2, Rectangle2D plotArea,
                                 Rectangle2D dataArea) {
        return getAxis().draw(g2, dataArea.getMinY(), plotArea, dataArea,
                RectangleEdge.TOP, null);
    }

}
