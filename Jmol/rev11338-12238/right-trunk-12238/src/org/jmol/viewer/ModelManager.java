
package org.jmol.viewer;

import org.jmol.modelset.ModelLoader;
import org.jmol.modelset.ModelSet;

class ModelManager {

  private final Viewer viewer;
  private ModelLoader modelLoader;

  private String fullPathName;
  private String fileName;

  ModelManager(Viewer viewer) {
    this.viewer = viewer;
  }

  ModelSet zap() {
    fullPathName = fileName = null;
    modelLoader = new ModelLoader(viewer, "empty");
    return (ModelSet) modelLoader;
  }
  
  String getModelSetFileName() {
    return fileName == null ? "zapped" : fileName;
  }

  String getModelSetPathName() {
    return fullPathName;
  }

  ModelSet createModelSet(String fullPathName, String fileName,
                          Object atomSetCollection, boolean isAppend) {
    
    if (isAppend) {
      if (atomSetCollection != null)
        modelLoader = new ModelLoader(viewer, atomSetCollection, modelLoader,
            "merge");
    } else if (atomSetCollection == null) {
      return zap();
    } else {
      this.fullPathName = fullPathName;
      this.fileName = fileName;
      String modelSetName = viewer.getModelAdapter().getAtomSetCollectionName(
          atomSetCollection);
      if (modelSetName != null) {
        modelSetName = modelSetName.trim();
        if (modelSetName.length() == 0)
          modelSetName = null;
      }
      if (modelSetName == null)
        modelSetName = reduceFilename(fileName);
      modelLoader = new ModelLoader(viewer, atomSetCollection, null,
          modelSetName);
    }
    if (modelLoader.getAtomCount() == 0)
      zap();
    return (ModelSet) modelLoader;
  }

  private static String reduceFilename(String fileName) {
    if (fileName == null)
      return null;
    int ichDot = fileName.indexOf('.');
    if (ichDot > 0)
      fileName = fileName.substring(0, ichDot);
    if (fileName.length() > 24)
      fileName = fileName.substring(0, 20) + " ...";
    return fileName;
  }

}
