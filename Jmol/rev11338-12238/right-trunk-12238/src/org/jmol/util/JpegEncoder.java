
















package org.jmol.util;

import java.awt.*;
import java.awt.image.*;
import java.io.*;



public class JpegEncoder extends Frame
{
  
  private BufferedOutputStream outStream;
  
  private JpegInfo JpegObj;
  private Huffman Huf;
  private DCT dct;
  private int Quality;
  

  public JpegEncoder(Image image, int quality, OutputStream out, String comment)
  {
    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(image, 0);
    try {
      tracker.waitForID(0);
    }
    catch (InterruptedException e) {
      
    }
    
    Quality=quality;
    
    
    JpegObj = new JpegInfo(image, comment);
    
    outStream = new BufferedOutputStream(out);
    dct = new DCT(Quality);
    Huf=new Huffman(JpegObj.imageWidth,JpegObj.imageHeight);
  }
  
  public static byte[] getBytes(Image image, int quality, String comment) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    write(image, quality, os, comment);
    try {
      os.flush();
      os.close();
    } catch (IOException e) {
      
    }
    return os.toByteArray();
  }

  public static void write(Image image, int quality, OutputStream os, String comment) {
    (new JpegEncoder(image, quality, os, comment)).Compress();
  }

  public void setQuality(int quality) {
    dct = new DCT(quality);
  }
  
  public void Compress() {
    WriteHeaders(outStream, JpegObj, dct);
    WriteCompressedData(outStream, JpegObj, dct, Huf);
    WriteEOI(outStream);
    try {
      outStream.flush();
    } catch (IOException e) {
      Logger.error("IO Error", e);
    }
  }
  
  static private void WriteCompressedData(BufferedOutputStream outStream,
                                          JpegInfo JpegObj, DCT dct,
                                          Huffman Huf) {
    int i, j, r, c, a, b;
    int comp, xpos, ypos, xblockoffset, yblockoffset;
    float inputArray[][];
    float dctArray1[][] = new float[8][8];
    double dctArray2[][] = new double[8][8];
    int dctArray3[] = new int[8 * 8];

    

    int lastDCvalue[] = new int[JpegObj.NumberOfComponents];
    
    
    
    int MinBlockWidth, MinBlockHeight;
    
    
    MinBlockWidth = ((Huf.ImageWidth % 8 != 0) ? (int) (Math
        .floor(Huf.ImageWidth / 8.0) + 1) * 8 : Huf.ImageWidth);
    MinBlockHeight = ((Huf.ImageHeight % 8 != 0) ? (int) (Math
        .floor(Huf.ImageHeight / 8.0) + 1) * 8 : Huf.ImageHeight);
    for (comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
      MinBlockWidth = Math.min(MinBlockWidth, JpegObj.BlockWidth[comp]);
      MinBlockHeight = Math.min(MinBlockHeight, JpegObj.BlockHeight[comp]);
    }
    xpos = 0;
    for (r = 0; r < MinBlockHeight; r++) {
      for (c = 0; c < MinBlockWidth; c++) {
        xpos = c * 8;
        ypos = r * 8;
        for (comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
          
          
          inputArray = (float[][]) JpegObj.Components[comp];
          int VsampF = JpegObj.VsampFactor[comp];
          int HsampF = JpegObj.HsampFactor[comp];
          int QNumber = JpegObj.QtableNumber[comp];
          int DCNumber = JpegObj.DCtableNumber[comp];
          int ACNumber = JpegObj.ACtableNumber[comp];

          for (i = 0; i < VsampF; i++) {
            for (j = 0; j < HsampF; j++) {
              xblockoffset = j * 8;
              yblockoffset = i * 8;
              for (a = 0; a < 8; a++) {
                for (b = 0; b < 8; b++) {

                  
                  
                  
                  
                  

                  dctArray1[a][b] = inputArray[ypos + yblockoffset + a][xpos
                      + xblockoffset + b];
                }
              }
              
              
              
              
              dctArray2 = DCT.forwardDCT(dctArray1);
              dctArray3 = DCT.quantizeBlock(dctArray2, dct.divisors[QNumber]);
              
              
              
              
              
              
              Huf.HuffmanBlockEncoder(outStream, dctArray3, lastDCvalue[comp],
                  DCNumber, ACNumber);
              lastDCvalue[comp] = dctArray3[0];
            }
          }
        }
      }
    }
    Huf.flushBuffer(outStream);
  }
  
  static private void WriteEOI(BufferedOutputStream out) {
    byte[] EOI = {(byte) 0xFF, (byte) 0xD9};
    WriteMarker(EOI, out);
  }
  
  static private void WriteHeaders(BufferedOutputStream out, JpegInfo JpegObj, DCT dct) {
    int i, j, index, offset, length;
    int tempArray[];
    
    
    byte[] SOI = {(byte) 0xFF, (byte) 0xD8};
    WriteMarker(SOI, out);
    
    
    
    byte JFIF[] = new byte[18];
    JFIF[0] = (byte) 0xff;
    JFIF[1] = (byte) 0xe0;
    JFIF[2] = (byte) 0x00;
    JFIF[3] = (byte) 0x10;
    JFIF[4] = (byte) 0x4a;
    JFIF[5] = (byte) 0x46;
    JFIF[6] = (byte) 0x49;
    JFIF[7] = (byte) 0x46;
    JFIF[8] = (byte) 0x00;
    JFIF[9] = (byte) 0x01;
    JFIF[10] = (byte) 0x00;
    JFIF[11] = (byte) 0x00;
    JFIF[12] = (byte) 0x00;
    JFIF[13] = (byte) 0x01;
    JFIF[14] = (byte) 0x00;
    JFIF[15] = (byte) 0x01;
    JFIF[16] = (byte) 0x00;
    JFIF[17] = (byte) 0x00;
    WriteArray(JFIF, out);
    
    
    String comment = "";
    comment = JpegObj.getComment();
    length = comment.length();
    byte COM[] = new byte[length + 4];
    COM[0] = (byte) 0xFF;
    COM[1] = (byte) 0xFE;
    COM[2] = (byte) ((length >> 8) & 0xFF);
    COM[3] = (byte) (length & 0xFF);
    java.lang.System.arraycopy(JpegObj.Comment.getBytes(), 0, 
        COM, 4, JpegObj.Comment.length());
    WriteArray(COM, out);
    
    
    
    byte DQT[] = new byte[134];
    DQT[0] = (byte) 0xFF;
    DQT[1] = (byte) 0xDB;
    DQT[2] = (byte) 0x00;
    DQT[3] = (byte) 0x84;
    offset = 4;
    for (i = 0; i < 2; i++) {
      DQT[offset++] = (byte) ((0 << 4) + i);
      tempArray = dct.quantum[i];
      for (j = 0; j < 64; j++) {
        DQT[offset++] = (byte) tempArray[Huffman.jpegNaturalOrder[j]];
      }
    }
    WriteArray(DQT, out);
    
    
    byte SOF[] = new byte[19];
    SOF[0] = (byte) 0xFF;
    SOF[1] = (byte) 0xC0;
    SOF[2] = (byte) 0x00;
    SOF[3] = (byte) 17;
    SOF[4] = (byte) JpegObj.Precision;
    SOF[5] = (byte) ((JpegObj.imageHeight >> 8) & 0xFF);
    SOF[6] = (byte) ((JpegObj.imageHeight) & 0xFF);
    SOF[7] = (byte) ((JpegObj.imageWidth >> 8) & 0xFF);
    SOF[8] = (byte) ((JpegObj.imageWidth) & 0xFF);
    SOF[9] = (byte) JpegObj.NumberOfComponents;
    index = 10;
    for (i = 0; i < SOF[9]; i++) {
      SOF[index++] = (byte) JpegObj.CompID[i];
      SOF[index++] = (byte) ((JpegObj.HsampFactor[i] << 4) + JpegObj.VsampFactor[i]);
      SOF[index++] = (byte) JpegObj.QtableNumber[i];
    }
    WriteArray(SOF, out);
    
    WriteDHTHeader(Huffman.bitsDCluminance, Huffman.valDCluminance, out);
    WriteDHTHeader(Huffman.bitsACluminance, Huffman.valACluminance, out);
    WriteDHTHeader(Huffman.bitsDCchrominance, Huffman.valDCchrominance, out);
    WriteDHTHeader(Huffman.bitsACchrominance, Huffman.valACchrominance, out);
    
    
    byte SOS[] = new byte[14];
    SOS[0] = (byte) 0xFF;
    SOS[1] = (byte) 0xDA;
    SOS[2] = (byte) 0x00;
    SOS[3] = (byte) 12;
    SOS[4] = (byte) JpegObj.NumberOfComponents;
    index = 5;
    for (i = 0; i < SOS[4]; i++) {
      SOS[index++] = (byte) JpegObj.CompID[i];
      SOS[index++] = (byte) ((JpegObj.DCtableNumber[i] << 4) + 
          JpegObj.ACtableNumber[i]);
    }
    SOS[index++] = (byte) JpegObj.Ss;
    SOS[index++] = (byte) JpegObj.Se;
    SOS[index++] = (byte) ((JpegObj.Ah << 4) + JpegObj.Al);
    WriteArray(SOS, out);
    
  }

  static void WriteDHTHeader(int[] bits, int[] val, BufferedOutputStream out) {
    byte DHT1[], DHT2[], DHT3[], DHT4[];
    int bytes, temp, oldindex, intermediateindex;
    int index = 4;
    oldindex = 4;
    DHT1 = new byte[17];
    DHT4 = new byte[4];
    DHT4[0] = (byte) 0xFF;
    DHT4[1] = (byte) 0xC4;
    bytes = 0;
    DHT1[index++ - oldindex] = (byte) bits[0];
    for (int j = 1; j < 17; j++) {
      temp = bits[j];
      DHT1[index++ - oldindex] = (byte) temp;
      bytes += temp;
    }
    intermediateindex = index;
    DHT2 = new byte[bytes];
    for (int j = 0; j < bytes; j++) {
      DHT2[index++ - intermediateindex] = (byte) val[j];
    }
    DHT3 = new byte[index];
    java.lang.System.arraycopy(DHT4, 0, DHT3, 0, oldindex);
    java.lang.System.arraycopy(DHT1, 0, DHT3, oldindex, 17);
    java.lang.System.arraycopy(DHT2, 0, DHT3, oldindex + 17, bytes);
    DHT4 = DHT3;
    oldindex = index;
    DHT4[2] = (byte) (((index - 2) >> 8) & 0xFF);
    DHT4[3] = (byte) ((index - 2) & 0xFF);
    WriteArray(DHT4, out);
  }
  
  static void WriteMarker(byte[] data, BufferedOutputStream out) {
    try {
      out.write(data, 0, 2);
    } catch (IOException e) {
      Logger.error("IO Error", e);
    }
  }
  
  static void WriteArray(byte[] data, BufferedOutputStream out) {
    int length;
    try {
      length = ((data[2] & 0xFF) << 8) + (data[3] & 0xFF) + 2;
      out.write(data, 0, length);
    } catch (IOException e) {
      Logger.error("IO Error", e);
    }
  }
}






