using NewsComponents;
namespace ChannelServices.AdsBlocker.AddIn
{
 public class NewsItemAdsBlocker: IChannelProcessor
 {
  public INewsChannel[] GetChannels() {
   return new INewsChannel[] {new AdsBlockerChannel()};
  }
 }
}
