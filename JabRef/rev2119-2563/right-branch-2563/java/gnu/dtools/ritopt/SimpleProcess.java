package gnu.dtools.ritopt;



import java.io.*;



public class SimpleProcess extends Process {

    

    private Process process;

    

    private InputStream processInput;

    

    private PrintStream yourOutput;

    

    private PrintStream yourError;

    

    private StreamPrinter in, out, error;

    

    public SimpleProcess( Process process ) throws IOException {
	this( process, System.in, System.out, System.err );
    }

    

    public SimpleProcess( Process process, InputStream processInput,
			  PrintStream yourOutput, PrintStream yourError )
                         throws IOException {
	super();
	this.process = process;
	this.processInput = processInput;
	this.yourOutput = yourOutput;
	this.yourError = yourError;
    }

    

    public OutputStream getOutputStream() {
	return process.getOutputStream();
    }

    

    public InputStream getInputStream() {
	return process.getInputStream();
    }

    

    public InputStream getErrorStream() {
	return process.getErrorStream();
    }

    

    public int waitFor() throws InterruptedException {
	int retval = waitForImpl();
	if ( in != null ) {
	    in.stop();
	}
	return retval;
    }

    

    private int waitForImpl() throws InterruptedException {
	process = process;
	in = new StreamPrinter( processInput,
				new PrintStream( process.getOutputStream() ) );
	in.setFlush( true );
	out = new StreamPrinter( process.getInputStream(), yourOutput );
	error = new StreamPrinter( process.getErrorStream(), yourError );
	in.start();
	out.start();
	error.start();
	out.join();
	error.join();
	return process.waitFor();
    }

    

    public int exitValue() {
	return process.exitValue();
    }

    

    public void destroy() throws IllegalThreadStateException {
	process.destroy();
    }
} 