class DCT
{

  
  private final static int N = 8;
  private final static int NN = N * N;
  
  
  
  
  int[][] quantum = new int[2][];
  double[][] divisors = new double[2][];
  
  
  private int quantum_luminance[]     = new int[NN];
  private double DivisorsLuminance[] = new double[NN];
  
  
  private int quantum_chrominance[]     = new int[NN];
  private double DivisorsChrominance[] = new double[NN];
  
  
  DCT(int quality)
  {
    initMatrix(quality);
  }
  
  
  
  private void initMatrix(int quality) {
    
    
    
    quality = (quality < 1 ? 1 : quality > 100 ? 100 : quality);
    quality = (quality < 50 ? 5000 / quality : 200 - quality * 2);
    
    
    
    quantum_luminance[0]=16;
    quantum_luminance[1]=11;
    quantum_luminance[2]=10;
    quantum_luminance[3]=16;
    quantum_luminance[4]=24;
    quantum_luminance[5]=40;
    quantum_luminance[6]=51;
    quantum_luminance[7]=61;
    quantum_luminance[8]=12;
    quantum_luminance[9]=12;
    quantum_luminance[10]=14;
    quantum_luminance[11]=19;
    quantum_luminance[12]=26;
    quantum_luminance[13]=58;
    quantum_luminance[14]=60;
    quantum_luminance[15]=55;
    quantum_luminance[16]=14;
    quantum_luminance[17]=13;
    quantum_luminance[18]=16;
    quantum_luminance[19]=24;
    quantum_luminance[20]=40;
    quantum_luminance[21]=57;
    quantum_luminance[22]=69;
    quantum_luminance[23]=56;
    quantum_luminance[24]=14;
    quantum_luminance[25]=17;
    quantum_luminance[26]=22;
    quantum_luminance[27]=29;
    quantum_luminance[28]=51;
    quantum_luminance[29]=87;
    quantum_luminance[30]=80;
    quantum_luminance[31]=62;
    quantum_luminance[32]=18;
    quantum_luminance[33]=22;
    quantum_luminance[34]=37;
    quantum_luminance[35]=56;
    quantum_luminance[36]=68;
    quantum_luminance[37]=109;
    quantum_luminance[38]=103;
    quantum_luminance[39]=77;
    quantum_luminance[40]=24;
    quantum_luminance[41]=35;
    quantum_luminance[42]=55;
    quantum_luminance[43]=64;
    quantum_luminance[44]=81;
    quantum_luminance[45]=104;
    quantum_luminance[46]=113;
    quantum_luminance[47]=92;
    quantum_luminance[48]=49;
    quantum_luminance[49]=64;
    quantum_luminance[50]=78;
    quantum_luminance[51]=87;
    quantum_luminance[52]=103;
    quantum_luminance[53]=121;
    quantum_luminance[54]=120;
    quantum_luminance[55]=101;
    quantum_luminance[56]=72;
    quantum_luminance[57]=92;
    quantum_luminance[58]=95;
    quantum_luminance[59]=98;
    quantum_luminance[60]=112;
    quantum_luminance[61]=100;
    quantum_luminance[62]=103;
    quantum_luminance[63]=99;
    
    AANscale(DivisorsLuminance, quantum_luminance, quality);
    
       
    
  
    for (int i = 4; i < 64; i++)
      quantum_chrominance[i]=99;
    
    quantum_chrominance[0]=17;
    quantum_chrominance[1]=18;
    quantum_chrominance[2]=24;
    quantum_chrominance[3]=47;
    
    quantum_chrominance[8]=18;
    quantum_chrominance[9]=21;
    quantum_chrominance[10]=26;
    quantum_chrominance[11]=66;
    
    quantum_chrominance[16]=24;
    quantum_chrominance[17]=26;
    quantum_chrominance[18]=56;

    quantum_chrominance[24]=47;
    quantum_chrominance[25]=66;
    

    AANscale(DivisorsChrominance, quantum_chrominance, quality);
    
    
    
    quantum[0] = quantum_luminance;
    quantum[1] = quantum_chrominance;

    divisors[0] = DivisorsLuminance;
    divisors[1] = DivisorsChrominance;
    
  }
  
