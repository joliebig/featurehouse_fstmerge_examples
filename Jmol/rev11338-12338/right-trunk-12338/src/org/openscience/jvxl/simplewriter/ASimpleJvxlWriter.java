
package org.openscience.jvxl.simplewriter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.readers.Parameters;

public class ASimpleJvxlWriter {

  
  

  public static void main(String[] args) {

    

    Parameters params = new Parameters();
    String outputFile = "c:/temp/simple.jvxl";
    params.cutoff = 0.01f;
    params.isCutoffAbsolute = false;
    
    int nX = 31;
    int nY = 31;
    int nZ = 31;

    String[] title = new String[] {"created by SimpleJvxlWriter "
        + new SimpleDateFormat("yyyy-MM-dd', 'HH:mm").format(new Date()) };

    VolumeData volumeData;
    VoxelDataCreator vdc;
    volumeData = new VolumeData();
    volumeData.setVolumetricOrigin(0, 0, 0);
    volumeData.setVolumetricVector(0, 1f, 0f, 0f);
    volumeData.setVolumetricVector(1, 0f, 1f, 0f);
    volumeData.setVolumetricVector(2, 0f, 0f, 1f);
    volumeData.setVoxelCounts(nX, nY, nZ);

    vdc = new VoxelDataCreator(volumeData);
    vdc.createVoxelData();

    
    
    
    float[] areaVolumeReturn = new float[2]; 
    Vector surfacePointsReturn = new Vector(); 

    params.isXLowToHigh = false;
    writeFile(outputFile + "A", jvxlGetData(null, params,
        volumeData, title, surfacePointsReturn, areaVolumeReturn));

    if (areaVolumeReturn != null)
      System.out.println("calculated area = " + areaVolumeReturn[0] 
                         + " volume = " + areaVolumeReturn[1]
                         + " for " + surfacePointsReturn.size() 
                         + " surface points");
    
    volumeData.setVoxelData(null);
    params.isXLowToHigh = true;
    writeFile(outputFile + "B", jvxlGetData(vdc, params,
        volumeData, title, surfacePointsReturn, areaVolumeReturn));

    System.out.flush();
    System.exit(0);
  }

  public static String jvxlGetData(VoxelDataCreator vdc, Parameters params,
                                   VolumeData volumeData, String[] title,
                                   Vector surfacePointsReturn,
                                   float[] areaVolumeReturn) {
    
    JvxlData jvxlData = new JvxlData();
    new SimpleMarchingCubes(vdc, volumeData, params,
        jvxlData, surfacePointsReturn, areaVolumeReturn);
    jvxlData.isXLowToHigh = params.isXLowToHigh;
    jvxlData.cutoff = params.cutoff;
    jvxlData.isCutoffAbsolute = params.isCutoffAbsolute;
    jvxlData.version = "ASimpleJvxlWriter -- version 2.2";
    return JvxlCoder.jvxlGetFile(volumeData, jvxlData, title);
  }

  static void writeFile(String fileName, String text) {
    try {
      FileOutputStream os = new FileOutputStream(fileName);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os), 8192);
      bw.write(text);
      bw.close();
      os = null;
    } catch (IOException e) {
      System.out.println("IO Exception: " + e.toString());
    }
  }

}