


 
package org.jmol.export;

import java.awt.Image;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.geodesic.Geodesic;
import org.jmol.modelset.Atom;
import org.jmol.shape.Text;
import org.jmol.util.Quaternion;

public class _IdtfExporter extends _Exporter {

  
  
  
  
  private AxisAngle4f viewpoint = new AxisAngle4f();
  private boolean haveSphere;
  private boolean haveCylinder;
  private boolean haveCone;
  private boolean haveCircle;
  
  public _IdtfExporter() {
    use2dBondOrderCalculation = true;
    canDoTriangles = false;
    isCartesianExport = true;
  }

  private void output(Tuple3f pt, StringBuffer sb,boolean checkpt) {
    if (checkpt)
      checkPoint(pt);
    sb.append(round(pt.x)).append(" ").append(round(pt.y)).append(" ").append(round(pt.z)).append(" ");
  }
  
  private Point3f ptMin = new Point3f(1e10f,1e10f,1e10f);
  private Point3f ptMax = new Point3f(-1e10f,-1e10f,-1e10f);
  
  private void checkPoint(Tuple3f pt) {
    if (pt.x < ptMin.x)
      ptMin.x = pt.x;
    if (pt.y < ptMin.y)
      ptMin.y = pt.y;
    if (pt.z < ptMin.z)
      ptMin.z = pt.z;
    if (pt.x > ptMax.x)
      ptMax.x = pt.x;
    if (pt.y > ptMax.y)
      ptMax.y = pt.y;
    if (pt.z > ptMax.z)
      ptMax.z = pt.z;
  }
  
  private int iObj;
  private Hashtable htDefs = new Hashtable();
  
  final private Point3f ptAtom = new Point3f();
  final private Matrix4f m = new Matrix4f();

  final private StringBuffer models = new StringBuffer();
  final private StringBuffer resources = new StringBuffer();
  final private StringBuffer modifiers = new StringBuffer();

  public void getHeader() {
    
    output.append("FILE_FORMAT \"IDTF\"\nFORMAT_VERSION 100\n");

    

    m.setIdentity();
    
    







    

    output.append("NODE \"GROUP\" {\n");
    output.append("NODE_NAME \"Jmol\"\n");
    output.append("PARENT_LIST {\nPARENT_COUNT 1\n"); 
    output.append("PARENT 0 {\n");
    output.append(getParentItem("", m));
    output.append("}}}\n");
    
  }

  private String getParentItem(String name, Matrix4f m) {
    StringBuffer sb= new StringBuffer();
    sb.append("PARENT_NAME \"" + name + "\"\n");
    sb.append("PARENT_TM {\n");
    sb.append(m.m00 + " " + m.m10 + " " + m.m20 + " 0.0\n");
    sb.append(m.m01 + " " + m.m11 + " " + m.m21 + " 0.0\n");
    sb.append(m.m02 + " " + m.m12 + " " + m.m22 + " 0.0\n");
    sb.append(m.m03 + " " + m.m13 + " " + m.m23 + " " + m.m33 + "\n");
    sb.append("}\n");
    return sb.toString();
  }

  private void addColix(short colix, boolean haveColors) {
    String key = "_" + colix;
    if (htDefs.containsKey(key))
      return;
    String color = (haveColors ? "1.0 1.0 1.0" : rgbFractionalFromColix(colix, ' '));
    htDefs.put(key, Boolean.TRUE);
    resources.append("RESOURCE_LIST \"SHADER\" {\n");
    resources.append("RESOURCE_COUNT 1\n");
    resources.append("RESOURCE 0 {\n");
    resources.append("RESOURCE_NAME \"Shader" + key + "\"\n");
    resources.append("ATTRIBUTE_USE_VERTEX_COLOR \"FALSE\"\n");
    resources.append("SHADER_MATERIAL_NAME \"Mat" + key +"\"\n");
    resources.append("SHADER_ACTIVE_TEXTURE_COUNT 0\n");
    resources.append("}}\n");
    resources.append("RESOURCE_LIST \"MATERIAL\" {\n");
    resources.append("RESOURCE_COUNT 1\n");
    resources.append("RESOURCE 0 {\n");
    resources.append("RESOURCE_NAME \"Mat" + key + "\"\n");
    resources.append("MATERIAL_AMBIENT " + color + "\n");
    resources.append("MATERIAL_DIFFUSE " + color + "\n");
    resources.append("MATERIAL_SPECULAR 0.0 0.0 0.0\n");
    resources.append("MATERIAL_EMISSIVE 0.0 0.0 0.0\n");
    resources.append("MATERIAL_REFLECTIVITY 0.00000\n");
    resources.append("MATERIAL_OPACITY " + opacityFractionalFromColix(colix) + "\n");
    resources.append("}}\n");
  }
  
