

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;

import java.util.Hashtable;
import java.util.Vector;

import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

abstract public class GamessReader extends MOReader {

  protected Vector atomNames;

  abstract protected void readAtomsInBohrCoordinates() throws Exception;  
 
  protected void readGaussianBasis(String initiator, String terminator) throws Exception {
    Vector gdata = new Vector();
    gaussianCount = 0;
    int nGaussians = 0;
    shellCount = 0;
    String thisShell = "0";
    String[] tokens;
    discardLinesUntilContains(initiator);
    readLine();
    int[] slater = null;
    Hashtable shellsByAtomType = new Hashtable();
    Vector slatersByAtomType = new Vector();
    String atomType = null;
    
    while (readLine() != null && line.indexOf(terminator) < 0) {
      
      if (line.indexOf("(") >= 0)
        line = GamessReader.fixBasisLine(line);
      tokens = getTokens();
      switch (tokens.length) {
      case 1:
        if (atomType != null) {
          if (slater != null) {
            slater[2] = nGaussians;
            slatersByAtomType.addElement(slater);
            slater = null;
          }
          shellsByAtomType.put(atomType, slatersByAtomType);
        }
        slatersByAtomType = new Vector();
        atomType = tokens[0];
        break;
      case 0:
        break;
      default:
        if (!tokens[0].equals(thisShell)) {
          if (slater != null) {
            slater[2] = nGaussians;
            slatersByAtomType.addElement(slater);
          }
          thisShell = tokens[0];
          shellCount++;
          slater = new int[] {
              JmolAdapter.getQuantumShellTagID(fixShellTag(tokens[1])), gaussianCount,
              0 };
          nGaussians = 0;
        }
        ++nGaussians;
        ++gaussianCount;
        gdata.addElement(tokens);
      }
    }
    if (slater != null) {
      slater[2] = nGaussians;
      slatersByAtomType.addElement(slater);
    }
    if (atomType != null)
      shellsByAtomType.put(atomType, slatersByAtomType);
    gaussians = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++) {
      tokens = (String[]) gdata.get(i);
      gaussians[i] = new float[tokens.length - 3];
      for (int j = 3; j < tokens.length; j++)
        gaussians[i][j - 3] = parseFloat(tokens[j]);
    }
    int atomCount = atomNames.size();
    if (shells == null && atomCount > 0) {
      shells = new Vector();
      for (int i = 0; i < atomCount; i++) {
        atomType = (String) atomNames.elementAt(i);
        Vector slaters = (Vector) shellsByAtomType.get(atomType);
        if (slaters == null) {
          Logger.error("slater for atom " + i + " atomType " + atomType
              + " was not found in listing. Ignoring molecular orbitals");
          return;
        }
        for (int j = 0; j < slaters.size(); j++) {
          slater = (int[]) slaters.elementAt(j);
          shells.addElement(new int[] { i, slater[0], slater[1], slater[2] });
        }
      }
    }

