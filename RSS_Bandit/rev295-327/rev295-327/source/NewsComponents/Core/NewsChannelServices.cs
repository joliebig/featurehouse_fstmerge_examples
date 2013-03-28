using System; 
using System.Collections; 
using System.Reflection; 
using System.Threading; namespace  NewsComponents {
	
 public class  NewsChannelServices {
		
  private static readonly  log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);
 
  private  SortedList _newsItemChannels = new SortedList(new ChannelComparer());
 
  private  SortedList _feedChannels = new SortedList(new ChannelComparer());
 
  private  ReaderWriterLock _newsItemChannelsLock = new ReaderWriterLock();
 
  private  ReaderWriterLock _feedChannelsLock = new ReaderWriterLock();
 
  public  NewsChannelServices(){}
 
  public  void RegisterNewsChannel(INewsChannel channel)
  {
   if (channel == null)
    throw new ArgumentNullException("channel");
   if (channel.ChannelProcessingType == ChannelProcessingType.NewsItem) {
    try {
     _newsItemChannelsLock.AcquireWriterLock(0);
     try {
      _newsItemChannels.Add(channel, channel);
     } finally {
      _newsItemChannelsLock.ReleaseWriterLock();
     }
    } catch (ApplicationException) {
    }
   } else if (channel.ChannelProcessingType == ChannelProcessingType.Feed) {
    try {
     _feedChannelsLock.AcquireWriterLock(0);
     try {
     _feedChannels.Add(channel, channel);
     } finally {
      _feedChannelsLock.ReleaseWriterLock();
     }
    } catch (ApplicationException) {
    }
   } else
    throw new NotSupportedException("The channel processing type is not yet supported.");
  }
 
  public  void UnregisterNewsChannel(INewsChannel channel) {
   if (channel == null)
    throw new ArgumentNullException("channel");
   if (channel.ChannelProcessingType == ChannelProcessingType.NewsItem) {
    try {
     _newsItemChannelsLock.AcquireWriterLock(0);
     try {
     if (_newsItemChannels.ContainsKey(channel))
      _newsItemChannels.Remove(channel);
     } finally {
      _newsItemChannelsLock.ReleaseWriterLock();
     }
    } catch (ApplicationException) {
    }
   } else if (channel.ChannelProcessingType == ChannelProcessingType.Feed) {
    try {
     _feedChannelsLock.AcquireWriterLock(0);
     try {
     if (_feedChannels.ContainsKey(channel))
      _feedChannels.Remove(channel);
     } finally {
      _feedChannelsLock.ReleaseWriterLock();
     }
    } catch (ApplicationException) {
    }
   } else
    throw new NotSupportedException("The channel processing type is not yet supported.");
  }
 
  public  INewsItem ProcessItem(INewsItem item)
  {
   try {
    _newsItemChannelsLock.AcquireReaderLock(0);
    try {
     if (_newsItemChannels.Count == 0)
      return item;
     foreach (NewsItemChannelBase sink in _newsItemChannels.Values){
      try {
       item = sink.Process(item);
      } catch (Exception ex) {
       _log.Error("NewsChannelServices.ProcessItem() sink '"+(sink != null ? sink.ChannelName : "[ChannelName]?") +"' failed to process INewsItem", ex);
      }
     }
    } finally {
     _newsItemChannelsLock.ReleaseReaderLock();
    }
   } catch (ApplicationException) {
   } catch (Exception ex) {
    _log.Error("Failed to process INewsItem.", ex);
   }
   return item;
  }
 
  public  IFeedDetails ProcessItem(IFeedDetails item) {
   try {
    _feedChannelsLock.AcquireReaderLock(0);
    try {
     if (_feedChannels.Count == 0)
      return item;
     foreach (FeedChannelBase sink in _feedChannels.Values){
      item = sink.Process(item);
     }
    } finally {
     _feedChannelsLock.ReleaseReaderLock();
    }
   } catch (ApplicationException) {
   } catch (Exception ex) {
    _log.Error("Failed to process IFeedDetails.", ex);
   }
   return item;
  }
 class  ChannelComparer : IComparer {
			
   public  int Compare(object x, object y) {
    INewsChannel lhsX = x as INewsChannel;
    INewsChannel rhsY = y as INewsChannel;
    if(lhsX == null || rhsY == null)
     return 0;
    if (Object.ReferenceEquals(lhsX, rhsY))
     return 0;
    if (lhsX.ChannelPriority == rhsY.ChannelPriority)
     return String.Compare(lhsX.ChannelName, rhsY.ChannelName);
    return (lhsX.ChannelPriority < rhsY.ChannelPriority ? -1: 1);
   }

		}

	}

}
