package net.sourceforge.pmd.cpd;

import java.util.ArrayList;


public class EcmascriptTokenizer extends AbstractTokenizer {
    public EcmascriptTokenizer() {
        
        this.stringToken = new ArrayList<String>();
        this.stringToken.add( "\'" );
        this.stringToken.add( "\"" );
        
        
        this.ignorableCharacter = new ArrayList<String>();
        this.ignorableCharacter.add( ";" );

        
        this.ignorableStmt = new ArrayList<String>();
    }
}