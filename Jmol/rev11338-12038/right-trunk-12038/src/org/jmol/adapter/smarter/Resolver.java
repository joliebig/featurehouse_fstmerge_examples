

package org.jmol.adapter.smarter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import netscape.javascript.JSObject;

import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;
import org.jmol.util.ZipUtil;


public class Resolver {

  
  static String getFileType(BufferedReader br) {
    try {
      return determineAtomSetCollectionReader(br, false);
    } catch (Exception e) {
      return null;
    }
  }

  
  static String[] specialLoad(String name, String type) {
    int pt = name.lastIndexOf(".spardir");
    boolean isPreliminary = (type.equals("filesNeeded?"));
    if (isPreliminary) {
      
      if (name.endsWith(".spt"))
        return new String[] { null, null, null }; 
      
      if (name.endsWith(".spardir.zip"))
        return new String[] { "SpartanSmol", "Directory Entry ", "?|output"};
      name = name.replace('\\', '/');
      if (!name.endsWith(".spardir") && name.indexOf(".spardir/") < 0)
        return null; 
      
      if (pt < 0)
        return null;
      if (name.lastIndexOf("/") > pt) {
        
        return new String[] { "SpartanSmol", "Directory Entry ",
            name + "/input", name + "/archive",
            name + "/Molecule:asBinaryString", name + "/proparc" };      
      }
      return new String[] { "SpartanSmol", "Directory Entry ", name + "/output" };
    }
    
    String[] dirNums = getSpartanDirs(type);
    if (dirNums.length == 0 && name.endsWith(".spardir.zip") 
        && type.indexOf(".zip|output") >= 0) {
      
      String sname = name.replace('\\','/');
      pt = sname.lastIndexOf("/");
      
      sname = name + "|" + name.substring(pt + 1, name.length() - 4);
      return new String[] { "SpartanSmol", sname, sname + "/output" };
    }    
    return getSpartanFileList(name, dirNums);
  }

  
  static String[] checkSpecialInZip(String[] zipDirectory) {
    String name;
    return (zipDirectory.length < 2 ? null 
        : (name = zipDirectory[1]).endsWith(".spardir/") || zipDirectory.length == 2 ?
        new String[] { "",
          (name.endsWith("/") ? name.substring(0, name.length() - 1) : name) } 
        : null);
  }

  
  static StringBuffer checkSpecialData(InputStream is, String[] zipDirectory) {
    boolean isSpartan = false;
    
    for (int i = 1; i < zipDirectory.length; i++) {
      if (zipDirectory[i].endsWith(".spardir/")
          || zipDirectory[i].indexOf("_spartandir") >= 0) {
        isSpartan = true;
        break;
      }
    }
    if (!isSpartan)
      return null;
    StringBuffer data = new StringBuffer();
    data.append("Zip File Directory: ").append("\n").append(
        Escape.escape(zipDirectory, true)).append("\n");
    Hashtable fileData = new Hashtable();
    ZipUtil.getAllData(is, new String[] {}, "",
        "Molecule", fileData);
    String prefix = "|";
    String outputData = (String) fileData.get(prefix + "output");
    if (outputData == null)
      outputData = (String) fileData.get((prefix = "|" + zipDirectory[1])
          + "output");
    data.append(outputData);
    String[] files = getSpartanFileList(prefix, getSpartanDirs(outputData));
    for (int i = 2; i < files.length; i++) {
      String name = files[i];
      if (fileData.containsKey(name))
        data.append(fileData.get(name));
      else
        data.append(name + "\n");
    }
    return data;
  }

  
  static Object getAtomCollectionAndCloseReader(String fullName, String type,
                        BufferedReader bufferedReader, Hashtable htParams,
                        int ptFile) throws Exception {
    AtomSetCollectionReader atomSetCollectionReader = null;
    String atomSetCollectionReaderName;
    fullName = fullName.replace('\\','/');
    String errMsg = null;
    if (type != null) {
      atomSetCollectionReaderName = getReaderFromType(type);
      if (atomSetCollectionReaderName == null)
        errMsg =  "unrecognized file format type " + type;
      else 
        Logger.info("The Resolver assumes " + atomSetCollectionReaderName);
    } else {
      atomSetCollectionReaderName = determineAtomSetCollectionReader(
          bufferedReader, true);
      if (atomSetCollectionReaderName.indexOf("\n") >= 0)
        errMsg = "unrecognized file format for file " + fullName + "\n"
            + atomSetCollectionReaderName;
      else if (atomSetCollectionReaderName.equals("spt"))
        errMsg = "NOTE: file recognized as a script file: " + fullName + "\n";
      else
        Logger.info("The Resolver thinks " + atomSetCollectionReaderName);
    }
    if (errMsg != null) {
      bufferedReader.close();
      return errMsg;
    }
    if (htParams == null)
      htParams = new Hashtable();
    htParams.put("ptFile", new Integer(ptFile));
    if (ptFile <= 0)
      htParams.put("readerName", atomSetCollectionReaderName);
    if (atomSetCollectionReaderName.indexOf("Xml") == 0)
      atomSetCollectionReaderName = "Xml";
    String className = null;
    Class atomSetCollectionReaderClass;
    String err = null;
    try {
      className = getReaderClassBase(atomSetCollectionReaderName);
      atomSetCollectionReaderClass = Class.forName(className);
      atomSetCollectionReader = (AtomSetCollectionReader) atomSetCollectionReaderClass
          .newInstance();
      return atomSetCollectionReader.readData(fullName, htParams, bufferedReader);
    } catch (Exception e) {
      err = "File reader was not found:" + className;
      Logger.error(err);
      return err;
    }
  }

  
  static Object DOMResolve(Object DOMNode, Hashtable htParams) throws Exception {
    String className = null;
    Class atomSetCollectionReaderClass;
    AtomSetCollectionReader atomSetCollectionReader; 
    String atomSetCollectionReaderName = getXmlType((JSObject) DOMNode);
    if (Logger.debugging) {
      Logger.debug("The Resolver thinks " + atomSetCollectionReaderName);
    }
    htParams.put("readerName", atomSetCollectionReaderName);
    try {
      className = classBase + "xml.XmlReader";
      atomSetCollectionReaderClass = Class.forName(className);
      atomSetCollectionReader = (AtomSetCollectionReader) atomSetCollectionReaderClass.newInstance();
      return atomSetCollectionReader.readData("DOM node", htParams, DOMNode);
    } catch (Exception e) {
      String err = "File reader was not found:" + className;
      Logger.error(err, e);
      return err;
    }
  }

  
  
