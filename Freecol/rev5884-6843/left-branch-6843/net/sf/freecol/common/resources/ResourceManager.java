

package net.sf.freecol.common.resources;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ImageIcon;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.video.Video;
import net.sf.freecol.common.io.sza.SimpleZippedAnimation;



public class ResourceManager {
    
    
    private static ResourceMapping baseMapping;
    private static ResourceMapping tcMapping;
    private static ResourceMapping campaignMapping;
    private static ResourceMapping scenarioMapping;
    private static List<ResourceMapping> modMappings = new LinkedList<ResourceMapping>();

    
    private static ResourceMapping mergedContainer;
    
    private static volatile Thread preloadThread = null;
    
    private static volatile boolean dirty = false;
    
    private static Dimension lastWindowSize;
    
    
    
    public static void setBaseMapping(final ResourceMapping _baseMapping) {
        baseMapping = _baseMapping;
        dirty = true;
    }

    
    public static void setTcMapping(final ResourceMapping _tcMapping) {
        tcMapping = _tcMapping;
        dirty = true;
    }

    
    public static void setModMappings(final List<ResourceMapping> _modMappings) {
        modMappings = _modMappings;
        dirty = true;
    }
    
    
    public static void setCampaignMapping(final ResourceMapping _campaignMapping) {
        campaignMapping = _campaignMapping;
        dirty = true;
    }

    
    public static void setScenarioMapping(final ResourceMapping _scenarioMapping) {
        scenarioMapping = _scenarioMapping;
        dirty = true;
    }

    
    public static void preload(final Dimension windowSize) {
        if (lastWindowSize != windowSize) {
            dirty = true;
        }
        lastWindowSize = windowSize;
        updateIfDirty(); 
        getImage("CanvasBackgroundImage", windowSize);
        getImage("TitleImage");
        getImage("BackgroundImage");
    }
    
    
    public static void startBackgroundPreloading(final Dimension windowSize) {
        lastWindowSize = windowSize;
        if (dirty) {
            updateIfDirty();
            return; 
        }
        preloadThread = new Thread(FreeCol.CLIENT_THREAD+"Resource loader") {
            public void run() {
                if (windowSize != null) {
                    getImage("EuropeBackgroundImage", windowSize);
                    getImage("CanvasBackgroundImage", windowSize);
                }
                for (String key : mergedContainer.getResources().keySet()) {
                    if (preloadThread != this) {
                        return;
                    }
                    getImage(key);
                    getSimpleZippedAnimation(key);
                }
            }
        };
        preloadThread.setPriority(2);
        preloadThread.start();
    }
    
    
    private static void updateIfDirty() {
        if (dirty) {
            dirty = false;
            preloadThread = null;
            createMergedContainer();
            startBackgroundPreloading(lastWindowSize);
        }
    }

    
    private static void createMergedContainer() {
        ResourceMapping _mergedContainer = new ResourceMapping();
        _mergedContainer.addAll(baseMapping);
        _mergedContainer.addAll(tcMapping);
        _mergedContainer.addAll(campaignMapping);
        _mergedContainer.addAll(scenarioMapping);
        ListIterator<ResourceMapping> it = modMappings.listIterator(modMappings.size()); 
        while (it.hasPrevious()) {
            _mergedContainer.addAll(it.previous());
        }
        mergedContainer = _mergedContainer;
    }
    
    
    private static <T> T getResource(final String resourceId, final Class<T> type) {
        final Resource r = mergedContainer.get(resourceId);
        if (type.isInstance(r)) {
            return type.cast(r);
        } else {
            return null;
        }
    }

    
    public static SimpleZippedAnimation getSimpleZippedAnimation(final String resource) {
        updateIfDirty();
        final SZAResource r = getResource(resource, SZAResource.class);
        return (r != null) ? r.getSimpleZippedAnimation() : null;
    }
    
    
    public static Video getVideo(final String resource) {
        updateIfDirty();
        final VideoResource r = getResource(resource, VideoResource.class);
        return (r != null) ? r.getVideo() : null;
    }
    
    
    public static SimpleZippedAnimation getSimpleZippedAnimation(final String resource, final double scale) {
        updateIfDirty();
        final SZAResource r = getResource(resource, SZAResource.class);
        return (r != null) ? r.getSimpleZippedAnimation(scale) : null;
    }

    
    public static Image getImage(final String resource) {
        updateIfDirty();
        final ImageResource r = getResource(resource, ImageResource.class);
        return (r != null) ? r.getImage() : null;
    }
    
    
    public static Image getImage(final String resource, final double scale) {
        updateIfDirty();
        final ImageResource r = getResource(resource, ImageResource.class);
        return (r != null) ? r.getImage(scale) : null;
    }
    
    
    public static Image getImage(final String resource, final Dimension size) {
        updateIfDirty();
        final ImageResource r = getResource(resource, ImageResource.class);
        return (r != null) ? r.getImage(size) : null;
    }
    
    
    public static Image getGrayscaleImage(final String resource, final Dimension size) {
        updateIfDirty();
        final ImageResource r = getResource(resource, ImageResource.class);
        return (r != null) ? r.getGrayscaleImage(size) : null;
    }
    
    
    public static Image getGrayscaleImage(final String resource, final double scale) {
        updateIfDirty();
        final ImageResource r = getResource(resource, ImageResource.class);
        return (r != null) ? r.getGrayscaleImage(scale) : null;
    }

    
    public static ImageIcon getImageIcon(final String resource) {
        updateIfDirty();
        final Image im = getImage(resource);
        return (im != null) ? new ImageIcon(im) : null;
    }

    
    public static Color getColor(final String resource) {
        updateIfDirty();
        final ColorResource r = getResource(resource, ColorResource.class);
        return (r != null) ? r.getColor() : null;
    }
}