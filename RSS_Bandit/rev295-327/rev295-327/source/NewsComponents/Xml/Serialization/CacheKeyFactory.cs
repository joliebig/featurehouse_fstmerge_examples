using System; 
using System.Text; 
using System.Xml.Serialization; namespace  NewsComponents.Xml.Serialization {
	
 public class  CacheKeyFactory {
		
  private  CacheKeyFactory()
  {
  }
 
  public static  string MakeKey(Type type
   , XmlAttributeOverrides overrides
   , Type[] types
   , XmlRootAttribute root
   , String defaultNamespace)
  {
   StringBuilder keyBuilder = new StringBuilder();
   keyBuilder.Append(type.FullName);
   keyBuilder.Append( "??" );
   keyBuilder.Append(SignatureExtractor.GetOverridesSignature(overrides));
   keyBuilder.Append( "??" );
   keyBuilder.Append(SignatureExtractor.GetTypeArraySignature(types));
   keyBuilder.Append("??");
   keyBuilder.Append(SignatureExtractor.GetXmlRootSignature(root));
   keyBuilder.Append("??");
   keyBuilder.Append(SignatureExtractor.GetDefaultNamespaceSignature(defaultNamespace));
   return keyBuilder.ToString();
  }

	}

}
