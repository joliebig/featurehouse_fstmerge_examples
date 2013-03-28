

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYAnnotationBoundsInfo;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.RendererUtilities;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class XYPlot extends Plot implements ValueAxisPlot, Pannable, Zoomable,
        RendererChangeListener, Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 7044148245716569264L;

    
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f,
            new float[] {2.0f, 2.0f}, 0.0f);

    
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    
    public static final Stroke DEFAULT_CROSSHAIR_STROKE
            = DEFAULT_GRIDLINE_STROKE;

    
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;

    
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.plot.LocalizationBundle");

    
    private PlotOrientation orientation;

    
    private RectangleInsets axisOffset;

    
    private ObjectList domainAxes;

    
    private ObjectList domainAxisLocations;

    
    private ObjectList rangeAxes;

    
    private ObjectList rangeAxisLocations;

    
    private ObjectList datasets;

    
    private ObjectList renderers;

    
    private Map datasetToDomainAxesMap;

    
    private Map datasetToRangeAxesMap;

    
    private transient Point2D quadrantOrigin = new Point2D.Double(0.0, 0.0);

    
    private transient Paint[] quadrantPaint
            = new Paint[] {null, null, null, null};

    
    private boolean domainGridlinesVisible;

    
    private transient Stroke domainGridlineStroke;

    
    private transient Paint domainGridlinePaint;

    
    private boolean rangeGridlinesVisible;

    
    private transient Stroke rangeGridlineStroke;

    
    private transient Paint rangeGridlinePaint;

    
    private boolean domainMinorGridlinesVisible;

    
    private transient Stroke domainMinorGridlineStroke;

    
    private transient Paint domainMinorGridlinePaint;

    
    private boolean rangeMinorGridlinesVisible;

    
    private transient Stroke rangeMinorGridlineStroke;

    
    private transient Paint rangeMinorGridlinePaint;

    
    private boolean domainZeroBaselineVisible;

    
    private transient Stroke domainZeroBaselineStroke;

    
    private transient Paint domainZeroBaselinePaint;

    
    private boolean rangeZeroBaselineVisible;

    
    private transient Stroke rangeZeroBaselineStroke;

    
    private transient Paint rangeZeroBaselinePaint;

    
    private boolean domainCrosshairVisible;

    
    private double domainCrosshairValue;

    
    private transient Stroke domainCrosshairStroke;

    
    private transient Paint domainCrosshairPaint;

    
    private boolean domainCrosshairLockedOnData = true;

    
    private boolean rangeCrosshairVisible;

    
    private double rangeCrosshairValue;

    
    private transient Stroke rangeCrosshairStroke;

    
    private transient Paint rangeCrosshairPaint;

    
    private boolean rangeCrosshairLockedOnData = true;

    
    private Map foregroundDomainMarkers;

    
    private Map backgroundDomainMarkers;

    
    private Map foregroundRangeMarkers;

    
    private Map backgroundRangeMarkers;

    
    private List annotations;

    
    private transient Paint domainTickBandPaint;

    
    private transient Paint rangeTickBandPaint;

    
    private AxisSpace fixedDomainAxisSpace;

    
    private AxisSpace fixedRangeAxisSpace;

    
    private DatasetRenderingOrder datasetRenderingOrder
            = DatasetRenderingOrder.REVERSE;

    
    private SeriesRenderingOrder seriesRenderingOrder
            = SeriesRenderingOrder.REVERSE;

    
    private int weight;

    
    private LegendItemCollection fixedLegendItems;

    
    private boolean domainPannable;

    
    private boolean rangePannable;

    
    public XYPlot() {
        this(null, null, null, null);
    }

    
    public XYPlot(XYDataset dataset,
                  ValueAxis domainAxis,
                  ValueAxis rangeAxis,
                  XYItemRenderer renderer) {

        super();

        this.orientation = PlotOrientation.VERTICAL;
        this.weight = 1;  
        this.axisOffset = RectangleInsets.ZERO_INSETS;

        
        this.domainAxes = new ObjectList();
        this.domainAxisLocations = new ObjectList();
        this.foregroundDomainMarkers = new HashMap();
        this.backgroundDomainMarkers = new HashMap();

        this.rangeAxes = new ObjectList();
        this.rangeAxisLocations = new ObjectList();
        this.foregroundRangeMarkers = new HashMap();
        this.backgroundRangeMarkers = new HashMap();

        this.datasets = new ObjectList();
        this.renderers = new ObjectList();

        this.datasetToDomainAxesMap = new TreeMap();
        this.datasetToRangeAxesMap = new TreeMap();

        this.annotations = new java.util.ArrayList();

        this.datasets.set(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        this.renderers.set(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }

        this.domainAxes.set(0, domainAxis);
        this.mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.domainAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);

        this.rangeAxes.set(0, rangeAxis);
        this.mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.rangeAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);

        configureDomainAxes();
        configureRangeAxes();

        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.domainMinorGridlinesVisible = false;
        this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainMinorGridlinePaint = Color.white;

        this.domainZeroBaselineVisible = false;
        this.domainZeroBaselinePaint = Color.black;
        this.domainZeroBaselineStroke = new BasicStroke(0.5f);

        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.white;

        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.black;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);

        this.domainCrosshairVisible = false;
        this.domainCrosshairValue = 0.0;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;

        this.rangeCrosshairVisible = false;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;

    }

    
    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
    }

    
    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    
    public void setOrientation(PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }
        if (orientation != this.orientation) {
            this.orientation = orientation;
            fireChangeEvent();
        }
    }

    
    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    
    public void setAxisOffset(RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.axisOffset = offset;
        fireChangeEvent();
    }

    
    public ValueAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    
    public ValueAxis getDomainAxis(int index) {
        ValueAxis result = null;
        if (index < this.domainAxes.size()) {
            result = (ValueAxis) this.domainAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getDomainAxis(index);
            }
        }
        return result;
    }

    
    public void setDomainAxis(ValueAxis axis) {
        setDomainAxis(0, axis);
    }

    
    public void setDomainAxis(int index, ValueAxis axis) {
        setDomainAxis(index, axis, true);
    }

    
    public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getDomainAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setDomainAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    
    public AxisLocation getDomainAxisLocation() {
        return (AxisLocation) this.domainAxisLocations.get(0);
    }

    
    public void setDomainAxisLocation(AxisLocation location) {
        
        setDomainAxisLocation(0, location, true);
    }

    
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        
        setDomainAxisLocation(0, location, notify);
    }

    
    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(getDomainAxisLocation(),
                this.orientation);
    }

    
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    
    public void clearDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.domainAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    
    public void configureDomainAxes() {
        for (int i = 0; i < this.domainAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.domainAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }

    
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.domainAxisLocations.size()) {
            result = (AxisLocation) this.domainAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation());
        }
        return result;
    }

    
    public void setDomainAxisLocation(int index, AxisLocation location) {
        
        setDomainAxisLocation(index, location, true);
    }

    
    public void setDomainAxisLocation(int index, AxisLocation location,
            boolean notify) {

        if (index == 0 && location == null) {
            throw new IllegalArgumentException(
                    "Null 'location' for index 0 not permitted.");
        }
        this.domainAxisLocations.set(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public RectangleEdge getDomainAxisEdge(int index) {
        AxisLocation location = getDomainAxisLocation(index);
        RectangleEdge result = Plot.resolveDomainAxisLocation(location,
                this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(getDomainAxisEdge());
        }
        return result;
    }

    
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    
    public void setRangeAxis(ValueAxis axis)  {

        if (axis != null) {
            axis.setPlot(this);
        }

        
        ValueAxis existing = getRangeAxis();
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        this.rangeAxes.set(0, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        fireChangeEvent();

    }

    
    public AxisLocation getRangeAxisLocation() {
        return (AxisLocation) this.rangeAxisLocations.get(0);
    }

    
    public void setRangeAxisLocation(AxisLocation location) {
        
        setRangeAxisLocation(0, location, true);
    }

    
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        
        setRangeAxisLocation(0, location, notify);
    }

    
    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(),
                this.orientation);
    }

    
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = null;
        if (index < this.rangeAxes.size()) {
            result = (ValueAxis) this.rangeAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getRangeAxis(index);
            }
        }
        return result;
    }

    
    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getRangeAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    
    public void clearRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.rangeAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    
    public void configureRangeAxes() {
        for (int i = 0; i < this.rangeAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.rangeAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }

    
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.rangeAxisLocations.size()) {
            result = (AxisLocation) this.rangeAxisLocations.get(index);
        }
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation());
        }
        return result;
    }

    
    public void setRangeAxisLocation(int index, AxisLocation location) {
        
        setRangeAxisLocation(index, location, true);
    }

    
    public void setRangeAxisLocation(int index, AxisLocation location,
            boolean notify) {

        if (index == 0 && location == null) {
            throw new IllegalArgumentException(
                    "Null 'location' for index 0 not permitted.");
        }
        this.rangeAxisLocations.set(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = getRangeAxisLocation(index);
        RectangleEdge result = Plot.resolveRangeAxisLocation(location,
                this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(getRangeAxisEdge());
        }
        return result;
    }

    
    public XYDataset getDataset() {
        return getDataset(0);
    }

    
    public XYDataset getDataset(int index) {
        XYDataset result = null;
        if (this.datasets.size() > index) {
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

    
    public int getDatasetCount() {
        return this.datasets.size();
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

    
    public void mapDatasetToDomainAxis(int index, int axisIndex) {
        List axisIndices = new java.util.ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToDomainAxes(index, axisIndices);
    }

    
    public void mapDatasetToDomainAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        Integer key = new Integer(index);
        this.datasetToDomainAxesMap.put(key, new ArrayList(axisIndices));
        
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    
    public void mapDatasetToRangeAxis(int index, int axisIndex) {
        List axisIndices = new java.util.ArrayList(1);
        axisIndices.add(new Integer(axisIndex));
        mapDatasetToRangeAxes(index, axisIndices);
    }

    
    public void mapDatasetToRangeAxes(int index, List axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        Integer key = new Integer(index);
        this.datasetToRangeAxesMap.put(key, new ArrayList(axisIndices));
        
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

    
    public int getRendererCount() {
        return this.renderers.size();
    }

    
    public XYItemRenderer getRenderer() {
        return getRenderer(0);
    }

    
    public XYItemRenderer getRenderer(int index) {
        XYItemRenderer result = null;
        if (this.renderers.size() > index) {
            result = (XYItemRenderer) this.renderers.get(index);
        }
        return result;

    }

    
    public void setRenderer(XYItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    
    public void setRenderer(int index, XYItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    
    public void setRenderer(int index, XYItemRenderer renderer,
                            boolean notify) {
        XYItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setRenderers(XYItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], false);
        }
        fireChangeEvent();
    }

    
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.datasetRenderingOrder;
    }

    
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.datasetRenderingOrder = order;
        fireChangeEvent();
    }

    
    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return this.seriesRenderingOrder;
    }

    
    public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        this.seriesRenderingOrder = order;
        fireChangeEvent();
    }

    
    public int getIndexOf(XYItemRenderer renderer) {
        return this.renderers.indexOf(renderer);
    }

    
    public XYItemRenderer getRendererForDataset(XYDataset dataset) {
        XYItemRenderer result = null;
        for (int i = 0; i < this.datasets.size(); i++) {
            if (this.datasets.get(i) == dataset) {
                result = (XYItemRenderer) this.renderers.get(i);
                if (result == null) {
                    result = getRenderer();
                }
                break;
            }
        }
        return result;
    }

    
    public int getWeight() {
        return this.weight;
    }

    
    public void setWeight(int weight) {
        this.weight = weight;
        fireChangeEvent();
    }

    
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    
    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public boolean isDomainMinorGridlinesVisible() {
        return this.domainMinorGridlinesVisible;
    }

    
    public void setDomainMinorGridlinesVisible(boolean visible) {
        if (this.domainMinorGridlinesVisible != visible) {
            this.domainMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    
    public void setDomainGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainGridlineStroke = stroke;
        fireChangeEvent();
    }

    

    public Stroke getDomainMinorGridlineStroke() {
        return this.domainMinorGridlineStroke;
    }

    
    public void setDomainMinorGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    
    public void setDomainGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public Paint getDomainMinorGridlinePaint() {
        return this.domainMinorGridlinePaint;
    }

    
    public void setDomainMinorGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    
    public void setRangeGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    
    public void setRangeGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRangeMinorGridlinesVisible() {
        return this.rangeMinorGridlinesVisible;
    }

    
    public void setRangeMinorGridlinesVisible(boolean visible) {
        if (this.rangeMinorGridlinesVisible != visible) {
            this.rangeMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    
    public Stroke getRangeMinorGridlineStroke() {
        return this.rangeMinorGridlineStroke;
    }

    
    public void setRangeMinorGridlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRangeMinorGridlinePaint() {
        return this.rangeMinorGridlinePaint;
    }

    
    public void setRangeMinorGridlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isDomainZeroBaselineVisible() {
        return this.domainZeroBaselineVisible;
    }

    
    public void setDomainZeroBaselineVisible(boolean visible) {
        this.domainZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    
    public Stroke getDomainZeroBaselineStroke() {
        return this.domainZeroBaselineStroke;
    }

    
    public void setDomainZeroBaselineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getDomainZeroBaselinePaint() {
        return this.domainZeroBaselinePaint;
    }

    
    public void setDomainZeroBaselinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainZeroBaselinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRangeZeroBaselineVisible() {
        return this.rangeZeroBaselineVisible;
    }

    
    public void setRangeZeroBaselineVisible(boolean visible) {
        this.rangeZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    
    public Stroke getRangeZeroBaselineStroke() {
        return this.rangeZeroBaselineStroke;
    }

    
    public void setRangeZeroBaselineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRangeZeroBaselinePaint() {
        return this.rangeZeroBaselinePaint;
    }

    
    public void setRangeZeroBaselinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeZeroBaselinePaint = paint;
        fireChangeEvent();
    }

    
    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    
    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    
    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
        fireChangeEvent();
    }

    
    public Point2D getQuadrantOrigin() {
        return this.quadrantOrigin;
    }

    
    public void setQuadrantOrigin(Point2D origin) {
        if (origin == null) {
            throw new IllegalArgumentException("Null 'origin' argument.");
        }
        this.quadrantOrigin = origin;
        fireChangeEvent();
    }

    
    public Paint getQuadrantPaint(int index) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index
                    + ") should be in the range 0 to 3.");
        }
        return this.quadrantPaint[index];
    }

    
    public void setQuadrantPaint(int index, Paint paint) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index
                    + ") should be in the range 0 to 3.");
        }
        this.quadrantPaint[index] = paint;
        fireChangeEvent();
    }

    
    public void addDomainMarker(Marker marker) {
        
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    
    public void addDomainMarker(Marker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    
    public void clearDomainMarkers() {
        if (this.backgroundDomainMarkers != null) {
            Set keys = this.backgroundDomainMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearDomainMarkers(key.intValue());
            }
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            Set keys = this.foregroundDomainMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearDomainMarkers(key.intValue());
            }
            this.foregroundDomainMarkers.clear();
        }
        fireChangeEvent();
    }

    
    public void clearDomainMarkers(int index) {
        Integer key = new Integer(index);
        if (this.backgroundDomainMarkers != null) {
            Collection markers
                = (Collection) this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            Collection markers
                = (Collection) this.foregroundDomainMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    
    public void addDomainMarker(int index, Marker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    
    public void addDomainMarker(int index, Marker marker, Layer layer,
            boolean notify) {
        if (marker == null) {
            throw new IllegalArgumentException("Null 'marker' not permitted.");
        }
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' not permitted.");
        }
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.foregroundDomainMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.foregroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.backgroundDomainMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.backgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    
    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return removeDomainMarker(0, marker, layer);
    }

    
    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    
    public boolean removeDomainMarker(int index, Marker marker, Layer layer,
            boolean notify) {
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundDomainMarkers.get(
                    new Integer(index));
        }
        else {
            markers = (ArrayList) this.backgroundDomainMarkers.get(
                    new Integer(index));
        }
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    
    public void clearRangeMarkers() {
        if (this.backgroundRangeMarkers != null) {
            Set keys = this.backgroundRangeMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearRangeMarkers(key.intValue());
            }
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            Set keys = this.foregroundRangeMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearRangeMarkers(key.intValue());
            }
            this.foregroundRangeMarkers.clear();
        }
        fireChangeEvent();
    }

    
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    
    public void addRangeMarker(int index, Marker marker, Layer layer,
            boolean notify) {
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.foregroundRangeMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.foregroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.backgroundRangeMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.backgroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void clearRangeMarkers(int index) {
        Integer key = new Integer(index);
        if (this.backgroundRangeMarkers != null) {
            Collection markers
                = (Collection) this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            Collection markers
                = (Collection) this.foregroundRangeMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    
    public boolean removeRangeMarker(Marker marker) {
        return removeRangeMarker(marker, Layer.FOREGROUND);
    }

    
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
    }

    
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    
    public boolean removeRangeMarker(int index, Marker marker, Layer layer,
            boolean notify) {
        if (marker == null) {
            throw new IllegalArgumentException("Null 'marker' argument.");
        }
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundRangeMarkers.get(
                    new Integer(index));
        }
        else {
            markers = (ArrayList) this.backgroundRangeMarkers.get(
                    new Integer(index));
        }
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    
    public void addAnnotation(XYAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    
    public void addAnnotation(XYAnnotation annotation, boolean notify) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        this.annotations.add(annotation);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean removeAnnotation(XYAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    
    public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        boolean removed = this.annotations.remove(annotation);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    
    public List getAnnotations() {
        return new ArrayList(this.annotations);
    }

    
    public void clearAnnotations() {
        this.annotations.clear();
        fireChangeEvent();
    }

    
    protected AxisSpace calculateAxisSpace(Graphics2D g2,
                                           Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = calculateRangeAxisSpace(g2, plotArea, space);
        Rectangle2D revPlotArea = space.shrink(plotArea, null);
        space = calculateDomainAxisSpace(g2, revPlotArea, space);
        return space;
    }

    
    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2,
                                                 Rectangle2D plotArea,
                                                 AxisSpace space) {

        if (space == null) {
            space = new AxisSpace();
        }

        
        if (this.fixedDomainAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(),
                        RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(),
                        RectangleEdge.RIGHT);
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(),
                        RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(),
                        RectangleEdge.BOTTOM);
            }
        }
        else {
            
            for (int i = 0; i < this.domainAxes.size(); i++) {
                Axis axis = (Axis) this.domainAxes.get(i);
                if (axis != null) {
                    RectangleEdge edge = getDomainAxisEdge(i);
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }

        return space;

    }

    
    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2,
                                                Rectangle2D plotArea,
                                                AxisSpace space) {

        if (space == null) {
            space = new AxisSpace();
        }

        
        if (this.fixedRangeAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(),
                        RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(),
                        RectangleEdge.BOTTOM);
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(),
                        RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(),
                        RectangleEdge.RIGHT);
            }
        }
        else {
            
            for (int i = 0; i < this.rangeAxes.size(); i++) {
                Axis axis = (Axis) this.rangeAxes.get(i);
                if (axis != null) {
                    RectangleEdge edge = getRangeAxisEdge(i);
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;

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

        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }

        
        drawBackground(g2, dataArea);
        Map axisStateMap = drawAxes(g2, area, dataArea, info);

        PlotOrientation orient = getOrientation();

        
        
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);

        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                double x;
                if (orient == PlotOrientation.VERTICAL) {
                    x = domainAxis.java2DToValue(anchor.getX(), dataArea,
                            getDomainAxisEdge());
                }
                else {
                    x = domainAxis.java2DToValue(anchor.getY(), dataArea,
                            getDomainAxisEdge());
                }
                crosshairState.setAnchorX(x);
            }
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (orient == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea,
                            getRangeAxisEdge());
                }
                else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea,
                            getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(getDomainCrosshairValue());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        AxisState domainAxisState = (AxisState) axisStateMap.get(
                getDomainAxis());
        if (domainAxisState == null) {
            if (parentState != null) {
                domainAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getDomainAxis());
            }
        }

        AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = (AxisState) parentState.getSharedAxisStates()
                        .get(getRangeAxis());
            }
        }
        if (domainAxisState != null) {
            drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            drawZeroDomainBaseline(g2, dataArea);
        }
        if (rangeAxisState != null) {
            drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            drawZeroRangeBaseline(g2, dataArea);
        }

        
        for (int i = 0; i < this.renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        for (int i = 0; i < this.renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }

        
        boolean foundData = false;
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        if (order == DatasetRenderingOrder.FORWARD) {

            
            int rendererCount = this.renderers.size();
            for (int i = 0; i < rendererCount; i++) {
                XYItemRenderer r = getRenderer(i);
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.BACKGROUND, info);
                }
            }

            
            for (int i = 0; i < getDatasetCount(); i++) {
                foundData = render(g2, dataArea, i, info, crosshairState)
                    || foundData;
            }

            
            for (int i = 0; i < rendererCount; i++) {
                XYItemRenderer r = getRenderer(i);
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.FOREGROUND, info);
                }
            }

        }
        else if (order == DatasetRenderingOrder.REVERSE) {

            
            int rendererCount = this.renderers.size();
            for (int i = rendererCount - 1; i >= 0; i--) {
                XYItemRenderer r = getRenderer(i);
                if (i >= getDatasetCount()) { 
                    continue;                 
                }
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.BACKGROUND, info);
                }
            }

            for (int i = getDatasetCount() - 1; i >= 0; i--) {
                foundData = render(g2, dataArea, i, info, crosshairState)
                    || foundData;
            }

            
            for (int i = rendererCount - 1; i >= 0; i--) {
                XYItemRenderer r = getRenderer(i);
                if (i >= getDatasetCount()) { 
                    continue;                 
                }
                if (r != null) {
                    ValueAxis domainAxis = getDomainAxisForDataset(i);
                    ValueAxis rangeAxis = getRangeAxisForDataset(i);
                    r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis,
                            Layer.FOREGROUND, info);
                }
            }

        }

        
        int xAxisIndex = crosshairState.getDomainAxisIndex();
        ValueAxis xAxis = getDomainAxis(xAxisIndex);
        RectangleEdge xAxisEdge = getDomainAxisEdge(xAxisIndex);
        if (!this.domainCrosshairLockedOnData && anchor != null) {
            double xx;
            if (orient == PlotOrientation.VERTICAL) {
                xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
            }
            else {
                xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
            }
            crosshairState.setCrosshairX(xx);
        }
        setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
        if (isDomainCrosshairVisible()) {
            double x = getDomainCrosshairValue();
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
        }

        
        int yAxisIndex = crosshairState.getRangeAxisIndex();
        ValueAxis yAxis = getRangeAxis(yAxisIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge(yAxisIndex);
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy;
            if (orient == PlotOrientation.VERTICAL) {
                yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
            } else {
                yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
            }
            crosshairState.setCrosshairY(yy);
        }
        setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
        if (isRangeCrosshairVisible()) {
            double y = getRangeCrosshairValue();
            Paint paint = getRangeCrosshairPaint();
            Stroke stroke = getRangeCrosshairStroke();
            drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
        }

        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }

        for (int i = 0; i < this.renderers.size(); i++) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i = 0; i < this.renderers.size(); i++) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }

        drawAnnotations(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, dataArea);

    }

    
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawQuadrants(g2, area);
        drawBackgroundImage(g2, area);
    }

    
    protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
        
        
        
        boolean somethingToDraw = false;

        ValueAxis xAxis = getDomainAxis();
        if (xAxis == null) {  
            return;
        }
        double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
        double xx = xAxis.valueToJava2D(x, area, getDomainAxisEdge());

        ValueAxis yAxis = getRangeAxis();
        if (yAxis == null) {  
            return;
        }
        double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
        double yy = yAxis.valueToJava2D(y, area, getRangeAxisEdge());

        double xmin = xAxis.getLowerBound();
        double xxmin = xAxis.valueToJava2D(xmin, area, getDomainAxisEdge());

        double xmax = xAxis.getUpperBound();
        double xxmax = xAxis.valueToJava2D(xmax, area, getDomainAxisEdge());

        double ymin = yAxis.getLowerBound();
        double yymin = yAxis.valueToJava2D(ymin, area, getRangeAxisEdge());

        double ymax = yAxis.getUpperBound();
        double yymax = yAxis.valueToJava2D(ymax, area, getRangeAxisEdge());

        Rectangle2D[] r = new Rectangle2D[] {null, null, null, null};
        if (this.quadrantPaint[0] != null) {
            if (x > xmin && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[0] = new Rectangle2D.Double(Math.min(yymax, yy),
                            Math.min(xxmin, xx), Math.abs(yy - yymax),
                            Math.abs(xx - xxmin));
                }
                else {  
                    r[0] = new Rectangle2D.Double(Math.min(xxmin, xx),
                            Math.min(yymax, yy), Math.abs(xx - xxmin),
                            Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[1] != null) {
            if (x < xmax && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[1] = new Rectangle2D.Double(Math.min(yymax, yy),
                            Math.min(xxmax, xx), Math.abs(yy - yymax),
                            Math.abs(xx - xxmax));
                }
                else {  
                    r[1] = new Rectangle2D.Double(Math.min(xx, xxmax),
                            Math.min(yymax, yy), Math.abs(xx - xxmax),
                            Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[2] != null) {
            if (x > xmin && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[2] = new Rectangle2D.Double(Math.min(yymin, yy),
                            Math.min(xxmin, xx), Math.abs(yy - yymin),
                            Math.abs(xx - xxmin));
                }
                else {  
                    r[2] = new Rectangle2D.Double(Math.min(xxmin, xx),
                            Math.min(yymin, yy), Math.abs(xx - xxmin),
                            Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[3] != null) {
            if (x < xmax && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[3] = new Rectangle2D.Double(Math.min(yymin, yy),
                            Math.min(xxmax, xx), Math.abs(yy - yymin),
                            Math.abs(xx - xxmax));
                }
                else {  
                    r[3] = new Rectangle2D.Double(Math.min(xx, xxmax),
                            Math.min(yymin, yy), Math.abs(xx - xxmax),
                            Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (somethingToDraw) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    getBackgroundAlpha()));
            for (int i = 0; i < 4; i++) {
                if (this.quadrantPaint[i] != null && r[i] != null) {
                    g2.setPaint(this.quadrantPaint[i]);
                    g2.fill(r[i]);
                }
            }
            g2.setComposite(originalComposite);
        }
    }

    
    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea,
                                    List ticks) {
        Paint bandPaint = getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea,
                            previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getUpperBound();
            if (fillBand) {
                getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea,
                        previous, end);
            }
        }
    }

    
    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea,
                                   List ticks) {
        Paint bandPaint = getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis axis = getRangeAxis();
            double previous = axis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillRangeGridBand(g2, this, axis, dataArea,
                            previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = axis.getUpperBound();
            if (fillBand) {
                getRenderer().fillRangeGridBand(g2, this, axis, dataArea,
                        previous, end);
            }
        }
    }

    
    protected Map drawAxes(Graphics2D g2,
                           Rectangle2D plotArea,
                           Rectangle2D dataArea,
                           PlotRenderingInfo plotState) {

        AxisCollection axisCollection = new AxisCollection();

        
        for (int index = 0; index < this.domainAxes.size(); index++) {
            ValueAxis axis = (ValueAxis) this.domainAxes.get(index);
            if (axis != null) {
                axisCollection.add(axis, getDomainAxisEdge(index));
            }
        }

        
        for (int index = 0; index < this.rangeAxes.size(); index++) {
            ValueAxis yAxis = (ValueAxis) this.rangeAxes.get(index);
            if (yAxis != null) {
                axisCollection.add(yAxis, getRangeAxisEdge(index));
            }
        }

        Map axisStateMap = new HashMap();

        
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(
                dataArea.getHeight());
        Iterator iterator = axisCollection.getAxesAtTop().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea,
                    RectangleEdge.TOP, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        
        cursor = dataArea.getMaxY()
                 + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        iterator = axisCollection.getAxesAtBottom().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea,
                    RectangleEdge.BOTTOM, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        
        cursor = dataArea.getMinX()
                 - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtLeft().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea,
                    RectangleEdge.LEFT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        
        cursor = dataArea.getMaxX()
                 + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        iterator = axisCollection.getAxesAtRight().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea,
                    RectangleEdge.RIGHT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        return axisStateMap;
    }

    
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index,
            PlotRenderingInfo info, CrosshairState crosshairState) {

        boolean foundData = false;
        XYDataset dataset = getDataset(index);
        if (!DatasetUtilities.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                return foundData;  
            }
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) { 
                    return foundData;
                }
            }

            XYItemRendererState state = renderer.initialise(g2, dataArea, this,
                    dataset, info);
            int passCount = renderer.getPassCount();

            SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; series--) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) {
                            continue;
                        }
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info,
                                    this, xAxis, yAxis, dataset, series, item,
                                    crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                    }
                }
            }
            else {
                
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info,
                                    this, xAxis, yAxis, dataset, series, item,
                                    crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }

    
    public ValueAxis getDomainAxisForDataset(int index) {
        int upper = Math.max(getDatasetCount(), getRendererCount());
        if (index < 0 || index >= upper) {
            throw new IllegalArgumentException("Index " + index
                    + " out of bounds.");
        }
        ValueAxis valueAxis = null;
        List axisIndices = (List) this.datasetToDomainAxesMap.get(
                new Integer(index));
        if (axisIndices != null) {
            
            Integer axisIndex = (Integer) axisIndices.get(0);
            valueAxis = getDomainAxis(axisIndex.intValue());
        }
        else {
            valueAxis = getDomainAxis(0);
        }
        return valueAxis;
    }

    
    public ValueAxis getRangeAxisForDataset(int index) {
        int upper = Math.max(getDatasetCount(), getRendererCount());
        if (index < 0 || index >= upper) {
            throw new IllegalArgumentException("Index " + index
                    + " out of bounds.");
        }
        ValueAxis valueAxis = null;
        List axisIndices = (List) this.datasetToRangeAxesMap.get(
                new Integer(index));
        if (axisIndices != null) {
            
            Integer axisIndex = (Integer) axisIndices.get(0);
            valueAxis = getRangeAxis(axisIndex.intValue());
        }
        else {
            valueAxis = getRangeAxis(0);
        }
        return valueAxis;
    }

    
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea,
                                       List ticks) {

        
        if (getRenderer() == null) {
            return;
        }

        
        if (isDomainGridlinesVisible() || isDomainMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            Iterator iterator = ticks.iterator();
            boolean paintLine = false;
            while (iterator.hasNext()) {
                paintLine = false;
                ValueTick tick = (ValueTick) iterator.next();
                if ((tick.getTickType() == TickType.MINOR) && isDomainMinorGridlinesVisible()){
                    gridStroke = getDomainMinorGridlineStroke();
                    gridPaint = getDomainMinorGridlinePaint();
                    paintLine = true;
                }
                else if ((tick.getTickType() == TickType.MAJOR) && isDomainGridlinesVisible()){
                    gridStroke = getDomainGridlineStroke();
                    gridPaint = getDomainGridlinePaint();
                    paintLine = true;
                }
                XYItemRenderer r = getRenderer();
                if ((r instanceof AbstractXYItemRenderer) && paintLine) {
                    ((AbstractXYItemRenderer) r).drawDomainLine(g2, this,
                            getDomainAxis(), dataArea, tick.getValue(),
                            gridPaint, gridStroke);
                }
            }
        }
    }

    
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area,
                                      List ticks) {

        
        if (getRenderer() == null) {
            return;
        }

        
        if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            ValueAxis axis = getRangeAxis();
            if (axis != null) {
                Iterator iterator = ticks.iterator();
                boolean paintLine = false;
                while (iterator.hasNext()) {
                    paintLine = false;
                    ValueTick tick = (ValueTick) iterator.next();
                    if ((tick.getTickType() == TickType.MINOR)
                            && isRangeMinorGridlinesVisible()) {
                        gridStroke = getRangeMinorGridlineStroke();
                        gridPaint = getRangeMinorGridlinePaint();
                        paintLine = true;
                    }
                    else if ((tick.getTickType() == TickType.MAJOR)
                            && isRangeGridlinesVisible()) {
                        gridStroke = getRangeGridlineStroke();
                        gridPaint = getRangeGridlinePaint();
                        paintLine = true;
                    }
                    if ((tick.getValue() != 0.0
                            || !isRangeZeroBaselineVisible()) && paintLine) {
                        getRenderer().drawRangeLine(g2, this, getRangeAxis(),
                                area, tick.getValue(), gridPaint, gridStroke);
                    }
                }
            }
        }
    }

    
    protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
        if (isDomainZeroBaselineVisible()) {
            XYItemRenderer r = getRenderer();
            
            
            
            if (r instanceof AbstractXYItemRenderer) {
                AbstractXYItemRenderer renderer = (AbstractXYItemRenderer) r;
                renderer.drawDomainLine(g2, this, getDomainAxis(), area, 0.0,
                        this.domainZeroBaselinePaint,
                        this.domainZeroBaselineStroke);
            }
        }
    }

    
    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (isRangeZeroBaselineVisible()) {
            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, 0.0,
                    this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
        }
    }

    
    public void drawAnnotations(Graphics2D g2,
                                Rectangle2D dataArea,
                                PlotRenderingInfo info) {

        Iterator iterator = this.annotations.iterator();
        while (iterator.hasNext()) {
            XYAnnotation annotation = (XYAnnotation) iterator.next();
            ValueAxis xAxis = getDomainAxis();
            ValueAxis yAxis = getRangeAxis();
            annotation.draw(g2, this, dataArea, xAxis, yAxis, 0, info);
        }

    }

    
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea,
                                     int index, Layer layer) {

        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        
        
        if (index >= getDatasetCount()) {
            return;
        }
        Collection markers = getDomainMarkers(index, layer);
        ValueAxis axis = getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }

    }

    
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea,
                                    int index, Layer layer) {

        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        
        
        if (index >= getDatasetCount()) {
            return;
        }
        Collection markers = getRangeMarkers(index, layer);
        ValueAxis axis = getRangeAxisForDataset(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                r.drawRangeMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    
    public Collection getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    
    public Collection getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    
    public Collection getDomainMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.foregroundDomainMarkers.get(key);
        }
        else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    
    public Collection getRangeMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.foregroundRangeMarkers.get(key);
        }
        else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.backgroundRangeMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea,
                                      double value, Stroke stroke,
                                      Paint paint) {

        ValueAxis axis = getRangeAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getDomainAxis();
        }
        if (axis.getRange().contains(value)) {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            Line2D line = new Line2D.Double(dataArea.getMinX(), yy,
                    dataArea.getMaxX(), yy);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }

    }

    
    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea,
            PlotOrientation orientation, double value, ValueAxis axis,
            Stroke stroke, Paint paint) {

        if (axis.getRange().contains(value)) {
            Line2D line = null;
            if (orientation == PlotOrientation.VERTICAL) {
                double xx = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx,
                        dataArea.getMaxY());
            }
            else {
                double yy = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.LEFT);
                line = new Line2D.Double(dataArea.getMinX(), yy,
                        dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }

    }

    
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea,
                                    double value, Stroke stroke, Paint paint) {

        ValueAxis axis = getDomainAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getRangeAxis();
        }
        if (axis.getRange().contains(value)) {
            double xx = axis.valueToJava2D(value, dataArea,
                    RectangleEdge.BOTTOM);
            Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx,
                    dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }

    }

    
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea,
            PlotOrientation orientation, double value, ValueAxis axis,
            Stroke stroke, Paint paint) {

        if (axis.getRange().contains(value)) {
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                double xx = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.BOTTOM);
                line = new Line2D.Double(xx, dataArea.getMinY(), xx,
                        dataArea.getMaxY());
            }
            else {
                double yy = axis.valueToJava2D(value, dataArea,
                        RectangleEdge.LEFT);
                line = new Line2D.Double(dataArea.getMinX(), yy,
                        dataArea.getMaxX(), yy);
            }
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }

    }

    
    public void handleClick(int x, int y, PlotRenderingInfo info) {

        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            
            ValueAxis xaxis = getDomainAxis();
            if (xaxis != null) {
                double hvalue = xaxis.java2DToValue(x, info.getDataArea(),
                        getDomainAxisEdge());
                setDomainCrosshairValue(hvalue);
            }

            
            ValueAxis yaxis = getRangeAxis();
            if (yaxis != null) {
                double vvalue = yaxis.java2DToValue(y, info.getDataArea(),
                        getRangeAxisEdge());
                setRangeCrosshairValue(vvalue);
            }
        }
    }

    
    private List getDatasetsMappedToDomainAxis(Integer axisIndex) {
        if (axisIndex == null) {
            throw new IllegalArgumentException("Null 'axisIndex' argument.");
        }
        List result = new ArrayList();
        for (int i = 0; i < this.datasets.size(); i++) {
            List mappedAxes = (List) this.datasetToDomainAxesMap.get(
                    new Integer(i));
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(this.datasets.get(i));
                }
            }
            else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(this.datasets.get(i));
                }
            }
        }
        return result;
    }

    
    private List getDatasetsMappedToRangeAxis(Integer axisIndex) {
        if (axisIndex == null) {
            throw new IllegalArgumentException("Null 'axisIndex' argument.");
        }
        List result = new ArrayList();
        for (int i = 0; i < this.datasets.size(); i++) {
            List mappedAxes = (List) this.datasetToRangeAxesMap.get(
                    new Integer(i));
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(this.datasets.get(i));
                }
            }
            else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(this.datasets.get(i));
                }
            }
        }
        return result;
    }

    
    public int getDomainAxisIndex(ValueAxis axis) {
        int result = this.domainAxes.indexOf(axis);
        if (result < 0) {
            
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot p = (XYPlot) parent;
                result = p.getDomainAxisIndex(axis);
            }
        }
        return result;
    }

    
    public int getRangeAxisIndex(ValueAxis axis) {
        int result = this.rangeAxes.indexOf(axis);
        if (result < 0) {
            
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot p = (XYPlot) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }

    
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        List mappedDatasets = new ArrayList();
        List includedAnnotations = new ArrayList();
        boolean isDomainAxis = true;

        
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(
                    new Integer(domainIndex)));
            if (domainIndex == 0) {
                
                Iterator iterator = this.annotations.iterator();
                while (iterator.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation) iterator.next();
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }

        
        int rangeIndex = getRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            isDomainAxis = false;
            mappedDatasets.addAll(getDatasetsMappedToRangeAxis(
                    new Integer(rangeIndex)));
            if (rangeIndex == 0) {
                Iterator iterator = this.annotations.iterator();
                while (iterator.hasNext()) {
                    XYAnnotation annotation = (XYAnnotation) iterator.next();
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }

        
        
        Iterator iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            XYDataset d = (XYDataset) iterator.next();
            if (d != null) {
                XYItemRenderer r = getRendererForDataset(d);
                if (isDomainAxis) {
                    if (r != null) {
                        result = Range.combine(result, r.findDomainBounds(d));
                    }
                    else {
                        result = Range.combine(result,
                                DatasetUtilities.findDomainBounds(d));
                    }
                }
                else {
                    if (r != null) {
                        result = Range.combine(result, r.findRangeBounds(d));
                    }
                    else {
                        result = Range.combine(result,
                                DatasetUtilities.findRangeBounds(d));
                    }
                }
                
                
                if (r instanceof AbstractXYItemRenderer) {
                    AbstractXYItemRenderer rr = (AbstractXYItemRenderer) r;
                    Collection c = rr.getAnnotations();
                    Iterator i = c.iterator();
                    while (i.hasNext()) {
                        XYAnnotation a = (XYAnnotation) i.next();
                        if (a instanceof XYAnnotationBoundsInfo) {
                            includedAnnotations.add(a);
                        }
                    }
                }
            }
        }

        Iterator it = includedAnnotations.iterator();
        while (it.hasNext()) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo) it.next();
            if (xyabi.getIncludeInDataBounds()) {
                if (isDomainAxis) {
                    result = Range.combine(result, xyabi.getXRange());
                }
                else {
                    result = Range.combine(result, xyabi.getYRange());
                }
            }
        }

        return result;

    }

    
    public void datasetChanged(DatasetChangeEvent event) {
        configureDomainAxes();
        configureRangeAxes();
        if (getParent() != null) {
            getParent().datasetChanged(event);
        }
        else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            e.setType(ChartChangeEventType.DATASET_UPDATED);
            notifyListeners(e);
        }
    }

    
    public void rendererChanged(RendererChangeEvent event) {
        
        
        if (event.getSeriesVisibilityChanged()) {
            configureDomainAxes();
            configureRangeAxes();
        }
        fireChangeEvent();
    }

    
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    
    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    
    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    
    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    
    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    
    public void setDomainCrosshairValue(double value) {
        setDomainCrosshairValue(value, true);
    }

    
    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    
    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    
    public void setDomainCrosshairStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.domainCrosshairStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    
    public void setDomainCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    
    public void setRangeCrosshairVisible(boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    
    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    
    public void setRangeCrosshairLockedOnData(boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    
    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    
    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    
    public void setRangeCrosshairValue(double value, boolean notify) {
        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    
    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }

    
    public void setRangeCrosshairStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.rangeCrosshairStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    
    public void setRangeCrosshairPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    
    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    
    public void setFixedDomainAxisSpace(AxisSpace space) {
        setFixedDomainAxisSpace(space, true);
    }

    
    public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
        this.fixedDomainAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }

    
    public void setFixedRangeAxisSpace(AxisSpace space) {
        setFixedRangeAxisSpace(space, true);
    }

    
    public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
        this.fixedRangeAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    
    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    
    public boolean isRangePannable() {
        return this.rangePannable;
    }

    
    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    
    public void panDomainAxes(double percent, PlotRenderingInfo info,
            Point2D source) {
        if (!isDomainPannable()) {
            return;
        }
        int domainAxisCount = getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = getDomainAxis(i);
            if (axis == null) {
                continue;
            }
            if (axis.isInverted()) {
                percent = -percent;
            }
            axis.pan(percent);
        }
    }

    
    public void panRangeAxes(double percent, PlotRenderingInfo info,
            Point2D source) {
        if (!isRangePannable()) {
            return;
        }
        int rangeAxisCount = getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = getRangeAxis(i);
            if (axis == null) {
                continue;
            }
            if (axis.isInverted()) {
                percent = -percent;
            }
            axis.pan(percent);
        }
    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo info,
                               Point2D source) {
        
        zoomDomainAxes(factor, info, source, false);
    }

    
    public void zoomDomainAxes(double factor, PlotRenderingInfo info,
                               Point2D source, boolean useAnchor) {

        
        for (int i = 0; i < this.domainAxes.size(); i++) {
            ValueAxis domainAxis = (ValueAxis) this.domainAxes.get(i);
            if (domainAxis != null) {
                if (useAnchor) {
                    
                    
                    double sourceX = source.getX();
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        sourceX = source.getY();
                    }
                    double anchorX = domainAxis.java2DToValue(sourceX,
                            info.getDataArea(), getDomainAxisEdge());
                    domainAxis.resizeRange2(factor, anchorX);
                }
                else {
                    domainAxis.resizeRange(factor);
                }
            }
        }
    }

    
    public void zoomDomainAxes(double lowerPercent, double upperPercent,
                               PlotRenderingInfo info, Point2D source) {
        for (int i = 0; i < this.domainAxes.size(); i++) {
            ValueAxis domainAxis = (ValueAxis) this.domainAxes.get(i);
            if (domainAxis != null) {
                domainAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo info,
                              Point2D source) {
        
        zoomRangeAxes(factor, info, source, false);
    }

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo info,
                              Point2D source, boolean useAnchor) {

        
        for (int i = 0; i < this.rangeAxes.size(); i++) {
            ValueAxis rangeAxis = (ValueAxis) this.rangeAxes.get(i);
            if (rangeAxis != null) {
                if (useAnchor) {
                    
                    
                    double sourceY = source.getY();
                    if (this.orientation == PlotOrientation.HORIZONTAL) {
                        sourceY = source.getX();
                    }
                    double anchorY = rangeAxis.java2DToValue(sourceY,
                            info.getDataArea(), getRangeAxisEdge());
                    rangeAxis.resizeRange2(factor, anchorY);
                }
                else {
                    rangeAxis.resizeRange(factor);
                }
            }
        }
    }

    
    public void zoomRangeAxes(double lowerPercent, double upperPercent,
                              PlotRenderingInfo info, Point2D source) {
        for (int i = 0; i < this.rangeAxes.size(); i++) {
            ValueAxis rangeAxis = (ValueAxis) this.rangeAxes.get(i);
            if (rangeAxis != null) {
                rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    
    public boolean isDomainZoomable() {
        return true;
    }

    
    public boolean isRangeZoomable() {
        return true;
    }

    
    public int getSeriesCount() {
        int result = 0;
        XYDataset dataset = getDataset();
        if (dataset != null) {
            result = dataset.getSeriesCount();
        }
        return result;
    }

    
    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    
    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
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
            if (dataset != null) {
                XYItemRenderer renderer = getRenderer(datasetIndex);
                if (renderer == null) {
                    renderer = getRenderer(0);
                }
                if (renderer != null) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int i = 0; i < seriesCount; i++) {
                        if (renderer.isSeriesVisible(i)
                                && renderer.isSeriesVisibleInLegend(i)) {
                            LegendItem item = renderer.getLegendItem(
                                    datasetIndex, i);
                            if (item != null) {
                                result.add(item);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYPlot)) {
            return false;
        }
        XYPlot that = (XYPlot) obj;
        if (this.weight != that.weight) {
            return false;
        }
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!this.domainAxes.equals(that.domainAxes)) {
            return false;
        }
        if (!this.domainAxisLocations.equals(that.domainAxisLocations)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData
                != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (this.domainMinorGridlinesVisible
                != that.domainMinorGridlinesVisible) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible
                != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (this.domainZeroBaselineVisible != that.domainZeroBaselineVisible) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.domainCrosshairValue != that.domainCrosshairValue) {
            return false;
        }
        if (this.domainCrosshairLockedOnData
                != that.domainCrosshairLockedOnData) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!ObjectUtilities.equal(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.renderers, that.renderers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!this.rangeAxisLocations.equals(that.rangeAxisLocations)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.datasetToDomainAxesMap,
                that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.datasetToRangeAxesMap,
                that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainGridlineStroke,
                that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint,
                that.domainGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeGridlineStroke,
                that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint,
                that.rangeGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainMinorGridlineStroke,
                that.domainMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainMinorGridlinePaint,
                that.domainMinorGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeMinorGridlineStroke,
                that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeMinorGridlinePaint,
                that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainZeroBaselinePaint,
                that.domainZeroBaselinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainZeroBaselineStroke,
                that.domainZeroBaselineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeZeroBaselinePaint,
                that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeZeroBaselineStroke,
                that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainCrosshairStroke,
                that.domainCrosshairStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainCrosshairPaint,
                that.domainCrosshairPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeCrosshairStroke,
                that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeCrosshairPaint,
                that.rangeCrosshairPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundDomainMarkers,
                that.foregroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundDomainMarkers,
                that.backgroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundRangeMarkers,
                that.foregroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundRangeMarkers,
                that.backgroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundDomainMarkers,
                that.foregroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundDomainMarkers,
                that.backgroundDomainMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.foregroundRangeMarkers,
                that.foregroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundRangeMarkers,
                that.backgroundRangeMarkers)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.annotations, that.annotations)) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainTickBandPaint,
                that.domainTickBandPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeTickBandPaint,
                that.rangeTickBandPaint)) {
            return false;
        }
        if (!this.quadrantOrigin.equals(that.quadrantOrigin)) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!PaintUtilities.equal(this.quadrantPaint[i],
                    that.quadrantPaint[i])) {
                return false;
            }
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {

        XYPlot clone = (XYPlot) super.clone();
        clone.domainAxes = (ObjectList) ObjectUtilities.clone(this.domainAxes);
        for (int i = 0; i < this.domainAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.domainAxes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.domainAxes.set(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }
        clone.domainAxisLocations = (ObjectList)
                this.domainAxisLocations.clone();

        clone.rangeAxes = (ObjectList) ObjectUtilities.clone(this.rangeAxes);
        for (int i = 0; i < this.rangeAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.rangeAxes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.rangeAxes.set(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }
        clone.rangeAxisLocations = (ObjectList) ObjectUtilities.clone(
                this.rangeAxisLocations);

        
        clone.datasets = (ObjectList) ObjectUtilities.clone(this.datasets);
        for (int i = 0; i < clone.datasets.size(); ++i) {
            XYDataset d = getDataset(i);
            if (d != null) {
                d.addChangeListener(clone);
            }
        }

        clone.datasetToDomainAxesMap = new TreeMap();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);

        clone.renderers = (ObjectList) ObjectUtilities.clone(this.renderers);
        for (int i = 0; i < this.renderers.size(); i++) {
            XYItemRenderer renderer2 = (XYItemRenderer) this.renderers.get(i);
            if (renderer2 instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) renderer2;
                clone.renderers.set(i, pc.clone());
            }
        }
        clone.foregroundDomainMarkers = (Map) ObjectUtilities.clone(
                this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = (Map) ObjectUtilities.clone(
                this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = (Map) ObjectUtilities.clone(
                this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = (Map) ObjectUtilities.clone(
                this.backgroundRangeMarkers);
        clone.annotations = (List) ObjectUtilities.deepClone(this.annotations);
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = (AxisSpace) ObjectUtilities.clone(
                    this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = (AxisSpace) ObjectUtilities.clone(
                    this.fixedRangeAxisSpace);
        }

        clone.quadrantOrigin = (Point2D) ObjectUtilities.clone(
                this.quadrantOrigin);
        clone.quadrantPaint = (Paint[]) this.quadrantPaint.clone();
        return clone;

    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.domainMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.rangeZeroBaselinePaint, stream);
        SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtilities.writePaint(this.domainTickBandPaint, stream);
        SerialUtilities.writePaint(this.rangeTickBandPaint, stream);
        SerialUtilities.writePoint2D(this.quadrantOrigin, stream);
        for (int i = 0; i < 4; i++) {
            SerialUtilities.writePaint(this.quadrantPaint[i], stream);
        }
        SerialUtilities.writeStroke(this.domainZeroBaselineStroke, stream);
        SerialUtilities.writePaint(this.domainZeroBaselinePaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.domainMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtilities.readPaint(stream);
        this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
        this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        this.domainTickBandPaint = SerialUtilities.readPaint(stream);
        this.rangeTickBandPaint = SerialUtilities.readPaint(stream);
        this.quadrantOrigin = SerialUtilities.readPoint2D(stream);
        this.quadrantPaint = new Paint[4];
        for (int i = 0; i < 4; i++) {
            this.quadrantPaint[i] = SerialUtilities.readPaint(stream);
        }

        this.domainZeroBaselineStroke = SerialUtilities.readStroke(stream);
        this.domainZeroBaselinePaint = SerialUtilities.readPaint(stream);

        
        
        int domainAxisCount = this.domainAxes.size();
        for (int i = 0; i < domainAxisCount; i++) {
            Axis axis = (Axis) this.domainAxes.get(i);
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        int rangeAxisCount = this.rangeAxes.size();
        for (int i = 0; i < rangeAxisCount; i++) {
            Axis axis = (Axis) this.rangeAxes.get(i);
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
            XYItemRenderer renderer = (XYItemRenderer) this.renderers.get(i);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }

    }

}
