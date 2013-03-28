using System;
using System.Threading;
using System.IO;
using System.Net;
using Logger = RssBandit.Common.Logging;
using AppExceptions = Microsoft.ApplicationBlocks.ExceptionManagement;
using NewsComponents;
using NewsComponents.Feed;
namespace RssBandit.WinGui {
 internal class PostReplyThreadHandler: EntertainmentThreadHandlerBase
 {
  private PostReplyThreadHandler() { }
  public PostReplyThreadHandler(FeedSource feedHandler, string commentApiUri, INewsItem item2post, INewsItem inReply2item) {
   this.feedHandler = feedHandler;
   this.commentApiUri = commentApiUri;
   this.item2post = item2post;
   this.inReply2item = inReply2item;
  }
  public PostReplyThreadHandler(FeedSource feedHandler, INewsItem item2post, INewsFeed postTarget) {
   this.feedHandler = feedHandler;
   this.item2post = item2post;
   this.postTarget = postTarget;
  }
  private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(PostReplyThreadHandler));
  private FeedSource feedHandler = null;
  private string commentApiUri = null;
  private INewsItem item2post = null, inReply2item = null;
  private INewsFeed postTarget = null;
  public string CommentApiUri {
   get { return this.commentApiUri; }
   set { this.commentApiUri = value; }
  }
  public INewsItem ItemToPost {
   get { return this.item2post; }
   set { this.item2post = value; }
  }
  public INewsFeed PostTarget {
   get { return this.postTarget ; }
   set { this.postTarget = value; }
  }
  protected override void Run() {
   try {
    if (this.postTarget != null)
     this.feedHandler.PostComment(item2post, postTarget) ;
    else
     this.feedHandler.PostComment(commentApiUri, item2post, inReply2item);
   } catch (ThreadAbortException) {
   } catch(WebException we) {
    System.Text.StringBuilder sb = new System.Text.StringBuilder();
    StringWriter writeStream = null;
    StreamReader readStream = null;
    if(we.Response != null) {
     writeStream = new StringWriter(sb);
     Stream receiveStream = we.Response.GetResponseStream();
     System.Text.Encoding encode = System.Text.Encoding.GetEncoding("utf-8");
     readStream = new StreamReader( receiveStream, encode );
     Char[] read = new Char[256];
     int count = readStream.Read( read, 0, 256 );
     while (count > 0) {
      writeStream.Write(read, 0, count);
      count = readStream.Read(read, 0, 256);
     }
     p_operationException = new WebException(sb.ToString(), we, we.Status, we.Response);
     _log.Error(@"Error while posting a comment" , p_operationException);
     AppExceptions.ExceptionManager.Publish(p_operationException);
     p_operationException = we;
     if(writeStream != null){ writeStream.Close(); }
     if (readStream != null) { readStream.Close(); }
    } else {
     p_operationException = we;
    }
   }
   catch (Exception ex) {
    p_operationException = ex;
   }
   finally {
    WorkDone.Set();
   }
  }
 }
}
