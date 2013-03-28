

package org.jfree.chart.plot;

import java.io.Serializable;

import org.jfree.chart.renderer.xy.XYBlockRenderer;



public class GreyPalette extends ColorPalette implements Serializable {

    
    private static final long serialVersionUID = -2120941170159987395L;
    
    
    public GreyPalette() {
        super();
        initialize();
    }

    
    public void initialize() {

        setPaletteName("Grey");

        this.r = new int[256];
        this.g = new int[256];
        this.b = new int[256];

        this.r[0] = 255;
        this.g[0] = 255;
        this.b[0] = 255;
        this.r[1] = 0;
        this.g[1] = 0;
        this.b[1] = 0;

        for (int i = 2; i < 256; i++) {
            this.r[i] = i;
            this.g[i] = i;
            this.b[i] = i;
        }

    }

}
