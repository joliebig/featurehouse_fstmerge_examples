package net.sf.freecol.common.io.sza;

import java.awt.Image;


public interface ImageAnimationEvent extends AnimationEvent {

    
    public Image getImage();
    
    
    public int getDurationInMs();
}
