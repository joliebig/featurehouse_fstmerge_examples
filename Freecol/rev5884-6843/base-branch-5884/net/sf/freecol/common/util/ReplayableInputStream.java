


package net.sf.freecol.common.util;


import java.io.IOException;
import java.io.InputStream;



public final class ReplayableInputStream extends InputStream {

    private static final  int  NORMAL    = 0;
    private static final  int  RECORDING = 1;
    private static final  int  REPLAYING = 2;

    private final  InputStream  underlyingStream;

    private  int     state;
    private  byte[]  replayBuffer;
    private  int     recordedBytes;
    private  int     replayedBytes;


    public ReplayableInputStream( InputStream in ) {

        underlyingStream = in;
    }


    public int read() throws IOException {

        int  i;
        if ( REPLAYING == state  &&  replayedBytes < recordedBytes) {

            i = replayBuffer[replayedBytes ++];
        }
        else {
            i = underlyingStream.read();
            
            
            if (i != -1  &&  RECORDING == state) {
                
                if ( recordedBytes < replayBuffer.length) {
                    replayBuffer[recordedBytes ++] = (byte) i;
                }
                else {
                    
                    state = NORMAL;
                }
            }
        }
        return i;
    }


    public boolean markSupported() {

        return true;
    }


    public synchronized void mark( int readlimit) {

        replayBuffer = new byte[readlimit];
        state = RECORDING;
        recordedBytes = 0;
    }


    public synchronized void reset() throws IOException {

        if ( state != RECORDING) {
            throw new IOException( "no mark set" );
        }
        state = REPLAYING;
        replayedBytes = 0;
    }

}
