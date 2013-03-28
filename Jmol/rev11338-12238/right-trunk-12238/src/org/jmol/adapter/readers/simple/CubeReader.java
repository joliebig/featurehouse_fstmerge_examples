

package org.jmol.adapter.readers.simple;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;



public class CubeReader extends AtomSetCollectionReader {
    
  boolean negativeAtomCount;
  int atomCount;
  boolean isAngstroms = false;
  
  final int[] voxelCounts = new int[3];
  final float[] origin = new float[3];
  final float[][] voxelVectors = new float[3][];
  
 public void readAtomSetCollection(BufferedReader br) {
    reader = br;
    atomSetCollection = new AtomSetCollection("cube", this);
    try {
      atomSetCollection.newAtomSet();
      readTitleLines();
      readAtomCountAndOrigin();
      readVoxelVectors();
      readAtoms();
      readExtraLine();
      
    } catch (Exception e) {
      setError(e);
    }

  }

  void readTitleLines() throws Exception {
    if (readLine().indexOf("#JVXL") == 0)
      while (readLine().indexOf("#") == 0) {
      }
    atomSetCollection.setAtomSetName(line.trim() + " - " + readLineTrimmed());
  }

  void readAtomCountAndOrigin() throws Exception {
    readLine();
    isAngstroms = (line.indexOf("ANGSTROMS") >= 0); 
    String[] tokens = getTokens();
    if (tokens[0].charAt(0) == '+') 
      tokens[0] = '-' + tokens[0].substring(1);
    atomCount = parseInt(tokens[0]);
    origin[0] = parseFloat(tokens[1]);
    origin[1] = parseFloat(tokens[2]);
    origin[2] = parseFloat(tokens[3]);
    if (atomCount < 0) {
      atomCount = -atomCount;
      negativeAtomCount = true;
    }
  }
  
  void readVoxelVectors() throws Exception {
    readVoxelVector(0);
    readVoxelVector(1);
    readVoxelVector(2);
  }

  void readVoxelVector(int voxelVectorIndex) throws Exception {
    readLine();
    float[] voxelVector = new float[3];
    voxelVectors[voxelVectorIndex] = voxelVector;
    voxelCounts[voxelVectorIndex] = parseInt(line);
    voxelVector[0] = parseFloat();
    voxelVector[1] = parseFloat();
    voxelVector[2] = parseFloat();
  }

  void readAtoms() throws Exception {
    for (int i = 0; i < atomCount; ++i) {
      readLine();
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementNumber = (short)parseInt(line); 
      atom.partialCharge = parseFloat();
      atom.x = parseFloat();
      atom.y = parseFloat();
      atom.z = parseFloat();
      if (!isAngstroms)
        atom.scale(ANGSTROMS_PER_BOHR);
    }
  }

  void readExtraLine() throws Exception {
    if (negativeAtomCount)
      readLine();
    int nSurfaces = parseInt(line);
    if (nSurfaces != Integer.MIN_VALUE && nSurfaces < 0)
      atomSetCollection.setFileTypeName("jvxl");
  }
}