  private final static double[] AANscaleFactor = { 1.0, 1.387039845, 1.306562965, 1.175875602,
    1.0, 0.785694958, 0.541196100, 0.275899379};

  static private void AANscale(double[] divisors, int[] values, int quality) {

    for (int j = 0; j < 64; j++) {
      int temp = (values[j] * quality + 50) / 100;
      values[j] = (temp < 1 ? 1 : temp > 255 ? 255 : temp);
    }

    for (int i = 0, index = 0; i < 8; i++)
      for (int j = 0; j < 8; j++, index++)
        
        
        
        
        
        divisors[index] = (0.125 / (values[index] * AANscaleFactor[i] * AANscaleFactor[j]));
  }

  
  
  
  
  
  
  
  static double[][] forwardDCT(float input[][])
  {
    double output[][] = new double[N][N];
    double tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
    double tmp10, tmp11, tmp12, tmp13;
    double z1, z2, z3, z4, z5, z11, z13;
    
    for (int i = 0; i < 8; i++)
      for(int j = 0; j < 8; j++)
        output[i][j] = (input[i][j] - 128.0);
        
    
    for (int i = 0; i < 8; i++) {
      tmp0 = output[i][0] + output[i][7];
      tmp7 = output[i][0] - output[i][7];
      tmp1 = output[i][1] + output[i][6];
      tmp6 = output[i][1] - output[i][6];
      tmp2 = output[i][2] + output[i][5];
      tmp5 = output[i][2] - output[i][5];
      tmp3 = output[i][3] + output[i][4];
      tmp4 = output[i][3] - output[i][4];
      
      tmp10 = tmp0 + tmp3;
      tmp13 = tmp0 - tmp3;
      tmp11 = tmp1 + tmp2;
      tmp12 = tmp1 - tmp2;
      
      output[i][0] = tmp10 + tmp11;
      output[i][4] = tmp10 - tmp11;
      
      z1 = (tmp12 + tmp13) * 0.707106781;
      output[i][2] = tmp13 + z1;
      output[i][6] = tmp13 - z1;
      
      tmp10 = tmp4 + tmp5;
      tmp11 = tmp5 + tmp6;
      tmp12 = tmp6 + tmp7;
      
      z5 = (tmp10 - tmp12) * 0.382683433;
      z2 = 0.541196100 * tmp10 + z5;
      z4 = 1.306562965 * tmp12 + z5;
      z3 = tmp11 * 0.707106781;
      
      z11 = tmp7 + z3;
      z13 = tmp7 - z3;
      
      output[i][5] = z13 + z2;
      output[i][3] = z13 - z2;
      output[i][1] = z11 + z4;
      output[i][7] = z11 - z4;
    }
    
    for (int i = 0; i < 8; i++) {
      tmp0 = output[0][i] + output[7][i];
      tmp7 = output[0][i] - output[7][i];
      tmp1 = output[1][i] + output[6][i];
      tmp6 = output[1][i] - output[6][i];
      tmp2 = output[2][i] + output[5][i];
      tmp5 = output[2][i] - output[5][i];
      tmp3 = output[3][i] + output[4][i];
      tmp4 = output[3][i] - output[4][i];
      
      tmp10 = tmp0 + tmp3;
      tmp13 = tmp0 - tmp3;
      tmp11 = tmp1 + tmp2;
      tmp12 = tmp1 - tmp2;
      
      output[0][i] = tmp10 + tmp11;
      output[4][i] = tmp10 - tmp11;
      
      z1 = (tmp12 + tmp13) * 0.707106781;
      output[2][i] = tmp13 + z1;
      output[6][i] = tmp13 - z1;
      
      tmp10 = tmp4 + tmp5;
      tmp11 = tmp5 + tmp6;
      tmp12 = tmp6 + tmp7;
      
      z5 = (tmp10 - tmp12) * 0.382683433;
      z2 = 0.541196100 * tmp10 + z5;
      z4 = 1.306562965 * tmp12 + z5;
      z3 = tmp11 * 0.707106781;
      
      z11 = tmp7 + z3;
      z13 = tmp7 - z3;
      
      output[5][i] = z13 + z2;
      output[3][i] = z13 - z2;
      output[1][i] = z11 + z4;
      output[7][i] = z11 - z4;
    }
    
    return output;
  }
  
  
  static int[] quantizeBlock(double inputData[][], double[] divisorsCode) {
    int outputData[] = new int[NN];
    for (int i = 0, index = 0 ; i < 8; i++)
      for (int j = 0; j < 8; j++, index++)
        
        outputData[index] = (int)(Math.round(inputData[i][j] * divisorsCode[index]));
        
    return outputData;
  }
  
  
  
  

}




