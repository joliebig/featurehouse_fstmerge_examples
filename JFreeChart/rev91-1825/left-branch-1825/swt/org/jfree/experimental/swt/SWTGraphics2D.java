

package org.jfree.experimental.swt;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.Transform;


public class SWTGraphics2D extends Graphics2D {

    
    private GC gc;

    
    private RenderingHints hints;

    
    private java.awt.Composite composite;

    
    private Map colorsPool = new HashMap();

    
    private Map fontsPool = new HashMap();

    
    private Map transformsPool = new HashMap();

    
    private List resourcePool = new ArrayList();

    
    public SWTGraphics2D(GC gc) {
        super();
        this.gc = gc;
        this.hints = new RenderingHints(null);
        this.composite = AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f);
    }

    
    public Graphics create() {
        
        return null;
    }

    
    public GraphicsConfiguration getDeviceConfiguration() {
        
        return null;
    }

    
    public Object getRenderingHint(Key hintKey) {
        return this.hints.get(hintKey);
    }

    
    public void setRenderingHint(Key hintKey, Object hintValue) {
        this.hints.put(hintKey, hintValue);
    }

    
    public RenderingHints getRenderingHints() {
        return (RenderingHints) this.hints.clone();
    }

    
    public void addRenderingHints(Map hints) {
        this.hints.putAll(hints);
    }

    
    public void setRenderingHints(Map hints) {
        if (hints == null) {
            throw new NullPointerException("Null 'hints' argument.");
        }
        this.hints = new RenderingHints(hints);
    }

    
    public Paint getPaint() {
        
        
        
        return SWTUtils.toAwtColor(this.gc.getForeground());
    }

    
    public void setPaint(Paint paint) {
        if (paint instanceof Color) {
            setColor((Color) paint);
        }
        else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) paint;
            setColor(gp.getColor1());
        }
        else {
            throw new RuntimeException("Can only handle 'Color' at present.");
        }
    }

    
    public Color getColor() {
        
        
        
        return SWTUtils.toAwtColor(this.gc.getForeground());
    }

    
    public void setColor(Color color) {
        org.eclipse.swt.graphics.Color swtColor = getSwtColorFromPool(color);
        this.gc.setForeground(swtColor);
        
        if (this.composite instanceof AlphaComposite) {
            AlphaComposite acomp = (AlphaComposite) this.composite;
            switch (acomp.getRule()) {
            case AlphaComposite.SRC_OVER:
                this.gc.setAlpha((int) (color.getAlpha() * acomp.getAlpha()));
                break;
            default:
                this.gc.setAlpha(color.getAlpha());
                break;
            }
        }
    }

    
    public void setBackground(Color color) {
        org.eclipse.swt.graphics.Color swtColor = getSwtColorFromPool(color);
        this.gc.setBackground(swtColor);
    }

    
    public Color getBackground() {
        return SWTUtils.toAwtColor(this.gc.getBackground());
    }

    
    public void setPaintMode() {
        
    }

    
    public void setXORMode(Color color) {
        
    }

    
    public Composite getComposite() {
        return this.composite;
    }

    
    public void setComposite(Composite comp) {
        this.composite = comp;
        if (comp instanceof AlphaComposite) {
            AlphaComposite acomp = (AlphaComposite) comp;
            int alpha = (int) (acomp.getAlpha() * 0xFF);
            this.gc.setAlpha(alpha);
        }
        else {
            System.out.println("warning, can only handle alpha composite at the moment.");
        }
    }

    
    public Stroke getStroke() {
        return new BasicStroke(this.gc.getLineWidth(), this.gc.getLineCap(),
                this.gc.getLineJoin());
    }

    
    public void setStroke(Stroke stroke) {
        if (stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) stroke;
            
            this.gc.setLineWidth((int) bs.getLineWidth());

            
            switch (bs.getLineJoin()) {
                case BasicStroke.JOIN_BEVEL :
                    this.gc.setLineJoin(SWT.JOIN_BEVEL);
                    break;
                case BasicStroke.JOIN_MITER :
                    this.gc.setLineJoin(SWT.JOIN_MITER);
                    break;
                case BasicStroke.JOIN_ROUND :
                    this.gc.setLineJoin(SWT.JOIN_ROUND);
                    break;
            }

            
            switch (bs.getEndCap()) {
                case BasicStroke.CAP_BUTT :
                    this.gc.setLineCap(SWT.CAP_FLAT);
                    break;
                case BasicStroke.CAP_ROUND :
                    this.gc.setLineCap(SWT.CAP_ROUND);
                    break;
                case BasicStroke.CAP_SQUARE :
                    this.gc.setLineCap(SWT.CAP_SQUARE);
                    break;
            }

            
            this.gc.setLineStyle(SWT.LINE_SOLID);

            
            float[] dashes = bs.getDashArray();
            if (dashes != null) {
                int[] swtDashes = new int[dashes.length];
                for (int i = 0; i < swtDashes.length; i++) {
                    swtDashes[i] = (int) dashes[i];
                }
                this.gc.setLineDash(swtDashes);
            }
        }
        else {
            throw new RuntimeException(
                    "Can only handle 'Basic Stroke' at present.");
        }
    }

    
    public void clip(Shape s) {
        Path path = toSwtPath(s);
        this.gc.setClipping(path);
        path.dispose();
    }

    
    public Rectangle getClipBounds() {
        org.eclipse.swt.graphics.Rectangle clip = this.gc.getClipping();
        return new Rectangle(clip.x, clip.y, clip.width, clip.height);
    }

    
    public void clipRect(int x, int y, int width, int height) {
        org.eclipse.swt.graphics.Rectangle clip = this.gc.getClipping();
        clip.intersects(x, y, width, height);
        this.gc.setClipping(clip);
    }

    
    public Shape getClip() {
        return SWTUtils.toAwtRectangle(this.gc.getClipping());
    }

    
    public void setClip(Shape clip) {
        if (clip == null) {
            return;
        }
        Path clipPath = toSwtPath(clip);
        this.gc.setClipping(clipPath);
        clipPath.dispose();
    }

    
    public void setClip(int x, int y, int width, int height) {
        this.gc.setClipping(x, y, width, height);
    }

    
    public AffineTransform getTransform() {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        AffineTransform awtTransform = toAwtTransform(swtTransform);
        swtTransform.dispose();
        return awtTransform;
    }

    
    public void setTransform(AffineTransform t) {
        Transform transform = getSwtTransformFromPool(t);
        this.gc.setTransform(transform);
    }

    
    public void transform(AffineTransform t) {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        swtTransform.multiply(getSwtTransformFromPool(t));
        this.gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void translate(int x, int y) {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        swtTransform.translate(x, y);
        this.gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void translate(double tx, double ty) {
        translate((int) tx, (int) ty);
    }

    
    public void rotate(double theta) {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        swtTransform.rotate((float) (theta * 180 / Math.PI));
        this.gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void rotate(double theta, double x, double y) {
        
    }

    
    public void scale(double scaleX, double scaleY) {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        swtTransform.scale((float) scaleX, (float) scaleY);
        this.gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void shear(double shearX, double shearY) {
        Transform swtTransform = new Transform(this.gc.getDevice());
        this.gc.getTransform(swtTransform);
        Transform shear = new Transform(this.gc.getDevice(), 1f, (float) shearX,
                (float) shearY, 1f, 0, 0);
        swtTransform.multiply(shear);
        this.gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void draw(Shape shape) {
        Path path = toSwtPath(shape);
        this.gc.drawPath(path);
        path.dispose();
    }

    
    public void drawLine(int x1, int y1, int x2, int y2) {
        this.gc.drawLine(x1, y1, x2, y2);
    }

    
    public void drawPolygon(int [] xPoints, int [] yPoints, int npoints) {
        drawPolyline(xPoints, yPoints, npoints);
        if (npoints > 1) {
            this.gc.drawLine(xPoints[npoints - 1], yPoints[npoints - 1],
                    xPoints[0], yPoints[0]);
        }
    }

    
    public void drawPolyline(int [] xPoints, int [] yPoints, int npoints) {
        if (npoints > 1) {
            int x0 = xPoints[0];
            int y0 = yPoints[0];
            int x1 = 0, y1 = 0;
            for (int i = 1; i < npoints; i++) {
                x1 = xPoints[i];
                y1 = yPoints[i];
                this.gc.drawLine(x0, y0, x1, y1);
                x0 = x1;
                y0 = y1;
            }
        }
    }

    
    public void drawOval(int x, int y, int width, int height) {
        this.gc.drawOval(x, y, width - 1, height - 1);
    }

    
    public void drawArc(int x, int y, int width, int height, int arcStart,
            int arcAngle) {
        this.gc.drawArc(x, y, width - 1, height - 1, arcStart, arcAngle);
    }

    
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        this.gc.drawRoundRectangle(x, y, width - 1, height - 1, arcWidth,
                arcHeight);
    }

    
    public void fill(Shape shape) {
        Path path = toSwtPath(shape);
        
        
        
        switchColors();
        this.gc.fillPath(path);
        switchColors();
        path.dispose();
    }

    
    public void fillRect(int x, int y, int width, int height) {
        this.switchColors();
        this.gc.fillRectangle(x, y, width, height);
        this.switchColors();
    }

    
    public void clearRect(int x, int y, int width, int height) {
        Paint saved = getPaint();
        setPaint(getBackground());
        fillRect(x, y, width, height);
        setPaint(saved);
    }

    
    public void fillPolygon(int [] xPoints, int [] yPoints, int npoints) {
        
    }

    
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        switchColors();
        this.gc.fillRoundRectangle(x, y, width - 1, height - 1, arcWidth,
                arcHeight);
        switchColors();
    }

    
    public void fillOval(int x, int y, int width, int height) {
        switchColors();
        this.gc.fillOval(x, y, width - 1, height - 1);
        switchColors();
    }

    
    public void fillArc(int x, int y, int width, int height, int arcStart,
            int arcAngle) {
        switchColors();
        this.gc.fillArc(x, y, width - 1, height - 1, arcStart, arcAngle);
        switchColors();
    }

    
    public Font getFont() {
        
        FontData[] fontData = this.gc.getFont().getFontData();
        
        return SWTUtils.toAwtFont(this.gc.getDevice(), fontData[0], true);
    }

    
    public void setFont(Font font) {
        org.eclipse.swt.graphics.Font swtFont = getSwtFontFromPool(font);
        this.gc.setFont(swtFont);
    }

    
    public FontMetrics getFontMetrics(Font font) {
        return SWTUtils.DUMMY_PANEL.getFontMetrics(font);
    }

    
    public FontRenderContext getFontRenderContext() {
        FontRenderContext fontRenderContext = new FontRenderContext(
                new AffineTransform(), true, true);
        return fontRenderContext;
    }

    
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        

    }

    
    public void drawString(String text, int x, int y) {
        float fm = this.gc.getFontMetrics().getAscent();
        this.gc.drawString(text, x, (int) (y - fm), true);
    }

    
    public void drawString(String text, float x, float y) {
        float fm = this.gc.getFontMetrics().getAscent();
        this.gc.drawString(text, (int) x, (int) (y - fm), true);
    }

    
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        
    }

    
    public void drawString(AttributedCharacterIterator iterator, float x,
            float y) {
        
    }

    
    public boolean hit(Rectangle rect, Shape text, boolean onStroke) {
        
        return false;
    }

    
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        
    }

    
    public boolean drawImage(Image image, AffineTransform xform,
            ImageObserver obs) {
        
        return false;
    }

    
    public void drawImage(BufferedImage image, BufferedImageOp op, int x,
            int y) {
        org.eclipse.swt.graphics.Image im = new org.eclipse.swt.graphics.Image(
                this.gc.getDevice(), SWTUtils.convertToSWT(image));
        this.gc.drawImage(im, x, y);
        im.dispose();
    }

    
    public void drawImage(org.eclipse.swt.graphics.Image image, int x, int y) {
        this.gc.drawImage(image, x, y);
    }

    
    public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
        
    }

    
    public void drawRenderableImage(RenderableImage image,
            AffineTransform xform) {
        

    }

    
    public boolean drawImage(Image image, int x, int y,
            ImageObserver observer) {
        ImageData data = SWTUtils.convertAWTImageToSWT(image);
        if (data == null) {
            return false;
        }
        org.eclipse.swt.graphics.Image im = new org.eclipse.swt.graphics.Image(
                this.gc.getDevice(), data);
        this.gc.drawImage(im, x, y);
        im.dispose();
        return true;
    }

    
    public boolean drawImage(Image image, int x, int y, int width, int height,
            ImageObserver observer) {
        ImageData data = SWTUtils.convertAWTImageToSWT(image);
        if (data == null) {
            return false;
        }
        org.eclipse.swt.graphics.Image im = new org.eclipse.swt.graphics.Image(
                this.gc.getDevice(), data);
        org.eclipse.swt.graphics.Rectangle bounds = im.getBounds();
        this.gc.drawImage(im, 0, 0, bounds.width, bounds.height, x, y, width,
                height);
        im.dispose();
        return true;
    }

    
    public boolean drawImage(Image image, int x, int y, Color bgcolor,
            ImageObserver observer) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        if (w == -1 || h == -1) {
            return false;
        }
        Paint savedPaint = getPaint();
        fill(new Rectangle2D.Double(x, y, w, h));
        setPaint(savedPaint);
        return drawImage(image, x, y, observer);
    }

    
    public boolean drawImage(Image image, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        if (image == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        if (w == -1 || h == -1) {
            return false;
        }
        Paint savedPaint = getPaint();
        fill(new Rectangle2D.Double(x, y, w, h));
        setPaint(savedPaint);
        return drawImage(image, x, y, width, height, observer);
    }

    
    public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        
        return false;
    }

    
    public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        
        return false;
    }

    
    public void dispose() {
        
        disposeResourcePool();
    }

    
    private Resource addToResourcePool(Resource resource) {
        this.resourcePool.add(resource);
        return resource;
    }

    
    private void disposeResourcePool() {
        for (Iterator it = this.resourcePool.iterator(); it.hasNext();) {
            Resource resource = (Resource) it.next();
            resource.dispose();
        }
        this.fontsPool.clear();
        this.colorsPool.clear();
        this.transformsPool.clear();
        this.resourcePool.clear();
    }

    
    private org.eclipse.swt.graphics.Font getSwtFontFromPool(Font font) {
        org.eclipse.swt.graphics.Font swtFont = (org.eclipse.swt.graphics.Font)
        this.fontsPool.get(font);
        if (swtFont == null) {
            swtFont = new org.eclipse.swt.graphics.Font(this.gc.getDevice(),
                    SWTUtils.toSwtFontData(this.gc.getDevice(), font, true));
            addToResourcePool(swtFont);
            this.fontsPool.put(font, swtFont);
        }
        return swtFont;
    }

    
    private org.eclipse.swt.graphics.Color getSwtColorFromPool(Color awtColor) {
        org.eclipse.swt.graphics.Color swtColor =
                (org.eclipse.swt.graphics.Color)
                
                
                
                this.colorsPool.get(new Integer(awtColor.getRGB()));
        if (swtColor == null) {
            swtColor = SWTUtils.toSwtColor(this.gc.getDevice(), awtColor);
            addToResourcePool(swtColor);
            
            
            this.colorsPool.put(new Integer(awtColor.getRGB()), swtColor);
        }
        return swtColor;
    }

    
    private Transform getSwtTransformFromPool(AffineTransform awtTransform) {
        Transform t = (Transform) this.transformsPool.get(awtTransform);
        if (t == null) {
            t = new Transform(this.gc.getDevice());
            double[] matrix = new double[6];
            awtTransform.getMatrix(matrix);
            t.setElements((float) matrix[0], (float) matrix[1],
                    (float) matrix[2], (float) matrix[3],
                    (float) matrix[4], (float) matrix[5]);
            addToResourcePool(t);
            this.transformsPool.put(awtTransform, t);
        }
        return t;
    }

    
    private void switchColors() {
        org.eclipse.swt.graphics.Color bg = this.gc.getBackground();
        org.eclipse.swt.graphics.Color fg = this.gc.getForeground();
        this.gc.setBackground(fg);
        this.gc.setForeground(bg);
    }

    
    private Path toSwtPath(Shape shape) {
        int type;
        float[] coords = new float[6];
        Path path = new Path(this.gc.getDevice());
        PathIterator pit = shape.getPathIterator(null);
        while (!pit.isDone()) {
            type = pit.currentSegment(coords);
            switch (type) {
                case (PathIterator.SEG_MOVETO):
                    path.moveTo(coords[0], coords[1]);
                    break;
                case (PathIterator.SEG_LINETO):
                    path.lineTo(coords[0], coords[1]);
                    break;
                case (PathIterator.SEG_QUADTO):
                    path.quadTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case (PathIterator.SEG_CUBICTO):
                    path.cubicTo(coords[0], coords[1], coords[2],
                            coords[3], coords[4], coords[5]);
                    break;
                case (PathIterator.SEG_CLOSE):
                    path.close();
                    break;
                default:
                    break;
            }
            pit.next();
        }
        return path;
    }

    
    private AffineTransform toAwtTransform(Transform swtTransform) {
        float[] elements = new float[6];
        swtTransform.getElements(elements);
        AffineTransform awtTransform = new AffineTransform(elements);
        return awtTransform;
    }

}
