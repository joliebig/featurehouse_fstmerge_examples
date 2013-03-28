

package net.sf.freecol.client.gui.animation;

import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.OutForAnimationCallback;
import net.sf.freecol.common.io.sza.AnimationEvent;
import net.sf.freecol.common.io.sza.ImageAnimationEvent;
import net.sf.freecol.common.io.sza.SimpleZippedAnimation;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Unit;



public final class UnitImageAnimation {
    
    private final Canvas canvas;
    private final Unit unit;
    private final SimpleZippedAnimation animation;
    private final Location currentLocation;
    
    private static final Integer UNIT_LABEL_LAYER = JLayeredPane.DEFAULT_LAYER;
    
    
    
    public UnitImageAnimation(Canvas canvas, Unit unit,
                              SimpleZippedAnimation animation) {
        this.canvas = canvas;
        this.unit = unit;
        this.currentLocation = unit.getLocation();
        this.animation = animation;
    }
    

    
    public void animate() {
        final GUI gui = canvas.getGUI();
        if (gui.getTilePosition(unit.getTile()) == null) {
            return;
        }
        
        canvas.paintImmediately(canvas.getBounds());
        gui.executeWithUnitOutForAnimation(unit, unit.getTile(), new OutForAnimationCallback() {
            public void executeWithUnitOutForAnimation(final JLabel unitLabel) {
                for (AnimationEvent event : animation) {
                    long time = System.nanoTime();
                    if (event instanceof ImageAnimationEvent) {
                        final ImageAnimationEvent ievent = (ImageAnimationEvent) event;
                        final ImageIcon icon = (ImageIcon) unitLabel.getIcon();
                        icon.setImage(ievent.getImage());
                        canvas.paintImmediately(getDirtyAnimationArea());

                        time = ievent.getDurationInMs() - (System.nanoTime() - time) / 1000000;
                        if (time > 0) {
                            try {
                                Thread.sleep(time);
                            } catch (InterruptedException ex) {
                                
                            }
                        }
                    }
                }             
            }
        });
    }
    
    protected Rectangle getDirtyAnimationArea() {
        return canvas.getGUI().getTileBounds(currentLocation.getTile());
    }
}
