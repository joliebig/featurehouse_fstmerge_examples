package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class DatabaseObjectInfo implements IDatabaseObjectInfo, Serializable
{
   
   public interface IPropertyNames
   {
      
      String CATALOG_NAME = "catalogName";

      
      String SCHEMA_NAME = "schemaName";

      
      String SIMPLE_NAME = "simpleName";

      
      String QUALIFIED_NAME = "qualifiedName";
   }

   static final long serialVersionUID = -7138016566181091160L;


   private final String _catalog;

   
   private final String _schema;

   
   private final String _simpleName;

   
   private final String _qualifiedName;

   
   private DatabaseObjectType _dboType = DatabaseObjectType.OTHER;

   public DatabaseObjectInfo(String catalog, String schema, String simpleName,
                             DatabaseObjectType dboType, ISQLDatabaseMetaData md)
   {
      super();
      if (dboType == null)
      {
         throw new IllegalArgumentException("DatabaseObjectType == null");
      }
      if (md == null)
      {
         throw new IllegalArgumentException("SQLDatabaseMetaData == null");
      }

      _catalog = catalog;
      _schema = schema;
      _simpleName = simpleName;
      _qualifiedName = generateQualifiedName(md);
      _dboType = dboType;
   }

   
   public DatabaseObjectInfo(String catalog, String schema, String simpleName) {
       _catalog = catalog;
       _schema = schema;
       _simpleName = simpleName;
       _qualifiedName = simpleName;
   }
   
   public String toString()
   {
      return getSimpleName();
   }

   public String getCatalogName()
   {
      return _catalog;
   }


   public String getSchemaName()
   {
      return _schema;
   }

   public String getSimpleName()
   {
      return _simpleName;
   }

   public String getQualifiedName()
   {
      return _qualifiedName;
   }

   public DatabaseObjectType getDatabaseObjectType()
   {
      return _dboType;
   }

   protected String generateQualifiedName(ISQLConnection conn)
   {
      return generateQualifiedName(conn.getSQLMetaData());
   }
   
   
   private String getInformixQualifiedName() {
       StringBuffer result = new StringBuffer();
       if (_catalog != null && _schema != null) {
           result.append(_catalog);
           result.append(":");
           result.append("\"");
           result.append(_schema);
           result.append("\"");
           result.append(".");
       }
       result.append(_simpleName);
       return result.toString();
   }
   
   protected String generateQualifiedName(final ISQLDatabaseMetaData md)
   {
      String catSep = null;
      String identifierQuoteString = null;
      boolean supportsSchemasInDataManipulation = false;
      boolean supportsCatalogsInDataManipulation = false;

      
      if (DialectFactory.isInformix(md)) {
          return getInformixQualifiedName();
      }
      
      try
      {
         supportsSchemasInDataManipulation = md.supportsSchemasInDataManipulation();
      }
      catch (SQLException ignore)
      {
         
      }
      try
      {
          supportsCatalogsInDataManipulation = md.supportsCatalogsInDataManipulation();
      }
      catch (SQLException ignore)
      {
         
      }
      
      try
      {


            catSep = md.getCatalogSeparator();

      }
      catch (SQLException ignore)
      {
         
      }
      
      
      if (StringUtilities.isEmpty(catSep))
      {
          catSep = ".";
      }
    		  
      try
      {
         identifierQuoteString = md.getIdentifierQuoteString();
         if (identifierQuoteString != null
            && identifierQuoteString.equals(" "))
         {
            identifierQuoteString = null;
         }
      }
      catch (SQLException ignore)
      {
         
      }

      if (DialectFactory.isSyBase(md)) {
         identifierQuoteString = 
            checkSybaseIdentifierQuoteString(md, identifierQuoteString);         
      }
            
      
      StringBuffer buf = new StringBuffer();
      if (supportsCatalogsInDataManipulation
            && !StringUtilities.isEmpty(_catalog))	  
      {
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(_catalog);
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(catSep);
      }

      if (supportsSchemasInDataManipulation && _schema != null
         && _schema.length() > 0)
      {
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(_schema);
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         
         buf.append(catSep);
      }

      if (identifierQuoteString != null)
      {
         buf.append(identifierQuoteString);
      }
      String quoteExpandedName = SQLUtilities.quoteIdentifier(_simpleName);
      buf.append(quoteExpandedName);
      if (identifierQuoteString != null)
      {
         buf.append(identifierQuoteString);
      }
      return buf.toString();
   }

   
   private String checkSybaseIdentifierQuoteString(
         final ISQLDatabaseMetaData md, final String quoteString) 
   {
      String result = quoteString;
      String productName = null;
      CharSequence sybaseTwelveVersionId = "12.";
      try {
         productName = md.getDatabaseProductVersion();
      } catch (SQLException e) {
         
      }
      if (productName != null) {
         if (productName.contains(sybaseTwelveVersionId)) {
            result = "";
         }
      }
      return result;
   }
   
   public boolean equals(Object obj)
   {
      if (obj == null) {
          return false;
      }
      if (obj.getClass() == this.getClass())
      {
         DatabaseObjectInfo info = (DatabaseObjectInfo) obj;
         if ((info._catalog == null && _catalog == null)
            || ((info._catalog != null && _catalog != null)
               && info._catalog.equals(_catalog)))
         {
            if ((info._qualifiedName == null && _qualifiedName == null)
               || ((info._qualifiedName != null && _qualifiedName != null)
                  && info._qualifiedName.equals(_qualifiedName)))
            {
               if ((info._schema == null && _schema == null)
                  || ((info._schema != null && _schema != null)
                     && info._schema.equals(_schema)))
               {
                  return (
                     (info._simpleName == null && _simpleName == null)
                        || ((info._simpleName != null
                           && _simpleName != null)
                           && info._simpleName.equals(_simpleName)));
               }

            }
         }
      }
      return false;
   }

   public int hashCode()
   {
      return _qualifiedName.hashCode();
   }

   public int compareTo(IDatabaseObjectInfo o)
   {
      return _qualifiedName.compareTo(o.getQualifiedName());
   }
}
