package net.sf.jabref.export.layout.format;

import net.sf.jabref.export.layout.*;
import net.sf.jabref.Globals;


public class RTFChars implements LayoutFormatter {

	public String format(String field) {

		StringBuffer sb = new StringBuffer("");
		StringBuffer currentCommand = null;
		boolean escaped = false, incommand = false;
		for (int i = 0; i < field.length(); i++) {
			char c = field.charAt(i);
			if (escaped && (c == '\\')) {
				sb.append('\\');
				escaped = false;
			}

			else if (c == '\\') {
				escaped = true;
				incommand = true;
				currentCommand = new StringBuffer();
			} else if (!incommand && (c == '{' || c == '}')) {
				
			} else if (Character.isLetter((char) c)
				|| (Globals.SPECIAL_COMMAND_CHARS.indexOf("" + (char) c) >= 0)) {
				escaped = false;
				if (!incommand){
					sb.append((char) c);
				} else {
					
					currentCommand.append((char) c);

					testCharCom: if ((currentCommand.length() == 1)
						&& (Globals.SPECIAL_COMMAND_CHARS.indexOf(currentCommand.toString()) >= 0)) {
						
						
						if (i >= field.length() - 1)
							break testCharCom;

						String command = currentCommand.toString();
						i++;
						c = field.charAt(i);
						String combody;
						if (c == '{') {
							IntAndString part = getPart(field, i);
							i += part.i;
							combody = part.s;
						} else {
							combody = field.substring(i, i + 1);
						}
						String result = (String)Globals.RTFCHARS.get(command + combody);

						if (result != null)
							sb.append(result);

						incommand = false;
						escaped = false;
				
					}

				}

			} else {
				
				testContent: if (!incommand || (!Character.isWhitespace(c) && (c != '{')))
					sb.append((char) c);
				else {
					
					if (i >= field.length() - 1)
						break testContent;

					if (c == '{') {

						String command = currentCommand.toString();
						
						
						if (command.equals("emph") || command.equals("textit")) {
							IntAndString part = getPart(field, i);
							i += part.i;
							sb.append("}{\\i ").append(part.s).append("}{");
						} else if (command.equals("textbf")) {
							IntAndString part = getPart(field, i);
							i += part.i;
							sb.append("}{\\b ").append(part.s).append("}{");
						}
					} else
						sb.append((char) c);

				}
				incommand = false;
				escaped = false;
			}
		}

		char[] chars = sb.toString().toCharArray();
		sb = new StringBuffer();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];

			if (c < 128) 
				sb.append(c);
			 else
				sb.append("\\u").append((long) c).append('?');
		}

		return sb.toString();
	}

	private IntAndString getPart(String text, int i) {
		char c;
		int count = 0;
		StringBuffer part = new StringBuffer();
		while ((count >= 0) && (i < text.length())) {
			i++;
			c = text.charAt(i);
			if (c == '}')
				count--;
			else if (c == '{')
				count++;

			part.append((char) c);
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
