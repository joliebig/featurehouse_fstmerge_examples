




















package net.sf.jabref.export.layout.format; 

import net.sf.jabref.AuthorList; 



public  class  CreateDocBookEditors  extends CreateDocBookAuthors {
	
    

    public String format(String fieldText)
    {
        
        StringBuilder sb = new StringBuilder(100);
        AuthorList al = AuthorList.getAuthorList(fieldText);
        addBody(sb, al, "editor");
        return sb.toString();
        
    }



}
