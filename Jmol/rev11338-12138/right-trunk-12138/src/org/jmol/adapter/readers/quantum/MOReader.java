

package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;


import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;



 
abstract class MOReader extends AtomSetCollectionReader {
    
  protected int shellCount = 0;
  protected int gaussianCount = 0;
  protected Hashtable moData = new Hashtable();
  protected Vector shells;
  protected float[][] gaussians;

  protected Vector orbitals = new Vector();
  protected String energyUnits = "";
  
  protected Vector moTypes;
  private boolean getNBOs;
  private boolean getNBOCharges;
  protected boolean haveNboCharges;

  private String[] filterTokens;
  private boolean filterIsNot; 

  protected boolean iHaveAtoms = false;
  protected boolean continuing = true;
  protected boolean ignoreMOs = false;
  protected String alphaBeta = "";

  final protected int HEADER_GAMESS_UK_MO = 3;
  final protected int HEADER_GAMESS_OCCUPANCIES = 2;
  final protected int HEADER_GAMESS_ORIGINAL = 1;
  final protected int HEADER_NONE = 0;
  

  abstract public void readAtomSetCollection(BufferedReader reader); 

  
  abstract protected boolean checkLine() throws Exception;
  
  public void readAtomSetCollection(BufferedReader reader, String type) {
    initializeMoReader(reader, type);
    try {
      readLine();
      iHaveAtoms = false;
      while (line != null && continuing)
        if (checkLine())
          readLine();
      finalizeMoReader();
    } catch (Exception e) {
      setError(e);
    }
  }
  
  protected void finalizeMoReader() {
    
  }

