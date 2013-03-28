

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.needle.ArrowNeedle;
import org.jfree.chart.needle.LineNeedle;
import org.jfree.chart.needle.LongNeedle;
import org.jfree.chart.needle.MeterNeedle;
import org.jfree.chart.needle.MiddlePinNeedle;
import org.jfree.chart.needle.PinNeedle;
import org.jfree.chart.needle.PlumNeedle;
import org.jfree.chart.needle.PointerNeedle;
import org.jfree.chart.needle.ShipNeedle;
import org.jfree.chart.needle.WindNeedle;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;


public class CompassPlot extends Plot implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 6924382802125527395L;
    
    
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", 
            Font.BOLD, 10);

    
    public static final int NO_LABELS = 0;

    
    public static final int VALUE_LABELS = 1;

    
    private int labelType;

    
    private Font labelFont;

    
    private boolean drawBorder = false;

    
    private transient Paint roseHighlightPaint = Color.black;

    
    private transient Paint rosePaint = Color.yellow;

    
    private transient Paint roseCenterPaint = Color.white;

    
    private Font compassFont = new Font("Arial", Font.PLAIN, 10);

    
    private transient Ellipse2D circle1;

    
    private transient Ellipse2D circle2;

    
    private transient Area a1;

    
    private transient Area a2;

    
    private transient Rectangle2D rect1;

    
    private ValueDataset[] datasets = new ValueDataset[1];

    
    private MeterNeedle[] seriesNeedle = new MeterNeedle[1];

    
    protected static ResourceBundle localizationResources 
            = ResourceBundle.getBundle(
                    "org.jfree.chart.plot.LocalizationBundle");

    
    protected double revolutionDistance = 360;

    
    public CompassPlot() {
        this(new DefaultValueDataset());
    }

    
    public CompassPlot(ValueDataset dataset) {
        super();
        if (dataset != null) {
            this.datasets[0] = dataset;
            dataset.addChangeListener(this);
        }
        this.circle1 = new Ellipse2D.Double();
        this.circle2 = new Ellipse2D.Double();
        this.rect1   = new Rectangle2D.Double();
        setSeriesNeedle(0);
    }

    
    public int getLabelType() {
        
        return this.labelType;
    }

    
    public void setLabelType(int type) {
        
        if ((type != NO_LABELS) && (type != VALUE_LABELS)) {
            throw new IllegalArgumentException(
                    "MeterPlot.setLabelType(int): unrecognised type.");
        }
        if (this.labelType != type) {
            this.labelType = type;
            fireChangeEvent();
        }
    }

    
    public Font getLabelFont() {
        
        return this.labelFont;
    }

    
    public void setLabelFont(Font font) {
        
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' not allowed.");
        }
        this.labelFont = font;
        fireChangeEvent();
    }

    
    public Paint getRosePaint() {
        return this.rosePaint;   
    }
    
    
    public void setRosePaint(Paint paint) {
        if (paint == null) {   
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.rosePaint = paint;
        fireChangeEvent();        
    }

    
    public Paint getRoseCenterPaint() {
        return this.roseCenterPaint;   
    }
    
    
    public void setRoseCenterPaint(Paint paint) {
        if (paint == null) {   
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.roseCenterPaint = paint;
        fireChangeEvent();        
    }
    
    
    public Paint getRoseHighlightPaint() {
        return this.roseHighlightPaint;   
    }
    
    
    public void setRoseHighlightPaint(Paint paint) {
        if (paint == null) {   
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.roseHighlightPaint = paint;
        fireChangeEvent();        
    }
    
    
    public boolean getDrawBorder() {
        return this.drawBorder;
    }

    
    public void setDrawBorder(boolean status) {
        this.drawBorder = status;
        fireChangeEvent();
    }

    
    public void setSeriesPaint(int series, Paint paint) {
       
        if ((series >= 0) && (series < this.seriesNeedle.length)) {
            this.seriesNeedle[series].setFillPaint(paint);
        }
    }

    
    public void setSeriesOutlinePaint(int series, Paint p) {

        if ((series >= 0) && (series < this.seriesNeedle.length)) {
            this.seriesNeedle[series].setOutlinePaint(p);
        }

    }

    
    public void setSeriesOutlineStroke(int series, Stroke stroke) {

        if ((series >= 0) && (series < this.seriesNeedle.length)) {
            this.seriesNeedle[series].setOutlineStroke(stroke);
        }

    }

    
    public void setSeriesNeedle(int type) {
        setSeriesNeedle(0, type);
    }

    
    public void setSeriesNeedle(int index, int type) {
        switch (type) {
            case 0:
                setSeriesNeedle(index, new ArrowNeedle(true));
                setSeriesPaint(index, Color.red);
                this.seriesNeedle[index].setHighlightPaint(Color.white);
                break;
            case 1:
                setSeriesNeedle(index, new LineNeedle());
                break;
            case 2:
                MeterNeedle longNeedle = new LongNeedle();
                longNeedle.setRotateY(0.5);
                setSeriesNeedle(index, longNeedle);
                break;
            case 3:
                setSeriesNeedle(index, new PinNeedle());
                break;
            case 4:
                setSeriesNeedle(index, new PlumNeedle());
                break;
            case 5:
                setSeriesNeedle(index, new PointerNeedle());
                break;
            case 6:
                setSeriesPaint(index, null);
                setSeriesOutlineStroke(index, new BasicStroke(3));
                setSeriesNeedle(index, new ShipNeedle());
                break;
            case 7:
                setSeriesPaint(index, Color.blue);
                setSeriesNeedle(index, new WindNeedle());
                break;
            case 8:
                setSeriesNeedle(index, new ArrowNeedle(true));
                break;
            case 9:
                setSeriesNeedle(index, new MiddlePinNeedle());
                break;

            default:
                throw new IllegalArgumentException("Unrecognised type.");
        }

    }

    
    public void setSeriesNeedle(int index, MeterNeedle needle) {

        if ((needle != null) && (index < this.seriesNeedle.length)) {
            this.seriesNeedle[index] = needle;
        }
        fireChangeEvent();

    }

    
    public ValueDataset[] getDatasets() {
        return this.datasets;
    }

    
    public void addDataset(ValueDataset dataset) {
        addDataset(dataset, null);
    }

    
    public void addDataset(ValueDataset dataset, MeterNeedle needle) {

        if (dataset != null) {
            int i = this.datasets.length + 1;
            ValueDataset[] t = new ValueDataset[i];
            MeterNeedle[] p = new MeterNeedle[i];
            i = i - 2;
            for (; i >= 0; --i) {
                t[i] = this.datasets[i];
                p[i] = this.seriesNeedle[i];
            }
            i = this.datasets.length;
            t[i] = dataset;
            p[i] = ((needle != null) ? needle : p[i - 1]);

            ValueDataset[] a = this.datasets;
            MeterNeedle[] b = this.seriesNeedle;
            this.datasets = t;
            this.seriesNeedle = p;

            for (--i; i >= 0; --i) {
                a[i] = null;
                b[i] = null;
            }
            dataset.addChangeListener(this);
        }
    }

    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        int outerRadius = 0;
        int innerRadius = 0;
        int x1, y1, x2, y2;
        double a;

        if (info != null) {
            info.setPlotArea(area);
        }

        
        RectangleInsets insets = getInsets();
        insets.trim(area);

        
        if (this.drawBorder) {
            drawBackground(g2, area);
        }

        int midX = (int) (area.getWidth() / 2);
        int midY = (int) (area.getHeight() / 2);
        int radius = midX;
        if (midY < midX) {
            radius = midY;
        }
        --radius;
        int diameter = 2 * radius;

        midX += (int) area.getMinX();
        midY += (int) area.getMinY();

        this.circle1.setFrame(midX - radius, midY - radius, diameter, diameter);
        this.circle2.setFrame(
            midX - radius + 15, midY - radius + 15, 
            diameter - 30, diameter - 30
        );
        g2.setPaint(this.rosePaint);
        this.a1 = new Area(this.circle1);
        this.a2 = new Area(this.circle2);
        this.a1.subtract(this.a2);
        g2.fill(this.a1);

        g2.setPaint(this.roseCenterPaint);
        x1 = diameter - 30;
        g2.fillOval(midX - radius + 15, midY - radius + 15, x1, x1);
        g2.setPaint(this.roseHighlightPaint);
        g2.drawOval(midX - radius, midY - radius, diameter, diameter);
        x1 = diameter - 20;
        g2.drawOval(midX - radius + 10, midY - radius + 10, x1, x1);
        x1 = diameter - 30;
        g2.drawOval(midX - radius + 15, midY - radius + 15, x1, x1);
        x1 = diameter - 80;
        g2.drawOval(midX - radius + 40, midY - radius + 40, x1, x1);

        outerRadius = radius - 20;
        innerRadius = radius - 32;
        for (int w = 0; w < 360; w += 15) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            x2 = midX - ((int) (Math.sin(a) * outerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));
            y2 = midY - ((int) (Math.cos(a) * outerRadius));
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setPaint(this.roseHighlightPaint);
        innerRadius = radius - 26;
        outerRadius = 7;
        for (int w = 45; w < 360; w += 90) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));
            g2.fillOval(x1 - outerRadius, y1 - outerRadius, 2 * outerRadius, 
                    2 * outerRadius);
        }

        
        for (int w = 0; w < 360; w += 90) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));

            Polygon p = new Polygon();
            p.addPoint(x1 - outerRadius, y1);
            p.addPoint(x1, y1 + outerRadius);
            p.addPoint(x1 + outerRadius, y1);
            p.addPoint(x1, y1 - outerRadius);
            g2.fillPolygon(p);
        }

        
        innerRadius = radius - 42;
        Font f = getCompassFont(radius);
        g2.setFont(f);
        g2.drawString("N", midX - 5, midY - innerRadius + f.getSize());
        g2.drawString("S", midX - 5, midY + innerRadius - 5);
        g2.drawString("W", midX - innerRadius + 5, midY + 5);
        g2.drawString("E", midX + innerRadius - f.getSize(), midY + 5);

        
        y1 = radius / 2;
        x1 = radius / 6;
        Rectangle2D needleArea = new Rectangle2D.Double(
            (midX - x1), (midY - y1), (2 * x1), (2 * y1)
        );
        int x = this.seriesNeedle.length;
        int current = 0;
        double value = 0;
        int i = (this.datasets.length - 1);
        for (; i >= 0; --i) {
            ValueDataset data = this.datasets[i];

            if (data != null && data.getValue() != null) {
                value = (data.getValue().doubleValue()) 
                    % this.revolutionDistance;
                value = value / this.revolutionDistance * 360;
                current = i % x;
                this.seriesNeedle[current].draw(g2, needleArea, value);
            }
        }

        if (this.drawBorder) {
            drawOutline(g2, area);
        }

    }

    
    public String getPlotType() {
        return localizationResources.getString("Compass_Plot");
    }

    
    public LegendItemCollection getLegendItems() {
        return null;
    }

    
    public void zoom(double percent) {
        
    }

    
    protected Font getCompassFont(int radius) {
        float fontSize = radius / 10.0f;
        if (fontSize < 8) {
            fontSize = 8;
        }
        Font newFont = this.compassFont.deriveFont(fontSize);
        return newFont;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompassPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CompassPlot that = (CompassPlot) obj;
        if (this.labelType != that.labelType) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (this.drawBorder != that.drawBorder) {
            return false;
        }
        if (!PaintUtilities.equal(this.roseHighlightPaint, 
                that.roseHighlightPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.rosePaint, that.rosePaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.roseCenterPaint, 
                that.roseCenterPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.compassFont, that.compassFont)) {
            return false;
        }
        if (!Arrays.equals(this.seriesNeedle, that.seriesNeedle)) {
            return false;
        }
        if (getRevolutionDistance() != that.getRevolutionDistance()) {
            return false;
        }
        return true;

    }

    
    public Object clone() throws CloneNotSupportedException {

        CompassPlot clone = (CompassPlot) super.clone();
        if (this.circle1 != null) {
            clone.circle1 = (Ellipse2D) this.circle1.clone();
        }
        if (this.circle2 != null) {
            clone.circle2 = (Ellipse2D) this.circle2.clone();
        }
        if (this.a1 != null) {
            clone.a1 = (Area) this.a1.clone();
        }
        if (this.a2 != null) {
            clone.a2 = (Area) this.a2.clone();
        }
        if (this.rect1 != null) {
            clone.rect1 = (Rectangle2D) this.rect1.clone();            
        }
        clone.datasets = (ValueDataset[]) this.datasets.clone();
        clone.seriesNeedle = (MeterNeedle[]) this.seriesNeedle.clone();

        
        for (int i = 0; i < this.datasets.length; ++i) {
            if (clone.datasets[i] != null) {
                clone.datasets[i].addChangeListener(clone);
            }
        }
        return clone;

    }

    
    public void setRevolutionDistance(double size) {
        if (size > 0) {
            this.revolutionDistance = size;
        }
    }

    
    public double getRevolutionDistance() {
        return this.revolutionDistance;
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.rosePaint, stream);
        SerialUtilities.writePaint(this.roseCenterPaint, stream);
        SerialUtilities.writePaint(this.roseHighlightPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.rosePaint = SerialUtilities.readPaint(stream);
        this.roseCenterPaint = SerialUtilities.readPaint(stream);
        this.roseHighlightPaint = SerialUtilities.readPaint(stream);
    }

}
