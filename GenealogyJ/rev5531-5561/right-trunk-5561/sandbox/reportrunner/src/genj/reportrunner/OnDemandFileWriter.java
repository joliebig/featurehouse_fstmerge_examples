package genj.reportrunner;

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;


public class OnDemandFileWriter extends FilterWriter
{
	
	private String filename;

	
	private boolean open = false;

	
	public OnDemandFileWriter(String filename)
	{
		super(new CharArrayWriter()); 
		this.filename = filename;
	}

	
	private void ensureOpen() throws IOException
	{
		if (!open)
		{
			out = new FileWriter(filename);
			open = true;
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		ensureOpen();
		out.write(cbuf, off, len);
	}

	@Override
	public void write(int c) throws IOException {
		ensureOpen();
		out.write(c);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		ensureOpen();
		out.write(str, off, len);
	}
}
