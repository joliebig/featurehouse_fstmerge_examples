
package org.openscience.jmol.app.jmolpanel;

import java.io.PipedOutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.OutputStream;
import java.io.IOException;

public class LoopedStreams {

  PipedOutputStream pipedOS = new PipedOutputStream();
  boolean keepRunning = true;
  ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {

    public void close() {

      keepRunning = false;
      try {
        super.close();
        pipedOS.close();
      } catch (IOException e) {

        
        
        System.exit(1);
      }
    }
  };

  private PipedInputStream pipedIS = new PipedInputStream() {

    public void close() {

      keepRunning = false;
      try {
        super.close();
      } catch (IOException e) {

        
        
        System.exit(1);
      }
    }
  };

  public LoopedStreams() throws IOException {
    pipedOS.connect(pipedIS);
    startByteArrayReaderThread();
  }    

  public InputStream getInputStream() {
    return pipedIS;
  }    

  public OutputStream getOutputStream() {
    return byteArrayOS;
  }    

  private void startByteArrayReaderThread() {

    new Thread(new Runnable() {

      public void run() {

        while (keepRunning) {

          
          if (byteArrayOS.size() > 0) {
            byte[] buffer = null;
            synchronized (byteArrayOS) {
              buffer = byteArrayOS.toByteArray();
              byteArrayOS.reset();    
            }
            try {

              
              
              pipedOS.write(buffer, 0, buffer.length);
            } catch (IOException e) {

              
              
              System.exit(1);
            }
          } else {                    
            try {

              
              
              Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
          }
        }
      }
    }).start();
  }    
}      




