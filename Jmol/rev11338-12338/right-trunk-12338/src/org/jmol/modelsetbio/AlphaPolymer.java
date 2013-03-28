
package org.jmol.modelsetbio;

import java.util.BitSet;
import java.util.Vector;

import org.jmol.modelset.Atom;
import org.jmol.modelset.ModelSet;

import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;


public class AlphaPolymer extends BioPolymer {

  AlphaPolymer(Monomer[] monomers) {
    super(monomers);
    
  }

  public void addSecondaryStructure(byte type,
                                    String structureID, int serialID, int strandCount,
                             char startChainID, int startSeqcode,
                             char endChainID, int endSeqcode) {
    int indexStart, indexEnd;
    if ((indexStart = getIndex(startChainID, startSeqcode)) == -1 ||
        (indexEnd = getIndex(endChainID, endSeqcode)) == -1)
      return;
    
    addSecondaryStructure(type, structureID, serialID, strandCount, indexStart, indexEnd);
  }

  void addSecondaryStructure(byte type, 
                             String structureID, int serialID, int strandCount,
                             int indexStart, int indexEnd) {

    

    
    if (indexEnd < indexStart) {
      Logger.error("AlphaPolymer:addSecondaryStructure error: " +
                         " indexStart:" + indexStart +
                         " indexEnd:" + indexEnd);
      return;
    }
    int structureCount = indexEnd - indexStart + 1;
    ProteinStructure proteinstructure = null;
    switch(type) {
    case JmolConstants.PROTEIN_STRUCTURE_HELIX:
      proteinstructure = new Helix(this, indexStart, structureCount, 0);
      break;
    case JmolConstants.PROTEIN_STRUCTURE_SHEET:

        proteinstructure = new Sheet(this, indexStart, structureCount, 0);
      break;
    case JmolConstants.PROTEIN_STRUCTURE_TURN:
      proteinstructure = new Turn(this, indexStart, structureCount, 0);
      break;
    default:
      Logger.error("unrecognized secondary structure type");
      return;
    }
    proteinstructure.structureID = structureID;
    proteinstructure.serialID = serialID;
    proteinstructure.strandCount = strandCount;
    for (int i = indexStart; i <= indexEnd; ++i)
      monomers[i].setStructure(proteinstructure);
  }

  void calcHydrogenBonds() {
    
  }

  
  public void calculateStructures() {
    if (monomerCount < 4)
      return;
    float[] angles = calculateAnglesInDegrees();
    byte[] codes = calculateCodes(angles);
    checkBetaSheetAlphaHelixOverlap(codes, angles);
    byte[] tags = calculateRunsFourOrMore(codes);
    extendRuns(tags);
    searchForTurns(codes, angles, tags);
    addStructuresFromTags(tags);
  }

  final static byte CODE_NADA        = 0;
  final static byte CODE_RIGHT_HELIX = 1;
  final static byte CODE_BETA_SHEET  = 2;
  final static byte CODE_LEFT_HELIX  = 3;

  final static byte CODE_LEFT_TURN  = 4;
  final static byte CODE_RIGHT_TURN = 5;
  
  float[] calculateAnglesInDegrees() {
    float[] angles = new float[monomerCount];
    for (int i = monomerCount - 1; --i >= 2; )
      angles[i] = 
        Measure.computeTorsion(monomers[i - 2].getLeadAtomPoint(),
                                   monomers[i - 1].getLeadAtomPoint(),
                                   monomers[i    ].getLeadAtomPoint(),
                                   monomers[i + 1].getLeadAtomPoint(), true);
    return angles;
  }

  byte[] calculateCodes(float[] angles) {
    byte[] codes = new byte[monomerCount];
    for (int i = monomerCount - 1; --i >= 2; ) {
      float degrees = angles[i];
      codes[i] = ((degrees >= 10 && degrees < 120)
                  ? CODE_RIGHT_HELIX
                  : ((degrees >= 120 || degrees < -90)
                     ? CODE_BETA_SHEET
                     : ((degrees >= -90 && degrees < 0)
                        ? CODE_LEFT_HELIX
                        : CODE_NADA)));
    }
    return codes;
  }

  void checkBetaSheetAlphaHelixOverlap(byte[] codes, float[] angles) {
    for (int i = monomerCount - 2; --i >= 2; )
      if (codes[i] == CODE_BETA_SHEET &&
          angles[i] <= 140 &&
          codes[i - 2] == CODE_RIGHT_HELIX &&
          codes[i - 1] == CODE_RIGHT_HELIX &&
          codes[i + 1] == CODE_RIGHT_HELIX &&
          codes[i + 2] == CODE_RIGHT_HELIX)
        codes[i] = CODE_RIGHT_HELIX;
  }

