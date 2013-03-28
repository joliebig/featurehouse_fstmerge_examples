

package net.sf.freecol.common.networking;



public class NetworkReplyObject {
    
    private Object response = null;
    private boolean responseGiven = false;
    private int networkReplyId;
    private boolean streamed;
    
    
    
    
    public NetworkReplyObject(int networkReplyId, boolean streamed) {
        this.networkReplyId = networkReplyId;
        this.streamed = streamed;
    }
    

    
    public boolean isStreamed() {
        return streamed;
    }
    
    
    public synchronized void setResponse(Object response) {
        if (response == null) {
            throw new NullPointerException();
        }
        this.response = response;
        this.responseGiven = true;
        notify();
    }
    
    
    
    public int getNetworkReplyId() {
        return networkReplyId;
    }

    
    
    public synchronized Object getResponse() {
        if (response == null) {
            try {
                while (!responseGiven) {
                    wait();
                }
            } catch (InterruptedException ie) {}
        }

       return response;
    }

    
    public synchronized void interrupt() {
        responseGiven = true;
        notify();
    }
}
