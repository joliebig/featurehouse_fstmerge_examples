

package org.jfree.chart.text;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.BreakIterator;

import org.jfree.chart.util.ObjectUtilities;


public class TextUtilities {

    
    private static boolean useDrawRotatedStringWorkaround;

    
    private static boolean useFontMetricsGetStringBounds;

    static {
        boolean isJava14 = ObjectUtilities.isJDK14();
        useDrawRotatedStringWorkaround = (isJava14 == false);
        useFontMetricsGetStringBounds = (isJava14 == true);
    }

    
    private TextUtilities() {
    }

    
    public static TextBlock createTextBlock(String text, Font font,
                                            Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        TextBlock result = new TextBlock();
        String input = text;
        boolean moreInputToProcess = (text.length() > 0);
        int start = 0;
        while (moreInputToProcess) {
            int index = input.indexOf("\n");
            if (index > start) {
                String line = input.substring(start, index);
                if (index < input.length() - 1) {
                    result.addLine(line, font, paint);
                    input = input.substring(index + 1);
                }
                else {
                    moreInputToProcess = false;
                }
            }
            else if (index == start) {
                if (index < input.length() - 1) {
                    input = input.substring(index + 1);
                }
                else {
                    moreInputToProcess = false;
                }
            }
            else {
                result.addLine(input, font, paint);
                moreInputToProcess = false;
            }
        }
        return result;
    }

    
    public static TextBlock createTextBlock(String text, Font font,
            Paint paint, float maxWidth, TextMeasurer measurer) {

        return createTextBlock(text, font, paint, maxWidth, Integer.MAX_VALUE,
                measurer);
    }

    
    public static TextBlock createTextBlock(String text, Font font,
            Paint paint, float maxWidth, int maxLines, TextMeasurer measurer) {

        TextBlock result = new TextBlock();
        BreakIterator iterator = BreakIterator.getLineInstance();
        iterator.setText(text);
        int current = 0;
        int lines = 0;
        int length = text.length();
        while (current < length && lines < maxLines) {
            int next = nextLineBreak(text, current, maxWidth, iterator,
                    measurer);
            if (next == BreakIterator.DONE) {
                result.addLine(text.substring(current), font, paint);
                return result;
            }
            result.addLine(text.substring(current, next), font, paint);
            lines++;
            current = next;
            while (current < text.length()&& text.charAt(current) == '\n') {
                current++;
            }
        }
        if (current < length) {
            TextLine lastLine = result.getLastLine();
            TextFragment lastFragment = lastLine.getLastTextFragment();
            String oldStr = lastFragment.getText();
            String newStr = "...";
            if (oldStr.length() > 3) {
                newStr = oldStr.substring(0, oldStr.length() - 3) + "...";
            }

            lastLine.removeFragment(lastFragment);
            TextFragment newFragment = new TextFragment(newStr,
                    lastFragment.getFont(), lastFragment.getPaint());
            lastLine.addFragment(newFragment);
        }
        return result;
    }

    
    private static int nextLineBreak(String text, int start,
            float width, BreakIterator iterator, TextMeasurer measurer) {

        
        
        int current = start;
        int end;
        float x = 0.0f;
        boolean firstWord = true;
        int newline = text.indexOf('\n', start);
        if (newline < 0) {
            newline = Integer.MAX_VALUE;
        }
        while (((end = iterator.next()) != BreakIterator.DONE)) {
            x += measurer.getStringWidth(text, current, end);
            if (x > width) {
                if (firstWord) {
                    while (measurer.getStringWidth(text, start, end) > width) {
                        end--;
                        if (end <= start) {
                            return end;
                        }
                    }
                    return end;
                }
                else {
                    end = iterator.previous();
                    return end;
                }
            }
            else {
                if (end > newline) {
                    return newline;
                }
            }
            
            firstWord = false;
            current = end;
        }
        return BreakIterator.DONE;
    }

    
    public static Rectangle2D getTextBounds(String text, Graphics2D g2,
            FontMetrics fm) {

        final Rectangle2D bounds;
        if (TextUtilities.useFontMetricsGetStringBounds) {
            bounds = fm.getStringBounds(text, g2);
            
            
            
            LineMetrics lm = fm.getFont().getLineMetrics(text,
                    g2.getFontRenderContext());
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(),
                    lm.getHeight());
        }
        else {
            double width = fm.stringWidth(text);
            double height = fm.getHeight();
            bounds = new Rectangle2D.Double(0.0, -fm.getAscent(), width,
                    height);
        }
        return bounds;
    }

    
    public static Rectangle2D drawAlignedString(String text,
            Graphics2D g2, float x, float y, TextAnchor anchor) {

        Rectangle2D textBounds = new Rectangle2D.Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor,
                textBounds);
        
        textBounds.setRect(x + adjust[0], y + adjust[1] + adjust[2],
            textBounds.getWidth(), textBounds.getHeight());
        g2.drawString(text, x + adjust[0], y + adjust[1]);
        return textBounds;
    }

    
    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2,
            String text, TextAnchor anchor, Rectangle2D textBounds) {

        float[] result = new float[3];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) -bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) -bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = -descent - leading + (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = halfAscent;

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = -metrics.getDescent() - metrics.getLeading();

        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }

    
    public static void setUseDrawRotatedStringWorkaround(boolean use) {
        useDrawRotatedStringWorkaround = use;
    }

    
    public static void drawRotatedString(String text, Graphics2D g2,
            double angle, float x, float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    
    public static void drawRotatedString(String text, Graphics2D g2,
            float textX, float textY, double angle,
            float rotateX, float rotateY) {

        if ((text == null) || (text.equals(""))) {
            return;
        }

        AffineTransform saved = g2.getTransform();

        
        AffineTransform rotate = AffineTransform.getRotateInstance(
                angle, rotateX, rotateY);
        g2.transform(rotate);

        if (useDrawRotatedStringWorkaround) {
            
            TextLayout tl = new TextLayout(text, g2.getFont(),
                    g2.getFontRenderContext());
            tl.draw(g2, textX, textY);
        }
        else {
            AttributedString as = new AttributedString(text,
                    g2.getFont().getAttributes());
        	g2.drawString(as.getIterator(), textX, textY);
        }
        g2.setTransform(saved);

    }

    
    public static void drawRotatedString(String text, Graphics2D g2, float x,
            float y, TextAnchor textAnchor, double angle, float rotationX,
            float rotationY) {

        if (text == null || text.equals("")) {
            return;
        }
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle,
                rotationX, rotationY);
    }

    
    public static void drawRotatedString(String text, Graphics2D g2,
            float x, float y, TextAnchor textAnchor,
            double angle, TextAnchor rotationAnchor) {

        if (text == null || text.equals("")) {
            return;
        }
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text,
                rotationAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1],
                angle, x + textAdj[0] + rotateAdj[0],
                y + textAdj[1] + rotateAdj[1]);

    }

    
    public static Shape calculateRotatedStringBounds(String text,
            Graphics2D g2, float x, float y,
            TextAnchor textAnchor, double angle,
            TextAnchor rotationAnchor) {

        if (text == null || text.equals("")) {
            return null;
        }
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text,
                rotationAnchor);
        Shape result = calculateRotatedStringBounds(text, g2,
                x + textAdj[0], y + textAdj[1], angle,
                x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
        return result;

    }

    
    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2,
            String text, TextAnchor anchor) {

        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) -bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) -bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = -descent - leading + (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = halfAscent;

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = -metrics.getDescent() - metrics.getLeading();

        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }

    
    private static float[] deriveRotationAnchorOffsets(Graphics2D g2,
            String text, TextAnchor anchor) {

        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics metrics = g2.getFont().getLineMetrics(text, frc);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.HALF_ASCENT_LEFT) {

            xAdj = 0.0f;

        }
        else if (anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.HALF_ASCENT_CENTER) {

            xAdj = (float) bounds.getWidth() / 2.0f;

        }
        else if (anchor == TextAnchor.TOP_RIGHT
                || anchor == TextAnchor.CENTER_RIGHT
                || anchor == TextAnchor.BOTTOM_RIGHT
                || anchor == TextAnchor.BASELINE_RIGHT
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            xAdj = (float) bounds.getWidth();

        }

        if (anchor == TextAnchor.TOP_LEFT
                || anchor == TextAnchor.TOP_CENTER
                || anchor == TextAnchor.TOP_RIGHT) {

            yAdj = descent + leading - (float) bounds.getHeight();

        }
        else if (anchor == TextAnchor.CENTER_LEFT
                || anchor == TextAnchor.CENTER
                || anchor == TextAnchor.CENTER_RIGHT) {

            yAdj = descent + leading - (float) (bounds.getHeight() / 2.0);

        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT
                || anchor == TextAnchor.HALF_ASCENT_CENTER
                || anchor == TextAnchor.HALF_ASCENT_RIGHT) {

            yAdj = -halfAscent;

        }
        else if (anchor == TextAnchor.BASELINE_LEFT
                || anchor == TextAnchor.BASELINE_CENTER
                || anchor == TextAnchor.BASELINE_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextAnchor.BOTTOM_LEFT
                || anchor == TextAnchor.BOTTOM_CENTER
                || anchor == TextAnchor.BOTTOM_RIGHT) {

            yAdj = metrics.getDescent() + metrics.getLeading();

        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;

    }

    
    public static Shape calculateRotatedStringBounds(String text,
            Graphics2D g2, float textX, float textY,
            double angle, float rotateX, float rotateY) {

        if ((text == null) || (text.equals(""))) {
            return null;
        }
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        AffineTransform translate = AffineTransform.getTranslateInstance(
                textX, textY);
        Shape translatedBounds = translate.createTransformedShape(bounds);
        AffineTransform rotate = AffineTransform.getRotateInstance(
                angle, rotateX, rotateY);
        Shape result = rotate.createTransformedShape(translatedBounds);
        return result;

    }

    
    public static boolean getUseFontMetricsGetStringBounds() {
        return useFontMetricsGetStringBounds;
    }

    
    public static void setUseFontMetricsGetStringBounds(final boolean use) {
        useFontMetricsGetStringBounds = use;
    }

    
    public static boolean isUseDrawRotatedStringWorkaround() {
        return useDrawRotatedStringWorkaround;
    }
}