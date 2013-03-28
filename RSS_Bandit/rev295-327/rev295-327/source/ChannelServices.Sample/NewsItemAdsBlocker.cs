using System; 
using NewsComponents; namespace  ChannelServices.AdsBlocker {
	
 public class  NewsItemAdsBlocker : IChannelProcessor {
		
  public  NewsItemAdsBlocker(){} 
  public  INewsChannel[] GetChannels() {
   return new INewsChannel[] {new AdsBlockerChannel()};
  }
	}

}
