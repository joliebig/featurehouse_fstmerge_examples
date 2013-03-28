package com.intersys.classes.SYSTEM;

import com.intersys.classes.Persistent;
import com.intersys.classes.RegisteredObject;
import com.intersys.objects.Database;
import com.intersys.objects.CacheException;
import com.intersys.objects.Id;
import com.intersys.cache.CacheObject;
import com.intersys.cache.SysDatabase;
import com.intersys.cache.Dataholder;


public class Process extends Persistent
{
   private static String CACHE_CLASS_NAME = "%SYSTEM.Process";

   
   public Process(CacheObject ref) throws CacheException
   {
      super(ref);
   }

   
   public Process(Database db) throws CacheException
   {
      super(((SysDatabase) db).newCacheObject(CACHE_CLASS_NAME));
   }

   
   public static RegisteredObject _open(Database db, Id id) throws CacheException
   {
      CacheObject cobj = (((SysDatabase) db).openCacheObject(CACHE_CLASS_NAME, id.toString()));
      return (RegisteredObject) (cobj.newJavaInstance());
   }

   public Dataholder terminate() throws CacheException
   {
      Dataholder[] args = new Dataholder[0];
      Dataholder res=mInternal.runInstanceMethod("Terminate",args,Database.RET_PRIM);
      return res;

   }
}
