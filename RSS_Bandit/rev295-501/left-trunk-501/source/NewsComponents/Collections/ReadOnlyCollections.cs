using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Runtime.Serialization;
namespace NewsComponents.Collections
{
    [Serializable]
    public class ReadOnlyICollection<T> : ICollection<T>
    {
        private ICollection<T> coll;
        public ReadOnlyICollection(ICollection<T> collectionToWrap)
        {
            coll = collectionToWrap;
        }
        public static ReadOnlyICollection<T> AsReadOnly(ICollection<T> collectionToWrap)
        {
            return new ReadOnlyICollection<T>(collectionToWrap);
        }
        public void Add(T item)
        {
        }
        public void Clear()
        {
        }
        public bool Contains(T item)
        {
            return coll.Contains(item);
        }
        public void CopyTo(T[] array, int arrayIndex)
        {
            coll.CopyTo(array, arrayIndex);
        }
        public int Count
        {
            get
            {
                return coll.Count;
            }
        }
        public bool IsReadOnly
        {
            get
            {
                return true;
            }
        }
        public bool Remove(T item)
        {
            return false;
        }
        public IEnumerator<T> GetEnumerator()
        {
            return coll.GetEnumerator();
        }
        IEnumerator IEnumerable.GetEnumerator()
        {
            return coll.GetEnumerator();
        }
    }
    [Serializable]
    public class ReadOnlyDictionary<TKey, TValue> : IDictionary<TKey, TValue>, ICollection<KeyValuePair<TKey, TValue> >, IEnumerable<KeyValuePair<TKey, TValue> >, IDictionary, ICollection, IEnumerable, ISerializable, IDeserializationCallback
    {
        private IDictionary<TKey, TValue> dict;
        private IDictionary idict;
        public ReadOnlyDictionary(IDictionary<TKey, TValue> dictionaryToWrap)
        {
            dict = dictionaryToWrap;
            idict = (IDictionary)dict;
        }
        public static ReadOnlyDictionary<TKey, TValue> AsReadOnly(IDictionary<TKey, TValue> dictionaryToWrap)
        {
            return new ReadOnlyDictionary<TKey, TValue>(dictionaryToWrap);
        }
  protected ReadOnlyDictionary(SerializationInfo info, StreamingContext context) {
  }
        public void Add(TKey key, TValue value)
        {
        }
        public bool ContainsKey(TKey key)
        {
            return dict.ContainsKey(key);
        }
        public ICollection<TKey> Keys
        {
            get
            {
                return ReadOnlyICollection<TKey>.AsReadOnly(dict.Keys);
            }
        }
        public bool Remove(TKey key)
        {
            return false;
        }
        public bool TryGetValue(TKey key, out TValue value)
        {
            return dict.TryGetValue(key, out value);
        }
        public ICollection<TValue> Values
        {
            get
            {
                return ReadOnlyICollection<TValue>.AsReadOnly(dict.Values);
            }
        }
        public TValue this[TKey key]
        {
            get
            {
                return dict[key];
            }
            set
            {
            }
        }
        public void Add(KeyValuePair<TKey, TValue> item)
        {
        }
        public void Clear()
        {
        }
        public bool Contains(KeyValuePair<TKey, TValue> item)
        {
            return dict.Contains(item);
        }
        public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
        {
            dict.CopyTo(array, arrayIndex);
        }
        public int Count
        {
            get
            {
                return dict.Count;
            }
        }
        public bool IsReadOnly
        {
            get
            {
                return true;
            }
        }
        public bool Remove(KeyValuePair<TKey, TValue> item)
        {
            return false;
        }
        public IEnumerator<KeyValuePair<TKey, TValue> > GetEnumerator()
        {
            return dict.GetEnumerator();
        }
        IEnumerator IEnumerable.GetEnumerator()
        {
            return idict.GetEnumerator();
        }
        public void Add(object key, object value)
        {
        }
        public bool Contains(object key)
        {
            return idict.Contains(key);
        }
        IDictionaryEnumerator IDictionary.GetEnumerator()
        {
            return idict.GetEnumerator();
        }
        public bool IsFixedSize
        {
            get
            {
                return idict.IsFixedSize;
            }
        }
        ICollection IDictionary.Keys
        {
            get
            {
                return idict.Keys;
            }
        }
        public void Remove(object key)
        {
        }
        ICollection IDictionary.Values
        {
            get
            {
                return idict.Values;
            }
        }
        public object this[object key]
        {
            get
            {
                return idict[key];
            }
            set
            {
            }
        }
        public void CopyTo(Array array, int index)
        {
        }
        public bool IsSynchronized
        {
            get
            {
                return idict.IsSynchronized;
            }
        }
        public object SyncRoot
        {
            get
            {
                return idict.SyncRoot;
            }
        }
        public void OnDeserialization(object sender)
        {
            IDeserializationCallback callback = dict as IDeserializationCallback;
            callback.OnDeserialization(sender);
        }
        public virtual void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            ISerializable serializable = dict as ISerializable;
            serializable.GetObjectData(info, context);
        }
    }
}
