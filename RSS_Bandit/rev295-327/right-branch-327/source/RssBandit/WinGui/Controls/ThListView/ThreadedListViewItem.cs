using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.Diagnostics;
namespace System.Windows.Forms.ThListView
{
 public class ThreadedListViewItem:ListViewItem
 {
  private int _indentLevel = 0;
  private int _groupIndex = 0;
  private bool _hasChilds = false;
  private bool _isComment = false;
  internal bool _expanded = false;
  private object _key = null;
  private static int _originalIndex = 0;
  private ThreadedListViewItem _parent;
  public ThreadedListViewItem():base() {
   RaiseIndex();
  }
  public ThreadedListViewItem(string text):this(null, text) { }
  public ThreadedListViewItem(object key, string[] items):base(items) {
   RaiseIndex();
   _key = key;
  }
  public ThreadedListViewItem(object key, string text):base(text) {
   RaiseIndex();
   _key = key;
  }
  public ThreadedListViewItem(object key, string[] items, int imageIndex) : base(items, imageIndex) {
   RaiseIndex();
   _key = key;
  }
  public ThreadedListViewItem(ThreadedListViewItem.ListViewSubItem[] subItems, int imageIndex) : base(subItems, imageIndex) {
  }
  public ThreadedListViewItem(object key, string[] items, int imageIndex, Color foreColor, Color backColor, Font font) : base(items, imageIndex, foreColor, backColor, font) {
   RaiseIndex();
   _key = key;
  }
  public ThreadedListViewItem(object key, string text, int imageIndex, int groupIndex) : base(text, imageIndex) {
   RaiseIndex();
   _key = key;
   this.GroupIndex = groupIndex;
  }
  public ThreadedListViewItem(object key, string[] items, int imageIndex, int groupIndex) : base(items, imageIndex){
   RaiseIndex();
   _key = key;
   this.GroupIndex = groupIndex;
  }
  public ThreadedListViewItem(ThreadedListViewItem.ListViewSubItem[] subItems, int imageIndex, int groupIndex) : base(subItems, imageIndex) {
   this.GroupIndex = groupIndex;
  }
  public ThreadedListViewItem(object key, string[] items, int imageIndex, Color foreColor, Color backColor, Font font, int groupIndex) : base(items, imageIndex, foreColor, backColor, font) {
   RaiseIndex();
   _key = key;
   this.GroupIndex = groupIndex;
  }
  [Browsable(true), Category("Info")]
  public int GroupIndex {
   get {
    return _groupIndex;
   }
   set {
    _groupIndex = value;
    Win32.API.AddItemToGroup(base.ListView.Handle, base.Index, _groupIndex);
   }
  }
  [Browsable(false)]
  internal string[] SubItemsArray {
   get {
    if (this.SubItems.Count == 0) {
     return null;
    }
    string[] a = new string[this.SubItems.Count - 1];
    for (int i = 0; i <= this.SubItems.Count - 1; i++) {
     a[i] = this.SubItems[i].Text;
    }
    return a;
   }
  }
  public ThreadedListViewItem Parent {
   get { return _parent; }
   set { _parent = value; }
  }
  public new ThreadedListView ListView {
   get { return (ThreadedListView)base.ListView; }
  }
  public object Key {
   get { return _key; }
   set { _key = value; }
  }
  public object[] KeyPath {
   get {
    Stack s = new Stack(this.IndentLevel + 1);
    s.Push(this.Key);
    if (base.ListView != null && base.ListView.Items.Count > 0){
     int currentIndent = this.IndentLevel;
     for (int i = this.Index-1; i >= 0 && currentIndent > 0; i--) {
      ThreadedListViewItem lvi = (ThreadedListViewItem)base.ListView.Items[i];
      if (lvi.IndentLevel < currentIndent) {
       s.Push(lvi.Key);
       currentIndent = lvi.IndentLevel;
      }
     }
    }
    return s.ToArray();
   }
  }
  public int IndentLevel
  {
   get { return _indentLevel; }
   set
   {
    _indentLevel = value;
    if (_indentLevel > 0 && base.ListView != null)
     this.SetListViewItemIndent(_indentLevel);
   }
  }
  public void SetSubItemImage(int subItemIndex, int imageIndex) {
   this.SetListViewSubItemImage(subItemIndex, imageIndex);
  }
  public void ClearSubItemImage(int subItemIndex) {
   this.SetListViewSubItemImage(subItemIndex, -1);
  }
  internal int OriginalIndex {
   get { return _originalIndex; }
  }
  internal static void ResetOriginalIndex() {
   System.Threading.Interlocked.Exchange(ref _originalIndex, 0);
  }
  internal protected void RaiseIndex() {
   try {
    checked {
     System.Threading.Interlocked.Increment(ref _originalIndex);
    }
   } catch (OverflowException) {
    ResetOriginalIndex();
   }
  }
  public virtual bool HasChilds {
   get { return _hasChilds; }
   set {
    if (value)
     this.StateImageIndex = 2;
    else
     this.StateImageIndex = 0;
    _hasChilds = value;
   }
  }
  public virtual bool IsComment{
   get { return _isComment; }
   set { _isComment = value; }
  }
  public virtual bool Expanded {
   get { return _expanded; }
   set {
    if (value) {
     if (this.ListView != null)
      this.ListView.ExpandListViewItem(this, false);
    } else {
     if (this.ListView != null)
      this.ListView.CollapseListViewItem(this);
    }
    this.SetThreadState(value);
   }
  }
  internal void SetThreadState(bool expanded) {
   if (expanded) {
    this.StateImageIndex = 3;
   } else {
    this.StateImageIndex = 2;
   }
   _expanded = expanded;
  }
  public virtual bool Collapsed {
   get { return !this.Expanded; }
   set { this.Expanded = !value; }
  }
  internal void ApplyIndentLevel()
  {
   IndentLevel = _indentLevel;
  }
  public bool StateImageHitTest(Point p)
  {
   Win32.LVHITTESTINFO htInfo;
            IntPtr ret;
   htInfo.pt.x = p.X;
   htInfo.pt.y = p.Y;
   htInfo.flags = 0;
   htInfo.iItem = 0;
   htInfo.iSubItem = 0;
   ret = Win32.API.SendMessage((IntPtr)base.ListView.Handle,Win32.W32_LVM.LVM_SUBITEMHITTEST , 0, ref htInfo);
   if ((Win32.ListViewHitTestFlags)htInfo.flags == Win32.ListViewHitTestFlags.LVHT_ONITEMSTATEICON)
    return true;
   return false;
  }
  private void SetListViewSubItemImage(int subItem, int imageIndex) {
   Win32.LVITEM lvi = new Win32.LVITEM();
   lvi.iItem = base.Index;
   lvi.iSubItem = subItem;
   lvi.iImage = imageIndex;
   lvi.mask = Win32.ListViewItemFlags.LVIF_IMAGE;
   Win32.API.SendMessage((IntPtr)base.ListView.Handle, Win32.W32_LVM.LVM_SETITEMA , 0, ref lvi);
  }
  private void SetListViewItemIndent(int level)
  {
   Win32.LVITEM lvi = new Win32.LVITEM();
   lvi.iItem = base.Index;
   lvi.iIndent = level;
   lvi.mask = Win32.ListViewItemFlags.LVIF_INDENT;
   Win32.API.SendMessage((IntPtr)base.ListView.Handle, Win32.W32_LVM.LVM_SETITEMA , 0, ref lvi);
  }
  private int GetListViewItemIndent()
  {
   Win32.LVITEM lvi = new Win32.LVITEM();
   int ret;
   lvi.iItem = base.Index;
   lvi.mask = Win32.ListViewItemFlags.LVIF_INDENT;
   Win32.API.SendMessage((IntPtr)base.ListView.Handle, Win32.W32_LVM.LVM_GETITEMA , 0, ref lvi);
   ret = lvi.iIndent;
   return ret;
  }
 }
 public class ThreadedListViewItemPlaceHolder: ThreadedListViewItem {
  private string _insertionPointTicket;
  public ThreadedListViewItemPlaceHolder():base() {
   CreateInsertionPointTicket();
  }
  public ThreadedListViewItemPlaceHolder(string text):base(null, text) {
   CreateInsertionPointTicket();
  }
  public ThreadedListViewItemPlaceHolder(string[] items):base(null, items) {
   CreateInsertionPointTicket();
  }
  public string InsertionPointTicket { get { return this._insertionPointTicket; } }
  private void CreateInsertionPointTicket() {
   this._insertionPointTicket = Guid.NewGuid().ToString();
  }
 }
}
