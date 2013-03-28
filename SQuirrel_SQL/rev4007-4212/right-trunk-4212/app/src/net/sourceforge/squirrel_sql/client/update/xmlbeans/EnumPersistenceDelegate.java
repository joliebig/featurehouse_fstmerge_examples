
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.beans.BeanInfo;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;

public class EnumPersistenceDelegate extends DefaultPersistenceDelegate {

   private static EnumPersistenceDelegate INSTANCE = new EnumPersistenceDelegate();

   public static void installFor(Enum<?>[] values) {
     Class<? extends Enum> declaringClass = values[0].getDeclaringClass();
     installFor(declaringClass);

     for (Enum<?> e : values)
       if (e.getClass() != declaringClass)
         installFor(e.getClass());
   }

   public static void installFor(Class<? extends Enum> enumClass) {
     try {
       BeanInfo info = Introspector.getBeanInfo( enumClass );
       info.getBeanDescriptor().setValue( "persistenceDelegate", INSTANCE );
     } catch( IntrospectionException exception ) {
       throw new RuntimeException("Unable to persist enumerated type "+enumClass, exception);
     }
   }

   protected Expression instantiate(Object oldInstance, Encoder out) {
     Enum e = (Enum)oldInstance;
     return new Expression(Enum.class,
                           "valueOf",
                           new Object[] { e.getDeclaringClass(),
                                          e.name() });
   }
}
