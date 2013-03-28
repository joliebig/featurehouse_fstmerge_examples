package gnu.dtools.ritopt;



import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;



public class StreamPrinter implements Runnable, Stoppable {

    

    private InputStream stream;

    

    private PrintStream out;

    

    private Stoppable stop;

    

    private boolean stopped;

    

    private boolean flush;

    

    private Thread thread;

    

    public StreamPrinter( InputStream s, PrintStream p ) {
	stream = s;
	out = p;
	thread = new Thread( this );
    }

    

    public void start() throws InterruptedException {
	thread.start();
    }

    

    public void setStop( Stoppable tostop ) {
	synchronized( this ) {
	    stop = tostop;
	}
    }

    

    public boolean isStopped() {
	return stopped;
    }

    

    public void setFlush( boolean flush ) {
	synchronized ( this ) {
	    this.flush = flush;
	}
    }

    

    public void stop() {
	synchronized( this ) {
	    stopped = true;
	}
	if ( stop != null ) {
	    synchronized( stop ) {
		if ( !stop.isStopped() ) {
		    stop.stop();
		}
	    }
	}
    }

    

    public void join() throws InterruptedException {
	thread.join();
    }

    

    
    public void run() {
	int buf;
	try {
	    boolean me;
	    while ( !stopped && ( buf = stream.read() ) != -1 ) {
		synchronized( this ) {
		    me = flush;
		}
		synchronized( out ) {
		    out.print( (char)buf );
		    if ( me ) out.flush();
		}
	    }
	}
	catch ( IOException e ) {
	    out.println( "I/O error" );
	}
	finally {
	    synchronized( out ) {
		out.flush();
	    }
	    stop();
	}
    }
}
