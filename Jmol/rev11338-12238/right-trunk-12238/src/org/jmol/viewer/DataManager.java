
package org.jmol.viewer;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Parser;



class DataManager {

  private Hashtable dataValues = new Hashtable();

  Viewer viewer;
  DataManager(Viewer viewer) {
    this.viewer = viewer;
  }

  void clear() {
    dataValues.clear();
  }
  
  void setData(String type, Object[] data, int atomCount,
               int matchField, int matchFieldColumnCount, int field,
               int fieldColumnCount) {
    
    
    if (type == null) {
      clear();
      return;
    }
    if (type.equals("element_vdw")) {
      String stringData = ((String) data[1]).trim();
      if (stringData.length() == 0) {
        userVdwMars = null;
        userVdws = null;
        bsUserVdws = null;
        return;
      }
      if (bsUserVdws == null)
        setUserVdw(defaultVdw);
      Parser.parseFloatArrayFromMatchAndField(stringData, bsUserVdws, 1, 0,
          (int[]) data[2], 2, 0, userVdws, 1);
      for (int i = userVdws.length; --i >= 0;)
        userVdwMars[i] = (int) (userVdws[i] * 1000);
      return;
    }
    if (data[2] != null && atomCount > 0) {
      String stringData = (String) data[1];
      boolean createNew = (matchField != 0 || field != Integer.MIN_VALUE
          && field != Integer.MAX_VALUE);
      Object[] oldData = (Object[]) dataValues.get(type);
      BitSet bs;
      float[] f = (oldData == null || createNew ? new float[atomCount]
          : ArrayUtil.ensureLength(((float[]) oldData[1]), atomCount));

      
      

      String[] strData = null;
      if (field == Integer.MIN_VALUE
          && (strData = Parser.getTokens(stringData)).length > 1)
        field = 0;

      if (field == Integer.MIN_VALUE) {
        
        bs = (BitSet) data[2];
        Parser.setSelectedFloats(Parser.parseFloat(stringData), bs, f);
      } else if (field == 0 || field == Integer.MAX_VALUE) {
        
        bs = (BitSet) data[2];
        Parser.parseFloatArray(strData == null ? Parser.getTokens(stringData)
            : strData, bs, f);
      } else if (matchField <= 0) {
        
        bs = (BitSet) data[2];
        Parser.parseFloatArrayFromMatchAndField(stringData, bs, 0, 0, null,
            field, fieldColumnCount, f, 1);
      } else {
        
        
        int[] iData = (int[]) data[2];
        Parser.parseFloatArrayFromMatchAndField(stringData, null, matchField,
            matchFieldColumnCount, iData, field, fieldColumnCount, f, 1);
        bs = new BitSet();
        for (int i = iData.length; --i >= 0;)
          if (iData[i] >= 0)
            bs.set(iData[i]);
      }
      if (oldData != null && oldData[2] instanceof BitSet && !createNew)
        bs.or((BitSet) (oldData[2]));
      data[2] = bs;
      data[1] = f;
      if (type.indexOf("property_") == 0) {
        int tok = Token.getSettableTokFromString(type.substring(9));
        if (tok != Token.nada) {
          int nValues = bs.cardinality();
          float[] fValues = new float[nValues];
          for (int n = 0, i = 0; n < nValues; i++)
            if (bs.get(i))
              fValues[n++] = f[i];
          viewer.setAtomProperty(bs, tok, 0, 0, null, fValues, null);
          return;
        }
      }
    }
    dataValues.put(type, data);
  }

  Object[] getData(String type) {
    if (dataValues == null || type == null)
      return null;
    if (type.equalsIgnoreCase("types")) {
      String[] info = new String[2];
      info[0] = "types";
      info[1] = "";
      int n = 0;
      Enumeration e = (dataValues.keys());
      while (e.hasMoreElements())
        info[1] += (n++ > 0 ? "\n" : "") + e.nextElement();
      return info;
    }
    return (Object[]) dataValues.get(type);
  }

