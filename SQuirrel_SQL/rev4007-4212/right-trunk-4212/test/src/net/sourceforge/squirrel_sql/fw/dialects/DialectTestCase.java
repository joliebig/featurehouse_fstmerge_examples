
package net.sourceforge.squirrel_sql.fw.dialects;

import java.lang.reflect.Field;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;

import org.hibernate.MappingException;

public class DialectTestCase extends BaseSQuirreLTestCase {

    protected void testAllTypes(HibernateDialect d) {
        try {
            Field[] fields = java.sql.Types.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Integer jdbcType = field.getInt(null);
                testType(jdbcType, d);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    protected void testType(int type, HibernateDialect dialect) {
        try {
            dialect.getTypeName(type);
        } catch (MappingException e) {
            if (type != 0            
                    && type != 70    
                    && type != 1111  
                    && type != 2000  
                    && type != 2001  
                    && type != 2002  
                    && type != 2003  
                    && type != 2006) 
            {
                fail("No mapping for type: "+type+"="+
                        JDBCTypeMapper.getJdbcTypeName(type));
            }
        }
    }

}
