

package net.sf.freecol.common.resources;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.common.io.sza.SimpleZippedAnimation;


public class SZAResource extends Resource {
    private static final Logger logger = Logger.getLogger(SZAResource.class.getName());

    private Map<Double, SimpleZippedAnimation> scaledSzAnimations = new HashMap<Double, SimpleZippedAnimation>();
    private SimpleZippedAnimation szAnimation = null;
    private volatile Object loadingLock = new Object();
    
    
    SZAResource(URL resourceLocator) {
        super(resourceLocator);
    }
    
    
    
    public SimpleZippedAnimation getSimpleZippedAnimation() {
        if (szAnimation != null) {
            return szAnimation;
        }
        synchronized (loadingLock) {
            if (szAnimation != null) {
                return szAnimation;
            }
            try {
                szAnimation = new SimpleZippedAnimation(getResourceLocator());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not load SimpleZippedAnimation: " + getResourceLocator(), e);
            }
            return szAnimation;
        }
    }
    
    
    public SimpleZippedAnimation getSimpleZippedAnimation(double scale) {
        final SimpleZippedAnimation sza = getSimpleZippedAnimation();
        if (scale == 1.0) {
            return sza;
        }
        final SimpleZippedAnimation cachedScaledVersion = scaledSzAnimations.get(scale);
        if (cachedScaledVersion != null) {
            return cachedScaledVersion;
        }
        synchronized (loadingLock) {
            if (scaledSzAnimations.get(scale) != null) {
                return scaledSzAnimations.get(scale);
            }
            final SimpleZippedAnimation scaledVersion = sza.createScaledVersion(scale);
            scaledSzAnimations.put(scale, scaledVersion);
            return scaledVersion;
        }
    }
}