  private final static String classBase = "org.jmol.adapter.readers.";
  private final static String[] readerSets = new String[] {
    "cifpdb.", "Cif;Pdb;",
    "molxyz.", "Mol;Xyz;",
    "xml.", "Xml;"
  };
  
  public final static String getReaderClassBase(String type) {
    String base = (type.startsWith("Xml") ? "xml." : "more.");
    for (int i = 1; i < readerSets.length; i += 2)
      if (readerSets[i].indexOf(type + ";") >= 0) {
        base = readerSets[i - 1];
        break;
      }
    return classBase + base + type + "Reader";
  }
  
  
  private static String[] getSpartanFileList(String name, String[] dirNums) {    
    String[] files = new String[2 + dirNums.length*5];
    files[0] = "SpartanSmol";
    files[1] = "Directory Entry ";
    int pt = 2;
    name = name.replace('\\', '/');
    if (name.endsWith("/"))
      name = name.substring(0, name.length() - 1);
    for (int i = 0; i < dirNums.length; i++) {
      String path = name + (Character.isDigit(dirNums[i].charAt(0)) ? 
          "/Profile." + dirNums[i] : "/" + dirNums[i]);
      files[pt++] = path + "/#JMOL_MODEL " + dirNums[i];
      files[pt++] = path + "/input";
      files[pt++] = path + "/archive";
      files[pt++] = path + "/Molecule:asBinaryString";
      files[pt++] = path + "/proparc";
    }
    return files;
  }

  
  private static String[] getSpartanDirs(String outputFileData) {
    if (outputFileData == null)
      return new String[]{};
    if (outputFileData.startsWith("java.io.FileNotFoundException")
        || outputFileData.startsWith("FILE NOT FOUND")
        || outputFileData.indexOf("<html") >= 0)
      return new String[] { "M0001" };
    Vector v = new Vector();
    String token;
    String lasttoken = "";
    try {
      StringTokenizer tokens = new StringTokenizer(outputFileData, " \t\r\n");
      while (tokens.hasMoreTokens()) {
        
        
        if ((token = tokens.nextToken()).equals(")"))
          v.add(lasttoken);
        else if (token.equals("Start-") && tokens.nextToken().equals("Molecule"))
          v.add(TextFormat.split(tokens.nextToken(), '"')[1]);
        lasttoken = token;
      }
    } catch (Exception e) {
      
    }
    String[] dirs = new String[v.size()];
    for (int i = 0; i < v.size(); i++)
      dirs[i] = (String) v.get(i);
    return dirs;
  }
  
