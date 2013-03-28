

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

import org.jfree.data.general.PieDataset;


public class PieSectionEntity extends ChartEntity
                              implements Serializable {

    
    private static final long serialVersionUID = 9199892576531984162L;
    
    
    private PieDataset dataset;
    
    
    private int pieIndex;

    
    private int sectionIndex;

    
    private Comparable sectionKey;

    
    public PieSectionEntity(Shape area, 
                            PieDataset dataset,
                            int pieIndex, int sectionIndex, 
                            Comparable sectionKey,
                            String toolTipText, String urlText) {

        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.pieIndex = pieIndex;
        this.sectionIndex = sectionIndex;
        this.sectionKey = sectionKey;

    }

    
    public PieDataset getDataset() {
        return this.dataset;
    }

    
    public void setDataset(PieDataset dataset) {
        this.dataset = dataset;
    }

    
    public int getPieIndex() {
        return this.pieIndex;
    }

    
    public void setPieIndex(int index) {
        this.pieIndex = index;
    }

    
    public int getSectionIndex() {
        return this.sectionIndex;
    }

    
    public void setSectionIndex(int index) {
        this.sectionIndex = index;
    }

    
    public Comparable getSectionKey() {
        return this.sectionKey;
    }

    
    public void setSectionKey(Comparable key) {
        this.sectionKey = key;
    }

    
    public String toString() {
        return "PieSection: " + this.pieIndex + ", " + this.sectionIndex + "("
                              + this.sectionKey.toString() + ")";
    }

}
