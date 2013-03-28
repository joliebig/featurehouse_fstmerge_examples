using System;
using System.Resources;
using System.Reflection;
using System.Collections;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Runtime.Serialization;
using System.Threading;
using System.Collections.Specialized;
using System.Security;
using System.Security.Principal;
using System.Security.Permissions;
namespace Microsoft.ApplicationBlocks.ExceptionManagement
{
 public sealed class ExceptionManager
 {
  private ExceptionManager()
  {
  }
  private const string EXCEPTIONMANAGEMENT_CONFIG_SECTION = "exceptionManagement";
  private readonly static string EXCEPTIONMANAGER_NAME = typeof(ExceptionManager).Name;
  private static ResourceManager resourceManager = new ResourceManager(typeof(ExceptionManager).Namespace + ".ExceptionManagerText",Assembly.GetAssembly(typeof(ExceptionManager)));
  public static void Publish(Exception exception)
  {
   ExceptionManager.Publish(exception, null);
  }
  public static void Publish(Exception exception, NameValueCollection additionalInfo)
  {
   try
   {
    if (null == additionalInfo) additionalInfo = new NameValueCollection();
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".MachineName", Environment.MachineName);
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".MachineName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".MachineName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".TimeStamp", DateTime.Now.ToString());
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".TimeStamp",resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".TimeStamp", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".FullName", Assembly.GetExecutingAssembly().FullName);
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".FullName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".FullName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".AppDomainName", AppDomain.CurrentDomain.FriendlyName);
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".AppDomainName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".AppDomainName", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".ThreadIdentity", Thread.CurrentPrincipal.Identity.Name);
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".ThreadIdentity", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".ThreadIdentity", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    try
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".WindowsIdentity", WindowsIdentity.GetCurrent().Name);
    }
    catch(SecurityException)
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".WindowsIdentity", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_PERMISSION_DENIED"));
    }
    catch
    {
     additionalInfo.Add(EXCEPTIONMANAGER_NAME + ".WindowsIdentity", resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_INFOACCESS_EXCEPTION"));
    }
    if (ConfigurationSettings.GetConfig(EXCEPTIONMANAGEMENT_CONFIG_SECTION) == null)
    {
     PublishToDefaultPublisher(exception, additionalInfo);
    }
    else
    {
     ExceptionManagementSettings config = (ExceptionManagementSettings)ConfigurationSettings.GetConfig(EXCEPTIONMANAGEMENT_CONFIG_SECTION);
     if (config.Mode == ExceptionManagementMode.On)
     {
      if (config.Publishers == null || config.Publishers.Count == 0)
      {
       PublishToDefaultPublisher(exception, additionalInfo);
      }
      else
      {
       foreach(PublisherSettings Publisher in config.Publishers)
       {
        try
        {
         if (Publisher.Mode == PublisherMode.On)
         {
          if (exception == null || !Publisher.IsExceptionFiltered(exception.GetType()))
          {
           PublishToCustomPublisher(exception, additionalInfo, Publisher);
          }
         }
        }
        catch(Exception e)
        {
         PublishInternalException(e,null);
         PublishToDefaultPublisher(exception, additionalInfo);
        }
       }
      }
     }
    }
   }
   catch(Exception e)
   {
    PublishInternalException(e,null);
    PublishToDefaultPublisher(exception, additionalInfo);
   }
  }
  private static void PublishToCustomPublisher(Exception exception, NameValueCollection additionalInfo, PublisherSettings publisher)
  {
   try
   {
    if (publisher.ExceptionFormat == PublisherFormat.Xml)
    {
     IExceptionXmlPublisher XMLPublisher = (IExceptionXmlPublisher)Activate(publisher.AssemblyName, publisher.TypeName);
     XMLPublisher.Publish(SerializeToXml(exception, additionalInfo),publisher.OtherAttributes);
    }
    else
    {
     IExceptionPublisher Publisher = (IExceptionPublisher)Activate(publisher.AssemblyName, publisher.TypeName);
     Publisher.Publish(exception, additionalInfo, publisher.OtherAttributes);
    }
   }
   catch(Exception e)
   {
    CustomPublisherException publisherException = new CustomPublisherException(resourceManager.GetString("RES_CUSTOM_PUBLISHER_FAILURE_MESSAGE"), publisher.AssemblyName, publisher.TypeName, publisher.ExceptionFormat, e);
                publisherException.AdditionalInformation.Add(publisher.OtherAttributes);
    throw(publisherException);
   }
  }
  private static void PublishToDefaultPublisher(Exception exception, NameValueCollection additionalInfo)
  {
   DefaultPublisher Publisher = new DefaultPublisher();
   Publisher.Publish(exception, additionalInfo, null);
  }
  internal static void PublishInternalException(Exception exception, NameValueCollection additionalInfo)
  {
   DefaultPublisher Publisher = new DefaultPublisher("Application", resourceManager.GetString("RES_EXCEPTIONMANAGER_INTERNAL_EXCEPTIONS"));
   Publisher.Publish(exception, additionalInfo, null);
  }
  private static object Activate(string assembly, string typeName)
  {
   return AppDomain.CurrentDomain.CreateInstanceAndUnwrap(assembly, typeName);
  }
  public static XmlDocument SerializeToXml(Exception exception, NameValueCollection additionalInfo)
  {
   try
   {
    string ROOT = resourceManager.GetString("RES_XML_ROOT");
    string ADDITIONAL_INFORMATION = resourceManager.GetString("RES_XML_ADDITIONAL_INFORMATION");
    string EXCEPTION = resourceManager.GetString("RES_XML_EXCEPTION");
    string STACK_TRACE = resourceManager.GetString("RES_XML_STACK_TRACE");
    XmlDocument xmlDoc = new XmlDocument();
    XmlElement root = xmlDoc.CreateElement(ROOT);
    xmlDoc.AppendChild(root);
    XmlElement element;
    XmlElement exceptionAddInfoElement;
    XmlElement stackTraceElement;
    XmlText stackTraceText;
    XmlAttribute attribute;
    if (additionalInfo != null && additionalInfo.Count > 0)
    {
     element = xmlDoc.CreateElement(ADDITIONAL_INFORMATION);
     foreach (string i in additionalInfo)
     {
      attribute = xmlDoc.CreateAttribute(i.Replace(" ", "_"));
      attribute.Value = additionalInfo.Get(i);
      element.Attributes.Append(attribute);
     }
     root.AppendChild(element);
    }
    if (exception == null)
    {
     element = xmlDoc.CreateElement(EXCEPTION);
     root.AppendChild(element);
    }
    else
    {
     Exception currentException = exception;
     XmlElement parentElement = null;
     do
     {
      element = xmlDoc.CreateElement(EXCEPTION);
      attribute = xmlDoc.CreateAttribute("ExceptionType");
      attribute.Value = currentException.GetType().FullName;
      element.Attributes.Append(attribute);
      PropertyInfo[] aryPublicProperties = currentException.GetType().GetProperties();
      NameValueCollection currentAdditionalInfo;
      foreach (PropertyInfo p in aryPublicProperties)
      {
       if (p.Name != "InnerException" && p.Name != "StackTrace")
       {
        if (p.GetValue(currentException,null) != null)
        {
         if (p.Name == "AdditionalInformation" && currentException is BaseApplicationException)
         {
          if (p.GetValue(currentException,null) != null)
          {
           currentAdditionalInfo = (NameValueCollection)p.GetValue(currentException,null);
           if (currentAdditionalInfo.Count > 0)
           {
            exceptionAddInfoElement = xmlDoc.CreateElement(ADDITIONAL_INFORMATION);
            foreach (string i in currentAdditionalInfo)
            {
             attribute = xmlDoc.CreateAttribute(i.Replace(" ", "_"));
             attribute.Value = currentAdditionalInfo.Get(i);
             exceptionAddInfoElement.Attributes.Append(attribute);
            }
            element.AppendChild(exceptionAddInfoElement);
           }
          }
         }
         else
         {
          attribute = xmlDoc.CreateAttribute(p.Name);
          attribute.Value = p.GetValue(currentException,null).ToString();
          element.Attributes.Append(attribute);
         }
        }
       }
      }
      if (currentException.StackTrace != null)
      {
       stackTraceElement = xmlDoc.CreateElement(STACK_TRACE);
       stackTraceText = xmlDoc.CreateTextNode(currentException.StackTrace.ToString());
       stackTraceElement.AppendChild(stackTraceText);
       element.AppendChild(stackTraceElement);
      }
      if (parentElement == null)
      {
       root.AppendChild(element);
      }
      else
      {
       parentElement.AppendChild(element);
      }
      parentElement = element;
      currentException = currentException.InnerException;
     } while (currentException != null);
    }
    return xmlDoc;
   }
   catch(Exception e)
   {
    throw new SerializationException(resourceManager.GetString("RES_EXCEPTIONMANAGEMENT_XMLSERIALIZATION_EXCEPTION"),e);
   }
  }
 }
 public sealed class DefaultPublisher : IExceptionPublisher
 {
  public DefaultPublisher()
  {
  }
  public DefaultPublisher(string logName, string applicationName)
  {
   this.logName = logName;
   this.applicationName = applicationName;
  }
  private static ResourceManager resourceManager = new ResourceManager(typeof(ExceptionManager).Namespace + ".ExceptionManagerText",Assembly.GetAssembly(typeof(ExceptionManager)));
  private string logName = "Application";
  private string applicationName = resourceManager.GetString("RES_EXCEPTIONMANAGER_PUBLISHED_EXCEPTIONS");
  private const string TEXT_SEPARATOR = "*********************************************";
  public void Publish(Exception exception, NameValueCollection additionalInfo, NameValueCollection configSettings)
  {
   if (configSettings != null)
   {
    if (configSettings["applicationName"] != null && configSettings["applicationName"].Length > 0) applicationName = configSettings["applicationName"];
    if (configSettings["logName"] != null && configSettings["logName"].Length > 0) logName = configSettings["logName"];
   }
   VerifyValidSource();
   StringBuilder strInfo = new StringBuilder();
   if (additionalInfo != null)
   {
    strInfo.AppendFormat("{0}General Information {0}{1}{0}Additional Info:", Environment.NewLine, TEXT_SEPARATOR);
    foreach (string i in additionalInfo)
    {
     strInfo.AppendFormat("{0}{1}: {2}", Environment.NewLine, i, additionalInfo.Get(i));
    }
   }
   if (exception == null)
   {
    strInfo.AppendFormat("{0}{0}No Exception object has been provided.{0}", Environment.NewLine);
   }
   else
   {
    Exception currentException = exception;
    int intExceptionCount = 1;
    do
    {
     strInfo.AppendFormat("{0}{0}{1}) Exception Information{0}{2}", Environment.NewLine, intExceptionCount.ToString(), TEXT_SEPARATOR);
     strInfo.AppendFormat("{0}Exception Type: {1}", Environment.NewLine, currentException.GetType().FullName);
     PropertyInfo[] aryPublicProperties = currentException.GetType().GetProperties();
     NameValueCollection currentAdditionalInfo;
     foreach (PropertyInfo p in aryPublicProperties)
     {
      if (p.Name != "InnerException" && p.Name != "StackTrace")
      {
       if (p.GetValue(currentException,null) == null)
       {
        strInfo.AppendFormat("{0}{1}: NULL", Environment.NewLine, p.Name);
       }
       else
       {
        if (p.Name == "AdditionalInformation" && currentException is BaseApplicationException)
        {
         if (p.GetValue(currentException,null) != null)
         {
          currentAdditionalInfo = (NameValueCollection)p.GetValue(currentException,null);
          if (currentAdditionalInfo.Count > 0)
          {
           strInfo.AppendFormat("{0}AdditionalInformation:", Environment.NewLine);
           for (int i = 0; i < currentAdditionalInfo.Count; i++)
           {
            strInfo.AppendFormat("{0}{1}: {2}", Environment.NewLine, currentAdditionalInfo.GetKey(i), currentAdditionalInfo[i]);
           }
          }
         }
        }
        else
        {
         strInfo.AppendFormat("{0}{1}: {2}", Environment.NewLine, p.Name, p.GetValue(currentException,null));
        }
       }
      }
     }
     if (currentException.StackTrace != null)
     {
      strInfo.AppendFormat("{0}{0}StackTrace Information{0}{1}", Environment.NewLine, TEXT_SEPARATOR);
      strInfo.AppendFormat("{0}{1}", Environment.NewLine, currentException.StackTrace);
     }
     currentException = currentException.InnerException;
     intExceptionCount++;
    } while (currentException != null);
   }
   WriteToLog(strInfo.ToString(), EventLogEntryType.Error);
  }
  private void WriteToLog(string entry, EventLogEntryType type)
  {
   try
   {
    EventLog.WriteEntry(applicationName,entry,type);
   }
   catch(SecurityException e)
   {
    throw new SecurityException(String.Format(resourceManager.GetString("RES_DEFAULTPUBLISHER_EVENTLOG_DENIED"), applicationName),e);
   }
  }
  private void VerifyValidSource()
  {
   try
   {
    if (!EventLog.SourceExists(applicationName))
    {
     EventLog.CreateEventSource(applicationName, logName);
    }
   }
   catch(SecurityException e)
   {
    throw new SecurityException(String.Format(resourceManager.GetString("RES_DEFAULTPUBLISHER_EVENTLOG_DENIED"), applicationName),e);
   }
  }
 }
}
