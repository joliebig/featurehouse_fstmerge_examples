

package org.jfree.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.Contributor;
import org.jfree.chart.ui.Licences;
import org.jfree.chart.ui.ProjectInfo;
import org.jfree.chart.util.Align;
import org.jfree.chart.util.HorizontalAlignment;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.Size2D;
import org.jfree.chart.util.VerticalAlignment;
import org.jfree.data.Range;


public class JFreeChart implements Drawable,
                                   TitleChangeListener,
                                   PlotChangeListener,
                                   Serializable,
                                   Cloneable {

        
    private static final long serialVersionUID = -3470703747817429120L;
    
    
    public static final ProjectInfo INFO = new JFreeChartInfo();

    
    public static final Font DEFAULT_TITLE_FONT 
            = new Font("SansSerif", Font.BOLD, 18);

    
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.WHITE;

    
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    
    public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = Align.FIT;

    
    public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

    
    private transient RenderingHints renderingHints;

    
    private boolean borderVisible;

    
    private transient Stroke borderStroke;

    
    private transient Paint borderPaint;

    
    private RectangleInsets padding;
    
    
    private TextTitle title;

    
    private List subtitles;

    
    private Plot plot;

    
    private transient Paint backgroundPaint;

    
    private transient Image backgroundImage;  

    
    private int backgroundImageAlignment = Align.FIT;

    
    private float backgroundImageAlpha = 0.5f;

    
    private transient EventListenerList changeListeners;

    
    private transient EventListenerList progressListeners;

    
    private boolean notify;
    
    
    public JFreeChart(Plot plot) {
        this(null, null, plot, true);
    }

    
    public JFreeChart(String title, Plot plot) {
        this(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    
    public JFreeChart(String title, Font titleFont, Plot plot, 
                      boolean createLegend) {

        if (plot == null) {
            throw new NullPointerException("Null 'plot' argument.");
        }

        
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.notify = true;  
                             

        this.renderingHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);

        this.borderVisible = false;
        this.borderStroke = new BasicStroke(1.0f);
        this.borderPaint = Color.black;

        this.padding = RectangleInsets.ZERO_INSETS;
        
        this.plot = plot;
        plot.addChangeListener(this);

        this.subtitles = new ArrayList();

        
        if (createLegend) {
            LegendTitle legend = new LegendTitle(this.plot);
            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
            legend.setFrame(new LineBorder());
            legend.setBackgroundPaint(Color.white);
            legend.setPosition(RectangleEdge.BOTTOM);
            this.subtitles.add(legend);
            legend.addChangeListener(this);
        }

        
        if (title != null) {
            if (titleFont == null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            this.title = new TextTitle(title, titleFont);
            this.title.addChangeListener(this);
        }

        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;

        this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;

    }

    
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    
    public void setRenderingHints(RenderingHints renderingHints) {
        if (renderingHints == null) {
            throw new NullPointerException("RenderingHints given are null");
        }
        this.renderingHints = renderingHints;
        fireChartChanged();
    }

    
    public boolean isBorderVisible() {
        return this.borderVisible;
    }

    
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        fireChartChanged();
    }

    
    public Stroke getBorderStroke() {
        return this.borderStroke;
    }

    
    public void setBorderStroke(Stroke stroke) {
        this.borderStroke = stroke;
        fireChartChanged();
    }

    
    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    
    public void setBorderPaint(Paint paint) {
        this.borderPaint = paint;
        fireChartChanged();
    }
    
    
    public RectangleInsets getPadding() {
        return this.padding;   
    }

    
    public void setPadding(RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");   
        }
        this.padding = padding;
        notifyListeners(new ChartChangeEvent(this));
    }
    
    
    public TextTitle getTitle() {
        return this.title;
    }

    
    public void setTitle(TextTitle title) {
        if (this.title != null) {
            this.title.removeChangeListener(this);
        }
        this.title = title;
        if (title != null) {
            title.addChangeListener(this);
        }
        fireChartChanged();
    }

    
    public void setTitle(String text) {
        if (text != null) {
            if (this.title == null) {
                setTitle(new TextTitle(text, JFreeChart.DEFAULT_TITLE_FONT));
            }
            else {
                this.title.setText(text);
            }
        }
        else {
            setTitle((TextTitle) null);
        }
    }

    
    public void addLegend(LegendTitle legend) {
        addSubtitle(legend);    
    }
    
    
    public LegendTitle getLegend() {
        return getLegend(0);
    }
    
    
    public LegendTitle getLegend(int index) {
        int seen = 0;
        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title subtitle = (Title) iterator.next();
            if (subtitle instanceof LegendTitle) {
                if (seen == index) {
                    return (LegendTitle) subtitle;
                }
                else {
                    seen++;   
                }
            }
        }
        return null;        
    }
    
    
    public void removeLegend() {
        removeSubtitle(getLegend());
    }
    
    
    public List getSubtitles() {
        return new ArrayList(this.subtitles);
    }

    
    public void setSubtitles(List subtitles) {
        if (subtitles == null) {
            throw new NullPointerException("Null 'subtitles' argument.");
        }
        setNotify(false);
        clearSubtitles();
        Iterator iterator = subtitles.iterator();
        while (iterator.hasNext()) {
            Title t = (Title) iterator.next();
            if (t != null) {
                addSubtitle(t);
            }
        }
        setNotify(true);  
    }

    
    public int getSubtitleCount() {
        return this.subtitles.size();
    }

    
    public Title getSubtitle(int index) {
        if ((index < 0) || (index >= getSubtitleCount())) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return (Title) this.subtitles.get(index);
    }

    
    public void addSubtitle(Title subtitle) {
        if (subtitle == null) {
            throw new IllegalArgumentException("Null 'subtitle' argument.");
        }
        this.subtitles.add(subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }
    
    
    public void addSubtitle(int index, Title subtitle) {
        if (index < 0 || index > getSubtitleCount()) {
            throw new IllegalArgumentException(
                    "The 'index' argument is out of range.");
        }
        if (subtitle == null) {
            throw new IllegalArgumentException("Null 'subtitle' argument.");
        }
        this.subtitles.add(index, subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }
    
    
    public void clearSubtitles() {
        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title t = (Title) iterator.next();
            t.removeChangeListener(this);
        }
        this.subtitles.clear();
        fireChartChanged();
    }

    
    public void removeSubtitle(Title title) {
        this.subtitles.remove(title);
        fireChartChanged();
    }
    
    
    public Plot getPlot() {
        return this.plot;
    }

    
    public CategoryPlot getCategoryPlot() {
        return (CategoryPlot) this.plot;
    }

    
    public XYPlot getXYPlot() {
        return (XYPlot) this.plot;
    }

    
    public boolean getAntiAlias() {
        Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
        return RenderingHints.VALUE_ANTIALIAS_ON.equals(val);
    }
    
    
    public void setAntiAlias(boolean flag) {

        Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
        if (val == null) {
            val = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
        }
        if (!flag && RenderingHints.VALUE_ANTIALIAS_OFF.equals(val) 
            || flag && RenderingHints.VALUE_ANTIALIAS_ON.equals(val)) {
            
            return;
        }
        if (flag) {
            this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else {
            this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        fireChartChanged();

    }

    
    public Object getTextAntiAlias() {
        return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING); 
    }
    
    
    public void setTextAntiAlias(boolean flag) {
        if (flag) {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        else {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }

    
    public void setTextAntiAlias(Object val) {
        this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
        this.notifyListeners(new ChartChangeEvent(this));
    }
    
    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    
    public void setBackgroundPaint(Paint paint) {

        if (this.backgroundPaint != null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }
        else {
            if (paint != null) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }

    }

    
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    
    public void setBackgroundImage(Image image) {

        if (this.backgroundImage != null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }
        else {
            if (image != null) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }

    }

    
    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    
    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChartChanged();
        }
    }

    
    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    
    public void setBackgroundImageAlpha(float alpha) {

        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }

    }

    
    public boolean isNotify() {
        return this.notify;
    }

    
    public void setNotify(boolean notify) {
        this.notify = notify;
        
        if (notify) {
            notifyListeners(new ChartChangeEvent(this));
        }
    }

    
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null, null);
    }

    
    public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
        draw(g2, area, null, info);
    }
    
    
    public void draw(Graphics2D g2, 
                     Rectangle2D chartArea, Point2D anchor, 
                     ChartRenderingInfo info) {

        notifyListeners(new ChartProgressEvent(this, this, 
                ChartProgressEvent.DRAWING_STARTED, 0));

        
        if (info != null) {
            info.clear();
            info.setChartArea(chartArea);
        }

        
        Shape savedClip = g2.getClip();
        g2.clip(chartArea);

        g2.addRenderingHints(this.renderingHints);

        
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(chartArea);
        }

        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    this.backgroundImageAlpha));
            Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, 
                    this.backgroundImage.getWidth(null), 
                    this.backgroundImage.getHeight(null));
            Align.align(dest, chartArea, this.backgroundImageAlignment);
            g2.drawImage(this.backgroundImage, (int) dest.getX(), 
                    (int) dest.getY(), (int) dest.getWidth(), 
                    (int) dest.getHeight(), null);
            g2.setComposite(originalComposite);
        }

        if (isBorderVisible()) {
            Paint paint = getBorderPaint();
            Stroke stroke = getBorderStroke();
            if (paint != null && stroke != null) {
                Rectangle2D borderArea = new Rectangle2D.Double(
                        chartArea.getX(), chartArea.getY(), 
                        chartArea.getWidth() - 1.0, chartArea.getHeight() 
                        - 1.0);
                g2.setPaint(paint);
                g2.setStroke(stroke);
                g2.draw(borderArea);
            }
        }

        
        Rectangle2D nonTitleArea = new Rectangle2D.Double();
        nonTitleArea.setRect(chartArea);
        this.padding.trim(nonTitleArea);
        
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();   
        }
        if (this.title != null) {
            EntityCollection e = drawTitle(this.title, g2, nonTitleArea, 
                    (entities != null));
            if (e != null) {
                entities.addAll(e);   
            }
        }

        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            Title currentTitle = (Title) iterator.next();
            EntityCollection e = drawTitle(currentTitle, g2, nonTitleArea, 
                    (entities != null));
            if (e != null) {
                entities.addAll(e);   
            }
        }

        Rectangle2D plotArea = nonTitleArea;
 
        
        PlotRenderingInfo plotInfo = null;
        if (info != null) {
            plotInfo = info.getPlotInfo();
        }
        this.plot.draw(g2, plotArea, anchor, null, plotInfo);

        g2.setClip(savedClip);

        notifyListeners(new ChartProgressEvent(this, this, 
                ChartProgressEvent.DRAWING_FINISHED, 100));
    }

    
    private Rectangle2D createAlignedRectangle2D(Size2D dimensions, 
            Rectangle2D frame, HorizontalAlignment hAlign, 
            VerticalAlignment vAlign) {
        double x = Double.NaN;
        double y = Double.NaN;
        if (hAlign == HorizontalAlignment.LEFT) {
            x = frame.getX();   
        }
        else if (hAlign == HorizontalAlignment.CENTER) {
            x = frame.getCenterX() - (dimensions.width / 2.0);   
        }
        else if (hAlign == HorizontalAlignment.RIGHT) {
            x = frame.getMaxX() - dimensions.width;   
        }
        if (vAlign == VerticalAlignment.TOP) {
            y = frame.getY();   
        }
        else if (vAlign == VerticalAlignment.CENTER) {
            y = frame.getCenterY() - (dimensions.height / 2.0);   
        }
        else if (vAlign == VerticalAlignment.BOTTOM) {
            y = frame.getMaxY() - dimensions.height;   
        }
        
        return new Rectangle2D.Double(x, y, dimensions.width, 
                dimensions.height);
    }
    
    
    protected EntityCollection drawTitle(Title t, Graphics2D g2, 
                                         Rectangle2D area, boolean entities) {

        if (t == null) {
            throw new IllegalArgumentException("Null 't' argument.");   
        }
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");   
        }
        Rectangle2D titleArea = new Rectangle2D.Double();
        RectangleEdge position = t.getPosition();
        double ww = area.getWidth();
        if (ww <= 0.0) {
            return null;
        }
        double hh = area.getHeight();
        if (hh <= 0.0) {
            return null;
        }
        RectangleConstraint constraint = new RectangleConstraint(ww, 
                new Range(0.0, ww), LengthConstraintType.RANGE, hh, 
                new Range(0.0, hh), LengthConstraintType.RANGE);
        Object retValue = null;
        BlockParams p = new BlockParams();
        p.setGenerateEntities(entities);
        if (position == RectangleEdge.TOP) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(size, area, 
                    t.getHorizontalAlignment(), VerticalAlignment.TOP);
            retValue = t.draw(g2, titleArea, p);
            area.setRect(area.getX(), Math.min(area.getY() + size.height, 
                    area.getMaxY()), area.getWidth(), Math.max(area.getHeight()
                    - size.height, 0));
        }
        else if (position == RectangleEdge.BOTTOM) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(size, area, 
                    t.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
            retValue = t.draw(g2, titleArea, p);
            area.setRect(area.getX(), area.getY(), area.getWidth(), 
                    area.getHeight() - size.height);
        }
        else if (position == RectangleEdge.RIGHT) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(size, area, 
                    HorizontalAlignment.RIGHT, t.getVerticalAlignment());
            retValue = t.draw(g2, titleArea, p);
            area.setRect(area.getX(), area.getY(), area.getWidth() 
                    - size.width, area.getHeight());
        }

        else if (position == RectangleEdge.LEFT) {
            Size2D size = t.arrange(g2, constraint);
            titleArea = createAlignedRectangle2D(size, area, 
                    HorizontalAlignment.LEFT, t.getVerticalAlignment());
            retValue = t.draw(g2, titleArea, p);
            area.setRect(area.getX() + size.width, area.getY(), area.getWidth() 
                    - size.width, area.getHeight());
        }
        else {
            throw new RuntimeException("Unrecognised title position.");
        }
        EntityCollection result = null;
        if (retValue instanceof EntityBlockResult) {
            EntityBlockResult ebr = (EntityBlockResult) retValue;
            result = ebr.getEntityCollection();
        }
        return result;   
    }

    
    public BufferedImage createBufferedImage(int width, int height) {
        return createBufferedImage(width, height, null);
    }

    
    public BufferedImage createBufferedImage(int width, int height, 
                                             ChartRenderingInfo info) {
        return createBufferedImage(width, height, BufferedImage.TYPE_INT_RGB, 
                info);
    }

    
    public BufferedImage createBufferedImage(int width, int height, 
                                             int imageType, 
                                             ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics2D g2 = image.createGraphics();
        draw(g2, new Rectangle2D.Double(0, 0, width, height), null, info);
        g2.dispose();
        return image;
    }

    
    public BufferedImage createBufferedImage(int imageWidth, 
                                             int imageHeight,
                                             double drawWidth, 
                                             double drawHeight, 
                                             ChartRenderingInfo info) {

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, 
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        double scaleX = imageWidth / drawWidth;
        double scaleY = imageHeight / drawHeight;
        AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
        g2.transform(st);
        draw(g2, new Rectangle2D.Double(0, 0, drawWidth, drawHeight), null, 
                info);
        g2.dispose();
        return image;

    }

    
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        
        
        this.plot.handleClick(x, y, info.getPlotInfo());

    }

    
    public void addChangeListener(ChartChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.add(ChartChangeListener.class, listener);
    }

    
    public void removeChangeListener(ChartChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.remove(ChartChangeListener.class, listener);
    }

    
    public void fireChartChanged() {
        ChartChangeEvent event = new ChartChangeEvent(this);
        notifyListeners(event);
    }

    
    protected void notifyListeners(ChartChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.changeListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChartChangeListener.class) {
                    ((ChartChangeListener) listeners[i + 1]).chartChanged(
                            event);
                }
            }
        }
    }

    
    public void addProgressListener(ChartProgressListener listener) {
        this.progressListeners.add(ChartProgressListener.class, listener);
    }

    
    public void removeProgressListener(ChartProgressListener listener) {
        this.progressListeners.remove(ChartProgressListener.class, listener);
    }

    
    protected void notifyListeners(ChartProgressEvent event) {

        Object[] listeners = this.progressListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChartProgressListener.class) {
                ((ChartProgressListener) listeners[i + 1]).chartProgress(event);
            }
        }

    }

    
    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    
    public void plotChanged(PlotChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JFreeChart)) {
            return false;
        }
        JFreeChart that = (JFreeChart) obj;
        if (!this.renderingHints.equals(that.renderingHints)) {
            return false;   
        }
        if (this.borderVisible != that.borderVisible) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.borderStroke, that.borderStroke)) {
            return false;   
        }
        if (!PaintUtilities.equal(this.borderPaint, that.borderPaint)) {
            return false;   
        }
        if (!this.padding.equals(that.padding)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.title, that.title)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.subtitles, that.subtitles)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.plot, that.plot)) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.backgroundPaint, that.backgroundPaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundImage, 
                that.backgroundImage)) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }
        return true;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.borderStroke, stream);
        SerialUtilities.writePaint(this.borderPaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.borderStroke = SerialUtilities.readStroke(stream);
        this.borderPaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.renderingHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);

        
        if (this.title != null) {
            this.title.addChangeListener(this);
        }

        for (int i = 0; i < getSubtitleCount(); i++) {
            getSubtitle(i).addChangeListener(this);
        }
        this.plot.addChangeListener(this);
    }

    
    public static void main(String[] args) {
        System.out.println(JFreeChart.INFO.toString());
    }

    
    public Object clone() throws CloneNotSupportedException {
        JFreeChart chart = (JFreeChart) super.clone();

        chart.renderingHints = (RenderingHints) this.renderingHints.clone();
        
        
        

        if (this.title != null) {
            chart.title = (TextTitle) this.title.clone();
            chart.title.addChangeListener(chart);
        }

        chart.subtitles = new ArrayList();
        for (int i = 0; i < getSubtitleCount(); i++) {
            Title subtitle = (Title) getSubtitle(i).clone();
            chart.subtitles.add(subtitle);
            subtitle.addChangeListener(chart);
        }

        if (this.plot != null) {
            chart.plot = (Plot) this.plot.clone();
            chart.plot.addChangeListener(chart);
        }

        chart.progressListeners = new EventListenerList();
        chart.changeListeners = new EventListenerList();
        return chart;
    }

}


