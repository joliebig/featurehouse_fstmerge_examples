package net.sourceforge.pmd.cpd;

import java.util.ArrayList;


public class JavascriptTokenizer extends AbstractTokenizer {
    public JavascriptTokenizer() {
        
        this.stringToken = new ArrayList<String>();
        this.stringToken.add( "\'" );
        this.stringToken.add( "\"" );
        
        
        this.ignorableCharacter = new ArrayList<String>();
        this.ignorableCharacter.add( ";" );

        
        this.ignorableStmt = new ArrayList<String>();
    }
}