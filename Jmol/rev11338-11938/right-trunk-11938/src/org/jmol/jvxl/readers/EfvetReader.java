
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import javax.vecmath.Point3f;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.Logger;


class EfvetReader extends PolygonFileReader {

  EfvetReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlFileHeaderBuffer.append("efvet file format\nvertices and triangles only\n");
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
    hasColorData = true;
  }

  

  void getSurfaceData() throws Exception{
    getHeader();
    getVertices();
    getTriangles();
    Logger.info("efvet file contains " + nVertices + " vertices and " + nTriangles + " triangles");
  }

  private void getHeader() throws Exception {
    skipTo("<efvet", null);
    while(readLine().length() > 0 && line.indexOf(">") < 0)
      jvxlFileHeaderBuffer.append("# " + line + "\n");
    Logger.info(jvxlFileHeaderBuffer.toString());
  }
  
  private void getVertices() throws Exception {
    Point3f pt = new Point3f();
    float value = 0;
    skipTo("<vertices", "count");
    jvxlData.vertexCount = nVertices = parseInt(); 
    skipTo("property=", null);
    line = line.replace('"',' ');
    String[] tokens = getTokens();
    int dataIndex = params.fileIndex;
    if (dataIndex > 0 && dataIndex < tokens.length)
      Logger.info("property " + tokens[dataIndex]);
    else
      Logger.info(line);
    for (int i = 0; i < nVertices; i++) {
      skipTo("<vertex", "image");
      pt.set(parseFloat(), parseFloat(), parseFloat());
      skipTo(null, "property");
      for(int j = 0; j < dataIndex; j++)
        value = parseFloat();
      addVertexCopy(pt, value, i);
    }
  }
  
  private void getTriangles() throws Exception {
    skipTo("<triangle_array", "count");
    nTriangles = parseInt();
    for (int i = 0; i < nTriangles; i++) {
      skipTo("<triangle", "vertex");
      addTriangleCheck(parseInt() - 1, parseInt() - 1, parseInt() - 1, 7, 0, false, 0);
    }
  }

}
