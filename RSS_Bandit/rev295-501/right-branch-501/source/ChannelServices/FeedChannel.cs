using System;
namespace NewsComponents {
 public class FeedChannel: INewsChannel {
  protected string p_channelName;
  protected int p_channelPriority;
  public FeedChannel():
   this("http://www.rssbandit.org/channels/feedchannel", 50){
  }
  public FeedChannel(string channelName, int channelPriority) {
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