class Huffman
{
  private int bufferPutBits, bufferPutBuffer;    
  int ImageHeight;
  int ImageWidth;
  private int DC_matrix0[][];
  private int AC_matrix0[][];
  private int DC_matrix1[][];
  private int AC_matrix1[][];
  private int[][] DC_matrix[];
  private int[][] AC_matrix[];
  
  int NumOfDCTables;
  int NumOfACTables;
  final static int[] bitsDCluminance = { 0x00, 0, 1, 5, 1, 1,1,1,1,1,0,0,0,0,0,0,0};
  final static int[] valDCluminance = { 0,1,2,3,4,5,6,7,8,9,10,11 };
  final static int[] bitsDCchrominance = { 0x01,0,3,1,1,1,1,1,1,1,1,1,0,0,0,0,0 };
  final static int[] valDCchrominance = { 0,1,2,3,4,5,6,7,8,9,10,11 };
  final static int[] bitsACluminance = {0x10,0,2,1,3,3,2,4,3,5,5,4,4,0,0,1,0x7d };
  final static int[] valACluminance =
  { 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12,
      0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07,
      0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08,
      0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0,
      0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16,
      0x17, 0x18, 0x19, 0x1a, 0x25, 0x26, 0x27, 0x28,
      0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
      0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49,
      0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59,
      0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69,
      0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79,
      0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
      0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98,
      0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7,
      0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6,
      0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5,
      0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4,
      0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2,
      0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea,
      0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
      0xf9, 0xfa };
  final static int[] bitsACchrominance = { 0x11,0,2,1,2,4,4,3,4,7,5,4,4,0,1,2,0x77 };
  final static int[] valACchrominance = 
  { 0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21,
      0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71,
      0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
      0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0,
      0x15, 0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34,
      0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19, 0x1a, 0x26,
      0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 
      0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 
      0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 
      0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 
      0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 
      0x79, 0x7a, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 
      0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 
      0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 
      0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 
      0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 
      0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 
      0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 
      0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 
      0xea, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
      0xf9, 0xfa };
  
  
  final static int[] jpegNaturalOrder = {
       0,  1,  8, 16,  9,  2,  3, 10,
      17, 24, 32, 25, 18, 11,  4,  5,
      12, 19, 26, 33, 40, 48, 41, 34,
      27, 20, 13,  6,  7, 14, 21, 28,
      35, 42, 49, 56, 57, 50, 43, 36,
      29, 22, 15, 23, 30, 37, 44, 51,
      58, 59, 52, 45, 38, 31, 39, 46,
      53, 60, 61, 54, 47, 55, 62, 63,
  };

