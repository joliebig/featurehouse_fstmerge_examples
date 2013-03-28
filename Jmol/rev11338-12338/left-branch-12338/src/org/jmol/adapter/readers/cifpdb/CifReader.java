
package org.jmol.adapter.readers.cifpdb;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolLineReader;


import java.io.BufferedReader;
import java.util.Hashtable;

import org.jmol.util.CifDataReader;
import org.jmol.util.Logger;


public class CifReader extends AtomSetCollectionReader implements JmolLineReader {

  private CifDataReader tokenizer = new CifDataReader(this);

  private String thisDataSetName = "";
  private String chemicalName = "";
  private String thisStructuralFormula = "";
  private String thisFormula = "";
  private boolean iHaveDesiredModel;

  private Hashtable htHetero;

  public void readAtomSetCollection(BufferedReader reader) {
    int nAtoms = 0;
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("cif");

    
    line = "";
    boolean skipping = false;
    try {
      while ((key = tokenizer.peekToken()) != null) {
        if (key.startsWith("data_")) {
          if (iHaveDesiredModel)
            break;
          skipping = !doGetModel(++modelNumber);
          if (skipping) {
            tokenizer.getTokenPeeked();
          } else {
            chemicalName = "";
            thisStructuralFormula = "";
            thisFormula = "";
            if (nAtoms == atomSetCollection.getAtomCount())
              
              atomSetCollection.removeAtomSet();
            else
              applySymmetryAndSetTrajectory();
            processDataParameter();
            iHaveDesiredModel = (isLastModel(modelNumber));
            nAtoms = atomSetCollection.getAtomCount();
          }
          continue;
        }
        if (key.startsWith("loop_")) {
          if (skipping) {
            tokenizer.getTokenPeeked();
            skipLoop();
          } else {
            processLoopBlock();
          }
          continue;
        }
        
        
        

        
        
        
        
        if (key.indexOf("_") != 0) {
          Logger.warn("CIF ERROR ? should be an underscore: " + key);
          tokenizer.getTokenPeeked();
        } else if (!getData()) {
          continue;
        }
        if (!skipping) {
          key = key.replace('.', '_');
          if (key.startsWith("_chemical_name")) {
            processChemicalInfo("name");
          } else if (key.startsWith("_chemical_formula_structural")) {
            processChemicalInfo("structuralFormula");
          } else if (key.startsWith("_chemical_formula_sum")) {
            processChemicalInfo("formula");
          } else if (key.startsWith("_cell_")) {
            processCellParameter();
          } else if (key.startsWith("_symmetry_space_group_name_H-M")
              || key.startsWith("_symmetry_space_group_name_Hall")) {
            processSymmetrySpaceGroupName();
          } else if (key.startsWith("_atom_sites_fract_tran")) {
            processUnitCellTransformMatrix();
          } else if (key.startsWith("_pdbx_entity_nonpoly")) {
            processNonpolyData();
          }
        }
      }

      if (atomSetCollection.getAtomCount() == nAtoms)
        atomSetCollection.removeAtomSet();
      else
        applySymmetryAndSetTrajectory();
      if (htSites != null)
        addSites(htSites);
      atomSetCollection.setCollectionName("<collection of "
          + atomSetCollection.getAtomSetCount() + " models>");
    } catch (Exception e) {
      setError(e);
    }
  }

  
  
  

  
  private void processDataParameter() {
    tokenizer.getTokenPeeked();
    thisDataSetName = (key.length() < 6 ? "" : key.substring(5));
    if (thisDataSetName.length() > 0) {
      if (atomSetCollection.getCurrentAtomSetIndex() >= 0) {
        
        
        atomSetCollection.newAtomSet();
      } else {
        atomSetCollection.setCollectionName(thisDataSetName);
      }
    }
    Logger.debug(key);
  }
  
  
  private void processChemicalInfo(String type) throws Exception {
    if (type.equals("name"))
      chemicalName = data = tokenizer.fullTrim(data);
    else if (type.equals("structuralFormula"))
      thisStructuralFormula = data = tokenizer.fullTrim(data);
    else if (type.equals("formula"))
      thisFormula = data = tokenizer.fullTrim(data);
    if (Logger.debugging) {
      Logger.debug(type + " = " + data);
    }
  }

  
  private void processSymmetrySpaceGroupName() throws Exception {
    setSpaceGroupName(data);
  }

