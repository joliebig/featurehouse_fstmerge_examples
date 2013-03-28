using System; 
using System.Reflection; 
using System.Collections.Generic; 
using Tst; namespace  NewsComponents.RelationCosmos {
	
 public class  RelationCosmos3 : IRelationCosmos {
		
  private static readonly  log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);
 
  private  Dictionary<string, IRelation> registeredRelations = new Dictionary<string, IRelation>();
 
  private  TstDictionaries relationsLinkTo = new TstDictionaries();
 
  private  object syncRoot = new Object();
 
  public  RelationCosmos3(){}
 
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
 
        public  IList<T> GetIncoming<T>(T relation, IList<T> excludeRelations)
            where T : RelationBase<T>
        {
            if (relation == null) return new List<T>();
   lock (syncRoot) {
    IList<T> ret = new List<T>(relation.OutgoingRelations.Count);
    string hRef = relation.HRef;
    if (hRef != null && hRef.Length > 0) {
     TstDictionary list = relationsLinkTo[hRef];
     if (list != null) {
      foreach (T linkBack in list.Values) {
       if ( ! excludeRelations.Contains(linkBack) && ! ret.Contains(linkBack)) {
        ret.Add(linkBack);
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
            return new List<T>() ;
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
     TstDictionary list = relationsLinkTo[hRef];
     if (list != null) {
      foreach (T linkBack in list.Values) {
       if (hRef != linkBack.HRef && ! excludeRelations.Contains(linkBack) )
        return true;
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
                foreach (T relation in relations)
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
 
  private static  TstDictionary GetRelationList(string key, TstDictionaries fromRelations) {
   TstDictionary list = fromRelations[key];
   if (list == null) {
    list = new TstDictionary();
    fromRelations.Add(key, list);
   }
   return list;
  }
 
  private static  void AddToRelationList<T>(string key, T relation, TstDictionaries toRelations)
            where T : RelationBase<T>
        {
   TstDictionary list = GetRelationList(key, toRelations);
   string href = relation.HRef;
   if (href != null && !list.Contains(href)) {
    list.Add(href, relation);
   }
  }
 
  private static  void RemoveFromRelationList<T>(string key, T relation, TstDictionaries fromRelations)
            where T : RelationBase<T>
        {
   if (key != null) {
    TstDictionary list = fromRelations[key];
    if (list != null) {
     string href = relation.HRef;
     if (href != null && !list.Contains(href))
      list.Remove(href);
     if (list.Count == 0) {
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
