using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Threading;
using Lucene.Net.Index;
using NewsComponents.Utils;
using NewsComponents.Feed;
using RssBandit.Common.Logging;
namespace NewsComponents.Search
{
 internal class LuceneIndexer {
  public event EventHandler IndexingFinished;
  public event LuceneIndexingProgressEventHandler IndexingProgress;
  private LuceneIndexModifier indexModifier;
  private Thread IndexingThread;
  private IDictionary restartInfo;
  private DictionaryEntry lastIndexed;
        private IList<NewsHandler> newsHandlers;
  public LuceneIndexer(LuceneIndexModifier indexModifier, IList<NewsHandler> newsHandlers) {
   this.indexModifier = indexModifier;
   if (this.indexModifier == null)
    throw new ArgumentNullException("indexModifier");
            this.newsHandlers = newsHandlers;
            if (this.newsHandlers == null || this.newsHandlers.Count == 0)
                throw new ArgumentException("newsHandlers");
  }
  public void IndexAll(IDictionary restartInfo, DictionaryEntry lastIndexed) {
   if (IndexingThread != null && IndexingThread.IsAlive)
    return;
   IndexingThread = new Thread(new ThreadStart(this.ThreadRun));
   IndexingThread.Name = "BanditIndexerThread";
   IndexingThread.IsBackground = true;
   if (restartInfo == null)
    restartInfo = new HybridDictionary();
   this.lastIndexed = lastIndexed;
   this.restartInfo = restartInfo;
   IndexingThread.Start();
  }
  private void ThreadRun() {
   System.DateTime start = System.DateTime.Now;
   Log.Info(String.Format("Lucene Indexing {0}started at {1}", restartInfo.Count == 0 ? String.Empty: "re-", start) );
   if (lastIndexed.Key != null && lastIndexed.Value != null)
   {
    this.RemoveNewsItems(lastIndexed.Value as string);
    restartInfo.Remove(lastIndexed.Key);
    Log.Info(String.Format("Lucene Indexing removed possible incomplete indexed documents of feed {0}", lastIndexed.Key) );
   }
   try {
                int feedCount = 0, itemCount = 0;
                foreach(NewsHandler newsHandler in newsHandlers)
                {
                if (newsHandler.FeedsListOK &&
                    0 < newsHandler.FeedsTable.Count)
                {
                    int maxCount = newsHandler.FeedsTable.Count;
                    string[] feedLinks = new string[maxCount];
                    newsHandler.FeedsTable.Keys.CopyTo(feedLinks, 0);
                    for (int i = 0; i < maxCount; i++)
                    {
                        string feedlink = feedLinks[i];
                        if (!newsHandler.FeedsTable.ContainsKey(feedlink))
                            continue;
                        feedCount++;
                        if (!restartInfo.Contains(feedlink))
                        {
                            string feedID = newsHandler.FeedsTable[feedlink].id;
                            if (!this.RaiseIndexingProgress(feedCount, maxCount, feedlink, feedID))
                            {
                                RemoveNewsItems(feedID);
                                itemCount += IndexNewsItems(newsHandler.GetCachedItemsForFeed(feedlink));
                                Log.Info("IndexAll() progress: " + feedCount + " feeds, " + itemCount + " items processed.");
                            }
                        }
                        else
                        {
                            INewsFeed f = null;
                            if (newsHandler.FeedsTable.TryGetValue(feedlink, out f))
                            {
                                f.id = restartInfo[feedlink] as string;
                            }
                        }
                    }
                }
                }
     System.DateTime end = System.DateTime.Now;
     TimeSpan timeRequired = TimeSpan.FromTicks(end.Ticks - start.Ticks);
     Log.Info(String.Format("Lucene Indexing all items finished at {0}. Time required: {1}h:{2} min for {3} feeds with {4} items.", end, timeRequired.TotalHours, timeRequired.TotalMinutes, feedCount, itemCount ) );
    this.RaiseIndexingFinished();
    if (feedCount != 0 || itemCount != 0)
     indexModifier.Optimize();
   } catch (ThreadAbortException) {
   } catch (Exception e) {
    Log.Error("Failure while indexing items.", e);
   } finally {
    this.IndexingThread = null;
   }
  }
  internal int IndexNewsItems(IList<NewsItem> newsItems) {
   if (newsItems != null)
   {
    for (int i=0; i < newsItems.Count; i++) {
     NewsItem item = newsItems[i] as NewsItem;
     if (item != null) {
      try {
       if (item.ContentType == ContentType.None && item.Feed != null && item.Feed.owner != null)
        item.Feed.owner.GetCachedContentForItem(item);
       indexModifier.Add(LuceneNewsItemSearch.Document(item), item.Language);
      } catch (Exception ex) {
       Log.Error("IndexNewsItems() error", ex);
      }
     }
    }
    return newsItems.Count;
   }
   return 0;
  }
  internal void RemoveNewsItems(IList newsItems) {
   if (newsItems != null && newsItems.Count > 0) {
    Term term = new Term(LuceneSearch.IndexDocument.ItemID, null);
    for (int i=0; i < newsItems.Count; i++) {
     NewsItem item = newsItems[i] as NewsItem;
     if (item != null) {
      term.text = LuceneNewsItemSearch.UID(item);
      indexModifier.Delete(term);
     }
    }
   }
  }
  internal void RemoveNewsItems(string feedID) {
   if (!string.IsNullOrEmpty(feedID)) {
    Term term = new Term(LuceneSearch.IndexDocument.FeedID, feedID);
     indexModifier.Delete(term);
   }
  }
  internal void RemoveFeed(string feedID) {
   if (!string.IsNullOrEmpty(feedID)) {
    Term term = new Term(LuceneSearch.IndexDocument.FeedID, feedID);
    indexModifier.DeleteFeed(term);
   }
  }
  private void RaiseIndexingFinished() {
   if (IndexingFinished != null)
    try { IndexingFinished(this, EventArgs.Empty); } catch {}
  }
  private bool RaiseIndexingProgress(int current, int max, string yetIndexedFeedUrl, string yetIndexedFeedID) {
   bool toReturn = false;
   if (IndexingProgress != null) {
    LuceneIndexingProgressCancelEventArgs cancelEventArgs = new LuceneIndexingProgressCancelEventArgs(toReturn, current, max, yetIndexedFeedUrl, yetIndexedFeedID);
    try { IndexingProgress(this, cancelEventArgs); } catch{}
    return cancelEventArgs.Cancel;
   }
   return toReturn;
  }
 }
 public delegate void LuceneIndexingProgressEventHandler(object sender, LuceneIndexingProgressCancelEventArgs e);
 public class LuceneIndexingProgressCancelEventArgs: CancelEventArgs
 {
  public readonly int Current;
  public readonly int Max;
  public readonly string YetIndexedFeedUrl;
  public readonly string YetIndexedFeedID;
  public LuceneIndexingProgressCancelEventArgs(bool cancel, int current, int max, string yetIndexedFeedUrl, string yetIndexedFeedID):
   base(cancel)
  {
   this.Current = current;
   this.Max = max;
   this.YetIndexedFeedUrl = yetIndexedFeedUrl;
   this.YetIndexedFeedID = yetIndexedFeedID;
  }
 }
}
