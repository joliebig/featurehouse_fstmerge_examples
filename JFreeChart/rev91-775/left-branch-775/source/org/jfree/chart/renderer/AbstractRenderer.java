

package org.jfree.chart.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeList;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.StrokeList;


public abstract class AbstractRenderer implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -828267569428206075L;
    
    
    public static final Double ZERO = new Double(0.0);
    
    
    public static final Paint DEFAULT_PAINT = Color.blue;

    
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);

    
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);

    
    public static final Shape DEFAULT_SHAPE 
            = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);

    
    public static final Font DEFAULT_VALUE_LABEL_FONT 
            = new Font("SansSerif", Font.PLAIN, 10);

    
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = Color.black;

    
    private Boolean seriesVisible;
    
    
    private BooleanList seriesVisibleList;

    
    private boolean baseSeriesVisible;
    
    
    private Boolean seriesVisibleInLegend;
    
    
    private BooleanList seriesVisibleInLegendList;

    
    private boolean baseSeriesVisibleInLegend;
        
    
    private transient Paint paint;

    
    private PaintList paintList;

    
    private boolean autoPopulateSeriesPaint;
    
    
    private transient Paint basePaint;

    
    private transient Paint fillPaint;

    
    private PaintList fillPaintList;
    
    
    private boolean autoPopulateSeriesFillPaint;

    
    private transient Paint baseFillPaint;

    
    private transient Paint outlinePaint;

    
    private PaintList outlinePaintList;

    
    private boolean autoPopulateSeriesOutlinePaint;
    
    
    private transient Paint baseOutlinePaint;

    
    private transient Stroke stroke;

    
    private StrokeList strokeList;

    
    private boolean autoPopulateSeriesStroke;

    
    private transient Stroke baseStroke;

    
    private transient Stroke outlineStroke;

    
    private StrokeList outlineStrokeList;

    
    private transient Stroke baseOutlineStroke;

    
    private boolean autoPopulateSeriesOutlineStroke;

    
    private transient Shape shape;

    
    private ShapeList shapeList;
    
    
    private boolean autoPopulateSeriesShape;

    
    private transient Shape baseShape;

    
    private Boolean itemLabelsVisible;

    
    private BooleanList itemLabelsVisibleList;

    
    private Boolean baseItemLabelsVisible;

    
    private Font itemLabelFont;

    
    private ObjectList itemLabelFontList;

    
    private Font baseItemLabelFont;

    
    private transient Paint itemLabelPaint;

    
    private PaintList itemLabelPaintList;

    
    private transient Paint baseItemLabelPaint;

    
    private ItemLabelPosition positiveItemLabelPosition;
    
    
    private ObjectList positiveItemLabelPositionList;
    
    
    private ItemLabelPosition basePositiveItemLabelPosition;
    
    
    private ItemLabelPosition negativeItemLabelPosition;
    
    
    private ObjectList negativeItemLabelPositionList;
    
    
    private ItemLabelPosition baseNegativeItemLabelPosition;

    
    private double itemLabelAnchorOffset = 2.0;

    
    private Boolean createEntities;

    
    private BooleanList createEntitiesList;

    
    private boolean baseCreateEntities;
    
    
    private transient EventListenerList listenerList;

    
    private transient RendererChangeEvent event;
    
    
    public AbstractRenderer() {

        this.seriesVisible = null;
        this.seriesVisibleList = new BooleanList();
        this.baseSeriesVisible = true;
        
        this.seriesVisibleInLegend = null;
        this.seriesVisibleInLegendList = new BooleanList();
        this.baseSeriesVisibleInLegend = true;

        this.paint = null;
        this.paintList = new PaintList();
        this.basePaint = DEFAULT_PAINT;
        this.autoPopulateSeriesPaint = true;

        this.fillPaint = null;
        this.fillPaintList = new PaintList();
        this.baseFillPaint = Color.white;
        this.autoPopulateSeriesFillPaint = false;

        this.outlinePaint = null;
        this.outlinePaintList = new PaintList();
        this.baseOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSeriesOutlinePaint = false;

        this.stroke = null;
        this.strokeList = new StrokeList();
        this.baseStroke = DEFAULT_STROKE;
        this.autoPopulateSeriesStroke = false;

        this.outlineStroke = null;
        this.outlineStrokeList = new StrokeList();
        this.baseOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSeriesOutlineStroke = false;

        this.shape = null;
        this.shapeList = new ShapeList();
        this.baseShape = DEFAULT_SHAPE;
        this.autoPopulateSeriesShape = true;

        this.itemLabelsVisible = null;
        this.itemLabelsVisibleList = new BooleanList();
        this.baseItemLabelsVisible = Boolean.FALSE;

        this.itemLabelFont = null;
        this.itemLabelFontList = new ObjectList();
        this.baseItemLabelFont = new Font("SansSerif", Font.PLAIN, 10);

        this.itemLabelPaint = null;
        this.itemLabelPaintList = new PaintList();
        this.baseItemLabelPaint = Color.black;

        this.positiveItemLabelPosition = null;
        this.positiveItemLabelPositionList = new ObjectList();
        this.basePositiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        
        this.negativeItemLabelPosition = null;
        this.negativeItemLabelPositionList = new ObjectList();
        this.baseNegativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);

        this.createEntities = null;
        this.createEntitiesList = new BooleanList();
        this.baseCreateEntities = true;
        
        this.listenerList = new EventListenerList();

    }

    
    public abstract DrawingSupplier getDrawingSupplier();
    
    

    
    public boolean getItemVisible(int series, int item) {
        return isSeriesVisible(series);
    }
    
    
    public boolean isSeriesVisible(int series) {
        boolean result = this.baseSeriesVisible;
        if (this.seriesVisible != null) {
            result = this.seriesVisible.booleanValue();   
        }
        else {
            Boolean b = this.seriesVisibleList.getBoolean(series);
            if (b != null) {
                result = b.booleanValue();   
            }
        }
        return result;
    }
    
    
    public Boolean getSeriesVisible() {
        return this.seriesVisible;   
    }
    
    
    public void setSeriesVisible(Boolean visible) {
         setSeriesVisible(visible, true);
    }
    
    
    public void setSeriesVisible(Boolean visible, boolean notify) {
        this.seriesVisible = visible;   
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Boolean getSeriesVisible(int series) {
        return this.seriesVisibleList.getBoolean(series);
    }
    
    
    public void setSeriesVisible(int series, Boolean visible) {
        setSeriesVisible(series, visible, true);
    }
    
    
    public void setSeriesVisible(int series, Boolean visible, boolean notify) {
        this.seriesVisibleList.setBoolean(series, visible);       
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getBaseSeriesVisible() {
        return this.baseSeriesVisible;
    }

    
    public void setBaseSeriesVisible(boolean visible) {
        
        setBaseSeriesVisible(visible, true);
    }
    
    
    public void setBaseSeriesVisible(boolean visible, boolean notify) {
        this.baseSeriesVisible = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    
    
    public boolean isSeriesVisibleInLegend(int series) {
        boolean result = this.baseSeriesVisibleInLegend;
        if (this.seriesVisibleInLegend != null) {
            result = this.seriesVisibleInLegend.booleanValue();   
        }
        else {
            Boolean b = this.seriesVisibleInLegendList.getBoolean(series);
            if (b != null) {
                result = b.booleanValue();   
            }
        }
        return result;
    }
    
    
    public Boolean getSeriesVisibleInLegend() {
        return this.seriesVisibleInLegend;   
    }
    
    
    public void setSeriesVisibleInLegend(Boolean visible) {
         setSeriesVisibleInLegend(visible, true);
    }
    
    
    public void setSeriesVisibleInLegend(Boolean visible, boolean notify) {
        this.seriesVisibleInLegend = visible;   
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Boolean getSeriesVisibleInLegend(int series) {
        return this.seriesVisibleInLegendList.getBoolean(series);
    }
    
    
    public void setSeriesVisibleInLegend(int series, Boolean visible) {
        setSeriesVisibleInLegend(series, visible, true);
    }
    
    
    public void setSeriesVisibleInLegend(int series, Boolean visible, 
                                         boolean notify) {
        this.seriesVisibleInLegendList.setBoolean(series, visible);       
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getBaseSeriesVisibleInLegend() {
        return this.baseSeriesVisibleInLegend;
    }

    
    public void setBaseSeriesVisibleInLegend(boolean visible) {
        
        setBaseSeriesVisibleInLegend(visible, true);
    }
    
    
    public void setBaseSeriesVisibleInLegend(boolean visible, boolean notify) {
        this.baseSeriesVisibleInLegend = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    
    
    public Paint getItemPaint(int row, int column) {
        return lookupSeriesPaint(row);
    }

    
    public Paint lookupSeriesPaint(int series) {

        
        if (this.paint != null) {
            return this.paint;
        }

        
        Paint seriesPaint = getSeriesPaint(series);
        if (seriesPaint == null && this.autoPopulateSeriesPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesPaint = supplier.getNextPaint();
                setSeriesPaint(series, seriesPaint, false);
            }
        }
        if (seriesPaint == null) {
            seriesPaint = this.basePaint;
        }
        return seriesPaint;

    }

    
    public void setPaint(Paint paint) {
        setPaint(paint, true);
    }
    
    
    public void setPaint(Paint paint, boolean notify) {
        this.paint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Paint getSeriesPaint(int series) {
        return this.paintList.getPaint(series);
    }
    
    
    public void setSeriesPaint(int series, Paint paint) {
        setSeriesPaint(series, paint, true);
    }
    
    
    public void setSeriesPaint(int series, Paint paint, boolean notify) {
        this.paintList.setPaint(series, paint);       
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Paint getBasePaint() {
        return this.basePaint;
    }

    
    public void setBasePaint(Paint paint) {
        
        setBasePaint(paint, true);
    }
    
    
    public void setBasePaint(Paint paint, boolean notify) {
        this.basePaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public boolean getAutoPopulateSeriesPaint() {
        return this.autoPopulateSeriesPaint;
    }
    
    
    public void setAutoPopulateSeriesPaint(boolean auto) {
        this.autoPopulateSeriesPaint = auto;
    }

    
    
    
    public Paint getItemFillPaint(int row, int column) {
        return lookupSeriesFillPaint(row);
    }

    
    public Paint lookupSeriesFillPaint(int series) {

        
        if (this.fillPaint != null) {
            return this.fillPaint;
        }

        
        Paint seriesFillPaint = getSeriesFillPaint(series);
        if (seriesFillPaint == null && this.autoPopulateSeriesFillPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesFillPaint = supplier.getNextFillPaint();
                setSeriesFillPaint(series, seriesFillPaint, false);
            }
        }
        if (seriesFillPaint == null) {
            seriesFillPaint = this.baseFillPaint;
        }
        return seriesFillPaint;

    }

    
    public Paint getSeriesFillPaint(int series) {
        return this.fillPaintList.getPaint(series);    
    }
    
    
    public void setSeriesFillPaint(int series, Paint paint) {
        setSeriesFillPaint(series, paint, true);
    }

        
    public void setSeriesFillPaint(int series, Paint paint, boolean notify) {
        this.fillPaintList.setPaint(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setFillPaint(Paint paint) {
        setFillPaint(paint, true);
    }

    
    public void setFillPaint(Paint paint, boolean notify) {
        this.fillPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Paint getBaseFillPaint() {
        return this.baseFillPaint;
    }

    
    public void setBaseFillPaint(Paint paint) {
        
        setBaseFillPaint(paint, true);
    }
    
    
    public void setBaseFillPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseFillPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getAutoPopulateSeriesFillPaint() {
        return this.autoPopulateSeriesFillPaint;
    }
    
    
    public void setAutoPopulateSeriesFillPaint(boolean auto) {
        this.autoPopulateSeriesFillPaint = auto;
    }

    
    
    
    public Paint getItemOutlinePaint(int row, int column) {
        return lookupSeriesOutlinePaint(row);
    }

    
    public Paint lookupSeriesOutlinePaint(int series) {

        
        if (this.outlinePaint != null) {
            return this.outlinePaint;
        }

        
        Paint seriesOutlinePaint = getSeriesOutlinePaint(series);
        if (seriesOutlinePaint == null && this.autoPopulateSeriesOutlinePaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesOutlinePaint = supplier.getNextOutlinePaint();
                setSeriesOutlinePaint(series, seriesOutlinePaint, false);
            }
        }
        if (seriesOutlinePaint == null) {
            seriesOutlinePaint = this.baseOutlinePaint;
        }
        return seriesOutlinePaint;

    }

    
    public Paint getSeriesOutlinePaint(int series) {
        return this.outlinePaintList.getPaint(series);    
    }
    
    
    public void setSeriesOutlinePaint(int series, Paint paint) {
        setSeriesOutlinePaint(series, paint, true);
    }

        
    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify) {
        this.outlinePaintList.setPaint(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setOutlinePaint(Paint paint) {
        setOutlinePaint(paint, true);
    }

    
    public void setOutlinePaint(Paint paint, boolean notify) {
        this.outlinePaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Paint getBaseOutlinePaint() {
        return this.baseOutlinePaint;
    }

    
    public void setBaseOutlinePaint(Paint paint) {
        
        setBaseOutlinePaint(paint, true);
    }
    
    
    public void setBaseOutlinePaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseOutlinePaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getAutoPopulateSeriesOutlinePaint() {
        return this.autoPopulateSeriesOutlinePaint;
    }
    
    
    public void setAutoPopulateSeriesOutlinePaint(boolean auto) {
        this.autoPopulateSeriesOutlinePaint = auto;
    }

    
    
    
    public Stroke getItemStroke(int row, int column) {
        return lookupSeriesStroke(row);
    }

    
    public Stroke lookupSeriesStroke(int series) {

        
        if (this.stroke != null) {
            return this.stroke;
        }

        
        Stroke result = getSeriesStroke(series);
        if (result == null && this.autoPopulateSeriesStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextStroke();
                setSeriesStroke(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseStroke;
        }
        return result;

    }
    
    
    public void setStroke(Stroke stroke) {
        setStroke(stroke, true);
    }
    
    
    public void setStroke(Stroke stroke, boolean notify) {
        this.stroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }    

    
    public Stroke getSeriesStroke(int series) {
        return this.strokeList.getStroke(series);
    }
    
    
    public void setSeriesStroke(int series, Stroke stroke) {
        setSeriesStroke(series, stroke, true);
    }
    
    
    public void setSeriesStroke(int series, Stroke stroke, boolean notify) {
        this.strokeList.setStroke(series, stroke);
        if (notify) {
            fireChangeEvent();
        }
    }    

    
    public Stroke getBaseStroke() {
        return this.baseStroke;
    }

    
    public void setBaseStroke(Stroke stroke) {
        
        setBaseStroke(stroke, true);
    }

    
    public void setBaseStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");   
        }
        this.baseStroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }    

    
    public boolean getAutoPopulateSeriesStroke() {
        return this.autoPopulateSeriesStroke;
    }
    
    
    public void setAutoPopulateSeriesStroke(boolean auto) {
        this.autoPopulateSeriesStroke = auto;
    }

    
    
    
    public Stroke getItemOutlineStroke(int row, int column) {
        return lookupSeriesOutlineStroke(row);
    }

    
    public Stroke lookupSeriesOutlineStroke(int series) {

        
        if (this.outlineStroke != null) {
            return this.outlineStroke;
        }

        
        Stroke result = getSeriesOutlineStroke(series);
        if (result == null && this.autoPopulateSeriesOutlineStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineStroke();
                setSeriesOutlineStroke(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseOutlineStroke;
        }
        return result;

    }

    
    public void setOutlineStroke(Stroke stroke) {
        setOutlineStroke(stroke, true);
    }

    
    public void setOutlineStroke(Stroke stroke, boolean notify) {
        this.outlineStroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Stroke getSeriesOutlineStroke(int series) {
        return this.outlineStrokeList.getStroke(series);
    }
    
    
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        setSeriesOutlineStroke(series, stroke, true);
    }

    
    public void setSeriesOutlineStroke(int series, Stroke stroke, 
                                       boolean notify) {
        this.outlineStrokeList.setStroke(series, stroke);
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Stroke getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }

    
    public void setBaseOutlineStroke(Stroke stroke) {
        setBaseOutlineStroke(stroke, true);
    }

    
    public void setBaseOutlineStroke(Stroke stroke, boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseOutlineStroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public boolean getAutoPopulateSeriesOutlineStroke() {
        return this.autoPopulateSeriesOutlineStroke;
    }
    
    
    public void setAutoPopulateSeriesOutlineStroke(boolean auto) {
        this.autoPopulateSeriesOutlineStroke = auto;
    }

    
    
    
    public Shape getItemShape(int row, int column) {
        return lookupSeriesShape(row);
    }

    
    public Shape lookupSeriesShape(int series) {

        
        if (this.shape != null) {
            return this.shape;
        }

        
        Shape result = getSeriesShape(series);
        if (result == null && this.autoPopulateSeriesShape) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextShape();
                setSeriesShape(series, result, false);
            }
        }
        if (result == null) {
            result = this.baseShape;
        }
        return result;

    }

    
    public void setShape(Shape shape) {
        setShape(shape, true);
    }
    
    
    public void setShape(Shape shape, boolean notify) {
        this.shape = shape;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Shape getSeriesShape(int series) {
        return this.shapeList.getShape(series);
    }

    
    public void setSeriesShape(int series, Shape shape) {
        setSeriesShape(series, shape, true);
    }

    
    public void setSeriesShape(int series, Shape shape, boolean notify) {
        this.shapeList.setShape(series, shape);
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Shape getBaseShape() {
        return this.baseShape;
    }

    
    public void setBaseShape(Shape shape) {
        
        setBaseShape(shape, true);
    }

    
    public void setBaseShape(Shape shape, boolean notify) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument."); 
        }
        this.baseShape = shape;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public boolean getAutoPopulateSeriesShape() {
        return this.autoPopulateSeriesShape;
    }
    
    
    public void setAutoPopulateSeriesShape(boolean auto) {
        this.autoPopulateSeriesShape = auto;
    }

    

    
    public boolean isItemLabelVisible(int row, int column) {
        return isSeriesItemLabelsVisible(row);
    }

        
    public boolean isSeriesItemLabelsVisible(int series) {

        
        if (this.itemLabelsVisible != null) {
            return this.itemLabelsVisible.booleanValue();
        }

        
        Boolean b = this.itemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = this.baseItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b.booleanValue();

    }
    
    
    public void setItemLabelsVisible(boolean visible) {        
        setItemLabelsVisible(BooleanUtilities.valueOf(visible));
        
        
        
    }
    
    
    public void setItemLabelsVisible(Boolean visible) {
        setItemLabelsVisible(visible, true);
    }
    
    
    public void setItemLabelsVisible(Boolean visible, boolean notify) {
        this.itemLabelsVisible = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public void setSeriesItemLabelsVisible(int series, boolean visible) {
        setSeriesItemLabelsVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    
    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        setSeriesItemLabelsVisible(series, visible, true);
    }

    
    public void setSeriesItemLabelsVisible(int series, Boolean visible, 
                                           boolean notify) {
        this.itemLabelsVisibleList.setBoolean(series, visible);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Boolean getBaseItemLabelsVisible() {
        
        
        
        return this.baseItemLabelsVisible;
    }

    
    public void setBaseItemLabelsVisible(boolean visible) {
        setBaseItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }
    
    
    public void setBaseItemLabelsVisible(Boolean visible) {
        setBaseItemLabelsVisible(visible, true);
    }

    
    public void setBaseItemLabelsVisible(Boolean visible, boolean notify) {
        this.baseItemLabelsVisible = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public Font getItemLabelFont(int row, int column) {
        Font result = this.itemLabelFont;
        if (result == null) {
            result = getSeriesItemLabelFont(row);
            if (result == null) {
                result = this.baseItemLabelFont;   
            }
        }
        return result;
    }

    
    public Font getItemLabelFont() {
        return this.itemLabelFont;   
    }
    
    
    public void setItemLabelFont(Font font) {
        setItemLabelFont(font, true);
    }
    
    
    public void setItemLabelFont(Font font, boolean notify) {
        this.itemLabelFont = font;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public Font getSeriesItemLabelFont(int series) {
        return (Font) this.itemLabelFontList.get(series);
    }

    
    public void setSeriesItemLabelFont(int series, Font font) {
        setSeriesItemLabelFont(series, font, true);
    }

    
    public void setSeriesItemLabelFont(int series, Font font, boolean notify) {
        this.itemLabelFontList.set(series, font);
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }

    
    public void setBaseItemLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        setBaseItemLabelFont(font, true);
    }

    
    public void setBaseItemLabelFont(Font font, boolean notify) {
        this.baseItemLabelFont = font;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public Paint getItemLabelPaint(int row, int column) {
        Paint result = this.itemLabelPaint;
        if (result == null) {
            result = getSeriesItemLabelPaint(row);
            if (result == null) {
                result = this.baseItemLabelPaint;   
            }
        }
        return result;
    }
    
    
    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;   
    }

    
    public void setItemLabelPaint(Paint paint) {
        setItemLabelPaint(paint, true);
    }

    
    public void setItemLabelPaint(Paint paint, boolean notify) {
        this.itemLabelPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Paint getSeriesItemLabelPaint(int series) {
        return this.itemLabelPaintList.getPaint(series);
    }

    
    public void setSeriesItemLabelPaint(int series, Paint paint) {
        setSeriesItemLabelPaint(series, paint, true);
    }
    
    
    public void setSeriesItemLabelPaint(int series, Paint paint, 
                                        boolean notify) {
        this.itemLabelPaintList.setPaint(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Paint getBaseItemLabelPaint() {
        return this.baseItemLabelPaint;
    }

    
    public void setBaseItemLabelPaint(Paint paint) {
        
        setBaseItemLabelPaint(paint, true);
    }

    
    public void setBaseItemLabelPaint(Paint paint, boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.baseItemLabelPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }
    
    

    
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
        return getSeriesPositiveItemLabelPosition(row);
    }

    
    public ItemLabelPosition getPositiveItemLabelPosition() {
        return this.positiveItemLabelPosition;
    }

    
    public void setPositiveItemLabelPosition(ItemLabelPosition position) {
        setPositiveItemLabelPosition(position, true);
    }
    
    
    public void setPositiveItemLabelPosition(ItemLabelPosition position, 
                                             boolean notify) {
        this.positiveItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series) {

        
        if (this.positiveItemLabelPosition != null) {
            return this.positiveItemLabelPosition;
        }

        
        ItemLabelPosition position = (ItemLabelPosition) 
            this.positiveItemLabelPositionList.get(series);
        if (position == null) {
            position = this.basePositiveItemLabelPosition;
        }
        return position;

    }
    
    
    public void setSeriesPositiveItemLabelPosition(int series, 
                                                   ItemLabelPosition position) {
        setSeriesPositiveItemLabelPosition(series, position, true);
    }

    
    public void setSeriesPositiveItemLabelPosition(int series, 
                                                   ItemLabelPosition position, 
                                                   boolean notify) {
        this.positiveItemLabelPositionList.set(series, position);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public ItemLabelPosition getBasePositiveItemLabelPosition() {
        return this.basePositiveItemLabelPosition;
    }

    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position) {
        
        setBasePositiveItemLabelPosition(position, true);
    }
    
    
    public void setBasePositiveItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");   
        }
        this.basePositiveItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    

    
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column) {
        return getSeriesNegativeItemLabelPosition(row);
    }

    
    public ItemLabelPosition getNegativeItemLabelPosition() {
        return this.negativeItemLabelPosition;
    }

    
    public void setNegativeItemLabelPosition(ItemLabelPosition position) {
        setNegativeItemLabelPosition(position, true);
    }
    
    
    public void setNegativeItemLabelPosition(ItemLabelPosition position, 
                                             boolean notify) {
        this.negativeItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series) {

        
        if (this.negativeItemLabelPosition != null) {
            return this.negativeItemLabelPosition;
        }

        
        ItemLabelPosition position = (ItemLabelPosition) 
            this.negativeItemLabelPositionList.get(series);
        if (position == null) {
            position = this.baseNegativeItemLabelPosition;
        }
        return position;

    }

    
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position) {
        setSeriesNegativeItemLabelPosition(series, position, true);
    }

    
    public void setSeriesNegativeItemLabelPosition(int series, 
                                                   ItemLabelPosition position, 
                                                   boolean notify) {
        this.negativeItemLabelPositionList.set(series, position);
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public ItemLabelPosition getBaseNegativeItemLabelPosition() {
        return this.baseNegativeItemLabelPosition;
    }

    
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position) {
        setBaseNegativeItemLabelPosition(position, true);
    }
    
    
    public void setBaseNegativeItemLabelPosition(ItemLabelPosition position, 
                                                 boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");   
        }
        this.baseNegativeItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }

    
    public void setItemLabelAnchorOffset(double offset) {
        this.itemLabelAnchorOffset = offset;
        fireChangeEvent();
    }

    
    public boolean getItemCreateEntity(int series, int item) {
        if (this.createEntities != null) {
            return this.createEntities.booleanValue();
        }
        else {
            Boolean b = getSeriesCreateEntities(series);
            if (b != null) {
                return b.booleanValue();
            }
            else {
                return this.baseCreateEntities;
            }
        }
    }
    
    
    public Boolean getCreateEntities() {
        return this.createEntities;  
    }
    
    
    public void setCreateEntities(Boolean create) {
         setCreateEntities(create, true);
    }
    
    
    public void setCreateEntities(Boolean create, boolean notify) {
        this.createEntities = create;   
        if (notify) {
            fireChangeEvent();
        }
    }
    
    
    public Boolean getSeriesCreateEntities(int series) {
        return this.createEntitiesList.getBoolean(series);
    }
    
    
    public void setSeriesCreateEntities(int series, Boolean create) {
        setSeriesCreateEntities(series, create, true);
    }
    
    
    public void setSeriesCreateEntities(int series, Boolean create, 
                                        boolean notify) {
        this.createEntitiesList.setBoolean(series, create);       
        if (notify) {
            fireChangeEvent();
        }
    }

    
    public boolean getBaseCreateEntities() {
        return this.baseCreateEntities;
    }

    
    public void setBaseCreateEntities(boolean create) {
        
        setBaseCreateEntities(create, true);
    }
    
    
    public void setBaseCreateEntities(boolean create, boolean notify) {
        this.baseCreateEntities = create;
        if (notify) {
            fireChangeEvent();
        }
    }

    
    private static final double ADJ = Math.cos(Math.PI / 6.0);
    
    
    private static final double OPP = Math.sin(Math.PI / 6.0);
    
    
    protected Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
            double x, double y, PlotOrientation orientation) {
        Point2D result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, 
                    y - ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, 
                    y - OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x + ADJ * this.itemLabelAnchorOffset, 
                    y + OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x + OPP * this.itemLabelAnchorOffset, 
                    y + ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, 
                    y + ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, 
                    y + OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x - ADJ * this.itemLabelAnchorOffset, 
                    y - OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x - OPP * this.itemLabelAnchorOffset, 
                    y - ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(
                    x + 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(
                    x + 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y - 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, 
                    y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(
                    x + 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y + 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(
                    x + 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, 
                    y + 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(
                    x - 2.0 * OPP * this.itemLabelAnchorOffset, 
                    y + 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(
                    x - 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y + 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelAnchorOffset, 
                    y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(
                    x - 2.0 * ADJ * this.itemLabelAnchorOffset, 
                    y - 2.0 * OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(
                x - 2.0 * OPP * this.itemLabelAnchorOffset, 
                y - 2.0 * ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, 
                    y - 2.0 * this.itemLabelAnchorOffset);
        }
        return result;
    }
    
    
    public void addChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");   
        }
        this.listenerList.add(RendererChangeListener.class, listener);
    }

    
    public void removeChangeListener(RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");   
        }
        this.listenerList.remove(RendererChangeListener.class, listener);
    }

    
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }
    
    
    protected void fireChangeEvent() {
        
        
        
        
        
        
        
        
        
        
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public void notifyListeners(RendererChangeEvent event) {
        Object[] ls = this.listenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            if (ls[i] == RendererChangeListener.class) {
                ((RendererChangeListener) ls[i + 1]).rendererChanged(event);
            }
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        AbstractRenderer that = (AbstractRenderer) obj;
        if (!ObjectUtilities.equal(this.seriesVisible, that.seriesVisible)) {
            return false;   
        }
        if (!this.seriesVisibleList.equals(that.seriesVisibleList)) {
            return false;   
        }
        if (this.baseSeriesVisible != that.baseSeriesVisible) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.seriesVisibleInLegend, 
                that.seriesVisibleInLegend)) {
            return false;   
        }
        if (!this.seriesVisibleInLegendList.equals(
                that.seriesVisibleInLegendList)) {
            return false;   
        }
        if (this.baseSeriesVisibleInLegend != that.baseSeriesVisibleInLegend) {
            return false;   
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.paintList, that.paintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.basePaint, that.basePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.fillPaintList, that.fillPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseFillPaint, that.baseFillPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlinePaintList,
                that.outlinePaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseOutlinePaint, 
                that.baseOutlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.stroke, that.stroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.strokeList, that.strokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseStroke, that.baseStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStrokeList, 
                that.outlineStrokeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.baseOutlineStroke, that.baseOutlineStroke)
        ) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapeList, that.shapeList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseShape, that.baseShape)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelsVisible, 
                that.itemLabelsVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelsVisibleList, 
                that.itemLabelsVisibleList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelsVisible, 
                that.baseItemLabelsVisible)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelFont, that.itemLabelFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelFontList, 
                that.itemLabelFontList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelFont, 
                that.baseItemLabelFont)) {
            return false;
        }
 
        if (!PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelPaintList, 
                that.itemLabelPaintList)) {
            return false;
        }
        if (!PaintUtilities.equal(this.baseItemLabelPaint, 
                that.baseItemLabelPaint)) {
            return false;
        }

        if (!ObjectUtilities.equal(this.positiveItemLabelPosition, 
                that.positiveItemLabelPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.positiveItemLabelPositionList, 
                that.positiveItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.basePositiveItemLabelPosition, 
                that.basePositiveItemLabelPosition)) {
            return false;
        }

        if (!ObjectUtilities.equal(this.negativeItemLabelPosition, 
                that.negativeItemLabelPosition)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.negativeItemLabelPositionList, 
                that.negativeItemLabelPositionList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseNegativeItemLabelPosition, 
                that.baseNegativeItemLabelPosition)) {
            return false;
        }
        if (this.itemLabelAnchorOffset != that.itemLabelAnchorOffset) {
            return false;
        }
        if (!ObjectUtilities.equal(this.createEntities, that.createEntities)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.createEntitiesList, 
                that.createEntitiesList)) {
            return false;   
        }
        if (this.baseCreateEntities != that.baseCreateEntities) {
            return false;   
        }
        return true;
    }
    
    
    public int hashCode() {
        int result = 193; 
        result = HashUtilities.hashCode(result, this.seriesVisibleList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisible);
        result = HashUtilities.hashCode(result, this.seriesVisibleInLegendList);
        result = HashUtilities.hashCode(result, this.baseSeriesVisibleInLegend);
        result = HashUtilities.hashCode(result, this.paintList);
        result = HashUtilities.hashCode(result, this.basePaint);
        result = HashUtilities.hashCode(result, this.fillPaintList);
        result = HashUtilities.hashCode(result, this.baseFillPaint);
        result = HashUtilities.hashCode(result, this.outlinePaintList);
        result = HashUtilities.hashCode(result, this.baseOutlinePaint);
        result = HashUtilities.hashCode(result, this.strokeList);
        result = HashUtilities.hashCode(result, this.baseStroke);   
        result = HashUtilities.hashCode(result, this.outlineStrokeList);
        result = HashUtilities.hashCode(result, this.baseOutlineStroke);   
        
        
        result = HashUtilities.hashCode(result, this.itemLabelsVisibleList);
        result = HashUtilities.hashCode(result, this.baseItemLabelsVisible);
        
        
        
        
        
        
        
        
        
        
        
        return result;
    }
    
    
    protected Object clone() throws CloneNotSupportedException {
        AbstractRenderer clone = (AbstractRenderer) super.clone();
        
        if (this.seriesVisibleList != null) {
            clone.seriesVisibleList 
                    = (BooleanList) this.seriesVisibleList.clone();
        }
        
        if (this.seriesVisibleInLegendList != null) {
            clone.seriesVisibleInLegendList 
                    = (BooleanList) this.seriesVisibleInLegendList.clone();
        }

        
        if (this.paintList != null) {
            clone.paintList = (PaintList) this.paintList.clone();
        }
        
        
        if (this.fillPaintList != null) {
            clone.fillPaintList = (PaintList) this.fillPaintList.clone();
        }
        
        if (this.outlinePaintList != null) {
            clone.outlinePaintList = (PaintList) this.outlinePaintList.clone();
        }
        
        
        
        if (this.strokeList != null) {
            clone.strokeList = (StrokeList) this.strokeList.clone();
        }
        
        
        
        if (this.outlineStrokeList != null) {
            clone.outlineStrokeList 
                = (StrokeList) this.outlineStrokeList.clone();
        }
        
        
        if (this.shape != null) {
            clone.shape = ShapeUtilities.clone(this.shape);
        }
        if (this.shapeList != null) {
            clone.shapeList = (ShapeList) this.shapeList.clone();
        }
        if (this.baseShape != null) {
            clone.baseShape = ShapeUtilities.clone(this.baseShape);
        }
        
        
        if (this.itemLabelsVisibleList != null) {
            clone.itemLabelsVisibleList 
                = (BooleanList) this.itemLabelsVisibleList.clone();
        }
        
        
        
        if (this.itemLabelFontList != null) {
            clone.itemLabelFontList 
                = (ObjectList) this.itemLabelFontList.clone();
        }
        

        
        if (this.itemLabelPaintList != null) {
            clone.itemLabelPaintList 
                = (PaintList) this.itemLabelPaintList.clone();
        }
        
        
        
        if (this.positiveItemLabelPositionList != null) {
            clone.positiveItemLabelPositionList 
                = (ObjectList) this.positiveItemLabelPositionList.clone();
        }
        

        
        if (this.negativeItemLabelPositionList != null) {
            clone.negativeItemLabelPositionList 
                = (ObjectList) this.negativeItemLabelPositionList.clone();
        }
        
        
        if (this.createEntitiesList != null) {
            clone.createEntitiesList 
                    = (BooleanList) this.createEntitiesList.clone();
        }
        clone.listenerList = new EventListenerList();
        clone.event = null;
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.basePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.baseFillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.baseOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writeStroke(this.baseStroke, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writeStroke(this.baseOutlineStroke, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeShape(this.baseShape, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.baseItemLabelPaint, stream);

    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.basePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.baseFillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.baseOutlinePaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.baseStroke = SerialUtilities.readStroke(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.baseOutlineStroke = SerialUtilities.readStroke(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.baseShape = SerialUtilities.readShape(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.baseItemLabelPaint = SerialUtilities.readPaint(stream);
        
        
        
        this.listenerList = new EventListenerList();

    }

}
