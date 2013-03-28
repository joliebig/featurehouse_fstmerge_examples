


 
package org.jmol.export;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Escape;
import org.jmol.util.Quaternion;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;

public class _X3dExporter extends _VrmlExporter {

  protected void outputHeader() {
    output("<X3D profile='Immersive' version='3.1' "
      + "xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' "
      + "xsd:noNamespaceSchemaLocation=' http://www.web3d.org/specifications/x3d-3.1.xsd '>"
      + "\n");
    output("<head>\n");
    output("<meta name='title' content=" + Escape.escape(viewer.getModelSetName()) + "/>\n");
    output("<meta name='description' content=' '/>\n");
    output("<meta name='creator' content=' '/>\n");
    output("<meta name='created' content='" + getExportDate() + "'/>\n");
    output("<meta name='generator' content='Jmol "+ Viewer.getJmolVersion() +", http://www.jmol.org'/>\n");
		output("<meta name='license' content='http://www.gnu.org/licenses/licenses.html#LGPL'/>\n");
    output("</head>\n");
    output("<Scene>\n");

    output("<NavigationInfo type='EXAMINE'/>\n");
    
    output("<Background skyColor='" 
      + rgbFractionalFromColix(backgroundColix, ' ') + "'/>\n");
    
    getViewpointPosition(tempP1);
    adjustViewpointPosition(tempP1);
    float angle = getFieldOfView();
    viewer.getAxisAngle(viewpoint);
    output("<Viewpoint fieldOfView='" + angle
      + "' position='" + tempP1.x + " " + tempP1.y + " " + tempP1.z 
      + "' orientation='" + viewpoint.x + " " + viewpoint.y + " " 
      + (viewpoint.angle == 0 ? 1 : viewpoint.z) + " " + -viewpoint.angle
      + "'\n jump='TRUE' description='v1'/>\n");
    output("\n  <!-- Jmol perspective:\n");
    output("  scalePixelsPerAngstrom: " + viewer.getScalePixelsPerAngstrom(false) + "\n");
    output("  cameraDepth: " + viewer.getCameraDepth() + "\n");
    output("  center: " + center + "\n");
    output("  rotationRadius: " + viewer.getRotationRadius() + "\n");
    output("  boundboxCenter: " + viewer.getBoundBoxCenter() + "\n");
    output("  translationOffset: " + viewer.getTranslationScript() + "\n");
    output("  zoom: " + viewer.getZoomPercentFloat() + "\n");
    output("  moveto command: " + viewer.getOrientationText(Token.moveto) + "\n");
    output("  screen width height dim: " + screenWidth + " " + screenHeight + " " 
      + viewer.getScreenDim() 
      + "\n  -->\n\n");

    output("<Transform translation='");
    tempP1.set(center);
    tempP1.scale(-1);
    output(tempP1);
    output("'>\n");
  }

  protected void outputFooter() {
    useTable = null;
    output("</Transform>\n");
    output("</Scene>\n");
    output("</X3D>\n");
  }

  protected void outputAppearance(short colix, boolean isText) {  
    String def = useTable.getDef((isText ? "T" : "") + colix);
    output("<Appearance ");
    if (def.charAt(0) == '_') {
      String color = rgbFractionalFromColix(colix, ' ');
      output("DEF='" + def + "'><Material diffuseColor='");
      if (isText)
        output("0 0 0' specularColor='0 0 0' ambientIntensity='0.0' shininess='0.0' emissiveColor='" 
            + color + "'/>");
      else
        output(color + "' transparency='" + translucencyFractionalFromColix(colix) + "'/>" );
    }
    else
      output(def +">");
    output("</Appearance>");
  }
  
