using System;
using System.Collections;
using System.Drawing;
using Infragistics.Win.UltraWinTree;
using NewsComponents;
using NewsComponents.Utils;
using RssBandit.Resources;
using RssBandit.WinGui.Interfaces;
using RssBandit.WinGui.Utility;
namespace RssBandit.WinGui.Controls
{
 public abstract class TreeFeedsNodeBase: UltraTreeNode {
  public event System.EventHandler ReadCounterZero;
  public string TextBeforeEditing;
  private FeedNodeType _type;
  private bool _editable, _anyUnread, _anyNewComments;
  protected bool m_hasCustomIcon;
  private int _unreadCount;
  private int _itemsWithNewCommentsCount;
  private int _highlightCount;
  private object _initialImage, _initialExpandedImage;
  private int _imageIndex, _selectedImageIndex;
  private static Image _clickableAreaExtenderImage;
  static TreeFeedsNodeBase() {
   _clickableAreaExtenderImage = new Bitmap(1,1);
  }
  protected TreeFeedsNodeBase() {}
  protected TreeFeedsNodeBase(string text, FeedNodeType nodeType):
   this(text, nodeType, false) {
   }
  protected TreeFeedsNodeBase(string text, FeedNodeType nodeType, bool editable):
   this(text, nodeType, editable, -1, -1) {
   }
  protected TreeFeedsNodeBase(string text, FeedNodeType nodeType, bool editable, int imageIndex, int expandedNodeImageIndex):
   this(text, nodeType, editable, imageIndex, expandedNodeImageIndex, null){
  }
  protected TreeFeedsNodeBase(string text, FeedNodeType nodeType, bool editable, int imageIndex, int expandedNodeImageIndex, Image image):
   base()
  {
   this.RightImages.Add(_clickableAreaExtenderImage);
   FontColorHelper.CopyFromFont( this.Override.NodeAppearance.FontData, FontColorHelper.NormalFont);
   this.ForeColor = FontColorHelper.NormalColor;
   _unreadCount = _highlightCount = 0;
   _initialImage = _initialExpandedImage = null;
   if(image != null){
    _initialImage = _initialExpandedImage = base.Override.NodeAppearance.Image = base.Override.ExpandedNodeAppearance.Image = image;
    m_hasCustomIcon = true;
   }else{
    if (imageIndex >= 0) {
     base.Override.NodeAppearance.Image = imageIndex;
     _imageIndex = imageIndex;
    }
    if (expandedNodeImageIndex >= 0) {
     base.Override.ExpandedNodeAppearance.Image = expandedNodeImageIndex;
     _selectedImageIndex = expandedNodeImageIndex;
    }
   }
   _type = nodeType;
   _editable = editable;
   _anyUnread = false;
   this.Text = text;
  }
  public new virtual string DataKey {
   get { return base.DataKey as string; }
   set { base.DataKey = value; }
  }
  public Image ImageResolved {
   get {
    if (this.Override.NodeAppearance.Image != null) {
     if (this.Override.NodeAppearance.Image.GetType().Equals(typeof(Image))) {
      return (Image)this.Override.NodeAppearance.Image;
     } else {
      if (this.Control != null && this.Control.ImageList != null)
       return this.Override.NodeAppearance.GetImage(this.Control.ImageList);
     }
    }
    return null;
   }
  }
  public void SetIndividualImage(Image image) {
   if (image == null) {
    this.Override.NodeAppearance.Image = this._initialImage;
    this.Override.SelectedNodeAppearance.Image = this._initialImage;
    this.Override.ExpandedNodeAppearance.Image = this._initialExpandedImage;
   } else {
    this._initialImage = this._initialExpandedImage = image;
    this.Override.NodeAppearance.Image = image;
    this.Override.SelectedNodeAppearance.Image = image;
    this.Override.ExpandedNodeAppearance.Image = image;
    this.m_hasCustomIcon = true;
   }
  }
  public virtual bool HasCustomIcon {
   get { return m_hasCustomIcon; }
  }
  public int SelectedImageIndex{
   get{ return this._selectedImageIndex; }
  }
  public int ImageIndex{
   get{ return this._imageIndex; }
  }
  public virtual FeedNodeType Type { get { return _type; } set { _type = value; } }
  public virtual bool Editable { get { return _editable; } set { _editable = value; } }
  public virtual Color ForeColor {
   get { return base.Override.NodeAppearance.ForeColor; }
   set {
    if (base.Override.NodeAppearance.ForeColor != value)
     base.Override.NodeAppearance.ForeColor = value;
   }
  }
  public virtual TreeFeedsNodeBase FirstNode {
   get {
    if(base.Nodes.Count==0)
     return null;
    else
     return (TreeFeedsNodeBase)base.Nodes[0];
   }
  }
  public virtual TreeFeedsNodeBase NextNode {
   get {
    for(int i=0; base.Parent != null && i<base.Parent.Nodes.Count-1; i++) {
     if(base.Parent.Nodes[i]==this) {
      return (TreeFeedsNodeBase) base.Parent.Nodes[i + 1];
     }
    }
    return null;
   }
  }
  public virtual TreeFeedsNodeBase LastNode {
   get {
    if(base.Nodes.Count==0)
     return null;
    else
     return (TreeFeedsNodeBase)base.Nodes[Nodes.Count-1];
   }
  }
  public virtual new TreeFeedsNodeBase Parent {
   get { return (TreeFeedsNodeBase)base.Parent; }
  }
  public virtual new string Text {
   get { return base.Text; }
   set {
    if (StringHelper.EmptyTrimOrNull(value))
     base.Text = SR.GeneralNewItemText;
    else
     base.Text = value;
   }
  }
  protected void InvalidateNode() {
   bool yetInvalidated = false;
   if (_highlightCount > 0) {
    if (!FontColorHelper.StyleEqual(this.Override.NodeAppearance.FontData, FontColorHelper.HighlightStyle)) {
     FontColorHelper.CopyFromFont(this.Override.NodeAppearance.FontData, FontColorHelper.HighlightFont);
     yetInvalidated = true;
    }
    if (this.ForeColor != FontColorHelper.HighlightColor) {
     this.ForeColor = FontColorHelper.HighlightColor;
     yetInvalidated = true;
    }
   } else if (_unreadCount > 0 || _anyUnread) {
    if (!FontColorHelper.StyleEqual(this.Override.NodeAppearance.FontData, FontColorHelper.UnreadStyle)) {
     FontColorHelper.CopyFromFont(this.Override.NodeAppearance.FontData, FontColorHelper.UnreadFont);
     yetInvalidated = true;
    }
    if (this.ForeColor != FontColorHelper.UnreadColor) {
     this.ForeColor = FontColorHelper.UnreadColor;
     yetInvalidated = true;
    }
   } else {
    if (!FontColorHelper.StyleEqual(this.Override.NodeAppearance.FontData, FontColorHelper.NormalStyle)) {
     FontColorHelper.CopyFromFont(this.Override.NodeAppearance.FontData, FontColorHelper.NormalFont);
     yetInvalidated = true;
    }
    if (this.ForeColor != FontColorHelper.NormalColor) {
     this.ForeColor = FontColorHelper.NormalColor;
     yetInvalidated = true;
    }
   }
   if (this._itemsWithNewCommentsCount > 0 || this._anyNewComments) {
    if (!FontColorHelper.StyleEqual(this.Override.NodeAppearance.FontData, FontColorHelper.NewCommentsStyle)) {
     Font font = FontColorHelper.MergeFontStyles(FontColorHelper.CopyToFont(this.Override.NodeAppearance.FontData), FontColorHelper.NewCommentsStyle);
     FontColorHelper.CopyFromFont(this.Override.NodeAppearance.FontData, font);
     yetInvalidated = true;
    }
    if (this.ForeColor != FontColorHelper.NewCommentsColor) {
     this.ForeColor = FontColorHelper.NewCommentsColor;
     yetInvalidated = true;
    }
   }
   if (!yetInvalidated && this.Control != null)
    this.Control.Invalidate();
  }
  public virtual bool AnyNewComments {
   get { return (_anyNewComments || _itemsWithNewCommentsCount > 0); }
   set {
    if (_anyNewComments != value) {
     _anyNewComments = value;
     InvalidateNode();
    }
   }
  }
  public virtual int ItemsWithNewCommentsCount {
   get { return _itemsWithNewCommentsCount; }
   set {
    if (value != _itemsWithNewCommentsCount) {
     _itemsWithNewCommentsCount = value;
     _anyNewComments = (_itemsWithNewCommentsCount > 0);
     InvalidateNode();
    }
   }
  }
  public virtual bool AnyUnread {
   get { return (_anyUnread || _unreadCount > 0); }
   set {
    if (_anyUnread != value) {
     _anyUnread = value;
     InvalidateNode();
    }
   }
  }
  public virtual int UnreadCount {
   get { return _unreadCount; }
   set {
    if (value != _unreadCount) {
     _unreadCount = value;
     _anyUnread = (_unreadCount > 0);
     InvalidateNode();
     if (_unreadCount == 0)
      RaiseReadCounterZero();
    }
   }
  }
  private void RaiseReadCounterZero() {
   if (this.ReadCounterZero != null)
    this.ReadCounterZero(this, EventArgs.Empty);
  }
  public virtual int HighlightCount {
   get { return _highlightCount; }
   set {
    if (value != _highlightCount) {
     _highlightCount = value;
     InvalidateNode();
    }
   }
  }
  public virtual string CategoryStoreName {
   get {
    return BuildCategoryStoreName(this);
   }
  }
  public void UpdateCommentStatus(TreeFeedsNodeBase thisNode, int readCounter) {
   if (thisNode == null) return;
   if (readCounter <= 0) {
    if (this.Equals(thisNode)) {
     if (thisNode.ItemsWithNewCommentsCount < Math.Abs(readCounter))
      readCounter = -thisNode.ItemsWithNewCommentsCount;
     if (readCounter == 0)
      readCounter = -thisNode.ItemsWithNewCommentsCount;
     thisNode.ItemsWithNewCommentsCount += readCounter;
     UpdateCommentStatus(thisNode.Parent, readCounter);
    }
    else {
     thisNode.ItemsWithNewCommentsCount += readCounter;
     UpdateCommentStatus(thisNode.Parent, readCounter);
    }
   } else {
    if (thisNode.Nodes.Count == 0 ) {
     UpdateCommentStatus(thisNode.Parent, -thisNode.ItemsWithNewCommentsCount);
     thisNode.ItemsWithNewCommentsCount = readCounter;
    } else {
     thisNode.ItemsWithNewCommentsCount += readCounter;
    }
    UpdateCommentStatus(thisNode.Parent, readCounter);
   }
  }
  public void UpdateCommentStatus(TreeFeedsNodeBase thisNode, bool anyNewComments) {
   if (thisNode == null) return;
   if (!anyNewComments) {
    if (this.Equals(thisNode)) {
     thisNode.AnyNewComments = false;
     UpdateCommentStatus(thisNode, 0);
    }
    else {
     thisNode.AnyNewComments = false;
     UpdateCommentStatus(thisNode.Parent, false);
    }
   }
   else {
    thisNode.AnyNewComments = true;
    UpdateCommentStatus(thisNode.Parent, true);
   }
  }
  public void UpdateReadStatus(TreeFeedsNodeBase thisNode, int readCounter) {
   if (thisNode == null) return;
   if (readCounter <= 0) {
    if (this.Equals(thisNode)) {
     if (thisNode.UnreadCount < Math.Abs(readCounter))
      readCounter = -thisNode.UnreadCount;
     if (readCounter == 0)
      readCounter = -thisNode.UnreadCount;
     thisNode.UnreadCount += readCounter;
     UpdateReadStatus(thisNode.Parent, readCounter);
    }
    else {
     thisNode.UnreadCount += readCounter;
     UpdateReadStatus(thisNode.Parent, readCounter);
    }
   } else {
    if (thisNode.Nodes.Count == 0 ) {
     UpdateReadStatus(thisNode.Parent, -thisNode.UnreadCount);
     thisNode.UnreadCount = readCounter;
    } else {
     thisNode.UnreadCount += readCounter;
    }
    UpdateReadStatus(thisNode.Parent, readCounter);
   }
  }
  public void UpdateReadStatus(TreeFeedsNodeBase thisNode, bool anyUnread) {
   if (thisNode == null) return;
   if (!anyUnread) {
    if (this.Equals(thisNode)) {
     thisNode.AnyUnread = false;
     UpdateReadStatus(thisNode, 0);
    }
    else {
     thisNode.AnyUnread = false;
     UpdateReadStatus(thisNode.Parent, false);
    }
   }
   else {
    thisNode.AnyUnread = true;
    UpdateReadStatus(thisNode.Parent, true);
   }
  }
  public static string BuildCategoryStoreName(TreeFeedsNodeBase node) {
   string[] catArray = BuildCategoryStoreNameArray(node);
   if (catArray.Length == 0)
    return null;
   return String.Join(NewsHandler.CategorySeparator, catArray);
  }
  public static string[] BuildCategoryStoreNameArray(TreeFeedsNodeBase node) {
   if (node == null)
    return new string[]{};
   if (node.Type == FeedNodeType.Feed || node.Type == FeedNodeType.Finder) {
    return TreeHelper.BuildCategoryStoreNameArray(node.FullPath, true);
   } else {
    return TreeHelper.BuildCategoryStoreNameArray(node.FullPath, false);
   }
  }
  public string TypedRootFullPath {
   get {
    string[] a = this.FullPath.Split(NewsHandler.CategorySeparator.ToCharArray());
    if (a.Length > 0) {
     a[0] = this.RootNode.GetType().Name;
    }
    return String.Join(NewsHandler.CategorySeparator, a);
   }
  }
  public abstract bool AllowedChild(FeedNodeType nsType);
  public abstract void PopupMenu(System.Drawing.Point screenPos);
  public abstract void UpdateContextMenu();
 }
}
