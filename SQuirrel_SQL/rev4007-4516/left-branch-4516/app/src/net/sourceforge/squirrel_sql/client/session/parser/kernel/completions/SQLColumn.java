
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserLogger;


public class SQLColumn extends SQLCompletion
{
    private String name;
    private String qualifier;

    private boolean isRepeatable = true;
    private SQLStatementContext parent;
    private int namePos = NO_POSITION;

    public SQLColumn(SQLStatementContext parent,  int start)
    {
        super(start);
        this.parent = parent;
    }

    public SQLColumn(SQLStatementContext parent, int start, int end)
    {
        super(start);
        this.parent = parent;
        setEndPosition(end);
    }

    public SQLColumn(SQLStatementContext parent)
    {
        super();
        this.parent = parent;
    }

    public void setQualifier(String alias, int pos)
    {
        this.qualifier = alias;
        this.namePos = pos+alias.length()+1;
        setEndPosition(namePos);
        ParserLogger.log("setAlias: s="+startPosition+" e="+endPosition);
    }

    public void setQualifier(String alias)
    {
        this.qualifier = alias;
    }

    public String getQualifier()
    {
        return qualifier;
    }

    public void setColumn(String name, int pos)
    {
        this.name = name;
        this.namePos = pos;
        setEndPosition(pos+name.length()-1);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    
    public boolean hasTable(int position)
    {
        return
              (qualifier == null &&  parent.getStatement().getTable() != null) ||
              (qualifier != null &&
              position >= namePos && position <= endPosition &&
              parent.getStatement().getTableForAlias(qualifier) != null);
    }

    public SQLStatement getStatement()
    {
        return parent.getStatement();
    }

    public String getText()
    {
        String text = qualifier != null ? qualifier+"."+name : name;

        if(hasTextPosition()) {
            int oldDataPos = endPosition - startPosition;
            return text.substring(oldDataPos, text.length());
        } else {
            return text;
        }
    }

    
    protected boolean isConcrete()
    {
        return name != null;
    }

    public String getText(int position)
    {
        return getText(position, name);
    }

    public String getText(int position, String option)
    {
        if(position == endPosition) {
            return option;
        }
        else if(mustReplace(position) || isOther(position)) {
            return qualifier != null ? qualifier+"."+option : option;
        }
        else {
            String text = qualifier != null ? qualifier+"."+option : option;
            int oldDataPos = endPosition - position;
            return text.substring(oldDataPos, text.length());
        }
    }

    
    private boolean isOther(int position)
    {
        return endPosition == NO_LIMIT || position < startPosition || position > endPosition;
    }

    public String[] getCompletions(int position)
    {
        SQLSchema.Table table = null;

        if(qualifier != null) {
            
            table = getStatement().getTableForAlias(qualifier);

            
            if(table == null) table = getStatement().getTable(null, null, qualifier);
        }
        else
            
            table = getStatement().getTable();

        
        if(table != null) {
            String col = null;
            if(name != null && position > namePos) {
                col = position <= endPosition ? name.substring(0, position-namePos) : name;
            }
            String[] result = table.getColumns(col);
            return (col != null && result.length == 1 && result[0].length() == col.length()) ?
                EMPTY_RESULT : result;  
        }
        else
            return EMPTY_RESULT;
    }

    public void setRepeatable(boolean repeatable)
    {
        isRepeatable = repeatable;
    }

    public boolean isRepeatable()
    {
        return isRepeatable;
    }

    public boolean mustReplace(int position)
    {
        return name != null && position >= startPosition && position <= endPosition;
    }
}
