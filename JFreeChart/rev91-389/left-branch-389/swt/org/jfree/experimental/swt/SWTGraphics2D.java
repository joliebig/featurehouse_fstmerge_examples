

package org.jfree.experimental.swt;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
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
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.Transform;


public class SWTGraphics2D extends Graphics2D {

    
    private GC gc;

    
    private java.awt.Composite composite;
    
    
    private Map colorsPool = new HashMap();

    
    private Map fontsPool = new HashMap();

    
    private List resourcePool = new ArrayList();

    
    public SWTGraphics2D(GC gc) {
        super();
        this.gc = gc;
        this.composite = AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f);
    }

    
    private Resource addToResourcePool(Resource resource) {
        resourcePool.add(resource);
        return resource;
    }

    
    private void disposeResourcePool() {
        for (Iterator it = resourcePool.iterator();it.hasNext();) {
            Resource resource = (Resource)it.next();
            resource.dispose();
        }
        resourcePool.clear();
        colorsPool.clear();
        resourcePool.clear();
    }

    
    private org.eclipse.swt.graphics.Font getSwtFontFromPool(Font font) {
        org.eclipse.swt.graphics.Font swtFont = (org.eclipse.swt.graphics.Font)
        fontsPool.get(font);
        if (swtFont == null) {
            swtFont = new org.eclipse.swt.graphics.Font( 
                    gc.getDevice(), 
                    SWTUtils.toSwtFontData(gc.getDevice(), font, true));
            addToResourcePool(swtFont);
            fontsPool.put(font, swtFont);
        }
        return swtFont;
    }

    
    private org.eclipse.swt.graphics.Color getSwtColorFromPool(Color awtColor) {
        org.eclipse.swt.graphics.Color swtColor = 
                (org.eclipse.swt.graphics.Color)
                
                
                
                this.colorsPool.get(new Integer(awtColor.getRGB()));
        if (swtColor == null) {
            swtColor = SWTUtils.toSwtColor(gc.getDevice(), awtColor);
            addToResourcePool(swtColor);
            
            
            this.colorsPool.put(new Integer(awtColor.getRGB()), swtColor);
        }
        return swtColor;
    }

    
    private void switchColors() {
        org.eclipse.swt.graphics.Color bg = gc.getBackground();
        org.eclipse.swt.graphics.Color fg = gc.getForeground();
        gc.setBackground(fg);
        gc.setForeground(bg);
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

    
    private Transform toSwtTransform(AffineTransform awtTransform) {
        Transform t = new Transform(gc.getDevice());
        double[] matrix = new double[6];
        awtTransform.getMatrix(matrix);
        t.setElements((float) matrix[0], (float) matrix[1],
                (float) matrix[2], (float) matrix[3],
                (float) matrix[4], (float) matrix[5]); 
        return t;
    }

    
    private AffineTransform toAwtTransform(Transform swtTransform) {
        float[] elements = new float[6];
        swtTransform.getElements(elements);
        AffineTransform awtTransform = new AffineTransform(elements);
        return awtTransform;
    }

    
    public void draw(Shape shape) {
        Path path = toSwtPath(shape);
        gc.drawPath(path);
        path.dispose();
    }

    
    public boolean drawImage(Image image, AffineTransform xform,
            ImageObserver obs) {
        
        return false;
    }

    
    public void drawImage(BufferedImage image, BufferedImageOp op, int x, 
            int y) {
        org.eclipse.swt.graphics.Image im 
            = new org.eclipse.swt.graphics.Image(gc.getDevice(), 
                    convertToSWT(image));
        gc.drawImage(im, x, y);
        im.dispose();
    }

    
    public void drawImage(org.eclipse.swt.graphics.Image image, int x, int y) {
        gc.drawImage(image, x, y);
    }

    
    public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
        
    }

    
    public void drawRenderableImage(RenderableImage image, 
            AffineTransform xform) {
        

    }

    
    public void drawString(String text, int x, int y) {
        float fm = gc.getFontMetrics().getAscent();
        gc.drawString(text, x, (int) (y - fm), true);
    }

    
    public void drawString(String text, float x, float y) {
        float fm = gc.getFontMetrics().getAscent();
        gc.drawString(text, (int) x, (int) ( y - fm ), true);
    }

    
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        

    }

    
    public void drawString(AttributedCharacterIterator iterator, float x, 
            float y) {
        

    }

    
    public void fill(Shape shape) {
        Path path = toSwtPath(shape);
        switchColors();
        this.gc.fillPath(path);
        switchColors();
        path.dispose();
    }

    
    public boolean hit(Rectangle rect, Shape text, boolean onStroke) {
        
        return false;
    }

    
    public GraphicsConfiguration getDeviceConfiguration() {
        
        return null;
    }

    
    public void setComposite(Composite comp) {
        this.composite = comp;
        if (comp instanceof AlphaComposite) {
            AlphaComposite acomp = (AlphaComposite) comp; 
            int alpha = (int) (acomp.getAlpha()*0xFF);
            this.gc.setAlpha(alpha);
        } 
        else {
            System.out.println("warning, can only handle alpha composite at the moment.");
        }
    }

    
    public void setPaint(Paint paint) {
        if (paint instanceof Color) {
            setColor((Color) paint);
        }
        else {
            throw new RuntimeException("Can only handle 'Color' at present.");
        }
    }

    
    public void setStroke(Stroke stroke) {
        if (stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) stroke;
            
            gc.setLineWidth((int) bs.getLineWidth());

            
            switch (bs.getLineJoin()) {
                case BasicStroke.JOIN_BEVEL :
                    gc.setLineJoin(SWT.JOIN_BEVEL);
                    break;
                case BasicStroke.JOIN_MITER :
                    gc.setLineJoin(SWT.JOIN_MITER);
                    break;
                case BasicStroke.JOIN_ROUND :
                    gc.setLineJoin(SWT.JOIN_ROUND);
                    break;
            }

            
            switch (bs.getEndCap()) {
                case BasicStroke.CAP_BUTT :
                    gc.setLineCap(SWT.CAP_FLAT);
                    break;
                case BasicStroke.CAP_ROUND :
                    gc.setLineCap(SWT.CAP_ROUND);
                    break;
                case BasicStroke.CAP_SQUARE :
                    gc.setLineCap(SWT.CAP_SQUARE);
                    break;
            }

            
            gc.setLineStyle(SWT.LINE_SOLID);

            
            float[] dashes = bs.getDashArray();
            if (dashes != null) {
                int[] swtDashes = new int[dashes.length];
                for (int i = 0; i < swtDashes.length; i++) {
                    swtDashes[i] = (int) dashes[i];
                }
                gc.setLineDash(swtDashes);
            }
        }
        else {
            throw new RuntimeException(
                    "Can only handle 'Basic Stroke' at present.");
        }
    }

    
    public void setRenderingHint(Key hintKey, Object hintValue) {
        

    }

    
    public Object getRenderingHint(Key hintKey) {
        
        return null;
    }

    
    public void setRenderingHints(Map hints) {
        

    }

    
    public void addRenderingHints(Map hints) {
        

    }

    
    public RenderingHints getRenderingHints() {
        
        return null;
    }

    
    public void translate(int x, int y) {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        swtTransform.translate(x, y);
        gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void translate(double tx, double ty) {
        translate((int) tx, (int) ty);
    }

    
    public void rotate(double theta) {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        swtTransform.rotate( (float) (theta * 180 / Math.PI));
        gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void rotate(double theta, double x, double y) {
        

    }

    
    public void scale(double scaleX, double scaleY) {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        swtTransform.scale((float) scaleX, (float) scaleY);
        gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void shear(double shearX, double shearY) {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        Transform shear = new Transform(gc.getDevice(), 1f, (float) shearX, 
                (float) shearY, 1f, 0, 0);
        swtTransform.multiply(shear);
        gc.setTransform(swtTransform);
        swtTransform.dispose();
    }

    
    public void transform(AffineTransform Tx) {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        Transform swtMatrix = toSwtTransform(Tx);
        swtTransform.multiply(swtMatrix);
        gc.setTransform(swtTransform);
        swtMatrix.dispose();
        swtTransform.dispose();
    }

    
    public void setTransform(AffineTransform Tx) {
        gc.setTransform(toSwtTransform(Tx));
    }

    
    public AffineTransform getTransform() {
        Transform swtTransform = new Transform(gc.getDevice()); 
        gc.getTransform(swtTransform);
        AffineTransform awtTransform = toAwtTransform(swtTransform);
        swtTransform.dispose();
        return awtTransform; 
    }

    
    public Paint getPaint() {
        return SWTUtils.toAwtColor(gc.getForeground());
    }

    
    public Composite getComposite() {
        return this.composite;
    }

    
    public void setBackground(Color color) {
        gc.getBackground().dispose();
        org.eclipse.swt.graphics.Color swtColor = SWTUtils.toSwtColor(gc.getDevice(), color);
        gc.setBackground(swtColor);
        swtColor.dispose(); 
    }

    
    public Color getBackground() {
        return SWTUtils.toAwtColor(gc.getBackground());
    }

    
    public Stroke getStroke() {
        return new BasicStroke(gc.getLineWidth(), gc.getLineCap(), 
                gc.getLineJoin());
    }

    
    public FontRenderContext getFontRenderContext() {
        FontRenderContext fontRenderContext 
            = new FontRenderContext(new AffineTransform(), true, true);
        return fontRenderContext;
    }

    
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        

    }

    
    public Graphics create() {
        
        return null;
    }

    
    public Color getColor() {
        return SWTUtils.toAwtColor(gc.getForeground());
    }

    
    public void setColor(Color color) {
        org.eclipse.swt.graphics.Color swtColor = getSwtColorFromPool(color);
        gc.setForeground(swtColor);
        
        if (this.composite instanceof AlphaComposite) {
            AlphaComposite acomp = (AlphaComposite) this.composite;
            switch (acomp.getRule()) {
            case AlphaComposite.SRC_OVER:
                this.gc.setAlpha((int) (color.getAlpha()*acomp.getAlpha()));
                break;
            default:
                this.gc.setAlpha(color.getAlpha());
                break;
            }
        }
    }

    
    public void setPaintMode() {
        
    }

    
    public void setXORMode(Color color) {
        

    }

    
    public Font getFont() {
        
        FontData[] fontData = gc.getFont().getFontData();
        
        return SWTUtils.toAwtFont(gc.getDevice(), fontData[0], true);
    }

    
    public void setFont(Font font) {
        org.eclipse.swt.graphics.Font swtFont = getSwtFontFromPool(font);
        gc.setFont(swtFont);
    }

    
    public FontMetrics getFontMetrics(Font font) {
        return SWTUtils.DUMMY_PANEL.getFontMetrics(font);
    }

    
    public void clip(Shape s) {
        Path path = toSwtPath(s);
        gc.setClipping(path);
        path.dispose();
    }

    
    public Rectangle getClipBounds() {
        org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
        return new Rectangle(clip.x, clip.y, clip.width, clip.height);
    }

    
    public void clipRect(int x, int y, int width, int height) {
        org.eclipse.swt.graphics.Rectangle clip = gc.getClipping();
        clip.intersects(x, y, width, height);
        gc.setClipping(clip);
    }

    
    public void setClip(int x, int y, int width, int height) {
        gc.setClipping(x, y, width, height);
    }

    
    public Shape getClip() {
        return SWTUtils.toAwtRectangle(gc.getClipping());
    }

    
    public void setClip(Shape clip) {
        if (clip == null) 
            return;
        Path clipPath = toSwtPath(clip);
        gc.setClipping(clipPath);
        clipPath.dispose();
    }

    
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        

    }

    
    public void drawLine(int x1, int y1, int x2, int y2) {
        gc.drawLine(x1, y1, x2, y2);
    }

    
    public void fillRect(int x, int y, int width, int height) {
        this.switchColors();
        gc.fillRectangle(x, y, width, height);
        this.switchColors();
    }

    
    public void clearRect(int x, int y, int width, int height) {
        

    }

    
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        

    }

    
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
        

    }

    
    public void drawOval(int x, int y, int width, int height) {
        

    }

    
    public void fillOval(int x, int y, int width, int height) {
        

    }

    
    public void drawArc(int x, int y, int width, int height, int arcStart,
            int arcAngle) {
        

    }

    
    public void fillArc(int x, int y, int width, int height, int arcStart,
            int arcAngle) {
        

    }

    
    public void drawPolyline(int [] xPoints, int [] yPoints, int npoints) {
        

    }

    
    public void drawPolygon(int [] xPoints, int [] yPoints, int npoints) {
        

    }

    
    public void fillPolygon(int [] xPoints, int [] yPoints, int npoints) {
        

    }

    
    public boolean drawImage(Image image, int x, int y, 
            ImageObserver observer) {
        
        return false;
    }

    
    public boolean drawImage(Image image, int x, int y, int width, int height,
            ImageObserver observer) {
        
        return false;
    }

    
    public boolean drawImage(Image image, int x, int y, Color bgcolor,
            ImageObserver observer) {
        
        return false;
    }

    
    public boolean drawImage(Image image, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        
        return false;
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

    static ImageData convertToSWT(BufferedImage bufferedImage) {
        if (bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel 
                    = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(),
                    colorModel.getGreenMask(), colorModel.getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), 
                    bufferedImage.getHeight(), colorModel.getPixelSize(),
                    palette);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[3];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    int pixel = palette.getPixel(new RGB(pixelArray[0], 
                            pixelArray[1], pixelArray[2]));
                    data.setPixel(x, y, pixel);
                }
            }
            return data;
        } 
        else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel) 
                    bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, 
                        blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), colorModel.getPixelSize(),
                    palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }
            return data;
        }
        return null;
    }
}
