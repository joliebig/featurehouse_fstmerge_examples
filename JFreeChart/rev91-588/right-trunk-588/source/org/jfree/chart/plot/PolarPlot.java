

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
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;


public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable,
        RendererChangeListener, Cloneable, Serializable {
   
    
    private static final long serialVersionUID = 3794383185924179525L;
    
    
    private static final int MARGIN = 20;
   
    
    private static final double ANNOTATION_MARGIN = 7.0;
   
    
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(
            0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
            0.0f, new float[]{2.0f, 2.0f}, 0.0f);
   
    
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.gray;
   
    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
   
    
    private List angleTicks;
    
    
    private ValueAxis axis;
    
    
    private XYDataset dataset;
   
    
    private PolarItemRenderer renderer;
   
    
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
   
    
    public PolarPlot() {
        this(null, null, null);
    }
   
   
    public PolarPlot(XYDataset dataset, 
                     ValueAxis radiusAxis,
                     PolarItemRenderer renderer) {
      
        super();
            
        this.dataset = dataset;
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
      
        this.angleTicks = new java.util.ArrayList();
        this.angleTicks.add(new NumberTick(new Double(0.0), "0", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(45.0), "45", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(90.0), "90", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(135.0), "135", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(180.0), "180", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(225.0), "225", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(270.0), "270", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(315.0), "315", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        
        this.axis = radiusAxis;
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
      
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
            this.renderer.addChangeListener(this);
        }
      
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;
      
        this.radiusGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;      
    }
   
    
    public void addCornerTextItem(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.cornerTextItems.add(text);
        this.notifyListeners(new PlotChangeEvent(this));
    }
   
    
    public void removeCornerTextItem(String text) {
        boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            this.notifyListeners(new PlotChangeEvent(this));        
        }
    }
   
    
    public void clearCornerTextItems() {
        if (this.cornerTextItems.size() > 0) {
            this.cornerTextItems.clear();
            this.notifyListeners(new PlotChangeEvent(this));        
        }
    }
   
    
    public String getPlotType() {
       return PolarPlot.localizationResources.getString("Polar_Plot");
    }
    
    
    public ValueAxis getAxis() {
        return this.axis;
    }
   
    
    public void setAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
       
        
        if (this.axis != null) {
            this.axis.removeChangeListener(this);
        }
       
        this.axis = axis;
        if (this.axis != null) {
            this.axis.configure();
            this.axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));
    }
   
    
    public XYDataset getDataset() {
        return this.dataset;
    }
    
    
    public void setDataset(XYDataset dataset) {
        
        
        XYDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
       
        
        this.dataset = dataset;
        if (this.dataset != null) {
            setDatasetGroup(this.dataset.getGroup());
            this.dataset.addChangeListener(this);
        }
       
        
        DatasetChangeEvent event = new DatasetChangeEvent(this, this.dataset);
        datasetChanged(event);
    }
   
    
    public PolarItemRenderer getRenderer() {
        return this.renderer;
    }
   
    
    public void setRenderer(PolarItemRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
       
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
        }
       
        notifyListeners(new PlotChangeEvent(this));
    }
   
    
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }
    
    
    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
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
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }
    
    
    public void setAngleLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.angleLabelPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }
    
    
    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }
   
    
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }
    
    
    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }
   
    
    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }
    
    
    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }
   
    
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }
    
    
    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }
    
    
    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    
    public void draw(Graphics2D g2, 
                     Rectangle2D area, 
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {
       
        
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
        double h = Math.min(dataArea.getWidth() / 2.0, 
                dataArea.getHeight() / 2.0) - MARGIN;
        Rectangle2D quadrant = new Rectangle2D.Double(dataArea.getCenterX(), 
                dataArea.getCenterY(), h, h);
        AxisState state = drawAxis(g2, area, quadrant);
        if (this.renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();
          
            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, getForegroundAlpha()));
          
            drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
          
            
            render(g2, dataArea, info);
          
            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
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
   
    
    protected AxisState drawAxis(Graphics2D g2, Rectangle2D plotArea, 
                                 Rectangle2D dataArea) {
        return this.axis.draw(g2, dataArea.getMinY(), plotArea, dataArea, 
                RectangleEdge.TOP, null);
    }
   
    
    protected void render(Graphics2D g2,
                       Rectangle2D dataArea,
                       PlotRenderingInfo info) {
      
        
        
        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            int seriesCount = this.dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                this.renderer.drawSeries(g2, dataArea, info, this, 
                        this.dataset, series);
            }
        }
        else {
            drawNoDataMessage(g2, dataArea);
        }
    }
   
    
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea, 
                                 List angularTicks, List radialTicks) {

        
        if (this.renderer == null) {
            return;
        }
       
        
        if (isAngleGridlinesVisible()) {
            Stroke gridStroke = getAngleGridlineStroke();
            Paint gridPaint = getAngleGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                this.renderer.drawAngularGridLines(g2, this, angularTicks, 
                        dataArea);
            }
        }
       
        
        if (isRadiusGridlinesVisible()) {
            Stroke gridStroke = getRadiusGridlineStroke();
            Paint gridPaint = getRadiusGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                this.renderer.drawRadialGridLines(g2, this, this.axis, 
                        radialTicks, dataArea);
            }
        }      
    }
   
    
    public void zoom(double percent) {
        if (percent > 0.0) {
            double radius = getMaxRadius();
            double scaledRadius = radius * percent;
            this.axis.setUpperBound(scaledRadius);
            getAxis().setAutoRange(false);
        } 
        else {
            getAxis().setAutoRange(true);
        }
    }
   
    
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (this.dataset != null) {
            result = Range.combine(result, 
                    DatasetUtilities.findRangeBounds(this.dataset));
        }
        return result;
    }
   
    
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.axis != null) {
            this.axis.configure();
        }
       
        if (getParent() != null) {
            getParent().datasetChanged(event);
        }
        else {
            super.datasetChanged(event);
        }
    }
   
    
    public void rendererChanged(RendererChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }
   
    
    public int getSeriesCount() {
        int result = 0;
       
        if (this.dataset != null) {
            result = this.dataset.getSeriesCount();
        }
        return result;
    }
   
    
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
       
        
        if (this.dataset != null) {
            if (this.renderer != null) {
                int seriesCount = this.dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = this.renderer.getLegendItem(i);
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
        if (!ObjectUtilities.equal(this.axis, that.axis)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.renderer, that.renderer)) {
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
        return super.equals(obj);
    }
   
    
    public Object clone() throws CloneNotSupportedException {
      
        PolarPlot clone = (PolarPlot) super.clone();
        if (this.axis != null) {
            clone.axis = (ValueAxis) ObjectUtilities.clone(this.axis);
            clone.axis.setPlot(clone);
            clone.axis.addChangeListener(clone);
        }
      
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
      
        if (this.renderer != null) {
            clone.renderer 
                = (PolarItemRenderer) ObjectUtilities.clone(this.renderer);
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
      
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
      
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
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
            double anchorX = this.axis.java2DToValue(sourceX, 
                    info.getDataArea(), RectangleEdge.BOTTOM);
            this.axis.resizeRange(factor, anchorX);
        }
        else {
            axis.resizeRange(factor);
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

    
    public double getMaxRadius() {
        return this.axis.getUpperBound();
    }

       
    public Point translateValueThetaRadiusToJava2D(double angleDegrees, 
                                                   double radius,
                                                   Rectangle2D dataArea) {
       
        double radians = Math.toRadians(angleDegrees - 90.0);
      
        double minx = dataArea.getMinX() + MARGIN;
        double maxx = dataArea.getMaxX() - MARGIN;
        double miny = dataArea.getMinY() + MARGIN;
        double maxy = dataArea.getMaxY() - MARGIN;
      
        double lengthX = maxx - minx;
        double lengthY = maxy - miny;
        double length = Math.min(lengthX, lengthY);
      
        double midX = minx + lengthX / 2.0;
        double midY = miny + lengthY / 2.0;
      
        double axisMin = this.axis.getLowerBound();
        double axisMax =  getMaxRadius();
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
    
}
