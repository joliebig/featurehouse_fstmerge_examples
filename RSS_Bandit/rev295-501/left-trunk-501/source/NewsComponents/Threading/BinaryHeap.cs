using System;
using System.Collections;
namespace NewsComponents.Collections
{
 public class BinaryHeap : ICollection, ICloneable
 {
  private ArrayList _list;
  public BinaryHeap(BinaryHeap heap)
  {
   _list = (ArrayList)heap._list.Clone();
  }
  public BinaryHeap(int capacity) { _list = new ArrayList(capacity); }
  public BinaryHeap() { _list = new ArrayList(); }
  public virtual void Clear() { _list.Clear(); }
  public virtual BinaryHeap Clone() { return new BinaryHeap(this); }
  public virtual bool Contains(object value)
  {
   foreach(BinaryHeapEntry entry in _list)
   {
    if (entry.Value == value) return true;
   }
   return false;
  }
  public virtual void Insert(IComparable key, object value)
  {
   BinaryHeapEntry entry = new BinaryHeapEntry(key, value);
   int pos = _list.Add(entry);
   if (pos == 0) return;
   while(pos > 0)
   {
    int nextPos = pos / 2;
    BinaryHeapEntry toCheck = (BinaryHeapEntry)_list[nextPos];
    if (entry.CompareTo(toCheck) > 0)
    {
     _list[pos] = toCheck;
     pos = nextPos;
    }
    else break;
   }
   _list[pos] = entry;
  }
  public virtual object Remove()
  {
   if (_list.Count == 0) throw new InvalidOperationException("Cannot remove an item from the heap as it is empty.");
   object toReturn = ((BinaryHeapEntry)_list[0]).Value;
   _list.RemoveAt(0);
   if (_list.Count > 1)
   {
    _list.Insert(0, _list[_list.Count - 1]);
    _list.RemoveAt(_list.Count - 1);
    int current=0, possibleSwap=0;
    while(true)
    {
     int leftChildPos = 2*current + 1;
     int rightChildPos = leftChildPos + 1;
     if (leftChildPos < _list.Count)
     {
      BinaryHeapEntry entry1 = (BinaryHeapEntry)_list[current];
      BinaryHeapEntry entry2 = (BinaryHeapEntry)_list[leftChildPos];
      if (entry2.CompareTo(entry1) > 0) possibleSwap = leftChildPos;
     }
     else break;
     if (rightChildPos < _list.Count)
     {
      BinaryHeapEntry entry1 = (BinaryHeapEntry)_list[possibleSwap];
      BinaryHeapEntry entry2 = (BinaryHeapEntry)_list[rightChildPos];
      if (entry2.CompareTo(entry1) > 0) possibleSwap = rightChildPos;
     }
     if (current != possibleSwap)
     {
      object temp = _list[current];
      _list[current] = _list[possibleSwap];
      _list[possibleSwap] = temp;
     }
     else break;
     current = possibleSwap;
    }
   }
   return toReturn;
  }
  object ICloneable.Clone() { return Clone(); }
  public virtual void CopyTo(System.Array array, int index)
  {
   _list.CopyTo(array, index);
  }
  public virtual bool IsSynchronized { get { return false; } }
  public virtual int Count { get { return _list.Count; } }
  public object SyncRoot { get { return this; } }
  public virtual IEnumerator GetEnumerator()
  {
   return new BinaryHeapEnumerator(_list.GetEnumerator());
  }
  public class BinaryHeapEnumerator : IEnumerator
  {
   private IEnumerator _enumerator;
   internal BinaryHeapEnumerator(IEnumerator enumerator)
   {
    _enumerator = enumerator;
   }
   public void Reset() { _enumerator.Reset(); }
   public bool MoveNext() { return _enumerator.MoveNext(); }
   public object Current
   {
    get
    {
     BinaryHeapEntry entry = _enumerator.Current as BinaryHeapEntry;
     return entry != null ? entry.Value : null;
    }
   }
  }
  public static BinaryHeap Synchronize(BinaryHeap heap)
  {
   if (heap is SyncBinaryHeap) return heap;
   return new SyncBinaryHeap(heap);
  }
  private class BinaryHeapEntry : IComparable, ICloneable
  {
   private IComparable _key;
   private object _value;
   public BinaryHeapEntry(IComparable key, object value)
   {
    _key = key;
    _value = value;
   }
   public IComparable Key { get { return _key; } set { _key = value; } }
   public object Value { get { return _value; } set { _value = value; } }
   public int CompareTo(BinaryHeapEntry entry)
   {
    if (entry == null) throw new ArgumentNullException("entry", "Cannot compare to a null value.");
    return _key.CompareTo(entry.Key);
   }
   int IComparable.CompareTo(object obj)
   {
    if (!(obj is BinaryHeapEntry)) throw new ArgumentException("Object is not a BinaryHeapEntry", "obj");
    return CompareTo((BinaryHeapEntry)obj);
   }
   public BinaryHeapEntry Clone()
   {
    return new BinaryHeapEntry(_key, _value);
   }
   object ICloneable.Clone()
   {
    return Clone();
   }
  }
  public class SyncBinaryHeap : BinaryHeap
  {
   private BinaryHeap _heap;
   internal SyncBinaryHeap(BinaryHeap heap) { _heap = heap; }
   public override BinaryHeap Clone()
   {
    lock(_heap.SyncRoot) return _heap.Clone();
   }
   public override void Clear()
   {
    lock(_heap.SyncRoot) _heap.Clear();
   }
   public override bool Contains(object value)
   {
    lock(_heap.SyncRoot) return _heap.Contains(value);
   }
   public override void Insert(IComparable key, object value)
   {
    lock(_heap.SyncRoot) _heap.Insert(key, value);
   }
   public override object Remove()
   {
    lock(_heap.SyncRoot) return _heap.Remove();
   }
   public override void CopyTo(System.Array array, int index)
   {
    lock(_heap.SyncRoot) _heap.CopyTo(array, index);
   }
   public override bool IsSynchronized
   {
    get { return true; }
   }
   public override int Count
   {
    get { lock(_heap.SyncRoot) return _heap.Count; }
   }
   public override IEnumerator GetEnumerator()
   {
    lock(_heap.SyncRoot) return _heap.GetEnumerator();
   }
  }
 }
}
