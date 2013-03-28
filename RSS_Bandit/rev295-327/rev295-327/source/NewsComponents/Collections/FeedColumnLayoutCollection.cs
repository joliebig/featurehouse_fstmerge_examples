using System; 
using System.Collections; 
using System.Collections.Specialized; 
using NewsComponents.Feed; 
using System.Collections.Generic; namespace  NewsComponents.Collections {
	
 public interface  IFeedColumnLayoutCollection {
		
  int Count { get; } 
  bool IsSynchronized { get; } 
  object SyncRoot { get; } 
  void CopyTo(FeedColumnLayout[] array, int arrayIndex); 
  IFeedColumnLayoutEnumerator GetEnumerator();
	}
	
 public interface 
  IFeedColumnLayoutList :  IFeedColumnLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  FeedColumnLayout this[int index] { get; set; } 
  int Add(FeedColumnLayout value); 
  void Clear(); 
  bool Contains(FeedColumnLayout value); 
  int IndexOf(FeedColumnLayout value); 
  void Insert(int index, FeedColumnLayout value); 
  void Remove(FeedColumnLayout value); 
  void RemoveAt(int index);
	}
	
 public interface  IFeedColumnLayoutEnumerator {
		
  FeedColumnLayout Current { get; } 
  bool MoveNext(); 
  void Reset();
	}
	
 public interface  IStringFeedColumnLayoutCollection {
		
  int Count { get; } 
  bool IsSynchronized { get; } 
  object SyncRoot { get; } 
  void CopyTo(FeedColumnLayoutEntry[] array, int arrayIndex); 
  IStringFeedColumnLayoutEnumerator GetEnumerator();
	}
	
 public interface 
  IStringFeedColumnLayoutDictionary :  IStringFeedColumnLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  FeedColumnLayout this[String key] { get; set; } 
  ICollection<string> Keys { get; } 
  IFeedColumnLayoutCollection Values { get; } 
  void Add(String key, FeedColumnLayout value); 
  void Clear(); 
  bool Contains(String key); 
  void Remove(String key);
	}
	
 public interface 
  IStringFeedColumnLayoutList :  IStringFeedColumnLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  FeedColumnLayoutEntry this[int index] { get; set; } 
  int Add(FeedColumnLayoutEntry entry); 
  void Clear(); 
  bool Contains(FeedColumnLayoutEntry entry); 
  int IndexOf(FeedColumnLayoutEntry entry); 
  void Insert(int index, FeedColumnLayoutEntry entry); 
  void Remove(FeedColumnLayoutEntry entry); 
  void RemoveAt(int index);
	}
	
 public interface  IStringFeedColumnLayoutEnumerator {
		
  FeedColumnLayoutEntry Current { get; } 
  FeedColumnLayoutEntry Entry { get; } 
  String Key { get; } 
  FeedColumnLayout Value { get; } 
  bool MoveNext(); 
  void Reset();
	}
	
 [Serializable] 
 public struct  FeedColumnLayoutEntry {
		
  private  String _key;
 
  private  FeedColumnLayout _value;
 
  public  FeedColumnLayoutEntry(String key, FeedColumnLayout value) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   this._key = key;
   this._value = value;
  }
 
  public  String Key {
   get { return this._key; }
   set {
    if ((object) value == null)
     throw new ArgumentNullException("value");
    this._key = value;
   }
  }
 
  public  FeedColumnLayout Value {
   get { return this._value; }
   set { this._value = value; }
  }
 
  public static  implicit operator FeedColumnLayoutEntry(DictionaryEntry entry) {
   FeedColumnLayoutEntry pair = new FeedColumnLayoutEntry();
   if (entry.Key != null) pair.Key = (String) entry.Key;
   if (entry.Value != null) pair.Value = (FeedColumnLayout) entry.Value;
   return pair;
  } 
  public static  implicit operator DictionaryEntry(FeedColumnLayoutEntry pair) {
   DictionaryEntry entry = new DictionaryEntry();
   if (pair.Key != null) entry.Key = pair.Key;
   entry.Value = pair.Value;
   return entry;
  }
	}
	
 [Serializable] 
 public class  FeedColumnLayoutCollection :
  IStringFeedColumnLayoutList, IList, ICloneable {
		
  private  const int _defaultCapacity = 16; 
  private  String[] _keys;
 
  private  FeedColumnLayout[] _values;
 
  private  int _count;
 
  [NonSerialized] 
  private  int _version;
 
  private  KeyList _keyList;
 
  private  ValueList _valueList;
 
  private enum  Tag  { Default } 
  private  FeedColumnLayoutCollection(Tag tag) { }
 
  public  FeedColumnLayoutCollection() {
   this._keys = new String[_defaultCapacity];
   this._values = new FeedColumnLayout[_defaultCapacity];
  }
 
  public  FeedColumnLayoutCollection(int capacity) {
   if (capacity < 0)
    throw new ArgumentOutOfRangeException("capacity",
     capacity, "Argument cannot be negative.");
   this._keys = new String[capacity];
   this._values = new FeedColumnLayout[capacity];
  }
 
  public  FeedColumnLayoutCollection(FeedColumnLayoutCollection collection) {
   if (collection == null)
    throw new ArgumentNullException("collection");
   this._keys = new String[collection.Count];
   this._values = new FeedColumnLayout[collection.Count];
   AddRange(collection);
  }
 
  public  FeedColumnLayoutCollection(FeedColumnLayoutEntry[] array) {
   if (array == null)
    throw new ArgumentNullException("array");
   this._keys = new String[array.Length];
   this._values = new FeedColumnLayout[array.Length];
   AddRange(array);
  }
 
  protected virtual  String[] InnerKeys {
   get { return this._keys; }
  }
 
  protected virtual  FeedColumnLayout[] InnerValues {
   get { return this._values; }
  }
 
  public virtual  int Capacity {
   get { return this._keys.Length; }
   set {
    if (value == this._keys.Length) return;
    if (value < this._count)
     throw new ArgumentOutOfRangeException("Capacity",
      value, "Value cannot be less than Count.");
    if (value == 0) {
     this._keys = new String[_defaultCapacity];
     this._values = new FeedColumnLayout[_defaultCapacity];
     return;
    }
    String[] newKeys = new String[value];
    FeedColumnLayout[] newValues = new FeedColumnLayout[value];
    Array.Copy(this._keys, 0, newKeys, 0, this._count);
    Array.Copy(this._values, 0, newValues, 0, this._count);
    this._keys = newKeys;
    this._values = newValues;
   }
  }
 
  public virtual  int Count {
   get { return this._count; }
  }
 
  public virtual  bool IsFixedSize {
   get { return false; }
  }
 
  public virtual  bool IsReadOnly {
   get { return false; }
  }
 
  public virtual  bool IsSynchronized {
   get { return false; }
  }
 
  public virtual  FeedColumnLayoutEntry this[int index] {
   get {
    ValidateIndex(index);
    return new FeedColumnLayoutEntry(this._keys[index], this._values[index]);
   }
   set {
    ValidateIndex(index);
    ++this._version;
    this._keys[index] = value.Key;
    this._values[index] = value.Value;
   }
  }
 
  object IList.this[int index] {
   get { return (DictionaryEntry) this[index]; }
   set { this[index] = (FeedColumnLayoutEntry) (DictionaryEntry) value; }
  }
 
  public virtual  ICollection<string> Keys {
   get { return GetKeyList(); }
  }
 
  public virtual  object SyncRoot {
   get { return this; }
  }
 
  public virtual  IFeedColumnLayoutCollection Values {
   get { return GetValueList(); }
  }
 
  public  int IndexOfSimilar(FeedColumnLayout layout)
  {
   for (int i = 0; i < this._count; ++i)
    if (this._values[i] != null && this._values[i].Equals(layout, true))
     return i;
   return -1;
  }
 
  public virtual  int Add(FeedColumnLayoutEntry entry) {
   if (this._count == this._keys.Length)
    EnsureCapacity(this._count + 1);
   ++this._version;
   this._keys[this._count] = entry.Key;
   this._values[this._count] = entry.Value;
   return this._count++;
  }
 
  public  int Add(String key, FeedColumnLayout value) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   return Add(new FeedColumnLayoutEntry(key, value));
  }
 
  int IList.Add(object entry) {
   return Add((FeedColumnLayoutEntry) entry);
  }
 
  public virtual  void AddRange(FeedColumnLayoutCollection collection) {
   if (collection == null)
    throw new ArgumentNullException("collection");
   if (collection.Count == 0) return;
   if (this._count + collection.Count > this._keys.Length)
    EnsureCapacity(this._count + collection.Count);
   ++this._version;
   Array.Copy(collection.InnerKeys, 0, this._keys, this._count, collection.Count);
   Array.Copy(collection.InnerValues, 0, this._values, this._count, collection.Count);
   this._count += collection.Count;
  }
 
  public virtual  void AddRange(FeedColumnLayoutEntry[] array) {
   if (array == null)
    throw new ArgumentNullException("array");
   if (array.Length == 0) return;
   if (this._count + array.Length > this._keys.Length)
    EnsureCapacity(this._count + array.Length);
   ++this._version;
   for (int i = 0; i < array.Length; ++i, ++this._count) {
    this._keys[this._count] = array[i].Key;
    this._values[this._count] = array[i].Value;
   }
  }
 
  public virtual  void Clear() {
   if (this._count == 0) return;
   ++this._version;
   Array.Clear(this._keys, 0, this._count);
   Array.Clear(this._values, 0, this._count);
   this._count = 0;
  }
 
  public virtual  object Clone() {
   FeedColumnLayoutCollection collection = new FeedColumnLayoutCollection(this._count);
   Array.Copy(this._keys, 0, collection._keys, 0, this._count);
   Array.Copy(this._values, 0, collection._values, 0, this._count);
   collection._count = this._count;
   collection._version = this._version;
   return collection;
  }
 
  public virtual  bool Contains(FeedColumnLayoutEntry entry) {
   return (IndexOf(entry) >= 0);
  }
 
  bool IList.Contains(object entry) {
   return Contains((FeedColumnLayoutEntry) entry);
  }
 
  public virtual  bool ContainsKey(String key) {
   return (IndexOfKey(key) >= 0);
  }
 
  public virtual  bool ContainsValue(FeedColumnLayout value) {
   return (IndexOfValue(value) >= 0);
  }
 
  public virtual  void CopyTo(FeedColumnLayoutEntry[] array, int arrayIndex) {
   CheckTargetArray(array, arrayIndex);
   for (int i = 0; i < this._count; i++) {
    FeedColumnLayoutEntry entry =
     new FeedColumnLayoutEntry(this._keys[i], this._values[i]);
    array.SetValue(entry, arrayIndex + i);
   }
  }
 
  void ICollection.CopyTo(Array array, int arrayIndex) {
   CopyTo((FeedColumnLayoutEntry[]) array, arrayIndex);
  }
 
  public virtual  bool Equals(FeedColumnLayoutCollection collection) {
   if (collection == null || this._count != collection.Count)
    return false;
   for (int i = 0; i < this._count; i++)
    if (this._keys[i] != collection.InnerKeys[i] ||
     this._values[i] != collection.InnerValues[i])
     return false;
   return true;
  }
 
  public virtual  FeedColumnLayout GetByIndex(int index) {
   ValidateIndex(index);
   return this._values[index];
  }
 
  public virtual  FeedColumnLayout GetByKey(String key) {
   int index = IndexOfKey(key);
   if (index >= 0) return this._values[index];
   return null;
  }
 
  public virtual  IStringFeedColumnLayoutEnumerator GetEnumerator() {
   return new Enumerator(this);
  }
 
  IEnumerator IEnumerable.GetEnumerator() {
   return (IEnumerator) GetEnumerator();
  }
 
  public virtual  String GetKey(int index) {
   ValidateIndex(index);
   return this._keys[index];
  }
 
  public virtual  ICollection<string> GetKeyList() {
   if (this._keyList == null)
    this._keyList = new KeyList(this);
   return this._keyList;
  }
 
  public virtual  IFeedColumnLayoutList GetValueList() {
   if (this._valueList == null)
    this._valueList = new ValueList(this);
   return this._valueList;
  }
 
  public virtual  int IndexOf(FeedColumnLayoutEntry entry) {
   for (int i = 0; i < this._count; ++i)
    if (entry.Key == this._keys[i] &&
     entry.Value == this._values[i])
     return i;
   return -1;
  }
 
  int IList.IndexOf(object entry) {
   return IndexOf((FeedColumnLayoutEntry) entry);
  }
 
  public virtual  int IndexOfKey(String key) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   return Array.IndexOf(this._keys, key, 0, this._count);
  }
 
  public virtual  int IndexOfValue(FeedColumnLayout value) {
   return Array.IndexOf(this._values, value, 0, this._count);
  }
 
  public virtual  void Insert(int index, FeedColumnLayoutEntry entry) {
   if (index < 0)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument cannot be negative.");
   if (index > this._count)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument cannot exceed Count.");
   if (this._count == this._keys.Length)
    EnsureCapacity(this._count + 1);
   ++this._version;
   if (index < this._count) {
    Array.Copy(this._keys, index,
     this._keys, index + 1, this._count - index);
    Array.Copy(this._values, index,
     this._values, index + 1, this._count - index);
   }
   this._keys[index] = entry.Key;
   this._values[index] = entry.Value;
   ++this._count;
  }
 
  void IList.Insert(int index, object entry) {
   Insert(index, (FeedColumnLayoutEntry) entry);
  }
 
  public virtual  void Remove(FeedColumnLayoutEntry entry) {
   int index = IndexOf(entry);
   if (index >= 0) RemoveAt(index);
  }
 
  void IList.Remove(object entry) {
   Remove((FeedColumnLayoutEntry) entry);
  }
 
  public virtual  void RemoveAt(int index) {
   ValidateIndex(index);
   ++this._version;
   if (index < --this._count) {
    Array.Copy(this._keys, index + 1,
     this._keys, index, this._count - index);
    Array.Copy(this._values, index + 1,
     this._values, index, this._count - index);
   }
   this._keys[this._count] = null;
   this._values[this._count] = null;
  }
 
  public virtual  void SetByIndex(int index, FeedColumnLayout value) {
   ValidateIndex(index);
   ++this._version;
   this._values[index] = value;
  }
 
  public virtual  int SetByKey(String key, FeedColumnLayout value) {
   int index = IndexOfKey(key);
   if (index >= 0) {
    this._values[index] = value;
    return index;
   }
   return Add(key, value);
  }
 
  public static  FeedColumnLayoutCollection Synchronized(FeedColumnLayoutCollection collection) {
   if (collection == null)
    throw new ArgumentNullException("collection");
   return new SyncList(collection);
  }
 
  public virtual  FeedColumnLayoutEntry[] ToArray() {
   FeedColumnLayoutEntry[] array = new FeedColumnLayoutEntry[this._count];
   CopyTo(array, 0);
   return array;
  }
 
  public virtual  void TrimToSize() {
   Capacity = this._count;
  }
 
  private  void CheckEnumIndex(int index) {
   if (index < 0 || index >= this._count)
    throw new InvalidOperationException(
     "Enumerator is not on a collection element.");
  }
 
  private  void CheckEnumVersion(int version) {
   if (version != this._version)
    throw new InvalidOperationException(
     "Enumerator invalidated by modification to collection.");
  }
 
  private  void CheckTargetArray(Array array, int arrayIndex) {
   if (array == null)
    throw new ArgumentNullException("array");
   if (array.Rank > 1)
    throw new ArgumentException(
     "Argument cannot be multidimensional.", "array");
   if (arrayIndex < 0)
    throw new ArgumentOutOfRangeException("arrayIndex",
     arrayIndex, "Argument cannot be negative.");
   if (arrayIndex >= array.Length)
    throw new ArgumentException(
     "Argument must be less than array length.", "arrayIndex");
   if (this._count > array.Length - arrayIndex)
    throw new ArgumentException(
     "Argument section must be large enough for collection.", "array");
  }
 
  private  void EnsureCapacity(int minimum) {
   int newCapacity = (this._keys.Length == 0 ?
   _defaultCapacity : this._keys.Length * 2);
   if (newCapacity < minimum) newCapacity = minimum;
   Capacity = newCapacity;
  }
 
  private  void ValidateIndex(int index) {
   if (index < 0)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument cannot be negative.");
   if (index >= this._count)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument must be less than Count.");
  }
 
  [Serializable] 
   private sealed class  Enumerator :
   IStringFeedColumnLayoutEnumerator, IDictionaryEnumerator {
			
   private readonly  FeedColumnLayoutCollection _collection;
 
   private readonly  int _version;
 
   private  int _index;
 
   internal  Enumerator(FeedColumnLayoutCollection collection) {
    this._collection = collection;
    this._version = collection._version;
    this._index = -1;
   }
 
   public  FeedColumnLayoutEntry Current {
    get { return Entry; }
   }
 
   object IEnumerator.Current {
    get { return (DictionaryEntry) Entry; }
   }
 
   public  FeedColumnLayoutEntry Entry {
    get {
     this._collection.CheckEnumIndex(this._index);
     this._collection.CheckEnumVersion(this._version);
     return new FeedColumnLayoutEntry(
      this._collection._keys[this._index],
      this._collection._values[this._index]);
    }
   }
 
   DictionaryEntry IDictionaryEnumerator.Entry {
    get { return Entry; }
   }
 
   public  String Key {
    get {
     this._collection.CheckEnumIndex(this._index);
     this._collection.CheckEnumVersion(this._version);
     return this._collection._keys[this._index];
    }
   }
 
   object IDictionaryEnumerator.Key {
    get { return Key; }
   }
 
   public  FeedColumnLayout Value {
    get {
     this._collection.CheckEnumIndex(this._index);
     this._collection.CheckEnumVersion(this._version);
     return this._collection._values[this._index];
    }
   }
 
   object IDictionaryEnumerator.Value {
    get { return Value; }
   }
 
   public  bool MoveNext() {
    this._collection.CheckEnumVersion(this._version);
    return (++this._index < this._collection.Count);
   }
 
   public  void Reset() {
    this._collection.CheckEnumVersion(this._version);
    this._index = -1;
   }

		}
		
  [Serializable] 
   private sealed class  KeyList : IList<string> {
			
   private  FeedColumnLayoutCollection _collection;
 
   internal  KeyList(FeedColumnLayoutCollection collection) {
    this._collection = collection;
   }
 
   public  int Count {
    get { return this._collection.Count; }
   }
 
   public  bool IsReadOnly {
    get { return true; }
   }
 
   public  bool IsFixedSize {
    get { return true; }
   }
 
   public  bool IsSynchronized {
    get { return this._collection.IsSynchronized; }
   }
 
   public  String this[int index] {
    get { return this._collection.GetKey(index); }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   public  object SyncRoot {
    get { return this._collection.SyncRoot; }
   }
 
   public  void Add(String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Clear() {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Contains(String key) {
    return this._collection.ContainsKey(key);
   }
 
   public  void CopyTo(String[] array, int arrayIndex) {
    this._collection.CheckTargetArray(array, arrayIndex);
    Array.Copy(this._collection._keys, 0,
     array, arrayIndex, this._collection.Count);
   }
 
   public  IEnumerator<string> GetEnumerator() {
    return _collection.Keys.GetEnumerator();
   }
 
   IEnumerator IEnumerable.GetEnumerator() {
    return (IEnumerator) GetEnumerator();
   }
 
   public  int IndexOf(String key) {
    return this._collection.IndexOfKey(key);
   }
 
   public  void Insert(int index, String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Remove(String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void RemoveAt(int index) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }

		}
		
  [Serializable] 
   private sealed class  ValueList : IFeedColumnLayoutList, IList {
			
   private  FeedColumnLayoutCollection _collection;
 
   internal  ValueList(FeedColumnLayoutCollection collection) {
    this._collection = collection;
   }
 
   public  int Count {
    get { return this._collection.Count; }
   }
 
   public  bool IsReadOnly {
    get { return true; }
   }
 
   public  bool IsFixedSize {
    get { return true; }
   }
 
   public  bool IsSynchronized {
    get { return this._collection.IsSynchronized; }
   }
 
   public  FeedColumnLayout this[int index] {
    get { return this._collection.GetByIndex(index); }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   object IList.this[int index] {
    get { return this[index]; }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   public  object SyncRoot {
    get { return this._collection.SyncRoot; }
   }
 
   public  int Add(FeedColumnLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   int IList.Add(object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Clear() {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Contains(FeedColumnLayout value) {
    return this._collection.ContainsValue(value);
   }
 
   bool IList.Contains(object value) {
    return Contains((FeedColumnLayout) value);
   }
 
   public  void CopyTo(FeedColumnLayout[] array, int arrayIndex) {
    this._collection.CheckTargetArray(array, arrayIndex);
    Array.Copy(this._collection._values, 0,
     array, arrayIndex, this._collection.Count);
   }
 
   void ICollection.CopyTo(Array array, int arrayIndex) {
    this._collection.CheckTargetArray(array, arrayIndex);
    CopyTo((FeedColumnLayout[]) array, arrayIndex);
   }
 
   public  IFeedColumnLayoutEnumerator GetEnumerator() {
    return new ValueEnumerator(this._collection);
   }
 
   IEnumerator IEnumerable.GetEnumerator() {
    return (IEnumerator) GetEnumerator();
   }
 
   public  int IndexOf(FeedColumnLayout value) {
    return this._collection.IndexOfValue(value);
   }
 
   int IList.IndexOf(object value) {
    return IndexOf((FeedColumnLayout) value);
   }
 
   public  void Insert(int index, FeedColumnLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   void IList.Insert(int index, object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Remove(FeedColumnLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   void IList.Remove(object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void RemoveAt(int index) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }

		}
		
  [Serializable] 
   private sealed class  ValueEnumerator :
   IFeedColumnLayoutEnumerator, IEnumerator {
			
   private readonly  FeedColumnLayoutCollection _collection;
 
   private readonly  int _version;
 
   private  int _index;
 
   internal  ValueEnumerator(FeedColumnLayoutCollection collection) {
    this._collection = collection;
    this._version = collection._version;
    this._index = -1;
   }
 
   public  FeedColumnLayout Current {
    get {
     this._collection.CheckEnumIndex(this._index);
     this._collection.CheckEnumVersion(this._version);
     return this._collection._values[this._index];
    }
   }
 
   object IEnumerator.Current {
    get { return Current; }
   }
 
   public  bool MoveNext() {
    this._collection.CheckEnumVersion(this._version);
    return (++this._index < this._collection.Count);
   }
 
   public  void Reset() {
    this._collection.CheckEnumVersion(this._version);
    this._index = -1;
   }

		}
		
  [Serializable] 
   private sealed class  SyncList : FeedColumnLayoutCollection {
			
   private  FeedColumnLayoutCollection _collection;
 
   private  object _root;
 
   internal  SyncList(FeedColumnLayoutCollection collection):
    base(Tag.Default) {
    this._collection = collection;
    this._root = collection.SyncRoot;
   }
 
   protected override  String[] InnerKeys {
    get { lock (this._root) return this._collection.InnerKeys; }
   }
 
   protected override  FeedColumnLayout[] InnerValues {
    get { lock (this._root) return this._collection.InnerValues; }
   }
 
   public override  int Capacity {
    get { lock (this._root) return this._collection.Capacity; }
   }
 
   public override  int Count {
    get { lock (this._root) return this._collection.Count; }
   }
 
   public override  bool IsFixedSize {
    get { return this._collection.IsFixedSize; }
   }
 
   public override  bool IsReadOnly {
    get { return this._collection.IsReadOnly; }
   }
 
   public override  bool IsSynchronized {
    get { return true; }
   }
 
   public override  FeedColumnLayoutEntry this[int index] {
    get { lock (this._root) return this._collection[index]; }
    set { lock (this._root) this._collection[index] = value; }
   }
 
   public override  ICollection<string> Keys {
    get { lock (this._root) return this._collection.Keys; }
   }
 
   public override  object SyncRoot {
    get { return this._root; }
   }
 
   public override  IFeedColumnLayoutCollection Values {
    get { lock (this._root) return this._collection.Values; }
   }
 
   public override  int Add(FeedColumnLayoutEntry entry) {
    lock (this._root) return this._collection.Add(entry);
   }
 
   public override  void AddRange(FeedColumnLayoutCollection collection) {
    lock (this._root) this._collection.AddRange(collection);
   }
 
   public override  void AddRange(FeedColumnLayoutEntry[] array) {
    lock (this._root) this._collection.AddRange(array);
   }
 
   public override  void Clear() {
    lock (this._root) this._collection.Clear();
   }
 
   public override  object Clone() {
    lock (this._root) return this._collection.Clone();
   }
 
   public override  bool Contains(FeedColumnLayoutEntry entry) {
    lock (this._root) return this._collection.Contains(entry);
   }
 
   public override  bool ContainsKey(String key) {
    lock (this._root) return this._collection.ContainsKey(key);
   }
 
   public override  bool ContainsValue(FeedColumnLayout value) {
    lock (this._root) return this._collection.ContainsValue(value);
   }
 
   public override  void CopyTo(FeedColumnLayoutEntry[] array, int index) {
    lock (this._root) this._collection.CopyTo(array, index);
   }
 
   public override  bool Equals(FeedColumnLayoutCollection collection) {
    lock (this._root) return this._collection.Equals(collection);
   }
 
   public override  FeedColumnLayout GetByIndex(int index) {
    lock (this._root) return this._collection.GetByIndex(index);
   }
 
   public override  FeedColumnLayout GetByKey(String key) {
    lock (this._root) return this._collection.GetByKey(key);
   }
 
   public override  IStringFeedColumnLayoutEnumerator GetEnumerator() {
    lock (this._root) return this._collection.GetEnumerator();
   }
 
   public override  String GetKey(int index) {
    lock (this._root) return this._collection.GetKey(index);
   }
 
   public override  ICollection<string> GetKeyList() {
    lock (this._root) return this._collection.GetKeyList();
   }
 
   public override  IFeedColumnLayoutList GetValueList() {
    lock (this._root) return this._collection.GetValueList();
   }
 
   public override  int IndexOf(FeedColumnLayoutEntry entry) {
    lock (this._root) return this._collection.IndexOf(entry);
   }
 
   public override  int IndexOfKey(String key) {
    lock (this._root) return this._collection.IndexOfKey(key);
   }
 
   public override  int IndexOfValue(FeedColumnLayout value) {
    lock (this._root) return this._collection.IndexOfValue(value);
   }
 
   public override  void Insert(int index, FeedColumnLayoutEntry entry) {
    lock (this._root) this._collection.Insert(index, entry);
   }
 
   public override  void Remove(FeedColumnLayoutEntry entry) {
    lock (this._root) this._collection.Remove(entry);
   }
 
   public override  void RemoveAt(int index) {
    lock (this._root) this._collection.RemoveAt(index);
   }
 
   public override  void SetByIndex(int index, FeedColumnLayout value) {
    lock (this._root) this._collection.SetByIndex(index, value);
   }
 
   public override  int SetByKey(String key, FeedColumnLayout value) {
    lock (this._root) return this._collection.SetByKey(key, value);
   }
 
   public override  FeedColumnLayoutEntry[] ToArray() {
    lock (this._root) return this._collection.ToArray();
   }
 
   public override  void TrimToSize() {
    lock (this._root) this._collection.TrimToSize();
   }

		}

	}

}
namespace  NewsComponents.Collections.Old {
	
 public interface  IlistviewLayoutCollection {
		
  int Count { get; } 
  bool IsSynchronized { get; } 
  object SyncRoot { get; } 
  void CopyTo(listviewLayout[] array, int arrayIndex); 
  IlistviewLayoutEnumerator GetEnumerator();
	}
	
 public interface 
  IlistviewLayoutList :  IlistviewLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  listviewLayout this[int index] { get; set; } 
  int Add(listviewLayout value); 
  void Clear(); 
  bool Contains(listviewLayout value); 
  int IndexOf(listviewLayout value); 
  void Insert(int index, listviewLayout value); 
  void Remove(listviewLayout value); 
  void RemoveAt(int index);
	}
	
 public interface  IlistviewLayoutEnumerator {
		
  listviewLayout Current { get; } 
  bool MoveNext(); 
  void Reset();
	}
	
 public interface  IStringlistviewLayoutCollection {
		
  int Count { get; } 
  bool IsSynchronized { get; } 
  object SyncRoot { get; } 
  void CopyTo(listviewLayoutEntry[] array, int arrayIndex); 
  IStringlistviewLayoutEnumerator GetEnumerator();
	}
	
 public interface 
  IStringlistviewLayoutDictionary :  IStringlistviewLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  listviewLayout this[String key] { get; set; } 
  ICollection<string> Keys { get; } 
  IlistviewLayoutCollection Values { get; } 
  void Add(String key, listviewLayout value); 
  void Clear(); 
  bool Contains(String key); 
  void Remove(String key);
	}
	
 public interface 
  IStringlistviewLayoutList :  IStringlistviewLayoutCollection {
		
  bool IsFixedSize { get; } 
  bool IsReadOnly { get; } 
  listviewLayoutEntry this[int index] { get; set; } 
  int Add(listviewLayoutEntry entry); 
  void Clear(); 
  bool Contains(listviewLayoutEntry entry); 
  int IndexOf(listviewLayoutEntry entry); 
  void Insert(int index, listviewLayoutEntry entry); 
  void Remove(listviewLayoutEntry entry); 
  void RemoveAt(int index);
	}
	
 public interface  IStringlistviewLayoutEnumerator {
		
  listviewLayoutEntry Current { get; } 
  listviewLayoutEntry Entry { get; } 
  String Key { get; } 
  listviewLayout Value { get; } 
  bool MoveNext(); 
  void Reset();
	}
	
 [Serializable] 
 public struct  listviewLayoutEntry {
		
  private  String _key;
 
  private  listviewLayout _value;
 
  public  listviewLayoutEntry(String key, listviewLayout value) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   this._key = key;
   this._value = value;
  }
 
  public  String Key {
   get { return this._key; }
   set {
    if ((object) value == null)
     throw new ArgumentNullException("value");
    this._key = value;
   }
  }
 
  public  listviewLayout Value {
   get { return this._value; }
   set { this._value = value; }
  }
 
  public static  implicit operator listviewLayoutEntry(DictionaryEntry entry) {
   listviewLayoutEntry pair = new listviewLayoutEntry();
   if (entry.Key != null) pair.Key = (String) entry.Key;
   if (entry.Value != null) pair.Value = (listviewLayout) entry.Value;
   return pair;
  } 
  public static  implicit operator DictionaryEntry(listviewLayoutEntry pair) {
   DictionaryEntry entry = new DictionaryEntry();
   if (pair.Key != null) entry.Key = pair.Key;
   entry.Value = pair.Value;
   return entry;
  }
	}
	
 [Serializable] 
 public class  listviewLayoutCollection :
  IStringlistviewLayoutDictionary, IDictionary, ICloneable {
		
  private  const int _defaultCapacity = 16; 
  private  String[] _keys;
 
  private  listviewLayout[] _values;
 
  private  IComparer _comparer;
 
  private  int _count;
 
  [NonSerialized] 
  private  int _version;
 
  private  KeyList _keyList;
 
  private  ValueList _valueList;
 
  private enum  Tag  { Default } 
  private  listviewLayoutCollection(Tag tag) { }
 
  public  listviewLayoutCollection() {
   this._keys = new String[_defaultCapacity];
   this._values = new listviewLayout[_defaultCapacity];
   this._comparer = Comparer.Default;
  }
 
  public  listviewLayoutCollection(IComparer comparer): this() {
   if (comparer != null) this._comparer = comparer;
  }
 
  public  listviewLayoutCollection(IDictionary dictionary): this(dictionary, null) { }
 
  public  listviewLayoutCollection(int capacity) {
   if (capacity < 0)
    throw new ArgumentOutOfRangeException("capacity",
     capacity, "Argument cannot be negative.");
   this._keys = new String[capacity];
   this._values = new listviewLayout[capacity];
   this._comparer = Comparer.Default;
  }
 
  public  listviewLayoutCollection(IComparer comparer, int capacity) : this(capacity) {
   if (comparer != null) this._comparer = comparer;
  }
 
  public  listviewLayoutCollection(IDictionary dictionary, IComparer comparer):
   this(comparer, (dictionary == null ? 0 : dictionary.Count)) {
   if (dictionary == null)
    throw new ArgumentNullException("dictionary");
   dictionary.Keys.CopyTo(this._keys, 0);
   dictionary.Values.CopyTo(this._values, 0);
   Array.Sort(this._keys, this._values, this._comparer);
   this._count = dictionary.Count;
  }
 
  public virtual  int Capacity {
   get { return this._keys.Length; }
   set {
    if (value == this._keys.Length) return;
    if (value < this._count)
     throw new ArgumentOutOfRangeException("Capacity",
      value, "Value cannot be less than Count.");
    if (value == 0) {
     this._keys = new String[_defaultCapacity];
     this._values = new listviewLayout[_defaultCapacity];
     return;
    }
    String[] newKeys = new String[value];
    listviewLayout[] newValues = new listviewLayout[value];
    Array.Copy(this._keys, 0, newKeys, 0, this._count);
    Array.Copy(this._values, 0, newValues, 0, this._count);
    this._keys = newKeys;
    this._values = newValues;
   }
  }
 
  public virtual  int Count {
   get { return this._count; }
  }
 
  public virtual  bool IsFixedSize {
   get { return false; }
  }
 
  public virtual  bool IsReadOnly {
   get { return false; }
  }
 
  public virtual  bool IsSynchronized {
   get { return false; }
  }
 
  public virtual  listviewLayout this[String key] {
   get {
    if ((object) key == null)
     throw new ArgumentNullException("key");
    int index = BinaryKeySearch(key);
    if (index >= 0) return this._values[index];
    return null;
   }
   set {
    if ((object) key == null)
     throw new ArgumentNullException("key");
    int index = BinaryKeySearch(key);
    if (index >= 0) {
     ++this._version;
     this._values[index] = value;
     return;
    }
    Insert(~index, key, value);
   }
  }
 
  public virtual  listviewLayoutEntry this[int index] {
   get {
    ValidateIndex(index);
    return new listviewLayoutEntry(this._keys[index], this._values[index]);
   }
   set {
    ValidateIndex(index);
    ++this._version;
    this._keys[index] = value.Key;
    this._values[index] = value.Value;
   }
  }
 
  object IDictionary.this[object key] {
   get { return this[(String) key]; }
   set { this[(String) key] = (listviewLayout) value; }
  }
 
  public virtual  ICollection<string> Keys {
   get { return GetKeyList(); }
  }
 
  ICollection IDictionary.Keys {
   get { return (ICollection) Keys; }
  }
 
  public virtual  object SyncRoot {
   get { return this; }
  }
 
  public virtual  IlistviewLayoutCollection Values {
   get { return GetValueList(); }
  }
 
  ICollection IDictionary.Values {
   get { return (ICollection) Values; }
  }
 
  public virtual  void Add(String key, listviewLayout value) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   int index = BinaryKeySearch(key);
   if (index >= 0)
    throw new ArgumentException(
     "Argument already exists in collection.", "key");
   Insert(~index, key, value);
  }
 
  void IDictionary.Add(object key, object value) {
   Add((String) key, (listviewLayout) value);
  }
 
  public virtual  void Clear() {
   if (this._count == 0) return;
   ++this._version;
   Array.Clear(this._keys, 0, this._count);
   Array.Clear(this._values, 0, this._count);
   this._count = 0;
  }
 
  public virtual  object Clone() {
   listviewLayoutCollection dictionary = new listviewLayoutCollection(this._count);
   Array.Copy(this._keys, 0, dictionary._keys, 0, this._count);
   Array.Copy(this._values, 0, dictionary._values, 0, this._count);
   dictionary._count = this._count;
   dictionary._comparer = this._comparer;
   dictionary._version = this._version;
   return dictionary;
  }
 
  public virtual  bool Contains(String key) {
   return (IndexOfKey(key) >= 0);
  }
 
  public virtual  bool Contains(Uri key) {
   return ContainsKey(key);
  }
 
  bool IDictionary.Contains(object key) {
   return (IndexOfKey((String) key) >= 0);
  }
 
  public virtual  bool ContainsKey(String key) {
   return (IndexOfKey(key) >= 0);
  }
 
  public virtual  bool ContainsKey(Uri key) {
   if (key == null)
    return false;
   string feedUrl = key.ToString();
   if (!ContainsKey(feedUrl) && (key.IsFile || key.IsUnc)) {
    feedUrl = key.LocalPath;
   }
   return ContainsKey(feedUrl);
  }
 
  public virtual  bool ContainsValue(listviewLayout value) {
   return (IndexOfValue(value) >= 0);
  }
 
  public virtual  void CopyTo(listviewLayoutEntry[] array, int arrayIndex) {
   CheckTargetArray(array, arrayIndex);
   for (int i = 0; i < this._count; i++) {
    listviewLayoutEntry entry =
     new listviewLayoutEntry(this._keys[i], this._values[i]);
    array.SetValue(entry, arrayIndex + i);
   }
  }
 
  void ICollection.CopyTo(Array array, int arrayIndex) {
   CopyTo((listviewLayoutEntry[]) array, arrayIndex);
  }
 
  public virtual  listviewLayout GetByIndex(int index) {
   ValidateIndex(index);
   return this._values[index];
  }
 
  public virtual  IStringlistviewLayoutEnumerator GetEnumerator() {
   return new Enumerator(this);
  }
 
  IDictionaryEnumerator IDictionary.GetEnumerator() {
   return (IDictionaryEnumerator) GetEnumerator();
  }
 
  IEnumerator IEnumerable.GetEnumerator() {
   return (IEnumerator) GetEnumerator();
  }
 
  public virtual  String GetKey(int index) {
   ValidateIndex(index);
   return this._keys[index];
  }
 
  public virtual  ICollection<String> GetKeyList() {
   if (this._keyList == null)
    this._keyList = new KeyList(this);
   return this._keyList;
  }
 
  public virtual  IlistviewLayoutList GetValueList() {
   if (this._valueList == null)
    this._valueList = new ValueList(this);
   return this._valueList;
  }
 
  public virtual  int IndexOfKey(String key) {
   if ((object) key == null)
    throw new ArgumentNullException("key");
   int index = BinaryKeySearch(key);
   return (index >= 0 ? index : -1);
  }
 
  public virtual  int IndexOfValue(listviewLayout value) {
   return Array.IndexOf(this._values, value, 0, this._count);
  }
 
  public virtual  void Remove(String key) {
   int index = IndexOfKey(key);
   if (index >= 0) RemoveAt(index);
  }
 
  void IDictionary.Remove(object key) {
   Remove((String) key);
  }
 
  public virtual  void RemoveAt(int index) {
   ValidateIndex(index);
   ++this._version;
   if (index < --this._count) {
    Array.Copy(this._keys, index + 1,
     this._keys, index, this._count - index);
    Array.Copy(this._values, index + 1,
     this._values, index, this._count - index);
   }
   this._keys[this._count] = null;
   this._values[this._count] = null;
  }
 
  public virtual  void SetByIndex(int index, listviewLayout value) {
   ValidateIndex(index);
   ++this._version;
   this._values[index] = value;
  }
 
  public static  listviewLayoutCollection Synchronized(listviewLayoutCollection dictionary) {
   if (dictionary == null)
    throw new ArgumentNullException("dictionary");
   return new SyncDictionary(dictionary);
  }
 
  public virtual  void TrimToSize() {
   Capacity = this._count;
  }
 
  private  int BinaryKeySearch(String key) {
   return Array.BinarySearch(this._keys, 0,
    this._count, key, this._comparer);
  }
 
  private  void CheckEnumIndex(int index) {
   if (index < 0 || index >= this._count)
    throw new InvalidOperationException(
     "Enumerator is not on a collection element.");
  }
 
  private  void CheckEnumVersion(int version) {
   if (version != this._version)
    throw new InvalidOperationException(
     "Enumerator invalidated by modification to collection.");
  }
 
  private  void CheckTargetArray(Array array, int arrayIndex) {
   if (array == null)
    throw new ArgumentNullException("array");
   if (array.Rank > 1)
    throw new ArgumentException(
     "Argument cannot be multidimensional.", "array");
   if (arrayIndex < 0)
    throw new ArgumentOutOfRangeException("arrayIndex",
     arrayIndex, "Argument cannot be negative.");
   if (arrayIndex >= array.Length)
    throw new ArgumentException(
     "Argument must be less than array length.", "arrayIndex");
   if (this._count > array.Length - arrayIndex)
    throw new ArgumentException(
     "Argument section must be large enough for collection.", "array");
  }
 
  private  void EnsureCapacity(int minimum) {
   int newCapacity = (this._keys.Length == 0 ?
   _defaultCapacity : this._keys.Length * 2);
   if (newCapacity < minimum) newCapacity = minimum;
   Capacity = newCapacity;
  }
 
  private  void Insert(int index,
   String key, listviewLayout value) {
   if (this._count == this._keys.Length)
    EnsureCapacity(this._count + 1);
   ++this._version;
   if (index < this._count) {
    Array.Copy(this._keys, index,
     this._keys, index + 1, this._count - index);
    Array.Copy(this._values, index,
     this._values, index + 1, this._count - index);
   }
   this._keys[index] = key;
   this._values[index] = value;
   ++this._count;
  }
 
  private  void ValidateIndex(int index) {
   if (index < 0)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument cannot be negative.");
   if (index >= this._count)
    throw new ArgumentOutOfRangeException("index",
     index, "Argument must be less than Count.");
  }
 
  [Serializable] 
   private sealed class  Enumerator :
   IStringlistviewLayoutEnumerator, IDictionaryEnumerator {
			
   private readonly  listviewLayoutCollection _dictionary;
 
   private readonly  int _version;
 
   private  int _index;
 
   internal  Enumerator(listviewLayoutCollection dictionary) {
    this._dictionary = dictionary;
    this._version = dictionary._version;
    this._index = -1;
   }
 
   public  listviewLayoutEntry Current {
    get { return Entry; }
   }
 
   object IEnumerator.Current {
    get { return (DictionaryEntry) Entry; }
   }
 
   public  listviewLayoutEntry Entry {
    get {
     this._dictionary.CheckEnumIndex(this._index);
     this._dictionary.CheckEnumVersion(this._version);
     return new listviewLayoutEntry(
      this._dictionary._keys[this._index],
      this._dictionary._values[this._index]);
    }
   }
 
   DictionaryEntry IDictionaryEnumerator.Entry {
    get { return Entry; }
   }
 
   public  String Key {
    get {
     this._dictionary.CheckEnumIndex(this._index);
     this._dictionary.CheckEnumVersion(this._version);
     return this._dictionary._keys[this._index];
    }
   }
 
   object IDictionaryEnumerator.Key {
    get { return Key; }
   }
 
   public  listviewLayout Value {
    get {
     this._dictionary.CheckEnumIndex(this._index);
     this._dictionary.CheckEnumVersion(this._version);
     return this._dictionary._values[this._index];
    }
   }
 
   object IDictionaryEnumerator.Value {
    get { return Value; }
   }
 
   public  bool MoveNext() {
    this._dictionary.CheckEnumVersion(this._version);
    return (++this._index < this._dictionary.Count);
   }
 
   public  void Reset() {
    this._dictionary.CheckEnumVersion(this._version);
    this._index = -1;
   }

		}
		
  [Serializable] 
   private sealed class  KeyList : IList<string> {
			
   private  listviewLayoutCollection _dictionary;
 
   internal  KeyList(listviewLayoutCollection dictionary) {
    this._dictionary = dictionary;
   }
 
   public  int Count {
    get { return this._dictionary.Count; }
   }
 
   public  bool IsReadOnly {
    get { return true; }
   }
 
   public  bool IsFixedSize {
    get { return true; }
   }
 
   public  bool IsSynchronized {
    get { return this._dictionary.IsSynchronized; }
   }
 
   public  String this[int index] {
    get { return this._dictionary.GetKey(index); }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   public  object SyncRoot {
    get { return this._dictionary.SyncRoot; }
   }
 
   public  void Add(String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Clear() {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Contains(String key) {
    return this._dictionary.Contains(key);
   }
 
   public  void CopyTo(String[] array, int arrayIndex) {
    this._dictionary.CheckTargetArray(array, arrayIndex);
    Array.Copy(this._dictionary._keys, 0,
     array, arrayIndex, this._dictionary.Count);
   }
 
   public  IEnumerator<string> GetEnumerator() {
                return _dictionary.Keys.GetEnumerator();
   }
 
   IEnumerator IEnumerable.GetEnumerator() {
    return (IEnumerator) GetEnumerator();
   }
 
   public  int IndexOf(String key) {
    return this._dictionary.IndexOfKey(key);
   }
 
   public  void Insert(int index, String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Remove(String key) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void RemoveAt(int index) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }

		}
		
  [Serializable] 
   private sealed class  ValueList : IlistviewLayoutList, IList {
			
   private  listviewLayoutCollection _dictionary;
 
   internal  ValueList(listviewLayoutCollection dictionary) {
    this._dictionary = dictionary;
   }
 
   public  int Count {
    get { return this._dictionary.Count; }
   }
 
   public  bool IsReadOnly {
    get { return true; }
   }
 
   public  bool IsFixedSize {
    get { return true; }
   }
 
   public  bool IsSynchronized {
    get { return this._dictionary.IsSynchronized; }
   }
 
   public  listviewLayout this[int index] {
    get { return this._dictionary.GetByIndex(index); }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   object IList.this[int index] {
    get { return this[index]; }
    set { throw new NotSupportedException(
        "Read-only collections cannot be modified."); }
   }
 
   public  object SyncRoot {
    get { return this._dictionary.SyncRoot; }
   }
 
   public  int Add(listviewLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   int IList.Add(object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Clear() {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  bool Contains(listviewLayout value) {
    return this._dictionary.ContainsValue(value);
   }
 
   bool IList.Contains(object value) {
    return Contains((listviewLayout) value);
   }
 
   public  void CopyTo(listviewLayout[] array, int arrayIndex) {
    this._dictionary.CheckTargetArray(array, arrayIndex);
    Array.Copy(this._dictionary._values, 0,
     array, arrayIndex, this._dictionary.Count);
   }
 
   void ICollection.CopyTo(Array array, int arrayIndex) {
    this._dictionary.CheckTargetArray(array, arrayIndex);
    CopyTo((listviewLayout[]) array, arrayIndex);
   }
 
   public  IlistviewLayoutEnumerator GetEnumerator() {
    return new ValueEnumerator(this._dictionary);
   }
 
   IEnumerator IEnumerable.GetEnumerator() {
    return (IEnumerator) GetEnumerator();
   }
 
   public  int IndexOf(listviewLayout value) {
    return this._dictionary.IndexOfValue(value);
   }
 
   int IList.IndexOf(object value) {
    return IndexOf((listviewLayout) value);
   }
 
   public  void Insert(int index, listviewLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   void IList.Insert(int index, object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void Remove(listviewLayout value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   void IList.Remove(object value) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }
 
   public  void RemoveAt(int index) {
    throw new NotSupportedException(
     "Read-only collections cannot be modified.");
   }

		}
		
  [Serializable] 
   private sealed class  ValueEnumerator :
   IlistviewLayoutEnumerator, IEnumerator {
			
   private readonly  listviewLayoutCollection _dictionary;
 
   private readonly  int _version;
 
   private  int _index;
 
   internal  ValueEnumerator(listviewLayoutCollection dictionary) {
    this._dictionary = dictionary;
    this._version = dictionary._version;
    this._index = -1;
   }
 
   public  listviewLayout Current {
    get {
     this._dictionary.CheckEnumIndex(this._index);
     this._dictionary.CheckEnumVersion(this._version);
     return this._dictionary._values[this._index];
    }
   }
 
   object IEnumerator.Current {
    get { return Current; }
   }
 
   public  bool MoveNext() {
    this._dictionary.CheckEnumVersion(this._version);
    return (++this._index < this._dictionary.Count);
   }
 
   public  void Reset() {
    this._dictionary.CheckEnumVersion(this._version);
    this._index = -1;
   }

		}
		
  [Serializable] 
   private sealed class  SyncDictionary : listviewLayoutCollection {
			
   private  listviewLayoutCollection _dictionary;
 
   private  object _root;
 
   internal  SyncDictionary(listviewLayoutCollection dictionary):
    base(Tag.Default) {
    this._dictionary = dictionary;
    this._root = dictionary.SyncRoot;
   }
 
   public override  int Capacity {
    get { lock (this._root) return this._dictionary.Capacity; }
   }
 
   public override  int Count {
    get { lock (this._root) return this._dictionary.Count; }
   }
 
   public override  bool IsFixedSize {
    get { return this._dictionary.IsFixedSize; }
   }
 
   public override  bool IsReadOnly {
    get { return this._dictionary.IsReadOnly; }
   }
 
   public override  bool IsSynchronized {
    get { return true; }
   }
 
   public override  listviewLayout this[String key] {
    get { lock (this._root) return this._dictionary[key]; }
    set { lock (this._root) this._dictionary[key] = value; }
   }
 
   public override  ICollection<string> Keys {
    get { lock (this._root) return this._dictionary.Keys; }
   }
 
   public override  object SyncRoot {
    get { return this._root; }
   }
 
   public override  IlistviewLayoutCollection Values {
    get { lock (this._root) return this._dictionary.Values; }
   }
 
   public override  void Add(String key, listviewLayout value) {
    lock (this._root) this._dictionary.Add(key, value);
   }
 
   public override  void Clear() {
    lock (this._root) this._dictionary.Clear();
   }
 
   public override  object Clone() {
    lock (this._root) return this._dictionary.Clone();
   }
 
   public override  bool Contains(String key) {
    lock (this._root) return this._dictionary.Contains(key);
   }
 
   public override  bool ContainsKey(String key) {
    lock (this._root) return this._dictionary.ContainsKey(key);
   }
 
   public override  bool ContainsValue(listviewLayout value) {
    lock (this._root) return this._dictionary.ContainsValue(value);
   }
 
   public override  void CopyTo(listviewLayoutEntry[] array, int index) {
    lock (this._root) this._dictionary.CopyTo(array, index);
   }
 
   public override  listviewLayout GetByIndex(int index) {
    lock (this._root) return this._dictionary.GetByIndex(index);
   }
 
   public override  IStringlistviewLayoutEnumerator GetEnumerator() {
    lock (this._root) return this._dictionary.GetEnumerator();
   }
 
   public override  String GetKey(int index) {
    lock (this._root) return this._dictionary.GetKey(index);
   }
 
   public override  ICollection<string> GetKeyList() {
    lock (this._root) return this._dictionary.GetKeyList();
   }
 
   public override  IlistviewLayoutList GetValueList() {
    lock (this._root) return this._dictionary.GetValueList();
   }
 
   public override  int IndexOfKey(String key) {
    lock (this._root) return this._dictionary.IndexOfKey(key);
   }
 
   public override  int IndexOfValue(listviewLayout value) {
    lock (this._root) return this._dictionary.IndexOfValue(value);
   }
 
   public override  void Remove(String key) {
    lock (this._root) this._dictionary.Remove(key);
   }
 
   public override  void RemoveAt(int index) {
    lock (this._root) this._dictionary.RemoveAt(index);
   }
 
   public override  void SetByIndex(int index, listviewLayout value) {
    lock (this._root) this._dictionary.SetByIndex(index, value);
   }
 
   public override  void TrimToSize() {
    lock (this._root) this._dictionary.TrimToSize();
   }

		}

	}

}