  private static final String CML_NAMESPACE_URI = "http://www.xml-cml.org/schema";

  private static String getXmlType(JSObject DOMNode) {
    String namespaceURI = (String) DOMNode.getMember("namespaceURI");
    String localName = (String) DOMNode.getMember("localName");
    if (namespaceURI.startsWith("http://www.molpro.net/"))
      return specialTags[SPECIAL_MOLPRO_DOM][0];
    if ("odyssey_simulation".equals(localName))
      return specialTags[SPECIAL_ODYSSEY_DOM][0];
    if ("arguslab".equals(localName))
      return specialTags[SPECIAL_ARGUS_DOM][0];
    if (namespaceURI.startsWith(CML_NAMESPACE_URI) || "cml".equals(localName))
      return specialTags[SPECIAL_CML_DOM][0];
    return "unidentified " + specialTags[SPECIAL_CML_DOM][0];
  }


  
  private static String determineAtomSetCollectionReader(BufferedReader bufferedReader, boolean returnLines)
      throws Exception {
    String[] lines = new String[16];
    LimitedLineReader llr = new LimitedLineReader(bufferedReader, 16384);
    int nLines = 0;
    for (int i = 0; i < lines.length; ++i) {
      lines[i] = llr.readLineWithNewline();
      if (lines[i].length() > 0)
        nLines++;
    }

    String readerName = checkSpecial(nLines, lines, false);
    
    if (readerName != null)
      return readerName;

    
    
    
    String leader = llr.getHeader(LEADER_CHAR_MAX);

    for (int i = 0; i < fileStartsWithRecords.length; ++i) {
      String[] recordTags = fileStartsWithRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        if (leader.startsWith(recordTag))
          return recordTags[0];
      }
    }
    for (int i = 0; i < lineStartsWithRecords.length; ++i) {
      String[] recordTags = lineStartsWithRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        for (int k = 0; k < lines.length; ++k) {
          if (lines[k].startsWith(recordTag))
            return recordTags[0];
        }
      }
    }

    if (lines[0].indexOf("PNG") == 1 || lines[0].indexOf("JPG") == 1)
      return "spt"; 
    for (int i = 0; i < lines.length; ++i)
      if (lines[i].indexOf("# Jmol state") >= 0)
        return "spt";

    
    
    
    String header = llr.getHeader(0);
    String type = null;
    for (int i = 0; i < containsRecords.length; ++i) {
      String[] recordTags = containsRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        if (header.indexOf(recordTag) != -1) {
          type = recordTags[0];
          if (type.equals("Xml")) {
            if (header.indexOf("XHTML") >= 0 || header.indexOf("xhtml") >= 0)
              break; 
            type = getXmlType(header);
          }
          return type;
        }
      }
    }
    
    readerName = checkSpecial(nLines, lines, true);
    
    if (readerName != null)
      return readerName;
    
    return (returnLines ? "\n" + lines[0] + "\n" + lines[1] + "\n" + lines[2] + "\n" : null);
  }

  private static String getXmlType(String header) throws Exception  {
    if (header.indexOf("http://www.molpro.net/") >= 0) {
      return specialTags[SPECIAL_MOLPRO_XML][0];
    }
    if (header.indexOf("odyssey") >= 0) {
      return specialTags[SPECIAL_ODYSSEY_XML][0];
    }
    if (header.indexOf("C3XML") >= 0) {
      return specialTags[SPECIAL_CHEM3D_XML][0];
    }
    if (header.indexOf("arguslab") >= 0) {
      return specialTags[SPECIAL_ARGUS_XML][0];
    }
    if (header.indexOf(CML_NAMESPACE_URI) >= 0
        || header.indexOf("cml:") >= 0) {
      return specialTags[SPECIAL_CML_XML][0];
    }
    if (header.indexOf("XSD") >= 0) {
      return specialTags[SPECIAL_XSD_XML][0];
    }
    return "unidentified " + specialTags[SPECIAL_CML_XML][0];
  }

  private final static int SPECIAL_JME                = 0;
  private final static int SPECIAL_MOPACGRAPHF        = 1;
  private final static int SPECIAL_V3000              = 2;
  private final static int SPECIAL_ODYSSEY            = 3;
  private final static int SPECIAL_MOL                = 4;
  private final static int SPECIAL_XYZ                = 5;
  private final static int SPECIAL_FOLDINGXYZ         = 6;
  private final static int SPECIAL_CUBE               = 7;
  private final static int SPECIAL_ALCHEMY            = 8;
  private final static int SPECIAL_WIEN               = 9;
  private final static int SPECIAL_CASTEP             = 10;
  private final static int SPECIAL_AIMS               = 11;
  
  
  
  public final static int SPECIAL_ARGUS_XML   = 12;
  public final static int SPECIAL_CML_XML     = 13;
  public final static int SPECIAL_CHEM3D_XML  = 14;
  public final static int SPECIAL_MOLPRO_XML  = 15;
  public final static int SPECIAL_ODYSSEY_XML = 16;
  public final static int SPECIAL_XSD_XML     = 17;
  public final static int SPECIAL_ARGUS_DOM   = 18;
  public final static int SPECIAL_CML_DOM     = 19;
  public final static int SPECIAL_CHEM3D_DOM  = 20;
  public final static int SPECIAL_MOLPRO_DOM  = 21;
  public final static int SPECIAL_ODYSSEY_DOM = 22;
  public final static int SPECIAL_XSD_DOM     = 23; 

  public final static String[][] specialTags = {
    { "Jme" },
    { "MopacGraphf" },
    { "V3000" },
    { "Odyssey" },
    { "Mol" },
    { "Xyz" },
    { "FoldingXyz" },
    { "Cube" },
    { "Alchemy" },
    { "Wien2k" },
    { "Castep" },
    { "Aims" },
    
    { "XmlArgus" }, 
    { "XmlCml" },
    { "XmlChem3d" },
    { "XmlMolpro" },
    { "XmlOdyssey" },
    { "XmlXsd" },

    { "XmlArgus(DOM)" }, 
    { "XmlCml(DOM)" },
    { "XmlChem3d(DOM)" },
    { "XmlMolpro(DOM)" },
    { "XmlOdyssey(DOM)" },
    { "XmlXsd(DOM)" },
    
    { "MdCrd" }

  };

  private final static String checkSpecial(int nLines, String[] lines, boolean isEnd) {
    
    if (isEnd) {
      if (checkGromacs(lines))
        return "Gromacs";
    }
    if (nLines == 1 && lines[0].length() > 0
        && Character.isDigit(lines[0].charAt(0)))
      return specialTags[SPECIAL_JME][0]; 
    if (checkMopacGraphf(lines))
      return specialTags[SPECIAL_MOPACGRAPHF][0]; 
    if (checkV3000(lines))
      return specialTags[SPECIAL_V3000][0];
    if (checkOdyssey(lines))
      return specialTags[SPECIAL_ODYSSEY][0];
    if (checkMol(lines))
      return specialTags[SPECIAL_MOL][0];
    if (checkXyz(lines))
      return specialTags[SPECIAL_XYZ][0];
    if (checkAlchemy(lines[0]))
      return specialTags[SPECIAL_ALCHEMY][0];
    if (checkFoldingXyz(lines))
      return specialTags[SPECIAL_FOLDINGXYZ][0];
    if (checkCube(lines))
      return specialTags[SPECIAL_CUBE][0];
    if (checkWien2k(lines))
      return specialTags[SPECIAL_WIEN][0];
    if (checkCastep(lines))
      return specialTags[SPECIAL_CASTEP][0];
    if (checkAims(lines))
      return specialTags[SPECIAL_AIMS][0];
    return null;
  }
  
  private static boolean checkGromacs(String[] lines) {
    if (Parser.parseInt(lines[1]) == Integer.MIN_VALUE)
      return false;
    int len = -1;
    for (int i = 2; i < 16 && len != 0; i++)
      if ((len = lines[i].length()) != 69 && len != 0)
        return false;
    return true;
  }

  private static boolean checkWien2k(String[] lines) {
    return (lines[2].startsWith("MODE OF CALC=") 
        || lines[2].startsWith("             RELA")
        || lines[2].startsWith("             NREL"));
  }

  private static boolean checkCastep(String[] lines) {
    for ( int i = 0; i<lines.length; i++ ) {
      if ( lines[i].toUpperCase().startsWith("%BLOCK LATTICE_ABC") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK LATTICE_CART") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK POSITIONS_FRAC") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK POSITIONS_ABS") ) return true;
    }
    return false;
  }

  private static boolean checkAims(String[] lines) {

    
    
    
    
    
    
    
    if (lines[0].startsWith("mol 1"))
      return false;  
    for (int i = 0; i < lines.length; i++) {
      String[] tokens = Parser.getTokens(lines[i]);
      if (tokens.length == 0)
        continue;
      if (tokens[0].startsWith("atom") && tokens.length >= 5
          || tokens[0].startsWith("multipole") && tokens.length >= 6
          || tokens[0].startsWith("lattice_vector") && tokens.length >= 4)
        return true;
    }
    return false;
  }


  private final static String getReaderFromType(String type) {
    type = type.toLowerCase();
    String base = null;
    if ((base = checkType(specialTags, type)) != null)
      return base;
    if ((base = checkType(fileStartsWithRecords, type)) != null)
      return base;
    if ((base = checkType(lineStartsWithRecords, type)) != null)
      return base;
    return checkType(containsRecords, type);
  }
  
  private final static String checkType(String[][] typeTags, String type) {
    for (int i = 0; i < typeTags.length; ++i)
      if (typeTags[i][0].toLowerCase().equals(type))
        return typeTags[i][0];
    return null;
  }
  
  
  
  

  private static boolean checkOdyssey(String[] lines) {
    int i;
    for (i = 0; i < lines.length; i++)
      if (!lines[i].startsWith("C ") && lines[i].length() != 0)
        break;
    if (i >= lines.length 
        || lines[i].charAt(0) != ' ' 
        || (i = i + 2) + 1 >= lines.length)
      return false;
    try {
      
      
      
      
      int spin = Integer.parseInt(lines[i].substring(2).trim());
      int charge = Integer.parseInt(lines[i].substring(0, 2).trim());
      
      int atom1 = Integer.parseInt(lines[++i].substring(0, 2).trim());
      if (spin < 0 || spin > 5 || atom1 <= 0 || charge > 5)
        return false;
      
      float[] atomline = new float[5];
      AtomSetCollectionReader.getTokensFloat(lines[i], atomline, 5);
      return !Float.isNaN(atomline[1]) && !Float.isNaN(atomline[2]) && !Float.isNaN(atomline[3]) && Float.isNaN(atomline[4]);
    } catch (Exception e) {
    }
    return false;
  }
  
  private static boolean checkV3000(String[] lines) {
    if (lines[3].length() >= 6) {
      String line4trimmed = lines[3].trim();
      if (line4trimmed.endsWith("V3000"))
        return true;
    }
    return false;
  }

  private static boolean checkMol(String[] lines) {
    if (lines[3].length() >= 6) {
      String line4trimmed = lines[3].trim();
      if (line4trimmed.endsWith("V2000") ||
          line4trimmed.endsWith("v2000"))
        return true;
      try {
        Integer.parseInt(lines[3].substring(0, 3).trim());
        Integer.parseInt(lines[3].substring(3, 6).trim());
        return (lines[0].indexOf("@<TRIPOS>") != 0 
            && lines[1].indexOf("@<TRIPOS>") != 0
            && lines[2].indexOf("@<TRIPOS>") != 0
            );
      } catch (NumberFormatException nfe) {
      }
    }
    return false;
  }

  private static boolean checkAlchemy(String line) {
    
    int pt;
    if ((pt = line.indexOf("ATOMS")) >= 0 && line.indexOf("BONDS") > pt)
      try {
        int n = Integer.parseInt(line.substring(0, pt).trim());
        return (n > 0);
      } catch (NumberFormatException nfe) {
        
      }
    return false;
  }

  private static boolean checkXyz(String[] lines) {
    try {
      Integer.parseInt(lines[0].trim());
      return true;
    } catch (NumberFormatException nfe) {
    }
    return false;
  }

  
  private static boolean checkFoldingXyz(String[] lines) {
    
    StringTokenizer tokens = new StringTokenizer(lines[0].trim(), " \t");
    if (tokens.countTokens() < 2)
      return false;
    try {
      Integer.parseInt(tokens.nextToken().trim());
    } catch (NumberFormatException nfe) {
      return false;
    }
    
    
    String secondLine = lines[1].trim();
    if (secondLine.length() == 0)
        secondLine = lines[2].trim();
    tokens = new StringTokenizer(secondLine, " \t");
    if (tokens.countTokens() == 0)
      return false;
    try {
      Integer.parseInt(tokens.nextToken().trim());
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }
  
  
  
  private static boolean checkMopacGraphf(String[] lines) {
    return (lines[0].indexOf("MOPAC-Graphical data") > 2); 
  }

  private static boolean checkCube(String[] lines) {
    try {
      StringTokenizer tokens2 = new StringTokenizer(lines[2]);
      if (tokens2 == null || tokens2.countTokens() != 4)
        return false;
      Integer.parseInt(tokens2.nextToken());
      for (int i = 3; --i >= 0; )
        new Float(tokens2.nextToken());
      StringTokenizer tokens3 = new StringTokenizer(lines[3]);
      if (tokens3 == null || tokens3.countTokens() != 4)
        return false;
      Integer.parseInt(tokens3.nextToken());
      for (int i = 3; --i >= 0; )
        if ((new Float(tokens3.nextToken())).floatValue() < 0)
          return false;
      return true;
    } catch (NumberFormatException nfe) {
    }
    return false;
  }

  
  
  
  

  private final static int LEADER_CHAR_MAX = 20;
  
  private final static String[] cubeFileStartRecords =
  {"Cube", "JVXL", "#JVXL"};

  private final static String[] mol2Records =
  {"Mol2", "mol2", "@<TRIPOS>"};

  private final static String[] webmoFileStartRecords =
  {"WebMO", "[HEADER]"};
  
  private final static String[] moldenFileStartRecords =
  {"Molden", "[Molden"};

  private final static String[] vaspOutcarStartRecords =
  {"Vasp", " vasp."};

  private final static String[] hinFileStartRecords = 
  { "Hin", "mol 1" };

  private final static String[][] fileStartsWithRecords =
  { cubeFileStartRecords, mol2Records, webmoFileStartRecords, moldenFileStartRecords, vaspOutcarStartRecords, hinFileStartRecords};

  
  
  

  private final static String[] pqrLineStartRecords = 
  { "Pqr", "REMARK   1 PQR" };

  private final static String[] pdbLineStartRecords = {
    "Pdb", "HEADER", "OBSLTE", "TITLE ", "CAVEAT", "COMPND", "SOURCE", "KEYWDS",
    "EXPDTA", "AUTHOR", "REVDAT", "SPRSDE", "JRNL  ", "REMARK",
    "DBREF ", "SEQADV", "SEQRES", "MODRES", 
    "HELIX ", "SHEET ", "TURN  ",
    "CRYST1", "ORIGX1", "ORIGX2", "ORIGX3", "SCALE1", "SCALE2", "SCALE3",
    "ATOM  ", "HETATM", "MODEL ",
  };

  private final static String[] shelxLineStartRecords =
  { "Shelx", "TITL ", "ZERR ", "LATT ", "SYMM ", "CELL " };

  private final static String[] cifLineStartRecords =
  { "Cif", "data_", "_publ" };

  private final static String[] ghemicalMMLineStartRecords =
  { "GhemicalMM", "!Header mm1gp", "!Header gpr" };

  private final static String[] jaguarLineStartRecords =
  { "Jaguar", "  |  Jaguar version", };

  private final static String[] mdlLineStartRecords = 
  { "Mol", "$MDL " };

  private final static String[] spartanSmolLineStartRecords =
  { "SpartanSmol", "INPUT=" };

  private final static String[] csfLineStartRecords =
  { "Csf", "local_transform" };
  
  private final static String[] mdTopLineStartRecords =
  { "MdTop", "%FLAG TITLE" };
  
  private final static String[][] lineStartsWithRecords =
  { cifLineStartRecords, pqrLineStartRecords, pdbLineStartRecords, shelxLineStartRecords, 
    ghemicalMMLineStartRecords, jaguarLineStartRecords, 
    mdlLineStartRecords, spartanSmolLineStartRecords, csfLineStartRecords, 
    mol2Records, mdTopLineStartRecords };

  
  
  

  private final static String[] xmlContainsRecords = 
  { "Xml", "<?xml", "<atom", "<molecule", "<reaction", "<cml", "<bond", ".dtd\"",
    "<list>", "<entry", "<identifier", "http://www.xml-cml.org/schema/cml2/core" };

  private final static String[] gaussianContainsRecords =
  { "Gaussian", "Entering Gaussian System", "Entering Link 1", "1998 Gaussian, Inc." };

  

  private final static String[] ampacContainsRecords =
  { "Ampac", "AMPAC Version" };
  
  private final static String[] mopacContainsRecords =
  { "Mopac", "MOPAC 93 (c) Fujitsu", "MOPAC2002 (c) Fujitsu",
    "MOPAC FOR LINUX (PUBLIC DOMAIN VERSION)" };

  private final static String[] qchemContainsRecords = 
  { "Qchem", "Welcome to Q-Chem", "A Quantum Leap Into The Future Of Chemistry" };

  private final static String[] gamessUKContainsRecords =
  { "GamessUK", "GAMESS-UK", "G A M E S S - U K" };

  private final static String[] gamessUSContainsRecords =
  { "GamessUS", "GAMESS" };

  private final static String[] spartanBinaryContainsRecords =
  { "SpartanSmol" , "|PropertyArchive", "_spartan", "spardir" };

  private final static String[] spartanContainsRecords =
  { "Spartan", "Spartan" };  

  private final static String[] adfContainsRecords =
  { "Adf", "Amsterdam Density Functional" };
  
  private final static String[] dgridContainsRecords =
  { "Dgrid", "BASISFILE   created by DGrid" };
  
  private final static String[] psiContainsRecords =
  { "Psi", "    PSI  3"};
 
  private final static String[] nwchemContainsRecords =
  { "NWChem", " argument  1 = "};

  private final static String[] uicrcifContainsRecords =
  { "Cif", "Crystallographic Information File"};
  
  private final static String[][] containsRecords =
  { xmlContainsRecords, gaussianContainsRecords, 
    ampacContainsRecords, mopacContainsRecords, qchemContainsRecords, 
    gamessUKContainsRecords, gamessUSContainsRecords,
    spartanBinaryContainsRecords, spartanContainsRecords, mol2Records, adfContainsRecords, psiContainsRecords,
    nwchemContainsRecords, uicrcifContainsRecords, dgridContainsRecords,
  };
}

