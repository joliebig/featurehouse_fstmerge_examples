using System; 
using System.Collections; namespace  NewsComponents {
	
 public interface  IFeedDetails :  ICloneable {
		
  string Language{ get; } 
  string Title{ get; } 
  string Link{ get; } 
  string Description{ get; } 
  IDictionary OptionalElements { get; } 
  FeedType Type { get; }
	}

}
