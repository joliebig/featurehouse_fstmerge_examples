

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ObjectUtilities;


public class CategoryItemEntity extends ChartEntity 
                                implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -8657249457902337349L;
    
    
    private CategoryDataset dataset;
    
    
    private int series;
    
    
    private Object category;

    
    private int categoryIndex;

    
    private Comparable rowKey;
    
    
    private Comparable columnKey;

    
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
                              CategoryDataset dataset,
                              int series, Object category, int categoryIndex) {

        super(area, toolTipText, urlText);
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;
        this.rowKey = dataset.getRowKey(series);
        this.columnKey = dataset.getColumnKey(categoryIndex);
    }
    
    
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
            CategoryDataset dataset, Comparable rowKey, Comparable columnKey) {
        super(area, toolTipText, urlText);
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        this.dataset = dataset;
        this.rowKey = rowKey;
        this.columnKey = columnKey;
        
        
        this.series = dataset.getRowIndex(rowKey);
        this.category = columnKey;
        this.categoryIndex = dataset.getColumnIndex(columnKey);
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
        
        this.series = this.dataset.getRowIndex(rowKey);
    }

    
    public Comparable getColumnKey() {
        return this.columnKey;
    }
    
    
    public void setColumnKey(Comparable columnKey) {
        this.columnKey = columnKey;
        
        this.category = columnKey;
        this.categoryIndex = this.dataset.getColumnIndex(columnKey);
    }

    
    public int getSeries() {
        return this.series;
    }

    
    public void setSeries(int series) {
        this.series = series;
    }

    
    public Object getCategory() {
        return this.category;
    }

    
    public void setCategory(Object category) {
        this.category = category;
    }

    
    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    
    public void setCategoryIndex(int index) {
        this.categoryIndex = index;
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
        
        
        if (this.categoryIndex != that.categoryIndex) {
            return false;   
        }
        if (this.series != that.series) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.category, that.category)) {
            return false;   
        }
        return super.equals(obj);
    }

}
