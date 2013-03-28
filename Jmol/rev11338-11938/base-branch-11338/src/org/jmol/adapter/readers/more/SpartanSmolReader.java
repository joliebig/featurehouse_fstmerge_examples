

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.Hashtable;

import org.jmol.util.Logger;



public class SpartanSmolReader extends SpartanInputReader {

  public void readAtomSetCollection(BufferedReader reader) {
    modelName = "Spartan file";
    this.reader = reader;
    try {
      readLine();
      atomSetCollection = new AtomSetCollection("spartan smol");
      boolean iHaveModelStatement = false;
      boolean iHaveModel = false;
      while (line != null) {
        if (line.indexOf("JMOL_MODEL") >= 0 && !line.startsWith("END")) {

          

          if (modelNumber > 0)
            applySymmetryAndSetTrajectory();
          iHaveModelStatement = true;
          int modelNo = getModelNumber();
          modelNumber = (bsModels == null && modelNo != Integer.MIN_VALUE ? modelNo : modelNumber + 1);
          bondData = "";
          if (!doGetModel(modelNumber)) {
            if (isLastModel(modelNumber) && iHaveModel)
              break;
            iHaveModel = false;
            readLine();
            continue;
          }
          iHaveModel = true;
          atomSetCollection.newAtomSet();
          moData = new Hashtable();
          if (modelNo == Integer.MIN_VALUE) {
            modelNo = modelNumber;
            title = "Model " + line.substring(line.lastIndexOf(" ") + 1);
          } else  {
            title = (String) titles.get("Title" + modelNo);
            title = "Profile " + modelNo + (title == null ? "" : ": " + title);
          }
          Logger.info(title);
          atomSetCollection.setAtomSetAuxiliaryInfo("title", title);
          atomSetCollection.setAtomSetName(title);
          atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.FALSE);
          atomSetCollection.setAtomSetNumber(modelNo);
          readLine();
          continue;
        }
        if (iHaveModelStatement && !iHaveModel) {
          readLine();
          continue;
        }

        if ((line.indexOf("BEGIN") == 0)) {
          String lcline = line.toLowerCase();
          if (lcline.endsWith("input")) {
            bondData = "";
            readInputRecords();
            if (atomSetCollection.errorMessage != null)
              return;
            if (title != null)
              atomSetCollection.setAtomSetName(title);
          } else if (lcline.endsWith("_output")) {
            readLine();
            continue;
          } else if (lcline.endsWith("output")) {
            readOutput();
            continue;
          } else if (lcline.endsWith("molecule") || lcline.endsWith("molecule:asbinarystring")) {
            readTransform();
            continue;
          } else if (lcline.endsWith("proparc")
              || lcline.endsWith("propertyarchive")) {
            readProperties();
            continue;
          } else if (lcline.endsWith("archive")) {
            readArchive();
            continue;
          }
        }

        

        if (line != null && line.indexOf("5D shell") >= 0) {
          moData.put("calculationType", calculationType = line);
        }
        readLine();
      }
      if (atomCount > 0)
        applySymmetryAndSetTrajectory();

      
      if (atomCount > 0 && spartanArchive != null && atomSetCollection.getBondCount() == 0
          && bondData != null)
        spartanArchive.addBonds(bondData, 0);
    } catch (Exception e) {
      setError(e);
    }
  }

  private class MoleculeRecord {
    float[] mat;
    
    MoleculeRecord(String binaryCodes) {
      String[] tokens = getTokens(binaryCodes.trim());
      if (tokens.length < 16)
        return;
      byte[] bytes = new byte[tokens.length];
      for (int i = 0; i < tokens.length;i++)
        bytes[i] = (byte) Integer.parseInt(tokens[i], 16);
      mat = new float[16];
      for (int i = 16, j = bytes.length; --i >= 0; j -= 8)
        mat[i] = bytesToDoubleToFloat(bytes, j);        
    }
    
    private float bytesToDoubleToFloat(byte[] bytes, int j) {
      double d = Double.longBitsToDouble((((long) bytes[--j]) & 0xff) << 56
          | (((long) bytes[--j]) & 0xff) << 48
          | (((long) bytes[--j]) & 0xff) << 40 
          | (((long) bytes[--j]) & 0xff) << 32
          | (((long) bytes[--j]) & 0xff) << 24
          | (((long) bytes[--j]) & 0xff) << 16
          | (((long) bytes[--j]) & 0xff) << 8
          | (((long) bytes[--j]) & 0xff));
      return (float) d;
    }

    protected void setTrans() {
      if (mat == null)
        return;
      setTransform(
          mat[0], mat[1], mat[2], 
          mat[4], mat[5], mat[6], 
          mat[8], mat[9], mat[10]);
    }
  }

  private String endCheck = "END Directory Entry ";
  private Hashtable moData = new Hashtable();
  private String title;

  SpartanArchive spartanArchive;


  Hashtable titles;
  
  private void readOutput() throws Exception {
    titles = new Hashtable();
    String header = "";
    int pt;
    while (readLine() != null && !line.startsWith("END ")) {
      header += line + "\n";
      if ((pt = line.indexOf(")")) > 0)
        titles.put("Title"+parseInt(line.substring(0, pt))
            , (line.substring(pt + 1).trim()));
    }
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("fileHeader", header);
  }

  private void readArchive() throws Exception {
    spartanArchive = new SpartanArchive(this, atomSetCollection, moData,
        bondData, endCheck);
    if (readArchiveHeader()) {
      modelAtomCount = spartanArchive.readArchive(line, false, atomCount, false);
      if (atomCount == 0 || !isTrajectory)
        atomCount += modelAtomCount;
    }
  }
  
  private void readProperties() throws Exception {
    spartanArchive.readProperties();
    if (!atomSetCollection
        .setAtomSetCollectionPartialCharges("MULCHARGES"))
      atomSetCollection.setAtomSetCollectionPartialCharges("Q1_CHARGES");
    Float n = (Float) atomSetCollection
        .getAtomSetCollectionAuxiliaryInfo("HOMO_N");
    if (moData != null && n != null)
      moData.put("HOMO", new Integer(n.intValue()));
    readLine();
  }
  
  private int getModelNumber() {
    try {
      int pt = line.indexOf("JMOL_MODEL ") + 11;
      return parseInt(line, pt);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private void readTransform() throws Exception {
    
    
    
    MoleculeRecord mr = new MoleculeRecord(readLine());
    mr.setTrans();
  }

  private boolean readArchiveHeader()
      throws Exception {
    String modelInfo = readLine();
    Logger.debug(modelInfo);
    if (modelInfo.indexOf("Error:") == 0) 
      return false;
    atomSetCollection.setCollectionName(modelInfo);
    modelName = readLine();
    Logger.debug(modelName);
    
    readLine();
    return true;
  }

}
