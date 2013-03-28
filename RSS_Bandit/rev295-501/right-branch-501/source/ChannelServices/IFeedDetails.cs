using System;
using System.Collections;
namespace NewsComponents
{
 public interface IFeedDetails: ICloneable{
  string Title{ get; }
  string Link{ get; }
  string Description{ get; }
  Hashtable OptionalElements { get; }
  FeedType Type { get; }
 }
}
