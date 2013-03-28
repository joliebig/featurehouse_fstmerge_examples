

package org.jfree.chart.plot;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;


public class CombinedDomainCategoryPlot extends CategoryPlot
                                        implements Zoomable,
                                                   Cloneable, PublicCloneable, 
                                                   Serializable,
                                                   PlotChangeListener {

    
    private static final long serialVersionUID = 8207194522653701572L;
    
    
    private List subplots;

    
    private int totalWeight;

    
    private double gap;

    
    private transient Rectangle2D[] subplotAreas;
    
    
    
    public CombinedDomainCategoryPlot() {
        this(new CategoryAxis());
    }
    
    
    public CombinedDomainCategoryPlot(CategoryAxis domainAxis) {
        super(null, domainAxis, null, null);
        this.subplots = new java.util.ArrayList();
        this.totalWeight = 0;
        this.gap = 5.0;
    }

    
    public double getGap() {
        return this.gap;
    }

    
    public void setGap(double gap) {
        this.gap = gap;
        notifyListeners(new PlotChangeEvent(this));
    }

    
    public void add(CategoryPlot subplot) {
        add(subplot, 1);    
    }
    
    
    public void add(CategoryPlot subplot, int weight) {
        if (subplot == null) {
            throw new IllegalArgumentException("Null 'subplot' argument.");
        }
        if (weight < 1) {
            throw new IllegalArgumentException("Require weight >= 1.");
        }
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        subplot.setDomainAxis(null);
        subplot.setOrientation(getOrientation());
        subplot.addChangeListener(this);
        this.subplots.add(subplot);
        this.totalWeight += weight;
        CategoryAxis axis = getDomainAxis();
        if (axis != null) {
            axis.configure();
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    
    public void remove(CategoryPlot subplot) {
        if (subplot == null) {
            throw new IllegalArgumentException("Null 'subplot' argument.");
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

            CategoryAxis domain = getDomainAxis();
            if (domain != null) {
                domain.configure();
            }
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    
    public List getSubplots() {
        return Collections.unmodifiableList(this.subplots);
    }

    
    public CategoryPlot findSubplot(PlotRenderingInfo info, Point2D source) {
        if (info == null) {
            throw new IllegalArgumentException("Null 'info' argument.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        CategoryPlot result = null;
        int subplotIndex = info.getSubplotIndex(source);
        if (subplotIndex >= 0) {
            result =  (CategoryPlot) this.subplots.get(subplotIndex);
        }
        return result;
    }
    
    
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, 
                              Point2D source) {
        
        CategoryPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(factor, info, source);
        }
        else {
            
            
            Iterator iterator = getSubplots().iterator();
            while (iterator.hasNext()) {
                subplot = (CategoryPlot) iterator.next();
                subplot.zoomRangeAxes(factor, info, source);
            }
        }
    }

    
    public void zoomRangeAxes(double lowerPercent, double upperPercent, 
                              PlotRenderingInfo info, Point2D source) {
        
        CategoryPlot subplot = findSubplot(info, source);
        if (subplot != null) {
            subplot.zoomRangeAxes(lowerPercent, upperPercent, info, source);
        }
        else {
            
            
            Iterator iterator = getSubplots().iterator();
            while (iterator.hasNext()) {
                subplot = (CategoryPlot) iterator.next();
                subplot.zoomRangeAxes(lowerPercent, upperPercent, info, source);
            }
        }
    }

    
    protected AxisSpace calculateAxisSpace(Graphics2D g2, 
                                           Rectangle2D plotArea) {
        
        AxisSpace space = new AxisSpace();
        PlotOrientation orientation = getOrientation();
        
        
        AxisSpace fixed = getFixedDomainAxisSpace();
        if (fixed != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.setLeft(fixed.getLeft());
                space.setRight(fixed.getRight());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.setTop(fixed.getTop());
                space.setBottom(fixed.getBottom());                
            }
        }
        else {
            CategoryAxis categoryAxis = getDomainAxis();
            RectangleEdge categoryEdge = Plot.resolveDomainAxisLocation(
                    getDomainAxisLocation(), orientation);
            if (categoryAxis != null) {
                space = categoryAxis.reserveSpace(g2, this, plotArea, 
                        categoryEdge, space);
            }
            else {
                if (getDrawSharedDomainAxis()) {
                    space = getDomainAxis().reserveSpace(g2, this, plotArea, 
                            categoryEdge, space);
                }
            }
        }
        
        Rectangle2D adjustedPlotArea = space.shrink(plotArea, null);
        
        
        int n = this.subplots.size();
        this.subplotAreas = new Rectangle2D[n];
        double x = adjustedPlotArea.getX();
        double y = adjustedPlotArea.getY();
        double usableSize = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            usableSize = adjustedPlotArea.getWidth() - this.gap * (n - 1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            usableSize = adjustedPlotArea.getHeight() - this.gap * (n - 1);
        }

        for (int i = 0; i < n; i++) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);

            
            if (orientation == PlotOrientation.HORIZONTAL) {
                double w = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, w, 
                        adjustedPlotArea.getHeight());
                x = x + w + this.gap;
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                double h = usableSize * plot.getWeight() / this.totalWeight;
                this.subplotAreas[i] = new Rectangle2D.Double(x, y, 
                        adjustedPlotArea.getWidth(), h);
                y = y + h + this.gap;
            }

            AxisSpace subSpace = plot.calculateRangeAxisSpace(g2, 
                    this.subplotAreas[i], null);
            space.ensureAtLeast(subSpace);

        }

        return space;
    }

    
    public void draw(Graphics2D g2, 
                     Rectangle2D area, 
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {
        
        
        if (info != null) {
            info.setPlotArea(area);
        }

        
        RectangleInsets insets = getInsets();
        area.setRect(area.getX() + insets.getLeft(),
                area.getY() + insets.getTop(),
                area.getWidth() - insets.getLeft() - insets.getRight(),
                area.getHeight() - insets.getTop() - insets.getBottom());


        
        setFixedRangeAxisSpaceForSubplots(null);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);

        
        setFixedRangeAxisSpaceForSubplots(space);

        
        CategoryAxis axis = getDomainAxis();
        RectangleEdge domainEdge = getDomainAxisEdge();
        double cursor = RectangleEdge.coordinate(dataArea, domainEdge);
        AxisState axisState = axis.draw(g2, cursor, area, dataArea, 
                domainEdge, info);
        if (parentState == null) {
            parentState = new PlotState();
        }
        parentState.getSharedAxisStates().put(axis, axisState);
        
        
        for (int i = 0; i < this.subplots.size(); i++) {
            CategoryPlot plot = (CategoryPlot) this.subplots.get(i);
            PlotRenderingInfo subplotInfo = null;
            if (info != null) {
                subplotInfo = new PlotRenderingInfo(info.getOwner());
                info.addSubplotInfo(subplotInfo);
            }
            plot.draw(g2, this.subplotAreas[i], null, parentState, subplotInfo);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

    }

    
    protected void setFixedRangeAxisSpaceForSubplots(AxisSpace space) {

        Iterator iterator = this.subplots.iterator();
        while (iterator.hasNext()) {
            CategoryPlot plot = (CategoryPlot) iterator.next();
            plot.setFixedRangeAxisSpace(space);
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
    
    
    public List getCategories() {
        List result = new java.util.ArrayList();
        if (this.subplots != null) {
            Iterator iterator = this.subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                List more = plot.getCategories();
                Iterator moreIterator = more.iterator();
                while (moreIterator.hasNext()) {
                    Comparable category = (Comparable) moreIterator.next();
                    if (!result.contains(category)) {
                        result.add(category);
                    }
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    
    public List getCategoriesForAxis(CategoryAxis axis) {
        
        
        return getCategories();    
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
        if (!(obj instanceof CombinedDomainCategoryPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CombinedDomainCategoryPlot plot = (CombinedDomainCategoryPlot) obj;
        if (!ObjectUtilities.equal(this.subplots, plot.subplots)) {
            return false;
        }
        if (this.totalWeight != plot.totalWeight) {
            return false;
        }
        if (this.gap != plot.gap) { 
            return false;
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        
        CombinedDomainCategoryPlot result 
            = (CombinedDomainCategoryPlot) super.clone(); 
        result.subplots = (List) ObjectUtilities.deepClone(this.subplots);
        for (Iterator it = result.subplots.iterator(); it.hasNext();) {
            Plot child = (Plot) it.next();
            child.setParent(result);
        }
        return result;
        
    }
    
}
