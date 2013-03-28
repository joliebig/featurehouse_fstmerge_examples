

package org.jmol.shapespecial;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomIndexIterator;
import org.jmol.modelset.Bond;
import org.jmol.shape.AtomShape;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.ArrayUtil;

import java.util.BitSet;
import java.util.Hashtable;
import javax.vecmath.Point3i;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.Measure;
import org.jmol.viewer.JmolConstants;

public class Polyhedra extends AtomShape {

  private final static float DEFAULT_DISTANCE_FACTOR = 1.85f;
  private final static float DEFAULT_FACECENTEROFFSET = 0.25f;
  private final static int EDGES_NONE = 0;
  final static int EDGES_ALL = 1;
  final static int EDGES_FRONT = 2;
  private final static int MAX_VERTICES = 150;
  private final static int FACE_COUNT_MAX = MAX_VERTICES - 3;
  private Atom[] otherAtoms = new Atom[MAX_VERTICES];

  int polyhedronCount;
  Polyhedron[] polyhedrons = new Polyhedron[32];
  int drawEdges;

  private float radius;
  private int nVertices;

  float faceCenterOffset;
  float distanceFactor;
  boolean isCollapsed;

  private boolean iHaveCenterBitSet;
  private boolean bondedOnly;
  private boolean haveBitSetVertices;

  private BitSet centers;
  private BitSet bsVertices;
  private BitSet bsVertexCount;

  public void setProperty(String propertyName, Object value, BitSet bs) {

    if (Logger.debugging) {
      Logger.debug("polyhedra: " + propertyName + " " + value);
    }

    if ("init" == propertyName) {
      faceCenterOffset = DEFAULT_FACECENTEROFFSET;
      distanceFactor = DEFAULT_DISTANCE_FACTOR;
      radius = 0.0f;
      nVertices = 0;
      bsVertices = null;
      centers = null;
      bsVertexCount = new BitSet();
      bondedOnly = isCollapsed = iHaveCenterBitSet = false;
      drawEdges = EDGES_NONE;
      haveBitSetVertices = false;
      return;
    }

    if ("generate" == propertyName) {
      if (!iHaveCenterBitSet)
        centers = bs;
      deletePolyhedra();
      buildPolyhedra();
      return;
    }

    if ("collapsed" == propertyName) {
      isCollapsed = ((Boolean) value).booleanValue();
      return;
    }

    if ("nVertices" == propertyName) {
      nVertices = ((Integer) value).intValue();
      bsVertexCount.set(nVertices);
      return;
    }

    if ("centers" == propertyName) {
      centers = (BitSet) value;
      iHaveCenterBitSet = true;
      return;
    }

    if ("to" == propertyName) {
      bsVertices = (BitSet) value;
      return;
    }

    if ("toBitSet" == propertyName) {
      bsVertices = (BitSet) value;
      haveBitSetVertices = true;
      return;
    }

    if ("faceCenterOffset" == propertyName) {
      faceCenterOffset = ((Float) value).floatValue();
      return;
    }

    if ("distanceFactor" == propertyName) {
      
      distanceFactor = ((Float) value).floatValue();
      return;
    }

    if ("bonds" == propertyName) {
      bondedOnly = true;
      return;
    }

    if ("delete" == propertyName) {
      if (!iHaveCenterBitSet)
        centers = bs;
      deletePolyhedra();
      return;
    }
    if ("on" == propertyName) {
      if (!iHaveCenterBitSet)
        centers = bs;
      setVisible(true);
      return;
    }
    if ("off" == propertyName) {
      if (!iHaveCenterBitSet)
        centers = bs;
      setVisible(false);
      return;
    }
    if ("noedges" == propertyName) {
      drawEdges = EDGES_NONE;
      return;
    }
    if ("edges" == propertyName) {
      drawEdges = EDGES_ALL;
      return;
    }
    if ("frontedges" == propertyName) {
      drawEdges = EDGES_FRONT;
      return;
    }
    if (propertyName.indexOf("color") == 0) {
      
      
      if ("colorThis" == propertyName && iHaveCenterBitSet)
        bs = centers;
      else
        andBitSet(bs);
      propertyName = "color";
      
    }

    if (propertyName.indexOf("translucency") == 0) {
      
      
      if ("translucencyThis" == propertyName && iHaveCenterBitSet)
        bs = centers;
      else
        andBitSet(bs);
      
    }

    if ("radius" == propertyName) {
      radius = ((Float) value).floatValue();
      return;
    }

    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      for (int i = polyhedronCount; --i >= 0;) {
        if (polyhedrons[i].modelIndex == modelIndex) {
          polyhedronCount--;
          polyhedrons = (Polyhedron[]) ArrayUtil.deleteElements(polyhedrons, i, 1);
        } else if (polyhedrons[i].modelIndex > modelIndex) {
          polyhedrons[i].modelIndex--;
        }
      }
      
    }

