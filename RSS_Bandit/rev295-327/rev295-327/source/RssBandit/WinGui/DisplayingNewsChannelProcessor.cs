using System; 
using NewsComponents; namespace  RssBandit.WinGui {
	
 public class  DisplayingNewsChannelProcessor : IChannelProcessor {
		
  public  DisplayingNewsChannelProcessor() {}
 
  public  INewsChannel[] GetChannels() {
   return new INewsChannel[] {new StripBadTagsChannel()};
  }

	}
	
 public class  StripBadTagsChannel : NewsItemChannelBase {
		
  public  StripBadTagsChannel():
   base("http://www.rssbandit.org/displaying-channels/newsitemcontent/stripbadtags", 1000) {
  }
 
  public override  INewsItem Process(INewsItem item) {
   return base.Process (item);
  }

	}

}
