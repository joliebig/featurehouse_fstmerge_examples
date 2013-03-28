

package net.sf.freecol.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;


public final class Worker extends Thread {



    private final LinkedBlockingQueue<Runnable> jobList;

    private volatile boolean stopRunning;

    private static final Logger logger = Logger.getLogger(Worker.class.getName());
    
    public Worker() {
        super(FreeCol.CLIENT_THREAD+"Worker");
        jobList = new LinkedBlockingQueue<Runnable>();
        stopRunning = false;
    }

    @Override
    public void run() {
        while (!stopRunning) {
            try {
                
                Runnable job = jobList.take();
                try {
                    job.run();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Worker task failed!", e);
                }
            } catch (InterruptedException e) {
                logger.log(Level.INFO, "Worker interrupted, aborting!");
            }
        }
    }

    
    public void schedule(Runnable job) {
        jobList.add(job);
    }

    
    public void askToStop() {
        stopRunning = true;
        this.interrupt();
    }
}
