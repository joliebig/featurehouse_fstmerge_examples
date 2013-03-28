

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;


public class RingPlot extends PiePlot implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 1556064784129676620L;

    
    private boolean separatorsVisible;

    
    private transient Stroke separatorStroke;

    
    private transient Paint separatorPaint;

    
    private double innerSeparatorExtension;

    
    private double outerSeparatorExtension;

    
    private double sectionDepth;

    
    public RingPlot() {
        this(null);
    }

    
    public RingPlot(PieDataset dataset) {
        super(dataset);
        this.separatorsVisible = true;
        this.separatorStroke = new BasicStroke(0.5f);
        this.separatorPaint = Color.gray;
        this.innerSeparatorExtension = 0.20;  
        this.outerSeparatorExtension = 0.20;  
        this.sectionDepth = 0.20; 
    }

    
    public boolean getSeparatorsVisible() {
        return this.separatorsVisible;
    }

    
    public void setSeparatorsVisible(boolean visible) {
        this.separatorsVisible = visible;
        fireChangeEvent();
    }

    
    public Stroke getSeparatorStroke() {
        return this.separatorStroke;
    }

    
    public void setSeparatorStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.separatorStroke = stroke;
        fireChangeEvent();
    }

    
    public Paint getSeparatorPaint() {
        return this.separatorPaint;
    }

    
    public void setSeparatorPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.separatorPaint = paint;
        fireChangeEvent();
    }

    
    public double getInnerSeparatorExtension() {
        return this.innerSeparatorExtension;
    }

    
    public void setInnerSeparatorExtension(double percent) {
        this.innerSeparatorExtension = percent;
        fireChangeEvent();
    }

    
    public double getOuterSeparatorExtension() {
        return this.outerSeparatorExtension;
    }

    
    public void setOuterSeparatorExtension(double percent) {
        this.outerSeparatorExtension = percent;
        fireChangeEvent();
    }

    
    public double getSectionDepth() {
        return this.sectionDepth;
    }

    
    public void setSectionDepth(double sectionDepth) {
        this.sectionDepth = sectionDepth;
        fireChangeEvent();
    }

    
    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea,
            PiePlot plot, Integer index, PlotRenderingInfo info) {

        PiePlotState state = super.initialise(g2, plotArea, plot, index, info);
        state.setPassesRequired(3);
        return state;

    }

    
    protected void drawItem(Graphics2D g2,
                            int section,
                            Rectangle2D dataArea,
                            PiePlotState state,
                            int currentPass) {

        PieDataset dataset = getDataset();
        Number n = dataset.getValue(section);
        if (n == null) {
            return;
        }
        double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;

        Rotation direction = getDirection();
        if (direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        }
        else if (direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;
        }
        else {
            throw new IllegalStateException("Rotation type not recognised.");
        }

        double angle = (angle2 - angle1);
        if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
            Comparable key = getSectionKey(section);
            double ep = 0.0;
            double mep = getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = getExplodePercent(key) / mep;
            }
            Rectangle2D arcBounds = getArcBounds(state.getPieArea(),
                    state.getExplodedPieArea(), angle1, angle, ep);
            Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle,
                    Arc2D.OPEN);

            
            double depth = this.sectionDepth / 2.0;
            RectangleInsets s = new RectangleInsets(UnitType.RELATIVE,
                depth, depth, depth, depth);
            Rectangle2D innerArcBounds = new Rectangle2D.Double();
            innerArcBounds.setRect(arcBounds);
            s.trim(innerArcBounds);
            
            
            Arc2D.Double arc2 = new Arc2D.Double(innerArcBounds, angle1
                    + angle, -angle, Arc2D.OPEN);
            GeneralPath path = new GeneralPath();
            path.moveTo((float) arc.getStartPoint().getX(),
                    (float) arc.getStartPoint().getY());
            path.append(arc.getPathIterator(null), false);
            path.append(arc2.getPathIterator(null), true);
            path.closePath();

            Line2D separator = new Line2D.Double(arc2.getEndPoint(),
                    arc.getStartPoint());

            if (currentPass == 0) {
                Paint shadowPaint = getShadowPaint();
                double shadowXOffset = getShadowXOffset();
                double shadowYOffset = getShadowYOffset();
                if (shadowPaint != null) {
                    Shape shadowArc = ShapeUtilities.createTranslatedShape(
                            path, (float) shadowXOffset, (float) shadowYOffset);
                    g2.setPaint(shadowPaint);
                    g2.fill(shadowArc);
                }
            }
            else if (currentPass == 1) {
                Paint paint = lookupSectionPaint(key);
                g2.setPaint(paint);
                g2.fill(path);
                Paint outlinePaint = lookupSectionOutlinePaint(key);
                Stroke outlineStroke = lookupSectionOutlineStroke(key);
                if (outlinePaint != null && outlineStroke != null) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(path);
                }

                
                if (state.getInfo() != null) {
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        String tip = null;
                        PieToolTipGenerator toolTipGenerator
                                = getToolTipGenerator();
                        if (toolTipGenerator != null) {
                            tip = toolTipGenerator.generateToolTip(dataset,
                                    key);
                        }
                        String url = null;
                        PieURLGenerator urlGenerator = getURLGenerator();
                        if (urlGenerator != null) {
                            url = urlGenerator.generateURL(dataset, key,
                                    getPieIndex());
                        }
                        PieSectionEntity entity = new PieSectionEntity(path,
                                dataset, getPieIndex(), section, key, tip,
                                url);
                        entities.add(entity);
                    }
                }
            }
            else if (currentPass == 2) {
                if (this.separatorsVisible) {
                    Line2D extendedSeparator = extendLine(separator,
                        this.innerSeparatorExtension,
                        this.outerSeparatorExtension);
                    g2.setStroke(this.separatorStroke);
                    g2.setPaint(this.separatorPaint);
                    g2.draw(extendedSeparator);
                }
            }
        }
        state.setLatestAngle(angle2);
    }

    
    protected double getLabelLinkDepth() {
        return Math.min(super.getLabelLinkDepth(), getSectionDepth() / 2);
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RingPlot)) {
            return false;
        }
        RingPlot that = (RingPlot) obj;
        if (this.separatorsVisible != that.separatorsVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.separatorStroke,
                that.separatorStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.separatorPaint, that.separatorPaint)) {
            return false;
        }
        if (this.innerSeparatorExtension != that.innerSeparatorExtension) {
            return false;
        }
        if (this.outerSeparatorExtension != that.outerSeparatorExtension) {
            return false;
        }
        if (this.sectionDepth != that.sectionDepth) {
            return false;
        }
        return super.equals(obj);
    }

    
    private Line2D extendLine(Line2D line, double startPercent,
                              double endPercent) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        double x1 = line.getX1();
        double x2 = line.getX2();
        double deltaX = x2 - x1;
        double y1 = line.getY1();
        double y2 = line.getY2();
        double deltaY = y2 - y1;
        x1 = x1 - (startPercent * deltaX);
        y1 = y1 - (startPercent * deltaY);
        x2 = x2 + (endPercent * deltaX);
        y2 = y2 + (endPercent * deltaY);
        return new Line2D.Double(x1, y1, x2, y2);
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.separatorStroke, stream);
        SerialUtilities.writePaint(this.separatorPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.separatorStroke = SerialUtilities.readStroke(stream);
        this.separatorPaint = SerialUtilities.readPaint(stream);
    }

}
