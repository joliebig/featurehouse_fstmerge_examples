package org.jmol.jvxl.api;

import javax.vecmath.Point3f;

import org.jmol.jvxl.data.MeshData;
import org.jmol.shapesurface.IsosurfaceMesh;

import java.util.BitSet;

public interface MeshDataServer extends VertexDataServer {
  
  
  
  public abstract void invalidateTriangles();
  public abstract void fillMeshData(MeshData meshData, int mode, IsosurfaceMesh mesh);
  public abstract void notifySurfaceGenerationCompleted();
  public abstract void notifySurfaceMappingCompleted();
  public abstract Point3f[] calculateGeodesicSurface(BitSet bsSelected, float envelopeRadius);  
}