    if (Logger.debugging) {
      Logger.debug(shellCount + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }

  abstract protected String fixShellTag(String tag);

  protected void readFrequencies() throws Exception {
    
    int totalFrequencyCount = 0;
    int atomCount = atomSetCollection.getLastAtomSetAtomCount();
    int atomIndex = atomSetCollection.getLastAtomSetAtomIndex();
    
    
    
    
    float[] xComponents = new float[10];
    float[] yComponents = new float[10];
    float[] zComponents = new float[10];
    float[] frequencies = new float[10];
    discardLinesUntilContains("FREQUENCY:");
    while (line != null && line.indexOf("FREQUENCY:") >= 0) {
      int frequencyCount = 0;
      String[] tokens = getTokens();
      for (int i = 0; i < tokens.length; i++) {
        float frequency = parseFloat(tokens[i]);
        if (tokens[i].equals("I"))
          frequencies[frequencyCount - 1] = -frequencies[frequencyCount - 1];
        if (Float.isNaN(frequency))
          continue; 
        frequencies[frequencyCount] = frequency;
        frequencyCount++;
        if (Logger.debugging) {
          Logger.debug(totalFrequencyCount + " frequency=" + frequency);
        }
        if (frequencyCount == frequencies.length)
          break;
      }
      String[] red_masses = null;
      String[] intensities = null;
      readLine();
      if (line.indexOf("MASS") >= 0) {
        red_masses = getTokens();
        readLine();
      }
      if (line.indexOf("INTENS") >= 0) {
        intensities = getTokens();
      }
      for (int i = 0; i < frequencyCount; i++) {
        ++totalFrequencyCount;
        
        
        if (totalFrequencyCount > 1)
          atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i] + " cm-1");
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i]
            + " cm-1");
        if (red_masses != null)
          atomSetCollection.setAtomSetProperty("Reduced Mass",
              red_masses[red_masses.length - frequencyCount + i] + " AMU");
        if (intensities != null)
          atomSetCollection.setAtomSetProperty("IR Intensity",
              intensities[intensities.length - frequencyCount + i]
                  + " D^2/AMU-Angstrom^2");

      }
      Atom[] atoms = atomSetCollection.getAtoms();
      discardLinesUntilBlank();
      
      
      int index0 = atomIndex - atomCount;
      for (int i = 0; i < atomCount; ++i) {
        atomIndex = index0 + i;
        readLine();
        readComponents(frequencyCount, xComponents);
        readLine();
        readComponents(frequencyCount, yComponents);
        readLine();
        readComponents(frequencyCount, zComponents);
        
        
        
        for (int j = 0; j < frequencyCount; ++j) {
          atomIndex += atomCount;
          Atom atom = atoms[atomIndex];
          atom.vectorX = xComponents[j];
          atom.vectorY = yComponents[j];
          atom.vectorZ = zComponents[j];
        }
      }
      atomIndex++;
      discardLines(12);
      readLine();
    }
  }

  private void readComponents(int count, float[] components) {
    for (int i = 0, start = 20; i < count; ++i, start += 12)
      components[i] = parseFloat(line, start, start + 12);
  }

  protected static String fixBasisLine(String line) {
    int pt, pt1;
    line = line.replace(')', ' ');
    while ((pt = line.indexOf("(")) >= 0) {
      pt1 = pt;
      while (line.charAt(--pt1) == ' '){}
      while (line.charAt(--pt1) != ' '){}
      line = line.substring(0, ++pt1) + line.substring(pt + 1);
    }
    return line;
  }

  



  private Hashtable calcOptions;
  private boolean isTypeSet;

  protected void setCalculationType() {
    if (calcOptions == null || isTypeSet)
      return;
    isTypeSet = true;
    String SCFtype = (String) calcOptions.get("contrl_options_SCFTYP");
    String Runtype = (String) calcOptions.get("contrl_options_RUNTYP");
    String igauss = (String) calcOptions.get("basis_options_IGAUSS");
    String gbasis = (String) calcOptions.get("basis_options_GBASIS");
    boolean DFunc = !"0".equals((String) calcOptions
        .get("basis_options_NDFUNC"));
    boolean PFunc = !"0".equals((String) calcOptions
        .get("basis_options_NPFUNC"));
    boolean FFunc = !"0".equals((String) calcOptions
        .get("basis_options_NFFUNC"));
    String DFTtype = (String) calcOptions.get("contrl_options_DFTTYP");
    int perturb = parseInt((String) calcOptions.get("contrl_options_MPLEVL"));
    String CItype = (String) calcOptions.get("contrl_options_CITYP");
    String CCtype = (String) calcOptions.get("contrl_options_CCTYP");

    if (igauss == null && SCFtype == null)
      return;

    if (calculationType.equals("?"))
      calculationType = "";

    if (igauss != null) {
      if ("0".equals(igauss)) { 
        
        
        boolean recognized = false;
        if (calculationType.length() > 0)
          calculationType += " ";
        if (gbasis.startsWith("ACC"))
          calculationType += "aug-cc-p";
        if (gbasis.startsWith("CC"))
          calculationType += "cc-p";
        if ((gbasis.startsWith("ACC") || gbasis.startsWith("CC"))
            && gbasis.endsWith("C"))
          calculationType += "C";
        if (gbasis.contains("CCD")) {
          calculationType += "VDZ";
          recognized = true;
        }
        if (gbasis.contains("CCT")) {
          calculationType += "VTZ";
          recognized = true;
        }
        if (gbasis.contains("CCQ")) {
          calculationType += "VQZ";
          recognized = true;
        }
        if (gbasis.contains("CC5")) {
          calculationType += "V5Z";
          recognized = true;
        }
        if (gbasis.contains("CC6")) {
          calculationType += "V6Z";
          recognized = true;
        }
        if (!recognized)
          calculationType += gbasis;
      } else {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += igauss + "-"
            + TextFormat.simpleReplace(gbasis, "N", "");
        if ("T".equals((String) calcOptions.get("basis_options_DIFFSP"))) {
          
          if ("T".equals((String) calcOptions.get("basis_options_DIFFS")))
            calculationType += "+";
          calculationType += "+";
        }
        calculationType += "G";
        
        
        if (DFunc || PFunc || FFunc) {
          calculationType += "(";
          if (FFunc) {
            calculationType += "f";
            if (DFunc || PFunc)
              calculationType += ",";
          }
          if (DFunc) {
            calculationType += "d";
            if (PFunc)
              calculationType += ",";
          }
          if (PFunc)
            calculationType += "p";
          calculationType += ")";
        }
      }
      if (DFTtype!=null && !DFTtype.contains("NONE")) {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += DFTtype;
      }
      if (CItype !=null && !CItype.contains("NONE")) {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += CItype;
      }
      if (CCtype !=null && !CCtype.contains("NONE")) {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += CCtype;
      }
      if (perturb > 0) {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += "MP" + perturb;
      }
      if (SCFtype != null) {
        if (calculationType.length() > 0)
          calculationType += " ";
        calculationType += SCFtype + " " + Runtype;
      }
    }
  }

  protected void readControlInfo() throws Exception {
    readCalculationInfo("contrl_options_");
  }

  protected void readBasisInfo() throws Exception {
    readCalculationInfo("basis_options_");
  }

  private void readCalculationInfo(String type) throws Exception {
    if (calcOptions == null) {
      calcOptions = new Hashtable();
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("calculationOptions",
          calcOptions);
    }
    while (readLine() != null && (line = line.trim()).length() > 0) {
      if (line.indexOf("=") < 0)
        continue;
      String[] tokens = getTokens(TextFormat.simpleReplace(line, "="," = "));
      for (int i = 0; i < tokens.length; i++) {
        if (!tokens[i].equals("="))
          continue;
        try {
        String key = type + tokens[i - 1];
        String value = (key.equals("basis_options_SPLIT3") ? tokens[++i] + " " + tokens[++i]
            + " " + tokens[++i] : tokens[++i]);
        if (Logger.debugging)
          Logger.debug(key + " = " + value);
        calcOptions.put(key, value);
        } catch (Exception e) {
          
        }
      }
    }
  }


}
