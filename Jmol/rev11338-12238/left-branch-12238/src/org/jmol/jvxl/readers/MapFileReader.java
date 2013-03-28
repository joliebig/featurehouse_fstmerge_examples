
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import javax.vecmath.Point3f;

import org.jmol.api.Interface;
import org.jmol.api.SymmetryInterface;
import org.jmol.util.Logger;

abstract class MapFileReader extends VolumeFileReader {

  MapFileReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isAngstroms = true;
    adjustment = sg.getParams().center;
    if (adjustment.x == Float.MAX_VALUE)
      adjustment = new Point3f();
  }

    

    protected int mapc, mapr, maps;
    protected int nx, ny, nz, mode;
    protected int[] nxyzStart = new int[3];
    protected int na, nb, nc;
    protected float a, b, c, alpha, beta, gamma;
    protected Point3f origin = new Point3f();
    
    protected Point3f adjustment = new Point3f();
    

    protected Point3f[] vectors = new Point3f[3];
    
    abstract protected void readParameters() throws Exception;
    
    protected int readVolumetricHeader() {
      try {
        readParameters();
        int downsampleFactor = params.downsampleFactor;
        boolean downsampling = (canDownsample && downsampleFactor > 0);
        if (downsampling) {
          downsampleRemainders = new int[3];
          Logger.info("downsample factor = " + downsampleFactor);
          for (int i = 0; i < 3; ++i) {
            int n = voxelCounts[i];
            downsampleRemainders[i] = n % downsampleFactor;
            voxelCounts[i] /= downsampleFactor;
            volumetricVectors[i].scale(downsampleFactor);
            Logger.info("downsampling axis " + (i + 1) + " from " + n + " to "
                + voxelCounts[i]);
          }
        }
        for (int i = 0; i < 3; ++i) {
          line = voxelCounts[i] + " " + volumetricVectors[i].x + " "
              + volumetricVectors[i].y + " " + volumetricVectors[i].z;
          jvxlFileHeaderBuffer.append(line).append('\n');
          Logger.info("voxel grid count/vector:" + line);
        }
        JvxlReader.jvxlReadAtoms(br, jvxlFileHeaderBuffer, atomCount, volumeData);
        return 1;
      } catch (Exception e) {
        Logger.error(e.toString());
        return 0;
      }
    }

    protected void getVectorsAndOrigin() {
      
      Logger.info("grid parameters: nx,ny,nz: " + nx + "," + ny + "," + nz);
      Logger.info("grid parameters: nxStart,nyStart,nzStart: " 
          + nxyzStart[0] + "," + nxyzStart[1] + "," + nxyzStart[2]);

      Logger.info("grid parameters: mx,my,mz: " + na + "," + nb + "," + nc);
      Logger.info("grid parameters: a,b,c,alpha,beta,gamma: " + a + "," + b + "," + c + "," + alpha + "," + beta + "," + gamma);
      Logger.info("grid parameters: mapc,mapr,maps: " + mapc + "," + mapr + "," + maps);
      Logger.info("grid parameters: originX,Y,Z: " + origin);
      

      SymmetryInterface unitCell;
      
      unitCell = (SymmetryInterface) Interface
          .getOptionInterface("symmetry.Symmetry");
      unitCell.setUnitCell(new float[] { a, b, c, alpha, beta, gamma });

                       
        
      vectors[0] = new Point3f(1f / na, 0, 0);
      vectors[1] = new Point3f(0, 1f / nb, 0);
      vectors[2] = new Point3f(0, 0, 1f / nc);
      unitCell.toCartesian(vectors[0]);
      unitCell.toCartesian(vectors[1]);
      unitCell.toCartesian(vectors[2]);

      Logger.info("Jmol unit cell vectors:");
      Logger.info("    a: " + vectors[0]);
      Logger.info("    b: " + vectors[1]);
      Logger.info("    c: " + vectors[2]);

      voxelCounts[0] = nz; 
      voxelCounts[1] = ny;
      voxelCounts[2] = nx; 
      
      volumetricVectors[0].set(vectors[maps - 1]);
      volumetricVectors[1].set(vectors[mapr - 1]);
      volumetricVectors[2].set(vectors[mapc - 1]);

      if (origin.x == 0 && origin.y == 0 && origin.z == 0) {
        
        
        
        int[] xyz2crs = new int[3];
        xyz2crs[mapc-1] = 0;        
        xyz2crs[mapr-1] = 1;        
        xyz2crs[maps-1] = 2;        
        int xIndex = xyz2crs[0];    
        int yIndex = xyz2crs[1];    
        int zIndex = xyz2crs[2];    
        
        origin.scaleAdd(nxyzStart[xIndex] + adjustment.x, vectors[0], origin);
        origin.scaleAdd(nxyzStart[yIndex] + adjustment.y, vectors[1], origin);
        origin.scaleAdd(nxyzStart[zIndex] + adjustment.z, vectors[2], origin);

      }
      
      volumetricOrigin.set(origin);

      Logger.info("Jmol grid origin in Cartesian coordinates: " + origin);
      Logger.info("Use  isosurface OFFSET {x y z}  if you want to shift it.");
        
      

    }    
  
}
