

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedString;
import java.text.CharacterIterator;

import org.jfree.data.general.Dataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.AttributedStringUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;


public class LegendItem implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -797214582948827144L;

    
    private Dataset dataset;

    
    private Comparable seriesKey;

    
    private int datasetIndex;

    
    private int series;

    
    private String label;

    
    private Font labelFont;

    
    private transient Paint labelPaint;

    
    private transient AttributedString attributedLabel;

    
    private String description;

    
    private String toolTipText;

    
    private String urlText;

    
    private boolean shapeVisible;

    
    private transient Shape shape;

    
    private boolean shapeFilled;

    
    private transient Paint fillPaint;

    
    private GradientPaintTransformer fillPaintTransformer;

    
    private boolean shapeOutlineVisible;

    
    private transient Paint outlinePaint;

    
    private transient Stroke outlineStroke;

    
    private boolean lineVisible;

    
    private transient Shape line;

    
    private transient Stroke lineStroke;

    
    private transient Paint linePaint;

    
    private static final Shape UNUSED_SHAPE = new Line2D.Float();

    
    private static final Stroke UNUSED_STROKE = new BasicStroke(0.0f);

    
    public LegendItem(String label) {
        this(label, null, null, null, new Rectangle2D.Double(-4.0, -4.0, 8.0,
                8.0), Color.black);
    }

    
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      Shape shape, Paint fillPaint) {

        this(label, description, toolTipText, urlText,
                 true, shape,
                 true, fillPaint,
                 false, Color.black, UNUSED_STROKE,
                 false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.black);

    }

    
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      Shape shape, Paint fillPaint,
                      Stroke outlineStroke, Paint outlinePaint) {

        this(label, description, toolTipText, urlText,
                 true, shape,
                 true, fillPaint,
                 true, outlinePaint, outlineStroke,
                 false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.black);

    }

    
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      Shape line, Stroke lineStroke, Paint linePaint) {

        this(label, description, toolTipText, urlText,
                 false, UNUSED_SHAPE,
                 false, Color.black,
                 false, Color.black, UNUSED_STROKE,
                 true, line, lineStroke, linePaint);
    }

    
    public LegendItem(String label, String description,
                      String toolTipText, String urlText,
                      boolean shapeVisible, Shape shape,
                      boolean shapeFilled, Paint fillPaint,
                      boolean shapeOutlineVisible, Paint outlinePaint,
                      Stroke outlineStroke,
                      boolean lineVisible, Shape line,
                      Stroke lineStroke, Paint linePaint) {

        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        if (fillPaint == null) {
            throw new IllegalArgumentException("Null 'fillPaint' argument.");
        }
        if (lineStroke == null) {
            throw new IllegalArgumentException("Null 'lineStroke' argument.");
        }
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Null 'outlinePaint' argument.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException(
                    "Null 'outlineStroke' argument.");
        }
        this.label = label;
        this.labelPaint = null;
        this.attributedLabel = null;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    
    public LegendItem(AttributedString label, String description,
                      String toolTipText, String urlText,
                      Shape shape, Paint fillPaint) {

        this(label, description, toolTipText, urlText,
                 true, shape,
                 true, fillPaint,
                 false, Color.black, UNUSED_STROKE,
                 false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.black);

    }

    
    public LegendItem(AttributedString label, String description,
                      String toolTipText, String urlText,
                      Shape shape, Paint fillPaint,
                      Stroke outlineStroke, Paint outlinePaint) {

        this(label, description, toolTipText, urlText,
                 true, shape,
                 true, fillPaint,
                 true, outlinePaint, outlineStroke,
                 false, UNUSED_SHAPE, UNUSED_STROKE,
                Color.black);
    }

    
    public LegendItem(AttributedString label, String description,
                      String toolTipText, String urlText,
                      Shape line, Stroke lineStroke, Paint linePaint) {

        this(label, description, toolTipText, urlText,
                 false, UNUSED_SHAPE,
                 false, Color.black,
                 false, Color.black, UNUSED_STROKE,
                 true, line, lineStroke, linePaint);
    }

    
    public LegendItem(AttributedString label, String description,
                      String toolTipText, String urlText,
                      boolean shapeVisible, Shape shape,
                      boolean shapeFilled, Paint fillPaint,
                      boolean shapeOutlineVisible, Paint outlinePaint,
                      Stroke outlineStroke,
                      boolean lineVisible, Shape line, Stroke lineStroke,
                      Paint linePaint) {

        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        if (fillPaint == null) {
            throw new IllegalArgumentException("Null 'fillPaint' argument.");
        }
        if (lineStroke == null) {
            throw new IllegalArgumentException("Null 'lineStroke' argument.");
        }
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        if (linePaint == null) {
            throw new IllegalArgumentException("Null 'linePaint' argument.");
        }
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Null 'outlinePaint' argument.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException(
                "Null 'outlineStroke' argument.");
        }
        this.label = characterIteratorToString(label.getIterator());
        this.attributedLabel = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    
    private String characterIteratorToString(CharacterIterator iterator) {
        int endIndex = iterator.getEndIndex();
        int beginIndex = iterator.getBeginIndex();
        int count = endIndex - beginIndex;
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        int i = 0;
        char c = iterator.first();
        while (c != CharacterIterator.DONE) {
            chars[i] = c;
            i++;
            c = iterator.next();
        }
        return new String(chars);
    }

    
    public Dataset getDataset() {
        return this.dataset;
    }

    
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    
    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
    }

    
    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    
    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    
    public int getSeriesIndex() {
        return this.series;
    }

    
    public void setSeriesIndex(int index) {
        this.series = index;
    }

    
    public String getLabel() {
        return this.label;
    }

    
    public Font getLabelFont() {
        return this.labelFont;
    }

    
    public void setLabelFont(Font font) {
        this.labelFont = font;
    }

    
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    
    public void setLabelPaint(Paint paint) {
        this.labelPaint = paint;
    }

    
    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }

    
    public String getDescription() {
        return this.description;
    }

    
    public String getToolTipText() {
        return this.toolTipText;
    }

    
    public String getURLText() {
        return this.urlText;
    }

    
    public boolean isShapeVisible() {
        return this.shapeVisible;
    }

    
    public Shape getShape() {
        return this.shape;
    }

    
    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    
    public void setFillPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.fillPaint = paint;
    }

    
    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }

    
    public Stroke getLineStroke() {
        return this.lineStroke;
    }

    
    public Paint getLinePaint() {
        return this.linePaint;
    }

    
    public void setLinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.linePaint = paint;
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.outlinePaint = paint;
    }

    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    
    public boolean isLineVisible() {
        return this.lineVisible;
    }

    
    public Shape getLine() {
        return this.line;
    }

    
    public GradientPaintTransformer getFillPaintTransformer() {
        return this.fillPaintTransformer;
    }

    
    public void setFillPaintTransformer(GradientPaintTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Null 'transformer' attribute.");
        }
        this.fillPaintTransformer = transformer;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendItem)) {
                return false;
        }
        LegendItem that = (LegendItem) obj;
        if (this.datasetIndex != that.datasetIndex) {
            return false;
        }
        if (this.series != that.series) {
            return false;
        }
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!AttributedStringUtilities.equal(this.attributedLabel,
                that.attributedLabel)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.description, that.description)) {
            return false;
        }
        if (this.shapeVisible != that.shapeVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.shape, that.shape)) {
            return false;
        }
        if (this.shapeFilled != that.shapeFilled) {
            return false;
        }
        if (!PaintUtilities.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.fillPaintTransformer,
                that.fillPaintTransformer)) {
            return false;
        }
        if (this.shapeOutlineVisible != that.shapeOutlineVisible) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.lineVisible == that.lineVisible) {
            return false;
        }
        if (!ShapeUtilities.equal(this.line, that.line)) {
            return false;
        }
        if (!this.lineStroke.equals(that.lineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.linePaint, that.linePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        LegendItem clone = (LegendItem) super.clone();
        if (this.seriesKey instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.seriesKey;
            clone.seriesKey = (Comparable) pc.clone();
        }
        
        clone.shape = ShapeUtilities.clone(this.shape);
        if (this.fillPaintTransformer instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.fillPaintTransformer;
            clone.fillPaintTransformer = (GradientPaintTransformer) pc.clone();

        }
        clone.line = ShapeUtilities.clone(this.line);
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeAttributedString(this.attributedLabel, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeShape(this.line, stream);
        SerialUtilities.writeStroke(this.lineStroke, stream);
        SerialUtilities.writePaint(this.linePaint, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtilities.readAttributedString(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.line = SerialUtilities.readShape(stream);
        this.lineStroke = SerialUtilities.readStroke(stream);
        this.linePaint = SerialUtilities.readPaint(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
    }

}
