
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.io.Serializable;
import java.util.Comparator;



public abstract class SQLCompletion implements Completion
{
    public static final int NO_POSITION = -1;
    public static final int NO_LIMIT = 99999;

    public static final String[] EMPTY_RESULT = new String[0];

    protected int startPosition=NO_POSITION, endPosition=NO_LIMIT;


    protected SQLCompletion(int startPosition)
    {
        this.startPosition = startPosition;
    }

    protected SQLCompletion() {}

    public Completion getCompletion(int position)
    {
        return isEnclosed(position) ? this : null;
    }

    
    protected boolean isConcrete()
    {
        return true;
    }

    public void setEndPosition(int position)
    {
        this.endPosition = position;
    }

    
    public String getText(int position)
    {
        throw new UnsupportedOperationException("completion not available");
    }

    
    public String getText(int position, String options)
    {
        throw new UnsupportedOperationException("completion not available");
    }

    public boolean hasTextPosition()
    {
        return startPosition != NO_POSITION && endPosition != NO_POSITION;
    }

    public boolean isRepeatable()
    {
        return false;
    }

    public int getLength()
    {
        return endPosition - startPosition + 1;
    }

    public int getStart()
    {
        return startPosition;
    }


    public boolean mustReplace(int position)
    {
        return false;
    }

    
    protected boolean isEnclosed(int position)
    {
        return position >= startPosition && position <= endPosition;
    }

    
    public static class ChildComparator implements Comparator<SQLCompletion>,
                                                   Serializable
    {
        private static final long serialVersionUID = -8912522485515591605L;

        public int compare(SQLCompletion c1, SQLCompletion c2)
        {
            if(c1.isConcrete() == c2.isConcrete()) {
                return c2.startPosition - c1.startPosition ;
            } else {
                return c1.isConcrete() ? -1 : 1;
            }
        }
    }
}
