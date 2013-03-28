package net.sf.jabref.export.layout.format;

import net.sf.jabref.Globals;
import net.sf.jabref.export.layout.LayoutFormatter;

import java.util.HashMap;


public class FormatChars implements LayoutFormatter {

    public static HashMap<String, String> CHARS = new HashMap<String, String>();

    static {
		CHARS.put("`A", "À"); 
		CHARS.put("'A", "Á"); 
		CHARS.put("^A", "Â"); 
		CHARS.put("~A", "Ã"); 
		CHARS.put("\"A", "Ä"); 
		CHARS.put("AA", "Å"); 
		CHARS.put("AE", "Æ"); 
		CHARS.put("cC", "Ç"); 
        CHARS.put("`E", "È"); 
		CHARS.put("'E", "É"); 
		CHARS.put("^E", "Ê"); 
		CHARS.put("\"E", "Ë"); 
		CHARS.put("`I", "Ì"); 
		CHARS.put("'I", "Í"); 
		CHARS.put("^I", "Î"); 
		CHARS.put("\"I", "Ï"); 
		CHARS.put("DH", "Ð"); 
		CHARS.put("~N", "Ñ"); 
		CHARS.put("`O", "Ò"); 
		CHARS.put("'O", "Ó"); 
		CHARS.put("^O", "Ô"); 
		CHARS.put("~O", "Õ"); 
		CHARS.put("\"O", "Ö"); 
		
		
		
		CHARS.put("O", "Ø"); 
		CHARS.put("`U", "Ù"); 
		CHARS.put("'U", "Ú"); 
		CHARS.put("^U", "Û"); 
		CHARS.put("\"U", "Ü"); 
		CHARS.put("'Y", "Ý"); 
		CHARS.put("TH", "Þ"); 
		CHARS.put("ss", "ß"); 
		CHARS.put("`a", "à"); 
		CHARS.put("'a", "á"); 
		CHARS.put("^a", "â"); 
		CHARS.put("~a", "ã"); 
		CHARS.put("\"a", "ä"); 
		CHARS.put("aa", "å"); 
		CHARS.put("ae", "æ"); 
		CHARS.put("cc", "ç"); 
		CHARS.put("`e", "è"); 
		CHARS.put("'e", "é"); 
		CHARS.put("^e", "ê"); 
		CHARS.put("\"e", "ë"); 
		CHARS.put("`i", "ì"); 
		CHARS.put("'i", "í"); 
		CHARS.put("^i", "î"); 
		CHARS.put("\"i", "ï"); 
		CHARS.put("dh", "ð"); 
		CHARS.put("~n", "ñ"); 
		CHARS.put("`o", "ò"); 
		CHARS.put("'o", "ó"); 
		CHARS.put("^o", "ô"); 
		CHARS.put("~o", "õ"); 
		CHARS.put("\"o", "ö"); 
		
		
		
		CHARS.put("o", "ø"); 
		CHARS.put("`u", "ù"); 
		CHARS.put("'u", "ú"); 
		CHARS.put("^u", "û"); 
		CHARS.put("\"u", "ü"); 
		CHARS.put("'y", "ý"); 
		CHARS.put("th", "þ"); 
		CHARS.put("\"y", "ÿ"); 

		
		
		CHARS.put("=A", "&#256;"); 
		CHARS.put("=a", "&#257;"); 
		CHARS.put("uA", "&#258;"); 
		CHARS.put("ua", "&#259;"); 
		CHARS.put("kA", "&#260;"); 
		CHARS.put("ka", "&#261;"); 
		CHARS.put("'C", "&#262;"); 
		CHARS.put("'c", "&#263;"); 
		CHARS.put("^C", "&#264;"); 
		CHARS.put("^c", "&#265;"); 
		CHARS.put(".C", "&#266;"); 
		CHARS.put(".c", "&#267;"); 
		CHARS.put("vC", "&#268;"); 
		CHARS.put("vc", "&#269;"); 
		CHARS.put("vD", "&#270;"); 
		
		CHARS.put("DJ", "&#272;"); 
		CHARS.put("dj", "&#273;"); 
		CHARS.put("=E", "&#274;"); 
		CHARS.put("=e", "&#275;"); 
		CHARS.put("uE", "&#276;"); 
		CHARS.put("ue", "&#277;"); 
		CHARS.put(".E", "&#278;"); 
		CHARS.put(".e", "&#279;"); 
		CHARS.put("kE", "&#280;"); 
		CHARS.put("ke", "&#281;"); 
		CHARS.put("vE", "&#282;"); 
		CHARS.put("ve", "&#283;"); 
		CHARS.put("^G", "&#284;"); 
		CHARS.put("^g", "&#285;"); 
		CHARS.put("uG", "&#286;"); 
		CHARS.put("ug", "&#287;"); 
		CHARS.put(".G", "&#288;"); 
		CHARS.put(".g", "&#289;"); 
		CHARS.put("cG", "&#290;"); 
		CHARS.put("'g", "&#291;"); 
		CHARS.put("^H", "&#292;"); 
		CHARS.put("^h", "&#293;"); 
		CHARS.put("Hstrok", "&#294;"); 
		CHARS.put("hstrok", "&#295;"); 
		CHARS.put("~I", "&#296;"); 
		CHARS.put("~i", "&#297;"); 
		CHARS.put("=I", "&#298;"); 
		CHARS.put("=i", "&#299;"); 
		CHARS.put("uI", "&#300;"); 
		CHARS.put("ui", "&#301;"); 
		CHARS.put("kI", "&#302;"); 
		CHARS.put("ki", "&#303;"); 
		CHARS.put(".I", "&#304;"); 
		CHARS.put("i", "&#305;"); 
		
		
		CHARS.put("^J", "&#308;"); 
		CHARS.put("^j", "&#309;"); 
		CHARS.put("cK", "&#310;"); 
		CHARS.put("ck", "&#311;"); 
		
		CHARS.put("'L", "&#313;"); 
		CHARS.put("'l", "&#314;"); 
		CHARS.put("cL", "&#315;"); 
		CHARS.put("cl", "&#316;"); 
		
		
		CHARS.put("Lmidot", "&#319;"); 
		CHARS.put("lmidot", "&#320;"); 
		CHARS.put("L", "&#321;"); 
		CHARS.put("l", "&#322;"); 
		CHARS.put("'N", "&#323;"); 
		CHARS.put("'n", "&#324;"); 
		CHARS.put("cN", "&#325;"); 
		CHARS.put("cn", "&#326;"); 
		CHARS.put("vN", "&#327;"); 
		CHARS.put("vn", "&#328;"); 
		
		CHARS.put("NG", "&#330;"); 
		CHARS.put("ng", "&#331;"); 
		CHARS.put("=O", "&#332;"); 
		CHARS.put("=o", "&#333;"); 
		CHARS.put("uO", "&#334;"); 
		CHARS.put("uo", "&#335;"); 
		CHARS.put("HO", "&#336;"); 
		CHARS.put("Ho", "&#337;"); 
		CHARS.put("OE", "&#338;"); 
		CHARS.put("oe", "&#339;"); 
		CHARS.put("'R", "&#340;"); 
		CHARS.put("'r", "&#341;"); 
		CHARS.put("cR", "&#342;"); 
		CHARS.put("cr", "&#343;"); 
		CHARS.put("vR", "&#344;"); 
		CHARS.put("vr", "&#345;"); 
		CHARS.put("'S", "&#346;"); 
		CHARS.put("'s", "&#347;"); 
		CHARS.put("^S", "&#348;"); 
		CHARS.put("^s", "&#349;"); 
		CHARS.put("cS", "&#350;"); 
		CHARS.put("cs", "&#351;"); 
		CHARS.put("vS", "&#352;"); 
		CHARS.put("vs", "&#353;"); 
		CHARS.put("cT", "&#354;"); 
		CHARS.put("ct", "&#355;"); 
		CHARS.put("vT", "&#356;"); 
		
		CHARS.put("Tstrok", "&#358;"); 
		CHARS.put("tstrok", "&#359;"); 
		CHARS.put("~U", "&#360;"); 
		CHARS.put("~u", "&#361;"); 
		CHARS.put("=U", "&#362;"); 
		CHARS.put("=u", "&#363;"); 
		CHARS.put("uU", "&#364;"); 
		CHARS.put("uu", "&#365;"); 
		CHARS.put("rU", "&#366;"); 
		CHARS.put("ru", "&#367;"); 
		CHARS.put("HU", "&#368;"); 
		CHARS.put("Hu", "&#369;"); 
		CHARS.put("kU", "&#370;"); 
		CHARS.put("ku", "&#371;"); 
		CHARS.put("^W", "&#372;"); 
		CHARS.put("^w", "&#373;"); 
		CHARS.put("^Y", "&#374;"); 
		CHARS.put("^y", "&#375;"); 
		CHARS.put("\"Y", "&#376;"); 
		CHARS.put("'Z", "&#377;"); 
		CHARS.put("'z", "&#378;"); 
		CHARS.put(".Z", "&#379;"); 
		CHARS.put(".z", "&#380;"); 
		CHARS.put("vZ", "&#381;"); 
		CHARS.put("vz", "&#382;"); 
		
        CHARS.put("%", "%"); 
    }

