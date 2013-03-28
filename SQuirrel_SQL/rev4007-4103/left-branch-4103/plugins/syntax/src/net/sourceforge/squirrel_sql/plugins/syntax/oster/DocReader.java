
package net.sourceforge.squirrel_sql.plugins.syntax.oster;

import java.io.Reader;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

class DocumentReader extends Reader
{

	
	public void update(int position, int adjustment)
	{
		if (position < this.position)
		{
			if (this.position < position - adjustment)
			{
				this.position = position;
			}
			else
			{
				this.position += adjustment;
			}
		}
	}

	
	private long position = 0;

	
	private long mark = -1;

	
	private AbstractDocument document;

	
	public DocumentReader(AbstractDocument document)
	{
		this.document = document;
	}

	
	public void close()
	{
	}

	
	public void mark(int readAheadLimit)
	{
		mark = position;
	}

	
	public boolean markSupported()
	{
		return true;
	}

	
	public int read()
	{
		if (position < document.getLength())
		{
			try
			{
				char c = document.getText((int) position, 1).charAt(0);
				position++;
				return c;
			}
			catch (BadLocationException x)
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	
	public int read(char[] cbuf)
	{
		return read(cbuf, 0, cbuf.length);
	}

	
	public int read(char[] cbuf, int off, int len)
	{
		if (position < document.getLength())
		{
			int length = len;
			if (position + length >= document.getLength())
			{
				length = document.getLength() - (int) position;
			}
			if (off + length >= cbuf.length)
			{
				length = cbuf.length - off;
			}
			try
			{
				String s = document.getText((int) position, length);
				position += length;
				for (int i = 0; i < length; i++)
				{
					cbuf[off + i] = s.charAt(i);
				}
				return length;
			}
			catch (BadLocationException x)
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	
	public boolean ready()
	{
		return true;
	}

	
	public void reset()
	{
		if (mark == -1)
		{
			position = 0;
		}
		else
		{
			position = mark;
		}
		mark = -1;
	}

	
	public long skip(long n)
	{
		if (position + n <= document.getLength())
		{
			position += n;
			return n;
		}
		else
		{
			long oldPos = position;
			position = document.getLength();
			return (document.getLength() - oldPos);
		}
	}

	
	public void seek(long n)
	{
		if (n <= document.getLength())
		{
			position = n;
		}
		else
		{
			position = document.getLength();
		}
	}
}