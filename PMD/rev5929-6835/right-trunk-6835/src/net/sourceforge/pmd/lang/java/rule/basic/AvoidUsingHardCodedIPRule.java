package net.sourceforge.pmd.lang.java.rule.basic;

import java.net.InetAddress;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidUsingHardCodedIPRule extends AbstractJavaRule {

    private static final String IPV4_REGEXP = "^\"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";
    private static final String IPV6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9a-fA-F]+\"$";
    private static final String IPV4_MAPPED_IPV6_REGEXP = "^\"[0-9a-fA-F:]+:[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\"$";

    private static final Pattern IPV4_PATTERM = Pattern.compile(IPV4_REGEXP);
    private static final Pattern IPV6_PATTERM = Pattern.compile(IPV6_REGEXP);
    private static final Pattern IPV4_MAPPED_IPV6_PATTERM = Pattern.compile(IPV4_MAPPED_IPV6_REGEXP);

    
    @Override
    public Object visit(ASTLiteral node, Object data) {
        if (!node.isStringLiteral()) {
            return data;
        }
        String image = node.getImage();

	
        char c = image.charAt(1);
        if ((Character.isDigit(c) || c == ':') &&
                (IPV4_PATTERM.matcher(image).matches() ||
                        IPV6_PATTERM.matcher(image).matches() ||
                        IPV4_MAPPED_IPV6_PATTERM.matcher(image).matches())) {
            try {
                
                InetAddress.getByName(image.substring(1, image.length()-1));

                
                addViolation(data, node);
            } catch (Exception e) {
		
            }
        }
        return data;
    }

}
