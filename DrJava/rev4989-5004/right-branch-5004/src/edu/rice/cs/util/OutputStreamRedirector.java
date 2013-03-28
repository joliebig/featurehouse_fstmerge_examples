

package edu.rice.cs.util;

import java.io.*;


public abstract class OutputStreamRedirector extends OutputStream {
  
  public final void write(int b) { write(new byte[] { (byte) b }, 0, 1); }

  public final void write(byte[] b) { print(new String(b)); }

  public final void write(byte[] b, int off, int len) { print(new String(b, off, len)); }

  
  public abstract void print(String s);
}
