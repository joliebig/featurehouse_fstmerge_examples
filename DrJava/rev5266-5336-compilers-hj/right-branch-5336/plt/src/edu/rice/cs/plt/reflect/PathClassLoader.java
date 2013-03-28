

package edu.rice.cs.plt.reflect;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class PathClassLoader extends AbstractClassLoader {
  
  
  public static URL getResourceInPath(String name, File... path) {
    return getResourceInPath(name, IterUtil.asIterable(path));
  }

  
  public static URL getResourceInPath(String name, Iterable<File> path) {
    return new PathClassLoader(EmptyClassLoader.INSTANCE, path).getResource(name);
  }

  
  public static InputStream getResourceInPathAsStream(String name, File... path) {
    return getResourceInPathAsStream(name, IterUtil.asIterable(path));
  }

  
  public static InputStream getResourceInPathAsStream(String name, Iterable<File> path) {
    return new PathClassLoader(EmptyClassLoader.INSTANCE, path).getResourceAsStream(name);
  }
  

  private final Iterable<? extends File> _path;
  private URLClassLoader _urlLoader;
  private Iterable<File> _urlLoaderPath;

  
  public PathClassLoader(File... path) { this(IterUtil.asIterable(path)); }
  
  
  public PathClassLoader(Iterable<? extends File> path) {
    super();
    _path = path;
    updateURLLoader();
  }
  
  
  public PathClassLoader(ClassLoader parent, File... path) { this(parent, IterUtil.asIterable(path)); }
  
  
  public PathClassLoader(ClassLoader parent, Iterable<? extends File> path) {
    super(parent);
    _path = path;
    updateURLLoader();
  }

  private void updateURLLoader() {
    _urlLoaderPath = IterUtil.snapshot(_path);
    List<URL> urls = new LinkedList<URL>();
    for (File f : _urlLoaderPath) {
      try { urls.add(f.toURI().toURL()); }
      catch (IllegalArgumentException e) { error.log(e); }
      catch (MalformedURLException e) { error.log(e); }
      
    }
    _urlLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), EmptyClassLoader.INSTANCE);
  }
  
  @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
    URL resource = findResource(name.replace('.', '/') + ".class");
    if (resource == null) { throw new ClassNotFoundException(); }
    else {
      try {
        InputStream stream = resource.openStream();
        try {
          byte[] bytes = IOUtil.toByteArray(stream);
          Class<?> result = defineClass(name, bytes, 0, bytes.length);
          definePackageForClass(name);
          return result;
        }
        finally { stream.close(); }
      }
      catch (IOException e) { throw new ClassNotFoundException("Can't access class file", e); }
    }
  }
  
  @Override protected URL findResource(String name) {
    if (!IterUtil.isEqual(_path, _urlLoaderPath)) { updateURLLoader(); }
    return _urlLoader.findResource(name);
  }
  
  @Override protected Enumeration<URL> findResources(String name) throws IOException {
    if (!IterUtil.isEqual(_path, _urlLoaderPath)) { updateURLLoader(); }
    return _urlLoader.findResources(name);
  }
  
}