  final public static String[] cellParamNames = { 
    "_cell_length_a", 
    "_cell_length_b",
    "_cell_length_c", 
    "_cell_angle_alpha", 
    "_cell_angle_beta",
    "_cell_angle_gamma" 
  };

  
  private void processCellParameter() throws Exception {
    for (int i = cellParamNames.length; --i >= 0;)
      if (isMatch(key, cellParamNames[i])) {
        setUnitCellItem(i, parseFloat(data));
        return;
      }
  }

  final private static String[] TransformFields = {
      "x[1][1]", "x[1][2]", "x[1][3]", "r[1]",
      "x[2][1]", "x[2][2]", "x[2][3]", "r[2]",
      "x[3][1]", "x[3][2]", "x[3][3]", "r[3]",
  };

  
  private void processUnitCellTransformMatrix() throws Exception {
    
    float v = parseFloat(data);
    if (Float.isNaN(v))
      return;
    for (int i = 0; i < TransformFields.length; i++) {
      if (key.indexOf(TransformFields[i]) >= 0) {
        setUnitCellItem(6 + i, v);
        return;
      }
    }
  }
  
  
  
  

  private String key;
  private String data;
  
  
  private boolean getData() throws Exception {
    key = tokenizer.getTokenPeeked();
    data = tokenizer.getNextToken();
    if (Logger.debugging)
      Logger.debug(key  + " " + data);
    if (data == null) {
      Logger.warn("CIF ERROR ? end of file; data missing: " + key);
      return false;
    }
    return (data.length() == 0 || data.charAt(0) != '\0');
  }
  
  
  private void processLoopBlock() throws Exception {
    tokenizer.getTokenPeeked(); 
    String str = tokenizer.peekToken();
    if (str == null)
      return;
    if (str.startsWith("_atom_site_") || str.startsWith("_atom_site.")) {
      if (!processAtomSiteLoopBlock())
        return;
      atomSetCollection.setAtomSetName(thisDataSetName);
      atomSetCollection.setAtomSetAuxiliaryInfo("chemicalName", chemicalName);
      atomSetCollection.setAtomSetAuxiliaryInfo("structuralFormula",
          thisStructuralFormula);
      atomSetCollection.setAtomSetAuxiliaryInfo("formula", thisFormula);
      return;
    }
    if (str.startsWith("_atom_type")) {
      processAtomTypeLoopBlock();
      return;
    }
    if (str.startsWith("_geom_bond")) {
      if (doApplySymmetry && !applySymmetryToBonds)
        skipLoop();
      else
        processGeomBondLoopBlock();
      return;
    }
    if (str.startsWith("_pdbx_entity_nonpoly")) {
      processNonpolyLoopBlock();
      return;
    }
    if (str.startsWith("_chem_comp")) {
      processChemCompLoopBlock();
      return;
    }
    if (str.startsWith("_struct_conf") && !str.startsWith("_struct_conf_type")) {
      processStructConfLoopBlock();
      return;
    }
    if (str.startsWith("_struct_sheet_range")) {
      processStructSheetRangeLoopBlock();
      return;
    }
    if (str.startsWith("_struct_sheet_range")) {
      processStructSheetRangeLoopBlock();
      return;
    }
    if (str.startsWith("_symmetry_equiv_pos")
        || str.startsWith("space_group_symop")) {
      if (ignoreFileSymmetryOperators) {
        Logger.warn("ignoring file-based symmetry operators");
        skipLoop();
      } else {
        processSymmetryOperationsLoopBlock();
      }
      return;
    }
    if (str.startsWith("_struct_site")) {
      processStructSiteBlock();
      return;
    }
    skipLoop();
  }

  
  
  


  private Hashtable atomTypes;
  
  final private static byte ATOM_TYPE_SYMBOL = 0;
  final private static byte ATOM_TYPE_OXIDATION_NUMBER = 1;

