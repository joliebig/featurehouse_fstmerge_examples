

package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.ValueDataset;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;


public class DialPlot extends Plot implements DialLayerChangeListener {

    
    private DialLayer background;
    
    
    private DialLayer cap;
    
    
    private DialFrame dialFrame;
    
    
    private ObjectList datasets;
    
    
    private ObjectList scales;
    
    
    private ObjectList datasetToScaleMap;

    
    private List layers;
    
    
    private List pointers;
    
    
    private double viewX;
    
    
    private double viewY;
    
    
    private double viewW;
    
    
    private double viewH;
    
    
    public DialPlot() {
        this(null);    
    }
    
    
    public DialPlot(ValueDataset dataset) {
        this.background = null;
        this.cap = null;
        this.dialFrame = new ArcDialFrame();
        this.datasets = new ObjectList();
        if (dataset != null) {
            this.setDataset(dataset);  
        }
        this.scales = new ObjectList();
        this.datasetToScaleMap = new ObjectList();
        this.layers = new java.util.ArrayList();
        this.pointers = new java.util.ArrayList();
        this.viewX = 0.0;
        this.viewY = 0.0;
        this.viewW = 1.0;
        this.viewH = 1.0;
    }

    
    public DialLayer getBackground() {
        return this.background;
    }
    
    
    public void setBackground(DialLayer background) {
        if (this.background != null) {
            this.background.removeChangeListener(this);
        }
        this.background = background;
        if (background != null) {
            background.addChangeListener(this);
        }
        fireChangeEvent();
    }
    
    
    public DialLayer getCap() {
        return this.cap;
    }
    
    
    public void setCap(DialLayer cap) {
        if (this.cap != null) {
            this.cap.removeChangeListener(this);
        }
        this.cap = cap;
        if (cap != null) {
            cap.addChangeListener(this);
        }
        fireChangeEvent();
    }

    
    public DialFrame getDialFrame() {
        return this.dialFrame;
    }
    
    
    public void setDialFrame(DialFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("Null 'frame' argument.");
        }
        this.dialFrame.removeChangeListener(this);
        this.dialFrame = frame;
        frame.addChangeListener(this);
        fireChangeEvent();
    }

    
    public double getViewX() {
        return this.viewX;
    }
    
    
    public double getViewY() {
        return this.viewY;
    }
    
    
    public double getViewWidth() {
        return this.viewW;
    }
    
    
    public double getViewHeight() {
        return this.viewH;
    }
    
    
    public void setView(double x, double y, double w, double h) {
        this.viewX = x;
        this.viewY = y;
        this.viewW = w;
        this.viewH = h;
        fireChangeEvent();
    }

    
    public void addLayer(DialLayer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' argument.");
        }
        this.layers.add(layer);
        layer.addChangeListener(this);
        fireChangeEvent();
    }
    
    
    public int getLayerIndex(DialLayer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' argument.");
        }
        return this.layers.indexOf(layer);
    }
    
    
    public void removeLayer(int index) {
        DialLayer layer = (DialLayer) this.layers.get(index);
        if (layer != null) {
            layer.removeChangeListener(this);
        }
        this.layers.remove(index);
        fireChangeEvent();
    }
    
    
    public void removeLayer(DialLayer layer) {
        
        removeLayer(getLayerIndex(layer));
    }
    
    
    public void addPointer(DialPointer pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("Null 'pointer' argument.");
        }
        this.pointers.add(pointer);
        pointer.addChangeListener(this);
        fireChangeEvent();
    }
    
    
    public int getPointerIndex(DialPointer pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("Null 'pointer' argument.");
        }
        return this.pointers.indexOf(pointer);
    }
    
    
    public void removePointer(int index) {
        DialPointer pointer = (DialPointer) this.pointers.get(index);
        if (pointer != null) {
            pointer.removeChangeListener(this);
        }
        this.pointers.remove(index);
        fireChangeEvent();
    }
    
    
    public void removePointer(DialPointer pointer) {
        
        removeLayer(getPointerIndex(pointer));
    }

    
    public DialPointer getPointerForDataset(int datasetIndex) {
        DialPointer result = null;
        Iterator iterator = this.pointers.iterator();
        while (iterator.hasNext()) {
            DialPointer p = (DialPointer) iterator.next();
            if (p.getDatasetIndex() == datasetIndex) {
                return p;
            }
        }
        return result;
    }
    
    
    public ValueDataset getDataset() {
        return getDataset(0);
    }

    
    public ValueDataset getDataset(int index) {
        ValueDataset result = null;
        if (this.datasets.size() > index) {
            result = (ValueDataset) this.datasets.get(index);
        }
        return result;
    }

    
    public void setDataset(ValueDataset dataset) {
        setDataset(0, dataset);
    }

    
    public void setDataset(int index, ValueDataset dataset) {
        
        ValueDataset existing = (ValueDataset) this.datasets.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        
        
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
        
    }

    
    public int getDatasetCount() {
        return this.datasets.size();
    }    
    
    
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, 
            PlotState parentState, PlotRenderingInfo info) {
        
        Shape origClip = g2.getClip();
        g2.setClip(area);
        
        
        Rectangle2D frame = viewToFrame(area);
        
        
        if (this.background != null && this.background.isVisible()) {
            if (this.background.isClippedToWindow()) {
                Shape savedClip = g2.getClip();
                g2.clip(this.dialFrame.getWindow(frame));
                this.background.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            }
            else {
                this.background.draw(g2, this, frame, area);
            }
        }
        
        Iterator iterator = this.layers.iterator();
        while (iterator.hasNext()) {
            DialLayer current = (DialLayer) iterator.next();
            if (current.isVisible()) {
                if (current.isClippedToWindow()) {
                    Shape savedClip = g2.getClip();
                    g2.clip(this.dialFrame.getWindow(frame));
                    current.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                }
                else {
                    current.draw(g2, this, frame, area);
                }
            }
        }
        
        
        iterator = this.pointers.iterator();
        while (iterator.hasNext()) {
            DialPointer current = (DialPointer) iterator.next();
            if (current.isVisible()) {
                if (current.isClippedToWindow()) {
                    Shape savedClip = g2.getClip();
                    g2.clip(this.dialFrame.getWindow(frame));
                    current.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                }
                else {
                    current.draw(g2, this, frame, area);
                }
            }
        }

        
        if (this.cap != null && this.cap.isVisible()) {
            if (this.cap.isClippedToWindow()) {
                Shape savedClip = g2.getClip();
                g2.clip(this.dialFrame.getWindow(frame));
                this.cap.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            }
            else {
                this.cap.draw(g2, this, frame, area);
            }
        }
        
        if (this.dialFrame.isVisible()) {
            this.dialFrame.draw(g2, this, frame, area);
        }
        
        g2.setClip(origClip);
        
    }
    
    
    private Rectangle2D viewToFrame(Rectangle2D view) {
        double width = view.getWidth() / this.viewW;
        double height = view.getHeight() / this.viewH;
        double x = view.getX() - (width * this.viewX);
        double y = view.getY() - (height * this.viewY);
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    
    public double getValue(int datasetIndex) {
        double result = Double.NaN;
        ValueDataset dataset = getDataset(datasetIndex);
        if (dataset != null) {
            Number n = dataset.getValue();
            if (n != null) {
                result = n.doubleValue();
            }
        }
        return result;
    }
    
    
    public void addScale(int index, DialScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        DialScale existing = (DialScale) this.scales.get(index);
        if (existing != null) {
            removeLayer(existing);
        }
        this.layers.add(scale);
        this.scales.set(index, scale);
        scale.addChangeListener(this);
        fireChangeEvent();         
    }
    
    
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
        }
        return result;
    }

    
    public void mapDatasetToScale(int index, int scaleIndex) {
        this.datasetToScaleMap.set(index, new Integer(scaleIndex));  
        fireChangeEvent(); 
    }
    
    
    public DialScale getScaleForDataset(int datasetIndex) {
        DialScale result = (DialScale) this.scales.get(0);    
        Integer scaleIndex = (Integer) this.datasetToScaleMap.get(datasetIndex);
        if (scaleIndex != null) {
            result = getScale(scaleIndex.intValue());
        }
        return result;    
    }
    
    
    public static Rectangle2D rectangleByRadius(Rectangle2D rect, 
            double radiusW, double radiusH) {
        if (rect == null) {
            throw new IllegalArgumentException("Null 'rect' argument.");
        }
        double x = rect.getCenterX();
        double y = rect.getCenterY();
        double w = rect.getWidth() * radiusW;
        double h = rect.getHeight() * radiusH;
        return new Rectangle2D.Double(x - w / 2.0, y - h / 2.0, w, h);
    }
    
    
    public void dialLayerChanged(DialLayerChangeEvent event) {
        fireChangeEvent();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialPlot)) {
            return false;
        }
        DialPlot that = (DialPlot) obj;
        if (!ObjectUtilities.equal(this.background, that.background)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.cap, that.cap)) {
            return false;
        }
        if (!this.dialFrame.equals(that.dialFrame)) {
            return false;
        }
        if (this.viewX != that.viewX) {
            return false;
        }
        if (this.viewY != that.viewY) {
            return false;
        }
        if (this.viewW != that.viewW) {
            return false;
        }
        if (this.viewH != that.viewH) {
            return false;
        }
        if (!this.layers.equals(that.layers)) {
            return false;
        }
        if (!this.pointers.equals(that.pointers)) {
            return false;
        }
        return super.equals(obj);
    }

    
    public int hashCode() {
        int result = 193;
        result = 37 * result + ObjectUtilities.hashCode(this.background);
        result = 37 * result + ObjectUtilities.hashCode(this.cap);
        result = 37 * result + this.dialFrame.hashCode();
        long temp = Double.doubleToLongBits(this.viewX);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewY);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewW);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewH);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    
    public String getPlotType() {
        return "DialPlot";
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

    
}
