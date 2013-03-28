using System; 
using System.Collections.Specialized; 
using System.Xml; namespace  Microsoft.ApplicationBlocks.ExceptionManagement {
	
 public interface  IExceptionPublisher {
		
  void Publish(Exception exception, NameValueCollection additionalInfo, NameValueCollection configSettings);
	}
	
 public interface  IExceptionXmlPublisher {
		
  void Publish(XmlDocument exceptionInfo, NameValueCollection configSettings);
	}

}
