package net.sourceforge.squirrel_sql.plugins.mssql.util;



import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class DatabaseObjectInfoRenderer extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;

    
    public DatabaseObjectInfoRenderer() {
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        
        if (value instanceof IDatabaseObjectInfo) {
            IDatabaseObjectInfo oi = (IDatabaseObjectInfo) value;

            String gif;
            String simpleName = oi.getSimpleName();
            
            int mssqlType = MssqlIntrospector.getObjectInfoType(oi);
            switch (mssqlType) {
                case MssqlIntrospector.MSSQL_TABLE:
                    gif = "properties.gif";
                    break;
                case MssqlIntrospector.MSSQL_VIEW:
                    gif = "arraypartition_obj.gif";
                    break;
                case MssqlIntrospector.MSSQL_STOREDPROCEDURE:
                    simpleName = simpleName.replaceAll(";0","");
                    gif = "thread_view.gif";
                    break;
                case MssqlIntrospector.MSSQL_UDF:
                    simpleName = simpleName.replaceAll(";1","");
                    gif = "variable_tab.gif";
                    break;
                case MssqlIntrospector.MSSQL_UDT:
                    gif = "type.gif";
                    break;
                default:
                    gif = "error_co.gif";
            }
            
            java.net.URL url = MssqlPlugin.class.getResource("resources/icons/eclipse/" + gif);
            if (url != null) {
                setText(simpleName);
                setIcon(new ImageIcon(url,oi.getDatabaseObjectType().toString()));
                return this;
            }
            else
                return null;
        }
        else
            return null;
    }
    
}
