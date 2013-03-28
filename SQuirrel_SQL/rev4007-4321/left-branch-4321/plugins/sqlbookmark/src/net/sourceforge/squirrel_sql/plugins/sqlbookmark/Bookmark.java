

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.Serializable;


public class Bookmark
{

   
   protected String _name;

   private String _description;
   
   protected String _sql;
   private String _toString;

   public Bookmark()
   {
      this(null, null, null);
   }

   public Bookmark(String name, String description, String sql)
   {
      _name = name;
      _description = description;
      setSql(sql);
      initToString();
   }

   private void initToString()
   {
      String name = null == _name ? "(missing name)" : _name;
      String description = null == _description ? "(missing description)" : _description;
      _toString = "(" + name + ")   " + description;
   }

   public String getName()
   {
      return _name;
   }

   public String getDescription()
   {
      return _description;
   }

   public String getSql()
   {
      return _sql;
   }

   public void setName(String name)
   {
      this._name = name;
      initToString();
   }

   public void setDescription(String description)
   {
      this._description = description;
      initToString();
   }

   public void setSql(String sql)
   {
      _sql = sql;








   }

   public String toString()
   {
      return _toString;
   }
}
