

package org.jfree.chart.util;

import java.awt.geom.Rectangle2D;


public final class Align {

    
    public static final int CENTER = 0x00;

    
    public static final int TOP = 0x01;

    
    public static final int BOTTOM = 0x02;

    
    public static final int LEFT = 0x04;

    
    public static final int RIGHT = 0x08;

    
    public static final int TOP_LEFT = TOP | LEFT;

    
    public static final int TOP_RIGHT = TOP | RIGHT;

    
    public static final int BOTTOM_LEFT = BOTTOM | LEFT;

    
    public static final int BOTTOM_RIGHT = BOTTOM | RIGHT;

    
    public static final int FIT_HORIZONTAL = LEFT | RIGHT;

    
    public static final int FIT_VERTICAL = TOP | BOTTOM;

    
    public static final int FIT = FIT_HORIZONTAL | FIT_VERTICAL;

    
    public static final int NORTH = TOP;

    
    public static final int SOUTH = BOTTOM;

    
    public static final int WEST = LEFT;

    
    public static final int EAST = RIGHT;

    
    public static final int NORTH_WEST = NORTH | WEST;

    
    public static final int NORTH_EAST = NORTH | EAST;

    
    public static final int SOUTH_WEST = SOUTH | WEST;

    
    public static final int SOUTH_EAST = SOUTH | EAST;

    
    private Align() {
        super();
    }

    
    public static void align(Rectangle2D rect, Rectangle2D frame, int align) {

        double x = frame.getCenterX() - rect.getWidth() / 2.0;
        double y = frame.getCenterY() - rect.getHeight() / 2.0;
        double w = rect.getWidth();
        double h = rect.getHeight();

        if ((align & FIT_VERTICAL) == FIT_VERTICAL) {
            h = frame.getHeight();
        }

        if ((align & FIT_HORIZONTAL) == FIT_HORIZONTAL) {
            w = frame.getWidth();
        }

        if ((align & TOP) == TOP) {
            y = frame.getMinY();
        }

        if ((align & BOTTOM) == BOTTOM) {
            y = frame.getMaxY() - h;
        }

        if ((align & LEFT) == LEFT) {
            x = frame.getX();
        }

        if ((align & RIGHT) == RIGHT) {
            x = frame.getMaxX() - w;
        }

        rect.setRect(x, y, w, h);

    }

}