  final static byte TAG_NADA  = JmolConstants.PROTEIN_STRUCTURE_NONE;
  final static byte TAG_TURN  = JmolConstants.PROTEIN_STRUCTURE_TURN;
  final static byte TAG_SHEET = JmolConstants.PROTEIN_STRUCTURE_SHEET;
  final static byte TAG_HELIX = JmolConstants.PROTEIN_STRUCTURE_HELIX;

  byte[] calculateRunsFourOrMore(byte[] codes) {
    byte[] tags = new byte[monomerCount];
    byte tag = TAG_NADA;
    byte code = CODE_NADA;
    int runLength = 0;
    for (int i = 0; i < monomerCount; ++i) {
      
      if (codes[i] == code && code != CODE_NADA && code != CODE_BETA_SHEET) {
        ++runLength;
        if (runLength == 4) {
          tag = (code == CODE_BETA_SHEET ? TAG_SHEET : TAG_HELIX);
          for (int j = 4; --j >= 0; )
            tags[i - j] = tag;
        } else if (runLength > 4)
          tags[i] = tag;
      } else {
        runLength = 1;
        code = codes[i];
      }
    }
    return tags;
  }

  void extendRuns(byte[] tags) {
    for (int i = 1; i < monomerCount - 4; ++i)
      if (tags[i] == TAG_NADA && tags[i + 1] != TAG_NADA)
        tags[i] = tags[i + 1];
    
    tags[0] = tags[1];
    tags[monomerCount - 1] = tags[monomerCount - 2];
  }

  void searchForTurns(byte[] codes, float[] angles, byte[] tags) {
    for (int i = monomerCount - 1; --i >= 2; ) {
      codes[i] = CODE_NADA;
      if (tags[i] == TAG_NADA) {
        float angle = angles[i];
        if (angle >= -90 && angle < 0)
          codes[i] = CODE_LEFT_TURN;
        else if (angle >= 0 && angle < 90)
          codes[i] = CODE_RIGHT_TURN;
      }
    }

    for (int i = monomerCount - 1; --i >= 0; ) {
      if (codes[i] != CODE_NADA &&
          codes[i + 1] == codes[i] &&
          tags[i] == TAG_NADA)
        tags[i] = TAG_TURN;
    }
  }

  void addStructuresFromTags(byte[] tags) {
    int i = 0;
    while (i < monomerCount) {
      byte tag = tags[i];
      if (tag == TAG_NADA) {
        ++i;
        continue;
      }
      int iMax;
      for (iMax = i + 1;
           iMax < monomerCount && tags[iMax] == tag;
           ++iMax)
        { }
      addSecondaryStructure(tag, null, 0, 0, i, iMax - 1);
      i = iMax;
    }
  }
  
