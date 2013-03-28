using System.Xml; namespace  RssBandit {
	
 internal sealed class  AdditionalFeedElements {
		
  public  const string Namespace = NewsComponents.NamespaceCore.Feeds_v2003; 
  public  const string OldElementPrefix = "bandit"; 
  public  const string ElementPrefix = "rssbandit"; 
  public  const string FlaggedElementName = "feed-url"; 
  public  const string DeletedElementName = "container-url"; 
  public  const string ErrorElementName = "failed-url"; 
  private static volatile  XmlQualifiedName _originalFeedOfFlaggedItem = null;
 
  private static volatile  XmlQualifiedName _originalFeedOfDeletedItem = null;
 
  private static volatile  XmlQualifiedName _originalFeedOfErrorItem = null;
 
  private static volatile  XmlQualifiedName _originalFeedOfWatchedItem = null;
 
  private static  object creationLock = new object();
 
  public static  XmlQualifiedName OriginalFeedOfFlaggedItem {
   get {
    if (_originalFeedOfFlaggedItem == null) {
     lock(creationLock) {
      _originalFeedOfFlaggedItem = new XmlQualifiedName(FlaggedElementName, Namespace);
     }
    }
    return _originalFeedOfFlaggedItem;
   }
  }
 
  public static  XmlQualifiedName OriginalFeedOfDeletedItem {
   get {
    if (_originalFeedOfDeletedItem == null) {
     lock(creationLock) {
      _originalFeedOfDeletedItem = new XmlQualifiedName(DeletedElementName, Namespace);
     }
    }
    return _originalFeedOfDeletedItem;
   }
  }
 
  public static  XmlQualifiedName OriginalFeedOfWatchedItem {
   get {
    if (_originalFeedOfWatchedItem == null) {
     lock(creationLock) {
      _originalFeedOfWatchedItem = new XmlQualifiedName(FlaggedElementName, Namespace);
     }
    }
    return _originalFeedOfWatchedItem;
   }
  }
 
  public static  XmlQualifiedName OriginalFeedOfErrorItem {
   get {
    if (_originalFeedOfErrorItem == null) {
     lock(creationLock) {
      _originalFeedOfErrorItem = new XmlQualifiedName(ErrorElementName, Namespace);
     }
    }
    return _originalFeedOfErrorItem;
   }
  }
 
  private  AdditionalFeedElements() {}

	}

}
