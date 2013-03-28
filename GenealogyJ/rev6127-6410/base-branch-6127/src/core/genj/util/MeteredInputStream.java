
package genj.util;

import java.io.IOException;
import java.io.InputStream;


public class MeteredInputStream extends InputStream {

  private long meter = 0;
  private long marked = -1;
  private InputStream in;

  public MeteredInputStream(InputStream in) {
    this.in = in;
  }

  public long getCount() {
    return meter;
  }

  public int available() throws IOException {
    return in.available();
  }

  public void close() throws IOException {
    in.close();
  }

  public synchronized void mark(int readlimit) {
    in.mark(readlimit);
    marked = meter;
  }

  public boolean markSupported() {
    return in.markSupported();
  }

  public int read() throws IOException {
    meter++;
    return in.read();
  }

  public int read(byte[] b, int off, int len) throws IOException {
    int read = in.read(b, off, len);
    meter+=read;
    return read;
  }

  public int read(byte[] b) throws IOException {
    int read = in.read(b);
    meter+=read;
    return read;
  }

  public synchronized void reset() throws IOException {
    if (marked<0)
      throw new IOException("reset() without mark()");
    in.reset();
    meter = marked;
  }

  public long skip(long n) throws IOException {
    int skipped = (int)super.skip(n);
    meter+=skipped;
    return skipped;
  }

}