package net.sourceforge.squirrel_sql.plugins.hibernate.mapping;

import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.Hashtable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

public class MappingRoot extends Object
{

   private static ILogger s_log = LoggerController.createLogger(MappingRoot.class);


   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(MappingRoot.class);

   
   private String _toString = s_stringMgr.getString("MappingRoot.toString");

   private Hashtable<String, String> _properties = new Hashtable<String, String>();


   public String toString()
   {
      return _toString;
   }

   public Hashtable<String, String> getMappingProperties()
   {
      return _properties;
   }

   public void clear()
   {
      _properties = new Hashtable<String, String>();
   }

   public void init(HibernateConnection con, HibernateConfiguration cfg)
   {
      clear();

      
      _properties.put(s_stringMgr.getString("MappingRoot.cfgName"), cfg.getName());

      
      _properties.put(s_stringMgr.getString("MappingRoot.classpath"), cfg.classpathAsString());


      try
      {



         Connection sqlCon = con.getSqlConnection();
         DatabaseMetaData md = sqlCon.getMetaData();

         
         _properties.put(s_stringMgr.getString("MappingRoot.url"), md.getURL());

         
         _properties.put(s_stringMgr.getString("MappingRoot.user"), md.getUserName());

         
         _properties.put(s_stringMgr.getString("MappingRoot.driverName"), md.getDriverName());

         
         _properties.put(s_stringMgr.getString("MappingRoot.driverVersion"), md.getDriverVersion());
      }
      catch (Exception e)
      {
         
         s_log.error(s_stringMgr.getString("MappingRoot.connectionErr"), e);
      }
   }
}
