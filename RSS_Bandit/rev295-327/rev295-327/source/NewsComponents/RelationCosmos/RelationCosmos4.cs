using System; 
using System.Reflection; 
using System.Collections.Generic; namespace  NewsComponents.RelationCosmos {
	
    internal class  StringComparer  : IEqualityComparer<string> {
		
        public static  StringComparer Comparer = new StringComparer();
 
        public  bool Equals(string s1, string s2) {
            return Object.ReferenceEquals(s1, s2);
        }
 
        public  int GetHashCode(string s) {
            return s.GetHashCode();
        }

	}
	
 public class  RelationCosmos4 : IRelationCosmos {
		
  private static readonly  log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);
 
        private readonly  Dictionary<string, IRelation> registeredRelations = new Dictionary<string, IRelation>(StringComparer.Comparer);
 
        private readonly  Dictionary<string, Object> relationsLinkTo = new Dictionary<string, Object>(50000,StringComparer.Comparer);
 
  private readonly  object syncRoot = new Object();
 
  public  RelationCosmos4(){}
 
  public  void Add<T>(T relation)
            where T : RelationBase<T>
        {
   InternalAdd(relation);
  }
 
  public  void AddRange<T>(IEnumerable<T> relations)
            where T : RelationBase<T>
        {
   InternalAddRange(relations);
  }
 
  public  void Remove<T>(T relation)
            where T : RelationBase<T>
        {
   InternalRemove(relation);
  }
 
        public  void RemoveRange<T>(IEnumerable<T> relations)
            where T : RelationBase<T>
        {
   InternalRemoveRange(relations);
  }
 
  public  void Clear() {
   lock (syncRoot) {
    relationsLinkTo.Clear();
    registeredRelations.Clear();
   }
  }
 
        public  IList<T> GetIncoming<T>(string hRef, DateTime since)
            where T : RelationBase<T>
        {
            IList<T> ret = new List<T>();
            if (String.IsNullOrEmpty(hRef)) return ret;
            lock (syncRoot) {
                Object value;
                bool inDictionary = relationsLinkTo.TryGetValue(hRef, out value);
                if (inDictionary) {
                    IList<T> list = value as IList<T>;
                    T item = value as T;
                    if (list != null) {
                        foreach (T linkBack in list) {
                            if (linkBack.PointInTime > since) {
                                ret.Add(linkBack);
                            }
                        }
                    } else if (item != null) {
                        if (item.PointInTime > since) {
                            ret.Add(item);
                        }
                    }
                }
            }
            return ret;
        }
 
        public  IList<T> GetIncoming<T>(T relation, IList<T> excludeRelations)
            where T : RelationBase<T>
        {
            if (relation == null) return new List<T>();
            lock (syncRoot) {
                IList<T> ret = new List<T>(relation.OutgoingRelations.Count);
                string hRef = relation.HRef;
                if (hRef != null && hRef.Length > 0) {
                    Object value;
                    bool inDictionary = relationsLinkTo.TryGetValue(hRef, out value);
                    if (inDictionary) {
                        IList<T> list = value as IList<T>;
                        T item = value as T;
                        if (list != null) {
                            foreach (T linkBack in list) {
                                if (!excludeRelations.Contains(linkBack) && !ret.Contains(linkBack)) {
                                    ret.Add(linkBack);
                                }
                            }
                        } else if (item != null) {
                            if (!excludeRelations.Contains(item)) {
                                ret.Add(item);
                           }
                        }
                    }
                }
                return ret;
            }
        }
 
        public  IList<T> GetOutgoing<T>(T relation, IList<T> excludeRelations)
            where T : RelationBase<T>
        {
            if (relation == null) return new List<T>();
   IList<string> excludeUrls = GetRelationUrls(excludeRelations);
   lock (syncRoot) {
    IList<T> ret = new List<T>(relation.OutgoingRelations.Count);
    foreach (string hrefOut in relation.outgoingRelationships) {
     if (excludeUrls.Contains(hrefOut))
      continue;
                    IRelation r;
                    if (registeredRelations.TryGetValue(hrefOut, out r))
                    {
                        if (r != null)
                            ret.Add((T)r);
                    }
    }
    return ret;
   }
  }
 
        public  IList<T> GetIncomingAndOutgoing<T>(T relation, IList<T> excludeRelations)
            where T : RelationBase<T>
        {
            return new List<T>();
  }
 
        public  bool HasIncomingOrOutgoing<T>(T relation, IList<T> excludeRelations)
            where T : RelationBase<T>
        {
   if (relation == null) return false;
   IList<string> excludeUrls = GetRelationUrls(excludeRelations);
            lock (syncRoot) {
                foreach (string hrefOut in relation.outgoingRelationships) {
                    if (excludeUrls.Contains(hrefOut))
                        continue;
                    if (hrefOut != relation.HRef && registeredRelations.ContainsKey(hrefOut))
                        return true;
                }
                string hRef = relation.HRef;
                if (hRef != null && hRef.Length > 0) {
                    Object value;
                    bool inDictionary = relationsLinkTo.TryGetValue(hRef, out value);
                    if (inDictionary) {
                        IList<T> list = value as IList<T>;
                        T item = value as T;
                        if (list != null) {
                            foreach (T linkBack in list) {
                                if (hRef != linkBack.HRef && !excludeRelations.Contains(linkBack)) {
                                    return true;
                                }
                            }
                        } else if (item != null) {
                            if (!excludeRelations.Contains(item)) {
                                return true;
                            }
                        }
                    }
                }
            }
   return false;
  }
 
  public  bool DeepCosmos {
   get { return false; }
   set {}
  }
 
  public  bool AdjustPointInTime {
   get { return false; }
   set {}
  }
 
  private  void InternalAddRange<T>(IEnumerable<T> relations)
            where T : RelationBase<T>
        {
   if (relations == null) return;
   lock (syncRoot) {
                foreach(T relation in relations)
                    InternalAdd(relation);
   }
  }
 
  private  void InternalAdd<T>(T relation)
            where T : RelationBase<T>
        {
   if (relation == null) return;
   try {
    lock (syncRoot) {
     string href = relation.HRef;
     if (href != null && href.Length > 0 && ! registeredRelations.ContainsKey(href)) {
      registeredRelations[href] = relation;
     }
     foreach (string hrefOut in relation.outgoingRelationships) {
      AddToRelationList(hrefOut, relation, relationsLinkTo);
     }
    }
   } catch (Exception ex) {
    _log.Error("InternalAdd() caused exception", ex);
   }
  }
 
  private  void InternalRemove<T>(T relation)
            where T : RelationBase<T>
        {
   if (relation == null) return;
   lock (syncRoot) {
    foreach (string hrefOut in relation.outgoingRelationships) {
     RemoveFromRelationList(hrefOut, relation, relationsLinkTo);
    }
    if (relation.HRef != null) registeredRelations.Remove(relation.HRef);
   }
  }
 
        private  void InternalRemoveRange<T>(IEnumerable<T> relations)
            where T : RelationBase<T>
        {
   if (relations == null) return;
   lock (syncRoot) {
    foreach(T relation in relations)
     InternalRemove(relation);
   }
  }
 
        private static  void AddToRelationList<T>(string key, T relation, IDictionary<string, Object> toRelations)
            where T : RelationBase<T>
        {
            Object value;
            bool inDictionary = toRelations.TryGetValue(key, out value);
            string href = relation.HRef;
            if(href != null){
                if (href.Equals(key)) {
                    return;
                }
                if (inDictionary) {
                    IList<T> list = value as IList<T>;
                    T item = value as T;
                    if (list != null) {
                        list.Add(relation);
                    } else if (item != null) {
                        list = new List<T>();
                        list.Add(item);
                        list.Add(relation);
                        toRelations[key] = list;
                    }
                } else {
                    toRelations.Add(key, relation);
                }
            }
        }
 
        private static  void RemoveFromRelationList<T>(string key, T relation, Dictionary<string, Object> fromRelations)
            where T : RelationBase<T>
        {
            if (key != null) {
                Object value;
                bool inDictionary = fromRelations.TryGetValue(key, out value);
                if (inDictionary) {
                    IList<T> list = value as IList<T>;
                    T item = value as T;
                    if (list != null) {
                            list.Remove(relation);
                        if (list.Count == 0) {
                            fromRelations.Remove(key);
                        }
                    } else if (item != null) {
                        fromRelations.Remove(key);
                    }
                }
            }
        }
 
        private static  IList<string> GetRelationUrls<T>(IEnumerable<T> relations)
            where T : IRelation
        {
            List<string> urls = new List<string>();
   foreach (T relation in relations) {
    string href = relation.HRef;
                if ((href != null) && !urls.Contains(href)) {
     urls.Add(href);
    }
   }
            return urls;
  }

	}

}
