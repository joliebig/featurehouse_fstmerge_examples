
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;


public interface SQLSchema
{
    
    public class Table implements Cloneable, Comparable<Table>
    {
        public final String catalog;
        public final String schema;
        public final String name;
        public final String compositeName;
        public transient String alias;

        private SQLDatabaseMetaData dmd;
        private String[] columns;

        public static String createCompositeName(String catalog, String schema, String name)
        {

            StringBuffer sbuf = new StringBuffer();
            if(catalog != null) {
                sbuf.append(catalog);
                sbuf.append(".");
            }
            if(schema != null) {
                sbuf.append(schema);
                sbuf.append(".");
            }
            sbuf.append(name);
            return sbuf.toString();
        }

        public Table clone(String alias)
        {
            try {
                Table newTable = (Table)super.clone();
                newTable.alias = alias;
                return newTable;
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }

        public Table(String catalog, String schema, String name, SQLDatabaseMetaData dmd)
        {
            this.catalog = catalog;
            this.schema = schema;
            this.name = name;
            this.compositeName = createCompositeName(catalog, schema, name);
            this.dmd = dmd;
        }

        public Table(String catalog, String schema, String name)
        {
            this(catalog, schema, name, null);
        }

        public Table(String schema, String name)
        {
            this(null, schema, name, null);
        }

        public Table(String name)
        {
            this(null, null, name, null);
        }

        public void setColumns(String[] columns)
        {
            this.columns = columns;
        }

        public void setColumns(List<String> columns)
        {
            this.columns = columns.toArray(new String[columns.size()]);
        }

        public String[] getColumns()
        {
            if(columns == null) loadColumns();
            return columns;
        }

        public String[] getColumns(String prefix)
        {
            String[] cols = getColumns();
            if(prefix == null) return cols;

            List<String> list = new ArrayList<String>(cols.length);
            for(int i=0; i<cols.length; i++)
                if(cols[i].startsWith(prefix)) list.add(cols[i]);
            return list.toArray(new String[list.size()]);
        }

        public String getCompositeName()
        {
            return compositeName;
        }

        public boolean hasAlias()
        {
            return alias != null;
        }

        public String toString()
        {
            return alias == null ? compositeName : compositeName + " "+alias;
        }

        public int hashCode()
        {
            return compositeName.hashCode();
        }

        public boolean equals(Object other)
        {
            if(other instanceof Table) {
                Table o = (Table)other;
                if(o.compositeName.equals(compositeName)) {
                    return alias == o.alias || alias != null && alias.equals(o.alias);
                }
                else return false;
            }
            else return false;
        }

        public boolean equals(String catalog, String schema, String table)
        {
            return
                  (this.catalog == catalog ||
                      (this.catalog != null && catalog != null && this.catalog.equals(catalog))) &&
                  (this.schema == schema ||
                      (this.schema != null && schema != null && this.schema.equals(schema))) &&
                  (this.name.equals(table));
        }

        public boolean matches(String catalog, String schema, String name)
        {
            return
                  (catalog == null ||
                      (this.catalog != null && this.catalog.startsWith(catalog))) &&
                  (schema == null ||
                      (this.schema != null && this.schema.startsWith(schema))) &&
                  (name == null || this.name.startsWith(name));
        }

        public int compareTo(Table other)
        {
            if(alias != null) {
                return other.alias != null ? alias.compareTo(other.alias) : -1;
            }
            else if(other.alias != null) {
                return 1;
            }
            return compositeName.compareTo(other.compositeName);
        }

        protected void loadColumns()
        {
            try {
                List<String> cols = new ArrayList<String>();
                TableColumnInfo[] infos = dmd.getColumnInfo(catalog, schema, name);
                for (int i = 0; i < infos.length; i++) {
                    cols.add(infos[i].getColumnName());
                }
                setColumns(cols);
            }
            catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    
    Table getTable(String catalog, String schema, String name);

    
    List<Table> getTables(String catalog, String schema, String name);

    
    Table getTableForAlias(String alias);
}
