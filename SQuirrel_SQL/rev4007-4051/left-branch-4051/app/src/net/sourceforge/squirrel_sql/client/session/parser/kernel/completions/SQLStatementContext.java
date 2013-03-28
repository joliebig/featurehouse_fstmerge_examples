
package net.sourceforge.squirrel_sql.client.session.parser.kernel.completions;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.Completion;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;



public interface SQLStatementContext extends Completion
{
    SQLStatement getStatement();
    void setSqlSchema(SQLSchema schema);
    void addContext(SQLStatementContext context);
    void addColumn(SQLColumn column);
}
