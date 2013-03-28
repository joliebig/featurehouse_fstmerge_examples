
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserLogger;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema.Table;

import java.util.List;
import java.util.Collections;


public class SQLTable extends SQLCompletion
{
    public String catalog;
    public String schema;
    public String name;
    public String alias;

    private SQLStatement statement;

    public SQLTable(SQLStatement statement, int start)
    {
        super(start);
        ParserLogger.log("SQLTable: "+start);
        this.statement = statement;
    }

    public SQLTable(SQLStatement statement, int start, int end)
    {
        super(start);
        ParserLogger.log("SQLTable: "+start+" "+end);
        this.statement = statement;
        setEndPosition(end);
    }

    public SQLStatement getStatement()
    {
        return statement;
    }

    public void setCatalog(String catalog, int pos)
    {
        this.catalog = catalog;
        setEndPosition(pos+catalog.length()-1);
    }

    public void setSchema(String schema, int pos)
    {
        this.schema = schema;
        setEndPosition(pos+schema.length()-1);
    }

    public void setName(String name, int pos)
    {
        this.name = name;
        setEndPosition(pos+name.length()-1);
    }

    public void setAlias(String alias, int pos)
    {
        this.alias = alias;
        setEndPosition(pos+alias.length()-1);
    }

    public SQLSchema.Table[] getCompletions(int position)
    {
        String tb = (name != null && position > startPosition) ?
              name.substring(0, position - startPosition) : null;

        List<Table> tables = getStatement().getTables(catalog, schema, tb);
        Collections.sort(tables);
        return tables.toArray(new SQLSchema.Table[tables.size()]);
    }

    
    protected boolean isConcrete()
    {
        return name != null;
    }

    
    public boolean isRepeatable()
    {
        return true;
    }

    public boolean mustReplace(int position)
    {
        return name != null && position >= startPosition && position <= endPosition;
    }

    public String getText(int position, String option)
    {
        return option;
    }
}
