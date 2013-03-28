

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;


public class PeriodAxisLabelInfo implements Cloneable, Serializable {
    
    
    
    
    
    
    
    private static final long serialVersionUID = 5710451740920277357L;
    
    
    public static final RectangleInsets DEFAULT_INSETS 
        = new RectangleInsets(2, 2, 2, 2);
    
    
    public static final Font DEFAULT_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);
    
    
    public static final Paint DEFAULT_LABEL_PAINT = Color.black;
    
    
    public static final Stroke DEFAULT_DIVIDER_STROKE = new BasicStroke(0.5f);
    
    
    public static final Paint DEFAULT_DIVIDER_PAINT = Color.gray;

    
    private Class periodClass;
    
    
    private RectangleInsets padding;
    
    
    private DateFormat dateFormat;
    
    
    private Font labelFont;
    
    
    private transient Paint labelPaint;
    
    
    private boolean drawDividers;
    
    
    private transient Stroke dividerStroke;
    
    
    private transient Paint dividerPaint;
        
    
    public PeriodAxisLabelInfo(Class periodClass, DateFormat dateFormat) {
        this(
            periodClass, dateFormat, DEFAULT_INSETS, DEFAULT_FONT, 
            DEFAULT_LABEL_PAINT, true, DEFAULT_DIVIDER_STROKE, 
            DEFAULT_DIVIDER_PAINT
        );
    }
    
    
    public PeriodAxisLabelInfo(Class periodClass, DateFormat dateFormat, 
                               RectangleInsets padding,
                               Font labelFont, Paint labelPaint, 
                               boolean drawDividers, Stroke dividerStroke, 
                               Paint dividerPaint) {
        if (periodClass == null) {
            throw new IllegalArgumentException("Null 'periodClass' argument.");   
        }
        if (dateFormat == null) {
            throw new IllegalArgumentException("Null 'dateFormat' argument.");   
        }
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");   
        }
        if (labelFont == null) {
            throw new IllegalArgumentException("Null 'labelFont' argument.");   
        }
        if (labelPaint == null) {
            throw new IllegalArgumentException("Null 'labelPaint' argument.");   
        }
        if (dividerStroke == null) {
            throw new IllegalArgumentException("Null 'dividerStroke' argument.");   
        }
        if (dividerPaint == null) {
            throw new IllegalArgumentException("Null 'dividerPaint' argument.");   
        }
        this.periodClass = periodClass;
        this.dateFormat = dateFormat;
        this.padding = padding;
        this.labelFont = labelFont;
        this.labelPaint = labelPaint;
        this.drawDividers = drawDividers;
        this.dividerStroke = dividerStroke;
        this.dividerPaint = dividerPaint;
    }
    
    
    public Class getPeriodClass() {
        return this.periodClass;   
    }
    
    
    public DateFormat getDateFormat() {
        return this.dateFormat;   
    }
    
    
    public RectangleInsets getPadding() {
        return this.padding;   
    }
    
    
    public Font getLabelFont() {
        return this.labelFont;   
    }
    
    
    public Paint getLabelPaint() {
        return this.labelPaint;   
    }
    
    
    public boolean getDrawDividers() {
        return this.drawDividers;   
    }
    
    
    public Stroke getDividerStroke() {
        return this.dividerStroke;   
    }
    
    
    public Paint getDividerPaint() {
        return this.dividerPaint;   
    }
    
    
    public RegularTimePeriod createInstance(Date millisecond, TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            Constructor c = this.periodClass.getDeclaredConstructor(
                new Class[] {Date.class, TimeZone.class}
            );
            result = (RegularTimePeriod) c.newInstance(
                new Object[] {millisecond, zone}
            );   
        }
        catch (Exception e) {
            
        }
        return result;  
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof PeriodAxisLabelInfo) {
            PeriodAxisLabelInfo info = (PeriodAxisLabelInfo) obj;
            if (!info.periodClass.equals(this.periodClass)) {
                return false;   
            }
            if (!info.dateFormat.equals(this.dateFormat)) {
                return false;   
            }
            if (!info.padding.equals(this.padding)) {
                return false;   
            }
            if (!info.labelFont.equals(this.labelFont)) {
                return false;
            }
            if (!info.labelPaint.equals(this.labelPaint)) {
                return false;   
            }
            if (info.drawDividers != this.drawDividers) {
                return false;   
            }
            if (!info.dividerStroke.equals(this.dividerStroke)) {
                return false;   
            }
            if (!info.dividerPaint.equals(this.dividerPaint)) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
    
    public int hashCode() {
        int result = 41;
        result = 37 * this.periodClass.hashCode();
        result = 37 * this.dateFormat.hashCode();
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        Object clone = (PeriodAxisLabelInfo) super.clone();
        return clone;
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.labelPaint, stream);
        SerialUtilities.writeStroke(this.dividerStroke, stream);
        SerialUtilities.writePaint(this.dividerPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.labelPaint = SerialUtilities.readPaint(stream);
        this.dividerStroke = SerialUtilities.readStroke(stream);
        this.dividerPaint = SerialUtilities.readPaint(stream);
    }
   
}
