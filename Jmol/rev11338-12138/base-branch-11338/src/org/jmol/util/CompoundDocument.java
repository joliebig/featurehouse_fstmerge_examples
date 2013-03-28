
package org.jmol.util;


import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;

import java.util.Hashtable;
import java.util.Vector;



public class CompoundDocument extends BinaryDocument {


  CmpDocHeader header = new CmpDocHeader();
  Vector directory = new Vector(); 
  CmpDocDirectoryEntry rootEntry;

  int[] SAT;
  int[] SSAT;
  int sectorSize;
  int shortSectorSize;
  int nShortSectorsPerStandardSector;
  int nIntPerSector;
  int nDirEntriesperSector;

  public CompoundDocument(BufferedInputStream bis) {

    
    if (!isRandom) {
      stream = new DataInputStream(bis);
    }
    stream.mark(Integer.MAX_VALUE);
    if (!readHeader())
      return;
    getSectorAllocationTable();
    getShortSectorAllocationTable();
    getDirectoryTable();
  }

  public static boolean isCompoundDocument(InputStream is) throws Exception {
    byte[] abMagic = new byte[8];
    is.mark(9);
    int countRead = is.read(abMagic, 0, 8);
    is.reset();
    return (countRead == 8 && abMagic[0] == (byte) 0xD0
        && abMagic[1] == (byte) 0xCF && abMagic[2] == (byte) 0x11
        && abMagic[3] == (byte) 0xE0 && abMagic[4] == (byte) 0xA1
        && abMagic[5] == (byte) 0xB1 && abMagic[6] == (byte) 0x1A 
        && abMagic[7] == (byte) 0xE1);
  }
  
  public static boolean isCompoundDocument(byte[] bytes) {
    return (bytes.length >= 8 && bytes[0] == (byte) 0xD0
        && bytes[1] == (byte) 0xCF && bytes[2] == (byte) 0x11
        && bytes[3] == (byte) 0xE0 && bytes[4] == (byte) 0xA1
        && bytes[5] == (byte) 0xB1 && bytes[6] == (byte) 0x1A 
        && bytes[7] == (byte) 0xE1);
  }
  

  public Vector getDirectory() {
    return directory;
  }

