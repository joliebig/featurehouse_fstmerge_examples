
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;


import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLCompletion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;

import java.util.*;



public class SQLStatement extends SQLCompletion implements SQLSchema, SQLStatementContext
{
    private SortedSet<Completion> children;
    protected SQLSchema sqlSchema;
    private static final List<Completion> EMPTY_LIST = 
        new ArrayList<Completion>();

    public SQLStatement(int start)
    {
        super(start);
    }

    
    public Completion getCompletion(int position)
    {
        if(isEnclosed(position)) {
            Iterator<Completion> it = getChildren();
            while(it.hasNext()) {
                Completion c = it.next().getCompletion(position);
                if(c != null) return c;
            }
        }
        return null;
    }

    public void setSqlSchema(SQLSchema schema)
    {
        if(schema == this) throw new RuntimeException("internal error: recursive schema");
        this.sqlSchema = schema;
    }

    protected void addChild(Completion child)
    {
        if(children == null) children = new TreeSet(new ChildComparator());
        children.add(child);
    }

    public void addContext(SQLStatementContext context)
    {
        context.setSqlSchema(this);
        addChild(context);
    }

    public void setEndPosition(int offset)
    {
        super.setEndPosition(offset);
        if(sqlSchema instanceof SQLStatement)
            ((SQLStatement)sqlSchema).setEndPosition(offset);
    }

    public void addTable(SQLTable table)
    {
        addChild(table);
    }

    public boolean setTable(SQLTable table)
    {
        return setTable(table.catalog, table.schema, table.name, table.alias);
    }

    
    public boolean setTable(String catalog, String schema, String name, String alias)
    {
        return sqlSchema.getTable(catalog, schema, name) != null;
    }

    public Table getTable(String catalog, String schema, String name)
    {
        return sqlSchema.getTable(catalog, schema, name);
    }

    public List<Table> getTables(String catalog, String schema, String name)
    {
        return sqlSchema.getTables(catalog, schema, name);
    }

    public Table getTableForAlias(String alias)
    {
        return sqlSchema.getTableForAlias(alias);
    }

    public void addColumn(SQLColumn column)
    {
        addChild(column);
    }

    public SQLStatement getStatement()
    {
        return this;
    }

    protected Iterator<Completion> getChildren()
    {
        return children != null ? children.iterator() : EMPTY_LIST.iterator();
    }

    
    public Table getTable()
    {
        return null;
    }
}
