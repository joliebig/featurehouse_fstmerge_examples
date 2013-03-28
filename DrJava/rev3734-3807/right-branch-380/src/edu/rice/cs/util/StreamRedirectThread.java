package edu.rice.cs.util;

import java.io.*;


public class StreamRedirectThread extends Thread {
    
    private final Reader in;

    
    private final Writer out;

    
    private static final int BUFFER_SIZE = 2048;

    
    public StreamRedirectThread(String name, InputStream in, OutputStream out) {
        super(name);
        this.in = new InputStreamReader(in);
        this.out = new OutputStreamWriter(out);
        setPriority(Thread.MAX_PRIORITY - 1);
    }

    
    public void run() {
        try {
            char[] cbuf = new char[BUFFER_SIZE];
            int count;
            while ((count = in.read(cbuf, 0, BUFFER_SIZE)) >= 0) {
                out.write(cbuf, 0, count);
                out.flush();
            }
            out.flush();
        }
        catch (IOException exc) {
          
        }
    }
}
