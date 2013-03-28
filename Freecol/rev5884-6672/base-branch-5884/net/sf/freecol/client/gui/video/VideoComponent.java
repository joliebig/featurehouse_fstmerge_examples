

package net.sf.freecol.client.gui.video;

import java.awt.Image;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.panel.FreeColImageBorder;
import net.sf.freecol.common.resources.ResourceManager;

import com.fluendo.player.Cortado;
import com.fluendo.player.StopListener;


public class VideoComponent extends JPanel {

    private final Cortado applet;
    private List<VideoListener> videoListeners = new LinkedList<VideoListener>();

    
    public VideoComponent(Video video, boolean mute) {
        final String url = video.getURL().toExternalForm();
        
        setBorder(createBorder());
        final Insets insets = getInsets();
        
        applet = new Cortado();
        applet.setSize(655, 480);
        
        applet.setLocation(insets.left - 1, insets.top - 1);
        
        applet.setParam ("url", url);
        applet.setParam ("local", "false");
        applet.setParam ("framerate", "60");
        applet.setParam ("keepaspect", "true");
        applet.setParam ("video", "true");
        String withAudio = "true";
        if(mute){
            withAudio = "false";
        }
        applet.setParam ("audio", withAudio);
        applet.setParam ("kateIndex", "0");
        applet.setParam ("bufferSize", "200");
        applet.setParam ("showStatus", "hide");
        applet.init();

        applet.setStopListener(new StopListener() {
            public void stopped() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (VideoListener sl : videoListeners) {
                            sl.stopped();
                        }
                    }
                });
            }
        });
        
        setLayout(null);
        add(applet);

        
        setSize(applet.getWidth() + insets.left + insets.right - 2,
                applet.getHeight() + insets.top + insets.bottom - 2);
    }
    
    
    public void addVideoListener(VideoListener videoListener) {
        videoListeners.add(videoListener);
    }
    
    
    public void removeVideoListener(VideoListener videoListener) {
        videoListeners.remove(videoListener);
    }
    
    @Override
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        applet.addMouseListener(l);
    }
    
    @Override
    public void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        applet.removeMouseListener(l);
    }
    
    
    public void play() {
        applet.start();
    }
    
    
    public void stop() {
        applet.stop();
    }
    
    private Border createBorder() {
        final Image menuborderN = ResourceManager.getImage("menuborder.n.image");
        final Image menuborderNW = ResourceManager.getImage("menuborder.nw.image");
        final Image menuborderNE = ResourceManager.getImage("menuborder.ne.image");
        final Image menuborderW = ResourceManager.getImage("menuborder.w.image");
        final Image menuborderE = ResourceManager.getImage("menuborder.e.image");
        final Image menuborderS = ResourceManager.getImage("menuborder.s.image");
        final Image menuborderSW = ResourceManager.getImage("menuborder.sw.image");
        final Image menuborderSE = ResourceManager.getImage("menuborder.se.image");
        final FreeColImageBorder imageBorder = new FreeColImageBorder(menuborderN, menuborderW, menuborderS,
                menuborderE, menuborderNW, menuborderNE, menuborderSW, menuborderSE);
        return imageBorder;
    }
}