  float[] getDataFloat(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[]))
      return null;
    return (float[]) data[1];
  }

  float getDataFloat(String label, int atomIndex) {
    if (dataValues != null) {
      Object[] data = getData(label);
      if (data != null && data[1] instanceof float[]) {
        float[] f = (float[]) data[1];
        if (atomIndex < f.length)
          return f[atomIndex];
      }
    }
    return Float.NaN;
  }

  float[][] getDataFloat2D(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[][]))
      return null;
    return (float[][]) data[1];
  }

  float[][][] getDataFloat3D(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[][][]))
      return null;
    return (float[][][]) data[1];
  }

  protected void deleteModelAtoms(int firstAtomIndex, int nAtoms, BitSet bsDeleted) {
    if (dataValues == null)
      return;
    Enumeration e = (dataValues.keys());
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (name.indexOf("property_") == 0) {
        Object[] obj = (Object[]) dataValues.get(name);
        BitSetUtil.deleteBits((BitSet) obj[2], bsDeleted);
        if (obj[1] instanceof float[]) {
          obj[1] = ArrayUtil.deleteElements((float[]) obj[1], firstAtomIndex, nAtoms);
        } else if (obj[1] instanceof float[][]){
          obj[1] = ArrayUtil.deleteElements((float[][]) obj[1], firstAtomIndex, nAtoms);
        } else {
          
        }
      }
    }    
  }

  void getDataState(StringBuffer state, StringBuffer sfunc, Atom[] atoms,
                    int atomCount, String atomProps) {
    if (dataValues == null)
      return;
    Enumeration e = (dataValues.keys());
    StringBuffer sb = new StringBuffer();
    int n = 0;
    if (atomProps.length() > 0) {
      n = 1;
      sb.append(atomProps);
    }
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (name.indexOf("property_") == 0) {
        n++;
        Object[] obj = (Object[]) dataValues.get(name);
        Object data = obj[1];
        if (data instanceof float[]) {
          AtomCollection.getAtomicPropertyState(viewer, sb, atoms, atomCount,
              AtomCollection.TAINT_MAX, 
              (BitSet) obj[2], 
              name, (float[]) data);
          sb.append("\n");
        } else {
          sb.append("\n").append(Escape.encapsulateData(name, data));
        }
      } else if (name.indexOf("data2d") == 0) {
        Object data = ((Object[]) dataValues.get(name))[1];
        if (data instanceof float[][]) {
          n++;
          sb.append("\n").append(Escape.encapsulateData(name, data));
        }
      }
    }
    
    if (userVdws != null) {
      String info = getDefaultVdwNameOrData(JmolConstants.VDW_USER, bsUserVdws);
      if (info.length() > 0) {
        n++;
        sb.append(info);
      }
    }
    
    if (n == 0)
      return;
    if (sfunc != null)
      state.append("function _setDataState() {\n");
    state.append(sb);  
    if (sfunc != null) {
      sfunc.append("  _setDataState;\n");
      state.append("}\n\n");
    }
  }

  int[] userVdwMars;
  float[] userVdws;
  int defaultVdw = JmolConstants.VDW_JMOL;
  BitSet bsUserVdws;
  
  public void setUserVdw(int iMode) {
    userVdwMars = new int[JmolConstants.elementNumberMax];
    userVdws = new float[JmolConstants.elementNumberMax];
    bsUserVdws = new BitSet();
    if (iMode == JmolConstants.VDW_USER)
      iMode = JmolConstants.VDW_JMOL;
    for (int i = 1; i < JmolConstants.elementNumberMax; i++) {
      userVdwMars[i] = JmolConstants.getVanderwaalsMar(i, iMode);
      userVdws[i] = userVdwMars[i] / 1000f;
    }
  }

  public void setDefaultVdw(int iType) {
    
    switch (iType) {
    case JmolConstants.VDW_JMOL:
    case JmolConstants.VDW_BABEL:
    case JmolConstants.VDW_RASMOL:
    case JmolConstants.VDW_AUTO:
      break;
    default:
      iType = JmolConstants.VDW_JMOL;
    }
    if (iType != defaultVdw && iType == JmolConstants.VDW_USER  
        && bsUserVdws == null)
      setUserVdw(defaultVdw);
    defaultVdw = iType;    
  }

  String getDefaultVdwNameOrData(int iType, BitSet bs) {
    
    
    switch (iType) {
    case Integer.MIN_VALUE:
      
      return JmolConstants.vdwLabels[defaultVdw];
    case Integer.MAX_VALUE:
      
      if ((bs = bsUserVdws) == null)
        return "";
      iType = JmolConstants.VDW_USER;
      break;
    case JmolConstants.VDW_AUTO:
    case JmolConstants.VDW_UNKNOWN:
      iType = defaultVdw;
      break;      
    }
    if (iType == JmolConstants.VDW_USER && bsUserVdws == null) {
      setUserVdw(defaultVdw);
    }
    StringBuffer sb = new StringBuffer(JmolConstants.vdwLabels[iType] + "\n");
    for (int i = 1; i < JmolConstants.elementNumberMax; i++)
      if (bs == null || bs.get(i))
        sb.append(i).append('\t').append(
            iType == JmolConstants.VDW_USER ? userVdws[i] : JmolConstants
                .getVanderwaalsMar(i, iType) / 1000f).append('\t').append(
            JmolConstants.elementSymbolFromNumber(i)).append('\n');
    return (bs == null ? sb.toString() : "\n  DATA \"element_vdw\"\n"
        + sb.append("  end \"element_vdw\";\n\n").toString());
  }

}
