using System;
using System.Drawing;
using System.Collections;
using Infragistics.Win.UltraWinToolbars;
using RssBandit.WinGui.Controls;
using RssBandit.WinGui.Interfaces;
using RssBandit.WinGui.Tools;
using RssBandit.WinGui.Utility;
using NewsComponents;
using NewsComponents.Utils;
namespace RssBandit.WinGui.Utility
{
 internal class History {
  const int DefaultMaxEntries = 100;
  private int _maxEntries;
  private ArrayList _historyEntries;
  private int _currentPosition;
  public event EventHandler StateChanged;
  public History():this(DefaultMaxEntries) {}
  public History(int maxEntries) {
   _maxEntries = (maxEntries > 0 ? maxEntries: DefaultMaxEntries);
   _currentPosition = 0;
   _historyEntries = new ArrayList(_maxEntries);
  }
  public void Add(HistoryEntry entry) {
   if (entry == null) return;
   TrimToPosition();
   int entryFoundAt = _historyEntries.IndexOf(entry);
   if(_historyEntries.Count > 0 && entryFoundAt >= 0) {
    _historyEntries.RemoveAt(entryFoundAt);
   }
   if(_historyEntries.Count > _maxEntries) {
    _historyEntries.RemoveAt(0);
   }
   _historyEntries.Add(entry);
   _currentPosition = _historyEntries.Count - 1;
   OnStateChanged();
  }
  public void Clear() {
   _historyEntries.Clear();
   _currentPosition = 0;
   OnStateChanged();
  }
  public int Count {
   get {
    return _historyEntries.Count;
   }
  }
  public HistoryEntry GetNext() {
   return GetNextAt(0);
  }
  public HistoryEntry GetNextAt(int index) {
   if (index < 0) throw new ArgumentOutOfRangeException();
   int newPos = _currentPosition + index + 1;
   if(newPos < _historyEntries.Count && newPos != _currentPosition) {
    _currentPosition = newPos;
    OnStateChanged();
   }
   if(_historyEntries.Count != 0 && _currentPosition < _historyEntries.Count) {
    HistoryEntry ret = (HistoryEntry)_historyEntries[_currentPosition];
    if (ret.Node == null || ret.Node != null && ret.Node.Control != null)
     return ret;
    else
     return GetNextAt(index);
   } else
    return null;
  }
  public HistoryEntry GetPrevious() {
   return GetPreviousAt(0);
  }
  public HistoryEntry GetPreviousAt(int index) {
   if (index < 0) throw new ArgumentOutOfRangeException();
   int newPos = _currentPosition - index - 1;
   if(newPos >= 0 && newPos != _currentPosition) {
    _currentPosition = newPos;
    OnStateChanged();
   }
   if(_historyEntries.Count != 0) {
    HistoryEntry ret = (HistoryEntry)_historyEntries[_currentPosition];
    if (ret.Node == null || ret.Node != null && ret.Node.Control != null)
     return ret;
    else
     return GetPreviousAt(index);
   } else
    return null;
  }
  public bool CanGetNext {
   get {
    return (
     _historyEntries.Count != 0 &&
     _currentPosition < _historyEntries.Count-1);
   }
  }
  public bool CanGetPrevious {
   get {return _currentPosition > 0; }
  }
  public ITextImageItem[] GetHeadOfNextEntries(int maxEntries) {
   if (! CanGetNext)
    return new ITextImageItem[]{};
   if (maxEntries <= 0)
    maxEntries = this._maxEntries;
   ArrayList head = new ArrayList(maxEntries);
   for (int i = _currentPosition + 1; i < Math.Min(_historyEntries.Count, _currentPosition + maxEntries + 1); i++) {
    HistoryEntry he = (HistoryEntry)_historyEntries[i];
    if (he.Node != null) {
     if (he.Node.Control == null)
      continue;
     Image img = he.Node.ImageResolved;
     head.Add(new TextImageItem(he.ToString(), img ));
    } else {
     head.Add(new TextImageItem(he.ToString(), null));
    }
   }
   if (head.Count > 0)
    return (ITextImageItem[])head.ToArray(typeof(ITextImageItem));
   return new ITextImageItem[]{};
  }
  public ITextImageItem[] GetHeadOfPreviousEntries(int maxEntries) {
   if (! CanGetPrevious)
    return new ITextImageItem[]{};
   if (maxEntries <= 0)
    maxEntries = this._maxEntries;
   ArrayList head = new ArrayList(maxEntries);
   for (int i = _currentPosition - 1; i >= Math.Max(0, _currentPosition - maxEntries); i--) {
    HistoryEntry he = (HistoryEntry)_historyEntries[i];
    if (he.Node != null) {
     if (he.Node.Control == null)
      continue;
     Image img = null;
     if(he.Node.Override.NodeAppearance.Image is Image){
      img = (Image) he.Node.Override.NodeAppearance.Image;
     }else if (null != he.Node.Override.NodeAppearance.Image){
      img = he.Node.Control.ImageList.Images[(int)he.Node.Override.NodeAppearance.Image];
     }
     head.Add(new TextImageItem(he.ToString(), img ));
    } else {
     head.Add(new TextImageItem(he.ToString(), null));
    }
   }
   if (head.Count > 0)
    return (ITextImageItem[])head.ToArray(typeof(ITextImageItem));
   return new ITextImageItem[]{};
  }
  protected void OnStateChanged() {
   if (StateChanged != null)
    StateChanged(this, EventArgs.Empty);
  }
  private void TrimToPosition() {
   if (_historyEntries.Count > 0 && _historyEntries.Count-1 > _currentPosition) {
    _historyEntries.RemoveRange(_currentPosition+1, _historyEntries.Count - _currentPosition - 1);
   }
  }
 }
 internal class HistoryEntry {
  public HistoryEntry():this(null,null) {}
  public HistoryEntry(TreeFeedsNodeBase feedsNode):this(feedsNode, null) {}
  public HistoryEntry(TreeFeedsNodeBase feedsNode, INewsItem item) {
   this.Node = feedsNode;
   this.Item = item;
  }
  public TreeFeedsNodeBase Node;
  public INewsItem Item;
  public override bool Equals(object obj) {
   if (null == (obj as HistoryEntry))
    return false;
   if (Object.ReferenceEquals(this, obj))
    return true;
   HistoryEntry o = obj as HistoryEntry;
   if (Object.ReferenceEquals(this.Node, o.Node) &&
    Object.ReferenceEquals(this.Item, o.Item))
    return true;
   if (this.Node == null || this.Item == null)
    return false;
   if (this.Node.Equals(o.Node) &&
    this.Item.Equals(o.Item))
    return true;
   return false;
  }
  public override int GetHashCode() {
   if (this.Node != null) {
    if (this.Item != null)
     return this.Node.GetHashCode() ^ this.Item.GetHashCode();
    return this.Node.GetHashCode();
   } else if (this.Item != null) {
    return this.Item.GetHashCode();
   }
   return base.GetHashCode();
  }
  public override string ToString() {
   if (this.Node != null) {
    if (this.Item != null)
     return StringHelper.ShortenByEllipsis(String.Format("{0} | {1}", this.Node.Text, this.Item.Title), 80);
    return StringHelper.ShortenByEllipsis(this.Node.Text, 80);
   } else if (this.Item != null) {
    return StringHelper.ShortenByEllipsis(String.Format("{0} | {1}", this.Item.FeedDetails.Title, this.Item.Title), 80);
   }
   return "?";
  }
 }
 internal class HistoryMenuManager
 {
  public event HistoryNavigationEventHandler OnNavigateBack;
  public event HistoryNavigationEventHandler OnNavigateForward;
  private CommandMediator mediator = null;
  private AppPopupMenuCommand _browserGoBackCommand = null;
  private AppPopupMenuCommand _browserGoForwardCommand = null;
  public HistoryMenuManager() {
   this.mediator = new CommandMediator();
  }
  internal void SetControls (AppPopupMenuCommand goBack, AppPopupMenuCommand goForward) {
   this._browserGoBackCommand = goBack;
   this._browserGoForwardCommand = goForward;
   Reset();
   this._browserGoBackCommand.ToolbarsManager.ToolClick -= new ToolClickEventHandler(OnToolbarsManager_ToolClick);
   this._browserGoBackCommand.ToolbarsManager.ToolClick += new ToolClickEventHandler(OnToolbarsManager_ToolClick);
  }
  internal void Reset() {
   this._browserGoBackCommand.Tools.Clear();
   this._browserGoForwardCommand.Tools.Clear();
  }
  internal void ReBuildBrowserGoBackHistoryCommandItems(ITextImageItem[] items) {
   this._browserGoBackCommand.Tools.Clear();
   for (int i=0; items != null && i < items.Length; i++)
   {
    ITextImageItem item = items[i];
    string toolKey = "cmdBrowserGoBack_" + i.ToString();
    AppButtonToolCommand cmd = null;
    if (this._browserGoBackCommand.ToolbarsManager.Tools.Exists(toolKey))
     cmd = (AppButtonToolCommand)this._browserGoBackCommand.ToolbarsManager.Tools[toolKey];
    if (cmd == null) {
     cmd = new AppButtonToolCommand(toolKey,
      this.mediator, new ExecuteCommandHandler(this.CmdBrowserGoBackHistoryItem),
      item.Text, String.Empty);
     this._browserGoBackCommand.ToolbarsManager.Tools.Add(cmd);
    }
    if (cmd.Mediator == null) {
     cmd.Mediator = this.mediator;
     cmd.OnExecute += new ExecuteCommandHandler(this.CmdBrowserGoBackHistoryItem);
     this.mediator.RegisterCommand(toolKey, cmd);
    } else
    if (cmd.Mediator != this.mediator) {
     this.mediator.ReRegisterCommand(cmd);
    }
    cmd.SharedProps.ShowInCustomizer = false;
    cmd.SharedProps.AppearancesSmall.Appearance.Image = item.Image;
    cmd.SharedProps.Caption = item.Text;
    cmd.Tag = i;
    this._browserGoBackCommand.Tools.Add(cmd);
   }
   if (this._browserGoBackCommand.Tools.Count == 0)
    this._browserGoBackCommand.DropDownArrowStyle = DropDownArrowStyle.None;
   else
    this._browserGoBackCommand.DropDownArrowStyle = DropDownArrowStyle.Segmented;
  }
  internal void ReBuildBrowserGoForwardHistoryCommandItems(ITextImageItem[] items) {
   _browserGoForwardCommand.Tools.Clear();
   for (int i=0; items != null && i < items.Length; i++)
   {
    ITextImageItem item = items[i];
    string toolKey = "cmdBrowserGoForward_" + i.ToString();
    AppButtonToolCommand cmd = null;
    if (this._browserGoForwardCommand.ToolbarsManager.Tools.Exists(toolKey))
     cmd = (AppButtonToolCommand)this._browserGoForwardCommand.ToolbarsManager.Tools[toolKey];
    if (cmd == null)
    {
     cmd = new AppButtonToolCommand(toolKey,
      this.mediator, new ExecuteCommandHandler(this.CmdBrowserGoForwardHistoryItem),
      item.Text, String.Empty);
     this._browserGoForwardCommand.ToolbarsManager.Tools.Add(cmd);
    }
    if (cmd.Mediator == null) {
     cmd.Mediator = this.mediator;
     cmd.OnExecute += new ExecuteCommandHandler(this.CmdBrowserGoForwardHistoryItem);
     this.mediator.RegisterCommand(toolKey, cmd);
    } else
    if (cmd.Mediator != this.mediator) {
     this.mediator.ReRegisterCommand(cmd);
    }
    cmd.SharedProps.ShowInCustomizer = false;
    cmd.SharedProps.AppearancesSmall.Appearance.Image = item.Image;
    cmd.SharedProps.Caption = item.Text;
    cmd.Tag = i;
    this._browserGoForwardCommand.Tools.Add(cmd);
   }
   if (this._browserGoForwardCommand.Tools.Count == 0)
    this._browserGoForwardCommand.DropDownArrowStyle = DropDownArrowStyle.None;
   else
    this._browserGoForwardCommand.DropDownArrowStyle = DropDownArrowStyle.Segmented;
  }
  private void CmdBrowserGoBackHistoryItem(ICommand sender) {
   AppButtonToolCommand cmd = sender as AppButtonToolCommand;
   if (cmd != null)
    RaiseNavigateBackEvent((int)cmd.Tag);
  }
  private void CmdBrowserGoForwardHistoryItem(ICommand sender) {
   AppButtonToolCommand cmd = sender as AppButtonToolCommand;
   if (cmd != null)
    RaiseNavigateForwardEvent((int)cmd.Tag);
  }
  private void RaiseNavigateBackEvent(int index) {
   if (this.OnNavigateBack != null)
    this.OnNavigateBack(this, new HistoryNavigationEventArgs(index));
  }
  private void RaiseNavigateForwardEvent(int index) {
   if (this.OnNavigateForward != null)
    this.OnNavigateForward(this, new HistoryNavigationEventArgs(index));
  }
  private void OnToolbarsManager_ToolClick(object sender, ToolClickEventArgs e) {
   this.mediator.Execute(e.Tool.Key);
  }
 }
 internal delegate void HistoryNavigationEventHandler(object sender, HistoryNavigationEventArgs e);
 internal class HistoryNavigationEventArgs: EventArgs {
  public HistoryNavigationEventArgs(int index) {
   this.Index = index;
  }
  public readonly int Index;
 }
}