    super.setProperty(propertyName, value, bs);
  }

  private void andBitSet(BitSet bs) {
    BitSet bsCenters = new BitSet();
    for (int i = polyhedronCount; --i >= 0;)
      bsCenters.set(polyhedrons[i].centralAtom.getAtomIndex());
    bs.and(bsCenters);
  }

  private void deletePolyhedra() {
    int newCount = 0;
    byte pid = JmolConstants.pidOf(null);
    for (int i = 0; i < polyhedronCount; ++i) {
      Polyhedron p = polyhedrons[i];
      int iAtom = p.centralAtom.getAtomIndex();
      if (centers.get(iAtom))
        setColixAndPalette(Graphics3D.INHERIT_ALL, pid, iAtom);
      else
        polyhedrons[newCount++] = p;
    }
    for (int i = newCount; i < polyhedronCount; ++i)
      polyhedrons[i] = null;
    polyhedronCount = newCount;
  }

  private void setVisible(boolean visible) {
    for (int i = polyhedronCount; --i >= 0;) {
      Polyhedron p = polyhedrons[i];
      if (p == null)
        continue;
      if (centers.get(p.centralAtom.getAtomIndex()))
        p.visible = visible;
    }
  }

  private void buildPolyhedra() {
    boolean useBondAlgorithm = radius == 0 || bondedOnly;
    for (int i = atomCount; --i >= 0;)
      if (centers.get(i)) {
        Polyhedron p = (haveBitSetVertices ? constructBitSetPolyhedron(i)
            : useBondAlgorithm ? constructBondsPolyhedron(i)
                : constructRadiusPolyhedron(i));
        if (p != null) {
          if (polyhedronCount == polyhedrons.length)
            polyhedrons = (Polyhedron[]) ArrayUtil.doubleLength(polyhedrons);
          polyhedrons[polyhedronCount++] = p;
        }
        if (haveBitSetVertices)
          return;
      }
  }

  private Polyhedron constructBondsPolyhedron(int atomIndex) {
    Atom atom = atoms[atomIndex];
    Bond[] bonds = atom.getBonds();
    if (bonds == null)
      return null;
    int bondCount = 0;
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      Atom otherAtom = bond.getAtom1() == atom ? bond.getAtom2() : bond
          .getAtom1();
      if (bsVertices != null && !bsVertices.get(otherAtom.getAtomIndex()))
        continue;
      if (radius > 0f && bond.getAtom1().distance(bond.getAtom2()) > radius)
        continue;
      otherAtoms[bondCount++] = otherAtom;
      if (bondCount == MAX_VERTICES)
        break;
    }
    if (bondCount < 3 || nVertices > 0 && !bsVertexCount.get(bondCount))
      return null;
    return validatePolyhedronNew(atom, bondCount, otherAtoms);
  }

  private Polyhedron constructBitSetPolyhedron(int atomIndex) {
    int otherAtomCount = 0;
    for (int i = atomCount; --i >= 0;)
      if (bsVertices.get(i))
        otherAtoms[otherAtomCount++] = atoms[i];
    return validatePolyhedronNew(atoms[atomIndex], otherAtomCount, otherAtoms);
  }

  private Polyhedron constructRadiusPolyhedron(int atomIndex) {
    Atom atom = atoms[atomIndex];
    int otherAtomCount = 0;
    AtomIndexIterator withinIterator = viewer.getWithinModelIterator(atom, radius);
    while (withinIterator.hasNext()) {
      Atom other = atoms[withinIterator.next()];
      if (other == atom 
          || bsVertices != null && !bsVertices.get(other.getAtomIndex())
          || atom.distance(other) > radius)
        continue;
      if (other.getAlternateLocationID() != atom.getAlternateLocationID()
          && other.getAlternateLocationID() != 0
          && atom.getAlternateLocationID() != 0)
        continue;
      if (otherAtomCount == MAX_VERTICES)
        break;
      otherAtoms[otherAtomCount++] = other;
    }
    if (otherAtomCount < 3 || nVertices > 0
        && !bsVertexCount.get(otherAtomCount))
      return null;
    return validatePolyhedronNew(atom, otherAtomCount, otherAtoms);
  }

  private short[] normixesT = new short[MAX_VERTICES];
  private byte[] planesT = new byte[MAX_VERTICES * 3];
  private final static Point3f randomPoint = new Point3f(3141f, 2718f, 1414f);

  private Polyhedron validatePolyhedronNew(Atom centralAtom, int vertexCount,
                                   Atom[] otherAtoms) {
    Vector3f normal = new Vector3f();
    int planeCount = 0;
    int ipt = 0;
    int ptCenter = vertexCount;
    int nPoints = ptCenter + 1;
    float distMax = 0;
    float dAverage = 0;

    Point3f[] points = new Point3f[MAX_VERTICES * 3];
    points[ptCenter] = centralAtom;
    otherAtoms[ptCenter] = centralAtom;
    for (int i = 0; i < ptCenter; i++) {
      points[i] = otherAtoms[i];
      dAverage += points[ptCenter].distance(points[i]);
    }
    dAverage = dAverage / ptCenter;
    float factor = distanceFactor;
    BitSet bs = new BitSet(ptCenter);
    boolean isOK = (dAverage == 0);

    
    

    while (!isOK && factor < 10.0f) {
      distMax = dAverage * factor;
      for (int i = 0; i < ptCenter; i++)
        bs.set(i);
      for (int i = 0; i < ptCenter - 2; i++)
        for (int j = i + 1; j < ptCenter - 1; j++) {
          if (points[i].distance(points[j]) > distMax)
            continue;
          for (int k = j + 1; k < ptCenter; k++) {
            if (points[i].distance(points[k]) > distMax
                || points[j].distance(points[k]) > distMax)
              continue;
            bs.clear(i);
            bs.clear(j);
            bs.clear(k);
          }
        }
      isOK = true;
      for (int i = 0; i < ptCenter; i++)
        if (bs.get(i)) {
          isOK = false;
          factor *= 1.05f;
          if (Logger.debugging) {
            Logger.debug("Polyhedra distanceFactor for " + ptCenter
                + " atoms increased to " + factor + " in order to include "
                + otherAtoms[i].getInfo());
          }
          break;
        }
    }

    

    
    String faceCatalog = "";
    String facetCatalog = "";
    for (int i = 0; i < ptCenter - 2; i++)
      for (int j = i + 1; j < ptCenter - 1; j++)
        for (int k = j + 1; k < ptCenter; k++)
          if (isPlanar(points[i], points[j], points[k], points[ptCenter]))
            faceCatalog += faceId(i, j, k);
    for (int j = 0; j < ptCenter - 1; j++)
      for (int k = j + 1; k < ptCenter; k++) {
        if (isAligned(points[j], points[k], points[ptCenter]))
          facetCatalog += faceId(j, k, -1);
      }
    Point3f ptRef = new Point3f();
    
    for (int i = 0; i < ptCenter - 2; i++)
      for (int j = i + 1; j < ptCenter - 1; j++) {
        if (points[i].distance(points[j]) > distMax)
          continue;
        for (int k = j + 1; k < ptCenter; k++) {
          if (points[i].distance(points[k]) > distMax
              || points[j].distance(points[k]) > distMax)
            continue;

          if (planeCount >= FACE_COUNT_MAX) {
            Logger.error("Polyhedron error: maximum face(" + FACE_COUNT_MAX
                + ") -- reduce RADIUS or DISTANCEFACTOR");
            return null;
          }
          if (nPoints >= MAX_VERTICES) {
            Logger.error("Polyhedron error: maximum vertex count("
                + MAX_VERTICES + ") -- reduce RADIUS");
            return null;
          }
          boolean isFaceCentered = (faceCatalog.indexOf(faceId(i, j, k)) >= 0);
          
          
          if (isFaceCentered)
            Measure.getNormalFromCenter(randomPoint, points[i], points[j],
                points[k], false, normal);
          else
            Measure.getNormalFromCenter(points[ptCenter], points[i],
                points[j], points[k], true, normal);
          normal.scale(isCollapsed && !isFaceCentered ? faceCenterOffset
              : 0.001f);
          int nRef = nPoints;
          if (isCollapsed && !isFaceCentered) {
            points[nPoints] = new Point3f(points[ptCenter]);
            points[nPoints].add(normal);
            otherAtoms[nPoints] = new Atom(points[nPoints]);
          } else if (isFaceCentered) {
            ptRef.set(points[ptCenter]);
            ptRef.sub(normal);
            nRef = ptCenter;
          }
          String facet;
          facet = faceId(i, j, -1);
          if (isCollapsed || isFaceCentered && facetCatalog.indexOf(facet) < 0) {
            facetCatalog += facet;
            planesT[ipt++] = (byte) i;
            planesT[ipt++] = (byte) j;
            planesT[ipt++] = (byte) nRef;
            Measure.getNormalFromCenter(points[k], points[i], points[j],
                ptRef, false, normal);
            normixesT[planeCount++] = (isFaceCentered ? g3d
                .get2SidedNormix(normal) : g3d.getNormix(normal));
          }
          facet = faceId(i, k, -1);
          if (isCollapsed || isFaceCentered && facetCatalog.indexOf(facet) < 0) {
            facetCatalog += facet;
            planesT[ipt++] = (byte) i;
            planesT[ipt++] = (byte) nRef;
            planesT[ipt++] = (byte) k;
            Measure.getNormalFromCenter(points[j], points[i], ptRef,
                points[k], false, normal);
            normixesT[planeCount++] = (isFaceCentered ? g3d
                .get2SidedNormix(normal) : g3d.getNormix(normal));
          }
          facet = faceId(j, k, -1);
          if (isCollapsed || isFaceCentered && facetCatalog.indexOf(facet) < 0) {
            facetCatalog += facet;
            planesT[ipt++] = (byte) nRef;
            planesT[ipt++] = (byte) j;
            planesT[ipt++] = (byte) k;
            Measure.getNormalFromCenter(points[i], ptRef, points[j],
                points[k], false, normal);
            normixesT[planeCount++] = (isFaceCentered ? g3d
                .get2SidedNormix(normal) : g3d.getNormix(normal));
          }
          if (!isFaceCentered) {
            if (isCollapsed) {
              nPoints++;
            } else {
              
              planesT[ipt++] = (byte) i;
              planesT[ipt++] = (byte) j;
              planesT[ipt++] = (byte) k;
              normixesT[planeCount++] = g3d.getNormix(normal);
            }
          }
        }
      }
    
    return new Polyhedron(centralAtom, ptCenter, nPoints, planeCount,
        otherAtoms, normixesT, planesT);
  }

  private String faceId(int i, int j, int k) {
    return "" + (new Point3i(i, j, k));
  }

  private Vector3f align1 = new Vector3f();
  private Vector3f align2 = new Vector3f();

  private boolean isAligned(Point3f pt1, Point3f pt2, Point3f pt3) {
    align1.sub(pt1, pt3);
    align2.sub(pt2, pt3);
    float angle = align1.angle(align2);
    return (angle < 0.01f || angle > 3.13f);
  }

  private final Vector3f vAB = new Vector3f();
  private final Vector3f vAC = new Vector3f();

  private static float minDistanceForPlanarity = 0.1f;

  private boolean isPlanar(Point3f pt1, Point3f pt2, Point3f pt3, Point3f ptX) {
    
    Vector3f norm = new Vector3f();
    float w = Measure.getNormalThroughPoints(pt1, pt2, pt3, norm, vAB, vAC);
    float d = Measure.distanceToPlane(norm, w, ptX);
    return (Math.abs(d) < minDistanceForPlanarity);
  }

  class Polyhedron {
    int modelIndex;
    final Atom centralAtom;
    final Atom[] vertices;
    int ptCenter;
    boolean visible;
    final short[] normixes;
    byte[] planes;
    
    int visibilityFlags = 0;
    boolean collapsed = false;
    float myFaceCenterOffset, myDistanceFactor;

    Polyhedron(Atom centralAtom, int ptCenter, int nPoints, int planeCount,
        Atom[] otherAtoms, short[] normixes, byte[] planes) {
      this.collapsed = isCollapsed;
      this.centralAtom = centralAtom;
      modelIndex = centralAtom.getModelIndex();
      this.ptCenter = ptCenter;
      this.vertices = new Atom[nPoints];
      this.visible = true;
      this.normixes = new short[planeCount];
      
      this.planes = new byte[planeCount * 3];
      myFaceCenterOffset = faceCenterOffset;
      myDistanceFactor = distanceFactor;
      for (int i = nPoints; --i >= 0;)
        vertices[i] = otherAtoms[i];
      for (int i = planeCount; --i >= 0;)
        this.normixes[i] = normixes[i];
      for (int i = planeCount * 3; --i >= 0;)
        this.planes[i] = planes[i];
    }

    protected String getState(Hashtable temp) {
      BitSet bs = new BitSet();
      for (int i = 0; i < ptCenter; i++)
        bs.set(vertices[i].getAtomIndex());
      return "  polyhedra ({" + centralAtom.getAtomIndex() + "}) "
          + (myDistanceFactor == DEFAULT_DISTANCE_FACTOR ? ""
              : " distanceFactor " + myDistanceFactor)
          + (myFaceCenterOffset == DEFAULT_FACECENTEROFFSET ? ""
              : " faceCenterOffset " + myFaceCenterOffset) + " to "
          + Escape.escape(bs) + (collapsed ? " collapsed" : "") + ";"
          + (visible ? "" : "polyhedra off;") + "\n";
    }
  }

  public void setVisibilityFlags(BitSet bs) {
    
    for (int i = polyhedronCount; --i >= 0;) {
      Polyhedron p = polyhedrons[i];
      p.visibilityFlags = (p.visible && bs.get(p.modelIndex)
          && !modelSet.isAtomHidden(p.centralAtom.getAtomIndex()) ? myVisibilityFlag
          : 0);
    }
  }

  public String getShapeState() {
    Hashtable temp = new Hashtable();
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < polyhedronCount; i++)
      s.append(polyhedrons[i].getState(temp));
    if (drawEdges == EDGES_FRONT)
      appendCmd(s, "polyhedra frontedges");
    else if (drawEdges == EDGES_ALL)
      appendCmd(s, "polyhedra edges");
    s.append(super.getShapeState());
    return s.toString();
  }
}
