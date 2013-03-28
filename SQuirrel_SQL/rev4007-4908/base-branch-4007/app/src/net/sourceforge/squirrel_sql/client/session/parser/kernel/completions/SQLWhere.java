
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;



public class SQLWhere extends SQLCompletion implements SQLStatementContext
{
    private SQLStatement statement;
    private List<Completion> children = new ArrayList<Completion>();

    public SQLWhere(SQLStatement statement, int startPosition)
    {
        super(startPosition);
        this.statement = statement;
        setEndPosition(NO_LIMIT);
    }

    public void setEndPosition(int position)
    {
        statement.setEndPosition(position);
        super.setEndPosition(position);
    }

    public Completion getCompletion(int position)
    {
        if(super.getCompletion(position) != null) {
            Iterator<Completion> it = children.iterator();
            while(it.hasNext()) {
                Completion comp = it.next();
                if((comp = comp.getCompletion(position)) != null)
                    return comp;
            }
            SQLColumn col = new SQLColumn(this, position);
            col.setRepeatable(false);
            return col;
        }
        return null;
    }

    public SQLStatement getStatement()
    {
        return statement;
    }

    public void setSqlSchema(SQLSchema schema)
    {
        
    }

    public void addContext(SQLStatementContext context)
    {
        context.setSqlSchema(statement);
        children.add(context);
    }

    public void addColumn(SQLColumn column)
    {
        children.add(column);
    }
}
