package net.sourceforge.squirrel_sql.client.util;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;


public final class EventDispatchThreadHangMonitor extends EventQueue {
    private static final EventQueue INSTANCE = new EventDispatchThreadHangMonitor();
    
    
    private static final long CHECK_INTERVAL_MS = 100;
    
    
    private static final long UNREASONABLE_DISPATCH_DURATION_MS = 500;
    
    
    
    private static final long NO_CURRENT_EVENT = 0;
    
    
    private long startedLastEventDispatchAt = NO_CURRENT_EVENT;
    
    
    private boolean reportedHang = false;
    
    
    private Thread eventDispatchThread = null;
    
    private EventDispatchThreadHangMonitor() {
        initTimer();
    }
    
    
    private void initTimer() {
        final long initialDelayMs = 0;
        final boolean isDaemon = true;
        Timer timer = new Timer("EventDispatchThreadHangMonitor", isDaemon);
        timer.schedule(new HangChecker(), initialDelayMs, CHECK_INTERVAL_MS);
    }
    
    private class HangChecker extends TimerTask {
        @Override
        public void run() {
            
            
            synchronized (INSTANCE) {
                checkForHang();
            }
        }
        
        private void checkForHang() {
            if (startedLastEventDispatchAt == NO_CURRENT_EVENT) {
                
                
                
                return;
            }
            if (timeSoFar() > UNREASONABLE_DISPATCH_DURATION_MS) {
                reportHang();
            }
        }
        
        private void reportHang() {
            if (reportedHang) {
                
                return;
            }
            
            reportedHang = true;
            System.out.println("--- event dispatch thread stuck processing event for " +  timeSoFar() + " ms:");
            StackTraceElement[] stackTrace = eventDispatchThread.getStackTrace();
            printStackTrace(System.out, stackTrace);
        }
        
        private void printStackTrace(PrintStream out, StackTraceElement[] stackTrace) {
            
            
            
            final String ourEventQueueClassName = EventDispatchThreadHangMonitor.class.getName();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName().equals(ourEventQueueClassName)) {
                    return;
                }
                out.println("    " + stackTraceElement);
            }
        }
    }
    
    
    private long timeSoFar() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startedLastEventDispatchAt);
    }
    
    
    public static void initMonitoring() {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(INSTANCE);
    }
    
    
    @Override
    protected void dispatchEvent(AWTEvent event) {
        preDispatchEvent();
        super.dispatchEvent(event);
        postDispatchEvent();
    }
    
    
    private synchronized void preDispatchEvent() {
        if (eventDispatchThread == null) {
            
            
            
            eventDispatchThread = Thread.currentThread();
        }
        
        reportedHang = false;
        startedLastEventDispatchAt = System.currentTimeMillis();
    }
    
    
    private synchronized void postDispatchEvent() {
        if (reportedHang) {
            System.out.println("--- event dispatch thread unstuck after " + timeSoFar() + " ms.");
        }
        startedLastEventDispatchAt = NO_CURRENT_EVENT;
    }
}