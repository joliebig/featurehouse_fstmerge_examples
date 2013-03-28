
package genj.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public abstract class InputSource {
  
  private String name;
  
  protected InputSource(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public abstract InputStream open() throws IOException;
  
  public static InputSource get(File file) {
    return get(file.getName(), file);
  }

  public static InputSource get(String name, File file) {
    return new FileInput(name, file);
  }
  
  public static InputSource get(String name, byte[] bytes) {
    return new ByteInput(name, bytes);
  }
  
  public static class FileInput extends InputSource {
    
    private File file;

    public FileInput(File file) {
      this(file.getName(), file);
    }
    public FileInput(String name, File file) {
      super(name);
      this.file = file;
    }
    
    public File getFile() {
      return file;
    }
    
    @Override
    public InputStream open() throws IOException {
      return new FileInputStream(file);
    }
    
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FileInput))
        return false;
      FileInput that = (FileInput)obj;
      return that.file.equals(this.file) && that.getName().equals(this.getName());
    }
    
    @Override
    public int hashCode() {
      return file.hashCode();
    }
    
    @Override
    public String toString() {
      return "file name="+getName()+" file="+file.toString();
    }
    
  }
  
  public static class ByteInput extends InputSource {
    
    private byte[] bytes;

    public ByteInput(String name, byte[] bytes) {
      super(name);
      this.bytes = bytes;
    }
    
    @Override
    public InputStream open() {
      return new ByteArrayInputStream(bytes);
    }
    
    @Override
    public boolean equals(Object obj) {
      return obj instanceof ByteInput && ((ByteInput)obj).bytes.equals(bytes);
    }
    
    @Override
    public int hashCode() {
      return bytes.hashCode();
    }
    
    @Override
    public String toString() {
      return "byte array size="+bytes.length+" name="+getName();
    }
    
  }
  
}
