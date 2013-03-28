
package net.sourceforge.pmd.rules.design;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.rules.regex.RegexHelper;

import org.jaxen.JaxenException;


public class GenericClassCounterRule extends AbstractJavaRule {


	private static final PropertyDescriptor nameMatchDescriptor = new StringProperty("nameMatch",
			"A series of regex, separeted by ',' to match on the classname", new String[] {""},1.0f,',');

	private static final PropertyDescriptor operandDescriptor = new StringProperty("operand",
			"or/and value to refined match criteria",new String(),2.0f);

	private static final PropertyDescriptor typeMatchDescriptor = new StringProperty("typeMatch",
			"A series of regex, separeted by ',' to match on implements/extends classname",new String[]{""},3.0f,',');

	private static final PropertyDescriptor thresholdDescriptor = new StringProperty("threshold",
			"Defines how many occurences are legal",new String(),4.0f);


	private List<Pattern> namesMatch = new ArrayList<Pattern>(0);
	private List<Pattern> typesMatch = new ArrayList<Pattern>(0);
	private List<SimpleNode> matches = new ArrayList<SimpleNode>(0);
	private List<String> simpleClassname = new ArrayList<String>(0);


	@SuppressWarnings("PMD") 
	private String operand;
	private int threshold;

	private static String COUNTER_LABEL;

	
	public GenericClassCounterRule() {
		super();
	}

	private List<String> arrayAsList(String[] array) {
		List<String> list = new ArrayList<String>(array.length);
		int nbItem = 0;
		while (nbItem < array.length )
			list.add(array[nbItem++]);
		return list;
	}

	protected void init(){
		
		COUNTER_LABEL = this.getClass().getSimpleName() + ".number of match";
		
		this.namesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(nameMatchDescriptor)));
		this.operand = getStringProperty(operandDescriptor);
		this.typesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(typeMatchDescriptor)));
		String thresholdAsString = getStringProperty(thresholdDescriptor);
		this.threshold = Integer.valueOf(thresholdAsString);
		
		this.matches = new ArrayList<SimpleNode>();

	}

	 @Override
     public void start(RuleContext ctx) {
		 
         ctx.setAttribute(COUNTER_LABEL, new AtomicLong());
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
    			 if ( simpleClassname == null )
    				 simpleClassname = new ArrayList<String>(1);
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
		
		
		for (Pattern pattern : this.namesMatch)
			if ( RegexHelper.isMatch(pattern, classType.getImage()))
				addAMatch(classType, data);
		return super.visit(classType, data);
	}

	private void addAMatch(SimpleNode node,Object data) {
		
		RuleContext ctx = (RuleContext)data;
		AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
		total.incrementAndGet();
		
		this.matches.add(node);
	}

	@SuppressWarnings("unchecked")
    private boolean searchForAMatch(String matchType,SimpleNode node) {
		boolean status = false;
    	 String xpathQuery = "//ClassOrInterfaceDeclaration[" +
							"(./ExtendsList/ClassOrInterfaceType[@Image = '" + matchType + "'])" +
							"or" +
							"(./ImplementsList/ClassOrInterfaceType[@Image = '" + matchType + "'])" +
							"]";
		try
		{
			List list = node.findChildNodesWithXPath(xpathQuery);
			if ( list != null && list.size() > 0 ) {
				
				status = true;
			}
		}
		catch (JaxenException e) {
			
			e.printStackTrace();
		}
		return status;
	}

	@Override
    public void end(RuleContext ctx) {
		AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
        
        if ( total.get() > this.threshold )
        	for (SimpleNode node : this.matches)
        		addViolation(ctx,node , new Object[] { total });
		
		ctx.removeAttribute(COUNTER_LABEL);
		super.start(ctx);
     }
}
