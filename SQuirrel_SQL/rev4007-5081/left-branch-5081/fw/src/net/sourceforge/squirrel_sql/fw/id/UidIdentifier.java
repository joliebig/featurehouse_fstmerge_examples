package net.sourceforge.squirrel_sql.fw.id;

import java.rmi.server.UID;
import java.io.Serializable;

public class UidIdentifier implements IIdentifier, Serializable
{
   private static final long serialVersionUID = -8010376070171401650L;

   public interface IPropertyNames
   {
      String STRING = "string";
   }

   private String _id;

   public UidIdentifier()
   {
      super();
      _id = new UID().toString();
   }

   public boolean equals(Object rhs)
   {
      boolean rc = false;
      if (rhs != null && rhs.getClass().equals(getClass()))
      {
         rc = ((UidIdentifier)rhs).toString().equals(toString());
      }
      return rc;
   }

   public synchronized int hashCode()
   {
      return toString().hashCode();
   }

   public String toString()
   {
      return _id;
   }

   
   public void setString(String value)
   {
      _id = value;
   }
}