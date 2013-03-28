
package genj.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.logging.Logger;


 class AnselCharset extends Charset {
  
  private final static Logger LOG = Logger.getLogger("genj.io");
  


  public AnselCharset() {
    super("Ansel", new String[]{"ANSEL"});
  }

  
  public boolean contains(Charset cs) {
    
    return false;
    
  }

  
  public CharsetDecoder newDecoder() {
    return new Decoder(this);
  }

  
  public CharsetEncoder newEncoder() {
    return new Encoder(this);
  }

  
  private static class Encoder extends CharsetEncoder {
    
    private Encoder(AnselCharset ansel) {
      super(ansel, 1.5F, 2F);
    }

        
    public boolean isLegalReplacement(byte[] repl) {
      return true;
    }

    public boolean canEncode( char c )
    {
       if( c < 128)
           return true;  
       return unicode2ansel(c) >-1;
    }
    
    
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {

      
      CoderResult rc = CoderResult.UNDERFLOW;
      int pos=in.position();
      for (int limit=in.limit();pos<limit;pos++) {
        
        
        char c = in.get(pos);

        
        if (c<128) {
          
          if (out.remaining()<1) {
            rc = CoderResult.OVERFLOW;
            break;
          }
          out.put((byte)c);
          
        } else {
          int ansel = unicode2ansel(c);
          if( ansel == -1 )  {
            

            
            
            
            LOG.warning("Can't encode character  '+"+Integer.toHexString(c).toUpperCase() +"' in Ansel charset. Position "+pos+" in: \""+in.toString()+"\"");
              rc = CoderResult.unmappableForLength(1); 
              break;
          }
          if (ansel < 256) {
            if (out.remaining()<1) {
              rc = CoderResult.OVERFLOW;
              break;
            }
            out.put((byte)ansel);
          } else {
            if (out.remaining()<2) {
              rc = CoderResult.OVERFLOW;
              break;
            }
            out.put((byte)(ansel >>8));
            out.put((byte)(ansel&255));
          }
          }
        
        
      }
      
      
      in.position(pos);
       
      
      return rc;
    }

    
    private int unicode2ansel(int unicode) {
      switch (unicode) {
        case 0x00A1: return 0xC6;  
        case 0x00A3: return 0xB9;  
        case 0x00A9: return 0xC3;  
        case 0x00AE: return 0xAA;  
        case 0x00B0: return 0xC0;  
        case 0x00B1: return 0xAB;  
        case 0x00B7: return 0xA8;  
        case 0x00B8: return 0xF020;  
        case 0x00BF: return 0xC5;  
        case 0x00C0: return 0xE141;  
        case 0x00C1: return 0xE241;  
        case 0x00C2: return 0xE341;  
        case 0x00C3: return 0xE441;  
        case 0x00C4: return 0xE841;  
        case 0x00C5: return 0xEA41;  
        case 0x00C6: return 0xA5;  
        case 0x00C7: return 0xF043;  
        case 0x00C8: return 0xE145;  
        case 0x00C9: return 0xE245;  
        case 0x00CA: return 0xE345;  
        case 0x00CB: return 0xE845;  
        case 0x00CC: return 0xE149;  
        case 0x00CD: return 0xE249;  
        case 0x00CE: return 0xE349;  
        case 0x00CF: return 0xE849;  
        case 0x00D0: return 0xA3;  
        case 0x00D1: return 0xE44E;  
        case 0x00D2: return 0xE14F;  
        case 0x00D3: return 0xE24F;  
        case 0x00D4: return 0xE34F;  
        case 0x00D5: return 0xE44F;  
        case 0x00D6: return 0xE84F;  
        case 0x00D8: return 0xA2;  
        case 0x00D9: return 0xE155;  
        case 0x00DA: return 0xE255;  
        case 0x00DB: return 0xE355;  
        case 0x00DC: return 0xE855;  
        case 0x00DD: return 0xE259;  
        case 0x00DE: return 0xA4;  
        case 0x00DF: return 0xCF;  
        case 0x00E0: return 0xE161;  
        case 0x00E1: return 0xE261;  
        case 0x00E2: return 0xE361;  
        case 0x00E3: return 0xE461;  
        case 0x00E4: return 0xE861;  
        case 0x00E5: return 0xEA61;  
        case 0x00E6: return 0xB5;  
        case 0x00E7: return 0xF063;  
        case 0x00E8: return 0xE165;  
        case 0x00E9: return 0xE265;  
        case 0x00EA: return 0xE365;  
        case 0x00EB: return 0xE865;  
        case 0x00EC: return 0xE169;  
        case 0x00ED: return 0xE269;  
        case 0x00EE: return 0xE369;  
        case 0x00EF: return 0xE869;  
        case 0x00F0: return 0xBA;  
        case 0x00F1: return 0xE46E;  
        case 0x00F2: return 0xE16F;  
        case 0x00F3: return 0xE26F;  
        case 0x00F4: return 0xE36F;  
        case 0x00F5: return 0xE46F;  
        case 0x00F6: return 0xE86F;  
        case 0x00F8: return 0xB2;  
        case 0x00F9: return 0xE175;  
        case 0x00FA: return 0xE275;  
        case 0x00FB: return 0xE375;  
        case 0x00FC: return 0xE875;  
        case 0x00FD: return 0xE279;  
        case 0x00FE: return 0xB4;  
        case 0x00FF: return 0xE879;  
        case 0x0100: return 0xE541;  
        case 0x0101: return 0xE561;  
        case 0x0102: return 0xE641;  
        case 0x0103: return 0xE661;  
        case 0x0104: return 0xF141;  
        case 0x0105: return 0xF161;  
        case 0x0106: return 0xE243;  
        case 0x0107: return 0xE263;  
        case 0x0108: return 0xE343;  
        case 0x0109: return 0xE363;  
        case 0x010A: return 0xE743;  
        case 0x010B: return 0xE763;  
        case 0x010C: return 0xE943;  
        case 0x010D: return 0xE963;  
        case 0x010E: return 0xE944;  
        case 0x010F: return 0xE964;  
        case 0x0110: return 0xA3;  
        case 0x0111: return 0xB3;  
        case 0x0112: return 0xE545;  
        case 0x0113: return 0xE565;  
        case 0x0114: return 0xE645;  
        case 0x0115: return 0xE665;  
        case 0x0116: return 0xE745;  
        case 0x0117: return 0xE765;  
        case 0x0118: return 0xF145;  
        case 0x0119: return 0xF165;  
        case 0x011A: return 0xE945;  
        case 0x011B: return 0xE965;  
        case 0x011C: return 0xE347;  
        case 0x011D: return 0xE367;  
        case 0x011E: return 0xE647;  
        case 0x011F: return 0xE667;  
        case 0x0120: return 0xE747;  
        case 0x0121: return 0xE767;  
        case 0x0122: return 0xF047;  
        case 0x0123: return 0xF067;  
        case 0x0124: return 0xE348;  
        case 0x0125: return 0xE368;  
        case 0x0128: return 0xE449;  
        case 0x0129: return 0xE469;  
        case 0x012A: return 0xE549;  
        case 0x012B: return 0xE569;  
        case 0x012C: return 0xE649;  
        case 0x012D: return 0xE669;  
        case 0x012E: return 0xF149;  
        case 0x012F: return 0xF169;  
        case 0x0130: return 0xE749;  
        case 0x0131: return 0xB8;  
        case 0x0134: return 0xE34A;  
        case 0x0135: return 0xE36A;  
        case 0x0136: return 0xF04B;  
        case 0x0137: return 0xF06B;  
        case 0x0139: return 0xE24C;  
        case 0x013A: return 0xE26C;  
        case 0x013B: return 0xF04C;  
        case 0x013C: return 0xF06C;  
        case 0x013D: return 0xE94C;  
        case 0x013E: return 0xE96C;  
        case 0x0141: return 0xA1;  
        case 0x0142: return 0xB1;  
        case 0x0143: return 0xE24E;  
        case 0x0144: return 0xE26E;  
        case 0x0145: return 0xF04E;  
        case 0x0146: return 0xF06E;  
        case 0x0147: return 0xE94E;  
        case 0x0148: return 0xE96E;  
        case 0x014C: return 0xE54F;  
        case 0x014D: return 0xE56F;  
        case 0x014E: return 0xE64F;  
        case 0x014F: return 0xE66F;  
        case 0x0150: return 0xEE4F;  
        case 0x0151: return 0xEE6F;  
        case 0x0152: return 0xA6;  
        case 0x0153: return 0xB6;  
        case 0x0154: return 0xE252;  
        case 0x0155: return 0xE272;  
        case 0x0156: return 0xF052;  
        case 0x0157: return 0xF072;  
        case 0x0158: return 0xE952;  
        case 0x0159: return 0xE972;  
        case 0x015A: return 0xE253;  
        case 0x015B: return 0xE273;  
        case 0x015C: return 0xE353;  
        case 0x015D: return 0xE373;  
        case 0x015E: return 0xF053;  
        case 0x015F: return 0xF073;  
        case 0x0160: return 0xE953;  
        case 0x0161: return 0xE973;  
        case 0x0162: return 0xF054;  
        case 0x0163: return 0xF074;  
        case 0x0164: return 0xE954;  
        case 0x0165: return 0xE974;  
        case 0x0168: return 0xE455;  
        case 0x0169: return 0xE475;  
        case 0x016A: return 0xE555;  
        case 0x016B: return 0xE575;  
        case 0x016C: return 0xE655;  
        case 0x016D: return 0xE675;  
        case 0x016E: return 0xEAAD;  
        case 0x016F: return 0xEA75;  
        case 0x0170: return 0xEE55;  
        case 0x0171: return 0xEE75;  
        case 0x0172: return 0xF155;  
        case 0x0173: return 0xF175;  
        case 0x0174: return 0xE357;  
        case 0x0175: return 0xE377;  
        case 0x0176: return 0xE359;  
        case 0x0177: return 0xE379;  
        case 0x0178: return 0xE859;  
        case 0x0179: return 0xE25A;  
        case 0x017A: return 0xE27A;  
        case 0x017B: return 0xE75A;  
        case 0x017C: return 0xE77A;  
        case 0x017D: return 0xE95A;  
        case 0x017E: return 0xE97A;  
        case 0x01A0: return 0xAC;  
        case 0x01A1: return 0xBC;  
        case 0x01AF: return 0xAD;  
        case 0x01B0: return 0xBD;  
        case 0x01CD: return 0xE941;  
        case 0x01CE: return 0xE961;  
        case 0x01CF: return 0xE949;  
        case 0x01D0: return 0xE969;  
        case 0x01D1: return 0xE94F;  
        case 0x01D2: return 0xE96F;  
        case 0x01D3: return 0xE955;  
        case 0x01D4: return 0xE975;  
        case 0x01E2: return 0xE5A5;  
        case 0x01E3: return 0xE5B5;  
        case 0x01E6: return 0xE947;  
        case 0x01E7: return 0xE967;  
        case 0x01E8: return 0xE94B;  
        case 0x01E9: return 0xE96B;  
        case 0x01EA: return 0xF14F;  
        case 0x01EB: return 0xF16F;  
        case 0x01F0: return 0xE96A;  
        case 0x01F4: return 0xE247;  
        case 0x01F5: return 0xE267;  
        case 0x01FC: return 0xE2A5;  
        case 0x01FD: return 0xE2B5;  
        case 0x02B9: return 0xA7;  
        case 0x02BA: return 0xB7;  
        case 0x02BE: return 0xAE;  
        case 0x02BF: return 0xB0;  
        case 0x0300: return 0xE1;  
        case 0x0301: return 0xE2;  
        case 0x0302: return 0xE3;  
        case 0x0303: return 0xE4;  
        case 0x0304: return 0xE5;  
        case 0x0306: return 0xE6;  
        case 0x0307: return 0xE7;  
        case 0x0309: return 0xE0;  
        case 0x030A: return 0xEA;  
        case 0x030B: return 0xEE;  
        case 0x030C: return 0xE9;  
        case 0x0310: return 0xEF;  
        case 0x0313: return 0xFE;  
        case 0x0315: return 0xED;  
        case 0x031C: return 0xF8;  
        case 0x0323: return 0xF2;  
        case 0x0324: return 0xF3;  
        case 0x0325: return 0xF4;  
        case 0x0326: return 0xF7;  
        case 0x0327: return 0xF0;  
        case 0x0328: return 0xF1;  
        case 0x032E: return 0xF9;  
        case 0x0332: return 0xF6;  
        case 0x0333: return 0xF5;  
        case 0x1E00: return 0xF441;  
        case 0x1E01: return 0xF461;  
        case 0x1E02: return 0xE742;  
        case 0x1E03: return 0xE762;  
        case 0x1E04: return 0xF242;  
        case 0x1E05: return 0xF262;  
        case 0x1E0A: return 0xE744;  
        case 0x1E0B: return 0xE764;  
        case 0x1E0C: return 0xF244;  
        case 0x1E0D: return 0xF264;  
        case 0x1E10: return 0xF044;  
        case 0x1E11: return 0xF064;  
        case 0x1E1E: return 0xE746;  
        case 0x1E1F: return 0xE766;  
        case 0x1E20: return 0xE547;  
        case 0x1E21: return 0xE567;  
        case 0x1E22: return 0xE748;  
        case 0x1E23: return 0xE768;  
        case 0x1E24: return 0xF248;  
        case 0x1E25: return 0xF268;  
        case 0x1E26: return 0xE848;  
        case 0x1E27: return 0xE868;  
        case 0x1E28: return 0xF048;  
        case 0x1E29: return 0xF068;  
        case 0x1E2A: return 0xF948;  
        case 0x1E2B: return 0xF968;  
        case 0x1E30: return 0xE24B;  
        case 0x1E31: return 0xE26B;  
        case 0x1E32: return 0xF24B;  
        case 0x1E33: return 0xF26B;  
        case 0x1E36: return 0xF24C;  
        case 0x1E37: return 0xF26C;  
        case 0x1E3E: return 0xE24D;  
        case 0x1E3F: return 0xE26D;  
        case 0x1E40: return 0xE74D;  
        case 0x1E41: return 0xE76D;  
        case 0x1E42: return 0xF24D;  
        case 0x1E43: return 0xF26D;  
        case 0x1E44: return 0xE74E;  
        case 0x1E45: return 0xE76E;  
        case 0x1E46: return 0xF24E;  
        case 0x1E47: return 0xF26E;  
        case 0x1E54: return 0xE250;  
        case 0x1E55: return 0xE270;  
        case 0x1E56: return 0xE750;  
        case 0x1E57: return 0xE770;  
        case 0x1E58: return 0xE752;  
        case 0x1E59: return 0xE772;  
        case 0x1E5A: return 0xF252;  
        case 0x1E5B: return 0xF272;  
        case 0x1E60: return 0xE753;  
        case 0x1E61: return 0xE773;  
        case 0x1E62: return 0xF253;  
        case 0x1E63: return 0xF273;  
        case 0x1E6A: return 0xE754;  
        case 0x1E6B: return 0xE774;  
        case 0x1E6C: return 0xF254;  
        case 0x1E6D: return 0xF274;  
        case 0x1E72: return 0xF355;  
        case 0x1E73: return 0xF375;  
        case 0x1E7C: return 0xE456;  
        case 0x1E7D: return 0xE476;  
        case 0x1E7E: return 0xF256;  
        case 0x1E7F: return 0xF276;  
        case 0x1E80: return 0xE157;  
        case 0x1E81: return 0xE177;  
        case 0x1E82: return 0xE257;  
        case 0x1E83: return 0xE277;  
        case 0x1E84: return 0xE857;  
        case 0x1E85: return 0xE877;  
        case 0x1E86: return 0xE757;  
        case 0x1E87: return 0xE777;  
        case 0x1E88: return 0xF257;  
        case 0x1E89: return 0xF277;  
        case 0x1E8A: return 0xE758;  
        case 0x1E8B: return 0xE778;  
        case 0x1E8C: return 0xE858;  
        case 0x1E8D: return 0xE878;  
        case 0x1E8E: return 0xE759;  
        case 0x1E8F: return 0xE779;  
        case 0x1E90: return 0xE35A;  
        case 0x1E91: return 0xE37A;  
        case 0x1E92: return 0xF25A;  
        case 0x1E93: return 0xF27A;  
        case 0x1E97: return 0xE874;  
        case 0x1E98: return 0xEA77;  
        case 0x1E99: return 0xEA79;  
        case 0x1EA0: return 0xF241;  
        case 0x1EA1: return 0xF261;  
        case 0x1EA2: return 0xE041;  
        case 0x1EA3: return 0xE061;  
        case 0x1EB8: return 0xF245;  
        case 0x1EB9: return 0xF265;  
        case 0x1EBA: return 0xE045;  
        case 0x1EBB: return 0xE065;  
        case 0x1EBC: return 0xE445;  
        case 0x1EBD: return 0xE465;  
        case 0x1EC8: return 0xE049;  
        case 0x1EC9: return 0xE069;  
        case 0x1ECA: return 0xF249;  
        case 0x1ECB: return 0xF269;  
        case 0x1ECC: return 0xF24F;  
        case 0x1ECD: return 0xF26F;  
        case 0x1ECE: return 0xE04F;  
        case 0x1ECF: return 0xE06F;  
        case 0x1EE4: return 0xF255;  
        case 0x1EE5: return 0xF275;  
        case 0x1EE6: return 0xE055;  
        case 0x1EE7: return 0xE075;  
        case 0x1EF2: return 0xE159;  
        case 0x1EF3: return 0xE179;  
        case 0x1EF4: return 0xF259;  
        case 0x1EF5: return 0xF279;  
        case 0x1EF6: return 0xE059;  
        case 0x1EF7: return 0xE079;  
        case 0x1EF8: return 0xE459;  
        case 0x1EF9: return 0xE479;  
        case 0x200C: return 0x8E;  
        case 0x200D: return 0x8D;  
        case 0x2113: return 0xC1;  
        case 0x2117: return 0xC2;  
        case 0x266D: return 0xA9;  
        case 0x266F: return 0xC4;  
        case 0xFE20: return 0xEB;  
        case 0xFE21: return 0xEC;  
        case 0xFE22: return 0xFA;  
        case 0xFE23: return 0xFB;  

        
        
        default: return -1;
      }
    }

  } 

  
  private class Decoder extends CharsetDecoder {

    
    private Decoder(AnselCharset ansel) {
      super(ansel,1F, 1F);
    }
    
    
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
      
      
      if (!in.hasRemaining())
        return CoderResult.UNDERFLOW;
      
      
      if (in.position()==0&&in.remaining()==1) {
        
        
        if (!out.hasRemaining())
          return CoderResult.OVERFLOW;
        out.put((char)ansel1byte2unicode(in.get()&0xff));
        
        return CoderResult.UNDERFLOW;
      }
      
      
      CoderResult rc = CoderResult.UNDERFLOW;
      int pos = in.position();
      for (int limit=in.limit(); pos<limit; ) {

        
        if (limit-pos<2)
          break;
        
        
        if (!out.hasRemaining()) {
          rc = CoderResult.OVERFLOW;
          break;
        }
          
        
        int
          a = in.get(pos  )&0xff,
          b = in.get(pos+1)&0xff;

        int unicode = ansel2byte2unicode( a << 8 | b);
        if (unicode>0) {
          out.put((char)unicode);
          pos+=2;
          continue;
        }

        out.put((char)ansel1byte2unicode(a));
        pos++;

        
      }
      
      
      in.position(pos);
      
      
      return rc;
    }








    
    
    private int ansel1byte2unicode(int ansel) {
    
      switch (ansel) {
        case 0x8D :
          return 0x200D; 
        case 0x8E :
          return 0x200C; 
        case 0xA1 :
          return 0x0141; 
        case 0xA2 :
          return 0x00D8; 
          
        case 0xA3 :
          return 0x00D0; 
        case 0xA4 :
          return 0x00DE; 
        case 0xA5 :
          return 0x00C6; 
        case 0xA6 :
          return 0x0152; 
        case 0xA7 :
          return 0x02B9; 
        case 0xA8 :
          return 0x00B7; 
        case 0xA9 :
          return 0x266D; 
        case 0xAA :
          return 0x00AE; 
        case 0xAB :
          return 0x00B1; 
        case 0xAC :
          return 0x01A0; 
        case 0xAD :
          return 0x01AF; 
        case 0xAE :
          return 0x02BE; 
        case 0xB0 :
          return 0x02BF; 
        case 0xB1 :
          return 0x0142; 
        case 0xB2 :
          return 0x00F8; 
        case 0xB3 :
          return 0x0111; 
        case 0xB4 :
          return 0x00FE; 
        case 0xB5 :
          return 0x00E6; 
        case 0xB6 :
          return 0x0153; 
        case 0xB7 :
          return 0x02BA; 
        case 0xB8 :
          return 0x0131; 
        case 0xB9 :
          return 0x00A3; 
        case 0xBA :
          return 0x00F0; 
        case 0xBC :
          return 0x01A1; 
        case 0xBD :
          return 0x01B0; 
        case 0xC0 :
          return 0x00B0; 
        case 0xC1 :
          return 0x2113; 
        case 0xC2 :
          return 0x2117; 
        case 0xC3 :
          return 0x00A9; 
        case 0xC4 :
          return 0x266F; 
        case 0xC5 :
          return 0x00BF; 
        case 0xC6 :
          return 0x00A1; 
        case 0xCF :
          return 0x00DF; 
        case 0xE0 :
          return 0x0309; 
        case 0xE1 :
          return 0x0300; 
        case 0xE2 :
          return 0x0301; 
        case 0xE3 :
          return 0x0302; 
        case 0xE4 :
          return 0x0303; 
        case 0xE5 :
          return 0x0304; 
        case 0xE6 :
          return 0x0306; 
        case 0xE7 :
          return 0x0307; 
        case 0xE9 :
          return 0x030C; 
        case 0xEA :
          return 0x030A; 
        case 0xEB :
          return 0xFE20; 
        case 0xEC :
          return 0xFE21; 
        case 0xED :
          return 0x0315; 
        case 0xEE :
          return 0x030B; 
        case 0xEF :
          return 0x0310; 
        case 0xF0 :
          return 0x0327; 
        case 0xF1 :
          return 0x0328; 
        case 0xF2 :
          return 0x0323; 
        case 0xF3 :
          return 0x0324; 
        case 0xF4 :
          return 0x0325; 
        case 0xF5 :
          return 0x0333; 
        case 0xF6 :
          return 0x0332; 
        case 0xF7 :
          return 0x0326; 
        case 0xF8 :
          return 0x031C; 
        case 0xF9 :
          return 0x032E; 
        case 0xFA :
          return 0xFE22; 
        case 0xFB :
          return 0xFE23; 
        case 0xFE :
          return 0x0313; 
      } 
    
      
      return (byte)ansel;
    }

    
    private int ansel2byte2unicode(int ansel) {
      switch (ansel) {
        case 0xE041 :
          return 0x1EA2; 
        case 0xE045 :
          return 0x1EBA; 
        case 0xE049 :
          return 0x1EC8; 
        case 0xE04F :
          return 0x1ECE; 
        case 0xE055 :
          return 0x1EE6; 
        case 0xE059 :
          return 0x1EF6; 
        case 0xE061 :
          return 0x1EA3; 
        case 0xE065 :
          return 0x1EBB; 
        case 0xE069 :
          return 0x1EC9; 
        case 0xE06F :
          return 0x1ECF; 
        case 0xE075 :
          return 0x1EE7; 
        case 0xE079 :
          return 0x1EF7; 
        case 0xE141 :
          return 0x00C0; 
        case 0xE145 :
          return 0x00C8; 
        case 0xE149 :
          return 0x00CC; 
        case 0xE14F :
          return 0x00D2; 
        case 0xE155 :
          return 0x00D9; 
        case 0xE157 :
          return 0x1E80; 
        case 0xE159 :
          return 0x1EF2; 
        case 0xE161 :
          return 0x00E0; 
        case 0xE165 :
          return 0x00E8; 
        case 0xE169 :
          return 0x00EC; 
        case 0xE16F :
          return 0x00F2; 
        case 0xE175 :
          return 0x00F9; 
        case 0xE177 :
          return 0x1E81; 
        case 0xE179 :
          return 0x1EF3; 
        case 0xE241 :
          return 0x00C1; 
        case 0xE243 :
          return 0x0106; 
        case 0xE245 :
          return 0x00C9; 
        case 0xE247 :
          return 0x01F4; 
        case 0xE249 :
          return 0x00CD; 
        case 0xE24B :
          return 0x1E30; 
        case 0xE24C :
          return 0x0139; 
        case 0xE24D :
          return 0x1E3E; 
        case 0xE24E :
          return 0x0143; 
        case 0xE24F :
          return 0x00D3; 
        case 0xE250 :
          return 0x1E54; 
        case 0xE252 :
          return 0x0154; 
        case 0xE253 :
          return 0x015A; 
        case 0xE255 :
          return 0x00DA; 
        case 0xE257 :
          return 0x1E82; 
        case 0xE259 :
          return 0x00DD; 
        case 0xE25A :
          return 0x0179; 
        case 0xE261 :
          return 0x00E1; 
        case 0xE263 :
          return 0x0107; 
        case 0xE265 :
          return 0x00E9; 
        case 0xE267 :
          return 0x01F5; 
        case 0xE269 :
          return 0x00ED; 
        case 0xE26B :
          return 0x1E31; 
        case 0xE26C :
          return 0x013A; 
        case 0xE26D :
          return 0x1E3F; 
        case 0xE26E :
          return 0x0144; 
        case 0xE26F :
          return 0x00F3; 
        case 0xE270 :
          return 0x1E55; 
        case 0xE272 :
          return 0x0155; 
        case 0xE273 :
          return 0x015B; 
        case 0xE275 :
          return 0x00FA; 
        case 0xE277 :
          return 0x1E83; 
        case 0xE279 :
          return 0x00FD; 
        case 0xE27A :
          return 0x017A; 
        case 0xE2A5 :
          return 0x01FC; 
        case 0xE2B5 :
          return 0x01FD; 
        case 0xE341 :
          return 0x00C2; 
        case 0xE343 :
          return 0x0108; 
        case 0xE345 :
          return 0x00CA; 
        case 0xE347 :
          return 0x011C; 
        case 0xE348 :
          return 0x0124; 
        case 0xE349 :
          return 0x00CE; 
        case 0xE34A :
          return 0x0134; 
        case 0xE34F :
          return 0x00D4; 
        case 0xE353 :
          return 0x015C; 
        case 0xE355 :
          return 0x00DB; 
        case 0xE357 :
          return 0x0174; 
        case 0xE359 :
          return 0x0176; 
        case 0xE35A :
          return 0x1E90; 
        case 0xE361 :
          return 0x00E2; 
        case 0xE363 :
          return 0x0109; 
        case 0xE365 :
          return 0x00EA; 
        case 0xE367 :
          return 0x011D; 
        case 0xE368 :
          return 0x0125; 
        case 0xE369 :
          return 0x00EE; 
        case 0xE36A :
          return 0x0135; 
        case 0xE36F :
          return 0x00F4; 
        case 0xE373 :
          return 0x015D; 
        case 0xE375 :
          return 0x00FB; 
        case 0xE377 :
          return 0x0175; 
        case 0xE379 :
          return 0x0177; 
        case 0xE37A :
          return 0x1E91; 
        case 0xE441 :
          return 0x00C3; 
        case 0xE445 :
          return 0x1EBC; 
        case 0xE449 :
          return 0x0128; 
        case 0xE44E :
          return 0x00D1; 
        case 0xE44F :
          return 0x00D5; 
        case 0xE455 :
          return 0x0168; 
        case 0xE456 :
          return 0x1E7C; 
        case 0xE459 :
          return 0x1EF8; 
        case 0xE461 :
          return 0x00E3; 
        case 0xE465 :
          return 0x1EBD; 
        case 0xE469 :
          return 0x0129; 
        case 0xE46E :
          return 0x00F1; 
        case 0xE46F :
          return 0x00F5; 
        case 0xE475 :
          return 0x0169; 
        case 0xE476 :
          return 0x1E7D; 
        case 0xE479 :
          return 0x1EF9; 
        case 0xE541 :
          return 0x0100; 
        case 0xE545 :
          return 0x0112; 
        case 0xE547 :
          return 0x1E20; 
        case 0xE549 :
          return 0x012A; 
        case 0xE54F :
          return 0x014C; 
        case 0xE555 :
          return 0x016A; 
        case 0xE561 :
          return 0x0101; 
        case 0xE565 :
          return 0x0113; 
        case 0xE567 :
          return 0x1E21; 
        case 0xE569 :
          return 0x012B; 
        case 0xE56F :
          return 0x014D; 
        case 0xE575 :
          return 0x016B; 
        case 0xE5A5 :
          return 0x01E2; 
        case 0xE5B5 :
          return 0x01E3; 
        case 0xE641 :
          return 0x0102; 
        case 0xE645 :
          return 0x0114; 
        case 0xE647 :
          return 0x011E; 
        case 0xE649 :
          return 0x012C; 
        case 0xE64F :
          return 0x014E; 
        case 0xE655 :
          return 0x016C; 
        case 0xE661 :
          return 0x0103; 
        case 0xE665 :
          return 0x0115; 
        case 0xE667 :
          return 0x011F; 
        case 0xE669 :
          return 0x012D; 
        case 0xE66F :
          return 0x014F; 
        case 0xE675 :
          return 0x016D; 
        case 0xE742 :
          return 0x1E02; 
        case 0xE743 :
          return 0x010A; 
        case 0xE744 :
          return 0x1E0A; 
        case 0xE745 :
          return 0x0116; 
        case 0xE746 :
          return 0x1E1E; 
        case 0xE747 :
          return 0x0120; 
        case 0xE748 :
          return 0x1E22; 
        case 0xE749 :
          return 0x0130; 
        case 0xE74D :
          return 0x1E40; 
        case 0xE74E :
          return 0x1E44; 
        case 0xE750 :
          return 0x1E56; 
        case 0xE752 :
          return 0x1E58; 
        case 0xE753 :
          return 0x1E60; 
        case 0xE754 :
          return 0x1E6A; 
        case 0xE757 :
          return 0x1E86; 
        case 0xE758 :
          return 0x1E8A; 
        case 0xE759 :
          return 0x1E8E; 
        case 0xE75A :
          return 0x017B; 
        case 0xE762 :
          return 0x1E03; 
        case 0xE763 :
          return 0x010B; 
        case 0xE764 :
          return 0x1E0B; 
        case 0xE765 :
          return 0x0117; 
        case 0xE766 :
          return 0x1E1F; 
        case 0xE767 :
          return 0x0121; 
        case 0xE768 :
          return 0x1E23; 
        case 0xE76D :
          return 0x1E41; 
        case 0xE76E :
          return 0x1E45; 
        case 0xE770 :
          return 0x1E57; 
        case 0xE772 :
          return 0x1E59; 
        case 0xE773 :
          return 0x1E61; 
        case 0xE774 :
          return 0x1E6B; 
        case 0xE777 :
          return 0x1E87; 
        case 0xE778 :
          return 0x1E8B; 
        case 0xE779 :
          return 0x1E8F; 
        case 0xE77A :
          return 0x017C; 
        case 0xE841 :
          return 0x00C4; 
        case 0xE845 :
          return 0x00CB; 
        case 0xE848 :
          return 0x1E26; 
        case 0xE849 :
          return 0x00CF; 
        case 0xE84F :
          return 0x00D6; 
        case 0xE855 :
          return 0x00DC; 
        case 0xE857 :
          return 0x1E84; 
        case 0xE858 :
          return 0x1E8C; 
        case 0xE859 :
          return 0x0178; 
        case 0xE861 :
          return 0x00E4; 
        case 0xE865 :
          return 0x00EB; 
        case 0xE868 :
          return 0x1E27; 
        case 0xE869 :
          return 0x00EF; 
        case 0xE86F :
          return 0x00F6; 
        case 0xE874 :
          return 0x1E97; 
        case 0xE875 :
          return 0x00FC; 
        case 0xE877 :
          return 0x1E85; 
        case 0xE878 :
          return 0x1E8D; 
        case 0xE879 :
          return 0x00FF; 
        case 0xE941 :
          return 0x01CD; 
        case 0xE943 :
          return 0x010C; 
        case 0xE944 :
          return 0x010E; 
        case 0xE945 :
          return 0x011A; 
        case 0xE947 :
          return 0x01E6; 
        case 0xE949 :
          return 0x01CF; 
        case 0xE94B :
          return 0x01E8; 
        case 0xE94C :
          return 0x013D; 
        case 0xE94E :
          return 0x0147; 
        case 0xE94F :
          return 0x01D1; 
        case 0xE952 :
          return 0x0158; 
        case 0xE953 :
          return 0x0160; 
        case 0xE954 :
          return 0x0164; 
        case 0xE955 :
          return 0x01D3; 
        case 0xE95A :
          return 0x017D; 
        case 0xE961 :
          return 0x01CE; 
        case 0xE963 :
          return 0x010D; 
        case 0xE964 :
          return 0x010F; 
        case 0xE965 :
          return 0x011B; 
        case 0xE967 :
          return 0x01E7; 
        case 0xE969 :
          return 0x01D0; 
        case 0xE96A :
          return 0x01F0; 
        case 0xE96B :
          return 0x01E9; 
        case 0xE96C :
          return 0x013E; 
        case 0xE96E :
          return 0x0148; 
        case 0xE96F :
          return 0x01D2; 
        case 0xE972 :
          return 0x0159; 
        case 0xE973 :
          return 0x0161; 
        case 0xE974 :
          return 0x0165; 
        case 0xE975 :
          return 0x01D4; 
        case 0xE97A :
          return 0x017E; 
        case 0xEA41 :
          return 0x00C5; 
        case 0xEA61 :
          return 0x00E5; 
        case 0xEA75 :
          return 0x016F; 
        case 0xEA77 :
          return 0x1E98; 
        case 0xEA79 :
          return 0x1E99; 
        case 0xEAAD :
          return 0x016E; 
        case 0xEE4F :
          return 0x0150; 
        case 0xEE55 :
          return 0x0170; 
        case 0xEE6F :
          return 0x0151; 
        case 0xEE75 :
          return 0x0171; 
        case 0xF020 :
          return 0x00B8; 
        case 0xF043 :
          return 0x00C7; 
        case 0xF044 :
          return 0x1E10; 
        case 0xF047 :
          return 0x0122; 
        case 0xF048 :
          return 0x1E28; 
        case 0xF04B :
          return 0x0136; 
        case 0xF04C :
          return 0x013B; 
        case 0xF04E :
          return 0x0145; 
        case 0xF052 :
          return 0x0156; 
        case 0xF053 :
          return 0x015E; 
        case 0xF054 :
          return 0x0162; 
        case 0xF063 :
          return 0x00E7; 
        case 0xF064 :
          return 0x1E11; 
        case 0xF067 :
          return 0x0123; 
        case 0xF068 :
          return 0x1E29; 
        case 0xF06B :
          return 0x0137; 
        case 0xF06C :
          return 0x013C; 
        case 0xF06E :
          return 0x0146; 
        case 0xF072 :
          return 0x0157; 
        case 0xF073 :
          return 0x015F; 
        case 0xF074 :
          return 0x0163; 
        case 0xF141 :
          return 0x0104; 
        case 0xF145 :
          return 0x0118; 
        case 0xF149 :
          return 0x012E; 
        case 0xF14F :
          return 0x01EA; 
        case 0xF155 :
          return 0x0172; 
        case 0xF161 :
          return 0x0105; 
        case 0xF165 :
          return 0x0119; 
        case 0xF169 :
          return 0x012F; 
        case 0xF16F :
          return 0x01EB; 
        case 0xF175 :
          return 0x0173; 
        case 0xF241 :
          return 0x1EA0; 
        case 0xF242 :
          return 0x1E04; 
        case 0xF244 :
          return 0x1E0C; 
        case 0xF245 :
          return 0x1EB8; 
        case 0xF248 :
          return 0x1E24; 
        case 0xF249 :
          return 0x1ECA; 
        case 0xF24B :
          return 0x1E32; 
        case 0xF24C :
          return 0x1E36; 
        case 0xF24D :
          return 0x1E42; 
        case 0xF24E :
          return 0x1E46; 
        case 0xF24F :
          return 0x1ECC; 
        case 0xF252 :
          return 0x1E5A; 
        case 0xF253 :
          return 0x1E62; 
        case 0xF254 :
          return 0x1E6C; 
        case 0xF255 :
          return 0x1EE4; 
        case 0xF256 :
          return 0x1E7E; 
        case 0xF257 :
          return 0x1E88; 
        case 0xF259 :
          return 0x1EF4; 
        case 0xF25A :
          return 0x1E92; 
        case 0xF261 :
          return 0x1EA1; 
        case 0xF262 :
          return 0x1E05; 
        case 0xF264 :
          return 0x1E0D; 
        case 0xF265 :
          return 0x1EB9; 
        case 0xF268 :
          return 0x1E25; 
        case 0xF269 :
          return 0x1ECB; 
        case 0xF26B :
          return 0x1E33; 
        case 0xF26C :
          return 0x1E37; 
        case 0xF26D :
          return 0x1E43; 
        case 0xF26E :
          return 0x1E47; 
        case 0xF26F :
          return 0x1ECD; 
        case 0xF272 :
          return 0x1E5B; 
        case 0xF273 :
          return 0x1E63; 
        case 0xF274 :
          return 0x1E6D; 
        case 0xF275 :
          return 0x1EE5; 
        case 0xF276 :
          return 0x1E7F; 
        case 0xF277 :
          return 0x1E89; 
        case 0xF279 :
          return 0x1EF5; 
        case 0xF27A :
          return 0x1E93; 
        case 0xF355 :
          return 0x1E72; 
        case 0xF375 :
          return 0x1E73; 
        case 0xF441 :
          return 0x1E00; 
        case 0xF461 :
          return 0x1E01; 
        case 0xF948 :
          return 0x1E2A; 
        case 0xF968 :
          return 0x1E2B; 

        default :
          return -1;
      } 
    }
    
  } 

} 
