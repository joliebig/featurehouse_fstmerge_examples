package net.sourceforge.pmd.lang.java.rule.basic;

import java.net.InetAddress;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidUsingHardCodedIPRule extends AbstractJavaRule {

    private static final String IPv4_REGEXP = "^\"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";
    private static final String IPv6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9a-fA-F]+\"$";
    private static final String IPv4_MAPPED_IPv6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";

    private static final Pattern IPv4_PATTERM = Pattern.compile(IPv4_REGEXP);
    private static final Pattern IPv6_PATTERM = Pattern.compile(IPv6_REGEXP);
    private static final Pattern IPv4_MAPPED_IPv6_PATTERM = Pattern.compile(IPv4_MAPPED_IPv6_REGEXP);

    
    public Object visit(ASTLiteral node, Object data) {
        String image = node.getImage();
        if (image == null || image.length() < 3 || image.charAt(0) != '"' ||
                image.charAt(image.length()-1) != '"') {
            return data;
        }
        
	
        char c = image.charAt(1);
        if ((Character.isDigit(c) || c == ':') &&
                (IPv4_PATTERM.matcher(image).matches() ||
                        IPv6_PATTERM.matcher(image).matches() ||
                        IPv4_MAPPED_IPv6_PATTERM.matcher(image).matches())) {
            try {
                
                InetAddress.getByName(image.substring(1, image.length()-1));
                
                
                addViolation(data, node);
            } catch (Exception e) {
		
            }
        }
        return data;
    }

}
