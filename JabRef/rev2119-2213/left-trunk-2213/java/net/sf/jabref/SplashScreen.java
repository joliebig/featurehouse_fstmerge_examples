package net.sf.jabref;


import java.awt.*;
import java.net.URL;



public class SplashScreen extends Window {
    private Image splashImage;
    private boolean paintCalled = false;
    
    public SplashScreen(Frame owner) {
        super(owner);
        URL imageURL = SplashScreen.class.getResource("/images/splash.png");
        splashImage = Toolkit.getDefaultToolkit().createImage(imageURL);

        
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(splashImage,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie) {}

                                                                                
        
        int imgWidth = splashImage.getWidth(this);
        int imgHeight = splashImage.getHeight(this);  

        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
            (screenDim.width - imgWidth) / 2,
            (screenDim.height - imgHeight) / 2
        );

    }
    
    
    
    public void update(Graphics g) {
        
        
        
        

        g.setColor(getForeground());
        paint(g);
    }
    

    public void paint(Graphics g) {
        g.drawImage(splashImage, 0, 0, this);

        
        
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    
      

    public static Frame splash() {
        Frame f = new Frame();
        SplashScreen w = new SplashScreen(f);

        
        w.setVisible(true);
        w.toFront();

        
        
        

        
        
        
        if (! EventQueue.isDispatchThread()) {
            synchronized (w) {
                if (! w.paintCalled) {
                    try { 
                        w.wait(5000);
                    } catch (InterruptedException e) {}
                }
            }
        }
        return f;
    }
}
