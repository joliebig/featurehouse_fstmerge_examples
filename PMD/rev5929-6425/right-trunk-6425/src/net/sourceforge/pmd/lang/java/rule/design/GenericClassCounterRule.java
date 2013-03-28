
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;


public class GenericClassCounterRule extends AbstractJavaRule {


	private static final PropertyDescriptor NAME_MATCH_DESCRIPTOR = new StringProperty("nameMatch",
			"A series of regex, separated by ',' to match on the classname", new String[] {""},1.0f,',');

	private static final PropertyDescriptor OPERAND_DESCRIPTOR = new StringProperty("operand",
			"or/and value to refined match criteria",new String(),2.0f);

	private static final PropertyDescriptor TYPE_MATCH_DESCRIPTOR = new StringProperty("typeMatch",
			"A series of regex, separated by ',' to match on implements/extends classname",new String[]{""},3.0f,',');

	
	private static final PropertyDescriptor THRESHOLD_DESCRIPTOR = new StringProperty("threshold",
			"Defines how many occurences are legal",new String(),4.0f);


	private List<Pattern> namesMatch = new ArrayList<Pattern>(0);
	private List<Pattern> typesMatch = new ArrayList<Pattern>(0);
	private List<Node> matches = new ArrayList<Node>(0);
	private List<String> simpleClassname = new ArrayList<String>(0);


	@SuppressWarnings("PMD") 
	private String operand;
	private int threshold;

	private static String counterLabel;

    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(
    		new PropertyDescriptor[] {
    				NAME_MATCH_DESCRIPTOR, OPERAND_DESCRIPTOR,
    				TYPE_MATCH_DESCRIPTOR, THRESHOLD_DESCRIPTOR
    				});

    @Override
    protected Map<String, PropertyDescriptor> propertiesByName() {
        return PROPERTY_DESCRIPTORS_BY_NAME;
    }


	private List<String> arrayAsList(String[] array) {
		List<String> list = new ArrayList<String>(array.length);
		int nbItem = 0;
		while (nbItem < array.length ) {
			list.add(array[nbItem++]);
		}
		return list;
	}

	protected void init(){
		
		counterLabel = this.getClass().getSimpleName() + ".number of match";
		
		this.namesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(NAME_MATCH_DESCRIPTOR)));
		this.operand = getStringProperty(OPERAND_DESCRIPTOR);
		this.typesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(TYPE_MATCH_DESCRIPTOR)));
		String thresholdAsString = getStringProperty(THRESHOLD_DESCRIPTOR);
		this.threshold = Integer.valueOf(thresholdAsString);
		
		this.matches = new ArrayList<Node>();

	}

	 @Override
     public void start(RuleContext ctx) {
		 
         ctx.setAttribute(counterLabel, new AtomicLong());
         super.start(ctx);
     }

     @Override
     public Object visit(ASTCompilationUnit node, Object data) {
    	 init();
    	 return super.visit(node,data);
     }

     @Override
     public Object visit(ASTImportDeclaration node, Object data) {
    	 
    	 for (Pattern pattern : this.typesMatch) {
    		 if ( RegexHelper.isMatch(pattern,node.getImportedName())) {
    			 if ( simpleClassname == null ) {
    				 simpleClassname = new ArrayList<String>(1);
    			 }
    			 simpleClassname.add(node.getImportedName());
    		 }
    		 
    	 }
         return super.visit(node, data);
     }

	@Override
	public Object visit(ASTClassOrInterfaceType classType,Object data) {
		
		
		for (String matchType : simpleClassname) {
			if ( searchForAMatch(matchType,classType)) {
				addAMatch(classType, data);
			}
		}
		
		
		for (Pattern pattern : this.namesMatch) {
			if ( RegexHelper.isMatch(pattern, classType.getImage())) {
				addAMatch(classType, data);
			}
		}
		return super.visit(classType, data);
	}

	private void addAMatch(Node node,Object data) {
		
		RuleContext ctx = (RuleContext)data;
		AtomicLong total = (AtomicLong)ctx.getAttribute(counterLabel);
		total.incrementAndGet();
		
		this.matches.add(node);
	}

    private boolean searchForAMatch(String matchType, Node node) {
        String xpathQuery = "//ClassOrInterfaceDeclaration[(./ExtendsList/ClassOrInterfaceType[@Image = '" + matchType
                + "']) or (./ImplementsList/ClassOrInterfaceType[@Image = '" + matchType + "'])]";

        return node.hasDescendantMatchingXPath(xpathQuery);
    }

	@Override
    public void end(RuleContext ctx) {
		AtomicLong total = (AtomicLong)ctx.getAttribute(counterLabel);
        
        if ( total.get() > this.threshold ) {
        	for (Node node : this.matches) {
        		addViolation(ctx,node , new Object[] { total });
        	}
		
		ctx.removeAttribute(counterLabel);
		super.start(ctx);
        }
     }
}
