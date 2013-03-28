

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.data.category.CategoryDataset;


public class CategoryItemEntity extends ChartEntity
        implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -8657249457902337349L;

    
    private CategoryDataset dataset;

    
    private Comparable rowKey;

    
    private Comparable columnKey;

    
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
            CategoryDataset dataset, Comparable rowKey, Comparable columnKey) {
        super(area, toolTipText, urlText);
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
        this.rowKey = rowKey;
        this.columnKey = columnKey;
    }

    
    public CategoryDataset getDataset() {
        return this.dataset;
    }

    
    public void setDataset(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
    }

    
    public Comparable getRowKey() {
        return this.rowKey;
    }

    
    public void setRowKey(Comparable rowKey) {
        this.rowKey = rowKey;
    }

    
    public Comparable getColumnKey() {
        return this.columnKey;
    }

    
    public void setColumnKey(Comparable columnKey) {
        this.columnKey = columnKey;
    }

    
    public String toString() {
        return "CategoryItemEntity: rowKey=" + this.rowKey
               + ", columnKey=" + this.columnKey + ", dataset=" + this.dataset;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryItemEntity)) {
            return false;
        }
        CategoryItemEntity that = (CategoryItemEntity) obj;
        if (!this.rowKey.equals(that.rowKey)) {
            return false;
        }
        if (!this.columnKey.equals(that.columnKey)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.dataset, that.dataset)) {
            return false;
        }
        return super.equals(obj);
    }

}
