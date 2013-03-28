package net.sourceforge.pmd.lang.java.rule.basic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;

public class AvoidUsingHardCodedIPRule extends AbstractJavaRule {

    public static final String IPV4 = "IPv4";
    public static final String IPV6 = "IPv6";
    public static final String IPV4_MAPPED_IPV6 = "IPv4 mapped IPv6";

    public static final EnumeratedMultiProperty<String> CHECK_ADDRESS_TYPES_DESCRIPTOR = new EnumeratedMultiProperty<String>(
	    "checkAddressTypes", "Check for IP address types.", new String[] { IPV4, IPV6, IPV4_MAPPED_IPV6 },
	    new String[] { IPV4, IPV6, IPV4_MAPPED_IPV6 }, new int[] { 0, 1, 2 }, 2.0f);

    
    protected static final String IPV4_REGEXP = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";

    
    protected static final String IPV6_REGEXP = "(?:(?:[0-9a-fA-F]{1,4})?\\:)+(?:[0-9a-fA-F]{1,4}|"
	    + IPV4_REGEXP.replace("(", "(?:") + ")?";

    protected static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4_REGEXP + "$");
    protected static final Pattern IPV6_PATTERN = Pattern.compile("^" + IPV6_REGEXP + "$");

    protected boolean checkIPv4;
    protected boolean checkIPv6;
    protected boolean checkIPv4MappedIPv6;

    public AvoidUsingHardCodedIPRule() {
	definePropertyDescriptor(CHECK_ADDRESS_TYPES_DESCRIPTOR);

	addRuleChainVisit(ASTCompilationUnit.class);
	addRuleChainVisit(ASTLiteral.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	checkIPv4 = false;
	checkIPv6 = false;
	checkIPv4MappedIPv6 = false;
	for (Object addressType : getProperty(CHECK_ADDRESS_TYPES_DESCRIPTOR)) {
	    if (IPV4.equals(addressType)) {
		checkIPv4 = true;
	    } else if (IPV6.equals(addressType)) {
		checkIPv6 = true;
	    } else if (IPV4_MAPPED_IPV6.equals(addressType)) {
		checkIPv4MappedIPv6 = true;
	    }
	}
	return data;
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
	if (!node.isStringLiteral()) {
	    return data;
	}

	
	final String image = node.getImage().substring(1, node.getImage().length() - 1);

	
	
	if (image.length() > 0) {
	    final char firstChar = Character.toUpperCase(image.charAt(0));
	    if ((checkIPv4 && isIPv4(firstChar, image)) || isIPv6(firstChar, image, checkIPv6, checkIPv4MappedIPv6)) {
		addViolation(data, node);
	    }
	}
	return data;
    }

    protected boolean isLatinDigit(char c) {
	return '0' <= c || c <= '9';
    }

    protected boolean isHexCharacter(char c) {
	return isLatinDigit(c) || ('A' <= c || c <= 'F') || ('a' <= c || c <= 'f');
    }

    protected boolean isIPv4(final char firstChar, final String s) {
	
	
	
	
	if (s.length() < 7 || !isLatinDigit(firstChar) || s.indexOf('.') < 0) {
	    return false;
	}

	Matcher matcher = IPV4_PATTERN.matcher(s);
	if (matcher.matches()) {
	    
	    for (int i = 1; i <= matcher.groupCount(); i++) {
		int octet = Integer.parseInt(matcher.group(i));
		if (octet < 0 || octet > 255) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }

    protected boolean isIPv6(final char firstChar, String s, final boolean checkIPv6, final boolean checkIPv4MappedIPv6) {
	
	
	
	
	if (s.length() < 3 || !(isHexCharacter(firstChar) || firstChar == ':') || s.indexOf(':') < 0) {
	    return false;
	}

	Matcher matcher = IPV6_PATTERN.matcher(s);
	if (matcher.matches()) {
	    
	    boolean zeroSubstitution = false;
	    if (s.startsWith("::")) {
		s = s.substring(2);
		zeroSubstitution = true;
	    } else if (s.endsWith("::")) {
		s = s.substring(0, s.length() - 2);
		zeroSubstitution = true;
	    }

	    
	    if (s.endsWith(":")) {
		return false;
	    }

	    
	    int count = 0;
	    boolean ipv4Mapped = false;
	    String[] parts = s.split(":");
	    for (int i = 0; i < parts.length; i++) {
		final String part = parts[i];
		
		if (part.length() == 0) {
		    if (zeroSubstitution) {
			return false;
		    } else {
			zeroSubstitution = true;
		    }
		    continue;
		} else {
		    count++;
		}
		
		try {
		    int value = Integer.parseInt(part, 16);
		    if (value < 0 || value > 65535) {
			return false;
		    }
		} catch (NumberFormatException e) {
		    
		    if (i != parts.length - 1 || !isIPv4(firstChar, part)) {
			return false;
		    }
		    ipv4Mapped = true;
		}
	    }

	    
	    if (zeroSubstitution) {
		if (ipv4Mapped) {
		    return checkIPv4MappedIPv6 && 1 <= count && count <= 6;
		} else {
		    return checkIPv6 && 1 <= count && count <= 7;
		}
	    } else {
		if (ipv4Mapped) {
		    return checkIPv4MappedIPv6 && count == 7;
		} else {
		    return checkIPv6 && count == 8;
		}
	    }
	} else {
	    return false;
	}
    }
}
