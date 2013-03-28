package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

 
 

public class BinaryDisplayConverter {
	
	
	 public static final int HEX = 16;
	 
	 
	 public static final int DECIMAL = 10;
	 
	 
	 public static final int OCTAL = 8;
	 
	 
	 public static final int BINARY = 2;
	
	
	 static class ConversionConstants {
	 	int width;	
	 	int radix;	
	 	
	 	ConversionConstants(int w, int r) {
	 		width = w;
	 		radix = r;
	 	}
	 }
	 
	 static ConversionConstants hex = new ConversionConstants(2, 16);
	 static ConversionConstants decimal = new ConversionConstants(3, 10);
	 private static ConversionConstants octal = new ConversionConstants(3, 8);
	 private static ConversionConstants binary = new ConversionConstants(8, 2);
	 
	 
	 private static String printable = "0123456789abcdefghijklmnopqrstuvwxyz" +
	 	"ABCDEFGHIJKLMNOPQRSTUVWXYZ`~!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
	 
	
	
	private BinaryDisplayConverter() {}
	
	
	public static String convertToString(Byte[] data, int base, boolean showAscii) {
		
		
		if (data == null)
			return null;

		StringBuffer buf = new StringBuffer();
		
		ConversionConstants convConst = getConstants(base);
		
		
		for (int i=0; i < data.length; i++) {
			int value = data[i].byteValue();
			String s = null;
			
			
			
			if (showAscii) {
				if (printable.indexOf((char)value) > -1) {
					s = Character.valueOf((char)value) +
						"          ".substring(10-(convConst.width - 1));
				}
			}
			
			
			
			if (s == null) {
				switch (base) {
					case DECIMAL:	
						
						if (value < 0)
							value = 256 + value;
						s = Integer.toString(value); break;
					case OCTAL:		s = Integer.toOctalString(value); break;
					case BINARY:	s = Integer.toBinaryString(value); break;
					case HEX:	
					default:
						s = Integer.toHexString(value);
				}
				
				
				if (s.length() > convConst.width)
					s = s.substring(s.length() - convConst.width);
			
				
				if (s.length() < convConst.width)
					buf.append("00000000".substring(8-(convConst.width - s.length())));
			}
			buf.append(s);
			buf.append("  ");	
		}
		return buf.toString();
	}
	
	
	public static Byte[] convertToBytes(String data, int base, boolean showAscii)
		throws NumberFormatException {
		
		ConversionConstants convConst = getConstants(base);
		
		if (data == null)
			return null;
		
		if (data.length() == 0)
			return new Byte[0];
		
		if (data.equals("<null>"))
			return null;
		
		int stringIndex = 0;
		int byteIndex = 0;
		Byte[] bytes = new Byte[(data.length()+2)/(convConst.width+2)];
		while (stringIndex < data.length()) {
			
			String s = data.substring(stringIndex, stringIndex+convConst.width);
			
			
			
			
			if (showAscii && s.charAt(1) == ' ') {
				
				bytes[byteIndex++] = Byte.valueOf((byte)s.charAt(0));
			}
			else {

				
				
				
				
				
				
				bytes[byteIndex++] = Byte.valueOf(
					(byte)(Integer.valueOf(s, convConst.radix)).intValue());	
			}	

			stringIndex += convConst.width + 2;
		}
	
		return bytes;
	}

	
	private static ConversionConstants getConstants(int base) {
		if (base == HEX) return hex;
		if (base == DECIMAL) return decimal;
		if (base == OCTAL) return octal;
		if (base == BINARY) return binary;
		return hex;	
	}
}