  private void addShader(String key, short colix) {
    modifiers.append("MODIFIER \"SHADING\" {\n");
    modifiers.append("MODIFIER_NAME \"" + key + "\"\n");
    modifiers.append("PARAMETERS {\n");
    modifiers.append("SHADER_LIST_COUNT 1\n");
    modifiers.append("SHADING_GROUP {\n");
    modifiers.append("SHADER_LIST 0 {\n");
    modifiers.append("SHADER_COUNT 1\n");
    modifiers.append("SHADER_NAME_LIST {\n");
    modifiers.append("SHADER 0 NAME: \"Shader_" + colix +"\"\n");
    modifiers.append("}}}}}\n");
  }

  public void getFooter() {
    htDefs = null;
    outputNodes();
    output.append(models);
    output.append(resources);    
    
    output.append("RESOURCE_LIST \"VIEW\" {\n");
    output.append("\tRESOURCE_COUNT 1\n");
    output.append("\tRESOURCE 0 {\n");
    output.append("\t\tRESOURCE_NAME \"View0\"\n");
    output.append("\t\tVIEW_PASS_COUNT 1\n");
    output.append("\t\tVIEW_ROOT_NODE_LIST {\n");
    output.append("\t\t\tROOT_NODE 0 {\n");
    output.append("\t\t\t\tROOT_NODE_NAME \"\"\n");
    output.append("\t\t\t}\n");
    output.append("\t\t}\n");
    output.append("\t}\n");
    output.append("}\n\n");

    
    
    
    
    
    
    
    
    
    
    
    
    viewer.getAxisAngle(viewpoint);
    Quaternion q = new Quaternion(viewpoint);
    viewpoint.set(q.getMatrix());
    if (viewpoint.angle == 0)
      viewpoint.z = 1;
    Quaternion q0 = new Quaternion(0.6414883f, -0.5258319f, 0.3542887f, 0.43182528f);
    q = q0.inv().mul(q);
    
    
    
    pt.set(ptMax);
    pt.add(ptMin);
    pt.scale(0.5f);
    
    pt.sub(center);
    ptAtom.set(q.transform(pt));
    float zoom = viewer.getZoomPercentFloat() / 100f;
    ptAtom.scale(zoom);
    String dxyz = ptAtom.x + " " + ptAtom.y + " " + ptAtom.z;
    String scale = " " + zoom;
    scale = scale + scale + scale;

    

    output.append("\nRESOURCE_LIST \"MOTION\" {");
    output.append("\n  RESOURCE_COUNT 1");
    output.append("\n  RESOURCE 0 {");
    output.append("\n    RESOURCE_NAME \"Motion0\"");
    output.append("\n    MOTION_TRACK_COUNT 1");
    output.append("\n    MOTION_TRACK_LIST {");
    output.append("\n      MOTION_TRACK 0 {");
    output.append("\n        MOTION_TRACK_NAME \"M00\"");
    output.append("\n        MOTION_TRACK_SAMPLE_COUNT 2");
    output.append("\n        KEY_FRAME_LIST {");
    output.append("\n          KEY_FRAME 0 {");
    output.append("\n            KEY_FRAME_TIME 0");
    output.append("\n            KEY_FRAME_DISPLACEMENT 0 0 0");
    output.append("\n            KEY_FRAME_ROTATION " + q.toString0123());
    output.append("\n            KEY_FRAME_SCALE 1 1 1");
    output.append("\n          }");
    output.append("\n          KEY_FRAME 1 {");
    output.append("\n            KEY_FRAME_TIME 1");
    output.append("\n            KEY_FRAME_DISPLACEMENT " + dxyz);
    output.append("\n            KEY_FRAME_ROTATION " + q.toString0123());
    output.append("\n            KEY_FRAME_SCALE" + scale);
    output.append("\n          }");
    output.append("\n         }");
    output.append("\n      }");
    output.append("\n    }");
    output.append("\n  }");
    output.append("\n}\n");
    output.append("\nMODIFIER \"ANIMATION\" {");
    output.append("\n  MODIFIER_NAME \"Jmol\"");
    output.append("\n  PARAMETERS {");
    output.append("\n    ATTRIBUTE_ANIMATION_PLAYING \"TRUE\"");
    output.append("\n    ATTRIBUTE_ROOT_BONE_LOCKED \"TRUE\"");
    output.append("\n    ATTRIBUTE_SINGLE_TRACK \"TRUE\"");
    output.append("\n    ATTRIBUTE_AUTO_BLEND \"FALSE\"");
    output.append("\n    TIME_SCALE 1.0");
    output.append("\n    BLEND_TIME 0.0");
    output.append("\n    MOTION_COUNT 1");
    output.append("\n    MOTION_INFO_LIST {");
    output.append("\n      MOTION_INFO 0 {");
    output.append("\n        MOTION_NAME \"Motion0\"");
    output.append("\n        ATTRIBUTE_LOOP \"FALSE\"");
    output.append("\n        ATTRIBUTE_SYNC \"FALSE\"");
    output.append("\n        TIME_OFFSET 0.0");
    output.append("\n        TIME_SCALE 1.0");
    output.append("\n      }");
    output.append("\n    }");
    output.append("\n  }");
    output.append("\n}\n");

    output.append(modifiers);    
  }

