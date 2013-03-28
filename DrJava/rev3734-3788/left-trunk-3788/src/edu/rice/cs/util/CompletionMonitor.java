package edu.rice.cs.util;


public class CompletionMonitor {
    private boolean _flag;

    public CompletionMonitor(boolean flag) { _flag = flag; }

    public CompletionMonitor() { this(false); }

    
    public synchronized boolean isFlag() { return _flag; }
    
    
    synchronized public void set() {
        _flag = true;
        this.notifyAll();
    }

    
    synchronized public void reset() { _flag = false; }

    
    synchronized public boolean waitOne() {
        while (!_flag) {
            try { this.wait(); } 
            catch (InterruptedException e) { return false; }
        }
        return true;
    }
}
