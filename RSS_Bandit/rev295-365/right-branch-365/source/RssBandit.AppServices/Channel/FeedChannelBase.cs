using System;
namespace NewsComponents {
 public class FeedChannelBase: INewsChannel {
  protected string p_channelName;
  protected int p_channelPriority;
  public FeedChannelBase():
   this("http://www.rssbandit.org/channels/feedchannel", 50){
  }
  public FeedChannelBase(string channelName, int channelPriority) {
   p_channelName = channelName;
   p_channelPriority = channelPriority;
  }
  public virtual string ChannelName {
   get { return p_channelName; }
  }
  public virtual int ChannelPriority {
   get {return p_channelPriority; }
  }
  public NewsComponents.ChannelProcessingType ChannelProcessingType {
   get { return ChannelProcessingType.Feed;}
  }
  public virtual IFeedDetails Process(IFeedDetails item) {
   return item;
  }
 }
}
