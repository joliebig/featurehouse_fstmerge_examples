

package org.jmol.minimize.forcefield;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.minimize.Minimizer;
import org.jmol.util.Logger;
import org.jmol.util.Parser;


public class ForceFieldUFF extends ForceField {

  
  public void setModel(Minimizer m) {
    super.setModel(m);
    calc = new CalculationsUFF(this, m.minAtoms, m.minBonds, 
        m.angles, m.torsions, m.partialCharges, m.constraints);
  }

  protected Hashtable getFFParameters() {
    FFParam ffParam;

    Hashtable temp = new Hashtable();

    
    URL url = null;
    String fileName = "UFF.txt";
    BufferedReader br = null;
    try {
      if ((url = this.getClass().getResource(fileName)) == null) {
        System.err.println("Couldn't find file: " + fileName);
        throw new NullPointerException();
      }
      
      
      

      br = new BufferedReader(new InputStreamReader(
          (InputStream) url.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
        String[] vs = Parser.getTokens(line);
        if (vs.length < 13)
          continue;
        if (Logger.debugging)
          Logger.info(line);
        if (line.substring(0, 5).equals("param")) {
          
          ffParam = new FFParam();
          temp.put(vs[1], ffParam);
          ffParam.dVal = new double[11];
          ffParam.sVal = new String[1];
          ffParam.sVal[0] = vs[1]; 
          
          ffParam.dVal[CalculationsUFF.PAR_R] = Parser.parseFloat(vs[2]); 
          ffParam.dVal[CalculationsUFF.PAR_THETA] = Parser.parseFloat(vs[3]) 
             * Calculations.DEG_TO_RAD; 
          ffParam.dVal[CalculationsUFF.PAR_X] = Parser.parseFloat(vs[4]); 
          ffParam.dVal[CalculationsUFF.PAR_D] = Parser.parseFloat(vs[5]); 
          ffParam.dVal[CalculationsUFF.PAR_ZETA] = Parser.parseFloat(vs[6]); 
          ffParam.dVal[CalculationsUFF.PAR_Z] = Parser.parseFloat(vs[7]); 
          ffParam.dVal[CalculationsUFF.PAR_V] = Parser.parseFloat(vs[8]); 
          ffParam.dVal[CalculationsUFF.PAR_U] = Parser.parseFloat(vs[9]); 
          ffParam.dVal[CalculationsUFF.PAR_XI] = Parser.parseFloat(vs[10]); 
          ffParam.dVal[CalculationsUFF.PAR_HARD] = Parser.parseFloat(vs[11]); 
          ffParam.dVal[CalculationsUFF.PAR_RADIUS] = Parser.parseFloat(vs[12]); 
          
          ffParam.iVal = new int[1];

          char coord = (vs[1].length() > 2 ? vs[1].charAt(2) : '1'); 

          switch (coord) {
          case 'R':
            coord = '2';
            break;
          default: 
            
            coord = '1';
            break;
          case '1': 
          case '2': 
          case '3': 
          case '4': 
          case '5': 
          case '6': 
            break;
          }
          ffParam.iVal[0] = coord - '0';
        }
      }
      br.close();
    } catch (Exception e) {
      System.err.println("Exception " + e.getMessage() + " in getResource "
          + fileName);
      try{
        br.close();
      } catch (Exception ee) {
        
      }
      return null;
    }
    Logger.info(temp.size() + " atom types read from " + fileName);
    return temp;
  }

  public Vector getAtomTypes() {
    Vector types = new Vector(); 
    URL url = null;
    String fileName = "UFF.txt";
    try {
      if ((url = this.getClass().getResource(fileName)) == null) {
        System.err.println("Couldn't find file: " + fileName);
        throw new NullPointerException();
      }

      
      
      

      BufferedReader br = new BufferedReader(new InputStreamReader(
          (InputStream) url.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() > 4 && line.substring(0, 4).equals("atom")) {
          String[] vs = Parser.getTokens(line);
          String[] info = new String[] { vs[1], vs[2] };
          types.addElement(info);
        }
      }

      br.close();
    } catch (Exception e) {
      System.err.println("Exception " + e.getMessage() + " in getResource "
          + fileName);

    }
    Logger.info(types.size() + " force field parameters read");
    return (types.size() > 0 ? types : null);
  }
}
