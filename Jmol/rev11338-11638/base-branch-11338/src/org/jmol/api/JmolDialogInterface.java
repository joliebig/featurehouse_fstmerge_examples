package org.jmol.api;

public interface JmolDialogInterface {

  public abstract void setupUI(boolean forceNewTranslation);

  
  public abstract String getType();

  
  public abstract int getQuality(String sType);

  public abstract String getOpenFileNameFromDialog(String appletContext,
                                                   JmolViewer viewer,
                                                   String fileName,
                                                   Object historyFile,
                                                   String windowName,
                                                   boolean allowAppend);

  public abstract String getSaveFileNameFromDialog(JmolViewer viewer,
                                                   String data, String type);

  public abstract String getImageFileNameFromDialog(JmolViewer viewer,
                                                    String fileName,
                                                    String type,
                                                    String[] imageChoices,
                                                    String[] imageExtensions,
                                                    int qualityJPG,
                                                    int qualityPNG);

}
