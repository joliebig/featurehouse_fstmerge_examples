using System;
using System.Collections;
using System.Xml.Serialization;
namespace NewsComponents.Xml.Serialization {
 public delegate void SerializerCacheDelegate(Type type
 , XmlAttributeOverrides overrides
 , Type[] types
 , XmlRootAttribute root
 , String defaultNamespace);
 public class XmlSerializerCache : IDisposable {
  public event SerializerCacheDelegate NewSerializer;
  public event SerializerCacheDelegate CacheHit;
  private Hashtable Serializers;
  private object SyncRoot;
  public XmlSerializerCache() {
   SyncRoot = new object();
   Serializers = new Hashtable();
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   ) {
   return GetSerializer(type, null, new Type[0], null, null);
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   , String defaultNamespace) {
   return GetSerializer(type, null, new Type[0], null, defaultNamespace);
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   , XmlRootAttribute root) {
   return GetSerializer(type, null, new Type[0], root, null);
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   , XmlAttributeOverrides overrides) {
   return GetSerializer(type
    , overrides
    , new Type[0]
    , null
    , null);
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   , Type[] types) {
   return GetSerializer(type
    , null
    , types
    , null
    , null);
  }
  public System.Xml.Serialization.XmlSerializer GetSerializer(Type type
   , XmlAttributeOverrides overrides
   , Type[] types
   , XmlRootAttribute root
   , String defaultNamespace) {
   string key = CacheKeyFactory.MakeKey(type
    , overrides
    , types
    , root
    , defaultNamespace);
   System.Xml.Serialization.XmlSerializer serializer = null;
   if (false == Serializers.ContainsKey(key)) {
    lock (SyncRoot) {
     if (false == Serializers.ContainsKey(key)) {
      serializer = new System.Xml.Serialization.XmlSerializer(type
       , overrides
       , types
       , root
       , defaultNamespace);
      Serializers.Add( key, serializer );
      if (null != NewSerializer) {
       NewSerializer(type
        , overrides
        , types
        , root
        , defaultNamespace);
      }
     }
    }
   }
   else {
    serializer = Serializers[key] as XmlSerializer;
    if (null != CacheHit) {
     CacheHit(type
      , overrides
      , types
      , root
      , defaultNamespace);
    }
   }
   System.Diagnostics.Debug.Assert(null != serializer);
   return serializer;
  }
  ~XmlSerializerCache() {
   Dispose(false);
  }
  private void Dispose(bool isDisposing) {
   if (true == isDisposing) {
   }
  }
  public void Dispose() {
   Dispose(true);
   GC.SuppressFinalize(this);
  }
 }
}