  protected void outputCircle(Point3f pt1, Point3f pt2, float radius, short colix,
                              boolean doFill) {
    if (doFill) {

      
      
      output("<Transform translation='");
      tempV1.set(tempP3);
      tempV1.add(pt1);
      tempV1.scale(0.5f);
      output(tempV1);
      output("'><Billboard axisOfRotation='0 0 0'><Transform rotation='1 0 0 1.5708'>");
      outputCylinderChild(pt1, tempP3, colix, Graphics3D.ENDCAPS_FLAT, radius);
      output("</Transform></Billboard>");
      output("</Transform>\n");
      
      return;
    }
    
    

    String child = useTable.getDef("C" + colix + "_" + radius);
    output("<Transform");
    outputTransRot(tempP3, pt1, 0, 0, 1);
    tempP3.set(1, 1, 1);
    tempP3.scale(radius);
    output(" scale='");
    output(tempP3);
    output("'>\n<Billboard ");
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'");
      output(" axisOfRotation='0 0 0'><Transform>");
      output("<Shape><Extrusion beginCap='FALSE' convex='FALSE' endCap='FALSE' creaseAngle='1.57'");
      output(" crossSection='");
      float rpd = 3.1415926f / 180;
      float scale = 0.02f / radius;
      for (int i = 0; i <= 360; i += 10) {
        output(round(Math.cos(i * rpd) * scale) + " ");
        output(round(Math.sin(i * rpd) * scale) + " ");
      }
      output("' spine='");
      for (int i = 0; i <= 360; i += 10) {
        output(round(Math.cos(i * rpd)) + " ");
        output(round(Math.sin(i * rpd)) + " 0 ");
      }
      output("'/>");
      outputAppearance(colix, false);
      output("</Shape></Transform>");
    } else {
      output(child + ">");
    }
    output("</Billboard>\n");
    output("</Transform>\n");
  }

  protected void outputComment(String comment) {
    
  }

  protected void outputCone(Point3f ptBase, Point3f ptTip, float radius,
                            short colix) {
    float height = ptBase.distance(ptTip);
    output("<Transform");
    outputTransRot(ptBase, ptTip, 0, 1, 0);
    output(">\n<Shape ");
    String cone = "o" + (int) (height * 100) + "_" + (int) (radius * 100);
    String child = useTable.getDef("c" + cone + "_" + colix);
    if (child.charAt(0) == '_') {
      output("DEF='" + child +  "'>");
      cone = useTable.getDef(cone);
      output("<Cone ");
      if (cone.charAt(0) == '_') {
        output("DEF='"+ cone + "' height='" + round(height) 
          + "' bottomRadius='" + round(radius) + "'/>");
      } else {
        output(cone + "/>");
      }
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  protected void outputCylinder(Point3f pt1, Point3f pt2, short colix,
                                byte endcaps, float radius) {
    output("<Transform");
    outputTransRot(pt1, pt2, 0, 1, 0);
    output(">\n");
    outputCylinderChild(pt1, pt2, colix, endcaps, radius);
    output("\n</Transform>\n");
    if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
      outputSphere(pt1, radius * 1.01f, colix);
      outputSphere(pt2, radius * 1.01f, colix);
    }
  }

  private void outputCylinderChild(Point3f pt1, Point3f pt2, short colix,
                                   byte endcaps, float radius) {
    float length = pt1.distance(pt2);
    String child = useTable.getDef("C" + colix + "_" + (int) (length * 100) + "_"
        + radius + "_" + endcaps);
    output("<Shape ");
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Cylinder ");
      String cyl = useTable.getDef("c" + round(length) + "_" + endcaps + "_" + radius);
      if (cyl.charAt(0) == '_') {
        output("DEF='"
            + cyl
            + "' height='"
            + round(length)
            + "' radius='"
            + radius
            + "'"
            + (endcaps == Graphics3D.ENDCAPS_FLAT ? ""
                : " top='FALSE' bottom='FALSE'") + "/>");
      } else {
        output(cyl + "/>");
      }
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>");
  }
  
  protected void outputEllipsoid(Point3f center, Point3f[] points, short colix) {
    output("<Transform translation='");
    output(center);
    output("'");
    
    
    
    
    
    AxisAngle4f a = Quaternion.getQuaternionFrame(center, points[1], points[3]).toAxisAngle4f();
    if (!Float.isNaN(a.x)) 
      output(" rotation='" + a.x + " " + a.y + " " + a.z + " " + a.angle + "'");
    tempP3.set(0, 0, 0);
    float sx = points[1].distance(center);
    float sy = points[3].distance(center);
    float sz = points[5].distance(center);
    output(" scale='" + sx + " " + sy + " " + sz + "'>");
    outputSphere(tempP3, 1.0f, colix);
    output("</Transform>\n");
  }

  protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                                  short[] colixes, int[][] indices,
                                  short[] polygonColixes,
                                  int nVertices, int nPolygons, int nFaces, BitSet bsFaces,
                                  int faceVertexMax, short colix, Vector colorList, Hashtable htColixes, Point3f offset) {
    output("<Shape>\n");
    outputAppearance(colix, false);
    output("<IndexedFaceSet \n");

    if (polygonColixes != null)
      output(" colorPerVertex='FALSE'\n");

    

    output("coordIndex='\n");
    int[] map = new int[nVertices];
    getCoordinateMap(vertices, map);
    outputIndices(indices, map, nPolygons, bsFaces, faceVertexMax);
    output("'\n");

    
    
    Vector vNormals = null;
    if (normals != null) {
      vNormals = new Vector();
      map = getNormalMap(normals, nVertices, vNormals);
      output("  solid='FALSE'\n  normalPerVertex='TRUE'\n  normalIndex='\n");
      outputIndices(indices, map, nPolygons, bsFaces, faceVertexMax);
      output("'\n");
    }      
    
    map = null;
    
    
        
    if (colorList != null) {
      output("  colorIndex='\n");
      outputColorIndices(indices, nPolygons, bsFaces, faceVertexMax, htColixes, colixes, polygonColixes);
      output("'\n");
    }    

    output(">\n");  
    
    
    
    output("<Coordinate point='\n");
    outputVertices(vertices, nVertices, offset);
    output("'/>\n");

    

    if (normals != null) {
      output("<Normal vector='\n");
      outputNormals(vNormals);
      vNormals = null;
      output("'/>\n");
    }

    

    if (colorList != null) {
      output("<Color color='\n");
      outputColors(colorList);
      output("'/>\n");
    }
   
    output("</IndexedFaceSet>\n");
    output("</Shape>\n");
    
  }

  protected void outputSphere(Point3f center, float radius, short colix) {
    output("<Transform translation='");
    output(center);
    output("'>\n<Shape ");
    String child = useTable.getDef("S" + colix + "_" + (int) (radius * 100));
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Sphere radius='" + radius + "'/>");
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  private void outputTransRot(Point3f pt1, Point3f pt2, int x, int y, int z) {    
    output(" ");
    outputTransRot(pt1, pt2, x, y, z, "='", "'");
  }
  
  protected void outputTriangle(Point3f pt1, Point3f pt2, Point3f pt3, short colix) {
    
    
    output("<Shape>\n");
    output("<IndexedFaceSet solid='FALSE' ");
    output("coordIndex='0 1 2 -1'>");
    output("<Coordinate point='");
    output(pt1);
    output(" ");
    output(pt2);
    output(" ");
    output(pt3);
    output("'/>");
    output("</IndexedFaceSet>\n");
    outputAppearance(colix, false);
    output("\n</Shape>\n");
  }

  protected void outputTextPixel(Point3f pt, int argb) {
    
    String color = rgbFractionalFromArgb(argb, ' ');
    output("<Transform translation='");
    output(pt);
    output("'>\n<Shape ");
    String child = useTable.getDef("p" + argb);
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Sphere radius='0.01'/>");
      output("<Appearance><Material diffuseColor='0 0 0' specularColor='0 0 0'"
        + " ambientIntensity='0.0' shininess='0.0' emissiveColor='" 
        + color + "'/></Appearance>'");
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    if (z < 3)
      z = viewer.getFrontPlane();
    String useFontStyle = font3d.fontStyle.toUpperCase();
    String preFontFace = font3d.fontFace.toUpperCase();
    String useFontFace = (preFontFace.equals("MONOSPACED") ? "TYPEWRITER"
        : preFontFace.equals("SERIF") ? "SERIF" : "SANS");
    output("<Transform translation='");
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    output(tempP1);
    output("'>");
    
    
    output("<Billboard ");
    String child = useTable.getDef("T" + colix + useFontFace + useFontStyle + "_" + text);
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "' axisOfRotation='0 0 0'>"
        + "<Transform translation='0.0 0.0 0.0'>"
        + "<Shape>");
      outputAppearance(colix, true);
      output("<Text string=" + Escape.escape(text) + ">");
      output("<FontStyle ");
      String fontstyle = useTable.getDef("F" + useFontFace + useFontStyle);
      if (fontstyle.charAt(0) == '_') {
        output("DEF='" + fontstyle + "' size='0.4' family='" + useFontFace
            + "' style='" + useFontStyle + "'/>");      
      } else {
        output(fontstyle + "/>");
      }
      output("</Text>");
      output("</Shape>");
      output("</Transform>");
    } else {
      output(child + ">");
    }
    output("</Billboard>\n");
    output("</Transform>\n");

    
  }


}