  public String getDirectoryListing(String separator) {
    String str = "";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = (CmpDocDirectoryEntry) directory.get(i);
      if (!thisEntry.isEmpty)
        str += separator
            + thisEntry.entryName
            + "\tlen="
            + thisEntry.lenStream
            + "\tSID="
            + thisEntry.SIDfirstSector
            + (thisEntry.isStandard ? "\tfileOffset="
                + getOffset(thisEntry.SIDfirstSector) : "");
    }
    return str;
  }

  StringBuffer data;
  
  public StringBuffer getAllData() {
    return getAllData(null);
  }

  
  public void getAllData(String prefix, 
                         String binaryFileList, Hashtable fileData) {
    fileData.put("#Directory_Listing", getDirectoryListing("|"));
    binaryFileList = "|" + binaryFileList + "|";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = (CmpDocDirectoryEntry) directory.get(i);
      String name = thisEntry.entryName;
      Logger.info("reading " + name);
      if (!thisEntry.isEmpty && thisEntry.entryType != 5) {
        boolean isBinary = (binaryFileList != null && binaryFileList.indexOf("|" + thisEntry.entryName + "|") >= 0);
        if (isBinary)
          name += ":asBinaryString";
        StringBuffer data = new StringBuffer();
        data.append("BEGIN Directory Entry ").append(name).append("\n"); 
        data.append(getFileAsString(thisEntry, isBinary));
        data.append("\nEND Directory Entry ").append(name).append("\n");
        fileData.put(prefix + "/" + name, data.toString());
      }
    }
    close();
  }

  public StringBuffer getAllData(String binaryFileList) {
    data = new StringBuffer();
    data.append("Compound Document File Directory: ");
    data.append(getDirectoryListing("|"));
    data.append("\n");
    binaryFileList = "|" + binaryFileList + "|";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = (CmpDocDirectoryEntry) directory.get(i);
      Logger.info("reading " + thisEntry.entryName);
      if (!thisEntry.isEmpty && thisEntry.entryType != 5) {
        data.append("BEGIN Directory Entry ").append(thisEntry.entryName).append("\n");            
        data.append(getFileAsString(thisEntry, binaryFileList != null && binaryFileList.indexOf("|" + thisEntry.entryName + "|") >= 0));
        data.append("\n");
        data.append("END Directory Entry ").append(thisEntry.entryName).append("\n");            
      }
    }
    close();
    return data;
  }

  public StringBuffer getFileAsString(String entryName) {
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = (CmpDocDirectoryEntry) directory.get(i);
      if (thisEntry.entryName.equals(entryName))
        return getFileAsString(thisEntry, false);
    }
    return new StringBuffer();
  }

  class CmpDocHeader {

    
    
    byte[] magicNumbers = new byte[8]; 
    byte[] uniqueID = new byte[16];
    byte revNumber; 
    
    byte verNumber; 
    
    
    short sectorPower; 
    short shortSectorPower; 
    byte[] unused = new byte[10];
    int nSATsectors; 
    int SID_DIR_start; 
    byte[] unused2 = new byte[4];
    
    int minBytesStandardStream; 
    int SID_SSAT_start; 
    
    int SID_MSAT_next; 
    int nAdditionalMATsectors; 
    
    int[] MSAT0 = new int[109]; 

    

    final boolean readData() {
      try {
        readByteArray(magicNumbers, 0, 8);
        if (magicNumbers[0] != (byte) 0xD0 || magicNumbers[1] != (byte) 0xCF
            || magicNumbers[2] != (byte) 0x11 || magicNumbers[3] != (byte) 0xE0
            || magicNumbers[4] != (byte) 0xA1 || magicNumbers[5] != (byte) 0xB1
            || magicNumbers[6] != (byte) 0x1A || magicNumbers[7] != (byte) 0xE1)
          return false;
        readByteArray(uniqueID);
        revNumber = readByte();
        readByte();
        verNumber = readByte();
        readByte();
        byte b1 = readByte();
        byte b2 = readByte();
        isBigEndian = (b1 == -1 && b2 == -2);
        sectorPower = readShort();
        shortSectorPower = readShort();
        readByteArray(unused);
        nSATsectors = readInt();
        SID_DIR_start = readInt();
        readByteArray(unused2);
        minBytesStandardStream = readInt();
        SID_SSAT_start = readInt();
        readInt();
        SID_MSAT_next = readInt();
        nAdditionalMATsectors = readInt();
        for (int i = 0; i < 109; i++)
          MSAT0[i] = readInt();
      } catch (Exception e) {
        Logger.error(null, e);
        return false;
      }
      return true;
    }
  }

  class CmpDocDirectoryEntry {
    
    
    byte[] unicodeName = new byte[64];
    short nBytesUnicodeName; 
    byte entryType; 
    
    
    
    
    byte[] uniqueID = new byte[16];
    byte[] userflags = new byte[4];
    
    
    
    int SIDfirstSector; 
    int lenStream;
    byte[] unused = new byte[4]; 

    

    String entryName;
    boolean isStandard;
    boolean isEmpty;

    final boolean readData() {
      try {
        readByteArray(unicodeName);
        nBytesUnicodeName = readShort();
        entryType = readByte();
        readByte();
        readInt();
        readInt();
        readInt();
        readByteArray(uniqueID);
        readByteArray(userflags);
        readLong();
        readLong();
        
        SIDfirstSector = readInt();
        lenStream = readInt();
        readByteArray(unused);
      } catch (Exception e) {
        Logger.error(null, e);
        return false;
      }
      entryName = "";
      for (int i = 0; i < nBytesUnicodeName - 2; i += 2)
        entryName += (char) unicodeName[i];
      isStandard = (entryType == 5 || lenStream >= header.minBytesStandardStream);
      isEmpty = (entryType == 0 || lenStream <= 0);
      
      return true;
    }
  }

  private long getOffset(int SID) {
    return (SID + 1) * sectorSize;
  }

  private void gotoSector(int SID) {
    seek(getOffset(SID));
  }

  private boolean readHeader() {
    if (!header.readData())
      return false;
    sectorSize = 1 << header.sectorPower;
    shortSectorSize = 1 << header.shortSectorPower;
    nShortSectorsPerStandardSector = sectorSize / shortSectorSize; 
    nIntPerSector = sectorSize / 4; 
    nDirEntriesperSector = sectorSize / 128; 
    if (Logger.debugging) {
      Logger.debug(
          "compound document: revNum=" + header.revNumber +
          " verNum=" + header.verNumber + " isBigEndian=" + isBigEndian +
          " bytes per standard/short sector=" + sectorSize + "/" + shortSectorSize);
    }
    return true;
  }

  private void getSectorAllocationTable() {
    int nSID = 0;
    int thisSID;
    SAT = new int[header.nSATsectors * nIntPerSector + 109];

    try {
      for (int i = 0; i < 109; i++) {
        thisSID = header.MSAT0[i];
        if (thisSID < 0)
          break;
        gotoSector(thisSID);
        for (int j = 0; j < nIntPerSector; j++) {
          SAT[nSID++] = readInt();
          
        }
      }
      int nMaster = header.nAdditionalMATsectors;
      thisSID = header.SID_MSAT_next;
      int[] MSAT = new int[nIntPerSector];
      out: while (nMaster-- > 0 && thisSID >= 0) {
        
        gotoSector(thisSID);
        for (int i = 0; i < nIntPerSector; i++)
          MSAT[i] = readInt();
        
        
        for (int i = 0; i < nIntPerSector - 1; i++) {
          thisSID = MSAT[i];
          if (thisSID < 0)
            break out;
          gotoSector(thisSID);
          for (int j = nIntPerSector; --j >= 0;)
            SAT[nSID++] = readInt();
        }
        thisSID = MSAT[nIntPerSector - 1];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  private void getShortSectorAllocationTable() {
    int nSSID = 0;
    int thisSID = header.SID_SSAT_start;
    
    
    
    int nMax = header.nSATsectors * nIntPerSector * 2;
    SSAT = new int[nMax];
    try {
      while (thisSID > 0 && nSSID < nMax) {
        gotoSector(thisSID);
        for (int j = 0; j < nIntPerSector; j++) {
          SSAT[nSSID++] = readInt();
          
        }
        thisSID = SAT[thisSID];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  private void getDirectoryTable() {
    int thisSID = header.SID_DIR_start;
    CmpDocDirectoryEntry thisEntry;
    rootEntry = null;
    try {
      while (thisSID > 0) {
        gotoSector(thisSID);
        for (int j = nDirEntriesperSector; --j >= 0;) {
          thisEntry = new CmpDocDirectoryEntry();
          thisEntry.readData();
          if (thisEntry.lenStream > 0) {
            directory.addElement(thisEntry);
            
          }
          if (thisEntry.entryType == 5)
            rootEntry = thisEntry;
        }
        thisSID = SAT[thisSID];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
    
      
  }

  private StringBuffer getFileAsString(CmpDocDirectoryEntry thisEntry, boolean asBinaryString) {
    if(thisEntry.isEmpty)
      return new StringBuffer();
    
    return (thisEntry.isStandard ? getStandardStringData(
            thisEntry.SIDfirstSector, thisEntry.lenStream, asBinaryString)
            : getShortStringData(thisEntry.SIDfirstSector, thisEntry.lenStream, asBinaryString));
  }

  private StringBuffer getStandardStringData(int thisSID, int nBytes,
                                             boolean asBinaryString) {
    StringBuffer data = new StringBuffer();
    byte[] byteBuf = new byte[sectorSize];
    try {
      while (thisSID > 0 && nBytes > 0) {
        gotoSector(thisSID);
        nBytes = getSectorData(data, byteBuf, sectorSize, nBytes, asBinaryString);
        thisSID = SAT[thisSID];
      }
      if (nBytes == -9999)
        return new StringBuffer();
    } catch (Exception e) {
      Logger.error(null, e);
    }
    return data;
  }

  private int getSectorData(StringBuffer data, byte[] byteBuf,
                            int nSectorBytes, int nBytes, boolean asBinaryString)
      throws Exception {
    readByteArray(byteBuf);
    if (asBinaryString) {
      for (int i = 0; i < nSectorBytes; i++) {
        data.append(Integer.toHexString(((int)byteBuf[i]) & 0xFF)).append(' ');
        if (--nBytes < 1)
          break;
      }
    } else {
      for (int i = 0; i < nSectorBytes; i++) {
        if (byteBuf[i] == 0)
          return -9999; 
        data.append((char) byteBuf[i]);
        if (--nBytes < 1)
          break;
      }
    }
    return nBytes;
  }
  private StringBuffer getShortStringData(int shortSID, int nBytes, boolean asBinaryString) {
    StringBuffer data = new StringBuffer();
    byte[] byteBuf = new byte[shortSectorSize];
    int ptShort = 0;
    int thisSID;
    if (rootEntry == null)
      return data;
    try {
      thisSID = rootEntry.SIDfirstSector;
      
      while (thisSID >= 0 && shortSID >= 0 && nBytes > 0) {
        while (shortSID - ptShort >= nShortSectorsPerStandardSector) {
          ptShort += nShortSectorsPerStandardSector;
          thisSID = SAT[thisSID];
        }
        seek(getOffset(thisSID) + (shortSID - ptShort) * shortSectorSize);
        nBytes = getSectorData(data, byteBuf, shortSectorSize, nBytes, asBinaryString);
        shortSID = SSAT[shortSID];
      }
    } catch (Exception e) {
      Logger.error(data.toString());
      Logger.error(null, e);
    }
    
    return data;
  }
  
  
}