    public String format(String field) {
		int i;
		field = field.replaceAll("&|\\\\&", "&amp;").replaceAll("[\\n]{1,}", "<p>");

		StringBuffer sb = new StringBuffer();
		StringBuffer currentCommand = null;
		
		char c;
		boolean escaped = false, incommand = false;
		
		for (i = 0; i < field.length(); i++) {
			c = field.charAt(i);
			if (escaped && (c == '\\')) {
				sb.append('\\');
				escaped = false;
			} else if (c == '\\') {
				if (incommand){
					
					String command = currentCommand.toString();
					Object result = CHARS.get(command);
					if (result != null) {
						sb.append((String) result);
					} else {
						sb.append(command);
					}
				}
				escaped = true;
				incommand = true;
				currentCommand = new StringBuffer();
			} else if (!incommand && (c == '{' || c == '}')) {
				
			} else if (Character.isLetter(c) || (c == '%')
				|| (Globals.SPECIAL_COMMAND_CHARS.indexOf(String.valueOf(c)) >= 0)) {
				escaped = false;

                if (!incommand)
					sb.append(c);
					
				else {
					currentCommand.append(c);
                    testCharCom: if ((currentCommand.length() == 1)
						&& (Globals.SPECIAL_COMMAND_CHARS.indexOf(currentCommand.toString()) >= 0)) {
						
						
						if (i >= field.length() - 1)
							break testCharCom;

						String command = currentCommand.toString();
						i++;
						c = field.charAt(i);
						
						String combody;
						if (c == '{') {
							IntAndString part = getPart(field, i, false);
							i += part.i;
							combody = part.s;
						} else {
							combody = field.substring(i, i + 1);
							
						}
						Object result = CHARS.get(command + combody);

						if (result != null)
							sb.append((String) result);

						incommand = false;
						escaped = false;
					} else { 
						
						if (i + 1 == field.length()){
							String command = currentCommand.toString();
                            Object result = CHARS.get(command);
							
							if (result != null) {
								sb.append((String) result);
							} else {
								sb.append(command);
							}
							
						}
					}
				}
			} else {
				String argument = null;

				if (!incommand) {
					sb.append(c);
				} else if (Character.isWhitespace(c) || (c == '{') || (c == '}')) {
					
					
					

					String command = currentCommand.toString();
                                                
                    if (c == '{') {
						IntAndString part = getPart(field, i, true);
						i += part.i;
						argument = part.s;
						if (argument != null) {
							
							Object result = CHARS.get(command + argument);
							
							
							
							
							
							if (result != null) {
								sb.append((String) result);
							} else {
								sb.append(argument);
							}
						}
                    } else if (c == '}') {
                        
                        
                        
                        Object result = CHARS.get(command);
                        if (result != null) {
                            sb.append((String) result);
                        } else {
                            
                            sb.append(command);
                        }
                    } else {
						Object result = CHARS.get(command);
						if (result != null) {
							sb.append((String) result);
						} else {
							sb.append(command);
						}
						sb.append(' ');
					}
				} else {
					
				}
				
				incommand = false;
				escaped = false;
			}
		}

		return sb.toString();
	}

	private IntAndString getPart(String text, int i, boolean terminateOnEndBraceOnly) {
		char c;
		int count = 0;
		
		StringBuffer part = new StringBuffer();
		
		
		i++;
		while (i < text.length() && Character.isWhitespace(text.charAt(i))){
			i++;
		}
		
		
		while (i < text.length()){
			c = text.charAt(i);
			if (!terminateOnEndBraceOnly && count == 0 && Character.isWhitespace(c)) {
				i--; 
					 
				break;
			}
			if (c == '}' && --count < 0)
				break;
			else if (c == '{')
				count++;
			part.append(c);
			i++;
		}
		return new IntAndString(part.length(), format(part.toString()));
	}

	private class IntAndString {
		public int i;

		String s;

		public IntAndString(int i, String s) {
			this.i = i;
			this.s = s;
		}
	}
}
