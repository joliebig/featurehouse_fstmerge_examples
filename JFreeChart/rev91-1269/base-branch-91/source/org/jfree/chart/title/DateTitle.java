

package org.jfree.chart.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;


public class DateTitle extends TextTitle implements Serializable {

    
    private static final long serialVersionUID = -465434812763159881L;
    
    
    public DateTitle() {
        this(DateFormat.LONG);
    }

    
    public DateTitle(int style) {
        this(style, Locale.getDefault(), new Font("Dialog", Font.PLAIN, 12), 
                Color.black);
    }

    
    public DateTitle(int style, Locale locale, Font font, Paint paint) {
        this(style, locale, font, paint, RectangleEdge.BOTTOM,
                HorizontalAlignment.RIGHT, VerticalAlignment.CENTER,
                Title.DEFAULT_PADDING);
    }

    
    public DateTitle(int style, Locale locale, Font font, Paint paint,
                     RectangleEdge position, 
                     HorizontalAlignment horizontalAlignment, 
                     VerticalAlignment verticalAlignment,
                     RectangleInsets padding) {
        super(DateFormat.getDateInstance(style, locale).format(new Date()),
                font, paint, position, horizontalAlignment, verticalAlignment,
                padding);
    }

    
    public void setDateFormat(int style, Locale locale) {
        setText(DateFormat.getDateInstance(style, locale).format(new Date()));
    }

}