  Huffman(int Width,int Height)
  {    
    initHuf();
    ImageWidth=Width;
    ImageHeight=Height;
    
  }
  
  
  
  void HuffmanBlockEncoder(BufferedOutputStream outStream, int zigzag[], int prec, int DCcode, int ACcode)
  {
    int temp, temp2, nbits, k, r, i;
    
    NumOfDCTables = 2;
    NumOfACTables = 2;
    
    int[][] matrixDC = DC_matrix[DCcode];
    int[][] matrixAC = AC_matrix[ACcode];
    
    
    
    temp = temp2 = zigzag[0] - prec;
    if(temp < 0) {
      temp = -temp;
      temp2--;
    }
    nbits = 0;
    while (temp != 0) {
      nbits++;
      temp >>= 1;
    }
    
    bufferIt(outStream, matrixDC[nbits][0], matrixDC[nbits][1]);
    
    if (nbits != 0) {
      bufferIt(outStream, temp2, nbits);
    }
    
    
    
    r = 0;
    
    for (k = 1; k < 64; k++) {
      if ((temp = zigzag[jpegNaturalOrder[k]]) == 0) {
        r++;
      }
      else {
        while (r > 15) {
          bufferIt(outStream, matrixAC[0xF0][0], matrixAC[0xF0][1]);
          r -= 16;
        }
        temp2 = temp;
        if (temp < 0) {
          temp = -temp;
          temp2--;
        }
        nbits = 1;
        while ((temp >>= 1) != 0) {
          nbits++;
        }
        i = (r << 4) + nbits;
        bufferIt(outStream, matrixAC[i][0], matrixAC[i][1]);
        bufferIt(outStream, temp2, nbits);
        
        r = 0;
      }
    }
    
    if (r > 0) {
      bufferIt(outStream, matrixAC[0][0], matrixAC[0][1]);
    }
    
  }
  
  
  
  
  void bufferIt(BufferedOutputStream outStream, int code,int size)
  {
    int PutBuffer = code;
    int PutBits = bufferPutBits;
    
    PutBuffer &= (1 << size) - 1;
    PutBits += size;
    PutBuffer <<= 24 - PutBits;
    PutBuffer |= bufferPutBuffer;
    
    while(PutBits >= 8) {
      int c = ((PutBuffer >> 16) & 0xFF);
      try
      {
        outStream.write(c);
      }
      catch (IOException e) {
        Logger.error("IO Error", e);
      }
      if (c == 0xFF) {
        try
        {
          outStream.write(0);
        }
        catch (IOException e) {
          Logger.error("IO Error", e);
        }
      }
      PutBuffer <<= 8;
      PutBits -= 8;
    }
    bufferPutBuffer = PutBuffer;
    bufferPutBits = PutBits;
    
  }
  
