using System; 
using System.Resources; 
using System.Collections; 
using System.Collections.Specialized; 
using System.Xml; 
using System.Xml.Serialization; 
using System.Configuration; 
using System.Globalization; namespace  Microsoft.ApplicationBlocks.ExceptionManagement {
	
 public enum  ExceptionManagementMode 
 {
  Off,
  On
 } 
 public enum  PublisherMode 
 {
  Off,
  On
 } 
 public enum  PublisherFormat 
 {
  Exception,
  Xml
 } 
 public class  ExceptionManagementSettings {
		
  private  ExceptionManagementMode mode = ExceptionManagementMode.On;
 
  private  ArrayList publishers = new ArrayList();
 
  public  ExceptionManagementMode Mode
  {
   get
   {
    return mode;
   }
   set
   {
    mode = value;
   }
  }
 
  public  ArrayList Publishers
  {
   get
   {
    return publishers;
   }
  }
 
  public  void AddPublisher(PublisherSettings publisher)
  {
   publishers.Add(publisher);
  }

	}
	
 public class  PublisherSettings {
		
  private  PublisherMode mode = PublisherMode.On;
 
  private  PublisherFormat exceptionFormat = PublisherFormat.Exception;
 
  private  string assemblyName;
 
  private  string typeName;
 
  private  TypeFilter includeTypes;
 
  private  TypeFilter excludeTypes;
 
  private  NameValueCollection otherAttributes = new NameValueCollection();
 
  public  PublisherMode Mode
  {
   get
   {
    return mode;
   }
   set
   {
    mode = value;
   }
  }
 
  public  PublisherFormat ExceptionFormat
  {
   get
   {
    return exceptionFormat;
   }
   set
   {
    exceptionFormat = value;
   }
  }
 
  public  string AssemblyName
  {
   get
   {
    return assemblyName;
   }
   set
   {
    assemblyName = value;
   }
  }
 
  public  string TypeName
  {
   get
   {
    return typeName;
   }
   set
   {
    typeName = value;
   }
  }
 
  public  TypeFilter IncludeTypes
  {
   get
   {
    return includeTypes;
   }
   set
   {
    includeTypes = value;
   }
  }
 
  public  TypeFilter ExcludeTypes
  {
   get
   {
    return excludeTypes;
   }
   set
   {
    excludeTypes = value;
   }
  }
 
  public  bool IsExceptionFiltered(Type exceptionType)
  {
   if (excludeTypes == null) return false;
   if (MatchesFilter(exceptionType, excludeTypes))
   {
    if (MatchesFilter(exceptionType, includeTypes))
    {
     return false;
    }
    else
    {
     return true;
    }
   }
   else
   {
    return false;
   }
  }
 
  private  bool MatchesFilter(Type type, TypeFilter typeFilter)
  {
   TypeInfo typeInfo;
   if (typeFilter == null) return false;
   if (typeFilter.AcceptAllTypes) return true;
   for (int i=0;i<typeFilter.Types.Count;i++)
   {
    typeInfo = (TypeInfo)typeFilter.Types[i];
    if (typeInfo.ClassType.Equals(type)) return true;
                if (typeInfo.IncludeSubClasses == true && typeInfo.ClassType.IsAssignableFrom(type)) return true;
   }
   return false;
  }
 
  public  NameValueCollection OtherAttributes
  {
   get
   {
    return otherAttributes;
   }
  }
 
  public  void AddOtherAttributes(string name, string value)
  {
   otherAttributes.Add(name, value);
  }

	}
	
 public class  TypeFilter {
		
  private  bool acceptAllTypes = false;
 
  private  ArrayList types = new ArrayList();
 
  public  bool AcceptAllTypes
  {
   get
   {
    return acceptAllTypes;
   }
   set
   {
    acceptAllTypes = value;
   }
  }
 
  public  ArrayList Types
  {
   get
   {
    return types;
   }
  }

	}
	
 public class  TypeInfo {
		
  private  Type classType;
 
  private  bool includeSubClasses = false;
 
  public  bool IncludeSubClasses
  {
   get
   {
    return includeSubClasses;
   }
   set
   {
    includeSubClasses = value;
   }
  }
 
  public  Type ClassType
  {
   get
   {
    return classType;
   }
   set
   {
    classType = value;
   }
  }

	}
	
 public class  ExceptionManagerSectionHandler  : IConfigurationSectionHandler {
		
  public  ExceptionManagerSectionHandler()
  {
   resourceManager = new ResourceManager(this.GetType().Namespace + ".ExceptionManagerText",this.GetType().Assembly);
  }
 
  private readonly static  char EXCEPTION_TYPE_DELIMITER = Convert.ToChar(";");
 
  private  const string EXCEPTIONMANAGEMENT_MODE = "mode"; 
  private  const string PUBLISHER_NODENAME = "publisher"; 
  private  const string PUBLISHER_MODE = "mode"; 
  private  const string PUBLISHER_ASSEMBLY = "assembly"; 
  private  const string PUBLISHER_TYPE = "type"; 
  private  const string PUBLISHER_EXCEPTIONFORMAT = "exceptionFormat"; 
  private  const string PUBLISHER_INCLUDETYPES = "include"; 
  private  const string PUBLISHER_EXCLUDETYPES = "exclude"; 
  private  ResourceManager resourceManager;
 
  public  object Create(object parent,object configContext,XmlNode section)
  {
   try
   {
    ExceptionManagementSettings settings = new ExceptionManagementSettings();
    if (section == null) return settings;
    XmlNode currentAttribute;
    XmlAttributeCollection nodeAttributes = section.Attributes;
    currentAttribute = nodeAttributes.RemoveNamedItem(EXCEPTIONMANAGEMENT_MODE);
    if (currentAttribute != null && currentAttribute.Value.ToUpper(CultureInfo.InvariantCulture) == "OFF")
    {
     settings.Mode = ExceptionManagementMode.Off;
    }
    PublisherSettings publisherSettings;
    foreach(XmlNode node in section.ChildNodes)
    {
     if (node.Name == PUBLISHER_NODENAME)
     {
      publisherSettings = new PublisherSettings();
      nodeAttributes = node.Attributes;
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_MODE);
      if (currentAttribute != null && currentAttribute.Value.ToUpper(CultureInfo.InvariantCulture) == "OFF") publisherSettings.Mode = PublisherMode.Off;
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_ASSEMBLY);
      if (currentAttribute != null) publisherSettings.AssemblyName = currentAttribute.Value;
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_TYPE);
      if (currentAttribute != null) publisherSettings.TypeName = currentAttribute.Value;
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_EXCEPTIONFORMAT);
      if (currentAttribute != null && currentAttribute.Value.ToUpper(CultureInfo.InvariantCulture) == "XML") publisherSettings.ExceptionFormat = PublisherFormat.Xml;
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_INCLUDETYPES);
      if (currentAttribute != null)
      {
       publisherSettings.IncludeTypes = LoadTypeFilter(currentAttribute.Value.Split(EXCEPTION_TYPE_DELIMITER));
      }
      currentAttribute = nodeAttributes.RemoveNamedItem(PUBLISHER_EXCLUDETYPES);
      if (currentAttribute != null)
      {
       publisherSettings.ExcludeTypes = LoadTypeFilter(currentAttribute.Value.Split(EXCEPTION_TYPE_DELIMITER));
      }
      for (int i = 0; i < nodeAttributes.Count; i++)
      {
       publisherSettings.AddOtherAttributes(nodeAttributes.Item(i).Name,nodeAttributes.Item(i).Value);
      }
      settings.Publishers.Add(publisherSettings);
     }
    }
    settings.Publishers.TrimToSize();
    return settings;
   }
   catch(Exception exc)
   {
    throw new ConfigurationException(resourceManager.GetString("RES_EXCEPTION_LOADING_CONFIGURATION"), exc, section);
   }
  }
 
  private  TypeFilter LoadTypeFilter(string[] rawFilter)
  {
   TypeFilter typeFilter = new TypeFilter();
   if (rawFilter != null)
   {
    TypeInfo exceptionTypeInfo;
    for (int i=0;i<rawFilter.GetLength(0);i++)
    {
     if (rawFilter[i] == "*")
     {
      typeFilter.AcceptAllTypes = true;
     }
     else
     {
      try
      {
       if (rawFilter[i].Length > 0)
       {
        exceptionTypeInfo = new TypeInfo();
        if (rawFilter[i].Trim().StartsWith("+"))
        {
         exceptionTypeInfo.IncludeSubClasses = true;
         exceptionTypeInfo.ClassType = Type.GetType(rawFilter[i].Trim().TrimStart(Convert.ToChar("+")),true);
        }
        else
        {
         exceptionTypeInfo.IncludeSubClasses = false;
         exceptionTypeInfo.ClassType = Type.GetType(rawFilter[i].Trim(),true);
        }
        typeFilter.Types.Add(exceptionTypeInfo);
       }
      }
      catch(TypeLoadException e)
      {
       ExceptionManager.PublishInternalException(new ConfigurationException(resourceManager.GetString("RES_EXCEPTION_LOADING_CONFIGURATION"), e), null);
      }
     }
    }
   }
   return typeFilter;
  }

	}

}
