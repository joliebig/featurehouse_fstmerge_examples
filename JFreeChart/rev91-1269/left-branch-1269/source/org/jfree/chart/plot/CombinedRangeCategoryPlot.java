

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;


public class CombinedRangeCategoryPlot extends CategoryPlot
        implements PlotChangeListener {

    
    private static final long serialVersionUID = 7260210007554504515L;

    
    private List subplots;

    
    private int totalWeight;

    
    private double gap;

    
    private transient Rectangle2D[] subplotArea;  

    
    public CombinedRangeCategoryPlot() {
        this(new NumberAxis());
    }

    
    public CombinedRangeCategoryPlot(ValueAxis rangeAxis) {
        super(null, null, rangeAxis, null);
        this.subplots = new java.util.ArrayList();
        this.totalWeight = 0;
        this.gap = 5.0;
    }

    
    public double getGap() {
        return this.gap;
    }

    
    public void setGap(double gap) {
        this.gap = gap;
        fireChangeEvent();
    }

    
    public void add(CategoryPlot subplot) {
        
        add(subplot, 1);
    }

    
    public void add(CategoryPlot subplot, int weight) {
        if (subplot == null) {
            throw new IllegalArgumentException("Null 'subplot' argument.");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Require weight >= 1.");
        }
        
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        subplot.setRangeAxis(null);
        subplot.setOrientation(getOrientation());
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        this.totalWeight += weight;

        
        ValueAxis axis = getRangeAxis();
        if (axis != null) {
            axis.configure();
        }
        fireChangeEvent();
    }

    
    public void remove(CategoryPlot subplot) {
        if (subplot == null) {
            throw new IllegalArgumentException(" Null 'subplot' argument.");
        }
        int position = -1;
        int size = this.subplots.size();
        int i = 0;
        while (position == -1 && i < size) {
            if (this.subplots.get(i) == subplot) {
                position = i;
            }
            i++;
        }
        if (position != -1) {
            this.subplots.remove(position);
            subplot.setParent(null);
            subplot.removeChangeListener(this);
            this.totalWeight -= subplot.getWeight();

            ValueAxis range = getRangeAxis();
            if (range != null) {
                range.configure();
            }

            ValueAxis range2 = getRangeAxis(1);
            if (range2 != null) {
                range2.configure();
            }
            fireChangeEvent();
        }
    }

    
    public List getSubplots() {
        if (this.subplots != null) {
            return Collections.unmodifiableList(this.subplots);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    
    protected AxisSpace calculateAxisSpace(Graphics2D g2,
                                           Rectangle2D plotArea) {

        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = getOrientation();

        
        AxisSpace fixed = getFixedRangeAxisSpace();
        if (fixed != null) {
            if (orientation == PlotOrientation.VERTICAL) {
                space.setLeft(fixed.getLeft());
                space.setRight(fixed.getRight());
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                space.setTop(fixed.getTop());
                space.setBottom(fixed.getBottom());
            }
        }
        else {
            ValueAxis valueAxis = getRangeAxis();
            RectangleEdge valueEdge = Plot.resolveRangeAxisLocation(
                    getRangeAxisLocation(), orientation);
            if (valueAxis != null) {
                space = valueAxis.reserveSpace(g2, this, plotArea, valueEdge,
                        space);
            }
        }

        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        
        int n = this.subplots.size();

        
        
        this.subplotArea = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getWidth() - this.gap * (n - 1);
        }
        else if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getHeight() - this.gap * (n - 1);
        }

        for (int i = 0; i < n; i++) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);

            
            if (orientation == PlotOrientation.VERTICAL) {
                double w = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotArea[i] = new Rectangle2D.Double(x, y, w,
                        adjustedPlotArea.getHeight());
                x = x + w + this.gap;
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                double h = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotArea[i] = new Rectangle2D.Double(x, y,
                        adjustedPlotArea.getWidth(), h);
                y = y + h + this.gap;
            }

            AxisSpace subSpace = plot.calculateDomainAxisSpace(g2,
                    this.subplotArea[i], null);
            space.ensureAtLeast(subSpace);

        }

        return space;
    }

    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        
        if (info != null) {
            info.setPlotArea(area);
        }

        
        RectangleInsets insets = getInsets();
        insets.trim(area);

        
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);

        
        setFixedDomainAxisSpaceForSubplots(space);

        
        ValueAxis axis = getRangeAxis();
        RectangleEdge rangeEdge = getRangeAxisEdge();
        double cursor = RectangleEdge.coordinate(dataArea, rangeEdge);
        AxisState state = axis.draw(g2, cursor, area, dataArea, rangeEdge,
                info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, state);

        
        for (int i = 0; i < this.subplots.size(); i++) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            Point2D subAnchor = null;
            if (anchor != null && this.subplotArea[i].contains(anchor)) {
            	subAnchor = anchor;
            }
            plot.draw(g2, this.subplotArea[i], subAnchor, parentState,
            		subplotInfo);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

    }

    
    public void setOrientation(PlotOrientation orientation) {

        super.setOrientation(orientation);

        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot plot = (CategoryPlot) iterator.next();
            plot.setOrientation(orientation);
        }

    }

    
     public Range getDataRange(ValueAxis axis) {
         Range result = null;
         if (this.subplots != null) {
             Iterator iterator = this.subplots.iterator();
             while (iterator.hasNext()) {
                 CategoryPlot subplot = (CategoryPlot) iterator.next();
                 result = Range.combine(result, subplot.getDataRange(axis));
             }
         }
         return result;
     }

    
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = getFixedLegendItems();
        if (result == null) {
            result = new LegendItemCollection();
            if (this.subplots != null) {
                Iterator iterator = this.subplots.iterator();
                while (iterator.hasNext()) {
                    CategoryPlot plot = (CategoryPlot) iterator.next();
                    LegendItemCollection more = plot.getLegendItems();
                    result.addAll(more);
                }
            }
        }
        return result;
    }

    
    protected void setFixedDomainAxisSpaceForSubplots(AxisSpace space) {
        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot plot = (CategoryPlot) iterator.next();
            plot.setFixedDomainAxisSpace(space, false);
        }
    }

    
    public void handleClick(int x, int y, PlotRenderingInfo info) {

        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            for (int i = 0; i < this.subplots.size(); i++) {
                CategoryPlot subplot = (CategoryPlot) this.subplots.get(i);
                PlotRenderingInfo subplotInfo = info.getSubplotInfo(i);
                subplot.handleClick(x, y, subplotInfo);
            }
        }

    }

    
    public void plotChanged(PlotChangeEvent event) {
        notifyListeners(event);
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CombinedRangeCategoryPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CombinedRangeCategoryPlot that = (CombinedRangeCategoryPlot) obj;
        if (!ObjectUtilities.equal(this.subplots, that.subplots)) {
            return false;
        }
        if (this.totalWeight != that.totalWeight) {
            return false;
        }
        if (this.gap != that.gap) {
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        CombinedRangeCategoryPlot result
            = (CombinedRangeCategoryPlot) super.clone();
        result.subplots = (List) ObjectUtilities.deepClone(this.subplots);
        for (Iterator it = result.subplots.iterator(); it.hasNext();) {
            Plot child = (Plot) it.next();
            child.setParent(result);
        }

        
        
        ValueAxis rangeAxis = result.getRangeAxis();
        if (rangeAxis != null) {
            rangeAxis.configure();
        }

        return result;
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();

        
        
        ValueAxis rangeAxis = getRangeAxis();
        if (rangeAxis != null) {
            rangeAxis.configure();
        }

    }

}
