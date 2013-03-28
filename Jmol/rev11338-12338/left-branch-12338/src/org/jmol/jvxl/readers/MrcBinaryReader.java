
package org.jmol.jvxl.readers;

import org.jmol.util.BinaryDocument;
import org.jmol.util.Logger;

class MrcBinaryReader extends MapFileReader {

  

  
  MrcBinaryReader(SurfaceGenerator sg, String fileName, boolean isBigEndian) {
    super(sg, null);
    binarydoc = new BinaryDocument();
    binarydoc.setStream(sg.getAtomDataServer().getBufferedInputStream(fileName), isBigEndian);
    
    params.insideOut = !params.insideOut;
  }
  

    
    
  protected String[] labels;

  protected void readParameters() throws Exception {

    float dmin, dmax, dmean;
    int ispg;
    int nsymbt;
    byte[] extra = new byte[100];
    byte[] map = new byte[4];
    byte[] machst = new byte[4];
    float rms;
    int nlabel;

    nx = binarydoc.readInt(); 
    ny = binarydoc.readInt();
    nz = binarydoc.readInt();

    mode = binarydoc.readInt();

    Logger.info("MRC header: mode: " + mode);

    nxyzStart[0] = binarydoc.readInt(); 
    nxyzStart[1] = binarydoc.readInt();
    nxyzStart[2] = binarydoc.readInt();

    na = binarydoc.readInt(); 
    nb = binarydoc.readInt();
    nc = binarydoc.readInt();

    if (na == 0)
      na = nx - 1;
    if (nb == 0)
      nb = ny - 1;
    if (nc == 0)
      nc = nz - 1;

    a = binarydoc.readFloat();
    b = binarydoc.readFloat();
    c = binarydoc.readFloat();
    alpha = binarydoc.readFloat();
    beta = binarydoc.readFloat();
    gamma = binarydoc.readFloat();

    mapc = binarydoc.readInt(); 
    mapr = binarydoc.readInt();
    maps = binarydoc.readInt();

    dmin = binarydoc.readFloat();
    dmax = binarydoc.readFloat();
    dmean = binarydoc.readFloat();

    Logger.info("MRC header: dmin,dmax,dmean: " + dmin + "," + dmax + ","
        + dmean);

    ispg = binarydoc.readInt();
    nsymbt = binarydoc.readInt();

    Logger.info("MRC header: ispg,nsymbt: " + ispg + "," + nsymbt);

    binarydoc.readByteArray(extra);

    origin.x = binarydoc.readFloat(); 
    origin.y = binarydoc.readFloat();
    origin.z = binarydoc.readFloat();

    binarydoc.readByteArray(map);
    binarydoc.readByteArray(machst);

    rms = binarydoc.readFloat();

    Logger.info("MRC header: rms: " + rms);

    nlabel = binarydoc.readInt();

    Logger.info("MRC header: labels: " + nlabel);

    labels = new String[nlabel];
    labels[0] = "Jmol MrcBinaryReader";

    for (int i = 0; i < 10; i++) {
      String s = binarydoc.readString(80).trim();
      if (i < nlabel) {
        labels[i] = s;
        Logger.info(labels[i]);
      }
    }

    for (int i = 0; i < nsymbt; i++) {
      long position = binarydoc.getPosition();
      String s = binarydoc.readString(80).trim();
      if (s.indexOf('\0') != s.lastIndexOf('\0')) {
        
        Logger.error("File indicates " + nsymbt + " symmetry lines, but " + i
            + " found!");
        binarydoc.seek(position);
        break;
      }
      Logger.info("MRC file symmetry information: " + s);
    }

    Logger.info("MRC header: bytes read: " + binarydoc.getPosition() + "\n");

    
    

    if (params.cutoff == 0.02f) {    
      params.cutoff = rms * 2 + dmean;
      Logger.info("Cutoff set to (dmean + 2*rms) = " + params.cutoff);
    }


    getVectorsAndOrigin();

    Logger.info("\n");

    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("MRC DATA ").append(labels[0]).append("\n");
    jvxlFileHeaderBuffer
        .append("see http://ami.scripps.edu/software/mrctools/mrc_specification.php\n");
  }
  
  protected float nextVoxel() throws Exception {
    float voxelValue;
    
    switch(mode) {
    case 0:
      voxelValue = binarydoc.readByte();
      break;
    case 1:
      voxelValue = binarydoc.readShort();
      break;
    default:
    case 2:
      voxelValue = binarydoc.readFloat();
      break;
    case 3:
      
      voxelValue = binarydoc.readShort();
      binarydoc.readShort();
      break;
    case 4:
      
      voxelValue = binarydoc.readFloat();
      binarydoc.readFloat();
      break;
    case 6:
      voxelValue = binarydoc.readUnsignedShort();
      break;
    }
    nBytes = binarydoc.getPosition();
    return voxelValue;
  }

  private static byte[] b8 = new byte[8];
  
  protected void skipData(int nPoints) throws Exception {
    for (int i = 0; i < nPoints; i++)
      switch(mode) {
      case 0:
        binarydoc.readByte();
        break;
      case 1:
      case 6:
        binarydoc.readByteArray(b8, 0, 2);
        break;
      default:
      case 2:
      case 3:
        binarydoc.readByteArray(b8, 0, 4);
        break;
      case 4:
        binarydoc.readByteArray(b8);
        break;
      }
  }
}
