
package org.jmol.adapter.readers.quantum; 

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;


import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.util.Logger;


public class CsfReader extends MopacReader {

  private int nAtoms = 0;
  private String strAtomicNumbers = "";
  private int fieldCount;
  private int nVibrations = 0;
  private int nGaussians = 0;
  private int nSlaters = 0;
  
 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("csf", this);
    try {
      readLine();
      while (line != null) {
        if (line.equals("local_transform")) {
          processLocalTransform();
        } else if (line.startsWith("object_class")) {
          processObjectClass();
          
          continue; 
        }
        readLine();
      }
    } catch (Exception e) {
      setError(e);
    }

  }
  private void processObjectClass() throws Exception {
    if (line.equals("object_class connector")) {
      processConnectorObject();
      return;
    }
    if (line.equals("object_class atom")) {
      processAtomObject();
      return;
    }
    if (line.equals("object_class bond")) {
      processBondObject();
      return;
    }
    if (line.equals("object_class vibrational_level")) {
      processVibrationObject();
      return;
    }
    if (line.equals("object_class mol_orbital")) {
      processMolecularOrbitalObject();
      return;
    }
    if (line.equals("object_class sto_basis_fxn")) {
      processBasisObject("sto");
      return;
    }
    if (line.equals("object_class gto_basis_fxn")) {
      processBasisObject("gto");
      return;
    }
    
    readLine();
  }

  
  private void processLocalTransform() throws Exception {
    String[] tokens = getTokens(readLine() + " " + readLine() + " "+ readLine() + " " + readLine());
    setTransform(
        parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]), 
        parseFloat(tokens[4]), parseFloat(tokens[5]), parseFloat(tokens[6]),
        parseFloat(tokens[8]), parseFloat(tokens[9]), parseFloat(tokens[10])
        );
  }

  private Hashtable propertyItemCounts = new Hashtable();
  private final int[] fieldTypes = new int[100]; 
  
  private int getPropertyCount(String what) {
    Integer count = (Integer)(propertyItemCounts.get(what));
    return (what.equals("ID") ? 1 : count == null ? 0 : count.intValue());
  }
  
  private int parseLineParameters(String[] fields,
                          byte[] fieldMap) throws Exception {
    for (int i = 0; i < fieldCount; i++)
      fieldTypes[i] = 0;
    fieldCount = -1;
    if (line == null || line.startsWith("property_flags:"))
      readLine();
    if (line == null || line.startsWith("object_class"))
      return fieldCount;

    String[] tokens = new String[0];
    

    while (line != null) {
      tokens = getTokens();
      if (line.indexOf("property ") == 0)
        propertyItemCounts.put(tokens[1], new Integer((tokens[6].equals("STRING") ? 1 : parseInt(tokens[5]))));
      else if (line.indexOf("ID") == 0)
        break;
      readLine();
    }
    
    for (int ipt = 0, fpt = 0; ipt < tokens.length; ipt++ ) {
      String field = tokens[ipt];
      for (int i = fields.length; --i >= 0; )
        if (field.equals(fields[i])) {
          fieldTypes[fpt] = fieldMap[i];
          fieldCount = fpt + 1;
          break;
        }
      fpt += getPropertyCount(field);
    }
    return fieldCount;
  }

  private void fillCsfArray(String property, String[] tokens, int i0, Object f)
      throws Exception {
    
    int n = getPropertyCount(property);
    int ioffset = i0;
    boolean isInteger = (f instanceof int[]);
    for (int i = 0; i < n; i++) {
      int ipt = ioffset + i;
      if (ipt == tokens.length) {
        tokens = getTokens(readLine());
        ioffset -= ipt - i0;
        ipt = i0;
      }
      if (isInteger)
        ((int[]) f)[i] = parseInt(tokens[ipt]);
      else
        ((float[]) f)[i] = parseFloat(tokens[ipt]);
    }
  }

  
  
  

  private final static byte objCls1 = 1;
  private final static byte objID1  = 2;
  private final static byte objCls2 = 3;
  private final static byte objID2  = 4;
  
  private final static String[] connectorFields = {
    "objCls1", "objID1", "objCls2", "objID2"
  };

  private final static byte[] connectorFieldMap = {
    objCls1, objID1, objCls2, objID2
  };
  
  private Hashtable connectors = new Hashtable();
  
  private void processConnectorObject() throws Exception {
    readLine();
    parseLineParameters(connectorFields, connectorFieldMap);
    out: for (; readLine() != null;) {
      if (line.startsWith("property_flags:"))
        break;
      String thisAtomID = null;
      String thisBondID = null;
      String tokens[] = getTokens();
      String field2 = "";
      boolean isVibration = false;
      for (int i = 0; i < fieldCount; ++i) {
        String field = tokens[i];
        switch (fieldTypes[i]) {
        case objCls1:
          if (!field.equals("atom"))
            continue out;
          break;
        case objCls2:
          field2 = field;
          if (field.equals("sto_basis_fxn"))
            nSlaters++;
          else if (field.equals("gto_basis_fxn"))
            nGaussians++;
          else if (field.equals("vibrational_level"))
            isVibration = true;
          else if (!field.equals("bond")) 
            continue out;
          break;
        case objID1:
          thisAtomID = "atom"+field;
          break;
        case objID2:
          thisBondID = field2+field;
          if (isVibration)
            nVibrations = Math.max(nVibrations, parseInt(field));
          break;
        default:
        }
      }
      if (thisAtomID != null && thisBondID != null) {
        if (connectors.containsKey(thisBondID)) {
          String[] connect = (String[])connectors.get(thisBondID);
          connect[1] = thisAtomID;
          
        } else {
          String[] connect = new String[2];
          connect[0] = thisAtomID;
          connectors.put(thisBondID, connect);
        }
      }
    }
  }

  
  
  

  private final static byte ID             = -1;

  private final static byte sym            = 1;
  private final static byte anum           = 2;
  private final static byte chrg           = 3;
  private final static byte xyz_coordinates = 4;
  private final static byte pchrg           = 5;
  

  private final static String[] atomFields = {
    "ID", "sym", "anum", "chrg", "xyz_coordinates", "pchrg"
  };

  private final static byte[] atomFieldMap = {
    ID, sym, anum, chrg, xyz_coordinates, pchrg
  };

  private void processAtomObject() throws Exception {
    readLine();
    parseLineParameters(atomFields, atomFieldMap);
    nAtoms = 0;
    for (; readLine() != null; ) {
      if (line.startsWith("property_flags:"))
        break;
      String tokens[] = getTokens();
      Atom atom = new Atom();
      for (int i = 0; i < fieldCount; i++) {
        String field = tokens[i];
        if (field == null)
          Logger.warn("field == null in " + line);
        switch (fieldTypes[i]) {
        case ID:
          atom.atomName = "atom"+field;
          break;
        case sym:
          atom.elementSymbol = field;
          break;
        case anum:
          strAtomicNumbers += field + " "; 
          break;
        case chrg:
          atom.formalCharge = parseInt(field);
          break;
        case pchrg:
          atom.partialCharge = parseFloat(field);
          break;
        case xyz_coordinates:
          setAtomCoord(atom, parseFloat(field), parseFloat(tokens[i + 1]), parseFloat(tokens[i + 2]));
          break;
        }
      }
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("atom " + atom.atomName + " has invalid/unknown coordinates");
      } else {
        nAtoms++;
        atomSetCollection.addAtomWithMappedName(atom);
      }
    }
  }

  
  
  

  private final static byte bondType = 1;

  private final static String[] bondFields  = {
    "ID", "type"
  };

  private final static byte[] bondFieldMap = {
    ID, bondType
  };

  private int nBonds = 0;
  
  private void processBondObject() throws Exception {
    readLine();
    parseLineParameters(bondFields, bondFieldMap);
    for (; readLine() != null;) {
      if (line.startsWith("property_flags:"))
        break;
      String thisBondID = null;
      String tokens[] = getTokens();
      for (int i = 0; i < fieldCount; ++i) {
        String field = tokens[i];
        switch (fieldTypes[i]) {
        case ID:
          thisBondID = "bond" + field;
          break;
        case bondType:
          int order = 1;
          if (field.equals("single"))
            order = 1;
          else if (field.equals("double"))
            order = 2;
          else if (field.equals("triple"))
            order = 3;
          else
            Logger.warn("unknown CSF bond order: " + field);
          String[] connect = (String[]) connectors.get(thisBondID);
          Bond bond = new Bond();
          bond.atomIndex1 = atomSetCollection.getAtomNameIndex(connect[0]);
          bond.atomIndex2 = atomSetCollection.getAtomNameIndex(connect[1]);
          bond.order = order;
          atomSetCollection.addBond(bond);
          nBonds++;
          break;
        }
      }
    }
  }

  
  private final static byte normalMode       = 1;
  private final static byte vibEnergy        = 2;
  private final static byte transitionDipole = 3;

  private final static String[] vibFields  = {
    "ID", "normalMode", "Energy", "transitionDipole"
  };

  private final static byte[] vibFieldMap = {
    ID, normalMode, vibEnergy, transitionDipole
  };

  private void processVibrationObject() throws Exception {
    
    float[][] vibData = new float[nVibrations][nAtoms * 3];
    float[] energies = new float[nVibrations];
    readLine();
    while (line != null && parseLineParameters(vibFields, vibFieldMap) > 0) {
      while (readLine() != null && !line.startsWith("property_flags:")) {
        String tokens[] = getTokens();
        int thisvib = -1;
        for (int i = 0; i < fieldCount; ++i) {
          String field = tokens[i];
          switch (fieldTypes[i]) {
          case ID:
            thisvib = parseInt(field) - 1;
            break;
          case normalMode:
            fillCsfArray("normalMode", tokens, i, vibData[thisvib]);
            break;
          case vibEnergy:
            energies[thisvib] = parseFloat(field);
            break;
          }
        }
      }
    }
    for (int i = 0; i < nVibrations; i++) {
      if (!doGetVibration(i + 1))
        continue;
      atomSetCollection.cloneFirstAtomSetWithBonds(nBonds);
      atomSetCollection.setAtomSetName(energies[i] + " cm^-1");
      atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
          "Frequencies");
      int ipt = 0;
      int baseAtom = nAtoms * (i + 1);
      for (int iAtom = 0; iAtom < nAtoms; iAtom++)
        atomSetCollection.addVibrationVector(baseAtom + iAtom,
            vibData[i][ipt++],
            vibData[i][ipt++], 
            vibData[i][ipt++]);
    }
  }

  
  
  

  private final static byte eig_val = 1;
  private final static byte mo_occ  = 2;
  private final static byte eig_vec = 3;
  private final static byte eig_vec_compressed = 4;
  private final static byte coef_indices  = 5;
  private final static byte bfxn_ang  = 6;
  private final static byte sto_exp  = 7;
  private final static byte contractions  = 8;
  private final static byte gto_exp = 9;
  private final static byte shell = 10;

  private final static String[] moFields = {
    "ID", "eig_val", "mo_occ", "eig_vec",
      "eig_vec_compressed", "coef_indices", "bfxn_ang", "sto_exp",
      "contractions", "gto_exp", "shell"
  };

  private final static byte[] moFieldMap = {
    ID, eig_val, mo_occ, eig_vec, eig_vec_compressed, 
    coef_indices, bfxn_ang, sto_exp, contractions, gto_exp, shell
  };
   
  private void processMolecularOrbitalObject() throws Exception {
    if (nSlaters == 0 && nGaussians == 0) {
      readLine();
      return; 
    }
    

    nOrbitals = (nSlaters + nGaussians);
    Logger.info("Reading CSF data for " + nOrbitals + " molecular orbitals");
    float[] energy = new float[nOrbitals];
    float[] occupancy = new float[nOrbitals];
    float[][] list = new float[nOrbitals][nOrbitals];
    float[][] listCompressed = null;
    int[][] coefIndices = null;
    int ipt = 0;
    boolean isCompressed = false;
    readLine();
    while (line != null && parseLineParameters(moFields, moFieldMap) > 0)
      while (readLine() != null && !line.startsWith("property_flags:")) {
        String tokens[] = getTokens();
        for (int i = 0; i < fieldCount; ++i) {
          switch (fieldTypes[i]) {
          case ID:
            ipt = parseInt(tokens[i]) - 1;
            break;
          case eig_val:
            energy[ipt] = parseFloat(tokens[i]);
            break;
          case mo_occ:
            occupancy[ipt] = parseFloat(tokens[i]);
            break;
          case eig_vec:
            fillCsfArray("eig_vec", tokens, i, list[ipt]);
            break;
          case eig_vec_compressed:
            isCompressed = true;
            if (listCompressed == null)
              listCompressed = new float[nOrbitals][nOrbitals];
            fillCsfArray("eig_vec_compressed", tokens, i, listCompressed[ipt]);
            break;
          case coef_indices:
            if (coefIndices == null)
              coefIndices = new int[nOrbitals][nOrbitals];
            fillCsfArray("coef_indices", tokens, i, coefIndices[ipt]);
            break;
          }
        }
      }
    
    for (int iMo = 0; iMo < nOrbitals; iMo++) {
      if (isCompressed) { 
        for (int i = 0; i < coefIndices[iMo].length; i++) {
          int pt = coefIndices[iMo][i] - 1;
          if (pt < 0)
            break;
          list[iMo][pt] = listCompressed[iMo][i];
        }
      }
      for (int i = 0; i < nOrbitals; i++)
        if (Math.abs(list[iMo][i]) < MIN_COEF)
          list[iMo][i] = 0;
      Hashtable mo = new Hashtable();
      mo.put("energy", new Float(energy[iMo]));
      mo.put("occupancy", new Float(occupancy[iMo]));
      mo.put("coefficients", list[iMo]);
      
      orbitals.addElement(mo);
    }
    setMOs("eV");
  }

  private void processBasisObject(String sto_gto) throws Exception {
    String[] atomNos = getTokens(strAtomicNumbers);
    atomicNumbers = new int[atomNos.length];
    for (int i = 0; i < atomicNumbers.length; i++)
      atomicNumbers[i] = parseInt(atomNos[i]);

    

    nOrbitals = (nSlaters + nGaussians);
    boolean isGaussian = (sto_gto.equals("gto"));
    float[][] zetas = new float[nOrbitals][];
    float[][] contractionCoefs = null;
    String[] types = new String[nOrbitals];
    int[] shells = new int[nOrbitals];
    int nZetas = 0;

    readLine();
    while (line != null && parseLineParameters(moFields, moFieldMap) > 0) {
      if (nZetas == 0)
        nZetas = getPropertyCount(sto_gto + "_exp");
      int ipt = 0;
      while (readLine() != null && !line.startsWith("property_flags:")) {
        String tokens[] = getTokens();
        for (int i = 0; i < fieldCount; ++i) {
          String field = tokens[i];
          switch (fieldTypes[i]) {
          case ID:
            ipt = parseInt(field) - 1;
            break;
          case bfxn_ang:
            types[ipt] = field;
            break;
          case sto_exp:
          case gto_exp:
            zetas[ipt] = new float[nZetas];
            fillCsfArray(sto_gto + "_exp", tokens, i, zetas[ipt]);
            break;
          case shell:
            shells[ipt] = parseInt(field);
            break;
          case contractions:
            if (contractionCoefs == null)
              contractionCoefs = new float[nOrbitals][nZetas];
            fillCsfArray("contractions", tokens, i, contractionCoefs[ipt]);
          }
        }
      }
    }
    if (isGaussian) {
      Vector sdata = new Vector();
      Vector gdata = new Vector();
      int iShell = 0;
      int gaussianCount = 0;
      for (int ipt = 0; ipt < nGaussians; ipt++) {
        if (shells[ipt] != iShell) {
          iShell = shells[ipt];
          int[] slater = new int[4];
          int iAtom = atomSetCollection
              .getAtomNameIndex(((String[]) (connectors.get(sto_gto
                  + "_basis_fxn" + (ipt + 1))))[0]);
          slater[0] = iAtom;
          slater[1] = JmolAdapter.getQuantumShellTagID(types[ipt]
              .substring(0, 1));
          int nZ = 0;
          while (++nZ < nZetas && zetas[ipt][nZ] != 0) {
          }
          slater[2] = gaussianCount; 
          slater[3] = nZ;
          sdata.addElement(slater);
          gaussianCount += nZ;
          for (int i = 0; i < nZ; i++)
            gdata.addElement(new float[] { zetas[ipt][i],
                contractionCoefs[ipt][i] });
        }
      }
      float[][] garray = new float[gaussianCount][];
      for (int i = 0; i < gaussianCount; i++)
        garray[i] = (float[]) gdata.get(i);
      moData.put("shells", sdata);
      moData.put("gaussians", garray);
    } else {
      for (int ipt = 0; ipt < nSlaters; ipt++) {
        int iAtom = atomSetCollection.getAtomNameIndex(((String[]) (connectors
            .get(sto_gto + "_basis_fxn" + (ipt + 1))))[0]);
        for (int i = 0; i < nZetas; i++) {
          if (zetas[ipt][i] == 0)
            break;
          createSphericalSlaterByType(iAtom, atomicNumbers[iAtom], types[ipt], zetas[ipt][i]
              * (i == 0 ? 1 : -1), contractionCoefs == null ? 1
              : contractionCoefs[ipt][i]);
        }
      }
      setSlaters(true, false); 
    }
  }  
}
