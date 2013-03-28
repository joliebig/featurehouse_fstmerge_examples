

package org.jmol.api;

import java.util.Hashtable;

public interface JmolStatusListener extends JmolCallbackListener {


  public String eval(String strEval);
  
  
  public float[][] functionXY(String functionName, int x, int y);
  
  
  public float[][][] functionXYZ(String functionName, int nx, int ny, int nz);

  
  public String createImage(String fileName, String type, Object text_or_bytes, int quality);

  public Hashtable getRegistryInfo();

  public void showUrl(String url);

  public String dialogAsk(String type, String fileName);

}