  void flushBuffer(BufferedOutputStream outStream) {
    int PutBuffer = bufferPutBuffer;
    int PutBits = bufferPutBits;
    while (PutBits >= 8) {
      int c = ((PutBuffer >> 16) & 0xFF);
      try
      {
        outStream.write(c);
      }
      catch (IOException e) {
        Logger.error("IO Error", e);
      }
      if (c == 0xFF) {
        try {
          outStream.write(0);
        }
        catch (IOException e) {
          Logger.error("IO Error", e);
        }
      }
      PutBuffer <<= 8;
      PutBits -= 8;
    }
    if (PutBits > 0) {
      int c = ((PutBuffer >> 16) & 0xFF);
      try
      {
        outStream.write(c);
      }
      catch (IOException e) {
        Logger.error("IO Error", e);
      }
    }
  }
  
  
  
  private void initHuf()
  {
    DC_matrix0=new int[12][2];
    DC_matrix1=new int[12][2];
    AC_matrix0=new int[255][2];
    AC_matrix1=new int[255][2];
    DC_matrix = new int[2][][];
    AC_matrix = new int[2][][];
    int p, l, i, lastp, si, code;
    int[] huffsize = new int[257];
    int[] huffcode= new int[257];
    
    
    
    p = 0;
    for (l = 1; l <= 16; l++)
    {

      for (i = bitsDCchrominance[l]; --i >= 0;)
      {
        huffsize[p++] = l; 
      }
    }
    huffsize[p] = 0;
    lastp = p;
    
    code = 0;
    si = huffsize[0];
    p = 0;
    while(huffsize[p] != 0)
    {
      while(huffsize[p] == si)
      {
        huffcode[p++] = code;
        code++;
      }
      code <<= 1;
      si++;
    }
    
    for (p = 0; p < lastp; p++)
    {
      DC_matrix1[valDCchrominance[p]][0] = huffcode[p];
      DC_matrix1[valDCchrominance[p]][1] = huffsize[p];
    }
    
    
    
    p = 0;
    for (l = 1; l <= 16; l++)
    {
      for (i = bitsACchrominance[l]; --i >= 0;)

      {
        huffsize[p++] = l;
      }
    }
    huffsize[p] = 0;
    lastp = p;
    
    code = 0;
    si = huffsize[0];
    p = 0;
    while(huffsize[p] != 0)
    {
      while(huffsize[p] == si)
      {
        huffcode[p++] = code;
        code++;
      }
      code <<= 1;
      si++;
    }
    
    for (p = 0; p < lastp; p++)
    {
      AC_matrix1[valACchrominance[p]][0] = huffcode[p];
      AC_matrix1[valACchrominance[p]][1] = huffsize[p];
    }
    
    
    p = 0;
    for (l = 1; l <= 16; l++)
    {

      for (i = bitsDCluminance[l]; --i >= 0;)
      {
        huffsize[p++] = l;
      }
    }
    huffsize[p] = 0;
    lastp = p;
    
    code = 0;
    si = huffsize[0];
    p = 0;
    while(huffsize[p] != 0)
    {
      while(huffsize[p] == si)
      {
        huffcode[p++] = code;
        code++;
      }
      code <<= 1;
      si++;
    }
    
    for (p = 0; p < lastp; p++)
    {
      DC_matrix0[valDCluminance[p]][0] = huffcode[p];
      DC_matrix0[valDCluminance[p]][1] = huffsize[p];
    }
    
    
    
    p = 0;
    for (l = 1; l <= 16; l++)
    {

      for (i = bitsACluminance[l]; --i >= 0; )
      {
        huffsize[p++] = l;
      }
    }
    huffsize[p] = 0;
    lastp = p;
    
    code = 0;
    si = huffsize[0];
    p = 0;
    while(huffsize[p] != 0)
    {
      while(huffsize[p] == si)
      {
        huffcode[p++] = code;
        code++;
      }
      code <<= 1;
      si++;
    }
    for (int q = 0; q < lastp; q++)
    {
      AC_matrix0[valACluminance[q]][0] = huffcode[q];
      AC_matrix0[valACluminance[q]][1] = huffsize[q];
    } 
    
    DC_matrix[0] = DC_matrix0;
    DC_matrix[1] = DC_matrix1;
    AC_matrix[0] = AC_matrix0;
    AC_matrix[1] = AC_matrix1;
  }
  
}



