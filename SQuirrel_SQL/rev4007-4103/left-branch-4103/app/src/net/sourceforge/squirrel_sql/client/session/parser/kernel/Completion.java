
package net.sourceforge.squirrel_sql.client.session.parser.kernel;


public interface Completion 
{
    
    Completion getCompletion(int position);

    
    String getText(int position);

    
    String getText(int position, String option);

    
    boolean hasTextPosition();

    
    boolean isRepeatable();

    
    int getLength();

    
    int getStart();

    
    boolean mustReplace(int position);
}
