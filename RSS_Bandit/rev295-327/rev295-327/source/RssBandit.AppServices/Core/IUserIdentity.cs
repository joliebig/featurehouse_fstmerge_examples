namespace  NewsComponents {
	
 public interface  IUserIdentity {
		
  string Name { get; } 
   [System.Xml.Serialization.XmlElementAttribute("real-name")]
   string RealName { get; } 
   string Organization { get; } 
   string MailAddress { get; } 
   string ResponseAddress { get; } 
   string ReferrerUrl { get; } 
   string Signature { get; }
	}

}
