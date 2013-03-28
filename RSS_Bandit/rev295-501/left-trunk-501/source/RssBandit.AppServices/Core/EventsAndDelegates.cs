using System;
using RssBandit;
namespace RssBandit.AppServices
{
 public delegate void FeedDeletedHandler(object sender, FeedDeletedEventArgs e);
 public class FeedDeletedEventArgs: FeedEventArgs
 {
  public FeedDeletedEventArgs(string feedUrl):
   base(feedUrl) {
  }
  public FeedDeletedEventArgs(string feedUrl, string feedTitle):
   base(feedUrl, feedTitle) {
  }
 }
 public class FeedEventArgs: EventArgs {
  public FeedEventArgs(string feedUrl):
   this(feedUrl, String.Empty) {
  }
  public FeedEventArgs(string feedUrl, string feedTitle):base() {
   this._feedUrl = feedUrl;
   this._feedTitle = feedTitle;
  }
  public string FeedUrl {
   get { return _feedUrl; }
  }
  private readonly string _feedUrl;
  public string FeedTitle {
   get { return _feedTitle; }
  }
  private readonly string _feedTitle;
 }
 public delegate void InternetConnectionStateChangeHandler(object sender, InternetConnectionStateChangeEventArgs e);
 public class InternetConnectionStateChangeEventArgs: EventArgs {
  public InternetConnectionStateChangeEventArgs(INetState currentState, INetState newState) {
   this._currentState = currentState;
   this._newState = newState;
  }
  public INetState CurrentState {
   get { return _currentState; }
  }
  private readonly INetState _currentState;
  public INetState NewState {
   get { return _newState; }
  }
  private readonly INetState _newState;
 }
}
