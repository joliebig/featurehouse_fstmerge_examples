
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import javax.vecmath.Point3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.Logger;
import org.jmol.util.Parser;




class ObjReader extends PolygonFileReader {

  private int nPolygons;
  String pmeshError;

  ObjReader(SurfaceGenerator sg, String fileName, BufferedReader br) {
    super(sg, br);
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer
        .append("pmesh file format\nvertices and triangles only\n");
    JvxlReader.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData,
        jvxlFileHeaderBuffer);
  }

  void getSurfaceData() throws Exception {
    if (readPmesh())
      Logger.info("obj file contains "
          + nVertices + " vertices and " + nPolygons + " polygons for "
          + nTriangles + " triangles");
    else
      Logger.error(params.fileName + ": " 
          + (pmeshError == null ? "Error reading obj data "
              : pmeshError));
  }

  private boolean readPmesh() {
    try {
      if (readVertices() && readPolygons())
        return true;
    } catch (Exception e) {
      if (pmeshError == null)
        pmeshError = "pmesh ERROR: " + e;
    }
    return false;
  }

  Point3f pt = new Point3f();
  private boolean readVertices() throws Exception {
    pmeshError = "pmesh ERROR: invalid vertex list";
    Point3f pt = new Point3f();
    while ((line = br.readLine()) != null) {
      if (line.length() == 0 || nVertices == 0 && line.indexOf("v ") != 0)
        continue;
      if (line.indexOf("v ") != 0)
        break;
      next[0] = 2;
      pt.set(Parser.parseFloat(line, next), Parser.parseFloat(line, next), Parser.parseFloat(line, next));
      addVertexCopy(pt, 0, ++nVertices);
    }
    pmeshError = null;
    return true;
  }

  private boolean readPolygons() {
    nPolygons = 0;
    int color = 0;
    try {
      if (!params.readAllData) {
        for (int i = 0; i < params.fileIndex; i++) {
          while (line != null && line.indexOf("g ") != 0)
            line = br.readLine();
          if (line == null)
            break;
          color = Graphics3D.getArgbFromString("[x" + line.substring(3) + "]");
          
          line = br.readLine();
        }
      }

      while (line != null) {
        if (line.indexOf("f ") == 0) {
          nPolygons++;
          next[0] = 2;
          int ia = Parser.parseInt(line, next);
          int ib = Parser.parseInt(line, next);
          int ic = Parser.parseInt(line, next);
          int id = Parser.parseInt(line, next);
          int vertexCount = (id == Integer.MIN_VALUE ? 3 : 4);
          if (vertexCount == 4) {
            nTriangles += 2;
            addTriangleCheck(ia - 1, ib - 1, ic - 1, 5, false, color);
            addTriangleCheck(ib - 1, ic - 1, id - 1, 3, false, color);
          } else {
            nTriangles++;
            addTriangleCheck(ia - 1, ib - 1, ic - 1, 7, false, color);
          }
        } else if (line.indexOf("g ") == 0) {
          if (!params.readAllData)
            break;
          color = Graphics3D.getArgbFromString("[x" + line.substring(3) + "]");
        }
        line = br.readLine();
      }
    } catch (Exception e) {
      if (line != null)
        pmeshError = "problem reading OBJ file at: " + line;
      
    }
    return true;
  }
}
