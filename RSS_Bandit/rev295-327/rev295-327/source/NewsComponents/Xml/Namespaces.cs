using NewsComponents.Xml.Serialization; namespace  NewsComponents {
	
 public sealed class  NamespaceCore {
		
  public  const string Feeds_v2003 = "http://www.25hoursaday.com/2003/RSSBandit/feeds/"; 
  public  const string Feeds_v2004 = "http://www.25hoursaday.com/2004/RSSBandit/feeds/"; 
  public  const string Feeds_vCurrent = Feeds_v2004; 
  private  NamespaceCore(){}

	}
	
 public sealed class  NamespaceXml {
		
  private  NamespaceXml() {}
 
  public  const string Xml = "http://www.w3.org/XML/1998/namespace"; 
  public  const string XmlNs = "http://www.w3.org/2000/xmlns/"; 
  public  const string XmlNsPrefix = "xmlns"; 
  public  const string Xsi = "http://www.w3.org/2001/XMLSchema-instance"; 
  public  const string Xsd = "http://www.w3.org/2001/XMLSchema";
	}
	
 public sealed class  XmlHelper {
		
  private  XmlHelper() {}
 
  public static  XmlSerializerCache SerializerCache {
   get { return InstanceHelper.instance; }
  }
 
  private class  InstanceHelper {
			
   static  InstanceHelper() {;}
 
   internal static readonly  XmlSerializerCache instance = new XmlSerializerCache();

		}

	}

}