  final private static String[] atomTypeFields = { 
      "_atom_type_symbol",
      "_atom_type_oxidation_number", 
  };

  
  private void processAtomTypeLoopBlock() throws Exception {
    parseLoopParameters(atomTypeFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        skipLoop();
        return;
      }

    while (tokenizer.getData()) {
      String atomTypeSymbol = null;
      float oxidationNumber = Float.NaN;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case ATOM_TYPE_SYMBOL:
          atomTypeSymbol = field;
          break;
        case ATOM_TYPE_OXIDATION_NUMBER:
          oxidationNumber = parseFloat(field);
          break;
        }
      }
      if (atomTypeSymbol == null || Float.isNaN(oxidationNumber))
        continue;
      if (atomTypes == null)
        atomTypes = new Hashtable();
      atomTypes.put(atomTypeSymbol, new Float(oxidationNumber));
    }
  }

  
  
  

  final private static byte NONE = -1;
  final private static byte TYPE_SYMBOL = 0;
  final private static byte LABEL = 1;
  final private static byte AUTH_ATOM = 2;
  final private static byte FRACT_X = 3;
  final private static byte FRACT_Y = 4;
  final private static byte FRACT_Z = 5;
  final private static byte CARTN_X = 6;
  final private static byte CARTN_Y = 7;
  final private static byte CARTN_Z = 8;
  final private static byte OCCUPANCY = 9;
  final private static byte B_ISO = 10;
  final private static byte COMP_ID = 11;
  final private static byte ASYM_ID = 12;
  final private static byte SEQ_ID = 13;
  final private static byte INS_CODE = 14;
  final private static byte ALT_ID = 15;
  final private static byte GROUP_PDB = 16;
  final private static byte MODEL_NO = 17;
  final private static byte DUMMY_ATOM = 18;
  final private static byte DISORDER_GROUP = 19;
  final private static byte ANISO_LABEL = 20;
  final private static byte ANISO_U11 = 21;
  final private static byte ANISO_U22 = 22;
  final private static byte ANISO_U33 = 23;
  final private static byte ANISO_U12 = 24;
  final private static byte ANISO_U13 = 25;
  final private static byte ANISO_U23 = 26;
  final private static byte ANISO_MMCIF_U11 = 27;
  final private static byte ANISO_MMCIF_U22 = 28;
  final private static byte ANISO_MMCIF_U33 = 29;
  final private static byte ANISO_MMCIF_U12 = 30;
  final private static byte ANISO_MMCIF_U13 = 31;
  final private static byte ANISO_MMCIF_U23 = 32;
  final private static byte U_ISO_OR_EQUIV = 33;
  final private static byte ANISO_B11 = 34;
  final private static byte ANISO_B22 = 35;
  final private static byte ANISO_B33 = 36;
  final private static byte ANISO_B12 = 37;
  final private static byte ANISO_B13 = 38;
  final private static byte ANISO_B23 = 39;
  final private static byte ANISO_Beta_11 = 40;
  final private static byte ANISO_Beta_22 = 41;
  final private static byte ANISO_Beta_33 = 42;
  final private static byte ANISO_Beta_12 = 43;
  final private static byte ANISO_Beta_13 = 44;
  final private static byte ANISO_Beta_23 = 45;
  final private static byte ADP_TYPE = 46;

  final private static String[] atomFields = { 
      "_atom_site_type_symbol",
      "_atom_site_label", 
      "_atom_site_auth_atom_id", 
      "_atom_site_fract_x",
      "_atom_site_fract_y", 
      "_atom_site_fract_z", 
      "_atom_site_Cartn_x",
      "_atom_site_Cartn_y", 
      "_atom_site_Cartn_z", 
      "_atom_site_occupancy",
      "_atom_site_b_iso_or_equiv", 
      "_atom_site_auth_comp_id",
      "_atom_site_auth_asym_id", 
      "_atom_site_auth_seq_id",
      "_atom_site_pdbx_PDB_ins_code", 
      "_atom_site_label_alt_id",
      "_atom_site_group_PDB", 
      "_atom_site_pdbx_PDB_model_num",
      "_atom_site_calc_flag", 
      "_atom_site_disorder_group",
      "_atom_site_aniso_label", 
      "_atom_site_aniso_U_11",
      "_atom_site_aniso_U_22",
      "_atom_site_aniso_U_33",
      "_atom_site_aniso_U_12",
      "_atom_site_aniso_U_13",
      "_atom_site_aniso_U_23",
      "_atom_site_anisotrop_U[1][1]",
      "_atom_site_anisotrop_U[2][2]",
      "_atom_site_anisotrop_U[3][3]",
      "_atom_site_anisotrop_U[1][2]",
      "_atom_site_anisotrop_U[1][3]",
      "_atom_site_anisotrop_U[2][3]",
      "_atom_site_U_iso_or_equiv",
      "_atom_site_aniso_B_11",
      "_atom_site_aniso_B_22",
      "_atom_site_aniso_B_33",
      "_atom_site_aniso_B_12",
      "_atom_site_aniso_B_13",
      "_atom_site_aniso_B_23",
      "_atom_site_aniso_Beta_11",
      "_atom_site_aniso_Beta_22",
      "_atom_site_aniso_Beta_33",
      "_atom_site_aniso_Beta_12",
      "_atom_site_aniso_Beta_13",
      "_atom_site_aniso_Beta_23",
      "_atom_site_adp_type",
  };


  

  
  boolean processAtomSiteLoopBlock() throws Exception {
    int currentModelNO = -1;
    boolean isPDB = false;
    parseLoopParameters(atomFields);
    if (fieldOf[CARTN_X] != NONE) {
      setFractionalCoordinates(false);
      disableField(FRACT_X);
      disableField(FRACT_Y);
      disableField(FRACT_Z);
    } else if (fieldOf[FRACT_X] != NONE) {
      setFractionalCoordinates(true);
      disableField(CARTN_X);
      disableField(CARTN_Y);
      disableField(CARTN_Z);
    } else if (fieldOf[ANISO_LABEL] != NONE) {
    } else {
      
      skipLoop();
      return false;
    }
    while (tokenizer.getData()) {
      Atom atom = new Atom();
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case TYPE_SYMBOL:
          String elementSymbol;
          if (field.length() < 2) {
            elementSymbol = field;
          } else {
            char ch1 = Character.toLowerCase(field.charAt(1));
            if (Atom.isValidElementSymbol(firstChar, ch1))
              elementSymbol = "" + firstChar + ch1;
            else
              elementSymbol = "" + firstChar;
          }
          atom.elementSymbol = elementSymbol;
          if (atomTypes != null && atomTypes.containsKey(field)) {
            float charge = ((Float) atomTypes.get(field)).floatValue();
            atom.formalCharge = (int) (charge + (charge < 0 ? -0.5 : 0.5));
            
            if (Math.abs(atom.formalCharge - charge) > 0.1)
              if (Logger.debugging) {
                Logger.debug("CIF charge on " + field + " was " + charge
                    + "; rounded to " + atom.formalCharge);
              }
          }
          break;
        case LABEL:
        case AUTH_ATOM:
          atom.atomName = field;
          break;
        case CARTN_X:
        case FRACT_X:
          atom.x = parseFloat(field);
          break;
        case CARTN_Y:
        case FRACT_Y:
          atom.y = parseFloat(field);
          break;
        case CARTN_Z:
        case FRACT_Z:
          atom.z = parseFloat(field);
          break;
        case OCCUPANCY:
          float floatOccupancy = parseFloat(field);
          if (!Float.isNaN(floatOccupancy))
            atom.occupancy = (int) (floatOccupancy * 100);
          break;
        case B_ISO:
          atom.bfactor = parseFloat(field) * (isPDB ? 1 : 100f);
          break;
        case COMP_ID:
          atom.group3 = field;
          break;
        case ASYM_ID:
          if (field.length() > 1)
            Logger.warn("Don't know how to deal with chains more than 1 char: "
                + field);
          atom.chainID = firstChar;
          break;
        case SEQ_ID:
          atom.sequenceNumber = parseInt(field);
          break;
        case INS_CODE:
          atom.chainID = firstChar;
          break;
        case ALT_ID:
        case DISORDER_GROUP: 
          atom.alternateLocationID = firstChar;
          break;
        case GROUP_PDB:
          isPDB = true;
          if ("HETATM".equals(field))
            atom.isHetero = true;
          break;
        case MODEL_NO:
          int modelNO = parseInt(field);
          if (modelNO != currentModelNO) {
            atomSetCollection.newAtomSet();
            currentModelNO = modelNO;
          }
          break;
        case DUMMY_ATOM:
          
          
          if ("dum".equals(field)) {
            atom.x = Float.NaN;
            continue; 
          }
          break;
        case ADP_TYPE:
          if (field.equalsIgnoreCase("Uiso")) {
            int j = fieldOf[U_ISO_OR_EQUIV];
            if (j != NONE) {
              if (atom.anisoBorU == null)
                atom.anisoBorU = new float[8];
              atom.anisoBorU[7] = parseFloat(tokenizer.loopData[j]);
              atom.anisoBorU[6] = 8; 
            }
          }
          break;
        case ANISO_LABEL:
          int iAtom = atomSetCollection.getAtomNameIndex(field);
          if (iAtom < 0)
            return false;
          atom = atomSetCollection.getAtom(iAtom);
          break;
        case ANISO_U11:
        case ANISO_U22:
        case ANISO_U33:
        case ANISO_U12:
        case ANISO_U13:
        case ANISO_U23:
        case ANISO_MMCIF_U11:
        case ANISO_MMCIF_U22:
        case ANISO_MMCIF_U33:
        case ANISO_MMCIF_U12:
        case ANISO_MMCIF_U13:
        case ANISO_MMCIF_U23:
          if (atom.anisoBorU == null)
            atom.anisoBorU = new float[8];
          int iType = (propertyOf[i] - ANISO_U11) % 6;
          atom.anisoBorU[iType] = parseFloat(field);
          atom.anisoBorU[6] = 8; 
          break;
        case ANISO_B11:
        case ANISO_B22:
        case ANISO_B33:
        case ANISO_B12:
        case ANISO_B13:
        case ANISO_B23:
           if (atom.anisoBorU == null)
             atom.anisoBorU = new float[8];
           int iTypeB = (propertyOf[i] - ANISO_B11) % 6;
           atom.anisoBorU[iTypeB] = parseFloat(field);
           atom.anisoBorU[6] = 4; 
          break;
        case ANISO_Beta_11:
        case ANISO_Beta_22:
        case ANISO_Beta_33:
        case ANISO_Beta_12:
        case ANISO_Beta_13:
        case ANISO_Beta_23:
           if (atom.anisoBorU == null)
             atom.anisoBorU = new float[8];
           int iTypeBeta = (propertyOf[i] - ANISO_Beta_11) % 6;
           atom.anisoBorU[iTypeBeta] = parseFloat(field);
           atom.anisoBorU[6] = 0; 
          break;
        }
      }
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("atom " + atom.atomName
            + " has invalid/unknown coordinates");
      } else {
        if (fieldOf[ANISO_LABEL] != NONE)
          continue;
        if (filter != null)
          if (!filterAtom(atom))
            continue;
        setAtomCoord(atom);
        atomSetCollection.addAtomWithMappedName(atom);
        if (atom.isHetero && htHetero != null) {
          atomSetCollection.setAtomSetAuxiliaryInfo("hetNames", htHetero);
          atomSetCollection.setAtomSetCollectionAuxiliaryInfo("hetNames",
              htHetero);
          htHetero = null;
        }
      }
    }
    if (isPDB) {
      atomSetCollection
          .setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    }
    atomSetCollection.setAtomSetAuxiliaryInfo("isCIF", Boolean.TRUE);
    return true;
  }
     
  
  
  

  final private static byte GEOM_BOND_ATOM_SITE_LABEL_1 = 0;
  final private static byte GEOM_BOND_ATOM_SITE_LABEL_2 = 1;
  final private static byte GEOM_BOND_SITE_SYMMETRY_2 = 2;

  final private static String[] geomBondFields = { 
      "_geom_bond_atom_site_label_1",
      "_geom_bond_atom_site_label_2", 

  };

  
  private void processGeomBondLoopBlock() throws Exception {
    parseLoopParameters(geomBondFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _geom_bond property:" + i);
        skipLoop();
        return;
      }

    while (tokenizer.getData()) {
      int atomIndex1 = -1;
      int atomIndex2 = -1;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case GEOM_BOND_ATOM_SITE_LABEL_1:
          atomIndex1 = atomSetCollection.getAtomNameIndex(field);
          break;
        case GEOM_BOND_ATOM_SITE_LABEL_2:
          atomIndex2 = atomSetCollection.getAtomNameIndex(field);
          break;
        case GEOM_BOND_SITE_SYMMETRY_2:
          
          break;
        }
      }
      if ( atomIndex1 < 0 || atomIndex2 < 0)
        continue;
      Bond bond = new Bond();
      bond.atomIndex1 = atomIndex1;
      bond.atomIndex2 = atomIndex2;
      atomSetCollection.addBond(bond);
    }
  }
  
  
  
  

  final private static byte NONPOLY_ENTITY_ID = 0;
  final private static byte NONPOLY_NAME = 1;
  final private static byte NONPOLY_COMP_ID = 2;

  final private static String[] nonpolyFields = { 
      "_pdbx_entity_nonpoly_entity_id",
      "_pdbx_entity_nonpoly_name", 
      "_pdbx_entity_nonpoly_comp_id", 
  };
  
  
  private String[] hetatmData;
  private void processNonpolyData() {
    if (hetatmData == null)
      hetatmData = new String[3];
    for (int i = nonpolyFields.length; --i >= 0;)
      if (isMatch(key, nonpolyFields[i])) {
        hetatmData[i] = data;
        break;
      }
    if (hetatmData[NONPOLY_NAME] == null || hetatmData[NONPOLY_COMP_ID] == null)
      return;
    addHetero(hetatmData[NONPOLY_COMP_ID], hetatmData[NONPOLY_NAME]);
    hetatmData = null;
  }


  final private static byte CHEM_COMP_ID = 0;
  final private static byte CHEM_COMP_NAME = 1;

  final private static String[] chemCompFields = { 
      "_chem_comp_id",
      "_chem_comp_name",  
  };
  

  
  private void processChemCompLoopBlock() throws Exception {
    parseLoopParameters(chemCompFields);
    while (tokenizer.getData()) {
      String groupName = null;
      String hetName = null;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case CHEM_COMP_ID:
          groupName = field;
          break;
        case CHEM_COMP_NAME:
          hetName = field;
          break;
        }
      }
      if (groupName == null || hetName == null)
        return;
      addHetero(groupName, hetName);
    }
  }

  
  private void processNonpolyLoopBlock() throws Exception {
    parseLoopParameters(nonpolyFields);
    while (tokenizer.getData()) {
      String groupName = null;
      String hetName = null;
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
        case NONPOLY_ENTITY_ID:
          break;
        case NONPOLY_COMP_ID:
          groupName = field;
          break;
        case NONPOLY_NAME:
          hetName = field;
          break;
        }
      }
      if (groupName == null || hetName == null)
        return;
      addHetero(groupName, hetName);
    }
  }

  private void addHetero(String groupName, String hetName) {
    if (!JmolAdapter.isHetero(groupName))
      return;
    if (htHetero == null)
      htHetero = new Hashtable();
    htHetero.put(groupName, hetName);
    if (Logger.debugging) {
      Logger.debug("hetero: " + groupName + " = " + hetName);
    }
  }
  
  
  
  

  final private static byte CONF_TYPE_ID = 0;
  final private static byte BEG_ASYM_ID = 1;
  final private static byte BEG_SEQ_ID = 2;
  final private static byte BEG_INS_CODE = 3;
  final private static byte END_ASYM_ID = 4;
  final private static byte END_SEQ_ID = 5;
  final private static byte END_INS_CODE = 6;
  final private static byte STRUCT_ID = 7;
  final private static byte SERIAL_NO = 8;

  final private static String[] structConfFields = { 
      "_struct_conf_conf_type_id",
      "_struct_conf_beg_auth_asym_id", 
      "_struct_conf_beg_auth_seq_id",
      "_struct_conf_pdbx_beg_PDB_ins_code",
      "_struct_conf_end_auth_asym_id", 
      "_struct_conf_end_auth_seq_id",
      "_struct_conf_pdbx_end_PDB_ins_code",
      "_struct_conf_id", 
      "_struct_conf_pdbx_PDB_helix_id", 
  };

  
  private void processStructConfLoopBlock() throws Exception {
    parseLoopParameters(structConfFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_conf property:" + i);
        skipLoop();
        return;
      }
    while (tokenizer.getData()) {
      Structure structure = new Structure();
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case NONE:
          break;
        case CONF_TYPE_ID:
          if (field.startsWith("HELX"))
            structure.structureType = "helix";
          else if (field.startsWith("TURN"))
            structure.structureType = "turn";
          else
            structure.structureType = "none";
          break;
        case BEG_ASYM_ID:
          structure.startChainID = firstChar;
          break;
        case BEG_SEQ_ID:
          structure.startSequenceNumber = parseInt(field);
          break;
        case BEG_INS_CODE:
          structure.startInsertionCode = firstChar;
          break;
        case END_ASYM_ID:
          structure.endChainID = firstChar;
          break;
        case END_SEQ_ID:
          structure.endSequenceNumber = parseInt(field);
          break;
        case END_INS_CODE:
          structure.endInsertionCode = firstChar;
          break;
        case STRUCT_ID:
          structure.structureID = field;
          break;
        case SERIAL_NO:
          structure.serialID = parseInt(field);
          break;
        }
      }
      atomSetCollection.addStructure(structure);
    }
  }
  
  
  

  final private static byte SHEET_ID = 0;
  final private static byte STRAND_ID = 7;

  final private static String[] structSheetRangeFields = {
    "_struct_sheet_range_sheet_id",  
    "_struct_sheet_range_beg_auth_asym_id",
    "_struct_sheet_range_beg_auth_seq_id",
    "_struct_sheet_range_pdbx_beg_PDB_ins_code",
    "_struct_sheet_range_end_auth_asym_id",
    "_struct_sheet_range_end_auth_seq_id",
    "_struct_sheet_range_pdbx_end_PDB_ins_code", 
    "_struct_sheet_range_id",
  };

  
  private void processStructSheetRangeLoopBlock() throws Exception {
    parseLoopParameters(structSheetRangeFields);
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_conf property:" + i);
        skipLoop();
        return;
      }
    while (tokenizer.getData()) {
      Structure structure = new Structure();
      structure.structureType = "sheet";
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case BEG_ASYM_ID:
          structure.startChainID = firstChar;
          break;
        case BEG_SEQ_ID:
          structure.startSequenceNumber = parseInt(field);
          break;
        case BEG_INS_CODE:
          structure.startInsertionCode = firstChar;
          break;
        case END_ASYM_ID:
          structure.endChainID = firstChar;
          break;
        case END_SEQ_ID:
          structure.endSequenceNumber = parseInt(field);
          break;
        case END_INS_CODE:
          structure.endInsertionCode = firstChar;
          break;
        case SHEET_ID:
          structure.strandCount = 1;
          structure.structureID = field;
          break;
        case STRAND_ID:
          structure.serialID = parseInt(field);
          break;
        }
      }
      atomSetCollection.addStructure(structure);
    }
  }

  final private static byte SITE_ID = 0;
  final private static byte SITE_COMP_ID = 1;
  final private static byte SITE_ASYM_ID = 2;
  final private static byte SITE_SEQ_ID = 3;
  final private static byte SITE_INS_CODE = 4; 

  final private static String[] structSiteRangeFields = {
    "_struct_site_gen_site_id",  
    "_struct_site_gen_auth_comp_id", 
    "_struct_site_gen_auth_asym_id", 
    "_struct_site_gen_auth_seq_id",  
    "_struct_site_gen_label_alt_id",  
  };

  
  
  
  private int siteNum;
  private Hashtable htSites;
  
  
  private void processStructSiteBlock() throws Exception {
    parseLoopParameters(structSiteRangeFields);
    for (int i = 3; --i >= 0;)
      if (fieldOf[i] == NONE) {
        Logger.warn("?que? missing _struct_site property:" + i);
        skipLoop();
        return;
      }
    String siteID = "";
    String seqNum = "";
    String insCode = "";
    String chainID = "";
    String resID = "";
    String group = "";
    Hashtable htSite = null;
    htSites = new Hashtable();
    while (tokenizer.getData()) {
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case SITE_ID:
          if (group != "") {
            String groups = (String) htSite.get("groups");
            groups += (groups.length() == 0 ? "" : ",") + group;
            group = "";
            htSite.put("groups", groups);
          }
          siteID = field;
          htSite = (Hashtable)htSites.get(siteID);
          if (htSite == null) {
            htSite = new Hashtable();
            htSite.put("seqNum", "site_" + (++siteNum));
            htSite.put("groups", "");
            htSites.put(siteID, htSite);
          }
          seqNum = "";
          insCode = "";
          chainID = "";
          resID = "";
          break;
        case SITE_COMP_ID:
          resID = field;
          break;
        case SITE_ASYM_ID:
          chainID = field;
          break;
        case SITE_SEQ_ID:
          seqNum = field;
          break;
        case SITE_INS_CODE: 
          insCode = field;
          break;
        }
        if (seqNum != "" && resID != "")
          group = "[" + resID + "]" + seqNum
            + (insCode.length() > 0 ?  "^" + insCode : "")
            + (chainID.length() > 0 ? ":" + chainID : "");
      }      
    }
    if (group != "") {
      String groups = (String) htSite.get("groups");
      groups += (groups.length() == 0 ? "" : ",") + group;
      group = "";
      htSite.put("groups", groups);
    }
  }

  
  
  

  final private static byte SYMOP_XYZ = 0;
  final private static byte SYM_EQUIV_XYZ = 1;

  final private static String[] symmetryOperationsFields = {
      "_space_group_symop_operation_xyz", 
      "_symmetry_equiv_pos_as_xyz", 
  };

  
  private void processSymmetryOperationsLoopBlock() throws Exception {
    parseLoopParameters(symmetryOperationsFields);
    int nRefs = 0;
    for (int i = propertyCount; --i >= 0;)
      if (fieldOf[i] != NONE)
        nRefs++;
    if (nRefs != 1) {
      Logger.warn("?que? _symmetry_equiv or _space_group_symop property not found");
      skipLoop();
      return;
    }
    while (tokenizer.getData()) {
      for (int i = 0; i < tokenizer.fieldCount; ++i) {
        switch (fieldProperty(i)) {
        case SYMOP_XYZ:
        case SYM_EQUIV_XYZ:
          setSymmetryOperator(field);
          break;
        }
      }
    }
  }
  
  private int fieldProperty(int i) {
    return ((field = tokenizer.loopData[i]).length() > 0 
        && (firstChar = field.charAt(0)) != '\0' ? 
            propertyOf[i] : NONE);
  }

  String field;
  
  private char firstChar;
  private int[] propertyOf = new int[100]; 
  private byte[] fieldOf = new byte[atomFields.length];
  private int propertyCount;
  
  
  
  private void parseLoopParameters(String[] fields) throws Exception {
    tokenizer.fieldCount = 0;
    for (int i = fields.length; --i >= 0; )
      fieldOf[i] = NONE;

    propertyCount = fields.length;
    while (true) {
      String str = tokenizer.peekToken();
      if (str == null) {
        tokenizer.fieldCount = 0;
        break;
      }
      if (str.charAt(0) != '_')
        break;
      tokenizer.getTokenPeeked();
      propertyOf[tokenizer.fieldCount] = NONE;
      for (int i = fields.length; --i >= 0;)
        if (isMatch(str, fields[i])) {
          propertyOf[tokenizer.fieldCount] = i;
          fieldOf[i] = (byte) tokenizer.fieldCount;
          break;
        }
      tokenizer.fieldCount++;
    }
    if (tokenizer.fieldCount > 0)
      tokenizer.loopData = new String[tokenizer.fieldCount];
  }

  public String readLine() throws Exception {
    super.readLine();
    if (line.indexOf("#jmolscript:") >= 0)
      checkLineForScript();
    return line;
  }
  
  
  private void disableField(int fieldIndex) {
    int i = fieldOf[fieldIndex];
    if (i != NONE)
        propertyOf[i] = NONE;
  }

  
  private void skipLoop() throws Exception {
    String str;
    while ((str = tokenizer.peekToken()) != null && str.charAt(0) == '_')
      str  = tokenizer.getTokenPeeked();
    while (tokenizer.getNextDataToken() != null) {
    }
  }  
  
  
  private static boolean isMatch(String str1, String str2) {
    int cch = str1.length();
    if (str2.length() != cch)
      return false;
    for (int i = cch; --i >= 0;) {
      char ch1 = str1.charAt(i);
      char ch2 = str2.charAt(i);
      if (ch1 == ch2)
        continue;
      if ((ch1 == '_' || ch1 == '.') && (ch2 == '_' || ch2 == '.'))
        continue;
      if (ch1 <= 'Z' && ch1 >= 'A')
        ch1 += 'a' - 'A';
      else if (ch2 <= 'Z' && ch2 >= 'A')
        ch2 += 'a' - 'A';
      if (ch1 != ch2)
        return false;
    }
    return true;
  }  
}
