package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;


public class DataCache
{
   
   private final static StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DataCache.class);

   
   private final static Class<SQLAlias> SQL_ALIAS_IMPL = SQLAlias.class;

   
   private final static Class<SQLDriver> SQL_DRIVER_IMPL = SQLDriver.class;

   
   private final static ILogger s_log =
               LoggerController.createLogger(DataCache.class);

   
   private final SQLDriverManager _driverMgr;

   
   private final XMLObjectCache _cache = new XMLObjectCache();

   private IApplication _app;

   
   public DataCache(SQLDriverManager driverMgr,
                    File driversFile,
                    File aliasesFile,
                    URL dftDriversURL,
                    IApplication app)
   {
      super();
      if (driverMgr == null)
      {
         throw new IllegalArgumentException("SQLDriverManager == null");
      }
      if (driversFile == null)
      {
         throw new IllegalArgumentException("driversFile == null");
      }
      if (aliasesFile == null)
      {
         throw new IllegalArgumentException("aliasesFile == null");
      }
      if (dftDriversURL == null)
      {
         throw new IllegalArgumentException("dftDriversURL == null");
      }

      _driverMgr = driverMgr;

      _app = app;

      loadDrivers(driversFile, dftDriversURL, NullMessageHandler.getInstance());
      loadAliases(aliasesFile, NullMessageHandler.getInstance());
   }

   
   public void saveDrivers(File file) throws IOException, XMLException
   {
      if (file == null)
      {
         throw new IllegalArgumentException("File == null");
      }

        saveSecure(file, SQL_DRIVER_IMPL);
   }

   
   public void saveAliases(File file) throws IOException, XMLException
   {
      if (file == null)
      {
         throw new IllegalArgumentException("File == null");
      }
        saveSecure(file, SQL_ALIAS_IMPL);
    }

   private void saveSecure(File file, Class<? extends IHasIdentifier> forClass) throws IOException, XMLException
   {
      File tempFile = new File(file.getPath() + "~");
      try
      {
         tempFile.delete();
      }
      catch (Exception e)
      {
      }


      _cache.saveAllForClass(tempFile.getPath(), forClass);
      if (false == tempFile.renameTo(file))
      {
         File doubleTemp = new File(file.getPath() + "~~");
         try
         {
            doubleTemp.delete();
         }
         catch (Exception e)
         {
         }
         File buf = new File(file.getPath());


         if (false == buf.renameTo(doubleTemp))
         {
            throw new IllegalStateException("Cannot rename file " + buf.getPath() + " to " + doubleTemp.getPath() + ". New File will not be saved.");
         }

         try
         {
            tempFile.renameTo(file);
            doubleTemp.delete();
         }
         catch (Exception e)
         {
            doubleTemp.renameTo(file);
         }
      }
   }

   
    public ISQLDriver getDriver(IIdentifier id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("ISQLDriver == null");
        }

        return (ISQLDriver)_cache.get(SQL_DRIVER_IMPL, id);
    }

   
   public void addDriver(ISQLDriver sqlDriver, IMessageHandler messageHandler)
      throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, DuplicateObjectException,
            MalformedURLException
   {
      if (sqlDriver == null)
      {
         throw new IllegalArgumentException("ISQLDriver == null");
      }
        if (messageHandler != null) {
            registerDriver(sqlDriver, messageHandler, true);
        }
      _cache.add(sqlDriver);
   }

   public void removeDriver(ISQLDriver sqlDriver)
   {
      _cache.remove(SQL_DRIVER_IMPL, sqlDriver.getIdentifier());
      _driverMgr.unregisterSQLDriver(sqlDriver);
   }

   public Iterator<ISQLDriver> drivers()
   {
      return _cache.getAllForClass(SQL_DRIVER_IMPL);
   }

   public void addDriversListener(IObjectCacheChangeListener lis)
   {
      _cache.addChangesListener(lis, SQL_DRIVER_IMPL);
   }

   public void removeDriversListener(IObjectCacheChangeListener lis)
   {
      _cache.removeChangesListener(lis, SQL_DRIVER_IMPL);
   }

   public ISQLAlias getAlias(IIdentifier id)
   {
      return (ISQLAlias) _cache.get(SQL_ALIAS_IMPL, id);
   }

   public Iterator<ISQLAlias> aliases()
   {
      return _cache.getAllForClass(SQL_ALIAS_IMPL);
   }

   public void addAlias(ISQLAlias alias) throws DuplicateObjectException
   {
      _cache.add(alias);
   }

   public void removeAlias(SQLAlias alias)
   {
      SchemaInfoCacheSerializer.aliasRemoved(alias);
      _app.getPluginManager().aliasRemoved(alias);
      _cache.remove(SQL_ALIAS_IMPL, alias.getIdentifier());
   }

   public Iterator<ISQLAlias> getAliasesForDriver(ISQLDriver driver)
   {
      ArrayList<ISQLAlias> data = new ArrayList<ISQLAlias>();
      for (Iterator<ISQLAlias> it = aliases(); it.hasNext();)
      {
         ISQLAlias alias = it.next();
         if (driver.equals(getDriver(alias.getDriverIdentifier())))
         {
            data.add(alias);
         }
      }
      return data.iterator();
   }

   public void addAliasesListener(IObjectCacheChangeListener lis)
   {
      _cache.addChangesListener(lis, SQL_ALIAS_IMPL);
   }

   public void removeAliasesListener(IObjectCacheChangeListener lis)
   {
      _cache.removeChangesListener(lis, SQL_ALIAS_IMPL);
   }

   
   private void loadDrivers(File driversFile, URL dftDriversURL,
                            IMessageHandler msgHandler)
   {
      if (driversFile == null)
      {
         throw new IllegalArgumentException("driversFile == null");
      }
      if (dftDriversURL == null)
      {
         throw new IllegalArgumentException("dftDriversURL == null");
      }
      if (msgHandler == null)
      {
         throw new IllegalArgumentException("msgHandler == null");
      }

      try
      {
         try
         {
            _cache.load(driversFile.getPath());
            if (!drivers().hasNext())
            {
               loadDefaultDrivers(dftDriversURL);
            }
            else
            {
               fixupDrivers();
                    mergeDefaultWebsites(dftDriversURL);
            }
         }
         catch (FileNotFoundException ex)
         {
            loadDefaultDrivers(dftDriversURL); 
         }
         catch (Exception ex)
         {
            String msg = s_stringMgr.getString("DataCache.error.loadingdrivers",
                                       driversFile.getPath());
            s_log.error(msg, ex);
            msgHandler.showErrorMessage(msg);
            msgHandler.showErrorMessage(ex, null);
            loadDefaultDrivers(dftDriversURL);
         }
      }
      catch (XMLException ex)
      {
         s_log.error("Error loading drivers", ex);
      }
      catch (IOException ex)
      {
         s_log.error("Error loading drivers", ex);
      }

      for (Iterator<ISQLDriver> it = drivers(); it.hasNext();)
      {
         registerDriver(it.next(), msgHandler, false);
      }
   }

   public SQLAlias createAlias(IIdentifier id)
   {
      return new SQLAlias(id);
   }

   public ISQLDriver createDriver(IIdentifier id)
   {
      return new SQLDriver(id);
   }

    
    public ISQLDriver[] findMissingDefaultDrivers(URL url)
        throws IOException, XMLException
    {
        ISQLDriver[] result = null;
        InputStreamReader isr = new InputStreamReader(url.openStream());
        ArrayList<ISQLDriver> missingDrivers = new ArrayList<ISQLDriver>();
        try
        {
            XMLObjectCache tmp = new XMLObjectCache();
            tmp.load(isr, null, true);

            for (Iterator<ISQLDriver> iter = tmp.getAllForClass(SQL_DRIVER_IMPL); iter.hasNext();) {
                ISQLDriver defaultDriver = iter.next();
                if (!containsDriver(defaultDriver)) {
                    missingDrivers.add(defaultDriver);
                }
            }
        }
        catch (DuplicateObjectException ex)
        {
            
            
            s_log.error("Received an unexpected DuplicateObjectException", ex);
        }
        finally
        {
            isr.close();
        }
        if (missingDrivers.size() > 0) {
            result = missingDrivers.toArray(new ISQLDriver[missingDrivers.size()]);
        }
        return result;
    }

    
    public boolean containsDriver(ISQLDriver driver) {
        boolean result = false;
        for (Iterator<ISQLDriver> iter = _cache.getAllForClass(SQL_DRIVER_IMPL); iter.hasNext();) {
            ISQLDriver cachedDriver = iter.next();
            if (cachedDriver.equals(driver)) {
                result = true;
                break;
            }
        }
        return result;
    }

   public void loadDefaultDrivers(URL url) throws IOException, XMLException
   {
      InputStreamReader isr = new InputStreamReader(url.openStream());
      try
      {
         _cache.load(isr, null, true);
      }
      catch (DuplicateObjectException ex)
      {
         
         
         s_log.error("Received an unexpected DuplicateObjectException", ex);
      }
      finally
      {
         isr.close();
      }
   }

   private void registerDriver(ISQLDriver sqlDriver, IMessageHandler msgHandler, boolean extendedMessaging)
   {
      boolean registrationSucessfully = false;
      try
      {
         _driverMgr.registerSQLDriver(sqlDriver);
         registrationSucessfully = true;
      }
      catch (ClassNotFoundException cnfe)
      {
         if(extendedMessaging)
         {
            Object[] params  = new Object[]
               {
                  sqlDriver.getDriverClassName(),
                  sqlDriver.getName(),
                  cnfe
               };

            String msg = s_stringMgr.getString("DataCache.error.driverClassNotFound", params);
            
            

            s_log.error(msg, cnfe);
            msgHandler.showErrorMessage(msg);
         }
      }
      catch (Throwable th)
      {
         String msg = s_stringMgr.getString("DataCache.error.registerdriver",
                                    sqlDriver.getName());
         s_log.error(msg, th);
         msgHandler.showErrorMessage(msg);
         msgHandler.showErrorMessage(th, null);
      }

      if(extendedMessaging && registrationSucessfully)
      {
         Object[] params  = new Object[]
            {
               sqlDriver.getDriverClassName(),
               sqlDriver.getName(),
            };


         String msg = s_stringMgr.getString("DataCache.msg.driverRegisteredSucessfully", params);
         
         
         msgHandler.showMessage(msg);
      }
   }

   private void loadAliases(File aliasesFile, IMessageHandler msgHandler)
   {
      try
      {
         _cache.load(aliasesFile.getPath());
      }
      catch (FileNotFoundException ignore)
      {
         
      }
      catch (Exception ex)
      {
         String msg = s_stringMgr.getString("DataCache.error.loadingaliases",
                                    aliasesFile.getPath());
         s_log.error(msg, ex);
         msgHandler.showErrorMessage(msg);
         msgHandler.showErrorMessage(ex, null);
      }
   }

   
   @SuppressWarnings("deprecation")
   private void fixupDrivers()
   {
      for (Iterator<ISQLDriver> it = drivers(); it.hasNext();)
      {
         ISQLDriver driver = it.next();
         String[] fileNames = driver.getJarFileNames();
         if (fileNames == null || fileNames.length == 0)
         {
            String fileName = driver.getJarFileName();
            if (fileName != null && fileName.length() > 0)
            {
               driver.setJarFileNames(new String[] {fileName});
               try
               {
                  driver.setJarFileName(null);
               }
               catch (ValidationException ignore)
               {
                  
               }
            }
         }
      }
   }

    
    private void mergeDefaultWebsites(URL defaultDriversUrl)
    {
        InputStreamReader isr = null;
        try
        {
            isr = new InputStreamReader(defaultDriversUrl.openStream());
            XMLObjectCache tmp = new XMLObjectCache();
            tmp.load(isr, null, true);

            for (Iterator<ISQLDriver> iter = tmp.getAllForClass(SQL_DRIVER_IMPL); iter.hasNext();) {

                ISQLDriver defaultDriver = iter.next();
                ISQLDriver cachedDriver = getDriver(defaultDriver.getIdentifier());
                if (cachedDriver != null) {
                    if (cachedDriver.getWebSiteUrl() == null
                            || "".equals(cachedDriver.getWebSiteUrl()))
                    {
                        if (defaultDriver.getWebSiteUrl() != null) {
                            cachedDriver.setWebSiteUrl(defaultDriver.getWebSiteUrl());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            s_log.error("Received an unexpected Exception", ex);
        } finally {
            if (isr != null) {
                try { isr.close(); } catch (Exception e) {}
            }
        }
    }

   public void refreshDriver(ISQLDriver driver, IMessageHandler messageHandler)
   {
      registerDriver(driver, messageHandler, true);
   }
}
