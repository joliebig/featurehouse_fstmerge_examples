package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class ObjFilterMatcher extends FilterMatcher
{
   public ObjFilterMatcher(SessionProperties properties)
   {
      super(properties.getObjectFilterInclude(), properties.getObjectFilterExclude());
   }

   
   public ObjFilterMatcher(String simpleObjectName)
   {
      super(simpleObjectName, null);
   }

   
   public ObjFilterMatcher()
   {
      super(null, null);
   }
}
