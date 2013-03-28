using System;
using System.Collections;
using System.Windows.Forms;
namespace System.Windows.Forms.ThListView
{
 public enum ThreadedListViewColumnHeaderChangedAction {
  Add,
  Remove
 }
 public delegate void ColumnHeaderCollectionChangedHandler(object sender, ThreadedListViewColumnHeaderChangedEventArgs e);
 public class ThreadedListViewColumnHeaderChangedEventArgs: EventArgs {
  public ThreadedListViewColumnHeader[] Columns;
  public ThreadedListViewColumnHeaderChangedAction Action;
  public ThreadedListViewColumnHeaderChangedEventArgs(ThreadedListViewColumnHeader[] c, ThreadedListViewColumnHeaderChangedAction a) {
   this.Columns = c;
   this.Action = a;
  }
 }
 public class ThreadedListViewColumnHeaderCollection: System.Windows.Forms.ListView.ColumnHeaderCollection
 {
  public event ColumnHeaderCollectionChangedHandler OnColumnHeaderCollectionChanged;
  public ThreadedListViewColumnHeaderCollection(ThreadedListView owner): base(((ListView)owner)) { }
  public int Add(ThreadedListViewColumnHeader colHeader) {
   int idx = base.Add(colHeader);
   RaiseThreadedListViewColumnHeaderChangedEvent(
    new ThreadedListViewColumnHeader[]{colHeader},
    ThreadedListViewColumnHeaderChangedAction.Add);
   return idx;
  }
  public new ThreadedListViewColumnHeader Add(string caption, int width, HorizontalAlignment textAlign) {
   return Add(null, caption, typeof(string), width, textAlign);
  }
  public ThreadedListViewColumnHeader Add(string id, string caption, Type valueType, int width, HorizontalAlignment textAlign) {
   ThreadedListViewColumnHeader c = new ThreadedListViewColumnHeader(id, valueType);
   c.Text = caption;
   c.Width = width;
   c.TextAlign = textAlign;
   int index = base.Add(c);
   RaiseThreadedListViewColumnHeaderChangedEvent(
    new ThreadedListViewColumnHeader[]{c},
    ThreadedListViewColumnHeaderChangedAction.Add);
   return c;
  }
  public void AddRange(ThreadedListViewColumnHeader[] columns) {
   base.AddRange(columns);
   RaiseThreadedListViewColumnHeaderChangedEvent(columns,
    ThreadedListViewColumnHeaderChangedAction.Add);
  }
  public void Remove(string columnID) {
   ThreadedListViewColumnHeader c = this[columnID];
   base.Remove(c);
   RaiseThreadedListViewColumnHeaderChangedEvent(
    new ThreadedListViewColumnHeader[]{c},
    ThreadedListViewColumnHeaderChangedAction.Remove);
  }
  public new ThreadedListViewColumnHeader this[int displayIndex] {
   get {
    return ((ThreadedListViewColumnHeader)base[displayIndex]);
   }
  }
  public ThreadedListViewColumnHeader this[string columnID] {
   get {
    if (columnID == null)
     throw new ArgumentNullException("columnID");
    int idx = this.GetIndexByKey(columnID);
    if (idx < 0)
     throw new InvalidOperationException("No ThreadedListViewColumnHeader found with ID '" + columnID + "'");
    return this[idx];
   }
  }
  public int GetIndexByKey(string columnID) {
   for (int i=0; i < base.Count; i++) {
    ThreadedListViewColumnHeader c = this[i];
    if (c.Key == columnID)
     return i;
   }
   return -1;
  }
  public ColumnKeyIndexMap GetColumnIndexMap() {
   ColumnKeyIndexMap map = new ColumnKeyIndexMap(base.Count);
   lock (this) {
    for (int i=0; i < base.Count; i++) {
     ThreadedListViewColumnHeader c = this[i];
     map.Add(c.Key, i);
    }
   }
   return map;
  }
  private void RaiseThreadedListViewColumnHeaderChangedEvent(ThreadedListViewColumnHeader[] c, ThreadedListViewColumnHeaderChangedAction a) {
   if (OnColumnHeaderCollectionChanged != null)
    OnColumnHeaderCollectionChanged(this, new ThreadedListViewColumnHeaderChangedEventArgs(c, a));
  }
 }
 public class ColumnKeyIndexMap: Hashtable {
  public ColumnKeyIndexMap(): base() {}
  public ColumnKeyIndexMap(int capacity): base(capacity) {}
  public ColumnKeyIndexMap(IDictionary d): base(d) {}
  public int this[string key] {
   get {
    return (int)base[key];
   }
   set {
    base[key] = value;
   }
  }
  public void Add(string key, int value) {
   base.Add (key, value);
  }
  public bool Contains(string key) {
   return base.Contains (key);
  }
  public bool ContainsKey(string key) {
   return base.ContainsKey (key);
  }
  public bool ContainsValue(int value) {
   return base.ContainsValue (value);
  }
 }
}
