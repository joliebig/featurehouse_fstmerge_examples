

package com.lowagie.text.pdf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.lowagie.text.pdf.internal.PolylineShape;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class PdfGraphics2D extends Graphics2D {
    
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private BasicStroke strokeOne = new BasicStroke(1);
    
    private static final AffineTransform IDENTITY = new AffineTransform();
    
    private Font font;
    private BaseFont baseFont;
    private float fontSize;
    private AffineTransform transform;
    private Paint paint;
    private Color background;
    private float width;
    private float height;
    
    private Area clip;
    
    private RenderingHints rhints = new RenderingHints(null);
    
    private Stroke stroke;
    private Stroke originalStroke;
    
    private PdfContentByte cb;
    
    
    private HashMap<String, BaseFont> baseFonts;
    
    private boolean disposeCalled = false;
    
    private FontMapper fontMapper;
    
    private static final class Kid {
        final int pos;
        final PdfGraphics2D graphics;
        Kid(int pos, PdfGraphics2D graphics) {
            this.pos = pos;
            this.graphics = graphics;
        }
    }
    private ArrayList<Kid> kids;
    
    private boolean kid = false;
    
    private Graphics2D dg2 = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB).createGraphics();
    
    private boolean onlyShapes = false;
    
    private Stroke oldStroke;
    private Paint paintFill;
    private Paint paintStroke;
    
    private MediaTracker mediaTracker;

    
    protected boolean underline;          
      
    protected PdfGState fillGState[] = new PdfGState[256];
    protected PdfGState strokeGState[] = new PdfGState[256];
    protected int currentFillGState = 255;
    protected int currentStrokeGState = 255;
    
    public static final int AFM_DIVISOR = 1000; 

    private boolean convertImagesToJPEG = false;
    private float jpegQuality = .95f;

    
    private float alpha;

    
    private Composite composite;

    
    private Paint realPaint;

    private PdfGraphics2D() {
        dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    }
    
    
    PdfGraphics2D(PdfContentByte cb, float width, float height, FontMapper fontMapper, boolean onlyShapes, boolean convertImagesToJPEG, float quality) {
        super();
        dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        this.convertImagesToJPEG = convertImagesToJPEG;
        this.jpegQuality = quality;
        this.onlyShapes = onlyShapes;
        this.transform = new AffineTransform();
        this.baseFonts = new HashMap<String, BaseFont>();
        if (!onlyShapes) {
            this.fontMapper = fontMapper;
            if (this.fontMapper == null)
                this.fontMapper = new DefaultFontMapper();
        }
        paint = Color.black;
        background = Color.white;
        setFont(new Font("sanserif", Font.PLAIN, 12));
        this.cb = cb;
        cb.saveState();
        this.width = width;
        this.height = height;
        clip = new Area(new Rectangle2D.Float(0, 0, width, height));
        clip(clip);
        originalStroke = stroke = oldStroke = strokeOne;
        setStrokeDiff(stroke, null);
        cb.saveState();
    }
    
    
    public void draw(Shape s) {
        followPath(s, STROKE);
    }
    
    
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return drawImage(img, null, xform, null, obs);
    }
    
    
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage result = img;
        if (op != null) {
            result = op.createCompatibleDestImage(img, img.getColorModel());
            result = op.filter(img, result);
        }
        drawImage(result, x, y, null);
    }
    
    
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        BufferedImage image = null;
        if (img instanceof BufferedImage) {
            image = (BufferedImage)img;
        } else {
            ColorModel cm = img.getColorModel();
            int width = img.getWidth();
            int height = img.getHeight();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            Hashtable<String, Object> properties = new Hashtable<String, Object>();
            String[] keys = img.getPropertyNames();
            if (keys!=null) {
                for (int i = 0; i < keys.length; i++) {
                    properties.put(keys[i], img.getProperty(keys[i]));
                }
            }
            BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
            img.copyData(raster);
            image=result;
        }
        drawImage(image, xform, null);
    }
    
    
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        drawRenderedImage(img.createDefaultRendering(), xform);
    }
    
    
    public void drawString(String s, int x, int y) {
        drawString(s, (float)x, (float)y);
    }
    
    
    public static double asPoints(double d, int i) {
        return (d * i) / AFM_DIVISOR;
    }
    
    protected void doAttributes(AttributedCharacterIterator iter) {
        underline = false;
        Set<AttributedCharacterIterator.Attribute> set = iter.getAttributes().keySet();
        for(AttributedCharacterIterator.Attribute attribute: set) {
            if (!(attribute instanceof TextAttribute))
                continue;
            TextAttribute textattribute = (TextAttribute)attribute;
            if(textattribute.equals(TextAttribute.FONT)) {
                Font font = (Font)iter.getAttributes().get(textattribute);
                setFont(font);
            }
            else if(textattribute.equals(TextAttribute.UNDERLINE)) {
                if(iter.getAttributes().get(textattribute) == TextAttribute.UNDERLINE_ON)
                    underline = true;
            }
            else if(textattribute.equals(TextAttribute.SIZE)) {
                Object obj = iter.getAttributes().get(textattribute);
                if(obj instanceof Integer) {
                    int i = ((Integer)obj).intValue();
                    setFont(getFont().deriveFont(getFont().getStyle(), i));
                }
                else if(obj instanceof Float) {
                    float f = ((Float)obj).floatValue();
                    setFont(getFont().deriveFont(getFont().getStyle(), f));
                }
            }
            else if(textattribute.equals(TextAttribute.FOREGROUND)) {
                setColor((Color) iter.getAttributes().get(textattribute));
            }
            else if(textattribute.equals(TextAttribute.FAMILY)) {
              Font font = getFont();
              Map fontAttributes = font.getAttributes();
              fontAttributes.put(TextAttribute.FAMILY, iter.getAttributes().get(textattribute));
              setFont(font.deriveFont(fontAttributes));
            }
            else if(textattribute.equals(TextAttribute.POSTURE)) {
              Font font = getFont();
              Map fontAttributes = font.getAttributes();
              fontAttributes.put(TextAttribute.POSTURE, iter.getAttributes().get(textattribute));
              setFont(font.deriveFont(fontAttributes)); 
            }
            else if(textattribute.equals(TextAttribute.WEIGHT)) {
              Font font = getFont();
              Map fontAttributes = font.getAttributes();
              fontAttributes.put(TextAttribute.WEIGHT, iter.getAttributes().get(textattribute));
              setFont(font.deriveFont(fontAttributes)); 
            }
        }
    }

    
    public void drawString(String s, float x, float y) {
        if (s.length() == 0)
            return;
        setFillPaint();
        if (onlyShapes) {
            drawGlyphVector(this.font.layoutGlyphVector(getFontRenderContext(), s.toCharArray(), 0, s.length(), java.awt.Font.LAYOUT_LEFT_TO_RIGHT), x, y);


        }
        else {
            boolean restoreTextRenderingMode = false;
            AffineTransform at = getTransform();
            AffineTransform at2 = getTransform();
            at2.translate(x, y);
            at2.concatenate(font.getTransform());
            setTransform(at2);
            AffineTransform inverse = this.normalizeMatrix();
            AffineTransform flipper = AffineTransform.getScaleInstance(1,-1);
            inverse.concatenate(flipper);
            double[] mx = new double[6];
            inverse.getMatrix(mx);
            cb.beginText();
            cb.setFontAndSize(baseFont, fontSize);
            
            
            
            
            
            
            if (font.isItalic() && font.getFontName().equals(font.getName())) {
                float angle = baseFont.getFontDescriptor(BaseFont.ITALICANGLE, 1000);
                float angle2 = font.getItalicAngle();
                
                
                if (angle2 == 0) {
                    
                    
                    
                    angle2 = 15.0f;
                } else {
                    
                    
                    angle2 = -angle2;
                }
                if (angle == 0) {
                    mx[2] = angle2 / 100.0f;
                }
            } 
            cb.setTextMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
            Float fontTextAttributeWidth = (Float)font.getAttributes().get(TextAttribute.WIDTH);
            fontTextAttributeWidth = (fontTextAttributeWidth == null)
                                     ? TextAttribute.WIDTH_REGULAR
                                     : fontTextAttributeWidth;
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth))
                cb.setHorizontalScaling(100.0f / fontTextAttributeWidth.floatValue());
            
            
            
            if (baseFont.getPostscriptFontName().toLowerCase().indexOf("bold") < 0) { 
                
                
                Float weight = (Float) font.getAttributes().get(TextAttribute.WEIGHT);
                if (weight == null) {
                    weight = (font.isBold()) ? TextAttribute.WEIGHT_BOLD
                                             : TextAttribute.WEIGHT_REGULAR;
                }
                if ((font.isBold() || (weight.floatValue() >= TextAttribute.WEIGHT_SEMIBOLD.floatValue()))
                    && (font.getFontName().equals(font.getName()))) {
                    
                    float strokeWidth = font.getSize2D() * (weight.floatValue() - TextAttribute.WEIGHT_REGULAR.floatValue()) / 30f;
                    if (strokeWidth != 1) {
                        cb.setTextRenderingMode(PdfContentByte.
                            TEXT_RENDER_MODE_FILL_STROKE);
                        cb.setLineWidth(strokeWidth);
                        cb.setColorStroke(getColor());
                        restoreTextRenderingMode = true;
                    }
                }
            }

            double width = 0;
            if (font.getSize2D() > 0) {
                float scale = 1000 / font.getSize2D();
                width = font.deriveFont(AffineTransform.getScaleInstance(scale, scale)).getStringBounds(s, getFontRenderContext()).getWidth() / scale;
            }
            
            Object url = getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF))
            {
                float scale = 1000 / font.getSize2D();
                double height = font.deriveFont(AffineTransform.getScaleInstance(scale, scale)).getStringBounds(s, getFontRenderContext()).getHeight() / scale;
                double leftX = cb.getXTLM();
                double leftY = cb.getYTLM();
                PdfAction action = new  PdfAction(url.toString());
                cb.setAction(action, (float)leftX, (float)leftY, (float)(leftX+width), (float)(leftY+height));
            }
            if (s.length() > 1) {
                float adv = ((float)width - baseFont.getWidthPoint(s, fontSize)) / (s.length() - 1);
                cb.setCharacterSpacing(adv);
            }
            cb.showText(s);
            if (s.length() > 1) {
                cb.setCharacterSpacing(0);
            }
            if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth))
                cb.setHorizontalScaling(100);
                
            
            if (restoreTextRenderingMode) {
                cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
            } 

            cb.endText();
            setTransform(at);
            if(underline)
            {
                
                
                int UnderlineThickness = 50;
                
                double d = asPoints(UnderlineThickness, (int)fontSize);
                setStroke(new BasicStroke((float)d));
                y = (float)(y + asPoints(UnderlineThickness, (int)fontSize));
                Line2D line = new Line2D.Double(x, y, width+x, y);
                draw(line);
            }
        }
    }

    
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float)x, (float)y);
    }
    
    
    public void drawString(AttributedCharacterIterator iter, float x, float y) {

        StringBuffer stringbuffer = new StringBuffer(iter.getEndIndex());
        for(char c = iter.first(); c != '\u'; c = iter.next())
        {
            if(iter.getIndex() == iter.getRunStart())
            {
                if(stringbuffer.length() > 0)
                {
                    drawString(stringbuffer.toString(), x, y);
                    FontMetrics fontmetrics = getFontMetrics();
                    x = (float)(x + fontmetrics.getStringBounds(stringbuffer.toString(), this).getWidth());
                    stringbuffer.delete(0, stringbuffer.length());
                }
                doAttributes(iter);
            }
            stringbuffer.append(c);
        }
        
        drawString(stringbuffer.toString(), x, y);
        underline = false;
    }
    
    
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape s = g.getOutline(x, y);
        fill(s);
    }
    
    
    public void fill(Shape s) {
        followPath(s, FILL);
    }
    
    
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke) {
            s = stroke.createStrokedShape(s);
        }
        s = transform.createTransformedShape(s);
        Area area = new Area(s);
        if (clip != null)
            area.intersect(clip);
        return area.intersects(rect.x, rect.y, rect.width, rect.height);
    }
    
    
    public GraphicsConfiguration getDeviceConfiguration() {
        return dg2.getDeviceConfiguration();
    }
    
    
    public void setComposite(Composite comp) {
        
        if (comp instanceof AlphaComposite) {

            AlphaComposite composite = (AlphaComposite) comp;

            if (composite.getRule() == 3) {

                alpha = composite.getAlpha();
                this.composite = composite;

                if (realPaint != null && (realPaint instanceof Color)) {

                    Color c = (Color) realPaint;
                    paint = new Color(c.getRed(), c.getGreen(), c.getBlue(),
                            (int) (c.getAlpha() * alpha));
                }
                return;
            }
        }

        this.composite = comp;
        alpha = 1.0F;

    }
    
    
    public void setPaint(Paint paint) {
        if (paint == null)
            return;
        this.paint = paint;
        realPaint = paint;

        if ((composite instanceof AlphaComposite) && (paint instanceof Color)) {
            
            AlphaComposite co = (AlphaComposite) composite;
            
            if (co.getRule() == 3) {
                Color c = (Color) paint;
                this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * alpha));
                realPaint = paint;
            }
        }

    }

    private Stroke transformStroke(Stroke stroke) {
        if (!(stroke instanceof BasicStroke))
            return stroke;
        BasicStroke st = (BasicStroke)stroke;
        float scale = (float)Math.sqrt(Math.abs(transform.getDeterminant()));
        float dash[] = st.getDashArray();
        if (dash != null) {
            for (int k = 0; k < dash.length; ++k)
                dash[k] *= scale;
        }
        return new BasicStroke(st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st.getDashPhase() * scale);
    }
    
    private void setStrokeDiff(Stroke newStroke, Stroke oldStroke) {
        if (newStroke == oldStroke)
            return;
        if (!(newStroke instanceof BasicStroke))
            return;
        BasicStroke nStroke = (BasicStroke)newStroke;
        boolean oldOk = (oldStroke instanceof BasicStroke);
        BasicStroke oStroke = null;
        if (oldOk)
            oStroke = (BasicStroke)oldStroke;
        if (!oldOk || nStroke.getLineWidth() != oStroke.getLineWidth())
            cb.setLineWidth(nStroke.getLineWidth());
        if (!oldOk || nStroke.getEndCap() != oStroke.getEndCap()) {
            switch (nStroke.getEndCap()) {
            case BasicStroke.CAP_BUTT:
                cb.setLineCap(0);
                break;
            case BasicStroke.CAP_SQUARE:
                cb.setLineCap(2);
                break;
            default:
                cb.setLineCap(1);
            }
        }
        if (!oldOk || nStroke.getLineJoin() != oStroke.getLineJoin()) {
            switch (nStroke.getLineJoin()) {
            case BasicStroke.JOIN_MITER:
                cb.setLineJoin(0);
                break;
            case BasicStroke.JOIN_BEVEL:
                cb.setLineJoin(2);
                break;
            default:
                cb.setLineJoin(1);
            }
        }
        if (!oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit())
            cb.setMiterLimit(nStroke.getMiterLimit());
        boolean makeDash;
        if (oldOk) {
            if (nStroke.getDashArray() != null) {
                if (nStroke.getDashPhase() != oStroke.getDashPhase()) {
                    makeDash = true;
                }
                else if (!java.util.Arrays.equals(nStroke.getDashArray(), oStroke.getDashArray())) {
                    makeDash = true;
                }
                else
                    makeDash = false;
            }
            else if (oStroke.getDashArray() != null) {
                makeDash = true;
            }
            else
                makeDash = false;
        }
        else {
            makeDash = true;
        }
        if (makeDash) {
            float dash[] = nStroke.getDashArray();
            if (dash == null)
                cb.setLiteral("[]0 d\n");
            else {
                cb.setLiteral('[');
                int lim = dash.length;
                for (int k = 0; k < lim; ++k) {
                    cb.setLiteral(dash[k]);
                    cb.setLiteral(' ');
                }
                cb.setLiteral(']');
                cb.setLiteral(nStroke.getDashPhase());
                cb.setLiteral(" d\n");
            }
        }
    }
    
    
    public void setStroke(Stroke s) {
        originalStroke = s;
        this.stroke = transformStroke(s);
    }
    
    
    
    public void setRenderingHint(Key arg0, Object arg1) {
         if (arg1 != null) {
             rhints.put(arg0, arg1);
         } else {
             if (arg0 instanceof HyperLinkKey)
             {
                 rhints.put(arg0, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
             }
             else
             {
                 rhints.remove(arg0);
             }
         }
    }
    
    
    public Object getRenderingHint(Key arg0) {
        return rhints.get(arg0);
    }
    
    
    public void setRenderingHints(Map<?,?> hints) {
        rhints.clear();
        rhints.putAll(hints);
    }
    
    
    public void addRenderingHints(Map<?,?> hints) {
        rhints.putAll(hints);
    }
    
    
    public RenderingHints getRenderingHints() {
        return rhints;
    }
    
    
    public void translate(int x, int y) {
        translate((double)x, (double)y);
    }
    
    
    public void translate(double tx, double ty) {
        transform.translate(tx,ty);
    }
    
    
    public void rotate(double theta) {
        transform.rotate(theta);
    }
    
    
    public void rotate(double theta, double x, double y) {
        transform.rotate(theta, x, y);
    }
    
    
    public void scale(double sx, double sy) {
        transform.scale(sx, sy);
        this.stroke = transformStroke(originalStroke);
    }
    
    
    public void shear(double shx, double shy) {
        transform.shear(shx, shy);
    }
    
    
    public void transform(AffineTransform tx) {
        transform.concatenate(tx);
        this.stroke = transformStroke(originalStroke);
    }
    
    
    public void setTransform(AffineTransform t) {
        transform = new AffineTransform(t);
        this.stroke = transformStroke(originalStroke);
    }
    
    
    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }
    
    
    public Paint getPaint() {
        if (realPaint != null) {
            return realPaint;
        } else {
            return paint;
        }
    }
    
    
    public Composite getComposite() {
        return composite;
    }
    
    
    public void setBackground(Color color) {
        background = color;
    }
    
    
    public Color getBackground() {
        return background;
    }
    
    
    public Stroke getStroke() {
        return originalStroke;
    }
    
    
    
    public FontRenderContext getFontRenderContext() {
        boolean antialias = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        boolean fractions = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), antialias, fractions);
    }
    
    
    public Graphics create() {
        PdfGraphics2D g2 = new PdfGraphics2D();
        g2.onlyShapes = this.onlyShapes;
        g2.transform = new AffineTransform(this.transform);
        g2.baseFonts = this.baseFonts;
        g2.fontMapper = this.fontMapper;
        g2.paint = this.paint;
        g2.fillGState = this.fillGState;
        g2.strokeGState = this.strokeGState;
        g2.background = this.background;
        g2.mediaTracker = this.mediaTracker;
        g2.convertImagesToJPEG = this.convertImagesToJPEG;
        g2.jpegQuality = this.jpegQuality;
        g2.setFont(this.font);
        g2.cb = this.cb.getDuplicate();
        g2.cb.saveState();
        g2.width = this.width;
        g2.height = this.height;
        g2.followPath(new Area(new Rectangle2D.Float(0, 0, width, height)), CLIP);
        if (this.clip != null)
            g2.clip = new Area(this.clip);
        g2.composite = composite;
        g2.stroke = stroke;
        g2.originalStroke = originalStroke;
        g2.strokeOne = (BasicStroke)g2.transformStroke(g2.strokeOne);
        g2.oldStroke = g2.strokeOne;
        g2.setStrokeDiff(g2.oldStroke, null);
        g2.cb.saveState();
        if (g2.clip != null)
            g2.followPath(g2.clip, CLIP);
        g2.kid = true;
        if (this.kids == null)
            this.kids = new ArrayList<Kid>();
        this.kids.add(new Kid(cb.getInternalBuffer().size(), g2));
        return g2;
    }
    
    public PdfContentByte getContent() {
        return this.cb;
    }
    
    public Color getColor() {
        if (paint instanceof Color) {
            return (Color)paint;
        } else {
            return Color.black;
        }
    }
    
    
    public void setColor(Color color) {
        setPaint(color);
    }
    
    
    public void setPaintMode() {}
    
    
    public void setXORMode(Color c1) {
        
    }
    
    
    public Font getFont() {
        return font;
    }
    
    
    
    public void setFont(Font f) {
        if (f == null)
            return;
        if (onlyShapes) {
            font = f;
            return;
        }
        if (f == font)
            return;
        font = f;
        fontSize = f.getSize2D();
        baseFont = getCachedBaseFont(f);
    }
    
    private BaseFont getCachedBaseFont(Font f) {
        synchronized (baseFonts) {
            BaseFont bf = baseFonts.get(f.getFontName());
            if (bf == null) {
                bf = fontMapper.awtToPdf(f);
                baseFonts.put(f.getFontName(), bf);
            }
            return bf;
        }
    }
    
    
    public FontMetrics getFontMetrics(Font f) {
        return dg2.getFontMetrics(f);
    }
    
    
    public Rectangle getClipBounds() {
        if (clip == null)
            return null;
        return getClip().getBounds();
    }
    
    
    public void clipRect(int x, int y, int width, int height) {
        Rectangle2D rect = new Rectangle2D.Double(x,y,width,height);
        clip(rect);
    }
    
    
    public void setClip(int x, int y, int width, int height) {
        Rectangle2D rect = new Rectangle2D.Double(x,y,width,height);
        setClip(rect);
    }
    
    
    public void clip(Shape s) {
        if (s == null) {
            setClip(null);
            return;
        }
        s = transform.createTransformedShape(s);
        if (clip == null)
            clip = new Area(s);
        else
            clip.intersect(new Area(s));
        followPath(s, CLIP);
    }
    
    
    public Shape getClip() {
        try {
            return transform.createInverse().createTransformedShape(clip);
        }
        catch (NoninvertibleTransformException e) {
            return null;
        }
    }
    
    
    public void setClip(Shape s) {
        cb.restoreState();
        cb.saveState();
        if (s != null)
            s = transform.createTransformedShape(s);
        if (s == null) {
            clip = null;
        }
        else {
            clip = new Area(s);
            followPath(s, CLIP);
        }
        paintFill = paintStroke = null;
        currentFillGState = currentStrokeGState = 255;
        oldStroke = strokeOne;
    }
    
    
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        
    }
    
    
    public void drawLine(int x1, int y1, int x2, int y2) {
        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        draw(line);
    }
    
    
    public void drawRect(int x, int y, int width, int height) {
        draw(new Rectangle(x, y, width, height));
    }
    
    
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle(x,y,width,height));
    }
    
    
    public void clearRect(int x, int y, int width, int height) {
        Paint temp = paint;
        setPaint(background);
        fillRect(x,y,width,height);
        setPaint(temp);
    }
    
    
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(x,y,width,height,arcWidth, arcHeight);
        draw(rect);
    }
    
    
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(x,y,width,height,arcWidth, arcHeight);
        fill(rect);
    }
    
    
    public void drawOval(int x, int y, int width, int height) {
        Ellipse2D oval = new Ellipse2D.Float(x, y, width, height);
        draw(oval);
    }
    
    
    public void fillOval(int x, int y, int width, int height) {
        Ellipse2D oval = new Ellipse2D.Float(x, y, width, height);
        fill(oval);
    }
    
    
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D arc = new Arc2D.Double(x,y,width,height,startAngle, arcAngle, Arc2D.OPEN);
        draw(arc);

    }
    
    
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D arc = new Arc2D.Double(x,y,width,height,startAngle, arcAngle, Arc2D.PIE);
        fill(arc);
    }
    
    
    public void drawPolyline(int[] x, int[] y, int nPoints) {
        PolylineShape polyline = new PolylineShape(x, y, nPoints);
        draw(polyline);
    }
    
    
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon(xPoints, yPoints, nPoints);
        draw(poly);
    }
    
    
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon();
        for (int i = 0; i < nPoints; i++) {
            poly.addPoint(xPoints[i], yPoints[i]);
        }
        fill(poly);
    }
    
    
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, null, observer);
    }
    
    
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }
    
    
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }
    
    
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        double scalex = width/(double)img.getWidth(observer);
        double scaley = height/(double)img.getHeight(observer);
        AffineTransform tx = AffineTransform.getTranslateInstance(x,y);
        tx.scale(scalex,scaley);
        return drawImage(img, null, tx, bgcolor, observer);
    }
    
    
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }
    
    
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        double dwidth = (double)dx2-dx1;
        double dheight = (double)dy2-dy1;
        double swidth = (double)sx2-sx1;
        double sheight = (double)sy2-sy1;
        
        
        if (dwidth == 0 || dheight == 0 || swidth == 0 || sheight == 0) return true;
        
        double scalex = dwidth/swidth;
        double scaley = dheight/sheight;
        
        double transx = sx1*scalex;
        double transy = sy1*scaley;
        AffineTransform tx = AffineTransform.getTranslateInstance(dx1-transx,dy1-transy);
        tx.scale(scalex,scaley);
        
        BufferedImage mask = new BufferedImage(img.getWidth(observer), img.getHeight(observer), BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = mask.getGraphics();
        g.fillRect(sx1,sy1, (int)swidth, (int)sheight);
        drawImage(img, mask, tx, null, observer);
        g.dispose();
        return true;
    }
    
    
    public void dispose() {
        if (kid)
            return;
        if (!disposeCalled) {
            disposeCalled = true;
            cb.restoreState();
            cb.restoreState();
            dg2.dispose();
            dg2 = null;
            if (kids != null) {
                ByteBuffer buf = new ByteBuffer();
                internalDispose(buf);
                ByteBuffer buf2 = cb.getInternalBuffer();
                buf2.reset();
                buf2.append(buf);
            }
        }
    }
    
    private void internalDispose(ByteBuffer buf) {
        int last = 0;
        int pos = 0;
        ByteBuffer buf2 = cb.getInternalBuffer();
        if (kids != null) {
            for (Kid kid: kids) {
                pos = kid.pos;
                PdfGraphics2D g2 = kid.graphics;
                g2.cb.restoreState();
                g2.cb.restoreState();
                buf.append(buf2.getBuffer(), last, pos - last);
                g2.dg2.dispose();
                g2.dg2 = null;
                g2.internalDispose(buf);
                last = pos;
            }
        }
        buf.append(buf2.getBuffer(), last, buf2.size() - last);
    }
    
    
    
    
    
    
    
    
    
    private void followPath(Shape s, int drawType) {
        if (s==null) return;
        if (drawType==STROKE) {
            if (!(stroke instanceof BasicStroke)) {
                s = stroke.createStrokedShape(s);
                followPath(s, FILL);
                return;
            }
        }
        if (drawType==STROKE) {
            setStrokeDiff(stroke, oldStroke);
            oldStroke = stroke;
            setStrokePaint();
        }
        else if (drawType==FILL)
            setFillPaint();
        PathIterator points;
        int traces = 0;
        if (drawType == CLIP)
            points = s.getPathIterator(IDENTITY);
        else
            points = s.getPathIterator(transform);
        float[] coords = new float[6];
        while(!points.isDone()) {
            ++traces;
            int segtype = points.currentSegment(coords);
            normalizeY(coords);
            switch(segtype) {
                case PathIterator.SEG_CLOSE:
                    cb.closePath();
                    break;

                case PathIterator.SEG_CUBICTO:
                    cb.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;

                case PathIterator.SEG_LINETO:
                    cb.lineTo(coords[0], coords[1]);
                    break;

                case PathIterator.SEG_MOVETO:
                    cb.moveTo(coords[0], coords[1]);
                    break;

                case PathIterator.SEG_QUADTO:
                    cb.curveTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
            }
            points.next();
        }
        switch (drawType) {
        case FILL:
            if (traces > 0) {
                if (points.getWindingRule() == PathIterator.WIND_EVEN_ODD)
                    cb.eoFill();
                else
                    cb.fill();
            }
            break;
        case STROKE:
            if (traces > 0)
                cb.stroke();
            break;
        default: 
            if (traces == 0)
                cb.rectangle(0, 0, 0, 0);
            if (points.getWindingRule() == PathIterator.WIND_EVEN_ODD)
                cb.eoClip();
            else
                cb.clip();
            cb.newPath();
        }
    }
    
    private float normalizeY(float y) {
        return this.height - y;
    }
    
    private void normalizeY(float[] coords) {
        coords[1] = normalizeY(coords[1]);
        coords[3] = normalizeY(coords[3]);
        coords[5] = normalizeY(coords[5]);
    }
    
    private AffineTransform normalizeMatrix() {
        double[] mx = new double[6];
        AffineTransform result = AffineTransform.getTranslateInstance(0,0);
        result.getMatrix(mx);
        mx[3]=-1;
        mx[5]=height;
        result = new AffineTransform(mx);
        result.concatenate(transform);
        return result;
    }
    
    private boolean drawImage(Image img, Image mask, AffineTransform xform, Color bgColor, ImageObserver obs) {
        if (xform==null)
            xform = new AffineTransform();
        else
            xform = new AffineTransform(xform);
        xform.translate(0, img.getHeight(obs));
        xform.scale(img.getWidth(obs), img.getHeight(obs));
        
        AffineTransform inverse = this.normalizeMatrix();
        AffineTransform flipper = AffineTransform.getScaleInstance(1,-1);
        inverse.concatenate(xform);
        inverse.concatenate(flipper);
        
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        if (currentFillGState != 255) {
            PdfGState gs = fillGState[255];
            if (gs == null) {
                gs = new PdfGState();
                gs.setFillOpacity(1);
                fillGState[255] = gs;
            }
            cb.setGState(gs);
        }
        
        try {
            com.lowagie.text.Image image = null;
            if(!convertImagesToJPEG){
                image = com.lowagie.text.Image.getInstance(img, bgColor);
            }
            else{
                BufferedImage scaled = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
                Graphics2D g3 = scaled.createGraphics();
                g3.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
                g3.dispose();
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
                iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwparam.setCompressionQuality(jpegQuality);
                ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                iw.setOutput(ios);
                iw.write(null, new IIOImage(scaled, null, null), iwparam);
                iw.dispose();
                ios.close();

                scaled.flush();
                scaled = null;
                image = com.lowagie.text.Image.getInstance(baos.toByteArray());
                
            }
            if (mask!=null) {
                com.lowagie.text.Image msk = com.lowagie.text.Image.getInstance(mask, null, true);
                msk.makeMask();
                msk.setInverted(true);
                image.setImageMask(msk);
            }
            cb.addImage(image, (float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]);
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }
        if (currentFillGState != 255) {
            PdfGState gs = fillGState[currentFillGState];
            cb.setGState(gs);
        }        
        return true;
    }
    
    private boolean checkNewPaint(Paint oldPaint) {
        if (paint == oldPaint)
            return false;
        return !((paint instanceof Color) && paint.equals(oldPaint));
    }
    
    private void setFillPaint() {
        if (checkNewPaint(paintFill)) {
            paintFill = paint;
            setPaint(false, 0, 0, true);
        }
    }
    
    private void setStrokePaint() {
        if (checkNewPaint(paintStroke)) {
            paintStroke = paint;
            setPaint(false, 0, 0, false);
        }
    }
    
    private void setPaint(boolean invert, double xoffset, double yoffset, boolean fill) {
        if (paint instanceof Color) {
            Color color = (Color)paint;
            int alpha = color.getAlpha();
            if (fill) {
                if (alpha != currentFillGState) {
                    currentFillGState = alpha;
                    PdfGState gs = fillGState[alpha];
                    if (gs == null) {
                        gs = new PdfGState();
                        gs.setFillOpacity(alpha / 255f);
                        fillGState[alpha] = gs;
                    }
                    cb.setGState(gs);
                }
                cb.setColorFill(color);
            }
            else {
                if (alpha != currentStrokeGState) {
                    currentStrokeGState = alpha;
                    PdfGState gs = strokeGState[alpha];
                    if (gs == null) {
                        gs = new PdfGState();
                        gs.setStrokeOpacity(alpha / 255f);
                        strokeGState[alpha] = gs;
                    }
                    cb.setGState(gs);
                }
                cb.setColorStroke(color);
            }
        }
        else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint)paint;
            Point2D p1 = gp.getPoint1();
            transform.transform(p1, p1);
            Point2D p2 = gp.getPoint2();
            transform.transform(p2, p2);
            Color c1 = gp.getColor1();
            Color c2 = gp.getColor2();
            PdfShading shading = PdfShading.simpleAxial(cb.getPdfWriter(), (float)p1.getX(), normalizeY((float)p1.getY()), (float)p2.getX(), normalizeY((float)p2.getY()), c1, c2);
            PdfShadingPattern pat = new PdfShadingPattern(shading);
            if (fill)
                cb.setShadingFill(pat);
            else
                cb.setShadingStroke(pat);
        }
        else if (paint instanceof TexturePaint) {
            try {
                TexturePaint tp = (TexturePaint)paint;
                BufferedImage img = tp.getImage();
                Rectangle2D rect = tp.getAnchorRect();
                com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(img, null);
                PdfPatternPainter pattern = cb.createPattern(image.getWidth(), image.getHeight());
                AffineTransform inverse = this.normalizeMatrix();
                inverse.translate(rect.getX(), rect.getY());
                inverse.scale(rect.getWidth() / image.getWidth(), -rect.getHeight() / image.getHeight());
                double[] mx = new double[6];
                inverse.getMatrix(mx);
                pattern.setPatternMatrix((float)mx[0], (float)mx[1], (float)mx[2], (float)mx[3], (float)mx[4], (float)mx[5]) ;
                image.setAbsolutePosition(0,0);
                pattern.addImage(image);
                if (fill)
                    cb.setPatternFill(pattern);
                else
                    cb.setPatternStroke(pattern);
            } catch (Exception ex) {
                if (fill)
                    cb.setColorFill(Color.gray);
                else
                    cb.setColorStroke(Color.gray);
            }
        }
        else {
            try {
                BufferedImage img = null;
                int type = BufferedImage.TYPE_4BYTE_ABGR;
                if (paint.getTransparency() == Transparency.OPAQUE) {
                    type = BufferedImage.TYPE_3BYTE_BGR;
                }
                img = new BufferedImage((int)width, (int)height, type);
                Graphics2D g = (Graphics2D)img.getGraphics();
                g.transform(transform);
                AffineTransform inv = transform.createInverse();
                Shape fillRect = new Rectangle2D.Double(0,0,img.getWidth(),img.getHeight());
                fillRect = inv.createTransformedShape(fillRect);
                g.setPaint(paint);
                g.fill(fillRect);
                if (invert) {
                    AffineTransform tx = new AffineTransform();
                    tx.scale(1,-1);
                    tx.translate(-xoffset,-yoffset);
                    g.drawImage(img,tx,null);
                }
                g.dispose();
                g = null;
                com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(img, null);
                PdfPatternPainter pattern = cb.createPattern(width, height);
                image.setAbsolutePosition(0,0);
                pattern.addImage(image);
                if (fill)
                    cb.setPatternFill(pattern);
                else
                    cb.setPatternStroke(pattern);
            } catch (Exception ex) {
                if (fill)
                    cb.setColorFill(Color.gray);
                else
                    cb.setColorStroke(Color.gray);
            }
        }
    }
    
    private synchronized void waitForImage(java.awt.Image image) {
        if (mediaTracker == null)
            mediaTracker = new MediaTracker(new PdfGraphics2D.fakeComponent());
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        }
        catch (InterruptedException e) {
            
        }
        mediaTracker.removeImage(image);
    }
        
    static private class fakeComponent extends Component {

        private static final long serialVersionUID = 6450197945596086638L;
    }

    
    public static class HyperLinkKey extends RenderingHints.Key
    {
         public static final HyperLinkKey KEY_INSTANCE = new HyperLinkKey(9999);
         public static final Object VALUE_HYPERLINKKEY_OFF = new String("0");
         
        protected HyperLinkKey(int arg0) {
            super(arg0);
        }
        
        public boolean isCompatibleValue(Object val)
        {
            return true;
        }
        public String toString()
        {
            return "HyperLinkKey";
        }
    }

}