  public void getPdbData(Viewer viewer, char ctype, char qtype, int mStep, int derivType,
                         boolean isDraw, BitSet bsAtoms, 
                         StringBuffer pdbATOM, StringBuffer pdbCONECT, 
                         BitSet bsSelected, boolean addHeader, BitSet bsWritten) {
    getPdbData(viewer, this, ctype, qtype, mStep, derivType, isDraw, bsAtoms, pdbATOM, 
        pdbCONECT, bsSelected, addHeader, bsWritten);
  }

  
  public Vector calculateStruts(ModelSet modelSet, Atom[] atoms, BitSet bs1,
                                BitSet bs2, Vector vCA, float thresh, int delta, boolean allowMultiple) {
    Vector vStruts = new Vector(); 
    float thresh2 = thresh * thresh; 

    
    
    int n = vCA.size();
    int nEndMin = 3;

    
    
    
    
    BitSet bsStruts = new BitSet();         
    BitSet bsNotAvailable = new BitSet();   
    BitSet bsNearbyResidues = new BitSet(); 
    
    
    
    
    Atom a1 = (Atom) vCA.get(0);
    Atom a2;
    int nBiopolymers = modelSet.getBioPolymerCountInModel(a1.modelIndex);
    int[][] biopolymerStartsEnds = new int[nBiopolymers][nEndMin * 2];
    for (int i = 0; i < n; i++) {
      a1 = (Atom) vCA.get(i);
      int polymerIndex = a1.getPolymerIndexInModel();
      int monomerIndex = a1.getMonomerIndex();
      int bpt = monomerIndex;
      if (bpt < nEndMin)
        biopolymerStartsEnds[polymerIndex][bpt] = i + 1;
      bpt = ((Monomer) a1.getGroup()).getBioPolymerLength() - monomerIndex - 1;
      if (bpt < nEndMin)
        biopolymerStartsEnds[polymerIndex][nEndMin + bpt] = i + 1;
    }

    
    
    
    
    
    

    float[] d2 = new float[n * (n - 1) / 2];
    for (int i = 0; i < n; i++) {
      a1 = (Atom) vCA.get(i);
      for (int j = i + 1; j < n; j++) {
        int ipt = strutPoint(i, j, n);
        a2 = (Atom) vCA.get(j);
        int resno1 = a1.getResno();
        int polymerIndex1 = a1.getPolymerIndexInModel();
        int resno2 = a2.getResno();
        int polymerIndex2 = a2.getPolymerIndexInModel();
        if (polymerIndex1 == polymerIndex2 && Math.abs(resno2 - resno1) < delta)
          bsNearbyResidues.set(ipt);
        float d = d2[ipt] = a1.distanceSquared((Atom) vCA.get(j));
        if (d >= thresh2)
          bsNotAvailable.set(ipt);
      }
    }

    
    

    for (int t = 5; --t >= 0;) { 
      thresh2 = (thresh - t) * (thresh - t);
      for (int i = 0; i < n; i++)
        if (allowMultiple || !bsStruts.get(i))
        for (int j = i + 1; j < n; j++) {
          int ipt = strutPoint(i, j, n);
          if (!bsNotAvailable.get(ipt) && !bsNearbyResidues.get(ipt)
              && (allowMultiple || !bsStruts.get(j)) && d2[ipt] <= thresh2)
            setStrut(i, j, n, vCA, bs1, bs2, vStruts, bsStruts, bsNotAvailable,
                bsNearbyResidues, atoms, delta);
        }
    }

    
    
    
    

    for (int b = 0; b < nBiopolymers; b++) {
      
      for (int k = 0; k < nEndMin * 2; k++) {
        int i = biopolymerStartsEnds[b][k] - 1;
        if (i >= 0 && bsStruts.get(i)) {
          for (int j = 0; j < nEndMin; j++) {
            int pt = (k / nEndMin) * nEndMin + j;
            if ((i = biopolymerStartsEnds[b][pt] - 1) >= 0)
              bsStruts.set(i);
            biopolymerStartsEnds[b][pt] = -1;
          }
        }
      }
      if (biopolymerStartsEnds[b][0] == -1 && biopolymerStartsEnds[b][nEndMin] == -1)
        continue;
      boolean okN = false;
      boolean okC = false;
      int iN = 0;
      int jN = 0;
      int iC = 0;
      int jC = 0;
      float minN = Float.MAX_VALUE;
      float minC = Float.MAX_VALUE;
      for (int j = 0; j < n; j++)
        for (int k = 0; k < nEndMin * 2; k++) {
          int i = biopolymerStartsEnds[b][k] - 1;
          if (i == -2) {
            
            k = (k / nEndMin + 1) * nEndMin - 1;
            continue;
          }
          if (j == i || i == -1)
            continue;
          int ipt = strutPoint(i, j, n);
          if (bsNearbyResidues.get(ipt)
              || d2[ipt] > (k < nEndMin ? minN : minC))
            continue;
          if (k < nEndMin) {
            if (bsNotAvailable.get(ipt))
              okN = true;
            jN = j;
            iN = i;
            minN = d2[ipt];
          } else {
            if (bsNotAvailable.get(ipt))
              okC = true;
            jC = j;
            iC = i;
            minC = d2[ipt];
          }
        }
      if (okN)
        setStrut(iN, jN, n, vCA, bs1, bs2, vStruts, bsStruts, bsNotAvailable,
            bsNearbyResidues, atoms, delta);
      if (okC)
        setStrut(iC, jC, n, vCA, bs1, bs2, vStruts, bsStruts, bsNotAvailable,
            bsNearbyResidues, atoms, delta);
    }
    return vStruts;
  }

  private int strutPoint(int i, int j, int n) {
    return (j < i ? j * (2 * n - j - 1) / 2 + i - j - 1
     : i * (2 * n - i - 1) / 2 + j - i - 1);
  }

  private void setStrut(int i, int j, int n, Vector vCA, BitSet bs1, BitSet bs2, 
                        Vector vStruts,
                        BitSet bsStruts, BitSet bsNotAvailable,
                        BitSet bsNearbyResidues, Atom[] atoms, int delta) {
    Atom a1 = (Atom) vCA.get(i);
    Atom a2 = (Atom) vCA.get(j);
    if (!bs1.get(a1.index) || !bs2.get(a2.index))
      return;
    vStruts.add(new Object[] { a1, a2 });
    bsStruts.set(i);
    bsStruts.set(j);
    for (int k1 = Math.max(0, i - delta); k1 <= i + delta && k1 < n; k1++)
      for (int k2 = Math.max(0, j - delta); k2 <= j + delta && k2 < n; k2++) {
        if (k1 == k2)
          continue;
        int ipt = strutPoint(k1, k2, n);
        if (!bsNearbyResidues.get(ipt))
          bsNotAvailable.set(ipt);
      }
  }
}