class JpegInfo
{
  String Comment;
  private Image imageobj;
  int imageHeight;
  int imageWidth;
  int BlockWidth[];
  int BlockHeight[];
  
  int Precision = 8;
  int NumberOfComponents = 3;
  float[][] Components[];
  int[] CompID = {1, 2, 3};
  int[] HsampFactor = {1, 1, 1};
  int[] VsampFactor = {1, 1, 1};
  int[] QtableNumber = {0, 1, 1};
  int[] DCtableNumber = {0, 1, 1};
  int[] ACtableNumber = {0, 1, 1};
  private boolean[] lastColumnIsDummy = {false, false, false};
  private boolean[] lastRowIsDummy = {false, false, false};
  int Ss = 0;
  int Se = 63;
  int Ah = 0;
  int Al = 0;
  private int compWidth[], compHeight[];
  private int MaxHsampFactor;
  private int MaxVsampFactor;
  
  
  public JpegInfo(Image image, String comment)
  {
    Components = new float[NumberOfComponents][][];
    compWidth = new int[NumberOfComponents];
    compHeight = new int[NumberOfComponents];
    BlockWidth = new int[NumberOfComponents];
    BlockHeight = new int[NumberOfComponents];
    imageobj = image;
    imageWidth = image.getWidth(null);
    imageHeight = image.getHeight(null);
    Comment = comment + "\n\nJPEG Encoder Copyright 1998, James R. Weeks and BioElectroMech.  ";
    getYCCArray();
  }
  
