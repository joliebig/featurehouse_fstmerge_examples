

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.PaintMap;
import org.jfree.chart.StrokeMap;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.KeyedValues;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBox;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;


public class PiePlot extends Plot implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -795612466005590431L;

    
    public static final double DEFAULT_INTERIOR_GAP = 0.08;

    
    public static final double MAX_INTERIOR_GAP = 0.40;

    
    public static final double DEFAULT_START_ANGLE = 90.0;

    
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif",
            Font.PLAIN, 10);

    
    public static final Paint DEFAULT_LABEL_PAINT = Color.black;

    
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255,
            255, 192);

    
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT = Color.black;

    
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(
            0.5f);

    
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT = new Color(151, 151,
            151, 128);

    
    public static final double DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW = 0.00001;

    
    private PieDataset dataset;

    
    private int pieIndex;

    
    private double interiorGap;

    
    private boolean circular;

    
    private double startAngle;

    
    private Rotation direction;

    
    private PaintMap sectionPaintMap;

    
    private transient Paint baseSectionPaint;

    
    private boolean autoPopulateSectionPaint;

    
    private boolean sectionOutlinesVisible;

    
    private PaintMap sectionOutlinePaintMap;

    
    private transient Paint baseSectionOutlinePaint;

    
    private boolean autoPopulateSectionOutlinePaint;

    
    private StrokeMap sectionOutlineStrokeMap;

    
    private transient Stroke baseSectionOutlineStroke;

    
    private boolean autoPopulateSectionOutlineStroke;

    
    private transient Paint shadowPaint = Color.gray;

    
    private double shadowXOffset = 4.0f;

    
    private double shadowYOffset = 4.0f;

    
    private Map explodePercentages;

    
    private PieSectionLabelGenerator labelGenerator;

    
    private Font labelFont;

    
    private transient Paint labelPaint;

    
    private transient Paint labelBackgroundPaint;

    
    private transient Paint labelOutlinePaint;

    
    private transient Stroke labelOutlineStroke;

    
    private transient Paint labelShadowPaint;

    
    private boolean simpleLabels = true;

    
    private RectangleInsets labelPadding;

    
    private RectangleInsets simpleLabelOffset;

    
    private double maximumLabelWidth = 0.14;

    
    private double labelGap = 0.025;

    
    private boolean labelLinksVisible;

    
    private PieLabelLinkStyle labelLinkStyle = PieLabelLinkStyle.STANDARD;

    
    private double labelLinkMargin = 0.025;

    
    private transient Paint labelLinkPaint = Color.black;

    
    private transient Stroke labelLinkStroke = new BasicStroke(0.5f);

    
    private AbstractPieLabelDistributor labelDistributor;

    
    private PieToolTipGenerator toolTipGenerator;

    
    private PieURLGenerator urlGenerator;

    
    private PieSectionLabelGenerator legendLabelGenerator;

    
    private PieSectionLabelGenerator legendLabelToolTipGenerator;

    
    private PieURLGenerator legendLabelURLGenerator;

    
    private boolean ignoreNullValues;

    
    private boolean ignoreZeroValues;

    
    private transient Shape legendItemShape;

    
    private double minimumArcAngleToDraw;

    
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.plot.LocalizationBundle");

    
    static final boolean DEBUG_DRAW_INTERIOR = false;

    
    static final boolean DEBUG_DRAW_LINK_AREA = false;

    
    static final boolean DEBUG_DRAW_PIE_AREA = false;

    
    public PiePlot() {
        this(null);
    }

    
    public PiePlot(PieDataset dataset) {
        super();
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.pieIndex = 0;

        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.circular = true;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW;

        this.sectionPaint = null;
        this.sectionPaintMap = new PaintMap();
        this.baseSectionPaint = Color.gray;
        this.autoPopulateSectionPaint = true;

        this.sectionOutlinesVisible = true;
        this.sectionOutlinePaint = null;
        this.sectionOutlinePaintMap = new PaintMap();
        this.baseSectionOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSectionOutlinePaint = false;

        this.sectionOutlineStroke = null;
        this.sectionOutlineStrokeMap = new StrokeMap();
        this.baseSectionOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSectionOutlineStroke = false;

        this.explodePercentages = new TreeMap();

        this.labelGenerator = new StandardPieSectionLabelGenerator();
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelBackgroundPaint = DEFAULT_LABEL_BACKGROUND_PAINT;
        this.labelOutlinePaint = DEFAULT_LABEL_OUTLINE_PAINT;
        this.labelOutlineStroke = DEFAULT_LABEL_OUTLINE_STROKE;
        this.labelShadowPaint = DEFAULT_LABEL_SHADOW_PAINT;
        this.labelLinksVisible = true;
        this.labelDistributor = new PieLabelDistributor(0);

        this.simpleLabels = false;
        this.simpleLabelOffset = new RectangleInsets(UnitType.RELATIVE, 0.18,
                0.18, 0.18, 0.18);
        this.labelPadding = new RectangleInsets(2, 2, 2, 2);

        this.toolTipGenerator = null;
        this.urlGenerator = null;
        this.legendLabelGenerator = new StandardPieSectionLabelGenerator();
        this.legendLabelToolTipGenerator = null;
        this.legendLabelURLGenerator = null;
        this.legendItemShape = Plot.DEFAULT_LEGEND_ITEM_CIRCLE;

        this.ignoreNullValues = false;
        this.ignoreZeroValues = false;
    }

    
    public PieDataset getDataset() {
        return this.dataset;
    }

    
    public void setDataset(PieDataset dataset) {
        
        
        PieDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }

    
    public int getPieIndex() {
        return this.pieIndex;
    }

    
    public void setPieIndex(int index) {
        this.pieIndex = index;
    }

    
    public double getStartAngle() {
        return this.startAngle;
    }

    
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        fireChangeEvent();
    }

    
    public Rotation getDirection() {
        return this.direction;
    }

    
    public void setDirection(Rotation direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Null 'direction' argument.");
        }
        this.direction = direction;
        fireChangeEvent();

    }

    
    public double getInteriorGap() {
        return this.interiorGap;
    }

    
    public void setInteriorGap(double percent) {

        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException(
                "Invalid 'percent' (" + percent + ") argument.");
        }

        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            fireChangeEvent();
        }

    }

    
    public boolean isCircular() {
        return this.circular;
    }

    
    public void setCircular(boolean flag) {
        setCircular(flag, true);
    }

    
    public void setCircular(boolean circular, boolean notify) {
        this.circular = circular;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getIgnoreNullValues() {
        return this.ignoreNullValues;
    }

    
    public void setIgnoreNullValues(boolean flag) {
        this.ignoreNullValues = flag;
        fireChangeEvent();
    }

    
    public boolean getIgnoreZeroValues() {
        return this.ignoreZeroValues;
    }

    
    public void setIgnoreZeroValues(boolean flag) {
        this.ignoreZeroValues = flag;
        fireChangeEvent();
    }

    

    
    protected Paint lookupSectionPaint(Comparable key) {
        return lookupSectionPaint(key, getAutoPopulateSectionPaint());
    }

    
    protected Paint lookupSectionPaint(Comparable key, boolean autoPopulate) {

        
        Paint result = getSectionPaint();
        if (result != null) {
            return result;
        }

        
        result = this.sectionPaintMap.getPaint(key);
        if (result != null) {
            return result;
        }

        
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextPaint();
                this.sectionPaintMap.put(key, result);
            }
            else {
                result = this.baseSectionPaint;
            }
        }
        else {
            result = this.baseSectionPaint;
        }
        return result;
    }

    
    public Paint getSectionPaint() {
        return this.sectionPaint;
    }

    
    public void setSectionPaint(Paint paint) {
        this.sectionPaint = paint;
        fireChangeEvent();
    }

    
    protected Comparable getSectionKey(int section) {
        Comparable key = null;
        if (this.dataset != null) {
            if (section >= 0 && section < this.dataset.getItemCount()) {
                key = this.dataset.getKey(section);
            }
        }
        if (key == null) {
            key = new Integer(section);
        }
        return key;
    }

    
    public Paint getSectionPaint(Comparable key) {
        
        return this.sectionPaintMap.getPaint(key);
    }

    
    public void setSectionPaint(Comparable key, Paint paint) {
        
        this.sectionPaintMap.put(key, paint);
        fireChangeEvent();
    }

    
    public void clearSectionPaints(boolean notify) {
        this.sectionPaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Paint getBaseSectionPaint() {
        return this.baseSectionPaint;
    }

    
    public void setBaseSectionPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSectionPaint = paint;
        fireChangeEvent();
    }

    
    public boolean getAutoPopulateSectionPaint() {
        return this.autoPopulateSectionPaint;
    }

    
    public void setAutoPopulateSectionPaint(boolean auto) {
        this.autoPopulateSectionPaint = auto;
        fireChangeEvent();
    }

    

    
    public boolean getSectionOutlinesVisible() {
        return this.sectionOutlinesVisible;
    }

    
    public void setSectionOutlinesVisible(boolean visible) {
        this.sectionOutlinesVisible = visible;
        fireChangeEvent();
    }

    
    protected Paint lookupSectionOutlinePaint(Comparable key) {
        return lookupSectionOutlinePaint(key,
                getAutoPopulateSectionOutlinePaint());
    }

    
    protected Paint lookupSectionOutlinePaint(Comparable key,
            boolean autoPopulate) {

        
        Paint result = getSectionOutlinePaint();
        if (result != null) {
            return result;
        }

        
        result = this.sectionOutlinePaintMap.getPaint(key);
        if (result != null) {
            return result;
        }

        
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlinePaint();
                this.sectionOutlinePaintMap.put(key, result);
            }
            else {
                result = this.baseSectionOutlinePaint;
            }
        }
        else {
            result = this.baseSectionOutlinePaint;
        }
        return result;
    }

    
    public Paint getSectionOutlinePaint(Comparable key) {
        
        return this.sectionOutlinePaintMap.getPaint(key);
    }

    
    public void setSectionOutlinePaint(Comparable key, Paint paint) {
        
        this.sectionOutlinePaintMap.put(key, paint);
        fireChangeEvent();
    }

    
    public void clearSectionOutlinePaints(boolean notify) {
        this.sectionOutlinePaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Paint getBaseSectionOutlinePaint() {
        return this.baseSectionOutlinePaint;
    }

    
    public void setBaseSectionOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSectionOutlinePaint = paint;
        fireChangeEvent();
    }

    
    public boolean getAutoPopulateSectionOutlinePaint() {
        return this.autoPopulateSectionOutlinePaint;
    }

    
    public void setAutoPopulateSectionOutlinePaint(boolean auto) {
        this.autoPopulateSectionOutlinePaint = auto;
        fireChangeEvent();
    }

    

    
    protected Stroke lookupSectionOutlineStroke(Comparable key) {
        return lookupSectionOutlineStroke(key,
                getAutoPopulateSectionOutlineStroke());
    }

    
    protected Stroke lookupSectionOutlineStroke(Comparable key,
            boolean autoPopulate) {

        
        Stroke result = getSectionOutlineStroke();
        if (result != null) {
            return result;
        }

        
        result = this.sectionOutlineStrokeMap.getStroke(key);
        if (result != null) {
            return result;
        }

        
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlineStroke();
                this.sectionOutlineStrokeMap.put(key, result);
            }
            else {
                result = this.baseSectionOutlineStroke;
            }
        }
        else {
            result = this.baseSectionOutlineStroke;
        }
        return result;
    }

    
    public Stroke getSectionOutlineStroke(Comparable key) {
        
        return this.sectionOutlineStrokeMap.getStroke(key);
    }

    
    public void setSectionOutlineStroke(Comparable key, Stroke stroke) {
        
        this.sectionOutlineStrokeMap.put(key, stroke);
        fireChangeEvent();
    }

    
    public void clearSectionOutlineStrokes(boolean notify) {
        this.sectionOutlineStrokeMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Stroke getBaseSectionOutlineStroke() {
        return this.baseSectionOutlineStroke;
    }

    
    public void setBaseSectionOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseSectionOutlineStroke = stroke;
        fireChangeEvent();
    }

    
    public boolean getAutoPopulateSectionOutlineStroke() {
        return this.autoPopulateSectionOutlineStroke;
    }

    
    public void setAutoPopulateSectionOutlineStroke(boolean auto) {
        this.autoPopulateSectionOutlineStroke = auto;
        fireChangeEvent();
    }

    
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    
    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
        fireChangeEvent();
    }

    
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    
    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        fireChangeEvent();
    }

    
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    
    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        fireChangeEvent();
    }

    
    public double getExplodePercent(Comparable key) {
        double result = 0.0;
        if (this.explodePercentages != null) {
            Number percent = (Number) this.explodePercentages.get(key);
            if (percent != null) {
                result = percent.doubleValue();
            }
        }
        return result;
    }

    
    public void setExplodePercent(Comparable key, double percent) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        if (this.explodePercentages == null) {
            this.explodePercentages = new TreeMap();
        }
        this.explodePercentages.put(key, new Double(percent));
        fireChangeEvent();
    }

    
    public double getMaximumExplodePercent() {
        if (this.dataset == null) {
            return 0.0;
        }
        double result = 0.0;
        Iterator iterator = this.dataset.getKeys().iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Number explode = (Number) this.explodePercentages.get(key);
            if (explode != null) {
                result = Math.max(result, explode.doubleValue());
            }
        }
        return result;
    }

    
    public PieSectionLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    
    public void setLabelGenerator(PieSectionLabelGenerator generator) {
        this.labelGenerator = generator;
        fireChangeEvent();
    }

    
    public double getLabelGap() {
        return this.labelGap;
    }

    
    public void setLabelGap(double gap) {
        this.labelGap = gap;
        fireChangeEvent();
    }

    
    public double getMaximumLabelWidth() {
        return this.maximumLabelWidth;
    }

    
    public void setMaximumLabelWidth(double width) {
        this.maximumLabelWidth = width;
        fireChangeEvent();
    }

    
    public boolean getLabelLinksVisible() {
        return this.labelLinksVisible;
    }

    
    public void setLabelLinksVisible(boolean visible) {
        this.labelLinksVisible = visible;
        fireChangeEvent();
    }

    
    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    
    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Null 'style' argument.");
        }
        this.labelLinkStyle = style;
        fireChangeEvent();
    }

    
    public double getLabelLinkMargin() {
        return this.labelLinkMargin;
    }

    
    public void setLabelLinkMargin(double margin) {
        this.labelLinkMargin = margin;
        fireChangeEvent();
    }

    
    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    
    public void setLabelLinkPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelLinkPaint = paint;
        fireChangeEvent();
    }

    
    public Stroke getLabelLinkStroke() {
        return this.labelLinkStroke;
    }

    
    public void setLabelLinkStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.labelLinkStroke = stroke;
        fireChangeEvent();
    }

    
    protected double getLabelLinkDepth() {
        return 0.1;
    }

    
    public Font getLabelFont() {
        return this.labelFont;
    }

    
    public void setLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.labelFont = font;
        fireChangeEvent();
    }

    
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    
    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getLabelBackgroundPaint() {
        return this.labelBackgroundPaint;
    }

    
    public void setLabelBackgroundPaint(Paint paint) {
        this.labelBackgroundPaint = paint;
        fireChangeEvent();
    }

    
    public Paint getLabelOutlinePaint() {
        return this.labelOutlinePaint;
    }

    
    public void setLabelOutlinePaint(Paint paint) {
        this.labelOutlinePaint = paint;
        fireChangeEvent();
    }

    
    public Stroke getLabelOutlineStroke() {
        return this.labelOutlineStroke;
    }

    
    public void setLabelOutlineStroke(Stroke stroke) {
        this.labelOutlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getLabelShadowPaint() {
        return this.labelShadowPaint;
    }

    
    public void setLabelShadowPaint(Paint paint) {
        this.labelShadowPaint = paint;
        fireChangeEvent();
    }

    
    public RectangleInsets getLabelPadding() {
        return this.labelPadding;
    }

    
    public void setLabelPadding(RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.labelPadding = padding;
        fireChangeEvent();
    }

    
    public boolean getSimpleLabels() {
        return this.simpleLabels;
    }

    
    public void setSimpleLabels(boolean simple) {
        this.simpleLabels = simple;
        fireChangeEvent();
    }

    
    public RectangleInsets getSimpleLabelOffset() {
        return this.simpleLabelOffset;
    }

    
    public void setSimpleLabelOffset(RectangleInsets offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Null 'offset' argument.");
        }
        this.simpleLabelOffset = offset;
        fireChangeEvent();
    }

    
    public AbstractPieLabelDistributor getLabelDistributor() {
        return this.labelDistributor;
    }

    
    public void setLabelDistributor(AbstractPieLabelDistributor distributor) {
        if (distributor == null) {
            throw new IllegalArgumentException("Null 'distributor' argument.");
        }
        this.labelDistributor = distributor;
        fireChangeEvent();
    }

    
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    
    public void setToolTipGenerator(PieToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    
    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    
    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
        fireChangeEvent();
    }

    
    public double getMinimumArcAngleToDraw() {
        return this.minimumArcAngleToDraw;
    }

    
    public void setMinimumArcAngleToDraw(double angle) {
        this.minimumArcAngleToDraw = angle;
    }

    
    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    
    public void setLegendItemShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendItemShape = shape;
        fireChangeEvent();
    }

    
    public PieSectionLabelGenerator getLegendLabelGenerator() {
        return this.legendLabelGenerator;
    }

    
    public void setLegendLabelGenerator(PieSectionLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendLabelGenerator = generator;
        fireChangeEvent();
    }

    
    public PieSectionLabelGenerator getLegendLabelToolTipGenerator() {
        return this.legendLabelToolTipGenerator;
    }

    
    public void setLegendLabelToolTipGenerator(
            PieSectionLabelGenerator generator) {
        this.legendLabelToolTipGenerator = generator;
        fireChangeEvent();
    }

    
    public PieURLGenerator getLegendLabelURLGenerator() {
        return this.legendLabelURLGenerator;
    }

    
    public void setLegendLabelURLGenerator(PieURLGenerator generator) {
        this.legendLabelURLGenerator = generator;
        fireChangeEvent();
    }

    
    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea,
            PiePlot plot, Integer index, PlotRenderingInfo info) {

        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
        if (this.dataset != null) {
            state.setTotal(DatasetUtilities.calculatePieDatasetTotal(
                    plot.getDataset()));
        }
        state.setLatestAngle(plot.getStartAngle());
        return state;

    }

    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState, PlotRenderingInfo info) {

        
        RectangleInsets insets = getInsets();
        insets.trim(area);

        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }

        drawBackground(g2, area);
        drawOutline(g2, area);

        Shape savedClip = g2.getClip();
        g2.clip(area);

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawPie(g2, area, info);
        }
        else {
            drawNoDataMessage(g2, area);
        }

        g2.setClip(savedClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, area);

    }

    
    protected void drawPie(Graphics2D g2, Rectangle2D plotArea,
                           PlotRenderingInfo info) {

        PiePlotState state = initialise(g2, plotArea, this, null, info);

        
        double labelReserve = 0.0;
        if (this.labelGenerator != null && !this.simpleLabels) {
            labelReserve = this.labelGap + this.maximumLabelWidth;
        }
        double gapHorizontal = plotArea.getWidth() * (this.interiorGap
                + labelReserve) * 2.0;
        double gapVertical = plotArea.getHeight() * this.interiorGap * 2.0;


        if (DEBUG_DRAW_INTERIOR) {
            double hGap = plotArea.getWidth() * this.interiorGap;
            double vGap = plotArea.getHeight() * this.interiorGap;

            double igx1 = plotArea.getX() + hGap;
            double igx2 = plotArea.getMaxX() - hGap;
            double igy1 = plotArea.getY() + vGap;
            double igy2 = plotArea.getMaxY() - vGap;
            g2.setPaint(Color.gray);
            g2.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1,
                    igy2 - igy1));
        }

        double linkX = plotArea.getX() + gapHorizontal / 2;
        double linkY = plotArea.getY() + gapVertical / 2;
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;

        
        if (this.circular) {
            double min = Math.min(linkW, linkH) / 2;
            linkX = (linkX + linkX + linkW) / 2 - min;
            linkY = (linkY + linkY + linkH) / 2 - min;
            linkW = 2 * min;
            linkH = 2 * min;
        }

        
        
        Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW,
                linkH);
        state.setLinkArea(linkArea);

        if (DEBUG_DRAW_LINK_AREA) {
            g2.setPaint(Color.blue);
            g2.draw(linkArea);
            g2.setPaint(Color.yellow);
            g2.draw(new Ellipse2D.Double(linkArea.getX(), linkArea.getY(),
                    linkArea.getWidth(), linkArea.getHeight()));
        }

        
        
        
        double lm = 0.0;
        if (!this.simpleLabels) {
            lm = this.labelLinkMargin;
        }
        double hh = linkArea.getWidth() * lm * 2.0;
        double vv = linkArea.getHeight() * lm * 2.0;
        Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0,
                linkY + vv / 2.0, linkW - hh, linkH - vv);

        state.setExplodedPieArea(explodeArea);

        
        
        
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);

        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX()
                + h1 / 2.0, explodeArea.getY() + v1 / 2.0,
                explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);

        if (DEBUG_DRAW_PIE_AREA) {
            g2.setPaint(Color.green);
            g2.draw(pieArea);
        }
        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY());
        state.setPieWRadius(pieArea.getWidth() / 2.0);
        state.setPieHRadius(pieArea.getHeight() / 2.0);

        
        if ((this.dataset != null) && (this.dataset.getKeys().size() > 0)) {

            List keys = this.dataset.getKeys();
            double totalValue = DatasetUtilities.calculatePieDatasetTotal(
                    this.dataset);

            int passesRequired = state.getPassesRequired();
            for (int pass = 0; pass < passesRequired; pass++) {
                double runningTotal = 0.0;
                for (int section = 0; section < keys.size(); section++) {
                    Number n = this.dataset.getValue(section);
                    if (n != null) {
                        double value = n.doubleValue();
                        if (value > 0.0) {
                            runningTotal += value;
                            drawItem(g2, section, explodeArea, state, pass);
                        }
                    }
                }
            }
            if (this.simpleLabels) {
                drawSimpleLabels(g2, keys, totalValue, plotArea, linkArea,
                        state);
            }
            else {
                drawLabels(g2, keys, totalValue, plotArea, linkArea, state);
            }

        }
        else {
            drawNoDataMessage(g2, plotArea);
        }
    }

    
    protected void drawItem(Graphics2D g2, int section, Rectangle2D dataArea,
                            PiePlotState state, int currentPass) {

        Number n = this.dataset.getValue(section);
        if (n == null) {
            return;
        }
        double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;

        if (this.direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        }
        else if (this.direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;
        }
        else {
            throw new IllegalStateException("Rotation type not recognised.");
        }

        double angle = (angle2 - angle1);
        if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
            double ep = 0.0;
            double mep = getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = getExplodePercent(section) / mep;
            }
            Rectangle2D arcBounds = getArcBounds(state.getPieArea(),
                    state.getExplodedPieArea(), angle1, angle, ep);
            Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle,
                    Arc2D.PIE);

            if (currentPass == 0) {
                if (this.shadowPaint != null) {
                    Shape shadowArc = ShapeUtilities.createTranslatedShape(
                            arc, (float) this.shadowXOffset,
                            (float) this.shadowYOffset);
                    g2.setPaint(this.shadowPaint);
                    g2.fill(shadowArc);
                }
            }
            else if (currentPass == 1) {
                Comparable key = getSectionKey(section);
                Paint paint = lookupSectionPaint(key);
                g2.setPaint(paint);
                g2.fill(arc);

                Paint outlinePaint = lookupSectionOutlinePaint(key);
                Stroke outlineStroke = lookupSectionOutlineStroke(key);
                if (this.sectionOutlinesVisible) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(arc);
                }

                
                
                if (state.getInfo() != null) {
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(
                                    this.dataset, key);
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(this.dataset,
                                    key, this.pieIndex);
                        }
                        PieSectionEntity entity = new PieSectionEntity(
                                arc, this.dataset, this.pieIndex, section, key,
                                tip, url);
                        entities.add(entity);
                    }
                }
            }
        }
        state.setLatestAngle(angle2);
    }

    
    protected void drawSimpleLabels(Graphics2D g2, List keys,
            double totalValue, Rectangle2D plotArea, Rectangle2D pieArea,
            PiePlotState state) {

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.0f));

        RectangleInsets labelInsets = new RectangleInsets(UnitType.RELATIVE,
                0.18, 0.18, 0.18, 0.18);
        Rectangle2D labelsArea = labelInsets.createInsetRectangle(pieArea);
        double runningTotal = 0.0;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            boolean include = true;
            double v = 0.0;
            Number n = getDataset().getValue(key);
            if (n == null) {
                include = !getIgnoreNullValues();
            }
            else {
                v = n.doubleValue();
                include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
            }

            if (include) {
                runningTotal = runningTotal + v;
                
                
                double mid = getStartAngle() + (getDirection().getFactor()
                        * ((runningTotal - v / 2.0) * 360) / totalValue);

                Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(),
                        mid - getStartAngle(), Arc2D.OPEN);
                int x = (int) arc.getEndPoint().getX();
                int y = (int) arc.getEndPoint().getY();

                PieSectionLabelGenerator labelGenerator = getLabelGenerator();
                if (labelGenerator == null) {
                    continue;
                }
                String label = labelGenerator.generateSectionLabel(
                        this.dataset, key);
                if (label == null) {
                    continue;
                }
                g2.setFont(this.labelFont);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
                Rectangle2D out = this.labelPadding.createOutsetRectangle(
                        bounds);
                Shape bg = ShapeUtilities.createTranslatedShape(out,
                        x - bounds.getCenterX(), y - bounds.getCenterY());
                if (this.labelShadowPaint != null) {
                    Shape shadow = ShapeUtilities.createTranslatedShape(bg,
                            this.shadowXOffset, this.shadowYOffset);
                    g2.setPaint(this.labelShadowPaint);
                    g2.fill(shadow);
                }
                if (this.labelBackgroundPaint != null) {
                    g2.setPaint(this.labelBackgroundPaint);
                    g2.fill(bg);
                }
                if (this.labelOutlinePaint != null
                        && this.labelOutlineStroke != null) {
                    g2.setPaint(this.labelOutlinePaint);
                    g2.setStroke(this.labelOutlineStroke);
                    g2.draw(bg);
                }

                g2.setPaint(this.labelPaint);
                g2.setFont(this.labelFont);
                TextUtilities.drawAlignedString(getLabelGenerator()
                        .generateSectionLabel(getDataset(), key), g2, x, y,
                        TextAnchor.CENTER);

            }
        }

        g2.setComposite(originalComposite);

    }

    
    protected void drawLabels(Graphics2D g2, List keys, double totalValue,
                              Rectangle2D plotArea, Rectangle2D linkArea,
                              PiePlotState state) {

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.0f));

        
        DefaultKeyedValues leftKeys = new DefaultKeyedValues();
        DefaultKeyedValues rightKeys = new DefaultKeyedValues();

        double runningTotal = 0.0;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            boolean include = true;
            double v = 0.0;
            Number n = this.dataset.getValue(key);
            if (n == null) {
                include = !this.ignoreNullValues;
            }
            else {
                v = n.doubleValue();
                include = this.ignoreZeroValues ? v > 0.0 : v >= 0.0;
            }

            if (include) {
                runningTotal = runningTotal + v;
                
                
                double mid = this.startAngle + (this.direction.getFactor()
                        * ((runningTotal - v / 2.0) * 360) / totalValue);
                if (Math.cos(Math.toRadians(mid)) < 0.0) {
                    leftKeys.addValue(key, new Double(mid));
                }
                else {
                    rightKeys.addValue(key, new Double(mid));
                }
            }
        }

        g2.setFont(getLabelFont());

        
        
        double marginX = plotArea.getX() + this.interiorGap
                * plotArea.getWidth();
        double gap = plotArea.getWidth() * this.labelGap;
        double ww = linkArea.getX() - gap - marginX;
        float labelWidth = (float) this.labelPadding.trimWidth(ww);

        
        if (this.labelGenerator != null) {
            drawLeftLabels(leftKeys, g2, plotArea, linkArea, labelWidth,
                    state);
            drawRightLabels(rightKeys, g2, plotArea, linkArea, labelWidth,
                    state);
        }
        g2.setComposite(originalComposite);

    }

    
    protected void drawLeftLabels(KeyedValues leftKeys, Graphics2D g2,
                                  Rectangle2D plotArea, Rectangle2D linkArea,
                                  float maxLabelWidth, PiePlotState state) {

        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;
        for (int i = 0; i < leftKeys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(
                    this.dataset, leftKeys.getKey(i));
            if (label != null) {
                TextBlock block = TextUtilities.createTextBlock(label,
                        this.labelFont, this.labelPaint, maxLabelWidth,
                        new G2TextMeasurer(g2));
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                labelBox.setShadowPaint(this.labelShadowPaint);
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(
                        leftKeys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() - Math.sin(theta)
                               * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);

                this.labelDistributor.addPieLabelRecord(new PieLabelRecord(
                        leftKeys.getKey(i), theta, baseY, labelBox, hh,
                        lGap / 2.0 + lGap / 2.0 * -Math.cos(theta), 1.0
                        - getLabelLinkDepth()
                        + getExplodePercent(leftKeys.getKey(i))));
            }
        }
        double hh = plotArea.getHeight();
        double gap = hh * getInteriorGap();
        this.labelDistributor.distributeLabels(plotArea.getMinY() + gap,
                hh - 2 * gap);
        for (int i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawLeftLabel(g2, state,
                    this.labelDistributor.getPieLabelRecord(i));
        }
    }

    
    protected void drawRightLabels(KeyedValues keys, Graphics2D g2,
                                   Rectangle2D plotArea, Rectangle2D linkArea,
                                   float maxLabelWidth, PiePlotState state) {

        
        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;

        for (int i = 0; i < keys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(
                    this.dataset, keys.getKey(i));

            if (label != null) {
                TextBlock block = TextUtilities.createTextBlock(label,
                        this.labelFont, this.labelPaint, maxLabelWidth,
                        new G2TextMeasurer(g2));
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                labelBox.setShadowPaint(this.labelShadowPaint);
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(keys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY()
                              - Math.sin(theta) * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);
                this.labelDistributor.addPieLabelRecord(new PieLabelRecord(
                        keys.getKey(i), theta, baseY, labelBox, hh,
                        lGap / 2.0 + lGap / 2.0 * Math.cos(theta),
                        1.0 - getLabelLinkDepth()
                        + getExplodePercent(keys.getKey(i))));
            }
        }
        double hh = plotArea.getHeight();
        double gap = hh * getInteriorGap();
        this.labelDistributor.distributeLabels(plotArea.getMinY() + gap,
                hh - 2 * gap);
        for (int i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawRightLabel(g2, state,
                    this.labelDistributor.getPieLabelRecord(i));
        }

    }

    
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset == null) {
            return result;
        }
        List keys = this.dataset.getKeys();
        int section = 0;
        Shape shape = getLegendItemShape();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Number n = this.dataset.getValue(key);
            boolean include = true;
            if (n == null) {
                include = !this.ignoreNullValues;
            }
            else {
                double v = n.doubleValue();
                if (v == 0.0) {
                    include = !this.ignoreZeroValues;
                }
                else {
                    include = v > 0.0;
                }
            }
            if (include) {
                String label = this.legendLabelGenerator.generateSectionLabel(
                        this.dataset, key);
                if (label != null) {
                    String description = label;
                    String toolTipText = null;
                    if (this.legendLabelToolTipGenerator != null) {
                        toolTipText = this.legendLabelToolTipGenerator
                                .generateSectionLabel(this.dataset, key);
                    }
                    String urlText = null;
                    if (this.legendLabelURLGenerator != null) {
                        urlText = this.legendLabelURLGenerator.generateURL(
                                this.dataset, key, this.pieIndex);
                    }
                    Paint paint = lookupSectionPaint(key);
                    Paint outlinePaint = lookupSectionOutlinePaint(key);
                    Stroke outlineStroke = lookupSectionOutlineStroke(key);
                    LegendItem item = new LegendItem(label, description,
                            toolTipText, urlText, true, shape, true, paint,
                            true, outlinePaint, outlineStroke,
                            false,          
                            new Line2D.Float(), new BasicStroke(), Color.black);
                    item.setDataset(getDataset());
                    item.setSeriesIndex(this.dataset.getIndex(key));
                    item.setSeriesKey(key);
                    result.add(item);
                }
                section++;
            }
            else {
                section++;
            }
        }
        return result;
    }

    
    public String getPlotType() {
        return localizationResources.getString("Pie_Plot");
    }

    
    protected Rectangle2D getArcBounds(Rectangle2D unexploded,
                                       Rectangle2D exploded,
                                       double angle, double extent,
                                       double explodePercent) {

        if (explodePercent == 0.0) {
            return unexploded;
        }
        else {
            Arc2D arc1 = new Arc2D.Double(unexploded, angle, extent / 2,
                    Arc2D.OPEN);
            Point2D point1 = arc1.getEndPoint();
            Arc2D.Double arc2 = new Arc2D.Double(exploded, angle, extent / 2,
                    Arc2D.OPEN);
            Point2D point2 = arc2.getEndPoint();
            double deltaX = (point1.getX() - point2.getX()) * explodePercent;
            double deltaY = (point1.getY() - point2.getY()) * explodePercent;
            return new Rectangle2D.Double(unexploded.getX() - deltaX,
                    unexploded.getY() - deltaY, unexploded.getWidth(),
                    unexploded.getHeight());
        }
    }

    
    protected void drawLeftLabel(Graphics2D g2, PiePlotState state,
                                 PieLabelRecord record) {

        double anchorX = state.getLinkArea().getMinX();
        double targetX = anchorX - record.getGap();
        double targetY = record.getAllocatedY();

        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta)
                    * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta)
                    * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta)
                    * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta)
                    * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            }
            else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            }
            else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D .Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY,
                        linkX, linkY);
                g2.draw(c);
            }
        }
        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.RIGHT);

    }

    
    protected void drawRightLabel(Graphics2D g2, PiePlotState state,
                                  PieLabelRecord record) {

        double anchorX = state.getLinkArea().getMaxX();
        double targetX = anchorX + record.getGap();
        double targetY = record.getAllocatedY();

        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta)
                    * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta)
                    * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta)
                    * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta)
                    * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            }
            else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            }
            else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D .Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY,
                        linkX, linkY);
                g2.draw(c);
            }
        }

        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.LEFT);

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        PiePlot that = (PiePlot) obj;
        if (this.pieIndex != that.pieIndex) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.circular != that.circular) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (this.direction != that.direction) {
            return false;
        }
        if (this.ignoreZeroValues != that.ignoreZeroValues) {
            return false;
        }
        if (this.ignoreNullValues != that.ignoreNullValues) {
            return false;
        }
        if (!PaintUtilities.equal(this.sectionPaint, that.sectionPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionPaintMap,
                that.sectionPaintMap)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseSectionPaint,
                that.baseSectionPaint)) {
            return false;
        }
        if (this.sectionOutlinesVisible != that.sectionOutlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.sectionOutlinePaint,
                that.sectionOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlinePaintMap,
                that.sectionOutlinePaintMap)) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.baseSectionOutlinePaint, that.baseSectionOutlinePaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlineStroke,
                that.sectionOutlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.sectionOutlineStrokeMap,
                that.sectionOutlineStrokeMap)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseSectionOutlineStroke, that.baseSectionOutlineStroke
        )) {
            return false;
        }
        if (!PaintUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (!(this.shadowXOffset == that.shadowXOffset)) {
            return false;
        }
        if (!(this.shadowYOffset == that.shadowYOffset)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.explodePercentages,
                that.explodePercentages)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelGenerator,
                that.labelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelBackgroundPaint,
                that.labelBackgroundPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelOutlinePaint,
                that.labelOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelOutlineStroke,
                that.labelOutlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelShadowPaint,
                that.labelShadowPaint)) {
            return false;
        }
        if (this.simpleLabels != that.simpleLabels) {
            return false;
        }
        if (!this.simpleLabelOffset.equals(that.simpleLabelOffset)) {
            return false;
        }
        if (!this.labelPadding.equals(that.labelPadding)) {
            return false;
        }
        if (!(this.maximumLabelWidth == that.maximumLabelWidth)) {
            return false;
        }
        if (!(this.labelGap == that.labelGap)) {
            return false;
        }
        if (!(this.labelLinkMargin == that.labelLinkMargin)) {
            return false;
        }
        if (this.labelLinksVisible != that.labelLinksVisible) {
            return false;
        }
        if (!this.labelLinkStyle.equals(that.labelLinkStyle)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelLinkStroke,
                that.labelLinkStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator,
                that.toolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlGenerator, that.urlGenerator)) {
            return false;
        }
        if (!(this.minimumArcAngleToDraw == that.minimumArcAngleToDraw)) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendLabelGenerator,
                that.legendLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendLabelToolTipGenerator,
                that.legendLabelToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendLabelURLGenerator,
                that.legendLabelURLGenerator)) {
            return false;
        }
        if (this.autoPopulateSectionPaint != that.autoPopulateSectionPaint) {
            return false;
        }
        if (this.autoPopulateSectionOutlinePaint
                != that.autoPopulateSectionOutlinePaint) {
            return false;
        }
        if (this.autoPopulateSectionOutlineStroke
                != that.autoPopulateSectionOutlineStroke) {
            return false;
        }
        
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        PiePlot clone = (PiePlot) super.clone();
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        if (this.urlGenerator instanceof PublicCloneable) {
            clone.urlGenerator = (PieURLGenerator) ObjectUtilities.clone(
                    this.urlGenerator);
        }
        clone.legendItemShape = ShapeUtilities.clone(this.legendItemShape);
        if (this.legendLabelGenerator != null) {
            clone.legendLabelGenerator = (PieSectionLabelGenerator)
                    ObjectUtilities.clone(this.legendLabelGenerator);
        }
        if (this.legendLabelToolTipGenerator != null) {
            clone.legendLabelToolTipGenerator = (PieSectionLabelGenerator)
                    ObjectUtilities.clone(this.legendLabelToolTipGenerator);
        }
        if (this.legendLabelURLGenerator instanceof PublicCloneable) {
            clone.legendLabelURLGenerator = (PieURLGenerator)
                    ObjectUtilities.clone(this.legendLabelURLGenerator);
        }
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.sectionPaint, stream);
        SerialUtilities.writePaint(this.baseSectionPaint, stream);
        SerialUtilities.writePaint(this.sectionOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSectionOutlinePaint, stream);
        SerialUtilities.writeStroke(this.sectionOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSectionOutlineStroke, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writePaint(this.labelBackgroundPaint, stream);
        SerialUtilities.writePaint(this.labelOutlinePaint, stream);
        SerialUtilities.writeStroke(this.labelOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelShadowPaint, stream);
        SerialUtilities.writePaint(this.labelLinkPaint, stream);
        SerialUtilities.writeStroke(this.labelLinkStroke, stream);
        SerialUtilities.writeShape(this.legendItemShape, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.sectionPaint = SerialUtilities.readPaint(stream);
        this.baseSectionPaint = SerialUtilities.readPaint(stream);
        this.sectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSectionOutlinePaint = SerialUtilities.readPaint(stream);
        this.sectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSectionOutlineStroke = SerialUtilities.readStroke(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.labelBackgroundPaint = SerialUtilities.readPaint(stream);
        this.labelOutlinePaint = SerialUtilities.readPaint(stream);
        this.labelOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelShadowPaint = SerialUtilities.readPaint(stream);
        this.labelLinkPaint = SerialUtilities.readPaint(stream);
        this.labelLinkStroke = SerialUtilities.readStroke(stream);
        this.legendItemShape = SerialUtilities.readShape(stream);
    }

    

    
    private transient Paint sectionPaint;

    
    private transient Paint sectionOutlinePaint;

    
    private transient Stroke sectionOutlineStroke;

    
    public Paint getSectionPaint(int section) {
        Comparable key = getSectionKey(section);
        return getSectionPaint(key);
    }

    
    public void setSectionPaint(int section, Paint paint) {
        Comparable key = getSectionKey(section);
        setSectionPaint(key, paint);
    }

    
    public Paint getSectionOutlinePaint() {
        return this.sectionOutlinePaint;
    }

    
    public void setSectionOutlinePaint(Paint paint) {
        this.sectionOutlinePaint = paint;
        fireChangeEvent();
    }

    
    public Paint getSectionOutlinePaint(int section) {
        Comparable key = getSectionKey(section);
        return getSectionOutlinePaint(key);
    }

    
    public void setSectionOutlinePaint(int section, Paint paint) {
        Comparable key = getSectionKey(section);
        setSectionOutlinePaint(key, paint);
    }

    
    public Stroke getSectionOutlineStroke() {
        return this.sectionOutlineStroke;
    }

    
    public void setSectionOutlineStroke(Stroke stroke) {
        this.sectionOutlineStroke = stroke;
        fireChangeEvent();
    }

    
    public Stroke getSectionOutlineStroke(int section) {
        Comparable key = getSectionKey(section);
        return getSectionOutlineStroke(key);
    }

    
    public void setSectionOutlineStroke(int section, Stroke stroke) {
        Comparable key = getSectionKey(section);
        setSectionOutlineStroke(key, stroke);
    }

    
    public double getExplodePercent(int section) {
        Comparable key = getSectionKey(section);
        return getExplodePercent(key);
    }

    
    public void setExplodePercent(int section, double percent) {
        Comparable key = getSectionKey(section);
        setExplodePercent(key, percent);
    }

}
