

package net.sf.freecol.common.resources;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class ImageResource extends Resource {

    private Map<Dimension, Image> grayscaleImages = new HashMap<Dimension, Image>();
    private Map<Dimension, Image> scaledImages = new HashMap<Dimension, Image>();
    private Image image = null;
    private Object loadingLock = new Object();
    private static final Component _c = new Component() {};
    
    
    ImageResource(URI resourceLocator) {
        super(resourceLocator);
    }
    
    
    
    public Image getImage() {
        if (image != null) {
            return image;
        }
        synchronized (loadingLock) {
            if (image != null) {
                return image;
            }
            MediaTracker mt = new MediaTracker(_c);
            Image im;
            try {
                im = Toolkit.getDefaultToolkit().createImage(getResourceLocator().toURL());
                mt.addImage(im, 0);
                mt.waitForID(0);
            } catch (Exception e) {
                return null;
            }
            image = im;
            return image;
        }
    }
    
    
    public Image getImage(double scale) {
        final Image im = getImage();
        return getImage(new Dimension((int) (im.getWidth(null) * scale), (int) (im.getHeight(null) * scale)));    
    }
    
    
    public Image getImage(Dimension d) {
        final Image im = getImage();
        if (im.getWidth(null) == d.width
                && im.getHeight(null) == d.height) {
            return im;
        }
        final Image cachedScaledVersion = scaledImages.get(d);
        if (cachedScaledVersion != null) {
            return cachedScaledVersion;
        }
        synchronized (loadingLock) {
            if (scaledImages.get(d) != null) {
                return scaledImages.get(d);
            }
            MediaTracker mt = new MediaTracker(_c);
            final Image scaledVersion = im.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
            mt.addImage(scaledVersion, 0, d.width, d.height);
            try {
                mt.waitForID(0);
            } catch (InterruptedException e) {
                return null;
            }
            scaledImages.put(d, scaledVersion);
            return scaledVersion;
        }
    }
    
    
    public Image getGrayscaleImage(Dimension d) {
        final Image cachedGrayscaleImage = grayscaleImages.get(d);
        if (cachedGrayscaleImage != null) {
            return cachedGrayscaleImage;
        }
        synchronized (loadingLock) {
            if (grayscaleImages.get(d) != null) {
                return grayscaleImages.get(d);
            }
            final Image grayscaleImage = convertToGrayscale(getImage(d));
            grayscaleImages.put(d, grayscaleImage);
            return grayscaleImage;
        }
    }
    
    
    public Image getGrayscaleImage(double scale) {
        final Image im = getImage();
        return getGrayscaleImage(new Dimension((int) (im.getWidth(null) * scale), (int) (im.getHeight(null) * scale)));    
    }
    
    
    private Image convertToGrayscale(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        ColorConvertOp filter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage srcImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImage.createGraphics().drawImage(image, 0, 0, null);
        return filter.filter(srcImage, null);
    }
}
