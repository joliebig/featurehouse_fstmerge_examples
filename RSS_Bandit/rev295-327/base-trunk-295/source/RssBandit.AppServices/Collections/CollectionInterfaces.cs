using System;
using System.Collections;
namespace RssBandit.AppServices
{
 public interface IStringCollection
 {
  int Count { get; }
  bool IsSynchronized { get; }
  object SyncRoot { get; }
  void CopyTo(String[] array, int arrayIndex);
  IStringEnumerator GetEnumerator();
 }
 public interface
  IStringList: IStringCollection
 {
  bool IsFixedSize { get; }
  bool IsReadOnly { get; }
  String this[int index] { get; set; }
  int Add(String value);
  void Clear();
  bool Contains(String value);
  int IndexOf(String value);
  void Insert(int index, String value);
  void Remove(String value);
  void RemoveAt(int index);
 }
 public interface IStringEnumerator
 {
  String Current { get; }
  bool MoveNext();
  void Reset();
 }
 public sealed class ReadOnlyDictionary: IDictionary
 {
  private IDictionary dict;
  public ReadOnlyDictionary(IDictionary dictionary) {
   if (dictionary == null)
    this.dict = new Hashtable(0);
   else
    this.dict = dictionary;
  }
  public void CopyTo(Array array, int index) {
   dict.CopyTo(array, index);
  }
  public int Count {
   get { return dict.Count; }
  }
  public object SyncRoot {
   get { return dict.SyncRoot; }
  }
  public bool IsSynchronized {
   get { return dict.IsSynchronized; }
  }
  public bool Contains(object key) {
   return dict.Contains(key);
  }
  public void Add(object key, object value) {
   ThrowReadOnlyException();
  }
  public void Clear() {
   ThrowReadOnlyException();
  }
  IDictionaryEnumerator IDictionary.GetEnumerator() {
   return ((IDictionary)dict).GetEnumerator();
  }
  public IEnumerator GetEnumerator() {
   return dict.GetEnumerator();
  }
  public void Remove(object key) {
   ThrowReadOnlyException();
  }
  public ICollection Keys {
   get { return dict.Keys; }
  }
  public ICollection Values {
   get { return dict.Values; }
  }
  public bool IsReadOnly {
   get { return true; }
  }
  public bool IsFixedSize {
   get { return true; }
  }
  public object this[object key] {
   get { return dict[key]; }
   set { ThrowReadOnlyException();}
  }
  private void ThrowReadOnlyException() {
   throw new NotSupportedException(
    "Read-only dictionary cannot be modified.");
  }
 }
}
