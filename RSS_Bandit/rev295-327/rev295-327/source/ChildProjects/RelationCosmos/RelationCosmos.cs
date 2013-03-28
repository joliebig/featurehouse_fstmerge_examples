using System; 
using System.Collections; 
using RelationCosmos.Collections; namespace  RelationCosmos {
	
 public class  RelationCosmos {
		
  public  RelationCosmos() {
   allRelations = new Relations();
   registeredRelations = new Relations();
   allIncomingRelations = new RelationLists();
   registeredIncomingRelations = new RelationLists();
  }
 
  public static  RelationList EmptyRelationList { get { return emptyRelationList; } }
 
  public static  RelationHRefList EmptyRelationHRefList { get { return emptyHRefList; } }
 
  public static  TimeSpan DefaultRelationTimeCorrection { get { return defaultRelationTimeCorrection; } }
 
  public static  DateTime UnknownPointInTime { get { return DateTime.MinValue; } }
 
  public  void Add(RelationBase relation) {
   if (relation == null)
    throw new ArgumentNullException("relation");
   if (relation.HRef == null)
    return;
   lock(syncRoot) {
    string href = relation.HRef;
    try {
     if (registeredRelations.Contains(href)) {
      Remove(relation);
     }
     if (deepCosmos && !allRelations.ContainsKey(href)) {
      allRelations.Add(href, relation);
     }
     if (relation.OutgoingRelations.Count > 0) {
      foreach (string hrefOut in relation.OutgoingRelations) {
       if (hrefOut != null && hrefOut.Length > 0) {
        if (deepCosmos && !allRelations.Contains(hrefOut) ) {
         allRelations.Add(hrefOut, new RelationProxy(hrefOut, relation.PointInTime, relation.PointInTimeIsAdjustable));
        }
        if (deepCosmos) {
         if (href != hrefOut && allRelations.Contains(hrefOut)) {
          RelationBase known = allRelations[hrefOut];
          if (known.PointInTime > relation.PointInTime){
           AdjustRelationPointInTime(relation, known, adjustPointInTime);
          }
          AddToRelationList(relation.HRef, known, allIncomingRelations);
         }
        }
        if (href != hrefOut && registeredRelations.Contains(hrefOut)) {
         RelationBase known = registeredRelations[hrefOut];
         if (known.PointInTime > relation.PointInTime) {
          AdjustRelationPointInTime(relation, known, adjustPointInTime);
         }
         AddToRelationList(known.HRef, relation, registeredIncomingRelations);
        }
       }
      }
      relation.OutgoingRelations.TrimToSize();
     } else {
     }
     if (relation.PointInTime == UnknownPointInTime)
      relation.SetInternalPointInTime(DateTime.UtcNow);
     foreach (RelationBase known in registeredRelations.Values) {
      if (known.OutgoingRelations.Contains(href)) {
       if (known.CompareTo(relation) <= 0) {
        AdjustRelationPointInTime(relation, known, adjustPointInTime);
       } else {
       }
       AddToRelationList(relation.HRef, known, registeredIncomingRelations);
      }
     }
     registeredRelations.Add(href, relation);
    } catch (Exception ex) {
     System.Diagnostics.Trace.WriteLine("RelationCosmos.Add() exception: "+ex.Message);
    }
   }
  }
 
  public  void AddRange(RelationBase[] relations) {
   foreach (RelationBase r in relations) {
    Add(r);
   }
  }
 
  public  void AddRange(IList relations) {
   foreach (RelationBase r in relations) {
    Add(r);
   }
  }
 
  public  void Remove(RelationBase relation) {
   if (relation == null || relation.HRef == null) {
    return;
   }
   lock(syncRoot) {
    try {
     if (allRelations.ContainsKey(relation.HRef))
      allRelations.Remove(relation.HRef);
     if (registeredRelations.ContainsKey(relation.HRef))
      registeredRelations.Remove(relation.HRef);
     if (relation.OutgoingRelations.Count > 0) {
      foreach (string hrefOut in relation.OutgoingRelations) {
       if (hrefOut != null && hrefOut.Length > 0) {
        if (allIncomingRelations.ContainsKey(hrefOut)) {
         RemoveFromRelationList(hrefOut, relation, allIncomingRelations);
        }
        if (registeredIncomingRelations.ContainsKey(hrefOut)) {
         RemoveFromRelationList(hrefOut, relation, registeredIncomingRelations);
        }
        if (allRelations.ContainsKey(hrefOut))
         allRelations.Remove(hrefOut);
       }
      }
     } else {
     }
    } catch (Exception ex) {
     System.Diagnostics.Trace.WriteLine("RelationCosmos.Remove() exception: "+ex.Message);
    }
   }
  }
 
  public  void RemoveRange(IList relations) {
   foreach (RelationBase r in relations) {
    Remove(r);
   }
  }
 
  public  void RemoveRange(RelationBase[] relations) {
   foreach (RelationBase r in relations) {
    Remove(r);
   }
  }
 
  public  void Clear() {
   allRelations.Clear();
   registeredRelations.Clear();
   allIncomingRelations.Clear();
   registeredIncomingRelations.Clear();
  }
 
  public  RelationList GetIncoming(RelationBase relation, IList excludeRelations) {
   if (relation == null || relation.HRef == null)
    return RelationCosmos.emptyRelationList;
   lock(syncRoot) {
    try {
     if (excludeRelations == null)
      excludeRelations = (IList) RelationCosmos.emptyRelationList;
     RelationList list = registeredIncomingRelations[relation.HRef];
     if (list != null && list.Count > 0) {
      RelationList returnList = new RelationList(list.Count);
      foreach (RelationBase r in list) {
       if (r != relation && !excludeRelations.Contains(r)) {
        returnList.Add(r);
       }
      }
      return returnList;
     }
    } catch (Exception ex) {
     System.Diagnostics.Trace.WriteLine("RelationCosmos.GetIncoming() exception: "+ex.Message);
    }
    return RelationCosmos.emptyRelationList;
   }
  }
 
  public  RelationList GetOutgoing(RelationBase relation, IList excludeRelations) {
   if (relation == null || relation.HRef == null)
    return RelationCosmos.emptyRelationList;
   lock(syncRoot) {
    try {
     if (excludeRelations == null)
      excludeRelations = (IList)RelationCosmos.emptyRelationList;
     RelationHRefList list = relation.OutgoingRelations;
     if (list != null && list.Count > 0) {
      RelationList returnList = new RelationList(list.Count);
      foreach (string hrefOut in list) {
       if (hrefOut != relation.HRef && !RelationListContainsHRef(excludeRelations, hrefOut) &&
        registeredRelations.Contains(hrefOut)) {
        returnList.Add(registeredRelations[hrefOut]);
       }
      }
      return returnList;
     }
    } catch (Exception ex) {
     System.Diagnostics.Trace.WriteLine("RelationCosmos.GetOutgoing() exception: "+ex.Message);
    }
    return RelationCosmos.emptyRelationList;
   }
  }
 
  public  RelationList GetIncomingAndOutgoing(RelationBase relation, IList excludeRelations) {
   RelationList returnList = new RelationList(this.GetIncoming(relation, excludeRelations));
   returnList.AddRange(this.GetOutgoing(relation, excludeRelations));
   if (returnList.Count > 0) {
    return returnList;
   }
   return RelationCosmos.emptyRelationList;
  }
 
  public  bool HasIncomingOrOutgoing(RelationBase relation, IList excludeRelations) {
   if (relation == null || relation.HRef == null)
    return false;
   lock(syncRoot) {
    try {
     if (excludeRelations == null)
      excludeRelations = RelationCosmos.emptyRelationList;
     foreach (string hrefOut in relation.OutgoingRelations) {
      if (hrefOut != null && hrefOut != relation.HRef &&
       !RelationListContainsHRef(excludeRelations, hrefOut) && registeredRelations.Contains(hrefOut)) {
       return true;
      }
     }
     RelationList list = registeredIncomingRelations[relation.HRef];
     if (list != null) {
      foreach (RelationBase r in list) {
       if (r != relation && !excludeRelations.Contains(r)) {
        return true;
       }
      }
     }
    } catch (Exception ex) {
     System.Diagnostics.Trace.WriteLine("RelationCosmos.HasIncomingOrOutgoing() exception: "+ex.Message);
    }
    return false;
   }
  }
 
  public  bool DeepCosmos { get { return deepCosmos; } set { deepCosmos = value; } }
 
  public  bool AdjustPointInTime { get { return adjustPointInTime; } set { adjustPointInTime = value; } }
 
  private static  RelationList GetRelationList(string key, RelationLists fromRelations) {
   RelationList list = fromRelations[key];
   if (list == null) {
    list = new RelationList(3);
    fromRelations.Add(key, list);
   }
   return list;
  }
 
  private static  void AddToRelationList(string key, RelationBase relation, RelationLists toRelations) {
   RelationList list = GetRelationList(key, toRelations);
   if (!list.Contains(relation)) {
    list.Add(relation);
   }
  }
 
  private static  void RemoveFromRelationList(string key, RelationBase relation, RelationLists fromRelations) {
   RelationList list = fromRelations[key];
   if (list != null) {
    int removeIndex = list.IndexOf(relation);
    if (removeIndex >= 0) {
     list.RemoveAt(removeIndex);
    }
    if (list.Count == 0) {
     fromRelations.Remove(key);
    }
   }
  }
 
  private static  bool RelationListContainsHRef(IList relationList, string href) {
   foreach (RelationBase r in relationList) {
    if (r.HRef == href)
     return true;
   }
   return false;
  }
 
  private static  void AdjustRelationPointInTime(RelationBase relation, RelationBase registeredRelation, bool force) {
   if (relation.PointInTime > registeredRelation.PointInTime) {
    if (relation.PointInTimeIsAdjustable) {
     relation.SetInternalPointInTime(registeredRelation.PointInTime.Subtract(defaultRelationTimeCorrection));
    } else if (registeredRelation.PointInTimeIsAdjustable && relation.PointInTime != UnknownPointInTime) {
     registeredRelation.SetInternalPointInTime(relation.PointInTime.Add(defaultRelationTimeCorrection));
    } else if (force) {
     relation.SetInternalPointInTime(registeredRelation.PointInTime.Subtract(defaultRelationTimeCorrection));
    }
   }
  }
 
  private static  RelationList emptyRelationList = RelationList.ReadOnly(new RelationList(0));
 
  private static  TimeSpan defaultRelationTimeCorrection = new TimeSpan(100);
 
  private static  RelationHRefList emptyHRefList = RelationHRefList.ReadOnly(new RelationHRefList(0));
 
  private  Relations allRelations;
 
  private  Relations registeredRelations;
 
  private  RelationLists allIncomingRelations;
 
  private  RelationLists registeredIncomingRelations;
 
  private  bool deepCosmos = false;
 
  private  bool adjustPointInTime = true;
 
  private  object syncRoot = new Object();

	}

}
