
package net.sourceforge.pmd.cpd;

import java.util.ArrayList;


public class FortranTokenizer extends AbstractTokenizer implements Tokenizer
{
	public FortranTokenizer()
	{
		this.spanMultipleLinesString = false; 

		this.stringToken = new ArrayList<String>();
		this.stringToken.add("\'");
		
		this.ignorableCharacter = new ArrayList<String>();
		this.ignorableCharacter.add("(");
		this.ignorableCharacter.add(")");
		this.ignorableCharacter.add(",");

		
		this.ignorableStmt = new ArrayList<String>();
		this.ignorableStmt.add("do");
		this.ignorableStmt.add("while");
		this.ignorableStmt.add("end");
		this.ignorableStmt.add("if");
		
		this.oneLineCommentChar = '!';
	}
}