  private Hashtable htNodes = new Hashtable();
  
  private void outputNodes() {
    Enumeration e = htNodes.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      Vector v = (Vector) htNodes.get(key);
      output.append("NODE \"MODEL\" {\n");
      output.append("NODE_NAME \"" + key + "\"\n");
      int n = v.size();
      output.append("PARENT_LIST {\nPARENT_COUNT " + n + "\n"); 
      for (int i = 0; i < n; i++) {
        output.append("PARENT " + i + " {\n");
        output.append((String)v.get(i));
        output.append("}\n");
      }
      output.append("}\n");
      int i = key.indexOf("_");
      if (i > 0)
        key = key.substring(0,i);
      output.append("RESOURCE_NAME \"" + key + "_Mesh\"\n}\n");
    }
  }

  public void renderAtom(Atom atom, short colix) {
    float r = atom.getMadAtom() / 2000f;
    outputSphere(atom, r, colix);
  }

  public void drawPixel(short colix, int x, int y, int z) {
    
    pt.set(x, y, z);
    viewer.unTransformPoint(pt, ptAtom);
    outputSphere(ptAtom, 0.02f, colix);
  }
    
  public void fillSphereCentered(short colix, int diameter, Point3f pt) {
    viewer.unTransformPoint(pt, ptAtom);
    outputSphere(ptAtom, viewer.unscaleToScreen((int)pt.z, diameter) / 2, colix);
  }

  private Matrix4f sphereMatrix = new Matrix4f();
  private Matrix4f cylinderMatrix = new Matrix4f();

  private void outputSphere(Point3f center, float radius, short colix) {
    outputEllipsoid(center, radius, radius, radius, null, colix);
  }
  private void outputEllipsoid(Point3f center, float rx, float ry, float rz, AxisAngle4f a, short colix) {
    if (!haveSphere) {
      models.append(getSphereResource());
      haveSphere = true;
      sphereMatrix = new Matrix4f();
    }
    checkPoint(center);
    addColix(colix, false);
    String key = "Sphere_" + colix;
    Vector v = (Vector) htNodes.get(key);
    if (v == null) {
      v = new Vector();
      htNodes.put(key, v);
      addShader(key, colix);
    }
    if (a != null) {
      Matrix3f mq = new Matrix3f();
      Matrix3f m = new Matrix3f();
      m.m00 = rx;
      m.m11 = ry;
      m.m22 = rz;
      mq.set(a);
      mq.mul(m);
      sphereMatrix.set(mq);
    } else {
      sphereMatrix.setIdentity();
      sphereMatrix.m00 = rx;
      sphereMatrix.m11 = ry;
      sphereMatrix.m22 = rz;
    }
    sphereMatrix.m03 = center.x;
    sphereMatrix.m13 = center.y;
    sphereMatrix.m23 = center.z;
    sphereMatrix.m33 = 1;
    v.add(getParentItem("Jmol", sphereMatrix));
  }

  private String getSphereResource() {
    StringBuffer sb = new StringBuffer();
    sb.append("RESOURCE_LIST \"MODEL\" {\n")
    .append("RESOURCE_COUNT 1\n")
    .append("RESOURCE 0 {\n")
    .append("RESOURCE_NAME \"Sphere_Mesh\"\n")
    .append("MODEL_TYPE \"MESH\"\n")
    .append("MESH {\n");
    int vertexCount = Geodesic.getVertexCount(2);
    short[] f = Geodesic.getFaceVertexes(2);
    int[] faces = new int[f.length];
    for (int i = 0; i < f.length; i++)
      faces[i] = f[i];
    Vector3f[] vertexes = new Vector3f[vertexCount];
    for (int i = 0; i < vertexCount;i++)
      vertexes[i] = Geodesic.getVertexVector(i);
    return getMeshData("Sphere", faces, vertexes, vertexes);
  }

  private String getMeshData(String type, int[] faces, Tuple3f[] vertexes, Tuple3f[] normals) {
    int nFaces = faces.length / 3;
    int vertexCount = vertexes.length;
    int normalCount = normals.length;
    StringBuffer sb = new StringBuffer();
    getMeshHeader(type, nFaces, vertexCount, normalCount, 0, sb);
    sb.append("MESH_FACE_POSITION_LIST { ");
    for (int i = 0; i < faces.length; i++)
      sb.append(faces[i]).append(" ");
    sb.append("}\n");
    sb.append("MESH_FACE_NORMAL_LIST { ");
    for (int i = 0; i < faces.length; i++)
      sb.append(faces[i]).append(" ");
    sb.append("}\n");
    sb.append("MESH_FACE_SHADING_LIST { ");
    for (int i = 0; i < nFaces; i++)
      sb.append("0 ");
    sb.append("}\n");
    sb.append("MODEL_POSITION_LIST { ");
    for (int i = 0; i < vertexCount; i++)
      output(vertexes[i], sb, false);
    sb.append("}\n");
    sb.append("MODEL_NORMAL_LIST { ");
    for (int i = 0; i < normalCount; i++)
      output(normals[i], sb, false);
    sb.append("}\n}}}\n");
    return sb.toString();
  }

  private void getMeshHeader(String type, int nFaces, int vertexCount, int normalCount,
                             int colorCount, StringBuffer sb) {
    sb.append("RESOURCE_LIST \"MODEL\" {\n")
        .append("RESOURCE_COUNT 1\n")
        .append("RESOURCE 0 {\n")
        .append("RESOURCE_NAME \"").append(type).append("_Mesh\"\n")
        .append("MODEL_TYPE \"MESH\"\n")
        .append("MESH {\n")
        .append("FACE_COUNT ").append(nFaces).append("\n")
        .append("MODEL_POSITION_COUNT ").append(vertexCount).append("\n")
        .append("MODEL_NORMAL_COUNT ").append(normalCount).append("\n")
        .append("MODEL_DIFFUSE_COLOR_COUNT ").append(colorCount).append("\n")
        .append("MODEL_SPECULAR_COLOR_COUNT 0\n")
        .append("MODEL_TEXTURE_COORD_COUNT 0\n")
        .append("MODEL_BONE_COUNT 0\n")
        .append("MODEL_SHADING_COUNT 1\n")
        .append("MODEL_SHADING_DESCRIPTION_LIST {\n")
          .append("SHADING_DESCRIPTION 0 {\n")
           .append("TEXTURE_LAYER_COUNT 0\n")
           .append("SHADER_ID 0\n}}\n");
  }

  private final Point3f pt2 = new Point3f();
  public void fillCylinder(Point3f ptA, Point3f ptB, short colix1,
                           short colix2, byte endcaps, int diameter,
                           int bondOrder) {
    if (bondOrder == -1) {
      
      ptAtom.set(ptA);
      pt2.set(ptB);
    } else {
      viewer.unTransformPoint(ptA, ptAtom);
      viewer.unTransformPoint(ptB, pt2);
    }
    int madBond = diameter;
    if (madBond < 20)
      madBond = 20;
    if (colix1 == colix2) {
      outputCylinder(ptAtom, pt2, colix1, endcaps, madBond);
    } else {
      tempV2.set(pt2);
      tempV2.add(ptAtom);
      tempV2.scale(0.5f);
      pt.set(tempV2);
      outputCylinder(ptAtom, pt, colix1, Graphics3D.ENDCAPS_FLAT, madBond);
      outputCylinder(pt, pt2, colix2, Graphics3D.ENDCAPS_FLAT, madBond);
      if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
        outputSphere(ptAtom, madBond / 2000f*1.01f, colix1);
        outputSphere(pt2, madBond / 2000f*1.01f, colix2);
      }
    }
  }

  private void outputCylinder(Point3f pt1, Point3f pt2, short colix,
                             byte endcaps, int madBond) {
    if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
      outputSphere(pt1, madBond / 2000f*1.01f, colix);
      outputSphere(pt2, madBond / 2000f*1.01f, colix);
    } else if (endcaps == Graphics3D.ENDCAPS_FLAT) {
      outputCircle(pt1, pt2, colix, madBond);      
      outputCircle(pt2, pt1, colix, madBond);      
    }
    if (!haveCylinder) {
      models.append(getCylinderResource());
      haveCylinder = true;
      cylinderMatrix = new Matrix4f();
    }
    checkPoint(pt1);
    checkPoint(pt2);
    addColix(colix, false);
    String key = "Cylinder_" + colix;
    Vector v = (Vector) htNodes.get(key);
    if (v == null) {
      v = new Vector();
      htNodes.put(key, v);
      addShader(key, colix);
    }
    float radius = madBond / 2000f;
    cylinderMatrix.set(getRotationMatrix(pt1, pt2, radius));
    cylinderMatrix.m03 = pt1.x;
    cylinderMatrix.m13 = pt1.y;
    cylinderMatrix.m23 = pt1.z;
    cylinderMatrix.m33 = 1;
    v.add(getParentItem("Jmol", cylinderMatrix));
  }

  private void outputCircle(Point3f ptCenter, Point3f ptPerp, short colix, int madBond) {
    if (!haveCircle) {
      models.append(getCircleResource());
      haveCircle = true;
      cylinderMatrix = new Matrix4f();
    }
    addColix(colix, false);
    String key = "Circle_" + colix;
    Vector v = (Vector) htNodes.get(key);
    if (v == null) {
      v = new Vector();
      htNodes.put(key, v);
      addShader(key, colix);
    }
    checkPoint(ptCenter);
    float radius = madBond / 2000f;
    cylinderMatrix.set(getRotationMatrix(ptCenter, ptPerp, radius));
    cylinderMatrix.m03 = ptCenter.x;
    cylinderMatrix.m13 = ptCenter.y;
    cylinderMatrix.m23 = ptCenter.z;
    cylinderMatrix.m33 = 1;
    v.add(getParentItem("Jmol", cylinderMatrix));
  }

  private Matrix3f getRotationMatrix(Point3f pt1, Point3f pt2, float radius) {    
    Matrix3f m = new Matrix3f();
    Matrix3f m1;
    if (pt2.x == pt1.x && pt2.y == pt1.y) {
      m1 = new Matrix3f();
      m1.setIdentity();
      if (pt1.z > pt2.z)
        m1.mul(-1);
    } else {
      tempV1.set(pt2);
      tempV1.sub(pt1);
      tempV2.set(0, 0, 1);
      tempV2.cross(tempV2, tempV1);
      tempV1.cross(tempV1, tempV2);
      Quaternion q = Quaternion.getQuaternionFrame(tempV2, tempV1, null);
      m1 = q.getMatrix();
    }
    m.m00 = radius;
    m.m11 = radius;
    m.m22 = pt2.distance(pt1);
    m1.mul(m);
    return m1;
  }

  private Object getCylinderResource() {
    int ndeg = 10;
    int vertexCount = 360 / ndeg * 2;
    int n = vertexCount / 2;
    int[] faces = new int[vertexCount * 3];
    int fpt = -1;
    for (int i = 0; i < n; i++) {
      faces[++fpt] = i;
      faces[++fpt] = (i + 1) % n;
      faces[++fpt] = (i + n);
      faces[++fpt] = (i + 1) % n;
      faces[++fpt] = (i + 1) % n + n;
      faces[++fpt] = (i + n);
    }
    Point3f[] vertexes = new Point3f[vertexCount];
    Point3f[] normals = new Point3f[vertexCount];
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * ndeg / 180. * Math.PI)); 
      float y = (float) (Math.sin(i * ndeg / 180. * Math.PI)); 
      normals[i] = vertexes[i] = new Point3f(x, y, 0);
    }
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos((i + 0.5) * ndeg / 180 * Math.PI)); 
      float y = (float) (Math.sin((i + 0.5) * ndeg / 180 * Math.PI)); 
      vertexes[i + n] = new Point3f(x, y, 1);
      normals[i + n] = new Point3f(x, y, 0);
    }
    return getMeshData("Cylinder", faces, vertexes, normals);
  }

  public void renderIsosurface(Point3f[] vertices, short colix,
                               short[] colixes, Vector3f[] normals,
                               int[][] indices, BitSet bsFaces, int nVertices,
                               int faceVertexMax, short[] polygonColixes,
                               int nPolygons) {
    if (nVertices == 0)
      return;
    int nFaces = 0;
    for (int i = nPolygons; --i >= 0;)
      if (bsFaces.get(i))
        nFaces += (faceVertexMax == 4 && indices[i].length == 4 ? 2 : 1);
    if (nFaces == 0)
      return;

    Vector colorList = null;
    Hashtable htColixes = new Hashtable();
    if (polygonColixes != null)
      colorList = getColorList(0, polygonColixes, nPolygons, bsFaces, htColixes);
    else if (colixes != null)
      colorList = getColorList(0, colixes, nVertices, null, htColixes);
    addColix(colix, polygonColixes != null || colixes != null);
    if (polygonColixes != null) {
      
      return; 
    }

    

    int[] coordMap = new int[nVertices];
    int nCoord = 0;
    for (int i = 0; i < nVertices; i++) {
      if (Float.isNaN(vertices[i].x))
        continue;
      coordMap[i] = nCoord++;
    }

    StringBuffer sbFaceCoordIndices = new StringBuffer();
    for (int i = nPolygons; --i >= 0;) {
      if (!bsFaces.get(i))
        continue;
      sbFaceCoordIndices.append(" " + coordMap[indices[i][0]] + " " + coordMap[indices[i][1]] + " "
          + coordMap[indices[i][2]]);
      if (faceVertexMax == 4 && indices[i].length == 4) {
        sbFaceCoordIndices.append(" " + coordMap[indices[i][0]] + " " + coordMap[indices[i][2]]
            + " " + coordMap[indices[i][3]]);
      }
    }

    
    
    StringBuffer sbFaceNormalIndices = new StringBuffer();
    Vector vNormals = null;
    if (normals != null) {
      Hashtable htNormals = new Hashtable();
      vNormals = new Vector();
      int[] normalMap = new int[nVertices];
      
      for (int i = 0; i < nVertices; i++) {
        String s;
        if (Float.isNaN(normals[i].x))
          continue;
        s = (" " + round(normals[i].x) + " " + round(normals[i].y) + " " + round(normals[i].z));
        if (htNormals.containsKey(s)) {
          normalMap[i] = ((Integer) htNormals.get(s)).intValue();
        } else {
          normalMap[i] = vNormals.size();
          vNormals.add(s);
          htNormals.put(s, new Integer(normalMap[i]));
        }
      }
      htNormals = null;
      for (int i = nPolygons; --i >= 0;) {
        if (!bsFaces.get(i))
          continue;
        sbFaceNormalIndices.append(" " + normalMap[indices[i][0]] + " " + normalMap[indices[i][1]] + " "
            + normalMap[indices[i][2]]);
        if (faceVertexMax == 4 && indices[i].length == 4)
          sbFaceNormalIndices.append(" " + normalMap[indices[i][0]] + " "
              + normalMap[indices[i][2]] + " " + normalMap[indices[i][3]]);
      }
    }      
    
    

    StringBuffer sbColorIndexes = new StringBuffer();
    if (colorList != null) {
      for (int i = nPolygons; --i >= 0;) {
        if (!bsFaces.get(i))
          continue;
        if (polygonColixes == null) {
          sbColorIndexes.append(" " + htColixes.get("" + colixes[indices[i][0]]) + " "
              + htColixes.get("" + colixes[indices[i][1]]) + " "
              + htColixes.get("" + colixes[indices[i][2]]));
          if (faceVertexMax == 4 && indices[i].length == 4)
            sbColorIndexes.append(" " + htColixes.get("" + colixes[indices[i][0]]) + " "
                + htColixes.get("" + colixes[indices[i][2]]) + " "
                + htColixes.get("" + colixes[indices[i][3]]));
        } else {
          
          
        }
      }
    }    


    
    
    
    StringBuffer sbCoords = new StringBuffer();
    for (int i = 0; i < nVertices; i++) {
      if (Float.isNaN(vertices[i].x))
        continue;
      output(vertices[i], sbCoords, true);
    }
    coordMap = null;

    

    StringBuffer sbNormals = new StringBuffer();
    int nNormals = 0;
    if (normals != null) {
      nNormals = vNormals.size();
      for (int i = 0; i < nNormals; i++)
        sbNormals.append(vNormals.get(i));
      vNormals = null;
    }

    

    StringBuffer sbColors = new StringBuffer();
    int nColors = 0;
    if (colorList != null) {
      nColors = colorList.size();
      for (int i = 0; i < nColors; i++) {
        short c = ((Short) colorList.get(i)).shortValue();
        sbColors.append(rgbFractionalFromColix(c, ' '))
                 .append(" ")
                 .append(translucencyFractionalFromColix(c))
                 .append(" ");
      }
    }
    String key = "mesh" + (++iObj);
    addMeshData(key, nFaces, nCoord, nNormals, nColors, sbFaceCoordIndices, sbFaceNormalIndices,
        sbColorIndexes, sbCoords, sbNormals, sbColors);
    Vector v = new Vector();
    htNodes.put(key, v);
    addShader(key, colix);
    cylinderMatrix.setIdentity();
    v.add(getParentItem("Jmol", cylinderMatrix));
  }

  private void addMeshData(String key, int nFaces, int nCoord, int nNormals, int nColors, 
                           StringBuffer sbFaceCoordIndices,
                           StringBuffer sbFaceNormalIndices,
                           StringBuffer sbColorIndices, 
                           StringBuffer sbCoords,
                           StringBuffer sbNormals, 
                           StringBuffer sbColors) {
    getMeshHeader(key, nFaces, nCoord, nNormals, nColors, models);
    models.append("MESH_FACE_POSITION_LIST { ")
      .append(sbFaceCoordIndices).append(" }\n")
      .append("MESH_FACE_NORMAL_LIST { ")
      .append(sbFaceNormalIndices).append(" }\n");
    models.append("MESH_FACE_SHADING_LIST { ");
    for (int i = 0; i < nFaces; i++)
      models.append("0 ");
    models.append("}\n");
    if (nColors > 0)
      models.append("MESH_FACE_DIFFUSE_COLOR_LIST { ")
            .append(sbColorIndices).append(" }\n");
    models.append("MODEL_POSITION_LIST { ")
      .append(sbCoords).append(" }\n")
      .append("MODEL_NORMAL_LIST { ")
      .append(sbNormals).append(" }\n");
    if (nColors > 0)
      models.append("MODEL_DIFFUSE_COLOR_LIST { ")
            .append(sbColors)
            .append(" }\n");
    models.append("}}}\n");
  }

  public void fillCone(short colix, byte endcap, int diameter,
                       Point3f screenBase, Point3f screenTip) {
    viewer.unTransformPoint(screenBase, tempP1);
    viewer.unTransformPoint(screenTip, tempP2);
    float radius = viewer.unscaleToScreen((int)screenBase.z, diameter) / 2f;
    if (radius < 0.05f)
      radius = 0.05f;
    if (!haveCone) {
      models.append(getConeResource());
      haveCone = true;
    }
    checkPoint(tempP1);
    checkPoint(tempP2);
    addColix(colix, false);
    String key = "Cone_" + colix;
    Vector v = (Vector) htNodes.get(key);
    if (v == null) {
      v = new Vector();
      htNodes.put(key, v);
      addShader(key, colix);
    }
    cylinderMatrix.set(getRotationMatrix(tempP1, tempP2, radius));
    cylinderMatrix.m03 = tempP1.x;
    cylinderMatrix.m13 = tempP1.y;
    cylinderMatrix.m23 = tempP1.z;
    cylinderMatrix.m33 = 1;
    v.add(getParentItem("Jmol", cylinderMatrix));
  }

  private Object getConeResource() {
    int ndeg = 10;
    int n = 360 / ndeg;
    int vertexCount = n + 1;
    int[] faces = new int[n * 3];
    int fpt = -1;
    for (int i = 0; i < n; i++) {
      faces[++fpt] = i;
      faces[++fpt] = (i + 1) % n;
      faces[++fpt] = n;
    }
    Point3f[] vertexes = new Point3f[vertexCount];
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * ndeg / 180. * Math.PI));
      float y = (float) (Math.sin(i * ndeg / 180. * Math.PI));
      vertexes[i] = new Point3f(x, y, 0);
    }
    vertexes[n] = new Point3f(0, 0, 1);
    return getMeshData("Cone", faces, vertexes, vertexes);
  }
  
  private Object getCircleResource() {
    int ndeg = 10;
    int n = 360 / ndeg;
    int vertexCount = n + 1;
    int[] faces = new int[n * 3];
    int fpt = -1;
    for (int i = 0; i < n; i++) {
      faces[++fpt] = i;
      faces[++fpt] = (i + 1) % n;
      faces[++fpt] = n;
    }
    Point3f[] vertexes = new Point3f[vertexCount];
    Point3f[] normals = new Point3f[vertexCount];
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * ndeg / 180. * Math.PI));
      float y = (float) (Math.sin(i * ndeg / 180. * Math.PI));
      vertexes[i] = new Point3f(x, y, 0);
      normals[i] = new Point3f(0, 0, 1);
    }
    vertexes[n] = new Point3f(0, 0, 0);
    normals[n] = new Point3f(0, 0, 1);
    return getMeshData("Circle", faces, vertexes, normals);
  }
  
  public void fillCylinder(short colix, byte endcaps, int diameter,
                           Point3f screenA, Point3f screenB) {
    Point3f ptA = new Point3f();
    Point3f ptB = new Point3f();
    viewer.unTransformPoint(screenA, ptA);
    viewer.unTransformPoint(screenB, ptB);
    int madBond = (int) (viewer.unscaleToScreen(
        (int)((screenA.z + screenB.z) / 2), diameter) * 1000);      
    if (madBond < 20)
      madBond = 20;
    outputCylinder(ptA, ptB, colix, endcaps, madBond);
    
  }

  public void fillTriangle(short colix, Point3f ptA, Point3f ptB, Point3f ptC) {
    
    
    viewer.unTransformPoint(ptA, tempP1);
    viewer.unTransformPoint(ptB, tempP2);
    viewer.unTransformPoint(ptC, tempP3);
    addColix(colix, false);
    String key = "T" + (++iObj);
    models.append(getTriangleResource(key, tempP1, tempP2, tempP3));
    Vector v = new Vector();
    htNodes.put(key, v);
    addShader(key, colix);
    if (cylinderMatrix == null)
      cylinderMatrix = new Matrix4f();
    cylinderMatrix.setIdentity();
    v.add(getParentItem("Jmol", cylinderMatrix));
  }

  private Object getTriangleResource(String key, Point3f pt1,
                                     Point3f pt2, Point3f pt3) {
    int[] faces = new int[] { 0, 1, 2 };
    Point3f[] vertexes = new Point3f[] { pt1, pt2, pt3 };
    tempV1.set(pt3);
    tempV1.sub(pt1);
    tempV2.set(pt2);
    tempV2.sub(pt1);
    tempV2.cross(tempV2, tempV1);
    tempV2.normalize();
    Vector3f[] normals = new Vector3f[] { tempV2, tempV2, tempV2 };
    return getMeshData(key, faces, vertexes, normals);
  }

  public void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    
    
    
    if (z < 3) {
      viewer.transformPoint(center, pt);
      z = (int)pt.z;
    }
    g3d.plotText(x, y, z, g3d.getColorArgbOrGray(colix), text, font3d, jmolRenderer);
  }

  public void startShapeBuffer(int iShape) {
  }

  public void endShapeBuffer() {
  }

  public void renderText(Text t) {
  }

  public void drawString(short colix, String str, Font3D font3d, int xBaseline,
                         int yBaseline, int z, int zSlab) {
  }

  public void drawCircleCentered(short colix, int diameter, int x, int y,
                                 int z, boolean doFill) {

    pt.set(x, y, z);
    viewer.unTransformPoint(pt, ptAtom);
    float d = viewer.unscaleToScreen(z, diameter);
    int mad = (int) (d * 1000);
    pt.set(x, y, z + 1);
    viewer.unTransformPoint(pt, pt);
    if (doFill) {
      outputCircle(ptAtom, pt, colix, mad);
      return;
    }
    if (true)
      return;
    
    float rpd = 3.1415926f / 180;
    Point3f[] pts = new Point3f[73];
    for (int i = 0, p = 0; i <= 360; i += 5, p++) {
      pts[p] = new Point3f((float) (Math.cos(i * rpd) * d / 2), (float) (Math
          .sin(i * rpd)
          * d / 2), 0);
      pts[p].add(ptAtom);
    }
    mad = (int) (0.02f * 2 / d * 1000);
    for (int i = 0; i < 72; i++) {
      outputCylinder(pts[i], pts[i+1], colix, Graphics3D.ENDCAPS_FLAT, mad);
    }
  }

  public void fillScreenedCircleCentered(short colix, int diameter, int x,
                                         int y, int z) {
    drawCircleCentered(colix, diameter, x, y, z, false);
    drawCircleCentered(Graphics3D.getColixTranslucent(colix, true, 0.5f), 
        diameter, x, y, z, true);
  }

  public void drawTextPixel(int argb, int x, int y, int z) {
    
    pt.set(x, y, z);
    viewer.unTransformPoint(pt, ptAtom);
    short colix = Graphics3D.getColix(argb); 
    outputSphere(ptAtom, 0.02f, colix);
  }

  public void plotImage(int x, int y, int z, Image image, short bgcolix,
                        int width, int height) {
    
  }

  void renderEllipsoid(Point3f center, Point3f[] points, short colix, int x,
                       int y, int z, int diameter, Matrix3f toEllipsoidal,
                       double[] coef, Matrix4f deriv, Point3i[] octantPoints) {
    
    
    
    
    AxisAngle4f a = Quaternion.getQuaternionFrame(center, points[1], points[3]).toAxisAngle4f();
    float sx = points[1].distance(center);
    float sy = points[3].distance(center);
    float sz = points[5].distance(center);
    outputEllipsoid(center, sx, sy, sz, a, colix);
  }

}
