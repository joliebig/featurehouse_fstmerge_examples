
package net.sourceforge.pmd.cpd;

import java.util.List;

public abstract class AbstractTokenizer implements Tokenizer
{

	protected List<String> stringToken;			
	protected List<String> ignorableCharacter; 	
												
	protected List<String> ignorableStmt; 		
	protected char ONE_LINE_COMMENT_CHAR = '#'; 

	private List<String> code;
	private int lineNumber = 0;
	private String currentLine;

	protected boolean spanMultipleLinesString = true;	

	private boolean downcaseString = true;

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        this.code = tokens.getCode();

        for ( this.lineNumber = 0; lineNumber < this.code.size(); lineNumber++ ) {
        	this.currentLine = this.code.get(this.lineNumber);
            int loc = 0;
            while ( loc < currentLine.length() ) {
                StringBuffer token = new StringBuffer();
                loc = getTokenFromLine(token,loc);
                if (token.length() > 0 && !isIgnorableString(token.toString())) {
                    if (downcaseString) {
                        token = new StringBuffer(token.toString().toLowerCase());
                    }
                    if ( CPD.debugEnable )
                    	System.out.println("Token added:" + token.toString());
                    tokenEntries.add(new TokenEntry(token.toString(),
                            tokens.getFileName(),
                            lineNumber));

                }
            }
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private int getTokenFromLine(StringBuffer token, int loc) {
        for (int j = loc; j < this.currentLine.length(); j++) {
            char tok = this.currentLine.charAt(j);
            if (!Character.isWhitespace(tok) && !ignoreCharacter(tok)) {
                if (isComment(tok)) {
                    if (token.length() > 0) {
                        return j;
                    } else {
                        return getCommentToken(token, loc);
                    }
                } else if (isString(tok)) {
                    if (token.length() > 0) {
                        return j; 
                    } else {
                        
                        return parseString(token, j, tok);
                    }
                } else {
                    token.append(tok);
                }
            } else {
                if (token.length() > 0) {
                    return j;
                }
            }
            loc = j;
        }
        return loc + 1;
    }

    private int parseString(StringBuffer token, int loc, char stringDelimiter) {
        boolean escaped = false;
        boolean done = false;
        char tok = ' '; 
        while ((loc < currentLine.length()) && ! done) {
            tok = currentLine.charAt(loc);
            if (escaped && tok == stringDelimiter) 
                escaped = false;
            else if (tok == stringDelimiter && (token.length() > 0)) 
                done = true;
            else if (tok == '\\') 
                escaped = true;
            else	
                escaped = false;
            
            token.append(tok);
            loc++;
        }
        
        if ( 	! done &&	
        		loc >= currentLine.length() && 
        		this.spanMultipleLinesString && 
        		++this.lineNumber < this.code.size() 
        	) {
        	
        	this.currentLine = this.code.get(this.lineNumber);
        	
        	loc = this.parseString(token, loc, stringDelimiter);
        }
        return loc + 1;
    }

    private boolean ignoreCharacter(char tok)
    {
    	return this.ignorableCharacter.contains("" + tok);
    }

    private boolean isString(char tok)
    {
    	return this.stringToken.contains("" + tok);
    }

    private boolean isComment(char tok)
    {
        return tok == ONE_LINE_COMMENT_CHAR;
    }

    private int getCommentToken(StringBuffer token, int loc)
    {
        while (loc < this.currentLine.length())
        {
            token.append(this.currentLine.charAt(loc++));
        }
        return loc;
    }

    private boolean isIgnorableString(String token)
    {
    	return this.ignorableStmt.contains(token);
    }
}