  private void initializeMoReader(BufferedReader reader, String type) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection(type, this);
    line = "\nNBOs in the AO basis:";
    getNBOs = filterMO();
    line = "\nNBOcharges";
    getNBOCharges = (filter != null && filterMO());
    if (filter == null)
      return;
    filter = TextFormat.simpleReplace(filter, "nbocharges","");
    if (filter.length() < 3)
      filter = null;
  }
  
  protected boolean filterMO() {
    if (filter == null)
      return true;
    boolean isOK = true;
    int nOK = 0;
    line = line.toLowerCase() + " " + alphaBeta;
    if (filterTokens == null) {
      filterIsNot = (filter.indexOf("!") >= 0);
      filterTokens = getTokens(filter.replace('!', ' ').replace(',', ' ')
          .replace(';', ' ').toLowerCase());
    }
    for (int i = 0; i < filterTokens.length; i++)
      if (line.indexOf(filterTokens[i]) >= 0) {
        if (!filterIsNot) {
          nOK = filterTokens.length;
          break;
        }
      } else if (filterIsNot) {
        nOK++;
      }
    isOK = (nOK == filterTokens.length);
    if (line.indexOf('\n') != 0)
      Logger.info("filter MOs: " + isOK + " for \"" + line + "\"");
    return isOK;
  }

  
  protected boolean checkNboLine() throws Exception {

    
    

    if (getNBOs) {
      if (line.indexOf("(Occupancy)   Bond orbital/ Coefficients/ Hybrids") >= 0) {
        getNboTypes();
        return false;
      }
      if (line.indexOf("NBOs in the AO basis:") >= 0) {
        readMolecularOrbitals(HEADER_NONE);
        return false;
      }
    }
    if (getNBOCharges && line.indexOf("Summary of Natural Population Analysis:") >= 0) {
      getNboCharges();
      return true;
    }
    return true;
  }
  
  
  
  private void getNboCharges() throws Exception {
    if (haveNboCharges)
      return; 
    discardLinesUntilContains("----");
    discardLinesUntilContains("----");
    haveNboCharges = true;
    int atomCount = atomSetCollection.getAtomCount();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = i0; i < atomCount; ++i) {
      
      while (atoms[i].elementNumber == 0)
        ++i;
      
      String[] tokens = getTokens(readLine());
      float charge;
      if (tokens == null || tokens.length < 3 || Float.isNaN(charge = parseFloat(tokens[2]))) {
        Logger.info("Error reading NBO charges: " + line);
        return;
      }
      atoms[i].partialCharge = charge;      
      if (Logger.debugging)
        Logger.debug("Atom " + i + " using NBOcharge: " + charge);
    }
    Logger.info("Using NBO charges for Model " + atomSetCollection.getAtomSetCount());
  }
  
  
  
  protected void getNboTypes() throws Exception {
    moTypes = new Vector();
    readLine();
    readLine();
    int n = 0;
    while (line != null && line.indexOf(".") == 4) {
      if (parseInt(line.substring(0, 4)) != n + 1)
        break;
      moTypes.add(n++, line.substring(5, 34).trim());
      while (readLine() != null && line.startsWith("     ")) {
      }
    }
    Logger.info(n + " natural bond orbitals read");
  }

  
  protected void readMolecularOrbitals(int headerType) throws Exception {
    if (ignoreMOs) {
      
      
      readLine();
      return;
    }
    Hashtable[] mos = null;
    Vector[] data = null;
    Vector coeffLabels = null;
    int ptOffset = -1;
    int fieldSize = 0;
    int nThisLine = 0;
    readLine();
    int moCount = 0;
    int nSkip = -1;
    boolean haveMOs = false;
    if (line.indexOf("---") >= 0)
      readLine();
    while (readLine() != null) {
      String[] tokens = getTokens();
      if (Logger.debugging) {
        Logger.debug(tokens.length + " --- " + line);
      }
      if (line.indexOf("end") >= 0)
        break;
      if (line.indexOf(" ALPHA SET ") >= 0) {
        alphaBeta = "alpha";
        if (readLine() == null)
          break;
      } else if (line.indexOf(" BETA SET ") >= 0) {
        if (haveMOs)
          break;
        alphaBeta = "beta";
        if (readLine() == null)
          break;
      }
        
      if (line.length() == 0 || line.indexOf("--") >= 0 || line.indexOf(".....") >=0 
           || line.indexOf("NBO BASIS") >= 0 
           || line.indexOf("CI EIGENVECTORS WILL BE LABELED") >=0 
           || line.indexOf("   THIS LOCALIZATION HAD") >=0) { 
        for (int iMo = 0; iMo < nThisLine; iMo++) {
          float[] coefs = new float[data[iMo].size()];
          int iCoeff = 0;
          while (iCoeff < coefs.length) {
            
            if (((String) coeffLabels.get(iCoeff)).equals("XXX")) {
              Hashtable fCoeffs = new Hashtable();
              for (int ifc = 0; ifc < 10; ifc++) {
                fCoeffs.put(coeffLabels.get(iCoeff+ifc), data[iMo].get(iCoeff+ifc));
              }
              for (int ifc = 0; ifc < 10; ifc++) {
                String orderLabel = JmolAdapter.getQuantumSubshellTag(JmolAdapter.SHELL_F_CARTESIAN, ifc);
                coefs[iCoeff++] = parseFloat((String) fCoeffs.get(orderLabel));
              }
            } else {
              coefs[iCoeff] = parseFloat((String) data[iMo].get(iCoeff));
              iCoeff++;
            }
          }
          haveMOs = true;
          mos[iMo].put("coefficients", coefs);
          if (alphaBeta.length() > 0)
            mos[iMo].put("type", alphaBeta);
          else if (moTypes != null && moCount < moTypes.size())
            mos[iMo].put("type", moTypes.get(moCount++));
          orbitals.addElement(mos[iMo]);
        }
        nThisLine = 0;
        if (line.length() == 0)
          continue;
        break;
      }
      
      if (nThisLine == 0) {
        nThisLine = tokens.length;
        if (tokens[0].equals("AO")) {
          
          
          nThisLine--;
          ptOffset = 16;
          fieldSize = 8;
          nSkip = 3;
            
        }
        if (mos == null || nThisLine > mos.length) {
           mos = new Hashtable[nThisLine];
           data = new Vector[nThisLine];
        }
        for (int i = 0; i < nThisLine; i++) {
          mos[i] = new Hashtable();
          data[i] = new Vector();
        }
        getMOHeader(headerType, tokens, mos, nThisLine);
        coeffLabels = new Vector();
        continue;
      }
      if (ptOffset < 0) {
        nSkip = tokens.length - nThisLine;
        for (int i = 0; i < nThisLine; i++)
          data[i].addElement(tokens[i + nSkip]);
      } else {
        int pt = ptOffset;
        for (int i = 0; i < nThisLine; i++, pt += fieldSize)
          data[i].addElement(line.substring(pt, pt + fieldSize).trim());
      }
      coeffLabels.addElement(JmolAdapter.canonicalizeQuantumSubshellTag(tokens[nSkip - 1].toUpperCase()));
      
      line = "";
    }
    energyUnits = "a.u.";
    setMOData(!alphaBeta.equals("alpha"));    
  }

  protected void getMOHeader(int headerType, String[] tokens, Hashtable[] mos, int nThisLine)
      throws Exception {
    readLine();
    switch (headerType) {
    default:
    case HEADER_NONE:
      
      for (int i = 0; i < nThisLine; i++) {
        mos[i].put("energy", "");
      }
      return;
    case HEADER_GAMESS_UK_MO:
      for (int i = 0; i < nThisLine; i++)
        mos[i].put("energy", new Float(tokens[i]));
      discardLines(5);
      return;
    case HEADER_GAMESS_ORIGINAL:
      
      tokens = getTokens();
      if (tokens.length == 0)
        tokens = getTokens(readLine());
      for (int i = 0; i < nThisLine; i++) {
        mos[i].put("energy", new Float(tokens[i]));
      }
      readLine();
      break;
    case HEADER_GAMESS_OCCUPANCIES:
      
      boolean haveSymmetry = (line.length() > 0 || readLine() != null);
      tokens = getTokens();
      for (int i = 0; i < nThisLine; i++)
        mos[i].put("occupancy", new Float(tokens[i].charAt(0) == '-' ? 2.0f
            : parseFloat(tokens[i])));
      readLine(); 
      if (!haveSymmetry)
        return;
      
      
    }
    if (line.length() > 0) {
      tokens = getTokens();
      for (int i = 0; i < nThisLine; i++)
        mos[i].put("symmetry", tokens[i]);
    }
  }

  protected void addMOData(int nColumns, Vector[] data, Hashtable[] mos) {
    for (int i = 0; i < nColumns; i++) {
      float[] coefs = new float[data[i].size()];
      for (int j = coefs.length; --j >= 0;)
        coefs[j] = parseFloat((String) data[i].get(j));
      mos[i].put("coefficients", coefs);
      orbitals.addElement(mos[i]);
    }
  }

  protected void setMOData(boolean clearOrbitals) {
    if (shells != null && gaussians != null) {
      moData.put("calculationType", calculationType);
      moData.put("energyUnits", energyUnits);
      moData.put("shells", shells);
      moData.put("gaussians", gaussians);
      moData.put("mos", orbitals);
      setMOData(moData);
    }
    if (clearOrbitals) {
      orbitals = new Vector();
      moData = new Hashtable();
      alphaBeta = "";
    }
  }
  
}
