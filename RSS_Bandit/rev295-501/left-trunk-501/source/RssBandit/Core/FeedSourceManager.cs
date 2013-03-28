using System;
using System.Collections.Generic;
using NewsComponents;
namespace RssBandit
{
 internal class ItemID
 {
  public readonly Guid SourceID;
  public ItemID(Guid sourceID, string itemID)
  {
   this.SourceID = sourceID;
   this._itemID = itemID;
  }
  public string FeedUrl
  {
   get { return _itemID; }
   set { _itemID = value; }
  }
  private string _itemID;
  public override string ToString()
  {
   return String.Format("{0} ({1})", _itemID, SourceID);
  }
 }
 internal class FeedSourceID : IComparable<FeedSourceID>
 {
  public readonly Guid ID;
  public readonly FeedSource Source;
  public FeedSourceID(FeedSource source, string name, int ordinal)
  {
   ID = new Guid();
   _name = name;
   _ordinal = ordinal;
   Source = source;
  }
  public string Name
  {
   get { return _name; }
   set { _name = value; }
  }
  private string _name;
  public int Ordinal
  {
   get { return _ordinal; }
   set { _ordinal = value; }
  }
  private int _ordinal;
  int IComparable<FeedSourceID>.CompareTo(FeedSourceID other)
  {
   if (other == null) return 1;
   if (Object.ReferenceEquals(other, this) ||
    Ordinal == other.Ordinal) return 0;
   if (Ordinal > other.Ordinal) return 1;
   return -1;
  }
 }
 internal class FeedSourceManager
 {
  readonly Dictionary<Guid, FeedSourceID> _feedSources = new Dictionary<Guid, FeedSourceID>();
  public IEnumerable<FeedSource> Sources
  {
   get {
    foreach (FeedSourceID entry in _feedSources.Values)
    {
     yield return entry.Source;
    }
   }
  }
  public List<FeedSourceID> GetOrderedFeedSources() {
   List<FeedSourceID> sources = new List<FeedSourceID>(_feedSources.Values);
   sources.Sort(Comparer<FeedSourceID>.Default);
   return sources;
  }
  public void ForEach(Action<FeedSource> action)
  {
   foreach (FeedSourceID entry in _feedSources.Values) {
    action(entry.Source);
   }
  }
  public FeedSourceID Add(FeedSource source, string name)
  {
   FeedSourceID fs = new FeedSourceID(source, name, _feedSources.Count);
   _feedSources.Add(fs.ID, fs);
   return fs;
  }
  public void Remove(FeedSourceID source)
  {
   if (_feedSources.ContainsKey(source.ID))
    _feedSources.Remove(source.ID);
  }
  public FeedSourceID GetSourceOf(ItemID item) {
   return _feedSources[item.SourceID];
  }
 }
}
