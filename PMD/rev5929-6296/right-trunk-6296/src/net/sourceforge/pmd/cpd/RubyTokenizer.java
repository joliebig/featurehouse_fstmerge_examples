package net.sourceforge.pmd.cpd;

import java.util.ArrayList;




 public class RubyTokenizer extends AbstractTokenizer
 {
 	public RubyTokenizer()
 	{
 		
 		this.stringToken = new ArrayList<String>();
 		this.stringToken.add("\'");
 		this.stringToken.add("\"");
 		
 		this.ignorableCharacter = new ArrayList<String>();
 		this.ignorableCharacter.add("{");
 		this.ignorableCharacter.add("}");
 		this.ignorableCharacter.add("(");
 		this.ignorableCharacter.add(")");
 		this.ignorableCharacter.add(";");
 		this.ignorableCharacter.add(",");

 		
 		this.ignorableStmt = new ArrayList<String>();
 		this.ignorableStmt.add("while");
 		this.ignorableStmt.add("do");
 		this.ignorableStmt.add("end");
 	}
 }
