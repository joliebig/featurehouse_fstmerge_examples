package net.sourceforge.squirrel_sql.client.gui.db;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;


@SuppressWarnings("serial")
public class SQLAlias implements Cloneable, Serializable, ISQLAliasExt, Comparable<Object>
{
    
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SQLAlias.class);


   private interface IStrings
   {
      String ERR_BLANK_NAME = s_stringMgr.getString("SQLAlias.error.blankname");
      String ERR_BLANK_DRIVER = s_stringMgr.getString("SQLAlias.error.blankdriver");
      String ERR_BLANK_URL = s_stringMgr.getString("SQLAlias.error.blankurl");
   }

   private interface IPropNames extends ISQLAlias.IPropertyNames
   {
      
   }

   
   private IIdentifier _id;

   
   private String _name;

   
   private IIdentifier _driverId;

   
   private String _url;

   
   private String _userName;

   
   private String _password;

   
   private boolean _autoLogon;

   
   private boolean _connectAtStartup;

   
   private boolean _useDriverProperties = false;

   
   private SQLDriverPropertyCollection _driverProps = new SQLDriverPropertyCollection();

   
   private transient PropertyChangeReporter _propChgReporter;

   private SQLAliasSchemaProperties _schemaProperties = new SQLAliasSchemaProperties();

   
   public SQLAlias()
   {
   }

   
   public SQLAlias(IIdentifier id)
   {
      _id = id;
      _name = "";
      _driverId = null;
      _url = "";
      _userName = "";
      _password = "";
   }

   
   public synchronized void assignFrom(SQLAlias rhs, boolean withIdentifier)
      throws ValidationException
   {
      if(withIdentifier)
      {
         setIdentifier(rhs.getIdentifier());
      }
      
      setName(rhs.getName());
      setDriverIdentifier(rhs.getDriverIdentifier());
      setUrl(rhs.getUrl());
      setUserName(rhs.getUserName());
      setPassword(rhs.getPassword());
      setAutoLogon(rhs.isAutoLogon());
      setUseDriverProperties(rhs.getUseDriverProperties());
      setDriverProperties(rhs.getDriverPropertiesClone());
      _schemaProperties = 
          (SQLAliasSchemaProperties) Utilities.cloneObject(rhs._schemaProperties, 
                                                           getClass().getClassLoader());
   }

   
   public boolean equals(Object rhs)
   {
      boolean rc = false;
      if (rhs != null && rhs.getClass().equals(getClass()))
      {
         rc = ((ISQLAlias)rhs).getIdentifier().equals(getIdentifier());
      }
      return rc;
   }




















   
   public synchronized int hashCode()
   {
      return getIdentifier().hashCode();
   }

   
   public String toString()
   {
      return getName();
   }

   
   public int compareTo(Object rhs)
   {
      return _name.compareTo(((ISQLAlias)rhs).getName());
   }

   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().removePropertyChangeListener(listener);
   }

   
   public synchronized boolean isValid()
   {
      return _name != null
                 && _name.length() > 0 
                 && _driverId != null
                 && _url != null
                 && _url.length() > 0;
   }

   public IIdentifier getIdentifier()
   {
      return _id;
   }

   public String getName()
   {
      return _name;
   }

   public IIdentifier getDriverIdentifier()
   {
      return _driverId;
   }

   public String getUrl()
   {
      return _url;
   }

   public String getUserName()
   {
      return _userName;
   }

   
   public String getPassword()
   {
      return _password;
   }

   
   public void setPassword(String password)
   {
      String data = getString(password);
      if (_password != data)
      {
         final String oldValue = _password;
         _password = data;
         getPropertyChangeReporter().firePropertyChange(IPropNames.PASSWORD,
                                    oldValue, _password);
      }
   }

   
   public boolean isAutoLogon()
   {
      return _autoLogon;
   }

   
   public void setAutoLogon(boolean value)
   {
      if (_autoLogon != value)
      {
         _autoLogon = value;
         getPropertyChangeReporter().firePropertyChange(IPropNames.AUTO_LOGON,
                                    !_autoLogon, _autoLogon);
      }
   }

   
   public boolean isConnectAtStartup()
   {
      return _connectAtStartup;
   }

   
   public void setConnectAtStartup(boolean value)
   {
      if (_connectAtStartup != value)
      {
         _connectAtStartup = value;
         getPropertyChangeReporter().firePropertyChange(IPropNames.CONNECT_AT_STARTUP,
                                    !_connectAtStartup, _connectAtStartup);
      }
   }

   
   public boolean getUseDriverProperties()
   {
      return _useDriverProperties;
   }

   public void setIdentifier(IIdentifier id)
   {
      _id = id;
   }

   public void setName(String name) throws ValidationException
   {
      String data = getString(name);
      if (data.length() == 0)
      {
         throw new ValidationException(IStrings.ERR_BLANK_NAME);
      }
      if (_name != data)
      {
         final String oldValue = _name;
         _name = data;
         getPropertyChangeReporter().firePropertyChange(IPropNames.NAME,
                                    oldValue, _name);
      }
   }

   public void setDriverIdentifier(IIdentifier data)
      throws ValidationException
   {
      if (data == null)
      {
         throw new ValidationException(IStrings.ERR_BLANK_DRIVER);
      }
      if (_driverId != data)
      {
         final IIdentifier oldValue = _driverId;
         _driverId = data;
         getPropertyChangeReporter().firePropertyChange(IPropNames.DRIVER,
                                    oldValue, _driverId);
      }
   }

   public void setUrl(String url) throws ValidationException
   {
      String data = getString(url);
      if (data.length() == 0)
      {
         throw new ValidationException(IStrings.ERR_BLANK_URL);
      }
      if (_url != data)
      {
         final String oldValue = _url;
         _url = data;
         getPropertyChangeReporter().firePropertyChange(IPropNames.URL,
                                       oldValue, _url);
      }
   }

   public void setUserName(String userName)
   {
      String data = getString(userName);
      if (_userName != data)
      {
         final String oldValue = _userName;
         _userName = data;
         getPropertyChangeReporter().firePropertyChange(IPropNames.USER_NAME,
                                    oldValue, _userName);
      }
   }

   public void setUseDriverProperties(boolean value)
   {
      if (_useDriverProperties != value)
      {
         final boolean oldValue = _useDriverProperties;
         _useDriverProperties = value;
         getPropertyChangeReporter().firePropertyChange(IPropNames.USE_DRIVER_PROPERTIES,
                                    oldValue, _useDriverProperties);
      }
   }

   
   public synchronized SQLDriverPropertyCollection getDriverPropertiesClone()
   {
      final int count = _driverProps.size();
      SQLDriverProperty[] newar = new SQLDriverProperty[count];
      for (int i = 0; i < count; ++i)
      {
         newar[i] = (SQLDriverProperty)_driverProps.getDriverProperty(i).clone();
      }
      SQLDriverPropertyCollection coll = new SQLDriverPropertyCollection();
      coll.setDriverProperties(newar);
      return coll;
   }

   public synchronized void setDriverProperties(SQLDriverPropertyCollection value)
   {
      _driverProps.clear();
      if (value != null)
      {
         synchronized (value)
         {
            final int count = value.size();
            SQLDriverProperty[] newar = new SQLDriverProperty[count];
            for (int i = 0; i < count; ++i)
            {
               newar[i] = (SQLDriverProperty)value.getDriverProperty(i).clone();

            }
            _driverProps.setDriverProperties(newar);
         }
      }
   }

   private synchronized PropertyChangeReporter getPropertyChangeReporter()
   {
      if (_propChgReporter == null)
      {
         _propChgReporter = new PropertyChangeReporter(this);
      }
      return _propChgReporter;
   }

   private String getString(String data)
   {
      return data != null ? data.trim() : "";
   }


   public SQLAliasSchemaProperties getSchemaProperties()
   {
      return _schemaProperties;      
   }

   public void setSchemaProperties(SQLAliasSchemaProperties schemaProperties)
   {
      _schemaProperties = schemaProperties;
   }

}
