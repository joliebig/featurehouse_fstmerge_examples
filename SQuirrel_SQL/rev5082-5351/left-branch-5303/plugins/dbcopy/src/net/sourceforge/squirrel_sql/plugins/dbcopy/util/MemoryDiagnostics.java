package net.sourceforge.squirrel_sql.plugins.dbcopy.util;





public class MemoryDiagnostics implements Runnable {

    Thread t = null;
    
    private volatile boolean shutdown = false;
    
    private static int sleepTimeMills = 10000;
    
    public MemoryDiagnostics() {
        t = new Thread(this);
        t.setName("MemoryDiagnosticsThread");
        t.start();
    }
    
    
    
    public void run() {
        while (!shutdown) {
            printMemoryUsage();
            gc();
            try {
                Thread.sleep(sleepTimeMills);
            } catch (InterruptedException e) {
                
            }
        }
        
    }

    public void printMemoryUsage() {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long max = Runtime.getRuntime().maxMemory();
        System.out.println("MemoryDiagnostics.printMemoryUsage: Total="+total);
        System.out.println("MemoryDiagnostics.printMemoryUsage: Free="+free);
        System.out.println("MemoryDiagnostics.printMemoryUsage: Max="+max);
        if (total > (max/2)) {
            System.out.println("Memory allocation > 50%, running GC");
            gc();
        }
    }
    
    public void gc() {
        System.gc();
    }
    
    public void shutdown() {
        t.interrupt();
    }
    
}