class JFreeChartInfo extends ProjectInfo {

    
    public JFreeChartInfo() {

        
        String baseResourceClass 
                = "org.jfree.chart.resources.JFreeChartResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseResourceClass);

        setName(resources.getString("project.name"));
        setVersion(resources.getString("project.version"));
        setInfo(resources.getString("project.info"));
        setCopyright(resources.getString("project.copyright"));
        setLogo(null);  
        setLicenceName("LGPL");
        setLicenceText(Licences.getInstance().getLGPL());

        setContributors(Arrays.asList(
            new Contributor[]{
                new Contributor("Eric Alexander", "-"),
                new Contributor("Richard Atkinson", 
                        "richard_c_atkinson@ntlworld.com"),
                new Contributor("David Basten", "-"),
                new Contributor("David Berry", "-"),
                new Contributor("Chris Boek", "-"),
                new Contributor("Zoheb Borbora", "-"),
                new Contributor("Anthony Boulestreau", "-"),
                new Contributor("Jeremy Bowman", "-"),
                new Contributor("Nicolas Brodu", "-"),
                new Contributor("Jody Brownell", "-"),
                new Contributor("David Browning", "-"),
                new Contributor("Soren Caspersen", "-"),
                new Contributor("Chuanhao Chiu", "-"),
                new Contributor("Brian Cole", "-"),
                new Contributor("Pascal Collet", "-"),
                new Contributor("Martin Cordova", "-"),
                new Contributor("Paolo Cova", "-"),
                new Contributor("Mike Duffy", "-"),
                new Contributor("Don Elliott", "-"),
                new Contributor("Jonathan Gabbai", "-"),
                new Contributor("David Gilbert", 
                        "david.gilbert@object-refinery.com"),
                new Contributor("Serge V. Grachov", "-"),
                new Contributor("Daniel Gredler", "-"),
                new Contributor("Hans-Jurgen Greiner", "-"),
                new Contributor("Joao Guilherme Del Valle", "-"),
                new Contributor("Aiman Han", "-"),
                new Contributor("Cameron Hayne", "-"),
                new Contributor("Jon Iles", "-"),
                new Contributor("Wolfgang Irler", "-"),
                new Contributor("Sergei Ivanov", "-"),
                new Contributor("Adriaan Joubert", "-"),
                new Contributor("Darren Jung", "-"),
                new Contributor("Xun Kang", "-"),
                new Contributor("Bill Kelemen", "-"),
                new Contributor("Norbert Kiesel", "-"),
                new Contributor("Gideon Krause", "-"),
                new Contributor("Pierre-Marie Le Biot", "-"),
                new Contributor("Arnaud Lelievre", "-"),
                new Contributor("Wolfgang Lenhard", "-"),
                new Contributor("David Li", "-"),
                new Contributor("Yan Liu", "-"),
                new Contributor("Tin Luu", "-"),
                new Contributor("Craig MacFarlane", "-"),
                new Contributor("Achilleus Mantzios", "-"),
                new Contributor("Thomas Meier", "-"),
                new Contributor("Jim Moore", "-"),
                new Contributor("Jonathan Nash", "-"),
                new Contributor("Barak Naveh", "-"),
                new Contributor("David M. O'Donnell", "-"),
                new Contributor("Krzysztof Paz", "-"),
                new Contributor("Tomer Peretz", "-"),
                new Contributor("Xavier Poinsard", "-"),
                new Contributor("Andrzej Porebski", "-"),
                new Contributor("Viktor Rajewski", "-"),
                new Contributor("Eduardo Ramalho", "-"),
                new Contributor("Michael Rauch", "-"),
                new Contributor("Cameron Riley", "-"),
                new Contributor("Klaus Rheinwald", "-"),
                new Contributor("Dan Rivett", "d.rivett@ukonline.co.uk"),
                new Contributor("Scott Sams", "-"),
                new Contributor("Michel Santos", "-"),
                new Contributor("Thierry Saura", "-"),
                new Contributor("Andreas Schneider", "-"),
                new Contributor("Jean-Luc SCHWAB", "-"),
                new Contributor("Bryan Scott", "-"),
                new Contributor("Tobias Selb", "-"),
                new Contributor("Mofeed Shahin", "-"),
                new Contributor("Pady Srinivasan", "-"),
                new Contributor("Greg Steckman", "-"),
                new Contributor("Roger Studner", "-"),
                new Contributor("Irv Thomae", "-"),
                new Contributor("Eric Thomas", "-"),
                new Contributor("Rich Unger", "-"),
                new Contributor("Daniel van Enckevort", "-"),
                new Contributor("Laurence Vanhelsuwe", "-"),
                new Contributor("Sylvain Vieujot", "-"),
                new Contributor("Jelai Wang", "-"),
                new Contributor("Mark Watson", "www.markwatson.com"),
                new Contributor("Alex Weber", "-"),
                new Contributor("Matthew Wright", "-"),
                new Contributor("Benoit Xhenseval", "-"),
                new Contributor("Christian W. Zuckschwerdt", 
                        "Christian.Zuckschwerdt@Informatik.Uni-Oldenburg.de"),
                new Contributor("Hari", "-"),
                new Contributor("Sam (oldman)", "-"),
            }
        ));

    }

    
    public Image getLogo() {

        Image logo = super.getLogo();
        if (logo == null) {
            URL imageURL = this.getClass().getClassLoader().getResource(
                    "org/jfree/chart/gorilla.jpg");
            if (imageURL != null) {
                ImageIcon temp = new ImageIcon(imageURL);  
                    
                logo = temp.getImage();
                setLogo(logo);
            }
        }
        return logo;

    }

}
