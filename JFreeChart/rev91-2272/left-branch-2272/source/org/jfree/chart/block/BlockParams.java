

package org.jfree.chart.block;


public class BlockParams implements EntityBlockParams {

    
    private boolean generateEntities;

    
    private double translateX;

    
    private double translateY;

    
    public BlockParams() {
        this.translateX = 0.0;
        this.translateY = 0.0;
        this.generateEntities = false;
    }

    
    public boolean getGenerateEntities() {
        return this.generateEntities;
    }

    
    public void setGenerateEntities(boolean generate) {
        this.generateEntities = generate;
    }

    
    public double getTranslateX() {
        return this.translateX;
    }

    
    public void setTranslateX(double x) {
        this.translateX = x;
    }

    
    public double getTranslateY() {
        return this.translateY;
    }

    
    public void setTranslateY(double y) {
        this.translateY = y;
    }

}
