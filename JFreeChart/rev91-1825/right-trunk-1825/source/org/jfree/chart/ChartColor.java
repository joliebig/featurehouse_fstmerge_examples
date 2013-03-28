

package org.jfree.chart;

import java.awt.Color;
import java.awt.Paint;


public class ChartColor extends Color {

    
    public static final Color VERY_DARK_RED = new Color(0x80, 0x00, 0x00);

    
    public static final Color DARK_RED = new Color(0xc0, 0x00, 0x00);

    
    public static final Color LIGHT_RED = new Color(0xFF, 0x40, 0x40);

    
    public static final Color VERY_LIGHT_RED = new Color(0xFF, 0x80, 0x80);

    
    public static final Color VERY_DARK_YELLOW = new Color(0x80, 0x80, 0x00);

    
    public static final Color DARK_YELLOW = new Color(0xC0, 0xC0, 0x00);

    
    public static final Color LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x40);

    
    public static final Color VERY_LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x80);

    
    public static final Color VERY_DARK_GREEN = new Color(0x00, 0x80, 0x00);

    
    public static final Color DARK_GREEN = new Color(0x00, 0xC0, 0x00);

    
    public static final Color LIGHT_GREEN = new Color(0x40, 0xFF, 0x40);

    
    public static final Color VERY_LIGHT_GREEN = new Color(0x80, 0xFF, 0x80);

    
    public static final Color VERY_DARK_CYAN = new Color(0x00, 0x80, 0x80);

    
    public static final Color DARK_CYAN = new Color(0x00, 0xC0, 0xC0);

    
    public static final Color LIGHT_CYAN = new Color(0x40, 0xFF, 0xFF);

    
    public static final Color VERY_LIGHT_CYAN = new Color(0x80, 0xFF, 0xFF);

    
    public static final Color VERY_DARK_BLUE = new Color(0x00, 0x00, 0x80);

    
    public static final Color DARK_BLUE = new Color(0x00, 0x00, 0xC0);

    
    public static final Color LIGHT_BLUE = new Color(0x40, 0x40, 0xFF);

    
    public static final Color VERY_LIGHT_BLUE = new Color(0x80, 0x80, 0xFF);

    
    public static final Color VERY_DARK_MAGENTA = new Color(0x80, 0x00, 0x80);

    
    public static final Color DARK_MAGENTA = new Color(0xC0, 0x00, 0xC0);

    
    public static final Color LIGHT_MAGENTA = new Color(0xFF, 0x40, 0xFF);

    
    public static final Color VERY_LIGHT_MAGENTA = new Color(0xFF, 0x80, 0xFF);

    
    public ChartColor(int r, int g, int b) {
        super(r, g, b);
    }

    
    public static Paint[] createDefaultPaintArray() {

        return new Paint[] {
            new Color(0xFF, 0x55, 0x55),
            new Color(0x55, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0x55),
            new Color(0xFF, 0xFF, 0x55),
            new Color(0xFF, 0x55, 0xFF),
            new Color(0x55, 0xFF, 0xFF),
            Color.pink,
            Color.gray,
            ChartColor.DARK_RED,
            ChartColor.DARK_BLUE,
            ChartColor.DARK_GREEN,
            ChartColor.DARK_YELLOW,
            ChartColor.DARK_MAGENTA,
            ChartColor.DARK_CYAN,
            Color.darkGray,
            ChartColor.LIGHT_RED,
            ChartColor.LIGHT_BLUE,
            ChartColor.LIGHT_GREEN,
            ChartColor.LIGHT_YELLOW,
            ChartColor.LIGHT_MAGENTA,
            ChartColor.LIGHT_CYAN,
            Color.lightGray,
            ChartColor.VERY_DARK_RED,
            ChartColor.VERY_DARK_BLUE,
            ChartColor.VERY_DARK_GREEN,
            ChartColor.VERY_DARK_YELLOW,
            ChartColor.VERY_DARK_MAGENTA,
            ChartColor.VERY_DARK_CYAN,
            ChartColor.VERY_LIGHT_RED,
            ChartColor.VERY_LIGHT_BLUE,
            ChartColor.VERY_LIGHT_GREEN,
            ChartColor.VERY_LIGHT_YELLOW,
            ChartColor.VERY_LIGHT_MAGENTA,
            ChartColor.VERY_LIGHT_CYAN
        };
    }

}
