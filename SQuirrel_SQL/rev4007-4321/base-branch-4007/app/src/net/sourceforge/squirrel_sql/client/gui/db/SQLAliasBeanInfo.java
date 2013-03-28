package net.sourceforge.squirrel_sql.client.gui.db;


import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class SQLAliasBeanInfo extends SimpleBeanInfo
{
    
   private static volatile PropertyDescriptor[] s_desc;
   private static Class<SQLAlias> CLAZZ = net.sourceforge.squirrel_sql.client.gui.db.SQLAlias.class;

   private interface IPropNames extends ISQLAlias.IPropertyNames
   {
      
   }

   public SQLAliasBeanInfo() throws IntrospectionException
   {
      super();
      if (s_desc == null)
      {
         s_desc = new PropertyDescriptor[]
            {
               new PropertyDescriptor(IPropNames.ID, CLAZZ, "getIdentifier", "setIdentifier"),
               new PropertyDescriptor(IPropNames.NAME, CLAZZ, "getName", "setName"),
               new PropertyDescriptor(IPropNames.URL, CLAZZ, "getUrl", "setUrl"),
               new PropertyDescriptor(IPropNames.USER_NAME, CLAZZ, "getUserName", "setUserName"),
               new PropertyDescriptor(IPropNames.DRIVER, CLAZZ, "getDriverIdentifier", "setDriverIdentifier"),
               new PropertyDescriptor(IPropNames.USE_DRIVER_PROPERTIES, CLAZZ, "getUseDriverProperties", "setUseDriverProperties"),
               new PropertyDescriptor(IPropNames.DRIVER_PROPERTIES, CLAZZ, "getDriverPropertiesClone", "setDriverProperties"),
               new PropertyDescriptor(IPropNames.PASSWORD, CLAZZ, "getPassword", "setPassword"),
               new PropertyDescriptor(IPropNames.AUTO_LOGON, CLAZZ, "isAutoLogon", "setAutoLogon"),
               new PropertyDescriptor(IPropNames.CONNECT_AT_STARTUP, CLAZZ, "isConnectAtStartup", "setConnectAtStartup"),
               new PropertyDescriptor(IPropNames.SCHEMA_PROPERTIES, CLAZZ, "getSchemaProperties", "setSchemaProperties")
            };
      }
   }

   public PropertyDescriptor[] getPropertyDescriptors()
   {
      return s_desc;
   }
}