class LimitedLineReader {
  private char[] buf;
  private int cchBuf;
  private int ichCurrent;

  LimitedLineReader(BufferedReader bufferedReader, int readLimit)
    throws Exception {
    bufferedReader.mark(readLimit);
    buf = new char[readLimit];
    cchBuf = Math.max(bufferedReader.read(buf), 0);
    ichCurrent = 0;
    bufferedReader.reset();
  }

  String getHeader(int n) {
    return (n == 0 ? new String(buf) : new String(buf, 0, Math.min(cchBuf, n)));
  }
  
  String readLineWithNewline() {
    
    
    
    
    
    
    while (ichCurrent < cchBuf) {
      int ichBeginningOfLine = ichCurrent;
      char ch = 0;
      while (ichCurrent < cchBuf &&
             (ch = buf[ichCurrent++]) != '\r' && ch != '\n') {
      }
      if (ch == '\r' && ichCurrent < cchBuf && buf[ichCurrent] == '\n')
        ++ichCurrent;
      int cchLine = ichCurrent - ichBeginningOfLine;
      if (buf[ichBeginningOfLine] == '#') {
        if (buf.length < ichBeginningOfLine + 6 || 
            buf[ichBeginningOfLine + 1] != ' '
            || buf[ichBeginningOfLine + 2] != 'J'
            || buf[ichBeginningOfLine + 3] != 'm'
            || buf[ichBeginningOfLine + 4] != 'o'
            || buf[ichBeginningOfLine + 5] != 'l')
        continue;
      }
      StringBuffer sb = new StringBuffer(cchLine);
      sb.append(buf, ichBeginningOfLine, cchLine);
      return sb.toString();
    }
    
    
    
    
    
    
    
    
    
    
    return "";
  }
}
