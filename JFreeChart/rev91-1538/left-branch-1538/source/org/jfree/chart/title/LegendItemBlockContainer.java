

package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.data.general.Dataset;


public class LegendItemBlockContainer extends BlockContainer {

    
    private Dataset dataset;

    
    private Comparable seriesKey;

    
    private int datasetIndex;

    
    private int series;

    
    private String toolTipText;

    
    private String urlText;

    
    public LegendItemBlockContainer(Arrangement arrangement, int datasetIndex,
            int series) {
        super(arrangement);
        this.datasetIndex = datasetIndex;
        this.series = series;
    }

    
    public LegendItemBlockContainer(Arrangement arrangement, Dataset dataset,
            Comparable seriesKey) {
        super(arrangement);
        this.dataset = dataset;
        this.seriesKey = seriesKey;
    }

    
    public Dataset getDataset() {
        return this.dataset;
    }

    
    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    
    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    
    public int getSeriesIndex() {
        return this.series;
    }

    
    public String getToolTipText() {
        return this.toolTipText;
    }

    
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    
    public String getURLText() {
        return this.urlText;
    }

    
    public void setURLText(String text) {
        this.urlText = text;
    }

    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        
        super.draw(g2, area, null);
        EntityBlockParams ebp = null;
        BlockResult r = new BlockResult();
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                EntityCollection ec = new StandardEntityCollection();
                LegendItemEntity entity = new LegendItemEntity(
                        (Shape) area.clone());
                entity.setSeriesIndex(this.series);
                entity.setSeriesKey(this.seriesKey);
                entity.setDataset(this.dataset);
                entity.setToolTipText(getToolTipText());
                entity.setURLText(getURLText());
                ec.add(entity);
                r.setEntityCollection(ec);
            }
        }
        return r;
    }
}
