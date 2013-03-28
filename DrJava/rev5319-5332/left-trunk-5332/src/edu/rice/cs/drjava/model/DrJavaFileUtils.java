

package edu.rice.cs.drjava.model;

import java.io.File;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.drjava.config.OptionConstants;


public class DrJavaFileUtils  {
  
  public static boolean isSourceFile(String fileName) {
    return fileName.endsWith(OptionConstants.JAVA_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.DJ_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ0_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ1_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ2_FILE_EXTENSION);
  }
  
  
  public static boolean isSourceFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return isSourceFile(fileName);
  }
  
  
  public static boolean isLLFile(String fileName) {
    return fileName.endsWith(OptionConstants.DJ_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ0_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ1_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ2_FILE_EXTENSION);
  }
  
  
  public static boolean isLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return isLLFile(fileName);
  }
  
  
  public static boolean isOldLLFile(String fileName) {
    return fileName.endsWith(OptionConstants.OLD_DJ0_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ1_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.OLD_DJ2_FILE_EXTENSION);
  }
  
  
  public static boolean isOldLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return isOldLLFile(fileName);
  }
  
  
  public static boolean isOldProjectFile(String fileName) {
    return fileName.endsWith(OptionConstants.OLD_PROJECT_FILE_EXTENSION);
  }
  
  
  public static boolean isOldProjectFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return isOldProjectFile(fileName);
  }
  
  
  public static boolean isProjectFile(String fileName) {
    return fileName.endsWith(OptionConstants.PROJECT_FILE_EXTENSION)
      || fileName.endsWith(OptionConstants.PROJECT_FILE_EXTENSION2)
      || fileName.endsWith(OptionConstants.OLD_PROJECT_FILE_EXTENSION);
  }
  
  
  public static boolean isProjectFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return isProjectFile(fileName);
  }

  
  public static String getJavaForLLFile(String fileName) {
    if (fileName.endsWith(OptionConstants.DJ_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.DJ_FILE_EXTENSION))
        + OptionConstants.JAVA_FILE_EXTENSION;
    }
    else if (fileName.endsWith(OptionConstants.OLD_DJ0_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ0_FILE_EXTENSION))
        + OptionConstants.JAVA_FILE_EXTENSION;
    }
    else if (fileName.endsWith(OptionConstants.OLD_DJ1_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ1_FILE_EXTENSION))
        + OptionConstants.JAVA_FILE_EXTENSION;
    }
    else if (fileName.endsWith(OptionConstants.OLD_DJ2_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ2_FILE_EXTENSION))
        + OptionConstants.JAVA_FILE_EXTENSION;
    }
    else return fileName;
  }
  
  
  public static File getJavaForLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return new File(getJavaForLLFile(fileName));
  }
  
  
  public static File getDJForJavaFile(File f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.DJ_FILE_EXTENSION);
  }

  
  public static File getDJ0ForJavaFile(File f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ0_FILE_EXTENSION);
  }
  
  
  public static File getDJ1ForJavaFile(File f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ1_FILE_EXTENSION);
  }
  
  
  public static File getDJ2ForJavaFile(File f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ2_FILE_EXTENSION);
  }
  
  
  public static String getDJForJavaFile(String f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.DJ_FILE_EXTENSION);
  }

  
  public static String getDJ0ForJavaFile(String f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ0_FILE_EXTENSION);
  }
  
  
  public static String getDJ1ForJavaFile(String f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ1_FILE_EXTENSION);
  }
  
  
  public static String getDJ2ForJavaFile(String f) {
    return getFileWithDifferentExt(f, OptionConstants.JAVA_FILE_EXTENSION, OptionConstants.OLD_DJ2_FILE_EXTENSION);
  }
  
  
  public static String getNewLLForOldLLFile(String fileName) {
    if (fileName.endsWith(OptionConstants.OLD_DJ0_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ0_FILE_EXTENSION))
        + OptionConstants.DJ_FILE_EXTENSION;
    }
    else if (fileName.endsWith(OptionConstants.OLD_DJ1_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ1_FILE_EXTENSION))
        + OptionConstants.DJ_FILE_EXTENSION;
    }
    else if (fileName.endsWith(OptionConstants.OLD_DJ2_FILE_EXTENSION)) {
      return fileName.substring(0, fileName.lastIndexOf(OptionConstants.OLD_DJ2_FILE_EXTENSION))
        + OptionConstants.JAVA_FILE_EXTENSION;
    }
    else return fileName;
  }
  
  
  public static File getNewLLForOldLLFile(File f) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return new File(getNewLLForOldLLFile(fileName));
  }

  
  public static String getFileWithDifferentExt(String fileName, String source, String dest) {
    if (fileName.endsWith(source)) {
      return fileName.substring(0, fileName.lastIndexOf(source)) + dest;
    }
    else return fileName;
  }

  
  public static File getFileWithDifferentExt(File f, String source, String dest) {
    File canonicalFile = IOUtil.attemptCanonicalFile(f);
    String fileName = canonicalFile.getPath();
    return new File(getFileWithDifferentExt(fileName, source, dest));
  }
  
  
  
  public static String getPackageDir(String className) {
    
    int lastDotIndex = className.lastIndexOf(".");
    if (lastDotIndex == -1) {
      
      return "";
    }
    else {
      String packageName = className.substring(0, lastDotIndex);
      packageName = packageName.replace('.', File.separatorChar);
      return packageName + File.separatorChar;
    }
  }
  
  
  public static String removeExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(".");
    if (lastDotIndex == -1) {
      
      return fileName;
    }
    return fileName.substring(0, lastDotIndex);
  }
  
  
  public static String getExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(".");
    if (lastDotIndex == -1) {
      
      return "";
    }
    return fileName.substring(lastDotIndex);
  }

}
