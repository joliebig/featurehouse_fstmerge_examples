
package org.openscience.jmol.app.webexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;

class JmolInstance {
  String name;
  String javaname;
  String script;
  int width;
  int height;
  String pictFile;
  boolean pictIsScratchFile;
  JmolViewer viewer;

  JmolInstance(JmolViewer viewer, String name, String script,
      int width, int height) {
    this.viewer = viewer;
    this.name = name;
    this.javaname = name.replaceAll("[^a-zA-Z_0-9-]", "_"); 
    this.script = script;
    this.width = width;
    this.height = height;
    
    FileSystemView Directories = FileSystemView.getFileSystemView();
    File homedir = Directories.getHomeDirectory();
    String homedirpath = homedir.getPath();
    String scratchpath = homedirpath + "/.jmol_WPM";
    File scratchfile = new File(scratchpath);
    if (!(scratchfile.exists())) {
      boolean made_scratchdir = scratchfile.mkdir();
      if (!(made_scratchdir)) {
        LogPanel.log(GT._("Attempt to make scratch directory failed."));
      }
    }
    pictFile = scratchpath + "/" + javaname + ".png";
    
    viewer.createImage(pictFile, "PNG", null, 2, width, height);
    pictIsScratchFile = true;
  }

  boolean movepict(String dirpath) throws IOException {
    String imagename = dirpath + "/" + this.javaname + ".png";
    if (pictFile.equals(imagename))
      return false;
    FileInputStream is = null;
    try {
      is = new FileInputStream(pictFile);
    } catch (IOException ise) {
      throw ise;
    }
    FileOutputStream os = null;
    try {
      os = new FileOutputStream(imagename);
      int pngbyteint = is.read();
      while (pngbyteint != -1) {
        os.write(pngbyteint);
        pngbyteint = is.read();
      }
      os.flush();
      os.close();
      is.close();
    } catch (IOException exc) {
      throw exc;
    }

    return true;
  }
  boolean delete() throws IOException {
    File scratchToErase = new File(pictFile);
    if (scratchToErase.exists() && !scratchToErase.delete())
        throw new IOException("Failed to delete scratch file " + pictFile + ".");
    
    return true;
  }
}
