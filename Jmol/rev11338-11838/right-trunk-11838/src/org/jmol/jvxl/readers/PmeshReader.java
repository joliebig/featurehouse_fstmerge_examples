
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import javax.vecmath.Point3f;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.BinaryDocument;
import org.jmol.util.Logger;




class PmeshReader extends PolygonFileReader {

  private int nPolygons;
  private boolean isBinary;
  final static String PMESH_BINARY_MAGIC_NUMBER = "PM" + '\1' + '\0';
  String pmeshError;

  PmeshReader(SurfaceGenerator sg, String fileName, BufferedReader br) {
    super(sg, br);
    jvxlFileHeaderBuffer
        .append("pmesh file format\nvertices and triangles only\n");
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData,
        jvxlFileHeaderBuffer);
    isBinary = checkBinary(fileName);
  }

  private boolean checkBinary(String fileName) {
    try {
      br.mark(4);
      char[] buf = new char[5];
      br.read(buf);
      if ((new String(buf)).startsWith(PMESH_BINARY_MAGIC_NUMBER)) {
        br.close();
        binarydoc = new BinaryDocument();
        binarydoc.setStream(sg.getAtomDataServer().getBufferedInputStream(
            fileName), (buf[4] == '\0'));
        return true;
      }
      br.reset();
    } catch (Exception e) {
    }
    return false;
  }

  void getSurfaceData() throws Exception {
    if (readPmesh())
      Logger.info((isBinary ? "binary " : "") + "pmesh file contains "
          + nVertices + " vertices and " + nPolygons + " polygons for "
          + nTriangles + " triangles");
    else
      Logger.error(params.fileName + ": " 
          + (pmeshError == null ? "Error reading pmesh data "
              : pmeshError));
  }

  private boolean readPmesh() {
    try {
      if (isBinary && !readBinaryHeader())
        return false;
      if (readVertices() && readPolygons())
        return true;
    } catch (Exception e) {
      if (pmeshError == null)
        pmeshError = "pmesh ERROR: " + e;
    }
    return false;
  }

  boolean readBinaryHeader() {
    pmeshError = "could not read binary Pmesh file header";
    try {
      byte[] ignored = new byte[64];
      binarydoc.readByteArray(ignored, 0, 8);
      nVertices = binarydoc.readInt();
      nPolygons = binarydoc.readInt();
      binarydoc.readByteArray(ignored, 0, 64);
    } catch (Exception e) {
      pmeshError += " " + e.getMessage();
      binarydoc.close();
      return false;
    }
    pmeshError = null;
    return true;
  }

  private boolean readVertices() throws Exception {
    pmeshError = "pmesh ERROR: vertex count must be positive";
    if (!isBinary)
      nVertices = getInt();
    if (nVertices <= 0) {
      pmeshError += " (" + nVertices + ")";
      return false;
    }
    pmeshError = "pmesh ERROR: invalid vertex list";
    Point3f pt = new Point3f();
    for (int i = 0; i < nVertices; i++) {
      pt.set(getFloat(), getFloat(), getFloat());
      if (Logger.debugging)
        Logger.debug(i + ": " + pt);
      addVertexCopy(pt, 0, i);
    }
    pmeshError = null;
    return true;
  }

  private boolean readPolygons() throws Exception {
    pmeshError = "pmesh ERROR: polygon count must be zero or positive";
    if (!isBinary)
      nPolygons = getInt();
    if (nPolygons < 0) {
      pmeshError += " (" + nPolygons + ")";
      return false;
    }
    int[] vertices = new int[5];
    for (int iPoly = 0; iPoly < nPolygons; iPoly++) {
      int intCount = getInt();
      int vertexCount = intCount - (isBinary ? 0 : 1);
      
      if (vertexCount < 1 || vertexCount > 4) {
        pmeshError = "pmesh ERROR: bad polygon (must have 1-4 vertices) at #"
            + (iPoly + 1);
        return false;
      }
      for (int i = 0; i < intCount; ++i)
        if ((vertices[i] = getInt()) < 0 || vertices[i] >= nVertices) {
          pmeshError = "pmesh ERROR: invalid vertex index: " + vertices[i];
          return false;
        }
      
      if (vertexCount < 3)
        for (int i = vertexCount; i < 3; ++i)
          vertices[i] = vertices[i - 1];
      
      
      
      
      
      
      
      
      
      
      
      if (vertexCount == 4) {
        nTriangles += 2;
        addTriangleCheck(vertices[0], vertices[1], vertices[3], 5, 0, false, 0);
        addTriangleCheck(vertices[1], vertices[2], vertices[3], 3, 0, false, 0);
      } else {
        nTriangles++;
        addTriangleCheck(vertices[0], vertices[1], vertices[2], 7, 0, false, 0);
      }
    }
    if (isBinary)
      nBytes = binarydoc.getPosition();
    return true;
  }

  public int addTriangleCheck(int iA, int iB, int iC, int check,
                               int check2, boolean isAbsolute, int color) {
    if (Logger.debugging)
      Logger.debug("tri: " + iA + " " + iB + " " + iC);
    return super.addTriangleCheck(iA, iB, iC, check, check2, isAbsolute, color); 
  }

  

  private String[] tokens = new String[0];
  private int iToken = 0;

  private String nextToken() throws Exception {
    while (iToken >= tokens.length) { 
      iToken = 0;
      readLine();
      tokens = getTokens();
    }
    return tokens[iToken++];
  }

  private int getInt() throws Exception {
    return (isBinary ? binarydoc.readInt() : parseInt(nextToken()));
  }

  private float getFloat() throws Exception {
    return (isBinary ? binarydoc.readFloat() : parseFloat(nextToken()));
  }
}