  String getComment() {
    return Comment;
  }
  
  
  
  private void getYCCArray()
  {
    int values[] = new int[imageWidth * imageHeight];
    int r, g, b, y, x;
    
    
    
    
    
    PixelGrabber grabber = 
      new PixelGrabber(imageobj.getSource(), 0, 0, imageWidth, imageHeight, 
          values, 0, imageWidth);
    MaxHsampFactor = 1;
    MaxVsampFactor = 1;
    for (y = 0; y < NumberOfComponents; y++) {
      MaxHsampFactor = Math.max(MaxHsampFactor, HsampFactor[y]);
      MaxVsampFactor = Math.max(MaxVsampFactor, VsampFactor[y]);
    }
    for (y = 0; y < NumberOfComponents; y++) {
      compWidth[y] = (((imageWidth%8 != 0) ? ((int) Math.ceil(imageWidth/8.0))*8 : imageWidth)/MaxHsampFactor)*HsampFactor[y];
      if (compWidth[y] != ((imageWidth/MaxHsampFactor)*HsampFactor[y])) {
        lastColumnIsDummy[y] = true;
      }
      
      
      
      
      BlockWidth[y] = (int) Math.ceil(compWidth[y]/8.0);
      compHeight[y] = (((imageHeight%8 != 0) ? ((int) Math.ceil(imageHeight/8.0))*8: imageHeight)/MaxVsampFactor)*VsampFactor[y];
      if (compHeight[y] != ((imageHeight/MaxVsampFactor)*VsampFactor[y])) {
        lastRowIsDummy[y] = true;
      }
      BlockHeight[y] = (int) Math.ceil(compHeight[y]/8.0);
    }
    try
    {
      if(grabber.grabPixels() != true)
      {
        try
        {
          throw new AWTException("Grabber returned false: " + grabber.status());
        }
        catch (Exception e) {}
      }
    }
    catch (InterruptedException e) {}
    float Y[][] = new float[compHeight[0]][compWidth[0]];
    float Cr1[][] = new float[compHeight[0]][compWidth[0]];
    float Cb1[][] = new float[compHeight[0]][compWidth[0]];
    
    
    int index = 0;
    for (y = 0; y < imageHeight; ++y)
    {
      for (x = 0; x < imageWidth; ++x)
      {
        r = ((values[index] >> 16) & 0xff);
        g = ((values[index] >> 8) & 0xff);
        b = (values[index] & 0xff);
        
        
        
        
        
        
        
        Y[y][x] = (float)((0.299 * r + 0.587 * g + 0.114 * b));
        Cb1[y][x] = 128 + (float)((-0.16874 * r - 0.33126 * g + 0.5 * b));
        Cr1[y][x] = 128 + (float)((0.5 * r - 0.41869 * g - 0.08131 * b));
        index++;
      }
    }
    
    
    
    
    
    
    
    Components[0] = Y;
    
    Components[1] = Cb1;
    
    Components[2] = Cr1;
  }

  
}
